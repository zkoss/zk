/* Borderlayout.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 6, 2007 2:34:19 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.layout;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.ChildChangedAware;

/**
 * This class represents a common layout manager used in desktop applications.
 * This class refer to Ext.BorderLayout by Ext JS at client side.
 * 
 * @author jumperchen
 */
public class Borderlayout extends LayoutBasedComponent {
	private North _north;

	private South _south;

	private West _west;

	private East _east;

	private Center _center;

	public North getNorth() {
		return _north;
	}

	public South getSouth() {
		return _south;
	}

	public West getWest() {
		return _west;
	}

	public East getEast() {
		return _east;
	}

	public Center getCenter() {
		return _center;
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof LayoutRegion))
			throw new UiException("Unsupported child for Borderlayout: "
					+ child);
		if (child instanceof North) {
			if (_north != null && child != _north)
				throw new UiException("Only one nregion child is allowed: "
						+ this);
			_north = (North) child;
		} else if (child instanceof South) {
			if (_south != null && child != _south)
				throw new UiException("Only one sregion child is allowed: "
						+ this);
			_south = (South) child;
		} else if (child instanceof West) {
			if (_west != null && child != _west)
				throw new UiException("Only one wregion child is allowed: "
						+ this);
			_west = (West) child;
		} else if (child instanceof East) {
			if (_east != null && child != _east)
				throw new UiException("Only one eregion child is allowed: "
						+ this);
			_east = (East) child;
		} else if (child instanceof Center) {
			if (_center != null && child != _center)
				throw new UiException("Only one cregion child is allowed: "
						+ this);
			_center = (Center) child;
		}
		return super.insertBefore(child, insertBefore);
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl implements ChildChangedAware {

		public boolean isChildChangedAware() {
			return true;
		}

	}
}
