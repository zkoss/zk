/* Frozen.ts

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
// Bug 3218078
function _onSizeLater(wgt: Frozen): void {
	var parent = wgt.parent!;

	// ZK-2130: should skip fake scroll bar
	if (parent.eheadtbl && parent._nativebar) {
		var cells = parent._getFirstRowCells(parent.eheadrows),
			head = parent.head!,
			totalcols = cells!.length - jq(head).find(head.$n_('bar')).length,
			columns = wgt._columns!,
			leftWidth = 0;

		// FIXME: cells is assumed to exist while `totalcols` is initialized
		//B70-ZK-2553: one may specify frozen without any real column
		if (!cells || totalcols <= 0) {
			//no need to do the following computation since there is no any column
			return;
		}

		//ZK-2776: don't take hidden column, like setVisible(false), into account
		for (var header = parent.head!.firstChild; header; header = header.nextSibling) {
			if (!header.isVisible())
				totalcols -= 1;
		}
		for (var i = 0; i < columns; i++)
				leftWidth += cells[i].offsetWidth;

		parent._deleteFakeRow(parent.eheadrows);

		wgt.$n_('cave').style.width = jq.px0(leftWidth);
		var scroll = wgt.$n_('scrollX'),
			width = parent.$n_('body').offsetWidth;

		// B70-ZK-2074: Resize forzen's width as meshwidget's body.
		parent.$n_('frozen').style.width = jq.px0(width);
		width -= leftWidth;
		scroll.style.width = jq.px0(width);
		var scrollScale = totalcols - columns - 1;
		(scroll.firstChild as HTMLElement).style.width = jq.px0(width + 50 * scrollScale);
		wgt.syncScroll();
	}
}

/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel.
 * <p>Default {@link #getZclass}: z-frozen.
 */
export class Frozen extends zul.Widget {
	// Parent could be null because it's checked in `Frozen.prototype.syncScroll`.
	declare public parent: zul.mesh.MeshWidget | null;
	public _start = 0;
	public _scrollScale = 0;
	public _smooth?: boolean;
	public _columns?: number;
	public _shallSyncScale?: boolean;
	private _delayedScroll?: number | null;
	private _lastScale?: number;
	private _shallSync?: boolean;

	/**
	 * Returns the number of columns to freeze.
	 * <p>Default: 0
	 * @return int
	 */
	public getColumns(): number | undefined {
		return this._columns;
	}

	/**
	 * Sets the number of columns to freeze.(from left to right)
	 * @param int columns positive only
	 */
	public setColumns(v: number, opts?: Record<string, boolean>): this {
		const o = this._columns;
		v = (v < 0 ? 0 : v);
		this._columns = v;

		if (o !== v || (opts && opts.force)) {
			if (this._columns) {
				if (this.desktop) {
					this.onSize();
					this.syncScroll();
				}
			} else this.rerender();
		}

		return this;
	}

	/**
	 * Returns the start position of the scrollbar.
	 * <p>Default: 0
	 * @return int
	 */
	public getStart(): number {
		return this._start;
	}

	/**
	 * Sets the start position of the scrollbar.
	 * <p> Default: 0
	 * @param int start the column number
	 */
	public setStart(start: number, opts?: Record<string, boolean>): this {
		const o = this._start;
		this._start = start;

		if (o !== start || (opts && opts.force)) {
			this.syncScroll();
		}

		return this;
	}

	/**
	 * Synchronizes the scrollbar according to {@link #getStart}.
	 */
	public syncScroll(): void {
		var p = this.parent;
		if (p && p._nativebar) {
			var scroll = this.$n('scrollX');
			if (scroll)
				scroll.scrollLeft = this._start * 50;
		}
	}

