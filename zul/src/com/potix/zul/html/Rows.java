/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:39     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.au.AuScript;

import com.potix.zul.html.impl.XulElement;

/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/29 04:28:26 $
 */
public class Rows extends XulElement {
	/** Returns the grid that contains this rows. */
	public Grid getGrid() {
		return (Grid)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for rows: "+parent);
		final boolean changed = getParent() != parent;
		super.setParent(parent);
		if (changed && parent != null) ((Grid)parent).initAtClient();
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		return super.insertBefore(child, insertBefore);
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);

		final Component p = getParent();
		if (p != null) {
			((Grid)p).initAtClient();
			response("stripe",
				new AuScript(this, "zkGrid.stripe('"+p.getUuid()+"')"));
		}
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final Component p = getParent();
		if (p != null) {
			((Grid)p).initAtClient();
			response("stripe",
				new AuScript(this, "zkGrid.stripe('"+p.getUuid()+"')"));
		}
    }
}
