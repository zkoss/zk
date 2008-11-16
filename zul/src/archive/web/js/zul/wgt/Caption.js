/* Caption.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 13:01:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Caption = zk.$extends(zul.LabelImageWidget, {
	//super//
	bind_: function (desktop) {
		this.$super('bind_', desktop);

		var n = this.node;
		if (n && zDom.tag(n) == 'LEGEND') //used with Groupbox
			zEvt.listen(n, "click", zul.wgt.Caption.onlegendclk);
	},
	unbind_: function () {
		var n = this.node;
		if (n && zDom.tag(n) == 'LEGEND') //used with Groupbox
			zEvt.unlisten(n, "click", zul.wgt.Caption.onlegendclk);

		//no need to unlisten since DOM elements are GCed
		this.$super('unbind_');
	}
},{
	onlegendclk: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt);
		if (zDom.tag(target) != 'LEGEND')
			return;

		target = zk.Widget.$(target);
		if (target) { //caption
			target = target.parent; //Groupbox
			target.setOpen(!target.isOpen());
		}
	}
});