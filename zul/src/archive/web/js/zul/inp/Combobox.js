/* Combobox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:52:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Combobox = zk.$extends(zul.inp.ComboWidget, {
	isAutocomplete: function () {
		return this._autocomplete;
	},
	setAutocomplete: function (autocomplete) {
		this._autocomplete = autocomplete;
	},

	//called by SimpleConstraint
	validateStrict: function (val) {
		return this._findItem(val.toLowerCase(), true) ?
			null: mesg.VALUE_NOT_MATCHED;
	},
	_findItem: function (val, strict) {
		val = val.toLowerCase();
		for (var item = this.firstChild; item; item = item.nextSibling)
			if ((!strict || !item.isDisabled()) && item.isVisible()
			&& val == item.getLabel().toLowerCase())
				return item;
	},
	_hilite: function (opts) {
		if (!opts) opts = {};

		var sel = this._sel, item;
		if (sel && sel.parent == this) { //we don't clear _sel precisely, so...
			var n = sel.getNode();
			if (n) zDom.rmClass(n, sel.getZclass() + '-seld');
		}

		this._sel = null;
		if (this._open || opts.sendOnSelect) {
			var inp = this.getInputNode(),
				val = inp.value;
			if (val.length) {
				var strict = this.getConstraint();
				strict = strict && strict._flags && strict._flags.STRICT;
				item = this._findItem(val.toLowerCase(), strict);
			}

			if ((this._sel = item) && this._open)
				zDom.addClass(item.getNode(), item.getZclass() + '-seld');
		}

		if (opts.sendOnSelect)
			this.fire('onSelect', {items: item?[item]:[], reference: item});
	},

	//super
	open: function (opts) {
		this.$supers('open', arguments);
		this._hilite(); //after _open is set
	},
	downPressed_: function (evt) {
	},
	upPressed_: function (evt) {
	},
	otherPressed_: function (evt) {
	},
	updateChange_: function () {
		if (this.$supers('updateChange_', arguments)) {
			this._hilite({sendOnSelect:true});
			return true;
		}
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});