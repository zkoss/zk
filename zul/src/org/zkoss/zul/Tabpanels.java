/* Tabpanels.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:08     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	 *
	 * <p>The default style class, i.e., the style class is not defined (i.e.,
	 * {@link #setSclass} is not called or called with null or empty):
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
	 * <p>Note: prior to 3.0.3, the default style class is always "tabpanels".
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;

		final Tabbox tabbox = getTabbox();
		final boolean vert = tabbox != null && tabbox.isVertical();
		final String mold = tabbox != null ? tabbox.getMold(): null;
		return mold == null || "default".equals(mold) ?
			vert ? "vtabpanels": "tabpanels":
			(vert ? "vtabpanels-": "tabpanels-")+mold;
	}
}
