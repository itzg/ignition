package me.itzg.ignition.common;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class IPv4AddressValidator implements ConstraintValidator<ValidIPv4Address, String> {
    private static final Pattern SANITY_CHECK = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
    @Override
    public void initialize(ValidIPv4Address constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!SANITY_CHECK.matcher(value).matches()) {
            return false;
        }

        try {
            final InetAddress inetAddress = InetAddress.getByName(value);
            return inetAddress instanceof Inet4Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
