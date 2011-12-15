/**
 * 
 */
package org.zkoss.zk.ui.select.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.select.SelectorComposer;

/**
 * Annotation for specifying components to wire.
 * {@link SelectorComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Wire {
	
	/**
	 * The selector string that specifies the Components to wire. If empty, 
	 * {@link SelectorComposer} will attempt to wire from fellows by name.
	 */
	String value() default "";
	
	/**
	 * If set to true, no Exception is throw when component/object is not found
	 * for wiring. By default, when {@link SelectorComposer} fails to wire an 
	 * object to a field, an UiException will be thrown. 
	 */
	boolean optional() default false;
	
	/**
	 * If true, the component will be rewired when the composer is deserialized 
	 * in cluster environment.
	 */
	boolean rewireOnActivate() default false;
	
}
