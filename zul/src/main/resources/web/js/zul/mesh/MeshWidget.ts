/* MeshWidget.ts

	Purpose:

	Description:

	History:
		Sat May  2 09:36:31     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The two-dimensional mesh related widgets.
 * A mesh widget is a two-dimensional widgets, such as grid, listbox and tree.
 * Classes in this package is the skeletal implementation that can be used
 * to simplify the implementation of grid, listbox and tree.
 */
//zk.$package('zul.mesh');
export interface MeshWidth {
	width: number;
	wds: number[];
}

var _shallFocusBack: boolean;
function _calcMinWd(wgt: MeshWidget): MeshWidth {
	var wgtn = wgt.$n(),
		ws = wgtn ? wgtn.style.whiteSpace : ''; //bug#3106514: sizedByContent with not visible columns
	if (wgtn) {
		if (zk.ie9)
			wgt._wsbak = ws; // B50-ZK-432
		wgtn.style.whiteSpace = 'nowrap'; // B50-3316030, B50-3346235: pre cause extra space
	}
	var eheadtblw: string | undefined,
		efoottblw: string | undefined,
		ebodytblw: string | undefined,
		eheadtblfix: string | undefined,
		efoottblfix: string | undefined,
		ebodytblfix: string | undefined,
		hdfaker = wgt.ehdfaker,
		bdfaker = wgt.ebdfaker,
		ftfaker = wgt.eftfaker,
		head = wgt.head,
		headn = head ? head.$n() : undefined,
		hdfakerws: string[] = [],
		bdfakerws: string[] = [],
		ftfakerws: string[] = [],
		hdws: string[] = [],
		hdcavews: string[] = [];

	if (wgt.eheadtbl && headn) {//clear and backup headers widths
		eheadtblw = wgt.getInnerWidth() || '';
		wgt.eheadtbl.width = '';
		wgt.eheadtbl.style.width = '';
		eheadtblfix = wgt.eheadtbl.style.tableLayout;
		wgt.eheadtbl.style.tableLayout = '';
		if (zk.chrome)
			wgt.ebodytbl!.style.display = 'block';
		var headcol = hdfaker!.firstChild,
			headcell = headn.firstChild;
		for (var i = 0; headcol; headcol = headcol.nextSibling, i++) {
			var headcave = headcell ? headcell.firstChild : undefined;
			if (!headcave)
				continue;
			hdfakerws[i] = (headcol as HTMLElement).style.width;
			(headcol as HTMLElement).style.width = '';
			hdws[i] = (headcell as HTMLElement).style.width;
			(headcell as HTMLElement).style.width = '';
			hdcavews[i] = (headcave as HTMLElement).style.width;
			(headcave as HTMLElement).style.width = '';

			headcell = headcell!.nextSibling;
		}
	}
	if (headn)
		headn.style.width = '';
	if (wgt.efoottbl) {//clear and backup footers widths
		efoottblw = eheadtblw || wgt.efoottbl.width;
		wgt.efoottbl.width = '';
		wgt.efoottbl.style.width = '';
		efoottblfix = wgt.efoottbl.style.tableLayout;
		wgt.efoottbl.style.tableLayout = '';
		if (zk.chrome)
			wgt.ebodytbl!.style.display = 'block';
		if (ftfaker) {
			var footcol = ftfaker.firstChild,
				ftrows = wgt.efootrows,
				ftcells = ftrows ? ftrows.rows[0].cells : undefined;
			for (var i = 0; footcol; footcol = footcol.nextSibling) {
				if (ftcells?.[i])
					ftcells[i].style.width = ''; // reset it

				ftfakerws[i++] = (footcol as HTMLElement).style.width;
				(footcol as HTMLElement).style.width = '';
			}
		}
	}
	if (wgt.ebodytbl) {//clear and backup body faker widths
		ebodytblw = eheadtblw || wgt.ebodytbl.width;
		wgt.ebodytbl.width = '';
		wgt.ebodytbl.style.width = '';
		ebodytblfix = wgt.ebodytbl.style.tableLayout;
		wgt.ebodytbl.style.tableLayout = '';
		if (zk.chrome)
			wgt.ebodytbl.style.display = 'block';
		if (bdfaker) {
			var bodycol = bdfaker.firstChild;
			for (var i = 0; bodycol; bodycol = bodycol.nextSibling) {
				bdfakerws[i++] = (bodycol as HTMLElement).style.width;
				(bodycol as HTMLElement).style.width = '';
			}
		}
	}

	//calculate widths
	var wds: number[] = [],
		width = 0,
		len = head ? head.nChildren : 0,
		w = head ? head.firstChild : undefined,
		headWgt = wgt.getHeadWidget();
	if (bdfaker && w) {
		var bodycells = wgt._getFirstRowCells(wgt.ebodyrows),
			footcells = ftfaker ? wgt._getFirstRowCells(wgt.efootrows) : undefined;

		for (var i = 0; i < len; i++) {
			var wd = bodycells?.[i] ? zk(bodycells[i]).offsetWidthCeil() : 0,
				ftwd = footcells?.[i] && zk(footcells[i]).isVisible() ? footcells[i].offsetWidth : 0,
				hdwd = w?.isVisible() ? w.getContentWidth_() : 0,
				header: zk.Widget | undefined;

			if ((header = headWgt!.getChildAt(i)) && header.getWidth())
				hdwd = Math.max(hdwd, ftwd);
			if (hdwd > wd)
				wd = hdwd;
			if (ftwd > wd)
				wd = ftwd;
			wds[i] = wd;
			// Bug ZK-2772 don't plus one when frozen exists.
			if (!wgt.frozen && (zk.ff! > 4 || zk.ie11 || zk.safari)) // firefox4 & IE9, 10, 11 & safari still cause break line in case B50-3147926 column 1
				++wds[i];
			width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
			if (w)
				w = w.nextSibling;
		}
		wgt._deleteFakeRow(wgt.ebodyrows);
		if (ftfaker)
			wgt._deleteFakeRow(wgt.efootrows);
	} else {
		var tr = _getSigRow(wgt);
		if (tr) {
			for (var cells = tr.cells, i = cells.length; i--;) {
				var wd = cells[i].offsetWidth;
				wds[i] = wd;

				// Bug ZK-2772 don't plus one when frozen exists.
				if (!wgt.frozen && (zk.ff! > 4 || zk.ie11)) // firefox4 & IE9, 10, 11 still cause break line in case B50-3147926 column 1
					++wds[i];
				width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
			}
		}
	}

	// ZK-3313: The width of empty message counts
	var empty = wgt.$n('empty');
	if (empty && empty.style.display != 'none') {
		var emptyWidth = empty.offsetWidth;
		if (emptyWidth > width)
			width = emptyWidth;
	}

	if (wgt.eheadtbl && headn) {//restore headers widths
		if (!eheadtblw!.includes('%')) { // once sized and consider faker bar
			var $hdfakerbar = jq(wgt.head!.$n_('hdfaker')).find('[id*=hdfaker-bar]'),
				hdfakerbar = $hdfakerbar[0];
			if (hdfakerbar) eheadtblw = jq.px0(parseInt(eheadtblw!) + parseInt(hdfakerbar.style.width));
			wgt.eheadtbl.style.width = eheadtblw!;
		} else
			wgt.eheadtbl.width = eheadtblw ?? '';

		wgt.eheadtbl.style.tableLayout = eheadtblfix ?? '';
		if (zk.chrome)
			wgt.eheadtbl.style.display = '';
		var headcol = hdfaker!.firstChild,
			headcell = headn.firstChild;
		for (var i = 0; headcol; headcol = headcol.nextSibling, i++) {
			var headcave = headcell ? headcell.firstChild : undefined;
			if (!headcave)
				continue;
			(headcol as HTMLElement).style.width = hdfakerws[i];
			(headcell as HTMLElement).style.width = hdws[i];
			(headcave as HTMLElement).style.width = hdcavews[i];

			headcell = headcell!.nextSibling;
		}
	}
	if (wgt.efoottbl) {//restore footers widths
		wgt.efoottbl.width = efoottblw ?? '';
		wgt.efoottbl.style.tableLayout = efoottblfix ?? '';
		if (zk.chrome)
			wgt.efoottbl.style.display = '';
		if (ftfaker) {
			var footcol = ftfaker.firstChild;
			for (var i = 0; footcol; footcol = footcol.nextSibling)
				(footcol as HTMLElement).style.width = ftfakerws[i++];
		}
	}
	if (wgt.ebodytbl) {//restore body fakers widths
		wgt.ebodytbl.width = ebodytblw ?? '';
		wgt.ebodytbl.style.tableLayout = ebodytblfix ?? '';
		if (zk.chrome)
			wgt.ebodytbl.style.display = '';
		if (bdfaker) {
			var bodycol = bdfaker.firstChild;
			for (var i = 0; bodycol; bodycol = bodycol.nextSibling)
				(bodycol as HTMLElement).style.width = bdfakerws[i++];
		}
	}

	if (wgtn)
		wgtn.style.whiteSpace = ws;
	return { width: width, wds: wds };
}
function _fixBodyMinWd(wgt: MeshWidget, fixMesh?: boolean): void {
	// effective only when there is no header
	var sbc = wgt.isSizedByContent(),
		meshmin = wgt._hflex == 'min';
	if (!wgt.head && (meshmin || sbc)) {
		var bdw = zk(wgt.$n()).padBorderWidth(),
			wd = _getMinWd(wgt) + bdw, // has to call _getMinWd first so wgt._minWd will be available
			// eslint-disable-next-line zk/noNull
			tr: ChildNode | null = wgt.ebodytbl!,
			wds = wgt._minWd!.wds,
			wlen = wds.length;
		if (fixMesh && meshmin)
			wgt.setFlexSize_({ width: wd }, true);
		if (!(tr = tr.firstChild) || !(tr = tr.firstChild))
			return; // no first tr
		// eslint-disable-next-line zk/noNull
		for (var c: ChildNode | null = tr.firstChild, i = 0; c && (i < wlen); c = c.nextSibling)
			(c as HTMLElement).style.width = jq.px(wds[i++]);
		if (sbc && !meshmin) {
			// add flex <td> if absent
			var bdfx = tr.lastChild,
				bdfxid = wgt.uuid + '-bdflex';
			if (!bdfx || (bdfx as HTMLElement).id != bdfxid) {
				jq(tr).append('<td id="' + bdfxid + '"></td>');
				bdfx = tr.lastChild;
			}
		}
	}
}
function _fixPageSize(wgt: MeshWidget, rows: HTMLCollectionOf<HTMLTableRowElement>): boolean {
	var ebody = wgt.ebody;
	if (!ebody)
		return false; //not ready yet
	var max = ebody.offsetHeight;
	if (zk(ebody).hasHScroll()) //with horizontal scrollbar
		max -= jq.scrollbarWidth();
	if (max == wgt._prehgh) return false; //same height, avoid fixing page size
	wgt._prehgh = max;
	var ebodytbl = wgt.ebodytbl!,
		etbparent = ebodytbl.offsetParent,
		etbtop = ebodytbl.offsetTop,
		hgh = 0,
		row: HTMLTableRowElement | undefined,
		j = 0;
	for (var it = wgt.getBodyWidgetIterator({ skipHidden: true }),
		len = rows.length; it.next() && j < len; j++) {
		row = rows[j];
		var top: number | undefined = row.offsetTop - (row.offsetParent == etbparent ? etbtop : 0);
		if (top > max) {
			--j;
			break;
		}
		hgh = top;
	}
	if (row) { //there is row
		var pgib = wgt.$n('pgib'),
			pgibHgh = 0;
		if (pgib)
			pgibHgh = pgib.offsetHeight;
		var withPgibMax = max + pgibHgh;
		if (top! <= withPgibMax) { //row not exceeds the height, estimate
			var paging = (wgt.paging) ? wgt.paging : wgt.getPaginal()!,
				totalsz = paging.getTotalSize(),
				rowsHgh = hgh + row.offsetHeight,
				j1 = Math.floor(j * max / rowsHgh),
				j2 = Math.floor(j * withPgibMax / rowsHgh);
			if (totalsz > j2 || !paging.isAutohide())
				j = j1;
			else
				j = j2;
		}
		//enforce pageSize change
		if (j == 0) j = 1; //at least one per page
		if (j != wgt.getPageSize()) {
			wgt.fire('onPageSize', { size: j });
			return true;
		}
	}
	return false;
}
function _adjMinWd(wgt: MeshWidget): void {
	if (wgt._hflex == 'min') {
		var w = _getMinWd(wgt),
			n = wgt.$n_();
		wgt._hflexsz = w + zk(n).padBorderWidth(); //override
		n.style.width = jq.px0(wgt._hflexsz);
	}
}
function _getMinWd(wgt: MeshWidget): number {
	wgt._calcMinWds();
	var bdfaker = wgt.ebdfaker,
		wd: number,
		wds: number[] = [],
		width: number,
		_minwds = wgt._minWd!.wds;
	if (wgt.head && bdfaker) {
		width = 0;
		var w = wgt.head.firstChild,
			bdcol = bdfaker.firstChild;

		for (var i = 0; w; w = w.nextSibling) {
			if (w._hflex == 'min')
				wd = wds[i] = _minwds[i] + zk(w.$n()).padBorderWidth();
			else {
				if (w._width && w._width.indexOf('px') > 0)
					wd = wds[i] = zk.parseInt(w._width);
				else
					wd = wds[i] = zk.parseInt((bdcol as HTMLElement).style.width);
			}
			// ZK-2130: should save the head width
			w._origWd = jq.px0(wd);
			width += wd;
			++i;
			bdcol = bdcol!.nextSibling;
		}
	} else
		width = wgt._minWd!.width; // no header
	return width + (zk(wgt.ebody).hasVScroll() ? jq.scrollbarWidth() : 0);
}
function _getSigRow(wgt: MeshWidget): HTMLTableRowElement | undefined {
	// scan for tr with largest number of td children
	var rw = wgt.getBodyWidgetIterator().next(),
		// eslint-disable-next-line zk/noNull
		tr = rw ? rw.$n() : null;
	if (!tr)
		return;
	// eslint-disable-next-line zk/noNull
	for (var maxtr = tr, len: number, max = maxtr.cells.length; tr; tr = tr.nextSibling as HTMLTableRowElement | null)
		if ((len = tr.cells.length) > max) {
			maxtr = tr;
			max = len;
		}
	return maxtr;
}
function _cpCellWd(wgt: MeshWidget): void {
	var dst = wgt.efootrows!.rows[0],
		srcrows = wgt.ebodyrows!.rows;
	if (!dst || !srcrows || !srcrows.length || !dst.cells.length)
		return;
	var ncols = dst.cells.length,
		// eslint-disable-next-line zk/noNull
		src: HTMLTableRowElement | null | undefined,
		maxnc = 0;
	for (var j = 0, it = wgt.getBodyWidgetIterator({ skipHidden: true }), w: zul.mesh.Item | undefined; (w = it.next());) {
		if (!w._loaded || w.z_rod)
			continue;

		var row = srcrows[j++], $row = zk(row),
			cells = row.cells, nc = $row.ncols(),
			valid = cells.length == nc && $row.isVisible();
		//skip with colspan and invisible
		if (valid && nc >= ncols) {
			maxnc = ncols;
			src = row;
			break;
		}
		if (nc > maxnc) {
			src = valid ? row : undefined;
			maxnc = nc;
		} else if (nc == maxnc && !src && valid) {
			src = row;
		}
	}
	if (!maxnc) return;

	var fakeRow = !src;
	if (fakeRow) { //the longest row containing colspan
		src = document.createElement('TR') as HTMLTableRowElement;
		src.style.height = '0px';
		//Note: we cannot use display="none" (offsetWidth won't be right)
		for (var j = 0; j < maxnc; ++j)
			src.appendChild(document.createElement('TD'));
		srcrows[0].parentNode!.appendChild(src);
	}
	//we have to clean up first, since, in FF, if dst contains %
	//the copy might not be correct
	for (var j = maxnc; j--;)
		dst.cells[j].style.width = '';

	var sum = 0;
	for (var j = maxnc; j--;) {
		var d = dst.cells[j], s = src!.cells[j];
		if (zk.opera) {
			sum += s.offsetWidth;
			d.style.width = zk(s).contentWidth() as unknown as string;
		} else {
			d.style.width = `${s.offsetWidth}px`;
			if (maxnc > 1) { //don't handle single cell case (bug 1729739)
				var v = s.offsetWidth - d.offsetWidth;
				if (v != 0) {
					v += s.offsetWidth;
					if (v < 0) v = 0;
					d.style.width = `${v}px`;
				}
			}
		}
	}
	if (zk.opera && wgt.isSizedByContent())
		(dst.parentNode!.parentNode as HTMLElement).style.width = `${sum}px`;
	if (fakeRow)
		src!.parentNode!.removeChild(src!);
}
function listenOnFitSize(wgt: MeshWidget): void {
	if (wgt._rows && !wgt._rowsOnFitSize) {
		zWatch.listen({ onFitSize: wgt });
		wgt._rowsOnFitSize = true;
	}
}
function unlistenOnFitSize(wgt: MeshWidget): void {
	if (wgt._rowsOnFitSize) {
		zWatch.unlisten({ onFitSize: wgt });
		delete wgt._rowsOnFitSize;
	}
}

