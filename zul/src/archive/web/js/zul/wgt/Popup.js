/* Popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:15:59     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Popup = zk.$extends(zul.Widget, {
	open: function (wgt, offset, position, sendOnOpen) {
		var ref = wgt, pos, dim;
		
		if (ref && position) {
			if (typeof ref == 'string')
				ref = zk.Widget.$(ref);
				
			if (ref) {
				var refn = ref.getNode(),
					ofs = zDom.cmOffset(refn);
				pos = position;
				dim = {
					top: ofs[0], left: ofs[1],
					width: zDom.offsetWidth(refn), height: zDom.offsetHeight(refn)  
				}
			}
		} else if (offset && offset.$array) {
			dim = {
				top: zk.parseInt(offset[0]),
				left:  zk.parseInt(offset[1])
			}
		}
		if (!dim) return;

		var node = this.getNode();
		zDom.setStyle(node, {position: "absolute"});
		zDom.makeVParent(node);
		zDom.autoPosition(node, dim, pos);
		
		this.setVisible(true);
		
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
				this._stackup = zDom.makeStackup(node, null, node);
			else {
				this._stackup.style.top = node.style.top;
				this._stackup.style.left = node.style.left;
				this._stackup.style.display = "block";
			}
		}
		if (sendOnOpen) this.fire('onOpen', ref ? [true, ref.uuid] : true);
		zDom.setStyle(node, {visibility: 'inherit'});
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		zWatch.unlisten('onResponse', this);
		this.mask = null;
	},
	close: function (sendOnOpen) {
		if (this._stackup)
			this._stackup.style.display = "none";
		
		this.setVisible(false);
		zDom.undoVParent(this.getNode());
		this.setFloating_(false);
		if (sendOnOpen) this.fire('onOpen', false);
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
		this.close(true);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onFloatUp', this);
		zWatch.listen('onVisible', this);
		this.setFloating_(true);
		this.ecave = zDom.$(this.uuid + "$cave");
	},
	unbind_: function () {
		if (this._stackup) {
			zDom.remove(this._stackup);
			this._stackup = null;
		}
		this.ecave = null;
		
		zWatch.unlisten('onFloatUp', this);
		this.setFloating_(false);
		this.$supers('unbind_', arguments);
	},
	onVisible: zk.ie7 ? function (wgt) {
		var node = wgt.getNode(),
			wdh = node.style.width,
			fir = zDom.firstChild(node, "DIV"),
			last = zDom.lastChild(zDom.lastChild(node, "DIV"), "DIV"),
			n = wgt.ecave.parentNode;
		
		if (!wdh || wdh == "auto") {
			var diff = zDom.frameWidth(n.parentNode) + zDom.frameWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.frameWidth(fir)
					+ zDom.frameWidth(fir.firstChild) - diff)) + "px";
			}
			if (last) {
				last.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.frameWidth(last)
					+ zDom.frameWidth(last.firstChild) - diff)) + "px";
			}
		} else {
			if (fir) fir.firstChild.firstChild.style.width = "";
			if (last) last.firstChild.firstChild.style.width = "";
		}
	}: zk.$void,
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onVisible', -1, this);
	},
	isImportantEvent_: function (evtnm) {
		return evtnm == "onOpen";
	}
});