	/**
	 * Synchronizes the scrollbar according to parent ebody scrollleft.
	 */
	public syncScrollByParentBody(): void {
		var p = this.parent,
			ebody: HTMLDivElement | null | undefined,
			l: number;
		if (p && p._nativebar && (ebody = p.ebody) && (l = ebody.scrollLeft) > 0) {
			var scroll = this.$n('scrollX');
			if (scroll) {
				var scrollScale = l / (ebody.scrollWidth - ebody.clientWidth);
				scroll.scrollLeft = Math.ceil(scrollScale * (scroll.scrollWidth - scroll.clientWidth));
			}
		}
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var p = this.parent!,
			body = p.$n('body'),
			foot = p.$n('foot');

		if (p._nativebar) {
			//B70-ZK-2130: No need to reset when beforeSize, ZK-343 with native bar works fine too.
			zWatch.listen({onSize: this});
			var scroll = this.$n_('scrollX'),
				scrollbarWidth = jq.scrollbarWidth();
			// ZK-2583: native IE bug, add 1px in scroll div's height for workaround
			this.$n_().style.height = this.$n_('cave').style.height = this.$n_('right').style.height = scroll.style.height
					= (scroll.firstChild as HTMLElement).style.height = jq.px0(zk.ie ? scrollbarWidth + 1 : scrollbarWidth);
			p._currentLeft = 0;
			this.domListen_(scroll, 'onScroll');

			var head = p.$n('head');
			if (head)
				this.domListen_(head, 'onScroll', '_doHeadScroll');

		} else {
			// Bug ZK-2264
			this._shallSyncScale = true;
		}
		// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
		zWatch.listen({onResponse: this});
		if (body)
			jq(body).addClass('z-word-nowrap');
		if (foot)
			jq(foot).addClass('z-word-nowrap');
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var p = this.parent!,
			body = p.$n('body'),
			foot = p.$n('foot'),
			head = p.$n('head');

		if (p._nativebar) {
			this.domUnlisten_(this.$n_('scrollX'), 'onScroll');
			p.unlisten({onScroll: this.proxy(this._onScroll)});
			zWatch.unlisten({onSize: this});

			if (head)
				this.domUnlisten_(head, 'onScroll', '_doHeadScroll');
		} else {
			this._shallSyncScale = false;
		}
		// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
		zWatch.unlisten({onResponse: this});
		if (body)
			jq(body).removeClass('z-word-nowrap');
		if (foot)
			jq(foot).removeClass('z-word-nowrap');
		super.unbind_(skipper, after, keepRod);
	}

	// Bug ZK-2264, we should resync the variable of _scrollScale, which do the same as HeadWidget.js
	public onResponse(): void {
		if (this.parent!._nativebar) {
			// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
			this._syncFrozenNow();
		} else if (this._shallSyncScale) {
			var hdfaker = this.parent!.ehdfaker;
			if (hdfaker) {
				this._scrollScale = hdfaker.childNodes.length - this._columns! - 1;
			}
			this._shallSyncScale = false;
		}
	}

	public override onSize(): void {
		if (!this._columns)
			return;
		var self = this;
		self._syncFrozen(); // B65-ZK-1470

		//B70-ZK-2129: prevent height changed by scrolling
		var p = this.parent!,
			phead = p.head,
			firstHdcell: HTMLElement | null;
		if (p._nativebar && phead) {
			interface HTMLElementWithOptionalCells extends HTMLElement {
				cells?: HTMLTableRowElement['cells'];
			}
			//B70-ZK-2558: frozen will onSize before other columns,
			//so there might be no any column in the beginning
			var n = phead.$n() as HTMLElementWithOptionalCells | null | undefined;
			firstHdcell = n ? (n.cells ? n.cells[0] : null) : null;
			//B70-ZK-2463: if firstHdcell is not undefined
			if (firstHdcell) {
				const fhcs = firstHdcell.style;
				if (!fhcs.height)
					fhcs.height = firstHdcell.offsetHeight + 'px';
			}
		}

		// Bug 3218078, to do the sizing after the 'setAttr' command
		setTimeout(function () {
			_onSizeLater(self);
			self._syncFrozenNow();
		});
	}

	public _syncFrozen(): void { //called by Rows, HeadWidget...
		this._shallSync = true;
	}

