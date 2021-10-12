/* ComponentParamVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 14:44:51 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

public class ComponentParamVM {
	@Command
	public void componentCommand(@BindingParam("target") Component target) {
		Clients.log(target.getWidgetClass());
	}
}
