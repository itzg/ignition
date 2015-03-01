package me.itzg.ignition.services;

import me.itzg.ignition.IgnitionConstants;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@Service
public class MachinesService {
    private static Logger LOG = LoggerFactory.getLogger(MachinesService.class);

    @Autowired
    private EtcdClient etcdClient;

    public String getOrCreateMachineName(String ipAddr) throws IOException, TimeoutException, EtcdException {
        final String nameNodeKey = buildMachineNodeKey(ipAddr, IgnitionConstants.ETCD_NAME_NODE);
        try {
            final EtcdKeysResponse etcdKeysResponse = etcdClient.get(nameNodeKey).send().get();
            return etcdKeysResponse.node.value;
        } catch (EtcdException e) {
            if (e.errorCode == IgnitionConstants.ETCD_ERR_KEY_NOT_FOUND) {
                String generatedName = UUID.randomUUID().toString();
                etcdClient.put(nameNodeKey, generatedName).send(); // and no need to wait
                return generatedName;
            } else throw e;
        }
    }

    public String getMachineRole(final String ipAddr, final String roleInRequest) throws IOException, TimeoutException, EtcdException {
        final String key = buildMachineNodeKey(ipAddr, IgnitionConstants.ETCD_ROLE_NODE);
        try {
            final EtcdKeysResponse etcdKeysResponse = etcdClient.get(key).send().get();
            final String knownRole = etcdKeysResponse.node.value;
            if (roleInRequest != null && !roleInRequest.equals(knownRole)) {
                throw new IllegalArgumentException("Role specified in request " + roleInRequest + " does not match known role " + knownRole);
            }

            return knownRole;
        } catch (EtcdException e) {
            if (e.errorCode == IgnitionConstants.ETCD_ERR_KEY_NOT_FOUND) {
                if (roleInRequest != null) {
                    etcdClient.put(key, roleInRequest).send();
                    return roleInRequest;
                } else {
                    return null;
                }
            }
            throw e;
        }
    }

    public String getDefaultContent(final String nodeName) throws IOException, TimeoutException, EtcdException {
        final String key = buildDefaultNodeKey(nodeName);
        final String content = getContent(key);
        if (content == null) {
            throw new FileNotFoundException("Unable to locate the default data node "+nodeName);
        }
        else {
            return content;
        }
    }

    private String getContent(String key) throws IOException, TimeoutException, EtcdException {
        try {
            final EtcdKeysResponse etcdKeysResponse = etcdClient.get(key).send().get();
            return etcdKeysResponse.node.value;
        } catch (EtcdException e) {
            if (e.errorCode == IgnitionConstants.ETCD_ERR_KEY_NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public String getMachineContent(String ipAddr, String role, String nodeName) throws EtcdException, TimeoutException, IOException {
        String content = getContent(buildMachineNodeKey(ipAddr, nodeName));
        if (content != null) {
            return content;
        }

        if (role != null) {
            content = getContent(buildRoleNodeKey(role, nodeName));
            if (content != null) {
                return content;
            }
        }

        content = getContent(buildDefaultNodeKey(nodeName));
        if (content != null) {
            return content;
        }
        else {
            throw new FileNotFoundException("Unable to find " + nodeName + " given ipAddr=" + ipAddr + ", role=" + role);
        }
    }

    public void setDefaultContent(final String nodeName, final String content) throws IOException, TimeoutException, EtcdException {
        final String key = buildDefaultNodeKey(nodeName);
        final EtcdResponsePromise<EtcdKeysResponse> promise = etcdClient.put(key, content).send();
    }

    public String getMachineSshKey(String ipAddr, String role) throws TimeoutException, EtcdException, IOException {
        return getMachineContent(ipAddr, role, IgnitionConstants.ETCD_SSHKEY_NODE);
    }

    private String buildMachineNodeKey(String ipAddr, String detailsNode) {
        return IgnitionConstants.PATH_JOINER.join(IgnitionConstants.ETCD_BASE, IgnitionConstants.ETCD_MACHINES,
                IgnitionConstants.ETCD_BY_IP, ipAddr, detailsNode);
    }

    private String buildRoleNodeKey(String role, String nodeName) {
        return IgnitionConstants.PATH_JOINER.join(IgnitionConstants.ETCD_BASE, IgnitionConstants.ETCD_MACHINES,
                IgnitionConstants.ETCD_BY_ROLE, role, nodeName);
    }

    private String buildDefaultNodeKey(String nodeName) {
        return IgnitionConstants.PATH_JOINER.join(IgnitionConstants.ETCD_BASE, IgnitionConstants.ETCD_MACHINES,
                IgnitionConstants.ETCD_DEFAULT, nodeName);
    }
}
