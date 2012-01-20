/* BindingRendererUtil.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Apr 25, 2011 4:44:06 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Iterator;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;

/**
 * @author simonpai
 * @since 5.0.7
 */
public class BindingRendererUtil {
	
	/**
	 * Link cloned components with bindings of templates
	 */
	/*package*/ static void linkTemplates(Component clone, Component template, Map<Object, Object> templatemap, DataBinder binder) {
		if (binder.existsBindings(template)) {
			templatemap.put(template, clone);
			clone.setAttribute(DataBinder.TEMPLATEMAP, templatemap);
			clone.setAttribute(DataBinder.TEMPLATE, template);
		}
		
		final Iterator<Component> itt = template.getChildren().iterator();
		final Iterator<Component> itc = clone.getChildren().iterator();
		while (itt.hasNext()) {
			final Component t = itt.next();
			final Component c = itc.next();
			// skip Listitem, Row, Comboitem
			// Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, etc.
			// no need to process down since BindingRowRenderer of the under collection
			// item will do its own linkTemplates() 
			if (isSkippable(t)) //bug#1968615.
				continue;
			linkTemplates(c, t, templatemap, binder);	//recursive
		}
	}
	
	/**
	 * Check if comp can be waived from linking templates
	 * @param comp
	 */
	private static boolean isSkippable(Component comp) {
		// Bug: B50-3183438: skip only when it has model
		if(comp instanceof Comboitem) {
			Combobox b = (Combobox)((Comboitem) comp).getParent();
			if(b != null && b.getModel() != null)
				return true;
		} else if(comp instanceof Row) {
			Grid g = ((Row) comp).getGrid();
			if(g != null && g.getModel() != null)
				return true;
		} else if(comp instanceof Listitem) {
			Listbox b = ((Listitem) comp).getListbox();
			if(b != null && b.getModel() != null)
				return true;
		}
		return false;
	}
	
	/**
	 * Setup id of cloned components (cannot called until the component is attached to Listbox)
	 */
	/*package*/ static void setupCloneIds(Component clone) {
		//bug #1813271: Data binding generates duplicate ids in grids/listboxes
		//Bug #1962153: Data binding generates duplicate id in some case (add "_")
		clone.setId(null); //init id to null to avoid duplicate id issue

		//Feature #3061671: Databinding foreach keep cloned cmp's id when in spaceowner
		if (!(clone instanceof IdSpace)) { //parent is an IdSpace, so keep the id as is, no need to traverse down
			for(final Iterator it = clone.getChildren().iterator(); it.hasNext(); ) {
				final Component kid = (Component) it.next();
				// skip Listitem, Row, Comboitem
				// Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, etc. 
				// no need to process down since BindingRowRenderer of the under collection
				// item will do its own setupCloneIds()
				if (isSkippable(kid)) //bug#1968615.
					continue;
				setupCloneIds(kid); //recursive
			}
		}
	}

}
