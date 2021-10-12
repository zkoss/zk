/* B85_ZK_3969VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 26 11:29:09 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rudyhuang
 */
public class B85_ZK_3969VM {
	public List<String> getValueList() {
		List<String> model = new ArrayList<String>();
		model.add("value1");
		model.add("value2");
		return model;
	}

	public String getStringValue() {
		return "string value";
	}
}