	private _syncFrozenNow(): void {
		var num = this._start;
		if (this._shallSync && num)
			this._doScrollNow(num, true);

		this._shallSync = false;
	}

	public override beforeParentChanged_(p: zk.Widget | null): void {
		//bug B50-ZK-238
		//ZK-2651: JS Error showed when clear grid children component that include frozen
		if (this.desktop && this._lastScale) //if large then 0
			this._doScroll(0);

		super.beforeParentChanged_(p);
	}

	private _onScroll(evt: zk.Event): void {
		if (!evt.data || !zk.currentFocus)
			return;

		var p = this.parent,
			td: HTMLTableCellElement | undefined,
			frozen = this,
			fn = function (): void { // p shouldn't be null when fn is called
				var cf = zk.currentFocus;
				if (cf) {
					td = p!.getFocusCell(cf.$n_());
					var index: number;
					if (td && (index = td.cellIndex - frozen._columns!) >= 0) {
						frozen.setStart(index);
						p!.ebody!.scrollLeft = 0;

						if (p!.ehead)
							p!.ehead.scrollLeft = 0;
					}
				}
			};
		if (p) {
			if (zk.ie < 11)
				setTimeout(fn, 0);
			else
				fn();
		}
		evt.stop();
	}

	public _doHeadScroll(evt: zk.Event): void {
		var head = evt.domTarget!,
			num = Math.ceil(head.scrollLeft / 50);
		// ignore scrollLeft is 0
		if (!head.scrollLeft || this._lastScale == num)
			return;
		evt.data = head.scrollLeft;
		this._onScroll(evt);
	}

	public _doScroll(n: number): void {
		var p = this.parent!,
			num: number;
		if (p._nativebar)
			num = Math.ceil(this.$n_('scrollX').scrollLeft / 50);
		else
			num = Math.ceil(n);
		if (this._lastScale == num)
			return;
		var self = this;
		if (this._delayedScroll) {
			clearTimeout(this._delayedScroll);
		}
		this._delayedScroll = setTimeout(function () {
			self._lastScale = num;
			self._doScrollNow(num);
			self.smartUpdate('start', num);
			self._start = num;
			self._delayedScroll = null;
		}, 0);
	}

