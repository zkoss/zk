/* eslint-disable @typescript-eslint/dot-notation */
/* dom.ts

	Purpose:
		Enhance jQuery
	Description:

	History:
		Fri Jun 12 10:44:53 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export interface SlideOptions {
	anchor?: string;
	easing?: string;
	duration?: number;
	beforeAnima?(self: zk.JQZK): void;
	afterAnima?(node: HTMLElement): void; // zul/inp/ComboWidget
}

export interface Dimension {
	width: number;
	height: number;
	left: number;
	top: number;
}

export interface PositionOptions {
	overflow?: boolean;
	dodgeRef?: boolean;
}

export interface RedoCSSOptions {
	fixFontIcon: boolean;
	selector: string;
}

var _txtStyles = [ //refer to http://www.w3schools.com/css/css_text.asp
		'font-family', 'font-size', 'font-weight', 'font-style',
		'letter-spacing', 'line-height', 'text-align', 'text-decoration',
		'text-indent', 'text-shadow', 'text-transform', 'text-overflow',
		'direction', 'word-spacing', 'white-space'],
	_txtFontStyles = ['font-style', 'font-variant', 'font-weight', 'font-size', 'font-family'],
	_txtStyles2 = ['color', 'background-color', 'background'],
	_zsyncs: ZSyncObject[] = [],
	_pendzsync = 0,
	_vpId = 0, //id for virtual parent's reference node
	_sbwDiv: undefined | HTMLDivElement & {_value?: number}; //scrollbarWidth

function _overflowElement(self: JQZK, recursive): [HTMLElement, HTMLElement][] {
	// eslint-disable-next-line zk/noNull
	var el: Node | undefined | null = self.jq[0],
		te, le, oels: [HTMLElement, HTMLElement][] = [];
	do {
		if (!te) {
			if (el == DocRoot() || (el as HTMLElement).style.overflow == 'auto' || (el as HTMLElement).style.overflowY == 'auto' || jq(el).css('overflow-y') == 'auto')
				te = el;
		}
		if (!le) {
			if (el == DocRoot() || (el as HTMLElement).style.overflow == 'auto' || (el as HTMLElement).style.overflowX == 'auto' || jq(el).css('overflow-x') == 'auto')
				le = el;
		}
		if (te && le) {
			oels.push([le as HTMLElement, te as HTMLElement]);
			if (!recursive)
				break;
			te = le = undefined;
		}
		el = el.parentNode;
	} while (el && (el != document));

	return oels;
}

function _ofsParent(el: HTMLElement): HTMLElement {
	if (el.offsetParent) return el.offsetParent as HTMLElement;
	if (el == document.body) return el;

	// eslint-disable-next-line zk/noNull
	let curr: HTMLElement | null = el;
	while ((curr = curr.parentElement) && curr != document.body)
		if (curr.style && jq(curr).css('position') != 'static') //in IE, style might not be available
			return curr;

	return document.body;
}
function _zsync(org: zk.Object): void {
	if (--_pendzsync <= 0)
		for (var j = _zsyncs.length; j--;)
			_zsyncs[j].zsync(org);
}
function _focus(n: HTMLElement): void {
	zk.afterAnimate(function () {
		try {
			n.focus();
			var w = zk.Widget.$(n);
			if (w) zk.currentFocus = w;

			zjq.fixInput(n);
		} catch (e) {
			zk.debugLog((e as Error).message ?? e);
		}
	}, -1); //FF cannot change focus to a DOM element being animated
}
function _select(n: HTMLInputElement | HTMLTextAreaElement): void {
	try {
		n.select();
	} catch (e) {
		zk.debugLog((e as Error).message ?? e);
	}
}

function _submit(this: HTMLElement): void {
	if (this instanceof HTMLFormElement) {
		if (this.requestSubmit) {
			this.requestSubmit();
		} else {
			jq.Event.fire(this, 'submit');
			this.submit();
		}
	}
}

function _dissel(this: HTMLElement): void {
	var $this = jq(this);
	$this.css('user-select', 'none');
}
function _ensel(this: HTMLElement): void {
	var $this = jq(this);
	$this.css('user-select', '');
}

type ScrollIntoViewInfo = { oft: zk.Offset; h: number; w: number; el: HTMLElement } | undefined;
// eslint-disable-next-line zk/noNull
function _scrlIntoView(outer: HTMLElement | null, inner: HTMLElement | null, info: ScrollIntoViewInfo | undefined, excludeHorizontal: boolean): ScrollIntoViewInfo {
	if (outer && inner) {
		var ooft = zk(outer).revisedOffset(),
			ioft = info ? info.oft : zk(inner).revisedOffset(),
			top = ioft[1] - ooft[1]
				+ (outer == DocRoot() ? 0 : outer.scrollTop),
			left = ioft[0] - ooft[0]
				+ (outer == DocRoot() ? 0 : outer.scrollLeft),
			ih = info ? info.h : inner.offsetHeight,
			iw = info ? info.w : inner.offsetWidth,
			right = left + iw,
			bottom = top + ih,
			updated;
		//for fix the listbox(livedate) keydown select always at top
		if (/*outer.clientHeight < inner.offsetHeight || */ outer.scrollTop > top) {
			outer.scrollTop = top;
			updated = true;
		} else if (bottom > outer.clientHeight + outer.scrollTop) {
			outer.scrollTop = !info ? bottom : bottom - (outer.clientHeight + (inner.parentNode == outer ? 0 : outer.scrollTop));
			updated = true;
		}

		// ZK-1924:	scrollIntoView can also adjust horizontal scroll position.
		// ZK-2193: scrollIntoView support exclude horizontal
		if (!excludeHorizontal)
			if (outer.scrollLeft > left) {
				outer.scrollLeft = left;
				updated = true;
			} else if (right > outer.clientWidth + outer.scrollLeft) {
				outer.scrollLeft = !info ? right : right - (outer.clientWidth + (inner.parentNode == outer ? 0 : outer.scrollLeft));
				updated = true;
			}

		if (updated || !info) {
			if (!info)
				info = {
					oft: ioft,
					h: inner.offsetHeight,
					w: inner.offsetWidth,
					el: inner
				};
			else info.oft = zk(info.el).revisedOffset();
		}

		return info;
	}
}


function _cmOffset(el: HTMLElement): zk.Offset {
	var t = 0, l = 0, operaBug;
	//Fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute
	if (zk.gecko) {
		var p = el.parentElement;
		while (p && p != document.body) {
			var $p = jq(p),
				style = $p.css('position');
			if (style == 'relative' || style == 'absolute') {
				t += zk.parseInt($p.css('border-top-width'));
				l += zk.parseInt($p.css('border-left-width'));
			}
			p = p.offsetParent as HTMLElement;
		}
	}

	do {
		//Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
		var $el = jq(el);
		if ($el.css('position') == 'fixed') {
			t += jq.innerY() + el.offsetTop;
			l += jq.innerX() + el.offsetLeft;
			break;
		} else {
			//Fix opera bug. If the parent of "input" or "span" is "div"
			// and the scrollTop of "div" is more than 0, the offsetTop of "input" or "span" always is wrong.
			if (zk.opera) {
				if (operaBug && jq.nodeName(el, 'div') && el.scrollTop != 0)
					t += el.scrollTop || 0;
				operaBug = jq.nodeName(el, 'span', 'input');
			}
			t += el.offsetTop || 0;
			l += el.offsetLeft || 0;
			//Bug 1721158: In FF, el.offsetParent is null in this case
			el = zk.gecko && el != document.body ?
				_ofsParent(el) : el.offsetParent as HTMLElement;
		}
	} while (el);
	return [l, t];
}
function _posOffset(el: HTMLElement): zk.Offset {
	if (zk.webkit && el instanceof HTMLTableRowElement && el.cells.length)
		el = el.cells[0];

	var t = 0, l = 0;
	do {
		t += el.offsetTop || 0;
		l += el.offsetLeft || 0;
		//Bug 1721158: In FF, el.offsetParent is null in this case
		el = zk.gecko && el != document.body ?
			_ofsParent(el) : el.offsetParent as HTMLElement;
		if (el) {
			if (jq.nodeName(el, 'body')) break;
			var p = jq(el).css('position');
			if (p == 'relative' || p == 'absolute') break;
		}
	} while (el);
	return [l, t];
}
function _addOfsToDim($this: JQZK, dim: Partial<zk.Dimension>, revised?: boolean): zk.Dimension {
	if (revised) {
		var ofs = $this.revisedOffset();
		dim.left = ofs[0];
		dim.top = ofs[1];
	} else {
		dim.left = $this.offsetLeft();
		dim.top = $this.offsetTop();
	}
	return dim as zk.Dimension;
}

//redoCSS
var _rdcss: HTMLElement[] = [];
function _redoCSS0(): void {
	if (_rdcss.length) {
		for (var el: HTMLElement | undefined; el = _rdcss.pop();)
			try {
				zjq._fixCSS(el);
			} catch (e) {
				zk.debugLog((e as Error).message ?? e);
			}

		// just in case
		setTimeout(_redoCSS0);
	}
}

// since ZK 7.0.0
var isHTML5DocType = (function () {
	var html5;
	return function (): boolean {
		if (html5 === undefined) {
			// eslint-disable-next-line zk/noNull
			if (document.doctype === null) return false;

			var node = document.doctype,
				doctype_string = '<!DOCTYPE ' + node.name
					+ (node.publicId ? ' PUBLIC"' + node.publicId + '"' : '')
					+ (!node.publicId && node.systemId ? ' SYSTEM' : '')
					+ (node.systemId ? ' "' + node.systemId + '"' : '') + '>';

			html5 = doctype_string === '<!DOCTYPE html>';
		}
		return !!html5;
	};
})();

// cache
let _txtStylesCamel: string[] = [],
	// eslint-disable-next-line zk/noNull
	_txtSizDiv: HTMLElement | null,
	_defaultStyle = 'left:-1000px;top:-1000px;position:absolute;visibility:hidden;border:none;display:none;',
	_cache: Record<string, zk.Offset | undefined> = {};

// refix ZK-2371
var DocRoot = (function () {
	var docRoot: undefined | HTMLElement,
		// document.body may not be initiated.
		initDocRoot = function (): HTMLElement {
			return docRoot = document.documentElement;
		};
	return function (): HTMLElement {
		return docRoot || initDocRoot();
	};
})();

/**
 * @import zk.Widget
 * Represents the object returned by the `zk` function, or by
 * {@link jq.zk}.
 * For example, `zk('#id');`
 *
 * <p>Refer to {@link JQZK.jq} for more information.
 *
 * <h3>Other Extension</h3>
 * <ul>
 * <li>{@link JQZK.jq} - the object returned by `jq(...)`. The original jQuery API.</li>
 * <li>{@link JQZK.jq} - DOM utilities (such as, {@link jq.innerX}</li>
 * <li>{@link jq.Event} - the event object passed to the event listener</li>
 * </ul>
 */
export class JQZK {
	/**
	 * The associated instance of {@link JQZK.jq}, the object returned by `jq(...)`.
	 */
	declare jq: JQuery;
	constructor(jq: JQuery) { //ZK extension
		this.jq = jq;
	}
	/**
	 * Cleans, i.e., reset, the visibility (of the CSS style) for the matched elements.
	 * Depending on the browser, the reset visibility is either visible or inherit.
	 */
	cleanVisibility(): JQuery {
		return this.jq.each(function (this: HTMLElement) {
			zjq._cleanVisi(this);
		});
	}
	/**
	 * @returns whether the first matched element is visible. `false` if not exist; `true` if no style attribute.
	 * @param strict - whether the visibility property must not be hidden, too.
	 * If false, only the style.display property is tested.
	 * If true, both the style.display and style.visibility properties are tested.
	 */
	isVisible(strict?: boolean): boolean {
		var n = this.jq[0];
		return n && (!n.style || (n.style.display != 'none' && (!strict || jq.inArray(n.style.visibility, ['hidden', 'collapse']) == -1)));
	}
	/**
	 * @returns whether the first match element is really visible.
	 * By real visible we mean the element and all its ancestors are visible.
	 * @param strict - whether the visibility property must not be hidden, too.
	 * If false, only the style.display property is tested.
	 * If true, both the style.display and style.visibility properties are tested.
	 */
	isRealVisible(strict?: boolean): boolean {
		// eslint-disable-next-line zk/noNull
		var n: HTMLElement | null = this.jq[0];
		return n && this.isVisible(strict) && (n.offsetWidth > 0 || n.offsetHeight > 0
			|| (!n.firstChild
				&& (!(n = n.parentElement) || n == document.body || zk(n).isRealVisible(strict))));
		//Bug 3141549: look up parent if !firstChild (no check if firstChild for better performance)
	}

