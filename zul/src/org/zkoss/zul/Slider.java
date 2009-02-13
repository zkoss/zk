/* Slider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 20:16:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Scrollable;

import org.zkoss.zul.impl.XulElement;

/**
 * A slider.
 *
 * <p>To customize the look, you can specify the style class with
 * {@link #setSclass}. Then, the following style classes are generated
 * to style the look. Assume that the style class is "slider"
 * <ul>
 * <li>slider-bk: the center background</li>
 * <li>slider-bkl: the left background</li>
 * <li>slider-bkr: the right background</li>
 * <li>slider-btn: the bottom background</li>
 * </ul>
 *
 * <p>If {@link #getSclass} is empty and {@link #getMold} is "default",
 * "slider" is assumed. If {@link #getMold} is "sphere", "slidersph" is assumed.
 *
 * @author tomyeh
 */
public class Slider extends XulElement {
	private int _curpos, _maxpos = 100, _pginc = 10;
	/** The name. */
	private String _name;
	private String _slidingtext = "{0}";

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

	/**
	 * Returns the sliding text.
	 * <p>Default : "{0}"
	 * @since 3.0.1
	 */
	public String getSlidingtext() {
		return _slidingtext;
	}
	
	/**
	 * Sets the sliding text.
	 * The syntax "{0}" will be replaced with the position at client side.
	 * @since 3.0.1
	 */
	public void setSlidingtext(String slidingtext) {		
		if (slidingtext == null || slidingtext.length() == 0)
			slidingtext = "{0}";
		if (!_slidingtext.equals(slidingtext)) {
			_slidingtext = slidingtext;
			smartUpdate("z.slidingtext", _slidingtext);
		}
	}
	/** Returns the current position of the slider.
	 *
	 * <p>Default: 0.
	 */
	public final int getCurpos() {
		return _curpos;
	}
	/** Sets the current position of the slider.
	 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
	 * {@link #getMaxpos} is assumed.
	 */
	public final void setCurpos(int curpos)
	throws WrongValueException {
		if (curpos < 0) curpos = 0;
		else if (curpos > _maxpos) curpos = _maxpos;

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
	 *
	 * @exception WrongValueException if non-positive maxpos is passed
	 */
	public final void setMaxpos(int maxpos)
	throws WrongValueException {
		if (maxpos <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+maxpos);

		if (_maxpos != maxpos) {
			if (_curpos > maxpos)
				setCurpos(maxpos);
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

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @since 3.0.0
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name the name of this component.
	 * @since 3.0.0
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("z.name", _name);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.name", _name);
		HTMLs.appendAttribute(sb, "z.curpos", _curpos);
		HTMLs.appendAttribute(sb, "z.maxpos", _maxpos);
		HTMLs.appendAttribute(sb, "z.pginc", _pginc);
		HTMLs.appendAttribute(sb, "z.slidingtext", getSlidingtext());
		
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
