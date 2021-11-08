/* Combobox.js

	Purpose:

	Description:

	History:
		Sun Mar 29 20:52:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
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
	_instantSelect: true,
	_iconSclass: 'z-icon-caret-down',

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
		/**
		 * Returns the message to display when no matching results was found
		 * @return String
		 * @since 8.5.1
		 */
		/**
		 * Sets the message to display when no matching results was found
		 * @param String msg
		 * @since 8.5.1
		 */
		emptySearchMessage: null,
		/**
		 * Returns true if onSelect event is sent as soon as user selects using keyboard navigation.
		 * <p>Default: true
		 *
		 * @return boolean
		 * @since 8.6.1
		 */
		/**
		 * Sets the instantSelect attribute. When the attribute is true, onSelect event
		 * will be fired as soon as user selects using keyboard navigation.
		 *
		 * If the attribute is false, user needs to press Enter key to finish the selection using keyboard navigation.
		 * @param boolean instantSelect
		 * @since 8.6.1
		 */
		instantSelect: null
	},
	onResponse: function () {
		// Bug ZK-2960: need to wait until the animation is finished before calling super
		var args = arguments;
		if (this.isOpen() && jq(this.getPopupNode_()).is(':animated')) {
			var self = this;
			setTimeout(function () {if (self.desktop) self.onResponse.apply(self, args);}, 50);
			return;
		}
		this.$supers('onResponse', arguments);
		if (this._shallRedoCss) { //fix in case
			zk(this.getPopupNode_()).redoCSS(-1);
			this._shallRedoCss = null;
		}
		//ZK-3204 check popup position after onChanging
		if (args[1] && args[1].rtags && args[1].rtags.onChanging && this.isOpen()) {
			this._checkPopupPosition();
			// F85-ZK-3827: Combobox empty search message
			this._fixEmptySearchMessage();
		}
		// B65-ZK-1990: Fix position of popup when it appears above the input, aligned to the left
		if (this.isOpen() && this._shallSyncPopupPosition) {
			zk(this.getPopupNode_()).position(this.getInputNode(), 'before_start');
			this._shallSyncPopupPosition = false;
		}
	},

	/**
	*  For internal use only
	*/
	setSelectedItemUuid_: function (v) {
		if (this.desktop) {
			if (!this._sel || v != this._sel.uuid) {
				var oldSel = this._sel,
					sel;
				this._sel = this._lastsel = null;
				var w = zk.$(v);
				if (w)
					sel = w;
				this._hiliteOpt(oldSel, this._sel = sel);
				this._lastsel = sel;
			}
		} else
			this._initSelUuid = v;
	},
	/**
	 * For internal use only.
	 * Update the value of the input element in this component
	 */
	setRepos: function (v) {
		if (!this._repos && v) {
			this.$supers('setRepos', arguments);
			if (this.desktop) {
				this._typeahead(this._bDel);
				this._bDel = null;

				//Fixed bug 3290858: combobox with autodrop and setModel in onChanging
				var pp = this.getPopupNode_();
				//will update it later in onResponse with _fixsz
				if (pp) {
					pp.style.width = 'auto';
					if (zk.webkit) this._shallRedoCss = true;
				}
			}
		}
	},

	setValue: function (val) {
		this.$supers('setValue', arguments);
		this._reIndex();
		this.valueEnter_ = null; // reset bug #3014660
		this._lastsel = this._sel; // ZK-1256, ZK-1276: set initial selected item
	},
	_reIndex: function () {
		var value = this.getValue();
		if (!this._sel || value != this._sel.getLabel()) {
			if (this._sel) {
				var n = this._sel.$n();
				if (n) jq(n).removeClass(this._sel.$s('selected'));
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
		var cst = this._cst;
		return this._findItem(val, true) ? null :
			(cst ? cst._errmsg['STRICT'] ? cst._errmsg['STRICT'] : '' : '') || msgzul.VALUE_NOT_MATCHED;
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

			for (var item = excluding ? sel.nextSibling ? sel.nextSibling : fchild : sel; ;) {
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
			this._findItem(this.valueEnter_ = this.getInputNode().value,
				this._isStrict() || (opts && opts.strict)), opts);
	},
	_hilite2: function (sel, opts) {
		opts = opts || {};

		var oldsel = this._sel;
		this._sel = sel;

		this._hiliteOpt(oldsel, sel);

		if (opts.sendOnSelect && this._lastsel != sel) {
			if (sel) { //set back since _findItem ignores cases
				var inp = this.getInputNode(),
					val = sel.getLabel(),
					selectionRange = null;

				if (zk.ie < 11) // ZK-4588: caret missing after edit input value in IE9/IE10
					selectionRange = zk(inp).getSelectionRange();
				this.valueEnter_ = inp.value = val;
				if (selectionRange)
					inp.setSelectionRange(selectionRange[0], selectionRange[1]);
				//Bug 3058028
				// ZK-518
				if (!opts.noSelectRange) {
					if (zk.gecko)
						inp.select();
					else
						zk(inp).setSelectionRange(0, val.length);
				}
			}

			if (opts.sendOnChange)
				this.$supers('updateChange_', []);

			this.fire('onSelect', {items: sel ? [sel] : [], reference: sel, prevSeld: oldsel});
			this._lastsel = sel;
			//spec change (diff from zk 3): onSelect fired after onChange
			//purpose: onSelect can retrieve the value correctly
			//If we want to change this spec, we have to modify Combobox.java about _lastCkVal
		} else if (opts.sendOnChange) // The value still didn't match any item, but onChange is still needed.
			this.$supers('updateChange_', []);
	},
	_hiliteOpt: function (oldTarget, newTarget) {
		if (oldTarget && oldTarget.parent == this) {
			var n = oldTarget.$n();
			if (n)
				jq(n).removeClass(oldTarget.$s('selected'));
		}

		if (newTarget && !newTarget.isDisabled())
			jq(newTarget.$n()).addClass(newTarget.$s('selected'));
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
		// ZK-2200: the empty combo item should work
		if (val || this._sel) {
			val = val.toLowerCase();
			var beg = this._sel;
			if (!beg || beg.parent != this) {
				beg = this._next(null, !bUp);
			}
			if (!beg) {
				evt.stop();
				return; //ignore it
			}

			//Note: we always assume strict when handling up/dn
			for (var item = beg; ;) {
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
				var nextitem = this._next(item, bUp);
				if (item == nextitem) break;  //prevent infinite loop
				if ((item = nextitem) == beg)
					break;
			}

			if (!sel)
				sel = looseSel;

			if (sel) { //exact match
				var ofs = zk(inp).getSelectionRange();
				if (ofs[0] == 0 && ofs[1] == val.length) { //full selected
					sel = this._next(sel, bUp); //next
				}
			} else {
				sel = this._next(null, !bUp);
			}
		} else {
			sel = this._next(null, true);
		}

		if (sel)
			zk(sel).scrollIntoView(this.$n('pp'));

		//B70-ZK-2548: fire onChange event to notify server the current value
		var highlightOnly = !this._instantSelect && this._open;
		this._select(sel, highlightOnly ? {} : {sendOnSelect: true, sendOnChange: true});
		evt.stop();
	},
	_next: (function () {
		function getVisibleItemOnly(item, bUp, including) {
			var next = bUp ? 'previousSibling' : 'nextSibling';
			for (var n = including ? item : item[next]; n; n = n[next])
				if (!n.isDisabled() && n.isVisible()) // ZK-1728: check if the item is visible
					return n;
			return null;
		}
		return function (item, bUp) {
			if (item)
				item = getVisibleItemOnly(item, bUp);
			return item ? item : getVisibleItemOnly(
					bUp ? this.firstChild : this.lastChild, !bUp, true);
		};
	})(),
	_select: function (sel, opts) {
		var inp = this.getInputNode(),
			val = inp.value = sel ? sel.getLabel() : '';
		this.valueSel_ = val;
		this._hilite2(sel, opts);

		// Fixed IE/Safari/Chrome
		// ZK-518: Selected value in combobox is right aligned in FF5+ if width is smaller than selected option
		// setSelectionRange of FF5 or up will set the position to end,
		// call select() of input element for select all
		if (val) {
			if (zk.gecko)
				inp.select();
			else
				zk(inp).setSelectionRange(0, val.length);
		}
	},
	otherPressed_: function (evt) {
		var keyCode = evt.keyCode;
		this._bDel = keyCode == 8 /*BS*/ || keyCode == 46; /*DEL*/
		if (this._readonly)
			switch (keyCode) {
			case 35://End
			case 36://Home
				this._hilite2();
				this.getInputNode().value = '';
				//fall through
			case 37://Left
			case 39://Right
				this._updnSel(evt, keyCode == 37 || keyCode == 35);
				break;
			case 8://Backspace
				evt.stop();
				break;
			default:
				//B70-ZK-2590: dealing with numpad keyDown, only 0~9
				if (keyCode >= 96 && keyCode <= 105)
					keyCode -= 48;
				var v = String.fromCharCode(keyCode);
				var sel = this._findItem0(v, true, true, !!this._sel);
				if (sel)
					this._select(sel, {sendOnSelect: true});
			}
	},
	escPressed_: function () {
		var highlightOnly = !this._instantSelect && this._open;
		if (highlightOnly && this._lastsel != this._sel) {
			this._hilite2(this._lastsel);
			var lastVal = this._lastsel ? this._lastsel.getLabel() : '';
			this.valueSel_ = this.valueEnter_ = this.getInputNode().value = lastVal;
		}
		this.$supers('escPressed_', arguments);
	},
	doKeyUp_: function (evt) {
		if (!this._disabled) {
			if (!this._readonly && !this._autoCompleteSuppressed) {
				var keyCode = evt.keyCode,
					bDel = keyCode == 8 /*BS*/ || keyCode == 46; /*DEL*/
				// ZK-3607: The value is not ready in onKeyDown, but is ready in onKeyUp
				this._typeahead(bDel);
			}
			this.$supers('doKeyUp_', arguments);
		}
	},
	_typeahead: function (bDel) {
		if (zk.currentFocus != this) return;
		var inp = this.getInputNode(),
			val = inp.value,
			ofs = zk(inp).getSelectionRange(),
			fchild = this.firstChild;
		this.valueEnter_ = val;
		if (!val || !fchild
		|| ofs[0] != val.length || ofs[0] != ofs[1]) //not at end
			return this._hilite({strict: true});

		var sel = this._findItem(val, true);
		if (sel || bDel || !this._autocomplete) {
			// ZK-2024: the value should have same case with selected label if autocomplete is enabled
			if (sel && sel.getLabel().toLowerCase().startsWith(val.toLowerCase()) && this._autocomplete)
				inp.value = sel.getLabel();
			return this._hilite2(sel);
		}

		//autocomplete
		val = val.toLowerCase();
		sel = this._sel;
		if (!sel || sel.parent != this) sel = fchild;

		for (var item = sel; ;) {
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
		var chng = this._value != this.getInputNode().value; // B50-ZK-297
		if (chng) {
			this._hilite({sendOnSelect: true, sendOnChange: true, noSelectRange: true});
			return true;
		}
		this.valueEnter_ = null;
	},
	bind_: function () {
		this.$supers(zul.inp.Combobox, 'bind_', arguments);
		// Bug ZK-403
		if (this.isListen('onOpen'))
			this.listen({onChanging: zk.$void}, -1000);
		// Bug ZK-1256, ZK-1276: set initial selected item
		if (this._initSelUuid) {
			this.setSelectedItemUuid_(this._initSelUuid);
			this._initSelUuid = null;
		}
		var input = this.getInputNode();
		this.domListen_(input, 'onCompositionstart', '_doCompositionstart')
			.domListen_(input, 'onCompositionend', '_doCompositionend');
	},
	unbind_: function () {
		this._hilite2();
		this._sel = this._lastsel = null;
		var input = this.getInputNode();
		this.domUnlisten_(input, 'onCompositionend', '_doCompositionend')
			.domUnlisten_(input, 'onCompositionstart', '_doCompositionstart');
		// Bug ZK-403
		if (this.isListen('onOpen'))
			this.unlisten({onChanging: zk.$void});
		this.$supers(zul.inp.Combobox, 'unbind_', arguments);
	},
	_doCompositionstart: function () {
		this._autoCompleteSuppressed = true;
	},
	_doCompositionend: function () {
		this._autoCompleteSuppressed = false;
		if (!this._disabled && !this._readonly)
			this._typeahead(false);
	},
	//@Override
	redrawpp_: function (out) {
		var uuid = this.uuid,
			msg = this._emptySearchMessage;
		out.push('<div id="', uuid, '-pp" class="', this.$s('popup'),
		// tabindex=0 to prevent a11y scrollable popup issue, see https://dequeuniversity.com/rules/axe/3.5/scrollable-region-focusable?application=AxeChrome
		' ', this.getSclass(), '" style="display:none" tabindex="0">');

		// F85-ZK-3827: Combobox empty search message
		if (msg) {
			out.push('<div id="', uuid, '-emptySearchMessage" class="',
			this.$s('emptySearchMessage'), ' ', this.$s('emptySearchMessage-hidden'),
			'">', msg, '</div>');
		}

		out.push('<ul id="', uuid, '-cave" class="', this.$s('content'), '" >');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		out.push('</ul></div>');
	},
	afterAnima_: function (visible) {
		// B50-ZK-568: Combobox does not scroll to selected item
		// shall do after slide down
		if (visible && this._lastsel)
			zk(this._lastsel).scrollIntoView(this.$n('pp'));
		this.$supers('afterAnima_', arguments);
	},
	_fixsz: function () {
		var pp = this.getPopupNode_();
		pp.style.width = 'auto';
		this.$supers('_fixsz', arguments);
		if (zk(pp).hasVScroll() && !this.getPopupWidth()) {
			pp.style.width = jq.px(pp.offsetWidth + jq.scrollbarWidth());
		}
	},
	_fixEmptySearchMessage: function () {
		if (this._emptySearchMessage) {
			jq(this.$n('emptySearchMessage')).toggleClass(
				this.$s('emptySearchMessage-hidden'), this.nChildren > 0);
		}
	},
	// ZK-5044 (touch enable)
	getChildMinSize_: function (attr, wgt) {
		let result = this.$supers('getChildMinSize_', arguments);
		if (attr == 'w' && result == 0) {
			// use label instead
			let zkn = zk(wgt.$n());
			return zkn.textWidth(wgt.getLabel()) + zkn.padBorderWidth();
		}
		return result;
	}
});
