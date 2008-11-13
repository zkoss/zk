/* Separator.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 12:31:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A separator.
 *  <p>Default {@link #getZclass} as follows: (since 3.5.0)
 *  <ol>
 *  	<li>Case 1: If {@link #getOrient()} is vertical and {@link #isBar()} is false, "z-separator-ver" is assumed</li>
 *  	<li>Case 2: If {@link #getOrient()} is vertical and {@link #isBar()} is true, "z-separator-ver-bar" is assumed</li>
 *  	<li>Case 3: If {@link #getOrient()} is horizontal and {@link #isBar()} is false, "z-separator-hor" is assumed</li>
 *  	<li>Case 4: If {@link #getOrient()} is horizontal and {@link #isBar()} is true, "z-separator-hor-bar" is assumed</li>
 *  </ol>
 *
 * @author tomyeh
 */
public class Separator extends XulElement implements org.zkoss.zul.api.Separator {
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
			smartUpdate("orient", _orient);
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
			smartUpdate("bar", _bar);
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
			else spacing = spacing.trim();

		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("spacing", _spacing);
		}
	}

	//-- super --//
	public String getWidth() {
		final String wd = super.getWidth();
		return isHorizontal() || (wd != null && wd.length() > 0)
			|| isPercentInFF() ? wd: _spacing;
	}
	public String getHeight() {
		final String hgh = super.getHeight();
		return isVertical() || (hgh != null && hgh.length() > 0)
			|| isPercentInFF() ? hgh: _spacing;
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
	
	//super//
	public String getZclass() {
		return _zclass != null ? _zclass:
			"z-separator" + (isVertical() ? "-ver" + (isBar() ? "-bar" : "") :
				"-hor" + (isBar() ? "-bar" : ""));
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "spacing", _spacing);
		render(renderer, "orient", _orient);
		render(renderer, "bar", _bar);
	}
}
