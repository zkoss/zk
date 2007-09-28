/* BasicLayoutRegion.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 6, 2007 12:22:24 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkforge.yuiext.layout;

import java.util.Set;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Selectable;

/**
 * This class represents a lightweight region in a layout manager. This class
 * refer to Ext.BasicLayoutRegion by Ext JS at client side.
 * <p>
 * Events:<br/> onSelect.
 * 
 * @author jumperchen
 */
public abstract class BasicLayoutRegion extends LayoutBasedComponent {

	/**
	 * defaults to {top: 0, left: 0, right:0, bottom: 0}
	 */
	private int[] _margins = new int[] { 0, 0, 0, 0 };

	private Contentpanel _panel;

	private int _initialSize = 0;

	/**
	 * This region position is north.
	 */
	public static final String POSITION_NORTH = "north";

	/**
	 * This region position is south.
	 */
	public static final String POSITION_SOUTH = "south";

	/**
	 * This region position is east.
	 */
	public static final String POSITION_EAST = "east";

	/**
	 * This region position is west.
	 */
	public static final String POSITION_WEST = "west";

	/**
	 * This region position is center.
	 */
	public static final String POSITION_CENTER = "center";

	/**
	 * This tab position is top in the region.
	 */
	public static final String TAB_POSITION_TOP = "top";

	/**
	 * This tab position is bottom in the region.
	 */
	public static final String TAB_POSITION_BOTTOM = "bottom";

	/**
	 * Returns this regions position (north/south/east/west/center).
	 * 
	 * @see #POSITION_NORTH
	 * @see #POSITION_SOUTH
	 * @see #POSITION_EAST
	 * @see #POSITION_WEST
	 * @see #POSITION_CENTER
	 */
	abstract public String getPosition();

	/**
	 * Sets margins for the element "{0,1,2,3}" that direction is
	 * "{top,left,right,bottom}"
	 */
	public void setMargins(String margin) {
		if (margin != null) {
			margin = margin.trim();
			if (margin.startsWith("{") && margin.endsWith("}")) {
				final String[] margins = margin.substring(1,
						margin.length() - 1).split(",");
				setMargins(Integer.parseInt(margins[0]), Integer
						.parseInt(margins[1]), Integer.parseInt(margins[2]),
						Integer.parseInt(margins[3]));
			}
		} else {
			throw new IllegalArgumentException(
					"margin must be a formation of {0,1,2,3}. The margin :"
							+ margin);
		}
	}

	/**
	 * Sets margins for the element.
	 */
	public void setMargins(int top, int left, int right, int bottom) {
		boolean f = false;
		if (_margins[0] != top) {
			f = true;
			_margins[0] = top;
		}
		if (_margins[1] != left) {
			f = true;
			_margins[1] = left;
		}
		if (_margins[2] != right) {
			f = true;
			_margins[2] = right;
		}
		if (_margins[3] != bottom) {
			f = true;
			_margins[3] = bottom;
		}
		if (f)
			smartUpdate("z.margins", _margins[0] + "," + _margins[1] + ","
					+ _margins[2] + "," + _margins[3]);

	}

	/**
	 * Returns the margins for the element (defaults to {top: 0, left: 0,
	 * right:0, bottom: 0})
	 */
	public int[] getMargins() {
		return _margins;
	}

	/**
	 * Returns the active panel for this region.
	 */
	public Contentpanel getActivePanel() {
		return _panel;
	}

	/**
	 * Sets the panel to show the specified panel.
	 * 
	 * @param panel
	 */
	public void setActivePanel(Contentpanel panel) {
		if (_panel != panel) {
			_panel = panel;
			smartUpdate("z.showPanel", panel.getUuid());
		}
	}

	/**
	 * Resizes the region to the specified size. For vertical regions (west,
	 * east) this adjusts the width, for horizontal (north, south) the height.
	 * 
	 * @param {Number}
	 *            newSize The new width or height
	 */
	public void resizeTo(int number) {
		smartUpdate("z.resizeTo", Integer.toString(number));
	}

	/**
	 * Returns the initial size.
	 */
	public int getInitialSize() {
		return _initialSize;
	}

	/**
	 * Sets the initial size. For vertical regions (west, east) this adjusts the
	 * width, for horizontal (north, south) the height.
	 * <p>
	 * <strong>Note:</strong>The method only is effected in initialization. If
	 * you want to change the size of this region please call
	 * {@link #resizeTo(int)}.
	 */
	public void setInitialSize(int initialSize) {
		if (_initialSize != initialSize) {
			_initialSize = initialSize;
		}
	}

	// -- Component --//
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		if (_margins[0] != 0 || _margins[1] != 0 || _margins[2] != 0
				|| _margins[3] != 0) {
			HTMLs.appendAttribute(sb, "z.margins", _margins[0] + ","
					+ _margins[1] + "," + _margins[2] + "," + _margins[3]);
			appendInitAttr(sb, "margins");
		}
		HTMLs.appendAttribute(sb, "z.lid", getParent().getUuid());
		if (getInitialSize() != 0) {
			HTMLs.appendAttribute(sb, "z.initialSize", getInitialSize());
			appendInitAttr(sb, "initialSize");
		}
		appendAsapAttr(sb, Events.ON_SELECT);
		return sb.toString();
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl implements Selectable {
		public void selectItemsByClient(Set selItems) {
			if (selItems == null || selItems.size() != 1)
				throw new UiException("Exactly one selected tab is required: "
						+ selItems); // debug purpose
			_panel = (Contentpanel) selItems.iterator().next();
		}
	}

}