export interface Item extends zul.Widget<HTMLTableRowElement> {
	_loaded?: boolean;
	_index?: number;
}

export interface ItemIterator<TItem extends zul.mesh.Item = zul.mesh.Item> {
	hasNext(): boolean;
	next(): TItem | undefined;
	length?: number;
}
/**
 *  A skeletal implementation for a mesh widget.
 *  @see zul.grid.Grid
 *  @see zul.sel.Tree
 *  @see zul.sel.Listbox
 */
@zk.WrapClass('zul.mesh.MeshWidget')
export abstract class MeshWidget extends zul.Widget {
	_rows = 0;
	_pagingPosition = 'bottom';
	_prehgh = -1;
	_minWd?: MeshWidth; //minimum width for each column
	_sizedByContent?: boolean;
	_span?: string | boolean;
	_nspan?: number;
	_autopaging?: boolean;
	_model?: boolean | string;
	_paginal?: zul.mesh.Paging;
	_pendOnRender?: boolean;
	_ebodyScrollPos?: { l: number; t: number };
	// Types established from inspecting https://www.zkoss.org/zkdemo/grid/header_and_footer
	ehead?: HTMLDivElement;
	eheadtbl?: HTMLTableElement;
	ehdfaker?: HTMLTableColElement;
	eheadrows?: HTMLTableSectionElement;
	// Types established from inspecting https://www.zkoss.org/zkdemo/grid/header_and_footer
	ebody?: HTMLDivElement;
	ebodytbl?: HTMLTableElement;
	ebdfaker?: HTMLTableColElement;
	ebodyrows?: HTMLTableSectionElement;
	// Types established from inspecting https://www.zkoss.org/zkdemo/grid/header_and_footer
	efoot?: HTMLDivElement;
	efoottbl?: HTMLTableElement;
	eftfaker?: HTMLTableColElement;
	efootrows?: HTMLTableSectionElement;

	efrozen?: HTMLElement;
	frozen?: zul.mesh.Frozen;

	paging?: zul.mesh.Paging;
	heads: zul.mesh.HeadWidget[];
	head?: zul.mesh.HeadWidget;
	foot?: zul.grid.Foot;
	_visiRows?: number;
	_wsbak?: string;
	_targetIndex?: number;
	_keepScroll?: boolean;
	_bottomBoundary?: number;
	_topBoundary?: number;
	_rowsOnFitSize?: boolean;
	_scrollbar?: zul.Scrollbar;
	_cachehgh?: number;
	_lastDevicePixelRatio?: number;
	_adjustScrollTopLater?: boolean;
	_shallSize?: boolean;
	_syncingbodyrows?: boolean;
	_shallClearTableWidth?: boolean;
	_shallShowScrollbar?: boolean;

	_syncEmpty(): void {
		// Empty on purpose. To be inherited.
	}

	constructor() {
		super(); // FIXME: arguments?
		this.heads = [];
	}

	// `zul.mesh.prototype.HeadWidget.onColSize` could assign `_innerWidth` a number
	_innerWidth = '100%';
	_currentTop = 0;
	_currentLeft = 0;
	_nativebar = true;

