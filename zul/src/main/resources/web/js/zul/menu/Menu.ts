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

function _isActiveItem(wgt: zul.menu.Menu): boolean | null | undefined {
	return wgt.isVisible() && wgt instanceof zul.menu.Menu && !wgt.isDisabled();
}
function _nextVisibleMenu(menu: zul.menu.Menu): zul.menu.Menu {
	for (var m: zul.menu.Menu | null = menu; m; m = m.nextSibling) {
		if (_isActiveItem(m))
			return m;
	}
	var mb = menu.parent!;
	if (mb.firstChild == menu)
		return menu;
	return _nextVisibleMenu(mb.firstChild!);
}

function _prevVisibleMenu(menu: zul.menu.Menu): zul.menu.Menu {
	for (var m: zul.menu.Menu | null = menu; m; m = m.previousSibling) {
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
 * <p>Default {@link #getZclass}: z-mean.
 */
export class Menu extends zul.LabelImageWidget {
	// parent could be null as asserted in getMenubar
	public override parent!: zul.menu.Menubar | null;
	public override nextSibling!: zul.menu.Menu | null;
	public override previousSibling!: zul.menu.Menu | null;
	// menupopup could be null as asserted in _doMouseEnter
	public menupopup?: zul.menu.Menupopup | null;
	private _content?: string;
	public _contentHandler?: ContentHandler;
	private _ignoreActive?: boolean;
	private _topmost?: boolean | null;

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 * <p>It is useful to show the description in more versatile way.
	 * <p>Default: empty ("").
	 * @return String
	 */
	public getContent(): string | undefined {
		return this._content;
	}

	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>There is a way to create {@link zkex.inp.Colorbox} automatically by using
	 * #color=#RRGGBB, usage example <code>setContent("#color=FFFFFF")</code>
	 *
	 * @param String content
	 */
	public setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || (opts && opts.force)) {
			if (!content || content.length == 0)
				return this;

			if (!this._contentHandler) {
				if (zk.feature.pe) {
					var self = this;
					zk.load('zkex.inp', null, function () {
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

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 8.0.3
	 * @return boolean
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

	public override getImage(): string | undefined {
		return this._image;
	}

	public override setImage(v: string, opts?: Record<string, boolean>): this {
		const o = this._image;
		this._image = v;

		if (o !== v || (opts && opts.force)) {
			if (v && this._preloadImage) zUtl.loadImage(v);
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
	public open(): void {
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
	public getAnchor_(): HTMLAnchorElement | null | undefined {
		return this.$n('a') as HTMLAnchorElement | null | undefined;
	}

	protected override domContent_(): string {
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

	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 * @return boolean
	 */
	public isTopmost(): boolean | null | undefined {
		return this._topmost;
	}

	public override beforeParentChanged_(newParent: zk.Widget | null): void {
		this._topmost = newParent && !(newParent instanceof zul.menu.Menupopup);
		super.beforeParentChanged_(newParent);
	}

	protected override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.menu.Menupopup) {
			this.menupopup = child;

			if (this._contentHandler)
				this._contentHandler.destroy();
		}
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.menupopup) {
			this.menupopup = null;

			if (this._contentHandler)
				this._contentHandler.setContent(this._content);
		}
	}

	/** Returns the {@link Menubar} that contains this menu, or null if not available.
	 * @return zul.menu.Menubar
	 */
	public getMenubar(): zul.menu.Menubar | null {
		for (var p: zk.Widget | null = this.parent; p; p = p.parent) {
			if (p instanceof zul.menu.Menubar)
				return p;

			if (!(p instanceof zul.menu.Menupopup || p instanceof zul.menu.Menu))
				break; // not found
		}
		return null;
	}

	public onShow(): void {
		if (this._contentHandler)
			this._contentHandler.onShow();
	}

	public onFloatUp(ctl: zk.ZWatchController): void {
		if (this._contentHandler)
			this._contentHandler.onFloatUp(ctl);
	}

	public onHide(): void {
		if (this._contentHandler)
			this._contentHandler.onHide();
	}

	public override focus_(timeout?: number, ignoreActive?: boolean/* used for Menupopup.js*/): boolean {
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
	public _getPrevVisibleMenu(): zul.menu.Menu {
		var prev = this.previousSibling;
		if (!prev) {
			var mb = this.getMenubar();
			if (mb)
				prev = mb.lastChild;
		}
		return prev ? _prevVisibleMenu(prev) : this;
	}

	// used for Menupopup.js
	public _getNextVisibleMenu(): zul.menu.Menu {
		var next = this.nextSibling;
		if (!next) {
			var mb = this.getMenubar();
			if (mb)
				next = mb.firstChild;
		}
		return next ? _nextVisibleMenu(next) : this;
	}

	protected override doKeyDown_(evt: zk.Event): void {

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
					this.menupopup.open(null, null, null, {focusFirst: true, sendOnOpen: true, disableMask: true});
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

	protected override doFocus_(evt: zk.Event): void {
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

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
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

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
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
	public _getArrowWidth(): number {
		return 20;
	}

	private _showContentHandler(): void {
		var content = this._contentHandler;
		if (content && !content.isOpen())
			content.onShow();
	}

	public _hideContentHandler(): void {
		var content = this._contentHandler;
		if (content && content.isOpen()) {
			content.onHide();
			if (this.isTopmost())
				this.focus();
			else
				this.parent!.focus();
		}
	}

	public override doClick_(evt: zk.Event, popupOnly?: boolean): void {
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

	protected override doMouseOver_(evt: zk.Event): void {
		if (!this.isTopmost()) {
			this._showContentHandler();
		}
		super.doMouseOver_(evt);
	}

	public _togglePopup(byKeyboard?: boolean): void {
		// show the content handler
		if (!this.menupopup && this._contentHandler) {
			this._showContentHandler();
		} else {
			if (!this.menupopup!.isOpen()) {
				if (this.isTopmost())
					_toggleClickableCSS(this);
				this.menupopup!.open(null, null, null, {focusFirst: byKeyboard, sendOnOpen: true, disableMask: true});
			} else if (this.isTopmost())
				this.menupopup!.close({sendOnOpen: true});
			else
				zk(this.menupopup!.$n('a')).focus(); // force to get a focus
		}
	}

	public _doMouseEnter(evt: zk.Event): void {
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._noFloatUp = false;
		}
		if (this._disabled) return;
		var	topmost = this.isTopmost();
		if (topmost)
			_toggleClickableCSS(this);
		if (topmost && zk.ie < 11 && !jq.isAncestor(this.getAnchor_(), evt.domTarget))
				return; // don't activate

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

	public _doMouseLeave(evt: zk.Widget): void { //not zk.Widget.doMouseOut_
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

	//@Override
	public override getImageNode(): HTMLImageElement | null | undefined {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n)
				this._eimg = this.$n_('a').firstChild as HTMLImageElement | null;
		}
		return this._eimg;
	}

	public override ignoreDescendantFloatUp_(des: zk.Widget): boolean {
		return des && des instanceof zul.menu.Menupopup;
	}

	public isVertical_(): boolean {
		if (this.isTopmost()) {
			var bar = this.getMenubar();
			if (bar)
				return bar.isVertical();
		}
		return false;
	}

	public static _isActive(wgt: zul.menu.Menu): boolean {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? menupopup && menupopup.isOpen() ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover');
		return jq(n).hasClass(cls);
	}

	public static _addActive(wgt: zul.menu.Menu): void {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? menupopup && menupopup.isOpen() ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover');
		jq(n).addClass(cls);
		if (top) {
			var mb = wgt.getMenubar();
			if (mb)
				mb._lastTarget = wgt;
		} else {
			// FIXME: This branch is hardly ever taken. I was never able to get into this branch by playing in ZK Demo.
			// So, I can't tell whether the logic below is correct or not. Two points seem fishy.
			// 1. Prior to ZK-4598 `wgt.parent` was not tested for nullity. After ZK-4598, the second `if` assumes that
			//    `wgt.parent` is not null, but tests it for the first `if`. What should be the case?
			// 2. Shouldn't `wgt.parent` be a Menubar? But the code assumes it to be a Menupopup. Which is the case?
			var parentMenupopup = wgt.parent as unknown as zul.menu.Menupopup;
			if (parentMenupopup)
				parentMenupopup.addActive_(wgt);
			if (parentMenupopup.parent instanceof zul.menu.Menu)
				this._addActive(parentMenupopup.parent);
		}
	}

	public static _rmActive(wgt: zul.menu.Menu, ignoreSeld?: boolean/* used for mouseout when topmost*/): void {
		var top = wgt.isTopmost(),
			n = wgt.$n_(),
			menupopup = wgt.menupopup,
			cls = top ? (!ignoreSeld && menupopup && menupopup.isOpen()) ? wgt.$s('selected') : wgt.$s('hover') : wgt.$s('hover'),
			anode = jq(n).removeClass(cls);
		if (top && !(anode.hasClass(wgt.$s('selected')) || anode.hasClass(wgt.$s('hover'))))
			_toggleClickableCSS(wgt, true);
	}

	// Prior to TS migration, this method was accidentally declared in ContentHandler.
	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<li', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></li>');
	}
}
zul.menu.Menu = zk.regClass(Menu);

export class ContentHandler extends zk.Object {
	private _wgt: Menu;
	private _content?: string;
	private _shadow?: zk.eff.Shadow | null;
	private _pp?: HTMLElement | null;

	public constructor(wgt: zul.menu.Menu, content: string) {
		super();
		this._wgt = wgt;
		this._content = content;
	}

	public setContent(content: string | undefined): void {
		if (this._content != content || !this._pp) {
			this._content = content;
			this._wgt.rerender();
		}
	}

	public redraw(out: string[]): void {
		var wgt = this._wgt;

		out.push('<div id="', wgt.uuid, '-cnt-pp" class="', wgt.$s('content-popup'),
			'" style=""><div class="', wgt.$s('content-body'), '">', this._content!, '</div></div>');
	}

	public bind(): void {
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

	public unbind(): void {
		var wgt = this._wgt;
		if (!wgt.menupopup) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
			wgt.domUnlisten_(wgt.$n_(), 'onClick', 'onShow');
			zWatch.unlisten({onFloatUp: wgt, onHide: wgt});
		}

		jq(this._pp, zk)
			.off('mouseenter', this.proxy(this._doMouseEnter))
			.off('mouseleave', this.proxy(this._doMouseLeave));
		this._pp = null;
	}

	public isOpen(): boolean | null | undefined {
		var pp = this._pp;
		return (pp && zk(pp).isVisible());
	}

	private _doMouseEnter(): void {
		var menubar = this._wgt.getMenubar();
		if (menubar) menubar._bOver = true;
	}

	private _doMouseLeave(): void {
		var menubar = this._wgt.getMenubar();
		if (menubar) menubar._bOver = false;
	}

	public onShow(): void {
		var wgt = this._wgt,
			pp = this._pp;
		if (!pp) return;

		pp.style.display = 'block';

		jq(pp).zk.makeVParent();
		zWatch.fireDown('onVParent', this);

		zk(pp).position(wgt.$n_(), this.getPosition());
		this.syncShadow();
		}

	public onHide(): void {
		var pp = this._pp;
		if (!pp || !zk(pp).isVisible()) return;

		pp.style.display = 'none';
		jq(pp).zk.undoVParent();
		zWatch.fireDown('onVParent', this);

		this.hideShadow();
	}

	public onFloatUp(ctl: zk.ZWatchController): void {
		if (!zUtl.isAncestor(this._wgt, ctl.origin))
			this.onHide();
	}

	public syncShadow(): void {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this._wgt.$n_('cnt-pp'), {stackup: zk.useStackup as boolean});
		this._shadow.sync();
	}

	public hideShadow(): void {
		if (this._shadow)
			this._shadow.hide();
	}

	public destroy(): void {
		this._wgt.rerender();
	}

	public getPosition(): 'end_before' | 'after_start' {
		var wgt = this._wgt;
		return wgt.isVertical_() ? 'end_before' : 'after_start';
	}
}
zul.menu.ContentHandler = zk.regClass(ContentHandler);