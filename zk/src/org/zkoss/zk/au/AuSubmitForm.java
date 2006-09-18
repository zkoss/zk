/* AuSubmitForm.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 26 14:34:43     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.au;

/**
 * A response to ask the client to submit the form with the specified ID,
 * if any.
 *
 * <p>data[0]: the form's UUID.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuSubmitForm extends AuResponse {
	/**
	 * @param formId the form's ID to submit, which is UUID if a component
	 * is used.
	 */
	public AuSubmitForm(String formId) {
		super("submit", formId); //component-independent
	}
}
