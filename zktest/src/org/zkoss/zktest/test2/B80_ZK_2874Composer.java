/* B80_ZK_2874Composer.java

	Purpose:
		
	Description:
		
	History:
		5:03 PM 10/15/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 */
public class B80_ZK_2874Composer extends SelectorComposer<Window> {

	@Wire Include inc;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		inc.setSrc("B80-ZK-2874_1.zul");
	}

	@Listen("onBookmarkChange=#win") public void submit() {
		System.out.println("Has triggered onBookmarkChange");
		inc.setSrc("B80-ZK-2874_2.zul");
	}

}