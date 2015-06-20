package me.itzg.ignition.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.ignition.common.*;
import me.itzg.ignition.etcd.EtcdException;
import me.itzg.ignition.etcd.EtcdUtils;
import me.itzg.ignition.etcd.keys.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import static me.itzg.ignition.IgnitionConstants.*;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
@Service
public class ConfigService {
    private static final String ADDRESS = "_address";
    private static final String DEFAULT_GATEWAY = "_default_gateway";
    private static final String PREFIX_LENGTH = "_prefix_length";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EtcdService etcd;

    @PostConstruct
    public void init() throws IOException, EtcdException {
        etcd.ensureDir(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS);
        etcd.ensureDir(ETCD_BASE, ETCD_NET, ETCD_NODES);
    }

    public void declareIpPool(IpPoolDeclaration ipPoolDeclaration) throws DatastoreException, AlreadyExistsException, IOException, EtcdException {
        final String name = ipPoolDeclaration.getName();
        if (etcd.createDirIfNotExists(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name)) {
            etcd.put(ipPoolDeclaration.getAddress(), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, ADDRESS);
            etcd.put(ipPoolDeclaration.getDefaultGateway(), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, DEFAULT_GATEWAY);
            etcd.put(String.valueOf(ipPoolDeclaration.getPrefixLength()), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, PREFIX_LENGTH);

            final byte[] poolAddress = InetAddress.getByName(ipPoolDeclaration.getAddress()).getAddress();

            final byte[] masked = AddressUtils.mask(poolAddress, ipPoolDeclaration.getPrefixLength());
            final int startingOffset = ipPoolDeclaration.getStartingOffset();
            for (int i = startingOffset; i < startingOffset + ipPoolDeclaration.getCount(); ++i) {
                byte[] specific = AddressUtils.applyIndex(masked, i);
                final String addr = InetAddress.getByAddress(specific).getHostAddress();
                if (!etcd.putIfNotExists("", ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, addr)) {
                    removeAddressesFromPool(name, masked, startingOffset, i - startingOffset);
                    throw new AlreadyExistsException("IP allocation of " + addr + " already existed in pool");
                }
            }
        } else {
            throw new AlreadyExistsException("IP pool already exists");
        }
    }

    private void removeAddressesFromPool(String name, byte[] maskedAddr, int startingOffset, int count) throws IOException, EtcdException {
        for (int i = startingOffset; i < startingOffset + count; ++i) {
            byte[] specific = AddressUtils.applyIndex(maskedAddr, i);
            final String addr = InetAddress.getByAddress(specific).getHostAddress();
            etcd.delete(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, addr);
        }
    }

    public NetAllocation allocateFromIpPool(String ipPoolName, UUID nodeId)
            throws IOException, EtcdException, IgnitionException {

        Node node = etcd.get(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, ipPoolName);
        if (node == null) {
            throw new DoesNotExistException("IP pool: " + ipPoolName);
        }

        if (!node.isDir()) {
            throw new IllegalStateException("Node at path was not a directory: " + ipPoolName);
        }

        if (node.getNodes() == null || node.getNodes().isEmpty()) {
            throw new IllegalStateException("No addresses declared in pool: " + ipPoolName);
        }

        for (Node addrNode : node.getNodes()) {
            if (addrNode.getValue().isEmpty()) {
                if (etcd.updateKeyAtomically(nodeId.toString(), addrNode.getModifiedIndex(), addrNode.getKey())) {
                    final String[] parentPath = EtcdUtils.extractParentPath(addrNode);
                    final EtcdService.BulkGetter bulkGetter = etcd.bulkGet(parentPath);
                    if (bulkGetter != null) {
                        NetAllocation netAllocation = new NetAllocation();
                        netAllocation.setIpAddress(EtcdUtils.extractNameFromNode(addrNode));

                        netAllocation.setGateway(EtcdUtils.extractValue(bulkGetter.get(DEFAULT_GATEWAY)));

                        final int prefixLength = EtcdUtils.extractIntValue(bulkGetter.get(PREFIX_LENGTH));
                        netAllocation.setPrefixLength(prefixLength);
                        netAllocation.setSubnetMask(AddressUtils.convertToSubnetMask(prefixLength));

                        return netAllocation;
                    } else {
                        throw new IllegalStateException("Using bulk getter failed");
                    }
                }
            }
        }

        throw new ResourcesExhaustedException("No more addresses available in pool: " + ipPoolName);
    }

    public void releaseBackToIpPool(String ipPoolName, UUID nodeId, String ipAddress) throws IgnitionException, IOException, EtcdException {
        if (!etcd.updateKeyAtomically("", nodeId.toString(), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, ipPoolName, ipAddress)) {
            throw new WrongPreconditionsException(String
                    .format("IP address %s was not allocated to %s in pool %s", ipAddress, nodeId, ipPoolName));
        }
    }

    public void assignNode(NodeAssignment nodeAssignment) throws EtcdException, IgnitionException, IOException {
        final NetAllocation netAllocation = allocateFromIpPool(nodeAssignment.getIpPool(), nodeAssignment.getId());

        final NodeAssignmentWithAllocation withAlloc = new NodeAssignmentWithAllocation();
        withAlloc.copy(nodeAssignment);
        withAlloc.setNetAllocation(netAllocation);

        etcd.putIfNotExists(objectMapper.writeValueAsString(withAlloc),
                ETCD_BASE, ETCD_NET, ETCD_NODES, nodeAssignment.getId().toString());
    }

    public void teardownNode(UUID nodeId) throws IOException, EtcdException, IgnitionException {
        final Node node = etcd.get(ETCD_BASE, ETCD_NET, ETCD_NODES, nodeId.toString());
        if (node == null) {
            throw new DoesNotExistException("Node does not exist: "+nodeId);
        }

        final NodeAssignmentWithAllocation assignment = objectMapper.readValue(node.getValue(), NodeAssignmentWithAllocation.class);
        if (assignment == null) {
            throw new IllegalStateException("Unable to obtain node assignment: " + nodeId);
        }

        if (etcd.deleteKeyAtomically(node.getKey(), node.getModifiedIndex())) {
            releaseBackToIpPool(assignment.getIpPool(), nodeId, assignment.getNetAllocation().getIpAddress());
        }
    }
}
