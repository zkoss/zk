/* B86_ZK_4195VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 14:55:56 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B86_ZK_4195VM {
	private int count = 0;

	@GlobalCommand
	@NotifyChange("count")
	public void globCommand() {
		count++;
	}

	public int getCount() {
		return count;
	}
}
