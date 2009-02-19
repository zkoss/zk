/* Area.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Mar 28 00:27:29     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * An area of a {@link Imagemap}.
 *
 * @author tomyeh
 */
public class Area extends AbstractComponent implements org.zkoss.zul.api.Area {
	private String _shape;
	private String _coords;
	private String _tooltiptext;

	public Area() {
	}
	public Area(String coords) {
		setCoords(coords);
	}

	/** Returns the shape of this area.
	 * <p>Default: null (means rectangle).
	 */
	public final String getShape() {
		return _shape;
	}
	/** Sets the shape of this area.
	 *
	 * @exception WrongValueException if shape is not one of
	 * null, "rect", "rectangle", "circle", "circ", "ploygon", and "poly".
	 */
	public final void setShape(String shape) throws WrongValueException {
		if (shape != null)
			if (shape.length() == 0) shape = null;
			else if (!"rect".equals(shape) && !"rectangle".equals(shape)
			&& !"circle".equals(shape) && !"circ".equals(shape)
			&& !"polygon".equals(shape) && !"poly".equals(shape))
				throw new WrongValueException("Unknown shape: "+shape);
		if (!Objects.equals(shape, _shape)) {
			_shape = shape;
			smartUpdate("shape", _shape);
		}
	}

	/** Returns the coordination of this area.
	 */
	public final String getCoords() {
		return _coords;
	}
	/** Sets the coords of this area.
	 * Its content depends on {@link #getShape}:
	 * <dl>
	 * <dt>circle</dt>
	 * <dd>coords="x,y,r"</dd>
	 * <dt>polygon</dt>
	 * <dd>coords="x1,y1,x2,y2,x3,y3..."<br/>
	 * The polygon is automatically closed, so it is not necessary to repeat
	 * the first coordination.</dd>
	 * <dt>rectangle</dt>
	 * <dd>coords="x1,y1,x2,y2"</dd>
	 * </dl>
	 *
	 * <p>Note: (0, 0) is the upper-left corner.
	 */
	public final void setCoords(String coords) {
		if (coords != null && coords.length() == 0) coords = null;
		if (!Objects.equals(coords, _coords)) {
			_coords = coords;
			smartUpdate("coords", _coords);
		}
	}

	/** Returns the text as the tooltip.
	 * <p>Default: null.
	 */
	public String getTooltiptext() {
		return _tooltiptext;
	}
	/** Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext) {
		if (tooltiptext != null && tooltiptext.length() == 0)
			tooltiptext = null;
		if (!Objects.equals(_tooltiptext, tooltiptext)) {
			_tooltiptext = tooltiptext;
			smartUpdate("title", getTooltiptext());
		}
	}

	//-- super --//
	public void setId(String id) {
		final String old = getId();
		super.setId(id);

		id = getId();
		if (!old.equals(id)) smartUpdate("z.aid", id);
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Imagemap))
			throw new UiException("Area's parent must be imagemap, not "+parent);
		super.setParent(parent);
	}
	public void redraw(java.io.Writer out) throws java.io.IOException {
		//TODO
	}
}
