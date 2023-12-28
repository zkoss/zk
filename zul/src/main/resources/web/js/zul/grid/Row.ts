/* Row.ts

	Purpose:

	Description:

	History:
		Tue Dec 23 15:26:27     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _isPE = (function () {
	var _isPE_ = zk.feature.pe;
	return function () {
		return _isPE_ && zk.isLoaded('zkex.grid');
	};
})();
/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 *
 * @defaultValue {@link getZclass}: z-row.
 *
 */
@zk.WrapClass('zul.grid.Row')
export class Row extends zul.Widget<HTMLTableRowElement> implements zul.mesh.Item {
	override parent?: zul.grid.Rows;
	override nextSibling?: zul.grid.Row;
	override previousSibling?: zul.grid.Row;

	/** @internal */
	_loaded?: boolean;
	/** @internal */
	_index?: number;

	/** @internal */
	_align?: string;
	/** @internal */
	_nowrap?: boolean;
	/** @internal */
	_valign?: string;
	/** @internal */
	_spans?: number[];
	detail?: zkex.grid.Detail;
	/** @internal */
	_musin?: boolean;

	/**
	 * @returns the horizontal alignment of the whole row.
	 * @defaultValue `null` (system default: left unless CSS specified).
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/**
	 * Sets the horizontal alignment of the whole row.
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.$n();
			if (n)
				n.style.textAlign = align;
		}

		return this;
	}

	/**
	 * @returns the nowrap.
	 * @defaultValue `null` (system default: wrap).
	 */
	isNowrap(): boolean {
		return !!this._nowrap;
	}

	/**
	 * Sets the nowrap.
	 */
	setNowrap(nowrap: boolean, opts?: Record<string, boolean>): this {
		const o = this._nowrap;
		this._nowrap = nowrap;

		if (o !== nowrap || opts?.force) {
			var cells = this.$n()?.cells;
			if (cells)
				for (var j = cells.length; j--;)
					cells[j].noWrap = nowrap;
		}

		return this;
	}

	/**
	 * @returns the vertical alignment of the whole row.
	 * @defaultValue `null` (system default: top).
	 */
	getValign(): string | undefined {
		return this._valign;
	}

	/**
	 * Sets the vertical alignment of the whole row.
	 */
	setValign(valign: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = valign;

		if (o !== valign || opts?.force) {
			var n = this.$n();
			if (n)
				n.style.verticalAlign = valign;
		}

		return this;
	}

	/**
	 * @returns the grid that contains this row.
	 */
	getGrid(): zul.grid.Grid | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	override setVisible(visible: boolean): this {
		if (this.isVisible() != visible) {
			super.setVisible(visible);
			if (this.desktop && this.isStripeable_())
				this.parent!._syncStripe();
		}
		return this;
	}

	/**
	 * @returns the spans, which is a list of numbers separated by comma.
	 * @defaultValue empty.
	 */
	getSpans(): string {
		return zUtl.intsToString(this._spans);
	}

	/**
	 * Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 */
	setSpans(spans: string): this {
		if (this.getSpans() != spans) {
			this._spans = zUtl.stringToInts(spans, 1);
			this.rerender();
		}
		return this;
	}

	/** @internal */
	_getIndex(): number {
		return this.parent ? this.getChildIndex() : -1;
	}

	/**
	 * @returns the group that this row belongs to, or null.
	 */
	getGroup(): zkex.grid.Group | undefined {
		// TODO: this performance is not good.
		if (_isPE() && this.parent && this.parent.hasGroup())
			// eslint-disable-next-line @typescript-eslint/no-this-alias
			for (var w: zul.grid.Row | undefined = this; w; w = w.previousSibling)
				if (w instanceof zkex.grid.Group) return w;

		return undefined;
	}

	override setStyle(style: string): this {
		if (this._style != style) {
			if (!zk._rowTime) zk._rowTime = jq.now();
			this._style = style;
			this.rerender();
		}
		return this;
	}

	override rerender(skipper?: number | zk.Skipper): this {
		if (this.desktop) {
			super.rerender(skipper);
			if (this.parent)
				this.parent._syncStripe();
		}
		return this;
	}

	override getSclass(): string | undefined {
		var /*safe*/ sclass = super.getSclass();
		if (sclass != null)
			return sclass;

		var grid = this.getGrid();
		return grid ? grid.getSclass() : sclass;
	}

	/** @internal */
	_getChdextr(child: zk.Widget): HTMLElement {
		return child.$n('chdextr') ?? child.$n_();
	}

