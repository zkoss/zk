/* Checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
_zkc = zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	_checked: false,

	$define: {
		disabled: function (v) {
			var n = this.getSubnode('real');
			if (n) n.disabled = v;
		},
		checked: function (v) {
			var n = this.getSubnode('real');
			if (n) n.checked = v;
		},
		name: function (v) {
			var n = this.getSubnode('real');
			if (n) n.name = v || '';
		},
		tabindex: function (v) {
			var n = this.getSubnode('real');
			if (n) n.tabIndex = v >= 0 ? v: '';
		}
	},

	//super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-checkbox";
	},
	contentAttrs_: function () {
		var html = '', v = this.getName(); // cannot use this._name for radio
		if (v)
			html += ' name="' + v + '"';
		if (this._disabled)
			html += ' disabled="disabled"';
		if (this._checked)
			html += ' checked="checked"';
		v = this._tabindex;
		if (v >= 0)
			html += ' tabindex="' + v + '"';
		return html;
	},
	bind_: function (desktop) {
		this.$supers('bind_', arguments);

		var n = this.getSubnode('real');

		if (zk.gecko2_)
			jq(n).click(zul.wgt.Checkbox._domClick);
		this.domListen_(n, "onFocus", "doFocus_");
		this.domListen_(n, "onBlur", "doBlur_");
	},
	unbind_: function () {
		var n = this.getSubnode('real');
		
		if (zk.gecko2_)
			jq(n).unbind("click", zul.wgt.Checkbox._domClick);
		this.domUnlisten_(n, "onFocus", "doFocus_");
		this.domUnlisten_(n, "onBlur", "doBlur_");

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		var real = this.getSubnode('real'),
			checked = real.checked;
		if (checked != this._checked) { //changed
			this.setChecked(checked); //so Radio has a chance to override it
			this.fire('onCheck', checked);
		}
		return this.$supers('doClick_', arguments);
	},
	getTextNode_: function () {
		return zDom.firstChild(this.getNode(), "LABEL");
	}
});
if (zk.gecko2_)
	_zkc._domClick = function (evt) {
		evt.target = evt.currentTarget;
			//bug #2233787 : this is a bug of firefox 2, it need get currentTarget
	};

