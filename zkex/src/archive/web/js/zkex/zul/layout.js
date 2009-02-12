/* layout.js

{{IS_NOTE
	Purpose:
		
	Description:
	
	History:
		Aug 27, 2007 5:46:09 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.Layout = Class.create();
zk.Layout.prototype = {
	initialize: function (cmp) {
		this.id = cmp.id;			
		this.el = cmp;
		zkau.setMeta(cmp, this);
		var meta = this; //the nested function only see local var		
		this._regions = {};		
	},
	addRegion: function (region, ambit) {
		this._regions[region] = ambit;
	},
	removeRegion: function (region) {
		delete this._regions[region];
	},
	getRegion: function (region) {
		return this._regions[region];
	},
	_getAmbit: function (cmp, region) {
		var xy = Position.positionedOffset(cmp);
		var w = cmp.style.width, h = cmp.style.height;
		var widx = w.indexOf("%");
		var hidx = h.indexOf("%");
		if (widx > 0) cmp._width = $int(w.substring(0, widx));
		if (hidx > 0) cmp._height = $int(h.substring(0, hidx));
		var ambit = {
		 	x: xy[0], 
			y: xy[1], 
			width: cmp._width ?  Math.max(Math.floor(this.el.offsetWidth * cmp._width / 100), 0) : cmp.offsetWidth, 
			height: cmp._height ?  Math.max(Math.floor(this.el.offsetHeight * cmp._height / 100), 0) : cmp.offsetHeight
		};
		if (region) {
			switch (region) {
				case "north":
					ambit.height += cmp.split.offsetHeight;
					break;
				case "south":
					ambit.height += cmp.split.offsetHeight;
					ambit.y -= cmp.split.offsetHeight;
					break;
				case "west": 
					ambit.width += cmp.split.offsetWidth;
					break;
				case "east":
					ambit.width += cmp.split.offsetWidth;
					ambit.x -= cmp.split.offsetWidth;
					break;
			}
		}
		return ambit;
	},
	render: function (isOnSize) {
		this.isOnSize = isOnSize;
		if (!zk.isRealVisible(this.el)) return;
		var width = this.el.offsetWidth, height = this.el.offsetHeight;
		var cW = width, cH = height, cY = 0, cX = 0;
		var n = this.getRegion("north"), s = this.getRegion("south"), 
			w = this.getRegion("west"), e = this.getRegion("east"), 
			c = this.getRegion("center");
		if (n && zk.isRealVisible(n)) {
			var ambit = this._getAmbit(n, "north");
			var margin = this._paserMargin(getZKAttr(n, "mars") || "0,0,0,0");
			ambit.width = width - (margin.left + margin.right);
			ambit.x = margin.left;
			ambit.y = margin.top;
			cY = ambit.height + ambit.y + margin.bottom;
			cH -= cY;
			ambit = this._resizeSplit(n, ambit, "north");
			this._resize(n, ambit);
		} else if (n && zk.isRealVisible(n.parentNode)) {
			var ambit = this._getAmbit(n.split);			
			ambit.width = width ;
			ambit.x = ambit.y = 0;
			cY = ambit.height + ambit.y;
			cH -= cY;
			this._resizeSplit(n, ambit, "north");
		}
		if (s && zk.isRealVisible(s)) {
			var ambit = this._getAmbit(s, "south");			
			var margin = this._paserMargin(getZKAttr(s, "mars") || "0,0,0,0");
			ambit.width = width - (margin.left + margin.right);
			ambit.x = margin.left;
			var total = (ambit.height + margin.top + margin.bottom);
			ambit.y = height - total + margin.top;
			cH -= total;
			ambit = this._resizeSplit(s, ambit, "south");
			this._resize(s, ambit);
		} else if (s && zk.isRealVisible(s.parentNode)) {
			var ambit = this._getAmbit(s.split);			
			ambit.width = width ;
			ambit.x = 0;
			ambit.y = height - ambit.height;	
			cH -= ambit.height;
			this._resizeSplit(s, ambit, "south");
		}
		if (w && zk.isRealVisible(w)) {
			var ambit = this._getAmbit(w, "west");			
			var margin = this._paserMargin(getZKAttr(w, "mars") || "0,0,0,0");
			ambit.height = cH - (margin.top + margin.bottom);
			ambit.x = margin.left;
			ambit.y = cY + margin.top;
			var total = (ambit.width + margin.left + margin.right);
			cX += total;
			cW -= total;	
			ambit = this._resizeSplit(w, ambit, "west");		
			this._resize(w, ambit);
		} else if (w && zk.isRealVisible(w.parentNode)) {
			var ambit = this._getAmbit(w.split);			
			ambit.height = cH ;
			ambit.x = 0;
			ambit.y = cY;	
			cX += ambit.width;
			cW -= ambit.width;
			this._resizeSplit(w, ambit, "west");
		}
		if (e && zk.isRealVisible(e)) {
			var ambit = this._getAmbit(e, "east");
			var margin = this._paserMargin(getZKAttr(e, "mars") || "0,0,0,0");
			ambit.height = cH - (margin.top + margin.bottom);
			var total = (ambit.width + margin.left + margin.right);
			ambit.x = width - total + margin.left;
			ambit.y = cY + margin.top;
			cW -= total;
			ambit = this._resizeSplit(e, ambit, "east");
			this._resize(e, ambit);
		} else if (e && zk.isRealVisible(e.parentNode)) {
			var ambit = this._getAmbit(e.split);			
			ambit.height = cH ;
			ambit.x = width - ambit.width;
			ambit.y = cY;	
			cW -= ambit.width;								
			this._resizeSplit(e, ambit, "east");
		} 
		if (c) {
			var margin = this._paserMargin(getZKAttr(c, "mars") || "0,0,0,0");
			var ambit = {
				x: cX + margin.left,
				y: cY + margin.top,
				width: cW - (margin.left + margin.right),
				height: cH - (margin.top + margin.bottom)
			};			
			this._resize(c, ambit);
		}
		zk.cleanVisibility(this.el);
	},
	_paserMargin: function (val) {
		var ms = val.split(",");
		return {top: $int(ms[0]), left: $int(ms[1]), right: $int(ms[2]), bottom: $int(ms[3])};
	},
	_resize: function (cmp, ambit) {
		cmp.style.left = ambit.x + "px";
		cmp.style.top = ambit.y + "px";		
		this._resizeBody(cmp, ambit);		
	},
	_resizeSplit: function (cmp, ambit, region) {	
		if (region == "north" || region == "south") cmp.split.splitbtn.style.marginLeft = ((ambit.width - cmp.split.splitbtn.offsetWidth) / 2)+"px";
		else cmp.split.splitbtn.style.marginTop = ((ambit.height - cmp.split.splitbtn.offsetHeight) / 2)+"px";
		var sAmbit = this._getAmbit(cmp.split);
		switch(region){
			case "north":
				ambit.height -= sAmbit.height;
			  	cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = (ambit.y + ambit.height) + "px";
				cmp.split.style.width = (ambit.width < 0 ? 0 : ambit.width) + "px";
				break;
			case "south":
				ambit.height -= sAmbit.height;
				ambit.y += sAmbit.height;
				cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = (ambit.y - sAmbit.height) + "px";
				cmp.split.style.width = (ambit.width < 0 ? 0 : ambit.width) + "px";
				break;
			case "west":
				ambit.width -= sAmbit.width;
				cmp.split.style.left = (ambit.x + ambit.width) + "px";
				cmp.split.style.top = ambit.y + "px";
				cmp.split.style.height = (ambit.height < 0 ? 0 : ambit.height) + "px";
				break;
			case "east":
				ambit.width -= sAmbit.width;
				cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = ambit.y + "px";
				cmp.split.style.height = (ambit.height < 0 ? 0 : ambit.height) + "px";
				ambit.x += sAmbit.width;
				break;					
		}
		return ambit;
	},
	_resizeBody: function (cmp, ambit) {		
		ambit.width = Math.max(0, ambit.width);
		ambit.height = Math.max(0, ambit.height);
		var bodyEl;		
		var cid = getZKAttr(cmp, "cid");
		if (getZKAttr(cmp, "flex") == "true" && cid != "zk_n_a") {
			bodyEl = $e(getZKAttr(cmp, "cid"));
		} else {
			bodyEl = $e($uuid(cmp) + "!cave");			
		} 	
		cmp.bodyEl = bodyEl;
		if (!this.ignoreResize(bodyEl, ambit.width, ambit.height)) {
			ambit.width = zk.revisedSize(cmp, ambit.width);
			cmp.style.width = ambit.width + "px";	   			
			ambit.width = zk.revisedSize(bodyEl, ambit.width);
			bodyEl.style.width = ambit.width + "px";
			
			ambit.height = zk.revisedSize(cmp, ambit.height, true);
			cmp.style.height = ambit.height + "px";
			ambit.height = zk.revisedSize(bodyEl, ambit.height, true);
			bodyEl.style.height = ambit.height + "px";
			if (getZKAttr(cmp, "autoscl") == "true") { 
				bodyEl.style.overflow = "auto";				
				bodyEl.style.position = "relative";
				setZKAttr(bodyEl, "autoscl", "true");
			} else if (getZKAttr(bodyEl, "autoscl")) {
				bodyEl.style.overflow = "hidden";							
				bodyEl.style.position = "";
				rmZKAttr(bodyEl, "autoscl");
			}
			if (!this.isOnSize) {
				zk.beforeSizeAt(bodyEl);
				zk.onSizeAt(bodyEl); // Bug #1862935
			}
		}
	},
	ignoreResize : function(cmp, w, h) { 
		if (cmp._lastSize && cmp._lastSize.width == w && cmp._lastSize.height == h) {
			return true;
		} else {
			cmp._lastSize = {width: w, height: h};
			return false;
		}
	},
	cleanup: function ()  {
		this.el = this._regions = null;
	}
};
/**
 * To notify the component that the parent is scrolled.
 * @since 3.0.6
 */
