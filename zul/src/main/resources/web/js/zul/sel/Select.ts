/* Select.ts

	Purpose:

	Description:

	History:
		Mon Jun  1 16:43:51     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML select tag.
 */
@zk.WrapClass('zul.sel.Select')
export class Select extends zul.Widget<HTMLSelectElement> {
	_selectedIndex = -1;
	_rows = 0;
	_shouldRerenderFlag?: boolean;
	_selItems: zul.sel.Option[];
	_groupsInfo: zul.sel.Optgroup[];
	_multiple?: boolean;
	_disabled?: boolean;
	_name?: string;
	_maxlength?: number;

	constructor() {
		super(); // FIXME: params?
		this._selItems = [];
		this._groupsInfo = [];
	}

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isMultiple(): boolean {
		return !!this._multiple;
	}

	/**
	 * Sets whether multiple selections are allowed.
	 * @param boolean multiple
	 */
	setMultiple(multiple: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiple;
		this._multiple = multiple;

		if (o !== multiple || opts?.force) {
			const n = this.$n();
			if (n) n.multiple = multiple;
		}

		return this;
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			const n = this.$n();
			if (n) n.disabled = disabled;
		}

		return this;
	}

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 * @return int
	 */
	getSelectedIndex(): number {
		return this._selectedIndex;
	}

	/**
	 * Deselects all of the currently selected items and selects the item with
	 * the given index.
	 * @param int selectedIndex
	 */
	setSelectedIndex(selectedIndex: number, opts?: Record<string, boolean>): this {
		const o = this._selectedIndex;
		this._selectedIndex = selectedIndex;

		if (o !== selectedIndex || opts?.force) {
			let i = 0,
				j = 0,
				w: zk.Widget | undefined;
			const n = this.$n();
			this.clearSelection();
			// B50-ZK-989: original skipFixIndex way gives wrong value for this._selectedIndex
			// select from server API call, fix the index
			for (w = this.firstChild; w && i < selectedIndex; w = w.nextSibling, i++) {
				if (w instanceof zul.sel.Option) {
					if (!w.isVisible())
						j++;
				} else if (w instanceof zul.sel.Optgroup)
					j++;
				else i--;
			}

			selectedIndex -= j;
			if (n)
				n.selectedIndex = selectedIndex;

			if (selectedIndex > -1 && w && w instanceof zul.sel.Option) {
				w.setSelected(true);
				this._selItems.push(w);
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
	getName(): string | undefined {
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
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			const n = this.$n();
			if (n) n.name = name;
		}

		return this;
	}

	/**
	 * Returns the rows. Zero means no limitation.
	 * <p>
	 * Default: 0.
	 * @return int
	 */
	getRows(): number {
		return this._rows;
	}

	/**
	 * Sets the rows.
	 * <p>
	 * Note: if both {@link #setHeight} is specified with non-empty,
	 * {@link #setRows} is ignored
	 * @param int rows
	 */
	setRows(rows: number, opts?: Record<string, boolean>): this {
		const o = this._rows;
		this._rows = rows;

		if (o !== rows || opts?.force) {
			const n = this.$n();
			if (n) n.size = rows;
		}

		return this;
	}

	/**
	 * Returns the maximal length of each item's label.
	 * @return int
	 */
	getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of each item's label.
	 * @param int maxlength
	 */
	// FIXME: can a defSet generated setter accept more than one arguments before `opts`?
	setMaxlength(maxlength: number, fromServer: boolean, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || opts?.force) {
			if (this.desktop)
				this.requestRerender_(fromServer);
		}

		return this;
	}

	// ZK-2133: should sync all items
	setChgSel(chgSel: string): this { //called from the server
		const sels = {};
		for (let j = 0; ;) {
			const k = chgSel.indexOf(',', j),
				s = (k >= 0 ? chgSel.substring(j, k) : chgSel.substring(j)).trim();
			if (s) sels[s] = true;
			if (k < 0) break;
			j = k + 1;
		}
		for (let w = this.firstChild; w; w = w.nextSibling)
			this._changeSelect(w as zul.sel.Option, sels[w.uuid] == true);
		return this;
	}

	/* Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	_changeSelect(child: zul.sel.Option | zul.sel.Optgroup, toSel: boolean): boolean {
		if (child instanceof zul.sel.Option) {
			const changed = child.isSelected() != toSel;
			if (changed) {
				child.setSelected(toSel);
			}
			return changed;
		}
		return false;
	}

	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 * @param Option item
	 */
	toggleItemSelection(item: zul.sel.Option): void {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 *
	 * @param Option item
	 *            the item to select. If null, all items are deselected.
	 */
	selectItem(item?: zul.sel.Option): void {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected()) {
			if (item.getOptionIndex_)
				this.setSelectedIndex(item.getOptionIndex_());
			else
				this.setSelectedIndex(item.getChildIndex());
		}
	}

	_addItemToSelection(item: zul.sel.Option): void {
		if (!item.isSelected()) {
			const multiple = this._multiple;
			if (!multiple)
				this.clearSelection();
			const index = item.getOptionIndex_ ? item.getOptionIndex_() : item.getChildIndex();
			if (!multiple || index < this._selectedIndex || this._selectedIndex < 0)
				this._selectedIndex = index;
			item._setSelectedDirectly(true);
			this._selItems.push(item);
		}
	}

	_removeItemFromSelection(item: zul.sel.Option): void {
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
	clearSelection(): void {
		if (this._selItems.length) {
			for (let item: zul.sel.Option | undefined; (item = this._selItems.pop());)
				item._setSelectedDirectly(false);
			this._selectedIndex = -1;
		}
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		let v;
		return super.domAttrs_(no)
			+ (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (this.isMultiple() ? ' multiple="multiple"' : '')
			+ ((v = this.getSelectedIndex()) > -1 ? ' selectedIndex="' + v + '"' : '')
			+ ((v = this.getRows()) > 0 ? ' size="' + v + '"' : '')
			+ ((v = this.getName()) ? ' name="' + v + '"' : '');
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		const n = this.$n_();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');

		if (!zk.gecko) {
			const fn: [unknown, CallableFunction] = [this, this._fixSelIndex];
			zWatch.listen({ onRestore: fn, onVParent: fn });
		}
		zWatch.listen({ onCommandReady: this });

		this._fixSelIndex();
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onCommandReady: this });
		const n = this.$n_();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		super.unbind_(skipper, after, keepRod);

		const fn: [unknown, CallableFunction] = [this, this._fixSelIndex];
		zWatch.unlisten({ onRestore: fn, onVParent: fn });
	}

	_fixSelIndex(): void {
		if (this._selectedIndex < 0)
			this.$n_().selectedIndex = -1;
	}

	_doChange(evt: zk.Event): void {
		const n = this.$n_(),
			opts = n.options,
			multiple = this._multiple,
			data: zul.sel.Option[] = [];
		let changed = false,
			reference: zk.Widget | undefined;
		for (let j = 0, ol = opts.length; j < ol; ++j) {
			const opt = opts[j],
				o = zk.Widget.$<zul.sel.Option>(opt.id),
				v = opt.selected;
			if (multiple) {
				if (o && o._selected != v) {
					this.toggleItemSelection(o);
					changed = true;
				}
				if (v) {
					data.push(o!);
					if (!reference) reference = o;
				}
			} else {
				if (o && o._selected != v && v) { // found the newly selected one
					this._addItemToSelection(o); //will clear other selection first
					changed = true;
					data.push(o);
					reference = o;
					break;
				}
			}
		}
		if (!changed)
			return;

		this.fire('onSelect', { items: data, reference });
	}

	override doBlur_(evt: zk.Event): void {
		// Empty for override
	}

	//Bug 1756559: ctrl key shall fore it to be sent first
	override beforeCtrlKeys_(evt: zk.Event): void {
		this._doChange(evt);
	}

	override onChildAdded_(child: zk.Widget): void {
		if (child instanceof zul.sel.Optgroup)
			this._groupsInfo.push(child);
		if (this.desktop)
			this.requestRerender_(true);
	}

	override onChildRemoved_(child: zk.Widget): void {
		if (child instanceof zul.sel.Optgroup)
			this._groupsInfo.$remove(child);
		if (this.desktop && !this.childReplacing_)
			this.requestRerender_(true);
	}

	requestRerender_(fromServer?: boolean): void {
		if (fromServer)
			this._shouldRerenderFlag = true;
		else
			this.rerender();
	}

	onCommandReady(): void {
		if (this._shouldRerenderFlag) {
			this._shouldRerenderFlag = false;
			this.rerender();
		}
	}

	/** Returns whether any {@link Optgroup} exists.
	 * @return boolean
	 * @since 8.6.0
	 */
	hasGroup(): boolean {
		return !!this._groupsInfo.length;
	}

	/** Returns the number of {@link Optgroup}.
	 * @return int
	 * @since 8.6.0
	 */
	getGroupCount(): number {
		return this._groupsInfo.length;
	}

	/** Returns a list of all {@link Optgroup}. The order is unmaintained.
	 * @return Array
	 * @since 8.6.0
	 */
	getGroups(): zul.sel.Optgroup[] {
		return this._groupsInfo.$clone();
	}

	setItemsInvalid_(itemsInvalid: ArrayLike<unknown>[]): void {
		zAu.createWidgets(itemsInvalid, (ws) => {
			this.replaceCavedChildren_('', ws);
		}, function (wx) {
			return wx;
		});
	}
}