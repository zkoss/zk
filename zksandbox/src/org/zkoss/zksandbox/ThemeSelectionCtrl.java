/* ThemeSelectionCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 30, 2010 12:08:29 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zksandbox;

import org.zkoss.lang.Strings;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.theme.Themes;

/**
 * Handles theme selection work. Upon selecting a new theme, it will write
 * zktheme=[theme name] in the cookie, and refresh the current page.
 * @author simon
 */
public class ThemeSelectionCtrl extends GenericForwardComposer<Component> {
	
	private static final long serialVersionUID = 6640048095093393013L;
	
	private Listbox themeSelectListbox;
	
	public void onSelect$themeSelectListbox(Event event){
		// get current theme
		String currentTheme = Themes.getCurrentTheme();
		
		// get selected theme from listbox
		Listitem seldItem = themeSelectListbox.getSelectedItem();
		if (seldItem == null)
			return;
		
		String selectedTheme = seldItem.getValue().toString();
		if(selectedTheme.equals(currentTheme)) 
			return;
		
		// write cookie, redirect
		Themes.setTheme(execution, selectedTheme);
		Executions.sendRedirect(null);
	}
	
	public void onAfterRender$themeSelectListbox(Event event) {
		
		String name = Themes.getCurrentTheme();
		if (Strings.isEmpty(name)) {
			themeSelectListbox.setVisible(false);
			return;
		}
		
		for (Listitem item : themeSelectListbox.getItems()) {
			if (name.equals(item.getValue())) {
				themeSelectListbox.setSelectedItem(item);
				break;
			}
		}
		
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Double version = execution.getBrowser("mobile");
		if (version != null && version > 1) {
			comp.setVisible(false);
			return;
		}
		themeSelectListbox.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object obj, int index) throws Exception {
				String name = (String) obj;
				item.setLabel(Themes.getDisplayName(name));
				item.setValue(name);
			}
		});
		
		ListModelList<String> model = new ListModelList<String>(Themes.getThemes());
		themeSelectListbox.setModel(model);
	}
	
}
