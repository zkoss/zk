/* Combobox.ts

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
 * is more flexible than menulist, such as {@link setAutocomplete}
 * {@link setAutodrop}.
 *
 * @defaultValue {@link getZclass}: z-combobox.
 *
 * <p>Like {@link zul.db.Datebox},
 * the value of a read-only comobobox ({@link isReadonly}) can be changed
 * by dropping down the list and selecting an combo item
 * (though users cannot type anything in the input box).
 *
 * @see Comboitem
 */
@zk.WrapClass('zul.inp.Combobox')
export class Combobox extends zul.inp.ComboWidget {
	override firstChild!: zul.inp.Comboitem | undefined;
	override lastChild!: zul.inp.Comboitem | undefined;
	/** @internal */
	_autocomplete = true;
	/** @internal */
	_instantSelect = true;
	/** @internal */
	override _iconSclass = 'z-icon-caret-down';
	/** @internal */
	_emptySearchMessage?: string;
	/** @internal */
	_shallRedoCss = false;
	/** @internal */
	_isOnChanging = false;
	/** @internal */
	_initSelIndex?: number;
	/** @internal */
	_sel?: zul.inp.Comboitem;
	/** @internal */
	_lastsel?: zul.inp.Comboitem;
	/** @internal */
	_autoCompleteSuppressed = false;
	/** @internal */
	_bDel = false;
	/** @internal */
	_initSelUuid?: string;
	/** @internal */
	declare _shallClose?: boolean;

	/**
	 * @returns whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 * It is also known as auto-type-ahead.
	 *
	 * @defaultValue `true`
	 *
	 * <p>If true, the nearest item will be searched and the text box is
	 * updated automatically.
	 * If false, user has to click the item or use the DOWN or UP keys to
	 * select it back.
	 *
	 * <p>Don't confuse it with the auto-completion feature mentioned by
	 * other framework. Such kind of auto-completion is supported well
	 * by listening to the onChanging event.
	 */
	isAutocomplete(): boolean {
		return this._autocomplete;
	}

	/**
	 * Sets whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 */
	setAutocomplete(autocomplete: boolean): this {
		this._autocomplete = autocomplete;
		return this;
	}

	/**
	 * @returns the message to display when no matching results was found
	 * @since 8.5.1
	 */
	getEmptySearchMessage(): string | undefined {
		return this._emptySearchMessage;
	}

	/**
	 * Sets the message to display when no matching results was found
	 * @since 8.5.1
	 */
	setEmptySearchMessage(emptySearchMessage: string, opts?: Record<string, boolean>): this {
		const o = this._emptySearchMessage;
		this._emptySearchMessage = emptySearchMessage;

		if (o !== emptySearchMessage || opts?.force) {
			var msg = this.$n('empty-search-message');
			if (emptySearchMessage && msg && emptySearchMessage != jq(msg).text()) {
				jq(msg).text(emptySearchMessage);
			}
		}

		return this;
	}

	/**
	 * @returns true if onSelect event is sent as soon as user selects using keyboard navigation.
	 * @defaultValue `true`
	 * @since 8.6.1
	 */
	isInstantSelect(): boolean {
		return this._instantSelect;
	}

	/**
	 * Sets the instantSelect attribute. When the attribute is true, onSelect event
	 * will be fired as soon as user selects using keyboard navigation.
	 *
	 * If the attribute is false, user needs to press Enter key to finish the selection using keyboard navigation.
	 * @since 8.6.1
	 */
	setInstantSelect(instantSelect: boolean): this {
		this._instantSelect = instantSelect;
		return this;
	}

	/**
	 * Returns the selected item if any.
	 * @since 10.0.0
	 */
	getSelectedItem(): zul.inp.Comboitem | undefined {
		return this._sel;
	}

