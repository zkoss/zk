/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying components or objects to wire in 
 * @{GenericAnnotatedComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Wire {
	
	/**
	 * The selector string that specifies the Components to wire. If empty, 
	 * GenericAnnotatedComposer will attempt to wire from implicit objects,
	 * XEL/ZScript variables and fellows.
	 */
	String value() default "";
	
	/**
	 * If set to true, no Exception is throw when component/object is not found
	 * for wiring. By default, when GenericAnnotatedComposer fails to wire an 
	 * object to a field, an UiException will be thrown. 
	 */
	boolean optional() default false;
	
	/**
	 * If true, the variable will be rewired when the composer is deserialized 
	 * in cluster environment. Session and Webapp variables are always rewired.
	 */
	boolean rewireOnActivate() default false;
	
}
