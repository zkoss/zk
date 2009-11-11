/* Separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Separator = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

	$define: { //zk.def
		orient: _zkf = function () {
			this.updateDomClass_();
		},
		bar: _zkf,
		spacing: function () {
			this.updateDomStyle_();
		}
	},

	isVertical: function () {
		return this._orient == 'vertical';
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);

		var n;
		if (zk.ie && (n = this.$n()).offsetWidth <= 2)
			n.style.backgroundPosition = "left top"; //2088712
	},

	getZclass: function () {
		var zcls = this.zclass,
			bar = this.isBar();
		return zcls ? zcls: "z-separator" +
			(this.isVertical() ? "-ver" + (bar ? "-bar" : "") :
				"-hor" + (bar ? "-bar" : ""))
	},
	domStyle_: function () {
		var s = this.$supers('domStyle_', arguments);
		if (!this._isPercentGecko()) return s;

		var v = zk.parseInt(this._spacing.substring(0, this._spacing.length - 1).trim());
		if (v <= 0) return s;
		v = v >= 2 ? (v / 2) + "%": "1%";

		return 'margin:' + (this.isVertical() ? '0 ' + v: v + ' 0')
			+ ';' + s;
	},
	getWidth: function () {
		var wd = this.$supers('getWidth', arguments);
		return !this.isVertical() || (wd != null && wd.length > 0)
			|| this._isPercentGecko() ? wd: this._spacing;
		
	},
	getHeight: function () {
		var hgh = this.$supers('getHeight', arguments);
		return this.isVertical() || (hgh != null && hgh.length > 0)
			|| this._isPercentGecko() ? hgh: this._spacing;
	},
	_isPercentGecko: function () {
		return zk.gecko && this._spacing != null && this._spacing.endsWith("%");
	}
});
