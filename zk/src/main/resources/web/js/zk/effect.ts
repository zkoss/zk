/* effect.ts

	Purpose:

	Description:

	History:
		Mon Nov 10 14:45:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us effects.js v1.7.0
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
let _defSKUOpts: { stackup: boolean } | undefined,
	_useSKU;
/** The effects, such as mask and shadow.
 */
//zk.$package('zk.eff');
export namespace eff {
	export interface EffectStackupOptions {
		stackup?: boolean;
		left?: number; // zul/inp/ComboWidget
		right?: number; // zul/inp/ComboWidget
		top?: number; // zul/inp/ComboWidget
		bottom?: number; // zul/inp/ComboWidget
	}

	export interface EffectFullMaskOptions extends EffectStackupOptions {
		mask?: HTMLElement;
		anchor?: HTMLElement;
		id?: string;
		zIndex?: number;
		visible?: boolean;
	}

	export interface EffectMaskOptions extends EffectStackupOptions {
		id?: string;
		anchor?: HTMLElement;
		message?: string;
		offset?: [number, number];
		width?: number;
		height?: number;
	}

	export type EffectActions = typeof eff.Actions;

	export interface Effect {
		destroy(): void;
		hide?(): void;
		sync?(): void;
	}

	export function shallStackup(): boolean {
		return !!_useSKU;
	}
	/** @internal */
	export function _skuOpts<T extends EffectStackupOptions>(opts?: EffectStackupOptions): T {
		return zk.$default(opts, _defSKUOpts ??= { stackup: eff.shallStackup() }) as T;
	}
	// ZK-1904: stackup should be moved from wgt to document.body
	/** @internal */
	export function _onVParent(evt: zk.Event, opts: {shadow?: {node: HTMLElement; stackup: HTMLElement}}): void {
		var sdw: undefined | {node: HTMLElement; stackup: HTMLElement}, stackup;
		if (opts && (sdw = opts.shadow) && (stackup = sdw.stackup)) {
			var $stk = jq(stackup);
			if ($stk.parent()[0] != document.body)
				$stk.insertBefore(sdw.node);
		}
	}
	/** The shadow effect.
	*/
	export class Shadow extends zk.Object implements Effect {
		declare wgt?: zk.Widget;
		declare node?: HTMLElement;
		declare stackup?: HTMLIFrameElement;
		declare opts: EffectStackupOptions;

		constructor(element: HTMLElement, opts?: EffectStackupOptions) {
			super();
			this.wgt = zk.Widget.$(element.id);
			this.opts = eff._skuOpts(opts);
			this.node = element;
			// ZK-1904: listen onVParent
			zWatch.listen({ onVParent: [this.node, eff._onVParent] });
		}

		destroy(): void {
			jq(this.stackup).remove();
			jq(this.node).removeClass(this.wgt!.getZclass() + '-shadow');
			zWatch.unlisten({ onVParent: [this.node, eff._onVParent] }); // ZK-2586
			this.wgt = this.node = this.stackup = undefined;
		}

		hide(): void {
			jq(this.stackup).hide();
			jq(this.node).removeClass(this.wgt!.getZclass() + '-shadow');
		}

		sync(): boolean {
			var node = this.node, $node = jq(node);
			if (!node || !$node.zk.isVisible(true)) {
				if (this.opts.stackup && node) {
					if (!this.stackup)
						this.stackup = jq.newStackup(node, node.id + '-sdwstk', node);
				}
				this.hide();
				return false;
			}

			$node.addClass(this.wgt!.getZclass() + '-shadow');

			var opts = this.opts,
				l = node.offsetLeft, t = node.offsetTop,
				w = node.offsetWidth, h = node.offsetHeight,
				stackup = this.stackup;

			if (opts.stackup) {
				if (!stackup)
					stackup = this.stackup = jq.newStackup(node, node.id + '-sdwstk', node);

				var st = stackup.style;
				st.left = jq.px(l);
				st.top = jq.px(t);
				st.width = jq.px0(w);
				st.height = jq.px0(h);
				st.zIndex = zk.parseInt($node.css('zIndex')) as unknown as string;
				st.display = 'block';
			}
			return true;
		}
		getBottomElement(): HTMLIFrameElement | undefined {
			return this.stackup;
		}
	}
	/**
	 * A mask covers the browser window fully.
	 */
	export class FullMask extends zk.Object implements Effect {
		declare mask?: HTMLElement;
		declare stackup?: HTMLIFrameElement;
		
