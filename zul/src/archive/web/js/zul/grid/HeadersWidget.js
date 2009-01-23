/* HeadersWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:15:38     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.HeadersWidget = zk.$extends(zul.Widget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onColSize', this, null, -1000);
	},
	onColSize: function (evt) {
		var owner = this.parent;
		if (!owner.isFixedLayout()) owner.$class.adjustHeadWidth(owner);
		owner.fire('onInnerWidth', owner.eheadtbl.style.width);
		owner.fireScrollRender(zk.gecko ? 200 : 60);
	},
	isSizable: function () {
		return this._sizable;
	},
	setSizable: function (sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			this.rerender();
		}
	},
	unbind_: function () {
		if (this.hdfaker) zDom.remove(this.hdfaker);
		if (this.bdfaker) zDom.remove(this.bdfaker);
		if (this.ftfaker) zDom.remove(this.ftfaker);
		this.$supers('unbind_', arguments);
	}
});
