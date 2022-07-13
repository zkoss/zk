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
	zWatch.listen({onShow: wgt});
	var v: string | undefined;
	if (v = wgt._upload)
		wgt._uplder = new zul.Upload(wgt, wgt._getUploadRef(), v);
}

function _cleanUpld(wgt: zul.menu.Menuitem): void {
	var v: zul.Upload | null | undefined;
	if (v = wgt._uplder) {
		zWatch.unlisten({onShow: wgt});
		wgt._uplder = null;
		v.destroy();
	}
}

/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 *
 * <p>Default {@link #getZclass}: z-menuitem.
 */
@zk.WrapClass('zul.menu.Menuitem')
export class Menuitem extends zul.LabelImageWidget implements zul.LabelImageWidgetWithAutodisable {
	// Parent could be null as asserted in _doMouseEnter
	override parent!: zul.menu.Menupopup | null;
	override nextSibling!: zul.menu.Menuitem | null;
	override previousSibling!: zul.menu.Menuitem | null;
	_value = '';
	_upload?: string;
	_checkmark?: boolean;
	_checked?: boolean;
	_autocheck?: boolean;
	_target?: string;
	_topmost?: boolean | null;
	_col?: string;
	_href?: string;
	_disabled?: boolean;
	_adbs?: boolean;
	_autodisable?: string;

	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 * @return boolean
	 */
	isCheckmark(): boolean | undefined {
		return this._checkmark;
	}

	/** Sets whether the check mark shall be displayed in front
	 * of each item.
	 * @param boolean checkmark
	 */
	setCheckmark(checkmark: boolean, opts?: Record<string, boolean>): this {
		const o = this._checkmark;
		this._checkmark = checkmark;

		if (o !== checkmark || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	setDisabled(v: boolean | undefined, opts?: Record<string, boolean>): this {
		const o = this._disabled;

		//B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
		if (opts && opts.adbs)
			// called from zul.wgt.ADBS.autodisable
			this._adbs = true;	// Start autodisabling
		else if (!opts || opts.adbs === undefined)
			// called somewhere else (including server-side)
			this._adbs = false;	// Stop autodisabling
		if (!v) {
			if (this._adbs)
			// autodisable is still active, enable allowed
			this._adbs = false;
			else if (opts && opts.adbs === false)
			// ignore re-enable by autodisable mechanism
			v = this._disabled;
		}
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			this.rerender(opts && opts.skip ? -1 : 0); //bind and unbind
		}

		return this;
	}

	/** Returns the href.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick handler.
	 * @return String
	 */
	getHref(): string | undefined {
		return this._href;
	}

	/** Sets the href.
	 * @param String href
	 */
	setHref(href: string, opts?: Record<string, boolean>): this {
		const o = this._href;
		this._href = href;

		if (o !== href || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the value.
	 * <p>Default: "".
	 * @return String
	 */
	getValue(): string {
		return this._value;
	}

	/** Sets the value.
	 * @param String value
	 */
	setValue(value: string): this {
		this._value = value;
		return this;
	}

	/** Returns whether it is checked.
	 * <p>Default: false.
	 * @return boolean
	 */
	isChecked(): boolean | undefined {
		return this._checked;
	}

	/** Sets whether it is checked.
	 * <p> This only applies when {@link #isCheckmark()} = true.
	 * @param boolean checked
	 */
	setChecked(checked: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = checked;

		if (o !== checked || (opts && opts.force)) {
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

	/** Returns whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p>Default: false.
	 * @return boolean
	 */
	isAutocheck(): boolean | undefined {
		return this._autocheck;
	}

	/** Sets whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p> This only applies when {@link #isCheckmark()} = true.
	 * @param boolean autocheck
	 */
	setAutocheck(autocheck: boolean): this {
		this._autocheck = autocheck;
		return this;
	}

	/** Returns the target frame or window.
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 * <p>Default: null.
	 * @return String
	 */
	getTarget(): string | undefined {
		return this._target;
	}

	/** Sets the target frame or window.
	 * @param String target the name of the frame or window to hyperlink.
	 */
	setTarget(target: string, opts?: Record<string, boolean>): this {
		const o = this._target;
		this._target = target;

		if (o !== target || (opts && opts.force)) {
			var anc = this.$n<HTMLAnchorElement>('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode as HTMLAnchorElement;
				anc.target = this._target;
			}
		}

		return this;
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 *
	 * <p>To represent the menuitem itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;menuitem id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;menuitem id="ok" autodisable="ok,cancel"/></code>
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
	 * <code>&lt;menuitem id="ok" autodisable="+self,+cancel"/></code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 *</code></pre>
		*
		* <p>Default: null.
		* @since 5.0.7
		* @return String
		*/
	getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/** Sets whether to disable the button after the user clicks it.
	 * @since 5.0.7
	 * @param String autodisable
	 */
	setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	/** Returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @return String
	 */
	getUpload(): string | undefined {
		return this._upload;
	}

	/** Sets the JavaScript class at the client to handle the upload if this
	 * button is used for file upload.
	 * <p>Default: null.
	 *
	 * @param String upload a JavaScript class to handle the file upload
	 * at the client, or "true" if the default class is used,
	 * or null or "false" to disable the file download (and then
	 * this button behaves like a normal button).
	 */
	setUpload(v: string, opts?: Record<string, boolean>): this {
		const o = this._upload;
		this._upload = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				_cleanUpld(this);
				if (v && v != 'false') _initUpld(this);
			}
		}

		return this;
	}

	/**
	 * Returns the file(s) belongs to this button if any.
	 * @since 10.0.0
	 */
	getFile(): FileList | null | undefined {
		return this._uplder?.getFile();
	}

	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 * @return boolean
	 */
	isTopmost(): boolean | null | undefined {
		return this._topmost;
	}

	override beforeParentChanged_(newParent: zk.Widget | null): void {
		this._topmost = newParent && !(newParent instanceof zul.menu.Menupopup);
		super.beforeParentChanged_(newParent);
	}

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

	/** Returns the {@link Menubar} that contains this menuitem, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar(): zul.menu.Menubar | null {
		for (var p: zk.Widget | null = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menubar)
				return p;
		return null;
	}

	_getRootMenu(): zul.menu.Menu | null {
		for (var p: zk.Widget | null = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menu && p.isTopmost())
				return p;
		return null;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
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

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
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

	override doClick_(evt: zk.Event): void {
		if (this._disabled)
			evt.stop();
		else {
			if (!this._canActivate(evt)) return;
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.ie || zk.ie > 10) {// ZK-2471
				if (!zk.chrome || (evt.domTarget as HTMLInputElement).type != 'file') //ZK-3089
					this._uplder!.openFileDialog();
			}

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
				var ifrm = jq.newFrame('mailtoFrame', anc.href, null);
				jq(ifrm).remove();
				evt.stop();
			} else {
				if (zk.ie < 11 && topmost && this.$n_().id != anc.id)
					zUtl.go(anc.href, {target: anc.target});
					// Bug #1886352 and #2154611
					//Note: we cannot eat onclick. or, <a> won't work

				if (zk.gecko && topmost && this.$n_().id != anc.id) {
					zUtl.go(anc.href, {target: anc.target});
					evt.stop();
					// Bug #2154611 we shall eat the onclick event, if it is FF3.
				}
			}
			if (!topmost) {
				var ref: zk.Widget | null | undefined = null;
				for (var p: zk.Widget | null = this.parent; p; p = p.parent) {
					if (p instanceof zul.menu.Menupopup) {
						// if close the popup before choosing a file, the file chooser can't be triggered.
						if (!p.isOpen() || this._uplder || p._keepOpen /*Bug #2911385 && !this._popup*/)
							break;
						this._updateHoverImage(); // remove hover image
						ref = p._fakeParent;
						p.close({sendOnOpen: true});
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

			var menubar: zul.menu.Menubar | null;
			if (zk.webkit && (menubar = this.getMenubar()) && menubar._autodrop)
				menubar._noFloatUp = true;
				//_noFloatUp used in Menu.js to fix Bug 1852304
			super.doClick_(evt, true);
		}
	}

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

	_canActivate(evt: zk.Event): boolean {
		return !this.isDisabled() && (zk.ie < 11 || !this.isTopmost() || !!this._uplder
				|| jq.isAncestor(this.$n('a'), evt.domTarget));
	}

	_getUploadRef(): HTMLAnchorElement | null | undefined {
		return this.$n('a');
	}

	_doMouseEnter(evt: MouseEvent): void {
		var isTopmost = this.isTopmost();
		if (zul.menu._nOpen || isTopmost)
			zWatch.fire('onFloatUp', this); //notify all
		if (!isTopmost && !this._disabled) {
			if (this.parent)
				this.parent.removeActive_();
			(this.$class as typeof Menuitem)._addActive(this);
		}
	}

	_doMouseLeave(evt: MouseEvent): void {
		var isTopmost = this.isTopmost();
		if (!isTopmost && !this._disabled) {
			(this.$class as typeof Menuitem)._rmActive(this);
		}
	}

	override deferRedrawHTML_(out: string[]): void {
		var tag = this.isTopmost() ? 'td' : 'li';
		out.push('<', tag, this.domAttrs_({domClass: true}), ' class="z-renderdefer"></', tag, '>');
	}

	//@Override
	override getImageNode(): HTMLImageElement | null | undefined {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n)
				this._eimg = this.$n_('a').firstChild as HTMLImageElement | null;
		}
		return this._eimg;
	}

	// internal use only.
	getAnchor_(): HTMLAnchorElement | null | undefined {
		return this.$n('a');
	}

	override focus_(timeout?: number): boolean {
		if (zk(this.getAnchor_()).focus(timeout)) {
			return true;
		}
		return super.focus_(timeout);
	}

	static _isActive(wgt: zk.Widget): boolean {
		return jq(wgt.$n_()).hasClass(wgt.$s('hover'));
	}

	static _addActive(wgt: zul.menu.Menuitem): void {
		var top = wgt.isTopmost();
		jq(wgt.$n_()).addClass(wgt.$s('hover'));
		if (!top) {
			// FIXME: See `zul.menu.Menu.prototype._addActive`, but there is an
			// additional point here.
			// 3. Shouldn't this function take a parameter of type Menuitem?
			//    However, `parentMenupopup.parent` seems to never be a Menuitem.
			var parentMenupopup = wgt.parent;
			if (parentMenupopup)
				parentMenupopup.addActive_(wgt);
			if (parentMenupopup!.parent instanceof zul.menu.Menu)
				this._addActive(parentMenupopup!.parent as unknown as zul.menu.Menuitem);
		}
	}

	static _rmActive(wgt: zk.Widget): JQuery {
		return jq(wgt.$n_()).removeClass(wgt.$s('hover'));
	}

	onShow(): void {
		if (this._uplder)
			this._uplder.sync();
	}
}