/* Checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.ereal)
				this.ereal.disabled = disabled;
		}
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked, fromServer) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this.ereal) {
				this.ereal.checked = checked;
				this.ereal.defaultChecked = checked;
				if (!fromServer) this.fire('onCheck', checked);
			}
		}
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			if (this.ereal)
				this.ereal.name = name;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			if (this.ereal)
				this.ereal.tabIndex = tabindex;
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
		var style = zDom.getTextStyle(this.domStyle_());
		return style ? ' style="' + style + '"' : "";
	},
	bind_: function (desktop) {
		this.$super('bind_', desktop);

		var $Checkbox = zul.wgt.Checkbox;
		
		this.ereal = zDom.$(this.uuid + '$real');

		zEvt.listen(this.ereal, "click", $Checkbox.doClick);
		zEvt.listen(this.ereal, "focus", this.proxy(this.domFocus_, '_fxFocus'));
		zEvt.listen(this.ereal, "blur", this.proxy(this.domBlur_, '_fxBlur'));
	},
	unbind_: function () {
		var $Checkbox = zul.wgt.Checkbox,
			n = this.ereal;
		
		if (n) {
			zEvt.unlisten(n, "mousedown", $Checkbox.doClick);			
			zEvt.unlisten(n, "focus", this._fxFocus);
			zEvt.unlisten(n, "blur", this._fxBlur);
		}

		this.ereal = null;
		this.$super('unbind_');
	},
	updateDomStyle_: function () {
		zDom.setStyle(this.node, zDom.parseStyle(this.domStyle_()));
		var label = zDom.firstChild(this.node, "LABEL", true);
		if (label) zDom.setStyle(label, zDom.parseStyle(zDom.getTextStyle(this.domStyle_())));
	}
}, {
	/** Handles onclick for checkbox and radio. */
	doClick: function (evt) {
		var wgt = zk.Widget.$(zk.gecko2Only ? evt.currentTarget : evt);
			// bug #2233787 : this is a bug of firefox 2, it need get currentTarget
		var newval = wgt.ereal.checked;
		if (newval != wgt.ereal.defaultChecked) //changed
			wgt.setChecked(newval);
	}
});