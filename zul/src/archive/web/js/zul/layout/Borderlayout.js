/* Borderlayout.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.Borderlayout = zk.$extends(zul.Widget, {
	setResize: function () {
		this.resize();
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.North))
			this.north = child;
		else if (child.$instanceof(zul.layout.South))
			this.south = child;
		else if (child.$instanceof(zul.layout.Center))
			this.center = child;
		else if (child.$instanceof(zul.layout.West))
			this.west = child;
		else if (child.$instanceof(zul.layout.East))
			this.east = child;
		this.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.north)
			this.north = null;
		else if (child == this.south)
			this.south = null;
		else if (child == this.center)
			this.center = null;
		else if (child == this.west)
			this.west = null;
		else if (child == this.east)
			this.east = null;
		this.resize();
	},
	getZclass: function () {
		return this._zclass == null ? "z-border-layout" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	},
	// private
	// returns the ambit of the specified cmp for region calculation. 
	_getAmbit: function (wgt, ignoreSplit) {
		var ambit, mars = this._getMargins(wgt), region = wgt.getPosition();
		if (region && !wgt.isOpen()) {
			var colled = wgt.getSubnode('colled');
			ambit = {
				x: mars.left,
				y: mars.top,
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
			ignoreSplit = true;
		} else {
			var w = wgt.getWidth() || '',
				h = wgt.getHeight() || '',
				widx = w.indexOf('%'),
				hidx = h.indexOf('%');
			ambit = {
				x: mars.left,
				y: mars.top,
				w: widx > 0 ?
					Math.max(
						Math.floor(this.getNode().offsetWidth * zk.parseInt(w.substring(0, widx)) / 100),
						0) : wgt.getSubnode('real').offsetWidth, 
				h: hidx > 0 ?
					Math.max(
						Math.floor(this.getNode().offsetHeight * zk.parseInt(h.substring(0, hidx)) / 100),
						0) : wgt.getSubnode('real').offsetHeight
			};
		}
		var split = ignoreSplit ? {offsetHeight:0, offsetWidth:0} : wgt.getSubnode('split') || {offsetHeight:0, offsetWidth:0};
		if (!ignoreSplit) wgt._fixSplit();
		switch (region) {
		case this.$class.NORTH:
		case this.$class.SOUTH:
			ambit.h += split.offsetHeight;
			ambit.ts = ambit.y + ambit.h + mars.bottom; // total size;
			ambit.w = mars.left + mars.right;
			if (region == 'south')
				ambit.y = ambit.h + mars.bottom;
			break;
		case this.$class.WEST:
		case this.$class.EAST:
			ambit.h = mars.top + mars.bottom;
			ambit.w += split.offsetWidth;
			ambit.ts = ambit.x + ambit.w + mars.right; // total size;
			if (region == 'east')
				ambit.x = ambit.w + mars.right; 
			break;
		}
		return ambit;
	},
	_getMargins: function (wgt) {
		return this._arrayToObject(wgt.isOpen() ? wgt._margins : wgt._cmargins);
	},
	resize: function () {
		if (this.desktop)
			this._resize();
	},
	_resize: function (isOnSize) {
		this._isOnSize = isOnSize;
		if (!this.isRealVisible()) return;
		var el = this.getNode(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			center = { 
				x: 0,
				y: 0,
				w: width,
				h: height
			};
		for (var region, ambit, margin,	rs = ['north', 'south', 'west', 'east'],
				j = 0, k = rs.length; j < k; ++j) {
			region = this[rs[j]];
			if (region && zk(region.getNode()).isVisible()) {
				ambit = this._getAmbit(region);
				switch (rs[j]) {
				case 'north':
				case 'south':
					ambit.w = width - ambit.w;
					if (rs[j] == 'north') 
						center.y = ambit.ts;
					else
						ambit.y = height - ambit.y;
					center.h -= ambit.ts;
					break;
				default:
					ambit.y += center.y;
					ambit.h = center.h - ambit.h;
					if (rs[j] == 'east')
						ambit.x = width - ambit.x;
					else center.x += ambit.ts;
					center.w -= ambit.ts;
					break;
				}
				this._resizeWgt(region, ambit);
			}
		}
		if (this.center && zk(this.center.getNode()).isVisible()) {
			var mars = this._getMargins(this.center);
			center.x += mars.left;
			center.y += mars.top;
			center.w -= mars.left + mars.right;
			center.h -= mars.top + mars.bottom;
			this._resizeWgt(this.center, center);
		}
		zk(el).cleanVisibility();
		this._isOnSize = false; // reset
	},
	_arrayToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	},
	_resizeWgt: function (wgt, ambit, ignoreSplit) {
		if (wgt.isOpen()) {
			if (!ignoreSplit && wgt.getSubnode('split')) {
				wgt._fixSplit();
				 ambit = this._resizeSplit(wgt, ambit);
			}
			var s = wgt.getSubnode('real').style; 
			s.left = ambit.x + "px";
			s.top = ambit.y + "px";
			this._resizeBody(wgt, ambit);
		} else {
			wgt.getSubnode('split').style.display = "none";
			var colled = wgt.getSubnode('colled');
			if (colled) {
				colled.style.left = ambit.x + "px";
				colled.style.top = ambit.y + "px";
				var $colled = zk(colled);
				colled.style.height = $colled.revisedHeight(ambit.h) + "px";
				colled.style.width = $colled.revisedWidth(ambit.w) + "px";
			}
		}
	},
	_resizeSplit: function (wgt, ambit) {
		var split = wgt.getSubnode('split');
		if (!zk(split).isVisible()) return ambit;
		var sAmbit = {
				w: split.offsetWidth, 
				h: split.offsetHeight
			},
			s = split.style;
		switch (wgt.getPosition()) {
		case this.$class.NORTH:
			ambit.h -= sAmbit.h;
		  	s.left = ambit.x + "px";
			s.top = (ambit.y + ambit.h) + "px";
			s.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
			break;
		case this.$class.SOUTH:
			ambit.h -= sAmbit.h;
			ambit.y += sAmbit.h;
			s.left = ambit.x + "px";
			s.top = (ambit.y - sAmbit.h) + "px";
			s.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
			break;
		case this.$class.WEST:
			ambit.w -= sAmbit.w;
			s.left = (ambit.x + ambit.w) + "px";
			s.top = ambit.y + "px";
			s.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
			break;
		case this.$class.EAST:
			ambit.w -= sAmbit.w;
			s.left = ambit.x + "px";
			s.top = ambit.y + "px";
			s.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
			ambit.x += sAmbit.w;
			break;
		}
		return ambit;
	},
	_resizeBody: function (wgt, ambit) {
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var el = wgt.getSubnode('real'),
			bodyEl = wgt.isFlex() && wgt.firstChild ?
						wgt.firstChild.getNode() : wgt.getSubnode('cave');
		if (!this._ignoreResize(el, ambit.w, ambit.h)) {
			ambit.w = zk(el).revisedWidth(ambit.w);
			el.style.width = ambit.w + "px";
			ambit.w = zk(bodyEl).revisedWidth(ambit.w);
			bodyEl.style.width = ambit.w + "px";

			ambit.h = zk(el).revisedHeight(ambit.h);
			el.style.height = ambit.h + "px";
			ambit.h = zk(bodyEl).revisedHeight(ambit.h);
			if (wgt.getSubnode('cap')) ambit.h = Math.max(0, ambit.h - wgt.getSubnode('cap').offsetHeight);
			bodyEl.style.height = ambit.h + "px";
			if (wgt.isAutoscroll()) { 
				bodyEl.style.overflow = "auto";
				bodyEl.style.position = "relative";
			} else {
				bodyEl.style.overflow = "hidden";
				bodyEl.style.position = "";
			}
			if (!this._isOnSize) {
				zWatch.fireDown('beforeSize', null, wgt);
				zWatch.fireDown('onSize', null, wgt);
			}
		}
	},
	_ignoreResize : function(el, w, h) { 
		if (el._lastSize && el._lastSize.width == w && el._lastSize.height == h) {
			return true;
		} else {
			el._lastSize = {width: w, height: h};
			return false;
		}
	},
	//zWatch//
	onSize: _zkf = function () {
		this._resize(true);
	},
	onShow: _zkf
}, {
	NORTH: "north",
	SOUTH: "south",
	EAST: "east",
	WEST: "west",
	CENTER: "center"
});
