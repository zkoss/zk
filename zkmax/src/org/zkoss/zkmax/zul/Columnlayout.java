/* Columnlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 10:42:53 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;

/**
 * 
 * @author gracelin
 */
public class Columnlayout extends HtmlBasedComponent {

	public Columnlayout() {
		setSclass("z-column-layout");
	}

	/**
	 * Re-size this layout component.
	 */
	public void resize() {
		smartUpdate("z.resize", "");
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Columnchildren))
			throw new UiException("Unsupported child for Columnlayout: "
					+ child);
		smartUpdate("z.chchg", true);
		return super.insertBefore(child, insertBefore);
	}
}
