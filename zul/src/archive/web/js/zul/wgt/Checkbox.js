/* Checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//Two onclick are fired if clicking on label, so ignore it if so
	function _shallIgnore(evt) {
		var v = evt.domEvent;
		return v && jq.nodeName(v.target, 'label');
	}

var Checkbox =
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
	//_tabindex: 0,
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
			if (n) {
				v ? jq(n).attr('checked','checked') : jq(n).removeAttr('checked');
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
			if (n) n.tabIndex = v || '';
		},
		/** Returns the value.
		 * <p>Default: "".
		 * @return String
		 * @since 5.0.4
		 */
		/** Sets the value.
		 * @param String value the value; If null, it is considered as empty.
		 * @since 5.0.4
		 */
		value: function (v) {
			var n = this.$n('real');
			if (n) n.value = v || '';
		},
		/** Returns a list of checkbox component IDs that shall be disabled when the user
		 * clicks this checkbox.
		 *
		 * <p>To represent the checkbox itself, the developer can specify <code>self</code>.
		 * For example, 
		 * <pre><code>
		 * checkbox.setId('ok');
		 * wgt.setAutodisable('self,cancel');
		 * </code></pre>
		 * is the same as
		 * <pre><code>
		 * checkbox.setId('ok');
		 * wgt.setAutodisable('ok,cancel');
		 * </code></pre>
		 * that will disable
		 * both the ok and cancel checkboxes when an user clicks it.
		 *
		 * <p>The checkbox being disabled will be enabled automatically
		 * once the client receives a response from the server or a fixed timeout.
		 * In other words, the server doesn't notice if a checkbox is disabled
		 * with this method.
		 *
		 * <p>However, if you prefer to enable them later manually, you can
		 * prefix with '+'. For example,
		 * <pre><code>
		 * checkbox.setId('ok');
		 * wgt.setAutodisable('+self,+cancel');
		 * </code></pre>
		 *
		 * <p>Then, you have to enable them manually such as
		 * <pre><code>if (something_happened){
		 *  ok.setDisabled(false);
		 *  cancel.setDisabled(false);
		 *</code></pre>
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets whether to disable the checkbox after the user clicks it.
		 * @param String autodisable
		 */
		autodisable: null
	},

	//super//
	focus_: function (timeout) {
		zk(this.$n('real') || this.$n()).focus(timeout);
		return true;
	},
	contentAttrs_: function () {
		var html = '', 
			v; // cannot use this._name for radio
		if (v = this.getName())
			html += ' name="' + v + '"';
		if (this._disabled)
			html += ' disabled="disabled"';
		if (this._checked)
			html += ' checked="checked"';
		if (v = this._tabindex)
			html += ' tabindex="' + v + '"';
		if (v = this.getValue())
			html += ' value="' + v + '"';
		return html;
	},
	bind_: function (desktop) {
		this.$supers(Checkbox, 'bind_', arguments);

		var n = this.$n('real');
		
		// Bug 2383106
		if (n.checked != n.defaultChecked)
			n.checked = n.defaultChecked;

		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');
	},
	unbind_: function () {
		var n = this.$n('real');
		
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		this.$supers(Checkbox, 'unbind_', arguments);
	},
	doSelect_: function (evt) {
		if (!_shallIgnore(evt))
			this.$supers('doSelect_', arguments);
	},
	doClick_: function (evt) {
		if (!_shallIgnore(evt)) {
			// F55-ZK-12: Checkbox automatically disable itself after clicked
			// use the autodisable handler of button directly
			zul.wgt.ADBS.autodisable(this);
			var real = this.$n('real'),
				checked = real.checked;
			if (checked != this._checked) { //changed
				this.setChecked(checked); //so Radio has a chance to override it
				this.fireOnCheck_(checked);
			}
			if (zk.webkit && !zk.mobile) 
				zk(real).focus();

			// B65-ZK-1837
			evt.stop({propagation: true});

			return this.$supers('doClick_', arguments);
		}
	},
	fireOnCheck_: function (checked) {
		this.fire('onCheck', checked);
	},
	beforeSendAU_: function (wgt, evt) {
		if (evt.name != 'onClick') //don't stop event if onClick (otherwise, check won't work)
			this.$supers('beforeSendAU_', arguments);
	},
	getTextNode: function () {
		return this.$n('cnt');
	}
});

})();