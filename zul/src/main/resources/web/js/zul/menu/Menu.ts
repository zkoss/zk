/* Menu.ts

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _toggleClickableCSS(wgt: zul.menu.Menu, remove?: boolean): void {
	if (wgt.isListen('onClick')) {
		jq(wgt.$n_())[remove ? 'removeClass' : 'addClass'](wgt.$s('hover'));
	}
}
function _doClick(wgt: zul.menu.Menu, evt: zk.Event): void {
	var byKeyboard = evt.name == 'onKeyDown',
		n = wgt.$n_();
	if (wgt.isListen('onClick')) {
		var clk = jq(n).find('.' + wgt.$s('separator')),
			zclk = zk(clk),
			offsetX = zclk.revisedOffset()[0];

		//Bug ZK-1357: minus 10px for easily open menupopup when click near arrow icon
		if (jq(n).hasClass('z-menu-clickable') && evt.pageX > offsetX) {
			jq(n).addClass(wgt.$s('selected'));
			wgt.menupopup!._shallClose = false;
			wgt._togglePopup(byKeyboard);
			evt.stop();
		} else
			wgt.fireX(new zk.Event(wgt, 'onClick', evt.data));

	} else {
		jq(n).addClass(wgt.isTopmost() ? wgt.$s('selected') : wgt.$s('hover'));
		wgt.menupopup!._shallClose = false;
		wgt._togglePopup(byKeyboard);
	}
}

function _isActiveItem(wgt: zul.menu.Menu): boolean {
	return wgt.isVisible() && wgt instanceof zul.menu.Menu && !wgt.isDisabled();
}
function _nextVisibleMenu(menu: zul.menu.Menu): zul.menu.Menu {
	for (var m: zul.menu.Menu | undefined = menu; m; m = m.nextSibling) {
		if (_isActiveItem(m))
			return m;
	}
	var mb = menu.parent!;
	if (mb.firstChild == menu)
		return menu;
	return _nextVisibleMenu(mb.firstChild!);
}

function _prevVisibleMenu(menu: zul.menu.Menu): zul.menu.Menu {
	for (var m: zul.menu.Menu | undefined = menu; m; m = m.previousSibling) {
		if (_isActiveItem(m))
			return m;
	}
	var mb = menu.parent!;
	if (mb.lastChild == menu)
		return menu;
	return _prevVisibleMenu(mb.lastChild!);
}
/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 *
 * @defaultValue {@link getZclass}: z-mean.
 */
@zk.WrapClass('zul.menu.Menu')
export class Menu extends zul.LabelImageWidget implements zul.LabelImageWidgetWithDisable {
	// parent could be null as asserted in getMenubar
	override parent!: zul.menu.Menubar | undefined;
	override nextSibling!: zul.menu.Menu | undefined;
	override previousSibling!: zul.menu.Menu | undefined;
	// menupopup could be null as asserted in _doMouseEnter
	menupopup?: zul.menu.Menupopup | undefined;
	/** @internal */
	_content?: string;
	/** @internal */
	_contentHandler?: zul.menu.ContentHandler | zkex.inp.ContentHandler;
	/** @internal */
	_ignoreActive?: boolean;
	/** @internal */
	_topmost = false;
	/** @internal */
	_disabled = false;
	/** @internal */
	_autodisable?: string;

	/**
	 * @returns the embedded content (i.e., HTML tags) that is shown as part of the description.
	 * <p>It is useful to show the description in more versatile way.
	 * @defaultValue empty ("").
	 */
	getContent(): string | undefined {
		return this._content;
	}