zk.Layout.onscroll = function (evt) {
	if (!evt) evt = window.event;
	zk.onScrollAt(Event.element(evt));
};
zk.Layout.getOwnerLayout = function (cmp, cleanup) {
	var bl = $parentByType(cmp, "BorderLayout");
	var meta = zkau.getMeta(bl);
	if (meta || cleanup) return meta;
	else return new zk.Layout(bl);
};
zk.Layout.getRootLayout = function (el) {
	for (; el; el = $parentByType($real(el), "BorderLayout")) {
		var lr = $e($uuid(el.parentNode));
		if ($type(lr) == "LayoutRegion") {
			 el = lr;
		} else return el;
	}	
};
zk.Layout.cumulativeOffset = function (element, rootelemnt) {
	var valueT = 0, valueL = 0;
	do {
		if (rootelemnt && element == rootelemnt)break;
		if (Element.getStyle(element, "position") == 'fixed') {
			valueT += zk.innerY() + element.offsetTop;
			valueL += zk.innerX() + element.offsetLeft;
			break;
		} else {
			valueT += element.offsetTop  || 0;
			valueL += element.offsetLeft || 0;			
			element = zk.gecko && element != document.body ? Position.offsetParent(element): element.offsetParent;
		}
	} while (element);
	return [valueL, valueT];
};
zkBorderLayout = {};
zkLayoutRegion = {};
zkBorderLayout.childchg = function (cmp) {
	var layout = zk.Layout.getOwnerLayout(cmp);
	if (layout) layout.render();
};
zkBorderLayout.onVisi = zkBorderLayout.onSize = function (cmp) {
	var layout = zk.Layout.getOwnerLayout(cmp);
	if (layout) layout.render(true);
};
zkBorderLayout.setAttr = function (cmp, nm, val) {
	switch (nm) {
		case "z.resize" :
		var meta = zkau.getMeta(cmp);	
		if (meta) meta.render();
		return true;
	}
	return false;
};

