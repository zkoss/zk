/* borderlayout.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 31 15:47:23 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This file is distributed under LGPL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
/**
 * New trendy mold of border layout
 * @since 3.5.0
 */
zk.Layout2 = zClass.create();
zk.Layout2.prototype = {
	initialize: function (cmp) {
		this.id = cmp.id;			
		this.el = cmp;
		zkau.setMeta(cmp, this);
		this._regions =  {};
	},
	/**
	 * Adds a cmp by its region.
	 * @param {String} region a kind of "north", "south", "west", "east", and "center".
	 * @param {Object} cmp a type of zkLayoutregion2
	 */
	addRegion: function (region, cmp) {
		this._regions[region] = cmp;
	},
	/**
	 * Removes a cmp by its region.
	 * @param {String} region a kind of "north", "south", "west", "east", and "center".
	 */
	removeRegion: function (region) {
		delete this._regions[region];
	},
	/**
	 * Returns a cmp by its region.
	 * @param {String} region a kind of "north", "south", "west", "east", and "center".
	 */
	getRegion: function (region) {
		return this._regions[region];
	},
	// private
	// returns the ambit of the specified cmp for region calculation. 
	_getAmbit: function (cmp, region, ignoreSplit) {
		var ambit, mars = this._getMargins(cmp);
		if (region && !this.isOpen(cmp)) {
			var colled = $e($uuid(cmp), "colpsd");
			ambit = {
				x: mars.left,
				y: mars.top,
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
			ignoreSplit = true;
		} else {
			var w = cmp.style.width || '',
			h = cmp.style.height || '',
			widx = w.indexOf('%'),
			hidx = h.indexOf('%');
			if (widx > 0) cmp._width = $int(w.substring(0, widx));
			if (hidx > 0) cmp._height = $int(h.substring(0, hidx));
			ambit = {
				x: mars.left,
				y: mars.top,
				w: cmp._width ?  Math.max(Math.floor(this.el.offsetWidth * cmp._width / 100), 0) : cmp.offsetWidth, 
				h: cmp._height ?  Math.max(Math.floor(this.el.offsetHeight * cmp._height / 100), 0) : cmp.offsetHeight
			};
		}
		
		var split = ignoreSplit ? {offsetHeight:0, offsetWidth:0} : cmp.split || {offsetHeight:0, offsetWidth:0};
		zkLayoutRegionSplit2._fixsplit(split);
		switch (region) {
		case "north":
		case "south":
			ambit.h += split.offsetHeight;
			ambit.ts = ambit.y + ambit.h + mars.bottom; // total size;
			ambit.w = mars.left + mars.right;
			if (region == 'south')
				ambit.y = ambit.h + mars.bottom;
			break;
		case "west":
		case "east":
			ambit.h = mars.top + mars.bottom;
			ambit.w += split.offsetWidth;
			ambit.ts = ambit.x + ambit.w + mars.right; // total size;
			if (region == 'east')
				ambit.x = ambit.w + mars.right; 
			break;
		}
		return ambit;
	},
	_getMargins: function (cmp) {
		return this.stringToBox(this.isOpen(cmp) ? getZKAttr(cmp, "mars") || "0,0,0,0" : getZKAttr(cmp, "cmars") || "5,5,5,5");
	},
	stringToBox: function (val) {
		val = val.split(',');
		return {top: $int(val[0]), left: $int(val[1]), right: $int(val[2]), bottom: $int(val[3])};
	},
	isOpen: function (cmp) {
		return getZKAttr(cmp, "open") == "true";
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
				this._resize(region, ambit, this.isOpen(region));
			}
		}
		var cnt = this.getRegion('center');
		if (cnt) {
			var mars = this.stringToBox(getZKAttr(cnt, "mars") || "0,0,0,0");
			center.x += mars.left;
			center.y += mars.top;
			center.w -= mars.left + mars.right;
			center.h -= mars.top + mars.bottom;
			this._resize(cnt, center, true);
		}
		zk.cleanVisibility(this.el);
		this.isOnSize = false; // reset
	},
	_resize: function (cmp, ambit, isOpen, ignoreSplit) {
		if (isOpen) {
			if (!ignoreSplit && cmp.split) {
				zkLayoutRegionSplit2._fixsplit(cmp.split);
				ambit = this._resizeSplit(cmp, ambit, cmp.split.pos);	
			}
			cmp.style.left = ambit.x + "px";
			cmp.style.top = ambit.y + "px";
			this._resizeBody(cmp, ambit);
		} else {
			cmp.split.style.display = "none";
			var colled = $e($uuid(cmp), "colpsd");
			if (colled) {
				colled.style.left = ambit.x + "px";
				colled.style.top = ambit.y + "px";
				colled.style.height = zk.revisedSize(colled, ambit.h, true) + "px";
				colled.style.width = zk.revisedSize(colled, ambit.w) + "px";
			}
		}
	},
	_resizeSplit: function (cmp, ambit, region) {	
		if (!zk.isVisible(cmp.split)) return ambit;
		var sAmbit = {
				w: cmp.split.offsetWidth, 
				h: cmp.split.offsetHeight
			};
		switch(region){
			case "north":
				ambit.h -= sAmbit.h;
			  	cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = (ambit.y + ambit.h) + "px";
				cmp.split.style.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
				break;
			case "south":
				ambit.h -= sAmbit.h;
				ambit.y += sAmbit.h;
				cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = (ambit.y - sAmbit.h) + "px";
				cmp.split.style.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
				break;
			case "west":
				ambit.w -= sAmbit.w;
				cmp.split.style.left = (ambit.x + ambit.w) + "px";
				cmp.split.style.top = ambit.y + "px";
				cmp.split.style.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
				break;
			case "east":
				ambit.w -= sAmbit.w;
				cmp.split.style.left = ambit.x + "px";
				cmp.split.style.top = ambit.y + "px";
				cmp.split.style.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
				ambit.x += sAmbit.w;
				break;					
		}
		return ambit;
	},
	_resizeBody: function (cmp, ambit) {		
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var cid = getZKAttr(cmp, "cid"),
			bodyEl = getZKAttr(cmp, "flex") == "true" && cid != "zk_n_a" ?
				$e(cid) : $e($uuid(cmp), "cave");
		cmp.bodyEl = bodyEl;
		
		if (!this.ignoreResize(cmp, ambit.w, ambit.h)) {
			ambit.w = zk.revisedSize(cmp, ambit.w);
			cmp.style.width = ambit.w + "px";	   			
			ambit.w = zk.revisedSize(bodyEl, ambit.w);
			bodyEl.style.width = ambit.w + "px";
			
			ambit.h = zk.revisedSize(cmp, ambit.h, true);
			cmp.style.height = ambit.h + "px";
			ambit.h = zk.revisedSize(bodyEl, ambit.h, true);
			var caption = $e($uuid(cmp), "caption");
			if (caption) ambit.h = Math.max(0, ambit.h - caption.offsetHeight);
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
zk.Layout2.onscroll = function (evt) {
	if (!evt) evt = window.event;
	zk.onScrollAt(Event.element(evt));
};
zk.Layout2.getOwnerLayout = function (cmp, cleanup) {
	var bl = $parentByType(cmp, "BorderLayout2");
	var meta = zkau.getMeta(bl);
	if (meta || cleanup) return meta;
	else return new zk.Layout2(bl);
};
/**
 * New trendy mold of border layout
 * @since 3.5.0
 */
zkBorderLayout2 = {
	childchg: function (cmp) {
		zk.Layout2.getOwnerLayout(cmp).render();
	},
	onSize: function (cmp) {
		zk.Layout2.getOwnerLayout(cmp).render(true);
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.resize" :
			var meta = zkau.getMeta(cmp);	
			if (meta) meta.render();
			return true;
		}
		return false;
	}
};
zkBorderLayout2.onVisi = zkBorderLayout2.onSize;
/**
 * New trendy mold of layout region
 * @since 3.5.0
 */
zkLayoutRegion2 = {
	init: function (cmp) {
		var split = $e(cmp, "split"), btn = $e(cmp, "btn");
		cmp = $real(cmp);
		if (!zk.isVisible(cmp)) $outer(cmp).style.display = "none";
		var pos = getZKAttr(cmp, "pos");
		if (btn) {
			var uuid = $uuid(btn),
				btned = $e(uuid, "btned"),
				colled = $e(uuid, "colpsd");
			
			// the effect of doing animation
			zk.on(colled, "afterSlideOut", this.onAfterSlideOut);
			zk.on(cmp, "afterSlideOut", this.onAfterSlideOut);
			zk.on(cmp, "beforeSlideOut", this.onBeforeSlideOut);
			zk.on(cmp, "afterSlideIn", this.onAfterSlideIn);
			zk.on(cmp, "afterSlideDown", this.onColledAfterSlideDown);
			zk.on(cmp, "afterSlideUp", this.onColledAfterSlideUp);		
			
			zk.listen(btn, "mouseover", this.onBtnMouseover);
			zk.listen(btn, "mouseout", this.onBtnMouseout);
			zk.listen(btn, "click", this.onBtnClick);
			zk.listen(btned, "mouseover", this.onBtnMouseover);
			zk.listen(btned, "mouseout", this.onBtnMouseout);
			zk.listen(btned, "click", this.onBtnClick);
			zk.listen(colled, "mouseover", this.onBtnMouseover);
			zk.listen(colled, "mouseout", this.onBtnMouseout);
			zk.listen(colled, "click", this.onColledClick)
		}
		
		if (split) {
			cmp.split = split;
			cmp.split.pos = pos;
			zkLayoutRegionSplit2.init(split);
		}
		
		var cid = getZKAttr(cmp, "cid"),
			isFlex = getZKAttr(cmp, "flex") == "true",
			bodyEl = (isFlex && cid != "zk_n_a") ? $e(getZKAttr(cmp, "cid")) : $e($uuid(cmp) + "!cave");
		if (getZKAttr(cmp, "autoscl") == "true") { 
			zk.listen(bodyEl, "scroll", zk.Layout2.onscroll);
		}
		if (isFlex) {
			zk.on(bodyEl, "onOuter", this.onOuter);
		}
		zk.Layout2.getOwnerLayout(cmp).addRegion(pos, cmp);
	},
	onOuter: function (child) {
		if (child) {
			var cmp = $real($parentByType(child, "LayoutRegion2"));
			if (!cmp) return;
			cmp._lastSize = null;// reset
			zk.Layout2.getOwnerLayout(cmp).render();
		}
	},
	// invokes border layout's renderer before the component slides out
	onBeforeSlideOut: function (cmp) {
		var colled = $e($uuid(cmp), "colpsd"),
			s = colled.style;
		s.display = "";
		s.visibility = "hidden";
		s.zIndex = 1;
		zk.Layout2.getOwnerLayout(cmp).render();
	},
	// a callback function after the component slides out.
	onAfterSlideOut: function (cmp) {
		var real = $real(cmp);
		if (getZKAttr(real, "open") == "true") 
			anima.slideIn(real, zkLayoutRegionSplit2.sanchors[real.split.pos]);
		else {
			var colled = $e($uuid(cmp), "colpsd");
			colled.style.zIndex = ""; // reset z-index refered to the onBeforeSlideOut()
			colled.style.visibility = "";
			anima.slideIn(colled, zkLayoutRegionSplit2.sanchors[real.split.pos], 200);
		}
	},
	// recalculates the size of the whole border layout after the component sildes in.
	onAfterSlideIn: function (cmp) {
		zk.Layout2.getOwnerLayout(cmp).render();
	},
	// a callback function after the colpsd region slides down
	onColledAfterSlideDown: function (cmp) {
		if (!cmp._slideIn)
			cmp._slideIn = function (evt) {
				var target = zkau.evtel(evt);
				if (cmp._isSlide && !zk.isAncestor(cmp, target)) {
					var uuid = $uuid(cmp);
					if (target.id == uuid + "!btned") {
						zkLayoutRegion2.onColledAfterSlideUp(cmp);
						zkLayoutRegionSplit2.open(cmp.split, true, false, false, true);
					} else if ((!cmp._isSlideUp && $uuid(target) != uuid) || !anima.count) {
						cmp._isSlideUp = true;
						anima.slideUp(cmp, zkLayoutRegionSplit2.sanchors[cmp.split.pos]);
					}
				}
			};
		zk.listen(document, "click", cmp._slideIn);
	},
	// a callback function after the collapsed region slides up
	onColledAfterSlideUp: function (cmp) {
		cmp.style.left = cmp._original[0];
		cmp.style.top = cmp._original[1];
		cmp._lastSize = null;// reset size
		cmp.style.zIndex = "";
		$e($uuid(cmp), "btn").style.display = "";
		zk.unlisten(document, "click", cmp._slideIn);
		cmp._isSlideUp = cmp._isSlide = false;
		
	},
	onColledClick: function (evt) {
		var colled = zkau.evtel(evt), real = $real(colled);
		if (!colled.id.endsWith("!colpsd") || real._isSlide) return;
		real._isSlide = true;
		var pos = getZKAttr(real, "pos");
		real.style.visibilty = "hidden";
		real.style.display = "";
		
		zkLayoutRegion2.syncSize(colled, real);
		real._original = [real.style.left, real.style.top];
		
		zkLayoutRegion2.alignTo(colled, real, pos);
		real.style.zIndex = 100;
		
		$e($uuid(real), "btn").style.display = "none"; 
		
		real.style.visibilty = "";
		real.style.display = "none";
		anima.slideDown(real, zkLayoutRegionSplit2.sanchors[real.split.pos]);
	},
	syncSize: function (colled, cmp, inclusive) {
		setZKAttr(cmp, "open", "true");
		var layout = zk.Layout2.getOwnerLayout(cmp),
			width = layout.el.offsetWidth,
			height = layout.el.offsetHeight,
			center = {
				x: 0,
				y: 0,
				w: width,
				h: height
			};
			
		for (var region, ambit, margin, rs = ['north', 'south', 'west', 'east'],
			j = 0, k = rs.length; j < k; ++j) {
			region = layout.getRegion(rs[j]);
			if (region && (zk.isVisible(region) || zk.isVisible($e($uuid(region), "colpsd")))) {
				var ignoreSplit = region == cmp;
				ambit = layout._getAmbit(region, rs[j], ignoreSplit);
				switch (rs[j]) {
				case 'north':
				case 'south':
					ambit.w = width - ambit.w;
					if (rs[j] == 'north') 
						center.y = ambit.ts;
					else
						ambit.y = height - ambit.y;
					center.h -= ambit.ts;
					if (ignoreSplit) {
						ambit.w = colled.offsetWidth;
						if (inclusive) {
							var cmars = layout.stringToBox(getZKAttr(region, "cmars") || "5,5,5,5");
							ambit.w += cmars.left + cmars.right;
						}
						layout._resize(region, ambit, true, ignoreSplit);
						setZKAttr(cmp, "open", "false");
						return;
					}
					break;
				default:
					ambit.y += center.y;
					ambit.h = center.h - ambit.h;
					if (rs[j] == 'east')
						ambit.x = width - ambit.x;
					else center.x += ambit.ts;
					center.w -= ambit.ts;
					if (ignoreSplit) {
						ambit.h = colled.offsetHeight;
						if (inclusive) {
							var cmars = layout.stringToBox(getZKAttr(region, "cmars") || "5,5,5,5");
							ambit.h += cmars.top + cmars.bottom;
						}
						layout._resize(region, ambit, true, ignoreSplit);
						setZKAttr(cmp, "open", "false");
						return;
					}
					break;
				}
				
			}
		}
	},
	alignTo: function (from, to, region) {
		switch (region) {
			case "north":
				to.style.top = from.offsetTop + from.offsetHeight + "px";
				to.style.left = from.offsetLeft + "px";
				break;
			case "south":
				to.style.top = from.offsetTop - to.offsetHeight + "px";
				to.style.left = from.offsetLeft + "px";
				break;
			case "west":
				to.style.left = from.offsetLeft + from.offsetWidth + "px";
				to.style.top = from.offsetTop + "px";
				break;
			case "east":
				to.style.left = from.offsetLeft - to.offsetWidth + "px";
				to.style.top = from.offsetTop + "px";
				break;
		}
	},
	onBtnClick: function (evt) {
		var btn = zkau.evtel(evt),
			real = $real(btn);
		if (real._isSlide || anima.count) return;
		if (btn.id.endsWith("!btned")) {
			real.style.visibilty = "hidden";
			real.style.display = "";
			zkLayoutRegion2.syncSize($e($uuid(real), "colpsd"), real, true);
			real.style.visibilty = "";
			real.style.display = "none";
		}
		zkLayoutRegionSplit2.open(real.split, getZKAttr(real, "open") == "false");
	},
	onBtnMouseover: function (evt) {
		var btn = zkau.evtel(evt), cls = zk.realClass(btn);
		zk.addClass(btn, cls + "-over");
		if (btn.id.endsWith("!btned")) {
			cls = zk.realClass(btn.parentNode);
			zk.addClass(btn.parentNode, cls + "-over");
		}
	},
	onBtnMouseout: function (evt) {
		var btn = zkau.evtel(evt), cls = zk.realClass(btn);
		zk.rmClass(btn, cls + "-over");
		if (btn.id.endsWith("!btned")) {
			cls = zk.realClass(btn.parentNode);
			zk.rmClass(btn.parentNode, cls + "-over");
		}
	},
	cleanup: function (cmp) {
		var colled = $e(cmp, "colpsd");
		cmp = $real(cmp);		
		var layout = zk.Layout2.getOwnerLayout(cmp, true);	// Bug #1814702
		if (cmp.split) {
			if (layout) layout.removeRegion(cmp.split.pos);
			var dg = zkLayoutRegionSplit2._drags[cmp.split.id];
			if (dg) {
				delete zkLayoutRegionSplit2._drags[cmp.split.id];
				dg.destroy();
			}
			cmp.split = null;
		}
		var pos = getZKAttr(cmp, "pos");
		cmp.bodyEl = null;
		if (pos != "center" && colled) {
			zk.un(colled, "afterSlideOut", this.onAfterSlideOut);
			zk.un(cmp, "afterSlideOut", this.onAfterSlideOut);
			zk.un(cmp, "beforeSlideOut", this.onBeforeSlideOut);
			zk.un(cmp, "afterSlideIn", this.onAfterSlideIn);
			zk.un(cmp, "afterSlideDown", this.onColledAfterSlideDown);
			zk.un(cmp, "afterSlideUp", this.onColledAfterSlideUp);
			cmp._slideIn = null;
		}
		if (layout) {
			layout.removeRegion(pos);
			zk.beforeSizeAt(layout.el);
			zk.onSizeAt(layout.el);
		}
	},
	setAttr: function (cmp, nm, val) {
		cmp = $real(cmp);
		switch (nm) {
			case "visibility":
				cmp.style.display = val == "true" ? "" : "none";
				$outer(cmp).style.display = cmp.style.display;
				zk.Layout2.getOwnerLayout(cmp).render();
				return true;			
			case "z.cid" :
			case "z.mars" :
			case "z.cmars" :
				zkau.setAttr(cmp, nm, val);
				zk.Layout2.getOwnerLayout(cmp).render();
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
					zk.listen(bodyEl, "scroll", zk.Layout2.onscroll);
				} else { 
					cmp.bodyEl.style.overflow = "hidden";							
					cmp.bodyEl.style.position = "";
					zk.unlisten(bodyEl, "scroll", zk.Layout2.onscroll);
				}
				return true;
			case "z.colps" :
			 	setZKAttr(cmp, "colps", val);
				$e($uuid(cmp), getZKAttr(cmp, "open") == "true" ? "btn" : "btned").style.display = val == "true" ? "" : "none";
				return true;
			case "z.splt" :
				setZKAttr(cmp, "splt", val);
				zk.Layout2.getOwnerLayout(cmp).render();
				return true;
			case "style.height" :
				cmp.style["height"] = val;
				cmp._height = false;
				zk.Layout2.getOwnerLayout(cmp).render();
				return true;				
			case "style.width" :
				cmp.style["width"] = val;			
				cmp._width = false;
				zk.Layout2.getOwnerLayout(cmp).render();
				return true;				
			case "z.open" :		
				zkLayoutRegionSplit2.open(cmp.split, val == "true", true, true);
				return true;
			case "class" :
				zkau.setAttr(cmp, nm, val);	
				cmp._width = false; // reset
				zk.Layout2.getOwnerLayout(cmp).render();
				return true;
			default:
				zkau.setAttr(cmp, nm, val);
				return true;
		}
	}
};

