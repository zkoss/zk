/* Combobox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:52:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Combobox = zk.$extends(zul.inp.InputWidget, {
	_btnVisible: true,

	isButtonVisible: function () {
		return this._btnVisible;
	},
	setButtonVisible: function (btnVisible) {
		if (this._btnVisible != btnVisible) {
			this._btnVisible = btnVisible;
			var n = this.getSubnode('btn');
			if (n)
				btnVisible ? zDom.show(n): zDom.hide(n);
		}
	},
	isAutodrop: function () {
		return this._autodrop;
	},
	setAutodrop: function (autodrop) {
		this._autodrop = autodrop;
	},
	isAutocomplete: function () {
		return this._autocomplete;
	},
	setAutocomplete: function (autocomplete) {
		this._autocomplete = autocomplete;
	},

	onSize: _zkf = function () {
		this._dropb.fixpos();
	},
	onVisible: _zkf,

	//super
	bind_: function () {
		this.$supers('bind_', arguments);
		var btn = this.getSubnode('btn'),
			inp = this.getSubnode('real'),
			dropb = this._dropb = new zul.Dropbutton(this, btn, inp);

		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);

		this._dropb.cleanup();
		delete this._dropb;

		this.$supers('unbind_', arguments);
	},
	doBlur_: function (evt) { //dom evt
		//TODO
		this.$supers('doBlur_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});