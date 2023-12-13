/* Menupopup.ts

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:34     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _getMenu(wgt: zk.Widget): zul.menu.Menu | undefined {
	var p = wgt.parent;
	return p instanceof zul.menu.Menu ? p : undefined;
}
function _getRootMenu(wgt: zul.menu.Menupopup): zul.menu.Menu | undefined {
	for (var w: zk.Widget | undefined = wgt; w && (w = _getMenu(w)); w = w.parent) {
		if ((w as zul.menu.Menu).isTopmost())
			return w as zul.menu.Menu;
	}
	return undefined;
}
//child must be _currentChild()
function _prevChild(popup: zul.menu.Menupopup, child: zk.Widget | undefined): zk.Widget | undefined {
	var $menubar = zul.menu.Menubar;
	if (child)
		while (child = child.previousSibling)
			if ($menubar._isActiveItem(child)) {
				popup._curIndex--;
				return child;
			}

	//return the last
	popup._curIndex = -1;
	for (var w = popup.firstChild; w; w = w.nextSibling)
		if ($menubar._isActiveItem(w)) { //return the first one
			child = w;
			popup._curIndex++;
		}
	return child;
}
//child must be _currentChild()
function _nextChild(popup: zul.menu.Menupopup, child: zk.Widget | undefined): zk.Widget | undefined {
	var $menubar = zul.menu.Menubar;
	if (child)
		while (child = child.nextSibling)
			if ($menubar._isActiveItem(child)) {
				popup._curIndex++;
				return child;
			}

	//return the first
	for (var w = popup.firstChild; w; w = w.nextSibling)
		if ($menubar._isActiveItem(w)) { //return the first one
			popup._curIndex = 0;
			return w;
		}
}
function _indexOfVisibleMenu(popup: zul.menu.Menupopup, child: zk.Widget): number {
	var i = -1,
		$menubar = zul.menu.Menubar;
	for (var c = popup.firstChild; c; c = c.nextSibling) {
		// check active first (child may be inactive)
		if ($menubar._isActiveItem(c)) {
			i++;
		}
		if (c == child)
			return i;
	}
	return i;
}
function _activateNextMenu(menu: zul.menu.Menu): void {
	var pp = menu.menupopup;
	if (pp) {
		pp._shallClose = false;
		if (!pp.isOpen()) {
			menu.focus();
			pp.open(undefined, undefined, undefined, {focusFirst: true, sendOnOpen: true, disableMask: true});
		}
	}
	(menu.$class as typeof zul.menu.Menu)._addActive(menu, 'focus');
	zWatch.fire('onFloatUp', menu); //notify all
}

/**
 * A container used to display menus. It should be placed inside a
 * {@link Menu}.
 *
 * @defaultValue {@link getZclass}: z-menupopup.
 */
@zk.WrapClass('zul.menu.Menupopup')
export class Menupopup extends zul.wgt.Popup {
	override parent!: zul.menu.Menu | undefined;
	override firstChild!: zul.menu.Menuitem | undefined;
	override lastChild!: zul.menu.Menuitem | undefined;
	/** @internal */
	_curIndex = -1;
	/** @internal */
	_keepOpen = false;
	/** @internal */
	_shallClose?: boolean;
	/** @internal */
	_shadow?: zk.eff.Shadow;
	/** @internal */
	_reverseDirection?: boolean;

