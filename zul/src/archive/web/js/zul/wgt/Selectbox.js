/* Selectbox.js

	Purpose:
		
	Description:
		
	History:
		Fri Sep 30 10:51:52 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A light weight dropdown list.
 * <p>Default {@link #getZclass}: z-selectbox.
 * @author jumperchen
 * @since 6.0.0
 */
zul.wgt.Selectbox = zk.$extends(zul.Widget, {
	$define: {
		/**
		 * Returns the tab order of this component.
		 * <p>
		 * Default: 0 (means the same as browser's default).
		 * @return int
		 */
		/**
		 * Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (tabindex) {
			var n = this.$n();
			if (n) n.tabindex = tabindex||'';
		},
		/**
		 * Returns the index of the selected item (-1 if no one is selected).
		 * @return int
		 */
		/**
		 * Selects the item with the given index.
		 * @param int selectedIndex
		 */
		selectedIndex: function (selectedIndex) {
			var n = this.$n();
			if (n)
				n.selectedIndex = selectedIndex;
		},
		/**
		 * Returns whether it is disabled.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function (disabled) {
			var n = this.$n();
			if (n) n.disabled = disabled ? 'disabled' : '';
		},
		/**
		 * Returns the name of this component.
		 * <p>
		 * Default: null.
		 * <p>
		 * The name is used only to work with "legacy" Web application that handles
		 * user's request by servlets. It works only with HTTP/HTML-based browsers.
		 * It doesn't work with other kind of clients.
		 * <p>
		 * Don't use this method if your application is purely based on ZK's
		 * event-driven model.
		 * @return String
		 */
		/**
		 * Sets the name of this component.
		 * <p>
		 * The name is used only to work with "legacy" Web application that handles
		 * user's request by servlets. It works only with HTTP/HTML-based browsers.
		 * It doesn't work with other kind of clients.
		 * <p>
		 * Don't use this method if your application is purely based on ZK's
		 * event-driven model.
		 * 
		 * @param String name
		 *            the name of this component.
		 */
		name: function (name) {
			var n = this.$n();
			if (n) n.name = name;
		}
	},
	_fixSelIndex: function () {
		if (this._selectedIndex < 0)
			this.$n().selectedIndex = -1;
	},
	bind_: function () {
		this.$supers(zul.wgt.Selectbox, 'bind_', arguments);
		var n = this.$n();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');
		
		if (!zk.gecko) {
			var fn = [this,  this._fixSelIndex];
			zWatch.listen({onRestore: fn, onVParent: fn});
		}

		this._fixSelIndex();
	},
	unbind_: function () {
		var n = this.$n();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_')
			.$supers(zul.wgt.Selectbox, 'unbind_', arguments);

		var fn = [this,  this._fixSelIndex];
		zWatch.unlisten({onRestore: fn, onVParent: fn});
	},
	_doChange: function (evt) {
		var n = this.$n(),
			v = n.selectedIndex;
		if (zk.opera) n.selectedIndex = v; //ZK-396: opera displays it wrong (while it is actually -1)
		if (this._selectedIndex == v)
			return;
		this.setSelectedIndex(n.selectedIndex);
		this.fire('onSelect', n.selectedIndex);
	},
	//Bug 3304408: IE does not fire onchange
	doBlur_: function (evt) {
		this._doChange(evt);
		return this.$supers('doBlur_', arguments); 		
	},
	//Bug 1756559: ctrl key shall fore it to be sent first
	beforeCtrlKeys_: function (evt) {
		this._doChange(evt);
	},
	domAttrs_: function () {
		var v;
		return this.$supers('domAttrs_', arguments)
			+ (this.isDisabled() ? ' disabled="disabled"' :'')
			+ ((v=this.getSelectedIndex()) > -1 ? ' selectedIndex="' + v + '"': '')
			+ ((v=this.getTabindex()) ? ' tabindex="' + v + '"': '')
			+ ((v=this.getName()) ? ' name="' + v + '"': '');
	}
});