/* Menubar.ts

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:32     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The menu related widgets, such as menubar and menuitem.
 */
//zk.$package('zul.menu');
function _closeOnOut(menubar: zul.menu.Menubar): void {
	//1) _noFloatUp: Bug 1852304: Safari/Chrome unable to popup with menuitem
	//   because popup causes mouseout, and mouseout casues onfloatup
	//2) _bOver: indicates whether it is over some part of menubar
	//3) Test also Bug 3052208
	if (!menubar._noFloatUp && !menubar._bOver && zul.menu._nOpen)
		zWatch.fire('onFloatUp', menubar); //notify all
}

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getZclass}: z-menubar
 */
@zk.WrapClass('zul.menu.Menubar')
export class Menubar extends zul.Widget {
	override firstChild!: zul.menu.Menu | undefined;
	override lastChild!: zul.menu.Menu | undefined;
	_orient = 'horizontal';
	_bodyScrollLeft = 0;
	_lastTarget?: zul.menu.Menu;
	_noFloatUp?: boolean;
	_bOver?: boolean;
	_autodrop?: boolean;
	_scrollable: boolean | undefined; // eslint-disable-line zk/preferStrictBooleanType
	_scrolling?: boolean;
	_runId?: number;

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	/** Sets the orient.
	 * @param String orient either horizontal or vertical
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether the menubar scrolling is enabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	isScrollable(): boolean {
		return !!this._scrollable;
	}

	/** Sets whether to enable the menubar scrolling
	 * @param boolean scrollable
	 */
	setScrollable(scrollable: boolean, opts?: Record<string, boolean>): this {
		const o = this._scrollable;
		this._scrollable = scrollable;

		if (o !== scrollable || opts?.force) {
			if (this.checkScrollable())
				this.rerender();
		}

		return this;
	}

	/** Returns whether to automatically drop down menus if user moves mouse
	 * over it.
	 * <p>Default: false.
	 * @return boolean
	 */
	isAutodrop(): boolean {
		return !!this._autodrop;
	}

