/* BindTreeitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
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
		final Template tm = resoloveTemplate(tree,parent,data,index,-1,"model");
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
				@Override
				public int getIndex() {
					return index;
				}
				@Override
				public Object getEach(){
					return data;
				}
				@Override
				public Integer getEnd(){
					throw new UiException("end attribute is not supported");
				}
			};
			
			final String var = (String) tm.getParameters().get(EACH_ATTR);
			final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
			final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
			final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified
			final Component[] items = tm.create(parent, item, 
				new VariableResolverX() {
					public Object resolveVariable(String name) {
						//shall never call here
						return varnm.equals(name) ? data : null;
					}

					public Object resolveVariable(XelContext ctx, Object base, Object name) throws XelException {
						if (base == null) {
							if(varnm.equals(name)){
								return data;
							}else if(itervarnm.equals(name)){//iteration status
								return iterStatus;
							}
						}
						return null;
					}
				}, null);
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Treeitem ti = (Treeitem)items[0];
			ti.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			addItemReference(ti, tree.getModel().getPath(data), varnm); //kept the reference to the data, before ON_BIND_INIT
			
			ti.setAttribute(itervarnm, iterStatus);
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
