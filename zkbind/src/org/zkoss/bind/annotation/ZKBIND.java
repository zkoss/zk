/* ZKBIND.java

	History:
		Wed Feb 22 12:36:28 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.annotation.ComponentAnnotation;

/**
 * A component annotation for providing the default annotation for a component.
 * <p>Unlike other annotations, which are used for annotating ViewModel,
 * {@link ZKBIND} is used to annotate component's Java class.
 * @author tomyeh
 * @since 6.0.1
 */
@ComponentAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ZKBIND {
	/** The property that this annotation is applied to.
	 * It is required if the annotation is specified at the class.
	 * It is not used if the annotation is specified at the method (getter or setter).
	 * <p>Default: an empty string
	 */
	public String property() default "";

	/** The access direction: can be "both", "save", "load";
	 * default to "load" if not found
	 */
	public String ACCESS() default "load";
	/** The save trigger event; meaningful only when ACCESS is "both" or "save".
	 * <p>Default: an empty string (i.e., no save event)
	 */
	public String SAVE_EVENT() default "";
	/** The load replacement ; e.g. value of textbox, it loads to rawValue.
	 * <p>Default: an empty string (i.e., no load replacement).
	 */
	public String LOAD_REPLACEMENT() default "";
	/** The class for loading.
	 * <p>Default: Object.class.
	 * It is used only if {@Link #LOAD_REPLACEMENT} is specified.
	 */
	public Class<?> LOAD_TYPE() default Object.class;
}
