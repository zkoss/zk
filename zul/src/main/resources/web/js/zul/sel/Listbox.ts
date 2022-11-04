/* Listbox.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:16:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _isListgroup(wgt: zk.Widget): wgt is zkex.sel.Listgroup {
	return zk.isLoaded('zkex.sel') && wgt instanceof zkex.sel.Listgroup;
}
function _syncFrozen(wgt: zul.sel.Listbox): void {
	const frozen = wgt.frozen;
	if (wgt._nativebar && frozen)
		frozen._syncFrozen();
}
function _fixForEmpty(wgt: zul.sel.Listbox): void {
	if (wgt.desktop) {
		var empty = wgt.$n_<HTMLTableCellElement>('empty'),
			colspan = 0;
		if (wgt._nrows) {
			empty.style.display = 'none';
		} else {
			if (wgt.listhead) {
				for (var w = wgt.listhead.firstChild; w; w = w.nextSibling)
					colspan++;
			}
			empty.colSpan = colspan || 1;
			// ZK-2365 table cell needs the "display:table-cell" when colspan is enable.
			empty.style.display = 'table-cell';
		}
	}
	wgt._shallFixEmpty = false;
}

@zk.WrapClass('zul.sel.Listbox')
export class Listbox extends zul.sel.SelectWidget {
	// override firstChild!: zul.sel.Listitem | null;
	// override lastChild!: zul.sel.Listitem | null;
	/** @internal */
	_nrows = 0;
	/**
	 * Whether to allow Listgroup to be selected
	 * @defaultValue `false`
	 * @since 5.0.7
	 */
	override groupSelect = false;
	/** @internal */
	override _scrollbar?: zul.Scrollbar = undefined;
	firstItem?: zul.sel.Listitem;
	lastItem?: zul.sel.Listitem;
	/** @internal */
	_emptyMessage?: string = undefined;
	/** @internal */
	_groupsInfo: zkex.sel.Listgroup[];
	/** @internal */
	_shallStripe?: boolean;
	/** @internal */
	_shallFixEmpty?: boolean;
	/** @internal */
	_scOddRow?: string;
	/** @internal */
	_shallScrollIntoView?: boolean;
	/** @internal */
	_listbox$shallUpdateScrollPos?: boolean;
	/** @internal */
	_listbox$shallSyncSelInView?: boolean;
	/** @internal */
	_listbox$noSelectAll?: boolean;
	/** @internal */
	_tmpScrollTop?: number;
	listhead?: zul.sel.Listhead;
	listfoot?: zul.sel.Listfoot;
	/** @internal */
	_offset: number | undefined;
	/** @internal */
	_listbox$rod?: boolean; // zkex/sel/Listgroup

	/**
	 * @returns the message to display when there are no items
	 * @since 5.0.7
	 */
	getEmptyMessage(): string | undefined {
		return this._emptyMessage;
	}

	/**
	 * Sets the message to display when there are no items
	 * @since 5.0.7
	 */
	setEmptyMessage(emptyMessage: string, opts?: Record<string, boolean>): this {
		const o = this._emptyMessage;
		this._emptyMessage = emptyMessage;

		if (o !== emptyMessage || opts?.force) {
			if (this.desktop) {
				var emptyContentDiv = jq(this.$n_('empty-content')),
					emptyContentClz = this.$s('emptybody-content');
				if (emptyMessage && emptyMessage.trim().length != 0)
					emptyContentDiv.addClass(emptyContentClz);
				else
					emptyContentDiv.removeClass(emptyContentClz);
				emptyContentDiv.html(emptyMessage);
			}
		}

		return this;
	}

	constructor() {
		super(); // FIXME: params?
		this._groupsInfo = [];
	}

	/**
	 * @returns the number of listgroup
	 */
	getGroupCount(): number {
		return this._groupsInfo.length;
	}

	/**
	 * @returns a list of all {@link Listgroup}.
	 */
	getGroups(): zkex.sel.Listgroup[] {
		return this._groupsInfo.$clone();
	}

	/**
	 * @returns whether listgroup exists.
	 */
	hasGroup(): boolean {
		return !!this._groupsInfo.length;
	}

	/**
	 * @returns the next item.
	 */
	nextItem(p: zk.Widget | undefined): zul.sel.Listitem | undefined {
		if (p)
			while ((p = p.nextSibling) && !(p instanceof zul.sel.Listitem));
		return p;
	}

	/**
	 * @returns the previous item.
	 */
	previousItem(p: zk.Widget | undefined): zul.sel.Listitem | undefined {
		if (p)
			while ((p = p.previousSibling) && !(p instanceof zul.sel.Listitem));
		return p;
	}

	/**
	 * @returns the style class for the odd rows.
	 * @defaultValue {@link getZclass}-odd.
	 */
	getOddRowSclass(): string {
		return this._scOddRow == null ? this.$s('odd') : this._scOddRow;
	}

	/**
	 * Sets the style class for the odd rows. If the style class doesn't exist,
	 * the striping effect disappears. You can provide different effects by
	 * providing the proper style classes.
	 * @returns Listbox
	 */
	setOddRowSclass(oddRowSclass: string): this {
		const scls = oddRowSclass || undefined;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.$n();
			// FIXME: Prior to TS migration, it has been `this.rows`, but that
			// seems wrong, as no `rows` is only defined in Grid, and MeshWidget
			// from which this class inherits, defines `_rows`.
			if (n && this._rows)
				this.stripe();
		}
		return this;
	}

	/**
	 * @returns whether the HTML's select tag is used.
	 */
	inSelectMold(): boolean {
		return 'select' == this.getMold();
	}

	// bug ZK-56 for non-ROD to scroll after onSize ready
	override onSize(): void {
		super.onSize();
		var canInitScrollbar = this.desktop && !this.inSelectMold() && !this._nativebar;
		// refix ZK-2840: only init scrollbar when height or vflex is set in mobile
		if (!this._scrollbar && canInitScrollbar) {
			if (!zk.mobile || (zk.mobile && (this.getHeight() || this.getVflex()))) {
				this._scrollbar = zul.mesh.Scrollbar.init(this); // 1823278: should show scrollbar here
			}
		}
		setTimeout(() => {
			if (this.desktop) {
				if (canInitScrollbar) {
					this.refreshBar_();
				}
				// we have to do this for B50-ZK-56.zul, no matter native scroll or not
				this._syncSelInView();
			}
		}, 300);
	}

	/** @internal */
	destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = undefined;
		}
	}

	/** @internal */
	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after); //it might invoke replaceHTML and then call bind_ again
		this._shallStripe = true;
		after.push(() => {
			this.stripe();
			_syncFrozen(this);
			_fixForEmpty(this);
		});
		this._shallScrollIntoView = true;
		if (this._listbox$shallUpdateScrollPos) {
			this._fireOnScrollPos();
		}
		zWatch.listen({ onCommandReady: this }); //ZK-3152
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onCommandReady: this }); //ZK-3152
		this.destroyBar_();
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_syncSelInView(): void {
		if (this._shallScrollIntoView) {
			// ZK-2971: should scroll when not in paging or in paging but operating with keyboard
			// ZK-3103: if in paging mode, should also scroll when setting selected item/index
			const $class = this.$class as typeof Listbox;
			if (!this.paging || $class.shallSyncSelInView?.[this.uuid] || this._listbox$shallSyncSelInView) {
				var selItems = this._selItems,
					selItemIndex = -1;
				// FIXME: If the purpose of the following loop is to find the selItem with the smallest _index, then the logic is wrong.
				for (var i = 0; i < selItems.length; i++) { // ZK-4323: find item that has the smallest index
					if (selItems[i]._index! < selItemIndex || selItemIndex < 0)
						selItemIndex = i;
				}
				var selItem = selItems[selItemIndex],
					isSetSelectedItemIndexCalled = this._listbox$shallSyncSelInView;
				if (selItem) {
					var bar = this._scrollbar,
						selItemTop = selItem.$n_().offsetTop;
					if (bar) {
						if (isSetSelectedItemIndexCalled)
							bar.scrollToElement(selItem.$n_());
						else
							bar.scrollTo(zul.mesh.Scrollbar.getScrollPosH(this), selItemTop);
					} else {
						if (isSetSelectedItemIndexCalled)
							zk(selItem).scrollIntoView(this.ebody);
						else
							this.ebody!.scrollTop = selItemTop;
						this._tmpScrollTop = this.ebody!.scrollTop;
					}
				}
				if ($class.shallSyncSelInView) $class.shallSyncSelInView[this.uuid] = false;
				if (isSetSelectedItemIndexCalled) this._listbox$shallSyncSelInView = false;
			}
			// do only once
			this._shallScrollIntoView = false;
		}
	}

	/** @internal */
	override _doScroll(): void {
		// B50-ZK-56
		// ebody.scrollTop will be reset after between fireOnRender and _doScroll after bind_
		if (this._tmpScrollTop) {
			this.ebody!.scrollTop = this._tmpScrollTop;
			this._tmpScrollTop = undefined;
		}
		super._doScroll();
	}

	onCommandReady(): void {
		//ZK-3152: stripe here will be after all commands and before onResponse to avoid flickering
		if (this._shallStripe)
			this.stripe();
	}

	override onResponse(ctl: zk.ZWatchController, opts: { rtags: { selectAll?} }): void {
		if (this.desktop) {
			//ZK-3152: no need to stripe here, already done in onCommandReady
			if (this._shallFixEmpty)
				_fixForEmpty(this);
		}
		super.onResponse(ctl, opts);
	}

	/** @internal */
	_syncStripe(): void {
		this._shallStripe = true;
	}

	/**
	 * Stripes the class for each item.
	 * @returns Listbox
	 */
	stripe(): this | undefined {
		var scOdd = this.getOddRowSclass();
		if (!scOdd) return;
		var odd = this._offset! & 1,
			even = !odd,
			it = this.getBodyWidgetIterator();
		for (var w: zul.sel.ItemWidget | undefined; (w = it.next());) {
			if (w.isVisible() && w.isStripeable_()) {
				jq(w)[even ? 'removeClass' : 'addClass'](scOdd);
				even = !even;
			}
		}
		this._shallStripe = false;
		return this;
	}

	override rerender(skipper?: zk.Skipper | number): this {
		super.rerender(skipper);
		this._syncStripe();
		return this;
	}

	override getCaveNode(): HTMLElement | undefined {
		return this.$n('rows') ?? this.$n('cave');
	}

	/** @internal */
	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		const nodeOfBefore = before && (!(child instanceof zul.sel.Listitem) || before instanceof zul.sel.Listitem) ? before.getFirstNode_() : undefined;
		if (nodeOfBefore)
			jq(nodeOfBefore).before(child.redrawHTML_());
		else
			jq(this.getCaveNode()).append(child.redrawHTML_());
		child.bind(desktop);
	}

	override insertBefore(child: zk.Widget, sibling: zk.Widget | undefined, ignoreDom?: boolean): boolean {
		if (super.insertBefore(child, sibling,
			ignoreDom || (!this.z_rod && !(child instanceof zul.sel.Listitem)))) {
			this._fixOnAdd(child, ignoreDom);
			return true;
		}
		return false;
	}

	override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (super.appendChild(child,
			ignoreDom || (!this.z_rod && !(child instanceof zul.sel.Listitem)))) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom);
			return true;
		}
		return false;
	}

	/** @internal */
	_fixOnAdd(child: zk.Widget, ignoreDom?: boolean, stripe?: boolean, ignoreAll?: boolean): this | undefined {
		var noRerender;
		if (child instanceof zul.sel.Listitem) {
			if (_isListgroup(child))
				this._groupsInfo.push(child);
			if (!this.firstItem || !this.previousItem(child))
				this.firstItem = child;
			if (!this.lastItem || !this.nextItem(child))
				this.lastItem = child;
			++this._nrows;

			if (child.isSelected() && !this._selItems.$contains(child))
				this._selItems.push(child);
			noRerender = stripe = true;
		} else if (child instanceof zul.sel.Listhead) {
			this.listhead = child;
		} else if (child instanceof zul.mesh.Paging) {
			this.paging = child;
			this.paging.setMeshWidget(this);
		} else if (child instanceof zul.sel.Listfoot) {
			this.listfoot = child;
		} else if (child instanceof zul.mesh.Frozen) {
			this.frozen = child;
		}

		this._syncEmpty();

		if (!ignoreAll) {
			if (!ignoreDom && !noRerender)
				return this.rerender();
			if (stripe)
				this._syncStripe();
			if (!ignoreDom)
				this._syncSize();
			if (this.desktop)
				_syncFrozen(this);
		}
	}

	override removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (super.removeChild(child, ignoreDom)) {
			this._fixOnRemove(child, ignoreDom);
			return true;
		}
		return false;
	}

	/** @internal */
	_fixOnRemove(child: zk.Widget, ignoreDom?: boolean): void {
		var stripe;
		if (child == this.listhead)
			this.listhead = undefined;
		else if (child == this.paging) {
			this.paging.setMeshWidget(undefined);
			this.paging = undefined;
		} else if (child == this.frozen) {
			this.frozen = undefined;
			this.destroyBar_();
		} else if (child == this.listfoot)
			this.listfoot = undefined;
		else if (!(child instanceof zul.mesh.Auxhead)) {
			if (child == this.firstItem) {
				for (var p = this.firstChild, Listitem = zul.sel.Listitem;
					p && !(p instanceof Listitem); p = p.nextSibling)
					;
				this.firstItem = p;
			}
			if (child == this.lastItem) {
				for (var p = this.lastChild, Listitem = zul.sel.Listitem;
					p && !(p instanceof Listitem); p = p.previousSibling)
					;
				this.lastItem = p;
			}
			if (_isListgroup(child))
				this._groupsInfo.$remove(child);
			--this._nrows;

			if ((child as zul.sel.ItemWidget).isSelected())
				this._selItems.$remove(child as zul.sel.ItemWidget);
			stripe = true;
		}
		this._syncEmpty();
		if (!ignoreDom) { //unlike _fixOnAdd, it ignores strip too (historical reason; might be able to be better)
			if (stripe) this._syncStripe();
			this._syncSize();
		}
	}

	/**
	 * A redraw method for the empty message , if you want to customize the message ,
	 * you could overwrite this.
	 * @param out - A array that contains html structure, it usually come from `mold(redraw_)`.
	 * @internal
	 */
	redrawEmpty_(out: string[]): void {
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="',
			this.uuid, '-empty" style="display:none">',
			'<div id="', this.uuid, '-empty-content"');
		if (this._emptyMessage && this._emptyMessage.trim().length != 0)
			out.push('class="', this.$s('emptybody-content'), '"');
		out.push('>', this._emptyMessage!, '</div></td></tr></tbody>');
	}

	/** @internal */
	override replaceChildHTML_(child: zk.Widget, n: HTMLElement | string, desktop?: zk.Desktop, skipper?: zk.Skipper, _trim_?: boolean): void {
		if (child._renderdefer) {
			var scOdd = this.getOddRowSclass(),
				isOdd = jq(n).hasClass(scOdd); // supers will change this result, we need to cache it

			super.replaceChildHTML_(child, n, desktop, skipper, _trim_);
			if (isOdd) jq(child).addClass(scOdd);
		} else
			super.replaceChildHTML_(child, n, desktop, skipper, _trim_);
	}

	/** @internal */
	override _syncEmpty(): void {
		this._shallFixEmpty = true;
	}

	/** @internal */
	override onChildReplaced_(oldc: zk.Widget | undefined, newc: zk.Widget | undefined): void {
		super.onChildReplaced_(oldc, newc);

		if (oldc) this._fixOnRemove(oldc, true);
		if (newc) this._fixOnAdd(newc, true, false, true); //ignoreAll: no sync stripe...

		if ((oldc && oldc instanceof zul.sel.Listitem)
			|| (newc && newc instanceof zul.sel.Listitem))
			this._syncStripe();
		this._syncSize();
		if (this.desktop)
			_syncFrozen(this);
	}

	/**
	 * @returns the head widget class
	 */
	getHeadWidgetClass(): typeof zul.sel.Listhead {
		return zul.sel.Listhead;
	}

	/**
	 * @returns the list item iterator.
	 */
	itemIterator(opts?: Record<string, unknown>): zul.sel.ItemIter {
		return new zul.sel.ItemIter(this, opts);
	}

	/**
	 * @returns the list item iterator.
	 * @see {@link itemIterator}
	 */
	getBodyWidgetIterator = Listbox.prototype.itemIterator;

	/** @internal */
	override _updHeaderCM(): void {
		// B50-3322970: need to clear Listheader _check cache
		var lh = this.listhead?.firstChild;
		if (this._headercm && this._multiple && lh)
			lh._checked = this._isAllSelected();
		super._updHeaderCM();
	} // @Override F70-ZK-2433

	/** @internal */
	override checkOnHighlightDisabled_(): boolean {
		if (this._selectOnHighlightDisabled) {
			window.getSelection()!.toString().length > 0;
		}
		return false;
	}

	/**
	 * Scroll to the specified item by the given index.
	 * @param index - the index of listitem
	 * @param scrollRatio - the scroll ratio
	 * @since 8.5.2
	 */
	scrollToIndex(index: number, scrollRatio: number): void {
		void this.waitForRendered_().then(() => {
			this._scrollToIndex(index, scrollRatio);
		});
	}

	/** @internal */
	_getFirstItemIndex(): number {
		return this.firstItem!._index!;
	}

	/** @internal */
	_getLastItemIndex(): number {
		return this.lastItem!._index!;
	}

	/** @internal */
	setItemsInvalid_(itemsInvalid: ArrayLike<unknown>[]): this {
		zAu.createWidgets(itemsInvalid, (ws) => {
			if (this.$n('rows')) {
				this.replaceCavedChildren_('rows', ws);
			} else {
				//remove all listitems
				var fc: zul.sel.Listitem | undefined;
				for (var item = this.firstItem; item;) {
					// B60-ZK-1230: Only removes the first list item
					var n = this.nextItem(item);
					if (!n)
						fc = item.nextSibling;
					this.removeChild(item, true);
					item = n;
				}

				//add new items
				for (var j = 0, len = ws.length; j < len; ++j)
					this.insertBefore(ws[j], fc, true); //no dom
			}
		}, (wx) => {
			for (var w: zk.Widget | undefined = wx, p = wx; w; p = w, w = w.parent)
				if (w == this && p instanceof zul.sel.Listitem)
					return; //ignore it since it is going to be removed
			return wx;
		});
		return this;
	}

	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (child instanceof zul.sel.Listitem) {
			if (zk.isLoaded('zkex.sel') && child instanceof zkex.sel.Listgroupfoot) {
				if (!this.hasGroup()) {
					zk.error('Listgroupfoot cannot exist alone, you have to add a Listgroup first');
					return false;
				}
				if (!insertBefore && zk.isLoaded('zkex.sel') && this.lastChild instanceof zkex.sel.Listgroupfoot) {
					zk.error('Only one Listgroupfoot is allowed per Listgroup');
					return false;
				}
			}
		} else if (child instanceof zul.sel.Listhead) {
			if (this.listhead && this.listhead != child) {
				zk.error('Only one listhead is allowed: ' + this.className);
				return false;
			}
		} else if (child instanceof zul.mesh.Frozen) {
			if (this.frozen && this.frozen != child) {
				zk.error('Only one frozen child is allowed: ' + this.className);
				return false;
			}
			if (this.inSelectMold()) {
				zk.error('Mold select ignores frozen');
			}
		} else if (child instanceof zul.sel.Listfoot) {
			if (this.listfoot && this.listfoot != child) {
				zk.error('Only one listfoot is allowed: ' + this.className);
				return false;
			}
			if (this.inSelectMold()) {
				zk.error('Mold select ignores listfoot');
			}
		} else if (child instanceof zul.mesh.Paging) {
			if (this.paging && this.paging != child) {
				zk.error('Only one paging is allowed: ' + this.className);
				return false;
			}
			if (this.getPaginal()) {
				zk.error('External paging cannot coexist with child paging, ' + this.className);
				return false;
			}
			if (this.getMold() != 'paging') {
				zk.error('The child paging is allowed only in the paging mold, ' + this.className);
				return false;
			}
		} else if (!(child instanceof zul.mesh.Auxhead)) {
			zk.error('Unsupported child for Listbox: ' + child.className);
			return false;
		}
		return true;
	}
}

