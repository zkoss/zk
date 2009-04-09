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
		return this._findItem(val, true) ? null: mesg.VALUE_NOT_MATCHED;
	},
	_findItem: function (val, strict) {
		var fchild = this.firstChild;
		if (fchild && val) {
			val = val.toLowerCase();
			var sel = this._sel;
			if (!sel || sel.parent != this) sel = fchild;

			for (var item = sel;;) {
				if ((!strict || !item.isDisabled()) && item.isVisible()
				&& val == item.getLabel().toLowerCase())
					return item;
				if (!(item = item.nextSibling)) item = fchild;
				if (item == sel) break;
			}
		}
	},
	_hilite: function (opts) {
		this._hilite2(
			this._findItem(this.getInputNode().value, this._isStrict()), opts);
	},
	_hilite2: function (sel, opts) {
		if (!opts) opts = {};

		var oldsel = this._sel;
		this._sel = sel;

		if (oldsel && oldsel.parent == this) { //we don't clear _sel precisely, so...
			var n = oldsel.getNode();
			if (n) zDom.rmClass(n, oldsel.getZclass() + '-seld');
		}

		if (sel && this._open)
			zDom.addClass(sel.getNode(), sel.getZclass() + '-seld');

		if (opts.sendOnSelect)
			this.fire('onSelect', {items: sel?[sel]:[], reference: sel});
	},
	_isStrict: function () {
		var strict = this.getConstraint();
		return strict && strict._flags && strict._flags.STRICT;
	},

	//super
	open: function (opts) {
		this.$supers('open', arguments);
		this._hilite(); //after _open is set
	},
	downPressed_: function (evt) {
		this._updnSel();
	},
	upPressed_: function (evt) {
		this._updnSel(true);
	},
	_updnSel: function (bUp) {
		var inp = this.getInputNode(),
			val = inp.value, sel;
		if (val) {
			val = val.toLowerCase();
			var beg = this._sel;
			if (!beg || beg.parent != this) beg = this._next(null, bUp);

			//Note: we always assume strict when handling up/dn
			for (var item = beg;;) {
				if (!item.isDisabled() && item.isVisible()
				&& val == item.getLabel().toLowerCase()) {
					sel = item;
					break;
				}
				if ((item = this._next(item, bUp)) == beg)
					break;
			}

			if (sel) { //exact match
				var ofs = zDom.getSelectionRange(inp);
				if (ofs[0] == 0 && ofs[1] == val.length) //full selected
					sel = this._next(sel, bUp); //next
			} else {
				for (var item = beg;;) {
					if (!item.isDisabled() && item.isVisible()
					&& item.getLabel().toLowerCase().startsWith(val)) {
						sel = item;
						break;
					}
					if ((item = this._next(item, bUp)) == beg)
						break;
				}
			}
		} else
			sel = this._next(null, bUp);

		val = inp.value = sel ? sel.getLabel(): '';
		if (val) zDom.setSelectionRange(inp, 0, val.length)

		this._hilite2(sel, {sendOnSelect:true});
	},
	_next: function (item, bUp) {
		if (item)
			item = bUp ? item.previousSibling: item.nextSibling;
		return item ? item: bUp ? this.lastChild: this.firstChild;
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