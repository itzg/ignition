package me.itzg.ignition.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.ignition.common.AddressUtils;
import me.itzg.ignition.common.AlreadyExistsException;
import me.itzg.ignition.common.DatastoreException;
import me.itzg.ignition.common.IpPoolDeclaration;
import me.itzg.ignition.etcd.EtcdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static me.itzg.ignition.IgnitionConstants.ETCD_BASE;
import static me.itzg.ignition.IgnitionConstants.ETCD_IP_POOLS;
import static me.itzg.ignition.IgnitionConstants.ETCD_NET;

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
    private EtcdPathService paths;

    @PostConstruct
    public void init() throws IOException, EtcdException {
        paths.ensureDir(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS);
    }

    public void declareIpPool(IpPoolDeclaration ipPoolDeclaration) throws DatastoreException, AlreadyExistsException, IOException, EtcdException {
        final String name = ipPoolDeclaration.getName();
        if (paths.createDirIfNotExists(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name)) {
            paths.put(ipPoolDeclaration.getAddress(), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, ADDRESS);
            paths.put(ipPoolDeclaration.getDefaultGateway(), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, DEFAULT_GATEWAY);
            paths.put(String.valueOf(ipPoolDeclaration.getPrefixLength()), ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, PREFIX_LENGTH);

            final byte[] poolAddress = InetAddress.getByName(ipPoolDeclaration.getAddress()).getAddress();

            final byte[] masked = AddressUtils.mask(poolAddress, ipPoolDeclaration.getPrefixLength());
            final int startingOffset = ipPoolDeclaration.getStartingOffset();
            for (int i = startingOffset; i < startingOffset + ipPoolDeclaration.getCount(); ++i) {
                byte[] specific = AddressUtils.applyIndex(masked, i);
                final String addr = InetAddress.getByAddress(specific).getHostAddress();
                if (!paths.putIfNotExists("", ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, addr)) {
                    removeAddressesFromPool(name, masked, startingOffset, i-startingOffset);
                    throw new AlreadyExistsException("IP allocation of " + addr + " already existed in pool");
                }
            }
        }
        else {
            throw new AlreadyExistsException("IP pool already exists");
        }
    }

    private void removeAddressesFromPool(String name, byte[] maskedAddr, int startingOffset, int count) throws IOException, EtcdException {
        for (int i = startingOffset; i < startingOffset + count; ++i) {
            byte[] specific = AddressUtils.applyIndex(maskedAddr, i);
            final String addr = InetAddress.getByAddress(specific).getHostAddress();
            paths.delete(ETCD_BASE, ETCD_NET, ETCD_IP_POOLS, name, addr);
        }
    }

}
