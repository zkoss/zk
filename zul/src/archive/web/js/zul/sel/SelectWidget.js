/* SelectWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
	$define: {
		checkmark: function (checkmark) {
			this.rerender();
		},
		multiple: function () {
			//TODO: handle selection
			if (this._checkmark) this.rerender();
		}
	},

	//super
	focus: function (timeout) {
		var btn;
		if (this.isVisible() && this.canActivate({checkOnly:true})
		&& (btn = this.getSubnode('a'))) {
			zDom.focus(btn, timeout);
			return true;
		}
		return false;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var btn = this.getSubnode('a');
		if (btn) {
			this.domListen_(btn, 'onFocus', 'doFocus_');
			this.domListen_(btn, 'onBlur', 'doBlur_');
		}
	},
	unbind_: function () {
		var btn = this.getSubnode('a');
		if (btn) {
			this.domUnisten_(btn, 'onFocus', 'doFocus_');
			this.domUnisten_(btn, 'onBlur', 'doBlur_');
		}
		this.$supers('unbind_', arguments);
	}
});
