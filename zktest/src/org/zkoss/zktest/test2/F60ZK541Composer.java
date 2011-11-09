/* F60ZK541Composer.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Nov 9, 2011 12:15:20 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.GenericAnnotatedComposer;
import org.zkoss.zk.ui.select.Wire;
import org.zkoss.zk.ui.select.WireZScript;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 *
 * @author simonpai
 */
@WireZScript
public class F60ZK541Composer extends GenericAnnotatedComposer<Window> {
	
	private static final long serialVersionUID = -8244252715921029506L;
	
	@Wire
	private String s;
	
	@Wire("#lb")
	private Label label;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		label.setValue(s);
	}
	
}
