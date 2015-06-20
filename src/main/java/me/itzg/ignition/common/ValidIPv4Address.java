package me.itzg.ignition.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
@Documented
@Constraint(validatedBy = IPv4AddressValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
public @interface ValidIPv4Address {
    String message() default "Invalid IPv4 address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