	/**
	 * Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>There is a way to create {@link zkex.inp.Colorbox} automatically by using
	 * `#color=#RRGGBB`, usage example `setContent("#color=FFFFFF")`
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || opts?.force) {
			if (!content || content.length == 0)
				return this;

			if (!this._contentHandler) {
				if (zk.feature.pe) {
					var self = this;
					zk.load('zkex.inp', undefined, function () {
						self._contentHandler = new zkex.inp.ContentHandler(self, content);
					});
					return this;
				}
				this._contentHandler = new zul.menu.ContentHandler(this, content);
			} else
				this._contentHandler.setContent(content);
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 * @since 8.0.3
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * @since 8.0.3
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			if (!this.desktop)
				return this;
			if (disabled) {
				jq(this.$n_('a'))
					.attr('disabled', 'disabled')
					.attr('tabindex', '-1');
			} else {
				jq(this.$n_('a'))
					.removeAttr('disabled')
					.removeAttr('tabindex');
			}
		}

		return this;
	}

	override getImage(): string | undefined {
		return this._image;
	}

	override setImage(image: string, opts?: Record<string, boolean>): this {
		const o = this._image;
		this._image = image;

		if (o !== image || opts?.force) {
			if (image && this._preloadImage) zUtl.loadImage(image);
			this.rerender();
		}

		return this;
	}

	/**
	 * Opens the menupopup that belongs to the menu.
	 * <p>
	 * Note that this function is only applied when it is topmost menu, i.e. the parent of the menu is {@link Menubar}
	 * @since 6.0.0
	 */
	open(): void {
		if (this.desktop && this.isTopmost()) {
			jq(this.$n_()).addClass(this.$s('selected'));
			var mb = this.getMenubar()!;
			if (mb._lastTarget)
				(this.$class as typeof Menu)._rmActive(mb._lastTarget);
			mb._lastTarget = this;
			this.menupopup!._shallClose = false;
			this._togglePopup();
		}
	}

	// since ZK 6.5.0 internal use only.
	/** @internal */
	getAnchor_(): HTMLAnchorElement | undefined {
		return this.$n('a');
	}

	/** @internal */
	override domContent_(): string {
		var label = '<span class="' + this.$s('text') + '">'
					+ (zUtl.encodeXML(this.getLabel())) + '</span>',
		img = this.getImage(),
		icon = '<i class="' + this.$s('icon') + ' z-icon-caret-'
				+ (this.isTopmost() && !this.isVertical_() ? 'down' : 'right') + '" aria-hidden="true"></i>',
		separator = '<div class="' + this.$s('separator') + '" aria-hidden="true"></div>',
		iconSclass = this.domIcon_();

		if (img)
			img = '<img id="' + this.uuid + '-img" src="' + img + '" class="' + this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />'
				+ (iconSclass ? ' ' + iconSclass : '');
		else {
			if (iconSclass) {
				img = iconSclass;
			} else {
				img = '<img id="' + this.uuid + '-img" ' + (this.isTopmost() ? 'style="display:none"' : '')
					+ ' src="data:image/png;base64,R0lGODlhAQABAIAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" class="'
					+ this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />';
			}
		}
		return img + label + separator + icon;
	}

	/**
	 * @returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 */
	isTopmost(): boolean {
		return this._topmost;
	}

