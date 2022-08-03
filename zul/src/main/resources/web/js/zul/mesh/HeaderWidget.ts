/* HeaderWidget.ts

	Purpose:

	Description:

	History:
		Mon Dec 29 17:33:15     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export type SortDirection = 'ascending' | 'descending' | 'natural';
/**
 * A skeletal implementation for a header.
 */
@zk.WrapClass('zul.mesh.HeaderWidget')
export abstract class HeaderWidget extends zul.LabelImageWidget<HTMLTableCellElement> {
	// NOTE: parent can be null, as `getMeshWidget` asserts
	override parent!: zul.mesh.HeadWidget | undefined;
	override nextSibling!: zul.mesh.HeaderWidget | undefined;
	override previousSibling!: zul.mesh.HeaderWidget | undefined;
	_sumWidth = true; // FIXME: never used
	_align?: string;
	_valign?: string;
	_hflexWidth?: number;
	_nhflexbak?: boolean;
	_origWd?: string;
	_dragsz?: zk.Draggable;

	_checked?: boolean; // zul.sel.SelectWidget.prototype._isAllSelected
	_sortDirection!: SortDirection;
	abstract getSortDirection(): SortDirection;
	abstract setSortDirection(sortDirection: SortDirection, opts?: Record<string, boolean>): this;

	/** Returns the horizontal alignment of this column.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @return String
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the horizontal alignment of this column.
	 * @param String align
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || (opts && opts.force)) {
			this.adjustDOMAlign_('align', align);
		}

		return this;
	}

	/** Returns the vertical alignment of this grid.
	 * <p>Default: null (system default: top).
	 * @return String
	 */
	getValign(): string | undefined {
		return this._valign;
	}

	/** Sets the vertical alignment of this grid.
	 * @param String valign
	 */
	setValign(valign: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = valign;

		if (o !== valign || (opts && opts.force)) {
			this.adjustDOMAlign_('valign', valign);
		}

		return this;
	}

	override getHeight(): string | undefined {
		return this._height;
	}

	override setHeight(height: string, opts?: Record<string, boolean>): this {
		const o = this._height;
		this._height = height;

		if (o !== height || (opts && opts.force)) {
			this.updateMesh_();
		}

		return this;
	}

	override getWidth(): string | undefined {
		// NOTE: Returning 0 is currently the intended behavior. A return value of 0 from getWidth is acceptable
		// in two current use cases.
		// 1. `'width:' + wd + ';'` where `wd` is assigned the return value of `getWidth` (could be indirectly
		//     achieved with nested function calls, e.g., `wd = f()` where `f(){return getWidth();}`).
		// 2. Asserting truthiness of the return value of `getWidth`.
		// FIXME: `0 as never` is a hack.
		// TODO:
		// 1. Find a way to embed return type 0 cleanly. One could try `getWidth(): string | null | undefined | 0`,
		//     but it requires a lot of code change from other places. However, it might turn out that incorporating
		//     the "0 literal type" into the return type union is the most robust solution, albeit cumbersome.
		// 2. Return something else instead of 0. Returning null seems feasible, but `'width:' + wd + ';'` will break.
		return this.isVisible() ? this._width : 0 as never;
	}

	override setWidth(width?: string): this {
		this._width = width;
		this.updateMesh_();
		return this;
	}

