/* Listheader.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:25:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The list header which defines the attributes and header of a column
 * of a list box.
 * Its parent must be {@link Listhead}.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 * @defaultValue {@link getZclass}: z-listheader.
 */
@zk.WrapClass('zul.sel.Listheader')
export class Listheader extends zul.mesh.SortWidget {
	override parent!: zul.sel.Listhead | undefined;
	/** @internal */
	_maxlength?: number;

	/**
	 * @returns the listbox that this belongs to.
	 */
	getListbox(): zul.sel.Listbox | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	constructor() {
		super(); // FIXME: params?
		this.listen({onGroup: this}, -1000);
	}

	/**
	 * @returns the mesh body that this belongs to.
	 */
	getMeshBody = Listheader.prototype.getListbox;

	/** @internal */
	override checkClientSort_(ascending: boolean): boolean {
		const body = this.getMeshBody();
		return !(!body || body.hasGroup())
				&& super.checkClientSort_(ascending);
	}

	/**
	 * @returns the maximal length of each item's label.
	 * @defaultValue `0` (no limit).
	 */
	getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of each item's label.
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength = !maxlength || maxlength < 0 ? 0 : maxlength;

		if (o !== maxlength || opts?.force) {
			if (this.desktop) {
				this.rerender();
				this.updateCells_();
			}
		}