	/** @internal */
	override beforeParentChanged_(newParent: zk.Widget | undefined): void {
		this._topmost = !!newParent && !(newParent instanceof zul.menu.Menupopup);
		super.beforeParentChanged_(newParent);
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.menu.Menupopup) {
			this.menupopup = child;

			if (this._contentHandler)
				this._contentHandler.destroy();
		}
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.menupopup) {
			this.menupopup = undefined;

			if (this._contentHandler)
				this._contentHandler.setContent(this._content);
		}
	}

	/**
	 * @returns the {@link Menubar} that contains this menu, or null if not available.
	 */
	getMenubar(): zul.menu.Menubar | undefined {
		for (var p: zk.Widget | undefined = this.parent; p; p = p.parent) {
			if (p instanceof zul.menu.Menubar)
				return p;

			if (!(p instanceof zul.menu.Menupopup || p instanceof zul.menu.Menu))
				break; // not found
		}
		return undefined;
	}

	onShow(): void {
		if (this._contentHandler)
			this._contentHandler.onShow();
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (this._contentHandler)
			this._contentHandler.onFloatUp(ctl);
	}

	onHide(): void {
		if (this._contentHandler)
			this._contentHandler.onHide();
	}

	/** @internal */
	override focus_(timeout?: number, ignoreActive?: boolean/* used for Menupopup.js*/): boolean {
		if (this.isTopmost() && zk(this.getAnchor_()).focus(timeout)) {
			// fixed for pressing TAB key from menupopup when the menupopup
			// is the last one, in IE it will delay to show the active effect.
			// We have to use the ignoreActive to avoid adding the active effect
			// to the menu widget.
			if (ignoreActive) {
				this._ignoreActive = true;
			}
			return true;
		}
		return super.focus_(timeout);
	}

	// used for Menupopup.js
	/** @internal */
	_getPrevVisibleMenu(): zul.menu.Menu {
		var prev = this.previousSibling;
		if (!prev) {
			var mb = this.getMenubar();
			if (mb)
				prev = mb.lastChild;
		}
		return prev ? _prevVisibleMenu(prev) : this;
	}

	// used for Menupopup.js
	/** @internal */
	_getNextVisibleMenu(): zul.menu.Menu {
		var next = this.nextSibling;
		if (!next) {
			var mb = this.getMenubar();
			if (mb)
				next = mb.firstChild;
		}
		return next ? _nextVisibleMenu(next) : this;
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {

		// only support for the topmost menu
		if (this.isTopmost()) {
			var keyCode = evt.keyCode;

			// switch the navigation key when in vertical view
			if (this.isVertical_()) {
				switch (keyCode) {
				case 38: //UP
					keyCode = 37;
					break;
				case 40: //DOWN
					keyCode = 39;
					break;
				case 37: //LEFT
					keyCode = 38;
					break;
				case 39: //RIGHT
					keyCode = 40;
					break;
				}
			}
			switch (keyCode) {
			case 38: //UP
				// 1. close the menupopup if any.
				// 2. make the menu as focus effect
				var pp = this.menupopup;
				if (pp && pp.isOpen()) {
					jq(this.$n_()).removeClass(this.$s('hover')).removeClass(this.$s('selected'));
					pp.close();
				}
				(this.$class as typeof Menu)._addActive(this); // keep the focus
				evt.stop();
				break;
			case 40: //DOWN
				// 1. open menupopup if any.
				// 2. pass the focus control to menupopup
				if (this.menupopup && !this._disabled) {
					jq(this.$n_()).addClass(this.$s('selected')).removeClass(this.$s('hover'));
					this.menupopup._shallClose = false;
					this.menupopup.open(undefined, undefined, undefined, {focusFirst: true, sendOnOpen: true, disableMask: true});
				}
				evt.stop();
				break;
			case 13: //ENTER
			case 32: //SPACE
				// 1. toggle the open/close status for the menupopup, if any.
				if (this.menupopup)
					_doClick(this, evt);
				evt.stop();
				break;
			case 9:
				// 1. deactive this menu, then it will jump to the next focus target.
				(this.$class as typeof Menu)._rmActive(this);
				break;
			}
		}
		super.doKeyDown_(evt);
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		if (this.isTopmost()) {
			var menubar = this.getMenubar(),
				$menu = this.$class as typeof Menu;

			// clear the previous active target, if any.
			if (menubar && menubar._lastTarget)
				$menu._rmActive(menubar._lastTarget);
			if (!this._ignoreActive)
				$menu._addActive(this);
		}
		// delete the variable used for IE
		delete this._ignoreActive;
		super.doFocus_(evt);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var anc = this.getAnchor_()!;

		this.domListen_(anc, 'onFocus', 'doFocus_') // used to handle keystroke
			.domListen_(anc, 'onBlur', 'doBlur_')
			.domListen_(anc, 'onMouseEnter')
			.domListen_(anc, 'onMouseLeave');


		if (this.isTopmost() && this.isListen('onClick')) {
			jq(this.$n_()).addClass(this.$s('clickable'));
		}

		if (this._contentHandler)
			this._contentHandler.bind();
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var anc = this.getAnchor_()!;
		this.domUnlisten_(anc, 'onFocus', 'doFocus_')
			.domUnlisten_(anc, 'onBlur', 'doBlur_')
			.domUnlisten_(anc, 'onMouseEnter')
			.domUnlisten_(anc, 'onMouseLeave');

		if (this._contentHandler)
			this._contentHandler.unbind();

		super.unbind_(skipper, after, keepRod);
	}

	// used for overriding from different theme
	/** @internal */
	_getArrowWidth(): number {
		return 20;
	}

	/** @internal */
	_showContentHandler(): void {
		var content = this._contentHandler;
		if (content && !content.isOpen())
			content.onShow();
	}

	/** @internal */
	_hideContentHandler(): void {
		var content = this._contentHandler;
		if (content && content.isOpen()) {
			content.onHide();
			if (this.isTopmost())
				this.focus();
			else
				this.parent!.focus();
		}
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (!this._disabled) {
			if (this.menupopup) {
				if (this.isTopmost())
					this.getMenubar()!._lastTarget = this;
				// toggle the open/close status of menupopup/contenthandler
				_doClick(this, evt);
			} else {
				this._showContentHandler();
			}
		}
		evt.stop();
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		if (!this.isTopmost()) {
			this._showContentHandler();
		}
		super.doMouseOver_(evt);
	}

	/** @internal */
	_togglePopup(byKeyboard?: boolean): void {
		// show the content handler
		if (!this.menupopup && this._contentHandler) {
			this._showContentHandler();
		} else {
			if (!this.menupopup!.isOpen()) {
				if (this.isTopmost())
					_toggleClickableCSS(this);
				this.menupopup!.open(undefined, undefined, undefined, {focusFirst: byKeyboard, sendOnOpen: true, disableMask: true});
			} else if (this.isTopmost())
				this.menupopup!.close({sendOnOpen: true});
			else
				zk(this.menupopup!.$n('a')).focus(); // force to get a focus
		}
	}

	/** @internal */
	_doMouseEnter(evt: zk.Event): void {
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._noFloatUp = false;
		}
		if (this._disabled) return;
		var	topmost = this.isTopmost();
		if (topmost)
			_toggleClickableCSS(this);

		if (this.menupopup)
			this.menupopup._shallClose = false;
		if (!topmost) {
			zWatch.fire('onFloatUp', this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
			// FIXME: Only Menupopup defines `removeActive_`, but shouldn't the parent be a Menubar?
			(this.parent as unknown as zul.menu.Menupopup).removeActive_();
		} else {
			// NOTE: It has always been the case that menubar is assumed to exist in this branch,
			// i.e., if `topmost` holds, but menubar is tested for nullity in the beginning of this
			// function, i.e., `if (menubar) { menubar._noFloatUp = false; }`. Is this intended?
			if (this.menupopup && menubar!._autodrop) {
				zWatch.fire('onFloatUp', this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar!._lastTarget;
				if (target && target != this) {
					(this.$class as typeof Menu)._rmActive(target);
					if (target.menupopup && target.menupopup.isVisible()) {
						target.menupopup.close({sendOnOpen: true});
						if (this.menupopup) this.menupopup.open();
					}
				}
			}
		}
		(this.$class as typeof Menu)._addActive(this);
	}

	/** @internal */
	_doMouseLeave(evt: zk.Widget): void { //not zk.Widget.doMouseOut_
		var menubar = this.getMenubar();
		this._updateHoverImage(); // remove hover image if any
		if (this._disabled) return;
		var topmost = this.isTopmost(),
			menupopup = this.menupopup;
		if (topmost) { //implies menubar
			(this.$class as typeof Menu)._rmActive(this, true);
			if (menupopup && menubar!._autodrop) {
				if (menupopup.isOpen())
					menupopup._shallClose = true; //autodrop -> autoclose if mouseout
			}
		} else if (!menupopup || !menupopup.isOpen())
			(this.$class as typeof Menu)._rmActive(this);
	}

	override getImageNode(): HTMLImageElement | undefined {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n)
				this._eimg = this.$n_('a').firstChild as HTMLImageElement | undefined;
		}
		return this._eimg;
	}

	/** @internal */
	override ignoreDescendantFloatUp_(des: zk.Widget): boolean {
		return des && des instanceof zul.menu.Menupopup;
	}

	/** @internal */
	isVertical_(): boolean {
		if (this.isTopmost()) {
			var bar = this.getMenubar();
			if (bar)
				return bar.isVertical();
		}
		return false;
	}

	/** @internal */
	static _isActive(wgt: zul.menu.Menu): boolean {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? menupopup && menupopup.isOpen() ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover');
		return jq(n).hasClass(cls);
	}

	/** @internal */
	static _addActive(wgt: zul.menu.Menu): void {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? menupopup && menupopup.isOpen() ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover');
		jq(n).addClass(cls);
		if (top) {
			var mb = wgt.getMenubar();
			if (mb)
				mb._lastTarget = wgt;
		} else if (wgt.parent instanceof zul.menu.Menupopup) {
			const parentMenupopup = wgt.parent;
			parentMenupopup.addActive_(wgt);
			if (parentMenupopup.parent instanceof zul.menu.Menu)
				this._addActive(parentMenupopup.parent);
		}
	}

	/** @internal */
	static _rmActive(wgt: zul.menu.Menu, ignoreSeld?: boolean/* used for mouseout when topmost*/): void {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? (!ignoreSeld && menupopup && menupopup.isOpen()) ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover'),
			anode = jq(n).removeClass(cls);
		if (top && !(anode.hasClass(wgt.$s('selected')) || anode.hasClass(wgt.$s('hover'))))
			_toggleClickableCSS(wgt, true);
	}

	// Prior to TS migration, this method was accidentally declared in ContentHandler.
	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<li', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></li>');
	}
}

