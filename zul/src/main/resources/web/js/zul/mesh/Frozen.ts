/* Frozen.ts

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
// Bug 3218078 (legacy non-smooth onSize sizing)
function _onSizeLater(wgt: Frozen): void {
	var parent = wgt.parent!;

	// ZK-2130: should skip fake scroll bar
	if (parent.eheadtbl && parent._nativebar) {
		var cells = parent._getFirstRowCells(parent.eheadrows)!,
			head = parent.head!,
			totalcols = cells.length - jq(head).find(head.$n_('bar')).length,
			columns = wgt._columns!,
			leftWidth = 0;

		//B70-ZK-2553: one may specify frozen without any real column
		if (totalcols <= 0) {
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
 * @defaultValue {@link getZclass}: z-frozen.
 */
@zk.WrapClass('zul.mesh.Frozen')
export class Frozen extends zul.Widget {
	// Parent could be null because it's checked in `Frozen.prototype.syncScroll`.
	override parent!: zul.mesh.MeshWidget | undefined;
	/** @internal */
	_start = 0;
	/** @internal */
	_scrollScale = 0;
	/** @internal ZK-6065: smooth frozen defaults on (was EE-only). */
	_smooth: boolean | undefined = true; // eslint-disable-line zk/preferStrictBooleanType
	/** @internal */
	_rightColumns = 0;
	/** @internal */
	_columns?: number;
	/** @internal */
	_shallSyncScale?: boolean;
	/** @internal */
	_delayedScroll?: number;
	/** @internal */
	_delayedSize?: number;
	/** Handle of the sizing timer the smooth onSize schedules. @internal */
	_delayedSmoothSize?: number;
	/** @internal */
	_lastScale?: number;
	/** @internal */
	_shallSync?: boolean;
	/** @internal ZK-6065: last mesh scrollLeft, used by smooth frozen. */
	_currentLeft?: number;
	/** @internal ZK-6065: guards double scroll sync (ZK-4743). */
	_scrolled?: boolean;
	/** @internal ZK-6065: first non-frozen header cells, to reset borders. */
	_firstNotFrozenCols?: HTMLElement[];

	/**
	 * @returns the number of columns to freeze.
	 * @defaultValue `0`
	 */
	getColumns(): number | undefined {
		return this._columns;
	}

	/**
	 * Sets the number of columns to freeze.(from left to right)
	 * @param columns - positive only
	 */
	setColumns(columns: number, opts?: Record<string, boolean>): this {
		const o = this._columns;
		columns = Math.max(0, columns);
		this._columns = columns;

		if (o !== columns || opts?.force) {
			if (this._columns) {
				if (this.desktop) {
					this.onSize();
					this.syncScroll();
				}
			} else this.rerender();
		}

		// ZK-6065: keep frozen column borders in sync (smooth frozen).
		if (this.desktop)
			this._syncColumnBorders();

		return this;
	}

	/**
	 * Sets whether to enable smooth (pixel-level) frozen scrolling.
	 * @param smooth - true to scroll smoothly, false for the legacy
	 * column-by-column snapping.
	 * @since 8.5.0 (available in CE since 11.0.0)
	 */
	setSmooth(smooth: boolean): this {
		this._smooth = smooth;
		return this;
	}

	/**
	 * @returns the start position of the scrollbar.
	 * @defaultValue `0`
	 */
	getStart(): number {
		return this._start;
	}

	/**
	 * Sets the start position of the scrollbar.
	 * @defaultValue `0`
	 * @param start - the column number
	 */
	setStart(start: number, opts?: Record<string, boolean>): this {
		// ZK-6065: smooth frozen tracks pixel scrollLeft, clear it on a
		// column-based setStart so syncScroll recomputes.
		if (this.desktop)
			this._currentLeft = undefined;

		const o = this._start;
		this._start = start;

		if (o !== start || opts?.force) {
			this.syncScroll();
		}

		return this;
	}

	/**
	 * @returns the number of columns to freeze on the right.
	 * @defaultValue `0`
	 * @since 8.6.2 (available in CE since 11.0.0)
	 */
	getRightColumns(): number {
		return this._rightColumns;
	}