/**
 * The listitem iterator.
 */
@zk.WrapClass('zul.sel.ItemIter')
export class ItemIter extends zk.Object implements zul.mesh.ItemIterator {
	box: zul.sel.Listbox;
	opts?: Record<string, unknown>;
	/** @internal */
	_isInit?: boolean;
	p?: zul.sel.Listitem;

	/**
	 * @param listbox - the widget that the iterator belongs to
	 */
	constructor(box: zul.sel.Listbox, opts?: Record<string, unknown>) {
		super();
		this.box = box;
		this.opts = opts;
	}

	/** @internal */
	_init(): void {
		if (!this._isInit) {
			this._isInit = true;
			var p = this.box.firstItem;
			if (this.opts?.skipHidden)
				for (; p && !p.isVisible(); p = this.box.nextItem(p)) { /* empty */ }
			this.p = p;
		}
	}

	/**
	* @returns `true` if the iteration has more elements
	*/
	hasNext(): boolean {
		this._init();
		return !!this.p;
	}

	/**
	 * @returns the next element in the iteration.
	 */
	next(): zul.sel.ItemWidget | undefined {
		this._init();
		var p = this.p,
			q = p ? p.parent!.nextItem(p) : undefined;
		if (this.opts?.skipHidden)
			for (; q && !q.isVisible(); q = q.parent!.nextItem(q)) { /* empty */ }
		if (p)
			this.p = q;
		return p;
	}
}