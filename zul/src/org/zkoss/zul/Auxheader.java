/* Auxheader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 10:07:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.HeaderElement;

/**
 * An auxiliary header.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Auxheader extends HeaderElement {
	private int _span = 1;

	public Auxheader() {
	}
	public Auxheader(String label) {
		setLabel(label);
	}
	public Auxheader(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns number of columns to span this header.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this header.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) throws WrongValueException {
		if (span <= 0)
			throw new WrongValueException("Positive only");
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", Integer.toString(_span));
		}
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs == null && _span == 1)
			return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		if (clkattrs != null) sb.append(clkattrs);
		if (_span != 1) HTMLs.appendAttribute(sb, "colspan", _span);
		return sb.toString();
	}

	protected void invalidateWhole() {
		Component p = getParent();
		if (p != null) {
			p = p.getParent();
			if (p != null)
				p.invalidate();
		}
	}

	//Component//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Auxhead))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
