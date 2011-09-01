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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;

/*package*/ class BindingRowRenderer 
implements org.zkoss.zul.RowRenderer, org.zkoss.zul.RowRendererExt, Serializable {
	private static final long serialVersionUID = 200808191425L;
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
		//Bug #1962153: Data binding generates duplicate id in some case (add "_")
		if (clone.getId().length() > 0) {
			clone.setId(null);
		}
					
		//link cloned component with template
		//each Row and its decendants share the same templatemap
		Map templatemap = new HashMap(7);
		BindingRendererUtil.linkTemplates(clone, _template, templatemap, _binder);
		
		//link this template map to parent templatemap (Grid in Grid)
		Map parenttemplatemap = (Map) grid.getAttribute(DataBinder.TEMPLATEMAP);
		if (parenttemplatemap != null) {
			templatemap.put(DataBinder.TEMPLATEMAP, parenttemplatemap);
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
		return DETACH_ON_RENDER;
	}
		
	//-- RowRenderer --//
	public void render(Row row, java.lang.Object bean) {
		final List kids = (List) row.getAttribute(KIDS);
		row.getChildren().addAll(kids);
//			row.removeAttribute(KIDS);
			
		//remove template mark of cloned component and its decendant
		_binder.setupTemplateComponent(row, null); 
			
		//setup clone id
		BindingRendererUtil.setupCloneIds(row);

		//bind bean to the associated listitem and its decendant
		final String varname = (String) _template.getAttribute(DataBinder.VARNAME);
		final Map templatemap = (Map) row.getAttribute(DataBinder.TEMPLATEMAP);
		templatemap.put(varname, bean);

		//apply the data binding
		_binder.loadComponent(row);

		//feature# 3026221: Databinder shall fire onCreate when cloning each items
		DataBinder.postOnCreateEvents(row); //since 5.0.4
	}
	
}
