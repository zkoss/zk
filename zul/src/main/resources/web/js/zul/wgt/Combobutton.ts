/* Combobutton.ts

	Purpose:

	Description:

	History:
		Wed May 18 17:32:15     2011, Created by benbai

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
//called when user mouseout some element,
//for prevent duplicate state change
function _setCloseTimer(wgt: zul.wgt.Combobutton): void {
	if (!wgt._tidclose)
		wgt._tidclose = setTimeout(function () {
			if (!wgt._bover) {
				if (wgt._autodrop && wgt.isOpen())
					wgt.close({sendOnOpen: true});
			}
			wgt._tidclose = undefined;
		}, 200);
}

function _fireOnOpen(wgt: zul.wgt.Combobutton, opts: zul.wgt.PopupOptions, o: boolean): void {
	jq(wgt.$n_()).toggleClass(wgt.$s('open'), o);
	if (opts && opts.sendOnOpen)
		wgt.fire('onOpen', {open: o, value: wgt.getLabel()}, {rtags: {onOpen: 1}});
}
// called by open method
function _attachPopup(wgt: zul.wgt.Combobutton, bListen: boolean): void {
	const pp = wgt.firstChild;
	// just attach if not attached
	if (!wgt._oldppclose && pp) {
		const $pp = jq(pp),
			wd = jq(wgt).width()!;
		if ($pp.width()! < wd) {
			$pp.width(wd - zk(pp).padBorderWidth());

			// If the width of combobutton is larger than that of its popup, resize the popup and its children.
			zWatch.fireDown('onSize', pp);
			const openInfo = pp._openInfo;
			if (openInfo) {
				pp.position(...openInfo);
				// B50-ZK-391
				// should keep openInfo, maybe used in onResponse later.
			}
		}
		wgt._oldppclose = pp.close;
		// listen to onmouseover and onmouseout events of popup child
		if (bListen)
			wgt.domListen_(pp.$n_(), 'onMouseOver')
				.domListen_(pp.$n_(), 'onMouseOut');

		// override close function of popup widget for clear objects
		pp.close = function (opts: zul.wgt.PopupOptions) {
			wgt._oldppclose!.call(pp, opts);
			_fireOnOpen(wgt, opts, false);

			if (bListen)
				wgt.domUnlisten_(pp.$n_(), 'onMouseOver')
					.domUnlisten_(pp.$n_(), 'onMouseOut');
			pp.close = wgt._oldppclose!;
			delete wgt._oldppclose;
		};
	}
}

/**
 * A combo button. A combo button consists of a button ({@link zul.wgt.Combobutton}) and
 * a popup window ({@link zul.wgt.Popup}).
 * It is similar to {@link zul.inp.Bandbox} except the input box is substituted by a button.
 * @since 6.0.0
 * <p>Default {@link #getZclass}: z-combobutton.
 */
@zk.WrapClass('zul.wgt.Combobutton')
export class Combobutton extends zul.wgt.Button {
	override firstChild!: zul.wgt.Popup | undefined;
	override lastChild!: zul.wgt.Popup | undefined;

	_autodrop?: boolean;
	_bover?: boolean;
	_tidclose?: number;
	_oldppclose?(opts?: zul.wgt.PopupOptions): void;

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: false.
	 * @return boolean
	 */
	isAutodrop(): boolean {
		return !!this._autodrop;
	}

