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
		var ambit, mars;
		if (!zk.isVisible(cmp)) {
			ambit = {x: 0, y: 0, w: 0, h: 0};
			mars = this.stringToBox('0,0,0,0');
		} else {
			var w = cmp.style.width || '',
			h = cmp.style.height || '',
			widx = w.indexOf('%'),
			hidx = h.indexOf('%'),
			mars = this.stringToBox(getZKAttr(cmp, "mars") || '0,0,0,0');
			
			if (widx > 0) cmp._width = $int(w.substring(0, widx));
			if (hidx > 0) cmp._height = $int(h.substring(0, hidx));
			ambit = {
				x: mars.left,
				y: mars.top,
				w: cmp._width ?  Math.max(Math.floor(this.el.offsetWidth * cmp._width / 100), 0) : cmp.offsetWidth, 
				h: cmp._height ?  Math.max(Math.floor(this.el.offsetHeight * cmp._height / 100), 0) : cmp.offsetHeight
			};
		}
		
		switch (region) {
		case "north":
		case "south":
			ambit.h += cmp.split.offsetHeight;
			ambit.ts = ambit.y + ambit.h + mars.bottom; // total size;
			ambit.w = mars.left + mars.right;
			if (region == 'south')
				ambit.y = ambit.h + mars.bottom;
			break;
		case "west":
		case "east":
			ambit.h = mars.top + mars.bottom;
			ambit.w += cmp.split.offsetWidth;
			ambit.ts = ambit.x + ambit.w + mars.right; // total size;
			if (region == 'east')
				ambit.x = ambit.w + mars.right; 
			break;
		}
		return ambit;
	},
	stringToBox: function (val) {
		val = val.split(',');
		return {top: $int(val[0]), left: $int(val[1]), right: $int(val[2]), bottom: $int(val[3])};
	},
	render: function (isOnSize) {
		this.isOnSize = isOnSize;
		if (!zk.isRealVisible(this.el)) return;
		var width = this.el.offsetWidth,
			height = this.el.offsetHeight,
			center = {
				x: 0,
				y: 0,
				w: width,
				h: height
			};
		
		for (var region, ambit, margin,	rs = ['north', 'south', 'west', 'east'],
				j = 0, k = rs.length; j < k; ++j) {
			region = this.getRegion(rs[j]);
			if (region && zk.isRealVisible(region.parentNode)) {
				ambit = this._getAmbit(region, rs[j]);
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
				this._resize(region, ambit, rs[j]);
			}
		}
		var cnt = this.getRegion('center');
		if (cnt) {
			var mars = this.stringToBox(getZKAttr(cnt, "mars") || "0,0,0,0");
			center.x += mars.left;
			center.y += mars.top;
			center.w -= mars.left + mars.right;
			center.h -= mars.top + mars.bottom;
			this._resize(cnt, center);
		}
		zk.cleanVisibility(this.el);
		this.isOnSize = false; // reset
	},
	_resize: function (cmp, ambit, region) {
		if (region) this._resizeSplit(cmp, ambit, region);
		cmp.style.left = ambit.x + "px";
		cmp.style.top = ambit.y + "px";
		if (zk.isVisible(cmp))
			this._resizeBody(cmp, ambit);
	},
	_resizeSplit: function (cmp, ambit, region) {	
		if (region == "north" || region == "south")
			cmp.split.splitbtn.style.marginLeft = ((ambit.w - cmp.split.splitbtn.offsetWidth) / 2) + "px";
		else
			cmp.split.splitbtn.style.marginTop = ((ambit.h - cmp.split.splitbtn.offsetHeight) / 2) + "px";
		var s = cmp.split.style,
			sAmbit = {
				w: cmp.split.offsetWidth,
				h: cmp.split.offsetHeight
			};
		switch(region){
		case "north":
		case "south":
			ambit.h -= sAmbit.h;
			s.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
		  	s.left = ambit.x + "px";
			if (region == 'south') {
				ambit.y += sAmbit.h;
				s.top = (ambit.y - sAmbit.h) + "px";
			} else 
				s.top = (ambit.y + ambit.h) + "px";
			break;
		case "west":
		case "east":
			ambit.w -= sAmbit.w;
			if (region == 'west')
				s.left = (ambit.x + ambit.w) + "px";
			else {
				s.left = ambit.x + "px";
				ambit.x += sAmbit.w;
			}
			s.top = ambit.y + "px";
			s.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
			break;					
		}
	},
	_resizeBody: function (cmp, ambit) {		
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var cid = getZKAttr(cmp, "cid"),
			bodyEl = getZKAttr(cmp, "flex") == "true" && cid != "zk_n_a" ?
				$e(cid) : $e($uuid(cmp) + "!cave");
		cmp.bodyEl = bodyEl;
		if (!this.ignoreResize(bodyEl, ambit.w, ambit.h)) {
			ambit.w = zk.revisedSize(cmp, ambit.w);
			cmp.style.width = ambit.w + "px";	   			
			ambit.w = zk.revisedSize(bodyEl, ambit.w);
			bodyEl.style.width = ambit.w + "px";
			
			ambit.h = zk.revisedSize(cmp, ambit.h, true);
			cmp.style.height = ambit.h + "px";
			ambit.h = zk.revisedSize(bodyEl, ambit.h, true);
			bodyEl.style.height = ambit.h + "px";
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
			var mars = ol.stringToBox(getZKAttr(real, "mars") || "0,0,0,0");
			var lr = zk.getFrameWidth(real) +
				(split.pos == "west" ? mars.left : mars.right);
			var tb = zk.getFrameHeight(real) + 
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