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
export class Select extends zul.Widget<HTMLSelectElement> {
	public _selectedIndex = -1;
	private _rows = 0;
	private _shouldRerenderFlag?: boolean;
	public _selItems: zul.sel.Option[];
	private _groupsInfo: zul.sel.Optgroup[];
	private _multiple?: boolean;
	private _disabled?: boolean;
	private _name?: string;
	private _maxlength?: number;

	public constructor() {
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
			var n = this.$n();
			if (n) n.multiple = (multiple ? 'multiple' : '') as unknown as boolean;
		}

		return this;
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * @param boolean disabled
	 */
	public setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.disabled = (disabled ? 'disabled' : '') as unknown as boolean;
		}

		return this;
	}

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 * @return int
	 */
	public getSelectedIndex(): number {
		return this._selectedIndex;
	}

	/**
	 * Deselects all of the currently selected items and selects the item with
	 * the given index.
	 * @param int selectedIndex
	 */
	public setSelectedIndex(selectedIndex: number, opts?: Record<string, boolean>): this {
		const o = this._selectedIndex;
		this._selectedIndex = selectedIndex;

		if (o !== selectedIndex || (opts && opts.force)) {
			var i = 0,
				j = 0,
				w: zk.Widget | null,
				n = this.$n();
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
			var n = this.$n();
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
	public getRows(): number {
		return this._rows;
	}

	/**
	 * Sets the rows.
	 * <p>
	 * Note: if both {@link #setHeight} is specified with non-empty,
	 * {@link #setRows} is ignored
	 * @param int rows
	 */
	public setRows(rows: number, opts?: Record<string, boolean>): this {
		const o = this._rows;
		this._rows = rows;

		if (o !== rows || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.size = rows;
		}

		return this;
	}

	/**
	 * Returns the maximal length of each item's label.
	 * @return int
	 */
	public getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of each item's label.
	 * @param int maxlength
	 */
	// FIXME: can a defSet generated setter accept more than one arguments before `opts`?
	public setMaxlength(maxlength: number, fromServer: boolean, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || (opts && opts.force)) {
			if (this.desktop)
				this.requestRerender_(fromServer);
		}

		return this;
	}

	// ZK-2133: should sync all items
	public setChgSel(val: string): void { //called from the server
		var sels = {};
		for (var j = 0; ;) {
			var k = val.indexOf(',', j),
			s = (k >= 0 ? val.substring(j, k) : val.substring(j)).trim();
			if (s) sels[s] = true;
			if (k < 0) break;
			j = k + 1;
		}
		for (var w = this.firstChild; w; w = w.nextSibling)
			this._changeSelect(w as zul.sel.Option, sels[w.uuid] == true);
	}

	/* Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	private _changeSelect(option: zul.sel.Option, toSel: boolean): boolean {
		var changed = !!option.isSelected() != toSel;
		if (changed) {
			option.setSelected(toSel);
		}
		return changed;
	}

	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 * @param Option item
	 */
	public toggleItemSelection(item: zul.sel.Option): void {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 *
	 * @param Option item
	 *            the item to select. If null, all items are deselected.
	 */
	public selectItem(item: zul.sel.Option): void {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected()) {
			if (item.getOptionIndex_)
				this.setSelectedIndex(item.getOptionIndex_());
			else
				this.setSelectedIndex(item.getChildIndex());
		}
	}

	private _addItemToSelection(item: zul.sel.Option): void {
		if (!item.isSelected()) {
			var multiple = this._multiple;
			if (!multiple)
				this.clearSelection();
			var index = item.getOptionIndex_ ? item.getOptionIndex_() : item.getChildIndex();
			if (!multiple || (index < this._selectedIndex || this._selectedIndex < 0))
				this._selectedIndex = index;
			item._setSelectedDirectly(true);
			this._selItems.push(item);
		}
	}

	private _removeItemFromSelection(item: zul.sel.Option): void {
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
			var item: zul.sel.Option | undefined;
			for (;(item = this._selItems.pop());)
				item._setSelectedDirectly(false);
			this._selectedIndex = -1;
		}
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		var v;
		return super.domAttrs_(no)
			+ (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (this.isMultiple() ? ' multiple="multiple"' : '')
			+ ((v = this.getSelectedIndex()) > -1 ? ' selectedIndex="' + v + '"' : '')
			+ ((v = this.getRows()) > 0 ? ' size="' + v + '"' : '')
			+ ((v = this.getName()) ? ' name="' + v + '"' : '');
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var n = this.$n_();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');

		if (!zk.gecko) {
			var fn: [unknown, zk.Callable] = [this, this._fixSelIndex];
			zWatch.listen({onRestore: fn, onVParent: fn});
		}
		zWatch.listen({onCommandReady: this});

		this._fixSelIndex();
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onCommandReady: this});
		var n = this.$n_();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		super.unbind_(skipper, after, keepRod);

		var fn: [unknown, zk.Callable] = [this, this._fixSelIndex];
		zWatch.unlisten({onRestore: fn, onVParent: fn});
	}

	private _fixSelIndex(): void {
		if (this._selectedIndex < 0)
			this.$n_().selectedIndex = -1;
	}

	private _doChange(evt: zk.Event): void {
		var n = this.$n_(),
			opts = n.options,
			multiple = this._multiple,
			data: string[] = [],
			changed = false,
			reference: string | undefined;
		for (var j = 0, ol = opts.length; j < ol; ++j) {
			var opt = opts[j],
				o = zk.Widget.$<zul.sel.Option>(opt.id),
				v = opt.selected;
			if (multiple) {
				if (o && o._selected != v) {
					this.toggleItemSelection(o);
					changed = true;
				}
				if (v) {
					data.push(opt.id);
					if (!reference) reference = opt.id;
				}
			} else {
				if (o && o._selected != v && v) { // found the newly selected one
					this._addItemToSelection(o); //will clear other selection first
					changed = true;
					data.push(opt.id);
					reference = opt.id;
					break;
				}
			}
		}
		if (!changed)
			return;

		this.fire('onSelect', {items: data, reference: reference});
	}

	protected override doBlur_(evt: zk.Event): void {
		// Empty for override
	}

	//Bug 1756559: ctrl key shall fore it to be sent first
	protected override beforeCtrlKeys_(evt: zk.Event): void {
		this._doChange(evt);
	}

	protected override onChildAdded_(child: zk.Widget): void {
		if (child instanceof zul.sel.Optgroup)
			this._groupsInfo.push(child);
		if (this.desktop)
			this.requestRerender_(true);
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		if (child instanceof zul.sel.Optgroup)
			this._groupsInfo.$remove(child);
		if (this.desktop && !this.childReplacing_)
			this.requestRerender_(true);
	}

	public requestRerender_(fromServer: boolean | undefined): void {
		if (fromServer)
			this._shouldRerenderFlag = true;
		else
			this.rerender();
	}

	public onCommandReady(): void {
		if (this._shouldRerenderFlag) {
			this._shouldRerenderFlag = false;
			this.rerender();
		}
	}

	/** Returns whether any {@link Optgroup} exists.
	 * @return boolean
	 * @since 8.6.0
	 */
	public hasGroup(): boolean {
		return !!this._groupsInfo.length;
	}

	/** Returns the number of {@link Optgroup}.
	 * @return int
	 * @since 8.6.0
	 */
	public getGroupCount(): number {
		return this._groupsInfo.length;
	}

	/** Returns a list of all {@link Optgroup}. The order is unmaintained.
	 * @return Array
	 * @since 8.6.0
	 */
	public getGroups(): zul.sel.Optgroup[] {
		return this._groupsInfo.$clone();
	}

	protected setItemsInvalid_(wgts: ArrayLike<unknown>[]): void {
		var wgt = this;
		zAu.createWidgets(wgts, function (ws) {
			wgt.replaceCavedChildren_('', ws);
		}, function (wx) {
			return wx;
		});
	}
}
zul.sel.Select = zk.regClass(Select);