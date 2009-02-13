/* ChangeThemeButton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 2, 2007 9:08:20 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;

/**
 * @author Dennis.Chen
 *
 */
public class ChangeThemeButton extends Button{
	
	
	public void onCreate(){
		Radiogroup frg = (Radiogroup)getFellow("frg");
		Execution exe = Executions.getCurrent();
		String fs = FontSizeThemeProvider.getFontSizeCookie(exe);
		if("lg".equals(fs)){
			frg.setSelectedIndex(2);
		}else if("sm".equals(fs)){
			frg.setSelectedIndex(0);
		}else{
			frg.setSelectedIndex(1);
		}
	}
	
	public void onClick() {
		Radiogroup frg = (Radiogroup)getFellow("frg");
		Execution exe = Executions.getCurrent();
		FontSizeThemeProvider.setFondSizeCookie(exe,frg.getSelectedItem().getValue());
		exe.sendRedirect("/userguide?id=theme");
	}
}
