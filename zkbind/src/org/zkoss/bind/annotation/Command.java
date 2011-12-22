/* Command.java

	Purpose:
		
	Description:
		
	History:
		Jul 5, 2011 2:56:03 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify a Command method. 
 * A Command method could also use with {@link BindingParam} and others to assign a binding argument as its parameter, 
 * and {@link Default} to assign a default value if the argument is null.
 * 
 * @see BindingParam
 * @see ExecutionParam
 * @see ExecutionArgParam
 * @see HeaderParam
 * @see CookieParam
 * @see QueryParam
 * @see ScopeParam
 * @see ContextnParam
 * @see Default
 * @author henrichen
 * @author dennischen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String[] value() default {};
}
