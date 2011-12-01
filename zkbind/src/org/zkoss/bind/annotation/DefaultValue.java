package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify default value of a parameter of a method. 
 * 
 * @see {@link Command}
 * @see {@link Param}
 * @author dennis
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
	/**
	 * Default value of the parameter. 
	 * It is string when you assign it in annotation, and will coerce to the corresponding type of the parameter if parameter value is not null.
	 * @return
	 */
	String value();
}
