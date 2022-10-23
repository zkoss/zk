/* B85_ZK_3950VM.java

        Purpose:
                
        Description:
                
        History:
                Mon Jun 11 15:03:06 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B85_ZK_3950VM {
	private boolean visible;
	
	@Command
	@NotifyChange("visible")
	public void toggleTab() {
		visible = !visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
