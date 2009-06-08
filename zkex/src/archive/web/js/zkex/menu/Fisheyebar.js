/* Fisheyebar.js

	Purpose:
		
	Description:
		
	History:
		Thu May 15 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zkex.menu.Fisheyebar = zk.$extends(zul.Widget, {
	_itemWidth : 50, 
	_itemHeight : 50,
	_itemMaxWidth : 200, 
	_itemMaxHeight : 200, 
	_itemPadding : 10,
	_orient : "horizontal", 
	_attachEdge : "center",
	_labelEdge : "bottom",

	$init: function () {
		this.$supers('$init', arguments);
		this.units = 2;
		this.subts = 0.5;
		this.box = {x: -1, y:- 1, l: -1, r: -1, t: -1, b: -1};
	},
	$define: { 
		itemWidth: _zkf = function () {
			this.syncAttr();
		},
		itemHeight: _zkf,
		itemMaxWidth: _zkf,
		itemMaxHeight: _zkf,
		itemPadding: _zkf,
		orient: _zkf,
		attachEdge: _zkf,
		labelEdge: _zkf 
	},

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-fisheyebar";
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		this._init();
		this.syncAttr();
		this.onSize();
		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		zEvt.unlisten(document.documentElement, "mousemove", this._onMouseMove);
		zEvt.unlisten(document.documentElement, "mouseout", this._onBodyOut);
		this.pbox = this._onMouseMove = this._onBodyOut = null;
		this.$supers('unbind_', arguments);
	},
	_init: function () {
		var n = this.getNode();
		zDom.disableSelection(n);
		this.syncAttr();
		var meta = this;
		if(!this._onMouseMove)
			this._onMouseMove = function (evt) {
				
				var x = zEvt.x(evt),
					y = zEvt.y(evt);
				if ((x >= meta.box.l) && (x <= meta.box.r) &&
					(y >= meta.box.t) && (y <= meta.box.b)) {
					if (!meta.active) {
						meta.active = true;
						meta.onSize(); // check the position of fisheye.
					}
					meta._calc(x - meta.box.l, y - meta.box.t);
				} else if (meta.active) {
					meta.active = false;
					meta._calc(-1, -1);
				} else {
					if (meta._bodyX && meta._bodyY && x <= meta._bodyX + 30 &&
						x >= meta._bodyX - 30 &&
						y <= meta._bodyY + 30 &&
						y >= meta._bodyY - 30)
						return;
						
					meta._bodyX = x;
					meta._bodyY = y;
					var now = zUtl.now();
					if (!meta._timer || meta._timer < now) {
						meta._timer = now + 1500;
						meta.onSize(); // check the position of fisheye.
					}
				}
			};
		if (!this._onBodyOut)
			this._onBodyOut = function (evt) {
				var x = zEvt.x(evt),
					y = zEvt.y(evt);
					offs = zDom.revisedOffset(document.body),
					t = offs[1], 
					b = t + document.body.offsetHeight,
					l = offs[0],
					r = l + document.body.offsetWidth;
				if (x >= l && x <= r && y >= t && y <= b)	return;
				meta.active = false;
				meta._calc(-1, -1);
			};
		zEvt.listen(document.documentElement, "mousemove", this._onMouseMove);
		zEvt.listen(document.documentElement, "mouseout", this._onBodyOut);
		zDom.cleanVisibility(n);
	},
	getAttachEdge: function () {
		var edge = this._attachEdge;
		return edge == "center" || (this.isHor && (edge == "left" || edge == "right" )) 
			|| (!this.isHor && (edge == "top" || edge == "bottom")) ? "center" : edge;
	},
	getLabelEdge: function () {
		var edge = this._labelEdge;
		if (edge == "center" || (this.isHor && (edge == "left" || edge == "right"))) edge = "top";
		else if (!this.isHor && (edge == "top" || edge == "bottom")) edge = "left";
		return edge;
	},
	getProximitybox: function () {
		var m = (this.units - this.subts),
			l = r = this.iw * m,
			t = b = this.ih * m;
		switch (this.aEdge) {
			case "left":
				l = 0;
				break;
			case "right":
				r = 0;
				break;
			case "top":
				t = 0;
				break;
			case "bottom":
				b = 0;
				break;
			default:
				l /= 2; r /= 2; t /= 2; b /=2;
		}
		return {l: l, r: r, t: t, b: b};
	},
	syncAttr: function() {
		if(!this.getNode())
			return;
		// sync zk attributes
		this.icnt= this.nChildren;
		this.iw = this._itemWidth;
		this.ih = this._itemHeight;
		this.imw = this._itemMaxWidth;
		this.imh = this._itemMaxHeight;
		this.isHor = (this._orient == "horizontal");
		this.aEdge = this.getAttachEdge();
		this.lEdge = this.getLabelEdge();
		this.pbox = this.getProximitybox();
		this.ip = this._itemPadding;

		var bw = (this.isHor ? this.icnt : 1) * this.iw,
			bh = (this.isHor ? 1 : this.icnt) * this.ih;
			
		this.tw = this.pbox.l + this.pbox.r + bw;
		this.th = this.pbox.t + this.pbox.b + bh;
		
		for (var i=0; i<this.icnt; i++) {
			var child = this.getChildAt(i),
			  	itm = child.getNode(), img = child.getSubnode("img"),
				x = this.iw  * (this.isHor ? i : 0),
				y = this.ih * (this.isHor ? 0 : i),
				s = itm.style, is = img.style;

			itm.cenX = x + (this.iw / 2);
			itm.cenY = y + (this.ih / 2);

			var isz = this.isHor ? this.iw : this.ih, 
				r = this.units * isz,
				cn = this.isHor ? itm.cenX : itm.cenY,
				lhs = this.isHor ? this.pbox.l : this.pbox.t,
				rhs = this.isHor ? this.pbox.r : this.pbox.b,
				siz = this.isHor ? bw : bh;

			var range_lhs = r, range_rhs = r;

			if (range_lhs > cn + lhs) range_lhs = cn + lhs;
			if (range_rhs > (siz - cn + rhs)) range_rhs = siz - cn + rhs;

			itm.effectRangeLeft = range_lhs / isz;
			itm.effectRangeRght = range_rhs / isz;
			
			s.left   = x + "px";
			s.top    = y + "px";
			s.width  = this.iw + "px";
			s.height = this.ih + "px";
			
			is.left = this.ip + "%";
			is.top = this.ip + "%";
			is.width = (100 - 2 * this.ip) + "%";
			is.height = (100 - 2 * this.ip) + "%";
		}
		var n = this.getNode();
		n.style.width = bw + "px";
		n.style.height = bh + "px";
		
		this.onSize();
	},
	_calc: function(x, y) {
		if (x <= this.box.x + 4 && x >= this.box.x - 4 && y <= this.box.y + 4 && y >= this.box.y - 4)
			return; // optimize
			
		this.box.x = x;
		this.box.y = y;

		if (this.icnt <= 0) return;
		
		var pos, prx, siz, sim;
		if (this.isHor) {
			pos = x;
			prx = this.pbox.l;
			siz = this.iw;
			sim = 1.0 * this.imw;
		} else {
			pos = y;
			prx = this.pbox.t;
			siz = this.ih;
			sim = 1.0 * this.imh;
		}
		var cen = ((pos - prx) / siz) - this.subts,
			max_off_cen = (sim / siz) - this.subts;

		if (max_off_cen > this.units) max_off_cen = this.units;

		var off_weight = 0, edge = this.aEdge;

		switch (edge) {
			case "bottom":
				var cen2 = (y - this.pbox.t) / this.ih;
				off_weight = (cen2 > this.subts) ? 1 : y / (this.pbox.t + (this.ih / 2));
				break;
			case "top":
				var cen2 = (y - this.pbox.t) / this.ih;
				off_weight = (cen2 < this.subts) ? 1 : (this.th - y) / (this.pbox.b + (this.ih / 2));
				break;
			case "right":
				var cen2 = (x - this.pbox.l) / this.iw;
				off_weight = (cen2 > this.subts) ? 1 : x / (this.pbox.l + (this.iw / 2));
				break;
			case "left":
				var cen2 = (x - this.pbox.l) / this.iw;
				off_weight = (cen2 < this.subts) ? 1 : (this.tw - x) / (this.pbox.r + (this.iw / 2));
				break;
			default:
				off_weight = this.isHor ? (y / (this.th)) :	(off_weight = x / (this.tw));
				if (off_weight > this.subts) off_weight = 1 - off_weight;
				off_weight *= 2;
		}
		
		for (var i=0; i<this.icnt; i++) {
			var child = this.getChildAt(i),
				weight = this._weighAt(cen, i,child.getNode());
			if (weight < 0) weight = 0;
			this._setItemSize(child.getNode(), weight * off_weight);
		}

		var main_p = Math.round(cen), offset = 0;
		if (cen < 0) {
			main_p = 0;
		} else if (cen > this.icnt - 1) {
			main_p = this.icnt - 1;
		} else {
			offset = (cen - main_p) * ((this.isHor ? this.iw : this.ih) - this.getChildAt(main_p).getNode().sizeMain);
		}
		this._fixEl(main_p, offset);
	},
	_weighAt: function(cen, i,cn) {//cn = childNode
		var dist = Math.abs(cen - i),
			limit = ((cen - i) > 0) ? cn.effectRangeRght : cn.effectRangeLeft;
		return (dist > limit) ? 0 : (1 - dist / limit);
	},
	_setItemSize: function(cn, scale) {
		scale *= 1.0;
		var w = Math.round(this.iw  + ((this.imw  - this.iw ) * scale)),
			h = Math.round(this.ih + ((this.imh - this.ih) * scale));
		if (this.isHor) {
			cn.sizeMain = cn.sizeW = w;
			cn.sizeH = h;

			var y = 0;
			if (this.aEdge == "top") {
				y = (cn.cenY - (this.ih / 2));
			} else if (this.aEdge == "bottom") {
				y = (cn.cenY - (h - (this.ih / 2)));
			} else {
				y = (cn.cenY - (h / 2));
			}

			cn.usualX = Math.round(cn.cenX - (w / 2));
			cn.style.top  = y + "px";
			cn.style.left  = cn.usualX + "px";

		} else {
			cn.sizeW = w;
			cn.sizeMain = cn.sizeH = h;

			var x = 0;
			if (this.aEdge == "left"){
				x = cn.cenX - (this.iw / 2);
			}else if (this.aEdge == "right"){
				x = cn.cenX - (w - (this.iw / 2));
			}else{
				x = cn.cenX - (w / 2);
			}

			cn.style.left = x + "px";
			cn.usualY = Math.round(cn.cenY - (h / 2));
			cn.style.top = cn.usualY + "px";
		}

		cn.style.width = w + "px";
		cn.style.height = h + "px";
	},
	_fixEl: function(p, offset) {
		var pos = 0,
			child=this.getChildAt(p),
			cn=child.getNode();
		if (this.isHor) {
			pos = Math.round(cn.usualX + offset);
			cn.style.left = pos + "px";
		} else {
			pos = Math.round(cn.usualY + offset);
			cn.style.top = pos + "px";
		}
		this._fixLab(child);

		// position before
		for (var bpos = pos, i = p; --i >= 0;) {
			var child=this.getChildAt(i);
			var cn=child.getNode();
			bpos -= cn.sizeMain;
			if (this.isHor) {
				cn.style.left = bpos + "px";
			} else {
				cn.style.top = bpos + "px";
			}
			this._fixLab(child);
		}

		// position after
		for (var apos = pos, i = p; ++i < this.icnt;) {
			var child=this.getChildAt(i);
			var cn=child.getNode();
			apos += this.getChildAt(i-1).getNode().sizeMain;
			if (this.isHor) {
				cn.style.left = apos + "px";
			} else {
				cn.style.top = apos + "px";
			}
			this._fixLab(child);
		}
	},
	_fixLab: function(child) {
		var itm = child.getNode(), label = child.getSubnode("label"),
			x = 0, y = 0, edge = this.lEdge,
			h = child._mh + label.offsetHeight,
			w = child._mw + label.offsetWidth;
		if (!zDom.isVisible(label)) return;
		switch (edge) {
			case "top":
				x = Math.round((itm.sizeW / 2) - (w / 2));
				y = -h;
				break;
			case "bottom":
				x = Math.round((itm.sizeW / 2) - (w / 2));
				y = itm.sizeH;
				break;
			case "left":
				x = -w;
				y = Math.round((itm.sizeH / 2) - (h / 2));
				break;
			case "right":
				x = itm.sizeW;
				y = Math.round((itm.sizeH / 2) - (h / 2));
				break;
		}
		label.style.left = x + "px";
		label.style.top = y + "px";
	},
	onSize: function() {
		var offs = zDom.revisedOffset(this.getNode());
		this.box.l = offs[0] - this.pbox.l;
		this.box.t = offs[1] - this.pbox.t;
		this.box.r = this.box.l + this.tw;
		this.box.b = this.box.t + this.th;
	}
});