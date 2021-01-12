/* Hlayout.js

	Purpose:

	Description:

	History:
		Fri Aug  6 11:54:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A horizontal layout.
 * <p>Default {@link #getZclass}: z-hlayout.
 * @since 5.0.4
 */
zul.box.Hlayout = zk.$extends(zul.box.Layout, {
	_valign: 'top',
	$define: { //zk.def
		/** Sets the vertical-align to top or bottom.
		 *
		 * @param String valign the value of vertical-align property
		 * "top", "middle", "bottom".
		 * @since 6.0.0
		 */
		/** Returns the current valign.
		 * @return String
		 */
		valign: function () {
			this.updateDomClass_();
		}
	},
	bind_: function () {
		this.$supers(zul.box.Hlayout, 'bind_', arguments);
		zWatch.listen({_beforeSizeForRead: this, beforeSize: this, onFitSize: this}); //ZK-4476
	},
	unbind_: function () {
		zWatch.unlisten({_beforeSizeForRead: this, beforeSize: this, onFitSize: this});
		this.$supers(zul.box.Hlayout, 'unbind_', arguments);
	},
	isVertical_: function () {
		return false;
	},
	// F60-ZK-537: Hlayout supports valign (top, middle and bottom),
	// set vertical-align to children cause wrong layout on IE6,
	// set it to parent directly
	domClass_: function () {
		var clsnm = this.$supers('domClass_', arguments),
			v;
		if ((v = this._valign) == 'middle')
			clsnm += ' z-valign-middle';
		else if (v == 'bottom')
			clsnm += ' z-valign-bottom';
		return clsnm;
	},
	getFlexDirection_: function () {
		return 'row';
	},
	//ZK-4476
	_beforeSizeForRead: function () {
		var n = this.$n();
		this._beforeSizeWidth = n ? n.offsetWidth : 0;
		for (var xc = this.firstChild; xc; xc = xc.nextSibling) {
			n = xc.$n();
			xc._beforeSizeWidth = n ? n.offsetWidth : 0;
		}
	},
	beforeSize: function () {
		var xc = this.firstChild,
			totalWdCached = this._beforeSizeWidth,
			totalWd = totalWdCached != null ? totalWdCached : this.$n().offsetWidth,
			flexCnt = 0,
			flexWgts = [];
		for (; xc; xc = xc.nextSibling) {
			if (xc.isVisible() && !(zkc = zk(xc)).hasVParent()) {
				var nhflex = xc._nhflex,
					nXc = xc.$n();
				if (nhflex) {
					flexWgts.push({wgt: xc, flex: nhflex});
					flexCnt += nhflex;
				} else if (nXc) {
					var xcOffsetWidthCached = xc._beforeSizeWidth,
						xcOffsetWidth = xcOffsetWidthCached != null ? xcOffsetWidthCached : nXc.offsetWidth;
					totalWd -= xcOffsetWidth;
					xc.$n('chdex').style.width = jq.px0(xcOffsetWidth);
				}
			}
			delete xc._beforeSizeWidth;
		}
		if (flexCnt > 0) {
			var perWd = totalWd / flexCnt;
			for (var i = 0, l = flexWgts.length; i < l; i++)
				flexWgts[i].wgt.$n('chdex').style.width = jq.px0(perWd * flexWgts[i].flex);
		}
		delete this._beforeSizeWidth;
	},
	onFitSize: function () {
		var xc = this.firstChild;
		for (; xc; xc = xc.nextSibling) {
			if (xc.isVisible() && !(zkc = zk(xc)).hasVParent())
				xc.$n('chdex').style.width = '';
		}
	}
});