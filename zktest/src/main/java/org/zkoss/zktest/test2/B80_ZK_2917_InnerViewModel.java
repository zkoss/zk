/* B80_ZK_2917_InnerViewModel.java

	Purpose:
		
	Description:
		
	History:
		12:14 PM 10/27/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2917_InnerViewModel {
	public String getTestLabel() {
		return "innerViewModel";
	}

	@Command
	public void someCommand(@SelectorParam("#msg") Label label) {
		label.setValue("inner command triggered");
		Clients.showNotification("inner command triggered");
	}
}
