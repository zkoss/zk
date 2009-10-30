/* fisheye.js

{{IS_NOTE
	Purpose:
		
	Description:
		This fisheye function is based on the Dojo Foundation, which can be found here - http://dojotoolkit.org/
		, due to the performance on the Linux is not acceptable, so we rewrite this function.
	History:
		Tue Jul  8 14:43:46 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.lang.msgzul*");
zk.fisheye = zClass.create();
/**
 * Menu similar to the fish eye menu on the Mac OS
 * @since 3.5.0
 */
zk.fisheye.prototype = {
	initialize: function (cmp) {
		this.id = cmp.id;
		this.el = cmp;
		this.units = 2;
		this.subts = 0.5;
		this.box = {x: -1, y:- 1, l: -1, r: -1, t: -1, b: -1};
		zkau.setMeta(cmp, this);
		this.init();
	},
	init: function () {
		zk.disableSelection(this.el);
		this.syncAttr();
		var meta = this;
		if(!this._onMouseMove)
			this._onMouseMove = function (evt) {
				if (zkau.processing() || !meta || !meta.el || !zk.isRealVisible(meta.el)) return;
				var x = Event.pointerX(evt),
					y = Event.pointerY(evt);
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
					var now = $now();
					if (!meta._timer || meta._timer < now) {
						meta._timer = now + 1500;
						meta.onSize(); // check the position of fisheye.
					}
				}
			};
		if (!this._onBodyOut)
			this._onBodyOut = function (evt) {
				if (zkau.processing() || !meta || !meta.el || !zk.isRealVisible(meta.el)) return;
				var x = Event.pointerX(evt),
					y = Event.pointerY(evt),
					offs = zk.revisedOffset(document.body),
					t = offs[1], 
					b = t + document.body.offsetHeight,
					l = offs[0],
					r = l + document.body.offsetWidth;
				if (x >= l && x <= r && y >= t && y <= b)	return;
				meta.active = false;
				meta._calc(-1, -1);
			};
		zk.listen(document.documentElement, "mousemove", this._onMouseMove);
		zk.listen(document.documentElement, "mouseout", this._onBodyOut);
		zk.cleanVisibility(this.el);
	},
	getAttachEdge: function () {
		var edge = getZKAttr(this.el, "attachedge") || "center";
		return edge == "center" || (this.isHor && (edge == "left" || edge == "right" )) 
			|| (!this.isHor && (edge == "top" || edge == "bottom")) ? "center" : edge;
	},
	getLabelEdge: function () {
		var edge = getZKAttr(this.el, "labeledge") || "top";
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
		// sync zk attributes
		this.children = zk.childNodes($e(this.el, "cave"));
		this.icnt = this.children.length;
		this.iw = $int(getZKAttr(this.el, "itemwidth"));
		this.ih = $int(getZKAttr(this.el, "itemheight"));
		this.imw = $int(getZKAttr(this.el, "itemmaxwidth"));
		this.imh = $int(getZKAttr(this.el, "itemmaxheight"));
		this.isHor = getZKAttr(this.el, "orient") == "horizontal";
		this.aEdge = this.getAttachEdge();
		this.lEdge = this.getLabelEdge();
		this.pbox = this.getProximitybox();
		this.ip = getZKAttr(this.el, "itempadding");

		var bw = (this.isHor ? this.icnt : 1) * this.iw,
			bh = (this.isHor ? 1 : this.icnt) * this.ih,
			c = this.children;
			
		this.tw = this.pbox.l + this.pbox.r + bw;
		this.th = this.pbox.t + this.pbox.b + bh;

		for (var i = 0; i < this.icnt; i++) {
			var itm = c[i], img = $e(itm, "img"),
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
		this.el.style.width = bw + "px";
		this.el.style.height = bh + "px";
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
		
		for(var i = 0; i < this.icnt; i++) {
			var weight = this._weighAt(cen, i);
			if (weight < 0) weight = 0;
			this._setItemSize(i, weight * off_weight);
		}

		var main_p = Math.round(cen), offset = 0;
		if (cen < 0) {
			main_p = 0;
		} else if (cen > this.icnt - 1) {
			main_p = this.icnt - 1;
		} else {
			offset = (cen - main_p) * ((this.isHor ? this.iw : this.ih) - this.children[main_p].sizeMain);
		}
		this._fixEl(main_p, offset);
	},
	_weighAt: function(cen, i) {
		var dist = Math.abs(cen - i),
			limit = ((cen - i) > 0) ? this.children[i].effectRangeRght : this.children[i].effectRangeLeft;
		return (dist > limit) ? 0 : (1 - dist / limit);
	},
	_setItemSize: function(p, scale) {
		scale *= 1.0;
		var c = this.children,
			w = Math.round(this.iw  + ((this.imw  - this.iw ) * scale)),
			h = Math.round(this.ih + ((this.imh - this.ih) * scale));
		if (this.isHor) {
			c[p].sizeMain = c[p].sizeW = w;
			c[p].sizeH = h;

			var y = 0;
			if (this.aEdge == "top") {
				y = (c[p].cenY - (this.ih / 2));
			} else if (this.aEdge == "bottom") {
				y = (c[p].cenY - (h - (this.ih / 2)));
			} else {
				y = (c[p].cenY - (h / 2));
			}

			c[p].usualX = Math.round(c[p].cenX - (w / 2));
			c[p].style.top  = y + "px";
			c[p].style.left  = c[p].usualX + "px";

		} else {
			c[p].sizeW = w;
			c[p].sizeMain = c[p].sizeH = h;

			var x = 0;
			if (this.aEdge == "left"){
				x = c[p].cenX - (this.iw / 2);
			}else if (this.aEdge == "right"){
				x = c[p].cenX - (w - (this.iw / 2));
			}else{
				x = c[p].cenX - (w / 2);
			}

			c[p].style.left = x + "px";
			c[p].usualY = Math.round(c[p].cenY - (h / 2));
			c[p].style.top = c[p].usualY + "px";
		}

		c[p].style.width = w + "px";
		c[p].style.height = h + "px";
	},
	_fixEl: function(p, offset) {
		var pos = 0, c = this.children;
		if (this.isHor) {
			pos = Math.round(c[p].usualX + offset);
			c[p].style.left = pos + "px";
		} else {
			pos = Math.round(c[p].usualY + offset);
			c[p].style.top = pos + "px";
		}
		this._fixLab(c[p]);

		// position before
		for (var bpos = pos, i = p; --i >= 0;) {
			bpos -= c[i].sizeMain;
			if (this.isHor) {
				c[i].style.left = bpos + "px";
			} else {
				c[i].style.top = bpos + "px";
			}
			this._fixLab(c[i]);
		}

		// position after
		for (var apos = pos, i = p; ++i < this.icnt;) {
			apos += c[i - 1].sizeMain;
			if (this.isHor) {
				c[i].style.left = apos + "px";
			} else {
				c[i].style.top = apos + "px";
			}
			this._fixLab(c[i]);
		}
	},
	_fixLab: function(itm) {
		var cmp = itm.parentNode, label = $e(itm, "label"),
			x = 0, y = 0, edge = this.lEdge,
			h = itm.mh + label.offsetHeight,
			w = itm.mw + label.offsetWidth;
		if (!zk.isVisible(label)) return;
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
		var offs = zk.revisedOffset(this.el);
		this.box.l = offs[0] - this.pbox.l;
		this.box.t = offs[1] - this.pbox.t;
		this.box.r = this.box.l + this.tw;
		this.box.b = this.box.t + this.th;
	},
	cleanup: function () {
		zk.unlisten(document.documentElement, "mousemove", this._onMouseMove);
		zk.unlisten(document.documentElement, "mouseout", this._onBodyOut);
		this.el = this.pbox = this.box = this.children = this._onMouseMove = this._onBodyOut = null;
	}
};
/**
 * A Fisheyebar component.
 * @since 3.5.0
 */
