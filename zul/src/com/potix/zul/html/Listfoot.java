/* Listfoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 13 12:42:31     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

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
 * A row of {@link Listfooter}.
 *
 * <p>Like {@link Listhead}, each listbox has at most one {@link Listfoot}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Listfoot extends XulElement {
	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Listfooter))
			throw new UiException("Unsupported child for listfoot: "+child);
		return super.insertBefore(child, insertBefore);
	}
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		initAtClient();
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		initAtClient();
	}
	private void initAtClient() {
		final Listbox box = getListbox();
		if (box != null) box.initAtClient();
	}
}