	/** Sets whether to automatically drop down menus if user moves mouse
	 * over it.
	 * @param boolean autodrop
	 */
	setAutodrop(autodrop: boolean): this {
		this._autodrop = autodrop;
		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		this._checkScrolling();
		return this;
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this.isVertical() ? 'vertical' : 'horizontal');
		}
		return sc;
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domUnlisten_(left, 'onClick', '_doScroll')
					.domUnlisten_(right, 'onClick', '_doScroll');
			}
			zWatch.unlisten({ onSize: this });
		}
		var n = this.$n_();
		n.removeEventListener('mouseleave', this.proxy(this._doMouseLeave));
		n.removeEventListener('mouseenter', this.proxy(this._doMouseEnter));

		this._lastTarget = undefined;
		super.unbind_(skipper, after, keepRod);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n = this.$n_();
		n.addEventListener('mouseenter', this.proxy(this._doMouseEnter));
		n.addEventListener('mouseleave', this.proxy(this._doMouseLeave));
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domListen_(left, 'onClick', '_doScroll')
					.domListen_(right, 'onClick', '_doScroll');
			}
			zWatch.listen({ onSize: this });
		}
	}

	/** Returns whether the menubar scrolling is enabled in horizontal orient.
	 * @return boolean
	 */
	checkScrollable(): boolean {
		return !!this._scrollable && !this.isVertical();
	}

	override onSize(): void {
		this._checkScrolling();
	}

	override onChildAdded_(child: zul.menu.Menu): void {
		super.onChildAdded_(child);
		this._checkScrolling();
	}

	override onChildRemoved_(child: zul.menu.Menu): void {
		super.onChildRemoved_(child);
		if (!this.childReplacing_)
			this._checkScrolling();
	}

	_checkScrolling(): void {
		if (!this.checkScrollable()) return;

		var node = this.$n();
		if (!node) return;
		jq(node).addClass(this.$s('scroll'));

		var nodeWidth = zk(node).contentWidth(),
			body = this.$n_('body'),
			children = jq(this.$n_('cave')).children().filter(':visible'),
			childrenLen = children.length,
			totalWidth = 0;

		for (var i = childrenLen; i--;)
			totalWidth += jq(children[i]).outerWidth(true)!; //ZK-3095

		if (zk.ie) // child width (text node) is not integer in IE
			totalWidth += childrenLen;

		if (totalWidth >= nodeWidth)
			this._scrolling = true;
		else {
			this._scrolling = false;
			this._fixBodyScrollLeft(0);
			//ZK-3094: Scrollable menubar body is not properly resized after container resizing.
			body.style.width = '';
		}
		this._fixButtonPos(node);

		var fixedSize = nodeWidth - zk(this.$n('left')).offsetWidth() - zk(this.$n('right')).offsetWidth();
		if (this._scrolling) {
			body.style.width = jq.px0(fixedSize);
			this._fixScrollPos(children.last()[0]);
		}
	}

	_fixScrollPos(lastChild?: HTMLElement): void {
		if (lastChild) {
			var offsetLeft = lastChild.offsetLeft;
			if (offsetLeft < this._bodyScrollLeft) {
				this._fixBodyScrollLeft(offsetLeft);
			}
		}
	}

	_fixButtonPos(node: HTMLElement): void {
		var body = this.$n_('body'),
			left = this.$n_('left'),
			right = this.$n_('right'),
			css = this._scrolling ? 'addClass' as const : 'removeClass' as const;

		left.style.display = right.style.display = this._scrolling ? 'block' : 'none';
		jq(node)[css](this.$s('scroll'));
		body.style.marginLeft = this._scrolling ? jq.px(left.offsetWidth) : '0';
		body.style.marginRight = this._scrolling ? jq.px(right.offsetWidth) : '0';
	}

	_forceStyle(node: HTMLElement, value: string): void {
		if (zk.parseInt(value) < 0)
			return;
		node.style.width = value;
	}

	_doMouseEnter(evt: MouseEvent): void {
		this._bOver = true;
		this._noFloatUp = false;
	}

	_doMouseLeave(evt: MouseEvent): void {
		this._bOver = false;
		this._closeOnOut();
	}

	_doScroll(evt: zk.Event): void {
		this._scroll(evt.domTarget == this.$n('left') || evt.domTarget.parentNode == this.$n('left') ? 'left' : 'right');
	}

	_fixBodyScrollLeft(scrollLeft: number): void {
		this.$n_('body').scrollLeft = this._bodyScrollLeft = scrollLeft;
	}

	_scroll(direction: string): void {
		if (!this.checkScrollable() || this._runId) return;
		var body = this.$n_('body'),
			currScrollLeft = this._bodyScrollLeft,
			children = jq(this.$n_('cave')).children().filter(':visible'),
			childrenLen = children.length,
			movePos = 0;

		if (!childrenLen) return;
		switch (direction) {
			case 'left':
				for (var i = 0; i < childrenLen; i++) {
					// B50-ZK-381: Menu scrolling bug
					// child width may be larger than body.offsetWidth
					if (children[i].offsetLeft >= currScrollLeft
						|| children[i].offsetLeft + (children[i].offsetWidth - body.offsetWidth) >= currScrollLeft) {
						var preChild = children[i].previousSibling;
						if (!preChild) return;
						movePos = (preChild as HTMLElement).offsetLeft;
						if (isNaN(movePos)) return;
						this._runId = setInterval(() => {
							if (!this._moveTo(body, movePos)) {
								this._afterMove();
							}
						}, 10);
						return;
					}
				}
				break;
			case 'right':
				var currRight = currScrollLeft + body.offsetWidth;
				for (var i = 0; i < childrenLen; i++) {
					var currChildRight = children[i].offsetLeft + children[i].offsetWidth;
					if (currChildRight > currRight) {
						movePos = currScrollLeft + (currChildRight - currRight);
						if (isNaN(movePos)) return;
						this._runId = setInterval(() => {
							if (!this._moveTo(body, movePos)) {
								this._afterMove();
							}
						}, 10);
						return;
					}
				}
				break;
		}
	}

	_moveTo(body: HTMLElement, moveDest: number): boolean {
		var currPos = this._bodyScrollLeft;
		if (currPos == moveDest)
			return false;

		var step = 5,
			delta = currPos > moveDest ? -step : step,
			setTo = currPos + delta;
		if ((setTo < moveDest && delta < 0) || (setTo > moveDest && delta > 0))
			setTo = moveDest;

		this._fixBodyScrollLeft(setTo);
		return true;
	}

	_afterMove(): void {
		clearInterval(this._runId);
		this._runId = undefined;
	}

	override insertChildHTML_(child: zul.menu.Menu, before?: zk.Widget, desktop?: zk.Desktop): void {
		var vert = this.isVertical();
		if (before)
			jq(before.$n('chdextr') ?? before.$n_()).before(
				this.encloseChildHTML_({ child: child, vertical: vert })!);
		else
			jq(this.$n_('cave')).append(
				this.encloseChildHTML_({ child: child, vertical: vert })!);

		child.bind(desktop);
	}

	override removeChildHTML_(child: zul.menu.Menu, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		jq(child.$n_('chdextr')).remove();
	}

	encloseChildHTML_(opts: { child: zk.Widget; vertical: boolean; out?: string[] }): string | undefined {
		var out = opts.out ?? new zk.Buffer(),
			child = opts.child;
		child.redraw(out);
		if (!opts.out) return out.join('');
	}

	//Closes all menupopup when mouse is moved out
	_closeOnOut(): void {
		if (this._autodrop && !zul.Widget.getOpenTooltip()) //dirty fix: don't auto close if tooltip shown
			setTimeout(() => _closeOnOut(this), 200);
	}

	/**
	 * Returns whether it is a vertical menubar.
	 * @return boolean
	 * @since 9.5.0
	 */
	isVertical(): boolean {
		return 'vertical' == this.getOrient();
	}

	override doKeyDown_(evt: zk.Event): void {
		var direction = 0,
			isVertical = this.isVertical(),
			currentTarget = evt.target;
		switch (evt.key) {
			case 'ArrowLeft':
				if (!isVertical) direction = -1;
				break;
			case 'ArrowUp':
				if (isVertical) direction = -1;
				break;
			case 'ArrowRight':
				if (!isVertical) direction = 1;
				break;
			case 'ArrowDown':
				if (isVertical) direction = 1;
				break;
		}
		if (direction && currentTarget) {
			var target = direction < 0
				? this._getPrevVisibleMenuTarget(currentTarget)
				: this._getNextVisibleMenuTarget(currentTarget);
			if (target)
				target.focus();
			evt.stop();
		}
		super.doKeyDown_(evt);
	}

	_getPrevVisibleMenuTarget(currentTarget: zk.Widget): zk.Widget | undefined {
		var prev = currentTarget.previousSibling;
		if (!prev) {
			prev = this.lastChild;
		}
		return prev ? this._prevVisibleMenu(prev) : undefined;
	}

	_getNextVisibleMenuTarget(currentTarget: zk.Widget): zk.Widget | undefined {
		var next = currentTarget.nextSibling;
		if (!next) {
			next = this.firstChild;
		}
		return next ? this._nextVisibleMenu(next) : undefined;
	}

	_nextVisibleMenu(menu?: zk.Widget): zk.Widget | undefined {
		for (var m = menu; m; m = m.nextSibling) {
			if (Menubar._isActiveItem(m))
				return m;
		}
		if (this.firstChild == menu)
			return menu;
		return this._nextVisibleMenu(this.firstChild);
	}

	_prevVisibleMenu(menu?: zk.Widget): zk.Widget | undefined {
		for (var m = menu; m; m = m.previousSibling) {
			if ((this.$class as typeof Menubar)._isActiveItem(m))
				return m;
		}
		if (this.lastChild == menu)
			return menu;
		return this._prevVisibleMenu(this.lastChild);
	}

	static _isActiveItem(wgt: zk.Widget): boolean {
		return wgt.isVisible()
			&& (wgt instanceof zul.menu.Menu || wgt instanceof zul.menu.Menuitem)
			&& !wgt.isDisabled();
	}
}