	/**
	 * Scrolls the browser window to make the first matched element visible.
	 * <p>Notice that it scrolls only the browser window. If the element is obscured by another element, it is still not visible. To really make it visible, use el.scrollIntoView() instead.
	 */
	scrollTo(): this {
		if (this.jq.length) {
			var pos = this.cmOffset();
			scrollTo(pos[0], pos[1]);
		}
		return this;
	}
	/**
	 * Causes the first matched element to scroll into view.
	 * @param parent - scrolls the first matched element into the parent's view,
	 * if any. Otherwise, document.body is assumed.
	 */
	scrollIntoView(parent?: Element): this {
		// ZK-3330: scroll into view after animation
		zk.afterAnimate(() => {
			this._scrollIntoView(parent);
		}, -1);
		return this;
	}
	/** @internal */
	_scrollIntoView(parent?: Element | boolean): this {
		// eslint-disable-next-line zk/noNull
		var n: HTMLElement | null = this.jq[0];
		if (n) {
			var real = jq('#' + n.id + '-real')[0];
			if (real)
				n = real;

			// only scroll when the target is not into view
			// for test case B60-ZK-1202.zul
			if (!this.isScrollIntoView(true)) {
				// fix browser's scrollIntoView issue, when offsetParent has absolute position.
				// for example, B65-ZK-2296-1.zul and B60-ZK-1202.zul
				var isAbsolute = parent,
					p = n;
				if (!isAbsolute) {
					while (p) {
						if (p == document.body) break;
						if (jq(p).css('position') == 'absolute') {
							isAbsolute = true;
							break;
						}
						p = p.offsetParent as HTMLElement;
					}
				}

				// check whether the n is an instance of ItemWidget
				// for B65-ZK-2193.zul, to have better scrollIntoView's behavior
				if (!isAbsolute && zk.isLoaded('zul.sel')) {
					var w = zk.Widget.$(n);
					isAbsolute = w && w instanceof zul.sel.ItemWidget;
				}

				if (isAbsolute) {
					const parent1 = parent || document.documentElement;
					// eslint-disable-next-line zk/noNull
					for (let p: HTMLElement | null = n, c: undefined | ScrollIntoViewInfo; (p = p.parentElement) && n != parent1; n = p)
						c = _scrlIntoView(p, n, c, true);
				} else {
					// use browser's scrollIntoView() method instead of ours for F70-ZK-1924.zul
					zk.delayFunction(this.$().uuid, function () {
						n!.scrollIntoView();
					}, {urgent: true});
				}
			}
		}
		return this;
	}
	/**
	 * Checks whether the element is shown in the current viewport (consider both native and fake scrollbar).
	 * @returns if false, it means the element is not shown.
	 * @since 7.0.6
	 */
	isRealScrollIntoView(opt?: boolean): boolean {
		var wgt = this.$();
		if (!wgt)
			return false;
		var desktop = wgt.desktop,
			p = wgt.parent,
			n = this.jq[0],
			bar: undefined | zul.Scrollbar,
			inView = true;

		// ZK-2069: check whether the input is shown in parents' viewport.
		while (p && p != desktop) {
			bar = p['_scrollbar'] as undefined | zul.Scrollbar;
			if (bar && (bar.hasVScroll() || bar.hasHScroll())) {
				inView = bar.isScrollIntoView(n);
				if (!inView)
					return inView;
			}
			bar = undefined;
			p = p.parent;
		}
		// ZK-2069: should check native and fake scrollbar case
		return inView && this.isScrollIntoView(true);
	}
	/**
	 * Checks whether the element is shown in the current viewport.
	 * @returns if false, it means the element is not shown.
	 * @since 6.5.2
	 */
	isScrollIntoView(recursive?: boolean): boolean {// ZK-2069: can check whether the element is shown in parents' viewport.
		var vOffset = this.viewportOffset(),
			x = vOffset[0],
			y = vOffset[1],
			w = this.jq[0].offsetWidth,
			h = this.jq[0].offsetHeight,
			x1 = x + w,
			y1 = y + h;

		// browser's viewport
		if (x >= 0 && y >= 0 && x1 <= jq.innerWidth() && y1 <= jq.innerHeight()) {
			var oels = _overflowElement(this, recursive),
				inView = true;
			for (var i = 0; i < oels.length; i++) {
				// ZK-2619 : Errorbox not shown when WrongValueException is thrown on a multiline textbox
				var oel = oels[i],
					le = this.jq[0] == oel[0] ? oel[0].parentElement : oel[0],
					te = this.jq[0] == oel[1] ? oel[1].parentElement : oel[1],
					lex = le ? zk(le).viewportOffset()[0] : 0,
					tey = te ? zk(te).viewportOffset()[1] : 0;
				// scrollbar's viewport
				inView = (x >= lex && x1 <= lex + (le?.offsetWidth ?? 0) && y >= tey && y1 <= tey + (te?.offsetHeight ?? 0));
				if (!inView)
					return inView;
			}
			return inView;
		}
		return false;
	}

	/**
	 * @returns whether the first matched DOM element has the vertical scrollbar
	 * @since 5.0.8
	 */
	hasVScroll(): boolean {
		var w: zk.Widget & {_scrollbar?: zul.Scrollbar} | undefined;
		if ((w = this.$()) && w._scrollbar) {// support a fake scrollbar
			return w._scrollbar.hasVScroll();
		}
		var n: undefined | HTMLElement, scrollbarWidth = 0; //zk-3938: if zoom-in, the scrollbarWidth will be smaller than 11.
		if (n = this.jq[0]) {
			var borderWidth = Math.round(jq.css(n, 'borderLeftWidth', true))
				+ Math.round(jq.css(n, 'borderRightWidth', true));
			scrollbarWidth = n.offsetWidth - borderWidth - n.clientWidth;
		}
		return scrollbarWidth > 0;
	}
	/**
	 * @returns whether the first matched DOM element has the horizontal scrollbar
	 * @since 5.0.8
	 */
	hasHScroll(): boolean {
		var w: zk.Widget & {_scrollbar?: zul.Scrollbar} | undefined;
		if ((w = this.$()) && w._scrollbar) {// support a fake scrollbar
			return w._scrollbar.hasHScroll();
		}
		var n: undefined | HTMLElement, scrollbarHeight = 0; //zk-3938: if zoom-in, the scrollbarHeight will be smaller than 11.
		if (n = this.jq[0]) {
			var borderHeight = Math.round(jq.css(n, 'borderTopWidth', true))
				+ Math.round(jq.css(n, 'borderBottomWidth', true));
			scrollbarHeight = n.offsetHeight - borderHeight - n.clientHeight;
		}
		return scrollbarHeight > 0;
	}

	/**
	 * Tests if the first matched element is overlapped with the specified
	 * element.
	 * @param el - the element to check with
	 * @param tolerant - tolerant value for the calculation
	 * @returns true if they are overlapped.
	 */
	isOverlapped(el: HTMLElement, tolerant?: number): boolean {
		var n: undefined | HTMLElement;
		if (n = this.jq[0])
			return jq.isOverlapped(
				// use revisedOffset instead of cmOffset for body's scroll issue
				this.revisedOffset(), [n.offsetWidth, n.offsetHeight], zk(el).revisedOffset(),
				[el.offsetWidth, el.offsetHeight], tolerant);
		return false;
	}

	/**
	 * @returns the summation of the specified styles.
	 * For example,
	 * ```ts
	 * jq(el).zk.sumStyles("lr", jq.paddings);
	 * //sums the style values of jq.paddings['l'] and jq.paddings['r'].
	 * ```
	 *
	 * @param areas - the areas is abbreviation for left "l", right "r", top "t", and bottom "b". So you can specify to be "lr" or "tb" or more.
	 * @param styles - an array of styles, such as {@link jq.paddings}, {@link jq.margins} or {@link jq.borders}.
	 */
	sumStyles(areas: string, styles: Record<string, string>): number {
		var val = 0;
		for (var i = 0, len = areas.length, $jq = this.jq; i < len; i++) {
			var w = Math.round(zk.parseFloat($jq.css(styles[areas.charAt(i)])));
			if (!isNaN(w)) val += w;
		}
		return val;
	}