	// Bug ZK-2401
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		//sync frozen
		var box: zul.mesh.MeshWidget | undefined,
			node: HTMLTableCellElement | undefined;
		if ((box = this.getMeshWidget()) && box.efrozen
			&& zk.Widget.$(box.efrozen.firstChild)
			&& (node = this.$n())) {
			box._moveToHidingFocusCell(node.cellIndex);
		}
	}

	/**
	 * Updates the whole mesh widget.
	 * @param String name
	 * @param Object value
	 */
	updateMesh_(nm?: string, val?: unknown): void { //TODO: don't rerender
		if (this.desktop) {
			var mesh = this.getMeshWidget();
			if (mesh) {
				var minWds: zul.mesh.MeshWidth | undefined;
				if (nm == 'visible' && val && this._width == '-1') //sizable + visible false -> true
					minWds = mesh._calcMinWds();
				// B70-ZK-2036: Clear min width cache before rerender.
				mesh._minWd = undefined;
				mesh.rerender();
				if (minWds) {
					var parent = this.parent!;
					for (var w = parent.firstChild, i = 0; w; w = w.nextSibling, i++) {
						if (w == this) {
							this._width = jq.px0(minWds.wds[i]);
							break;
						}
					}
					zUtl.fireSized(mesh, -1);
				}
			}
		}
	}

	adjustDOMAlign_(direction: 'align' | 'valign', value: string): void {
		var n = this.$n();
		if (n) {
			if (direction == 'align') {
				n.style.textAlign = value;
			} else if (direction == 'valign') {
				n.style.verticalAlign = value;
			}
		}
	}

	override setFlexSize_(flexSize_: zk.FlexSize, isFlexMin?: boolean): void {
		if (this._cssflex && this.parent!.getFlexContainer_() != null && !isFlexMin)
			return;
		if ((flexSize_.width !== undefined && flexSize_.width != 'auto' && flexSize_.width != '') || flexSize_.width as unknown == 0) { //JavaScript deems 0 == ''
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			//updated: need to concern inner padding due to wgt.getContentEdgeWidth_()
			//spec in flex.js
			var rvw = this._hflex == 'min' && this.firstChild && this.isRealVisible() ? // B50-ZK-394
					zk(this.$n('cave')).revisedWidth(flexSize_.width as number) : flexSize_.width as number;
			this._hflexWidth = rvw;
		} else
			super.setFlexSize_(flexSize_, isFlexMin);
	}

	override getContentEdgeHeight_(): number {
		return zk(this).sumStyles('tb', jq.margins);
	}

	override getContentEdgeWidth_(): number {
		return zk(this).sumStyles('lr', jq.margins);
	}

	override domStyle_(no?: zk.DomStyleOptions): string {
		var style = '';
		if (this._hflexWidth) { //handle hflex
			style = 'width: ' + this._hflexWidth + 'px;';

			if (no) no.width = true;
			else no = {width: true};
		}
		if (this._align)
			style += 'text-align:' + this._align + ';';
		if (this._valign)
			style += 'vertical-align:' + this._valign + ';';

		return style + super.domStyle_(no);
	}

	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 */
	getMeshWidget(): zul.mesh.MeshWidget | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	/**
	 * Returns whether the widget is sortable or not.
	 * <p> Default: false.
	 * @return boolean
	 */
	isSortable_(): boolean {
		return false;
	}

	override setVisible(visible: boolean): this {
		if (this.isVisible() != visible) {
			super.setVisible(visible);
			this.updateMesh_('visible', visible);
			//ZK-3332 update server side component width
			var mesh = this.getMeshWidget();
			if (mesh && mesh.desktop && !this._hflexWidth && this.getWidth())
				this.parent!.fire('onColSize', {
					index: zk(this.$n()).cellIndex(),
					column: this,
					width: this.isVisible() ? this._width : '-1'
				}, undefined, 0);
		}
		return this;
	}

	override getTextNode(): HTMLElement | undefined {
		return jq(this.$n_()).find('>div:first')[0];
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this.parent!.isSizable())
			this._initsz();
		var mesh = this.getMeshWidget(),
			width0 = zul.mesh.MeshWidget.WIDTH0;
		if (mesh) {
			var $n = jq(this.$n_()),
				$faker = jq(this.$n('hdfaker')), // `this.$n('hdfaker')` can be null. Musn't use `this.$n_('hdfaker')`.
				w = this.getWidth();
			if (mesh._cssflex && mesh.isChildrenFlex && mesh.isChildrenFlex()) { //skip not MeshWidget
				if (!this.isVisible()) {
					$n.css('display', 'none');
				} else {
					$n.css('display', '');
				}
			} else {
				if (!this.isVisible()) {
					$faker.css('display', '');
					$faker.css('visibility', 'collapse');
					$faker.css('width', width0);
					if (mesh._cssflex && this._nhflex! > 0) {
						$n.css('display', 'none');
					} else {
						$n.css('display', '');
					}
				} else {
					$faker.css('visibility', '');
					// B70-ZK-2036: Check if header has hflex width first.
					if (!this._hflexWidth && w) {
						$faker.css('width', w);
					}
				}
			}
		}
		this.fixFaker_();
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._dragsz) {
			this._dragsz.destroy();
			this._dragsz = undefined;
		}
		super.unbind_(skipper, after, keepRod);
	}

	_initsz(): void {
		var n = this.$n();
		if (n && !this._dragsz) {
			this._dragsz = new zk.Draggable(this, undefined, {
				revert: true,
				constraint: 'horizontal',
				ghosting: HeaderWidget._ghostsizing,
				endghosting: HeaderWidget._endghostsizing,
				snap: HeaderWidget._snapsizing,
				ignoredrag: HeaderWidget._ignoresizing,
				zIndex: '99999', // Bug: B50-3285153
				endeffect: HeaderWidget._aftersizing
			});
		}
	}

	/**
	 * Fixes the faker (an visible row for adjusting column), if any.
	 */
	fixFaker_(): void {
		if (!(this.parent instanceof zul.mesh.Auxhead)) {
			var n = this.$n_(),
				index = zk(n).cellIndex(),
				owner = this.getMeshWidget()!;
			for (var faker: HTMLElement | undefined, fs = HeaderWidget._faker, i = fs.length; i--;) {
				type MethodName = `e${typeof fs[number]}`;
				faker = owner[('e' + fs[i]) as MethodName]; // internal element
				if (faker && !this.$n(fs[i])) {
					faker[faker.childNodes.length > index ? 'insertBefore' : 'appendChild'](this._createFaker(n, fs[i]), faker.childNodes[index]);
					this._subnodes[fs[i]] = undefined; // clear inner cache
				}
			}
		}
	}

	_createFaker(n: HTMLElement, postfix: string): HTMLTableColElement {
		var _getWidth = zul.mesh.MeshWidget._getWidth,
			wd = _getWidth(this, this._hflexWidth ? this._hflexWidth + 'px' : this.getWidth()),
			t = document.createElement('col'),
			frozen = this.getMeshWidget()!.frozen;
		wd = wd ? 'width: ' + wd + ';' : '';
		if (!wd && frozen && !frozen._smooth)
			wd = this._calcFakerWidth(postfix);
		t.id = n.id + '-' + postfix;
		t.style.cssText = wd;
		return t;
	}

	_calcFakerWidth(postfix: string): string {
		var parent = this.parent!,
			child = parent.firstChild,
			totalWidth = 0,
			fakerCount = 1;

		while (child) {
			if (!child.getWidth() && !child.getHflex() && child != this) {
				var fakerWidth = child.$n_(postfix).style.width;
				if (fakerWidth == '')
					break;
				if (fakerWidth == zul.mesh.MeshWidget.WIDTH0)
					totalWidth += parseFloat(child._origWd!);
				else
					totalWidth += parseFloat(fakerWidth);
				fakerCount++;
			}
			child = child.nextSibling;
		}
		if (totalWidth == 0)
			return '';
		else {
			var eachWidth = jq.px0(totalWidth / fakerCount);
			this._syncFakerWidth(eachWidth, postfix);
			return 'width: ' + eachWidth;
		}
	}

	_syncFakerWidth(width: string, postfix: string): void {
		var parent = this.parent!,
			child = parent.firstChild;

		while (child) {
			if (!child.getWidth() && !child.getHflex() && child != this) {
				var faker = child.$n_(postfix);
				if (faker.style.width == zul.mesh.MeshWidget.WIDTH0) {
					if (postfix == 'hdfaker')
						child._origWd = width;
				} else
					faker.style.width = width;
			}
			child = child.nextSibling;
		}
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var tg = evt.domTarget,
			wgt = zk.Widget.$(tg)!,
			n = this.$n(),
			ofs = this._dragsz ? zk(n).revisedOffset() : false,
			ignoreSort = false;

		if (!zk.dragging && (wgt == this || (wgt instanceof zul.wgt.Label))
				&& this.isSortable_() && !jq.nodeName(tg, 'input')
				&& (!this._dragsz || !this._insizer(evt.pageX - ofs[0]))
				&& !ignoreSort) {
			this.fire('onSort', 'ascending' != this.getSortDirection()); // B50-ZK-266
			evt.stop();
		} else {
			if (jq.nodeName(tg, 'input'))
				evt.stop({propagation: true});
			super.doClick_(evt, popupOnly);
		}
	}

	override doDoubleClick_(evt: zk.Event): void {
		if (this._dragsz) {
			var n = this.$n(),
				$n = zk(n),
				ofs = $n.revisedOffset();
			if (this._insizer(evt.pageX - ofs[0])) {
				var mesh = this.getMeshWidget()!,
					cIndex = $n.cellIndex();
				mesh.clearCachedSize_();
				mesh._calcMinWds();
				var sz = mesh._minWd!.wds[cIndex];
				// NOTE: `{control: this, _zszofs: sz}` has intended behavior but is a poor hack.
				// TODO: Should refactor `{control: this, _zszofs: sz}` to match `zk.Draggable`.
				HeaderWidget._aftersizing({control: this, _zszofs: sz} as unknown as zk.Draggable, evt);
			} else
				super.doDoubleClick_(evt);
		} else
			super.doDoubleClick_(evt);
	}

	override doMouseMove_(evt: zk.Event): void {
		if (zk.dragging || !this.parent!.isSizable())
			return;
		var n = this.$n_(),
			ofs = zk(n).revisedOffset(); // Bug #1812154
		if (this._insizer(evt.pageX - ofs[0])) {
			jq(n).addClass(this.$s('sizing'));
		} else {
			jq(n).removeClass(this.$s('sizing'));
		}
	}

	override doMouseOut_(evt: zk.Event): void {
		if (this.parent!.isSizable()) {
			var n = this.$n_();
			jq(n).removeClass(this.$s('sizing'));
		}
		super.doMouseOut_(evt);
	}

	override ignoreDrag_(pt: zk.Offset): boolean {
		if (this.parent!.isSizable()) {
			var n = this.$n(),
				ofs = zk(n).revisedOffset();
			return this._insizer(pt[0] - ofs[0]);
		}
		return false;
	}

	//@Override to avoid add child offset
	override ignoreChildNodeOffset_(attr: string): boolean {
		return true;
	}

	override listenOnFitSize_(): void { return; } // skip flex
	override unlistenOnFitSize_(): void { return; }

	//@Override to find the minimum width of listheader
	override beforeMinFlex_(o: zk.FlexOrient): number | undefined {
		if (o == 'w') {
			var wgt = this.getMeshWidget();
			if (wgt) {
				wgt._calcMinWds();
				if (wgt._minWd) {
					var n = this.$n(), zkn = zk(n),
						cidx = zkn.cellIndex();
					return zkn.revisedWidth(wgt._minWd.wds[cidx]);
				}
			}
		}
		return undefined;
	}

	override clearCachedSize_(): void {
		super.clearCachedSize_();
		var mw: zul.mesh.MeshWidget | undefined;
		if (mw = this.getMeshWidget())
			mw._clearCachedSize();
	}

	//@Override to get width/height of MeshWidget
	override getParentSize_(): {height: number; width: number} {
		//to be overridden
		var mw = this.getMeshWidget()!,
			p = mw.$n(),
			zkp = p ? zk(p) : undefined;
		if (zkp) {
			return {
				height: zkp.contentHeight(),
				width: zkp.contentWidth()
			};
		}
		// NOTE: originally `{}`
		return {height: 0, width: 0};
	}

	override isWatchable_(name: string, p: zk.Widget, cache?: Record<string, unknown>): boolean {
		//Bug 3164504: Hflex will not recalculate when the colum without label
		//Cause: DIV (parent of HeadWidget) is invisible if all columns have no label
		var wp: zul.mesh.HeadWidget | zul.mesh.MeshWidget | undefined;
		return !!(this._visible && (wp = this.parent) && wp._visible //check this and HeadWidget
			&& (wp = wp.parent) && wp.isWatchable_(name, p, cache)); //then MeshWidget.isWatchable_
	}

	_insizer(x: number): boolean {
		return x >= this.$n_().offsetWidth - 8;
	}

	override deferRedrawHTML_(out: string[]): void {
		out.push('<th', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></th>');
	}

	override afterClearFlex_(): void {
		this.parent!.afterClearFlex_();
	}

	getContentWidth_(): number {
		var $cv = zk(this.$n('cave')),
			isTextOnly = !this.nChildren && !this._iconSclass,
			contentWidth = isTextOnly ? $cv.textWidth() : $cv.textSize()[0];
		return Math.ceil(contentWidth + $cv.padBorderWidth() + zk(this.$n()).padBorderWidth());
	}

	//static
	static readonly _faker = ['hdfaker', 'bdfaker', 'ftfaker'] as const;

	//drag
	static _ghostsizing(dg: zk.Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement | undefined {
		var wgt = dg.control as HeaderWidget,
			el = wgt.getMeshWidget()!.eheadtbl!,
			of = zk(el).revisedOffset(),
			n = wgt.$n();

		ofs[1] = of[1];
		ofs[0] += zk(n).offsetWidth();
		jq(document.body).append(
			'<div id="zk_hdghost" style="position:absolute;top:'
			+ ofs[1] + 'px;left:' + ofs[0] + 'px;width:3px;height:' + zk(el.parentNode!.parentNode).offsetHeight()
			+ 'px;background:darkgray"></div>');
		return jq('#zk_hdghost')[0];
	}

	static _endghostsizing(dg: zk.Draggable, origin: HTMLElement): void {
		dg._zszofs = zk(dg.node).revisedOffset()[0] - zk(origin).revisedOffset()[0];
	}

	static _snapsizing(dg: zk.Draggable, pointer: zk.Offset): zk.Offset {
		var n = (dg.control as zk.Widget).$n(),
			$n = zk(n),
			ofs = $n.viewportOffset(),
			sofs = $n.scrollOffset(),
			min = ofs[0] + sofs[0] + dg._zmin!;
		pointer[0] += $n.offsetWidth();
		if (pointer[0] < min)
			pointer[0] = min;
		return pointer;
	}

	static _ignoresizing(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var wgt = dg.control as HeaderWidget,
			n = wgt.$n(), $n = zk(n),
			ofs = $n.revisedOffset(); // Bug #1812154

		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + $n.padBorderWidth();
			return false;
		}
		return true;
	}

	static _aftersizing(dg: zk.Draggable, evt: zk.Event): void {
		var wgt = dg.control as HeaderWidget,
			mesh = wgt.getMeshWidget()!,
			wd = jq.px(dg._zszofs!),
			hdfaker = mesh.ehdfaker!,
			bdfaker = mesh.ebdfaker!,
			ftfaker = mesh.eftfaker,
			cidx = zk(wgt.$n()).cellIndex(),
			hdcols = hdfaker.childNodes as NodeListOf<HTMLElement>,
			bdcols = bdfaker.childNodes as NodeListOf<HTMLElement>,
			ftcols = ftfaker ? ftfaker.childNodes as NodeListOf<HTMLElement> : undefined,
			wds: string[] = [];

		//1. store resized width
		// B70-ZK-2199: convert percent width to fixed width
		for (var w = mesh.head!.firstChild, i = 0; w; w = w.nextSibling, i++) {
			var stylew = hdcols[i].style.width,
				origWd = w._origWd, // ZK-1022: get original width if it is shrinked by Frozen.js#_doScrollNow
				isFixedWidth = stylew && !stylew.includes('%');

			if (origWd) {
				if (isFixedWidth && zk.parseFloat(stylew) > 1) {
					origWd = stylew; // use the latest one;
				}
				w._width = wds[i] = origWd;
			} else {
				wds[i] = isFixedWidth ? stylew : jq.px0(w.$n_().offsetWidth);
				if (w.isVisible()) w._width = wds[i];
				else if (!w._width && !w._hflex) //invisible and no width
					w._width = '-1';
			}
			if (!isFixedWidth) {
				hdcols[i].style.width = bdcols[i].style.width = wds[i];
				if (ftcols) //ZK-2769: Listfooter is not aligned with listhead on changing width
					ftcols[i].style.width = wds[i];
			}

			// reset hflex, Bug ZK-2772 - Misaligned Grid columns
			var wdInt = zk.parseInt(wds[i]);
			if (mesh._cssflex && mesh.isChildrenFlex()) {
				zFlex.clearCSSFlex(w, 'h', true);
			} else if (w._hflexWidth) {
				w.setHflex_();
				w._hflexWidth = undefined;
			}
			if (mesh._minWd) {
				mesh._minWd.wds[i] = wdInt;
			}
		}


		//2. set resized width to colgroup col
		if (!wgt._origWd) // NOTE: originally, `if(!wgt.origWd)` which was wrong.
			wgt._width = wds[cidx] = wd;
		hdcols[cidx].style.width = bdcols[cidx].style.width = wd;
		if (ftcols) //ZK-2769: Listfooter is not aligned with listhead on changing width
			ftcols[cidx].style.width = wd;

		//3. clear width=100% setting, otherwise it will try to expand to whole width
		mesh.eheadtbl!.width = '';
		mesh.ebodytbl!.width = '';
		if (mesh.efoottbl)
			mesh.efoottbl.width = '';

		delete mesh._span; //no span!
		delete mesh._sizedByContent; //no sizedByContent!
		for (var w = mesh.head!.firstChild; w; w = w.nextSibling)
			w.setHflex_(); //has side effect of setting w.$n().style.width of w._width

		wgt.parent!.fire('onColSize', zk.copy({
			index: cidx,
			column: wgt,
			width: wd,
			widths: wds
		}, evt.data), undefined, 0);

		// bug #2799258 in IE, we have to force to recalculate the size.
		mesh.$n_()._lastsz = undefined;

		// for the test case of B70-ZK-2290.zul, we need to put the width back.
		if (!zk.webkit) {
			mesh.eheadtbl!.width = '100%';
			mesh.ebodytbl!.width = '100%';
			if (mesh.efoottbl)
				mesh.efoottbl.width = '100%';
		}
		// bug #2799258
		zUtl.fireSized(mesh, -1); //no beforeSize
	}

	static redraw(this: HeaderWidget, out: string[]): void {
		var uuid = this.uuid,
			label = this.domContent_();
		out.push('<th', this.domAttrs_({width: true}), ' role="columnheader"><div id="',
			uuid, '-cave" class="', this.$s('content'), '"',
			this.domTextStyleAttr_()!, '><div class="', this.$s('sorticon'),
			'"><i id="', uuid, '-sort-icon" aria-hidden="true"></i></div>',
			((!this.firstChild && label == '') ? '&nbsp;' : label)); //ZK-805 MenuPopup without columns issue

		if (this.parent!._menupopup && this.parent!._menupopup != 'none')
			out.push('<a id="', uuid, '-btn" href="javascript:;" class="',
				this.$s('button'), '" tabindex="-1" aria-hidden="true"><i class="z-icon-caret-down"></i></a>');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></th>');
	}
}