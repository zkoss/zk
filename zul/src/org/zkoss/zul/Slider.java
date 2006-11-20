/* Slider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 20:16:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Scrollable;

import org.zkoss.zul.impl.XulElement;

/**
 * A slider.
 *
 * @author tomyeh
 */
public class Slider extends XulElement {
	private int _curpos, _maxpos = 100, _pginc = 10;

	public Slider() {
		setWidth("100px");
	}
	/**
	 * @param curpos the current position (default: 0)
	 */
	public Slider(int curpos) {
		this();
		setCurpos(curpos);
	}

	/** Returns the current position of the slider.
	 *
	 * <p>Default: 0.
	 */
	public final int getCurpos() {
		return _curpos;
	}
	/** Sets the current position of the slider.
	 */
	public final void setCurpos(int curpos)
	throws WrongValueException {
		if (curpos < 0)
			throw new WrongValueException("Negative is not allowed: "+curpos);
		if (_curpos != curpos) {
			_curpos = curpos;
			smartUpdate("z.curpos", _curpos);
		}
	}

	/** Returns the maximum position of the slider.
	 *
	 * <p>Default: 100.
	 */
	public final int getMaxpos() {
		return _maxpos;
	}
	/** Sets the maximum position of the slider.
	 */
	public final void setMaxpos(int maxpos)
	throws WrongValueException {
		if (maxpos <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+maxpos);
		if (_maxpos != maxpos) {
			_maxpos = maxpos;
			smartUpdate("z.maxpos", _maxpos);
		}
	}

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked. 
	 *
	 * <p>Default: 10.
	 */
	public final int getPageIncrement() {
		return _pginc;
	}
	/** Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 */
	public final void setPageIncrement(int pginc)
	throws WrongValueException {
		if (pginc <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+pginc);
		if (_pginc != pginc) {
			_pginc = pginc;
			smartUpdate("z.pginc", _pginc);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.curpos", _curpos);
		HTMLs.appendAttribute(sb, "z.maxpos", _maxpos);
		HTMLs.appendAttribute(sb, "z.pginc", _pginc);

		appendAsapAttr(sb, Events.ON_SCROLL);
		appendAsapAttr(sb, Events.ON_SCROLLING);
		appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
			//no z.dbclk to avoid confusion
			//no z.lfclk since it will be supported by sld.js

		return sb.toString();
	}

	//-- Component --//
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Scrollable {
		//-- Scrollable --//
		public final void setCurposByClient(int curpos) {
			if (curpos < 0)
				throw new WrongValueException("Negative is not allowed: "+curpos);
			_curpos = curpos;
		}
	}
}
