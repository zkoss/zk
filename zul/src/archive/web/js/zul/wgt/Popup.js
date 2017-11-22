/* Popup.js

	Purpose:

	Description:

	History:
		Wed Dec 17 19:15:59     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A container that is displayed as a popup.
 * The popup window does not have any special frame.
 * Popups can be displayed when an element is clicked by assigning
 * the id of the popup to either the {@link #setPopup},
 * {@link #setContext} or {@link #setTooltip} attribute of the element.
 *
 * <p>Default {@link #getZclass}: z-popup.
 */
zul.wgt.Popup = zk.$extends(zul.Widget, {
	_visible: false,
	_minzindex: -1,
	$define: {
		/** Sets the minimal Z index.
		 * <p>Default: -1
		 * @since 8.5.1
		 */
		/**
		 * Returns the minimum Z index.
		 * <p>Default: -1.
		 * @return int
		 */
		minzindex: null
	},
	/**
	 * Returns whether the popup is visible.
	 * @return boolean
	 */
	isOpen: function () {
		return this.isVisible();
	},
	/**
	 * Opens the popup.
	 * <p>Note: the ref with the position parameter is prior to the offset parameter,
	 * if any.
	 * @param zk.Widget ref the referred widget.
	 * @param Offset offset the offset of x and y
	 * @param String position
	 * <p> Possible values for the position attribute are:
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
	 * </ul></p>
	 * @param Map opts
	 * 	if opts.sendOnOpen exists, it will fire onOpen event. If opts.disableMask exists,
	 *  it will show a disable mask. If opts.overflow exists, it allows the popup to appear
	 *  out of the screen range. If opts.dodgeRef exists, it will avoid covering the reference
	 *  element.
	 */
	/**
	 * Opens the popup.
	 * <p>Note: the ref with the position parameter is prior to the offset parameter,
	 * if any.
	 * @param String ref the uuid of the ref widget.
	 * @param Offset offset the offset of x and y
	 * @param String position Possible values for the position attribute
	 * @param Map opts
	 * 	if opts.sendOnOpen exists, it will fire onOpen event. If opts.disableMask exists,
	 *  it will show a disable mask. If opts.overflow exists, it allows the popup to appear
	 *  out of the screen range. If opts.dodgeRef exists, it will avoid covering the reference
	 *  element.
	 *  @see #open(zk.Widget, Offset, String, Map)
	 */
	open: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position),
			node = this.$n(),
			top = node.style.top,
			$n = jq(node);

		// the top is depend on children's height, if child will re-size after onSize/onShow,
		// popup need to re-position top after children height has calculated.
		// B50-ZK-391
		// should keep openInfo each time,
		// maybe have to reposition in onResponse if the child changed with onOpen event,
		if (arguments.length != 0) //do not update _openInfo when just call open()
			this._openInfo = arguments;

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
	},
	/** The effect for opening the popup. Override this function to provide
	 * opening effect. afterOpenAnima_ needs to be called after the effect.
	 * @since 6.0.1
	 */
	openAnima_: function (ref, offset, position, opts) {
		this.afterOpenAnima_(ref, offset, position, opts);
	},
	/** The handling after the opening effect of popup.
	 * @since 6.0.1
	 */
	afterOpenAnima_: function (ref, offset, position, opts) {
		var node = this.$n(),
			sendOnOpen = opts && opts.sendOnOpen;
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

		// B30-1819264 : should skip null
		if (this.shallStackup_() && node) {
			if (!this._stackup)
				this._stackup = jq.newStackup(node, node.id + '-stk');
			else {
				var dst, src;
				(dst = this._stackup.style).top = (src = node.style).top;
				dst.left = src.left;
				dst.zIndex = src.zIndex;
				dst.display = 'block';
			}
		}
		// resync position if the content is not calculated. Bug ZK-2257
		var openInfo = this._openInfo;
		if (openInfo) {
			this.position.apply(this, openInfo);
		}

		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		if (sendOnOpen) this.fire('onOpen', {open: true, reference: ref});
		//add extra CSS class for easy customize
		jq(node).addClass(this.$s('open'));

		// B85-ZK-3606: for adjusting popup position
		if (ref && ref.desktop && this.desktop) {
			var refDim = zk(ref).dimension(true), thisDim = zk(this).dimension(true);
			if (refDim && thisDim) {
				this._adjustLeft = thisDim.left - refDim.left;
				this._adjustTop = thisDim.top - refDim.top;
			}
			this._keepVisible = true;
		}
	},
	/** Returns whether to instantiate a stackup when {@link #open}
	 * is called.
	 * <p>If the derive class created its own stackup (such as creating
	 * a shadow), it shall override this method to return false.
	 * @return boolean
	 */
	shallStackup_: function () {
		return zk.eff.shallStackup();
	},
	/**
	 * Sets the popup position.
	 * <p>Note: the ref with the position parameter is prior to the offset parameter,
	 * if any.
	 * @param zk.Widget ref the referred widget.
	 * @param Offset offset the offset of x and y
	 * @param String position
	 * <p> Possible values for the position attribute
	 * refer to {@link #open}.
	 * </p>
	 * @param Map opts a map of addition options.<br/>
	 * Allowed values: refer to {@link jqzk#position(Dimension,String,Map)}.
	 */
	position: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zk(this.$n()).position(posInfo.dim, posInfo.pos, opts);
	},
	/** Reset the position on scroll
	 * @param zk.Widget wgt
	 */
	onScroll: function (wgt) {
		if (wgt) {
			if (this.isInView_()) {
				var args = this.getPositionArgs_();
				if (!this.isOpen() && this._keepVisible) {
					this.open.apply(this, args);
				} else {
					this.position.apply(this, args);
				}
			} else if (this.isOpen()) {
				this.close({keepVisible: true});
			}
		}
	},
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;

		if (position) {
			if (ref) {
				if (typeof ref == 'string')
					ref = zk.Widget.$(ref);

				if (ref) {
					var refn = zul.Widget.isInstance(ref) ? ref.$n() : ref;
					// B65-ZK-1934: Make sure refn is not null
					if (refn) {
						pos = position;
						dim = zk(refn).dimension(true);
					} else
						return {pos: position};
				}
			} else
				return {pos: position};
		} else if (jq.isArray(offset)) {
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
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		// B50-ZK-391: Tooltip loses "position=after_end" positioning if onOpen eventlistener added to popup
		var openInfo = this._openInfo;
		if (openInfo) {
			this.position.apply(this, openInfo);
			this._openInfo = null;
		}
		zWatch.unlisten({onResponse: this});
		this.mask = null;
	},
	/**
	 * Closes this popup at the client.
	 *
	 * <p>In most cases, the popup is closed automatically when the user
	 * clicks outside of the popup.
	 * @param Map opts if opts.sendOnOpen exists, it will fire onOpen event.
	 */
	close: function (opts) {
		if (this._stackup)
			this._stackup.style.display = 'none';
		// F70-ZK-2007: Clear toggle type.
		this._shallToggle = false;

		try {
			//fix firefox and ie issue
			if ((zk.ie || zk.ff) && zk.currentFocus) {
				 // Bug ZK-2922, check ancestor first.
				var n = zk.currentFocus.getInputNode ? zk.currentFocus.getInputNode() : zk.currentFocus.$n();
				if (jq.nodeName(n, 'input')) {
					if (zk.ff && jq.isAncestor(this.$n(), n)) {
						jq(n).blur(); // trigger a missing blur event.
					} else if (zk.ie && document.activeElement !== n) {
						//ZK-3244 popup miss focus on input on IE
						zk(n).focus();
					}
				}
			}
		} catch (e) {
			zk.debugLog(e.message || e);
		}

		// remove visible flag
		if (!opts || !opts.keepVisible) {
			this._keepVisible = false;
		}
		// remove fake parent flag
		if (this._hasFakeParent) {
			this._hasFakeParent = false;
		}

		this.closeAnima_(opts);  // Bug ZK-1124: should pass arguments to closeAnima_ function
	},
	/** The effect for closing the popup. Override this function to provide
	 * closing effect. afterCloseAnima_ needs to be called after the effect.
	 * @since 6.0.1
	 */
	closeAnima_: function (opts) {
		this.afterCloseAnima_(opts);
	},
	/** The handling after the closing effect of popup.
	 * @since 6.0.1
	 */
	afterCloseAnima_: function (opts) {
		this.setVisible(false);

		var node = this.$n();
		zk(node).undoVParent();
		zWatch.fireDown('onVParent', this);

		this.setFloating_(false);
		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open: false});

		if (zk.ie < 11) { // re-create dom element to remove :hover state style
			var that = this;
			setTimeout(function () {
				that.replaceHTML(node); // see also ZK-1216, ZK-1124, ZK-318
			}, 50);
		}
		//remove extra CSS class
		jq(node).removeClass(this.$s('open'));
	},
	onFloatUp: function (ctl, opts) {
		if (!this.isVisible())
			return;
		var openInfo = this._openInfo,
			length = ctl.args.length;

		// F70-ZK-2007: If popup belongs to widget's ascendant then return.
		if (this._shallToggle && openInfo && opts && (
				opts.triggerByClick === undefined || (
				openInfo[3].which == opts.triggerByClick && zUtl.isAncestor(this._openInfo[0], ctl.origin)))) {
			return;
		}
		this._doFloatUp(ctl);
	},
	_doFloatUp: function (ctl) {
		if (!this.isVisible())
			return;
		var wgt = ctl.origin;
		for (var floatFound; wgt; wgt = wgt.parent) {
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
	},
	// ZK-2990: should also change the zIndex of the stackup of the widget
	setFloatZIndex_: function (node, zi) {
		this.$supers('setFloatZIndex_', arguments);
		if (this._stackup) {
			this._stackup.style.zIndex = zi;
		}
	},
	onVParent: function (ctl) {
		// ZK-2554: call _doFloatUp when triggered onVParent only if _shallToggle is true
		if (this._shallToggle)
			this._doFloatUp(ctl);
	},
	bind_: function () {
		this.$supers(zul.wgt.Popup, 'bind_', arguments);
		zWatch.listen({onFloatUp: this, onShow: this, onVParent: this, onSize: this, onScroll: this});
		this.setFloating_(true);
	},
	unbind_: function () {
		zk(this.$n()).undoVParent(); //Bug 3079480
		if (this._stackup) {
			jq(this._stackup).remove();
			this._stackup = null;
		}
		if (this._openInfo)
			this._openInfo = null;
		this._shallToggle = null;
		zWatch.unlisten({onFloatUp: this, onShow: this, onVParent: this, onSize: this, onScroll: this});
		this.setFloating_(false);
		this.$supers(zul.wgt.Popup, 'unbind_', arguments);
	},
	onSize: function () {
		this.reposition();
	},
	/** Reposition popup
	 * @since 8.0.3
	 */
	reposition: function () {
		if (this.parent) {
			// B85-ZK-3606: reposition based on the current position of the item
			this.position.apply(this, this.getPositionArgs_());
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
	},
	onShow: function (ctl) {
		//bug 3034505: call children's onShow to calculate the height first
		ctl.fire(this.firstChild);
		zk(this).redoCSS(-1, {'fixFontIcon': true});
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop)
			zUtl.fireShown(this);
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop)
			zWatch.fireDown('onShow', this);
	},
	prologHTML_: function (out) {
	},
	epilogHTML_: function (out) {
	},
	isInView_: function () {
		return this.parent ? zk(this.parent).isRealScrollIntoView(true) : false;
	},
	getPositionArgs_: function () {
		var p = this.parent, dim = zk(p).dimension(true);
		return [p, [dim.left + this._adjustLeft, dim.top + this._adjustTop] , null, {dodgeRef: false}];
	},
	doClick_: function (evt, popupOnly) {
		if (this._hasFakeParent)
			evt.stop(); // B85-ZK-3606: prevent event from bubbling up to its fake parent
		this.$supers('doClick_', arguments);
	},
	doRightClick_: function (evt) {
		if (this._hasFakeParent)
			evt.stop(); // B85-ZK-3606: prevent event from bubbling up to its fake parent
		this.$supers('doRightClick_', arguments);
	},
	doTooltipOver_: function (evt) {
		if (this._hasFakeParent)
			evt.stop(); // B85-ZK-3606: prevent event from bubbling up to its fake parent
		this.$supers('doTooltipOver_', arguments);
	}
});
