/* Popup.ts

	Purpose:

	Description:

	History:
		Wed Dec 17 19:15:59     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

// zul.menu.Menupopup.prototype.open passes an HTMLAnchorElement
export type Ref = zk.Widget | HTMLElement | string;
export interface PopupOptions extends zk.PositionOptions {
	focusFirst?: boolean;
	sendOnOpen?: boolean;
	type?: string;
	disableMask?: boolean;
	which?: number; // See the usage of `Popup.prototype.open` in `zul.Widget`.
	keepVisible?: boolean;
}
// Offset can be null in zul.inp.Errorbox.prototype.getPositionArgs_
export type PositionArgs = [
	Ref | undefined,
	zk.Offset | undefined,
	string | undefined,
	PopupOptions | undefined
]
export interface PositionInfo {
	dim?: zk.Dimension;
	pos?: string;
}

/**
 * A container that is displayed as a popup.
 * The popup window does not have any special frame.
 * Popups can be displayed when an element is clicked by assigning
 * the id of the popup to either the {@link setPopup},
 * {@link setContext} or {@link setTooltip} attribute of the element.
 *
 * @defaultValue {@link getZclass}: z-popup.
 */
@zk.WrapClass('zul.wgt.Popup')
export class Popup extends zul.Widget {
	/** @internal */
	override _visible = false;
	/** @internal */
	_fakeParent?: zk.Widget;
	mask?: zk.eff.Mask;
	/** @internal */
	_openInfo?: PositionArgs;
	/** @internal */
	_adjustLeft?: number;
	/** @internal */
	_adjustTop?: number;
	/** @internal */
	_shallToggle?: boolean;
	/** @internal */
	_keepVisible?: boolean;
	/** @internal */
	_stackup?: HTMLIFrameElement;

	/**
	 * @returns whether the popup is visible.
	 */
	isOpen(): boolean {
		return this.isVisible();
	}

	// a delegator for open() in stateless.
	setOpen(open: PositionArgs): this {
		if (this.desktop || this.z_rod) {
			this.open(...open);
		} else {
			zk.afterMount(() => {
				if (this.desktop || this.z_rod) {// just in case if removed.
					this.open(...open);
				}
			});
		}
		return this;
	}

	// a delegator for close() in stateless.
	setClose(close: boolean): this {
		if (close != this.isOpen()) return this; // do nothing.
		if (this.desktop || this.z_rod) {
			this.close();
		} else {
			zk.afterMount(() => {
				if (this.desktop || this.z_rod) {
					this.close();
				}
			});
		}
		return this;
	}

	/**
	 * Opens the popup.
	 * <p>Note: the ref with the position parameter is prior to the offset parameter,
	 * if any.
	 * @param ref - the referred widget or its uuid.
	 * @param offset - the offset of x and y
	 * @param position - Possible values for the position attribute are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the popup appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the popup appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the popup appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the popup appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the popup appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the popup at the horizontal position of the mouse cursor.</li>
	 * </ul>
	 * @param opts - if opts.sendOnOpen exists, it will fire onOpen event. If opts.disableMask exists,
	 *  it will show a disable mask. If opts.overflow exists, it allows the popup to appear
	 *  out of the screen range. If opts.dodgeRef exists, it will avoid covering the reference
	 *  element.
	 */
	open(ref?: Ref, offset?: zk.Offset, position?: string, opts?: PopupOptions): void {
		this._fakeParent = zk.$(ref);
		this._fakeParent!.listen({'onUnbindNow': this.proxy(this._fakeParentOnUnbind)});
		var posInfo = this._posInfo(ref, offset, position),
			node = this.$n(),
			$n = jq(node);

		// the top is depend on children's height, if child will re-size after onSize/onShow,
		// popup need to re-position top after children height has calculated.
		// B50-ZK-391
		// should keep openInfo each time,
		// maybe have to reposition in onResponse if the child changed with onOpen event,
		if (arguments.length != 0) //do not update _openInfo when just call open()
			this._openInfo = arguments as unknown as PositionArgs;

		//F70-ZK-2007: Check if it is toggle type.
		this._shallToggle = opts && opts.type == 'toggle';

		$n.css({position: 'absolute'}).zk.makeVParent();

		// F70-ZK-2007: Fire to all the widgets that listen onVParent.
		zWatch.fire('onVParent', this);

		if (posInfo)
			$n.zk.position(posInfo.dim, posInfo.pos, opts);

		this.setFloating_(true); // B50-ZK-280: setFloating_ first
		this.setTopmost();
		this.openAnima_(ref, offset, position, opts);
	}

