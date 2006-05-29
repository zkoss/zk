/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:00:29     2005, Created by tomyeh@potix.com
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
import com.potix.zul.html.impl.XulElement;

/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:28:21 $
 */
public class Columns extends XulElement {
	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: "+parent);
		final boolean changed = getParent() != parent;
		super.setParent(parent);
		if (changed && parent != null) ((Grid)parent).initAtClient();
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: "+child);
		if (super.insertBefore(child, insertBefore)) {
			final Component parent = getParent();
			if (parent != null) parent.invalidate(INNER);
				//FUTURE: optimize the invalidation to attributes of
				//certain cells
				//It implies initAtClient
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (super.removeChild(child)) {
			final Component parent = getParent();
			if (parent != null) parent.invalidate(INNER);
				//FUTURE: optimize the invalidation to attributes of
				//certain cells
				//It implies initAtClient
			return true;
		}
		return false;
	}
}