	/**
	 * Sets the number of columns to freeze on the right.
	 * @param rightColumns - positive only
	 * @since 8.6.2 (available in CE since 11.0.0)
	 */
	setRightColumns(rightColumns: number, opts?: Record<string, boolean>): this {
		if (this._rightColumns != rightColumns || opts?.force) {
			this._rightColumns = rightColumns;
			if (this.desktop)
				this.rerender();
		}
		return this;
	}

	/**
	 * Sets the current pixel scroll offset (smooth frozen only).
	 * @since 8.5.1 (available in CE since 11.0.0)
	 */
	setCurrentLeft(currentLeft: number): this {
		this._currentLeft = currentLeft;
		if (this._smoothFrozenEnabled() && this.desktop) {
			this._updateMeshScrollLeft(this._currentLeft);
		}
		return this;
	}

	/**
	 * Synchronizes the scrollbar according to {@link getStart}.
	 */
	syncScroll(start?: number): void {
		if (!this._smoothFrozenEnabled()) {
			// legacy: column-based snapping
			if (this.parent?._nativebar) {
				var scroll = this.$n('scrollX');
				if (scroll)
					scroll.scrollLeft = this._start * 50;
			}
			return;
		}
		if (start === undefined)
			start = this._start;
		const mesh = this.parent;
		if (mesh?._nativebar) {
			const scroll = this.$n('scrollX');
			if (scroll) {
				const columns = this._columns!,
					totalScrolledColumn = start + columns,
					headRows = mesh.eheadrows,
					currentLeft = this._currentLeft,
					shouldUseCurrentLeft = currentLeft != null;
				let meshScrollLeft = 0,
					headNode: ChildNode | undefined;
				if (start && headRows && !shouldUseCurrentLeft) {
					const headRowNodes = headRows.childNodes;
					for (let i = 0; i < headRowNodes.length; i++) { // find non-auxhead
						const headWgt = zk(headRowNodes[i]).$();
						if (!(headWgt instanceof zul.mesh.Auxhead)) {
							headNode = headRowNodes[i];
							break;
						}
					}
					if (headNode) {
						const columnNodes = headNode.childNodes as NodeListOf<HTMLElement>;
						for (let i = columns; i < totalScrolledColumn; i++) {
							meshScrollLeft += columnNodes[i].offsetWidth;
						}
					}
				}
				this._updateMeshScrollLeft(shouldUseCurrentLeft ? currentLeft : meshScrollLeft);
			}
		}
	}

	/**
	 * Synchronizes the scrollbar according to parent ebody scrollleft.
	 */
	syncScrollByParentBody(): void {
		if (!this._smoothFrozenEnabled()) {
			// legacy
			var p = this.parent,
				ebody: HTMLDivElement | undefined,
				l: number;
			if (p?._nativebar && (ebody = p.ebody) && (l = ebody.scrollLeft) > 0) {
				var scroll = this.$n('scrollX');
				if (scroll) {
					var scrollScale = l / (ebody.scrollWidth - ebody.clientWidth);
					scroll.scrollLeft = Math.ceil(scrollScale * (scroll.scrollWidth - scroll.clientWidth));
				}
			}
			return;
		}
		const mesh = this.parent;
		let ebodyEl: HTMLDivElement | undefined,
			meshScrollLeft: number;
		if (mesh?._nativebar && (ebodyEl = mesh.ebody) && (meshScrollLeft = ebodyEl.scrollLeft) >= 0) {
			// ZK-5678: avoid skipping scroll sync by checking if meshScrollLeft and this._currentLeft are equal
			if (this._scrolled && meshScrollLeft === this._currentLeft) {
				this._scrolled = false;
				return;
			}
			const scroll = this.$n('scrollX');
			if (scroll) {
				scroll.scrollLeft = meshScrollLeft;
				this._doScrollNow(false, true);
			}
		}
	}