	abstract getHeadWidgetClass(): typeof zul.mesh.HeadWidget;
	abstract getBodyWidgetIterator(opts?: Record<string, unknown>): ItemIterator;
	abstract itemIterator(opts?: Record<string, unknown>): ItemIterator;

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
			listenOnFitSize(this);
			var n = this.$n();
			if (n) {
				n._lastsz = undefined;
				this.onSize();
			}
		}

		return this;
	}

	/**
	 * Returns how to position the paging of the widget at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @return String
	 */
	getPagingPosition(): string {
		return this._pagingPosition;
	}

	/**
	 * Sets how to position the paging of the widget at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @param String pagingPosition how to position. It can only be "bottom" (the default), or
	 * "top", or "both".
	 */
	setPagingPosition(pagingPosition: string, opts?: Record<string, boolean>): this {
		const o = this._pagingPosition;
		this._pagingPosition = pagingPosition;

		if (o !== pagingPosition || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether sizing the widget column width by its content. Default is false.
	 * <p>Note: if the "sized-by-content" attribute of component is specified,
	 * it's prior to the original value.
	 * @return boolean
	 * @see #setSizedByContent
	 */
	isSizedByContent(): boolean {
		return !!this._sizedByContent;
	}

	/**
	 * Sets whether sizing the widget column width by its content. Default is false, i.e.
	 * the outline of the widget is dependent on browser. It means, we don't
	 * calculate the width of each cell. If set to true, the outline will count on
	 * the content of body. In other words, the outline of the widget will be like
	 * ZK version 2.4.1 that the header's width is only for reference.
	 *
	 * <p> You can also specify the "sized-by-content" attribute of component in
	 * lang-addon.xml directly, it will then take higher priority.
	 * @param boolean byContent
	 */
	setSizedByContent(sizedByContent: boolean, opts?: Record<string, boolean>): this {
		const o = this._sizedByContent;
		this._sizedByContent = sizedByContent;

		if (o !== sizedByContent || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Return column span hint of this widget.
	 * <p>Default: null
	 * @return String column span hint of this widget.
	 * @since 5.0.6
	 * @see #setSpan
	 */
	getSpan(): string | boolean | undefined {
		return this._span;
	}
	isSpan(): string | boolean | undefined {
		return this._span;
	}

	/**
	 * Sets column span hint of this mesh widget.
	 * <p>The parameter span is a number in String type indicating how this
	 * component distributes remaining empty space to the
	 * specified column(0-based). "0" means distribute remaining empty space to the 1st column; "1" means
	 * distribute remaining empty space to the 2nd column, etc.. The spanning column will grow to
	 * fit the extra remaining space.</p>
	 * <p>Special span hint with "true" means span ALL columns proportionally per their
	 * original widths while null or "false" means NOT spanning any column.</p>
	 * <p>Default: null. That is, NOT span any column.</p>
	 * <p>Note span is meaningful only if there is remaining empty space for columns.</p>
	 *
	 * @param String span the column span hint.
	 * @since 5.0.6
	 * @see #getSpan
	 */
	setSpan(span: string | boolean, opts?: Record<string, boolean>): this {
		const o = this._span;
		this._span = span;

		if (o !== span || opts?.force) {
			//ZK-2776: if span="false", !isSpan() will return false, because "false" is string not boolean
			var isTrue = true === span || 'true' == span,
				isFalse = false === span || 'false' == span;
			this._span = isTrue ? true : isFalse ? false : span;

			var x = isTrue ? -65500 : isFalse ? 0 : (zk.parseInt(span) + 1);
			this._nspan = x < 0 && x != -65500 ? 0 : x;
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether turn on auto-paging facility when mold is
	 * "paging". If it is set to true, the {@link #setPageSize} is ignored;
	 * rather, the page size(number of item count) is automatically determined by the
	 * height of this widget dynamically.
	 * @return boolean
	 * @see #setAutopaging
	 */
	isAutopaging(): boolean {
		return !!this._autopaging;
	}

	/**
	 * Sets whether turn on auto-paging facility when mold is
	 * "paging". If it is set to true, the {@link #setPageSize} is ignored;
	 * rather, the page size(number of item count) is automatically determined by the
	 * height of this widget dynamically.
	 * @param boolean autopaging
	 */
	setAutopaging(autopaging: boolean, opts?: Record<string, boolean>): this {
		const o = this._autopaging;
		this._autopaging = autopaging;

		if (o !== autopaging || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether the widget is in model mode or not.
	 * @return boolean
	 */
	isModel(): boolean {
		return !!this._model;
	}
	// TODO: Used by grid. Should prefer the use of isModel
	getModel(): boolean | string | undefined {
		return this._model; // it could be "group" for groups model
	}

	/**
	 * Sets whether the widget is in model mode.
	 * @param boolean inModel
	 */
	setModel(model: boolean): this {
		this._model = model;
		return this;
	}

	/**
	 * Returns the inner width of this component.
	 * The inner width is the width of the inner table.
	 * <p>Default: "100%"
	 * @see #setInnerWidth
	 * @return String
	 */
	getInnerWidth(): string {
		return this._innerWidth;
	}

	/**
	 * Sets the inner width of this component.
	 * The inner width is the width of the inner table.
	 * By default, it is 100%. That is, it is the same as the width
	 * of this component. However, it is changed when the user
	 * is sizing the column's width.
	 *
	 * <p>Application developers rarely call this method, unless
	 * they want to preserve the widths of sizable columns
	 * changed by the user.
	 * To preserve the widths, the developer have to store the widths of
	 * all columns and the inner width ({@link #getInnerWidth}),
	 * and then restore them when re-creating this component.
	 *
	 * @param String innerWidth the inner width. If null, "100%" is assumed.
	 */
	setInnerWidth(innerWidth: string, opts?: Record<string, boolean>): this {
		const o = this._innerWidth;
		this._innerWidth = innerWidth;

		if (o !== innerWidth || opts?.force) {
			if (innerWidth == null) this._innerWidth = innerWidth = '100%';
			if (this.eheadtbl) this.eheadtbl.style.width = innerWidth;
			if (this.ebodytbl) this.ebodytbl.style.width = innerWidth;
			if (this.efoottbl) this.efoottbl.style.width = innerWidth;
		}

		return this;
	}

	/**
	 * Returns the external Paging widget, if any.
	 * @return Paging
	 */
	getPaginal(): zul.mesh.Paging | undefined {
		return this._paginal;
	}

	/**
	 * Sets the external Paging widget.
	 * @param Paging paging
	 */
	setPaginal(paginal?: zul.mesh.Paging): this {
		if (this._paginal != paginal) {
			if (this._paginal) {
				this._paginal.setMeshWidget();
			}
			this._paginal = paginal;

			if (this._paginal) {
				this._paginal.setMeshWidget(this);
			}
		}
		return this;
	}

	/** Returns the page size, aka., the number rows per page.
	 * @return int
	 * @see Paging#getPageSize
	 */
	getPageSize(): number {
		var pgnl = this.getPagingChild();
		return pgnl ? pgnl.getPageSize() : 0;
	}

	/** Sets the page size, aka., the number rows per page.
	 * @param int pageSize
	 * @see Paging#setPageSize
	 */
	setPageSize(pageSize: number): this {
		this.getPagingChild()?.setPageSize(pageSize);
		return this;
	}

	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @return int
	 * @see Paging#getPageCount
	 */
	getPageCount(): number {
		return this.getPagingChild()?.getPageCount() ?? 1;
	}

	/** Returns the active page (starting from 0).
	 * @return int
	 * @see Paging#getActivePage
	 */
	getActivePage(): number {
		var pgnl = this.getPagingChild();
		return pgnl ? pgnl.getActivePage() : 0;
	}

	/** Sets the active page (starting from 0).
	 * @param int activePage
	 * @see Paging#setActivePage
	 */
	setActivePage(activePage: number): this {
		const pgnl = this.getPagingChild();
		if (pgnl)
			pgnl.setActivePage(activePage);
		return this;
	}

	/**
	 * Returns whether the widget is in paging mold.
	 * @return boolean
	 */
	inPagingMold(): boolean {
		return 'paging' == this.getMold();
	}

	getPagingChild(): zul.mesh.Paging | undefined {
		return this.paging ?? this.getPaginal();
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop) {
			this._setHgh(height);
			this.onSize();
		}
		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.eheadtbl) this.eheadtbl.style.width = '';
		if (this.efoottbl) this.efoottbl.style.width = '';
		if (this.desktop)
			this.onSize();
		return this;
	}

	override setStyle(style: string): this {
		if (this._style != style) {
			super.setStyle(style);
			if (this.desktop)
				this.onSize();
		}
		return this;
	}

	/**
	 * Returns the self's head widget.
	 * @return zul.mesh.HeadWidget
	 * @since 5.0.4
	 */
	getHeadWidget(): zul.mesh.HeadWidget | undefined {
		return this.head;
	}

	/**
	 * Returns the index of the cell including the child got focus.
	 * @param DOMElement el the element got focus.
	 * @return DOMElement the cell element.
	 * @since 5.0.7
	 */
	getFocusCell(el: HTMLElement): HTMLTableCellElement | undefined {
		var td: HTMLTableCellElement | undefined;
		// Note: `jq.each` is in general not the same as `jq().each` although it is in this case.
		// But, only `jq.each` matches the definition in `jQuery.d.ts`.
		// See https://api.jquery.com/jquery.each/#:~:text=%24.each()%20function-,is%20not%20the%20same%20as,-%24(selector).each()
		jq.each([this.ebodytbl, this.eheadtbl, this.efoottbl], function (i) {
			if (this && jq.isAncestor(this, el)) {
				var tds = jq(el).parents(i == 1 ? 'th' : 'td'); // headtable uses TH, others uses TD
				for (var i = 0, j = tds.length; i < j; i++) {
					td = tds[i];
					if (jq(td).parents('table')[0] == this) {
						return false; // break the loop;
					}
				}
			}
		});
		return td;
	}

	_moveToHidingFocusCell(index: number, ignoreWidth?: boolean, notFocusBack?: boolean): void { //used in Row/Listcell and a11y frozen
		//B50-3178977 navigating the input in hiddin column.
		var td = this.ehdfaker ? this.ehdfaker.childNodes[index] as HTMLTableCellElement | undefined : undefined,
			frozen = this.frozen;
		if (td && frozen && (ignoreWidth || zk.parseInt(td.style.width) == 0)
			&& (index = index - frozen.getColumns()!) >= 0) {
			const bar = this._scrollbar;
			if (this._nativebar) {
				frozen.setStart(index);
			} else if (bar) {
				frozen._doScrollNow(index);
				bar.setBarPosition(index);
			}
			_shallFocusBack = !notFocusBack;
		}
	}

	_restoreFocus(): void { //used in Frozen
		if (_shallFocusBack && zk.currentFocus) {
			_shallFocusBack = false;
			zk.currentFocus.focus();
		}
	}

	disableAutoSizing_(): void {
		this._span = false;
		this._nspan = 0;
		this._sizedByContent = false;
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		this._bindDomNode();
		if (this.frozen || zk.ie)
			this._cssflex = false; //force to use old flex
		if (this._cssflex && this.isChildrenFlex()) {//css flex
			this.ehdfaker!.style.display = 'none';
		} else if (this._hflex != 'min')
			this._fixHeaders();
		if (this.ehead) //sync scroll for input tab key scroll
			this.domListen_(this.ehead, 'onScroll', '_doSyncScroll');

		var ebody = this.ebody;
		if (this._nativebar && ebody) {
			this.domListen_(ebody, 'onScroll', '_doScroll');
			ebody.style.overflow = 'auto';

			if (this.efrozen && !zk.mobile)
				jq(ebody).css('overflow-x', 'hidden'); // keep non line break
		}
		zWatch.listen({ onSize: this, onResponse: this });
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		unlistenOnFitSize(this);
		zWatch.unlisten({ onSize: this, onResponse: this });
		if (this.ehead) //sync scroll for input tab key scroll
			this.domUnlisten_(this.ehead, 'onScroll', '_doSyncScroll');
		var ebody = this.ebody;
		if (this._nativebar && ebody) {
			this.domUnlisten_(ebody, 'onScroll', '_doScroll');
			if (this.efrozen)
				jq(ebody).css('overflow-x', 'auto');
		}
		super.unbind_(skipper, after, keepRod);
	}

	override clearCache(): void {
		super.clearCache();
		this.ebody = this.ehead = this.efoot = this.efrozen = this.ebodytbl
			= this.eheadtbl = this.efoottbl = this.ebodyrows
			= this.ehdfaker = this.ebdfaker = undefined;
	}

	/** Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize(): void {
		if (this.desktop) {
			this.clearCachedSize_();
			if (this._hflex == 'min') {
				zFlex.onFitSize.call(this);
			} else {
				this._fixHeaders();
			}
			zWatch.fireDown('onSize', this);
		}
	}

	// These paramters are not used. They exist for the sake of inheritance.
	onResponse(ctl?: zk.ZWatchController, opts?: Record<string, unknown>): void {
		if (this._shallSize) {
			if (this._shallClearTableWidth) {
				this._clearTableWidth();
				this._shallClearTableWidth = false;
			}
			this.syncSize();
			this._shallSize = false; // just in case
		}
	}

	_syncSize(shallClearTableWidth?: boolean): void {
		// fixed for F50-3025422.zul on ZTL
		if (this.desktop) {
			this._shallSize = true;
			this._shallClearTableWidth = !!shallClearTableWidth;
		}
	}

	_clearTableWidth(): void {
		if (this.desktop) {
			// clear table width
			var hdtbl = this.eheadtbl,
				bdtbl = this.ebodytbl,
				fttbl = this.efoottbl;
			if (hdtbl)
				hdtbl.style.width = '';
			if (bdtbl)
				bdtbl.style.width = '';
			if (fttbl)
				fttbl.style.width = '';
		}
	}

	_fixHeaders(force?: boolean): boolean { //used in HeadWidget
		if (this.head && this.ehead) {
			if (this._cssflex && this.isChildrenFlex()) return false;
			var empty = true,
				flex = false,
				shouldFix = false,
				shouldCheckEmptyWidth = !this.frozen && this.eheadtbl && this.eheadtbl.style.width,
				hdsmin = (this._hflex == 'min') || this.isSizedByContent();
			for (var i = this.heads.length; i-- > 0;) {
				var header = this.heads[i],
					emptyHeader = true,
					column = 0;
				for (var w = header.firstChild; w; w = w.nextSibling) {
					//B70-ZK-2559: when dynamic adding auxhead, there has already
					//been auxhead widget while dom element hasn't attached yet
					var childNode = this.ehdfaker!.childNodes[column++];
					if (hdsmin && (childNode && !(childNode as HTMLElement).style.width) && !w._nhflex) {
						// B50-3357475: assume header hflex min if width/hflex unspecified
						w._hflex = 'min';
						w._nhflex = -65500; // min
						w._nhflexbak = true;
					}
					if (!flex && w._nhflex)
						flex = true;
					if (w.getLabel() || w.getImage() || w.nChildren) {
						emptyHeader = false;
						empty = false;
					}
					if (shouldCheckEmptyWidth && !w.getWidth()) {
						shouldFix = true;
					}
				}

				if (header._visible) {
					//B70-ZK-2559: description as mentioned
					var n = header.$n();
					if (n)
						n.style.display = emptyHeader ? 'none' : ''; // Bug ZK-2348
				}
			}
			var old = this.ehead.style.display,
				tofix = force && flex && this.isRealVisible(); //Bug ZK-1647: no need to consider empty header for flex calculation
			this.ehead.style.display = empty ? 'none' : '';
			//onSize is not fired to empty header when loading page, so we have to simulate it here
			for (var w = this.head.firstChild; w; w = w.nextSibling) {
				if (tofix && w._nhflex)
					w.fixFlex_();
				if (w._nhflexbak) {
					delete w._hflex;
					delete w._nhflex;
					delete w._nhflexbak;
				}
			}
			return shouldFix || old != this.ehead.style.display;
		}
		return false;
	}

	_adjFlexWd(): void { //used by HeadWidget
		var head = this.head;
		if (head) {
			var hdfaker = this.ehdfaker!,
				bdfaker = this.ebdfaker!,
				hdcol = hdfaker.firstChild,
				bdcol = bdfaker.firstChild,
				ftfaker = this.eftfaker,
				// eslint-disable-next-line zk/noNull
				ftcol: ChildNode | null = null;


			if (ftfaker)
				ftcol = ftfaker.firstChild;

			//B70-ZK-2130: clean table width to prevent incorrect width
			this.eheadtbl!.style.width = '';
			this.ebodytbl!.style.width = '';

			//B70-ZK-2394: store total bdcol width
			var tblWidth = 0,
				cachedOffsetWidths: Record<string, number> = {};

			// ZK-2098: should skip if bdcol doesn't exist
			for (var w = head.firstChild; w && bdcol; w = w.nextSibling) {
				// ZK-2130: should save the header width
				// ZK-2071: use offsetWidth instead of style.width
				cachedOffsetWidths[w.uuid] = w.$n_().offsetWidth;
				bdcol = bdcol.nextSibling;
			}

			bdcol = bdfaker.firstChild;
			// ZK-4320: Do not adjust widget's width if smooth is false and frozen is scrolled
			var frozen = this.frozen;
			if (!(frozen && !frozen._smooth && frozen.getStart())) {
				for (var w = head.firstChild, wd: number | undefined; w && bdcol; w = w.nextSibling) {
					var wwd = cachedOffsetWidths[w.uuid];
					if (w.isVisible() && wwd > 0.1)
						w._origWd = jq.px0(wwd);
					// B70-ZK-2036: Do not adjust widget's width if it is not visible.
					if (w.isVisible() && (wd = w._hflexWidth) !== undefined) {
						var revisedWidth = zk(bdcol).revisedWidth(Math.round(wd)),
							revisedWidthPx = jq.px0(revisedWidth);
						//B70-ZK-2509: w.$n().offsetWidth is small when there are many columns at beginning, so save revised width if any
						w._origWd = revisedWidthPx;
						(bdcol as HTMLElement).style.width = revisedWidthPx;
						(hdcol as HTMLElement).style.width = revisedWidthPx;
						if (ftcol)
							(ftcol as HTMLElement).style.width = revisedWidthPx;

						//B70-ZK-2394: store total bdcol width
						tblWidth += revisedWidth;
					}
					bdcol = bdcol.nextSibling;
					hdcol = hdcol!.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
				}
			}

			//B70-ZK-2394: sync width from colgroup to hdtbl, bdtbl, fttbl
			var allWidths = this._isAllWidths();
			if (allWidths) {
				var hdtbl = this.eheadtbl,
					bdtbl = this.ebodytbl,
					fttbl = this.efoottbl;

				if (hdtbl) {
					var tblWidthPx = jq.px0(tblWidth);
					hdtbl.style.width = tblWidthPx;
					if (bdtbl)
						bdtbl.style.width = tblWidthPx;
					if (fttbl)
						fttbl.style.width = tblWidthPx;
				}
			}

			_adjMinWd(this);
			this._afterCalcSize();
		}
	}

	_bindDomNode(): void {
		this.ehead = this.$n('head');
		this.eheadtbl = this.$n('headtbl');
		this.ebody = this.$n('body');
		this.ebodytbl = this.$n('cave');
		this.efoot = this.$n('foot');
		this.efoottbl = this.$n('foottbl');
		this.efrozen = this.$n('frozen');

		// Grid will bind ebodyrows in Rows widget
		var erows = this.$n<HTMLTableSectionElement>('rows');
		if (this.ebody && erows)
			this.ebodyrows = erows;

		if (this.ehead) {
			this.eheadrows = this.$n('headrows');
			this.ehdfaker = this.head!.$n('hdfaker');
			this.ebdfaker = this.head!.$n('bdfaker');
			if (this.efoot)
				this.eftfaker = this.head!.$n('ftfaker');
		}
		if (this.efoot)
			this.efootrows = this.$n('footrows');
	}

	override replaceHTML(n: HTMLElement | string, desktop?: zk.Desktop, skipper?: zk.Skipper, _trim_?: boolean, _callback_?: CallableFunction[]): void { // tree outer
		var old = this._syncingbodyrows;
		this._syncingbodyrows = true;
		try {
			//bug #2995434
			//20100503, Henri: cannot use $supers('replaceHTML') since it
			//will recursive back to this function via fire('onSize'). However,
			//ZK's $supers() is simulated and when we call $supers() again
			//here, the system thought it is calling from its super class rather
			//than this class and it will be wrong. Therefore, we are forced to
			//call super class's replaceHTML directly instead.
			//Therefore, we have to specify MeshWidget as follows
			super.replaceHTML(n, desktop, skipper, _trim_, _callback_);
		} finally {
			this._syncingbodyrows = old;
		}
	}

	override replaceChildHTML_(child: zk.Widget, n: HTMLElement | string, desktop?: zk.Desktop, skipper?: zk.Skipper, _trim_?: boolean): void { // rows outer
		var old = this._syncingbodyrows;
		this._syncingbodyrows = true;
		try {
			super.replaceChildHTML_(child, n, desktop, skipper, _trim_);
		} finally {
			this._syncingbodyrows = old;
		}
	}

	fireOnRender(timeout: number): void {
		if (!this._pendOnRender) {
			this._pendOnRender = true;
			setTimeout(this.proxy(this._onRender), timeout ? timeout : 100);
		}
	}

	_doScroll(): void { //called zkmax, overriden in Listbox
		var t = zul.mesh.Scrollbar.getScrollPosV(this),
			l = zul.mesh.Scrollbar.getScrollPosH(this),
			hScrolled = l != this._currentLeft,
			scrolled = (t != this._currentTop || hScrolled),
			ebody = this.ebody!,
			ehead = this.ehead,
			efoot = this.efoot;

		// ZK-2069: fire onScroll if has scrollable property
		if (jq(this).data('scrollable')) {
			zWatch.fireDown('onScroll', this);
			zWatch.fire('_onSyncScroll', this); // ZK-4408: for Popup only
		}

		//B70-ZK-2070: if scrolled, the scrollbar need fire onScroll event.
		if (scrolled && !(this.fire('onScroll', ebody.scrollLeft).stopped) && this._nativebar)
			if (this._currentLeft != ebody.scrollLeft) {
				if (ehead)
					ehead.scrollLeft = ebody.scrollLeft;
				if (efoot)
					efoot.scrollLeft = ebody.scrollLeft;
			}

		// ZK-2046: should sync currentTop in rod mode see also Bug ZK-353
		if (scrolled /* && !this._listbox$rod && !this._grid$rod*/)
			this._currentTop = t;

		if (scrolled) // always sync for B30-1737660.zul
			this._currentLeft = l;

		if (!this.paging && !this._paginal)
			this.fireOnRender(zk.gecko ? 200 : 60);

		if (scrolled)
			this._fireOnScrollPos();

		if (this.frozen && hScrolled)
			this.frozen.syncScrollByParentBody();
	}

	_doSyncScroll(): void { //sync scroll for input tab key scroll
		var ehead = this.ehead,
			ebody = this.ebody,
			efoot = this.efoot;
		if (ehead && zk(ehead).isVisible()) {
			if (this._currentLeft != ehead.scrollLeft) {
				if (ebody)
					ebody.scrollLeft = ehead.scrollLeft;
				if (efoot)
					efoot.scrollLeft = ehead.scrollLeft;
			}
		}
	}

	_timeoutId: number | undefined;

	_fireOnScrollPos(time?: number, forceToScroll?: boolean /* overriden in zkmax */): void {
		clearTimeout(this._timeoutId);
		this._timeoutId = setTimeout(this._onScrollPos.bind(this), time! >= 0 ? time : 300);
	}

	_onScrollPos(): void {
		// Bug ZK-414
		if (this.ebody) {
			this._currentTop = zul.mesh.Scrollbar.getScrollPosV(this);
			this._currentLeft = zul.mesh.Scrollbar.getScrollPosH(this);
			this.fire('onScrollPos', {
				top: this._currentTop,
				left: this._currentLeft
			});
		}
	}

	_onRender(): boolean { //overriden in zkmax
		if (!this.$n())
			return false; // the target may not exist. B50-ZK-963

		this._pendOnRender = false;
		if (this._syncingbodyrows || zAu.processing()) { //wait if busy (it might run outer)
			this.fireOnRender(zk.gecko ? 200 : 60); //is syncing rows, try it later
			return true;
		}

		var rows = this.ebodyrows ? this.ebodyrows.rows : undefined;
		if (this.inPagingMold() && this._autopaging && rows && rows.length)
			if (_fixPageSize(this, rows))
				return false; //need to reload with new page size

		if (zk.ie9 && (this._wsbak !== undefined)) { // B50-ZK-432
			this.$n_().style.whiteSpace = this._wsbak;
			delete this._wsbak;
		}

		if (!this.desktop || !this._model || !rows || !rows.length)
			return false;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var items: zk.Widget[] = [],
			min = zul.mesh.Scrollbar.getScrollPosV(this),
			max = min + this.ebody!.offsetHeight;
		if (min == 0 && max == 0)
			return false; //ZK-2796: Uncessary onRender command triggered when setting tabbox's maximalHeight attribute to true
		for (var j = 0, it = this.getBodyWidgetIterator({ skipHidden: true }),
			len = rows.length, w: zul.mesh.Item | undefined; (w = it.next()) && j < len; j++) {
			if (!w._loaded) {
				//B70-ZK-2589: w and rows[j] belongs to different widget,
				//w shouldn't depend on rows[j], origin -> row = rows[j];
				var row = w.$n();
				if (row == null) continue;
				var $row = zk(row),
					top = $row.offsetTop();

				if (top + $row.offsetHeight() < min) continue;
				if (top > max) break; //Bug 1822517
				items.push(w);
			}
		}
		if (items.length) {
			this.fire('onRender', { items: items }, { implicit: true });
		}
		return false;
	}

	override onSize(): void {
		var devicePixelRatio = zUtl.getDevicePixelRatio();
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n_();
			if (n._lastsz && n._lastsz.height == n.offsetHeight
				&& n._lastsz.width == n.offsetWidth
				&& this._lastDevicePixelRatio == devicePixelRatio) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
			this._lastDevicePixelRatio = devicePixelRatio;
			this._calcSize();// Bug #1813722
			this.fireOnRender(155);

			if (this._nativebar) { // Bug ZK-355: keep scrollbar position
				var ebody = this.ebody!;
				// ZK-3647: if Listbox enable ROD and need to init pad sizes, it will adjust scrollTop when _initPadSizes().
				if (ebody.scrollHeight >= this._currentTop && !this._adjustScrollTopLater)
					ebody.scrollTop = this._currentTop;

				if (ebody.scrollWidth >= this._currentLeft) {
					ebody.scrollLeft = this._currentLeft;
					if (this.ehead)
						this.ehead.scrollLeft = this._currentLeft;
					if (this.efoot)
						this.efoot.scrollLeft = this._currentLeft;
				}
			}
			this._shallSize = false;
			if (this._keepScroll) {
				this._scrollToIndex(this._targetIndex!);
			}
		}
	}

	_vflexSize(): number {
		var n = this.$n(),
			pgHgh = 0;
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) pgHgh += pgit.offsetHeight;
			if (pgib) pgHgh += pgib.offsetHeight;
		}
		// Bug #1815882 and Bug #1835369
		var hgh = zk(n).contentHeight()
			- (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0)
			- pgHgh;
		return this.frozen && this._nativebar ?
			hgh - this.efrozen!.offsetHeight : hgh;
	}

	override setFlexSize_(flexSize: zk.FlexSize, isFlexMin?: boolean): void {
		if (this._cssflex && this.parent!.getFlexContainer_() != null && !isFlexMin)
			return;
		var n = this.$n_(),
			head = this.$n('head');
		if (flexSize.height !== undefined) {
			if (flexSize.height == 'auto') {
				n.style.height = '';
				if (head)
					head.style.height = '';
			} else {
				return super.setFlexSize_(flexSize, isFlexMin);
			}
		}
		if (flexSize.width !== undefined) {
			if (flexSize.width == 'auto') {
				if (this._hflex != 'min')
					n.style.width = '';
				if (head)
					head.style.width = '';
			} else {
				return super.setFlexSize_(flexSize, isFlexMin);
			}
		}
	}

	/* set the height. */
	_setHgh(hgh?: string): void {
		var n = this.$n_(),
			ebody = this.ebody!,
			ebodyStyle = ebody.style;
		if (this.isVflex() || (hgh && hgh != 'auto' && !hgh.includes('%'))) {
			if (zk.webkit && ebodyStyle.height == jq.px(this._vflexSize()))
				return; // Bug ZK-417, ignore to set the same size
			ebodyStyle.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize();
			if (h < 0)
				h = 0;
			if (this._vflex != 'min')
				ebodyStyle.height = `${h}px`;
		} else {
			//Bug 1556099
			ebodyStyle.height = '';
			n.style.height = hgh!;
		}
	}

	_ignoreHghExt(): boolean {
		return false;
	}

	/** Calculates the size. */
	_calcSize(): void {
		this._beforeCalcSize();
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//note: we don't solve this bug for paging yet
		var n = this.$n_(),
			sizedByContent = this.isSizedByContent(),
			ehead = this.ehead,
			ebodyrows = this.ebodyrows,
			efoot = this.efoot,
			efootrows = this.efootrows;

		if (ehead) {
			if (sizedByContent && ebodyrows)
				this._adjHeadWd();
		} else if (efoot) {
			if (efootrows && ebodyrows)
				_cpCellWd(this);
		}

		//check if need to span width
		this._adjSpanWd();
		// no header case
		_fixBodyMinWd(this, true);

		// B50-ZK-543, B50-ZK-773
		// should re-calculate height because
		// the string height maybe changed after width changed.
		if (sizedByContent
			&& this.getRows && this.getRows() > 1
			&& (typeof this._calcHgh == 'function')
			&& this.ebody!.style.height) { // check only if height exists for F50-3000339.zul
			this._calcHgh(); // recalculate height again ZK-796
		}

		n._lastsz = { height: n.offsetHeight, width: n.offsetWidth }; // cache for the dirty resizing.

		this._afterCalcSize();
	}

	_beforeCalcSize(): void {
		var ebody = this.ebody!;
		if (!this._nativebar && (ebody.scrollLeft || ebody.scrollTop)) {
			// ZK-2046: Keep ebody scroll position before calculated size, _setHgh would reset it to 0.
			this._ebodyScrollPos = { l: ebody.scrollLeft, t: ebody.scrollTop };
		}
		this._calcHgh();
	}

	_afterCalcSize(): void {
		// Fix B96-ZK-5247
		if (!this.isRealVisible()) return;

		var isCSSFlex = this._cssflex && this.isChildrenFlex();
		if (this._ebodyScrollPos) {
			// ZK-2046: Restore ebody scroll position after calculated size.
			this.ebody!.scrollLeft = this._ebodyScrollPos.l;
			this.ebody!.scrollTop = this._ebodyScrollPos.t;
			this._ebodyScrollPos = undefined;
		}
		// Set style width to table to avoid colgroup width not working
		// because of width attribute (width="100%") on table
		var allWidths = this._isAllWidths(),
			head = this.head,
			ebody = this.ebody,
			ebodyrows = this.ebodyrows,
			hdfakerbar = head ? head.$n('hdfaker-bar') : undefined,
			hdbar = head ? jq(this).find('.' + head.$s('bar'))[0] : undefined,
			ftfakerbar = this.eftfaker ? this.head!.$n('ftfaker-bar') : undefined,
			scrollbarWidth = jq.scrollbarWidth(),
			hasVScroll = zk(ebody).hasVScroll(),
			hasHScroll = zk(ebody).hasHScroll();

		if (hasVScroll) {
			if (hdfakerbar) {
				if (isCSSFlex && hdbar)
					hdbar.style.flex = `0 1 ${scrollbarWidth}px`;
				else
					hdfakerbar.style.width = `${scrollbarWidth}px`;
			}
			if (ftfakerbar)
				ftfakerbar.style.width = `${scrollbarWidth}px`;
		} else {
			var zero = MeshWidget.WIDTH0;
			//refix B70-ZK-2114: remove hdfakerbar when there is no native scrollbar
			if (hdfakerbar) {
				if (isCSSFlex && hdbar)
					hdbar.style.flex = '0 1 ' + zero + 'px';
				else
					hdfakerbar.style.width = zero;
			}
			if (ftfakerbar)
				ftfakerbar.style.width = zero;
		}
		if (hasHScroll && ebodyrows && this._vflex == 'min') {
			var ehead = this.ehead,
				efoot = this.efoot,
				hgh = jq(ebodyrows).height()!;
			hgh += scrollbarWidth;
			ebody!.style.height = jq.px0(hgh);
			if (ehead) {
				hgh += ehead.offsetHeight;
			}
			if (efoot) {
				hgh += efoot.offsetHeight;
			}
			this.$n_().style.height = jq.px0(hgh);
		}
		if (isCSSFlex) {
			//update
			head!.afterChildrenFlex_();
			return;
		}
		if (allWidths) {
			var hdtbl = this.eheadtbl,
				bdtbl = this.ebodytbl,
				fttbl = this.efoottbl;

			if (hdtbl) {
				var wd = 0;
				for (var w = this.ehdfaker!.firstChild; w; w = w.nextSibling) {
					const n = w as HTMLElement;
					if (n.style.display != 'none' && !n.id.endsWith('hdfaker-bar')) // B70-ZK-2307 and B70-ZK-2358
						wd += zk.parseInt(n.style.width);
				}
				if (wd > 0) { //ZK-2772, ZK-2903: only when hdfaker has width, set back to table
					//ZK-3938: only adjust width in Chrome (ZK-4219: and safari), but zk.chrome returns true in Edge, we need to check !zk.edge
					hdtbl.style.width = (hdfakerbar && hasVScroll && (zk.chrome || zk.safari) && !zk.edge_legacy) ? `${wd + scrollbarWidth}px` : `${wd}px`;
					if (bdtbl)
						bdtbl.style.width = `${wd}px`;
					if (fttbl)
						fttbl.style.width = `${wd}px`;
				} else {
					var hideTable = false;
					for (var header = this.head!.firstChild; header; header = header.nextSibling) {
						if (header.isVisible()) {
							hideTable = false;
							break;
						}
						hideTable = true;
					}
					if (hideTable) {
						hdtbl.style.visibility = 'hidden';
						if (bdtbl)
							bdtbl.style.visibility = 'hidden';
						if (fttbl)
							fttbl.style.visibility = 'hidden';
					}
				}
			}
		} else if (this.frozen) {
			//B70-ZK-2468: should sync ebody width with ebodytbl width
			var ehead = this.ehead,
				hdtbl = this.eheadtbl,
				ebody = this.ebody,
				bdtbl = this.ebodytbl,
				efoot = this.efoot,
				fttbl = this.efoottbl;
			if (ehead && hdtbl)
				hdtbl.style.width = jq.px0(ehead.clientWidth);
			if (ebody && bdtbl)
				bdtbl.style.width = jq.px0(ebody.clientWidth);
			if (efoot && fttbl)
				fttbl.style.width = jq.px0(efoot.clientWidth);

			if (!this.frozen._smooth)
				this._syncFaker();
		}
	}

	_syncFaker(): void {
		var head = this.head;
		if (!head || !this.desktop)
			return;

		var totalCols = head.nChildren,
			width0 = zul.mesh.MeshWidget.WIDTH0,
			newTotalWidth = this.ebodytbl!.clientWidth,
			oldTotalWidth = 0,
			epsilon = 1;
		for (var i = 0, header = head.firstChild!, hdcol = this.ehdfaker!.firstChild; i < totalCols;
			header = header.nextSibling!, hdcol = hdcol!.nextSibling, i++) {
			var fakerStyleWidth = (hdcol as HTMLElement).style.width;
			if (fakerStyleWidth == '')
				return;

			var isHidden = fakerStyleWidth == width0,
				width = isHidden ? parseFloat(header._origWd!) : parseFloat(fakerStyleWidth);
			if (header.getWidth() || header.getHflex())
				newTotalWidth -= width;
			else
				oldTotalWidth += width;
		}

		if (Math.abs(newTotalWidth - oldTotalWidth) < epsilon)
			return;

		for (var i = 0, header = head.firstChild!, hdcol = this.ehdfaker!.firstChild; i < totalCols;
			header = header.nextSibling!, hdcol = hdcol!.nextSibling, i++) {
			if (header.getWidth() || header.getHflex())
				continue;

			var fakerStyleWidth = (hdcol as HTMLElement).style.width,
				multiplier = newTotalWidth / oldTotalWidth,
				isHidden = fakerStyleWidth == width0;
			if (isHidden)
				header._origWd = jq.px0(parseFloat(header._origWd!) * multiplier);
			else {
				var newStyleWidth = jq.px0(parseFloat(fakerStyleWidth) * multiplier),
					bdfaker = header.$n('bdfaker'),
					ftfaker = header.$n('ftfaker');
				(hdcol as HTMLElement).style.width = newStyleWidth;
				if (bdfaker)
					bdfaker.style.width = newStyleWidth;
				if (ftfaker)
					ftfaker.style.width = newStyleWidth;
			}
		}
	}

	//return if all widths of columns are fixed (directly or indirectly)
	_isAllWidths(): boolean {
		// ZK-2157: should skip if the mesh has no children
		if (this.isSizedByContent() && this.ebodyrows && this.ebodyrows.firstChild)
			return true;
		if (!this.head)
			return false;
		for (var w = this.head.firstChild; w; w = w.nextSibling) {
			if ((w._width == undefined || w._width.indexOf('px') <= 0)
				&& (w._hflex != 'min' || w._hflexsz === undefined)
				&& w.isVisible()) {
				return false;
			}
		}
		return true;
	}

	domFaker_(out: string[], fakeId: string): void { //used by redraw
		var head = this.head!;
		out.push('<colgroup id="', head.uuid, fakeId, '">');
		for (var w = head.firstChild; w; w = w.nextSibling) {
			var wd = MeshWidget._getWidth(w, w._hflexWidth ? `${w._hflexWidth}px` : w.getWidth()),
				visibility = w.isVisible() ? '' : 'visibility: collapse;';
			// B70-ZK-2036: Style width should end with 'px'.
			wd = wd != null ? 'width: ' + wd + ';' : '';
			out.push('<col id="', w.uuid, fakeId, '" style="', wd, visibility, '"></col>');
		}
		if (fakeId.indexOf('hd') > 0 || fakeId.indexOf('ft') > 0)
			out.push('<col id="', head.uuid, fakeId, '-bar" style="width: 0px" ></col>');
		out.push('</colgroup>');
	}

	//super//
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);

		if (child instanceof this.getHeadWidgetClass()) {
			this.head = child;
			this._minWd = undefined;
			// TODO: remove the type assertion below. Should be unnecessary, but tsc infers it
			// to be `never` which is not wrong.
		} else if (!(child instanceof zul.mesh.Auxhead))
			return;

		var nsib = child.nextSibling;
		if (nsib)
			for (var hds = this.heads, j = 0, len = hds.length; j < len; ++j)
				if (hds[j] == nsib) {
					hds.splice(j, 0, child);
					return; //done
				}
		this.heads.push(child);
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

		if (child == this.head) { // If true, child is guaranteed to be a HeadWidget
			this._minWd = this.head = undefined;
			this.heads.$remove(child as zul.mesh.HeadWidget);
		} else if (child instanceof zul.mesh.Auxhead)
			this.heads.$remove(child);
		else if (child instanceof zul.mesh.Frozen)
			this.efrozen = undefined;
	}

	//bug# 3022669: listbox hflex="min" sizedByContent="true" not work
	override beforeMinFlex_(orient: zk.FlexOrient): number | undefined {
		if (this._hflexsz === undefined && orient == 'w' && this._width === undefined) {
			if (this.isSizedByContent())
				this._calcSize();
			if (this.head) {
				this._fixHeaders(true);/* B50-3315594.zul */
				for (var w = this.head.firstChild; w; w = w.nextSibling)
					if (w._hflex == 'min' && w._hflexsz === undefined) //header hflex="min" not done yet!
						return undefined;
			}
			_fixBodyMinWd(this); // sized by content without header
			return _getMinWd(this); //grid.invalidate() with hflex="min" must maintain the width
		}
		return undefined;
	}

	// fixed for B50-3315594.zul
	override beforeParentMinFlex_(orient: zk.FlexOrient): void {
		if (orient == 'w') {
			if (this.isSizedByContent())
				this._calcSize();
			if (this.head)
				this._fixHeaders();
		} else
			this._calcSize();
	}

	override clearCachedSize_(): void {
		super.clearCachedSize_();
		this._clearCachedSize();

		var tr: HTMLTableRowElement | undefined;
		if (!this.ebdfaker && (tr = _getSigRow(this))) { //empty head case
			for (var cells = tr.cells, i = cells.length; i--;)
				cells[i].style.width = '';
		}
		var head = this.getHeadWidget();
		if (head) {
			for (var w = head.firstChild; w; w = w.nextSibling)
				delete w._hflexsz;
		}
	}

	_clearCachedSize(): void {
		const n = this.$n();
		if (n)
			n._lastsz = this._minWd = undefined;
	}

	_calcMinWds(): MeshWidth { //used in HeaderWidgets
		if (!this._minWd)
			this._minWd = _calcMinWd(this);
		return this._minWd;
	}

	_adjSpanWd(): void { //used in HeadWidgets
		if (!this._isAllWidths() || !this.isSpan())
			return;
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker;
		if (!hdfaker || !bdfaker)
			return;

		var head = this.head!.$n();
		if (!head)
			return;
		this._calcMinWds();
		var wd: number,
			wds: number[] = [],
			width = 0,
			hdcol = hdfaker.firstChild,
			bdcol = bdfaker.firstChild,
			_minwds = this._minWd!.wds,
			hdlen = this.head!.nChildren;

		for (var temphdcol = hdcol, w = this.head!.firstChild, i = 0; w; w = w.nextSibling, i++) {
			if (zk(temphdcol).isVisible(true)) {
				var wdh = w._width;

				if (w._hflex == 'min')
					wd = wds[i] = _minwds[i];
				else if (wdh?.endsWith('px'))
					wd = wds[i] = zk.parseInt(wdh);
				else
					wd = wds[i] = _minwds[i];

				width += wd;
			}
			temphdcol = temphdcol!.nextSibling;
		}

		var ftcol = ftfaker?.firstChild,
			total = this.ebody!.clientWidth,
			extSum = total - width,
			count = total,
			visj = -1,
			tblWidth = 0; //refix B70-ZK-2394: should sync colgroup width with table width

		//span to all columns
		if (this._nspan! < 0) {


			var hasFrozenScrolled = this.frozen?.getStart();
			for (var i = 0; hdcol && i < hdlen; hdcol = hdcol.nextSibling, i++) {
				// ZK-2222: should check visibility
				if (!zk(hdcol).isVisible(true)) {
					bdcol = bdcol!.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
					continue;
				} else {

					// for bug ZK-2772, we don't span it when frozen column has scrolled.
					// Instead, we use its faker width.
					if (hasFrozenScrolled) {
						if (extSum <= 0) {
							wds[i] = wd = wds[i];
						} else {
							if ((hdcol as HTMLElement).style.width) {
								wds[i] = wd = zk.parseInt((hdcol as HTMLElement).style.width);
							} else {
								wds[i] = wd = Math.round(((wds[i] * total / width) + 0.5) || 0);
							}
						}
					} else {
						wds[i] = wd = extSum <= 0 ? wds[i] : Math.round(((wds[i] * total / width) + 0.5) || 0);
					}
					var stylew = jq.px0(wd);
					count -= wd;
					visj = i;

					(hdcol as HTMLElement).style.width = stylew;
					(bdcol as HTMLElement).style.width = stylew;
					tblWidth += wd; //refix B70-ZK-2394: store each col's width
					bdcol = bdcol!.nextSibling;

					if (ftcol) {
						(ftcol as HTMLElement).style.width = stylew;
						ftcol = ftcol.nextSibling;
					}
				}
			}
			//compensate calc error but excluding scrolled frozen column
			if (extSum > 0 && count != 0 && visj >= 0) {
				tblWidth -= wd!; //refix B70-ZK-2394: subtract the last wd (visj is the last)
				wd = wds[visj] + count;
				var stylew = jq.px0(wd);

				tblWidth += wd; //refix B70-ZK-2394: and add new wd

				if (!hasFrozenScrolled) {
					(bdfaker.childNodes[visj] as HTMLElement).style.width = stylew;
					(hdfaker.childNodes[visj] as HTMLElement).style.width = stylew;

					if (ftfaker)
						(ftfaker.childNodes[visj] as HTMLElement).style.width = stylew;
				}
			}
		} else { //feature#3184415: span to a specific column
			visj = this._nspan! - 1;
			for (var i = 0; hdcol && i < hdlen; hdcol = hdcol.nextSibling, i++) {
				if (!zk(hdcol).isVisible(true)) {
					bdcol = bdcol!.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
					continue;
				} else {
					wd = visj == i && extSum > 0 ? (wds[visj] + extSum) : wds[i];
					var stylew = jq.px0(wd);
					(hdcol as HTMLElement).style.width = stylew;
					(bdcol as HTMLElement).style.width = stylew;
					tblWidth += wd; //refix B70-ZK-2394: store each col's width
					bdcol = bdcol!.nextSibling;
					if (ftcol) {
						(ftcol as HTMLElement).style.width = stylew;
						ftcol = ftcol.nextSibling;
					}
				}
			}
		}

		//refix B70-ZK-2394: sync colgroup width with (head, body, foot)table width
		var allWidths = this._isAllWidths();
		if (allWidths) {
			var hdtbl = this.eheadtbl,
				bdtbl = this.ebodytbl,
				fttbl = this.efoottbl;

			if (hdtbl) {
				hdtbl.style.width = `${tblWidth}px`;
				if (bdtbl)
					bdtbl.style.width = `${tblWidth}px`;
				if (fttbl)
					fttbl.style.width = `${tblWidth}px`;
			}
		}

		//bug 3188738: Opera only. Grid/Listbox/Tree span="x" not working
		if (zk.opera)
			zk(this.$n()).redoCSS();
	}

	_adjHeadWd(): void {
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker;

		if (!hdfaker || !bdfaker || !this.getBodyWidgetIterator().hasNext())
			return;

		var hdtable = this.eheadtbl!,
			head = this.head!.$n();

		if (!head)
			return;

		// Bug #1886788 the size of these table must be specified a fixed size.
		var ebody = this.ebody!,
			bdtable = this.ebodytbl!,
			bdwd = ebody.offsetWidth,
			total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth),
			tblwd = Math.min(bdwd, bdtable.offsetWidth);

		if (total == bdwd && bdwd > tblwd && bdwd - tblwd < 20)
			total = tblwd;

		var minWd = this._calcMinWds(),
			wds = minWd.wds,
			width = minWd.width,
			hdcol = hdfaker.firstChild,
			bdcol = bdfaker.firstChild,
			ftcol = ftfaker ? ftfaker.firstChild : undefined,
			hwgt = this.head!.firstChild,
			notVisibleWidth = MeshWidget.WIDTH0;

		for (var i = 0; hwgt; hwgt = hwgt.nextSibling, i++) {
			if (hwgt.isVisible()) {
				// sizedByContent shall not override column width
				if (hwgt._width || wds[i] == 0) {
					if (wds[i] == 0) {
						(hdcol as HTMLElement).style.width = notVisibleWidth;
						(bdcol as HTMLElement).style.width = notVisibleWidth;
						if (ftcol)
							(ftcol as HTMLElement).style.width = notVisibleWidth;
					}
					hdcol = hdcol!.nextSibling;
					bdcol = bdcol!.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
				} else if (!this.frozen || !this.frozen.getStart() || (hdcol as HTMLElement).style.width === '') {
					var wd = jq.px(wds[i]);
					(hdcol as HTMLElement).style.width = (bdcol as HTMLElement).style.width = wd;
					hdcol = hdcol!.nextSibling;
					bdcol = bdcol!.nextSibling;
					if (ftcol) {
						(ftcol as HTMLElement).style.width = wd;
						ftcol = ftcol.nextSibling;
					}
				}
			} else {
				(hdcol as HTMLElement).style.width = notVisibleWidth;
				(bdcol as HTMLElement).style.width = notVisibleWidth;
				hdcol = hdcol!.nextSibling;
				bdcol = bdcol!.nextSibling;
				if (ftcol) {
					(ftcol as HTMLElement).style.width = notVisibleWidth;
					ftcol = ftcol.nextSibling;
				}
			}
		}

		hdtable.style.width = jq.px(width);
		bdtable.style.width = jq.px(width);
		if (ftfaker)
			this.efoottbl!.style.width = jq.px(width);

		_adjMinWd(this);
	}

	_getFirstRowCells(tbody: HTMLTableSectionElement | undefined): HTMLCollectionOf<HTMLTableCellElement> | undefined {
		var rowsLength: number,
			tbodyrows: HTMLCollectionOf<HTMLTableRowElement>;
		if (tbody && (tbodyrows = tbody.rows) && (rowsLength = tbodyrows.length)) {
			var cells = tbodyrows[0].cells,
				length = cells.length,
				ncols = 0;
			//ZK-2752: find the first visible row
			//keep the same behavior even there isn't any row visible to avoid other side effects
			if (tbody.offsetHeight > 0) {
				for (var i = 0; i < rowsLength; i++) {
					if (jq(tbodyrows[i]).css('display') != 'none') {
						cells = tbodyrows[i].cells;
						length = cells.length;
						break;
					}
				}
			}
			for (var i = 0; i < length; i++) {
				var span = cells[i].colSpan;
				ncols += span != 1 ? span : 1;
			}
			if (ncols == length)
				return cells;
			else {
				var out: string | undefined = '';
				out += '<tr id="' + tbody.id + '-fakeRow" style="visibility:hidden;height:0">';
				for (var i = 0; i < ncols; i++)
					out += '<td></td>';
				out += '</tr>';
				jq(tbodyrows[0]).before(out);
				out = undefined;
				return tbodyrows[0].cells;
			}
		}
	}

	_deleteFakeRow(tbody: HTMLTableSectionElement | undefined): void {
		if (tbody)
			jq('#' + tbody.id + '-fakeRow').remove();
	} // for Grid.js and Listbox.js

	refreshBar_(showBar?: boolean, scrollToTop?: boolean): void {
		var bar = this._scrollbar;
		if (bar) {
			// ZK-355: Keep scroll position before sync scrollbar size
			var currentLeft = this._currentLeft,
				currentTop = this._currentTop;
			bar.syncSize(showBar || this._shallShowScrollbar);
			delete this._shallShowScrollbar; // use undefined rather false
			if (scrollToTop)
				bar.scrollTo(0, 0);
			else
				bar.scrollTo(currentLeft, currentTop);
			//sync frozen
			var frozen = this.frozen,
				start: number;
			if (frozen && (start = frozen._start) != 0) {
				frozen._doScrollNow(start);
				bar.setBarPosition(start);
			}
			this._afterCalcSize(); // for ZK-2370, we need to check the faker-bar again
		}
	}

	onFitSize(): void {
		// B50-ZK-598: when having rows, height needs to be determined when onFitSize
		if (this._rows)
			this._calcHgh();
	}

	_calcHgh(): void {
		var rows: ArrayLike<HTMLTableRowElement> = this.ebodyrows ? this.ebodyrows.rows : [],
			n = this.$n_(),
			hgh: string | number = n.style.height,
			isHgh = hgh && hgh != 'auto' && !hgh.includes('%');
		if (isHgh) {
			hgh = zk.parseInt(hgh) - zk(n).padBorderHeight();
			if (hgh) {
				hgh -= this._headHgh(0);
				if (hgh < 0) hgh = 0;
				var sz = 0;
				l_out:
				for (var h, j = 0, rl = rows.length; j < rl; ++sz, ++j) {
					//next visible row
					var r: HTMLTableRowElement;
					for (; ; ++j) {//no need to check length again
						if (j >= rl) break l_out;
						r = rows[j];
						if (zk(r).isVisible()) break;
					}

					var $r = zk(r);
					h = $r.offsetTop() + $r.offsetHeight();
					if (h >= hgh) {
						if (h > hgh + 2) ++sz; //experimental
						break;
					}
				}
				sz = Math.ceil(sz && h ? (hgh * sz) / h : hgh / this._headHgh(20));
				this._setOrGetVisibleRows(sz);
				hgh -= (this.efoot ? this.efoot.offsetHeight : 0);
				//bug# 3036398: frozen scrollbar disappear when listbox with vflex="1"
				hgh -= (this.efrozen && this._nativebar ? this.efrozen.offsetHeight : 0);
				this.ebody!.style.height = `${Math.max(hgh, 0)}px`;
				return; //done
			}
		}

		var nVisiRows = 0,
			nRows = this.getRows(),
			lastVisiRow: HTMLTableRowElement | undefined,
			firstVisiRow: HTMLTableRowElement | undefined,
			midVisiRow: HTMLTableRowElement | undefined;
		for (var j = 0, rl = rows.length; j < rl; ++j) { //tree might collapse some items
			var r = rows[j];
			if (zk(r).isVisible()) {
				++nVisiRows;
				if (!firstVisiRow)
					firstVisiRow = r;

				if (nRows === nVisiRows) {
					midVisiRow = r;
					break;
					//nVisiRows and lastVisiRow useful only if nRows is larger,
					//so ok to break here
				}
				lastVisiRow = r;
			}
		}

		hgh = 0;
		var diff = 2;/*experiment*/
		if (!nRows) {
			if (this.isVflex()) {
				// Since ie6 support was dropped, the argument of _vflexSize is not used.
				// Some time later, the argument of _vflexSize is dropped from the signature,
				// but `this._vflexSize(n.style.height)` wasn't cleaned up until now.
				hgh = this._vflexSize();

				if (hgh < 25) hgh = 25;

				var rowhgh = firstVisiRow ? zk(firstVisiRow).offsetHeight() : undefined;
				if (!rowhgh)
					rowhgh = this._headHgh(20);

				nRows = Math.round((hgh - diff) / rowhgh);
			}
			this._setOrGetVisibleRows(nRows);
		}

		if (nRows) {
			if (!hgh) {
				if (!nVisiRows) {
					hgh = this._headHgh(20, true) * nRows;
				} else if (nRows <= nVisiRows) {
					var $midVisiRow = zk(midVisiRow);
					hgh = $midVisiRow.offsetTop() + $midVisiRow.offsetHeight();
				} else {
					var $lastVisiRow = zk(lastVisiRow);
					hgh = $lastVisiRow.offsetTop() + $lastVisiRow.offsetHeight();
					hgh = Math.ceil((nRows * hgh) / nVisiRows);
				}
			}
			this.ebody!.style.height = `${hgh}px`;
		} else {
			this.ebody!.style.height = '';
			var focusEL = this.$n('a');
			if ((this.getPagingChild()) && focusEL)
				focusEL.style.top = '0px'; // Bug ZK-1715: focus has no chance to sync if don't select item after changing page.
		}
	}

	/* Returns the real # of rows (aka., real size). */
	_setOrGetVisibleRows(v?: number): number | undefined {
		if ('number' == typeof v) {
			this._visiRows = v;
		} else
			return this.getRows() || this._visiRows || 0;
	}

	/* Height of the head row. If no header, defval is returned. */
	_headHgh(defVal: number, isExcludeAuxhead?: boolean): number {
		var headWidget = this.getHeadWidget(), //Bug ZK-1297: get head height exclude auxhead
			head = this.ehead,
			hgh = isExcludeAuxhead ? (headWidget ? headWidget.$n_().offsetHeight : 0) : (head ? head.offsetHeight : 0);
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) hgh += pgit.offsetHeight;
			if (pgib) hgh += pgib.offsetHeight;
		}
		return hgh ? hgh : defVal;
	}

	/**
	 * Scroll to the specified item by the given index, used by Grid and Listbox,
	 * this function could be invoked by server-side or client-side,
	 * when invoked by client-side, scrollRatio will be undefined.
	 * @param int index the index of item
	 * @param double scrollRatio the scroll ratio
	 */
	_scrollToIndex(index: number, scrollRatio?: number): void {
		// NOTE: _scrollToIndex will only be called by Grid and Listbox, and both of them
		// implements _getFirstItemIndex and _getLastItemIndex.

		// @ts-expect-error: calling `_getFirstItemIndex` is safe in `_scrollToIndex`
		// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-call
		var firstItemIndex = this._getFirstItemIndex(),
			// @ts-expect-error: calling `_getLastItemIndex` is safe in `_scrollToIndex`
			// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-call
			lastItemIndex = this._getLastItemIndex(),
			body = this.ebody!;
		this._targetIndex = index;
		this._keepScroll = true;

		if (index >= firstItemIndex && index <= lastItemIndex) {
			var itemIterator = this.getBodyWidgetIterator();
			while (itemIterator.hasNext()) {
				var item = itemIterator.next()!;
				if (item._index == index) {
					item.$n_().scrollIntoView(true);
					this._keepScroll = false;
					this._topBoundary = this._bottomBoundary = undefined;
					return;
				}
			}
		} else if (scrollRatio != undefined) {
			body.scrollTop = body.scrollHeight * scrollRatio;
		} else if (index < firstItemIndex) {
			this._bottomBoundary = body.scrollTop;
			this._topBoundary = this._topBoundary == undefined ? 0 : this._topBoundary;
			body.scrollTop -= (body.scrollTop - this._topBoundary) / 2;
		} else if (index > lastItemIndex) {
			this._topBoundary = body.scrollTop;
			this._bottomBoundary = this._bottomBoundary == undefined ? body.scrollHeight : this._bottomBoundary;
			body.scrollTop += (this._bottomBoundary - body.scrollTop) / 2;
		}
	}

	override getContentEdgeHeight_(height: number): number {
		var height = super.getContentEdgeHeight_(height),
			efoot = this.efoot;
		if (efoot) {
			var zkefoot = zk(efoot);
			height += zkefoot.padBorderHeight() + zkefoot.sumStyles('tb', jq.margins);
		}
		return height;
	}

	override afterChildrenMinFlex_(o: zk.FlexOrient): void {
		var n = this.$n_();
		if (o == 'h') {
			n.style.height = jq.px0(Math.ceil(this._vflexsz!));
		}
	}

	//css flex
	isChildrenFlex(): boolean {
		if (this.head && this.ehead) {
			for (var i = this.heads.length; i-- > 0;) {
				var header = this.heads[i];
				for (var w = header.firstChild; w; w = w.nextSibling) {
					if (w._hflex && w._hflex != 'min') {
						return true;
					}
				}
			}
		}
		return false;
	}

	waitForRendered_(): Promise<void> {
		return new Promise(resolve => {
			const callback = (): void => {
				if (!this._pendOnRender) return resolve();
				setTimeout(callback);
			};
			callback();
		});
	}

	// ZK-5028 for Treecols, Listhead, and Columns
	//@Override
	override shallFireSizedLaterWhenAddChd_(): boolean {
		if (this._rerendering) {
			zWatch.listen({
				onResponse: [this, this._fixFireSizedLaterWhenAddChd]
			});
			return true;
		}
		return false;
	}

	// ZK-5028 for Treecols, Listhead, and Columns
	_fixFireSizedLaterWhenAddChd(): void {
		zUtl.fireSized(this);
		zWatch.unlisten({
			onResponse: [this, this._fixFireSizedLaterWhenAddChd]
		});
	}

	static readonly WIDTH0 = zk.webkit ? '0.001px' : '0px';

	static _getWidth(wgt: zk.Widget, width: string | undefined): string | undefined {
		if (wgt.isVisible())
			return width;
		return this.WIDTH0;
	}
}

