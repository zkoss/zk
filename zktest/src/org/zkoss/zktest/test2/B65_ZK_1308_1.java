/* B65_ZK_1308_1.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 14, 2012 10:22:48 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 *
 */
public class B65_ZK_1308_1 extends B65_ZK_1308{
	 @WireVariable
	 private Page _page;
	 
	 @Override
	 public void doAfterCompose(Window comp) throws Exception {
		 super.doAfterCompose(comp);
		 comp.appendChild(new Label("Subclass: " + _page));
	 }
}