	public _doScrollNow(num: number, force?: boolean): void {
		var totalWidth = 0,
			mesh = this.parent!,
			cnt = num,
			c = this._columns!,
			width0 = zul.mesh.MeshWidget.WIDTH0,
			hasVScroll = zk(mesh.ebody).hasVScroll(),
			scrollbarWidth = hasVScroll ? jq.scrollbarWidth() : 0;
		if (mesh.head) {
			// set fixed size
			var totalCols = mesh.head.nChildren,
				// B70-ZK-2071: Use mesh.head to get columns.
				hdcells = (mesh.head.$n_() as HTMLTableRowElement).cells,
				hdcol = mesh.ehdfaker!.firstChild,
				ftrows = mesh.foot ? mesh.efootrows : null,
				ftcells = ftrows ? ftrows.rows[0].cells : null;

			for (var faker: HTMLElement | null | undefined, i = 0; hdcol && i < totalCols; hdcol = hdcol.nextSibling, i++) {
				if ((hdcol as HTMLElement).style.width.indexOf('px') == -1) {
					var sw = (hdcol as HTMLElement).style.width = jq.px0(hdcells[i].offsetWidth),
						wgt = zk.Widget.$(hdcol)!;
					if (!(wgt instanceof zul.mesh.HeadWidget)) {
						if ((faker = wgt.$n('bdfaker')))
							faker.style.width = sw;
						if ((faker = wgt.$n('ftfaker')))
							faker.style.width = sw;
					}
				}
			}

			interface Update {
				node: HTMLTableCellElement;
				index: number;
				width?: string;
			}
			var updateBatch: Update[] = [];
			// B70-ZK-2071: Use mesh.head to get column.
			for (var i = c, faker: HTMLElement | null | undefined; i < totalCols; i++) {
				var n = hdcells[i],
					hdWgt = zk.Widget.$<zul.mesh.HeaderWidget>(n)!,
					isVisible = hdWgt && hdWgt.isVisible(),
					shallUpdate = false,
					cellWidth: string | undefined;

				//ZK-2776, once a column is hidden, there is an additional style
				if (!hdWgt.isVisible())
					continue; //skip column which is hide

				if (cnt-- <= 0) { //show
					var wd = isVisible ?
							(zk.ie ? Math.max(jq(n).width()!, 0) : n.offsetWidth) // Bug ZK-2690
							: 0;
					// ZK-2071: nativebar behavior should be same as fakebar
					// ZK-4762: cellWidth should update while scroll into view
					if (force || (wd < 2)) {
						cellWidth = hdWgt._origWd || jq.px(wd);
						// ZK-2772: consider faker's width first for layout consistent
						// if the column is visible.
						if ((wd > 1) && (faker = jq('#' + n.id + '-hdfaker')[0]) && faker.style.width)
							cellWidth = faker.style.width;
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (force ||
							// Bug ZK-2690
						((zk.ie ? Math.max(jq(n).width()!, 0) : n.offsetWidth) != 0)) { //hide
					faker = jq('#' + n.id + '-hdfaker')[0];
					//ZK-2776: consider faker's width first for layout consistent
					if (faker.style.width && zk.parseInt(faker.style.width) > 1)
						hdWgt._origWd = faker.style.width;
					cellWidth = width0;
					shallUpdate = true;
				}

				if (force || shallUpdate) {
					updateBatch.push({node: n, index: i, width: cellWidth});
				}
			}

			//hide the element without losing focus
			jq(mesh).css({position: 'absolute', left: -9999});

			var update: Update | undefined;
			while (update = updateBatch.shift()) {
				const n = update.node,
					cellWidth = update.width!,
					i = update.index;

				if ((faker = jq('#' + n.id + '-hdfaker')[0]))
					faker.style.width = cellWidth;
				if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
					faker.style.width = cellWidth;
				if ((faker = jq('#' + n.id + '-ftfaker')[0]))
					faker.style.width = cellWidth;
				// ZK-2071: display causes wrong in colspan case
				hdcells[i].style.width = cellWidth;
				// foot
				if (ftcells) {
					// ZK-2071: display causes wrong in colspan case
					if (ftcells.length > i)
						ftcells[i].style.width = cellWidth;
				}
			}

			hdcol = mesh.ehdfaker!.firstChild;
			for (var i = 0; hdcol && i < totalCols; hdcol = hdcol.nextSibling, i++) {
				if ((hdcol as HTMLElement).style.display != 'none')
					totalWidth += zk.parseInt((hdcol as HTMLElement).style.width);
			}
			totalWidth += scrollbarWidth;

			//hide the element without losing focus
			jq(mesh).css({position: '', left: ''});
		}
		// TODO: The following lines could be refactored with optional chaining since
		// transpilation to "if statements" is as bloat as it currently is.
		// Set style width to table to avoid colgroup width not working
		// because of width attribute (width="100%") on table
		var headtbl: HTMLTableElement | null | undefined,
			bodytbl: HTMLTableElement | null | undefined,
			foottbl: HTMLTableElement | null | undefined;
		if (headtbl = mesh.eheadtbl)
			headtbl.style.width = jq.px(totalWidth);
		if (bodytbl = mesh.ebodytbl)
			bodytbl.style.width = jq.px(totalWidth - scrollbarWidth);
		if (foottbl = mesh.efoottbl)
			foottbl.style.width = jq.px(totalWidth);

		mesh._restoreFocus();

		// Bug ZK-601, Bug ZK-1572
		if (zk.ie9_) {
			const n = mesh.$n_();
			n.className += ' ';
			if (n.offsetHeight) {
				// Empty on purpose.
				// This is a trick for IE to recalculate its size by invoking `offsetHeight` or `offsetWidth`.
			}
			n.className.trim();
		}
	}
}
zul.mesh.Frozen = zk.regClass(Frozen);