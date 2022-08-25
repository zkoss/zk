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
@zk.WrapClass('zul.mesh.Paging')
export class Paging extends zul.Widget {
	override parent?: zul.mesh.MeshWidget;
	_pageSize = 20;
	_totalSize = 0;
	_pageCount = 1;
	_activePage = 0;
	_pageIncrement = zk.mobile ? 5 : 10;
	_showFirstLast = true;
	_detailed = false;
	_autohide?: boolean;
	_disabled?: boolean;
	_meshWidget?: zul.mesh.MeshWidget;
	static _autoFocusInfo?: PagingFocusInfo;
	_lastIsWide?: boolean;
	_navWidth?: number;

	/** Sets the total number of items.
	 * @param int totalSize
	 */
	setTotalSize(totalSize: number, opts?: Record<string, boolean>): this {
		const o = this._totalSize;
		this._totalSize = totalSize;

		if (o !== totalSize || opts?.force) {
			this._updatePageNum();
			if (this._detailed) {
				if (!_rerenderIfBothPaging(this)) {
					const info = this.$n('info');
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
	getTotalSize(): number {
		return this._totalSize;
	}

	/** Returns the number of page anchors shall appear at the client.
	 *
	 * <p>Default: 10.
	 * @return int
	 */
	getPageIncrement(): number {
		return this._pageIncrement;
	}

	/** Sets the number of page anchors shall appear at the client.
	 * @param int pageIncrement
	 */
	setPageIncrement(pageIncrement: number, opts?: Record<string, boolean>): this {
		const o = this._pageIncrement;
		this._pageIncrement = pageIncrement;

		if (o !== pageIncrement || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether to show the detailed info, such as {@link #getTotalSize}.
	 * @return boolean
	 */
	isDetailed(): boolean {
		return this._detailed;
	}

	/** Sets whether to show the detailed info, such as {@link #getTotalSize}.
	 * @param boolean detailed
	 */
	setDetailed(detailed: boolean, opts?: Record<string, boolean>): this {
		const o = this._detailed;
		this._detailed = detailed;

		if (o !== detailed || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @return int
	 */
	getPageCount(): number {
		return this._pageCount;
	}

	/** Sets the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @param int pageCount
	 */
	setPageCount(pageCount: number, opts?: Record<string, boolean>): this {
		const o = this._pageCount;
		this._pageCount = pageCount;

		if (o !== pageCount || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns the active page (starting from 0).
	 * @return int
	 */
	getActivePage(): number {
		return this._activePage;
	}

	/** Sets the active page (starting from 0).
	 * @param int activePage
	 */
	setActivePage(activePage: number, opts?: Record<string, boolean>): this {
		const o = this._activePage;
		this._activePage = activePage;

		if (o !== activePage || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns the page size, aka., the number rows per page.
	 * @return int
	 */
	getPageSize(): number {
		return this._pageSize;
	}

	/** Sets the page size, aka., the number rows per page.
	 * @param int pageSize
	 */
	setPageSize(pageSize: number, opts?: Record<string, boolean>): this {
		const o = this._pageSize;
		this._pageSize = pageSize;

		if (o !== pageSize || opts?.force) {
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
	isAutohide(): boolean {
		return !!this._autohide;
	}

	/**
	 * Sets whether to automatically hide this component if there is only one
	 * page available.
	 * @param boolean autohide
	 */
	setAutohide(autohide: boolean, opts?: Record<string, boolean>): this {
		const o = this._autohide;
		this._autohide = autohide;

		if (o !== autohide || opts?.force) {
			if (this.getPageCount() == 1) this.rerender();
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 * @since 8.0.3
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 * @since 8.0.3
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			if (this.desktop) this._drawDisabled(disabled);
		}

		return this;
	}

	/** Returns the associated {@link zul.mesh.MeshWidget} if any.
	 * @return zul.mesh.MeshWidget
	 * @since 10.0.0
	 */
	getMeshWidget(): zul.mesh.MeshWidget | undefined {
		return this._meshWidget;
	}

	/** Sets the associated {@link zul.mesh.MeshWidget}.
	 * @param zul.mesh.MeshWidget meshWidget
	 * @since 10.0.0
	 */
	setMeshWidget(meshWidget?: zul.mesh.MeshWidget): this {
		this._meshWidget = meshWidget;
		return this;
	}

	override setStyle(style: string): this {
		super.setStyle(style);
		_rerenderIfBothPaging(this);
		return this;
	}

	override setSclass(sclass: string): this {
		super.setSclass(sclass);
		_rerenderIfBothPaging(this);
		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		_rerenderIfBothPaging(this);
		if (this.desktop)
			zUtl.fireSized(this, -1);
		return this;
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		_rerenderIfBothPaging(this);
		return this;
	}

	override setLeft(left: string): this {
		super.setLeft(left);
		_rerenderIfBothPaging(this);
		return this;
	}

	override setTop(top: string): this {
		super.setTop(top);
		_rerenderIfBothPaging(this);
		return this;
	}

	override setTooltiptext(tooltiptext: string): this {
		super.setTooltiptext(tooltiptext);
		_rerenderIfBothPaging(this);
		return this;
	}

	override replaceHTML(n: HTMLElement | string, desktop: zk.Desktop | undefined, skipper?: zk.Skipper, _trim_?: boolean, _callback_?: CallableFunction[]): void {
		if (!_rerenderIfBothPaging(this))
			super.replaceHTML(n, desktop, skipper, _trim_, _callback_);
	}

	/**
	 * Returns whether the paging is in both mold. i.e. Top and Bottom
	 * @return boolean
	 */
	isBothPaging(): boolean {
		return !!this.parent && this.parent.getPagingPosition
			&& 'both' == this.parent.getPagingPosition();
	}

	_drawDisabled(disabled: boolean): void {
		const uuid = this.uuid,
			ap = this.getActivePage(),
			pc = this.getPageCount(),
			input = jq.$$(uuid, 'real')!;
		if ('os' == this.getMold()) {
			const btns = jq.$$(uuid, 'button')!;
			jq(btns).attr('disabled', disabled);
		} else {
			const postfix = (ap == 0 && ap == pc - 1) ? [] : ap == 0 ? ['last', 'next'] : ap == pc - 1 ? ['first', 'prev'] : ['first', 'prev', 'last', 'next'];
			for (let j = input.length; j--;) {
				jq(input[j]).attr('disabled', disabled);
				for (let k = postfix.length; k--;)
					for (let btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', disabled);
				const fnm = disabled ? 'addClass' : 'removeClass',
					pgtext = input[j].nextSibling;
				jq(pgtext!)[fnm](this.$s('text-disabled'));
			}
		}
	}

	_updatePageNum(): void {
		let pageCount = Math.floor((this.getTotalSize() - 1) / this._pageSize + 1);
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
						// Bug ZK-2624
						setTimeout(() => {
							if (this.desktop) {
								const n = this.parent?.$n();

								// reset and recalculate
								if (n?._lastsz) {
									n._lastsz = undefined;
									this.parent!.onSize();
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
	infoText_(): string {
		const acp = this.getActivePage(),
			psz = this.getPageSize(),
			tsz = this.getTotalSize(),
			lastItem = (acp + 1) * psz;
		let dash = '';

		if ('os' != this.getMold())
			dash = ` - ${Math.min(lastItem, tsz)}`;

		return `[ ${acp * psz + 1}${dash} / ${tsz} ]`;
	}

	_infoTags(out: string[]): void {
		if (this.getTotalSize() == 0)
			return;
		const uuid = this.uuid,
			nameOrId = _rerenderIfBothPaging(this) ? 'name' : 'id'; // Bug ZK-2280
		out.push('<div ', nameOrId, '="', uuid, '-detail" class="', this.$s('info'), '"><span ',
			nameOrId, '="', uuid, '-info" aria-hidden="true">', this.infoText_(), '</span></div>');
	}

	_innerTags(): string {
		const out = new zk.Buffer(),
			pinc = this.getPageIncrement(),
			pcount = this.getPageCount(),
			acp = this.getActivePage(),
			half = Math.round(pinc / 2);
		let begin: number,
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

		const bNext = acp < pcount - 1;
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

	appendAnchor(out: string[], label: string | number, val: number, seld?: boolean): void {
		let cls = this.$s('button'),
			navCls = '';

		if (!_isUnsignedInteger(label)) {
			cls += ' ' + this.$s('noborder');
			navCls = ' class="' + this.$s('navigate') + '"';
		}
		if (seld)
			cls += ' ' + this.$s('selected');

		out.push('<li', navCls, '><a name="', this.uuid, '-button" class="', cls,
			'" href="javascript:;" data-paging="', val as unknown as string, '"', seld ? ' aria-current="page"' : '', '>', label as string, '</a></li>');
	}

	override domClass_(no?: zk.DomClassOptions): string {
		const cls = super.domClass_(no),
			added = 'os' == this.getMold() ? ' ' + this.$s('os') : '';
		return cls + added;
	}

	override isVisible(strict?: boolean): boolean {
		const visible = super.isVisible(strict);
		return visible && (this.getPageCount() > 1 || !this._autohide);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({ onSize: this });
		const uuid = this.uuid,
			input = jq.$$(uuid, 'real')!,
			pcount = this.getPageCount(),
			acp = this.getActivePage(),
			postfix = ['first', 'prev', 'last', 'next'] as const,
			focusInfo = zul.mesh.Paging._autoFocusInfo;

		if (!this.$weave)
			for (let i = input.length; i--;)
				jq(input[i]).on('keydown', this.proxy(this._domKeyDown)).on('blur', this.proxy(this._domBlur));

		for (let k = postfix.length; k--;) {
			const btn = jq.$$(uuid, postfix[k])!;
			for (let j = btn.length; j--;) {
				if (!this.$weave)
					jq(btn[j]).on('click', this.proxy(this[`_dom${postfix[k]}Click`]));

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
			const btns = jq.$$(uuid, 'button')!;
			for (let j = btns.length; j--;) {
				// eslint-disable-next-line @typescript-eslint/no-this-alias
				const self = this;
				jq(btns[j]).on('click', function (this: HTMLElement) {
					if (self.isDisabled()) return;
					Paging.go(self, parseInt(jq(this).attr('data-paging')!));
				});
			}
		}

		if (this.isDisabled()) this._drawDisabled(true);

		if (focusInfo && focusInfo.uuid === this.uuid) {
			const pos = focusInfo.lastPos!,
				zinp = zk(input[focusInfo.inpIdx!]);
			zinp.focus();
			zinp.setSelectionRange(pos[0], pos[1]);
			zul.mesh.Paging._autoFocusInfo = undefined;
		}

		//remove second id
		if (this.isBothPaging())
			jq(this.parent!.$n_('pgib')).find('.' + this.$s())[0].id = '';
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		const uuid = this.uuid;
		if (this.getMold() == 'os') {
			const btns = jq.$$(uuid, 'button')!;
			for (let j = btns.length; j--;)
				jq(btns[j]).off('click');
		} else {
			const input = jq.$$(uuid, 'real')!,
				postfix = ['first', 'prev', 'last', 'next'] as const;

			for (let i = input.length; i--;)
				jq(input[i])
					.off('keydown', this.proxy(this._domKeyDown))
					.off('blur', this.proxy(this._domBlur));

			for (let k = postfix.length; k--;) {
				const btn = jq.$$(uuid, postfix[k])!;
				for (let j = btn.length; j--;)
					jq(btn[j]).off('click', this.proxy(this[`_dom${postfix[k]}Click`]));
			}
		}
		zWatch.unlisten({ onSize: this });
		super.unbind_(skipper, after, keepRod);
	}

	_domKeyDown(evt: JQuery.KeyDownEvent<unknown, unknown, unknown, HTMLInputElement>): void {
		const inp = evt.target;
		if (inp.disabled || inp.readOnly)
			return;

		const code = evt.keyCode;
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
				Paging._increase(inp, this, 1);
				evt.stop();
				break;
			case 39://right
				break;
			case 40: //down
				Paging._increase(inp, this, -1);
				evt.stop();
				break;
			case 33: // PageUp
				Paging._increase(inp, this, -1);
				Paging.go(this, +inp.value - 1, inp);
				evt.stop();
				break;
			case 34: // PageDown
				Paging._increase(inp, this, +1);
				Paging.go(this, +inp.value - 1, inp);
				evt.stop();
				break;
			case 36://home
				Paging.go(this, 0, inp);
				evt.stop();
				break;
			case 35://end
				Paging.go(this, this.getPageCount() - 1, inp);
				evt.stop();
				break;
			case 9: case 8: case 46: //tab, backspace, delete
				break;
			case 13: //enter
				Paging._increase(inp, this, 0);
				Paging.go(this, +inp.value - 1, inp);
				evt.stop();
				break;
			default:
				if (!(code >= 112 && code <= 123) //F1-F12
					&& !evt.ctrlKey && !evt.altKey)
					evt.stop();
		}
	}

	_domBlur(evt: JQuery.BlurEvent<unknown, unknown, unknown, HTMLInputElement>): void {
		const inp = evt.target;
		if (inp.disabled || inp.readOnly)
			return;

		Paging._increase(inp, this, 0);
		Paging.go(this, +inp.value - 1);
		evt.stop();
	}

	_domfirstClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		if (this.isDisabled()) return;
		const uuid = this.uuid,
			postfix = ['first', 'prev'];

		if (this.getActivePage() != 0) {
			Paging.go(this, 0);
			for (let k = postfix.length; k--;)
				for (let btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
		Paging._callWgtDoAfterGo(this, evt.currentTarget, 'first');
	}

	_domprevClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		if (this.isDisabled()) return;
		const uuid = this.uuid,
			ap = this.getActivePage(),
			postfix = ['first', 'prev'];

		if (ap > 0) {
			Paging.go(this, ap - 1);
			if (ap - 1 == 0) {
				for (let k = postfix.length; k--;)
					for (let btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
		Paging._callWgtDoAfterGo(this, evt.currentTarget, 'prev');
	}

	_domnextClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		if (this.isDisabled()) return;
		const uuid = this.uuid,
			ap = this.getActivePage(),
			pc = this.getPageCount(),
			postfix = ['last', 'next'];

		if (ap < pc - 1) {
			Paging.go(this, ap + 1);
			if (ap + 1 == pc - 1) {
				for (let k = postfix.length; k--;)
					for (let btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
		Paging._callWgtDoAfterGo(this, evt.currentTarget, 'next');
	}

	_domlastClick(evt: JQuery.ClickEvent<unknown, unknown, HTMLElement>): void {
		if (this._disabled) return;
		const uuid = this.uuid,
			pc = this.getPageCount(),
			postfix = ['last', 'next'];

		if (this.getActivePage() < pc - 1) {
			Paging.go(this, pc - 1);
			for (let k = postfix.length; k--;)
				for (let btn = jq.$$(uuid, postfix[k])!, i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
		Paging._callWgtDoAfterGo(this, evt.currentTarget, 'last');
	}

	override onSize(): void {
		if (this.desktop) {
			// There are two nodes if using pagingPosition="both"
			const nodes = jq.$$(this.uuid)!;
			if (nodes.length > 0) {
				const node = nodes[0],
					navWidth = Paging._getNavWidth(node, this),
					tolerant = 50,
					isWide = jq(node).width()! > navWidth + tolerant,
					wideChanged = this._lastIsWide != isWide;
				if (wideChanged)
					this._lastIsWide = this._showFirstLast = isWide;
				// eslint-disable-next-line @typescript-eslint/prefer-for-of
				for (let i = 0; i < nodes.length; i++)
					Paging._fixControl(nodes[i], this, wideChanged);
			}
		}
	}

	_doAfterGo(postfix: string, btnIndex?: number): void {
		// This function shoudn't do anything.
	}

	//static
	/**
	 * Goes to the active page according to the page number.
	 * @param DOMElement anc the anchor of the page number
	 * @param int pagenumber the page number
	 */
	static go(anc: Paging | HTMLAnchorElement, pgno: number, inp?: HTMLInputElement): void {
		const wgt = anc instanceof zk.Widget ? anc : zk.Widget.$<Paging>(anc);
		if (wgt && wgt.getActivePage() != pgno) {
			if (inp) {
				const uuid = wgt.uuid,
					focusInfo: PagingFocusInfo = zul.mesh.Paging._autoFocusInfo = { uuid: uuid };
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

	static _increase(inp: HTMLInputElement, wgt: Paging, add: number): void {
		let value = zk.parseInt(inp.value);
		value += add;
		if (value < 1)
			value = 1;
		else if (value > wgt.getPageCount())
			value = wgt.getPageCount();
		inp.value = String(value);
	}

	static _fixControl(node: Node, wgt: Paging, wideChanged: boolean): void {
		const control = jq('> ul', node),
			info = jq('> .z-paging-info', node),
			mold = wgt.getMold(),
			showFirstLast = wgt._showFirstLast;

		if (wideChanged) {
			// in mode=os, developer sets pageIncrement smaller manually
			if (mold == 'default') {
				const navs = control.find('li');
				navs.first().toggle(showFirstLast);
				navs.last().toggle(showFirstLast);
			}
		}
		info.css('visibility', function () {
			return zk(control).isOverlapped(this, 1) ? 'hidden' : '';
		});
	}

	static _getNavWidth(node: Node, wgt: zul.mesh.Paging): number {
		if (wgt._navWidth)
			return wgt._navWidth;

		let navWidth = 0;
		jq('ul > li', node).each(function () {
			navWidth += jq(this).outerWidth(true)!;
		});
		wgt._navWidth = navWidth;
		return navWidth;
	}

	static _callWgtDoAfterGo(wgt: Paging, btn: HTMLElement, postfix: string): void {
		let btnIdx = 0;
		jq(jq.$$(wgt.uuid, postfix)!).each(function (idx) {
			if (this == btn) {
				btnIdx = idx;
				return false;
			}
		});
		wgt._doAfterGo(postfix, btnIdx);
	}
}