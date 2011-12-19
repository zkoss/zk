/* BindTreeitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.IterationStatus;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * Treeitem renderer for binding.
 * @author henrichen
 *
 */
public class BindTreeitemRenderer implements TreeitemRenderer<Object>,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	public void render(final Treeitem item, final Object data) throws Exception {
		final Tree tree = item.getTree();
		final Component parent = item.getParent();
		final Template tm = tree.getTemplate("model");
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
			//will call into BindUiLifeCycle#afterComponentAttached, and apply binding management there
			final String varnm = (String) tree.getAttribute(BinderImpl.VAR); //see BinderImpl#initRendererIfAny
			final String itervarnm = (String) tree.getAttribute(BinderImpl.ITERATION_VAR); //see BinderImpl#initRendererIfAny
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
								return new IterationStatus(){
									@Override
									public int getIndex() {
										return Integer.valueOf(item.getIndex());
									}
								};
							}
						}
						return null;
					}
				}, null);
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Treeitem ti = (Treeitem)items[0];
			ti.setAttribute(varnm, data); //kept the value
			
			ti.setAttribute(itervarnm, new IterationStatus(){//provide iteration status in this context
				@Override
				public int getIndex() {
					return Integer.valueOf(ti.getIndex());
				}
			});
			if (ti.getValue() == null) //template might set it
				ti.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", ti);
				//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
