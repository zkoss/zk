/* B65_ZK_1634Renderer.java

	Purpose:
		
	Description:
		
	History:
		8:21 PM 9/18/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

/**
 * @author jumperchen
 */
public class B65_ZK_1634Renderer implements TreeitemRenderer {
	public static class VR implements org.zkoss.xel.VariableResolver {
		Object each;

		public VR(Object each) {
			this.each = each;
		}

		public Object resolveVariable(String name) {
			if ("each".equals(name)) {
				return each;
			}
			return null;
		}
	}

	public void render(Treeitem ti, Object node, int index) {
		Tree tree = ti.getTree();
		Component parent = ti.getParent();

		if (tree == null) {
			throw new RuntimeException("Tree is null");
		}
		if (parent == null) {
			throw new RuntimeException("Parent is null");
		}

		org.zkoss.zk.ui.util.Template tm = tree.getTemplate("model");

		final Object each = node;

		org.zkoss.xel.VariableResolver vr = new VR(node);

		Component[] items = tm.create(ti.getParent(), ti, vr, null);
		if (items.length != 1)
			throw new UiException(
					"The model template must have exactly one item, not "
							+ items.length);

		Treeitem nti = (Treeitem) items[0];
		if (nti.getValue() == null) //template might set it
			nti.setValue(node);
		ti.setAttribute("org.zkoss.zul.model.renderAs", nti);
		//indicate a new item is created to replace the existent one
		ti.detach();
	}

}
