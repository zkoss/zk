/* LayoutRegion.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:17:16 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * A layout region in a border layout.
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public abstract class LayoutRegion extends XulElement implements org.zkoss.zul.api.LayoutRegion {

	private String _border = "normal";
	private int[] _margins = new int[] { 0, 0, 0, 0 };
	private boolean _flex;
	private boolean _autoscroll;

	public LayoutRegion() {
	}

	/**
	 * Returns the border.
	 * <p>
	 * The border actually controls what CSS class to use: If border is null, it
	 * implies "none".
	 * 
	 * <p>
	 * If you also specify the CSS class ({@link #setClass}), it overwrites
	 * whatever border you specify here.
	 * 
	 * <p>
	 * Default: "normal".
	 */
	public String getBorder() {
		return _border;
	}

	/**
	 * Sets the border (either none or normal).
	 * 
	 * @param border
	 *            the border. If null or "0", "none" is assumed.
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border))
			border = "none";
		if (!_border.equals(border)) {
			_border = border;
			smartUpdate("border", _border);
		}
	}

	/**
	 * Returns whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * 
	 * <p>
	 * Default: false.
	 */
	public final boolean isFlex() {
		return _flex;
	}

	/**
	 * Sets whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * 
	 */
	public void setFlex(boolean flex) {
		if (_flex != flex) {
			_flex = flex;
			smartUpdate("flex", _flex);
		}
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
			smartUpdate("imargins", getMargins());
		}
	}

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutoscroll() {
		return _autoscroll;
	}

	/**
	 * Sets whether enable overflow scrolling.
	 */
	public void setAutoscroll(boolean autoscroll) {
		if (_autoscroll != autoscroll) {
			_autoscroll = autoscroll;
			smartUpdate("autoscroll", _autoscroll);
		}
	}

	/**
	 * Returns this regions position (north/south/east/west/center).
	 * 
	 * @see Borderlayout#NORTH
	 * @see Borderlayout#SOUTH
	 * @see Borderlayout#EAST
	 * @see Borderlayout#WEST
	 * @see Borderlayout#CENTER
	 */
	abstract public String getPosition();

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setHeight(String)} and {@link #setWidth(String)}. If this region
	 * is {@link North} or {@link South}, this method will invoke
	 * {@link #setHeight(String)}. If this region is {@link West} or
	 * {@link East}, this method will invoke {@link #setWidth(String)}.
	 * Otherwise it will throw a {@link UnsupportedOperationException}.
	 */
	abstract public void setSize(String size);

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()} and {@link #getWidth()}. If this region is
	 * {@link North} or {@link South}, this method will invoke
	 * {@link #getHeight()}. If this region is {@link West} or {@link East},
	 * this method will invoke {@link #getWidth()}. Otherwise it will throw a
	 * {@link UnsupportedOperationException}.
	 */
	abstract public String getSize();
	
	public String getZclass() {
		return _zclass == null ? "z-" + getPosition() : _zclass;
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (getChildren().size() > 0)
			throw new UiException("Only one child is allowed: " + this);
		super.beforeChildAdded(child, refChild);
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Borderlayout))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (!"normal".equals(_border))
			render(renderer, "border", _border);
		render(renderer, "flex", _flex);
		render(renderer, "autoscroll", _autoscroll);
		if (_margins[0] != 0 || _margins[1] != 0 || _margins[2] != 0 || _margins[3] != 0)
			render(renderer, "margins", getMargins());
	}
}
