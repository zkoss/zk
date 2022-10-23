/* B86_ZK_4014VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 02 17:49:05 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B86_ZK_4014VM {
	public ListModel<String> getModel() {
		return new ListModelList<>(new String[] {
				"test1", "test2", "test3", "test4"
		});
	}
}
