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
 * Marker annotation to identify the name of a parameter of a method.
 * By default, the value of this parameter is getting from current scopes in sequence of [requestScope, componentScope, spaceScope, pageScope, desktopScope, sessionScope, applicationScope]
 * You could arrange another sequence by setting the {@linkplain #scopes()} and in this case, the {@link Scope#ALL} is ignored.
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
	 * @return the scopes, default {@linkplain Scope#ALL}
	 */
	Scope[] scopes() default {Scope.ALL};
}
