/* Validator.java

	Purpose:
		
	Description:
		
	History:
		2011/9/29 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;


/**
 * Generic binding validation interface.
 * @author dennis
 * @since 6.0.0
 */
public interface Validator {

	void validate(ValidationContext ctx);
}
