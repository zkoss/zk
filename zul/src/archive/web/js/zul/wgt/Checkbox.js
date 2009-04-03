/* Checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	_checked: false,
	
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			var n = this.getSubnode('real');
			if (n) n.disabled = disabled;
		}
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			var n = this.getSubnode('real');
			if (n) n.checked = checked || false;
		}
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (this._name != name) {
			this._name = name;
			var n = this.getSubnode('real');
			if (n) n.name = name;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getSubnode('real');
			if (n) n.tabIndex = tabindex;
		}
	},
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
	labelAttrs_: function () {
		var style = zDom.filterTextStyle(this.domStyle_());
		return style ? ' style="' + style + '"' : "";
	},
	bind_: function (desktop) {
		this.$supers('bind_', arguments);

		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');

		if (zk.gecko2Only)
			zEvt.listen(n, "click", zul.wgt.Checkbox._doClick);
			// bug #2233787 : this is a bug of firefox 2, it need get currentTarget
		zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
		zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
	},
	unbind_: function () {
		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');
		
		if (zk.gecko2Only)
			zEvt.unlisten(n, "click", zul.wgt.Checkbox._doClick);
		zEvt.unlisten(n, "focus", this._fxFocus);
		zEvt.unlisten(n, "blur", this._fxBlur);

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		var real = this.getSubnode('real'),
			checked = real.checked;
		if (checked != this._checked) { //changed
			this.setChecked(checked); //so Radio has a chance to override it
			this.fire('onCheck', val);
		}
		return this.$supers('doClick_', arguments);
	},
	updateDomStyle_: function () {
		var node = this.getNode()
		zDom.setStyles(node, zDom.parseStyle(this.domStyle_()));
		var label = zDom.firstChild(node, "LABEL", true);
		if (label) zDom.setStyles(label, zDom.parseStyle(zDom.filterTextStyle(this.domStyle_())));
	}
});
if (zk.gecko2Only)
	zul.wgt.Checkbox._doClick = function (evt) {
		evt.z_target = evt.currentTarget;
			//bug #2233787 : this is a bug of firefox 2, it need get currentTarget
	};
