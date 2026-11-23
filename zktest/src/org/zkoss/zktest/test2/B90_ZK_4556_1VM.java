/* B90_ZK_4556_1VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 27 12:46:50 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jameschu
 */
public class B90_ZK_4556_1VM implements Serializable {
	private boolean visible;

	public boolean isVisible() {
		return visible;
	}

	@Command
	@NotifyChange("visible")
	public void toggle() {
		visible = !visible;
	}
}
