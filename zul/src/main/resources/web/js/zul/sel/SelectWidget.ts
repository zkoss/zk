/* SelectWidget.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The selectable widgets, such as listbox and tree.
 */
//zk.$package('zul.sel');
function _beforeChildKey(wgt: SelectWidget, evt: zk.Event): boolean | undefined {
	return zAu.processing() || wgt._shallIgnore(evt);
}
function _afterChildKey(evt: zk.Event<zk.EventKeyData>): boolean {
	switch (evt.data!.keyCode) {
	case 33: //PgUp
	case 34: //PgDn
	case 38: //UP
	case 40: //DOWN
	case 37: //LEFT
	case 39: //RIGHT
	case 32: //SPACE
	case 36: //Home
	case 35: //End
		evt.stop();
		return false;
	}
	return true;
}
function _updHeaderCM(box: SelectWidget): void { //update header's checkmark
	if (box.$$selectAll != undefined)
		return; // update by server's state

	if (--box._nUpdHeaderCM! <= 0 && box.desktop && box._headercm && box._multiple) {
		var headerWgt = zk.Widget.$<zul.mesh.HeaderWidget>(box._headercm)!,
			zcls = headerWgt.getZclass() + '-checked',
			$headercm = jq(box._headercm),
			isAllSelected = box._isAllSelected();
		$headercm[isAllSelected ? 'addClass' : 'removeClass'](zcls);
		headerWgt._checked = isAllSelected;
	}
}
function _isButton(evt: zk.Event): boolean {
	return evt.target!.$button //for extension, it makes a widget as a button
		|| (zk.isLoaded('zul.wgt')
		&& (evt.target instanceof zul.wgt.Button || evt.target instanceof zul.wgt.Toolbarbutton));
}
function _isInputWidget(evt: zk.Event): boolean { // B50-ZK-460
	return evt.target!.$inputWidget //for extension, it makes a widget as a input widget
		|| (zk.isLoaded('zul.inp') && evt.target instanceof zul.inp.InputWidget);
}
function _focusable(evt: zk.Event): boolean {
	return (jq.nodeName(evt.domTarget!, 'input', 'textarea', 'button', 'select', 'option', 'a')
			&& !(evt.target instanceof zul.sel.SelectWidget))
		|| _isButton(evt) || _isInputWidget(evt);
}
function _fixReplace(w: zul.sel.ItemWidget | null | undefined): zul.sel.ItemWidget | null {
	return w && w.uuid ? zk.Widget.$<zul.sel.ItemWidget>(w.uuid) : null;
}
function _isListgroup(w: zul.sel.ItemWidget): boolean {
	return zk.isLoaded('zkex.sel') && w instanceof zkex.sel.Listgroup;
}
function _isListgroupfoot(w: zul.sel.ItemWidget): boolean {
	return zk.isLoaded('zkex.sel') && w instanceof zkex.sel.Listgroupfoot;
}

export abstract class SelectWidget extends zul.mesh.MeshWidget {
	public override firstChild!: zul.sel.ItemWidget | null;
	public override lastChild!: zul.sel.ItemWidget | null;
	/** Whether to change a list item selection on right click
	 * <p>Default: true (unless the server changes the setting)
	 * @since 5.0.5
	 * @type boolean
	 */
	public rightSelect = true;
	private _anchorTop = 0;
	private _anchorLeft = 0;
	protected _isSelecting = true;
	private _startRow: zul.sel.ItemWidget | null = null;
	public nonselectableTags?: string;
	private _checkmark?: boolean;
	public _multiple?: boolean;
	public _selItems: zul.sel.ItemWidget[];
	public _focusItem?: zul.sel.ItemWidget | null;
	public efield?: HTMLElement | null;
	private _selectedIndex?: number;
	private _name?: string;
	protected _selectOnHighlightDisabled?: boolean;
	private _checkmarkDeselectOther?: boolean;
	private _cdo?: boolean;
	public $$selectAll?: boolean;
	private _oldCSS?: string;
	private _lastSelectedItem?: zul.sel.ItemWidget | null;
	public _nUpdHeaderCM?: number;
	public _headercm?: HTMLElement;
	private _$services?: Record<string, zk.Callable[] | null>;
	public groupSelect?: boolean;
	private _shallSyncFocus?: boolean | zul.sel.ItemWidget;
	public _shallSyncCM?: boolean;
	protected _itemForSelect?: zul.sel.ItemWidget;
	private _disableSelection_?: boolean;
	declare public static shallSyncSelInView?: Record<string, boolean>;

	public abstract stripe(): this | undefined

	public constructor() {
		super(); // FIXME: params?
		this._selItems = [];
	}

	/**
	 * Returns whether the check mark shall be displayed in front of each item.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	public isCheckmark(): boolean | undefined {
		return this._checkmark;
	}

	/**
	 * Sets whether the check mark shall be displayed in front of each item.
	 * <p>
	 * The check mark is a checkbox if {@link #isMultiple} returns true. It is a
	 * radio button if {@link #isMultiple} returns false.
	 * @param boolean checkmark
	 */
	public setCheckmark(checkmark: boolean, opts?: Record<string, boolean>): this {
		const o = this._checkmark;
		this._checkmark = checkmark;

		if (o !== checkmark || (opts && opts.force)) {
			if (this.desktop)
				this.rerenderLater_();
		}

		return this;
	}

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	public isMultiple(): boolean | undefined {
		return this._multiple;
	}

