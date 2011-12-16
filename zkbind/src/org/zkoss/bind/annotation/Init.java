/* Init.java

	Purpose:
		
	Description:
		
	History:
		2011/11/30 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify a init method
 * A init method could also use with {@link Param} to assign a binding argument as its parameter, 
 * and {@link Default} to assign a default value if the argument is null.
 * 
 * @see Param
 * @see Default
 * @author dennis
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {

}
