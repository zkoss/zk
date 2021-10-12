/* DefaultParamVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 12:27:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Default;
import org.zkoss.zk.ui.util.Clients;

public class DefaultParamVM {
	@Command
	public void test(@BindingParam("number") @Default("-1") int number) {
		Clients.log("test param: " + number);
	}
}