	/**
	 * Sets whether multiple selections are allowed.
	 * @param boolean multiple
	 */
	public setMultiple(multiple: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiple;
		this._multiple = multiple;

		if (o !== multiple || (opts && opts.force)) {
			if (!this._multiple && this._selItems.length) {
				var item = this.getSelectedItem();
				for (var it: zul.sel.ItemWidget | undefined; (it = this._selItems.pop());)
					if (it != item) {
						if (!this._checkmark)
							it._setSelectedDirectly(false);
						else it._selected = false;
					}

				this._selItems.push(item);
			}
			if (this._checkmark && this.desktop)
				this.rerenderLater_();
		}

		return this;
	}

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 * @return int
	 */
	public getSelectedIndex(): number | undefined {
		return this._selectedIndex;
	}

	/**
	 * Deselects all of the currently selected items and selects the item with
	 * the given index.
	 * @param int selectedIndex
	 */
	public setSelectedIndex(selectedIndex: number, opts?: Record<string, boolean>): this {
		const o = this._selectedIndex,
			v = selectedIndex;
		selectedIndex = v < -1 || (!v && v !== 0) ? -1 : v;
		this._selectedIndex = selectedIndex;

		if (o !== selectedIndex || (opts && opts.force)) {
			var selected = this._selectedIndex;
			this.clearSelection();
			this._selectedIndex = selected;
			if (selected > -1) {
				var w: zul.sel.ItemWidget | null | undefined;
				for (var it = this.getBodyWidgetIterator(); selected-- >= 0;)
					w = it.next();
				if (w) {
					var isMultiSelected = this._selItems.length > 1;
					this._selectOne(w, true);

					// refix ZK-1483: do not have to scroll selected item into view when multiple items are selected
					if (!isMultiSelected) {
						var bar = this._scrollbar;
						if (bar)
							bar.scrollToElement(w.$n_());
						else
							zk(w).scrollIntoView(this.ebody);
					}
				}
			}
		}

		return this;
	}

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
	public getName(): string | undefined {
		return this._name;
	}

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
	public setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || (opts && opts.force)) {
			if (this.desktop) this.updateFormData();
		}

		return this;
	}

	public getSelectOnHighlightDisabled(): boolean | undefined {
		return this._selectOnHighlightDisabled;
	}

	public setSelectOnHighlightDisabled(v: boolean): this {
		this._selectOnHighlightDisabled = v;
		return this;
	}

	public getCheckmarkDeselectOther(): boolean | undefined {
		return this._checkmarkDeselectOther;
	}

	public setCheckmarkDeselectOther(cdo: boolean, opts?: Record<string, boolean>): this {
		const o = this._checkmarkDeselectOther;
		this._checkmarkDeselectOther = cdo;

		if (o !== cdo || (opts && opts.force)) {
			this._cdo = cdo; // backward compatible
		}

		return this;
	}

	public setChgSel(val: string): void { //called from the server
		var sels = {};
		for (var j = 0; ;) {
			var k = val.indexOf(',', j),
				s = (k >= 0 ? val.substring(j, k) : val.substring(j)).trim();
			if (s) sels[s] = true;
			if (k < 0) break;
			j = k + 1;
		}

		// reset $$selectAll
		this.$$selectAll = undefined;

		for (var it = this.getBodyWidgetIterator(), w: zul.sel.ItemWidget | null | undefined; (w = it.next());)
			this._changeSelect(w, sels[w.uuid] == true);
	}

	public setFocusIndex(index: number): void { // called from server
		// F60-ZK-715
		if (index < 0)
			return;
		var self = this;
		setTimeout(function () { // items not ready yet
			var w: zul.sel.ItemWidget | null | undefined;
			for (var it = self.getBodyWidgetIterator(); (w = it.next()) && index--;)
				if (!it.hasNext())
					break;
			self._focusItem = w;
		});
	}

	public updateFormData(): void {
		if (this._name) {
			if (!this.efield)
				this.efield = jq(this.$n_()).append('<div style="display:none;"></div>').find('> div:last-child')[0];

			jq(this.efield).children().remove();

			// don't use jq.newHidden() in this case, because the performance is not good.
			var data = '',
				tmp = '<input type="hidden" name="' + zUtl.encodeXML(this._name) + '" value="';
			for (var i = 0, j = this._selItems.length; i < j; i++)
				data += tmp + zUtl.encodeXML(this._selItems[i].getValue()!) + '"/>';

			jq(this.efield).append(data);
		} else if (this.efield) {
			jq(this.efield).remove();
			this.efield = null;
		}
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItem}.
	 * @param ItemWidget item
	 */
	public setSelectedItem(item: zul.sel.ItemWidget): void {
		if (!item)
			this.clearSelection();
		else {
			var isMultiSelected = this._selItems.length > 1;
			this._selectOne(item, true);

			// refix ZK-1483: do not have to scroll selected item into view when multiple items are selected
			if (!isMultiSelected) {
				var bar = this._scrollbar;
				if (bar)
					bar.scrollToElement(item.$n_());
				if (this._nativebar)
					zk(item).scrollIntoView(this.ebody);
			}

			if ((zk.edge_legacy || zk.ff! >= 4 || zk.ie == 10) && this.ebody && this._nativebar) { // B50-ZK-293: FF5 misses to fire onScroll
				// B50-ZK-440: ebody can be null when ROD
				this._currentTop = this.ebody.scrollTop;
				this._currentLeft = this.ebody.scrollLeft;
			}
		}
	}

	/**
	 * Returns the selected item.
	 * @return ItemWidget
	 */
	public getSelectedItem(): zul.sel.ItemWidget {
		return this._selItems[0];
	}

	/**
	 * Returns all selected items.
	 * @return Array
	 */
	public getSelectedItems(): zul.sel.ItemWidget[] {
		// returns a readonly array
		return this._selItems.$clone();
	}

	public override setHeight(height: string | null): void {
		if (!this._nvflex && this._height != height) {
			this._height = height;
			var n = this.$n();
			if (n) {
				n.style.height = height || '';
				this.onSize();
			}
		}
	}

	public override setVflex(v: boolean | string | null | undefined): void {
		super.setVflex(v);
		if (this.desktop) this.onSize();
	}

	public override setHflex(v: boolean | string | null | undefined): void {
		super.setHflex(v);
		if (this.desktop) this.onSize();
	}

	protected _getEbodyWd(): number {
		var anchor = this.$n_('a');
		// Bug in B30-1823236.zul, the anchor needs to be hidden before invoking this.ebody.clientWidth
		if (zk.webkit)
			anchor.style.display = 'none';

		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = zk.opera && this.ebody!.offsetHeight == 0 ? // B50-ZK-269
				this.ebody!.offsetWidth : this.ebody!.clientWidth;

		if (zk.webkit)
			anchor.style.display = '';
		return tblwd;
	}

	protected override _beforeCalcSize(): void {
		// Bug 279925
		if (zk.ie9) {
			var anchor = this.$n_('a');
			this._oldCSS = anchor.style.display;
			anchor.style.display = 'none';
		}

		//Bug in B30-1926094-1.zul
		if (zk.ie < 11)
			this._syncFocus(this._focusItem);

		this._calcHgh();
	}

	protected override _afterCalcSize(): void {
		// Bug 279925
		if (zk.ie9) {
			this.$n_('a').style.display = this._oldCSS!;
			delete this._oldCSS;
		}
		super._afterCalcSize();
	}

	/**
	 * Returns the index of the ItemWidget
	 * @param ItemWidget item
	 * @return int
	 */
	public indexOfItem(item: zul.sel.ItemWidget): number {
		if (item.getMeshWidget() == this) {
			for (var i = 0, it = this.getBodyWidgetIterator(), w; (w = it.next()); i++)
				if (w == item) return i;
		}
		return -1;
	}

	public toggleItemSelection(item: zul.sel.ItemWidget): void {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
		this.updateFormData();
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItem}.
	 *
	 * @param ItemWidget item
	 *            the item to select. If null, all items are deselected.
	 */
	public selectItem(item: zul.sel.ItemWidget): void {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected())
			this.setSelectedIndex(this.indexOfItem(item));
	}

	private _addItemToSelection(item: zul.sel.ItemWidget): void {
		if (!item.isSelected()) {
			if (!this._multiple) {
				this._selectedIndex = this.indexOfItem(item);
			} else {
				var index = this.indexOfItem(item);
				if (index < this._selectedIndex! || this._selectedIndex! < 0) {
					this._selectedIndex = index;
				}
				item._setSelectedDirectly(true);
			}
			this._selItems.push(item);
		}
	}

	private _removeItemFromSelection(item: zul.sel.ItemWidget): void {
		if (item.isSelected()) {
			if (!this._multiple) {
				this.clearSelection();
			} else {
				item._setSelectedDirectly(false);
				this._selItems.$remove(item);
			}
		}
	}

	/**
	 * Clears the selection.
	 */
	public clearSelection(): void {
		if (this._selItems.length) {
			for (var item: zul.sel.ItemWidget | undefined; (item = this._selItems.pop());)
				item._setSelectedDirectly(false);
			this._selectedIndex = -1;
			this._updHeaderCM();
		} else {
			//Bug ZK-3528: should reset _focusItem after clearing selected item
			this._focusItem = null;
			//Bug ZK-1834: should reset Focus Element after clearing selected item
			this._anchorTop = this._anchorLeft = 0;
			this._syncFocus();
		}
	}

	//super
	public override focus_(timeout?: number): boolean {
		var btn: HTMLAnchorElement | null | undefined;
		if (btn = this.$n('a')) {
			if (this._focusItem) {
				for (var it = this.getBodyWidgetIterator(), w: zul.sel.ItemWidget | null | undefined; (w = it.next());)
					if (this._isFocus(w)) {
						w.focus_(timeout);
						break;
					}
			} else {
				// Bug ZK-414
				if (this._currentTop) {
					// Bug ZK-2987: _currentTop might still holds the value from
					// previous page, make sure the anchor does not goes beyond
					// table height
					var offsetTop = this.ebody!.scrollHeight;
					if (this._currentTop > offsetTop)
						btn.style.top = offsetTop + 'px';
					else
						btn.style.top = this._currentTop + 'px';
				}
				if (this._currentLeft)
					btn.style.left = this._currentLeft + 'px';
			}
			this.focusA_(btn, timeout);
			return true;
		}
		return false;
	}

	public focusA_(btn: HTMLAnchorElement, timeout: number | undefined): void { //called by derived class
		zk(btn).focus(timeout);
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var btn = this.$n('a');
		if (btn)
			this.domListen_(btn, 'onFocus', 'doFocus_')
				.domListen_(btn, 'onKeyDown')
				.domListen_(btn, 'onBlur', 'doBlur_');
		this.updateFormData();
		this._updHeaderCM();
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var btn = this.$n('a');
		if (btn)
			this.domUnlisten_(btn, 'onFocus', 'doFocus_')
				.domUnlisten_(btn, 'onKeyDown')
				.domUnlisten_(btn, 'onBlur', 'doBlur_');
		super.unbind_(skipper, after, keepRod);
	}

	public override clearCache(): void {
		super.clearCache();
		this.efield = null;
	}

	protected override doFocus_(evt: zk.Event): void {
		var row	= this._focusItem || this._lastSelectedItem;
		if (row) row._doFocusIn();
		super.doFocus_(evt);
	}

	protected override doBlur_(evt: zk.Event): void { //if clicking on the selected item, don't lose focus
		if (this._focusItem) {
			this._lastSelectedItem = this._focusItem;
			this._focusItem._doFocusOut();
		}
		this._focusItem = null;
		super.doBlur_(evt);
	}

	/** Returns whether to ignore the selection.
	 * It is called when selecting an item ({@link ItemWidget#doSelect_}).
	 * <p>Default: always false (don't ignore) unless {@link #rightSelect} is true and event is onRightClick.
	 * Notice that clicking on button/textbox are already ignored, i.e.,
	 * this method won't be called if the user clicks on, say, a button.
	 * @param zk.Event evt the event
	 * @param ItemWidget row the row about to be selected
	 * @return int 1 (true): ignore,<br/>
	 * 0 (false): select if single select, and toggle selection if multiple,<br/>
	 * and -1: always select (even if multiple)
	 */
	protected shallIgnoreSelect_(evt: zk.Event, row: zul.sel.ItemWidget): boolean | -1 { //row has to be the second argument for backward compatible
		//see also _shallIgnore
		return evt.name == 'onRightClick' ? this.rightSelect ? -1 : true : false;
	}

	//@param bSel whether it is called by _doItemSelect
	public _shallIgnore(evt: zk.Event, bSel?: string | boolean): boolean | undefined { // move this function in the widget for override
		// F70-ZK-2433
		if (this.checkOnHighlightDisabled_())
			return true;

		if (!evt.domTarget || !evt.target!.canActivate())
			return true;

		if (bSel) {
			try {
				var el: HTMLElement | null = evt.domTarget;
				if (el) //Not use jq.isAncestor since it calls vparentNode
					for (;;) {
						if (el.id == this.uuid) //listbox
							break;
						if (!(el = el.parentNode as HTMLElement | null))
							return true; //vparent
					}
			} catch (e) {
				zk.debugLog((e as Error).message || e as string);
			}

			if (typeof (bSel = this.nonselectableTags) == 'string') {
				if (!bSel)
					return; //not ignore
				if (bSel == '*')
					return true;

				var tn = jq.nodeName(evt.domTarget),
					bInpBtn = tn == 'input' && (evt.domTarget as HTMLInputElement).type.toLowerCase() == 'button';
				if (bSel.indexOf(tn) < 0) {
					return bSel.indexOf('button') >= 0
						&& (_isButton(evt) || bInpBtn);
				}
				return !bInpBtn || bSel.indexOf('button') >= 0;
			}
		}

		return _focusable(evt);
	}

	// F70-ZK-2433 to be overridden
	protected checkOnHighlightDisabled_(): boolean {
		return false;
	}

	public _doItemSelect(row: zul.sel.ItemWidget, evt: zk.Event<zk.EventMetaData>): void { //called by ItemWidget
		//It is better not to change selection only if dragging selected
		//(like Windows does)
		//However, FF won't fire onclick if dragging, so the spec is
		//not to change selection if dragging (selected or not)
		var alwaysSelect,
			tg = evt.domTarget!,
			cm = row.$n('cm'),
			cmClicked = this._checkmark && (tg == cm || tg.parentNode == cm);
		if (zk.dragging || (!cmClicked && (this._shallIgnore(evt, true)
		|| ((alwaysSelect = this.shallIgnoreSelect_(evt, row))
			&& !(alwaysSelect = alwaysSelect < 0)))))
			return;

		var skipFocus = _focusable(evt); //skip focus if evt is on a focusable element
		if (this._checkmark
		&& !evt.data!.shiftKey && !(evt.data!.ctrlKey || evt.data!.metaKey)
		&& (!this._cdo || cmClicked)) {
			// Bug 2997034
			this._syncFocus(row);

			if (this._multiple) {
				var seled = row.isSelected();
				if (!seled || !alwaysSelect)
					this._toggleSelect(row, !seled, evt, skipFocus);
			} else
				this._select(row, evt, skipFocus);
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.webkit) && row.isListen('onDoubleClick')) {
				var now = jq.now(), last = row._last;
				row._last = now;
				if (last && now - last < 900)
					return; //ignore double-click
			}
			this._syncFocus(row);
			if (this._multiple) {
				if (evt.data!.shiftKey)
					this._selectUpto(row, evt, skipFocus);
				else if (evt.data!.ctrlKey || evt.data!.metaKey) //let multiple selection without checkmark work on tablet
					this._toggleSelect(row, !row.isSelected(), evt, skipFocus);
				else if (!alwaysSelect || !row.isSelected())// Bug: 1973470
					this._select(row, evt, skipFocus);
			} else
				this._select(row, evt, skipFocus);

			//since row might was selected, we always enforce focus here
			if (!skipFocus)
				row.focus();
			//if (evt) evt.stop();
			//No much reason to eat the event.
			//In opposite, it disabled popup (bug 1578659)
		}
	}

	/* Handles keydown sent to the body. */
	protected override doKeyDown_(evt: zk.Event<zk.EventKeyData>): false | undefined {
		if (!this._shallIgnore(evt)) {

		// Note: We don't intercept body's onfocus to gain focus back to anchor.
		// Otherwise, it cause scroll problem on IE:
		// When user clicks on the scrollbar, it scrolls first and call onfocus,
		// then it will scroll back to the focus because _focusToAnc is called
			switch (evt.data!.keyCode) {
			case 33: //PgUp
			case 34: //PgDn
			case 38: //UP
			case 40: //DOWN
			case 37: //LEFT
			case 39: //RIGHT
			case 32: //SPACE
			case 36: //Home
			case 35: //End
				if (!jq.nodeName(evt.domTarget!, 'a'))
					this.focus();
				if (evt.domTarget == this.$n('a')) {// for test tool.
					if (evt.target == this) //try to avoid the condition inside the _doKeyDown()
						evt.target = this._focusItem || this.getSelectedItem() || this;
					this._doKeyDown(evt);
				}
				evt.stop();
				return false;
			}
		}

		// disable item's content selection excluding input box and textarea
		if (!jq.nodeName(evt.domTarget!, 'input', 'textarea')) {
			this._disableSelection_ = true;
			zk(this.$n()).disableSelection();
		}
		// Feature #1978624
		if (evt.target == this) //try to give to the focus item
			evt.target = this._focusItem || this.getSelectedItem() || this;
		super.doKeyDown_(evt);
	}

	protected override doKeyUp_(evt: zk.Event): void {
		if (this._disableSelection_) {
			this._disableSelection_ = false;
			zk(this.$n()).enableSelection();
		}
		evt.stop({propagation: true});
		super.doKeyUp_(evt);
	}

	public _doKeyDown(evt: zk.Event<zk.EventKeyData>): boolean | undefined { //called by listener of this widget and ItemWidget
		if (_beforeChildKey(this, evt))
			return true;

		var row = this._focusItem || this.getSelectedItem(),
			data = evt.data as zk.EventKeyData,
			shift = data.shiftKey, ctrl = (data.ctrlKey || data.metaKey);
		if (shift && !this._multiple)
			shift = false; //OK to

		// F85-ZK-3507
		if (this._startRow && !shift && this._multiple)
			this._startRow = null;
		else if (!this._startRow && shift)
			this._startRow = row;

		var endless = false,
			step!: number,
			lastrow: zul.sel.ItemWidget | undefined;

		// for test tool when browser is webkit
		if (zk.webkit && typeof data.keyCode == 'string')
			data.keyCode = zk.parseInt(data.keyCode);
		switch (data.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
			var pgnl = this.paging || this._paginal;
			if (row && pgnl && !pgnl.isDisabled()) { // F60-ZK-715
				var npg = this.getActivePage() + (data.keyCode == 33 ? -1 : 1);
				// TODO: concern ctrl
				if (npg > -1 && npg < this.getPageCount())
					this.fire('onAcrossPage', {
						page: npg,
						offset: this.indexOfItem(row),
						shift: !shift || !this._multiple ? 0 :
							data.keyCode == 33 ? this.getPageSize() : -this.getPageSize()
					});
				return;
			}
			step = this._visibleRows()!;
			if (step == 0)
				step = this.getPageSize() || 20;
			if (data.keyCode == 33)
				step = -step;
			break;
		case 38: //UP
		case 40: //DOWN
			step = data.keyCode == 40 ? 1 : -1;
			break;
		case 32: //SPACE
			if (row) {
				if (this._multiple)
					this._toggleSelect(row, !row.isSelected(), evt);
				else
					this._select(row, evt);
			}
			break;
		case 36: //Home
		case 35: //End
			step = data.keyCode == 35 ? 1 : -1;
			endless = true;
			break;
		case 37: //LEFT
			if (row)
				this._doLeft(row);
			break;
		case 39: //RIGHT
			if (row)
				this._doRight(row);
			break;
		}

		if (step > 0 || (step < 0 && row)) {
			if (row && shift && !row.isDisabled() && row.isSelectable()) { // Bug ZK-1715: not select item if disabled.
				// F85-ZK-3507: shift + up/down: select item when moving outwards,
				// and deselect item when moving inwards
				this._toggleSelect(row, this._getToSelFlag(row, this._startRow!, step), evt);
			}
			var nrow: Node | null | undefined = row ? row.$n() : null;
			for (;;) {
				if (!nrow) { // no focused/selected item yet
					var w = this.getBodyWidgetIterator().next();
					if (w)
						nrow = w.$n(); // F60-ZK-423: first row
					else
						return; // empty
				} else
					nrow = step > 0 ? nrow.nextSibling : nrow.previousSibling;

				if (!nrow) { // F60-ZK-715: across to next/previous page if any
					if (endless)
						break; // ignore Home/End key
					var pg = this.paging || this._paginal,
						pnum: number;
					if (pg && !pg.isDisabled()) {
						pnum = pg.getActivePage();
						// TODO: concern ctrl
						if (step > 0 ? (pnum + 1 < pg.getPageCount()) : pnum > 0) {
							this.fire('onAcrossPage', {
								page: pnum + (step > 0 ? 1 : 0),
								offset: step > 0 ? 0 : -1,
								shift: !this._multiple || !shift ? 0 : step > 0 ? -1 : 1
							});
							const $class = this.$class as typeof SelectWidget;
							$class.shallSyncSelInView ||= {};
							$class.shallSyncSelInView[this.uuid] = true;
						}
					}
					break;
				}
				var r = zk.Widget.$<zul.sel.ItemWidget>(nrow)!;
				if (r instanceof zul.sel.Treerow)
					r = r.parent as unknown as zul.sel.ItemWidget; // FIXME: type of parent is inconsistent
				if (!r.isDisabled() && r.isSelectable()) {
					// F85-ZK-3507
					if (shift) this._toggleSelect(r, endless ? this._getToSelFlag(r, this._startRow!, step) : true, evt);

					if (zk(nrow).isVisible()) {
						// ZK-2971: save last row even when pressing shift
						lastrow = r;
						if (!endless) {
							if (step > 0) --step;
							else ++step;
							if (step == 0) break;
						}
					}
				}
			}
		}

		if (lastrow) {
			if (!shift) { // ZK-2971: already handled in the previous code block, ignore shift
				if (ctrl)
					this._focus(lastrow);
				else
					this._select(lastrow, evt);
			}
			this._syncFocus(lastrow);
			var bar = this._scrollbar;
			if (bar)
				bar.scrollToElement(lastrow.$n_());
			else {
				// 1823278: key up until selection is out of view port, then it should scroll.
				zk(lastrow.$n()).scrollIntoView(this.ebody); // Bug #1823947 and #1823278
			}
		}

		return _afterChildKey(evt);
	}

	private _getToSelFlag(row: zul.sel.ItemWidget, startRow: zul.sel.ItemWidget, step: number): boolean { // F85-ZK-3507
		if (!startRow)
			return true;
		var pos = startRow.compareItemPos_(row);
		return pos == 0 || (pos == -1 && step == -1) || (pos == 1 && step == 1);
	}

	public _doKeyUp(evt: zk.Event<zk.EventKeyData>): boolean { //called by ItemWidget only
		return _beforeChildKey(this, evt) || _afterChildKey(evt);
	}

	private _doLeft(row: zul.sel.ItemWidget): void {
		// An empty default implementation.
	}
	private _doRight(row: zul.sel.ItemWidget): void {
		// An empty default implementation.
	}

	/* maintain the offset of the focus proxy*/
	public _syncFocus(row?: zul.sel.ItemWidget | null): void {
		var focusEl = this.$n('a');
		if (!focusEl) //Bug ZK-1480: widget may not rendered when ROD enabled
			return;

		var focusElStyle = focusEl.style,
			oldTop = this._anchorTop,
			oldLeft = this._anchorLeft,
			offs: zk.Offset,
			n: HTMLElement | null | undefined;
		if (row && (n = row.$n())) {
			offs = zk(n).revisedOffset();
			offs = this._toStyleOffset(focusEl, offs[0] + this.ebody!.scrollLeft, offs[1]);
		} else	// ZK-798, use old value if exists
			offs = [oldLeft ? oldLeft : 0, oldTop ? oldTop : 0];

		this.fixAnchor_(offs, focusEl);
		if (this._anchorTop != offs[1] || this._anchorLeft != offs[0]) {
			//ZK-798, to prevent firing onAnchorPos too many times when moust over a rod listbox,
			//if _anchorTop/_anchorLeft is the same , just ignore the event.
			this._anchorTop = offs[1];
			this._anchorLeft = offs[0];
			this.fire('onAnchorPos', {top: this._anchorTop, left: this._anchorLeft});
		}

		focusElStyle.top = this._anchorTop + 'px';
		focusElStyle.left = this._anchorLeft + 'px';
	}

	private _toStyleOffset(el: HTMLElement, x: number, y: number): zk.Offset {
		var ofs1 = zk(el).revisedOffset(),
		x2 = zk.parseFloat(el.style.left), y2 = zk.parseFloat(el.style.top);
		return [x - ofs1[0] + x2, y - ofs1[1] + y2];
	}

	/**
	 * May need fix anchor.
	 * @param int[] offs The anchor offset [left, top]
	 * @since 6.0.0
	 */
	protected fixAnchor_(offs: zk.Offset, focusEl: HTMLElement): void {
		var body = this.ebody!,
			sw = body.scrollWidth,
			sh = body.scrollHeight;
		if (offs[0] >= sw) offs[0] = sw - jq(focusEl).width()!;
		if (offs[1] >= sh) offs[1] = sh - jq(focusEl).height()!;
	}

	/* Selects an item, notify server and change focus if necessary. */
	private _select(row: zul.sel.ItemWidget, evt: zk.Event<zk.EventMetaData>, skipFocus?: boolean): void {
		if (this._selectOne(row, skipFocus)) {
			//notify server
			this.fireOnSelect(row, evt);
		}
	}

	/* Selects a range from the last focus up to the specified one.
	 * Callable only if multiple
	 */
	private _selectUpto(row: zul.sel.ItemWidget, evt: zk.Event<zk.EventMetaData>, skipFocus: boolean): void {
		if (row.isSelected()) {
			if (!skipFocus)
				this._focus(row);
			return; //nothing changed
		}

		var focusfound = false, rowfound = false,
			// ZK-1096: this._lastSelectedItem is only updated when doBlur
			lastSelected = this._focusItem || this._lastSelectedItem!;
		//Bugfix: if lastSelected is no longer selected, look for closest selected item as starting point
		if (!lastSelected.isSelected()) {
			var rowIndex = this.indexOfItem(row),
				min = Number.MAX_VALUE,
				closestSelItem;
			// eslint-disable-next-line @typescript-eslint/prefer-for-of
			for (var i = 0; i < this._selItems.length; ++i) {
				var item = this._selItems[i],
					index = this.indexOfItem(item),
					diff = rowIndex - index,
					oldmin = min;
				if ((diff <= 0) && closestSelItem)
					break;
				min = Math.min(diff, min);
				if (min != oldmin)
					lastSelected = item;
			}
		}
		for (var it = this.getBodyWidgetIterator(), si = this.getSelectedItem(), w: zul.sel.ItemWidget | null | undefined; (w = it.next());) {
			if (w.isDisabled() || !w.isSelectable()) continue; // Bug: 2030986
			if (focusfound) {
				this._changeSelect(w, true);
				if (w == row)
					break;
			} else if (rowfound) {
				this._changeSelect(w, true);
				if (this._isFocus(w) || w == lastSelected)
					break;
			} else if (!si) { // Bug: 3337441
				if (w != row)
					continue;
				this._changeSelect(w, true);
				break;
			} else {
				rowfound = w == row;
				focusfound = this._isFocus(w) || w == lastSelected;
				if (rowfound || focusfound) {
					this._changeSelect(w, true);
					if (rowfound && focusfound)
						break;
				}
			}
		}

		if (!skipFocus)
			this._focus(row);
		this.fireOnSelect(row, evt);
	}

	/**
	 * Selects all items.
	 * @param boolean notify if true, fire onSelect event to server
	 * @param jq.Event evt
	 * @disable(zkgwt)
	 */
	public setSelectAll(notify: boolean, evt: zk.Event<zk.EventMetaData>): void {
		for (var it = this.getBodyWidgetIterator(), w: zul.sel.ItemWidget | null | undefined; (w = it.next());)
			if (w._loaded && !w.isDisabled() && w.isSelectable())
				this._changeSelect(w, true);
		if (notify) // FIXME: why was the condition `notify && evt !== true`?
			this.fireOnSelect(this.getSelectedItem(), evt);
	}

	/**
	 * Selects all items.
	 * @param boolean notify if true, fire onSelect event to server
	 * @param jq.Event evt
	 * @disable(zkgwt)
	 */
	public selectAll = SelectWidget.prototype.setSelectAll;

	/* Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
	 */
	private _selectOne(row: zul.sel.ItemWidget, skipFocus: boolean | undefined): boolean {
		var selItem = this.getSelectedItem();
		if (this._multiple) {
			if (row) this._unsetFocusExcept(row);
			var changed = this._unsetSelectAllExcept(row);
			if (!changed && row && selItem == row) {
				if (!skipFocus) this._setFocus(row, true);
				return false; //not changed
			}
		} else {
			if (selItem) {
				if (selItem == row) {
					if (!skipFocus) this._setFocus(row, true);
					return false; //not changed
				}
				this._changeSelect(selItem, false);
				if (row)
					if (!skipFocus) this._setFocus(selItem, false);
			}
			if (row) this._unsetFocusExcept(row);
		}
		//we always invoke _changeSelect to change focus
		if (row) {
			this._changeSelect(row, true);
			this._lastSelectedItem = row;
			if (!skipFocus) this._setFocus(row, true);
		}
		return true;
	}

	/* Toggle the selection and notifies server. */
	private _toggleSelect(row: zul.sel.ItemWidget, toSel: boolean, evt: zk.Event<zk.EventMetaData>, skipFocus?: boolean): void {
		//B70-ZK-2588: don't jump the focus if item is deselected and _multiple is true
		this._isSelecting = toSel;
		if (!this._multiple) {
			var old = this.getSelectedItem();
			if (row != old && toSel)
				this._changeSelect(row, false);
		}

		this._changeSelect(row, toSel);
		if (!skipFocus) this._focus(row);

		//notify server
		this.fireOnSelect(row, evt);
	}

	/** Fires the onSelect event.
	 * If the widget is created at the server, the event will be sent
	 * to the server too.
	 * @param zk.Widget ref the reference which causes this onSelect event.
	 * Ignored if null.
	 * @since 5.0.5
	 */
	public fireOnSelect(ref: zk.Widget, evt: zk.Event<zk.EventMetaData>): void {
		var data: zul.sel.ItemWidget[] = [];

		for (var it = this.getSelectedItems(), len = it.length, j = 0; j < len; j++)
			if (it[j].isSelected())
				data.push(it[j]);

		var edata: zk.EventMetaData | undefined,
			keep: boolean | undefined = true,
			checkSelectAll = false;
		if (this._multiple && this._headercm) {
			checkSelectAll = jq(this._headercm).hasClass(zk.Widget.$(this._headercm)!.$s('checked'));
		}
		if (evt) {
			edata = evt.data!;
			if (this._multiple) {// B50-ZK-421

				// Bug ZK-2969
				if (this._headercm && jq.isAncestor(this._headercm, evt.domTarget) && !checkSelectAll) {
					keep = false;
				} else {
					var tg = evt.domTarget!,
						cm = ref && ref.$n('cm');
					keep = (edata.ctrlKey || edata.metaKey) || edata.shiftKey
							|| (this._checkmark && (!this._cdo || (tg == cm || tg.parentNode == cm) || checkSelectAll));
				}
			}
		}

		this.fire('onSelect',
			zk.copy({items: data, reference: ref, clearFirst: !keep, selectAll: checkSelectAll}, edata),
			{rtags: {selectAll: checkSelectAll}, toServer: !!this._model});
	}

	/* Changes the specified row as focused. */
	private _focus(row: zul.sel.ItemWidget): void {
		if (this.canActivate({checkOnly: true})) {
			this._unsetFocusExcept(row);
			this._setFocus(row, true);
		}
	}

	/* Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	private _changeSelect(row: zul.sel.ItemWidget, toSel: boolean): boolean {
		var changed = !!row.isSelected() != toSel;
		if (changed) {
			row.setSelected(toSel);
			//row._toggleEffect(true);
		}
		return changed;
	}

	private _isFocus(row: zul.sel.ItemWidget): boolean {
		return this._focusItem == row;
	}

	/* Changes the focus status, and return whether it is changed. */
	private _setFocus(row: zul.sel.ItemWidget, bFocus: boolean): boolean {
		var changed = this._isFocus(row) != bFocus;
		if (changed) {
			if (bFocus) {
				if (!row.focus())
					this.focus();

				if (!this.paging && zk.gecko)
					this.fireOnRender(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...
			}
		}
		if (!bFocus)
			row._doFocusOut();
		return changed;
	}

	/* Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	private _unsetSelectAllExcept(row: zul.sel.ItemWidget): boolean {
		this.$$selectAll = undefined;
		var changed = false;
		for (var it = this.getSelectedItems(), j = it.length; j--;) {
			if (it[j] != row && this._changeSelect(it[j], false))
				changed = true;
		}
		return changed;
	}

	/* Cleans focus except the specified one.
	 */
	private _unsetFocusExcept(row: zul.sel.ItemWidget): void {
		if (this._focusItem && this._focusItem != row) {
			this._setFocus(this._focusItem, false);
			this._focusItem = null;
		}
	}

	public _updHeaderCM(): void { //update header's checkmark
		if (this._headercm && this._multiple) {
			var box = this,
				v: number;
			this._nUpdHeaderCM = (v = this._nUpdHeaderCM!) > 0 ? v + 1 : 1;
			setTimeout(function () {_updHeaderCM(box);}, 100); //do it in batch
		}
	}

	public $fireService(evtName: string, data: unknown, callback: zk.Callable): void {
		if (!this._$services) {
			this._$services = {};
		}
		if (!this._$services[evtName]) {
			this._$services[evtName] = [];
		}
		this._$services[evtName]!.push(callback);
		this.fire(evtName, data, {toServer: true}, 250);
	}

	public $doService(evtName: string, data: unknown): void {
		var s = this._$services && this._$services[evtName];
		if (s) {
			while (s.length)
				s.shift()!.call(this, data);

			this._$services![evtName] = null;
		}
	}

	public $hasService(evtName: string): boolean {
		return !!(this._$services && this._$services[evtName]);
	}

	public _isAllSelected(): boolean {
		//B70-ZK-1953: if selectedItems is empty return false.
		if (!this._selItems.length) {
			if (this._headercm && this._model && !this.$hasService('onUpdateSelectAll')) {
				var headerWgt = zk.Widget.$<zul.mesh.HeaderWidget>(this._headercm)!,
					zcls = headerWgt.getZclass() + '-checked',
					$headercm = jq(this._headercm);
				$headercm.removeClass(zcls);
				headerWgt._checked = false;
			}
			return false;
		}
		if (this._model) {
			if (!this.$hasService('onUpdateSelectAll')) {
				this.$fireService('onUpdateSelectAll', null, function (this: SelectWidget, v: boolean) {
					if (this.desktop && this._headercm && this._multiple) {
						var headerWgt = zk.Widget.$<zul.mesh.HeaderWidget>(this._headercm)!,
							zcls = headerWgt.getZclass() + '-checked',
							$headercm = jq(this._headercm);
						$headercm[v ? 'addClass' : 'removeClass'](zcls);
						headerWgt._checked = v;
						this.$$selectAll = v;
					}
				});
			}

			// use the server's state
			if (this.$$selectAll != undefined) {
				return this.$$selectAll;
			}
		}

		var isGroupSelect = this.groupSelect;
		for (var it = this.getBodyWidgetIterator({skipHidden: true}), w: zul.sel.ItemWidget | null | undefined; (w = it.next());) {
			//Bug ZK-1998: skip listgroup and listgroupfoot widget if groupSelect is false
			if ((_isListgroup(w) || _isListgroupfoot(w)) && !isGroupSelect)
				continue;
			if (w._loaded && !w.isDisabled() && w.isSelectable() && !w.isSelected())
				return false;
		}
		return true;
	}

	protected override _ignoreHghExt(): boolean {
		return this._rows > 0;
	}

	//@Override
	public override onResponse(ctl: zk.ZWatchController, opts: {rtags: {selectAll?}}): void {
		if (this._shallSyncFocus) {
			var child = this._shallSyncFocus;

			// Bug ZK-2901
			if (child && child === true) { // called by Tree.js
				this._anchorTop = this._anchorLeft = 0;
				jq(this.$n_('a')).css({top: 0, left: 0});
			} else {

				// 1. Bug ZK-1473: when using template to render listbox,
				//   this._focusItem still remain the removed one,
				//   set it with the newly rendered one to prevent keyboard navigation jump back to top
				// 2. ZK-2048: should ignore Treechildren
				// 3. for ZK-2342 Bug, we move the invoking of this._syncFocus() from onChildRemoved_() and onChildAdded_() to here.
				if (!child.desktop) {
					child = this.getSelectedItem();
					if (!child && jq.isNumeric(this.getSelectedIndex())) {
						var selIndex = this.getSelectedIndex()!;
						if (selIndex >= 0) {
							for (var it = this.getBodyWidgetIterator(); it.hasNext();) {
								var row = it.next();
								if (row && row._index == selIndex) {
									child = row;
									break;
								}

							}
						}
					}
				}
				this._focusItem = child;
				this._syncFocus(child);
			}
			this._shallSyncFocus = false;
		}
		if (this._shallSyncCM) {
			if (!opts.rtags.selectAll)
				this._updHeaderCM();
			this._shallSyncCM = false;
		}
		super.onResponse(ctl, opts);
	}

	protected override onChildAdded_(child: zul.mesh.HeadWidget): void {
		super.onChildAdded_(child);
		if (this.desktop) {
			if (child instanceof zul.sel.ItemWidget && child.isSelected())
				this._shallSyncFocus = child;
		}
	}

	//@Override
	protected override onChildRemoved_(child: zul.mesh.HeadWidget): void {
		super.onChildRemoved_(child);
		var selItems = this._selItems, len;
		if (this.desktop && (len = selItems.length) && child instanceof zul.sel.ItemWidget)
			this._shallSyncFocus = selItems[len - 1];
		//	Bug ZK-1473: when using template to render listbox,
		//   the item will be remove but current this._focusItem still remain,
		//   disable it to prevent keyboard navigation jump back to top
		if (this._focusItem == child as never) { // FIXME: condition always because types don't overlap
			this._focusItem = null;
			// Bug in test case ZK-2534, we need to resync the lastSelectedItem if onBlur event is not triggered.
			this._lastSelectedItem = this._focusItem;

			//ZK-2804: the first selected item may be removed in rod when
			//select multiple items with shift key
			this._itemForSelect = child as never; // FIXME: Same problem as in the `if` condition.
		}
	}

	//@Override
	public override replaceWidget(newwgt: zul.sel.SelectWidget, skipper?: zk.Skipper): void {
		super.replaceWidget(newwgt, skipper);

		newwgt._lastSelectedItem = _fixReplace(this._lastSelectedItem);
		newwgt._focusItem = _fixReplace(this._focusItem);
	}
}
zul.sel.SelectWidget = zk.regClass(SelectWidget);