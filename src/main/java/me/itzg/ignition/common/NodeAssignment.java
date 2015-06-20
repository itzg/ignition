package me.itzg.ignition.common;

import com.google.common.base.Strings;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Geoff Bourne
 * @since 6/20/2015
 */
public class NodeAssignment {
    @NotBlank
    private
    String name;

    @NotNull
    private
    UUID id;

    @NotBlank
    private
    String channel;

    private String configRef;

    private String sshKey;

    private String sshKeyRef;

    @NotBlank
    private
    String ipPool;

    public NodeAssignment copy(NodeAssignment from) {
        this.name = from.name;
        this.id = from.id;
        this.channel = from.channel;
        this.configRef = from.configRef;
        this.sshKey = from.sshKey;
        this.sshKeyRef = from.sshKeyRef;
        return this;
    }

    @AssertTrue(message = "sshKey or sshKeyRef needs to be set")
    public boolean validateSshKeySet() {
        return !Strings.isNullOrEmpty(sshKey) || !Strings.isNullOrEmpty(sshKeyRef);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getConfigRef() {
        return configRef;
    }

    public void setConfigRef(String configRef) {
        this.configRef = configRef;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getSshKeyRef() {
        return sshKeyRef;
    }

    public void setSshKeyRef(String sshKeyRef) {
        this.sshKeyRef = sshKeyRef;
    }

    public String getIpPool() {
        return ipPool;
    }

    public void setIpPool(String ipPool) {
        this.ipPool = ipPool;
    }
}
