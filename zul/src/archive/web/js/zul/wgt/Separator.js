/* Separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	var _shallFixPercent = zk.gecko ? function (wgt) {
			var s;
			return (s = wgt._spacing) && s.endsWith('%');
		} : zk.$void;

/**
 * A separator.
 *  <p>Default {@link #getZclass} is "z-separator".
 */
zul.wgt.Separator = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

	$define: { //zk.def
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: function () {
			this.updateDomClass_();
		},
		/** Returns whether to display a visual bar as the separator.
		 * <p>Default: false
		 * @return boolean
		 */
		/** Sets  whether to display a visual bar as the separator.
		 * @param boolean bar
		 */
		bar: function () {
			this.updateDomClass_();
		},
		/** Returns the spacing.
		 * <p>Default: null (depending on CSS).
		 * @return String
		 */
		/** Sets the spacing.
		 * @param String spacing the spacing (such as "0", "5px", "3pt" or "1em")
		 */
		spacing: function () {
			this.updateDomStyle_();		
		}
	},

	/** Returns whether it is a vertical separator.
	 * @return boolean
	 */
	isVertical: function () {
		return this._orient == 'vertical';
	},

	//super//
	bind_: function () {
		this.$supers(zul.wgt.Separator, 'bind_', arguments);
	},
	getZclass: function () {
		return 'z-separator';
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			bar = this.isBar();
		if (!no || !no.zclass) {
			sc += ' ' + this.$s((this.isVertical() ? 'vertical' + (bar ? '-bar' : '') :
				'horizontal' + (bar ? '-bar' : '')));
		}
		return sc;
	},
	domStyle_: function () {
		var s = this.$supers('domStyle_', arguments);
		if (!_shallFixPercent(this))
			return s;

		//_spacing contains %
		var space = this._spacing,
			v = zk.parseInt(space.substring(0, space.length - 1).trim());
		if (v <= 0) return s;
		v = v >= 2 ? (v / 2) + '%' : '1%';

		return 'margin:' + (this.isVertical() ? '0 ' + v : v + ' 0')
			+ ';' + s;
	},
	getWidth: function () {
		var wd = this.$supers('getWidth', arguments);
		return !this.isVertical() || (wd != null && wd.length > 0)
			|| _shallFixPercent(this) ? wd : this._spacing;
		
	},
	getHeight: function () {
		var hgh = this.$supers('getHeight', arguments);
		return this.isVertical() || (hgh != null && hgh.length > 0)
			|| _shallFixPercent(this) ? hgh : this._spacing;
	}
});

})();