/* B80_ZK_3084VM.java

	Purpose:

	Description:

	History:
		Mon Feb 22 15:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3084VM {

	@Command
	public void printTrue(@BindingParam("e") Event event) throws Exception {
		throw new Exception("It should not be triggered!");
	}
	
	@Command
	public void printFalse(@BindingParam("e") Event event){
		Clients.log("false");
	}
	
}