	/**
	 * The effect for opening the popup. Override this function to provide
	 * opening effect. afterOpenAnima_ needs to be called after the effect.
	 * @since 6.0.1
	 * @internal
	 */
	openAnima_(ref?: Ref, offset?: zk.Offset, position?: string, opts?: PopupOptions): void {
		this.afterOpenAnima_(ref, offset, position, opts);
	}

	/**
	 * The handling after the opening effect of popup.
	 * @since 6.0.1
	 * @internal
	 */
	afterOpenAnima_(ref?: Ref, _offset?: zk.Offset, _position?: string, opts?: PopupOptions): void {
		var node = this.$n(),
			sendOnOpen = opts && opts.sendOnOpen;
		// B85-ZK-3606: for adjusting popup position
		// B86-ZK-4030: because afterSize, in setVisible would use the this._adjustLeft property.
		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		this._adjustOffsets(ref);
		this.setVisible(true);
		if ((!opts || !opts.disableMask) && this.isListen('onOpen', {asapOnly: true})) {
			//Racing? Previous onResponse has not been fired and user triggers open again
			if (this.mask) this.mask.destroy();

			//ZK-2775, only trigger open() with doClick_ will set sendOnOpen to true
			if (sendOnOpen) {
				// use a progress bar to hide the popup
				this.mask = new zk.eff.Mask({
					id: this.uuid + '-mask',
					anchor: node
				});
				// register onResponse to remove the progress bar after receiving
				// the response from server.
				zWatch.listen({onResponse: this});
			}
		}

		// resync position if the content is not calculated. Bug ZK-2257
		const openInfo = this._openInfo;
		if (openInfo) {
			this.position(...openInfo);
			this._adjustOffsets(ref);
		}
		// B30-1819264 : should skip null
		if (this.shallStackup_() && node) {
			if (!this._stackup)
				this._stackup = jq.newStackup(node, node.id + '-stk');
			else {
				let dst: CSSStyleDeclaration, src: CSSStyleDeclaration;
				(dst = this._stackup.style).top = (src = node.style).top;
				dst.left = src.left;
				dst.zIndex = src.zIndex;
				dst.display = 'block';
			}
		}

		if (sendOnOpen) this.fire('onOpen', {open: true, reference: ref});
		//add extra CSS class for easy customize
		jq(node).addClass(this.$s('open'));
	}

	/** @internal */
	_adjustOffsets(ref?: zk.Widget): void {
		if (ref && ref.desktop && this.desktop) {
			var refDim = zk(ref).dimension(true),
				thisDim = zk(this).dimension(true);
			if (refDim && thisDim) {
				this._adjustLeft = thisDim.left - refDim.left;
				this._adjustTop = thisDim.top - refDim.top;
			}
			this._keepVisible = true;
		}
	}

	/**
	 * @returns whether to instantiate a stackup when {@link open}
	 * is called.
	 * <p>If the derive class created its own stackup (such as creating
	 * a shadow), it shall override this method to return false.
	 * @internal
	 */
	shallStackup_(): boolean {
		return zk.eff.shallStackup();
	}

	/**
	 * Sets the popup position.
	 * <p>Note: the ref with the position parameter is prior to the offset parameter,
	 * if any.
	 * @param ref - the referred widget.
	 * @param offset - the offset of x and y
	 * @param position - Possible values for the position attribute refer to {@link open}.
	 * @param opts - a map of addition options. Allowed values: refer to {@link zjq.position}.
	 */
	position(ref?: Ref, offset?: zk.Offset, position?: string, opts?: zk.PositionOptions): void {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zk(this.$n()).position(posInfo.dim, posInfo.pos, opts);
	}

