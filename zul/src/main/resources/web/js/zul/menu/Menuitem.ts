/* Menuitem.ts

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _initUpld(wgt: zul.menu.Menuitem): void {
	zWatch.listen({ onShow: wgt });
	var v = wgt._upload;
	if (v)
		wgt._uplder = new zul.Upload(wgt, wgt._getUploadRef(), v);
}

function _cleanUpld(wgt: zul.menu.Menuitem): void {
	var v = wgt._uplder;
	if (v) {
		zWatch.unlisten({ onShow: wgt });
		wgt._uplder = undefined;
		v.destroy();
	}
}

/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 *
 * @defaultValue {@link getZclass}: z-menuitem.
 */
@zk.WrapClass('zul.menu.Menuitem')
export class Menuitem extends zul.LabelImageWidget implements zul.LabelImageWidgetWithAutodisable {
	// Parent could be null as asserted in _doMouseEnter
	override parent!: zul.menu.Menupopup | undefined;
	override nextSibling!: zul.menu.Menuitem | undefined;
	override previousSibling!: zul.menu.Menuitem | undefined;
	/** @internal */
	_value = '';
	/** @internal */
	_upload?: string;
	/** @internal */
	_checkmark?: boolean;
	/** @internal */
	_checked?: boolean;
	/** @internal */
	_autocheck?: boolean;
	/** @internal */
	_target?: string;
	/** @internal */
	_topmost = false;
	/** @internal */
	_col?: string;
	/** @internal */
	_href?: string;
	/** @internal */
	_disabled?: boolean;
	/** @internal */
	_adbs?: boolean;
	/** @internal */
	_autodisable?: string;

	/**
	 * @returns whether the check mark shall be displayed in front of each item.
	 * @defaultValue `false`.
	 */
	isCheckmark(): boolean {
		return !!this._checkmark;
	}

