/* B86_ZK_4255VM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 14 12:28:36 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.Textbox;

public class B86_ZK_4255VM {
	private int intValue;
	
	public int getIntValue() {
		return intValue;
	}
	
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	
	@Command("doDisplay")
	public void doDisplay( @BindingParam("argValue")Textbox argValue){
		argValue.setErrorMessage("test");
		argValue.getText();
	}
}
