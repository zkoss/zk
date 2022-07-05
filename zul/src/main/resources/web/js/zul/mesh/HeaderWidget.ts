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
	public override parent!: zul.mesh.HeadWidget | null;
	public override nextSibling!: zul.mesh.HeaderWidget | null;
	public override previousSibling!: zul.mesh.HeaderWidget | null;
	protected _sumWidth = true; // FIXME: never used
	public _align?: string;
	public _valign?: string;
	public _hflexWidth?: number;
	public _nhflexbak?: boolean;
	public _origWd?: string | null;
	private _dragsz?: zk.Draggable | null;

	public _checked?: boolean | null; // zul.sel.SelectWidget.prototype._isAllSelected
	protected _sortDirection!: SortDirection;
	public abstract getSortDirection(): SortDirection;
	public abstract setSortDirection(direction: SortDirection, opts?: Record<string, boolean>): this;

	/** Returns the horizontal alignment of this column.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @return String
	 */
	public getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the horizontal alignment of this column.
	 * @param String align
	 */
	public setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			this.adjustDOMAlign_('align', v);
		}

		return this;
	}

	/** Returns the vertical alignment of this grid.
	 * <p>Default: null (system default: top).
	 * @return String
	 */
	public getValign(): string | undefined {
		return this._valign;
	}

	/** Sets the vertical alignment of this grid.
	 * @param String valign
	 */
	public setValign(v: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = v;

		if (o !== v || (opts && opts.force)) {
			this.adjustDOMAlign_('valign', v);
		}

		return this;
	}

	public override getHeight(): string | null | undefined {
		return this._height;
	}

	public override setHeight(v: string, opts?: Record<string, boolean>): this {
		const o = this._height;
		this._height = v;

		if (o !== v || (opts && opts.force)) {
			this.updateMesh_();
		}

		return this;
	}

	public override getWidth(): string | null | undefined {
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

	public override setWidth(w: string | null): void {
		this._width = w;
		this.updateMesh_();
	}

	// Bug ZK-2401
	protected override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		//sync frozen
		var box: zul.mesh.MeshWidget | null,
			node: HTMLTableCellElement | null | undefined;
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
	protected updateMesh_(nm?: string, val?: unknown): void { //TODO: don't rerender
		if (this.desktop) {
			var mesh = this.getMeshWidget();
			if (mesh) {
				var minWds: zul.mesh.MeshWidth | null = null;
				if (nm == 'visible' && val && this._width == '-1') //sizable + visible false -> true
					minWds = mesh._calcMinWds();
				// B70-ZK-2036: Clear min width cache before rerender.
				mesh._minWd = null;
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

	protected adjustDOMAlign_(direction: 'align' | 'valign', value: string): void {
		var n = this.$n();
		if (n) {
			if (direction == 'align') {
				n.style.textAlign = value;
			} else if (direction == 'valign') {
				n.style.verticalAlign = value;
			}
		}
	}

	public override setFlexSize_(sz: zk.FlexSize, isFlexMin?: boolean): zk.FlexSize | undefined {
		if (this._cssflex && this.parent!.getFlexContainer_() != null && !isFlexMin)
			return;
		if ((sz.width !== undefined && sz.width != 'auto' && sz.width != '') || sz.width as unknown == 0) { //JavaScript deems 0 == ''
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			//updated: need to concern inner padding due to wgt.getContentEdgeWidth_()
			//spec in flex.js
			var rvw = this._hflex == 'min' && this.firstChild && this.isRealVisible() ? // B50-ZK-394
					zk(this.$n('cave')).revisedWidth(sz.width as number) : sz.width as number;
			this._hflexWidth = rvw;
			return {width: rvw};
		} else
			super.setFlexSize_(sz, isFlexMin);
	}

	protected override getContentEdgeHeight_(): number {
		return zk(this).sumStyles('tb', jq.margins);
	}

	protected override getContentEdgeWidth_(): number {
		return zk(this).sumStyles('lr', jq.margins);
	}

	protected override domStyle_(no?: zk.DomStyleOptions): string {
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
	public getMeshWidget(): zul.mesh.MeshWidget | null {
		return this.parent ? this.parent.parent : null;
	}

	/**
	 * Returns whether the widget is sortable or not.
	 * <p> Default: false.
	 * @return boolean
	 */
	protected isSortable_(): boolean {
		return false;
	}

	public override setVisible(visible: boolean): void {
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
				}, null, 0);
		}
	}

	public override getTextNode(): HTMLElement | null | undefined {
		return jq(this.$n_()).find('>div:first')[0];
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this.parent!.isSizable())
			this._initsz();
		var mesh = this.getMeshWidget(),
			width0 = zul.mesh.MeshWidget.WIDTH0;
		if (mesh) {
			var $n = jq(this.$n_()),
				$faker = jq(this.$n('hdfaker')!), // `this.$n('hdfaker')` can be null. Musn't use `this.$n_('hdfaker')`.
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

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._dragsz) {
			this._dragsz.destroy();
			this._dragsz = null;
		}
		super.unbind_(skipper, after, keepRod);
	}

	private _initsz(): void {
		var n = this.$n();
		if (n && !this._dragsz) {
			this._dragsz = new zk.Draggable(this, null, {
				revert: true,
				constraint: 'horizontal',
				ghosting: HeaderWidget._ghostsizing,
				endghosting: HeaderWidget._endghostsizing,
				snap: HeaderWidget._snapsizing,
				ignoredrag: HeaderWidget._ignoresizing,
				zIndex: 99999, // Bug: B50-3285153
				endeffect: HeaderWidget._aftersizing
			});
		}
	}

	/**
	 * Fixes the faker (an visible row for adjusting column), if any.
	 */
	protected fixFaker_(): void {
		if (!(this.parent instanceof zul.mesh.Auxhead)) {
			var n = this.$n_(),
				index = zk(n).cellIndex(),
				owner = this.getMeshWidget()!;
			for (var faker: HTMLElement | null | undefined, fs = HeaderWidget._faker, i = fs.length; i--;) {
				type MethodName = `e${typeof fs[number]}`;
				faker = owner[('e' + fs[i]) as MethodName]; // internal element
				if (faker && !this.$n(fs[i])) {
					faker[faker.childNodes.length > index ? 'insertBefore' : 'appendChild'](this._createFaker(n, fs[i]), faker.childNodes[index]);
					this._subnodes[fs[i]] = null; // clear inner cache
				}
			}
		}
	}

	private _createFaker(n: HTMLElement, postfix: string): HTMLTableColElement {
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

	private _calcFakerWidth(postfix: string): string {
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

	private _syncFakerWidth(width: string, postfix: string): void {
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

	public override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var tg = evt.domTarget!,
			wgt = zk.Widget.$(tg)!,
			n = this.$n(),
			ofs = this._dragsz ? zk(n).revisedOffset() : false,
			btn = wgt.$n('btn'),
			ignoreSort = false;

		//IE will trigger doClick during closing menupopup
		if (zk.ie < 11 && btn && !zk(btn).isRealVisible())
			ignoreSort = true;

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

	protected override doDoubleClick_(evt: zk.Event): void {
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

	protected override doMouseMove_(evt: zk.Event): void {
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

	protected override doMouseOut_(evt: zk.Event): void {
		if (this.parent!.isSizable()) {
			var n = this.$n_();
			jq(n).removeClass(this.$s('sizing'));
		}
		super.doMouseOut_(evt);
	}

	protected override ignoreDrag_(pt: zk.Draggable): boolean {
		if (this.parent!.isSizable()) {
			var n = this.$n(),
				ofs = zk(n).revisedOffset();
			return this._insizer(pt[0] - ofs[0]);
		}
		return false;
	}

	//@Override to avoid add child offset
	public override ignoreChildNodeOffset_(attr: string): boolean {
		return true;
	}

	public override listenOnFitSize_ = zk.$void; // skip flex
	public override unlistenOnFitSize_ = zk.$void;

	//@Override to find the minimum width of listheader
	public override beforeMinFlex_(o: zk.FlexOrient): number | null {
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
		return null;
	}

	protected override clearCachedSize_(): void {
		super.clearCachedSize_();
		var mw: zul.mesh.MeshWidget | null;
		if (mw = this.getMeshWidget())
			mw._clearCachedSize();
	}

	//@Override to get width/height of MeshWidget
	public override getParentSize_(): {height: number; width: number} {
		//to be overridden
		var mw = this.getMeshWidget()!,
			p = mw.$n(),
			zkp = p ? zk(p) : null;
		if (zkp) {
			// Bug #3255116
			if (mw.ebody) {
				if (zk.ie < 11) { //Related bugs: ZK-890 and ZK-242
					if (mw.ebodytbl && !mw.ebodytbl.width) {
						mw.ebodytbl.width = '100%';
						// reset the width for IE
					}
				}
			}
			return {
				height: zkp.contentHeight(),
				width: zkp.contentWidth()
			};
		}
		// NOTE: originally `{}`
		return {height: 0, width: 0};
	}

	public override isWatchable_(name: string, p: zk.Widget, cache?: Record<string, unknown>): boolean | null | undefined {
		//Bug 3164504: Hflex will not recalculate when the colum without label
		//Cause: DIV (parent of HeadWidget) is invisible if all columns have no label
		var wp: zul.mesh.HeadWidget | zul.mesh.MeshWidget | null;
		return this._visible && (wp = this.parent) && wp._visible //check this and HeadWidget
			&& (wp = wp.parent) && wp.isWatchable_(name, p, cache); //then MeshWidget.isWatchable_
	}

	private _insizer(x: number): boolean {
		return x >= this.$n_().offsetWidth - 8;
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<th', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></th>');
	}

	public override afterClearFlex_(): void {
		this.parent!.afterClearFlex_();
	}

	public getContentWidth_(): number {
		var $cv = zk(this.$n('cave')),
			isTextOnly = !this.nChildren && !this._iconSclass,
			contentWidth = isTextOnly ? $cv.textWidth() : $cv.textSize()[0];
		return Math.ceil(contentWidth + $cv.padBorderWidth() + zk(this.$n()).padBorderWidth());
	}

	//static
	public static readonly _faker = ['hdfaker', 'bdfaker', 'ftfaker'] as const;

	//drag
	private static _ghostsizing(dg: zk.Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement | undefined {
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

	private static _endghostsizing(dg: zk.Draggable, origin: HTMLElement): void {
		dg._zszofs = zk(dg.node).revisedOffset()[0] - zk(origin).revisedOffset()[0];
	}

	private static _snapsizing(dg: zk.Draggable, pointer: zk.Offset): zk.Offset {
		var n = dg.control!.$n(),
			$n = zk(n),
			ofs = $n.viewportOffset(),
			sofs = $n.scrollOffset(),
			min = ofs[0] + sofs[0] + dg._zmin!;
		pointer[0] += $n.offsetWidth();
		if (pointer[0] < min)
			pointer[0] = min;
		return pointer;
	}

	private static _ignoresizing(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var wgt = dg.control as HeaderWidget,
			n = wgt.$n(), $n = zk(n),
			ofs = $n.revisedOffset(); // Bug #1812154

		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + $n.padBorderWidth();
			return false;
		}
		return true;
	}

	private static _aftersizing(dg: zk.Draggable, evt: zk.Event): void {
		var wgt = dg.control as HeaderWidget,
			mesh = wgt.getMeshWidget()!,
			wd = jq.px(dg._zszofs!),
			hdfaker = mesh.ehdfaker!,
			bdfaker = mesh.ebdfaker!,
			ftfaker = mesh.eftfaker,
			cidx = zk(wgt.$n()).cellIndex(),
			hdcols = hdfaker.childNodes as NodeListOf<HTMLElement>,
			bdcols = bdfaker.childNodes as NodeListOf<HTMLElement>,
			ftcols = ftfaker ? ftfaker.childNodes as NodeListOf<HTMLElement> : null,
			wds: string[] = [];

		//1. store resized width
		// B70-ZK-2199: convert percent width to fixed width
		for (var w = mesh.head!.firstChild, i = 0; w; w = w.nextSibling, i++) {
			var stylew = hdcols[i].style.width,
				origWd = w._origWd, // ZK-1022: get original width if it is shrinked by Frozen.js#_doScrollNow
				isFixedWidth = stylew && stylew.indexOf('%') < 0;

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
				zFlex.clearCSSFlex(this, 'h', true);
			} else if (w._hflexWidth) {
				w.setHflex_(null);
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
			w.setHflex_(null); //has side effect of setting w.$n().style.width of w._width

		wgt.parent!.fire('onColSize', zk.copy({
			index: cidx,
			column: wgt,
			width: wd,
			widths: wds
		}, evt.data), null, 0);

		// bug #2799258 in IE, we have to force to recalculate the size.
		mesh.$n_()._lastsz = null;

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

	public static redraw(this: HeaderWidget, out: string[]): void {
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