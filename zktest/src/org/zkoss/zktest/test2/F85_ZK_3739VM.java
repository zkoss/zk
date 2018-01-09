/* F85_ZK_3739VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 08 18:45:02 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class F85_ZK_3739VM {
	private String insertedText = "";

	public String getInsertedText() {
		return insertedText;
	}

	public void setInsertedText(String insertedText) {
		this.insertedText = insertedText;
	}

	@Command
	@NotifyChange("insertedText")
	public void insertText(@BindingParam("text") String text) {
		setInsertedText(text);
	}
}
