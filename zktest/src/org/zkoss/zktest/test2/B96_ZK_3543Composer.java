/* B96_ZK_3543Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 28 17:45:21 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zuti.zul.Apply;

/**
 * @author jameschu
 */
public class B96_ZK_3543Composer extends SelectorComposer<Component> {
	@Wire("::shadow")
	private Apply statusBox;

	@Wire("::shadow#pageContent")
	private Apply pageContent;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Clients.log(pageContent.getId());
	}
}