		/**
		 * The constructor of the full mask object.
		 * <p>To remove the full mask, invoke {@link destroy}.
		 * @param opts - the options. Allowed options:
		 * <ul>
		 * <li>{@link DOMElement} mask: the mask element if the mask was created somewhere else. Default: create a new one.</li>
		 * <li>{@link DOMElement} anchor: whether to insert the mask before.</li>
		 * <li>String id: the mask ID. Default: z_mask.</li>
		 * <li>int zIndex: z-index to assign. Default: defined in the CSS called z-modal-mask.
		 * <li>boolean visible: whether it is visible</li>
		 * </ul>
		 */
		constructor(opts: EffectFullMaskOptions) {
			super();
			opts = eff._skuOpts(opts);
			var mask = this.mask = jq(opts.mask || [], zk)[0];
			if (this.mask) {
				if (opts.anchor)
					opts.anchor.parentNode?.insertBefore(mask, opts.anchor);
				if (opts.id) mask.id = opts.id;
				if (opts.zIndex != null) mask.style.zIndex = opts.zIndex as unknown as string;
				if (opts.visible == false) mask.style.display = 'none';
			} else {
				var maskId = opts.id || 'z_mask',
					html = '<div id="' + maskId + '" class="z-modal-mask"';//FF: don't add tabIndex
				if (opts.zIndex != null || opts.visible == false) {
					html += ' style="';
					if (opts.zIndex != null) html += 'z-index:' + opts.zIndex;
					if (opts.visible == false) html += ';display:none';
					html += '"';
				}

				html += '></div>';
				if (opts.anchor)
					jq(opts.anchor, zk).before(html);
				else
					jq(document.body).append(html);
				mask = this.mask = jq(maskId, zk)[0];
			}
			if (opts.stackup) {
				this.stackup = jq.newStackup(mask, mask.id + '-mkstk');
				jq(this.stackup).css({width: '100%', height: '100%', position: 'fixed'});
			}

			jq(mask).click(jq.Event.stop); //don't eat mousemove (drag depends on it)
		}
		/**
		 * Removes the full mask. You can not access this object any more.
		 */
		destroy(): void {
			var mask = this.mask;
			if (mask) {
				jq(mask).off('click', jq.Event.stop)
					.remove();
			}
			jq(this.stackup).remove();
			this.mask = this.stackup = undefined;
		}
		/**
		 * Hide the full mask. Application developers rarely need to invoke this method.
		 * Rather, use {@link sync} to synchronized the visual states.
		 */
		hide(): void {
			if (this.mask) this.mask.style.display = 'none';
			if (this.stackup) this.stackup.style.display = 'none';
		}
		/**
		 * Synchronizes the visual states of the full mask with the specified element and the browser window.
		 * The visual states include the visibility and Z Index.
		 */
		sync(el?: HTMLElement): void {
			if (el === undefined || !zk(el).isVisible(true)) {
				this.hide();
				return;
			}

			if (this.mask) {
				if (this.mask.nextSibling != el) {
					var p = el.parentNode as HTMLElement;
					p.insertBefore(this.mask, el);
					if (this.stackup)
						p.insertBefore(this.stackup, this.mask);
				}

				var st = this.mask.style;
				st.display = 'block';
				st.zIndex = el.style.zIndex;
			}

			if (this.stackup) {
				st = this.stackup.style;
				st.display = 'block';
				st.zIndex = el.style.zIndex;
			}
		}
	}
	/**
	 * Applies the mask over the specified element to indicate it is busy.
	 */
	export class Mask extends zk.Object implements Effect {
		declare mask?: HTMLElement;
		/** @internal */
		declare _opts: EffectMaskOptions;
		/** @internal */
		declare __mask?: Required<Effect>;
		declare wgt?: zk.Widget & Pick<Mask, '__mask'>;