zkLayoutRegion.init = function (cmp) {
	var split = $e(cmp.id + "!split");	
	cmp = $real(cmp);
	if (!zk.isVisible(cmp)) $outer(cmp).style.display = "none";
	var pos = getZKAttr(cmp, "pos");
	if (split) {
		cmp.split = split;
		cmp.split.pos = pos;
		cmp.split.splitbtn = $e($uuid(cmp) + "!splitbtn");
		zkLayoutRegionSplit.init(split);
	}
	
	if (getZKAttr(cmp, "autoscl") == "true") { 
		var cid = getZKAttr(cmp, "cid"), bodyEl = (getZKAttr(cmp, "flex") == "true" && cid != "zk_n_a") ?
			$e(getZKAttr(cmp, "cid")) : $e($uuid(cmp) + "!cave");
		zk.listen(bodyEl, "scroll", zk.Layout.onscroll);
	}
	var layout = zk.Layout.getOwnerLayout(cmp);	
	if (layout)	layout.addRegion(pos, cmp);
};
zkLayoutRegion.cleanup = function (cmp) {
	cmp = $real(cmp);		
	var layout = zk.Layout.getOwnerLayout(cmp, true);	// Bug #1814702
	if (cmp.split) {
		if (layout) layout.removeRegion(cmp.split.pos);
		var dg = zkLayoutRegionSplit._drags[cmp.split.id];
		if (dg) {
			delete zkLayoutRegionSplit._drags[cmp.split.id];
			dg.drag.destroy();
		}
		cmp.split.splitbtn = null;
		cmp.split = null;
	} else if (layout) {
		layout.removeRegion(getZKAttr(cmp, "pos"));
	}
	cmp.bodyEl = null;
	if (layout) {
		zk.beforeSizeAt(layout.el);
		zk.onSizeAt(layout.el);
	}
};
zkLayoutRegion.setAttr = function (cmp, nm, val) {
	cmp = $real(cmp);
	switch (nm) {
		case "visibility":
			cmp.style.display = val == "true" ? "" : "none";
			$outer(cmp).style.display = cmp.style.display;
			zk.Layout.getOwnerLayout(cmp).render();
			return true;	
		case "z.cid" :
			setZKAttr(cmp, "cid", val); 
			return true;
		case "z.mars" :
			setZKAttr(cmp, "mars", val); 
			var l = zk.Layout.getOwnerLayout(cmp);
			if (l) l.render();
			return true;
		case "z.maxs" :			
			setZKAttr(cmp, "maxs", val);
			return true; 
		case "z.mins" :			
			setZKAttr(cmp, "mins", val);
			return true; 
		case "z.autoscl" :
			setZKAttr(cmp, "autoscl", val);
			var cid = getZKAttr(cmp, "cid"), bodyEl = (getZKAttr(cmp, "flex") == "true" && cid != "zk_n_a") ?
				$e(getZKAttr(cmp, "cid")) : $e($uuid(cmp) + "!cave");
			if (val == "true") { 
				cmp.bodyEl.style.overflow = "auto";				
				cmp.bodyEl.style.position = "relative";
				zk.listen(bodyEl, "scroll", zk.Layout.onscroll);
			} else { 
				cmp.bodyEl.style.overflow = "hidden";							
				cmp.bodyEl.style.position = "";
				zk.unlisten(bodyEl, "scroll", zk.Layout.onscroll);
			}
			return true;
		case "z.colps" :
		 	setZKAttr(cmp, "colps", val);
			zkLayoutRegionSplit._fixbtn(cmp.split);
			zkLayoutRegionSplit._fixsplit(cmp.split);
			return true;
		case "z.splt" :
			setZKAttr(cmp, "splt", val);
			var vert = cmp.split.pos == "west" || cmp.split.pos == "east" ? false : true;
			if (getZKAttr(cmp, "open") != "false" && val == "true") {				
				cmp.split.style.cursor = vert ? "s-resize": "e-resize";
				zk.rmClass(cmp.split, vert ? "layout-split-v-ns" : "layout-split-h-ns");
				zk.addClass(cmp.split, vert ? "layout-split-v" : "layout-split-h");
			} else {
				cmp.split.style.cursor = "default";		
				zk.addClass(cmp.split, vert ? "layout-split-v-ns" : "layout-split-h-ns");
				zk.rmClass(cmp.split, vert ? "layout-split-v" : "layout-split-h");
			}
			zkLayoutRegionSplit._fixsplit(cmp.split);
			return true;
		case "style.height" :
			cmp.style["height"] = val;
			cmp._height = false;
			return true;				
		case "style.width" :
			cmp.style["width"] = val;			
			cmp._width = false;
			return true;				
		case "z.open" :		
			zkLayoutRegionSplit.open(cmp.split, val == "true", true, true);
			return true;
	}
	return false;
};

