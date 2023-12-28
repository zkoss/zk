/* ComboWidget.ts

	Purpose:

	Description:

	History:
		Tue Mar 31 14:15:39     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

export type PopupSize = [width: string, height: string];
export interface ResponseOptions {
	rtags: { // see zk/evt/EventOptions
		onOpen?: unknown;
		onChanging?: unknown;
	};
}
export interface OpenOptions {
	focus?: boolean;
	sendOnOpen?: boolean;
}

/**
 * A skeletal implementation for a combo widget.
 */
@zk.WrapClass('zul.inp.ComboWidget')
export class ComboWidget extends zul.inp.InputWidget<string> {
	/** @internal */
	_buttonVisible = true;
	/** @internal */
	_iconSclass?: string;
	/** @internal */
	_autodrop?: boolean;
	/** @internal */
	_popupWidth?: string;
	/** @internal */
	_shallFixPopupDimension?: boolean;
	/** @internal */
	_repos?: boolean;
	/** @internal */
	_open?: boolean;
	/** @internal */
	_shallSyncPopupPosition?: boolean;
	/** @internal */
	_shadow?: zk.eff.Shadow;
	/** @internal */
	_windowX?: number;
	/** @internal */
	_windowY?: number;

	/**
	 * @returns whether the button (on the right of the textbox) is visible.
	 * @defaultValue `true`.
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	setButtonVisible(buttonVisible: boolean, opts?: Record<string, boolean>): this {
		const o = this._buttonVisible;
		this._buttonVisible = buttonVisible;

		if (o !== buttonVisible || opts?.force) {
			zul.inp.RoundUtl.buttonVisible(this, buttonVisible);
		}

		return this;
	}

	/**
	 * @returns whether to automatically drop the list if users is changing
	 * this text box.
	 * @defaultValue `false`.
	 */
	isAutodrop(): boolean {
		return !!this._autodrop;
	}

	/**
	 * Sets whether to automatically drop the list if users is changing
	 * this text box.
	 */
	setAutodrop(autodrop: boolean): this {
		this._autodrop = autodrop;
		return this;
	}

	/**
	 * @returns the width of the popup of this component.
	 * @since 8.0.3
	 */
	getPopupWidth(): string | undefined {
		return this._popupWidth;
	}

	/**
	 * Sets the width of the popup of this component
	 * If the input is a percentage, the popup width will be calculated by multiplying the width of this component with the percentage.
	 * (e.g. if the input string is 130%, and the width of this component is 300px, the popup width will be 390px = 300px * 130%)
	 * Others will be set directly.
	 * @param popupWidth - of the popup of this component
	 * @since 8.0.3
	 */
	setPopupWidth(popupWidth: string, opts?: Record<string, boolean>): this {
		const o = this._popupWidth;
		this._popupWidth = popupWidth;

		if (o !== popupWidth || opts?.force) {
			if (this._open) {
				var pp = this.getPopupNode_(),
					pp2 = this.getPopupNode_(true);
				if (!pp) return this;

				var ppofs = this._getPopupSize(pp, pp2);
				this._fixsz(ppofs);
				this._checkPopupSpaceAndPosition(pp, this.$n_());
				this._fixFfWhileBothScrollbar(pp, pp2);
			}
		}

		return this;
	}

	/**
	 * @returns the type.
	 * @defaultValue text.
	 */
	override getType(): string {
		return this._type;
	}

	/**
	 * Sets the type.
	 * @param type - the type. Acceptable values are "text" and "password".
	 * Unlike XUL, "timed" is redudant because it is enabled as long as
	 * onChanging is added.
	 * @since 8.5.0
	 */
	setType(type: string, opts?: Record<string, boolean>): this {
		const o = this._type;
		this._type = type;

		if (o !== type || opts?.force) {
			var inp = this.getInputNode();
			if (inp)
				inp.type = type;
		}

		return this;
	}

	/**
	 * @returns the iconSclass name of this ComboWidget.
	 */
	getIconSclass(): string | undefined {
		return this._iconSclass;
	}