		/**
		 * The constructor.
		 * <p>To remove the mask, invoke {@link destroy}.
		 * @param opts - the options:
		 * <ul>
		 * <li>String id - the id of the applied mask, if any.</li>
		 * <li>String/{@link DOMElement} anchor - the anchor of the applied mask, it can be an instance of {@link String} or {@link DOMElement}.</li>
		 * <li>String message - the message of the indicator, if any. null, Loading... is assumed.</li>
		 * </ul>
		 */
		constructor(opts?: EffectMaskOptions) {
			super();
			opts = opts || {};
			var $anchor = zk(opts.anchor);

			this._opts = opts;

			if (!$anchor.jq.length || !$anchor.isRealVisible(true)) return; //nothing do to.

			this._draw(opts, $anchor);
			this.sync();
		}
		//ZK-3118
		/** @internal */
		_draw(opts: EffectMaskOptions, $anchor: zk.JQZK): void {
			var maskId = opts.id || 'z_applymask',
				progbox = jq(maskId, zk)[0];

			if (progbox) return;

			var msg = opts.message || ((window.msgzk ? msgzk.LOADING : 'Loading') + '...'),
				n = document.createElement('div');

			document.body.appendChild(n);
			var xy = opts.offset || $anchor.revisedOffset(),
				w = opts.width || $anchor.offsetWidth(),
				h = opts.height || $anchor.offsetHeight();
			jq(n).replaceWith(
			'<div id="' + maskId + '" style="display:none">' //$anchor size changed if using visibility: hidden
			+ '<div class="z-apply-mask" style="display:block;top:' + xy[1]
			+ 'px;left:' + xy[0] + 'px;width:' + w + 'px;height:' + h + 'px;"></div>'
			+ '<div id="' + maskId + '-z_loading" class="z-apply-loading"><div class="z-apply-loading-indicator">'
			+ '<span class="z-apply-loading-icon"></span> '
			+ msg + '</div></div></div>');

			this.mask = jq(maskId, zk)[0];
			this.wgt = zk.Widget.$(opts.anchor);
			if (this.wgt) {
				zWatch.listen({
					onHide: [
						this.wgt, this.onHide
					],
					onSize: [
						this.wgt, this.onSize
					]
				});
				this.wgt.__mask = this;
			}

		}
		/**
		 * Hide the mask. Application developers rarely need to invoke this method.
		 * Rather, use {@link sync} to synchronized the visual states.
		 */
		hide(): void {
			this.mask!.style.display = 'none';
		}
		onHide(): void {
			this.__mask!.hide();
		}
		/**
		 * Synchronizes the visual states of the mask with the specified element and the browser window.
		 * The visual states include the visibility and Z Index.
		 */
		sync(): void {
			var opts = this._opts,
				anchor = opts.anchor,
				$anchor = zk(anchor);

			if (!anchor || !this.mask) {
				var optsId = opts.id!;
				opts.anchor = anchor = jq('#' + optsId.substring(0, optsId.indexOf('-')))[0];
				$anchor = zk(anchor);
				this._draw(opts, $anchor);
			}

			if (!$anchor.isVisible(true)) {
				this.hide();
				return;
			}

			var st = (this.mask!.firstChild as HTMLElement).style,
				xy = opts.offset || $anchor.revisedOffset(),
				w = opts.width || $anchor.offsetWidth(),
				h = opts.height || $anchor.offsetHeight();

			st.top = jq.px(xy[1]);
			st.left = jq.px(xy[0]);
			st.width = jq.px(w);
			st.height = jq.px(h);

			// ZK-726: The z-index required to cover anchor is the maximum z-index
			// along the anchor's ancestor chain, from document body to the highest
			// non-static node with non-auto z-index.
			var body = document.body,
				html = body.parentNode,
				rleaf: JQuery = $anchor.jq,
				zi: string | number = 'auto',
				zic: string, zicv: number;
			// find the highest non-static node with non-auto z-index
			for (var offp = rleaf.offsetParent(); offp[0] != body && offp[0] != html; offp = offp.offsetParent()) {
				if ((zic = offp.css('z-index')) && zic != 'auto') {
					zi = zk.parseInt(zic);
					rleaf = offp;
				}
			}
			// grab the maximum along the chain of nodes
			// eslint-disable-next-line zk/noNull
			for (var n: HTMLElement | null = rleaf[0]; n && n.style; n = n.parentElement) {
				//Chrome and Safari only, HTML tag's zIndex value is empty
				if (n.tagName == 'HTML' && zk.webkit)
					n.style.zIndex = 'auto';
				let zic = n.style.zIndex || jq(n).css('z-index');
				if (zic && zic != 'auto') {
					zicv = zk.parseInt(zic);
					if (zi == 'auto' || zicv > zi)
						zi = zicv;
				}
			}

			if (zi != 'auto') { //Bug ZK-1381: only apply z-index when it is not auto
				st.zIndex = zi as string;
				(this.mask!.lastChild as HTMLElement).style.zIndex = zi as string;
			}

			this.mask!.style.display = 'block';

			var loading = jq(this.mask!.id + '-z_loading', zk)[0];
			if (loading) {
				if (loading.offsetHeight > h)
					loading.style.height = jq.px0(zk(loading).revisedHeight(h));
				if (loading.offsetWidth > w)
					loading.style.width = jq.px0(zk(loading).revisedWidth(w));
				loading.style.top = jq.px0(xy[1] + ((h - loading.offsetHeight) / 2)); //non-negative only
				loading.style.left = jq.px0(xy[0] + ((w - loading.offsetWidth) / 2));
			}

			this.mask!.style.visibility = '';
		}
		onSize(): void {
			this.__mask!.sync();
		}