@zk.WrapClass('zul.menu.ContentHandler')
export class ContentHandler extends zk.Object {
	/** @internal */
	_wgt: Menu;
	/** @internal */
	_content?: string;
	/** @internal */
	_shadow?: zk.eff.Shadow;
	/** @internal */
	_pp?: HTMLElement;

	constructor(wgt: zul.menu.Menu, content: string) {
		super();
		this._wgt = wgt;
		this._content = content;
	}

	setContent(content: string | undefined): this {
		if (this._content != content || !this._pp) {
			this._content = content;
			this._wgt.rerender();
		}
		return this;
	}

	redraw(out: string[]): void {
		var wgt = this._wgt;

		out.push('<div id="', wgt.uuid, '-cnt-pp" class="', wgt.$s('content-popup'),
			'" style=""><div class="', wgt.$s('content-body'), '">', this._content!, '</div></div>');
	}

	bind(): void {
		var wgt = this._wgt;
		if (!wgt.menupopup) {
			wgt.domListen_(wgt.$n_(), 'onClick', 'onShow');
			zWatch.listen({onFloatUp: wgt, onHide: wgt});
		}

		this._pp = wgt.$n('cnt-pp');

		jq(this._pp, zk)
			.on('mouseenter', this.proxy(this._doMouseEnter))
			.on('mouseleave', this.proxy(this._doMouseLeave));
	}