	/**
	 * Reset the position on scroll
	 * @internal
	 */
	_onSyncScroll(evt: zk.ZWatchController): void {
		if (evt && (!this._fakeParent || zUtl.isAncestor(evt.origin, this._fakeParent))) {
			if (this.isInView_()) {
				const args = this.getPositionArgs_();
				if (!this.isOpen() && this._keepVisible) {
					this.open(...args);
				} else {
					this.position(...args);
				}
			} else if (this.isOpen()) {
				this.close({keepVisible: true});
			}
		}
	}

	/** @internal */
	_posInfo(ref?: Ref, offset?: zk.Offset, position?: string, _opts?: PopupOptions): PositionInfo | undefined {
		var pos: string | undefined,
			dim: zk.Dimension | undefined;

		if (position) {
			if (ref) {
				if (typeof ref == 'string')
					ref = zk.Widget.$(ref);

				if (ref) {
					var refn = (ref instanceof zul.Widget) ? ref.$n() as HTMLElement | undefined : ref;
					// B65-ZK-1934: Make sure refn is not null
					if (refn) {
						pos = position;
						dim = zk(refn).dimension(true);
					} else
						return {pos: position};
				}
			} else
				return {pos: position};
		} else if (Array.isArray(offset)) {
			dim = {
				left: zk.parseInt(offset[0]), top: zk.parseInt(offset[1]),
				width: 0, height: 0
			};
		}
		if (dim) {
			// we should include margin in this case for customizing theme. (since ZK 7.0.0)
			var $n = zk(this.$n());
			dim.top += $n.sumStyles('t', jq.margins);
			dim.left += $n.sumStyles('l', jq.margins);
			return {pos: pos, dim: dim};
		}
	}

	onResponse(): void {
		if (this.mask) this.mask.destroy();
		// B50-ZK-391: Tooltip loses "position=after_end" positioning if onOpen eventlistener added to popup
		var openInfo = this._openInfo;
		if (openInfo) {
			this.position(...openInfo);
		}
		zWatch.unlisten({onResponse: this});
		this.mask = undefined;
	}

	/**
	 * Closes this popup at the client.
	 *
	 * <p>In most cases, the popup is closed automatically when the user
	 * clicks outside of the popup.
	 * @param opts - if opts.sendOnOpen exists, it will fire onOpen event.
	 */
	close(opts?: PopupOptions): void {
		if (this._stackup)
			this._stackup.style.display = 'none';
		// F70-ZK-2007: Clear toggle type.
		this._shallToggle = false;

		try {
			//fix firefox, safari and ie issue
			if ((zk.ff || zk.safari) && zk.currentFocus) {
				 // Bug ZK-2922, check ancestor first.
				var n = zk.currentFocus.getInputNode ? zk.currentFocus.getInputNode() : zk.currentFocus.$n();
				if (jq.nodeName(n!, 'input')) {
					if ((zk.ff || zk.safari) && jq.isAncestor(this.$n(), n)) {
						jq(n).blur(); // trigger a missing blur event.
					}
				}
			}
		} catch (e) {
			zk.debugLog((e as Error).message || e as string);
		}

		this.closeAnima_(opts);  // Bug ZK-1124: should pass arguments to closeAnima_ function

		// remove visible flag
		if (!opts || !opts.keepVisible) {
			this._keepVisible = false;
			if (this._fakeParent) {
				this._fakeParent.unlisten({'onUnbindNow': this.proxy(this._fakeParentOnUnbind)});
			}
			this._fakeParent = undefined;
		}
	}

	/**
	 * The effect for closing the popup. Override this function to provide
	 * closing effect. afterCloseAnima_ needs to be called after the effect.
	 * @since 6.0.1
	 * @internal
	 */
	closeAnima_(opts?: PopupOptions): void {
		this.afterCloseAnima_(opts);
	}

