/* F85_ZK_2459VM.java

        Purpose:
                
        Description:
                
        History:
                Thu May 31 10:14:27 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class F85_ZK_2459VM {
	private String propertyOne;
	private String propertyTwo;
	private String propertyThree;
	
	@Command
	public void changeProperties() {
		propertyOne = "one";
		propertyTwo = "two";
		propertyThree = "three";
		BindUtils.postNotifyChange(null, null, this, "propertyOne", "propertyTwo", "propertyThree");
	}
	
	public String getPropertyOne() {
		return propertyOne;
	}
	
	public String getPropertyTwo() {
		return propertyTwo;
	}
	
	public String getPropertyThree() {
		return propertyThree;
	}
}
