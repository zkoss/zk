/* Separator.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 12:31:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A separator.
 *  <p>Default {@link #getZclass} is "z-separator".
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
	 * @param spacing the spacing (such as "0", "5px", "3pt")
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

	//super//
	public String getZclass() {
		return _zclass == null ? "z-separator" : _zclass;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "spacing", _spacing);
		render(renderer, "orient", _orient);
		render(renderer, "bar", _bar);
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
}