////
zkLayoutRegionSplit = {};
zkLayoutRegionSplit._drags = {};
zkLayoutRegionSplit.init = function (split) {
	var real = $real(split);
	zkLayoutRegionSplit._fixsplit(split);
	var snap = function (x, y) {return zkLayoutRegionSplit._snap(split, x, y);};
	var vert = split.pos == "west" || split.pos == "east" ? false : true;
	var drag = zkLayoutRegionSplit._drags[split.id] = {
		vert: vert,
		drag: new Draggable(split, {
			constraint: vert ? "vertical": "horizontal",
			ghosting: zkLayoutRegionSplit._ghostsizing,
			snap: snap,
			ignoredrag: zkLayoutRegionSplit._ignoresizing,
			zindex: 12000,
			endeffect: zkLayoutRegionSplit._endDrag})
	};

	zk.listen(split.splitbtn, "click", function () {
		zkLayoutRegionSplit.open(split, getZKAttr(real, "open") == "false");
	});
	if (zk.ie) {
		zk.listen(split.splitbtn, "mouseover", function () {split.splitbtn.style.filter = "alpha(opacity=100);";});
	 	zk.listen(split.splitbtn, "mouseout", function () {split.splitbtn.style.filter = "alpha(opacity=50);";});
	}
	zkLayoutRegionSplit._fixbtn(split);
	if (getZKAttr(real, "open") == "false"){
		zkLayoutRegionSplit.open(split, false, true, true);
	} else {
		if (getZKAttr(real, "splt") == "true") {
			split.style.cursor = vert ? "s-resize": "e-resize";
		} else {
			zk.addClass(split, vert ? "layout-split-v-ns" : "layout-split-h-ns");
			zk.rmClass(split, vert ? "layout-split-v" : "layout-split-h");			
			split.style.cursor = "default";
		}
	}
	split.splitbtn.style.cursor = "pointer";
};
zkLayoutRegionSplit._fixsplit = function (split) {
	var real = $real(split);
	zk.show(split.id, !(getZKAttr(real, "splt") == "false" && getZKAttr(real, "colps") == "false"));	
};
zkLayoutRegionSplit._fixbtn = function (split) {
	var real = $real(split);
	var colps = getZKAttr(real, "colps");
	if (colps == "false") {
		split.splitbtn.style.display = "none";
	} else {
		var vert = split.pos == "west" || split.pos == "east" ? false : true;
		var before = colps == "true";
		if (split.pos == "east" || split.pos == "south") before = !before;
		if (getZKAttr(real, "open") == "false") before = !before;
		split.splitbtn.className = zk.renType(split.splitbtn.className,
			vert ? before ? 't': 'b': before ? 'l': 'r');
		split.splitbtn.style.display = "";
	}
};
zkLayoutRegionSplit._ignoresizing = function (split, pointer, event) {
	var dg = zkLayoutRegionSplit._drags[split.id];
	if (dg) {
		var el = Event.element(event);
		if (!el || !el.id || !el.id.endsWith("!split")) return true;
		var real = $real(split);
		if (real && getZKAttr(real, "open") == "true" && getZKAttr(real, "splt") == "true") {			
			var maxs = $int(getZKAttr(real, "maxs")) || 2000;
			var mins = $int(getZKAttr(real, "mins")) || 0;	
			var ol = zk.Layout.getOwnerLayout(real);
			var mars = ol._paserMargin(getZKAttr(real, "mars") || "0,0,0,0");
			var lr = zk.sumStyles(real, "lr", zk.borders) + 
				zk.sumStyles(real, "lr", zk.paddings) + 
				(split.pos == "west" ? mars.left : mars.right);
			var tb = zk.sumStyles(real, "tb", zk.borders) + 
				zk.sumStyles(real, "tb", zk.paddings) + 
				(split.pos == "north" ? mars.top : mars.bottom);
			var min = 0;
			switch (split.pos) {
				case "north":	
				case "south":
					var uuid = $e($uuid(real));
					var rr = split.pos == "north" ? ol.getRegion("center") || ol.getRegion("south")
 						: ol.getRegion("center") || ol.getRegion("north");
					if (rr) {
						var pos = getZKAttr(rr, "pos");
						if (pos == "center") {
							var east = ol.getRegion("east"), west = ol.getRegion("west"),
								btn = 0;
							if (east && east.split && east.split.splitbtn)
								btn = Math.max(btn, east.split.splitbtn.offsetHeight);
							if (west && west.split && west.split.splitbtn)
								btn = Math.max(btn, west.split.splitbtn.offsetHeight);
							maxs = Math.min(maxs, (real.offsetHeight + rr.offsetHeight - btn)- min);
						} else {
							maxs = Math.min(maxs, ol.el.offsetHeight - rr.offsetHeight - rr.split.offsetHeight - split.offsetHeight - min); 
						}
					} else {
						maxs = ol.el.offsetHeight - split.offsetHeight;
					}
					break;				
				case "west":				
				case "east":
					var uuid = $e($uuid(real));
					var rr = split.pos == "west" ? ol.getRegion("center") || ol.getRegion("east")
 						: ol.getRegion("center") || ol.getRegion("west");
					if (rr) {
						var pos = getZKAttr(rr, "pos");
						if (pos == "center") {
							maxs = Math.min(maxs, (real.offsetWidth + zk.revisedSize(rr, rr.offsetWidth))- min);
						} else {
							maxs = Math.min(maxs, ol.el.offsetWidth - rr.offsetWidth - rr.split.offsetWidth - split.offsetWidth - min); 
						}
					} else {
						maxs = ol.el.offsetWidth - split.offsetWidth;
					}
					break;						
			}
			var ofs = Position.cumulativeOffset(real);
			dg.drag.z_rootlyt = {
				maxs: maxs,
				mins: mins,
				top: ofs[1], left : ofs[0], right : real.offsetWidth, bottom:real.offsetHeight
			};
			return false;
		}
	}
	return true;
};
zkLayoutRegionSplit._endDrag = function (split, evt) {
	var dg = zkLayoutRegionSplit._drags[split.id];
	if (!dg) return;
	var real = $real(split);
	if (split.pos == "west" || split.pos == "east") {
		real.style["width"] = dg.drag.z_point[0] + "px";
	} else {		
		real.style["height"] = dg.drag.z_point[1] + "px";
	}	
	real._width = real._height = false;
	var layout = zk.Layout.getOwnerLayout(real);	
	layout.render();
	dg.drag.z_rootlyt = null;
	var keys = "";
	if (evt) {
		if (evt.altKey) keys += 'a';
		if (evt.ctrlKey) keys += 'c';
		if (evt.shiftKey) keys += 's';
	}	
	zkau.send({uuid: $uuid(real), cmd: "onSize",
		data: [real.style.width, real.style.height, keys]},
		zkau.asapTimeout(real, "onSize"));
};
/***/
zkLayoutRegionSplit._snap = function (split, x, y) {
	var dd = zkLayoutRegionSplit._drags[split.id];
	if (dd) {
		var b = dd.drag.z_rootlyt;
		var w, h;
		switch (split.pos) {
			case "north":
				if (y > b.maxs + b.top) y = b.maxs + b.top;
				if (y < b.mins + b.top) y = b.mins + b.top;
				w = x;
				h = y - b.top;
				break;				
			case "south":
				if (b.top + b.bottom - y - split.offsetHeight > b.maxs) {
					y = b.top + b.bottom - b.maxs - split.offsetHeight;
					h = b.maxs;			
				} else if (b.top + b.bottom - b.mins - split.offsetHeight <= y) {
					y = b.top + b.bottom - b.mins - split.offsetHeight;
					h = b.mins;	
				} else h = b.top - y + b.bottom - split.offsetHeight;
				w = x;	
				break;				
			case "west":
				if (x > b.maxs + b.left) x = b.maxs + b.left;
				if (x < b.mins + b.left) x = b.mins + b.left;
				w = x - b.left;
				h = y;
				break;		
			case "east":			
				if (b.left + b.right - x - split.offsetWidth > b.maxs) {
					x = b.left + b.right - b.maxs - split.offsetWidth;
					w = b.maxs;
				} else if (b.left + b.right - b.mins - split.offsetWidth <= x) {
					x = b.left + b.right - b.mins - split.offsetWidth;
					w = b.mins;
				} else w = b.left - x + b.right - split.offsetWidth;
				h = y;
				break;						
		}
		dd.drag.z_point = [w, h];
	}
	return [x, y];
};
zkLayoutRegionSplit.open = function (split, open, silent, enforce) {
	var real = $real(split);
	if (!enforce && (getZKAttr(real, "open") != "false") == open)
		return; //nothing changed

	var colps = getZKAttr(real, "colps")
	if (colps == "false") return; //nothing to do

	setZKAttr(real, "open", open ? "true": "false");
	var vert = split.pos == "west" || split.pos == "east" ? false : true;
	if (open && getZKAttr(real, "splt") == "true") {				
		split.style.cursor = vert ? "s-resize": "e-resize";
		zk.rmClass(split, vert ? "layout-split-v-ns" : "layout-split-h-ns");
		zk.addClass(split, vert ? "layout-split-v" : "layout-split-h");
	} else {
		split.style.cursor = "default";		
		zk.addClass(split, vert ? "layout-split-v-ns" : "layout-split-h-ns");
		zk.rmClass(split, vert ? "layout-split-v" : "layout-split-h");
	}
	zkLayoutRegionSplit._fixbtn(split);
	var layout = zk.Layout.getOwnerLayout(split);	
	zk.show(real.id, open);
	layout.render();
	if (!silent)
		zkau.send({uuid: $uuid(split), cmd: "onOpen", data: [open]},
			zkau.asapTimeout(real, "onOpen"));	
};
zkLayoutRegionSplit._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var pointer = zkau.beginGhostToDIV(dg);	
		var html = '<div id="zk_ddghost" style="background:#AAA;position:absolute;top:'
			+pointer[1]+'px;left:'+pointer[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;"><img src="'+zk.getUpdateURI('/web/img/spacer.gif')
					+'"/></div>';
		document.body.insertAdjacentHTML("afterbegin", html);
		dg.element = $e("zk_ddghost");
	} else {		
		zkau.endGhostToDIV(dg);
	}
};