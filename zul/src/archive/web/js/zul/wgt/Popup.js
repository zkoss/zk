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
		this._openInfo = arguments;

		$n.css({position: "absolute"}).zk.makeVParent();
		zWatch.fireDown("onVParent", this);

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
		var node = this.$n();
		this.setVisible(true);
		if ((!opts || !opts.disableMask) && this.isListen("onOpen", {asapOnly:true})) {
			//Racing? Previous onResponse has not been fired and user triggers open again
			if (this.mask) this.mask.destroy(); 
			
			// use a progress bar to hide the popup
			this.mask = new zk.eff.Mask({
				id: this.uuid + "-mask",
				anchor: node
			});
			
			// register onResponse to remove the progress bar after receiving
			// the response from server.
			zWatch.listen({onResponse: this});		
		}
		if (this.shallStackup_()) {
			if (!this._stackup)
				this._stackup = jq.newStackup(node, node.id + "-stk");
			else {
				var dst, src;
				(dst = this._stackup.style).top = (src = node.style).top;
				dst.left = src.left;
				dst.zIndex = src.zIndex;
				dst.display = "block";
			}
		}
		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		if (opts && opts.sendOnOpen) this.fire('onOpen', {open: true, reference: ref});
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
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;
		
		if (position) {
			if (ref) {
				if (typeof ref == 'string')
					ref = zk.Widget.$(ref);
					
				if (ref) {
					var refn = zul.Widget.isInstance(ref) ? ref.$n() : ref;
					pos = position;
					dim = zk(refn).dimension(true);
				}
			} else
				return {pos: position};
		} else if (jq.isArray(offset)) {
			dim = {
				left: zk.parseInt(offset[0]), top: zk.parseInt(offset[1]),
				width: 0, height: 0
			}
		}
		if (dim) return {pos: pos, dim: dim};
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
			this._stackup.style.display = "none";
		this.closeAnima_();
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
		zk(this.$n()).undoVParent();
		zWatch.fireDown("onVParent", this);

		this.setFloating_(false);
		if (opts && opts.sendOnOpen) this.fire('onOpen', {open:false});
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-popup";
	},
	onFloatUp: function(ctl){
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
		this.close({sendOnOpen:true});
	},
	bind_: function () {
		this.$supers(zul.wgt.Popup, 'bind_', arguments);
		zWatch.listen({onFloatUp: this, onShow: this});
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
		zWatch.unlisten({onFloatUp: this, onShow: this});
		this.setFloating_(false);
		this.$supers(zul.wgt.Popup, 'unbind_', arguments);
	},
	onShow: function (ctl) {
		//bug 3034505: call children's onShow to calculate the height first
		ctl.fire(this.firstChild);
		var openInfo = this._openInfo;
		if (openInfo) {
			this.position.apply(this, openInfo);
			// B50-ZK-391
			// should keep openInfo, maybe used in onResponse later.
		}
		this._fixWdh();
		this._fixHgh();
	},
	_offsetHeight: function () {
		var node = this.$n(),
			isFrameRequired = zul.wgt.PopupRenderer.isFrameRequired(),
			h = node.offsetHeight - (isFrameRequired ? 1: 0), 
			bd = this.$n('body');
		
		if (isFrameRequired) {
			var tl = jq(node).find('> div:first-child')[0],
				bl = jq(node).find('> div:last')[0],
				n = this.getCaveNode().parentNode,
				bd = this.$n('body');
		
			h -= tl.offsetHeight;
			h -= bl.offsetHeight;
			h -= zk(n).padBorderHeight();
			h -= zk(bd).padBorderHeight();
		} else {
			h -= zk(bd).padBorderHeight();
		}
		
		return h;
	},
	_fixHgh: function () {
		var hgh = this.$n().style.height,
			c = this.getCaveNode();
		if (zk.ie6_ && ((hgh && hgh != "auto" )|| c.style.height)) c.style.height = "0px";
		if (hgh && hgh != "auto")
			zk(c).setOffsetHeight(this._offsetHeight());
		else 
			c.style.height = "auto";
	},
	_fixWdh: zk.ie7_ ? function () {
		if (!zul.wgt.PopupRenderer.isFrameRequired()) return;
		var node = this.$n(),
			wdh = node.style.width,
			cn = jq(node).children('div'),
			fir = cn[0],
			last = cn[cn.length - 1],
			n = this.$n('cave').parentNode;
		
		if (!wdh || wdh == "auto") { //Popup will disappear when width is null in IE 
			var diff = zk(n.parentNode).padBorderWidth() + zk(n.parentNode.parentNode).padBorderWidth();
			if (fir) {
				fir.firstChild.style.width = jq.px0(n.offsetWidth - (zk(fir).padBorderWidth()
					+ zk(fir.firstChild).padBorderWidth() - diff));
			}
			if (last) {
				last.firstChild.style.width = jq.px0(n.offsetWidth - (zk(last).padBorderWidth()
					+ zk(last.firstChild).padBorderWidth() - diff));
			}
		} else {
			if (fir) fir.firstChild.style.width = "";
			if (last) last.firstChild.style.width = "";
		}
	}: zk.$void,
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
	}
});
/** @class zul.wgt.PopupRenderer
 * The renderer used to render a Popup.
 * It is designed to be overriden
 * @since 5.0.5
 */
zul.wgt.PopupRenderer = {
	/** Check the Popup whether to render the frame
	 * 
	 * @param zul.wgt.Popup wgt the window
	 */
	isFrameRequired: function () {
		return false;
	}
};