	override zsync(opts?: zk.Object): void {
		super.zsync(opts);

		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.$n_());
		this._shadow.sync();
	}

	/** @internal */
	_hideShadow(): void {
		if (this._shadow) this._shadow.hide();
	}

	/** @internal */
	_syncPos(): void {
		var menu = _getMenu(this);
		if (menu) {
			var n = this.$n_(),
				m = menu.$n_(),
				$n = jq(n),
				$m = jq(m),
				nol = $n.offset()!.left,
				mol = $m.offset()!.left,
				nwd = $n.outerWidth()!,
				mwd = $m.outerWidth()!,
				mp: zk.Widget | undefined = menu.parent,
				mb = menu.getMenubar(),
				ori = mb ? mb.getOrient() : '',
				calculateToRight = false,
				left = -1;

			// ZK-2356 should sync position after calling super.onResponse()
			if (menu.isTopmost() && ori == 'horizontal' && n)
				n.style.top = jq.px0(zk.parseInt(n.style.top));

			while (mp && !(mp instanceof zul.menu.Menupopup))
				mp = mp.parent;

			// sync menupopup position according to parent menupopup if any,
			// once expected position exceed the border, reverse open direction.
			if (mp) {
				if (mp._reverseDirection) {
					// calculateToLeft
					var expectedLeft = mol - nwd,
						leftBorderExceeded = expectedLeft < 0,
						expectedLeft = leftBorderExceeded ? mol + mwd : expectedLeft,
						expectedRight = expectedLeft + nwd,
						viewportRight = jq.innerX() + jq.innerWidth();
					left = expectedRight > viewportRight ? viewportRight - nwd : expectedLeft;
					this._reverseDirection = !leftBorderExceeded;
				} else {
					calculateToRight = true;
				}
			}

			/* ZK-2040: should sync position
				* when the overlap between menu and menupopup is greater than 5px,
				* only check the overlap situation when parent menupopup does not exist
				*/
			if (!mp && zk(n).isOverlapped(m, 1)
					&& (((mol + mwd - nol > 5) && (ori != 'vertical'))
					|| ((nol < mol + mwd / 2) && (ori == 'vertical')))) {
				calculateToRight = true;
			}

			if (calculateToRight) {
				var expectedLeft = mol + mwd,
					expectedRight = expectedLeft + nwd,
					viewportRight = jq.innerX() + jq.innerWidth(),
					rightBorderExceeded = expectedRight > viewportRight,
					expectedLeft = rightBorderExceeded ? mol - nwd : expectedLeft;
				left = Math.max(expectedLeft, 0);
				this._reverseDirection = rightBorderExceeded;
			}

			if (left >= 0) {
				n.style.left = jq.px0(left);
			}
		}
	}

	override close(opts?: zul.wgt.PopupOptions): void {
		if (this.isOpen())
			zul.menu._nOpen--;

		super.close(opts);
		jq(this.$n_()).hide(); // force to hide the element
		this._hideShadow();
		var menu: zul.menu.Menu | undefined;
		if ((menu = _getMenu(this)) && menu.isTopmost())
			jq(menu.$n_()).removeClass(menu.$s('selected'));

		this.removeActive_();
		(this.$class as typeof Menupopup)._rmActive(this);
	}

	override open(ref?: zul.wgt.Ref, offset?: zk.Offset, position?: string, opts?: zul.wgt.PopupOptions): void {
		if (!this.isOpen())
			zul.menu._nOpen++;
		var menu: zul.menu.Menu | undefined;
		if (menu = _getMenu(this)) {
			if (!offset) {
				ref = menu.getAnchor_();
				if (!position) {
					if (menu.isTopmost())
						position = menu.parent!.getOrient() == 'vertical'
							? 'end_before' : 'after_start';
					else position = 'end_before';
				}
			}
		}
		super.open(ref, offset, position, opts || {sendOnOpen: true, disableMask: true});
			//open will fire onShow which invoke this.zsync()

		this._syncPos(); //ZK-1248: re-sync position if sub-menu is overlapped on parent menu
		// focus on the first menuitem
		if (opts && opts.focusFirst) {
			this.doKeyDown_(new zk.Event(this, 'onKeyDown', {keyCode: 40, key: 'ArrowDown'}));
		}
	}

	/** @internal */
	override shallStackup_(): boolean {
		return false;
	}

	// eslint-disable-next-line zk/javaStyleSetterSignature
	override setTopmost(): number {
		const result = super.setTopmost();
		this.zsync();
		return result;
	}

	override onFloatUp(ctl: zk.ZWatchController, opts: zk.FireOptions): void {
		if (!this.isVisible())
			return;

		var openInfo = this._openInfo;

		// F70-ZK-2049: If popup belongs to widget's ascendant then return.
		if (this._shallToggle && openInfo && opts && (
				opts.triggerByClick === undefined || (
				openInfo[3]!.which == opts.triggerByClick && zUtl.isAncestor(this._openInfo![0] as zk.Widget, ctl.origin)))) {
			return;
		}

		this._doFloatUp(ctl);
	}

	/** @internal */
	override _doFloatUp(ctl: zk.ZWatchController): void {
		if (!this.isVisible())
			return;

		var org = ctl.origin;
		if (this.parent!.menupopup == this && !this.parent!.isTopmost() && !(this.parent!.$class as typeof zul.menu.Menu)._isActive(this.parent!, 'hover')) {
			this.close({sendOnOpen: true});
			return;
		}

		// check if org belongs to the popup
		for (var floatFound = false, wgt: zk.Object | zk.Widget | undefined = org; wgt; wgt = (wgt as zk.Widget).parent) {
			if (wgt == this || ((wgt as zul.menu.Menu).menupopup == this && !this._shallClose)) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			floatFound = floatFound || (wgt as zk.Widget).isFloating_();
		}

		// check if the popup is one of org's children
		if (org instanceof zk.Widget && org.ignoreDescendantFloatUp_(this)) {
			for (var floatFound = false, wgt: zk.Object | zk.Widget | undefined = this; wgt = (wgt as zk.Widget).parent;) {
				if (wgt == org) {
					if (this._shallClose)
						break; //close it
					if (!floatFound)
						this.setTopmost();
					return;
				}
				floatFound = floatFound || (wgt as zk.Widget).isFloating_();
			}

			//No need to check _lastTarget since we have to close any other open menupopup
		}
		this.close({sendOnOpen: true});
	}

	override onShow(): void {
		this.zsync();
		var anc = this.getAnchor_();
		if (anc) {
			if (zk(anc).isRealVisible()) {
				anc.focus();
				zk.currentFocus = this; //Bug 2807475: (IE only) s.t. _docMouseDown will focus later (since menupop becomes invisible)
			}
		}

		zk(this).redoCSS(-1, {'fixFontIcon': true});

	}

	onHide(): void {
		if (this.isOpen())
			this.close();
		this._hideShadow();
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onHide: this, onResponse: this});

		var n = this.$n_();
		n.addEventListener('mouseenter', this.proxy(this._doMouseEnter));
		n.addEventListener('mouseleave', this.proxy(this._doMouseLeave));
		if (!zk.css3) jq.onzsync(this);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this.isOpen())
			this.close();
		if (this._shadow)
			this._shadow.destroy();
		if (!zk.css3) jq.unzsync(this);
		this._shadow = undefined;
		zWatch.unlisten({onHide: this, onResponse: this});

		var n = this.$n_();
		n.removeEventListener('mouseleave', this.proxy(this._doMouseLeave));
		n.removeEventListener('mouseenter', this.proxy(this._doMouseEnter));

		super.unbind_(skipper, after, keepRod);
	}

	override onResponse(): void {
		if (!this.isOpen())
			return; // Bug 2950364
		this.zsync();

		super.onResponse(); //Bug #2870616

		this._syncPos(); // For Bug ZK-2160, resync position again after invoking supers.
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		var w: zk.Widget | undefined = this._currentChild(),
			menu: zul.menu.Menu | undefined,
			keyCode = evt.keyCode;
		switch (keyCode) {
		case 38: //UP
		case 40: //DOWN
			// UP: 1. jump to the previousSibling item
			// DOWN: 1. jump to the nextSibling item
			this.removeAllChildrenActive_();
			w = keyCode == 38 ? _prevChild(this, w) : _nextChild(this, w);
			if (w) {
				(w.$class as typeof zul.menu.Menuitem)._addActive(w as zul.menu.Menuitem, 'focus'); // FIXME: type of w is inconsistent
				w.focus();
			}
			break;
		case 37: //LEFT
			// 1. close the contenthandler (like colorbox), if any
			// 2. close the menupopup and jump to the parent menu
			// 3. if in the topmost menu, then jump to the previous menu
			// 4. close it when not above scenario matched.
			if (w && w instanceof zul.menu.Menu && w._contentHandler && w._contentHandler.isOpen()) {
				w._contentHandler.onHide();
			} else if (((menu = _getMenu(this))) && !menu.isTopmost()) {
				this.close();
				(menu.$class as typeof zul.menu.Menu)._addActive(menu, 'focus');
				const pp = menu.parent as unknown as zul.menu.Menupopup | undefined; // FIXME: type of pp is inconsistent
				if (pp) {
					pp.focus();
					pp._curIndex = _indexOfVisibleMenu(pp, menu);
				}
			} else {
				var root = _getRootMenu(this);
				if (root && (root = root._getPrevVisibleMenu()))
					_activateNextMenu(root);
				else { // the parent is not menu widget
					var ref = this._fakeParent;
					this.close();
					if (ref) ref.focus();
				}
			}
			break;
		case 39: //RIGHT
			// 1. Open the descendant menupopup if any
			// 2. jump to the next topmost menu
			if (w && w instanceof zul.menu.Menu && !w.isDisabled()) {
				w._togglePopup(true);
			} else {
				var root = _getRootMenu(this);
				if (root && (root = root._getNextVisibleMenu()))
					_activateNextMenu(root);
			}
			break;
		case 13: //ENTER
			// 1. fire onClick event when target is Menuitem
			// 2. toggle menupopup when target is Menu
			// 3. fire onClick event if target is Menu and clickable
			if (w && w instanceof zul.menu.Menuitem) {
				//{} for emulate as onClick, escape the checking data == null at serverside
				w.doClick_(new zk.Event(w, 'onClick', {}));
				zWatch.fire('onFloatUp', w); //notify all
				this.close({sendOnOpen: true});
			} else if (w && w instanceof zul.menu.Menu) {
				w._togglePopup(true);
			} else {
				if ((menu = _getMenu(this))) {
					this.close();
					if (menu.isTopmost()) {
						menu.focus();
					} else {
						(menu.$class as typeof zul.menu.Menu)._addActive(menu, 'focus');
						const pp = menu.parent;
						if (pp) {
							pp.focus();
						}
					}
				}
			}
			break;
		case 27: //ESC
			// 1. close the menupopup, if any
			// 2. close the contenthandler, if any
			if ((menu = _getMenu(this))) {
				if (menu.isTopmost()) {
					this.close();
					menu.focus();
				} else {
					var content = menu._contentHandler;
					if (content && content.isOpen()) {
						content.onHide();
					} else {
						this.close();
						(menu.$class as typeof zul.menu.Menu)._addActive(menu, 'focus');
						const pp = menu.parent;
						if (pp) {
							pp.focus();
						}
					}
				}
			} else { // the parent is not menu widget
				var ref = this._fakeParent;
				this.close({sendOnOpen: true});
				if (ref) ref.focus();
			}
			break;
		case 9: // TAB
			// 1. close the menupopup and then it will jump to next menu, if any.
			// Note: don't stop the event.
			var root = _getRootMenu(this);
			if (root) {
				// a trick way to jump to the next menu.
				root.focus_(undefined, false);
			}
			this.close();
			break;
		}
		if (keyCode != 9 && keyCode != 27) // TAB && ESC
			evt.stop(); // Bug ZK-442
		super.doKeyDown_(evt);
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		// Prevent from closing the popup if being triggered by space key.
		// https://bugzilla.mozilla.org/show_bug.cgi?id=1220143
		if (evt.domTarget == this.getAnchor_())
			evt.stop();
		super.doClick_(evt, popupOnly);
	}

	/**
	 * @returns the {@link Menubar} that contains this menuitem, or null if not available.
	 * @since 5.0.5
	 */
	getMenubar(): zul.menu.Menubar | undefined {
		for (var p: zk.Widget | undefined = this.parent; p; p = p!.parent) {
			if (p instanceof zul.menu.Menubar)
				return p;
			if (p instanceof zul.menu.Menu)
				return p.getMenubar();
			break; // not found
		}
		return undefined;
	}

	/** @internal */
	_doMouseEnter(evt: MouseEvent): void {
		var menubar = this.getMenubar();
		if (menubar) menubar._bOver = true;
		this._shallClose = false;
	}

	/** @internal */
	_doMouseLeave(evt: MouseEvent): void {
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._bOver = false;
			if (menubar._autodrop)
				menubar._closeOnOut();
		}
		// Don't remove if current active is menu (with a menupopup)
		if (!(this._currentChild() instanceof zul.menu.Menu))
			this.removeActive_();
	}

	/**
	 * Sets the current active item in this menupopup.
	 * @param childIndex - the index of menupopup children
	 * @since 8.6.0
	 */
	// eslint-disable-next-line zk/javaStyleSetterSignature
	setActive(childIndex: number): this {
		if (childIndex >= 0 && childIndex < this.nChildren) {
			var newCurrIndex = -1,
				$menubar = zul.menu.Menubar;
			for (var w = this.firstChild, i = 0, visibleIndex = 0; w; w = w.nextSibling, i++) {
				var isActive = $menubar._isActiveItem(w);
				if (childIndex === i && isActive) {
					newCurrIndex = visibleIndex;
					break;
				}
				if (isActive) visibleIndex++;
			}
			if (newCurrIndex >= 0) { // The item is eligible to be active
				this.removeActive_();

				this._curIndex = newCurrIndex;
				var target = this.getChildAt<zul.menu.Menuitem>(childIndex);
				if (target) (target.$class as typeof zul.menu.Menuitem)._addActive(target, 'hover');
			}
		}
		return this;
	}

	// internal use only.
	/** @internal */
	getAnchor_(): HTMLAnchorElement | undefined {
		return this.$n('a');
	}

	/** @internal */
	override focus_(timeout?: number): boolean {
		if (zk(this.getAnchor_()).focus(timeout)) {
			return true;
		}
		return super.focus_(timeout);
	}

	/** @internal */
	addActive_(wgt: zk.Widget): void {
		this._curIndex = _indexOfVisibleMenu(this, wgt);
	}

	/** @internal */
	removeActive_(): void {
		const currentActive = this._currentChild();
		if (currentActive) {
			(currentActive.$class as unknown as {_rmActive(wgt)})._rmActive(currentActive);
			this._curIndex = -1;
		}
	}

	/** @internal */
	removeAllChildrenActive_(): void {
		for (let child: zk.Widget | undefined = this?.firstChild; child != undefined; child = child.nextSibling) {
			if (child instanceof zul.menu.Menuitem) {
				(child.$class as typeof zul.menu.Menuitem)._rmActive(child);
			} else if (child instanceof zul.menu.Menu) {
				(child.$class as typeof zul.menu.Menu)._rmActive(child);
				const pp = child.menupopup;
				if (pp?.isOpen()) {
					jq(child.$n_()).removeClass(child.$s('hover')).removeClass(child.$s('selected'));
					pp.close();
				}
			}
		}
	}

	/** @internal */
	_currentChild(): zul.menu.Menuitem | zul.menu.Menu | undefined {
		const index = this._curIndex,
			$menubar = zul.menu.Menubar;
		if (index >= 0) {
			for (let w = this.firstChild, k = 0; w; w = w.nextSibling)
				if ($menubar._isActiveItem(w) && k++ == index)
					return w;
		}
		return undefined;
	}

	/** @internal */
	static _rmActive(wgt: zk.Widget): void {
		if (wgt.parent instanceof zul.menu.Menu) {
			(wgt.parent.constructor as typeof zul.menu.Menu)._rmActive(wgt.parent);
		}
	}
}
export var _nOpen = 0;
