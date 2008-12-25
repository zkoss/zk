/* Separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Separator = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

	isVertical: function () {
		return this._orient == 'vertical';
	},
	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			if (this.desktop) this.updateDomClass_();
		}
	},

	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: false
	 */
	isBar: function () {
		return this._bar;
	},
	/** Sets  whether to display a visual bar as the separator.
	 */
	setBar: function(bar) {
		if (this._bar != bar) {
			this._bar = bar;
			if (this.desktop) this.updateDomClass_();
		}
	},
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	getSpacing: function () {
		return this._spacing;
	},
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	setSpacing: function(spacing) {
		if (this._spacing != spacing) {
			this._spacing = spacing;
			if (this.desktop) this.updateDomStyle_();
		}
	},

	//super//
	getZclass: function () {
		var zcls = this.zclass,
			bar = this.isBar();
		return zcls ? zcls: "z-separator" +
			(this.isVertical() ? "-ver" + (bar ? "-bar" : "") :
				"-hor" + (bar ? "-bar" : ""))
	},
	domStyle_: function (no) {
		var s = this.$super('domStyle_', no);
		if (!this._isPercentGecko()) return s;

		var v = zk.parseInt(_spacing.substring(0, _spacing.length() - 1).trim());
		if (v <= 0) return s;
		v = v >= 2 ? (v / 2) + "%": "1%";

		return 'margin:' + (this.isVertical() ? '0 ' + v: v + ' 0')
			+ ';' + s;
	},
	getWidth: function () {
		var wd = this.$super('getWidth');
		return !this.isVertical() || (wd != null && wd.length() > 0)
			|| this._isPercentGecko() ? wd: this._spacing;
		
	},
	getHeight: function () {
		var hgh = this.$super('getHeight');
		return this.isVertical() || (hgh != null && hgh.length() > 0)
			|| this._isPercentGecko() ? hgh: this._spacing;
	},
	_isPercentGecko: function () {
		return zk.gecko && this._spacing != null && this._spacing.endsWith("%");
	}
});
