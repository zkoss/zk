/* Popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:15:59     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Popup = zk.$extends(zul.Widget, {
	_visible: false,
	isOpen: function () {
		return this.isVisible();
	},
	open: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);

		var node = this.getNode();
		zDom.setStyles(node, {position: "absolute"});
		zDom.makeVParent(node);
		if (posInfo)
			zDom.position(node, posInfo.dim, posInfo.pos, opts);
		
		this.setVisible(true);
		this.setFloating_(true);
		this.setTopmost();
		
		if (this.isListen("onOpen")) {
			// use a progress bar to hide the popup
			this.mask = new zk.eff.Mask({
				id: this.uuid + "$mask",
				anchor: node
			});
			
			// register onResponse to remove the progress bar after receiving
			// the response from server.
			zWatch.listen('onResponse', this);		
		}
		if (zk.ie6Only) {
			if (!this._stackup)
				this._stackup = zDom.newStackup(node);
			else {
				this._stackup.style.top = node.style.top;
				this._stackup.style.left = node.style.left;
				this._stackup.style.display = "block";
			}
		}
		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		if (opts && opts.sendOnOpen) this.fire('onOpen', {open: true, reference: ref});
		zDom.cleanVisibility(node);
	},
	position: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zDom.position(this.getNode(), posInfo.dim, posInfo.pos, opts);
	},
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;
		
		if (ref && position) {
			if (typeof ref == 'string')
				ref = zk.Widget.$(ref);
				
			if (ref) {
				var refn = zul.Widget.isInstance(ref) ? ref.getNode() : ref;
				pos = position;
				dim = zDom.dimension(refn, true);
			}
		} else if (offset && offset.$array) {
			dim = {
				left: zk.parseInt(offset[0]), top: zk.parseInt(offset[1]),
				width: 0, height: 0
			}
		}
		if (dim) return {pos: pos, dim: dim};
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		zWatch.unlisten('onResponse', this);
		this.mask = null;
	},
	close: function (opts) {
		if (this._stackup)
			this._stackup.style.display = "none";
		
		this.setVisible(false);
		zDom.undoVParent(this.getNode());
		this.setFloating_(false);
		if (opts && opts.sendOnOpen) this.fire('onOpen', {open:false});
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-popup";
	},
	onFloatUp: function(wgt){
		if (!this.isVisible()) 
			return;
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
		zWatch.listen('onFloatUp', this);
		zWatch.listen('onVisible', this);
		this.setFloating_(true);
	},
	unbind_: function () {
		if (this._stackup) {
			zDom.remove(this._stackup);
			this._stackup = null;
		}
		
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten('onVisible', this);
		this.setFloating_(false);
		this.$supers('unbind_', arguments);
	},
	onVisible: zk.ie7 ? function (wgt) {
		var node = wgt.getNode(),
			wdh = node.style.width,
			fir = zDom.firstChild(node, "DIV"),
			last = zDom.lastChild(node, "DIV"),
			n = wgt.getSubnode('cave').parentNode;
		
		if (!wdh || wdh == "auto") { //Popup will disappear when width is null in IE 
			var diff = zDom.padBorderWidth(n.parentNode) + zDom.padBorderWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(fir)
					+ zDom.padBorderWidth(fir.firstChild) - diff)) + "px";
			}
			if (last) {
				last.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(last)
					+ zDom.padBorderWidth(last.firstChild) - diff)) + "px";
			}
		} else {
			if (fir) fir.firstChild.style.width = "";
			if (last) last.firstChild.style.width = "";
		}
	}: zk.$void,
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onVisible', {visible:true}, this);
	},
	prologHTML_: function (out) {
	},
	epilogHTML_: function (out) {
	}
});
