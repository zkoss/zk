/* BindingRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Feb  3 14:18:27     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListitemRendererExt;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/*package*/ class BindingRenderer implements org.zkoss.zul.ListitemRenderer, org.zkoss.zul.ListitemRendererExt {
	private static final String KIDS = "zkplus.databind.KIDS";
	private Listitem _template;
	private DataBinder _binder;
	private int x = 0;
	
	public BindingRenderer(Listitem template, DataBinder binder) {
		_template = template;
		_binder = binder;
	}
	
	//-- ListitemRendererExt --//
	public Listitem newListitem(Listbox listbox) {
		//clone from template
		final Listitem clone = (Listitem)_template.clone();
		
		//avoid duplicate id error, will set to new id when render()
		if (!ComponentsCtrl.isAutoId(clone.getId())) {
			clone.setId("@"+ clone.getUuid() + x++);
		}
					
		//link cloned component with template
		//each Listitem and and it decendants share the same templatemap
		Map templatemap = new HashMap(7);
		linkTemplates(clone, _template, templatemap);
		
		//link this template map to parent templatemap (Listbox in Listbox)
		Map parenttemplatemap = (Map) listbox.getAttribute(_binder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
				templatemap.put(_binder.TEMPLATEMAP, parenttemplatemap);
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
	
	public boolean shallDetachOnRender(Listcell cell) {
		return true;
	}
		
	//-- ListitemRenderer --//
	public void render(Listitem item, java.lang.Object bean) {
		final List kids = (List) item.getAttribute(KIDS);
		//kids is null when it is a simple rerender without going thru newListitem()
		if (kids != null) {
			item.getChildren().addAll(kids);
			item.removeAttribute(KIDS);
			
			//remove template mark of cloned component and its decendant
			_binder.setupTemplateComponent(item, null); 
			
			//setup clone id
			setupCloneIds(item);
		}

		//bind bean to the associated listitem and its decendant
		final String varname = (String) _template.getAttribute(_binder.VARNAME);
		final Map templatemap = (Map) item.getAttribute(_binder.TEMPLATEMAP);
		templatemap.put(varname, bean);

		//apply the data binding
		_binder.loadComponent(item);
	}

	//link cloned components with bindings of templates
	private void linkTemplates(Component clone, Component template, Map templatemap) {
		if (_binder.existsBindings(template)) {
			templatemap.put(template, clone);
			clone.setAttribute(_binder.TEMPLATEMAP, templatemap);
			clone.setAttribute(_binder.TEMPLATE, template);
		}
		
		//Listbox in Listbox, no need to process
		if (template instanceof Listbox) {
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
		clone.setId("@"+clone.getUuid()); //init id to @uuid to avoid duplicate id issue

		//Listbox in Listbox, no need to process down
		if (clone instanceof Listbox) {
			return;
		}
		
		for(final Iterator it = clone.getChildren().iterator(); it.hasNext(); ) {
			setupCloneIds((Component) it.next()); //recursive
		}
	}
}
