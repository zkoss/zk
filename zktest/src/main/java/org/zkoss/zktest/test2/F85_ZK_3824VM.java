/* F85_ZK_3824VM.java

        Purpose:
                
        Description:
                
        History:
                Mon Jun 04 12:59:14 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Random;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F85_ZK_3824VM {
	
	private int value;
	
	public int getValue() {
		return value;
	}
	
	@Command
	@NotifyChange("value")
	public void randomValue() {
		value = new Random().nextInt(100);
	}
}
