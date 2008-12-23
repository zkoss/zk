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
	open: function (mode, x, y, fromServer) {
		var ref, pos, dim;
		if (mode === "2") { //ref
			ref = zk.Widget.$(x);
			if (ref) {
				var ofs = zDom.cmOffset(ref.node);
				pos = y;
				dim = {
					top: ofs[0], left: ofs[1],
					width: zDom.offsetWidth(ref.node), height: zDom.offsetHeight(ref.node)  
				}
			}
		} else {
			dim = {
				top: zk.parseInt(x),
				left:  zk.parseInt(y)
			}
		}
		zDom.setStyle(this.node, {position: "absolute"});
		zDom.makeVParent(this.node);
		zDom.autoPosition(this.node, dim, pos);
		
		this.setVisible(true);
		
		if (this.isListen("onOpen")) {
			// use a progress bar to hide the popup
			this.mask = new zk.eff.Mask({
				id: this.uuid + "$mask",
				anchor: this.node
			});
			
			// register onResponse to remove the progress bar after receiving
			// the response from server.
			zWatch.listen('onResponse', this);		
		}
		if (zk.ie6Only) {
			if (!this._stackup)
				this._stackup = zDom.makeStackup(this.node, null, this.node);
			else {
				this._stackup.style.top = this.node.style.top;
				this._stackup.style.left = this.node.style.left;
				this._stackup.style.display = "block";
			}
		}
		if (!fromServer) this.fire('onOpen', ref ? [true, ref.id] : true);
		zDom.setStyle(this.node, {visibility: 'inherit'});
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		zWatch.unlisten('onResponse', this);
		this.mask = null;
	},
	close: function (fromServer) {
		if (this._stackup)
			this._stackup.style.display = "none";
		
		this.setVisible(false);
		zDom.undoVParent(this.node);
		this.setFloating_(false);
		if (!fromServer) this.fire('onOpen', false);
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
		this.close();
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
		var node = wgt.node,
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