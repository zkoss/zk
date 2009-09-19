/* Borderlayout.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.Borderlayout = zk.$extends(zul.Widget, {
	setResize: function () {
		this.resize();
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		var BL = zul.layout.Borderlayout;
		if (child.getPosition() == BL.NORTH)
			this.north = child;
		else if (child.getPosition() == BL.SOUTH)
			this.south = child;
		else if (child.getPosition() == BL.CENTER)
			this.center = child;
		else if (child.getPosition() == BL.WEST)
			this.west = child;
		else if (child.getPosition() == BL.EAST)
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
		return this._zclass == null ? "z-borderlayout" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	},
	//@Override, region with vflex/hflex, must wait flex resolved then do resize
	afterChildrenFlex_: function () {
		//region's min vflex/hflex resolved and try the border resize
		//@see #_resize
		if (this._isOnSize)
			this._resize(true);
	},
	resize: function () {
		if (this.desktop)
			this._resize();
	},
	_resize: function (isOnSize) {
		this._isOnSize = isOnSize;
		if (!this.isRealVisible()) return;

		//make sure all regions size is resolved
		var rs = ['north', 'south', 'west', 'east'], k = rs.length; 
		for (var region, j = 0; j < k; ++j) {
			region = this[rs[j]];
			if (region && zk(region.$n()).isVisible()
				&& ((region._nvflex && region._vflexsize === undefined) 
						|| (region._nhflex && region._hflexsize === undefined)))
				return;	//region size unknown, border cannot _resize() now, 
						//return and keep this._isOnSize true
						//onSize event will be fired to region later, and region will
						//call back to _resize() via afterChildrenFlex_() when it resolve
						//itself the vflex and hflex
		}

		var el = this.$n(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			center = { 
				x: 0,
				y: 0,
				w: width,
				h: height
			};
		for (var region, ambit, margin,	j = 0; j < k; ++j) {
			region = this[rs[j]];
			if (region && zk(region.$n()).isVisible()) {
				ambit = region._ambit();
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
				this._resizeWgt(region, ambit); //might recursive back
			}
		}
		if (this.center && zk(this.center.$n()).isVisible()) {
			var mars = this.center.getCurrentMargins_();
			center.x += mars.left;
			center.y += mars.top;
			center.w -= mars.left + mars.right;
			center.h -= mars.top + mars.bottom;
			this._resizeWgt(this.center, center); //might recursive back
		}
		zk(el).cleanVisibility();
		this._isOnSize = false; // reset
	},
	_resizeWgt: function (wgt, ambit, ignoreSplit) {
		if (wgt._open) {
			if (!ignoreSplit && wgt.$n('split')) {
				wgt._fixSplit();
				 ambit = wgt._reszSplt(ambit);
			}
			zk.copy(wgt.$n('real').style, {
				left: jq.px(ambit.x),
				top: jq.px(ambit.y)
			});
			this._resizeBody(wgt, ambit);
		} else {
			wgt.$n('split').style.display = "none";
			var colled = wgt.$n('colled');
			if (colled) {
				var $colled = zk(colled);
				zk.copy(colled.style, {
					left: jq.px(ambit.x),
					top: jq.px(ambit.y),
					width: jq.px($colled.revisedWidth(ambit.w)),
					height: jq.px($colled.revisedHeight(ambit.h))
				});
			}
		}
	},
	_resizeBody: function (wgt, ambit) {
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var el = wgt.$n('real'),
			bodyEl = wgt.isFlex() && wgt.firstChild ?
						wgt.firstChild.$n() : wgt.$n('cave');
		if (!this._ignoreResize(el, ambit.w, ambit.h)) {
			ambit.w = zk(el).revisedWidth(ambit.w);
			el.style.width = jq.px(ambit.w);
			ambit.w = zk(bodyEl).revisedWidth(ambit.w);
			bodyEl.style.width = jq.px(ambit.w);

			ambit.h = zk(el).revisedHeight(ambit.h);
			el.style.height = jq.px(ambit.h);
			ambit.h = zk(bodyEl).revisedHeight(ambit.h);
			if (wgt.$n('cap'))
				ambit.h = Math.max(0, ambit.h - wgt.$n('cap').offsetHeight);
			bodyEl.style.height = jq.px(ambit.h);
			if (wgt.isAutoscroll()) { 
				bodyEl.style.overflow = "auto";
				bodyEl.style.position = "relative";
			} else {
				bodyEl.style.overflow = "hidden";
				bodyEl.style.position = "";
			}
			if (!this._isOnSize) {
				zWatch.fireDown('beforeSize', wgt);
				zWatch.fireDown('onSize', wgt);
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