	/**
	 * The handling after the closing effect of popup.
	 * @since 6.0.1
	 * @internal
	 */
	afterCloseAnima_(opts?: PopupOptions): void {
		this.setVisible(false);

		var node = this.$n()!;
		zk(node).undoVParent();
		zWatch.fireDown('onVParent', this);

		this.setFloating_(false);
		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open: false});
		//remove extra CSS class
		jq(node).removeClass(this.$s('open'));
	}

	onFloatUp(ctl: zk.ZWatchController, opts?: zk.FireOptions): void {
		this._keepVisible = false;
		if (!this.isVisible())
			return;
		var openInfo = this._openInfo;

		// F70-ZK-2007: If popup belongs to widget's ascendant then return.
		if (this._shallToggle && openInfo && opts && (
				opts.triggerByClick === undefined || (
				openInfo[3]!.which == opts.triggerByClick && zUtl.isAncestor(openInfo[0] as zk.Widget, ctl.origin)))) {
			return;
		}
		this._doFloatUp(ctl);
	}
	/** @internal */
	_fakeParentOnUnbind(): void {
		if (this._fakeParent) {
			this._fakeParent.unlisten({'onUnbindNow': this.proxy(this._fakeParentOnUnbind)});
		}
		this._fakeParent = undefined;
	}
	/** @internal */
	_doFloatUp(ctl: zk.ZWatchController): void {
		if (!this.isVisible())
			return;
		var wgt: zk.Widget | undefined = ctl.origin as zk.Widget | undefined;
		for (var floatFound = false; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			if (wgt == this.parent && wgt.ignoreDescendantFloatUp_(this))
				return;
			floatFound = floatFound || wgt.isFloating_();
		}
		this.close({sendOnOpen: true});
	}

	// ZK-2990: should also change the zIndex of the stackup of the widget
	/** @internal */
	override setFloatZIndex_(floatZIndex: HTMLElement, zi: number): void {
		super.setFloatZIndex_(floatZIndex, zi);
		if (this._stackup) {
			this._stackup.style.zIndex = zi as unknown as string;
		}
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onFloatUp: this, onShow: this, afterSize: this, _onSyncScroll: this});
		this.setFloating_(true);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zk(this.$n()).undoVParent(); //Bug 3079480
		if (this._stackup) {
			jq(this._stackup).remove();
			this._stackup = undefined;
		}
		if (this._openInfo)
			this._openInfo = undefined;
		this._shallToggle = undefined;
		zWatch.unlisten({onFloatUp: this, onShow: this, afterSize: this, _onSyncScroll: this});
		this.setFloating_(false);
		super.unbind_(skipper, after, keepRod);
	}

	afterSize(): void {
		this.reposition();
	}

	/**
	 * Reposition popup
	 * @since 8.0.3
	 */
	reposition(): void {
		if (this._fakeParent) {
			// B85-ZK-3606: reposition based on the current position of the item
			this.position(...this.getPositionArgs_());
		} else {
			var openInfo = this._openInfo;
			//once opened
			if (openInfo) {
				//openInfo: ref, offset, position, opts
				var posInfo = this._posInfo(openInfo[0], openInfo[1], openInfo[2]);
				if (posInfo)
					jq(this.$n()).zk.position(posInfo.dim, posInfo.pos, openInfo[3]);
			}
		}
	}

	onShow(ctl: zk.ZWatchController): void {
		//bug 3034505: call children's onShow to calculate the height first
		ctl.fire(this.firstChild);
		zk(this).redoCSS(-1, {'fixFontIcon': true});
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop)
			zUtl.fireShown(this);
		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.desktop)
			zWatch.fireDown('onShow', this);
		return this;
	}

	/** @internal */
	prologHTML_(_out: string[]): void {
		// empty on purpose
	}

	/** @internal */
	epilogHTML_(_out: string[]): void {
		// empty on purpose
	}

	/** @internal */
	isInView_(): boolean {
		return this._fakeParent ? zk(this._fakeParent).isRealScrollIntoView(true) : false;
	}

	/** @internal */
	getPositionArgs_(): PositionArgs {
		var p = this._fakeParent, dim = zk(p).dimension(true);
		return [p, [dim.left + this._adjustLeft!, dim.top + this._adjustTop!], undefined, {dodgeRef: false}];
	}
}