	override onResponse(ctl: zk.ZWatchController, opts: zul.inp.ResponseOptions): void {
		// Bug ZK-2960: need to wait until the animation is finished before calling super
		if (this.isOpen() && jq(this.getPopupNode_()).is(':animated')) {
			setTimeout(() => {
				if (this.desktop)
					this.onResponse(ctl, opts);
			}, 50);
			return;
		}
		super.onResponse(ctl, opts);
		if (opts && opts.rtags && opts.rtags.onChanging && this.isOpen()) {
			this._isOnChanging = true;
		}
		this.fixPopupDimension_();
	}

	/** @internal */
	override fixPopupDimension_(): void {
		super.fixPopupDimension_();
		if (this._shallRedoCss) { //fix in case
			zk(this.getPopupNode_()).redoCSS(-1);
			this._shallRedoCss = false;
		}
		//ZK-3204 check popup position after onChanging
		if (this._isOnChanging && this.isOpen()) {
			this._isOnChanging = false;
			this._checkPopupPosition();
			// F85-ZK-3827: Combobox empty search message
			this._fixEmptySearchMessage();
		}
		// B65-ZK-1990: Fix position of popup when it appears above the input, aligned to the left
		if (this.isOpen() && this._shallSyncPopupPosition) {
			zk(this.getPopupNode_()).position(this.getInputNode(), 'before_start');
			this._shallSyncPopupPosition = false;
		}
	}

	/** @internal */
	setSelectedItemUuid_(selectedItemUuid: string): void {
		if (this.desktop) {
			if (!this._sel || selectedItemUuid != this._sel.uuid) {
				var oldSel = this._sel,
					sel: zul.inp.Comboitem | undefined;
				this._sel = this._lastsel = undefined;
				var w = zk.$<zul.inp.Comboitem>(selectedItemUuid);
				if (w)
					sel = w;
				this._hiliteOpt(oldSel, this._sel = sel);
				this._lastsel = sel;
			}
		} else
			this._initSelUuid = selectedItemUuid;
	}

	// since 10.0.0 for Client Bind to use
	/** @internal */
	setSelectedIndex_(selectedIndex: number): void {
		if (selectedIndex >= 0) {
			if (this.desktop) {
				const selectedItem = this.getChildAt(selectedIndex);
				this.setSelectedItemUuid_(selectedItem!.uuid);
			} else {
				this._initSelIndex = selectedIndex;
			}
		}
	}

	/**
	 * For internal use only.
	 * Update the value of the input element in this component
	 */
	override setRepos(repos: boolean): this {
		if (!this._repos && repos) {
			super.setRepos(repos);
			if (this.desktop) {
				this._typeahead(this._bDel);
				this._bDel = false;

				//Fixed bug 3290858: combobox with autodrop and setModel in onChanging
				var pp = this.getPopupNode_();
				//will update it later in onResponse with _fixsz
				if (pp) {
					pp.style.width = 'auto';
					if (zk.webkit) this._shallRedoCss = true;
				}
			}
		}
		return this;
	}

	override setValue(value: string, fromServer?: boolean): this {
		super.setValue(value, fromServer);
		this._reIndex();
		this.valueEnter_ = undefined; // reset bug #3014660
		this._lastsel = this._sel; // ZK-1256, ZK-1276: set initial selected item
		return this;
	}