	/**
	 * Sets the iconSclass name of this ComboWidget.
	 * @since 8.6.2
	 */
	setIconSclass(iconSclass: string, opts?: Record<string, boolean>): this {
		const o = this._iconSclass;
		this._iconSclass = iconSclass;

		if (o !== iconSclass || opts?.force) {
			var icon = this.$n('icon');
			if (this.desktop && icon)
				icon.className = (this.$s('icon') + ' ' + iconSclass);
		}

		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.desktop) {
			this.onSize();
		}
		return this;
	}

	/**
	 * For internal use only.
	 * Update the value of the input element in this component
	 */
	setRepos(repos: boolean): this {
		if (!this._repos && repos) {
			if (this.desktop) {
				this._shallFixPopupDimension = true;
			}
			this._repos = false;
		}
		return this;
	}

	override onSize(): void {
		if (this._open) {
			var pp = this.getPopupNode_();
			if (pp)
				this._checkPopupSpaceAndPosition(pp, this.$n_());
		}
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if ((!this._inplace && !this.isOpen()) || jq(this.getPopupNode_()).is(':animated'))
			return;
		var wgt = ctl.origin;
		if (!zUtl.isAncestor(this, wgt)) {
			if (this.isOpen())
				this.close({sendOnOpen: true});
			if (this._inplace) {
				var n = this.$n()!,
					inplace = this.getInplaceCSS();

				if (jq(n).hasClass(inplace)) return;

				n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
				jq(this.getInputNode()).addClass(inplace);
				jq(n).addClass(inplace);
				this.onSize();
				n.style.width = this.getWidth() ?? '';
			}
		}
	}

	onResponse(ctl: zk.ZWatchController, opts: ResponseOptions): void {
		if ((this._shallFixPopupDimension || opts.rtags.onOpen || opts.rtags.onChanging) && this.isOpen()) {
			// ZK-2192: Only need to determine if popup is animating
			if (jq(this.getPopupNode_()).is(':animated')) {
				setTimeout(() => {if (this.desktop) this.onResponse(ctl, opts);}, 50);
				return;
			}
			this.fixPopupDimension_();
		}
	}

	/** @internal */
	fixPopupDimension_(): void {
		this._shallFixPopupDimension = false;
		var pp = this.getPopupNode_()!,
			pz = this.getPopupSize_(pp),
			scrollPos: {left?: number; Top?: number} = {}; // Bug ZK-2294
		try {
			scrollPos.left = pp.scrollLeft;
			scrollPos.Top = pp.scrollTop;
			pp.style.height = 'auto'; // ZK-2086: BandBox popup invalid render if ON_OPEN event listener is attached
			this._fixsz(pz);
		} finally {
			// Bug ZK-2294, restore the scroll position
			pp.scrollTop = scrollPos.Top ?? 0;
			pp.scrollLeft = scrollPos.left ?? 0;
		}
	}

	onScroll(wgt?: zk.Widget): void {
		if (this.isOpen()) {
			// ZK-1552: fix the position of popup when scroll
			if (wgt) {
				var inp = this.getInputNode();
				// ZK-2211: should close when the input is out of view
				if (inp && zul.inp.InputWidget._isInView(this))
					zk(this.getPopupNode_()).position(inp, 'after_start');
				else
					this.close({sendOnOpen: true});
			}
		}
	}

	/**
	 * Drops down or closes the list of combo items ({@link Comboitem}.
	 * @param opts - the options.
	 * @see {@link open}
	 * @see {@link close}
	 */
	setOpen(open: boolean, opts?: Record<string, unknown>): this {
		if (this.desktop) {
			if (this.isRealVisible()) {
				if (open) this.open(opts);
				else this.close(opts);
			}
		} else {
			zk.afterMount(() => this.setOpen(open, opts));
		}
		return this;
	}

	/**
	 * @returns whether the list of combo items is open
	 */
	isOpen(): boolean {
		return !!this._open;
	}

	/**
	 * Drops down the list of combo items ({@link Comboitem}.
	 * It is the same as setOpen(true).
	 * @param opts - the options.
	 */
	open(opts?: OpenOptions): void {
		if (this._open) return;
		if (this._inplace) this._inplaceIgnore = true;
		this._open = true;
		if (opts && opts.focus)
			this.focus();

		var pp = this.getPopupNode_(),
			inp = this.getInputNode(),
			pp2 = this.getPopupNode_(true);
		if (!pp) return;

		this.setFloating_(true, {node: pp});
		zWatch.fire('onFloatUp', this); //notify all
		var topZIndex = this.setTopmost(),
			/*safe*/ sclass = this.getSclass();
		pp.className = this.$s('popup') + (sclass ? ' ' + sclass : ''); // ZK-4234: updated sclass on combobox doesn't update popup

		pp.style.zIndex = (topZIndex > 0 ? topZIndex : 1) as unknown as string; //on-top of everything
		pp.style.position = 'absolute'; //just in case
		pp.style.display = 'block';

		var ppofs = this._getPopupSize(pp, pp2);
		// throw out
		pp.style.visibility = 'hidden';
		pp.style.left = '-10000px';

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		//NOTE: since the parent/child relation is changed, new listitem
		//must be inserted into the popup (by use of uuid!child) rather
		//than invalidate!!
		var $pp = zk(pp);
		$pp.makeVParent();
		zWatch.fireDown('onVParent', this);

		this._fixsz(ppofs);

		// throw in
		pp.style.left = '';

		var n = this.$n()!,
			jqn = jq(n), jqpp = jq(pp);

		this._checkPopupSpaceAndPosition(pp, n);
		this._shallSyncPopupPosition = false;

		var pptop = jqpp.offset()!.top;

		pp.style.display = 'none';
		pp.style.visibility = '';
		
		if (jqn.offset()!.top > pptop)
			this.slideDown_(pp, 'b');
		else
			this.slideDown_(pp);

		this._fixFfWhileBothScrollbar(pp, pp2);

		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(pp,
				{left: -4, right: 4, top: -2, bottom: 3});

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open: true, value: inp!.value}, {rtags: {onOpen: 1}});

		//add extra CSS class for easy customize
		var openClass = this.$s('open');
		jqn.addClass(openClass);
		jqpp.addClass(openClass);
	}

	/** @internal */
	_getPopupSize(pp: HTMLElement, pp2?: HTMLElement): PopupSize {
		var ppofs = this.getPopupSize_(pp);
		pp.style.width = ppofs[0];
		pp.style.height = 'auto';

		if (pp2) pp2.style.width = pp2.style.height = 'auto';

		// B50-ZK-859: need to carry out min size here
		if (this.presize_())
			ppofs = this.getPopupSize_(pp);
		return ppofs;
	}

	/** @internal */
	_checkPopupSpaceAndPosition(pp: HTMLElement, inp: HTMLElement): void {
		//B80-ZK-3051
		//check the popup space before position()
		var $pp = zk(pp),
			ppHeight = $pp.dimension().height,
			ppWidth = $pp.dimension().width,
			inpDim = zk(inp).dimension(true),
			inpTop = inpDim.top,
			inpLeft = inpDim.left,
			inpHeight = inpDim.height,
			btn = this.$n('btn'),
			inpWidth = inpDim.width - (btn ? btn.offsetWidth : 0),
			screenX = jq.innerX(),
			screenY = jq.innerY(),
			screenHeight = jq.innerHeight(),
			screenWidth = jq.innerWidth(),
			hPosition = 'start',
			vPosition = 'after',
			opts: zk.PositionOptions | undefined;

		if (screenX + screenWidth - inpLeft - inpWidth > ppWidth) {
			hPosition = 'start';
		} else if (inpLeft - screenX > ppWidth) {
			hPosition = 'end';
		} else {
			opts = {overflow: true};
		}
		if (screenY + screenHeight - inpTop - inpHeight > ppHeight) {
			vPosition = 'after';
		} else if (inpTop - screenY > ppHeight) {
			vPosition = 'before';
		} else {
			opts = {overflow: true};
		}

		$pp.position(inp, vPosition + '_' + hPosition, opts);
	}

	/** @internal */
	_fixFfWhileBothScrollbar(pp: HTMLElement, pp2?: HTMLElement & Partial<Pick<HTMLTableElement, 'rows'>>): void {
		//FF issue:
		//If both horz and vert scrollbar are visible:
		//a row might be hidden by the horz bar.
		if (zk.gecko) {
			var rows = pp2 ? pp2.rows : undefined;
			if (rows) {
				var gap = pp.offsetHeight - pp.clientHeight;
				if (gap > 10 && pp.offsetHeight < 150) { //scrollbar
					var hgh = 0;
					for (var j = rows.length; j--;)
						hgh += rows[j].offsetHeight;
					pp.style.height = jq.px0((hgh + 20));
						//add the height of scrollbar (18 is an experimental number)
				}
			}
		}
	}

	/** @internal */
	_checkPopupPosition(): boolean {
		var pp = this.getPopupNode_(),
			$pp = zk(pp),
			inp = this.getInputNode(),
			ppDim = $pp.dimension(true),
			inpDim = zk(inp).dimension(true),
			ppBottom = ppDim.top + ppDim.height,
			ppRight = ppDim.left + ppDim.width,
			ppRelativeBottom = ppBottom - $pp.scrollOffset()[1], //minus scroll offset
			inpBottom = inpDim.top + inpDim.height,
			inpRight = inpDim.left + inpDim.width;

		if (ppRelativeBottom >= jq.innerHeight()
			|| (ppDim.top < inpDim.top && ppBottom < inpDim.top)
			|| ppDim.left < inpRight
			&& ppRight > inpDim.left
			&& ppBottom > inpDim.top
			&& ppDim.top < inpBottom
		) {
			return this._shallSyncPopupPosition = true;
		}
		return false;
	}

	/**
	 * Extra handling for min size of popup widget. Return true if size is affected.
	 * @internal
	 */
	presize_(): boolean {
		return false;
	}

	/**
	 * Slides down the drop-down list.
	 * @defaultValue `zk(pp).slideDown(this, {afterAnima: this._afterSlideDown});`
	 * @param pp - the DOM element of the drop-down list.
	 * @since 5.0.4
	 * @internal
	 */
	slideDown_(pp: HTMLElement, anchor?: string): void {
		zk(pp).slideDown(this, {afterAnima: this._afterSlideDown, duration: 100, anchor: anchor});
	}

	/**
	 * Slides up the drop-down list.
	 * @defaultValue `pp.style.display = "none";`<br/>
	 * In other words, it just hides it without any animation effect.
	 * @param pp - the DOM element of the drop-down list.
	 * @since 5.0.4
	 * @internal
	 */
	slideUp_(pp: HTMLElement): void {
		pp.style.display = 'none';
	}

	override zsync(opts?: zk.Object): void {
		super.zsync(opts);
		if (!zk.css3 && this.isOpen() && this._shadow)
			this._shadow.sync();
	}

	/** @internal */
	_afterSlideDown(n: HTMLElement): void {
		if (!this.desktop) {
			//Bug 3035847: close (called by unbind) won't remove popup when animating
			zk(n).undoVParent(); //no need to fire onVParent since it will be removed
			jq(n).remove();
		}
		if (this._shadow) this._shadow.sync();
	}

	/**
	 * @returns the DOM element of the popup.
	 * @defaultValue `inner ? this.$n("cave"): this.$n("pp")`.
	 * Override it if it is not the case.
	 * @param inner - whether to return the inner popup.
	 * ComboWidget assumes there is at least one popup and returned by
	 * `getPopupNode_()`, and there might be an inner DOM element
	 * returned by `getPopupNode_(true)`.
	 * @since 5.0.4
	 * @internal
	 */
	getPopupNode_(inner?: boolean): HTMLElement | undefined {
		return inner ? this.$n('cave') : this.$n('pp');
	}

	/**
	 * Closes the list of combo items ({@link Comboitem} if it was
	 * dropped down.
	 * It is the same as setOpen(false).
	 * @param opts - the options.
	 */
	close(opts?: Record<string, unknown>): void {
		if (!this._open) return;
		if (this._inplace) this._inplaceIgnore = false;
		var self = this;
		// ZK-2192: Only need to determine if popup is animating
		if (jq(this.getPopupNode_()).is(':animated')) {
			setTimeout(function () {if (self.desktop) self.close(opts);}, 50);
			return;
		}
		this._open = false;
		if (opts && opts.focus) {
			this.focus();
		}

		var pp = this.getPopupNode_();
		if (!pp) return;

		this.setFloating_(false);
		zWatch.fireDown('onHide', this);
		this.slideUp_(pp);

		zk.afterAnimate(function () {
			zk(pp).undoVParent();
			zWatch.fireDown('onVParent', self);
		}, -1);

		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = undefined;
		}

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open: false, value: this.getInputNode()!.value}, {rtags: {onOpen: 1}});

		//remove extra CSS class
		var openClass = this.$s('open');
		jq(this.$n()).removeClass(openClass);
		jq(pp).removeClass(openClass);
	}

	/** @internal */
	_fixsz(ppofs: PopupSize): void {
		var pp = this.getPopupNode_();
		if (!pp) return;

		var pp2 = this.getPopupNode_(true);
		if (ppofs[1] == 'auto' && pp.offsetHeight > 350) {
			pp.style.height = '350px';
		} else if (pp.offsetHeight < 10) {
			// B65-ZK-2021: Only need to manually sync shadow when there is no item matched.
			if (this._shadow)
				this._shadow.sync();
		}

		var cb = this.$n()!,
			i: string | undefined;
		if (i = this.getPopupWidth()) {
			if (i.endsWith('%')) {
				pp.style.width = jq.px0(cb.offsetWidth * parseFloat(i) / 100.0);
			} else {
				pp.style.width = i;
			}
			return;
		}

		if (ppofs[0] == 'auto') {
			if (pp.offsetWidth <= cb.offsetWidth) {
				pp.style.width = jq.px0(zk(pp).revisedWidth(cb.offsetWidth));
				if (pp2) pp2.style.width = '100%';
					//Note: we have to set width to auto and then 100%
					//Otherwise, the width is too wide in IE
			} else {
				var wd = jq.innerWidth() - 20;
				if (wd < cb.offsetWidth) wd = cb.offsetWidth;
				if (pp.offsetWidth > wd) pp.style.width = jq.px0(wd);
			}
		}
	}

	/** @internal */
	dnPressed_(evt: zk.Event): void {
		// empty on purpose
	}
	/** @internal */
	upPressed_(evt: zk.Event): void {
		// empty on purpose
	}
	/** @internal */
	otherPressed_(evt: zk.Event): void {
		// empty on purpose
	}

	/**
	 * Called when the user presses enter when this widget has the focus ({@link focus}).
	 * <p>call the close function
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link close}
	 * @internal
	 */
	enterPressed_(evt: zk.Event): void {
		this.close({sendOnOpen: true});
		this.updateChange_();
		evt.stop();
	}

	/**
	 * Called when the user presses escape key when this widget has the focus ({@link focus}).
	 * <p>call the close function
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link close}
	 * @internal
	 */
	escPressed_(evt: zk.Event): void {
		this.close({sendOnOpen: true});
		evt.stop();
	}

	/**
	 * @returns [width, height] for the popup if specified by user.
	 * @defaultValue ['auto', 'auto']
	 * @internal
	 */
	getPopupSize_(pp: HTMLElement): PopupSize {
		return ['auto', 'auto'];
	}

	/**
	 * Called by {@link redraw_} to redraw popup.
	 * @defaultValue does nothing
	 *  @param out - an array of HTML fragments.
	 * @internal
	 */
	redrawpp_(out: string[]): void {
		// empty on purpose
	}

	/** @internal */
	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n_()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	/** @internal */
	_dnInputOpen(): void {
		if (this._autodrop && !this._open)
			this.open({sendOnOpen: true});
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var btn: HTMLElement | undefined;

		if (btn = this.$n('btn')) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domListen_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		this.domListen_(this.$n('real')!, 'onInput', '_dnInputOpen');

		zWatch.listen({onSize: this, onFloatUp: this, onResponse: this, onScroll: this});
		if (!zk.css3) jq.onzsync(this);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this.close();

		var btn = this.$n('btn');
		if (btn) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domUnlisten_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.unlisten({onSize: this, onFloatUp: this, onResponse: this, onScroll: this});
		if (!zk.css3) jq.unzsync(this);

		this.domUnlisten_(this.$n('real')!, 'onInput', '_dnInputOpen');

		super.unbind_(skipper, after, keepRod);
	}

	override inRoundedMold(): boolean {
		return true;
	}

	/** @internal */
	_doBtnClick(evt: zk.Event): void {
		this._inplaceIgnore = false;
		if (!this._buttonVisible) return;
		// ZK-2192: Only need to determine if popup is animating
		if (!this._disabled && !jq(this.getPopupNode_()).is(':animated')) {
			if (this._open) this.close({focus: zul.inp.InputCtrl.isPreservedFocus(this), sendOnOpen: true});
			else this.open({focus: zul.inp.InputCtrl.isPreservedFocus(this), sendOnOpen: true});
		}
		if (zk.ios) { //Bug ZK-1313: keep window offset information before virtual keyboard opened on ipad
			this._windowX = window.pageXOffset;
			this._windowY = window.pageYOffset;
		}
		// Bug ZK-2544, B70-ZK-2849
		evt.stop((this._open ? {propagation: 1 as unknown as boolean} : undefined));
	}

	/** @internal */
	_doBtnMouseDown(evt: zk.Event): void {
		this._inplaceIgnore = true;
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		if (!this._disabled) {
			this._doKeyDown(evt);
			if (!evt.stopped)
				super.doKeyDown_(evt);
		}
	}

	/** @internal */
	override doKeyUp_(evt: zk.Event): void {
		this._updateValue();
		super.doKeyUp_(evt);
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (!this._disabled) {
			if (evt.domTarget == this.getPopupNode_())
				this.close({
					focus: zul.inp.InputCtrl.isPreservedFocus(this),
					sendOnOpen: true
				});
			else if (this._readonly && !this.isOpen() && this._buttonVisible)
				this.open({
					focus: zul.inp.InputCtrl.isPreservedFocus(this),
					sendOnOpen: true
				});
			super.doClick_(evt, popupOnly);
		}
	}

	/** @internal */
	_doKeyDown(evt: zk.Event): void {
		var keyCode = evt.keyCode,
			bOpen = this._open;
			// Bug ZK-475, ZK-3635
		if (evt.target == this && keyCode == 9) { //TAB or SHIFT-TAB
			if (bOpen)
				this.close({sendOnOpen: true});
			return;
		}

		if (evt.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (bOpen) this.close({sendOnOpen: true});
			else this.open({sendOnOpen: true});

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = {propagation: true};
			evt.stop(opts);
			return;
		}

		//Request 1537962: better responsive
		if (bOpen && (keyCode == 13 || keyCode == 27)) { //ENTER or ESC
			if (keyCode == 13) this.enterPressed_(evt);
			else this.escPressed_(evt);
			return;
		}

		if (keyCode == 38) this.upPressed_(evt);
		else if (keyCode == 40) this.dnPressed_(evt);
		else this.otherPressed_(evt);
	}

	/* B65-ZK-2021: Too many unnecessary shadow sync calls.
	onChildAdded_: _zkf = function (child) {
		if (this._shadow) this._shadow.sync();
	},
	onChildRemoved_: _zkf,
	onChildVisible_: _zkf,
	*/
	/**
	 * Utility to implement {@link redraw}.
	 * @param out - an array of HTML fragments.
	 * @internal
	 */
	redraw_(out: string[]): void {
		var uuidHTML = this.uuid,
			isButtonVisible = this._buttonVisible;

		out.push('<span', this.domAttrs_({text: true, tabindex: true}), ' role="combobox" aria-expanded="false" aria-owns="', uuidHTML, '-pp" aria-haspopup="dialog"><input id="',
			uuidHTML, '-real" class="', this.$s('input'));

		if (!isButtonVisible)
			out.push(' ', this.$s('input-full'));

		out.push('" autocomplete="off" aria-autocomplete="none" aria-controls="', uuidHTML, '-pp"',
			/*safe*/ this.textAttrs_(), '/><a id="', uuidHTML, '-btn" tabindex="-1" role="button" aria-label="', /*safe*/ msgzul.PANEL_EXPAND, '" class="',
			this.$s('button'));

		if (!isButtonVisible)
			out.push(' ', this.$s('disabled'));

		out.push('" aria-hidden="true"><i id="', /*safe*/ uuidHTML, '-icon" class="', this.$s('icon'), ' ', /*safe*/ this.getIconSclass()!, '"></i></a>');

		this.redrawpp_(out);

		out.push('</span>');
	}

	static $redraw = ComboWidget.prototype.redraw_;
}