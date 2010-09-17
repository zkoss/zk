/* BindingListitemRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Feb  3 14:18:27     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

/*package*/ class BindingListitemRenderer 
implements org.zkoss.zul.ListitemRenderer, org.zkoss.zul.ListitemRendererExt, Serializable {
	private static final long serialVersionUID = 200808191417L;
	private static final String KIDS = "zkplus.databind.KIDS";
	private Listitem _template;
	private DataBinder _binder;
	private int x = 0;
	
	public BindingListitemRenderer(Listitem template, DataBinder binder) {
		_template = template;
		_binder = binder;
	}
	
	//-- ListitemRendererExt --//
	public Listitem newListitem(Listbox listbox) {
		//clone from template
		final Listitem clone = (Listitem)_template.clone();
		//TODO: see if databinder has this kind of Listitem, if not, add new CollectionListItem 
		//avoid duplicate id error, will set to new id when render()
		//Bug #1962153: Data binding generates duplicate id in some case (add "_")
		if (clone.getId().length() > 0) {
			clone.setId(null);
		}
					
		//link cloned component with template
		//each Listitem and and it decendants share the same templatemap
		Map templatemap = new HashMap(7);
		linkTemplates(clone, _template, templatemap);
		
		//link this template map to parent templatemap (Listbox in Listbox)
		Map parenttemplatemap = (Map) listbox.getAttribute(DataBinder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
				templatemap.put(DataBinder.TEMPLATEMAP, parenttemplatemap);
		}
		//kept clone kids somewhere to avoid create too many components in browser
		final List kids = new ArrayList(clone.getChildren());
		clone.setAttribute(KIDS, kids);
		clone.getChildren().clear();
		return clone;
	}
	
	public Listcell newListcell(Listitem item) {
		return null;
	}
	
	public int getControls() {
		return DETACH_ON_RENDER;
	}
		
	//-- ListitemRenderer --//
	public void render(Listitem item, java.lang.Object bean) {
		final List kids = (List) item.getAttribute(KIDS);
		item.getChildren().addAll(kids);
//			item.removeAttribute(KIDS);
			
		//remove template mark of cloned component and its decendant
		_binder.setupTemplateComponent(item, null); 
			
		//setup clone id
		setupCloneIds(item);

		//bind bean to the associated listitem and its decendant
		final String varname = (String) _template.getAttribute(DataBinder.VARNAME);
		final Map templatemap = (Map) item.getAttribute(DataBinder.TEMPLATEMAP);
		templatemap.put(varname, bean);

		//apply the data binding
		_binder.loadComponent(item);
		
		//feature# 3026221: Databinder shall fire onCreate when cloning each items
		DataBinder.postOnCreateEvents(item); //since 5.0.4
	}

	//link cloned components with bindings of templates
	private void linkTemplates(Component clone, Component template, Map templatemap) {
		if (_binder.existsBindings(template)) {
			templatemap.put(template, clone);
			clone.setAttribute(DataBinder.TEMPLATEMAP, templatemap);
			clone.setAttribute(DataBinder.TEMPLATE, template);
		}
		
		//Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, 
		//no need to process down since BindingRowRenderer of the under Grid
		//owner will do its own linkTemplates()
		//bug #1888911 databind and Grid in Grid not work when no _var in inner Grid
		if (DataBinder.isCollectionOwner(template)) {
			return;
		}

		final Iterator itt = template.getChildren().iterator();
		final Iterator itc = clone.getChildren().iterator();
		while (itt.hasNext()) {
			final Component t = (Component) itt.next();
			final Component c = (Component) itc.next();
			linkTemplates(c, t, templatemap);	//recursive
		}
	}
	
	//setup id of cloned components (cannot called until the component is attached to Listbox)
	private void setupCloneIds(Component clone) {
		//bug #1813271: Data binding generates duplicate ids in grids/listboxes
		//Bug #1962153: Data binding generates duplicate id in some case (add "_")
		clone.setId(null); //init id to null to avoid duplicate id issue

		//Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, 
		//no need to process down since BindingRowRenderer of the under Grid
		//owner will do its own setupCloneIds()
		//bug #1893247: Not unique in the new ID space when Grid in Grid
		final Component template = DataBinder.getComponent(clone); 
		if (template != null && DataBinder.isCollectionOwner(template)) {
			return;
		}
		
		//Feature #3061671: Databinding foreach keep cloned cmp's id when in spaceowner
		if (!(clone instanceof IdSpace)) { //parent is an IdSpace, so keep the id as is, no need to traverse down
			for(final Iterator it = clone.getChildren().iterator(); it.hasNext(); ) {
				setupCloneIds((Component) it.next()); //recursive
			}
		}
	}
}
