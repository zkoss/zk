/* B80_ZK_3120Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 21 14:32:09 CST 2016, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author wenning
 */
public class B80_ZK_3120Composer extends GenericForwardComposer {

	@Wire
	private Textbox benutzer;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		benutzer.setConstraint(new SimpleConstraint(SimpleConstraint.NO_EMPTY, "Missing"));
		benutzer.focus();
	}

}