	/**
	 * Converts the specified offset in the element's coordinate to
	 * to the browser window's coordinateReturns the revised (calibrated) offset, i.e., the offset of the element
	 * related to the screen.
	 * <p>It is calculated by subtracting the offset of the scrollbar
	 * ({@link scrollOffset} and {@link jq.innerX}), for the first matched element.
	 * @param ofs - the offset to revise. If not specified, the first matched
	 * element's bounding rectange is assumed.
	 * @returns the revised (i.e., browser's coordinate) offset of the selected element.
	 * In other words, it is the offset of the left-top corner related to the browser window.
	 * @see {@link cmOffset}
	 */
	revisedOffset(ofs?: zk.Offset): zk.Offset {
		var el = this.jq[0];
		if (!ofs) {
			if (el.getBoundingClientRect) { // IE and FF3
				var elst: undefined | CSSStyleDeclaration, oldvisi: undefined | string,
					rect = el.getBoundingClientRect(),
					b = [rect.left + jq.innerX() - el.ownerDocument.documentElement.clientLeft,
						rect.top + jq.innerY() - el.ownerDocument.documentElement.clientTop] as zk.Offset;

				if (elst) {
					elst.display = 'none';
					elst.visibility = oldvisi!;
				}
				// fix float number issue for ZTL B50-3298164
				b[0] = Math.ceil(b[0]);
				b[1] = Math.ceil(b[1]);

				return b;
				// IE adds the HTML element's border, by default it is medium which is 2px
				// IE 6 and 7 quirks mode the border width is overwritable by the following css html { border: 0; }
				// IE 7 standards mode, the border is always 2px
				// This border/offset is typically represented by the clientLeft and clientTop properties
				// However, in IE6 and 7 quirks mode the clientLeft and clientTop properties are not updated when overwriting it via CSS
				// Therefore this method will be off by 2px in IE while in quirksmode
			}
			ofs = this.cmOffset();
		}
		var parent = el.parentElement,
			scrolls = parent ? zk(parent).scrollOffset() : [0, 0];
		scrolls[0] -= jq.innerX(); scrolls[1] -= jq.innerY();
		return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
	}
	/**
	 * @returns the revised (calibrated) width, which subtracted the width of
	 * its CSS border or padding, for the first matched element if the box-sizing
	 * is not in the border-box mode.
	 * <p>It is usually used to assign the width to an element (since we have to subtract the padding).
	 * ```ts
	 * el.style.width = zk(parentEL).revisedWidth(100);
	 * ```
	 *
	 * @param size - the width to be assigned to the specified element.
	 * @param excludeMargin - whether to subtract the margins, too.
	 * You rarely need this unless the width is specified in term of the parent's perspective.
	 * @see {@link contentWidth}
	 */
	revisedWidth(size: number, excludeMargin?: boolean): number {
		if (this.jq.css('box-sizing') != 'border-box')
			size -= this.padBorderWidth();
		if (size > 0 && excludeMargin)
			size -= this.marginWidth();
		return size < 0 ? 0 : size;
	}
	/**
	 * @returns the revised (calibrated) height, which subtracted the height of
	 * its CSS border or padding, for the first matched element if the box-sizing
	 * is not in the border-box mode.
	 * <p>It is usually used to assign the height to an element
	 * (since we have to subtract the padding).
	 * ```ts
	 * el.style.height = zk(parentEL).revisedHeight(100);
	 * ```
	 *
	 * @param size - the height to be assigned to the first matched element.
	 * @param excludeMargin - whether to subtract the margins, too.
	 * You rarely need this unless the height is specified in term of the parent's perspective.
	 * @see {@link contentHeight}
	 */
	revisedHeight(size: number, excludeMargin?: boolean): number {
		if (this.jq.css('box-sizing') != 'border-box')
			size -= this.padBorderHeight();
		if (size > 0 && excludeMargin)
			size -= this.marginHeight();
		return size < 0 ? 0 : size;
	}
	/**
	 * @returns the content width of the element, which substracted the width of its
	 * CSS border or padding, unlike {@link revisedWidth}, the contentWidth
	 * will ignore the box-sizing with border-box.
	 * @param excludeMargin - whether to subtract the margins, too.
	 * @since 7.0.0
	 */
	contentWidth(excludeMargin?: boolean): number {
		var size = this.jq[0].offsetWidth;
		size -= this.padBorderWidth();
		if (size > 0 && excludeMargin)
			size -= this.marginWidth();
		return size < 0 ? 0 : size;
	}
	/**
	 * @returns the content height of the element, which substracted the height of its
	 * CSS border or padding, unlike {@link revisedHeight},
	 * the contentHeight will ignore the box-sizing with border-box.
	 * @param excludeMargin - whether to subtract the margins, too.
	 * @since 7.0.0
	 */
	contentHeight(excludeMargin?: boolean): number {
		var size = this.jq[0].offsetHeight;
		size -= this.padBorderHeight();
		if (size > 0 && excludeMargin)
			size -= this.marginHeight();
		return size < 0 ? 0 : size;
	}
	/**
	 * @returns the summation of the margin width of the first matched element.
	 * @since 7.0.0
	 */
	marginWidth(): number {
		return this.sumStyles('lr', jq.margins);
	}
	/**
	 * @returns the summation of the margin height of the first matched element.
	 * @since 7.0.0
	 */
	marginHeight(): number {
		return this.sumStyles('tb', jq.margins);
	}
	/**
	 * @returns the summation of the border width of the first matched element.
	 */
	borderWidth(): number {
		return this.sumStyles('lr', jq.borders);
	}
	/**
	 * @returns the summation of the border height of the first matched element.
	 */
	borderHeight(): number {
		return this.sumStyles('tb', jq.borders);
	}
	/**
	 * @returns the summation of the padding width of the first matched element.
	 */
	paddingWidth(): number {
		return this.sumStyles('lr', jq.paddings);
	}
	/**
	 * @returns the summation of the padding height of the first matched element.
	 */
	paddingHeight(): number {
		return this.sumStyles('tb', jq.paddings);
	}
	/**
	 * @returns the summation of the padding width and the border width of the first matched element.
	 */
	padBorderWidth(): number {
		return this.borderWidth() + this.paddingWidth();
	}
	/**
	 * @returns the summation of the padding height and the border height of the first matched element.
	 */
	padBorderHeight(): number {
		return this.borderHeight() + this.paddingHeight();
	}
	/**
	 * @returns the maximal allowed height of the first matched element.
	 * In other words, it is the client height of the parent minus all sibling's.
	 */
	vflexHeight(): number {
		var el = this.jq[0],
			parent = el.parentElement,
			hgh = parent ? zk(parent).clientHeightDoubleValue() : 0,
			zkp: undefined | zk.JQZK;
		// eslint-disable-next-line zk/noNull
		for (let p: Element | null = el.previousElementSibling; p; p = p.previousElementSibling) {
			zkp = zk(p);
			if (zkp.isVisible())
				hgh -= zkp.offsetHeightDoubleValue();
		}
		// eslint-disable-next-line zk/noNull
		for (let p: Element | null = el.nextElementSibling; p; p = p.nextElementSibling) {
			zkp = zk(p);
			if (zkp.isVisible())
				hgh -= zkp.offsetHeightDoubleValue();
		}
		return hgh;
	}
	/**
	 * @returns the index of the first selected (table) cell in the cells collection of a (table) row.
	 * <p>Note: The function fixed the problem of IE that cell.cellIndex returns a wrong index if there is a hidden cell in the table.
	 * @see {@link ncols}
	 */
	cellIndex(): number {
		var cell = this.jq[0];
		return cell instanceof HTMLTableCellElement ? cell.cellIndex : 0;
	}
	/**
	 * @returns the number of columns of a row. Notice that it, unlike row.cells.length, calculate colSpan, too. In addition, it can filter out invisible cells.
	 * @param visibleOnly - whether not count invisible cells
	 * @see {@link cellIndex}
	 */
	ncols(visibleOnly?: boolean): number {
		var row = this.jq[0],
			cnt = 0;
		if (row instanceof HTMLTableRowElement) {
			var cells = row.cells;
			for (var j = 0, cl = cells.length; j < cl; ++j) {
				var cell = cells[j];
				if (!visibleOnly || zk(cell).isVisible()) {
					var span = cell.colSpan;
					if (span >= 1) cnt += span;
					else ++cnt;
				}
			}
		}
		return cnt;
	}
	/**
	 * Converts an offset (x,y) from absolute coordination to the element's style coordination, such that you can assign them to the style (el.style).
	 * @param x - the X coordinate
	 * @param y - the Y coordinate
	 * @returns the offset
	 */
	toStyleOffset(x: number, y: number): zk.Offset {
		var el = this.jq[0],
			oldx = el.style.left, oldy = el.style.top,
			resetFirst = zk.webkit || zk.opera;
		//Opera:
		//1)we have to reset left/top. Or, the second call position wrong
		//test case: Tooltips and Popups
		//2)we cannot assing "", either
		//test case: menu
		//IE/gecko fix: auto causes toStyleOffset incorrect
		if (resetFirst || el.style.left == '' || el.style.left == 'auto')
			el.style.left = '0';
		if (resetFirst || el.style.top == '' || el.style.top == 'auto')
			el.style.top = '0';

		var ofs1 = this.cmOffset(),
			x2 = zk.parseInt(el.style.left),
			y2 = zk.parseInt(el.style.top);
		ofs1 = [x - ofs1[0] + x2, y - ofs1[1] + y2];

		el.style.left = oldx; el.style.top = oldy; //restore
		return ofs1;
	}
	/**
	 * ```ts
	 * jq(el).zk.center('left top');
	 * jq(el).zk.center('left center');
	 * jq(el).zk.center('left'); //not to change the Y coordinate
	 * jq(el).zk.center(); //same as 'center'
	 * ```
	 * Positions the first selected element at the particular location of the browser window.
	 * ```ts
	 * jq(el).zk.center('left top');
	 * jq(el).zk.center('left center');
	 * jq(el).zk.center('left'); //not to change the Y coordinate
	 * jq(el).zk.center(); //same as 'center'
	 * ```
	 *
	 * @param flags - A combination of center, left, right, top and bottom. If omitted, center is assumed.
	 * @see {@link position}
	 */
	center(flags?: string): this {
		var el = this.jq[0],
			wdgap = this.offsetWidth(),
			hghgap = this.offsetHeight();

		if ((!wdgap || !hghgap) && !this.isVisible()) {
			el.style.left = el.style.top = '-10000px'; //avoid annoying effect
			el.style.display = 'block'; //we need to calculate the size
			wdgap = this.offsetWidth();
			hghgap = this.offsetHeight(),
				el.style.display = 'none'; //avoid Firefox to display it too early
		}

		var left = jq.innerX(), top = jq.innerY(),
			x, y, skipx, skipy;

		wdgap = jq.innerWidth() - wdgap;
		if (!flags) x = left + wdgap / 2;
		else if (flags.includes('left')) x = left;
		else if (flags.includes('right')) x = left + wdgap - 1; //just in case
		else if (flags.includes('center')) x = left + wdgap / 2;
		else {
			x = 0; skipx = true;
		}

		hghgap = jq.innerHeight() - hghgap;
		if (!flags) y = top + hghgap / 2;
		else if (flags.includes('top')) y = top;
		else if (flags.includes('bottom')) y = top + hghgap - 1; //just in case
		else if (flags.includes('center')) y = top + hghgap / 2;
		else {
			y = 0; skipy = true;
		}

		if (x < left) x = left;
		if (y < top) y = top;

		var ofs = this.toStyleOffset(x as number, y as number);

		if (!skipx) el.style.left = jq.px(ofs[0]);
		if (!skipy) el.style.top = jq.px(ofs[1]);
		return this;
	}
	/**
	 * Position the first matched element to the specified location.
	 *
	 * @param dim - the dimension of the anchor location
	 * @param where - where to position. Default: `overlap`<br/>
	 * Allowed values:</br>
	 * <ul>
	 * 	<li><b>before_start</b><br/> the element appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the element appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the element appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the element appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the element appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the element appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the element appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the element appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the element appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the element appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the element appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the element appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the element overlaps the anchor, with anchor and element aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the element overlaps the anchor, with anchor and element aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the element overlaps the anchor, with anchor and element aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the element appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the element appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the element at the horizontal position of the mouse cursor.</li>
	 * </ul>
	 * @param opts - a map of addition options. Allowed values include
	<ol><li> <b>overflow</b><br /> whether to allow the element being scrolled out of the visible area (Default: false, i.e., not allowed). If not specified (or false), the popup always remains visible even if the user scrolls the anchor widget out of the visible area</li></ol>
	 * @see {@link center}
	 */
	position(dim?: Dimension, where?: string, opts?: PositionOptions): this
	/**
	 * Position the first matched element to the specified location.
	 *
	 * @param dim - the element used to get the dimension of the achor location.
	 * (by use of {@link dimension})
	 * @param where - where to position. Default: `overlap`<br/>
	 * Allowed values: refer to {@link position}.
	 * @param opts - a map of addition options.<br/>
	 * Allowed values: refer to {@link position}.
	 * @see {@link center}
	 */
	position(dim?: Element, where?: string, opts?: PositionOptions): this
	position(dim?: Dimension | Element, where?: string, opts?: PositionOptions): this {
		where = where || 'overlap';

		if (!dim) {
			var bd = jq('body')[0];
			dim = {
				left: 0, top: 0,
				width: bd.offsetWidth, height: bd.offsetHeight
			};
		}

		if (dim instanceof Element) //DOM element
			dim = zk(dim).dimension(true);
		var x = dim.left, y = dim.top,
			wd = this.dimension(), hgh = wd.height, //only width and height
			wdh = wd.width;

		/*Fixed since ios safari 5.0.2(webkit 533.17.9)
		if (zk.ios) { // Bug 3042165(iphone/ipad)
			x -= jq.innerX();
			y -= jq.innerY();
		}
		*/
		switch (where) {
			case 'before_start':
				y -= hgh;
				break;
			case 'before_center':
				y -= hgh;
				x += (dim.width - wdh) / 2 | 0;
				break;
			case 'before_end':
				y -= hgh;
				x += dim.width - wdh;
				break;
			case 'after_start':
				y += dim.height;
				break;
			case 'after_center':
				y += dim.height;
				x += (dim.width - wdh) / 2 | 0;
				break;
			case 'after_end':
				y += dim.height;
				x += dim.width - wdh;
				break;
			case 'start_before':
				x -= wdh;
				break;
			case 'start_center':
				x -= wdh;
				y += (dim.height - hgh) / 2 | 0;
				break;
			case 'start_after':
				x -= wdh;
				y += dim.height - hgh;
				break;
			case 'end_before':
				x += dim.width;
				break;
			case 'end_center':
				x += dim.width;
				y += (dim.height - hgh) / 2 | 0;
				break;
			case 'end_after':
				x += dim.width;
				y += dim.height - hgh;
				break;
			case 'at_pointer':
				var offset = zk.currentPointer;
				x = offset[0];
				y = offset[1];
				break;
			case 'after_pointer':
				var offset = zk.currentPointer;
				x = offset[0];
				y = offset[1] + 20;
				break;
			case 'top_right':
			case 'overlap_end':
				x += dim.width - wdh;
				break;
			case 'top_center':
				x += (dim.width - wdh) / 2 | 0;
				break;
			case 'middle_left':
				y += (dim.height - hgh) / 2 | 0;
				break;
			case 'middle_center':
				x += (dim.width - wdh) / 2 | 0;
				y += (dim.height - hgh) / 2 | 0;
				break;
			case 'middle_right':
				x += dim.width - wdh;
				y += (dim.height - hgh) / 2 | 0;
				break;
			case 'bottom_left':
			case 'overlap_before':
				y += dim.height - hgh;
				break;
			case 'bottom_center':
				x += (dim.width - wdh) / 2 | 0;
				y += dim.height - hgh;
				break;
			case 'bottom_right':
			case 'overlap_after':
				x += dim.width - wdh;
				y += dim.height - hgh;
				break;
			default: // overlap/top_left is assumed
			// nothing to do.
		}

		if (!opts || !opts.overflow) {
			var scX = jq.innerX(),
				scY = jq.innerY(),
				scMaxX = scX + jq.innerWidth(),
				scMaxY = scY + jq.innerHeight();

			if (x + wdh > scMaxX) x = scMaxX - wdh;
			if (x < scX) x = scX;
			if (y + hgh > scMaxY) y = scMaxY - hgh;
			if (y < scY) y = scY;
		}

		// Bug 3251564
		// dodge reference element (i.e. not to cover the reference textbox, etc)
		if (opts && opts.dodgeRef) {
			var dl = dim.left, dt = dim.top,
				dr = dl + dim.width, db = dt + dim.height;
			// overlap test
			if (x + wdh > dl && x < dr && y + hgh > dt && y < db) {
				if (opts.overflow) {
					// overflow allowed: try to dodge to the right
					x = dr;
				} else {
					var scX = jq.innerX(),
						scMaxX = scX + jq.innerWidth(),
						spr = scMaxX - dr,
						spl = dl - scX;
					// no overflow: dodge to the larger space
					// try right side first
					if (spr >= wdh || spr >= spl)
						x = Math.min(dr, scMaxX - wdh);
					else
						x = Math.max(dl - wdh, scX);
				}
			}
		}

		var el = this.jq[0],
			ofs = this.toStyleOffset(x, y);
		el.style.left = jq.px(ofs[0]);
		el.style.top = jq.px(ofs[1]);
		return this;
	}

