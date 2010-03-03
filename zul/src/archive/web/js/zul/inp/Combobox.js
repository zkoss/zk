/* Combobox.js

	Purpose:

	Description:

	History:
		Sun Mar 29 20:52:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A combobox.
 *
 * <p>Non-XUL extension. It is used to replace XUL menulist. This class
 * is more flexible than menulist, such as {@link #setAutocomplete}
 * {@link #setAutodrop}.
 *
 * <p>Default {@link #getZclass}: z-combobox.
 *
 * <p>Like {@link zul.db.Datebox},
 * the value of a read-only comobobox ({@link #isReadonly}) can be changed
 * by dropping down the list and selecting an combo item
 * (though users cannot type anything in the input box).
 *
 * @see Comboitem
 */
zul.inp.Combobox = zk.$extends(zul.inp.ComboWidget, {
	_autocomplete: true,

	$define: {
		/** Returns whether to automatically complete this text box
		 * by matching the nearest item ({@link Comboitem}.
		 * It is also known as auto-type-ahead.
		 *
		 * <p>Default: true
		 *
		 * <p>If true, the nearest item will be searched and the text box is
		 * updated automatically.
		 * If false, user has to click the item or use the DOWN or UP keys to
		 * select it back.
		 *
		 * <p>Don't confuse it with the auto-completion feature mentioned by
		 * other framework. Such kind of auto-completion is supported well
		 * by listening to the onChanging event.
		 * @return boolean
		 */
		/** Sets whether to automatically complete this text box
		 * by matching the nearest item ({@link Comboitem}.
		 * @param boolean autocomplete
		 */
		autocomplete: null,
		/** Update the value of the input element in this component
		 */
		repos: function () {
			if (this.desktop) {
				var n = this.getInputNode(),
					ofs;
				n.value = this.valueEnter_ != null ? this.valueEnter_ : this._value || '';
				
				// Fixed bug 2944355
				if (zk.ie && n.value) {
					ofs = n.value.length;
					ofs = [ofs, ofs];
				}
				this._typeahead(this._bDel, ofs);
				this._bDel = null;
			}
			this._repos = false;
		}
	},
	setValue: function (val) {
		this.$supers('setValue', arguments);
		this._reIndex();
	},
	_reIndex: function () {
		var value = this.getValue();
		if (!this._sel || value != this._sel.getLabel()) {
			if (this._sel) {
				var n = this._sel.$n();
				if (n) jq(n).removeClass(this._sel.getZclass() + '-seld');
			}
			this._sel = this._lastsel = null;
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (value == w.getLabel()) {
					this._sel = w;
					break;
				}
			}
		}
	},
	/**called by SimpleConstraint
	 * @param String val the name of flag, such as "no positive".
	 */
	validateStrict: function (val) {
		return this._findItem(val, true) ? null: msgzul.VALUE_NOT_MATCHED;
	},
	_findItem: function (val, strict) {
		return this._findItem0(val, strict);
	},
	_findItem0: function (val, strict, startswith, excluding) {
		var fchild = this.firstChild;
		if (fchild && val) {
			val = val.toLowerCase();
			var sel = this._sel;
			if (!sel || sel.parent != this) sel = fchild;

			for (var item = excluding ? sel.nextSibling ? sel.nextSibling : fchild : sel;;) {
				if ((!strict || !item.isDisabled()) && item.isVisible()
				&& (startswith ? item.getLabel().toLowerCase().startsWith(val) : val == item.getLabel().toLowerCase()))
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
			if (sel) //set back since _findItem ignores cases
				this.getInputNode().value = sel.getLabel();

			if (opts.sendOnChange)
				this.$supers('updateChange_', []);
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
			val = inp.value, sel, looseSel;
		if (val) {
			val = val.toLowerCase();
			var beg = this._sel,
				last = this._next(null, !bUp);
			if (!beg || beg.parent != this) beg = this._next(null, bUp);

			//Note: we always assume strict when handling up/dn
			for (var item = beg;;) {
				if (!item.isDisabled() && item.isVisible()) {
					var label = item.getLabel().toLowerCase();
					if (val == label) {
						sel = item;
						break;
					} else if (!looseSel && label.startsWith(val)) {
						looseSel = item;
						break;
					}
				}
				if ((item = this._next(item, bUp)) == beg)
					break;
			}

			if (!sel)
				sel = looseSel;

			if (sel) { //exact match
				var ofs = zk(inp).getSelectionRange();
				if (ofs[0] == 0 && ofs[1] == val.length) //full selected
					sel = this._next(sel, bUp); //next
			} else
				sel = this._next(null, bUp);
		} else
			sel = this._next(null, true);

		if (sel)
			zk(sel).scrollIntoView(this.$n('pp'));

		this._select(sel, {sendOnSelect:true});
		evt.stop();
	},
	_next: (function() {
		function getVisibleItemOnly(item, bUp, including) {
			var next = bUp ? 'previousSibling' : 'nextSibling';
			for (var n = including ? item : item[next]; n; n = n[next])
				if (!n.isDisabled())
					return n;
			return null;
		}
		return function(item, bUp) {
			if (item)
				item = getVisibleItemOnly(item, bUp);
			return item ? item : getVisibleItemOnly(
					bUp ? this.firstChild : this.lastChild, !bUp, true);
		};
	})(),
	_select: function (sel, opts) {
		var inp = this.getInputNode(),
			val = inp.value = sel ? sel.getLabel(): '';
		this.valueSel_ = val;
		this._hilite2(sel, opts);

		// Fixed IE/Safari/Chrome
		if (val)
			zk(inp).setSelectionRange(0, val.length);
	},
	otherPressed_: function (evt) {
		var wgt = this,
			keyCode = evt.keyCode,
			bDel;
		this._bDel = bDel = keyCode == 8 /*BS*/ || keyCode == 46 /*DEL*/;
		if (this._readonly)
			switch (keyCode) {
			case 35://End
			case 36://Home
				this._hilite2();
				this.getInputNode().value = '';
				//fall thru
			case 37://Left
			case 39://Right
				this._updnSel(evt, keyCode == 37 || keyCode == 35);
				break;
			case 8://Backspace
				evt.stop();
				break;
			default:
				var v = String.fromCharCode(keyCode);
				var sel = this._findItem0(v, true, true, !!this._sel);
				if (sel)
					this._select(sel, {sendOnSelect: true});
			}
		else
			setTimeout(function () {wgt._typeahead(bDel);}, zk.opera ? 10 : 0);
			//use timeout, since, when key down, value not ready yet, opear need extra time to set value to dom
	},
	_typeahead: function (bDel, ofs) {
		if (zk.currentFocus != this) return;

		var inp = this.getInputNode(),
			val = inp.value,
			ofs = ofs || zk(inp).getSelectionRange(),
			fchild = this.firstChild;
		this.valueEnter_ = val;
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
		this.valueEnter_ = null;
	},
	unbind_: function () {
		this._hilite2();
		this._sel = this._lastsel = null;
		this.$supers('unbind_', arguments);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs ? zcs: "z-combobox" + (this.inRoundedMold() ? "-rounded": "");
	},

	redrawpp_: function (out) {
		var uuid = this.uuid;
		out.push('<div id="', uuid, '-pp" class="', this.getZclass(),
		'-pp" style="display:none" tabindex="-1"><table id="',
		uuid, '-cave"', zUtl.cellps0, '>');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		out.push('</table></div>');
	}
});