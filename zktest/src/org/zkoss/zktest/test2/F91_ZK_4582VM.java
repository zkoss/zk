/* F91_ZK_4582VM.java

		Purpose:
		
		Description:
		
		History:
				Fri May 22 11:44:27 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class F91_ZK_4582VM {
	private String propertyOne;
	private String propertyTwo;
	private String propertyThree;
	
	public String getPropertyOne() {
		return propertyOne;
	}
	
	public String getPropertyTwo() {
		return propertyTwo;
	}
	
	public String getPropertyThree() {
		return propertyThree;
	}
	
	@Command
	public void changeProperties() {
		propertyOne = "one";
		propertyTwo = "two";
		propertyThree = "three";
		BindUtils.postNotifyChange(this, "propertyOne", "propertyTwo", "propertyThree");
	}
	
	@Command
	public void changeProperty() {
		propertyOne = "1";
		BindUtils.postNotifyChange(this, "propertyOne");
	}
}
