/* Center.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A center region of a borderlayout.
 * <strong>Note:</strong> This component doesn't support the following method,
 * including {@link #setSplittable(boolean)}, {@link #setOpen(boolean)},
 * {@link #setCollapsible(boolean)}, {@link #setMaxsize(int)},
 * {@link #setMinsize(int)}, {@link #setHeight(String)},
 * {@link #setWidth(String)}, {@link #getSize()}, {@link #setSize(String)},
 * and {@link #setVisible(boolean)}.
 * 
 * <p>Default {@link #getZclass}: z-center.
 */
zul.layout.Center = zk.$extends(zul.layout.LayoutRegion, {
	_sumFlexWidth: true, //indicate shall add this flex width for borderlayout. @See _setMinFlexSize in widget.js
	_maxFlexHeight: true, //indicate shall check if the maximum flex height for borderlayout. @See _setMinFlexSize in widget.js
	
	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 * @param String height
	 */
	setHeight: zk.$void,      // readonly
	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 * @param String width
	 */
	setWidth: zk.$void,       // readonly
	/**
	 * This component can't be hidden.
	 * @param boolean visible
	 */
	setVisible: zk.$void,     // readonly
	/**
	 * The size can't be returned in this component.
	 * @return String
	 */
	getSize: zk.$void,        // readonly
	/**
	 * The size can't be specified in this component.
	 * @param String size
	 */
	setSize: zk.$void,        // readonly
	/**
	 * Center region can't be enabled the collapsed margin functionality.
	 * @param String cmargins
	 */
	setCmargins: zk.$void,    // readonly
	/**
	 * Center region can't be enabled the split functionality.
	 * @param boolean splittable
	 */
	setSplittable: zk.$void,  // readonly
	/**
	 * Center region can't be closed.
	 * @param boolean open
	 */
	setOpen: zk.$void,        // readonly
	/**
	 * Center region can't be enabled the collapse functionality.
	 * @param boolean collapsible
	 */
	setCollapsible: zk.$void, // readonly
	/**
	 * Center region can't be enabled the maxsize.
	 * @param int maxsize
	 */
	setMaxsize: zk.$void,     // readonly
	/**
	 * Center region can't be enabled the minsize.
	 * @param int minsize
	 */
	setMinsize: zk.$void,     // readonly
	doMouseOver_: zk.$void,   // do nothing.
	doMouseOut_: zk.$void,    // do nothing.
	doClick_: zk.$void,       // do nothing.
	/**
	 * Returns {@link Borderlayout#CENTER}.
	 * @return String
	 */
	getPosition: function () {
		return zul.layout.Borderlayout.CENTER;
	}
});