	/**
	 * Sets whether the check mark shall be displayed in front of each item.
	 */
	setCheckmark(checkmark: boolean, opts?: Record<string, boolean>): this {
		const o = this._checkmark;
		this._checkmark = checkmark;

		if (o !== checkmark || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;

		// eslint-disable-next-line zk/preferStrictBooleanType
		let value: boolean | undefined = disabled;

		//B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
		if (opts?.adbs)
			// called from zul.wgt.ADBS.autodisable
			this._adbs = true;	// Start autodisabling
		else if (!opts || opts.adbs === undefined)
			// called somewhere else (including server-side)
			this._adbs = false;	// Stop autodisabling
		if (!value) {
			if (this._adbs)
				// autodisable is still active, enable allowed
				this._adbs = false;
			// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
			else if (opts && opts.adbs === false)
				// ignore re-enable by autodisable mechanism
				value = this._disabled;
		}
		this._disabled = value;

		if (o !== value || opts?.force) {
			this.rerender(opts?.skip ? -1 : 0); //bind and unbind
		}

		return this;
	}

	/**
	 * @returns the href.
	 * @defaultValue `null`. If null, the button has no function unless you
	 * specify the onClick handler.
	 */
	getHref(): string | undefined {
		return this._href;
	}

	/**
	 * Sets the href.
	 */
	setHref(href: string, opts?: Record<string, boolean>): this {
		const o = this._href;
		this._href = href;

		if (o !== href || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the value.
	 * @defaultValue `""`.
	 */
	getValue(): string {
		return this._value;
	}

	/**
	 * Sets the value.
	 */
	setValue(value: string): this {
		this._value = value;
		return this;
	}

	/**
	 * @returns whether it is checked.
	 * @defaultValue `false`.
	 */
	isChecked(): boolean {
		return !!this._checked;
	}

	/**
	 * Sets whether it is checked.
	 * <p> This only applies when {@link isCheckmark} = true.
	 */
	setChecked(checked: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = checked;

		if (o !== checked || opts?.force) {
			if (checked)
				this._checkmark = checked;
			var n = this.$n();
			if (n && !this.isTopmost() && !this.getImage()) {
				var $n = jq(n);
				$n[checked ? 'addClass' : 'removeClass'](this.$s('checked'));
				if (this._checkmark)
					$n.addClass(this.$s('checkable'));
			}
		}

		return this;
	}

	/**
	 * @returns whether the menuitem check mark will update each time the menu item is selected.
	 * @defaultValue `false`.
	 */
	isAutocheck(): boolean {
		return !!this._autocheck;
	}

	/**
	 * Sets whether the menuitem check mark will update each time the menu item is selected.
	 * <p> This only applies when {@link isCheckmark} = true.
	 */
	setAutocheck(autocheck: boolean): this {
		this._autocheck = autocheck;
		return this;
	}

	/**
	 * @returns the target frame or window.
	 * <p>Note: it is useful only if href ({@link setHref}) is specified
	 * (i.e., use the onClick listener).
	 * @defaultValue `null`.
	 */
	getTarget(): string | undefined {
		return this._target;
	}

	/**
	 * Sets the target frame or window.
	 * @param target - the name of the frame or window to hyperlink.
	 */
	setTarget(target: string, opts?: Record<string, boolean>): this {
		const o = this._target;
		this._target = target;

		if (o !== target || opts?.force) {
			var anc = this.$n<HTMLAnchorElement>('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode as HTMLAnchorElement;
				anc.target = this._target;
			}
		}

		return this;
	}

	/**
	 * @returns a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 *
	 * <p>To represent the menuitem itself, the developer can specify `self`.
	 * For example, `<menuitem id="ok" autodisable="self,cancel"/>`
	 * is the same as `<menuitem id="ok" autodisable="ok,cancel"/>`
	 * that will disable
	 * both the ok and cancel menuitem when an user clicks it.
	 *
	 * <p>The menuitem being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a menuitem is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * `<menuitem id="ok" autodisable="+self,+cancel"/>`
	 *
	 * <p>Then, you have to enable them manually such as
	 * ```ts
	 * if (something_happened) {
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 * }
	 * ```
	 *
	 * @defaultValue `null`.
	 * @since 5.0.7
	 */
	getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/**
	 * Sets whether to disable the button after the user clicks it.
	 * @since 5.0.7
	 */
	setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	/**
	 * @returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link setUpload} for more details.
	 */
	getUpload(): string | undefined {
		return this._upload;
	}

	/**
	 * Sets the JavaScript class at the client to handle the upload if this
	 * button is used for file upload.
	 * @defaultValue `null`.
	 *
	 * @param upload - a JavaScript class to handle the file upload
	 * at the client, or "true" if the default class is used,
	 * or null or "false" to disable the file download (and then
	 * this button behaves like a normal button).
	 */
	setUpload(upload: string, opts?: Record<string, boolean>): this {
		const o = this._upload;
		this._upload = upload;

		if (o !== upload || opts?.force) {
			var n = this.$n();
			if (n) {
				_cleanUpld(this);
				if (upload && upload != 'false') _initUpld(this);
			}
		}

		return this;
	}

	/**
	 * @returns the file(s) belongs to this button if any.
	 * @since 10.0.0
	 */
	getFile(): FileList | undefined {
		return this._uplder?.getFile();
	}

	/**
	 * @returns whether this is an top-level menu, i.e., not owning by another {@link Menupopup}.
	 */
	isTopmost(): boolean {
		return this._topmost;
	}

	/** @internal */
	override beforeParentChanged_(newParent?: zk.Widget): void {
		this._topmost = !!newParent && !(newParent instanceof zul.menu.Menupopup);
		super.beforeParentChanged_(newParent);
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.$s('disabled') : '';
			if (added) scls += (scls ? ' ' : '') + added;
			added = (!this.getImage() && this.isCheckmark()) ?
				this.$s('checkable') + (this.isChecked() ? ' ' + this.$s('checked') : '') : '';
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	}

	/** @internal */
	override domContent_(): string {
		var label = '<span class="' + this.$s('text') + '">'
			+ (zUtl.encodeXML(this.getLabel())) + '</span>',
			icon = '<i class="' + this.$s('icon') + ' z-icon-check" aria-hidden="true"></i>',
			img = this.getImage(),
			iconSclass = this.domIcon_();

		if (img)
			img = '<img src="' + img + '" class="' + this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />'
				+ (iconSclass ? ' ' + iconSclass : '');
		else {
			if (iconSclass) {
				img = iconSclass;
			} else {
				img = '<img ' + (this.isTopmost() ? 'style="display:none"' : '')
					+ ' src="data:image/png;base64,R0lGODlhAQABAIAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" class="' + this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />';
			}
		}
		return img + (this.isAutocheck() || this.isCheckmark() ? icon : '') + label;
	}

	/**
	 * @returns the {@link Menubar} that contains this menuitem, or null if not available.
	 */
	getMenubar(): zul.menu.Menubar | undefined {
		for (var p: zk.Widget | undefined = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menubar)
				return p;
		return undefined;
	}

	/** @internal */
	_getRootMenu(): zul.menu.Menu | undefined {
		for (var p: zk.Widget | undefined = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menu && p.isTopmost())
				return p;
		return undefined;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		if (!this.isDisabled()) {
			var anc = this.$n_('a');
			if (this.isTopmost()) {
				this.domListen_(anc, 'onFocus', 'doFocus_')
					.domListen_(anc, 'onBlur', 'doBlur_');
			}
			this.domListen_(anc, 'onMouseEnter')
				.domListen_(anc, 'onMouseLeave');
			if (this._upload) _initUpld(this);
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (!this.isDisabled()) {
			if (this._upload) _cleanUpld(this);
			var anc = this.$n_('a');
			if (this.isTopmost()) {
				this.domUnlisten_(anc, 'onFocus', 'doFocus_')
					.domUnlisten_(anc, 'onBlur', 'doBlur_');
			}
			this.domUnlisten_(anc, 'onMouseEnter')
				.domUnlisten_(anc, 'onMouseLeave');
		}

		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean /* For inheritance */): void {
		if (this._disabled)
			evt.stop();
		else {
			if (!this._canActivate(evt)) return;
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.chrome || (evt.domTarget as HTMLInputElement).type != 'file') //ZK-3089
				this._uplder!.openFileDialog();

			var topmost = this.isTopmost(),
				anc = this.$n_<HTMLAnchorElement>('a');

			if (anc.href.startsWith('javascript:')) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
				//ZK-2679: prevent default behavior when upload=false
				if (!this._upload) //if upload=true, it won't fire onbeforeunload in IE <= 10
					evt.stop(); //if we stop evt when upload=true, it won't open upload window in IE <= 10
			} else if (anc.href.toLowerCase().startsWith('mailto:')) { // ZK-2506
				var ifrm = jq.newFrame('mailtoFrame', anc.href, undefined);
				jq(ifrm).remove();
				evt.stop();
			} else {
				if (zk.gecko && topmost && this.$n_().id != anc.id) {
					zUtl.go(anc.href, { target: anc.target });
					evt.stop();
					// Bug #2154611 we shall eat the onclick event, if it is FF3.
				}
			}
			if (!topmost) {
				var ref: zk.Widget | undefined;
				for (var p: zk.Widget | undefined = this.parent; p; p = p.parent) {
					if (p instanceof zul.menu.Menupopup) {
						// if close the popup before choosing a file, the file chooser can't be triggered.
						if (!p.isOpen() || this._uplder || p._keepOpen /*Bug #2911385 && !this._popup*/)
							break;
						this._updateHoverImage(); // remove hover image
						ref = p._fakeParent;
						p.close({ sendOnOpen: true });
					} else if (!(p instanceof zul.menu.Menu)) //either menubar or non-menu*
						break;
					else
						p._updateHoverImage(); // remove parent Menu hover image
				}
				// regain the focus on the root menu
				if (!this.isRealVisible()) {
					var rootMenu = this._getRootMenu();
					if (rootMenu) rootMenu.focus();
					else if (ref) {
						// https://bugzilla.mozilla.org/show_bug.cgi?id=1220143
						if (zk.ff)
							setTimeout(function () {
								var currentFocus = zk.currentFocus;
								if (!currentFocus || !currentFocus.isRealVisible())
									ref!.focus();
							}, 200);
						else
							ref.focus();
					}
				}
			}

			var menubar: zul.menu.Menubar | undefined;
			if (zk.webkit && (menubar = this.getMenubar()) && menubar._autodrop)
				menubar._noFloatUp = true;
			//_noFloatUp used in Menu.js to fix Bug 1852304
			super.doClick_(evt, true);
		}
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		if (this.isTopmost() && !this._disabled) {
			var key = evt.key;
			if (key == ' ' || key == 'Enter') {
				evt.stop();
				this.doClick_(new zk.Event(this, 'onClick', {}));
			}
		}
		super.doKeyDown_(evt);
	}

	/** @internal */
	_canActivate(evt: zk.Event): boolean {
		return !this.isDisabled() && (!this.isTopmost() || !!this._uplder
			|| jq.isAncestor(this.$n('a'), evt.domTarget));
	}

	/** @internal */
	_getUploadRef(): HTMLAnchorElement | undefined {
		return this.$n('a');
	}

	/** @internal */
	_doMouseEnter(evt: MouseEvent): void {
		var isTopmost = this.isTopmost();
		if (zul.menu._nOpen || isTopmost)
			zWatch.fire('onFloatUp', this); //notify all
		if (!isTopmost && !this._disabled) {
			if (this.parent instanceof zul.menu.Menupopup)
				this.parent.removeAllChildrenActive_();
			(this.$class as typeof Menuitem)._addActive(this, 'hover');
			this.focus();
		}
	}

	/** @internal */
	_doMouseLeave(evt: MouseEvent): void {
		var isTopmost = this.isTopmost();
		if (!isTopmost && !this._disabled) {
			(this.$class as typeof Menuitem)._rmActive(this);
		}
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		var tag = this.isTopmost() ? 'td' : 'li';
		out.push('<', tag, this.domAttrs_({ domClass: true }), ' class="z-renderdefer"></', tag, '>');
	}

	override getImageNode(): HTMLImageElement | undefined {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n)
				this._eimg = this.$n_('a').firstChild as HTMLImageElement | undefined;
		}
		return this._eimg;
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
	static _isActive(wgt: zk.Widget): boolean {
		return jq(wgt.$n_()).hasClass(wgt.$s('hover'));
	}

	/** @internal */
	static _addActive(wgt: zul.menu.Menu | zul.menu.Menuitem, type: string): void {
		var top = wgt.isTopmost();
		jq(wgt.$n_()).addClass(wgt.$s(type));
		if (!top && wgt.parent instanceof zul.menu.Menupopup) {
			const parentMenupopup = wgt.parent;
			parentMenupopup.addActive_(wgt);
			if (parentMenupopup.parent instanceof zul.menu.Menu)
				this._addActive(parentMenupopup.parent, type);
		}
	}

	/** @internal */
	static _rmActive(wgt: zk.Widget): JQuery {
		return jq(wgt.$n_()).removeClass(wgt.$s('hover')).removeClass(wgt.$s('focus'));
	}

	onShow(): void {
		if (this._uplder)
			this._uplder.sync();
	}
}