zkFisheyebar = {
	init: function(cmp) {
		var meta = zkau.getMeta(cmp);
		if (meta) 
			meta.init();
		else 
			new zk.fisheye(cmp);
	},
	onSize: function (cmp) {
		var meta = zkau.getMeta(cmp);
		if (meta) meta.onSize();
	},
	setAttr: function (cmp, nm, val) {
		var meta = zkau.getMeta(cmp);
		if (meta) {
			switch (nm) {
				case "z.childchg":
				case "z.itemwidth":
				case "z.itemheight":
				case "z.itemmaxwidth":
				case "z.itemmaxheight":
				case "z.itempadding":
				case "z.attachedge":
				case "z.labeledge":
				case "z.orient":
					zkau.setAttr(cmp, nm, val);
					meta.syncAttr();
					meta.onSize();
					return true;
			}
		}
		return false;
	},
	childchg: function (cmp) {
		var meta = zkau.getMeta(cmp);
		if (meta) meta.syncAttr();
	}
};
/**
 * A Fisheye component.
 * @since 3.5.0
 */
zkFisheye = {
	init: function (cmp) {
		zk.disableSelection(cmp);
		var img = $e(cmp, "img");
		if (zk.ie6Only && img.src.endsWith(".png")) {
			img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+img.src+"', sizingMethod='scale')";
			img.src = zk.getUpdateURI('/web/img/spacer.gif');
		}
		var label = $e(cmp, "label");
		
		// store the two attributes for better performance.
		cmp.mh = zk.getMarginHeight(label);
		cmp.mw = zk.getMarginWidth(label);
		
		zk.listen(img, "mouseover", this.onMouseOver);
		zk.listen(img, "mouseout", this.onMouseOut);
		zk.listen(cmp, "click", zkau.onclick);
	},
	onMouseOver: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $parentByType(Event.element(evt), "Fisheye"),
			label = $e(cmp, "label");
			
		if (getZKAttr(cmp, "label") == "true") {
			label.style.display = "block";
			label.style.visibility = "hidden";
		}
		var meta = zkau.getMeta(cmp.parentNode);
		if (meta) {
			if (!meta.active)
				meta.active = true;
			meta._fixLab(cmp);
		}
		zk.cleanVisibility(label);
	},
	onMouseOut: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $parentByType(Event.element(evt), "Fisheye");
		$e(cmp, "label").style.display = "none";
	}
};
