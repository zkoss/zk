/* Separator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 12:31:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A separator.
 *
 * @author tomyeh
 */
public class Separator extends XulElement {
	private String _orient = "horizontal";
	private String _spacing;
	private boolean _bar;

	public Separator() {
	}
	/**
	 * @param orient either "horizontal" or "vertical".
	 */
	public Separator(String orient) {
		setOrient(orient);
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}
	/** Returns whether it is a horizontal separator.
	 * @since 3.0.4
	 */
	public boolean isHorizontal() {
		return "horizontal".equals(getOrient());
	}
	/** Returns whether it is a vertical separator.
	 * @since 3.0.4
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}

	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: false
	 */
	public boolean isBar() {
		return _bar;
	}
	/** Sets  whether to display a visual bar as the separator.
	 */
	public void setBar(boolean bar) {
		if (_bar != bar) {
			_bar = bar;
			smartUpdate("class", getSclass());
		}
	}

	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	public String getSpacing() {
		return _spacing;
	}
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	public void setSpacing(String spacing) {
		if (spacing != null)
			if (spacing.length() == 0) spacing = null;
			else spacing = spacing.trim(); //getRealStyle depends on it

		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("style", getRealStyle());
		}
	}

	//-- super --//
	public String getWidth() {
		final String wd = super.getWidth();
		return isHorizontal() || (wd != null && wd.length() > 0)
			|| isPercentInFF() || isSpaceWithMargin() ? wd: _spacing;
	}
	public String getHeight() {
		final String hgh = super.getHeight();
		return isVertical() || (hgh != null && hgh.length() > 0)
			|| isPercentInFF() || isSpaceWithMargin() ? hgh: _spacing;
	}
	protected String getRealStyle() {
		final String style = super.getRealStyle();
		final String spacing =
			isSpaceWithMargin() ? _spacing: splitPercentInFF();
		if (spacing == null)
			return style;

		//3.0.3-compatibility
		final StringBuffer sb = new StringBuffer(64).append("margin:");
		if ("vertical".equals(_orient))
			sb.append("0 ").append(spacing);
		else
			sb.append(spacing).append(" 0");
		return sb.append(';').append(style).toString();
	}
	/** Bug 1526742: FF ignores the width if % is specified.
	 * In this case we have to use margin instead.
	 */
	private boolean isPercentInFF() {
		if (_spacing != null && _spacing.endsWith("%")) {
			final Execution exec = Executions.getCurrent();
			return exec != null && exec.isGecko();
		}
		return false;
	}
	/** Bug 1526742: split % to half (20% -> 10%), if % and FF.
	 */
	private String splitPercentInFF() {
		if (isPercentInFF()) {
			try {
				int v = Integer.parseInt(
					_spacing.substring(0, _spacing.length() - 1).trim());
				if (v > 0)
					return v > 1 ? (v / 2) + "%": "1%";
			} catch (Throwable ex) { //ignore if not recognizable
			}
		}
		return null;
	}

	/** Returns the style class.
	 * If the style class is not defined ({@link #setSclass} is not called
	 * or called with null or empty), it returns the style class based
	 * on {@link #getOrient} and {@link #isBar}.
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;
		return "vertical".equals(getOrient()) ?
			isBar() ?"vsep-bar":"vsep": isBar() ?"hsep-bar":"hsep";
	}

	/** Returns whether to use margins for spacing.
	 * <p>Default: false since 3.0.4.
	 * It indicates the width and height instead of margins
	 * will be used to control the spacing ({@link #getSpacing}).
	 *
	 * <p>If you want to use margins instead of width/height
	 * like 3.0.3 or ealier did, you can specify the sytem property
	 * called "org.zkoss.zul.Separator.spaceWithMargin" with a non-empty
	 * value.
	 * In other words, define it only if you want to apply
	 * the 3.0.3-compatible behavior.
	 * @since 3.0.4
	 */
	public boolean isSpaceWithMargin() { //it cannot be static since EL uses it
		if (_spmargin == null) {
			final String s = Library.getProperty("org.zkoss.zul.Separator.spaceWithMargin");
			_spmargin = Boolean.valueOf(s != null && s.length() > 0);
		}
		return _spmargin.booleanValue();
	}
	private static Boolean _spmargin;

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
}
