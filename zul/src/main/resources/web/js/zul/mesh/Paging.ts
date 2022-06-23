/* Paging.ts

	Purpose:

	Description:

	History:
		Fri Jan 23 15:00:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _rerenderIfBothPaging(wgt: Paging): true | undefined {
	if (wgt.isBothPaging()) {
		wgt.parent!.rerender();
		return true;
	}
}

//Returns whether the string is integer or not
function _isUnsignedInteger(s: string | number): boolean {
	return s.toString().search(/^[0-9]+$/) == 0;
}

export interface PagingFocusInfo {
	uuid: string;
	lastPos?: [number, number];
	inpIdx?: number;
}

/**
 * Paging of long content.
 *
 * <p>Default {@link #getZclass}: z-paging.
 */
export class Paging extends zul.Widget {
	public override parent!: zul.mesh.MeshWidget | null;
	private _pageSize = 20;
	private _totalSize = 0;
	private _pageCount = 1;
	private _activePage = 0;
	private _pageIncrement = zk.mobile ? 5 : 10;
	private _showFirstLast = true;
	private _detailed?: boolean;
	private _autohide?: boolean;
	private _disabled?: boolean;
	private _meshWidget?: zul.mesh.MeshWidget | null;
	private static _autoFocusInfo: PagingFocusInfo | null;
	private _lastIsWide?: boolean;

	/** Sets the total number of items.
	 * @param int totalSize
	 */
	public setTotalSize(v: number, opts?: Record<string, boolean>): this {
		const o = this._totalSize;
		this._totalSize = v;

		if (o !== v || (opts && opts.force)) {
			this._updatePageNum();
			if (this._detailed) {
				if (!_rerenderIfBothPaging(this)) {
					var info = this.$n('info');
					if (info) {
						info.innerHTML = this.infoText_();
					} else if (this._totalSize) {
						this.rerender(); // recreate infoTag
					}
				}
			}
		}

		return this;
	}

	/** Returns the total number of items.
	 * @return int
	 */
	public getTotalSize(): number {
		return this._totalSize;
	}

	/** Returns the number of page anchors shall appear at the client.
	 *
	 * <p>Default: 10.
	 * @return int
	 */
	public getPageIncrement(): number {
		return this._pageIncrement;
	}

