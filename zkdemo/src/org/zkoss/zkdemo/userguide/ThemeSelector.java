/* ThemeSelector.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 22, 2010 11:57:19 AM , Created by Sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

/**
 * @author Sam
 *
 */
public class ThemeSelector extends Combobox {
	private HashMap themes = new HashMap();

	public void onCreate() {
		setReadonly(true);
		initChildren();
	}
	
	/**
	 * Switch Theme by selected item
	 */
	public void onSelect() {
		Execution exe = Executions.getCurrent();
		String current = Themes.getThemeStyle(exe);		
		String theme = (String)getSelectedItem().getValue();
		if (!Objects.equals(theme, current)) {
			Themes.setThemeStyle(exe, theme);
			Executions.sendRedirect(null);
		}
	}
	
	private void initChildren() {
		boolean hasSilver = Themes.hasSilvergrayLib();
		boolean hasBreeze = Themes.hasBreezeLib();
		if (!hasSilver && !hasBreeze) {
			setVisible(false);
			return;
		}	
		
		if (Themes.hasBreezeLib()) {
			Comboitem breeze = new Comboitem("Breeze");
			breeze.setValue(Themes.BREEZE_THEME);
			themes.put(Themes.BREEZE_THEME, breeze);
			appendChild(breeze);
		}
		
		Comboitem zk = new Comboitem("Blue");
		zk.setValue(Themes.ZK_THEME);
		themes.put(Themes.ZK_THEME, zk);
		appendChild(zk);
		
		
		if (hasSilver) {
			Comboitem silvergray = new Comboitem("Silvergray");
			silvergray.setValue(Themes.SILVERGRAY_THEME);
			themes.put(Themes.SILVERGRAY_THEME, silvergray);
			appendChild(silvergray);
		}
		

		setSelectedItemByTheme();
	}

	
	/**
	 * Sets the selected comboitem by current theme, if can't find a match theme from cookie
	 * Use default theme as selected item
	 *  
	 * @param defaultItem
	 */
	private void setSelectedItemByTheme () {
		Execution exe = Executions.getCurrent();
		String current = Themes.getThemeStyle(exe);
		Comboitem t =  (Comboitem)themes.get(current);
		if (t != null) {
			setSelectedItem(t);
			return;
		}
		
		Comboitem defListitem = (Comboitem)themes.get(Themes.BREEZE_THEME);
		defListitem = defListitem != null ? defListitem : (Comboitem)themes.get(Themes.ZK_THEME);
		defListitem = defListitem != null ? defListitem : (Comboitem)themes.get(Themes.SILVERGRAY_THEME);
		setSelectedItem(defListitem);
	}
}