	/** Sets whether to automatically drop the list if users is changing
	 * this text box.
	 * @param boolean autodrop
	 */
	setAutodrop(autodrop: boolean): this {
		this._autodrop = autodrop;
		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	override isDisabled(): boolean {
		return !!this._disabled;
	}

	/** Sets whether it is disabled.
	 * If disabled is true, user cannot tab into the element.
	 * @param boolean disabled
	 */
	override setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				jq(n).attr('disabled', disabled ? 'disabled' : null);
				jq(n).attr('tabindex', disabled ? null : this._tabindex! | 0);
			}
		}

		return this;
	}

	override getZclass(): string {
		return 'z-combobutton';
	}

	override domContent_(): string {
		var label = '<span id="' + this.uuid + '-txt" class="' + this.$s('text') + '">'
			+ zUtl.encodeXML(this.getLabel()) + '</span>',
			img = this.domImage_(),
			iconSclass = this.domIcon_();
		if (!img && !iconSclass) return label;

		if (!img) img = iconSclass;
		else
			img += (iconSclass ? ' ' + iconSclass : '');
		var space = 'vertical' == this.getOrient() ? '<br/>' : ' ';
		return this.getDir() == 'reverse' ?
			label + space + img : img + space + label;
	}

	override domImage_(): string {
		var img = this._image;
		return img ? '<img class="' + this.$s('image') + '" src="' + img + '" alt="" aria-hidden="true">' : '';
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no);
		if (!this._isDefault())
			cls += ' z-combobutton-toolbar';
		return cls;
	}

	_isDefault(): boolean {
		return this._mold == 'default';
	}

	/** Returns whether the list of combo items is open
	 * @return boolean
	 */
	isOpen(): boolean {
		var pp = this.firstChild;
		return !!pp && pp.isOpen();
	}

	/** Drops down or closes the child popup ({@link zul.wgt.Popup})
	 * ({@link zul.menu.Menupopup}, and fire onOpen if it is called with an Event.
	 * @param boolean open
	 * @param Map opts
	 * 	if opts.sendOnOpen exists, it will fire onOpen event.
	 * @see #open
	 * @see #close
	 */
	setOpen(open: boolean, opts?: zul.wgt.PopupOptions): this {
		if (!this._disabled && !zk.animating())
			// have to provide empty opts or menupopup will set sendOnOpen to true
			this[open ? 'open' : 'close'](opts || {});
		return this;
	}

	renderInner_(out: string[]): void {
		for (var w: zk.Widget | undefined = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	}

	isTableLayout_(): boolean {
		return true;
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var pp = this.firstChild?.$n();
		// ZK-983
		if (pp)
			this.domUnlisten_(pp, 'onMouseOver')
				.domUnlisten_(pp, 'onMouseOut');
		super.unbind_(skipper, after, keepRod);
	}

	override doFocus_(evt: zk.Event): void {
		if (this == evt.target)
			// not change style if mouse down in popup node
			super.doFocus_(evt);
	}

	/** Open the dropdown widget of the Combobutton.
	 */
	open(opts: zul.wgt.PopupOptions): void {
		var pp = this.firstChild;
		if (pp && !this.isOpen()) {
			if (pp instanceof zul.wgt.Popup) {
				pp.open(this.uuid, undefined, 'after_start', opts);
				_fireOnOpen(this, opts, true);
			}
			_attachPopup(this, !(zul.menu && pp instanceof zul.menu.Menupopup));
		}
	}

	/** Close the dropdown widget of the Combobutton.
	 */
	close(opts?: zul.wgt.PopupOptions): void {
		if (this.isOpen())
			this.firstChild!.close(opts);
	}

	override doClick_(evt: zk.Event): void {
		var d = evt.domTarget;
		// click will fired twice, one with dom target, another with undefined,
		// see _fixClick in Button.js
		if (d) {
			// open it if click on right side,
			// close it if click on both left and right side
			var open = !this.isOpen();
			if (this == evt.target) {
				if (this.$n('btn') == d || this.$n('icon') == d || !open)
					this.setOpen(open, {sendOnOpen: true});
				else
					super.doClick_(evt);
			}
		}
	}

	override doMouseDown_(evt: zk.Event): void {
		if (this == evt.target)
			// not change style if mouse down in popup node
			super.doMouseDown_(evt);
	}

	override doMouseOver_(evt: zk.Event): void {
		this._bover = true;
		if (this == evt.target) {
			var d = evt.domTarget;
			// not change style and call open method if mouse over popup node
			if (this._autodrop && (this.$n('btn') == d || this.$n('icon') == d) && !this.isOpen())
				this.open({sendOnOpen: true});
			super.doMouseOver_(evt);
		}
	}

	override doMouseOut_(evt: zk.Event): void {
		this._bover = false;
		_setCloseTimer(this);
		super.doMouseOut_(evt);
	}

	_doMouseOver(evt: zk.Event): void { //not zk.Widget.doMouseOver_
		// should not close popup if mouse out combobutton but over popup
		this._bover = true;
	}

	_doMouseOut(evt: zk.Event): void { //not zk.Widget.doMouseOut_
		// should close it if mouse out popup
		this._bover = false;
		_setCloseTimer(this);
	}

	override doKeyDown_(evt: zk.Event): void {
		this._doKeyDown(evt);
		if (!evt.stopped)
			super.doKeyDown_(evt);
	}

	_doKeyDown(evt: zk.Event): false | undefined { //support enter,space, arrow down, and escape
		if (this.isDisabled()) {
			return false;
		}
		var keyCode = evt.keyCode,
			bOpen = this.isOpen();
		if (keyCode == 40 && !bOpen)
			this.open({sendOnOpen: true});
		else if (keyCode == 13 || keyCode == 32) {
			this.fire('onClick');
		} else if (keyCode == 27 && bOpen)
			this.close();
	}

	override focus_(timeout: number): boolean { //support focus
		if (this.isDisabled())
			return false;
		if (!zk.focusBackFix || !this._upload) {
			var self = this,
				n = this.$n_();
			zk.afterAnimate(function () {
				try {
					n.focus();
					zk.currentFocus = self;
					zjq.fixInput(n);
				} catch (e) {
					zk.debugLog((e as Error).message || e as string);
				}
			}, timeout);
		}
		return true;
	}

	override ignoreDescendantFloatUp_(des: zk.Widget): boolean {
		return des && des instanceof zul.wgt.Popup;
	}

	// B60-ZK-1216
	// Combobutton has problems with label-change if its popup did not close beforehand
	// Override rerender should also work for the case of image-change
	override rerender(skipper?: zk.Skipper | number): void {
		if (this.isOpen()) {
			this.close();
		}
		super.rerender(skipper);
	}
}