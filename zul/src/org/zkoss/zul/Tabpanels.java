/* Tabpanels.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:08     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A collection of tab panels.
 *
 * <p>Default {@link #getSclass}:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td>sclass</td><td>tabbox's mold</td>
 * <td>tabbox's orient {@link Tabbox#getOrient}</td>
 * </tr>
 * <tr><td>tabpanels</td><td>default</td><td>horizontal</td></tr>
 * <tr><td>tabpanels-<em>something</em></td><td><em>something</em></td><td>horizontal</td></tr>
 * <tr><td>vtabpanels</td><td>default</td><td>vertical</td></tr>
 * <tr><td>vtabpanels-<em>something</em></td><td><em>something</em></td><td>vertical</td></tr>
 * </table>
 *
 * @author tomyeh
 */
public class Tabpanels extends XulElement {
	public Tabpanels() {
	}

	/** Returns the tabbox owns this component.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Tabbox getTabbox() {
		return (Tabbox)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tabbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Tabpanel))
			throw new UiException("Unsupported child for tabpanels: "+child);
		return super.insertBefore(child, insertBefore);
	}
	/** Returns the style class.
	 * @since 3.5.0
	 * <p>Note: the default style class is always "z-tabpanels".
	 */
	public String getMoldSclass(){		
		final Tabbox tabbox = getTabbox();
		final boolean vert = tabbox != null && tabbox.isVertical();
		final String mold = tabbox != null ? tabbox.getMold(): null;
		return mold == null || "default".equals(mold) ?
				vert ? "z-vtabpanels": "z-tabpanels":
				(vert ? "z-vtabpanels-": "z-tabpanels-")+mold;
	}	
}