		/**
		 * Removes the mask.
		 */
		destroy(): void {
			jq(this.mask).remove();
			if (this.wgt) {
				zWatch.unlisten({onHide: [this.wgt, this.onHide], onSize: [this.wgt, this.onSize]});
				delete this.wgt.__mask;
			}
			this.mask = this.wgt = undefined;
		}
	}
	/** @class zk.Eff.Actions
	 * A collection of actions that can be used with {@link zk.Widget#setAction}.
	 * <p>The signature of an action must be as follows:<br>
	 * `function ({@link DOMElement} n, {@link Map} opts) {}`
	 * <p>Furthermore, the method will be called as a widget's method, i.e.,
	 * `this` references to the widget.
	 * @since 5.0.6
	 */
	export const Actions = {
		/**
		 * Slides down to display this widget.
		 * @param n - the node to display
		 * @param opts - the options. Allowed options:
		 * <ul>
		 * <li>`duration`: how many milliseconds to slide down</li>
		 * </ul>
		 */
		slideDown(this: zk.Widget, n: HTMLElement, opts?: zk.SlideOptions): void {
			zk(n).slideDown(this, opts);
		},
		/**
		 * Slides up to hide this widget.
		 * @param n - the node to hide
		 * @param opts - the options. Allowed options:
		 * <ul>
		 * <li>`duration`: how many milliseconds to slide up</li>
		 * </ul>
		 */
		slideUp(this: zk.Widget, n: HTMLElement, opts?: zk.SlideOptions): void {
			zk(n).slideUp(this, opts);
		},
		/**
		 * Slides in to display this widget.
		 * @param n - the node to display
		 * @param opts - the options. Allowed options:
		 * <ul>
		 * <li>`duration`: how many milliseconds to slide in</li>
		 * </ul>
		 */
		slideIn(this: zk.Widget, n: HTMLElement, opts?: zk.SlideOptions): void {
			zk(n).slideIn(this, opts);
		},
		/**
		 * Slides out to hide this widget.
		 * @param n - the node to hide
		 * @param opts - the options. Allowed options:
		 * <ul>
		 * <li>`duration`: how many milliseconds to slide out</li>
		 * </ul>
		 */
		slideOut(this: zk.Widget, n: HTMLElement, opts?: zk.SlideOptions): void {
			zk(n).slideOut(this, opts);
		}
	};
	/**
	 * Applies a keyboard trap that only allows focus moving within an area.
	 * @since 9.5.0
	 */
	export class KeyboardTrap extends zk.Object {
		/** @internal */
		declare _area?: HTMLElement;
		/** @internal */
		declare _boundaryTop?: HTMLDivElement;
		/** @internal */
		declare _boundaryBottom?: HTMLDivElement;
		/**
		 * The constructor.
		 * <p>To remove the trap, invoke {@link destroy}.
		 * @param area - which area should the focus be restricted.
		 */
		constructor(area: HTMLElement) {
			super();
			this._area = area;
			this._boundaryTop = this._createBoundary('top');
			this._boundaryBottom = this._createBoundary('bottom');
			area.insertAdjacentElement('beforebegin', this._boundaryTop);
			area.insertAdjacentElement('afterend', this._boundaryBottom);
		}
		/** @internal */
		_createBoundary(id: string): HTMLDivElement {
			var boundary = document.createElement('div'),
				self = this;
			boundary.tabIndex = 0;
			boundary.setAttribute('aria-hidden', 'true');
			boundary.addEventListener('focus', function () {
				self._handleFocus(id);
			});
			return boundary;
		}
		/** @internal */
		_handleFocus(id: string): void {
			var focusableElements = (this._area!).querySelectorAll(
				'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'),
				focusableElementsCount = focusableElements.length,
				isTop = id == 'top';
			if (focusableElementsCount > 0) {
				var elem = isTop
					? this._getLastFocusableElement(focusableElements)
					: this._getFirstFocusableElement(focusableElements);
				if (elem) (elem as HTMLElement).focus();
			}
		}
		/** @internal */
		_getFirstFocusableElement(elems: NodeListOf<Element>): Element | undefined {
			var len = elems.length;
			for (var i = 0; i < len; i++) {
				if (this._isFocusable(elems[i] as HTMLElement)) return elems[i];
			}
			return undefined;
		}
		/** @internal */
		_getLastFocusableElement(elems: NodeListOf<Element>): Element | undefined {
			var len = elems.length;
			for (var i = len - 1; i >= 0; i--) {
				if (this._isFocusable(elems[i] as HTMLElement)) return elems[i];
			}
			return undefined;
		}
		/** @internal */
		_isFocusable(elem: HTMLElement & {disabled?: boolean}): boolean {
			return !(elem.disabled || elem.getAttribute('disabled')) // not disabled
				&& !!(elem.offsetWidth || elem.offsetHeight || elem.getClientRects().length); // visible
		}
		/**
		 * Removes the keyboard trap.
		 * You can not access this object any more.
		 */
		destroy(): void {
			var area = this._area;
			if (area != null) {
				var areaParent = area.parentNode;
				if (areaParent) {
					areaParent.removeChild(this._boundaryTop as HTMLElement);
					areaParent.removeChild(this._boundaryBottom as HTMLElement);
				}
			}
			this._area = this._boundaryTop = this._boundaryBottom = undefined;
		}
	}
}
jq(function () {
	//Handle zk.useStackup
	var _lastFloat, _autohideCnt = 0, _shallUse = false, _callback: Partial<typeof zWatch>;

	/** @internal */
	function _onFloatUp(ctl: {origin?: zk.Widget}): void {
		var wgt = ctl.origin;
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt) {
				if (wgt)
					wgt = wgt.getTopWidget();
				if (wgt != _lastFloat) {
					_lastFloat = wgt;
					zk._wgtutl.autohide(); //see widget.js
				}
			}
		}, 120); //filter
	}
	/** @internal */
	function _autohide(): void {
		_lastFloat = false; //enforce to run if onFloatUp also fired
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt)
				zk._wgtutl.autohide();
		}, 100); //filter
	}

	_useSKU = zk.useStackup;
	if (_useSKU == 'auto' || (_shallUse = _useSKU == 'auto/gecko')) {
		if (zk.gecko && _shallUse)
			_useSKU = false;
		else {
			_shallUse = !!(zk.webkit || zk.opera);
			_useSKU = !_shallUse;
		}
	}

	//if (_shallUse) { all browser should support autohide
		var w2hide = function (name): void {
			if (name == 'onSize' || name == 'onMove'
			|| name == 'onShow' || name == 'onHide'
			|| name == 'onResponse')
				_autohide();
		};
		zk.override(zWatch, _callback = {}, {
			fire(name: string, origin: zk.Object, opts?: Partial<zk.FireOptions> | unknown, ...vararg: unknown[]): void {
				_callback.fire!.call(this, name, origin, opts, ...vararg as []);
				w2hide(name);
			},
			fireDown(name: string, origin: zk.Object, opts?: Partial<zk.FireOptions>, ...vararg: unknown[]): void {
				_callback.fireDown!.call(this, name, origin, opts, ...vararg);
				w2hide(name);
			}
		});
		zWatch.listen({onFloatUp: ['', _onFloatUp]});
	// }
}); //jq
zk.eff = eff;