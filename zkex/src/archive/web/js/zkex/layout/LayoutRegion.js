/* LayoutRegion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.layout.LayoutRegion = zk.$extends(zul.Widget, {
	_open: true,
	_border: "normal",
	_maxsize: 2000,
	_minsize: 0,

	$init: function () {
		this.$supers('$init', arguments);
		this._margins = [0, 0, 0, 0];
		this._cmargins = [5, 5, 5, 5];
	},
	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (this._title != title) {
			this._title = title;
			this.rerender();
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (!border || '0' == border)
			border = "none";
		if (this._border != border) {
			this._border = border;
			this.updateDomClass_();
		}
	},
	isSplittable: function () {
		return this._splittable;
	},
	setSplittable: function (splittable) {
		if (this._splittable != splittable) {
			this._splittable = splittable;
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	setMaxsize: function (maxsize) {
		if (this._maxsize != maxsize)
			this._maxsize = maxsize;
	},
	getMaxsize: function () {
		return this._maxsize;
	},
	setMinsize: function (minsize) {
		if (this._minsize != minsize)
			this._minsize = minsize;
	},
	getMinsize: function () {
		return this._minsize;
	},
	isFlex: function () {
		return this._flex;
	},
	setFlex: function (flex) {
		if (this._flex != flex) {
			this._flex = flex;
			this.rerender();
		}
	},
	getMargins: function () {
		return zUtl.intsToString(this._margins);
	},
	setMargins: function (margins) {
		if (this.getMargins() != margins) {
			this._margins = zUtl.stringToInts(margins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	getCmargins: function () {
		return zUtl.intsToString(this._cmargins);
	},
	setCmargins: function (cmargins) {
		if (this.getCmargins() != cmargins) {
			this._cmargins = zUtl.stringToInts(cmargins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	isCollapsible: function () {
		return this._collapsible;
	},
	setCollapsible: function (collapsible) {
		if (this._collapsible != collapsible) {
			this._collapsible = collapsible;
			var btn = this.isOpen() ? this.ebtn : this.ebtned;
			if (btn)
				btn.style.display = collapsible ? '' : 'none';
		}
	},
	isAutoscroll: function () {
		return this._autoscroll;
	},
	setAutoscroll: function (autoscroll) {
		if (this._autoscroll != autoscroll) {
			this._autoscroll = autoscroll;
			if (this.ecave) {
				var bodyEl = this.isFlex() && this.firstChild ?
						this.firstChild.getNode() : this.ecave;
				if (autoscroll) {
					bodyEl.style.overflow = "auto";
					bodyEl.style.position = "relative";
					zEvt.listen(bodyEl, "scroll", this.proxy(this.doScroll_, '_pxDoscroll'));
				} else {
					bodyEl.style.overflow = "hidden";
					bodyEl.style.position = "";
					zEvt.unlisten(bodyEl, "scroll", this._pxDoscroll);
				}
			}
		}
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open) {
		if (this._open != open) {
			this.open_(open, true);
		}
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = "normal" == this.getBorder() ? '' : this.getZclass() + "-noborder";
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-" + this.getPosition() : this._zclass;
	},
	//-- super --//
	setWidth: function (width) {
		this._width = width;
		if (this.ereal) this.ereal.style.width = width ? width: '';
	},
	setHeight: function (height) {
		this._height = height;
		if (this.ereal) this.ereal.style.height = height ? height: '';
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			if (this.ereal) {
				this.ereal.style.display = this.ereal.parentNode.style.display;
				this.parent.resize();
			}
		}
	},	
	updateDomClass_: function () {
		if (this.desktop && this.ereal) {
			this.ereal.className = this.domClass_();
			if (this.parent) this.parent.resize();
		}
	},
	updateDomStyle_: function () {
		if (this.desktop && this.ereal) {
			zDom.setStyle(this.ereal, zDom.parseStyle(this.domStyle_()));
			if (this.parent) this.parent.resize();
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zkex.layout.Borderlayout)) {
			this.setFlex(true);
			zDom.addClass(this.getNode(), this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zkex.layout.Borderlayout)) {
			this.setFlex(false);
			zDom.rmClass(this.getNode(), this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.parent)
			this.parent.resize();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getPosition() != zkex.layout.Borderlayout.CENTER) {
			this.esplit = zDom.$(this.uuid, 'split');
			this.ebtn = zDom.$(this.uuid, 'btn');
			if (this.ebtn) {
				this.ebtned = zDom.$(this.uuid, "btned");
				this.ecolled = zDom.$(this.uuid, "collapsed");
			}
			if (this.esplit) {
				this._fixSplit();
				var vert = this._isVertical(),
					$LayoutRegion = this.$class;
				
				this._drag = new zk.Draggable(this, this.esplit, {
					constraint: vert ? 'vertical': 'horizontal',
					ghosting: $LayoutRegion._ghosting,
					snap: $LayoutRegion._snap,
					zIndex: 12000,
					overlay: true,
					ignoredrag: $LayoutRegion._ignoredrag,
					endeffect: $LayoutRegion._endeffect					
				});	
				if (!this.isOpen())
					this.open_(false, true, true);
			}
		}
		var n = this.getNode();
		this.ereal = n.firstChild;
		this.ecaption = zDom.$(this.uuid, 'caption');
		this.ecave = zDom.$(this.uuid, 'cave');
		
		if (!zDom.isVisible(this.ereal)) n.style.display = "none";
		
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.ecave;
			zEvt.listen(bodyEl, "scroll", this.proxy(this.doScroll_, '_pxDoscroll'));
		}
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.ecave;
			zEvt.unlisten(bodyEl, "scroll", this._pxDoscroll);
		}
		if (this.esplit) {			
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}
		this.ecaption = this.esplit = this.ereal = this.ebtn = this.ebtned = this.ecolled = null;
		this.$supers('unbind_', arguments);
	},
	doScroll_: function () {
		zWatch.fireDown('onScroll', this);
	},
	doMouseOver_: function (wevt, devt) {
		if (this.ebtn) {
			var target = zEvt.target(devt);
			switch (target) {
				case this.ebtn:
					zDom.addClass(this.ebtn, this.getZclass() + '-collapse-over');
					break;
				case this.ebtned:
					zDom.addClass(this.ebtned, this.getZclass() + '-expand-over');
					// don't break
				case this.ecolled:
					zDom.addClass(this.ecolled, this.getZclass() + '-collapsed-over');
					break;
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (wevt, devt) {
		if (this.ebtn) {
			var target = zEvt.target(devt);
			switch (target) {
				case this.ebtn:
					zDom.rmClass(this.ebtn, this.getZclass() + '-collapse-over');
					break;
				case this.ebtned:
					zDom.rmClass(this.ebtned, this.getZclass() + '-expand-over');
					// don't break
				case this.ecolled:
					zDom.rmClass(this.ecolled, this.getZclass() + '-collapsed-over');
					break;
			}
		}
		this.$supers('doMouseOut_', arguments);		
	},
	doClick_: function (wevt, devt) {
		if (this.ebtn) {
			var target = zEvt.target(devt);
			switch (target) {
				case this.ebtn:
				case this.ebtned:
					if (this._isSlide || zAnima.count) return;
					if (this.ebtned == target) {
						var s = this.ereal.style;
						s.visibilty = "hidden";
						s.display = "";
						this._syncSize(true);
						s.visibilty = "";
						s.display = "none";
					}
					this.open_(!this.isOpen());
					break;
				case this.ecolled:					
					if (this._isSlide) return;
					this._isSlide = true;
					var s = this.ereal.style;
					s.visibilty = "hidden";
					s.display = "";
					this._syncSize();
					this._original = [s.left, s.top];
					this._alignTo();
					s.zIndex = 100;
			
					this.ebtn.style.display = "none"; 
					s.visibilty = "";
					s.display = "none";
					zAnima.slideDown(this, this.ereal, {
						anchor: this.sanchor,
						afterAnima: this.$class.afterSlideDown
					});
					break;
			}
		}
		this.$supers('doClick_', arguments);		
	},
	_doDocClick: function (evt) {
		var target = zEvt.target(evt);
		if (this._isSlide && !zDom.isAncestor(this.ereal, target)) {
			if (this.ebtned == target) {
				this.$class.afterSlideUp.apply(this, [target]);
				this.open_(true, false, true);
			} else 
				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid) || !zAnima.count) {
					this._isSlideUp = true;
					zAnima.slideUp(this, this.ereal, {
						anchor: this.sanchor,
						afterAnima: this.$class.afterSlideUp
					});
				}
		}
	},
	_syncSize: function (inclusive) {
		var layout = this.parent,
			el = layout.getNode(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			cH = height,
			cY = 0,
			cX = 0,
			n = layout.north,
			s = layout.south,
			e = layout.east,
			w = layout.west,
			c = layout.center;
		this._open = true;
		if (n && (zDom.isVisible(n.getNode()) || zDom.isVisible(n.ecolled))) {
			var ignoreSplit = n == this,
				ambit = layout._getAmbit(n, ignoreSplit),
				mars = layout._getMargins(n);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = mars.top;
			cY = ambit.h + ambit.y + mars.bottom;
			cH -= cY;
			if (ignoreSplit) {
				ambit.w = this.ecolled.offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(n, ambit, true);
				this._open = false;
				return;
			}
		}
		if (s && (zDom.isVisible(s.getNode()) || zDom.isVisible(s.ecolled))) {
			var ignoreSplit = s == this,
				ambit = layout._getAmbit(s, ignoreSplit),
				mars = layout._getMargins(s),
				total = (ambit.h + mars.top + mars.bottom);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = height - total + mars.top;
			cH -= total;
			if (ignoreSplit) {
				ambit.w = this.ecolled.offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(s, ambit, true);
				this._open = false;
				return;
			}
		}
		if (w && (zDom.isVisible(w.getNode()) || zDom.isVisible(w.ecolled))) {
			var ignoreSplit = w == this,
				ambit = layout._getAmbit(w, ignoreSplit),
				mars = layout._getMargins(w);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.ecolled.offsetHeight
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.h += cmars.top + cmars.bottom;
				}
				layout._resizeWgt(w, ambit, true);
				this._open = false;
				return;
			}
		}
		if (e && (zDom.isVisible(e.getNode()) || zDom.isVisible(e.ecolled))) {
			var ignoreSplit = e == this,
				ambit = layout._getAmbit(e, ignoreSplit),
				mars = layout._getMargins(e),
				total = (ambit.w + mars.left + mars.right); 
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = width - total + mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.ecolled.offsetHeight
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.h += cmars.top + cmars.bottom;
				}
				layout._resizeWgt(e, ambit, true);
				this._open = false;
				return;
			}
		}
	},
	_fixSplit: function () {
		zDom[this.isSplittable() ? 'show' : 'hide'](this.esplit);	
	},
	_alignTo: function () {
		var from = this.ecolled,
			to = this.ereal;
		switch (this.getPosition()) {
			case zkex.layout.Borderlayout.NORTH:
				to.style.top = from.offsetTop + from.offsetHeight + "px";
				to.style.left = from.offsetLeft + "px";
				break;
			case zkex.layout.Borderlayout.SOUTH:
				to.style.top = from.offsetTop - to.offsetHeight + "px";
				to.style.left = from.offsetLeft + "px";
				break;
			case zkex.layout.Borderlayout.WEST:
				to.style.left = from.offsetLeft + from.offsetWidth + "px";
				to.style.top = from.offsetTop + "px";
				break;
			case zkex.layout.Borderlayout.EAST:
				to.style.left = from.offsetLeft - to.offsetWidth + "px";
				to.style.top = from.offsetTop + "px";
				break;
		}
	},
	_isVertical : function () {
		return this.getPosition() != zkex.layout.Borderlayout.WEST &&
				this.getPosition() != zkex.layout.Borderlayout.EAST;
	},
	open_: function (open, silent, nonAnima) {
		if (!this.isCollapsible() || this.isOpen() == open)
			return; //nothing changed
			
		this._open = open;
		if (open) {
			if (this.ecolled) {
				if (!nonAnima) 
					zAnima.slideOut(this, this.ecolled, {
						anchor: this.sanchor,
						duration: 200,
						afterAnima: this.$class.afterSlideOut
					});
				else {
					zDom[open ? 'show' : 'hide'](this.ereal);
					zDom[!open ? 'show' : 'hide'](this.ecolled);
					zWatch.fireDown(open ? 'onVisible' : 'onHide', -1, this);
				}
			}
		} else {
			if (this.ecolled && !nonAnima) 
				zAnima.slideOut(this, this.ereal, {
						anchor: this.sanchor,
						beforeAnima: this.$class.beforeSlideOut,
						afterAnima: this.$class.afterSlideOut
					});
			else {
				if (this.ecolled)
					zDom[!open ? 'show' : 'hide'](this.ecolled);
				zDom[open ? 'show' : 'hide'](this.ereal);
			}
		}
		if (nonAnima) this.parent.resize();
		if (!silent) this.fire('onOpen', open);	
	},
	isImportantEvent_: function (evtnm) {
		return this.$class._impEvts[evtnm];
	}
}, {
	_impEvts: {onSize:1, onOpen:1},
	// invokes border layout's renderer before the component slides out
	beforeSlideOut: function (n) {
		var s = this.ecolled.style;
		s.display = "";
		s.visibility = "hidden";
		s.zIndex = 1;
		this.parent.resize();
	},
	// a callback function after the component slides out.
	afterSlideOut: function (n) {
		if (this.isOpen()) 
			zAnima.slideIn(this, this.ereal, {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideIn
			});
		else {
			var s = this.ecolled.style;
			s.zIndex = ""; // reset z-index refered to the beforeSlideOut()
			s.visibility = "";
			zAnima.slideIn(this, this.ecolled, {
				anchor: this.sanchor,				
				duration: 200
			});
		}
	},
	// recalculates the size of the whole border layout after the component sildes in.
	afterSlideIn: function (n) {
		this.parent.resize();
	},
	// a callback function after the collapsed region slides down
	afterSlideDown: function (n) {
		zEvt.listen(document, "click", this.proxy(this._doDocClick, '_pxdoDocClick'));
	},
	// a callback function after the collapsed region slides up
	afterSlideUp: function (n) {
		var s = n.style;
		s.left = this._original[0];
		s.top = this._original[1];
		n._lastSize = null;// reset size for Borderlayout
		s.zIndex = "";
		this.ebtn.style.display = "";
		zEvt.unlisten(document, "click", this._pxdoDocClick);
		this._isSlideUp = this._isSlide = false;
	},
	// Drag and drop
	_ignoredrag: function (dg, pointer, evt) {
			var target = zEvt.target(evt),
				wgt = dg.control;
			if (!target || target != wgt.esplit) return true;
			if (wgt.isSplittable() && wgt.isOpen()) {			
				var $Layout = zkex.layout.Borderlayout,
					pos = wgt.getPosition(),
					maxs = wgt.getMaxsize(),
					mins = wgt.getMinsize(),
					ol = wgt.parent,
					mars = ol._arrayToObject(wgt._margins),
					lr = zDom.frameWidth(wgt.ereal)
						+ (pos == $Layout.WEST ? mars.left : mars.right),
					tb = zDom.frameWidth(wgt.ereal)
						+ (pos == $Layout.NORTH ? mars.top : mars.bottom),
					min = 0,
					uuid = wgt.uuid;
				switch (pos) {
					case $Layout.NORTH:	
					case $Layout.SOUTH:
						var r = ol.center || (pos == $Layout.NORTH ? ol.south : ol.north);
						if (r) {
							if ($Layout.CENTER == r.getPosition()) {
								var east = ol.east,
									west = ol.west;
								maxs = Math.min(maxs, (wgt.ereal.offsetHeight + r.ereal.offsetHeight)- min);
							} else {
								maxs = Math.min(maxs, ol.getNode().offsetHeight
										- r.ereal.offsetHeight - r.esplit.offsetHeight
										- wgt.esplit.offsetHeight - min); 
							}
						} else {
							maxs = ol.getNode().offsetHeight - wgt.esplit.offsetHeight;
						}
						break;				
					case $Layout.WEST:
					case $Layout.EAST:
						var r = ol.center || (pos == $Layout.WEST ? ol.east : ol.west);
						if (r) {
							if ($Layout.CENTER == r.getPosition()) {
								maxs = Math.min(maxs, (wgt.ereal.offsetWidth
										+ zDom.revisedWidth(r.ereal, r.ereal.offsetWidth))- min);
							} else {
								maxs = Math.min(maxs, ol.getNode().offsetWidth
										- r.ereal.offsetWidth - r.esplit.offsetWidth - wgt.esplit.offsetWidth - min); 
							}
						} else {
							maxs = ol.getNode().offsetWidth - wgt.esplit.offsetWidth;
						}
						break;						
				}
				var ofs = zDom.cmOffset(wgt.ereal);
				dg._rootoffs = {
					maxs: maxs,
					mins: mins,
					top: ofs[1],
					left : ofs[0],
					right : wgt.ereal.offsetWidth,
					bottom: wgt.ereal.offsetHeight
				};
				return false;
			}
		return true;
	},
	_endeffect: function (dg, evt) {
		var wgt = dg.control,
			keys = "";
		if (wgt._isVertical())
			wgt.setHeight(dg._point[1] + 'px');
		else
			wgt.setWidth(dg._point[0] + 'px');
			
		dg._rootoffs = dg._point = null;
		
		wgt.parent.resize();
		wgt.fire('onSize', {
			width: wgt.ereal.style.width,
			height: wgt.ereal.style.height,
			keys: zEvt.keyMetaData(evt),
			marshal: wgt.$class._onSizeMarshal
		});
	},
	_onSizeMarshal: function () {
		return [this.width, this.height, this.keys ? this.keys.marshal(): ''];
	},
	_snap: function (dg, pointer) {
		var wgt = dg.control,
			x = pointer[0],
			y = pointer[1],
			$Layout = zkex.layout.Borderlayout,
			b = dg._rootoffs, w, h;	
		switch (wgt.getPosition()) {
			case $Layout.NORTH:
				if (y > b.maxs + b.top) y = b.maxs + b.top;
				if (y < b.mins + b.top) y = b.mins + b.top;
				w = x;
				h = y - b.top;
				break;				
			case $Layout.SOUTH:
				if (b.top + b.bottom - y - wgt.esplit.offsetHeight > b.maxs) {
					y = b.top + b.bottom - b.maxs - wgt.esplit.offsetHeight;
					h = b.maxs;
				} else if (b.top + b.bottom - b.mins - wgt.esplit.offsetHeight <= y) {
					y = b.top + b.bottom - b.mins - wgt.esplit.offsetHeight;
					h = b.mins;
				} else h = b.top - y + b.bottom - wgt.esplit.offsetHeight;
				w = x;
				break;
			case $Layout.WEST:
				if (x > b.maxs + b.left) x = b.maxs + b.left;
				if (x < b.mins + b.left) x = b.mins + b.left;
				w = x - b.left;
				h = y;
				break;		
			case $Layout.EAST:			
				if (b.left + b.right - x - wgt.esplit.offsetWidth > b.maxs) {
					x = b.left + b.right - b.maxs - wgt.esplit.offsetWidth;
					w = b.maxs;
				} else if (b.left + b.right - b.mins - wgt.esplit.offsetWidth <= x) {
					x = b.left + b.right - b.mins - wgt.esplit.offsetWidth;
					w = b.mins;
				} else w = b.left - x + b.right - wgt.esplit.offsetWidth;
				h = y;
				break;						
		}
		dg._point = [w, h];
		return [x, y];
	},
	_ghosting: function (dg, ofs, evt) {
		var el = dg.node,
			html = '<div id="zk_layoutghost" style="background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;cursor:'+el.style.cursor+';"><img src="'+zAu.comURI('/web/img/spacer.gif')
					+'"/></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return zDom.$("zk_layoutghost");
	}
});
