/* Layout.ts

	Purpose:

	Description:

	History:
		Fri Aug  6 16:13:00 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeleton of Vlayout and Hlayout.
 * @since 5.0.4
 */
@zk.WrapClass('zul.box.Layout')
export class Layout extends zul.Widget {
	/** @internal */
	_spacing = '5px';
	/** @internal */
	_shallSize = false;

	/**
	 * Sets the spacing between adjacent children.
	 * @param spacing - the spacing (such as "0", "5px", "3pt" or "1em"),
	 * or null to use the default spacing. If the spacing is set to "auto",
	 * the DOM style is left intact, so the spacing can be customized from
	 * CSS.
	 * @see {@link getSpacing}
	 */
	setSpacing(spacing: string, opts?: Record<string, boolean>): this {
		const o = this._spacing;
		this._spacing = spacing;

		if (o !== spacing || opts?.force) {
			var n = this.$n(),
				vert = this.isVertical_(),
				spc = this._spacing;
			if (n)
				jq(n).children('div:not(:last-child)').css('padding-' + (vert ? 'bottom' : 'right'), (spc && spc != 'auto') ? spc : '');
		}

		return this;
	}

	/**
	 * @returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * @defaultValue `0.3em` (means to use the default spacing).
	 */
	getSpacing(): string {
		return this._spacing;
	}

	/** @internal */
	_chdextr(child: zk.Widget): HTMLElement | undefined {
		return child.$n('chdex') || child.$n();
	}