	/**
	 * Calculates the cumulative scroll offset of the first matched element in nested scrolling containers. It adds the cumulative scrollLeft and scrollTop of an element and all its parents.
	 * <p>It is used for calculating the scroll offset of an element that is in more than one scroll container (e.g., a draggable in a scrolling container which is itself part of a scrolling document).
	 * <p>Note that all values are returned as numbers only although they are expressed in pixels.
	 * @returns the cumulative scroll offset.
	 * @see {@link cmOffset}
	 * @see {@link viewportOffset}
	 * @see {@link revisedOffset}
	 */
	scrollOffset(): zk.Offset {
		// eslint-disable-next-line zk/noNull
		var el: HTMLElement | null = this.jq[0],
			t = 0, l = 0;
		while (el) {
			t += el.scrollTop || 0;
			l += el.scrollLeft || 0;
			el = el.parentElement;
		}
		return [l, t];
	}
	/**
	 * @returns the cumulative offset of the first matched element from the top left corner of the document.
	 * <p>It actually adds the cumulative offsetLeft and offsetTop of an element and all its parents.
	 * <p>Note that it ignores the scroll offset. If you want the element's coordinate,
	 * use {@link revisedOffset} instead.
	 * <p>Note that all values are returned as numbers only although they are expressed in pixels.
	 * @see {@link scrollOffset}
	 * @see {@link viewportOffset}
	 * @see {@link revisedOffset}
	 */
	cmOffset(): zk.Offset {
		//fix safari's bug: TR has no offsetXxx
		var el = this.jq[0];
		if (zk.webkit && el instanceof HTMLTableRowElement && el.cells.length)
			el = el.cells[0];

		//fix gecko and safari's bug: if not visible before, offset is wrong
		if (!(zk.gecko || zk.webkit)
			|| this.isVisible() || this.offsetWidth())
			return _cmOffset(el);

		el.style.display = '';
		var ofs = _cmOffset(el);
		el.style.display = 'none';
		return ofs;
	}
	/**
	 * A short cut for looking up ZK Widget from jQuery object.
	 * Now the code `zk.Widget.$(jq("@listbox"))` could be replaced with `zk("@listbox").$()`
	 */
	$<T extends zk.Widget>(opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): T {
		const e = this.jq[0];
		if (e) {
			const target = e[zk.Widget._TARGET] as T;
			if (target) {
				// reset all query targets, if any
				let len = this.jq.length;
				while (--len) {
					delete this.jq[len][zk.Widget._TARGET];
				}
				return target;
			}
		}
		return zk.Widget.$(e, opts) as T;
	}
	/**
	 * Makes the position of the first selected element as absolute.
	 * In addition to changing the style's position to absolute, it
	 * will store the location such that it can be restored later when
	 * {@link relativize} is called.
	 * ```ts
	 * zk('abc').absolutize()
	 * ```
	 * @see {@link relativize}
	 */
	absolutize(): this {
		var el = this.jq[0];
		if (el.style.position == 'absolute') return this;

		var offsets = _posOffset(el),
			left = offsets[0], top = offsets[1],
			st = el.style;
		el['_$orgLeft'] = left - parseFloat(st.left || '0');
		el['_$orgTop'] = top - parseFloat(st.top || '0');
		st.position = 'absolute';
		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	}
	/**
	 * Makes the position of the element as relative. In addition to changing the style's position to relative, it tries to restore the location before calling {@link absolutize}.
	 * @see {@link absolutize}
	 */
	relativize(): this {
		var el = this.jq[0];
		if (el.style.position == 'relative') return this;

		var st = el.style;
		st.position = 'relative';
		var top = parseFloat(st.top || '0') - (el['_$orgTop'] || 0),
			left = parseFloat(st.left || '0') - (el['_$orgLeft'] || 0);

		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	}

	/**
	 * @returns the offset width. It is similar to el.offsetWidth, except it solves some browser's bug or limitation.
	 */
	offsetWidth(): number {
		var n = this.jq[0],
			width;
		if (typeof n.getBoundingClientRect != 'undefined') {
			var rect = n.getBoundingClientRect();
			width = rect.width || rect.right - rect.left;
		}
		return Math.max(width as number, n.offsetWidth);
	}
	/**
	 * @returns the offset height. It is similar to el.offsetHeight, except it solves some browser's bug or limitation.
	 */
	offsetHeight(): number {
		var n = this.jq[0];
		// span will cause a special gap between top and bottom
		// when use HTML5 doctype
		if (isHTML5DocType()
			&& jq.nodeName(n, 'SPAN') && this.jq.css('display') != 'block') {
			var textHTML = /*safe*/ n.outerHTML;

			// replace uuid to speed up the calculation
			if (zk.Widget.$(n, {exact: 1 as unknown as boolean})) {
				textHTML = textHTML.replace(/id="[^"]*"/g, '');
			}
			return zk(document.body).textSize(/*safe*/ textHTML)[1];
		}
		return n.offsetHeight;
	}
	/**
	 * @returns the offset top. It is similar to el.offsetTop, except it solves some browser's bug or limitation.
	 */
	offsetTop(): number {
		return this.jq[0].offsetTop;
	}
	/**
	 * @returns the offset left. It is similar to el.offsetLeft, except it solves some browser's bug or limitation.
	 */
	offsetLeft(): number {
		return this.jq[0].offsetLeft;
	}
	/**
	 * @returns the offset width. The value will not be rounded.
	 * @since 8.5.1
	 */
	offsetWidthDoubleValue(): number {
		const n = this.jq[0],
			width = n.getBoundingClientRect().width,
			diff = Math.abs(n.offsetWidth - width);
		if (diff > 0 && diff <= 1) {
			return width; // double value
		}
		return n.offsetWidth; // return offsetWidth instead for ZK-5168
	}
	/**
	 * @returns the offset height. The value will not be rounded.
	 * @since 8.5.1
	 */
	offsetHeightDoubleValue(): number {
		const n = this.jq[0],
			height = n.getBoundingClientRect().height,
			diff = Math.abs(n.offsetHeight - height);
		if (diff > 0 && diff <= 1) {
			return height; // double value
		}
		return n.offsetHeight; // return offsetHeight instead for ZK-5168
	}
	/**
	 * @returns the client width. The value will not be rounded.
	 * @since 9.5.1
	 */
	clientWidthDoubleValue(): number {
		return this.offsetWidthDoubleValue() - this.borderWidth();
	}
	/**
	 * @returns the client height. The value will not be rounded.
	 * @since 9.5.1
	 */
	clientHeightDoubleValue(): number {
		return this.offsetHeightDoubleValue() - this.borderHeight();
	}
	/**
	 * @returns the offset top. The value will not be rounded.
	 * @since 8.5.1
	 */
	offsetTopDoubleValue(): number {
		return this.jq[0].getBoundingClientRect().top;
	}
	/**
	 * @returns the offset left. The value will not be rounded.
	 * @since 8.5.1
	 */
	offsetLeftDoubleValue(): number {
		return this.jq[0].getBoundingClientRect().left;
	}

	/**
	 * @returns the actual client width rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	clientWidthCeil(): number {
		return Math.ceil(this.clientWidthDoubleValue());
	}
	/**
	 * @returns the actual client height rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	clientHeightCeil(): number {
		return Math.ceil(this.clientHeightDoubleValue());
	}
	/**
	 * @returns the actual offset width rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	offsetWidthCeil(): number {
		return Math.ceil(this.offsetWidthDoubleValue());
	}
	/**
	 * @returns the actual offset height rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	offsetHeightCeil(): number {
		return Math.ceil(this.offsetHeightDoubleValue());
	}
	/**
	 * @returns the actual offset top rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	offsetTopCeil(): number {
		return Math.ceil(this.offsetTopDoubleValue());
	}
	/**
	 * @returns the actual offset left rounded "up" to the closest integer.
	 * @since 9.6.3
	 */
	offsetLeftCeil(): number {
		return Math.ceil(this.offsetLeftDoubleValue());
	}

