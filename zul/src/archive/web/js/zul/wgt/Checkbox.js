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
	
	/** Returns whether this button is disabled.
	 * <p>Default: false.
	 */
	isDisabled: function () {
		return this._disabled;
	},
	/** Sets whether this button is disabled
	 */
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.ereal)
				this.ereal.disabled = disabled;
		}
	},
	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	isChecked: function () {
		return _checked;
	},
	/** Sets whether it is checked.
	 */
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
	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	getName: function () {
		return this._name;
	},
	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name the name of this component.
	 */
	setName: function (name) {
		if (name != null && name.length() == 0) name = null;
		if (this._name != name) {
			this._name = name;
			if (this.ereal)
				this.ereal.name = name;
		}
	},
	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	getTabindex: function () {
		return this._tabindex;
	},
	/** Sets the tab order of this component.
	 */
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
	_contentAttrs: function () {
		var html = '', v = this._name;
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
	_labelAttrs: function () {
		var style = zDom.getTextStyle(this.domStyle_());
		return style ? ' style="' + style + '"' : "";
	},
	bind_: function (desktop) {
		this.$super('bind_', desktop);

		var $Checkbox = zul.wgt.Checkbox;
		
		this.ereal = zDom.$(this.uuid + '$real');

		zEvt.listen(this.ereal, "click", $Checkbox.doClick);
		this._onFocus = this.proxy(this.domFocus_);
		this._onBlur = this.proxy(this.domBlur_);
		zEvt.listen(this.ereal, "focus", this._onFocus);
		zEvt.listen(this.ereal, "blur", this._onBlur);
	},
	unbind_: function () {
		var $Checkbox = zul.wgt.Checkbox,
			n = this.ereal;
		
		if (n) {
			zEvt.unlisten(n, "mousedown", $Checkbox.doClick);			
			zEvt.unlisten(n, "focus", this._onFocus);
			zEvt.unlisten(n, "blur", this._onBlur);
		}

		this.ereal = this._onFocus = this._onBlur = null;
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
		if (newval != wgt.ereal.defaultChecked) { //changed
			// bug #1893575 : we have to clean all of the radio at the same group.
			// in addition we can filter unnecessary onCheck with defaultChecked
			if (wgt.ereal.type == "radio") {
				var nms = document.getElementsByName(wgt.ereal.name);
				for (var i = nms.length; --i >= 0;)
					nms[i].defaultChecked = false;
			}
			wgt.ereal.defaultChecked = newval;
			wgt.fire('onCheck', newval);
		}
	}
});