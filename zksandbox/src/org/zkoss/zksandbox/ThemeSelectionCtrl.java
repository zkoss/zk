package org.zkoss.zksandbox;

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

import java.util.List;
import java.util.TreeSet;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import org.zkoss.zksandbox.ThemesSnd;

/**
 * Handles theme selection work. Upon selecting a new theme, it will write
 * zktheme=[theme name] in the cookie, and refresh the current page.
 * @author simon
 */
public class ThemeSelectionCtrl extends GenericForwardComposer {
	
	private Listbox themeSelectListbox;
	
	public void onSelect$themeSelectListbox(Event event){
		// get current theme
		String currentTheme = ThemesSnd.getCurrentTheme();
		
		// get selected theme from listbox
		Listitem seldItem = themeSelectListbox.getSelectedItem();
		if (seldItem == null)
			return;
		String selectedTheme = seldItem.getValue().toString();
		
		if(selectedTheme.equals(currentTheme)) return;
		
		// write cookie, redirect
		ThemesSnd.setTheme(execution, selectedTheme);
		Executions.sendRedirect(null);
	}
	
	public void onAfterRender$themeSelectListbox(Event event) {
		String name = ThemesSnd.getCurrentTheme();
		List chd = themeSelectListbox.getItems();
		for (Object obj : chd) {
			Listitem item = (Listitem)obj;
			if (name.equals(item.getValue())) {
				themeSelectListbox.setSelectedItem(item);
				break;
			}
		}
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initThemeListbox();
	}
	
	private final static String PREFIX_KEY_THEME_DISPLAYS = "org.zkoss.theme.display.";
	private void initThemeListbox() {
		themeSelectListbox.setItemRenderer(new ListitemRenderer() {
			
			@Override
			public void render(Listitem item, Object obj) throws Exception {
				String name = (String)obj;
				String display = Library.getProperty(PREFIX_KEY_THEME_DISPLAYS + name);
				item.setLabel(display);
				item.setValue(name);
			}
		});

		String val = Library.getProperty(ThemesSnd.getProperty("THEME_NAMES"));
		if (Strings.isEmpty(val))
			return;
		TreeSet themes = new TreeSet();
		String[] vals = val.split(";");
		for (int i = 0; i < vals.length; i++) {
			String name = vals[i];
			if (!Strings.isEmpty(name))
				themes.add(name);
		}
		ListModelList model = new ListModelList();
		model.addAll(themes);
		
		themeSelectListbox.setModel(model);
	}
}
