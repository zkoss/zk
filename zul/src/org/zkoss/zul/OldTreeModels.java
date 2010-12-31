/* OldTreeModels.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 31 16:53:39 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.lang.reflect.Method;
import java.util.List;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Utilities to handle the backward compatible (5.0.5 and prior)
 * @author tomyeh
 * @since 5.0.6
 */
/*package*/ class OldTreeModels {
	/*package*/ static Component
	getChildByNode(TreeModel model, Tree tree, Object root, Object node) {
		final int[] path;
		try {
			final Method m = model.getClass().getMethod(
				"getPath", new Class[] {Object.class, Object.class});
			Fields.setAccessible(m, true);
			path = (int[])m.invoke(model, new Object[] {root, node});
		} catch (Exception t) {
			throw UiException.Aide.wrap(t);
		}

		if(path == null || path.length == 0)
			return tree; //backward compatible (see old implementation)

		Treeitem ti = null;
		Treechildren tc = tree.getTreechildren();
		for(int i = 0; i < path.length; i++){
			//If the children are not rendered yet, return null
			final List children = tc.getChildren();
			if(children.size() < path[i] && path[i] < 0)
				return null;

			ti = (Treeitem) children.get(path[i]);
			tc = ti.getTreechildren();
		}
		return ti;
	}
}