////
zkLayoutRegionSplit2 = {
	_drags: {},
	sanchors: {
        "west" : "l",
        "east" : "r",
        "north" : "t",
        "south" : "b"
    },
	init: function (split) {
		zkLayoutRegionSplit2._fixsplit(split);
		var vert = split.pos == "west" || split.pos == "east" ? false : true;
		
		this._drags[split.id] = new zDraggable(split, {
			constraint: vert ? "vertical": "horizontal",
			ghosting: zkLayoutRegionSplit2._ghostsizing,
			snap: function (x, y) {return zkLayoutRegionSplit2._snap(split, x, y);},
			zindex: 12000, stackup: true, ignoredrag: zkLayoutRegionSplit2._ignoresizing,
			endeffect: zkLayoutRegionSplit2._endDrag
		});
		var real = $real(split);
		if (getZKAttr(real, "open") == "false"){
			this.open(split, false, true, true, true);
		}
	},
	_fixsplit: function (split) {
		var real = $real(split);
		if (real) zk.show(split.id, !(getZKAttr(real, "splt") == "false"));	
	},
	_ignoresizing: function (split, pointer, event) {
		var dg = zkLayoutRegionSplit2._drags[split.id];
		if (dg) {
			var el = Event.element(event);
			if (!el || !el.id || !el.id.endsWith("!split")) return true;
			var real = $real(split);
			if (real && getZKAttr(real, "open") == "true" && getZKAttr(real, "splt") == "true") {			
				var maxs = $int(getZKAttr(real, "maxs")) || 2000,
					mins = $int(getZKAttr(real, "mins")) || 0,
					ol = zk.Layout2.getOwnerLayout(real),
					mars = ol.stringToBox(getZKAttr(real, "mars") || "0,0,0,0"),
					lr = zk.getPadBorderWidth(real) + (split.pos == "west" ? mars.left : mars.right),
					tb = zk.getPadBorderHeight(real) + (split.pos == "north" ? mars.top : mars.bottom),
					min = 0,
					uuid = $e($uuid(real));
				switch (split.pos) {
					case "north":	
					case "south":
						var rr = split.pos == "north" ? 
								ol.getRegion("center") || ol.getRegion("south")
	 							: ol.getRegion("center") || ol.getRegion("north");
						if (rr) {
							var pos = getZKAttr(rr, "pos");
							if (pos == "center") {
								var east = ol.getRegion("east"),
									west = ol.getRegion("west");
								maxs = Math.min(maxs, (real.offsetHeight + rr.offsetHeight)- min);
							} else {
								maxs = Math.min(maxs, ol.el.offsetHeight - rr.offsetHeight - rr.split.offsetHeight - split.offsetHeight - min); 
							}
						} else {
							maxs = ol.el.offsetHeight - split.offsetHeight;
						}
						break;				
					case "west":				
					case "east":
						var rr = split.pos == "west" ?
								ol.getRegion("center") || ol.getRegion("east")
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
				var ofs = zPos.cumulativeOffset(real);
				dg.z_rootlyt = {
					maxs: maxs,
					mins: mins,
					top: ofs[1], left : ofs[0], right : real.offsetWidth, bottom: real.offsetHeight
				};
				return false;
			}
		}
		return true;
	},
	_endDrag: function (split, evt) {
		var dg = zkLayoutRegionSplit2._drags[split.id];
		if (!dg) return;
		var real = $real(split),
			keys = "";
		if (split.pos == "west" || split.pos == "east") {
			real.style["width"] = dg.z_point[0] + "px";
		} else {		
			real.style["height"] = dg.z_point[1] + "px";
		}
		real._width = real._height = false;
		zk.Layout2.getOwnerLayout(real).render();
		dg.z_rootlyt = null;
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}	
		zkau.send({uuid: $uuid(real), cmd: "onSize",
			data: [real.style.width, real.style.height, keys]},
			zkau.asapTimeout(real, "onSize"));
	},
	_snap: function (split, x, y) {
		var dd = zkLayoutRegionSplit2._drags[split.id];
		if (dd) {
			var b = dd.z_rootlyt, w, h;
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
			dd.z_point = [w, h];
		}
		return [x, y];
	},
	open: function (split, open, silent, enforce, nonAnima) {
		var real = $real(split);
		if (getZKAttr(real, "colps") != "true" || (!enforce && (getZKAttr(real, "open") != "false") == open))
			return; //nothing changed
	
		setZKAttr(real, "open", open ? "true": "false");
		var vert = split.pos == "west" || split.pos == "east" ? false : true,
			colled = $e($uuid(real), "colpsd");
		if (open) {
			if (colled) {
				if (!nonAnima) 
					anima.slideOut(colled, this.sanchors[split.pos], 200);
				else {
					zk.show(real.id, open);
					zk.show(colled.id, !open);
				}
			}
		} else {
			if (colled && !nonAnima) 
				anima.slideOut(real, this.sanchors[split.pos]);
			else {
				if (colled)
					zk.show(colled.id, !open);
				zk.show(real.id, open);
			}
		}
		if (nonAnima) zk.Layout2.getOwnerLayout(split).render();
		if (!silent)
			zkau.send({uuid: $uuid(split), cmd: "onOpen", data: [open]},
				zkau.asapTimeout(real, "onOpen"));	
	},
	_ghostsizing: function (dg, ghosting, pointer) {
		if (ghosting) {
			var pointer = zkau.beginGhostToDIV(dg);	
			var html = '<div id="zk_ddghost" style="background:#AAA;position:absolute;top:'
				+pointer[1]+'px;left:'+pointer[0]+'px;width:'
				+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
				+'px;cursor:'+dg.element.style.cursor+';"></div>';
			document.body.insertAdjacentHTML("afterbegin", html);
			dg.element = $e("zk_ddghost");
		} else {		
			zkau.endGhostToDIV(dg);
		}
	}
};
