/* Absolutechildren.js

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * <p>A container component that can contain any other ZK component and can only 
 * be contained as direct child of Absolutelayout component. It can be absolutely
 * positioned within Absolutelayout component by either setting "x" and "y"
 * attribute or calling {@link #setX(int)} and {@link #setY(int)} methods.
 * 
 * <p>Default {@link #getZclass}: z-absolutechildren.
 * 
 * @author ashish
 * @since 6.0.0
 */
zul.layout.Absolutechildren = zk.$extends(zul.Widget, {
	_x: 0,
	_y: 0,
	$define: {
		/**
		 * Sets current "x" position within parent container component.
		 * <p>Default: 0
		 * @param int x the x position
		 */
		/**
		 * Returns the current "x" position within parent container component
		 * @return int
		 */
		x: function () {
			if (this.desktop) {
				this._rePositionX();
			}
		},
		/**
		 * Sets current "y" position within parent container component.
		 * <p>Default: 0
		 * @param int y the y position
		 */
		/**
		 * Returns the current "y" position within parent container component
		 * @return int
		 */
		y: function () {
			if (this.desktop) {
				this._rePositionY();
			}
		}
	},
	_rePositionBoth: function() {
		this._rePositionX();
		this._rePositionY();
	},
	_rePositionX: function() {
		jq(this.$n()).css("left", this._x);
	},
	_rePositionY: function() {
		jq(this.$n()).css("top", this._y);
	},
	bind_: function () {
		this.$supers(zul.layout.Absolutechildren, 'bind_', arguments);
		this._rePositionBoth();
	}
});