	/** @internal */
	_reIndex(): void {
		var value = this.getValue() as unknown;
		if (!this._sel || value != this._sel.getLabel()) {
			if (this._sel) {
				var n = this._sel.$n();
				if (n) jq(n).removeClass(this._sel.$s('selected'));
			}
			this._sel = this._lastsel = undefined;
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (value == w.getLabel()) {
					this._sel = w;
					break;
				}
			}
		}
	}

	/**
	 * Called by SimpleConstraint
	 * @param val - the name of flag, such as "no positive".
	 */
	validateStrict(val: string): string | undefined {
		var cst = this._cst as zul.inp.SimpleConstraint | undefined;
		return this._findItem(val, true) ? undefined :
			(cst ? cst._errmsg.STRICT ? cst._errmsg.STRICT : '' : '') || msgzul.VALUE_NOT_MATCHED;
	}

	/** @internal */
	_findItem(val: string, strict?: boolean): zul.inp.Comboitem | undefined {
		return this._findItem0(val, strict);
	}

	/** @internal */
	_findItem0(val: string, strict?: boolean, startswith?: boolean, excluding?: boolean): zul.inp.Comboitem | undefined {
		var fchild = this.firstChild;
		if (fchild && val) {
			val = val.toLowerCase();
			var sel = this._sel;
			if (!sel || sel.parent != this) sel = fchild;

			for (var item: zul.inp.Comboitem | undefined = excluding ? sel.nextSibling ? sel.nextSibling : fchild : sel; ;) {
				if ((!strict || !item.isDisabled()) && item.isVisible()
					&& (startswith ? item.getLabel().toLowerCase().startsWith(val) : val == item.getLabel().toLowerCase()))
					return item;
				if (!(item = item.nextSibling)) item = fchild;
				if (item == sel) break;
			}
		}
	}

	/** @internal */
	_hilite(opts?: Record<string, boolean>): void {
		this._hilite2(
			this._findItem(
				this.valueEnter_ = this.getInputNode()!.value,
				this._isStrict() || opts?.strict
			),
			opts
		);
	}

	/** @internal */
	_hilite2(sel?: zul.inp.Comboitem, opts?: Record<string, unknown>): void {
		opts = opts ?? {};

		var oldsel = this._sel;
		this._sel = sel;

		this._hiliteOpt(oldsel, sel);

		if (opts.sendOnSelect && this._lastsel != sel) {
			if (sel) { //set back since _findItem ignores cases
				var inp = this.getInputNode()!,
					val = sel.getLabel(),
					selectionRange: [number, number] | undefined;

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
				super.updateChange_();

			this.fire('onSelect', { items: sel ? [sel] : [], reference: sel, prevSeld: oldsel });
			this._lastsel = sel;
			//spec change (diff from zk 3): onSelect fired after onChange
			//purpose: onSelect can retrieve the value correctly
			//If we want to change this spec, we have to modify Combobox.java about _lastCkVal
		} else if (opts.sendOnChange) // The value still didn't match any item, but onChange is still needed.
			super.updateChange_();
	}

	/** @internal */
	_hiliteOpt(oldTarget?: zk.Widget, newTarget?: zul.inp.Comboitem): void {
		if (oldTarget && oldTarget.parent == this) {
			var n = oldTarget.$n();
			if (n)
				jq(n).removeClass(oldTarget.$s('selected'));
		}

		if (newTarget && !newTarget.isDisabled())
			jq(newTarget.$n()).addClass(newTarget.$s('selected'));
	}

	/** @internal */
	_isStrict(): boolean {
		var strict = this.getConstraint() as Partial<zul.inp.SimpleConstraint> | undefined;
		return !!strict?._flags?.STRICT;
	}

	//super
	override open(opts?: zul.inp.OpenOptions): void {
		super.open(opts);
		this._hilite(); //after _open is set
	}

	/** @internal */
	override dnPressed_(evt: zk.Event): void {
		this._updnSel(evt);
	}

	/** @internal */
	override upPressed_(evt: zk.Event): void {
		this._updnSel(evt, true);
	}

	/** @internal */
	_updnSel(evt: zk.Event, bUp?: boolean): void {
		var inp = this.getInputNode()!,
			val = inp.value,
			sel: zul.inp.Comboitem | undefined,
			looseSel: zul.inp.Comboitem | undefined;
		// ZK-2200: the empty combo item should work
		if (val || this._sel) {
			val = val.toLowerCase();
			var beg = this._sel;
			if (!beg || beg.parent != this) {
				beg = this._next(undefined, !bUp);
			}
			if (!beg) {
				evt.stop();
				return; //ignore it
			}

			//Note: we always assume strict when handling up/dn
			for (var item: zul.inp.Comboitem | undefined = beg; ;) {
				if (!item!.isDisabled() && item!.isVisible()) {
					var label = item!.getLabel().toLowerCase();
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
				sel = this._next(undefined, !bUp);
			}
		} else {
			sel = this._next(undefined, true);
		}

		if (sel)
			zk(sel).scrollIntoView(this.$n('pp'));

		//B70-ZK-2548: fire onChange event to notify server the current value
		var highlightOnly = !this._instantSelect && this._open;
		this._select(sel, highlightOnly ? {} : { sendOnSelect: true, sendOnChange: true });
		evt.stop();
	}

	/** @internal */
	_next(item: zul.inp.Comboitem | undefined, bUp?: boolean): zul.inp.Comboitem | undefined {
		function getVisibleItemOnly(item: zul.inp.Comboitem, bUp?: boolean, including?: boolean): zul.inp.Comboitem | undefined {
			var next = bUp ? 'previousSibling' as const : 'nextSibling' as const;
			for (var n = including ? item : item[next]; n; n = n[next])
				if (!n.isDisabled() && n.isVisible()) // ZK-1728: check if the item is visible
					return n;
			return undefined;
		}
		if (item)
			item = getVisibleItemOnly(item, bUp);
		return item ? item : getVisibleItemOnly(
			(bUp ? this.firstChild! : this.lastChild!), !bUp, true);
	}

	/** @internal */
	_select(sel: zul.inp.Comboitem | undefined, opts: Record<string, unknown>): void {
		var inp = this.getInputNode()!,
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
	}

	/** @internal */
	override otherPressed_(evt: zk.Event): void {
		var keyCode = evt.keyCode;
		this._bDel = keyCode == 8 /*BS*/ || keyCode == 46; /*DEL*/
		if (this._readonly)
			switch (keyCode) {
				case 35://End
				case 36://Home
					this._hilite2();
					this.getInputNode()!.value = '';
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
						this._select(sel, { sendOnSelect: true });
			}
	}

	/** @internal */
	override escPressed_(evt: zk.Event): void {
		var highlightOnly = !this._instantSelect && this._open;
		if (highlightOnly && this._lastsel != this._sel) {
			this._hilite2(this._lastsel);
			var lastVal = this._lastsel ? this._lastsel.getLabel() : '';
			this.valueSel_ = this.valueEnter_ = this.getInputNode()!.value = lastVal;
		}
		super.escPressed_(evt);
	}

	/** @internal */
	override doKeyUp_(evt: zk.Event): void {
		if (!this._disabled) {
			if (!this._readonly && !this._autoCompleteSuppressed) {
				var keyCode = evt.keyCode,
					bDel = keyCode == 8 /*BS*/ || keyCode == 46; /*DEL*/
				// ZK-3607: The value is not ready in onKeyDown, but is ready in onKeyUp
				this._typeahead(bDel);
			}
			super.doKeyUp_(evt);
		}
	}

	/** @internal */
	_typeahead(bDel: boolean): void {
		if (zk.currentFocus != this) return;
		var inp = this.getInputNode()!,
			val = inp.value,
			ofs = zk(inp).getSelectionRange(),
			fchild = this.firstChild;
		this.valueEnter_ = val;
		if (!val || !fchild
			|| ofs[0] != val.length || ofs[0] != ofs[1]) //not at end
			return this._hilite({ strict: true });

		var sel = this._findItem(val, true);
		if (sel || bDel || !this._autocomplete) {
			// ZK-2024: the value should have same case with selected label if autocomplete is enabled
			if (sel?.getLabel().toLowerCase().startsWith(val.toLowerCase()) && this._autocomplete)
				inp.value = sel.getLabel();
			return this._hilite2(sel);
		}

		//autocomplete
		val = val.toLowerCase();
		sel = this._sel;
		if (!sel || sel.parent != this) sel = fchild;

		for (var item: zul.inp.Comboitem | undefined = sel; ;) {
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
	}

	/** @internal */
	override updateChange_(): boolean {
		var chng = this._value != this.getInputNode()!.value as unknown; // B50-ZK-297
		if (chng) {
			this._hilite({ sendOnSelect: true, sendOnChange: true, noSelectRange: true });
			return true;
		}
		this.valueEnter_ = undefined;
		return false;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// Bug ZK-403
		if (this.isListen('onOpen'))
			this.listen({ onChanging: zk.$void }, -1000);
		// Bug ZK-1256, ZK-1276: set initial selected item
		if (this._initSelUuid) {
			this.setSelectedItemUuid_(this._initSelUuid);
			this._initSelUuid = undefined;
		} else if (this._initSelIndex! >= 0) { // for stateless to use
			this.setSelectedIndex_(this._initSelIndex!);
			this._initSelIndex = undefined;
		}
		var input = this.getInputNode()!;
		this.domListen_(input, 'onCompositionstart', '_doCompositionstart')
			.domListen_(input, 'onCompositionend', '_doCompositionend');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._hilite2();
		this._sel = this._lastsel = undefined;
		var input = this.getInputNode()!;
		this.domUnlisten_(input, 'onCompositionend', '_doCompositionend')
			.domUnlisten_(input, 'onCompositionstart', '_doCompositionstart');
		// Bug ZK-403
		if (this.isListen('onOpen'))
			this.unlisten({ onChanging: zk.$void });
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_doCompositionstart(): void {
		this._autoCompleteSuppressed = true;
	}

	/** @internal */
	_doCompositionend(): void {
		this._autoCompleteSuppressed = false;
		if (!this._disabled && !this._readonly)
			this._typeahead(false);
	}

	/** @internal */
	override redrawpp_(out: string[]): void {
		var uuid = this.uuid,
			msg = this._emptySearchMessage;
		out.push('<div id="', /*safe*/ uuid, '-pp" class="', this.$s('popup'),
			// tabindex=0 to prevent a11y scrollable popup issue, see https://dequeuniversity.com/rules/axe/3.5/scrollable-region-focusable?application=AxeChrome
			' ', zUtl.encodeXML(this.getSclass()!), '" style="display:none" tabindex="0">');

		// F85-ZK-3827: Combobox empty search message
		if (msg) {
			out.push('<div id="', /*safe*/ uuid, '-empty-search-message" class="',
				this.$s('empty-search-message'), ' ', this.$s('empty-search-message-hidden'),
				'">', zUtl.encodeXML(msg), '</div>');
		}

		out.push('<ul id="', /*safe*/ uuid, '-cave" class="', this.$s('content'), '" >');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		out.push('</ul></div>');
	}

	/** @internal */
	override afterAnima_(visible: boolean): void {
		// B50-ZK-568: Combobox does not scroll to selected item
		// shall do after slide down
		if (visible && this._lastsel)
			zk(this._lastsel).scrollIntoView(this.$n('pp'));
		super.afterAnima_(visible);
	}

	/** @internal */
	override _fixsz(ppofs: zul.inp.PopupSize): void {
		var pp = this.getPopupNode_()!;
		pp.style.width = 'auto';
		super._fixsz(ppofs);
		if (zk(pp).hasVScroll() && !this.getPopupWidth()) {
			pp.style.width = jq.px(pp.offsetWidth + jq.scrollbarWidth());
		}
	}

	/** @internal */
	_fixEmptySearchMessage(): void {
		if (this._emptySearchMessage) {
			jq(this.$n('empty-search-message')).toggleClass(
				this.$s('empty-search-message-hidden'), this.nChildren > 0);
		}
	}

	// ZK-5044 (touch enable)
	/** @internal */
	override getChildMinSize_(attr: zk.FlexOrient, wgt: zul.LabelImageWidget): number {
		const result = super.getChildMinSize_(attr, wgt);
		if (attr == 'w' && result == 0) {
			// use label instead
			const zkn = zk(wgt.$n());
			return zkn.textWidth(wgt.getLabel()) + zkn.padBorderWidth();
		}
		return result;
	}
}