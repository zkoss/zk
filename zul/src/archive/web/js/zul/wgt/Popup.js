/* Popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:15:59     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned on the left.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned on the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned on the left.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned on the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned on the top.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned on the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned on the top.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned on the bottom.</li>
	 *  <li><b>overlap</b><br/> the popup overlaps the anchor, with the top-left 
	 *  	corners of both the anchor and popup aligned.</li>
	 *  <li><b>overlap_end</b><br/> the popup overlaps the anchor, with the top-right 
	 *  	corners of both the anchor and popup aligned.</li>
	 *  <li><b>overlap_before</b><br/> the popup overlaps the anchor, with the bottom-left 
	 *  	corners of both the anchor and popup aligned.</li>
	 *  <li><b>overlap_after</b><br/> the popup overlaps the anchor, with the bottom-right 
	 *  	corners of both the anchor and popup aligned.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the anchor, with the topleft corner of the popup at the horizontal position of the mouse pointer.</li>
	 * </ul></p>
	 * @param Map opts 
	 * 	if opts.sendOnOpen exists, it will fire onOpen event. If opts.disableMask exists,
	 *  it will show a disable mask.
	 */
	open: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position),
			node = this.$n(),
			$n = jq(node);
		$n.css({position: "absolute"}).zk.makeVParent();
		if (posInfo)
			$n.zk.position(posInfo.dim, posInfo.pos, opts);
		
		this.setVisible(true);
		this.setFloating_(true);
		this.setTopmost();
		
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
				this._stackup.style.top = node.style.top;
				this._stackup.style.left = node.style.left;
				this._stackup.style.display = "block";
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

	position: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zk(this.$n()).position(posInfo.dim, posInfo.pos, opts);
	},
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;
		
		if (ref && position) {
			if (typeof ref == 'string')
				ref = zk.Widget.$(ref);
				
			if (ref) {
				var refn = zul.Widget.isInstance(ref) ? ref.$n() : ref;
				pos = position;
				dim = zk(refn).dimension(true);
			}
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
		
		this.setVisible(false);
		zk(this.$n()).undoVParent();
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
			floatFound = floatFound || wgt.isFloating_();
		}
		this.close({sendOnOpen:true});
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onFloatUp: this, onShow: this});
		this.setFloating_(true);
	},
	unbind_: function () {
		if (this._stackup) {
			jq(this._stackup).remove();
			this._stackup = null;
		}
		
		zWatch.unlisten({onFloatUp: this, onShow: this});
		this.setFloating_(false);
		this.$supers('unbind_', arguments);
	},
	onShow: function () {
		this._fixWdh();
		this._fixHgh();
	},
	_offsetHeight: function () {
		var node = this.$n(),
			h = node.offsetHeight - 1, 
			tl = jq(node).find('> div:first-child')[0],
			bl = jq(node).find('> div:last')[0],
			n = this.getCaveNode().parentNode,
			bd = this.$n('body');
		
			h -= tl.offsetHeight;
			h -= bl.offsetHeight;
			h -= zk(n).padBorderHeight();
			h -= zk(bd).padBorderHeight();
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
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onShow', this);
	},
	prologHTML_: function (out) {
	},
	epilogHTML_: function (out) {
	}
});
