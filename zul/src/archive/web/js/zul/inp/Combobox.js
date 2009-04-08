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
	_markSel: function (item, fire) {
		var sel = this._sel;
		if (sel && sel.parent == this) { //we don't clear _sel precisely, so...
			var n = sel.getNode();
			if (n) zDom.rmClass(n, sel.getZclass() + '-seld');
		}

		if (this._sel = item)
			zDom.addClass(item.getNode(), item.getZclass() + '-seld');

		if (fire)
			this.fire('onSelect', zk.copy(
				{items: item?[item]:[], reference: item},
				zEvt.getMetaKeys(evt)));
	},

	//super
	doBlur_: function (evt) {
		var inp = this.getInputNode(),
			val = inp.value;
		if (val.length) {
			var strict = this.getConstraint();
			strict = strict && strict._flags && strict._flags.STRICT;
			var item = this._findItem(val.toLowerCase(), strict);
			this._markSel(item, item || !strict);
		}
		this.$supers('doBlur_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});