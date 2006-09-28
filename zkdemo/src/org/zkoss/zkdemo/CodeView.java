/* CodeView.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 28 21:56:20     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 * The code textbox.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CodeView extends Textbox implements AfterCompose {
	public void afterCompose() {
		execute();
	}
	public void execute() {
		Component view = getFellow("view");
		Components.removeAllChildren(view);
		Executions.createComponentsDirectly(getValue(), "zul", view, null);
	}
}