	/**
	 * @returns the X/Y coordinates of the first matched element relative to the viewport.
	 * @see {@link cmOffset}
	 * @see {@link scrollOffset}
	 */
	viewportOffset(): zk.Offset {
		// eslint-disable-next-line zk/noNull
		var t = 0, l = 0, el: HTMLElement | null = this.jq[0], p = el;
		while (p) {
			t += p.offsetTop || 0;
			l += p.offsetLeft || 0;

			// Safari fix
			if (p.offsetParent == document.body)
				if (jq(p).css('position') == 'absolute') break;
			p = p.offsetParent as HTMLElement;
		}

		while (el && (el = el.parentElement)) {
			// Opera 12.15 fix this
			// if (!zk.opera || jq.nodeName(el, 'body')) {
			t -= el.scrollTop || 0;
			l -= el.scrollLeft || 0;
			//}
		}
		return [l, t];
	}
	/**
	 * @returns the size of the text if it is placed inside the first matched element.
	 * @param text - the content text
	 * @deprecated Use {@link textWidth} instead for better performance.
	 */
	textSize(text?: string): zk.Offset {
		const jq = this.jq;
		/*safe*/ text = /*safe*/ text || jq[0].innerHTML;
		if (!_txtSizDiv) {
			_txtSizDiv = document.createElement('div');
			_txtSizDiv.style.cssText = _defaultStyle;
			document.body.appendChild(_txtSizDiv);

			for (var ss = _txtStyles, j = ss.length; j--;)
				_txtStylesCamel[j] = ss[j].$camel();
		}
		var newStyle = '';
		for (var ss = _txtStylesCamel, j = ss.length; j--;) {
			var nm = ss[j];
			newStyle += _txtStyles[j] + ':' + jq.css(nm) + ';';
		}

		var result: undefined | zk.Offset,
			key = newStyle + text;
		if (!(result = _cache[key])) {
			// ZK-2181: remove name attritube to prevent the radio has wrong status
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			_txtSizDiv.innerHTML = DOMPurify.sanitize(text.replace(/name="[^"]*"/g, ''));
			_txtSizDiv.style.cssText = _defaultStyle + newStyle;
			_txtSizDiv.style.display = '';
			result = _cache[key] = [_txtSizDiv.offsetWidth, _txtSizDiv.offsetHeight];
			_txtSizDiv.style.display = 'none';
			_txtSizDiv.innerHTML = ''; //reset
		}
		return result;
	}

	/**
	 * @returns the width of the text if it is placed inside the first matched element.
	 * @param text - the content text. The text of the first matched element if omitted
	 * @since 9.0.1
	 * @see <a href="https://stackoverflow.com/a/21015393">Stack Overflow</a>
	 */
	textWidth(text?: string): number {
		var $obj = this.jq;
		text = text || $obj[0].textContent || '';
		var canvas: HTMLCanvasElement = this.textWidth['canvas'] as undefined | HTMLCanvasElement ?? (this.textWidth['canvas'] = document.createElement('canvas')),
			context = canvas.getContext('2d')!,
			fontStyles = $obj.css(_txtFontStyles),
			newFont = '';
		jq.each(fontStyles, function (prop, val) {
			newFont += val + ' ';
		});
		context.font = newFont;
		return context.measureText(text).width;
	}
	/**
	 * @returns the dimension of the specified element.
	 * <p>If revised not specified (i.e., not to calibrate), the left and top are the offsetLeft and offsetTop of the element.
	 * @param revised - if revised is true, {@link revisedOffset} will be used (i.e., the offset is calibrated).
	 */
	dimension(revised?: boolean): Dimension {
		var display = this.jq.css('display');
		if (display != 'none' && display != null) // Safari bug
			return _addOfsToDim(this,
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);

		// All *Width and *Height properties give 0 on elements with display none,
		// so enable the element temporarily
		var st = this.jq[0].style,
			backup = {};
		zk.copy(st, {
			visibility: 'hidden',
			position: 'absolute',
			display: 'block'
		}, backup);
		try {
			return _addOfsToDim(this,
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);
		} finally {
			zk.copy(st, backup);
		}
	}

	/**
	 * Forces the browser to redo (re-apply) CSS of all matched elements.
	 * <p>Notice that calling this method might introduce some performance penality.
	 * @param timeout - number of milliseconds to wait before really re-applying CSS.
	 * 100 is assumed if not specified , -1 means re-applying css right now.
	 */
	redoCSS(timeout?: number, opts?: Partial<RedoCSSOptions>): this {
		if (opts?.fixFontIcon) {
			return this;
		}
		if (timeout == -1) { //timeout -1 means immediately
			for (var j = this.jq.length; j--;)
				zjq._fixCSS(this.jq[j]);
		} else {
			for (var j = this.jq.length; j--;)
				_rdcss.push(this.jq[j]);
			setTimeout(_redoCSS0, Number(timeout) >= 0 ? timeout : 100);
		}
		return this;
	}
	/**
	 * Forces the browser to re-load the resource specified in the `src`
	 * attribute for all matched elements.
	 */
	redoSrc(): this {
		for (let j = this.jq.length; j--;) {
			let el = this.jq[j],
				src: string | undefined;
			src = el['src'] as string | undefined;
			el['src'] = zjq.src0;
			el['src'] = src;
		}
		return this;
	}

	/**
	 * @returns the virtual parent of the first matched element.
	 * <p>Refer to {@link makeVParent} for more information.
	 * @param real - whether to return DOM element's parentNode if
	 * no virtual parent. In other words, `zk(n).vparentNode(true)`
	 * is the same as `zk(n).vparentNode()||n.parentNode`.
	 */
	vparentNode(real?: boolean): HTMLElement | undefined {
		var el = this.jq[0];
		if (el) {
			let v = el['z_vp'] as undefined | string; //might be empty
			if (v) return jq('#' + v)[0];
			v = el['z_vpagt'] as undefined | string;
			let agt: HTMLElement | undefined;
			if (v && (agt = jq('#' + v)[0]))
				return agt.parentNode as HTMLElement | undefined;
			if (real)
				return el.parentNode as HTMLElement | undefined;
		}
	}
	/**
	 * Creates a virtual parent for the specified element. Creating a virtual parent makes the specified element able to appear above any other element (such as a menu popup). By default, the Z order of an element is decided by its parent and ancestors (if any of them has the relative or absolute position). If you want to resolve this limitation, you can create a virtual parent for it with this method.
	 * <p>To undo the creation of the virtual parent, use {@link undoVParent}.
	 * <p>Notice that, due to browser's limitation, creating a virtual parent is still not enough to make an element appear on top of all others in all conditions. For example, it cannot in IE6 if there is a SELECT element underneath. To really solve this, you have to create a so-called stackup, which is actually an IFRAME element. By position the iframe right under the element, you can really want it appear on top.
	 * Refer to {@link jq.newStackup} for more information.
	 * <h3>What Really Happens</h3>
	 * <p>This method actually moves the element to the topmost level in the DOM tree (i.e., as the child of document.body), and then the original parent (the parent before calling this method) becomes the virtual parent, which can be retrieved by {@link vparentNode}.
	 * <h3>When to Use</h3>
	 * <p>When you implement a widget that appears above others, such as an overlapped window, a menu popup and a dropdown list, you can invoke this method when {@link Widget#bind_} is called. And then, restore it
	 * by calling {@link undoVParent} when {@link Widget#unbind_} is called.
	 */
	makeVParent(): this {
		var el = this.jq[0],
			p = el.parentElement;
		if (el['z_vp'] || el['z_vpagt'] || p == document.body || p == null)
			return this; //called twice or not necessary

		var sib = el.nextSibling,
			agt = document.createElement('span');
		agt.id = el['z_vpagt'] = '_z_vpagt' + _vpId++;
		agt.style.display = 'none';

		// Bug 3049181 and 3092040
		zjq._fixedVParent(el, true);

		if (sib) p.insertBefore(agt, sib);
		else p.appendChild(agt);

		el['z_vp'] = p.id; //might be empty
		var st = el.style;
		if (!st.top) st.top = '0';
		//B3178359: if no top and parent is relative+absolute, the following
		//line causes browser crazy
		//Strange: all browsers have the same behavior
		document.body.appendChild(el);
		return this;
	}
	/**
	 * Undoes the creation of a virtual parent of the first matched element.
	 * <p>Refer to {@link makeVParent} for more information.
	 */
	undoVParent(): this {
		if (this.hasVParent()) {
			var el = this.jq[0],
				p = el['z_vp'] as undefined | string,
				agt = el['z_vpagt'] as string,
				$agt = jq('#' + agt);
			el['z_vp'] = el['z_vpagt'] = undefined;
			const agtNode = $agt[0],
				parent = (p ? jq('#' + p)[0] : agtNode ? agtNode.parentNode : undefined) as undefined | HTMLElement;
			if (parent) {

				// Bug 3049181
				zjq._fixedVParent(el);

				if (agtNode) {
					parent.insertBefore(el, agtNode);
					$agt.remove();
				} else
					parent.appendChild(el);

				var cf: undefined | zk.Widget, parentWidget: undefined | zk.Widget, a: undefined | HTMLElement;
				// ZK-851
				if ((zk.ff || zk.opera) && (cf = zk._prevFocus)
					&& (parentWidget = zk.Widget.$(el)) && zUtl.isAncestor(parentWidget, cf)) {
					if (cf.getInputNode)
						jq(cf.getInputNode()).trigger('blur');
					else if ((a = cf.$n('a')) // ZK-1955
						&& jq.nodeName(a, 'button', 'input', 'textarea', 'a', 'select', 'iframe'))
						jq(a).trigger('blur');
					else if (cf instanceof zul.wgt.Button) // ZK-1324: Trendy button inside bandbox popup doesn't lose focus when popup is closed
						jq(cf.$n('btn') ?? cf.$n()).trigger('blur');
				}
			}
		}
		return this;
	}
	/**
	 * Check if element has virtual parent
	 * @since 7.0.3
	 */
	hasVParent(): boolean {
		//Fix Bug ZK-2434, consider virtual element
		var el = this.jq[0];
		return !!(el && (el['z_vp'] || el['z_vpagt']));
	}

	/**
	 * Fixes DOM elements when a widget's unbind_ is called
	 * and it will hide the DOM element (display="none" or visibility="hidden").
	 * <p>For firefox, it has to reset the src attribute of iframe (Bug 3076384)
	 */
	beforeHideOnUnbind(): unknown { return; }

	//focus/select//
	/**
	 * Sets the focus to the first matched element.
	 * It is the same as jq(n).focus() and n.focus, except
	 * <ul>
	 * <li>it doesn't throw any exception (rather, returns false).</li>
	 * <li>it can set the focus later (by use of timeout). </li>
	 * <li>it maintains {@link zk#currentFocus}.</li>
	 * </ul>
	 * <p>In general, it is suggested to use zk(n).focus(), unless
	 * n does not belong to any widget.
	 * @param timeout - the number of milliseconds to delay before setting the focus. If omitted or negative, the focus is set immediately.
	 * @returns whether the focus is allowed to set to the element. Currently, only SELECT, BUTTON, INPUT and IFRAME is allowed.
	 * @see {@link select}
	 */
	focus(timeout?: number): boolean {
		var n = this.jq[0];
		if (!n || !n.focus) return false;
		//ie: INPUT's focus not function

		if (!jq.nodeName(n, 'button', 'input', 'textarea', 'a', 'select', 'iframe') && n.getAttribute('tabindex') == null)
			return false;

		if (Number(timeout) >= 0) setTimeout(function () {_focus(n);}, timeout);
		else _focus(n);
		return true;
	}
	/**
	 * Selects the first matched element. It is the same as DOMElement.select, except it won't throws an exception (rather, returns false), and can delay the execution for the specified number of milliseconds.
	 * @param timeout - the number of milliseconds to delay before setting the focus. If omitted or negative, the focus is set immediately.
	 * @returns whether the element supports the select method. In other words, it returns false if the object doesn't have the select method.
	 * @see {@link getSelectionRange}
	 * @see {@link setSelectionRange}
	 * @see {@link focus}
	 */
	select(timeout?: number): boolean {
		var n = this.jq[0];
		if (!n || typeof n['select'] != 'function') return false;

		if (Number(timeout) >= 0) setTimeout(function () {_select(n as HTMLInputElement);}, timeout);
		else _select(n as HTMLInputElement);
		return true;
	}

	/**
	 * @returns the selection range of the specified input-type element. The selection range is returned as a two-element array, where the first item is the starting index, and the second item is the ending index (excluding).
	 * <p>If an exception occurs, [0, 0] is returned.
	 * @see {@link setSelectionRange}
	 * @see {@link select}
	 */
	getSelectionRange(): zk.Offset {
		const inp = this.jq[0] as HTMLInputElement;
		try {
			return [inp.selectionStart || 0, inp.selectionEnd || 0];
		} catch (e) {
			return [0, 0];
		}
	}
	/**
	 * Sets the selection range of the specified input-type element.
	 * @param start - the starting index of the selection range
	 * @param end - the ending index of the selection rane (excluding). In other words, the text between start and (end-1) is selected.
	 */
	// eslint-disable-next-line zk/javaStyleSetterSignature
	setSelectionRange(start: number, end?: number): this {
		var inp = this.jq[0] as HTMLInputElement,
			len = inp.value ? inp.value.length : 0; //ZK-2805
		if (start == null || start < 0) start = 0;
		if (start > len) start = len;
		if (end == null || end > len) end = len;
		if (end < 0) end = 0;

		if (inp.setSelectionRange) {
			inp.setSelectionRange(start, end);
		}
		return this;
	}

	/**
	 * Submit the selected form.
	 * @since 5.0.4
	 */
	submit(): this {
		this.jq.each(_submit);
		return this;
	}

	//selection//
	/**
	 * Disallows the user to select a portion of its content. You usually invoke this method to disable the selection for button-like widgets. By default, all elements can be selected (unless disabled with CSS -- which not all browsers support).
	 * <p>If you disable the selection in {@link Widget#bind_}, you shall enable it back in {@link Widget#unbind_}
	 * since this method will register a DOM-level listener for certain browsers.
	 */
	disableSelection(): this {
		this.jq.each(_dissel);
		return this;
	}
	/**
	 * Allows the user to select a portion of its content. You usually invoke this method to undo {@link disableSelection}.
	 * By default, all elements can be selected (unless disabled with CSS -- which not all browsers support).
	 */
	enableSelection(): this {
		this.jq.each(_ensel);
		return this;
	}

	/**
	 * Sets the CSS style with a map of style names and values. For example,
	 * ```ts
	 * jq(el).css({width:'100px', paddingTop: "1px", "border-left": "2px"});
	 * jq(el).css(jq.parseStyle(jq.filterTextStle('width:100px;font-size:10pt')));
	 * ```
	 * <p>To parse a style (e.g., 'width:10px;padding:2px') to a map of style names and values, use {@link jq.parseStyle}.
	 * @deprecated As of release 5.0.2, use jq.css(map) instead
	 */
	setStyles(styles: JQuery.PlainObject<string | number | ((this: HTMLElement, index: number, value: string) => string | number | undefined)>): this {
		this.jq.css(styles);
		return this;
	}
	/**
	 * Clears the CSS styles (excluding the inherited styles).
	 * @since 5.0.2
	 */
	clearStyles(): this {
		var n = this.jq[0];
		if (n && n.hasAttribute('style'))
			n.removeAttribute('style');
		return this;
	}
	/**
	 * Detaches all child elements and return them as an array.
	 * @since 5.0.10
	 * @returns an array of {@link DOMElement} that are detached,
	 * or null if no element is selected.
	 */
	detachChildren(): HTMLElement[] | undefined {
		var embed = this.jq[0];
		if (embed) {
			var val: HTMLElement[] = [], n;
			while (n = embed.firstChild) {
				val.push(n as HTMLElement);
				embed.removeChild(n);
			}
			return val;
		}
		return undefined;
	}

	/**
	 * Tests if all elements are input elements (including textarea).
	 * @since 5.0.8
	 */
	isInput(): boolean {
		var $jq = this.jq,
			len = $jq.length,
			types = ['text', 'password', 'number', 'tel', 'url', 'email'];
		for (var j = len, tag, n: undefined | HTMLElement & {type?}; j--;)
			if ((tag = jq.nodeName(n = $jq[j])) != 'textarea'
				&& (tag != 'input' || (jq.inArray(n.type, types) == -1)))
				return false;
		return len > 0; //false if nothing selected
	}

	//Returns the minimal width to hold the given cell called by getChildMinSize_
	static minWidth(el: Element | zk.Widget): number {
		return zk(el).offsetWidth();
	}
	//overriden in dom.js to fix the focus issue (losing caret...)
	static fixInput(el: Element): void { //ZK-3237: including domie.js for IE 11 will have many side effects
		try {
			var $n = zk(el), pos: zk.Offset;
			if ($n.isInput()) {
				pos = $n.getSelectionRange();
				$n.setSelectionRange(pos[0], pos[1]);
			}
		} catch (e) {
			zk.debugLog((e as Error).message ?? e);
		}
	}
	/** @internal */
	static _fixCSS(el: HTMLElement): void { //overriden in domie.js , domsafari.js , domopera.js
		el.className += ' ';
		if (el.offsetHeight) {
			// just make the browser reflow
		}
		el.className = el.className.trim();
	}
	/** @internal */
	static _cleanVisi(n: HTMLElement): void { //overriden in domopera.js
		n.style.visibility = 'inherit';
	}
	/** @internal */
	static _fixClick(el: Event | JQuery.ClickEvent): void {//overridden in domie.js
		// empty for overriding
	}
	/** @internal */
	static _fixedVParent(el: Element, option?: boolean): void {
		// empty for overriding
	}
	/** @internal */
	static _fixIframe(el: Element): void {
		// empty for overriding
	}

	//The source URI used for iframe (to avoid HTTPS's displaying nonsecure issue)
	static src0 = ''; //an empty src; overriden in domie.js
	static eventTypes: Record<string, string | undefined> = {
		zmousedown: 'mousedown',
		zmouseup: 'mouseup',
		zmousemove: 'mousemove',
		zdblclick: 'dblclick',
		zcontextmenu: 'contextmenu'
	};

	// used in domsafari.ts
	/** @internal */
	declare static _sfKeys: Record<number, number>;
	/** @internal */
	declare static _evt: Record<'fix' | string, Function>; // eslint-disable-line @typescript-eslint/ban-types
}
jq.fn.zon = jq.fn.on;
jq.fn.zoff = jq.fn.off;
/**
 * @class jq
 * @import jq.Event
 * @import zk.Widget
 * @import zk.Desktop
 * @import zk.Skipper
 * @import zk.eff.Shadow
 * Represents the object returned by the `jq` function.
 * For example, `jq('#id');`
 *
 * <p>ZK 5 Client Engine is based on <a href="http://jquery.com/" target="jq">jQuery</a>.
 * It inherits all functionality provided by jQuery. Refer to <a href="http://docs.jquery.com/Main_Page" target="jq">jQuery documentation</a>
 * for complete reference. However, we use the global function called `jq`
 * to represent jQuery. Furthermore, for documentation purpose,
 * we use {@link JQZK.jq} to represent the object returned by the `jq` function.</p>
 * <p>Notice that there is no package called `_`.
 * Rather, it represents the global namespace of JavaScript.
 * In other words, it is the namespace of the `window` object
 * in a browser.
 *
 * <h2>Diffirence and Enhancement to jQuery</h2>
 * <p>`{@link JQZK.jq} jq(Object selector, Object context);`
 *
 * <blockquote>
 * <h3>Refer jQuery as `jq`</h3>
 * <p>First of all, the jQuery class is referenced as {@link JQZK.jq}, and it is suggested to use jq instead of $ or jQuery when developing a widget, since it might be renamed later by an application (say, overridden by other client framework). Here is an example uses jq:
 * ```ts
 * jq(document.body).append("<div></div>");
 * ```
 *
 * <h3>Dual Objects</h3>
 * <p>To extend jQuery's functionally,  each time `jq(...)`
 * or `zk(...)` is called, an instance of {@link JQZK.jq}
 * and an instance of {@link jqzk} are created. The former one provides the
 * standard jQuery API plus some minimal enhancement as described below.
 * The later is ZK's addions APIs.
 *
 * <p>You can retrieve one of the other with
 * {@link jq.zk} and {@link jqzk#jq}.
 *
 * ```ts
 * jq('#abc').zk; //the same as zk('#abc')
 * zk('#abc').jq; //the same as jq('#abc');
 * ```
 *
 * <h3>Extra Selectors</h3>
 * <blockquote>
 *
 * <h4>\@tagName</h4>
 * <p>`jq` is extended to support the selection by use of ZK widget's
 * tagName. For example,
 * ```ts
 * jq('@window');
 * ```
 *
 *<p>Notice that it looks for the ZK widget tree to see if any widget whose className
 * ends with `window`.
 * <p>If you want to search the widget in the nested tag, you can specify
 * the selector after \@. For example, the following searches the space owner named x,
 * then y, and finally z
 * ```ts
 * jq('@x @y @z');
 * ```
 * or search the element from the given attribute of the widget, you can specify
 * the selector as follows. For example,
 * ```ts
 * jq('@window[border="normal"]')
 * ```
 *
 * <h4>$id</h4>
 * <p>`jq` is extended to support the selection by use of widget's
 * ID ({@link zk.Widget.id}), and then DOM element's ID. For example,
 * ```ts
 * jq('$xx');
 * ```
 *
 * <p>Notice that it looks for any bound widget whose ID is xx, and
 * select the associated DOM element if found.
 *
 * <p>If you want to search the widget in the inner ID space, you can specify
 * the selector after $. For example, the following searches the space owner
 * named x, then y, and finally z
 * ```ts
 * jq('$x $y $z');
 * ```
 * or advanced search combine with CSS3 and \@, you can specify like this.
 * ```ts
 * jq('@window[border="normal"] > $x + div$y > @button:first');
 * ```
 *
 * <h4>A widget</h4>
 * <p>`jq` is extended to support {@link Widget}.
 * If the selector is a widget, `jq` will select the associated DOM element
 * of the widget.
 *
 * ```ts
 * jq(widget).after('<div></div>'); //assume widget is an instance of {@link Widget}
 * ```
 *
 * <p>In other words, it is the same as
 *
 * ```ts
 * jq(widget.$n()).after('<div></div>');
 * ```
 * </blockquote>
 *
 * <h3>Extra Contexts</h3>
 * <blockquote>
 * <h4>The `zk` context</h4>
 * ```ts
 * jq('foo', zk);
 * ```
 * <p>With the zk context, the selector without any prefix is assumed to be
 * the identifier of ID. In other words, you don't need to prefix it with '#'.
 * Thus, the above example is the same as
 * ```ts
 * jq('#foo')
 * ```
 *
 * <p>Of course, if the selector is not a string or prefix with a non-alphnumeric letter, the zk context is ignored.
 * </blockquote>
 *
 * <h3>Extra Global Functions</h3>
 * <blockquote>
 * <h4>The `zk` function</h4>
 * ```ts
 * {@link JQZK.jq} zk(Object selector);
 * ```
 *
 * <p>It is the same as `jq(selector, zk).zk`. In other words,
 * it assumes the zk context and returns an instance of {@link jqzk}
 * rather than an instance of {@link JQZK.jq}.
 * </blockquote>
 *
 * <h3>Other Extension</h3>
 * <ul>
 * <li>{@link JQZK.jq} - DOM utilities (such as, {@link jq.innerX}</li>
 * <li>{@link jqzk} - additional utilities to {@link JQZK.jq}.</li>
 * <li>{@link Event} - the event object passed to the event listener</li>
 * </ul>
 * <h3>Not override previous copy if any</h3>
 * <p>Unlike the original jQuery behavior, ZK's jQuery doesn't override
 * the previous copy, if any, so ZK can be more compatible with other frameworks
 * that might use jQuery. For example, if you manually include a copy
 * of jQuery before loading ZK Client Engine, `jQuery`
 * will refer to the copy of jQuery you included explicitly. To refer
 * ZK's copy, always use `jq`.
 * </blockquote>
 *
 * @author tomyeh
 */
var _jq /** original jQuery */ = zk.augment(jq.fn, {
	init(sel: string | zk.Widget | HTMLElement, ctx, ...rest: unknown[]): JQuery {
		if (ctx === zk) {
			if (typeof sel == 'string'
			&& zUtl.isChar(sel.charAt(0), {digit: 1, upper: 1, lower: 1, '_': 1})) {
				var el = document.getElementById(sel);
				if (!el || el.id == sel) {
					var ret = jq(el || []);
					ret['context'] = document;
					ret['selector'] = '#' + sel;
					ret.zk = new zjq(ret);
					return ret;
				}
				sel = '#' + sel;
			}
			ctx = undefined;
		}
		if (zk.Widget && sel instanceof zk.Widget)
			sel = sel.$n() || '#' + sel.uuid;
		if (sel == '#') sel = ''; //ZK-4565, '#' is not allowed in jquery 3.5.0
		const ret1 = (_jq['init'] as CallableFunction).bind(this)(sel, ctx, ...rest) as JQuery;
		ret1.zk = new zjq(ret1);
		return ret1;
	},
	/**
	 * Replaces the match elements with the specified HTML, DOM or {@link Widget}.
	 * We extends <a href="http://docs.jquery.com/Manipulation/replaceWith">jQuery's replaceWith</a>
	 * to allow replacing with an instance of {@link Widget}.
	 * @param widget - a widget
	 * @param desktop - the desktop. It is optional.
	 * @param skipper - the skipper. It is optional.
	 * @returns jq the jq object matching the DOM element after replaced
	 */
	// @ts-expect-error: incompatible with the signature in the original jQuery
	replaceWith(w: zk.Widget | unknown, desktop: zk.Desktop, skipper: zk.Skipper): JQuery {
		if (!(w instanceof zk.Widget))
			return (_jq['replaceWith'] as CallableFunction).bind(this)(w, desktop, skipper) as JQuery;

		var n = this[0];
		if (n) w.replaceHTML(n, desktop, skipper);
		return this;
	},
	// @ts-expect-error: incompatible with the signature in the original jQuery
	on(type: string, selector: string | undefined, data, fn: CallableFunction, ...rest: unknown[]): JQuery {
		type = zjq.eventTypes[type] ?? type;
		return this.zon(type, selector, data, fn, ...rest);
	},
	// @ts-expect-error: incompatible with the signature in the original jQuery
	off(type: string, selector: string | undefined, fn: CallableFunction, ...rest: unknown[]): JQuery {
		type = zjq.eventTypes[type] || type;
		return this.zoff(type, selector, fn, ...rest);
	},
	// @ts-expect-error: incompatible with the signature in the original jQuery
	bind(types: string, data: unknown, fn: CallableFunction): JQuery {
		return this.on(types, undefined, data, fn);
	},
	// @ts-expect-error: incompatible with the signature in the original jQuery
	unbind: function (types: string, fn: CallableFunction): JQuery {
		return this.off(types, undefined, fn);
	}
});
(jq.fn['init'] as NewableFunction).prototype = jq.fn;

jq.each(['remove', 'empty', 'show', 'hide'], function (i, nm) {
	_jq[nm] = jq.fn[nm] as CallableFunction;
	jq.fn[nm] = function (): JQuery {
		return !this.selector && this[0] === document as unknown as HTMLElement ? this :
			(_jq[nm] as CallableFunction).bind(this)(...arguments as unknown as []) as JQuery;
	};
});
jq.each(['before', 'after', 'append', 'prepend'], function (i, nm) {
	_jq[nm] = jq.fn[nm] as CallableFunction;
	jq.fn[nm] = function (w: zk.Widget | unknown, desktop: zk.Desktop): JQuery {
		if (!(w instanceof zk.Widget))
			return (_jq[nm] as CallableFunction).bind(this)(...(arguments as unknown as [])) as JQuery;

		if (!this.length) return this;
		if (!zk.Desktop._ndt) zk.stateless();

		var ret = (_jq[nm] as CallableFunction).bind(this)(/*safe*/ w.redrawHTML_()) as JQuery;
		if (!w.z_rod) {
			w.bind(desktop);
			zUtl.fireSized(w as zk.Widget);
		}
		return ret;
	};
});

Object.assign(jq, {
	nodeName(el?: Element, ...tags: string[]): boolean {
		var tag = el && el.nodeName ? el.nodeName.toLowerCase() : '',
			j = arguments.length;
		if (j <= 1)
			return !!tag;
		while (--j)
			if (tag == (arguments[j] as string).toLowerCase())
				return true;
		return false;// don't remove this line, texts are highlighted when SHIFT-click listitems (because of IE's onselect depends on it)
	},

	/**
	 * Converting an integer to a string ending with "px".
	 * <p>It is usually used for generating left or top.
	 * @param v - the number of pixels
	 * @returns the integer with string.
	 * @see {@link px0}
	 */
	px(v: number): string {
		return (v ?? 0) + 'px';
	},
	/**
	 * Converting an integer a string ending with "px".
	 * <p>Unlike {@link px}, this method assumes 0 if v is negative.
	 * <p>It is usually used for generating width or height.
	 * @param v - the number of pixels. 0 is assumed if negative.
	 * @returns the integer with string.
	 * @see {@link px}
	 */
	px0(v?: number): string {
		return Math.max(v ?? 0, 0) + 'px';
	},

	/**
	 * @returns an array of {@link DOMElement} that matches.
	 * It invokes `document.getElementsByName` to retrieve
	 * the DOM elements.
	 * @param id - the identifier
	 * @param subId - [Optional] the identifier of the sub-element.
	 * Example, `jq.$$('_u_12', 'cave');`.
	 */
	$$(id: string, subId?: string): NodeListOf<HTMLElement> {
		return document.getElementsByName(id + (subId ? '-' + subId : ''));
	},

	/**
	 * Tests if one element (p) is an ancestor of another (c).
	 * <p>Notice that, if you want to test widgets, please use
	 * {@link zUtl#isAncestor} instead.
	 *
	 * @param p - the parent element to test
	 * @param c - the child element to test
	 * @see zUtl#isAncestor
	 */
	isAncestor(p?: HTMLElement, c?: HTMLElement): boolean {
		if (!p) return true;
		for (; c; c = zk(c).vparentNode(true))
			if (p == c)
				return true;
		return false;
	},
	/**
	 * @returns the X coordination of the visible part of the browser window.
	 */
	innerX(): number {
		return Math.round(window.pageXOffset
			|| DocRoot().scrollLeft || 0); //ZK-2633: browser might return decimal number
	},
	/**
	 * @returns the Y coordination of the visible part of the browser window.
	 */
	innerY(): number {
		return Math.round(window.pageYOffset
			|| DocRoot().scrollTop || 0); //ZK-2633: browser might return decimal number
	},
	/**
	 * @returns the width of the viewport (visible part) of the browser window.
	 * It is the same as jq(window).width().
	 */
	innerWidth(): number {
		return jq(window).width()!;
	},
	/**
	 * @returns the height of the viewport (visible part) of the browser window.
	 * It is the same as jq(window).height().
	 */
	innerHeight(): number {
		return jq(window).height()!;
	},

	/**
	 * A map of the margin style names: `{l: 'margin-left', t: 'margin-top'...}`.
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles.
	 * @see {@link margins}
	 * @see {@link paddings}
	 */
	margins: {l: 'margin-left', r: 'margin-right', t: 'margin-top', b: 'margin-bottom'},
	/**
	 * A map of the border style names: `{l: 'border-left', t: 'border-top'...}`.
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles.
	 * @see {@link margins}
	 * @see {@link paddings}
	 */
	borders: {l: 'border-left-width', r: 'border-right-width', t: 'border-top-width', b: 'border-bottom-width'},
	/**
	 * A map of the padding style names: `{l: 'padding-left', t: 'padding-top'...}`.
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles.
	 * @see {@link margins}
	 * @see {@link borders}
	 */
	paddings: {l: 'padding-left', r: 'padding-right', t: 'padding-top', b: 'padding-bottom'},

	/**
	 * @returns the width of the scrollbar
	 */
	scrollbarWidth(): number {
		var devicePixelRatio = zUtl.getDevicePixelRatio(),
			body = document.body;
		if (this['_lastDevicePixelRatio'] != devicePixelRatio) {
			this['_lastDevicePixelRatio'] = devicePixelRatio;
			if (_sbwDiv) {
				body.removeChild(_sbwDiv);
				_sbwDiv = undefined;
			}
		}
		if (!_sbwDiv) {
			_sbwDiv = document.createElement('div');
			const outerDivStyleString = 'top:-1000px;left:-1000px;position:absolute;visibility:hidden;border:none;width:50px;height:50px;overflow:scroll;';
			// `!!` is to prevent the TS error "This condition will always return true" without resorting to `@ts-ignore` or `@ts-expect-error`.
			// eslint-disable-next-line no-extra-boolean-cast
			if (!!HTMLElement.prototype.attachShadow) {
				// Shadow DOM is supported. The purpose of using a shadow DOM is explained in the comment preceding `shadow.appendChild(style)`.

				// `mode` must be `'open'` so that its children can be access externally via `_sbwDiv.shadowRoot`
				const shadow = _sbwDiv.attachShadow({mode: 'open'});
				// Append `div` before `style`
				shadow.appendChild(document.createElement('div'));
				const style = document.createElement('style');
				style.textContent =
					':host{' + outerDivStyleString + '}' +
					':host::-webkit-scrollbar{background-color:gold;}'; // This line is the key. The body of `::-webkit-scrollbar` must not be empty.
				// By concretely styling `::-webkit-scrollbar`, the scrollbar will be forced into displaying itself
				// "obtrusively" as opposed to "floating" for browsers based on WebKit (e.g., Chrome, Safari, and Edge)
				// on macOS and Win11, so that we can compute the scrollbar width through DOM API.

				// Use shadow DOM to create a scoped `style`. This is to prevent name conflicts and tricky rule
				// precedence in global CSS, as CSS rules won't leak in to nor leak out of shadow DOM. Also, controlling
				// the CSS rule here directly with JS avoids the need to duplicate CSS rules for all ZK themes which
				// people often (understandably) forget to do as demonstrated in ZK-5170.
				shadow.appendChild(style);
			} else {
				// Among all browsers we currently support, only IE doesn't support shadow DOM. For IE, `::-webkit-scrollbar`
				// won't take effect either. Furthermore, IE can't run on macOS nor Win11 where the "floating" scrollbar
				// plagues. Hence, we don't have to do anything fancy.
				_sbwDiv.style.cssText = outerDivStyleString;
				_sbwDiv.appendChild(document.createElement('div'));
			}
			body.appendChild(_sbwDiv);
		}
		if (!_sbwDiv._value) {
			const innerDiv = (_sbwDiv.shadowRoot || _sbwDiv).firstChild as HTMLElement;
			let width = _sbwDiv.getBoundingClientRect().width - innerDiv.getBoundingClientRect().width;
			if (width < 2) {
				// `width` will result in 0 for Firefox on macOS and Win11 with "floating" scrollbars. In this case, we
				// return a scrollbar width of a sensible default value. We use the condition `width < 2` instead of
				// `width === 0` to account for possible floating point imprecision.
				width = 16; // Many browsers have a default scrollbar width of 16 or 17 pixels.
			}
			_sbwDiv._value = width;
		}
		return _sbwDiv._value;
	},
	isOverlapped(ofs1: zk.Offset, dim1: zk.Offset, ofs2: zk.Offset, dim2: zk.Offset, tolerant?: number): boolean {
		var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
			o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1,
			o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
			o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
		if (tolerant) {
			return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1
				&& o1x2 - o2x1 > tolerant && o2x2 - o1x1 > tolerant
				&& o1y2 - o2y1 > tolerant && o2y2 - o1y1 > tolerant;
		} else
			return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
	},

	/**
	 * Clears the current selection in the browser window.
	 * <p>Notice: {@link jqzk#setSelectionRange} is used for the input-type
	 * elements, while this method is applied to the whole browser window.
	 * @see jqzk#setSelectionRange
	 * @see jqzk#enableSelection
	 * @see jqzk#disableSelection
	 * @returns whether it is cleared successfully
	 */
	clearSelection(): boolean {
		try {
			const sel = window.getSelection();
			// eslint-disable-next-line zk/noNull
			if (zk.webkit) sel?.collapse(null);
			else sel?.removeAllRanges();

			return true;
		} catch (e) {
			return false;
		}
	},
	filterTextStyle(style: Record<string, string> | string, plus?: string[]): Record<string, string> | string {
		if (typeof style == 'string') {
			let ts = '';
			if (style)
				for (var j = 0, k = 0; k >= 0; j = k + 1) {
					k = style.indexOf(';', j);
					var s = k >= 0 ? style.substring(j, k) : style.substring(j),
						l = s.indexOf(':'),
						nm = l < 0 ? s.trim() : s.substring(0, l).trim();
					if (nm && (_txtStyles.$contains(nm)
					|| _txtStyles2.$contains(nm)
					|| (plus && plus.$contains(nm))))
						ts += s + ';';
				}
			return ts;
		}

		let ts: Record<string, string> = {};
		for (var nm in style)
			if (_txtStyles.$contains(nm) || _txtStyles2.$contains(nm)
			|| (plus && plus.$contains(nm)))
				ts[nm] = style[nm];
		return ts;
	},

	/**
	 * Parses a string-type CSS style into a map of names and values of styles.
	 * It is usually used with jq.css(map) to update the CSS style of an element.
	 * @param style - the style to parse
	 * @returns a map of styles (name, value)
	 */
	parseStyle(style: string): Record<string, string> {
		var map = {};
		if (style) {
			var dummy = document.createElement('div');
			dummy.setAttribute('style', style);
			var dummyStyle = dummy.style;
			for (let i = 0, length = dummyStyle.length; i < length; i++) {
				var nm = dummyStyle[i];
				map[nm] = dummyStyle.getPropertyValue(nm);
			}
		}
		return map;
	},

	/**
	 * Creates an IFRAME element with the specified ID, src and style.
	 * @param id - ID (required)
	 * @param src - the source URL. If omitted, an one-pixel gif is assumed.
	 * @param style - the CSS style. Ingored if omitted.
	 */
	newFrame(id: string, src?: string, style?: string): HTMLIFrameElement {
		if (!src) src = zjq.src0;
			//IE: prevent secure/nonsecure warning with HTTPS

		// eslint-disable-next-line zk/noMixedHtml
		var html = '<iframe id="' + id + '" name="' + id + '" src="' + src + '"';
		if (style == null) style = 'display:none';
		// eslint-disable-next-line zk/noMixedHtml
		html += ' style="' + style + '"></iframe>';
		jq(document.body).append(DOMPurify.sanitize(html));
		return zk(id).jq[0] as HTMLIFrameElement;
	},
	/**
	 * Creates a 'stackup' (actually, an iframe) that makes an element
	 * (with position:absolute) shown above others.
	 * It is used to solve the layer issues of the browser.
	 * <ol>
	 * <li>IE6: SELECT's dropdown above any other DOM element</li>
	 * <li>All browser: PDF iframe above any other DOM element. However, this approach works only in FF and IE, and FF doesn't position IFRAME well if two or more of them are with the absolute position. </li>
	 * </ol>
	 *
	 * <p>Notice that you usually have to call {@link jqzk#makeVParent} before calling this, since DIV with relative or absolute position will crop the child element. In other words, you have to make the element as the top-level element before creating a stackup for it.
	 * <p>To remove the stackup, call {@link remove}.
	 * <p>If you want to create a shadow, you don't need to access this method since {@link zk.eff.Shadow} has an option to create and maintain the stackup automatically.
	 * @param el - the element to retrieve the dimensions. If omitted, the stackup is not appended to the DOM tree.
	 * @param id - ID of the stackup (iframe). If omitted and el is specified, it is el.id + '$ifrstk'. If both el and id are omitted, 'z_ifrstk' is assumed.
	 * @param anchor - where to insert the DOM element before
	 * (i.e., anchor will become the next sibling of the stackup, so anchor will be on top of the stackup if z-index is the same). If omitted, el is assumed.
	 */
	newStackup(el: HTMLElement | undefined, id: string, anchor?: Node): HTMLIFrameElement {
		el = jq(el || [], zk)[0];
		var ifr = document.createElement('iframe');
		ifr.id = id ?? (el ? el.id + '-ifrstk' : 'z_ifrstk');
		ifr.style.cssText = 'position:absolute;overflow:hidden;opacity:0;width:0;height:0;border:none;filter:alpha(opacity=0)';
		ifr.setAttribute('aria-hidden', 'true');
		ifr.tabIndex = -1;
		ifr.src = zjq.src0;
		if (el) {
			ifr.style.width = el.offsetWidth + 'px';
			ifr.style.height = el.offsetHeight + 'px';
			ifr.style.top = el.style.top;
			ifr.style.left = el.style.left;
			ifr.style.zIndex = el.style.zIndex;
			el.parentNode!.insertBefore(ifr, anchor || el);
		}
		return ifr;
	},
	/**
	 * Creates a HIDDEN element
	 * @param name - the name of the HIDDEN tag.
	 * @param value - the value of the HIDDEN tag.
	 * @param parent - the parent node. Ignored if not specified.
	 */
	newHidden(nm: string, val: string, parent?: Node): HTMLInputElement {
		var inp = document.createElement('input');
		inp.type = 'hidden';
		inp.name = nm;
		inp.value = val;
		if (parent) parent.appendChild(inp);
		return inp;
	},

	/**
	 * @returns the head element of this document.
	 * @since 5.0.1
	 */
	head(): HTMLElement | undefined {
		return document.getElementsByTagName('head')[0] || document.documentElement;
	},

	//dialog//
	/**
	 * It is the same as `window.confirm`, except it will set
	 * {@link zk#alerting} so widgets know to ignore `onblur` (since the focus will be back later).
	 * <p>It is strongly suggested to use this method instead of `window.confirm`.
	 * @returns whether the Yes button is pressed
	 */
	confirm(msg: string): boolean {
		zk.alerting = true;
		try {
			return confirm(msg); // eslint-disable-line no-alert
		} finally {
			try {
				zk.alerting = false;
			} catch (e) {
				zk.debugLog((e as Error).message ?? e);
			} //doc might be unloaded
		}
	},
	/**
	 * Shows up a message.
	 * If opts.mode is os, this method is the same as window.alert, except it will set
	 * {@link zk#alerting}, so widgets (particularly input widgets) know to ignore onblur (since the focus will be back later).
	 * <p>It is strongly suggested to use this method instead of window.alert.
	 * <p>If opts is omitted or opts.mode is not os, it is similar to
	 * `org.zkoss.zul.Messagebox.show()` at the server.
	 * ```ts
	 * jq.alert('Hi');
	 * jq.alert('This is a popup message box', {mode:"popup", icon: "ERROR"});
	 * jq.alert('With listener', {
	 * 	button : {
	 *   		YES: function () {jq.alert('Yes clicked')},
	 *   		NO: function () {jq.alert('No clicked')}
	 * 	}
	 * });
	 * ```
	 * @param msg - the message to show
	 * @param opts - the options.
	 * <table border="1" cellspacing="0" width="100%">
	 * <caption> Allowed Options
	 * </caption>
	 * <tr>
	 * <th> Name
	 * </th><th> Allowed Values
	 * </th><th> Default Value
	 * </th><th> Description
	 * </th></tr>
	 * <tr>
	 * <td> icon
	 * </td><td> 'QUESTION', 'EXCLAMATION', 'INFORMATION', 'ERROR', 'NONE'
	 * </td><td> 'INFORMATION'
	 * </td><td> Specifies the icon to display.
	 * </td></tr>
	 * <tr>
	 * <td> mode
	 * </td><td> 'overlapped', 'popup', 'modal'
	 * </td><td> 'modal'
	 * </td><td> Specifies which window mode to use.
	 * </td></tr>
	 * <tr>
	 * <td> title
	 * </td><td> any string
	 * </td><td> 'ZK'
	 * </td><td> Specifies the message box's title.
	 * </td></tr>
	 * <tr>
	 * <td> desktop
	 * </td><td> a desktop ({@link Desktop}) or null
	 * </td><td> The current desktop
	 * </td><td> Specifies which desktop this message box belongs to. You rarely need to specify it.
	 * </td></tr>
	 * <tr>
	 * <td> button
	 * </td><td> a map ({@link Map}) of buttons.
	 * </td><td> If null or empty, OK is assumed
	 * </td><td> Specifies what buttons to display. The key is the button name,
	 * and the value is a function ({@link Function}) to execute when the button
	 * is clicked.
	 * The label is assumed to be `msgzul[name.toUpperCase()]||name`.
	 * Localized labels include OK, Cancel, Yes, No, Retry, Abort, Ignore, Reload.
	 * You can add your own labels by puttingit to `msgzul`.
	 * </td></tr>
	 * </table>
	 */
	alert(msg: string, opts?: zk.AlertOptions): void {
		zk.alerting = true;
		try {
			alert(msg); // eslint-disable-line no-alert
		} finally {
			try {
				zk.alerting = false;
			} catch (e) {
				zk.debugLog((e as Error).message ?? e);
			} //doc might be unloaded
		}
	},
	/**
	 * To register one object for the `zsync` invocation.
	 * For example,
	 * ```ts
	 * jq.onzsync(obj1);
	 * ```
	 * @param obj - the object to register
	 * @see {@link zsync}
	 * @see {@link unzsync}
	 * @since 5.0.1
	 */
	onzsync(obj: ZSyncObject): void {
		_zsyncs.unshift(obj);
	},
	/**
	 * To unregister one object for the `zsync` invocation.
	 * For example,
	 * ```ts
	 * jq.unzsync(obj1);
	 * ```
	 * @param obj - the object to register
	 * @see {@link zsync}
	 * @see {@link onzsync}
	 * @since 5.0.1
	 */
	unzsync(obj: ZSyncObject): void {
		_zsyncs.$remove(obj);
	},
	/**
	 * To invoke the `zsync` method of the registered objects.
	 * <p>`zsync` is called automatically when {@link zWatch}
	 * fires onSize, onShow or onHide.
	 * It is useful if you have a DOM element whose position is absolute.
	 * Then, if you register the widget, the widget's zsync method will be called when some widget becomes visible, is added and so on.
	 *
	 * <p>For example, {@link zul.wnd.Window} uses DIV to simulate the shadow in Firefox,
	 * then it can register itself in {@link Widget#bind_} and then
	 * synchronize the position and size of shadow (DIV) in zsync as follows.
	 * ```ts
	 * bind_: function () {
	 *   if (zk.ff) jq.onzsync(this); //register
	 * ...
	 * },
	 * unbind_: function () {
	 *   if (zk.ff) jq.unzsync(this); //unregister
	 * ...
	 * },
	 * zsync: function () {
	 * this._syncShadow(); //synchronize shadow
	 * ...
	 * }
	 * ```
	 * <p>Notice that it is better not to use the absolute position for any child element, so the browser will maintain the position for you.
	 * After all, it runs faster and zsync won't be called if some 3rd-party library is used to create DOM element directly (without ZK).
	 */
	zsync(org: zk.Object): void {
		++_pendzsync;
		setTimeout(function () {_zsync(org);}, 50);
	},

	/**
	 * Move the focus out of any element.
	 * <p>Notice that you cannot simply use `jq(window).focus()`
	 * or `zk(window).focus()`,
	 * because it has no effect for browsers other than IE.
	 * @since 5.0.1
	 */
	focusOut(): void {
		var a = jq('#z_focusOut')[0];
		if (!a) {
			// for Chrome and Safari, we can't set "display:none;"
			jq(document.body).append(/*safe*/ '<a href="javascript:;" style="position:absolute;'
					+ 'left:' + jq.px(zk.clickPointer[0]) + ';top:' + jq.px(zk.clickPointer[1])
					+ ';" id="z_focusOut"/>');
			a = jq('#z_focusOut')[0];
		}
		a.focus();
		setTimeout(function () {jq(a).remove();}, 500);
	},

	_syncScroll: <Record<string, zk.Widget>> {},
	/**
	 * To register one object for the `doSyncScroll` invocation.
	 * For example,
	 * ```ts
	 * onSyncScroll();
	 * ```
	 * @param wgt - the object to register
	 * @see {@link doSyncScroll}
	 * @see {@link unSyncScroll}
	 * @since 6.5.0
	 */
	onSyncScroll(wgt: zk.Widget): void {
		var sync = this._syncScroll;
		if (!sync[wgt.uuid])
			sync[wgt.uuid] = wgt;
	},
	/**
	 * To invoke the `doSyncScroll` method of the registered objects.
	 * <p>`doSyncScroll` is called automatically when {@link zWatch}
	 * fires onResponse, onShow or onHide.
	 * It is useful if you have a Widget that using zul.Scrollbar.
	 * Then, if you register the widget, the widget's doSyncScroll method will be called when widget add/remove/hide/show its child widget.
	 * @see {@link onSyncScroll}
	 * @see {@link unSyncScroll}
	 * @since 6.5.0
	 */
	doSyncScroll(): void {
		var sync = this._syncScroll;
		for (var id in sync) {
			sync[id].doResizeScroll_();
		}
		this._syncScroll = {}; // reset
	},
	/**
	 * To unregister one object for the `doSyncScroll` invocation.
	 * For example,
	 * ```ts
	 * unSyncScroll(wgt);
	 * ```
	 * @param wgt - the object to register
	 * @see {@link doSyncScroll}
	 * @see {@link onSyncScroll}
	 * @since 6.5.0
	 */
	unSyncScroll(wgt: zk.Widget): void {
		delete this._syncScroll[wgt.uuid];
	}
});

/** @class jq.Event
 * A DOM event.
 */
export interface EventMetaData {
	altKey?: boolean;
	ctrlKey?: boolean;
	shiftKey?: boolean;
	metaKey?: boolean;
	which: number;
}
export interface EventMouseData extends EventMetaData {
	pageX: number | undefined;
	pageY: number | undefined;
}
export interface EventKeyData extends EventMetaData {
	keyCode: string | number | undefined;
	charCode: number | undefined;
	key: string | undefined;
}
/** @internal */
export const _JQEvent = {
	/**
	 * Stops the event propagation.
	 */
	stop(this: JQuery.Event): void {
		this.preventDefault();
		this.stopPropagation();
	},
	/**
	 * @returns the mouse information of a DOM event. The properties of the returned object include pageX, pageY and the meta information
	 * @see {@link zk.Event.data}
	 */
	mouseData(this: JQuery.MouseEventBase): EventMouseData {
		return zk.copy({
			pageX: this.pageX, pageY: this.pageY
		}, this.metaData());
	},
	/**
	 * @returns the key information of a DOM event. The properties of the returned object include keyCode, charCode, key and the meta information ({@link metaData}).
	 * @see {@link zk.Event.data}
	 */
	keyData(this: JQuery.KeyboardEventBase): EventKeyData {
		return zk.copy({
			keyCode: this.keyCode,
			charCode: this.charCode,
			key: this._keyDataKey()
			}, this.metaData());
	},
	/** @internal */
	_keyDataKey(this: JQuery.KeyboardEventBase): string {
		// Ref: https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values
		var key = this.originalEvent!.key;
		switch (key) {
			case 'Scroll': return 'ScrollLock';
			case 'Spacebar': return ' ';
			case 'Left': return 'ArrowLeft';
			case 'Right': return 'ArrowRight';
			case 'Up': return 'ArrowUp';
			case 'Down': return 'ArrowDown';
			case 'Del': return 'Delete';
			case 'Crsel': return 'CrSel';
			case 'Exsel': return 'ExSel';
			case 'Esc': return 'Escape';
			case 'App': return 'ContextMenu';
			case 'Nonconvert': return 'NonConvert';
			case 'MediaNextTrack': return 'MediaTrackNext';
			case 'MediaPreviousTrack': return 'MediaTrackPrevious';
			case 'FastFwd': return 'MediaFastForward';
			case 'VolumeUp': return 'AudioVolumeUp';
			case 'VolumeDown': return 'AudioVolumeDown';
			case 'VolumeMute': return 'AudioVolumeMute';
			case 'Live': return 'TV';
			case 'Zoom': return 'ZoomToggle';
			case 'SelectMedia':
			case 'MediaSelect':
				return 'LaunchMediaPlayer';
			case 'Decimal': return '.';
			case 'Multiply': return '*';
			case 'Add': return '+';
			case 'Divide': return '/';
			case 'Subtract': return '-';
			default: return key;
		}
	},
	/**
	 * @returns the meta-information of a DOM event. The properties of the returned object include altKey, ctrlKey, shiftKey, metaKey and which.
	 * @see {@link zk.Event.data}
	 */
	metaData(this: JQuery.Event): EventMetaData {
		var inf: EventMetaData = {
			which: this.which || 0
		};
		if (this.altKey) inf.altKey = true;
		if (this.ctrlKey) inf.ctrlKey = true;
		if (this.shiftKey) inf.shiftKey = true;
		if (this.metaKey) inf.metaKey = true;
		return inf;
	}
};
zk.copy(jq.Event.prototype, _JQEvent);

/** @internal */
export const _JQEventStatic = {
	/**
	 * Fires a DOM element.
	 * @param el - the target element
	 * @param evtnm - the name of the event
	 */
	fire(el: HTMLElement, evtnm: string): void {
		var evt = document.createEvent('HTMLEvents');
		evt.initEvent(evtnm, false, false);
		el.dispatchEvent(evt);
	},
	/**
	 * Stops the event propagation of the specified event.
	 * It is usually used as the event listener, such as
	 * ```ts
	 * jq(el).mousemove(jq.Event.stop)
	 * ```
	 * @param evt - the event.
	 */
	stop(evt: JQuery.Event): void {
		evt.stop();
	},
	/**
	 * @returns only the properties that are meta data, such as altKey, ctrlKey, shiftKey, metaKey and which.
	 * <p>For example, the following returns `{ctrlKey:true}`.
	 * ```ts
	 * jq.event.filterMetaData({some:1,ctrlKey:true});
	 * ```
	 * @param data - a map of data. It is usually the value returned
	 * by {@link mouseData} or {@link metaData}.
	 */
	filterMetaData(data: EventMetaData): EventMetaData {
		var inf: EventMetaData = {
			which: data.which || 0
		};
		if (data.altKey) inf.altKey = true;
		if (data.ctrlKey) inf.ctrlKey = true;
		if (data.shiftKey) inf.shiftKey = true;
		if (data.metaKey) inf.metaKey = true;
		return inf;
	},
	/**
	 * Converts a DOM event ({@link jq.Event}) to a ZK event ({@link zk.Event}).
	 * @param evt - the DOM event
	 * @param wgt - the target widget. It is used if the widget
	 * can be resolved from the event (`zk.Widget.$(evt)`)
	 * @returns the ZK event
	 */
	zk(evt: JQuery.TriggeredEvent, wgt?: zk.Widget): zk.Event {
		var type = evt.type,
			target = zk.Widget.$(evt) ?? wgt,
			data;

		if (type.startsWith('mouse')) {
			if (type.length > 5)
				type = 'Mouse' + type.charAt(5).toUpperCase() + type.substring(6);
			data = (evt as JQuery.MouseEventBase).mouseData();
		} else if (type.startsWith('key')) {
			if (type.length > 3)
				type = 'Key' + type.charAt(3).toUpperCase() + type.substring(4);
			data = (evt as JQuery.KeyboardEventBase).keyData();
		} else if (type == 'dblclick') {
			data = (evt as JQuery.MouseEventBase).mouseData();
			type = 'DoubleClick';
		} else {
			if (type == 'click')
				data = (evt as JQuery.MouseEventBase).mouseData();
			type = type.charAt(0).toUpperCase() + type.substring(1);
		}
		return new zk.Event(target, 'on' + type, data, {}, evt);
	}
};
Object.assign(jq.Event, _JQEventStatic);
// eslint-disable-next-line @typescript-eslint/ban-types
export var delayQue: Record<string, Function[]> = {};
zk.delayQue = delayQue; //key is uuid, value is array of pending functions
/**
 * Execute function related to specified widget after a while,
 * and will insure the execution order.
 * @param uuid - wgt's uuid
 * @param func - a function to be executed
 * @param opts - the options. Allowed options:
 * <ul>
 * <li>int timeout: number of milliseconds to wait before executing the function. Default: 50</li>
 * <li>boolean urgent: whether to execute function as soon as possible</li>
 * </ul>
 * Note: timeout is only meaningful for the first function added to wgt
 */
export function delayFunction(uuid: string, func: () => void, opts?: Partial<{ timeout: number; urgent: boolean }>): void {
	if (uuid && typeof func == 'function') {
		if (!opts)
			opts = {};
		var timeout = opts.timeout,
			urgent = opts.urgent, //indicate the func should be executed as soon as possible
			idQue = zk.delayQue[uuid];
		if (!idQue || !idQue.length) { //execute directly
			zk.delayQue[uuid] = idQue = [func];
			setTimeout(function () {
				var pendFunc = idQue.shift();
				while (pendFunc) {
					pendFunc();
					pendFunc = idQue.shift();
				}
			}, Number(timeout) >= 0 ? timeout : 50);
		} else { //put func to queue
			if (urgent)
				idQue.splice(0, 0, func);
			else
				idQue.push(func);
		}
	}
}
zk.delayFunction = delayFunction;
zk.JQZK = JQZK;