	/** @internal */
	_updateMeshScrollLeft(scrollLeft?: number): void {
		if (scrollLeft == undefined) return; // shall not return when 0
		const mesh = this.parent,
			scroll = this.$n('scrollX');
		if (mesh?._nativebar && scroll) {
			const { ehead, ebody } = mesh;
			if (ehead)
				ehead.scrollLeft = scrollLeft;
			if (ebody) {
				ebody.scrollLeft = scrollLeft;
			}
			scroll.scrollLeft = scrollLeft;
			this._scrolled = true; // ZK-4743: shall update head, body, scrollbar and stop _doHeadScroll
		}
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var p = this.parent!,
			body = p.$n('body'),
			foot = p.$n('foot');

		if (p._nativebar) {
			//B70-ZK-2130: No need to reset when beforeSize, ZK-343 with native bar works fine too.
			zWatch.listen({ onSize: this });
			var scroll = this.$n_('scrollX'),
				scrollbarWidth = jq.scrollbarWidth();
			// ZK-2583: native IE bug, add 1px in scroll div's height for workaround
			this.$n_().style.height = this.$n_('cave').style.height = this.$n_('right').style.height = scroll.style.height
				= (scroll.firstChild as HTMLElement).style.height = jq.px0(scrollbarWidth);
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
		zWatch.listen({ onResponse: this });
		if (body)
			jq(body).addClass('z-word-nowrap');
		if (foot)
			jq(foot).addClass('z-word-nowrap');

		// ZK-6065: smooth frozen column borders + mobile scrollbar hiding.
		this._syncColumnBorders();
		if (zk.mobile) // ZK-5842
			jq(this.parent?.$n('body')).css('scrollbar-width', 'none');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		// ZK-6065 / ZK-5842: restore mobile scrollbar, clear frozen borders.
		if (zk.mobile)
			jq(this.parent?.$n('body')).css('scrollbar-width', '');
		this._clearColumnBorders();

		var p = this.parent!,
			body = p.$n('body'),
			foot = p.$n('foot'),
			head = p.$n('head');

		// these timers reach the DOM, so they must not outlive the binding
		if (this._delayedSmoothSize) {
			clearTimeout(this._delayedSmoothSize);
			this._delayedSmoothSize = undefined;
		}
		if (this._delayedSize) {
			clearTimeout(this._delayedSize);
			this._delayedSize = undefined;
		}
		if (this._delayedScroll) {
			clearTimeout(this._delayedScroll);
			this._delayedScroll = undefined;
		}

		if (p._nativebar) {
			this.domUnlisten_(this.$n_('scrollX'), 'onScroll');
			p.unlisten({ onScroll: this.proxy(this._onScroll) });
			zWatch.unlisten({ onSize: this });

			if (head)
				this.domUnlisten_(head, 'onScroll', '_doHeadScroll');
		} else {
			this._shallSyncScale = false;
		}
		// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
		zWatch.unlisten({ onResponse: this });
		if (body)
			jq(body).removeClass('z-word-nowrap');
		if (foot)
			jq(foot).removeClass('z-word-nowrap');
		super.unbind_(skipper, after, keepRod);
	}

	// Bug ZK-2264, we should resync the variable of _scrollScale, which do the same as HeadWidget.js
	onResponse(): void {
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

	override onSize(): void {
		if (!this._smoothFrozenEnabled()) {
			this._onSizeLegacy();
			return;
		}

		// ZK-6065: smooth frozen sizing (was EE-only)
		if (!this._columns && !this._rightColumns)
			return;
		const p = this.parent!,
			phead = p.head;
		if (p._nativebar && phead) {
			const n = phead.$n(),
				firstHdcell = n?.cells ? n.cells[0] : undefined;
			if (firstHdcell) {
				const fhcs = firstHdcell.style;
				if (!fhcs.height || n!.cells[1]) {
					fhcs.height = jq.px0(Math.max(firstHdcell.offsetHeight, n!.cells[1] ? n!.cells[1].offsetHeight : 0));
				}
			}
		}
		if (this._delayedSmoothSize)
			clearTimeout(this._delayedSmoothSize);
		this._delayedSmoothSize = setTimeout(() => {
			this._delayedSmoothSize = undefined;
			this._freezeRightColumns();
			this._onSizeLater();
		});
	}

	/** @internal ZK-6065: legacy non-smooth onSize sizing. */
	_onSizeLegacy(): void {
		if (!this._columns)
			return;
		this._syncFrozen(); // B65-ZK-1470

		//B70-ZK-2129: prevent height changed by scrolling
		var p = this.parent!,
			phead = p.head,
			firstHdcell: HTMLElement | undefined;
		if (p._nativebar && phead) {
			//B70-ZK-2558: frozen will onSize before other columns,
			//so there might be no any column in the beginning
			var n = phead.$n() as (HTMLElement & Partial<Pick<HTMLTableRowElement, 'cells'>>) | undefined;
			firstHdcell = n ? (n.cells ? n.cells[0] : undefined) : undefined;
			//B70-ZK-2463: if firstHdcell is not undefined
			if (firstHdcell) {
				const fhcs = firstHdcell.style;
				if (!fhcs.height || n!.cells![1]) {
					fhcs.height = jq.px0(Math.max(firstHdcell.offsetHeight, n!.cells![1] ? n!.cells![1].offsetHeight : 0));
				}
			}
		}

		// Bug 3218078, to do the sizing after the 'setAttr' command
		if (this._delayedSize)
			clearTimeout(this._delayedSize);
		this._delayedSize = setTimeout(() => {
			this._delayedSize = undefined;
			_onSizeLater(this);
			this._syncFrozenNow();
		});
	}

	/** @internal ZK-6065: smooth frozen onSize sizing. */
	_onSizeLater(): void {
		const mesh = this.parent!;

		if (mesh.eheadtbl && mesh._nativebar) {
			const cells = mesh._getFirstRowCells(mesh.eheadrows)!,
				cellsSize = cells.length,
				columns = this._columns!,
				rightColumns = jq(mesh.head!).find('.' + this.$s('right-col')).length;
			let leftWidth = 0,
				rightWidth = 0;

			if (!cells || cellsSize <= 0) { //no need to do the following computation since there is no any column
				return;
			}

			for (let i = 0; i < columns; i++)
				leftWidth += cells[i].offsetWidth;
			for (let i = 1; i <= rightColumns; i++)
				rightWidth += cells[cellsSize - i].offsetWidth;
			mesh._deleteFakeRow(mesh.eheadrows);

			this.$n('cave')!.style.width = jq.px0(leftWidth);
			this.$n('right')!.style.width = jq.px0(rightWidth);
			const scroll = this.$n('scrollX')!,
				ebody = mesh.ebody,
				ehead = mesh.ehead!;
			let width = ebody ? ebody.offsetWidth : ehead.offsetWidth;
			mesh.$n('frozen')!.style.width = jq.px0(width);
			width -= (leftWidth + rightWidth);

			scroll.style.width = jq.px0(width);

			//check body
			let meshTotalScrollWidth = width,
				bufferWidth = 0;
			if (ebody)
				bufferWidth = ebody.scrollWidth - ebody.clientWidth;
			if (bufferWidth == 0)
				bufferWidth = ehead.scrollWidth - ehead.clientWidth;
			meshTotalScrollWidth += bufferWidth;
			(scroll.firstChild as HTMLElement).style.width = jq.px0(meshTotalScrollWidth);
			this.syncScroll();
		}
	}

	/** @internal */
	_doHeadScroll(evt: zk.Event): void {
		if (!this._smoothFrozenEnabled()) {
			// legacy
			var head = evt.domTarget,
				num = Math.ceil(head.scrollLeft / 50);
			// ignore scrollLeft is 0
			if (!head.scrollLeft || this._lastScale == num)
				return;
			evt.data = head.scrollLeft;
			this._onScroll(evt);
			return;
		}
		if (this._scrolled) {
			this._scrolled = false;
			return;
		}
		this._syncFrozenCellsPosition();
	}

	/** @internal */
	_doScroll(n: number): void {
		if (!this._smoothFrozenEnabled()) {
			// legacy
			var p = this.parent!,
				num: number;
			if (p._nativebar)
				num = Math.ceil(this.$n_('scrollX').scrollLeft / 50);
			else
				num = Math.ceil(n);
			if (this._lastScale == num)
				return;
			if (this._delayedScroll) {
				clearTimeout(this._delayedScroll);
			}
			this._delayedScroll = setTimeout(() => {
				this._lastScale = num;
				this._doScrollNow(num);
				this.smartUpdate('start', num);
				this._start = num;
				this._delayedScroll = undefined;
			}, 0);
			return;
		}
		this._doScrollNow();
	}

	// ZK-6065: signature is the union of the legacy CE `(num, force)` and the
	// smooth `(force, ignoreMeshScroll)` forms. In the legacy branch `force`
	// carries the column index (num) and `ignoreMeshScroll` carries the boolean
	// force flag; in the smooth branch they keep their smooth meaning. Callers
	// such as MeshWidget._moveToHidingFocusCell / _syncFrozenNow pass a number,
	// which the smooth branch tolerates (true→1, false→0, undefined→NaN), the
	// same behavior as when this lived in zkmax.
	/** @internal */
	_doScrollNow(force?: number | boolean, ignoreMeshScroll?: boolean): void {
		if (!this._smoothFrozenEnabled()) {
			// legacy: (num, force)
			var num = force as number,
				_force = ignoreMeshScroll,
				totalWidth = 0,
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
					hdcells = mesh.head.$n_().cells,
					hdcol = mesh.ehdfaker!.firstChild,
					ftrows = mesh.foot ? mesh.efootrows : undefined,
					ftcells = ftrows ? ftrows.rows[0].cells : undefined;

				for (var faker: HTMLElement | undefined, i = 0; hdcol && i < totalCols; hdcol = hdcol.nextSibling, i++) {
					if (!(hdcol as HTMLElement).style.width.includes('px')) {
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
				var updateBatch: Update[] = [], isVisible = false;
				// B70-ZK-2071: Use mesh.head to get column.
				for (var i = c, faker: HTMLElement | undefined; i < totalCols; i++) {
					var n = hdcells[i],
						hdWgt = zk.Widget.$<zul.mesh.HeaderWidget>(n)!,
						shallUpdate = false,
						cellWidth: string | undefined;

					isVisible = hdWgt && hdWgt.isVisible();

					//ZK-2776, once a column is hidden, there is an additional style
					if (!hdWgt.isVisible())
						continue; //skip column which is hide

					if (cnt-- <= 0) { //show
						var wd = isVisible ?
							n.offsetWidth // Bug ZK-2690
							: 0;
						// ZK-2071: nativebar behavior should be same as fakebar
						// ZK-4762: cellWidth should update while scroll into view
						if (_force || (wd < 2)) {
							cellWidth = hdWgt._origWd || jq.px(wd);
							// ZK-2772: consider faker's width first for layout consistent
							// if the column is visible.
							if ((wd > 1) && (faker = jq('#' + n.id + '-hdfaker')[0]) && faker.style.width)
								cellWidth = faker.style.width;
							hdWgt._origWd = undefined;
							shallUpdate = true;
						}
					} else if (_force ||
						// Bug ZK-2690
						(n.offsetWidth != 0)) { //hide
						faker = jq('#' + n.id + '-hdfaker')[0];
						//ZK-2776: consider faker's width first for layout consistent
						if (faker.style.width && zk.parseInt(faker.style.width) > 1)
							hdWgt._origWd = faker.style.width;
						cellWidth = width0;
						shallUpdate = true;
					}

					if (_force || shallUpdate) {
						updateBatch.push({ node: n, index: i, width: cellWidth });
					}
				}

				//hide the element without losing focus
				jq(mesh).css({ position: 'absolute', left: -9999 });

				var update: Update | undefined;
				while ((update = updateBatch.shift())) {
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
				jq(mesh).css({ position: '', left: '' });
			}
			// NOTE: Set style width to table to avoid colgroup width not working
			// because of width attribute (width="100%") on table

			const { eheadtbl, ebodytbl, efoottbl } = mesh;
			if (eheadtbl)
				eheadtbl.style.width = jq.px(totalWidth);
			if (ebodytbl)
				ebodytbl.style.width = jq.px(totalWidth - scrollbarWidth);
			if (efoottbl)
				efoottbl.style.width = jq.px(totalWidth);

			mesh._restoreFocus();
			return;
		}
		this._doScrollNowSmooth(ignoreMeshScroll);
	}

	/** @internal ZK-6065: smooth frozen scroll (was EE-only). */
	_doScrollNowSmooth(ignoreMeshScroll?: boolean): void {
		const mesh = this.parent!,
			$scrollX = this.$n('scrollX'),
			tables = [mesh.ehead!, mesh.ebody!, mesh.efoot!],
			ehead = mesh.ehead!;
		let meshScrollLeft: number | undefined;

		if (ignoreMeshScroll) {
			meshScrollLeft = ehead.scrollLeft;
		} else if ($scrollX) { // no $scrollX if nativebar is false
			meshScrollLeft = $scrollX.scrollLeft;
		}

		if (!ignoreMeshScroll) {
			for (let i = 0; i < tables.length; i++) {
				if (tables[i]) {
					tables[i].scrollLeft = meshScrollLeft!;
				}
			}
		}
		this._syncFrozenCellsPosition(meshScrollLeft);
		this._syncStart(meshScrollLeft!);
		this._scrolled = true;
		mesh._restoreFocus();
		//scrollPos sync
		this._currentLeft = meshScrollLeft;
		this.fire('onScrollPos', {
			left: meshScrollLeft
		});
	}

	/** @internal */
	_syncStart(meshScrollLeft: number): void {
		const headRows = this.parent?.eheadrows;
		if (headRows) {
			const rowNodes = headRows.childNodes;
			let start = 0,
				rowNode!: ChildNode;
			for (let i = 0; i < rowNodes.length; i++) {
				const headWgt = zk(rowNodes[i]).$();
				if (!(headWgt instanceof zul.mesh.Auxhead)) {
					rowNode = headRows.childNodes[i];
				}
			}
			const cellNodes = rowNode.childNodes as NodeListOf<HTMLElement>;
			for (let i = this._columns!; i < cellNodes.length; i++) {
				meshScrollLeft -= cellNodes[i].offsetWidth;
				if (meshScrollLeft < 0)
					break;
				else
					start++;
			}
			if (start != this._start) {
				this.smartUpdate('start', start);
				this._start = start;
			}
		}
	}

	/** @internal */
	_syncFrozenCellsPosition(meshScrollLeft?: number): void {
		const frozenColNum = this._columns;
		if (!frozenColNum) return;
		const mesh = this.parent!,
			ehead = mesh.ehead;
		if (meshScrollLeft === undefined) {
			meshScrollLeft = 0;
			const ebody = mesh.ebody;
			//consider head first
			if (ehead)
				meshScrollLeft = ehead.scrollLeft;
			else if (ebody)
				meshScrollLeft = ebody.scrollLeft;
		}

		if (zk.mobile) return; // in mobile use sticky
		const tableRows = [mesh.eheadrows, mesh.ebodyrows, mesh.efootrows];
		for (let i = 0; i < tableRows.length; i++) {
			const tableRow = tableRows[i];
			if (!tableRow) //skip when no row data
				continue;
			this._adjustFrozenCols(
				tableRow.childNodes,
				frozenColNum,
				cellNode => jq(cellNode).css({
					'transform': `translate3d(${jq.px0(meshScrollLeft)}, 0, 0)`,
					'zIndex': 1
				})
			);
		}
	}

	/** @internal */
	_syncFrozen(): void { //called by Rows, HeadWidget...
		this._shallSync = true;
	}

	/** @internal */
	_syncFrozenNow(): void {
		var num = this._start;
		if (this._shallSync && num)
			this._doScrollNow(num, true);

		this._shallSync = false;
	}

	/** @internal */
	override beforeParentChanged_(p: zk.Widget | undefined): void {
		//bug B50-ZK-238
		//ZK-2651: JS Error showed when clear grid children component that include frozen
		if (this.desktop && this._lastScale) //if large then 0
			this._doScroll(0);

		super.beforeParentChanged_(p);
	}

	/** @internal */
	_onScroll(evt: zk.Event): void {
		if (!evt.data || !zk.currentFocus)
			return;

		var p = this.parent,
			td: HTMLTableCellElement | undefined,
			fn = (): void => { // p shouldn't be null when fn is called
				var cf = zk.currentFocus;
				if (cf) {
					td = p!.getFocusCell(cf.$n_());
					var index: number;
					if (td && (index = td.cellIndex - this._columns!) >= 0) {
						this.setStart(index);
						p!.ebody!.scrollLeft = 0;

						if (p!.ehead)
							p!.ehead.scrollLeft = 0;
					}
				}
			};
		if (p) {
			fn();
		}
		evt.stop();
	}

	/** @internal ZK-6065: freeze the right-most columns (sticky). */
	_freezeRightColumns(): void {
		const rightColClass = this.$s('right-col'),
			rightColumns = this._rightColumns,
			mesh = this.parent;
		jq(mesh!).find('.' + rightColClass).removeClass(rightColClass).css('right', '');
		if (!rightColumns)
			return;
		if (mesh?._nativebar) {
			const hasHeadBar = mesh.head?.$n('bar');
			this._calcRightPosition(mesh.eheadrows, !!hasHeadBar);
			this._calcRightPosition(mesh.ebodyrows, false);
			this._calcRightPosition(mesh.efootrows, true);
		}
	}

	/** @internal */
	_calcRightPosition(rows: HTMLElement | undefined, includeBar: boolean): void {
		if (!rows)
			return;
		for (let i = 0; i < rows.children.length; i++) {
			const headers = rows.children[i].children,
				length = headers.length;
			let rightColumns = this._rightColumns + (includeBar ? 1 : 0),
				right = 0;
			for (let j = 0; j < rightColumns; j++) {
				const head = headers[length - 1 - j] as HTMLTableCellElement | undefined;
				if (head) {
					jq(head).addClass(this.$s('right-col')).css('right', right);
					right += head.getBoundingClientRect().width;
					const colSpan = head.colSpan;
					if (colSpan > 1) {
						if (colSpan >= rightColumns - j)
							break;
						rightColumns -= colSpan - 1;
					}
				}
			}
		}
	}

	/** @internal ZK-6065: mark/refresh frozen column borders (smooth only). */
	_syncColumnBorders(): void {
		if (!this._smoothFrozenEnabled()) return;
		const headRows = this.parent!.$n('headrows');
		if (!headRows) return;
		this._clearColumnBorders();
		const frozenColNum = this._columns;
		if (!frozenColNum) return;
		this._adjustFrozenCols(
			headRows.childNodes,
			frozenColNum,
			cellNode => jq(cellNode).addClass(this.$s('col')),
			true
		);

		if (zk.mobile)
			this._adjustStickyFrozenCols(false);
	}

	/** @internal */
	_clearColumnBorders(): void {
		const firstNotFrozenCols = this._firstNotFrozenCols,
			frozenBorderClz = this.$s('col');
		if (firstNotFrozenCols) {
			for (let i = 0; i < firstNotFrozenCols.length; i++)
				jq(firstNotFrozenCols[i]).css('border-left', '');
		}
		jq(this.parent!.$n()).find('.' + frozenBorderClz).removeClass(frozenBorderClz);
		if (zk.mobile)
			this._clearStickyFrozenCols();
	}

	/** @internal */
	_adjustFrozenCols(rowNodes: NodeListOf<ChildNode>, frozenColNum: number, adjustFrozenFunction: (cellNode: HTMLElement, cssLeft: number) => void, isHeadInit?: boolean): void {
		if (!rowNodes) return;
		if (isHeadInit)
			this._firstNotFrozenCols = []; //reset
		const spanLookup = {},
			rowsLength = rowNodes.length;
		for (let rowIndex = 0; rowIndex < rowsLength; rowIndex++) {
			const rowNode = rowNodes[rowIndex],
				cellNodes = rowNode.childNodes as NodeListOf<HTMLTableCellElement>,
				cellsLength = cellNodes.length;
			let curCellCssLeft = 0;
			for (let cellIndex = 0, colIndex = 0; cellIndex < cellsLength; cellIndex++) {
				const cellNode = cellNodes[cellIndex];
				while (spanLookup[`${rowIndex}/${colIndex}`]) {
					colIndex++; //adjust colIndex by previous rowSpans
				}
				if (colIndex < frozenColNum) {
					adjustFrozenFunction(cellNode, curCellCssLeft);
					const colSpan = cellNode.colSpan || 1,
						rowSpan = cellNode.rowSpan || 1;
					for (let i = 0; i < rowSpan; i++) {
						for (let j = 0; j < colSpan; j++) {
							spanLookup[`${rowIndex + i}/${colIndex + j}`] = true;
						}
					}
				} else if (isHeadInit) {
					this._firstNotFrozenCols!.push(cellNode);
					jq(cellNode).css('border-left', '0');
					break;
				}
				curCellCssLeft += jq(cellNode).outerWidth()!;
			}
		}
	}

	/** @internal */
	_clearStickyFrozenCols(): void {
		this._adjustStickyFrozenCols(true);
	}

	/** @internal */
	_adjustStickyFrozenCols(isClear: boolean): void {
		const frozenColNum = this._columns;
		if (!frozenColNum) return;
		const mesh = this.parent!,
			tableRows = [
				jq(mesh.$n('head')).find('tbody')[0],
				jq(mesh.$n('cave')).find('tbody')[0],
				jq(mesh.$n('foot')).find('tbody')[0],
			],
			stickyCls = this.$s('sticky'),
			adjustFrozenFunction = isClear ? function (cellNode) {
				jq(cellNode).css('left', '').removeClass(stickyCls);
			} : function (cellNode: HTMLElement, cssLeft: number) {
				jq(cellNode).css('left', jq.px0(cssLeft)).addClass(stickyCls);
			};

		for (let i = 0; i < tableRows.length; i++) {
			if (!tableRows[i]) //skip when no row data
				continue;
			this._adjustFrozenCols(tableRows[i].childNodes, frozenColNum, adjustFrozenFunction);
		}
	}

	/** @internal */
	_smoothFrozenEnabled(): boolean {
		return this._smooth || !!zk.mobile;
	}

	/** @internal ZK-6065: group cell colspan adjust, called by EE zkex group hook. */
	_adjustGroupCellColSpan(group: zk.Widget<HTMLTableRowElement>, colgroup: Node): void {
		let frozenColumns = this._columns!;
		const n = group.$n()!,
			cells = n.cells,
			cellsLength = cells.length;
		if (cellsLength == 0) //no cell, no need to count
			return;
		let span = 0;
		for (let col = colgroup.firstChild; col; col = col.nextSibling)
			if (zk(col).isVisible())
				span++;
		let lastFrozenCell: HTMLTableCellElement | undefined;
		for (let i = 0; i < cellsLength; i++) {
			const cell = cells[i],
				cellColSpan = cell.colSpan;
			span -= cellColSpan;
			frozenColumns -= cellColSpan;
			if (!lastFrozenCell && frozenColumns <= 0) //try to find the last frozen cell
				lastFrozenCell = cell;
		}
		if (span > 0) {
			if (!lastFrozenCell) //if not defined, use the last one (ex. frozen 3, column only 2)
				lastFrozenCell = cells[cellsLength - 1];
			if (lastFrozenCell == cells[cellsLength - 1]) { //if trying to expand the last frozen cell, append empty cell
				if (frozenColumns > 0) {
					lastFrozenCell.colSpan += frozenColumns;
					span -= frozenColumns;
				}
				const emptyCell = document.createElement('td');
				emptyCell.className = lastFrozenCell.className; //copy css class for style
				n.appendChild(emptyCell);
				span -= 1;
			}
			n.cells[n.cells.length - 1].colSpan += span;
		}
	}
}
