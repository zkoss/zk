/* Checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>onCheck is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 */
zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	_checked: false,

	$define: {
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function (v) {
			var n = this.$n('real');
			if (n) n.disabled = v;
		},
		/** Returns whether it is checked.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is checked.
		 * @param boolean checked
		 */
		checked: function (v) {
			var n = this.$n('real');
			if (n) n.checked = v;
		},
		/** Returns the name of this component.
		 * <p>Default: null.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * @return String
		 */
		/** Sets the name of this component.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 *
		 * @param String name the name of this component.
		 */
		name: function (v) {
			var n = this.$n('real');
			if (n) n.name = v || '';
		},
		/** Returns the tab order of this component.
		 * <p>Default: -1 (means the same as browser's default).
		 * @return int
		 */
		/** Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (v) {
			var n = this.$n('real');
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

		var n = this.$n('real');
		
		// Bug 2383106
		if (n.checked != n.defaultChecked)
			n.checked = n.defaultChecked;

		if (zk.gecko2_)
			jq(n).click(zul.wgt.Checkbox._domClick);
		this.domListen_(n, "onFocus", "doFocus_")
			.domListen_(n, "onBlur", "doBlur_");
	},
	unbind_: function () {
		var n = this.$n('real');
		
		if (zk.gecko2_)
			jq(n).unbind("click", zul.wgt.Checkbox._domClick);
		this.domUnlisten_(n, "onFocus", "doFocus_")
			.domUnlisten_(n, "onBlur", "doBlur_");

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		var real = this.$n('real'),
			checked = real.checked;
		if (checked != this._checked) //changed
			this.setChecked(checked) //so Radio has a chance to override it
				.fire('onCheck', checked);
		if (zk.safari) jq(real).focus();
		return this.$supers('doClick_', arguments);
	},
	beforeSendAU_: function (wgt, evt) {
		if (evt.name != "onClick") //don't stop event if onClick (otherwise, check won't work)
			this.$supers("beforeSendAU_", arguments);
	},
	getTextNode: function () {
		return jq(this.$n()).find('label:first')[0];
	}
}, zk.gecko2_ ? {
	_domClick: function (evt) {
		var e = evt.originalEvent;
		if (e) e.z$target = e.currentTarget;
			//bug 2233787: bug of firefox 2, use currentTarget not target
			//if pressing arrow keys
			//It is used by zk.Widget.$
			//Store it in originalEvent (since jq will construct another)
	}
}:null);

