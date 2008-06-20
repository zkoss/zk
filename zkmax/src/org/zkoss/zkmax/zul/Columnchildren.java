/* ColumnChildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 17:45:11 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * 
 * @author liwn
 */
public class Columnchildren extends XulElement {
	private int[] _margins = new int[] { 0, 0, 0, 0 };

	public Columnchildren() {
		super();
		setSclass("z-plain z-column-children");
	}

	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * Default: "0,0,0,0".
	 */
	public String getMargins() {
		return Utils.intsToString(_margins);
	}

	/**
	 * Sets margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 */
	public void setMargins(String margins) {
		final int[] imargins = Utils.stringToInts(margins, 0);
		if (!Objects.equals(imargins, _margins)) {
			_margins = imargins;
			smartUpdate("z.mars", Utils.intsToString(_margins));
		}
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Columnlayout))
			throw new UiException("Wrong parent: " + parent);
		super.setParent(parent);
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(80).append(super
				.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_SIZE);
		HTMLs.appendAttribute(sb, "z.mars", getMargins());
		return sb.toString();
	}
}
