/* HeadWidget.ts

	Purpose:

	Description:

	History:
		Mon Dec 29 17:15:38     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
//Bug 1926480: opera failed to add listheader dynamically (since hdfakerflex introduced)
var _fixOnChildChanged = zk.opera ? function (head: HeadWidget): boolean | null | undefined {
	return head.parent && head.parent.rerender() as undefined; //later
} : zk.$void;

function _syncFrozen(wgt: HeadWidget): void {
	var mesh = wgt.getMeshWidget(),
		frozen: zul.mesh.Frozen | undefined | null;
	if (mesh && (frozen = mesh.frozen)) {
		var hdfaker: HTMLTableColElement | null | undefined;
		if (mesh._nativebar) {
			frozen._syncFrozen();
		} else if ((hdfaker = mesh.ehdfaker)) {
			//_scrollScale is used in Scrollbar.js
			frozen._scrollScale = hdfaker.childNodes.length - frozen._columns! - 1;

			// Bug ZK-2264
			frozen._shallSyncScale = false;
		}
	}
}

export class HeadWidget extends zul.Widget<HTMLTableRowElement> {
	// NOTE: Parent could be null because it is checked in `afterChildrenFlex_`.
	public override parent!: zul.mesh.MeshWidget | null;
	public override firstChild!: zul.mesh.HeaderWidget | null;
	public override lastChild!: zul.mesh.HeaderWidget | null;
	private _sizable?: boolean;
	// FIXME: The following three properties are never assigned.
	public hdfaker!: HTMLTableColElement;
	public bdfaker!: HTMLTableColElement;
	public ftfaker!: HTMLTableColElement;
	public _menupopup?: string;
	public _mpop?: zul.menu.Menupopup | null;

	public constructor() {
		super(); // FIXME: reconsider constructor params
		this.listen({onColSize: this}, -1000);
	}

	/** Returns whether the width of the child column is sizable.
	 * @return boolean
	 */
	public isSizable(): boolean | undefined {
		return this._sizable;
	}

	/** Sets whether the width of the child column is sizable.
	 * If true, an user can drag the border between two columns (e.g.,
	 * {@link HeaderWidget})
	 * to change the widths of adjacent columns.
	 * <p>Default: false.
	 * @param boolean sizable
	 */
	public setSizable(sizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._sizable;
		this._sizable = sizable;

		if (o !== sizable || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	public override isVisible(): boolean | undefined {
		return this._visible;
	}

	public override setVisible(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._visible;
		this._visible = v;

		if (o !== v || (opts && opts.force)) {
			this.rerender();
			var mesh = this.getMeshWidget();
			setTimeout(function () {
				// ZK-2217: fix height if mesh.desktop exists
				if (mesh && mesh.desktop) {
					// ZK-2130: should fix ebody height
					// ZK-2217: should contain foot and paging
					var foot = mesh.$n('foot'),
						pgib = mesh.$n('pgib'),
						hgh = zk(mesh).contentHeight() - mesh.$n_('head').offsetHeight
						- (foot ? foot.offsetHeight : 0) - (pgib ? pgib.offsetHeight : 0)
						- (mesh._nativebar && mesh.frozen ? mesh.frozen.$n_().offsetHeight : 0);
					mesh.ebody!.style.height = jq.px0(hgh);
				}
			}, 0);
		}

		return this;
	}

	public override removeChildHTML_(child: zul.mesh.HeaderWidget, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		if (!(this instanceof zul.mesh.Auxhead))
			for (var fs = zul.mesh.HeaderWidget._faker, i = fs.length; i--;)
				jq(child.uuid + '-' + fs[i], zk).remove();
	}

	//bug #3014664
	public override setVflex(v: string): void { //vflex ignored for Listhead/Columns/Treecols
		super.setVflex(false);
	}

	//bug #3014664
	public override setHflex(v: string): void { //hflex ignored for Listhead/Columns/Treecols
		super.setHflex(false);
	}

	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 * @since 5.0.5
	 */
	public getMeshWidget(): zul.mesh.MeshWidget | null {
		return this.parent;
	}

	public onColSize(evt: zk.Event & {widths?: string[]}): void {
		var owner = this.parent!,
			widths = evt.widths,
			headWidth: string | 0 = 0;
		if (widths) {
			for (var i = 0; i < widths.length; i++)
				headWidth += parseInt(widths[i]);
			if (headWidth != 0) headWidth = jq.px0(headWidth);
		}
		owner._innerWidth = headWidth || owner.eheadtbl!.width || owner.eheadtbl!.style.width;
		owner.fire('onInnerWidth', owner._innerWidth);
		owner.fireOnRender(zk.gecko ? 200 : 60);
		owner.disableAutoSizing_(); // clear span and sizedByContent without rerender
	}

	protected override bind_(desktop: zk.Desktop | null | undefined, skipper: zk.Skipper | null | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var w = this;
		after.push(function () {
			_syncFrozen(w);
		});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this.hdfaker).remove();
		jq(this.bdfaker).remove();
		jq(this.ftfaker).remove();
		super.unbind_(skipper, after, keepRod);
	}

	protected override insertChildHTML_(child: zul.mesh.HeaderWidget, before?: zk.Widget | null, desktop?: zk.Desktop | null): void {
		//ZK-2461: ie8 will take head-bar as one of columns, and equalize the width with other normal heads
		if (zk.ie9 && this.$n('bar')) {
			this.$n_('bar').style.display = 'none';
		}
		super.insertChildHTML_(child, before, desktop);
	}

	protected override onChildAdded_(child: zul.mesh.HeaderWidget): void {
		super.onChildAdded_(child);
		if (this.desktop) {
			var _getWidth = zul.mesh.MeshWidget._getWidth,
				mesh = this.getMeshWidget()!;
			if (!_fixOnChildChanged(this) && mesh && mesh._fixHeaders()) {
				// refix-ZK-2466 : grid dynamic add childern should 'syncSize' at the end (instead of 'add one and trigger one's onSize')
				mesh._syncSize(true);
			}
			_syncFrozen(this);
			mesh._minWd = null;
			var frozen = mesh.frozen;
			if (frozen)
				frozen.onSize();

			// B70-ZK-2128: Auxhead doesn't have to add faker.
			if (this instanceof zul.mesh.Auxhead)
				return;

			// ZK-2098: recovery the header faker if not exists
			var head = this,
				recoverFakerbar = !frozen && !!mesh._nativebar,
				fakers = ['hdfaker', 'bdfaker', 'ftfaker'];
			// B30-1926480: ie8 does not support array.forEach
			// eslint-disable-next-line @typescript-eslint/prefer-for-of
			for (var i = 0; i < fakers.length; i++) {
				var faker = fakers[i],
					$faker = jq(mesh['e' + faker]);
				// ZK-3643: Handle fakerbar/bar even if the faker exists
				if ($faker[0] != null) {
					//B70-ZK-2130: virtual bar doesn't have to add fakerbar
					//fkaker bar need recover if native bar has vscrollbar
					var $bar = jq(mesh).find('.' + head.$s('bar')), // head.$n('bar') still exists after remove
						bar = $bar[0],
						$hdfakerbar = jq(head.$n_('hdfaker')).find('[id*=hdfaker-bar]'),
						hdfakerbar = $hdfakerbar[0],
						barstyle = '', hdfakerbarstyle = '';

					// ZK-2096, ZK-2124: should refresh this.$n('bar') if children change with databinding
					// B30-1926480: the bar should be removed
					if ((faker == 'hdfaker') && bar) {
						var s: CSSStyleDeclaration;
						if (s = bar.style) {
							// ZK-2114: should not store display
							// barstyle = s.display ? 'display:' + s.display + ';' : '';
							barstyle += s.width ? 'width:' + s.width + ';' : '';
						}
						$bar.remove();
						this._subnodes['bar'] = null;

						if (recoverFakerbar && hdfakerbar && (s = hdfakerbar.style)) {
							hdfakerbarstyle = s.display ? 'display:' + s.display + ';' : '';
							hdfakerbarstyle += s.width ? 'width:' + s.width + ';' : '';
						}
						$hdfakerbar.remove();
						this._subnodes['hdfaker-bar'] = null;
					}

					if (!$faker.find(child.$n_(faker)).length) {
						var wd = _getWidth(child, child._hflexWidth ? child._hflexWidth + 'px' : child.getWidth());
						wd = wd ? 'width:' + wd + ';' : '';
						var html = '<col id="' + child.uuid + '-' + faker + '" style="' + wd + '"></col>',
							index = child.getChildIndex();
						// B30-1926480: child can be added after any brother node
						if (index > 0)
							jq($faker.find('col')[index - 1]).after(html);
						else
							$faker.append(html);
					}

					// resync var
					$bar = jq(mesh).find('.' + head.$s('bar'));
					bar = $bar[0];
					$hdfakerbar = jq(head.$n_('hdfaker')).find('[id*=hdfaker-bar]');
					hdfakerbar = $hdfakerbar[0];

					if ((faker == 'hdfaker') && !bar && recoverFakerbar) {
						if (!hdfakerbar)
							jq(head.$n_('hdfaker')).append('<col id="' + head.uuid + '-hdfaker-bar" style="' + hdfakerbarstyle + '" ></col>');
						jq(head).append('<th id="' + head.uuid + '-bar" class="' + head.$s('bar') + '" style="' + barstyle + '" ></th>');
					}
				}
			}
		}
	}

	protected override onChildRemoved_(child: zul.mesh.HeaderWidget): void {
		super.onChildRemoved_(child);
		if (this.desktop) {
			if (!_fixOnChildChanged(this) && !this.childReplacing_
				&& this.parent!._fixHeaders())
				this.parent!.onSize();
			this.parent!._minWd = null;
			// Fix IE, FF for the issue B30-1926480-1.zul and B30-1926480.zul
			var mesh = this.getMeshWidget()!;
			mesh.rerender(1);
		}
	}

	public override beforeChildrenFlex_(hwgt: zul.mesh.HeaderWidget): boolean { //HeaderWidget
		if (hwgt && !hwgt._flexFixed) {
			//bug #3033010
			//clear associated hdfaker and bdfaker
			var wgt = this.parent!,
				hdfaker = wgt.ehdfaker,
				bdfaker = wgt.ebdfaker,
				hdf = hdfaker ? hdfaker.firstChild : null,
				bdf = bdfaker ? bdfaker.firstChild : null;
			for (var h = this.firstChild; h; h = h.nextSibling) {
				// B70-ZK-2036: Do not adjust widget's width if it is not visible.
				if (h.isVisible() && h._nhflex! > 0) { //not min or undefined
					if (hdf) (hdf as HTMLElement).style.width = '';
					if (bdf) (bdf as HTMLElement).style.width = '';
				}
				if (hdf) hdf = hdf.nextSibling;
				if (bdf) bdf = bdf.nextSibling;
			}
		}
		return true;
	}

	public override afterChildrenFlex_(hwgt?: zul.mesh.HeaderWidget): void { //hflex in HeaderWidget
		var wgt = this.parent!,
			ebody = wgt.ebody!,
			ehead = wgt.ehead,
			efoot = wgt.efoot,
			currentLeft = wgt._currentLeft;
		if (wgt) {
			if (wgt._cssflex && wgt.isChildrenFlex()) {
				var minWd = wgt._calcMinWds(),
					wds: Record<string, number> | number[] = minWd.wds,
					isFlex = false,
					emptyHead = true;

				// reset display
				ehead!.style.display = '';
				var tblWidth = 0,
					sizedByContent = wgt.isSizedByContent();
				for (let i = 0, wd = -1, hwgt = this.firstChild; hwgt; hwgt = hwgt.nextSibling, i++) {
					var nhwgt = hwgt.$n_();
					if (hwgt._hflex == 'min' || (!hwgt._nhflex && sizedByContent))
						wd = wds[i];
					else if (hwgt._width)
						wd = parseInt(hwgt._width);
					else if (hwgt._nhflex! > 0)
						isFlex = true;

					if (!hwgt.isVisible())
						nhwgt.style.display = 'none';
					if (wd >= 0) {
						nhwgt.style.flex = '0 1 ' + jq.px(wd);
						if (!isFlex)
							tblWidth += wd;
						wd = -1; //reset
					}
					//check empty head
					if (hwgt.getLabel() || hwgt.getImage() || hwgt.nChildren)
						emptyHead = false;
				}

				//set table width
				var hdtbl = wgt.eheadtbl,
					bdtbl = wgt.ebodytbl,
					fttbl = wgt.efoottbl,
					tblWidthPx = jq.px0(tblWidth);
				if (hdtbl) {
					hdtbl.style.width = isFlex ? '100%' : tblWidthPx;
					if (bdtbl)
						bdtbl.style.width = isFlex ? '100%' : tblWidthPx;
					if (fttbl)
						fttbl.style.width = isFlex ? '100%' : tblWidthPx;
				}

				//set body column/footer column width
				var bdfaker = wgt.ebdfaker!,
					ftfaker = wgt.eftfaker,
					bdcol = bdfaker.firstChild,
					ftcol: ChildNode | null | undefined,
					width0 = zul.mesh.MeshWidget.WIDTH0;
				if (ftfaker)
					ftcol = ftfaker.firstChild;
				wds = {};
				for (let hwgt = this.firstChild; hwgt && bdcol; hwgt = hwgt.nextSibling) {
					wds[hwgt.uuid] = hwgt.$n_().getBoundingClientRect().width;
					bdcol = bdcol.nextSibling;
				}
				bdcol = bdfaker.firstChild;
				for (let hwgt = this.firstChild; hwgt && bdcol; hwgt = hwgt.nextSibling) {
					var wd = wds[hwgt.uuid],
						wdpx = jq.px0(wd);
					if (hwgt.isVisible() && wdpx !== undefined) {
						(bdcol as HTMLElement).style.width = wdpx;
						if (ftcol)
							(ftcol as HTMLElement).style.width = wdpx;
					} else {
						(bdcol as HTMLElement).style.width = width0;
						if (ftcol)
							(ftcol as HTMLElement).style.width = width0;
					}
					bdcol = bdcol.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
				}
				ehead!.style.display = emptyHead ? 'none' : '';
			} else {
				wgt._adjFlexWd();
				wgt._adjSpanWd(); //if there is span and shall span the column width for extra space

				// ZK-2772 Misaligned Grid columns
				if (wgt.frozen && !wgt._isAllWidths()) {
					wgt._calcSize(); // yes, we need to do it again.
				}
				// ZK-2551: need to restore scroll pos after flexs are fixed
				if (zk(ebody).hasHScroll() && currentLeft != ebody.scrollLeft) {
					ebody.scrollLeft = currentLeft;
					if (ehead)
						ehead.scrollLeft = currentLeft;
					if (efoot)
						efoot.scrollLeft = currentLeft;
				}
			}
		}
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}

	protected override getFlexDirection_(): string {
		return 'row';
	}

	public override afterClearFlex_(): void {
		var mesh = this.parent!;
		if (mesh._cssflex)
			mesh.ehdfaker!.style.display = '';
	}

	//static
	public static redraw(this: HeadWidget, out: string[]): void {
		out.push('<tr', this.domAttrs_(), ' style="text-align: left;">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		var mesh = this.getMeshWidget();
		if (mesh && mesh._nativebar)
			out.push('<th id="', this.uuid, '-bar" class="', this.$s('bar'), '" ></th>');
		out.push('</tr>');
	}
}
zul.mesh.HeadWidget = zk.regClass(HeadWidget);