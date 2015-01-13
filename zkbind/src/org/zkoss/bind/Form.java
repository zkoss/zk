/* Form.java

	Purpose:
		
	Description:
		
	History:
		Jun 24, 2011 6:36:05 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 * Virtual Bean that associated with a form. 
 * @author henrichen
 * @since 6.0.0
 */
public interface Form {

	/**
	 * Returns the status object of this form
	 * @since 8.0.0
	 */
	public FormStatus getFormStatus();
}
