/** B80_ZK_2758VM.java.

	Purpose:
		
	Description:
		
	History:
		12:04:20 PM May 28, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

/**
 * @author jumperchen
 *
 */
public class B80_ZK_2758VM {
	private String error;

	@Command
	public void toggleError() {
		if(error == null) {
			error = "some error message";
		} else {
			error = null;
		}
		BindUtils.postNotifyChange(null, null, this, "errorMessage");
	}

	public String getErrorMessage() {
		return error;
	}
}
