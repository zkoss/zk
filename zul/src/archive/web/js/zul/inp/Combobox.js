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
	_autocomplete: true,

	$define: {
		autocomplete: null,
		repos: function () {
			if (this.desktop) {
				this._typeahead(this._bDel);
				this._bDel = null;
			}
			this._repos = false;
		}
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
			this._findItem(this.getInputNode().value,
				this._isStrict() || (opts && opts.strict)), opts);
	},
	_hilite2: function (sel, opts) {
		if (!opts) opts = {};

		var oldsel = this._sel;
		this._sel = sel;

		if (oldsel && oldsel.parent == this) { //we don't clear _sel precisely, so...
			var n = oldsel.$n();
			if (n) jq(n).removeClass(oldsel.getZclass() + '-seld');
		}

		if (sel && !sel.isDisabled())
			jq(sel.$n()).addClass(sel.getZclass() + '-seld');

		if (opts.sendOnSelect && this._lastsel != sel) {
			this._lastsel = sel;
			this.fire('onSelect', {items: sel?[sel]:[], reference: sel});
		}
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
	dnPressed_: function (evt) {
		this._updnSel(evt);
	},
	upPressed_: function (evt) {
		this._updnSel(evt, true);
	},
	_updnSel: function (evt, bUp) {
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
				var ofs = zk(inp).getSelectionRange();
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

		this._select(sel, {sendOnSelect:true});
		evt.stop();
	},
	_next: function (item, bUp) {
		if (item)
			item = bUp ? item.previousSibling: item.nextSibling;
		return item ? item: bUp ? this.lastChild: this.firstChild;
	},
	_select: function (sel, opts) {
		var inp = this.getInputNode(),
			val = inp.value = sel ? sel.getLabel(): '';
		if (val)
			zk(inp).setSelectionRange(0, val.length)
		this._hilite2(sel, opts);
	},
	otherPressed_: function (evt) {
		var wgt = this,
			bDel = evt.keyCode;
		this._bDel = bDel = bDel == zk.Event.BS || bDel == zk.Event.DEL;
		setTimeout(function () {wgt._typeahead(bDel);}, 0);
			//use timeout, since, when key down, value not ready yet
	},
	_typeahead: function (bDel) {
		if (zk.currentFocus != this) return;

		var inp = this.getInputNode(),
			val = inp.value,
			ofs = zk(inp).getSelectionRange()
			fchild = this.firstChild;
		if (!val || !fchild
		|| ofs[0] != val.length || ofs[0] != ofs[1]) //not at end
			return this._hilite({strict:true});

		var sel = this._findItem(val, true);
		if (sel || bDel || !this._autocomplete)
			return this._hilite2(sel);

		//autocomplete
		val = val.toLowerCase();
		var sel = this._sel;
		if (!sel || sel.parent != this) sel = fchild;

		for (var item = sel;;) {
			if (!item.isDisabled() && item.isVisible()
			&& item.getLabel().toLowerCase().startsWith(val)) {
				inp.value = item.getLabel();
				zk(inp).setSelectionRange(val.length, inp.value.length);
				this._hilite2(item);
				return;
			}

			if (!(item = item.nextSibling)) item = fchild;
			if (item == sel) {
				this._hilite2(); //not found
				return;
			}
		}
	},
	updateChange_: function () {
		if (this.$supers('updateChange_', arguments)) {
			this._hilite({sendOnSelect:true});
			return true;
		}
	},
	unbind_: function () {
		this._hilite2();
		this._lastsel = null;
		this.$supers('unbind_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});