/* BindingComboitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Jan 3, 2008 10:54:54 AM , Created by jumperchen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

/**
 * @author jumperchen
 * @since 3.0.2
 */
/*package*/ class BindingComboitemRenderer implements org.zkoss.zul.ComboitemRenderer, org.zkoss.zul.ComboitemRendererExt, Serializable {
	private static final long serialVersionUID = 200808191415L;
	private static final String KIDS = "zkplus.databind.KIDS";
	private Comboitem _template;
	private DataBinder _binder;
	private int x = 0;
	
	public BindingComboitemRenderer(Comboitem template, DataBinder binder) {
		_template = template;
		_binder = binder;
	}

	public void render(Comboitem item, Object bean) throws Exception {
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
	
	public Comboitem newComboitem(Combobox combobox) {
		//clone from template
		final Comboitem clone = (Comboitem)_template.clone();
		//TODO: see if databinder has this kind of Comboitem, if not, add new CollectionListItem 
		//avoid duplicate id error, will set to new id when render()
		//Bug #1962153: Data binding generates duplicate id in some case add "_".
		if (clone.getId().length() > 0) {
			clone.setId(null);
		}
		
		//link cloned component with template
		//each Comboitem and and it decendants share the same templatemap
		Map<Object, Object> templatemap = new HashMap<Object, Object>(8);
		BindingRendererUtil.linkTemplates(clone, _template, templatemap, _binder);
		
		//link this template map to parent templatemap (Combobox in Combobox)
		Map parenttemplatemap = (Map) combobox.getAttribute(DataBinder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
			templatemap.put(DataBinder.TEMPLATEMAP, parenttemplatemap);
		}
		//kept clone kids somewhere to avoid create too many components in browser
		final List<Component> kids = new ArrayList<Component>(clone.getChildren());
		clone.setAttribute(KIDS, kids);
		clone.getChildren().clear();
		return clone;
	}
}
