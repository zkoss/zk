/**
 * 
 */
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify value change. 
 * By default, a property set by binder will notify this property changed. 
 * You could use this annotation on the set method to change or add notification target.
 * You could also add this annotation on a command method to notify properties that will be changed after the command.
 * To avoid the default notification, use {@link NotifyChangeDisabled} on the set method independently.   
 *  
 * @author henrichen
 * @see NotifyChangeDisabled
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyChange {
	String[] value() default {};
}
