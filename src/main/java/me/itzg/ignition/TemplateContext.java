package me.itzg.ignition;

import org.springframework.core.style.ToStringCreator;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
public class TemplateContext {
    private String ignitionBaseUrl;
    private String private_ipv4;
    private String public_ipv4;
    private String name;
    private String role;
    private String sshKey;

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("ignitionBaseUrl", ignitionBaseUrl)
                .append("private_ipv4", private_ipv4)
                .append("public_ipv4", public_ipv4)
                .append("name", name)
                .append("role", role)
                .append("sshKey", sshKey != null ? "(hidden)" : null)
                .toString();
    }

    public String getIgnitionBaseUrl() {
        return ignitionBaseUrl;
    }

    public void setIgnitionBaseUrl(String ignitionBaseUrl) {
        this.ignitionBaseUrl = ignitionBaseUrl;
    }

    public String getPrivate_ipv4() {
        return private_ipv4;
    }

    public void setPrivate_ipv4(String private_ipv4) {
        this.private_ipv4 = private_ipv4;
    }

    public String getPublic_ipv4() {
        return public_ipv4;
    }

    public void setPublic_ipv4(String public_ipv4) {
        this.public_ipv4 = public_ipv4;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }
}
