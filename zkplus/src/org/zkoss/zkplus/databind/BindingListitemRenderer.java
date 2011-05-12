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
import java.util.List;
import java.util.Map;

import static org.zkoss.lang.Generics.cast;

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
		Map<Object, Object> templatemap = new HashMap<Object, Object>(8);
		BindingRendererUtil.linkTemplates(clone, _template, templatemap, _binder);
		
		//link this template map to parent templatemap (Listbox in Listbox)
		Map parenttemplatemap = (Map) listbox.getAttribute(DataBinder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
			templatemap.put(DataBinder.TEMPLATEMAP, parenttemplatemap);
		}
		//kept clone kids somewhere to avoid create too many components in browser
		final List<Component> kids = new ArrayList<Component>(clone.getChildren());
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
		final List<Component> kids = cast((List) item.getAttribute(KIDS));
		item.getChildren().addAll(kids);
		//item.removeAttribute(KIDS);
			
		//remove template mark of cloned component and its decendant
		_binder.setupTemplateComponent(item, null); 
			
		//setup clone id
		BindingRendererUtil.setupCloneIds(item);

		//bind bean to the associated listitem and its decendant
		final String varname = (String) _template.getAttribute(DataBinder.VARNAME);
		final Map<Object, Object> templatemap = cast((Map) item.getAttribute(DataBinder.TEMPLATEMAP));
		templatemap.put(varname, bean);

		//apply the data binding
		_binder.loadComponent(item);
		
		//feature# 3026221: Databinder shall fire onCreate when cloning each items
		DataBinder.postOnCreateEvents(item); //since 5.0.4
	}
}
