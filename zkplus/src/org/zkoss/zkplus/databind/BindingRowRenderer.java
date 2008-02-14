/* BindingRowRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 19 11:10:12     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/*package*/ class BindingRowRenderer 
implements org.zkoss.zul.RowRenderer, org.zkoss.zul.RowRendererExt, Serializable {
	private static final String KIDS = "zkplus.databind.KIDS";
	private Row _template;
	private DataBinder _binder;
	private int x = 0;
	
	public BindingRowRenderer(Row template, DataBinder binder) {
		_template = template;
		_binder = binder;
	}
	
	//-- RowRendererExt --//
	public Row newRow(Grid grid) {
		//clone from template
		final Row clone = (Row)_template.clone();
		
		//avoid duplicate id error, will set to new id when render()
		if (!ComponentsCtrl.isAutoId(clone.getId())) {
			clone.setId("@"+ clone.getUuid() + x++);
		}
					
		//link cloned component with template
		//each Row and its decendants share the same templatemap
		Map templatemap = new HashMap(7);
		linkTemplates(clone, _template, templatemap);
		
		//link this template map to parent templatemap (Grid in Grid)
		Map parenttemplatemap = (Map) grid.getAttribute(_binder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
			templatemap.put(_binder.TEMPLATEMAP, parenttemplatemap);
		}
		//kept clone kids somewhere to avoid create too many components in browser
		final List kids = new ArrayList(clone.getChildren());
		clone.setAttribute(KIDS, kids);
		clone.getChildren().clear();
		return clone;
	}
	
	public Component newCell(Row row) {
		return null;
	}
	
	public int getControls() {
		//bug 1869003: Add DETACH_ON_UNLOAD. That is, always use a new Row whenever 
		//model is sync; otherwise Row's children is also reused.
		return DETACH_ON_RENDER | DETACH_ON_UNLOAD;
	}
		
	//-- RowRenderer --//
	public void render(Row row, java.lang.Object bean) {
		final List kids = (List) row.getAttribute(KIDS);
		row.getChildren().addAll(kids);
//			row.removeAttribute(KIDS);
			
		//remove template mark of cloned component and its decendant
		_binder.setupTemplateComponent(row, null); 
			
		//setup clone id
		setupCloneIds(row);

		//bind bean to the associated listitem and its decendant
		final String varname = (String) _template.getAttribute(_binder.VARNAME);
		final Map templatemap = (Map) row.getAttribute(_binder.TEMPLATEMAP);
		templatemap.put(varname, bean);

		//apply the data binding
		_binder.loadComponent(row);
	}

	//link cloned components with bindings of templates
	private void linkTemplates(Component clone, Component template, Map templatemap) {
		if (_binder.existsBindings(template)) {
			templatemap.put(template, clone);
			clone.setAttribute(_binder.TEMPLATEMAP, templatemap);
			clone.setAttribute(_binder.TEMPLATE, template);
		}
		
		//Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, 
		//no need to process down since BindingRowRenderer of the under Grid
		//owner will do its own linkTemplates()
		//bug#1888911 databind and Grid in Grid not work when no _var in inner Grid
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
	
	//setup id of cloned components (cannot called until the component is attached to Grid)
	private void setupCloneIds(Component clone) {
		//bug #1813271: Data binding generates duplicate ids in grids/listboxes
		clone.setId("@" + clone.getUuid() + x++); //init id to @uuid to avoid duplicate id issue

		//Listbox in Listbox, Listbox in Grid, Grid in Listbox, Grid in Grid, 
		//no need to process down since BindingRowRenderer of the under Grid
		//owner will do its own setupCloneIds()
		//bug#1893247: Not unique in the new ID space when Grid in Grid
		final Component template = DataBinder.getComponent(clone); 
		if (template != null && DataBinder.isCollectionOwner(template)) {
			return;
		}
		
		for(final Iterator it = clone.getChildren().iterator(); it.hasNext(); ) {
			setupCloneIds((Component) it.next()); //recursive
		}
	}
}
