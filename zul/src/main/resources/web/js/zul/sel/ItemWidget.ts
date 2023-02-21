/* ItemWidget.ts

	Purpose:

	Description:

	History:
		Fri May 22 21:50:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _isListgroup(w: ItemWidget): boolean {
	return zk.isLoaded('zkex.sel') && w instanceof zkex.sel.Listgroup;
}
function _isListgroupfoot(w: ItemWidget): boolean {
	return zk.isLoaded('zkex.sel') && w instanceof zkex.sel.Listgroupfoot;
}
/**
 * The item widget for {@link Treeitem} and {@link Listitem}
 */
@zk.WrapClass('zul.sel.ItemWidget')
export class ItemWidget extends zul.Widget<HTMLTableRowElement> implements zul.mesh.Item {
	override nextSibling?: zul.sel.ItemWidget;
	override previousSibling?: zul.sel.ItemWidget;

	/** @internal */
	_loaded?: boolean;
	/** @internal */
	_index?: number;

	/** @internal */
	_selectable = true;
	/** @internal */
	_checkable?: boolean;
	/** @internal */
	_disabled?: boolean;
	/** @internal */
	_value?: string;
	/** @internal */
	_selected?: boolean;
	/** @internal */
	_shallCheckClearCache?: boolean;
	/** @internal */
	_userSelection?: boolean;
	/** @internal */
	_disableSelection_?: boolean;
	/** @internal */
	_last?: number; // zul.sel.SelectWidget.prototype._doItemSelect

	/**
	 * @deprecated As of release 8.0.0, please use {@link isSelectable}
	 * @returns boolean
	 */
	isCheckable(): boolean {
		return !!this._checkable;
	}

	/**
	 * @deprecated As of release 8.0.0, please use {@link setSelectable}
	 */
	setCheckable(checkable: boolean, opts?: Record<string, boolean>): this {
		const o = this._checkable;
		this._checkable = checkable;

		if (o !== checkable || opts?.force) {
			this.setSelectable(checkable);
		}

		return this;
	}

	/**
	 * @returns whether it is selectable.
	 * @defaultValue `true`.
	 * @since 8.0.0
	 */
	isSelectable(): boolean {
		return this._selectable;
	}

	/**
	 * Sets whether it is selectable.
	 * @defaultValue `true`.
	 * @since 8.0.0
	 */
	setSelectable(selectable: boolean, opts?: Record<string, boolean>): this {
		const o = this._selectable;
		this._selectable = selectable;

		if (o !== selectable || opts?.force) {
			if (this.desktop)
				this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			if (this.desktop)
				this.rerender();
		}

		return this;
	}

	/**
	 * @returns the value.
	 * @defaultValue `null`.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem/treeitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	getValue(): string | undefined {
		return this._value;
	}

	/**
	 * @returns the index of this item (a.k.a., the order in the SelectWidget).
	 * @since 10.0.0
	 */
	getIndex(): number | undefined {
		return this._index;
	}

	/**
	 * Sets the value.
	 * @param value - the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem/treeitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	setValue(value: string): this {
		this._value = value;
		return this;
	}

	/**
	 * Sets whether it is selected.
	 */
	setSelected(selected: boolean): this {
		if (this._selected != selected) {
			this.getMeshWidget()?.toggleItemSelection(this);
			this._setSelectedDirectly(selected);
		}
		return this;
	}

	/** @internal */
	_setSelectedDirectly(selected: boolean): void {
		const n = this.$n();

		// do this before _updHeaderCM(), otherwise, it will call too many times to sync the state.
		this._selected = selected;

		if (n) {
			jq(n)[selected ? 'addClass' : 'removeClass'](this.$s('selected')!);
			this._updHeaderCM();
		}
	}

	/**
	 * @returns the label of the {@link Listcell} or {@link Treecell} it contains, or null
	 * if no such cell.
	 */
	getLabel(): string | undefined {
		// Note: Only Listitem uses this method. Treeitem overrides this method.
		return this.firstChild ? (this.firstChild as zul.sel.Listcell).getLabel() : undefined;
	}

	/**
	 * @returns whether it is selected.
	 * @defaultValue `false`.
	 */
	isSelected(): boolean {
		return !!this._selected;
	}

	/**
	 * @returns whether is stripeable or not.
	 * @defaultValue `true`.
	 * @internal
	 */
	isStripeable_(): boolean {
		return true;
	}

	/**
	 * @returns the mesh widget.
	 */
	getMeshWidget(): zul.sel.SelectWidget | undefined {
		return this.parent as zul.sel.SelectWidget | undefined;
	}

	/** @internal */
	_getVisibleChild(row: HTMLTableRowElement): HTMLElement {
		for (let i = 0, j = row.cells.length; i < j; i++)
			if (zk(row.cells[i]).isVisible()) return row.cells[i];
		return row;
	}

	override setVisible(visible: boolean): this {
		if (this._visible != visible) { // not to use isVisible()
			super.setVisible(visible);
			if (this.isStripeable_()) {
				// Only Listbox is stripeable.
				(this.getMeshWidget() as zul.sel.Listbox | undefined)?.stripe();
			}
		}
		return this;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		let scls = super.domClass_(no);
		if (!no || !no.zclass) {
			if (this.isDisabled())
				scls += (scls ? ' ' : '') + this.$s('disabled');
			//Bug ZK-1998: only apply selected style if groupSelect is true
			if (_isListgroup(this) || _isListgroupfoot(this)) {
				if (this.getMeshWidget()!.groupSelect && this.isSelected())
					scls += (scls ? ' ' : '') + this.$s('selected');
			} else {
				if (this.isSelected())
					scls += (scls ? ' ' : '') + this.$s('selected');
			}
		}
		return scls;
	}