	/** @internal */
	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		if (before)
			jq(this._chdextr(before)).before(this.encloseChildHTML_(child));
		else {
			var jqn = jq(this.$n()),
			spc = this._spacing;
			jqn.children('div:last-child').css('padding-' + (this.isVertical_() ? 'bottom' : 'right'), (spc && spc != 'auto') ? spc : '');
			jqn.append(this.encloseChildHTML_(child));
		}
		child.bind(desktop);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	/**
	 * Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize(): void {
		this._shallSize = false;
		if (this.desktop) {
			// only fire when child has h/vflex
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (w._nvflex || w._nhflex) {
					zUtl.fireSized(this);
					break;
				}
			}
		}
	}

	onResponse(): void {
		if (this._shallSize)
			this.syncSize();
	}

	//Bug ZK-1579: should resize if child's visible state changed.
	/** @internal */
	override onChildVisible_(child: zk.Widget): void {
		super.onChildVisible_(child);
		if (this.desktop) {
			var n = child.$n('chdex');
			this._shallSize = true;
			//Bug ZK-1650: change chdex display style according to child widget
			if (n) n.style.display = child.isVisible() ? '' : 'none';
		}
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.desktop) {
			var n = child.$n('chdex');
			this._shallSize = true;
			//Bug ZK-1732: change chdex display style according to child widget
			if (n) n.style.display = child.isVisible() ? '' : 'none';
		}
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop)
			this._shallSize = true;
	}

	/** @internal */
	override removeChildHTML_(child: zk.Widget, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		jq(child.uuid + '-chdex', zk).remove();
		if (this._spacing != 'auto' && this.lastChild == child)
			jq(this.$n()).children('div:last-child').css('padding-' + (this.isVertical_() ? 'bottom' : 'right'), '');
	}

	/**
	 * Enclose child with HTML tag such as DIV,
	 * and return a HTML code or add HTML fragments in out array.
	 * @param child - the child which will be enclosed
	 * @param out - an array of HTML fragments.
	 * @internal
	 */
	encloseChildHTML_(child: zk.Widget, out?: string[]): string {
		var oo = new zk.Buffer(),
			vert = this.isVertical_(),
			spc = this._spacing;

		oo.push('<div id="' + child.uuid + '-chdex" class="' + this.$s('inner') + '"');
		if (spc && spc != 'auto') {
			oo.push(' style="', !child.isVisible() ? 'display:none;' : ''); //Bug ZK-1650: set chdex display style according to child widget
			var next = child.nextSibling; //Bug ZK-1526: popup should not consider spacing
			if (next && !(next instanceof zul.wgt.Popup) && !(child instanceof zul.wgt.Popup))
				oo.push('padding-', vert ? 'bottom:' : 'right:', spc);
			oo.push('"');
		}
		oo.push('>');
		child.redraw(oo);
		oo.push('</div>');

		if (!out) {
			return oo.join('');
		} else {
			const output = oo.join('');
			out.push(output);
			return output;
		}

	}

	/**
	 * @returns whether the layout is vertical
	 * @internal
	 */
	isVertical_(): boolean {
		return false;
	}

	/** @internal */
	_resetBoxSize(vert: boolean): void {
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			var chdex = kid.$n('chdex');
			// ZK-1861: Js error when flex + visible = false
			if (chdex) {
				//ZK-1679: clear height only vflex != min, clear width only hflex != min
				if (vert && kid._nvflex && kid.getVflex() != 'min') {
					var n: HTMLElement | undefined;
					if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) { // keep the scroll status
						// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
					} else {
						kid.setFlexSize_({height: '', width: ''});
					}
					if (chdex)
						chdex.style.height = '';
				}
				if (!vert && kid._nhflex && kid.getHflex() != 'min') {
					var n: HTMLElement | undefined;
					if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) { // keep the scroll status
						// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
					} else {
						kid.setFlexSize_({height: '', width: ''});
					}
					if (chdex)
						chdex.style.width = '';
				}
			}
		}
	}

	//bug#3296056
	/** @internal */
	override afterResetChildSize_(orient: string): void {
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			var chdex = kid.$n('chdex');
			if (chdex) {
				if (orient == 'h')
					chdex.style.height = '';
				if (orient == 'w')
					chdex.style.width = '';
				chdex.style.minWidth = '1px'; //Bug ZK-1509: add minium 1px width to pass isWatchable_
			}
		}
	}

	//bug#3042306
	/** @internal */
	override resetSize_(orient: zk.FlexOrient): void { ////@Overrid zk.Widget#resetSize_, called when beforeSize
		super.resetSize_(orient);
		var vert = this.isVertical_();
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			if (vert ? (kid._nvflex && kid.getVflex() != 'min')
					: (kid._nhflex && kid.getHflex() != 'min')) {

				var chdex = kid.$n('chdex');
				if (chdex) {
					if (orient == 'h')
						chdex.style.height = '';
					if (orient == 'w')
						chdex.style.width = '';
				}
			}
		}
	}

	/** @internal */
	override getChildMinSize_(attr: string, wgt: zk.Widget): number { //'w' for width or 'h' for height
		var el = wgt.$n()!; //Bug ZK-1578: should get child size instead of chdex size
		//If child uses hflex="1" when parent has hflex="min"
		//   Find max sibling width and apply on the child
		if (attr == 'w' && wgt._hflex && this.isVertical_()) {
			for (var w = wgt.nextSibling, max = 0, width: number; w; w = w.nextSibling) {
				if (!w._hflex) {
					width = zjq.minWidth(w.$n_());
					max = width > max ? width : max;
				}
			}
			return max;
		}
		if (attr == 'h') { // if display is not block the offsetHeight is wrong
			return zk(el.parentNode).contentHeight();
		} else {
			return zjq.minWidth(el); //See also bug ZK-483
		}
	}

	//Bug ZK-1577: should consider spacing size of all chdex node
	/** @internal */
	override getContentEdgeHeight_(height: number): number {
		var h = 0;
		for (var kid = this.firstChild; kid; kid = kid.nextSibling)
			h += zk(kid.$n('chdex')).paddingHeight();

		return h;
	}

	//Bug ZK-1577: should consider spacing size of all chdex node
	/** @internal */
	override getContentEdgeWidth_(width: number): number {
		var w = 0;
		for (var kid = this.firstChild; kid; kid = kid.nextSibling)
			w += zk(kid.$n('chdex')).paddingWidth();

		return w;
	}

	/** @internal */
	override beforeChildrenFlex_(child: zk.Widget): boolean {
		// optimized for performance
		this._shallSize = false;
		child._flexFixed = true;

		var	vert = this.isVertical_(),
			vflexs: zk.Widget[] = [],
			vflexsz = vert ? 0 : 1,
			hflexs: zk.Widget[] = [],
			hflexsz = !vert ? 0 : 1,
			p = this.$n(),
			psz = child.getParentSize_(p!),
			zkp = zk(p),
			hgh = psz.height,
			wdh = psz.width,
			xc = this.firstChild,
			scrWdh;

		if (!zk.mounting) { // ignore for the loading time
			this._resetBoxSize(vert);
		}

		// Bug 3185686, B50-ZK-452
		if (zkp.hasVScroll()) //with vertical scrollbar
			wdh -= (scrWdh = jq.scrollbarWidth());

		// B50-3312936.zul
		if (zkp.hasHScroll()) //with horizontal scrollbar
			hgh -= scrWdh || jq.scrollbarWidth();

		for (; xc; xc = xc.nextSibling) {
			//Bug ZK-2434: not considering the element with vparent (like popup)
			var zkc: zk.JQZK;
			if (xc.getMold() == 'nodom') { //ZK-4354: a nodom sibling causes hflex=1 calculate the wrong size
				var fc = xc.firstChild;
				if (fc) {
					var xcp = fc.$n()!.parentNode as HTMLElement,
						zkxcp = zk(xcp);
					if (vert)
						hgh -= zkxcp.offsetHeightDoubleValue() + zkxcp.marginHeight();
					else
						wdh -= xcp.offsetWidth + zkxcp.marginWidth();
				}
				continue;
			}
			if (xc.isVisible() && !(zkc = zk(xc)).hasVParent()) {
				var cwgt = xc,
					c = cwgt.$n()!,
					cp = c.parentNode as HTMLElement,
					zkxc = zk(cp);
				//vertical size
				if (xc && xc._nvflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						cwgt.fixMinFlex_(c, 'h');

						//Bug ZK-1577: should consider padding size
						var h = c.offsetHeight + zkc.marginHeight() + zkxc.padBorderHeight();
						cp.style.height = jq.px0(h);
						if (vert)
							hgh -= cp.offsetHeight + zkxc.marginHeight();
					} else {
						vflexs.push(cwgt);
						if (vert) {
							vflexsz += cwgt._nvflex!;

							//bug#3157031: remove chdex's padding, border, margin
							hgh = hgh - zkxc.marginHeight();
						}
					}
				} else if (vert) {
					hgh -= zkxc.offsetHeightDoubleValue() + zkxc.marginHeight();
				}

				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						cwgt.fixMinFlex_(c, 'w');

						//Bug ZK-1577: should consider padding size
						var w = c.offsetWidth + zkc.marginWidth() + zkxc.padBorderWidth();
						cp.style.width = jq.px0(zkxc.revisedWidth(w));
						if (!vert)
							wdh -= cp.offsetWidth + zkxc.marginWidth();
					} else {
						hflexs.push(cwgt);
						if (!vert) {
							hflexsz += cwgt._nhflex;

							//bug#3157031: remove chdex's padding, border, margin
							wdh = wdh - zkxc.marginWidth();
						}
					}
				} else if (!vert)
					wdh -= cp.offsetWidth + zkxc.marginWidth();
			}
		}

		//setup the height for the vflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		var lastsz = hgh > 0 ? hgh : 0;
		while (vflexs.length > 1) {
			var cwgt = vflexs.shift()!,
				vsz = (vert ? (cwgt._nvflex! * hgh / vflexsz) : hgh) | 0, //cast to integer
				isz = vsz,
				chdex = cwgt.$n('chdex')!,
				minus = zk(chdex).padBorderHeight();

			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height: isz - minus});
			cwgt._vflexsz = vsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.height = jq.px0(vsz);
			if (vert) lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift()!,
				isz = lastsz,
				chdex = cwgt.$n('chdex')!,
				minus = zk(chdex).padBorderHeight();

			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height: isz - minus});
			cwgt._vflexsz = lastsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.height = jq.px0(lastsz);
		}
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		while (hflexs.length > 1) {
			var cwgt = hflexs.shift()!, //{n: node, f: hflex}
				hsz = (vert ? wdh : (cwgt._nhflex! * wdh / hflexsz)) | 0, //cast to integer
				chdex = cwgt.$n('chdex')!,
				minus = zk(chdex).padBorderWidth();

			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width: hsz - minus});
			cwgt._hflexsz = hsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.width = jq.px0(hsz);

			if (!vert) lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift()!,
				chdex = cwgt.$n('chdex')!,
				minus = zk(chdex).padBorderWidth();

			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width: lastsz - minus});
			cwgt._hflexsz = lastsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.width = jq.px0(lastsz);
		}

		//notify all of children with xflex is done.
		child.parent!.afterChildrenFlex_(child);
		child._flexFixed = false;

		return false; //to skip original _fixFlex
	}

	/** @internal */
	override afterChildrenMinFlex_(opts: string): void {
		var n = this.$n()!;
		if (opts == 'h') {
			if (this.isVertical_()) {
				var total = 0;
				for (var w = n.firstChild; w; w = w.nextSibling) {
					var fchd = w.firstChild as HTMLElement;
					if (fchd.style.height) {
						var hgh = fchd.offsetHeight
								+ zk(w).padBorderHeight()
								+ zk(fchd).marginHeight();
						(w as HTMLElement).style.height = jq.px0(hgh);
						total += hgh;
					} else
						total += (w as HTMLElement).offsetHeight;
				}
				n.style.height = jq.px0(total);
			} else {
				var max = 0;
				for (var w = n.firstChild; w; w = w.nextSibling) {
					// use w.offsetHeight instead of w.firstChild.offsetHeight
					// for avoiding span's special gap when using HTML5 doctype
					var h = (w as HTMLElement).offsetHeight + zk(w.firstChild).marginHeight();
					if (h > max)
						max = h;
				}
				n.style.height = jq.px0(max);
			}
		} else {
			if (!this.isVertical_()) {
				var total = 0;
				for (var w = n.firstChild; w; w = w.nextSibling) {
					var fchd = w.firstChild as HTMLElement;
					if (fchd.style.width) {
						var wdh = fchd.offsetWidth
								+ zk(w).padBorderWidth()
								+ zk(fchd).marginWidth();
						(w as HTMLElement).style.width = jq.px0(wdh);
						total += wdh;
					} else
						total += (w as HTMLElement).offsetWidth;
				}

				// IE9+ bug ZK-483
				if (zk.ie11 && this._hflexsz)
					total = Math.max(this._hflexsz, total);

				n.style.width = jq.px0(total);
			} else {
				var max = 0;
				for (var w = n.firstChild; w; w = w.nextSibling) {
					var wd = (w.firstChild as HTMLElement).offsetWidth + zk(w.firstChild).marginWidth();
					if (wd > max)
						max = wd;
				}

				// IE9+ bug ZK-483
				if (zk.ie11 && this._hflexsz)
					max = Math.max(this._hflexsz, max);

				n.style.width = jq.px0(max);
			}
		}
	}
}