/** @class zul.mesh.Scrollbar
 * @import zk.Widget
 * The extra Scrollbar for the MeshWidget.
 * @since 6.5.0
 */
export var Scrollbar = {
	/**
	 * Initialize the scrollbar of MeshWidget.
	 * @param zk.Widget wgt a widget
	 */
	init(wgt: zul.mesh.MeshWidget): zul.Scrollbar | undefined /* zkmax-override could return undefined */ {
		var embed = jq(wgt.$n_()).data('embedscrollbar') !== false, // change default value to true since 7.0.2
			frozen = wgt.frozen,
			startPositionX = 0;

		if (frozen) {
			var columns = frozen.getColumns()!;
			if (wgt.eheadtbl) {
				var cells = wgt._getFirstRowCells(wgt.eheadrows);
				if (cells) {
					for (var i = 0; i < columns; i++)
						startPositionX += cells[i].offsetWidth;
				}
				wgt._deleteFakeRow(wgt.eheadrows);
			}
		}
		return new zul.Scrollbar(wgt.ebody, wgt.ebodytbl, {
			embed: embed,
			startPositionX: startPositionX,
			onSyncPosition: function () {
				if (!this.frozen) {
					var pos = this.getCurrentPosition(),
						head = wgt.ehead,
						foot = wgt.efoot;
					if (pos && this.hasHScroll()) {
						if (head)
							head.scrollLeft = pos.x;
						if (foot)
							foot.scrollLeft = pos.x;
					}
				}
			},
			onScrollEnd: function () {
				wgt._doScroll();
			}
		});
	},
	/**
	 * Return the vertical scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 */
	getScrollPosV(wgt: zul.mesh.MeshWidget): number {
		var bar = wgt._scrollbar;
		if (bar)
			return bar.getCurrentPosition()!.y;

		return wgt.ebody!.scrollTop;
	},
	/**
	 * Return the horizontal scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 * @since 7.0.0
	 */
	getScrollPosH(wgt: zul.mesh.MeshWidget): number {
		var bar = wgt._scrollbar;
		if (bar)
			return bar.getCurrentPosition()!.x;

		return wgt.ebody!.scrollLeft;
	}
};
zul.mesh.Scrollbar = Scrollbar;