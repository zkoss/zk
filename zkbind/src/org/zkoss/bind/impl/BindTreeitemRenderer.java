/* BindTreeitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * Treeitem renderer for binding.
 * @author henrichen
 * @since 6.0.0
 */
public class BindTreeitemRenderer extends AbstractRenderer implements TreeitemRenderer<Object> {
	private static final long serialVersionUID = 1463169907348730644L;
	public void render(final Treeitem item, final Object data, final int index) throws Exception {
		final Tree tree = item.getTree();
		final Component parent = item.getParent();
		final Template tm = resolveTemplate(tree,parent,data,index,-1,"model");
		if (tm == null) {
			Treecell tc = new Treecell(Objects.toString(data));
			Treerow tr = null;
			item.setValue(data);
			if(item.getTreerow()==null){
				tr = new Treerow();
				tr.setParent(item);
			}else{
				tr = item.getTreerow();
				tr.getChildren().clear();
			}
			tc.setParent(tr);
		} else {

			final ForEachStatus iterStatus = new AbstractForEachStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				
				public int getIndex() {
					return index;
				}
				
				public Object getCurrent(){
					return data;
				}
				
				public Integer getEnd(){
					throw new UiException("end attribute is not supported");
				}
			};
			
			final String var = (String) tm.getParameters().get(EACH_ATTR);
			final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
			final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
			final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified
			
			
			//bug 1188, EL when nested var and itervar
			Object oldVar = parent.getAttribute(varnm);
			Object oldIter = parent.getAttribute(itervarnm);
			parent.setAttribute(varnm, data);
			parent.setAttribute(itervarnm, iterStatus);
			
			final Component[] items = tm.create(parent, item, null, null);
			
			parent.setAttribute(varnm, oldVar);
			parent.setAttribute(itervarnm, oldIter);
			
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Treeitem ti = (Treeitem)items[0];
			ti.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			//zk-1698, mvvm tree performance issue, 
			//no need to use reference binding for tree because we don't allow it to notify a path change also.
			ti.setAttribute(varnm,data); 
			
			ti.setAttribute(itervarnm, iterStatus);
			
			//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
			//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
			ti.setAttribute(TemplateResolver.TEMPLATE_OBJECT, parent.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
			
			//add template dependency
			addTemplateTracking(tree, ti, data, index, -1);
			
			if (ti.getValue() == null) //template might set it
				ti.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", ti);
				//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
