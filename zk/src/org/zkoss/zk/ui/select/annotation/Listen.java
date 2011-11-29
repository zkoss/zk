/**
 * 
 */
package org.zkoss.zk.ui.select.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.select.GenericAnnotatedComposer;

/**
 * Annotation for specifying Event handling in {@link GenericAnnotatedComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listen {
	
	/**
	 * The instruction string for adding EventListeners. The string should 
	 * contain a list of [event name] = [selector] pairs, separated by semicolon.
	 * 
	 * For example:
	 * <pre><code>
	 * &#064;Listen("onClick = button#submitBtn; onOK = textbox#passwordBox")
	 * </code></pre>
	 */
	String value();
	
}