	override scrollIntoView(): this {
		var bar = this.getGrid()!._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n_());
		} else {
			super.scrollIntoView();
		}
		return this;
	}

	/** @internal */
	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		var childHTML = this.encloseChildHTML_({
			child: child,
			index: child.getChildIndex(),
			zclass: /*safe*/ this.getZclass()
		})!;
		if (before)
			jq(this._getChdextr(before)).before(/*safe*/ childHTML);
		else
			jq(this).append(/*safe*/ childHTML);
		child.bind(desktop);
	}

	/** @internal */
	override removeChildHTML_(child: zk.Widget, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		jq(child.uuid + '-chdextr', zk).remove();
	}

	/**
	 * Enclose child with HTML tag with TD and DIV,
	 * and return a HTML code or add HTML fragments in out array.
	 * @internal
	 */
	encloseChildHTML_(opts: { out?: string[]; child: zk.Widget; index: number; zclass: string; visible?: boolean }): string | undefined {
		var out = opts.out ?? new zk.Buffer(),
			child = opts.child;

		if (child instanceof zul.wgt.Cell)
			child._headerVisible = opts.visible;
		else {
			out.push('<td id="', /*safe*/ child.uuid, '-chdextr"',
				/*safe*/ this._childAttrs(child, opts.index), ' tabindex="-1" role="gridcell"><div id="', /*safe*/ child.uuid,
				'-cell" class="', zUtl.encodeXML(opts.zclass), '-content">');
		}
		child.redraw(out);
		if (!(child instanceof zul.wgt.Cell))
			out.push('</div></td>');
		if (!opts.out)
			return out.join('');
	}

	/** @internal */
	_childAttrs(child: zk.Widget, index: number): string {
		var realIndex = index, span = 1;
		if (this._spans) {
			for (var j = 0, k = this._spans.length; j < k; ++j) {
				if (j == index) {
					span = this._spans[j];
					break;
				}
				realIndex += this._spans[j] - 1;
			}
		}
		var visible: boolean | undefined, // eslint-disable-line zk/preferStrictBooleanType
			hgh: string | undefined,
			align: string | undefined,
			valign: string | undefined,
			grid = this.getGrid();
		if (grid) {
			var cols = grid.columns;
			if (cols) {
				if (realIndex < cols.nChildren) {
					var col = cols.getChildAt<zul.grid.Column>(realIndex)!;
					visible = col.isVisible();
					hgh = col.getHeight();
					align = col.getAlign();
					valign = col.getValign();
				}
			}
		}
		var /*safe*/ style = this.domStyle_({ visible: true, width: true, height: true }),
			isDetail = zk.isLoaded('zkex.grid') && child instanceof zkex.grid.Detail;
		if (isDetail) {
			var wd = child.getWidth();
			if (wd)
				style += 'width:' + wd + ';';
		}
		if (hgh || align || valign) {
			if (hgh)
				style += 'height:' + hgh + ';';
			if (align)
				style += 'text-align:' + align + ';';
			if (valign)
				style += 'vertical-align:' + valign + ';';
		}
		var clx = isDetail ? child.$s('outer') : this.$s('inner'),
			attrs = '';
		if (span !== 1)
			attrs += ` colspan="${span}"`;
		if (this._nowrap)
			attrs += ' nowrap="nowrap"';
		if (style)
			attrs += ' style="' + style + '"';
		if (visible === false) {
			if (!(zk.isLoaded('zkex.grid') && this instanceof zkex.grid.Group))
				attrs += ' aria-hidden="true"';
			clx += ' ' + this.$s('hidden-column');
		}
		return DOMPurify.sanitize(attrs + ' class="' + clx + '"');
	}

	/**
	 * @returns whether is stripeable or not.
	 * @defaultValue `true`.
	 * @internal
	 */
	isStripeable_(): boolean {
		return true;
	}

	//-- super --//
	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		if ((_isPE() && (this instanceof zkex.grid.Group || this instanceof zkex.grid.Groupfoot))
			|| no?.visible)
			return super.domStyle_(no);

		var /*safe*/ style = super.domStyle_(no),
			group = this.getGroup();
		if (this._align)
			style += ' text-align:' + this._align + ';';
		if (this._valign)
			style += ' vertical-align:' + this._valign + ';';
		return group && !group.isOpen() ? style + 'display:none;' : style;
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (zk.isLoaded('zkex.grid') && child instanceof zkex.grid.Detail)
			this.detail = child;
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.detail)
			this.detail = undefined;
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);
		//sync frozen
		var grid = this.getGrid(),
			frozen = grid?.frozen,
			tbody = grid?.rows?.$n();
		if (frozen && tbody) {
			const tds = jq(evt.domTarget).parents('td');
			for (var i = 0, j = tds.length; i < j; i++) {
				const td = tds[i];
				if (td.parentNode!.parentNode == tbody) {
					grid!._moveToHidingFocusCell(td.cellIndex);
					break;
				}
			}
		}
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		if (this._musin)
			return;
		this._musin = true;
		var n = this.$n();

		// ZK-2250: all children should apply -moz-user-select: none
		if (n && zk.gecko && this._draggable
			&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).addClass('z-draggable-over');
		}

		super.doMouseOver_(evt);
	}

	/** @internal */
	override doMouseOut_(evt: zk.Event): void {
		var n = this.$n();
		if (this._musin && jq.isAncestor(n,
			(evt.domEvent as JQuery.MouseOutEvent).relatedTarget as HTMLElement || evt.domEvent!.toElement)) {
			// fixed mouse-over issue for datebox
			this.parent!._musout = this;
			return;
		}
		this._musin = false;

		// ZK-2250: all children should unapply -moz-user-select: none
		if (n && zk.gecko && this._draggable
			&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).removeClass('z-draggable-over');
		}

		super.doMouseOut_(evt);
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ cls = super.domClass_(no),
			grid = this.getGrid(),
			sclass: string;
		// NOTE: It has always been the case that the following `this.$n()` possibly
		// returns null or undefined.
		if (grid && jq(this.$n()).hasClass(sclass = grid.getOddRowSclass()))
			return cls + ' ' + sclass;
		return cls;
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({ domClass: true }), ' class="z-renderdefer"></tr>');
	}

	/** @internal */
	override getFlexContainer_(): HTMLElement | undefined { //use old flex inside tr/td
		return undefined;
	}
}