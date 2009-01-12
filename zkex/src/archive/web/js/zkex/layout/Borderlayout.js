/* Borderlayout.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.layout.Borderlayout = zk.$extends(zul.Widget, {
	setResize: function () {
		this.resize();
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zkex.layout.North))
			this.north = child;
		else if (child.$instanceof(zkex.layout.South))
			this.south = child;
		else if (child.$instanceof(zkex.layout.Center))
			this.center = child;
		else if (child.$instanceof(zkex.layout.West))
			this.west = child;
		else if (child.$instanceof(zkex.layout.East))
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
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);		
		this.$supers('unbind_', arguments);
	},
	// private
	// returns the ambit of the specified cmp for region calculation. 
	_getAmbit: function (wgt, ignoreSplit) {
		var region = wgt.getPosition();
		if (region && !wgt.isOpen())
			return {
				w: wgt.ecolled ? wgt.ecolled.offsetWidth : 0,
				h: wgt.ecolled ? wgt.ecolled.offsetHeight : 0
			};
		
		var w = wgt.getWidth() || '',
			h = wgt.getHeight() || '',
			widx = w.indexOf('%'),
			hidx = h.indexOf('%');
			
		var ambit = {
			w: widx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetWidth * zk.parseInt(w.substring(0, widx)) / 100),
					0) : wgt.ereal.offsetWidth, 
			h: hidx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetHeight * zk.parseInt(h.substring(0, hidx)) / 100),
					0) : wgt.ereal.offsetHeight
		};
		if (region && !ignoreSplit) {
			var split = wgt.esplit || {offsetHeight:0, offsetWidth:0};
			wgt._fixSplit();
			switch (region) {
				case this.$class.NORTH:
				case this.$class.SOUTH:
					ambit.h += split.offsetHeight;
					break;
				case this.$class.WEST:
				case this.$class.EAST:
					ambit.w += split.offsetWidth;
					break;
			}
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
		if (!this.isRealVisible()) return;
		this._isOnSize = isOnSize;
		var el = this.getNode(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			cW = width,
			cH = height,
			cY = 0,
			cX = 0;
			
		if (this.north && zDom.isVisible(this.north.getNode())) {
			var ambit = this._getAmbit(this.north),
				mars = this._getMargins(this.north);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = mars.top;
			cY = ambit.h + ambit.y + mars.bottom;
			cH -= cY;
			this._resizeWgt(this.north, ambit);
		}
		if (this.south && zDom.isVisible(this.south.getNode())) {
			var ambit = this._getAmbit(this.south),
				mars = this._getMargins(this.south),
				total = (ambit.h + mars.top + mars.bottom);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = height - total + mars.top;
			cH -= total;
			this._resizeWgt(this.south, ambit);
		}
		if (this.west && zDom.isVisible(this.west.getNode())) {
			var ambit = this._getAmbit(this.west),
				mars = this._getMargins(this.west),
				total = (ambit.w + mars.left + mars.right);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = mars.left;
			ambit.y = cY + mars.top;
			cX += total;
			cW -= total;
			this._resizeWgt(this.west, ambit);
		}
		if (this.east && zDom.isVisible(this.east.getNode())) {
			var ambit = this._getAmbit(this.east),
				mars = this._getMargins(this.east),
				total = (ambit.w + mars.left + mars.right);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = width - total + mars.left;
			ambit.y = cY + mars.top;
			cW -= total;
			this._resizeWgt(this.east, ambit);
		}
		if (this.center && zDom.isVisible(this.center.getNode())) {
			var mars = this._getMargins(this.center),
				ambit = {
					x: cX + mars.left,
					y: cY + mars.top,
					w: cW - (mars.left + mars.right),
					h: cH - (mars.top + mars.bottom)
				};
			this._resizeWgt(this.center, ambit);
		}
		zDom.cleanVisibility(el);
		this._isOnSize = false; // reset
	},
	_arrayToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	},
	_resizeWgt: function (wgt, ambit, ignoreSplit) {
		if (wgt.isOpen()) {
			if (!ignoreSplit && wgt.esplit) {
				wgt._fixSplit();
				 ambit = this._resizeSplit(wgt, ambit);	
			}
			wgt.ereal.style.left = ambit.x + "px";
			wgt.ereal.style.top = ambit.y + "px";
			this._resizeBody(wgt, ambit);
		} else {
			wgt.esplit.style.display = "none";
			if (wgt.ecolled) {
				wgt.ecolled.style.left = ambit.x + "px";
				wgt.ecolled.style.top = ambit.y + "px";
				wgt.ecolled.style.height = zDom.revisedHeight(wgt.ecolled, ambit.h) + "px";
				wgt.ecolled.style.width = zDom.revisedWidth(wgt.ecolled, ambit.w) + "px";
			}
		}
	},
	_resizeSplit: function (wgt, ambit) {	
		if (!zDom.isVisible(wgt.esplit)) return ambit;
		var sAmbit = {
				w: wgt.esplit.offsetWidth, 
				h: wgt.esplit.offsetHeight
			},
			s = wgt.esplit.style;
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
		var el = wgt.ereal,
			bodyEl = wgt.isFlex() && wgt.firstChild ?
						wgt.firstChild.getNode() : wgt.ecave;
		if (!this._ignoreResize(el, ambit.w, ambit.h)) {
			ambit.w = zDom.revisedWidth(el, ambit.w);
			el.style.width = ambit.w + "px";	   			
			ambit.w = zDom.revisedWidth(bodyEl, ambit.w);
			bodyEl.style.width = ambit.w + "px";
			
			ambit.h = zDom.revisedHeight(el, ambit.h);
			el.style.height = ambit.h + "px";
			ambit.h = zDom.revisedHeight(bodyEl, ambit.h);
			if (wgt.ecaption) ambit.h = Math.max(0, ambit.h - wgt.ecaption.offsetHeight);
			bodyEl.style.height = ambit.h + "px";
			if (wgt.isAutoscroll()) { 
				bodyEl.style.overflow = "auto";				
				bodyEl.style.position = "relative";
			} else {
				bodyEl.style.overflow = "hidden";							
				bodyEl.style.position = "";
			}
			if (!this._isOnSize) {
				zWatch.fireDown('beforeSize', -1, wgt);
				zWatch.fireDown('onSize', -1, wgt);
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
	onVisible: _zkf
}, {	
	NORTH: "north",
	SOUTH: "south",
	EAST: "east",
	WEST: "west",
	CENTER: "center"
});
