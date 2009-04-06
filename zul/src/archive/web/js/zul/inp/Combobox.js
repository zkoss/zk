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
		val = val.toLowerCase();
		for (var item = this.firstChild; item; item = item.nextSibling)
			if (!item.isDisabled() && item.isVisible()
			&& val == item.getLabel().toLowerCase())
				return null;
		return mesg.VALUE_NOT_MATCHED;
	},

	//super
	doBlur_: function (evt) {
		var inp = this.getInputNode(),
			val = inp.value.toLowerCase();
		if (val.length) {
			var strict = this.getConstraint(), found;
			strict = strict && strict._flags && strict._flags.STRICT;
			for (var item = this.firstChild; item; item = item.nextSibling)
				if ((!strict || !item.isDisabled()) && item.isVisible()
				&& val == item.getLabel().toLowerCase()) {
					found = item;
					break;
				}

			var idfnd = found ? found.uuid: '';
			if ((found || !strict) && this._selid != idfnd) {
				this._selid = idfnd;
				this.fire('onSelect', zk.copy({items: found?[found]:[]},
					zEvt.getMetaKeys(evt)));
			}
		}
		this.$supers('doBlur_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});