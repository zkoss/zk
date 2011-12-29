/* ScopeParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify the name of a parameter of a method. <br/>
 * By default, the {@linkplain #scopes()} is  {@link Scope#AUTO}. <br/>
 * You could arrange another sequence by setting the {@linkplain #scopes()}. <br/>
 * 
 * @see Init
 * @see Command
 * @see Scope
 * @author dennis
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScopeParam {
	/**
	 * name of the parameter
	 * @return name of the parameter
	 */
	String value();
	
	/**
	 * the scopes to evaluate.
	 * @return the scopes, default {@linkplain Scope#AUTO}
	 */
	Scope[] scopes() default {Scope.AUTO};
}
