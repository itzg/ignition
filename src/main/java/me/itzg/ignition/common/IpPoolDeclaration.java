package me.itzg.ignition.common;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.Inet4Address;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class IpPoolDeclaration {
    @NotNull
    @ValidIPv4Address
    private
    String address;

    @Min(1)
    @Max(31)
    private
    int prefixLength;

    @NotBlank
    private
    String name;

    @Min(0)
    private
    int startingOffset = 0;

    @Min(1)
    private
    int count;

    @NotNull @ValidIPv4Address
    private
    String defaultGateway;


    public int getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartingOffset() {
        return startingOffset;
    }

    public void setStartingOffset(int startingOffset) {
        this.startingOffset = startingOffset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }
}