	unbind(): void {
		var wgt = this._wgt;
		if (!wgt.menupopup) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = undefined;
			}
			wgt.domUnlisten_(wgt.$n_(), 'onClick', 'onShow');
			zWatch.unlisten({onFloatUp: wgt, onHide: wgt});
		}

		jq(this._pp, zk)
			.off('mouseenter', this.proxy(this._doMouseEnter))
			.off('mouseleave', this.proxy(this._doMouseLeave));
		this._pp = undefined;
	}

	isOpen(): boolean {
		var pp = this._pp;
		return (!!pp && zk(pp).isVisible());
	}

	/** @internal */
	_doMouseEnter(): void {
		var menubar = this._wgt.getMenubar();
		if (menubar) menubar._bOver = true;
	}

	/** @internal */
	_doMouseLeave(): void {
		var menubar = this._wgt.getMenubar();
		if (menubar) menubar._bOver = false;
	}

	onShow(): void {
		var wgt = this._wgt,
			pp = this._pp;
		if (!pp) return;

		pp.style.display = 'block';

		jq(pp).zk.makeVParent();
		zWatch.fireDown('onVParent', this);

		zk(pp).position(wgt.$n_(), this.getPosition());
		this.syncShadow();
		}

	onHide(): void {
		var pp = this._pp;
		if (!pp || !zk(pp).isVisible()) return;

		pp.style.display = 'none';
		jq(pp).zk.undoVParent();
		zWatch.fireDown('onVParent', this);

		this.hideShadow();
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (!zUtl.isAncestor(this._wgt, ctl.origin))
			this.onHide();
	}

	syncShadow(): void {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this._wgt.$n_('cnt-pp'), {stackup: zk.useStackup as boolean});
		this._shadow.sync();
	}

	hideShadow(): void {
		if (this._shadow)
			this._shadow.hide();
	}

	destroy(): void {
		this._wgt.rerender();
	}

	getPosition(): 'end_before' | 'after_start' {
		var wgt = this._wgt;
		return wgt.isVertical_() ? 'end_before' : 'after_start';
	}
}