	/** Sets the number of page anchors shall appear at the client.
	 * @param int pageIncrement
	 */
	public setPageIncrement(pageIncrement: number, opts?: Record<string, boolean>): this {
		const o = this._pageIncrement;
		this._pageIncrement = pageIncrement;

		if (o !== pageIncrement || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether to show the detailed info, such as {@link #getTotalSize}.
	 * @return boolean
	 */
	public isDetailed(): boolean | undefined {
		return this._detailed;
	}

	/** Sets whether to show the detailed info, such as {@link #getTotalSize}.
	 * @param boolean detailed
	 */
	public setDetailed(detailed: boolean, opts?: Record<string, boolean>): this {
		const o = this._detailed;
		this._detailed = detailed;

		if (o !== detailed || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @return int
	 */
	public getPageCount(): number {
		return this._pageCount;
	}

	/** Sets the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @param int pageCount
	 */
	public setPageCount(pageCount: number, opts?: Record<string, boolean>): this {
		const o = this._pageCount;
		this._pageCount = pageCount;

		if (o !== pageCount || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the active page (starting from 0).
	 * @return int
	 */
	public getActivePage(): number {
		return this._activePage;
	}

	/** Sets the active page (starting from 0).
	 * @param int activePage
	 */
	public setActivePage(activePage: number, opts?: Record<string, boolean>): this {
		const o = this._activePage;
		this._activePage = activePage;

		if (o !== activePage || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the page size, aka., the number rows per page.
	 * @return int
	 */
	public getPageSize(): number {
		return this._pageSize;
	}

	/** Sets the page size, aka., the number rows per page.
	 * @param int pageSize
	 */
	public setPageSize(pageSize: number, opts?: Record<string, boolean>): this {
		const o = this._pageSize;
		this._pageSize = pageSize;

		if (o !== pageSize || (opts && opts.force)) {
			this._updatePageNum();
		}

		return this;
	}

	/**
	 * Returns whether to automatically hide this component if there is only one
	 * page available.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	public isAutohide(): boolean | undefined {
		return this._autohide;
	}

	/**
	 * Sets whether to automatically hide this component if there is only one
	 * page available.
	 * @param boolean autohide
	 */
	public setAutohide(autohide: boolean, opts?: Record<string, boolean>): this {
		const o = this._autohide;
		this._autohide = autohide;

		if (o !== autohide || (opts && opts.force)) {
			if (this.getPageCount() == 1) this.rerender();
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 * @since 8.0.3
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 * @since 8.0.3
	 */
	public setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			if (this.desktop) this._drawDisabled(disabled);
		}

		return this;
	}

	/** Returns the associated {@link zul.mesh.MeshWidget} if any.
	 * @return zul.mesh.MeshWidget
	 * @since 10.0.0
	 */
	public getMeshWidget(): zul.mesh.MeshWidget | null | undefined {
		return this._meshWidget;
	}

	/** Sets the associated {@link zul.mesh.MeshWidget}.
	 * @param zul.mesh.MeshWidget meshWidget
	 * @since 10.0.0
	 */
	public setMeshWidget(v: zul.mesh.MeshWidget | null): this {
		this._meshWidget = v;
		return this;
	}

	public override setStyle(style: string): void {
		super.setStyle(style);
		_rerenderIfBothPaging(this);
	}

	public override setSclass(sclass: string): void {
		super.setSclass(sclass);
		_rerenderIfBothPaging(this);
	}

	public override setWidth(width: string | null): void {
		super.setWidth(width);
		_rerenderIfBothPaging(this);
		if (this.desktop)
			zUtl.fireSized(this, -1);
	}

	public override setHeight(height: string | null): void {
		super.setHeight(height);
		_rerenderIfBothPaging(this);
	}

	public override setLeft(left: string): void {
		super.setLeft(left);
		_rerenderIfBothPaging(this);
	}

	public override setTop(top: string): void {
		super.setTop(top);
		_rerenderIfBothPaging(this);
	}

	public override setTooltiptext(tooltiptext: string): void {
		super.setTooltiptext(tooltiptext);
		_rerenderIfBothPaging(this);
	}

	public override replaceHTML(n: HTMLElement | string, desktop: zk.Desktop | null, skipper?: zk.Skipper | null, _trim_?: boolean, _callback_?: CallableFunction[]): void {
		if (!_rerenderIfBothPaging(this))
		super.replaceHTML(n, desktop, skipper, _trim_, _callback_);
	}

	/**
	 * Returns whether the paging is in both mold. i.e. Top and Bottom
	 * @return boolean
	 */
	public isBothPaging(): boolean | null | undefined {
		return this.parent && this.parent.getPagingPosition
					&& 'both' == this.parent.getPagingPosition();
	}

	private _drawDisabled(disabled: boolean): void {
		var uuid = this.uuid,
			ap = this.getActivePage(),
			pc = this.getPageCount(),
			input = jq.$$(uuid, 'real')!;
		if ('os' == this.getMold()) {
			var btns = jq.$$(uuid, 'button')!;
			jq(btns).attr('disabled', disabled);
		} else {
			var postfix = (ap == 0 && ap == pc - 1) ? [] : ap == 0 ? ['last', 'next'] : ap == pc - 1 ? ['first', 'prev'] : ['first', 'prev', 'last', 'next'];
			for (var j = input.length; j--;) {
				jq(input[j]).attr('disabled', disabled);
				for (var k = postfix.length; k--;)
					for (var btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', disabled);
				var fnm = disabled ? 'addClass' as const : 'removeClass' as const,
					pgtext = input[j].nextSibling;
				jq(pgtext!)[fnm](this.$s('text-disabled'));
			}
		}
	}

	private _updatePageNum(): void {
		var pageCount = Math.floor((this.getTotalSize() - 1) / this._pageSize + 1);
		if (pageCount == 0) pageCount = 1;
		if (pageCount != this.getPageCount()) {
			this.setPageCount(pageCount);
			if (this.getActivePage() >= pageCount)
				this.setActivePage(pageCount - 1);
			if (this.desktop && this.parent) {
				if (!_rerenderIfBothPaging(this)) {
					this.rerender();

					// Bug 2931951
					if (this.parent instanceof zul.mesh.MeshWidget) {
						var self = this;
						// Bug ZK-2624
						setTimeout(function () {
							if (self.desktop) {
								var n = self.parent!.$n();

								// reset and recalculate
								if (n && n._lastsz) {
									n._lastsz = null;
									self.parent!.onSize();
								}
							}
						});
					}
				}
			}
		}
	}

	/**
	 * Returns the information text of the paging, if {@link #isDetailed()} is enabled.
	 * @return String
	 */
	protected infoText_(): string {
		var acp = this.getActivePage(),
			psz = this.getPageSize(),
			tsz = this.getTotalSize(),
			lastItem = (acp + 1) * psz,
			dash = '';

		if ('os' != this.getMold())
			dash = ' - ' + (lastItem > tsz ? tsz : lastItem);

		return '[ ' + (acp * psz + 1) + dash + ' / ' + tsz + ' ]';
	}

	private _infoTags(out: string[]): void {
		if (this.getTotalSize() == 0)
			return;
		var uuid = this.uuid,
			nameOrId = _rerenderIfBothPaging(this) ? 'name' : 'id'; // Bug ZK-2280
		out.push('<div ', nameOrId, '="', uuid, '-detail" class="', this.$s('info'), '"><span ',
				nameOrId, '="', uuid, '-info" aria-hidden="true">', this.infoText_(), '</span></div>');
	}

	public _innerTags(): string {
		var out = new zk.Buffer<string>(),
			pinc = this.getPageIncrement(),
			pcount = this.getPageCount(),
			acp = this.getActivePage(),
			half = Math.round(pinc / 2),
			begin: number,
			end = acp + half - 1;

		if (end >= pcount) {
			end = pcount - 1;
			begin = end - pinc + 1;
			if (begin < 0)
				begin = 0;
		} else {
			begin = acp - half;
			if (begin < 0)
				begin = 0;
			end = begin + pinc - 1;
			if (end >= pcount)
				end = pcount - 1;
		}
		out.push('<ul>');
		if (acp > 0) {
			if (begin > 0) //show first
				this.appendAnchor(out, msgzul.FIRST, 0);
			this.appendAnchor(out, msgzul.PREV, acp - 1);
		}

		var bNext = acp < pcount - 1;
		for (; begin <= end; ++begin)
			this.appendAnchor(out, begin + 1, begin, begin == acp);

		if (bNext) {
			this.appendAnchor(out, msgzul.NEXT, acp + 1);
			if (end < pcount - 1) //show last
				this.appendAnchor(out, msgzul.LAST, pcount - 1);
		}
		out.push('</ul>');
		if (this.isDetailed())
			this._infoTags(out);
		return out.join('');
	}

	public appendAnchor(out: string[], label: string | number, val: number, seld?: boolean): void {
		var isInt = _isUnsignedInteger(label),
			cls = this.$s('button'),
			navCls = '';

		if (!isInt) {
			cls += ' ' + this.$s('noborder');
			navCls = ' class="' + this.$s('navigate') + '"';
		}
		if (seld)
			cls += ' ' + this.$s('selected');

		out.push('<li', navCls, '><a name="', this.uuid, '-button" class="', cls,
			'" href="javascript:;" data-paging="', val as unknown as string, '"', seld ? ' aria-current="page"' : '', '>', label as string, '</a></li>');
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no),
			added = 'os' == this.getMold() ? ' ' + this.$s('os') : '';
		return cls + added;
	}

	public override isVisible(strict?: boolean): boolean | null | undefined {
		var visible = super.isVisible(strict);
		return visible && (this.getPageCount() > 1 || !this._autohide);
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this});
		var uuid = this.uuid,
			input = jq.$$(uuid, 'real')!,
			pcount = this.getPageCount(),
			acp = this.getActivePage(),
			postfix = ['first', 'prev', 'last', 'next'] as const,
			focusInfo = zul.mesh.Paging._autoFocusInfo;
		type ProxyMethodName = `_dom${typeof postfix[number]}Click`;

		if (!this.$weave)
			for (var i = input.length; i--;)
				jq(input[i]).on('keydown', this.proxy(this._domKeyDown)).on('blur', this.proxy(this._domBlur));

		for (var k = postfix.length; k--;) {
			var btn = jq.$$(uuid, postfix[k])!;
			for (var j = btn.length; j--;) {
				if (!this.$weave)
					jq(btn[j]).on('click', this.proxy(this[('_dom' + postfix[k] + 'Click') as ProxyMethodName]));

				if (pcount == 1) {
					jq(btn[j]).attr('disabled', true);
				} else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (acp == 0)
						jq(btn[j]).attr('disabled', true);
				} else if (acp == pcount - 1) {
					jq(btn[j]).attr('disabled', true);
				}
			}
		}

		if (this.getMold() == 'os') {
			var btns = jq.$$(uuid, 'button')!;
			for (var j = btns.length; j--;) {
				var self = this;
				jq(btns[j]).on('click', function (this: HTMLElement) {
					if (self.isDisabled()) return;
					Paging.go(self, parseInt(jq(this).attr('data-paging')!));
				});
			}
		}

		if (this.isDisabled()) this._drawDisabled(true);

		if (focusInfo && focusInfo.uuid === this.uuid) {
			var pos = focusInfo.lastPos!,
				zinp = zk(input[focusInfo.inpIdx!]);
			zinp.focus();
			zinp.setSelectionRange(pos[0], pos[1]);
			zul.mesh.Paging._autoFocusInfo = null;
		}

		//remove second id
		if (this.isBothPaging())
			jq(this.parent!.$n_('pgib')).find('.' + this.$s())[0].id = '';
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var uuid = this.uuid;
		if (this.getMold() == 'os') {
			var btns = jq.$$(uuid, 'button')!;
			for (var j = btns.length; j--;)
				jq(btns[j]).off('click');
		} else {
			var input = jq.$$(uuid, 'real')!,
				postfix = ['first', 'prev', 'last', 'next'] as const;
			type ProxyMethodName = `_dom${typeof postfix[number]}Click`;

			for (var i = input.length; i--;)
				jq(input[i])
					.off('keydown', this.proxy(this._domKeyDown))
					.off('blur', this.proxy(this._domBlur));

			for (var k = postfix.length; k--;) {
				var btn = jq.$$(uuid, postfix[k])!;
				for (j = btn.length; j--;)
					jq(btn[j]).off('click', this.proxy(this[('_dom' + postfix[k] + 'Click') as ProxyMethodName]));
			}
		}
		zWatch.unlisten({onSize: this});
		super.unbind_(skipper, after, keepRod);
	}

	private _domKeyDown(evt: JQuery.KeyDownEvent<unknown, unknown, unknown, HTMLInputElement>): void {
		var inp = evt.target,
			wgt = this;
		if (inp.disabled || inp.readOnly)
			return;

		var code = evt.keyCode;
		switch (code) {
		case 48: case 96://0
		case 49: case 97://1
		case 50: case 98://2
		case 51: case 99://3
		case 52: case 100://4
		case 53: case 101://5
		case 54: case 102://6
		case 55: case 103://7
		case 56: case 104://8
		case 57: case 105://9
			break;
		case 37://left
			break;
		case 38: //up
			Paging._increase(inp, wgt, 1);
			evt.stop();
			break;
		case 39://right
			break;
		case 40: //down
			Paging._increase(inp, wgt, -1);
			evt.stop();
			break;
		case 33: // PageUp
			Paging._increase(inp, wgt, -1);
			Paging.go(wgt, +inp.value - 1, inp);
			evt.stop();
			break;
		case 34: // PageDown
			Paging._increase(inp, wgt, +1);
			Paging.go(wgt, +inp.value - 1, inp);
			evt.stop();
			break;
		case 36://home
			Paging.go(wgt, 0, inp);
			evt.stop();
			break;
		case 35://end
			Paging.go(wgt, wgt.getPageCount() - 1, inp);
			evt.stop();
			break;
		case 9: case 8: case 46: //tab, backspace, delete
			break;
		case 13: //enter
			Paging._increase(inp, wgt, 0);
			Paging.go(wgt, +inp.value - 1, inp);
			evt.stop();
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
					&& !evt.ctrlKey && !evt.altKey)
				evt.stop();
		}
	}

	private _domBlur(evt: JQuery.BlurEvent<unknown, unknown, unknown, HTMLInputElement>): void {
		var inp = evt.target,
			wgt = this;
		if (inp.disabled || inp.readOnly)
			return;

		Paging._increase(inp, wgt, 0);
		Paging.go(wgt, +inp.value - 1);
		evt.stop();
	}

	private _domfirstClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		var wgt = this;
		if (wgt.isDisabled()) return;
		var uuid = wgt.uuid,
			postfix = ['first', 'prev'];

		if (wgt.getActivePage() != 0) {
			Paging.go(wgt, 0);
			for (var k = postfix.length; k--;)
				for (var btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
		Paging._callWgtDoAfterGo(wgt, evt.currentTarget, 'first');
	}

	private _domprevClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		var wgt = this;
		if (wgt.isDisabled()) return;
		var uuid = wgt.uuid,
			ap = wgt.getActivePage(),
			postfix = ['first', 'prev'];

		if (ap > 0) {
			Paging.go(wgt, ap - 1);
			if (ap - 1 == 0) {
				for (var k = postfix.length; k--;)
					for (var btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
		Paging._callWgtDoAfterGo(wgt, evt.currentTarget, 'prev');
	}

	private _domnextClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		var wgt = this;
		if (wgt.isDisabled()) return;
		var uuid = wgt.uuid,
			ap = wgt.getActivePage(),
			pc = wgt.getPageCount(),
			postfix = ['last', 'next'];

		if (ap < pc - 1) {
			Paging.go(wgt, ap + 1);
			if (ap + 1 == pc - 1) {
				for (var k = postfix.length; k--;)
					for (var btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
		Paging._callWgtDoAfterGo(wgt, evt.currentTarget, 'next');
	}

	private _domlastClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		var wgt = this;
		if (wgt._disabled) return;
		var uuid = wgt.uuid,
			pc = wgt.getPageCount(),
			postfix = ['last', 'next'];

		if (wgt.getActivePage() < pc - 1) {
			Paging.go(wgt, pc - 1);
			for (var k = postfix.length; k--;)
				for (var btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
		Paging._callWgtDoAfterGo(wgt, evt.currentTarget, 'last');
	}

	public override onSize(): void {
		if (this.desktop) {
			// There are two nodes if using pagingPosition="both"
			var nodes = jq.$$(this.uuid)!;
			if (nodes.length > 0) {
				var node = nodes[0],
					navWidth = Paging._getNavWidth(node, this),
					tolerant = 50,
					isWide = jq(node).width()! > navWidth + tolerant,
					wideChanged = this._lastIsWide != isWide;
				if (wideChanged)
					this._lastIsWide = this._showFirstLast = isWide;
				for (var i = 0; i < nodes.length; i++)
					Paging._fixControl(nodes[i], this, wideChanged);
			}
		}
	}

	private _doAfterGo(...rest: unknown[]): false { // previously, zk.$void
		return false;
	}

	//static
	/**
	 * Goes to the active page according to the page number.
	 * @param DOMElement anc the anchor of the page number
	 * @param int pagenumber the page number
	 */
	public static go(anc: Paging | HTMLAnchorElement, pgno: number, inp?: HTMLInputElement): void {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$<Paging>(anc);
		if (wgt && wgt.getActivePage() != pgno) {
			if (inp) {
				var uuid = wgt.uuid,
					focusInfo: PagingFocusInfo = zul.mesh.Paging._autoFocusInfo = {uuid: uuid};
				focusInfo.lastPos = zk(inp).getSelectionRange();
				// concern about _pagingPosition equals "both"
				jq(jq.$$(uuid, 'real')!).each(function (idx) {
					if (this == inp) {
						focusInfo.inpIdx = idx;
						return false;
					}
				});
			} else if (wgt.getMold() == 'os') {
				wgt._doAfterGo((anc as HTMLAnchorElement).text);
			}
			wgt.fire('onPaging', pgno);

			// update activePage at client for Zephyr
			wgt.setActivePage(pgno);
		}
	}

	private static _increase(inp: HTMLInputElement, wgt: Paging, add: number): void {
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1)
			value = 1;
		else if (value > wgt.getPageCount())
			value = wgt.getPageCount();
		inp.value = value as unknown as string;
	}

	private static _fixControl(node: Node, wgt: Paging, wideChanged: boolean): void {
		var control = jq('> ul', node),
			info = jq('> .z-paging-info', node),
			mold = wgt.getMold(),
			showFirstLast = wgt._showFirstLast;

		if (wideChanged) {
			// in mode=os, developer sets pageIncrement smaller manually
			if (mold == 'default') {
				var navs = control.find('li');
				navs.first().toggle(showFirstLast);
				navs.last().toggle(showFirstLast);
			}
		}
		info.css('visibility', function () {
			return zk(control).isOverlapped(this, 1) ? 'hidden' : '';
		});
	}

	private static _getNavWidth(node: Node, wgt: zk.Widget): number {
		if (wgt._navWidth)
			return wgt._navWidth;

		var navWidth = 0;
		jq('ul > li', node).each(function () {
			navWidth += jq(this).outerWidth(true)!;
		});
		wgt._navWidth = navWidth;
		return navWidth;
	}

	private static _callWgtDoAfterGo(wgt: Paging, btn: HTMLElement, postfix: string): void {
		var btnIdx = 0;
		jq(jq.$$(wgt.uuid, postfix)!).each(function (idx) {
			if (this == btn) {
				btnIdx = idx;
				return false;
			}
		});
		wgt._doAfterGo(postfix, btnIdx);
	}
}
zul.mesh.Paging = zk.regClass(Paging);