	/** @internal */
	override focus_(timeout?: number): boolean {
		const mesh = this.getMeshWidget()!;
		this._doFocusIn();
		mesh._syncFocus(this);
		mesh.focusA_(mesh.$n('a')!, timeout);
		return true;
	}

	/** @internal */
	_doFocusIn(): void {
		const n = this.$n(),
			mesh = this.getMeshWidget();
		if (n) {
			const cls = this.$s('focus')!,
				lastn = mesh?._focusItem?.$n();
			// ZK-3077: focus out the last focused item first (for draggable issue)
			if (lastn)
				jq(lastn).removeClass(cls);
			// Bugfix: add focus class on itself, not on its children elements
			jq(n).addClass(cls);
		}

		if (mesh)
			mesh._focusItem = this;
	}

	/** @internal */
	_doFocusOut(): void {
		const n = this.$n();
		if (n) {
			const cls = this.$s('focus')!;
			jq(n).removeClass(cls);
			jq(n.cells).removeClass(cls);
		}
	}

	/** @internal */
	_updHeaderCM(bRemove?: boolean): void { //update header's checkmark
		const box = this.getMeshWidget();
		if (box?._headercm && box._multiple) {
			if (bRemove) {
				box._updHeaderCM();
				return;
			}

			const headerWgt = zk.Widget.$<zul.mesh.HeaderWidget>(box._headercm)!,
				zcls = headerWgt.$s('checked')!,
				$headercm = jq(box._headercm);

			// only update for user's selection or sharable model case (ZK-2969 test case)
			if (!this.isSelected() && (box.$$selectAll == undefined || this._userSelection)) {
				$headercm.removeClass(zcls);
				headerWgt._checked = false;
			} else if (!$headercm.hasClass(zcls))
				box._updHeaderCM(); //update in batch since we have to examine one-by-one
		}
	}

	/** @internal */
	override getDragMessage_(): string | undefined {
		const iterator = this.getMeshWidget()!.itemIterator();
		let cnt = 2,
			msg: string | undefined;
		if (!this.isSelected()) return zUtl.encodeXML(this.getLabel()!);
		while (iterator.hasNext()) {
			const item = iterator.next()!;
			if (item.isSelected()) {
				let label = item.getLabel()!;
				if (label.length > 9)
					label = label.substring(0, 9) + '...';
				label = zUtl.encodeXML(label);
				if (!msg)
					msg = label;
				else
					msg += '</div><div class="z-drop-content">' +
						`<span id="zk_ddghost-img${cnt++}" class="z-drop-icon"></span>&nbsp;${label}`;
			}
		}
		return msg;
	}

	// override it because msg cut in getDragMessage_,
	// do not want cut again here, and change _dragImg to array
	/** @internal */
	override cloneDrag_(drag: zk.Draggable, ofs: zk.Offset): HTMLElement {
		//See also bug 1783363 and 1766244
		const msg = this.getDragMessage_(),
			dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = 'pointer';
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		// has multi drag image
		drag._dragImg = jq('span[id^="zk_ddghost-img"]');
		return dgelm;
	}

	/** @internal */
	override beforeParentChanged_(newp?: zk.Widget): void {
		if (!newp) {//remove
			const mesh = this.getMeshWidget();
			if (mesh) mesh._shallSyncCM = true;
		}
		super.beforeParentChanged_(newp);
	}

	/** @internal */
	override afterParentChanged_(oldparent?: zk.Widget): void {
		if (this.parent) {//add
			const mesh = this.getMeshWidget();
			if (mesh) mesh._shallSyncCM = true;
		}
		super.afterParentChanged_(oldparent);
	}

	// event
	/** @internal */
	override doSelect_(evt: zk.Event<zk.EventMouseData>): void {
		if (this.isDisabled() || !this.isSelectable()) return;
		try {
			this._userSelection = true;
			if (!evt.itemSelected) {
				this.getMeshWidget()!._doItemSelect(this, evt);
				evt.itemSelected = true;
			}
			super.doSelect_(evt);
		} finally {
			this._userSelection = undefined;
		}
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event<zk.EventKeyData>): void {
		const mesh = this.getMeshWidget()!;

		// disable item's content selection excluding input box and textarea
		if (!jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			this._disableSelection_ = true;
			zk(mesh.$n()).disableSelection();
		}
		mesh._doKeyDown(evt);
		super.doKeyDown_(evt);
	}

	/** @internal */
	override doKeyUp_(evt: zk.Event<zk.EventKeyData>): void {
		const mesh = this.getMeshWidget()!;
		if (this._disableSelection_) {
			zk(mesh.$n()).enableSelection();
			this._disableSelection_ = false;
		}
		mesh._doKeyUp(evt);
		super.doKeyUp_(evt);
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push(`<tr ${this.domAttrs_({ domClass: true })} class="z-renderdefer"></tr>`);
	}

	/**
	 * This method should be overridden by its subwidget.
	 * @returns -1 if item is before this object,
	 * 0 if item is the same as this object,
	 * 1 if item is after this object.
	 * @since 8.5.0
	 * @internal
	 */
	compareItemPos_(item: zul.sel.ItemWidget): number {
		return 0;
	}

	/** @internal */
	override getFlexContainer_(): HTMLElement | undefined { //use old flex inside tr/td
		return undefined;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({ onResponse: this });
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onResponse: this });
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// ZK-5038
		this._shallCheckClearCache = true;
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		// ZK-5038
		this._shallCheckClearCache = true;
	}

	onResponse(): void {
		if (this._shallCheckClearCache) {
			this._shallCheckClearCache = false;
			if (this.getMeshWidget()?.isCheckmark()) {
				this.clearCache();
			}
		}
	}
}