		return this;
	}

	//B70-ZK-1816, also add in zk 8, ZK-2660
	override setVisible(visible: boolean): this {
		if (this.isVisible() != visible) {
			super.setVisible(visible);
			if (this.desktop)
				this.smartUpdate('visible', visible);
		}
		return this;
	}

	/**
	 * Groups and sorts the items ({@link Listitem}) based on
	 * {@link getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 *
	 * @param ascending - whether to use {@link getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param evt - the event causes the group
	 * @returns boolean whether the items are grouped.
	 * @since 6.5.0
	 */
	group(ascending: boolean, evt: zk.Event): boolean {
		var dir = this.getSortDirection();
		if (ascending) {
			if ('ascending' == dir) return false;
		} else {
			if ('descending' == dir) return false;
		}

		var sorter = ascending ? this._sortAscending : this._sortDescending;
		if (sorter == 'fromServer')
			return false;
		else if (sorter == 'none') {
			evt.stop();
			return false;
		}

		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel() || !zk.feature.pe || !zk.isLoaded('zkex.sel')) return false;
			// if in model, the sort should be done by server

		var	body = this.getMeshBody();
		if (!body) return false;
		evt.stop();

		var desktop = body.desktop,
			node = body.$n_();
		try {
			body.unbind();
			if (body.hasGroup()) {
				for (var gs = body.getGroups(), len = gs.length; --len >= 0;)
					body.removeChild(gs[len]);
			}

			interface Data {
				wgt: zk.Widget;
				index: number;
			}
			var d: Data[] = [],
				col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w: zk.Widget | undefined; (w = it.next()); z++)
				for (var k = 0, cell: zk.Widget | undefined = w.firstChild; cell; cell = cell.nextSibling, k++)
					if (k == col) {
						d[i++] = {
							wgt: cell,
							index: z
						};
					}

			var dsc = dir == 'ascending' ? -1 : 1,
				fn = this.sorting,
				isNumber = sorter == 'client(number)';
			d.sort(function (a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});

			// clear all items
			for (var item = body.firstItem; item; item = body.nextItem(item))
				body.removeChild(item);

			for (var previous: Data | undefined, row: Data, index = this.getChildIndex(), i = 0, k = d.length; i < k; i++) {
				row = d[i];
				if (!previous || fn(previous.wgt, row.wgt, isNumber) != 0) {
					//new group
					let group!: zkex.sel.Listgroup,
						cell = row.wgt.parent!.getChildAt<zul.sel.Listcell>(index);
					if (cell) {
						if (cell.getLabel()) {
							group = new zkex.sel.Listgroup({
								label: cell.getLabel()
							});
						} else {
							var cc = cell.firstChild;
							if (cc && cc instanceof zul.wgt.Label) {
								group = new zkex.sel.Listgroup({
									label: cc.getValue()
								});
							} else {
								group = new zkex.sel.Listgroup({
									label: msgzul.GRID_OTHER
								});
							}
						}
					}
					body.appendChild(group);
				}
				body.appendChild(row.wgt.parent!);
				previous = row;
			}
			this._fixDirection(ascending);
		} finally {
			body.replaceHTML(node, desktop);
		}
		return true;
	}

	/**
	 * It invokes {@link group} to group list items and maintain
	 * {@link getSortDirection}.
	 * @since 6.5.0
	 */
	onGroup(evt: zk.Event): void {
		var dir = this.getSortDirection();
		if ('ascending' == dir)
			this.group(false, evt);
		else if ('descending' == dir)
			this.group(true, evt);
		else if (!this.group(true, evt))
			this.group(false, evt);
	}

	/**
	 * Updates the cells according to the listheader
	 * @internal
	 */
	updateCells_(): void {
		var box = this.getListbox();
		if (box == null || box.getMold() == 'select')
			return;

		var jcol = this.getChildIndex(),
			w: zul.sel.ItemWidget | zul.sel.Listfoot | undefined;
		for (var it = box.getBodyWidgetIterator(); (w = it.next());)
			if (jcol < w.nChildren)
				w.getChildAt(jcol)!.rerender();

		w = box.listfoot;
		if (w && jcol < w.nChildren)
			w.getChildAt(jcol)!.rerender();
	}
	
	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var cm = this.$n('cm'),
			n = this.$n();
		if (cm) {
			var box = this.getListbox();
			if (box) box._headercm = cm;
			this.domListen_(cm, 'onClick', '_doClick');
		}
		if (n)
			this.domListen_(n, 'onMouseOver', '_doMouseOver')
				.domListen_(n, 'onMouseOut', '_doMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domListen_(btn, 'onClick', '_doMenuClick');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var cm = this.$n('cm'),
			n = this.$n();
		if (cm) {
			var box = this.getListbox();
			if (box) box._headercm = undefined;
			this._checked = undefined;
			this.domUnlisten_(cm, 'onClick', '_doClick');
		}
		if (n)
			this.domUnlisten_(n, 'onMouseOver', '_doMouseOver')
				.domUnlisten_(n, 'onMouseOut', '_doMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domUnlisten_(btn, 'onClick', '_doMenuClick');
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_doMouseOver(evt: zk.Event): void {
		if (this.isSortable_() || (this.parent!._menupopup && this.parent!._menupopup != 'none'))
			jq(this.$n_()).addClass(this.$s('hover'));
	}

	/** @internal */
	_doMouseOut(evt: zk.Event): void {
		if (this.isSortable_() || (this.parent!._menupopup && this.parent!._menupopup != 'none')) {
			var $n = jq(this.$n_());
			if (!$n.hasClass(this.$s('visited')))
				$n.removeClass(this.$s('hover'));
		}
	}

	/** @internal */
	_doClick(evt: zk.Event<zk.EventMetaData>): void {
		this._checked = !this._checked;
		var box = this.getListbox()!,
			cm = this.$n_('cm'),
			$n = jq(cm);
		if (this._checked) {
			$n.addClass(this.$s('checked'));
			box.selectAll(true, evt);
		} else {
			$n.removeClass(this.$s('checked'));
			box._select(undefined, evt);
		}
		box.fire('onCheckSelectAll', this._checked, {toServer: true});
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var box = this.getListbox(),
			cm = this.$n('cm');
		if (box && box._checkmark) {
			var n = evt.domTarget;
			if (n == cm || n.parentNode == cm) //may click on font-awesome element
				return; //ignore it (to avoid sort or other activity)
		}
		super.doClick_(evt, popupOnly);
	}

	/** @internal */
	override domContent_(): string {
		var s = super.domContent_(),
			box = this.getListbox()!;
		if (this._hasCheckbox())
			s = '<span id="' + this.uuid + '-cm" class="' + this.$s('checkable')
				+ (box.$$selectAll ? ' ' + this.$s('checked') : '') + '"><i class="' + this.$s('icon') + ' z-icon-check"></i></span>'
				+ (s ? '&nbsp;' + s : '');
		return s;
	}

	/** @internal */
	_hasCheckbox(): boolean {
		var box = this.getListbox();
		return !!(box != null && this.parent!.firstChild == this
			&& box._checkmark && box._multiple && !box._listbox$noSelectAll);  // B50-ZK-873
	}

	/** @internal */
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this._maxlength});
	}

	/** @internal */
	override getContentWidth_(): number {
		var $cv = zk(this.$n('cave')),
			isTextOnly = !this.nChildren && !this._iconSclass && !this._hasCheckbox(),
			contentWidth = isTextOnly ? $cv.textWidth() : $cv.textSize()[0];
		return Math.ceil(contentWidth + $cv.padBorderWidth() + zk(this.$n()).padBorderWidth());
	}
}