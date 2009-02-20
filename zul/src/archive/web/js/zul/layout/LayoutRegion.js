/* LayoutRegion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.LayoutRegion = zk.$extends(zul.Widget, {
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
			var btn = this.getSubnode(this.isOpen() ? 'btn' : 'btned');
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
			var cave = this.getSubnode('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.firstChild ?
						this.firstChild.getNode() : cave;
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
	setOpen: function (open, fromServer, nonAnima) {
		if (this._open != open) {
			this._open = open;
			if (!this.getNode() || !this.isCollapsible())
				return; //nothing changed

			var colled = this.getSubnode('colled'),
				real = this.getSubnode('real');
			if (open) {
				if (colled) {
					if (!nonAnima) 
						zAnima.slideOut(this, colled, {
							anchor: this.sanchor,
							duration: 200,
							afterAnima: this.$class.afterSlideOut
						});
					else {
						zDom[open ? 'show' : 'hide'](real);
						zDom[!open ? 'show' : 'hide'](colled);
						zWatch.fireDown(open ? 'onVisible' : 'onHide', {visible:true}, this);
					}
				}
			} else {
				if (colled && !nonAnima) 
					zAnima.slideOut(this, real, {
							anchor: this.sanchor,
							beforeAnima: this.$class.beforeSlideOut,
							afterAnima: this.$class.afterSlideOut
						});
				else {
					if (colled)
						zDom[!open ? 'show' : 'hide'](colled);
					zDom[open ? 'show' : 'hide'](real);
				}
			}
			if (nonAnima) this.parent.resize();
			if (!fromServer) this.fire('onOpen', open);	
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
		var real = this.getSubnode('real');
		if (real) real.style.width = width ? width: '';
	},
	setHeight: function (height) {
		this._height = height;
		var real = this.getSubnode('real');
		if (real) real.style.height = height ? height: '';
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			var real = this.getSubnode('real');
			if (real) {
				real.style.display = real.parentNode.style.display;
				this.parent.resize();
			}
		}
	},	
	updateDomClass_: function () {
		if (this.desktop) {
			var real = this.getSubnode('real');
			if (real) {
				real.className = this.domClass_();
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var real = this.getSubnode('real');
			if (real) {
				zDom.setStyle(real, zDom.parseStyle(this.domStyle_()));
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(true);
			zDom.addClass(this.getNode(), this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
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
		if (this.getPosition() != zul.layout.Borderlayout.CENTER) {
			var split = this.getSubnode('split');			
			if (split) {
				this._fixSplit();
				var vert = this._isVertical(),
					$LayoutRegion = this.$class;
				
				this._drag = new zk.Draggable(this, split, {
					constraint: vert ? 'vertical': 'horizontal',
					ghosting: $LayoutRegion._ghosting,
					snap: $LayoutRegion._snap,
					zIndex: 12000,
					overlay: true,
					ignoredrag: $LayoutRegion._ignoredrag,
					endeffect: $LayoutRegion._endeffect					
				});	
				if (!this.isOpen()) {
					this._open = true;
					this.setOpen(false, true, true);
				}
			}
		}
		var n = this.getNode(),
			real = n.firstChild;
					
		if (this.isOpen() && !zDom.isVisible(real)) n.style.display = "none";
		
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			zEvt.listen(bodyEl, "scroll", this.proxy(this.doScroll_, '_pxDoscroll'));
		}
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			zEvt.unlisten(bodyEl, "scroll", this._pxDoscroll);
		}
		if (this.getSubnode('split')) {			
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}
		this.$supers('unbind_', arguments);
	},
	doScroll_: function () {
		zWatch.fireDown('onScroll', null, this);
	},
	doMouseOver_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.nativeTarget) {
			case this.getSubnode('btn'):
				zDom.addClass(this.getSubnode('btn'), this.getZclass() + '-collapse-over');
				break;
			case this.getSubnode('btned'):
				zDom.addClass(this.getSubnode('btned'), this.getZclass() + '-expand-over');
				// don't break
			case this.getSubnode('colled'):
				zDom.addClass(this.getSubnode('colled'), this.getZclass() + '-collapsed-over');
				break;
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.nativeTarget) {
			case this.getSubnode('btn'):
				zDom.rmClass(this.getSubnode('btn'), this.getZclass() + '-collapse-over');
				break;
			case this.getSubnode('btned'):
				zDom.rmClass(this.getSubnode('btned'), this.getZclass() + '-expand-over');
				// don't break
			case this.getSubnode('colled'):
				zDom.rmClass(this.getSubnode('colled'), this.getZclass() + '-collapsed-over');
				break;
			}
		}
		this.$supers('doMouseOut_', arguments);		
	},
	doClick_: function (evt) {
		if (this.getSubnode('btn')) {
			var target = evt.nativeTarget;
			switch (target) {
			case this.getSubnode('btn'):
			case this.getSubnode('btned'):
				if (this._isSlide || zAnima.count) return;
				if (this.getSubnode('btned') == target) {
					var s = this.getSubnode('real').style;
					s.visibilty = "hidden";
					s.display = "";
					this._syncSize(true);
					s.visibilty = "";
					s.display = "none";
				}
				this.setOpen(!this.isOpen());
				break;
			case this.getSubnode('colled'):					
				if (this._isSlide) return;
				this._isSlide = true;
				var real = this.getSubnode('real'),
					s = real.style;
				s.visibilty = "hidden";
				s.display = "";
				this._syncSize();
				this._original = [s.left, s.top];
				this._alignTo();
				s.zIndex = 100;

				this.getSubnode('btn').style.display = "none"; 
				s.visibilty = "";
				s.display = "none";
				zAnima.slideDown(this, real, {
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
		if (this._isSlide && !zDom.isAncestor(this.getSubnode('real'), target)) {
			if (this.getSubnode('btned') == target) {
				this.$class.afterSlideUp.apply(this, [target]);
				this.setOpen(true, false, true);
			} else 
				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid) || !zAnima.count) {
					this._isSlideUp = true;
					zAnima.slideUp(this, this.getSubnode('real'), {
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
		if (n && (zDom.isVisible(n.getNode()) || zDom.isVisible(n.getSubnode('colled')))) {
			var ignoreSplit = n == this,
				ambit = layout._getAmbit(n, ignoreSplit),
				mars = layout._getMargins(n);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = mars.top;
			cY = ambit.h + ambit.y + mars.bottom;
			cH -= cY;
			if (ignoreSplit) {
				ambit.w = this.getSubnode('colled').offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(n, ambit, true);
				this._open = false;
				return;
			}
		}
		if (s && (zDom.isVisible(s.getNode()) || zDom.isVisible(s.getSubnode('colled')))) {
			var ignoreSplit = s == this,
				ambit = layout._getAmbit(s, ignoreSplit),
				mars = layout._getMargins(s),
				total = (ambit.h + mars.top + mars.bottom);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = height - total + mars.top;
			cH -= total;
			if (ignoreSplit) {
				ambit.w = this.getSubnode('colled').offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(s, ambit, true);
				this._open = false;
				return;
			}
		}
		if (w && (zDom.isVisible(w.getNode()) || zDom.isVisible(w.getSubnode('colled')))) {
			var ignoreSplit = w == this,
				ambit = layout._getAmbit(w, ignoreSplit),
				mars = layout._getMargins(w);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.getSubnode('colled').offsetHeight
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.h += cmars.top + cmars.bottom;
				}
				layout._resizeWgt(w, ambit, true);
				this._open = false;
				return;
			}
		}
		if (e && (zDom.isVisible(e.getNode()) || zDom.isVisible(e.getSubnode('colled')))) {
			var ignoreSplit = e == this,
				ambit = layout._getAmbit(e, ignoreSplit),
				mars = layout._getMargins(e),
				total = (ambit.w + mars.left + mars.right); 
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = width - total + mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.getSubnode('colled').offsetHeight
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
		zDom[this.isSplittable() ? 'show' : 'hide'](this.getSubnode('split'));	
	},
	_alignTo: function () {
		var from = this.getSubnode('colled'),
			to = this.getSubnode('real');
		switch (this.getPosition()) {
		case zul.layout.Borderlayout.NORTH:
			to.style.top = from.offsetTop + from.offsetHeight + "px";
			to.style.left = from.offsetLeft + "px";
			break;
		case zul.layout.Borderlayout.SOUTH:
			to.style.top = from.offsetTop - to.offsetHeight + "px";
			to.style.left = from.offsetLeft + "px";
			break;
		case zul.layout.Borderlayout.WEST:
			to.style.left = from.offsetLeft + from.offsetWidth + "px";
			to.style.top = from.offsetTop + "px";
			break;
		case zul.layout.Borderlayout.EAST:
			to.style.left = from.offsetLeft - to.offsetWidth + "px";
			to.style.top = from.offsetTop + "px";
			break;
		}
	},
	_isVertical : function () {
		return this.getPosition() != zul.layout.Borderlayout.WEST &&
				this.getPosition() != zul.layout.Borderlayout.EAST;
	}
}, {
	// invokes border layout's renderer before the component slides out
	beforeSlideOut: function (n) {
		var s = this.getSubnode('colled').style;
		s.display = "";
		s.visibility = "hidden";
		s.zIndex = 1;
		this.parent.resize();
	},
	// a callback function after the component slides out.
	afterSlideOut: function (n) {
		if (this.isOpen()) 
			zAnima.slideIn(this, this.getSubnode('real'), {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideIn
			});
		else {
			var colled = this.getSubnode('colled'),
				s = colled.style;
			s.zIndex = ""; // reset z-index refered to the beforeSlideOut()
			s.visibility = "";
			zAnima.slideIn(this, colled, {
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
		this.getSubnode('btn').style.display = "";
		zEvt.unlisten(document, "click", this._pxdoDocClick);
		this._isSlideUp = this._isSlide = false;
	},
	// Drag and drop
	_ignoredrag: function (dg, pointer, evt) {
			var target = zEvt.target(evt),
				wgt = dg.control;
			if (!target || target != wgt.getSubnode('split')) return true;
			if (wgt.isSplittable() && wgt.isOpen()) {			
				var $Layout = zul.layout.Borderlayout,
					pos = wgt.getPosition(),
					maxs = wgt.getMaxsize(),
					mins = wgt.getMinsize(),
					ol = wgt.parent,
					real = wgt.getSubnode('real'),
					mars = ol._arrayToObject(wgt._margins),
					lr = zDom.padBorderWidth(real)
						+ (pos == $Layout.WEST ? mars.left : mars.right),
					tb = zDom.padBorderWidth(real)
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
							maxs = Math.min(maxs, (real.offsetHeight + r.getSubnode('real').offsetHeight)- min);
						} else {
							maxs = Math.min(maxs, ol.getNode().offsetHeight
									- r.getSubnode('real').offsetHeight - r.getSubnode('split').offsetHeight
									- wgt.getSubnode('split').offsetHeight - min); 
						}
					} else {
						maxs = ol.getNode().offsetHeight - wgt.getSubnode('split').offsetHeight;
					}
					break;				
				case $Layout.WEST:
				case $Layout.EAST:
					var r = ol.center || (pos == $Layout.WEST ? ol.east : ol.west);
					if (r) {
						if ($Layout.CENTER == r.getPosition()) {
							maxs = Math.min(maxs, (real.offsetWidth
									+ zDom.revisedWidth(r.getSubnode('real'), r.getSubnode('real').offsetWidth))- min);
						} else {
							maxs = Math.min(maxs, ol.getNode().offsetWidth
									- r.getSubnode('real').offsetWidth - r.getSubnode('split').offsetWidth - wgt.getSubnode('split').offsetWidth - min); 
						}
					} else {
						maxs = ol.getNode().offsetWidth - wgt.getSubnode('split').offsetWidth;
					}
					break;						
				}
				var ofs = zDom.cmOffset(real);
				dg._rootoffs = {
					maxs: maxs,
					mins: mins,
					top: ofs[1],
					left : ofs[0],
					right : real.offsetWidth,
					bottom: real.offsetHeight
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
			width: wgt.getSubnode('real').style.width,
			height: wgt.getSubnode('real').style.height,
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
			$Layout = zul.layout.Borderlayout,
			split = wgt.getSubnode('split'),
			b = dg._rootoffs, w, h;
		switch (wgt.getPosition()) {
		case $Layout.NORTH:
			if (y > b.maxs + b.top) y = b.maxs + b.top;
			if (y < b.mins + b.top) y = b.mins + b.top;
			w = x;
			h = y - b.top;
			break;				
		case $Layout.SOUTH:
			if (b.top + b.bottom - y - split.offsetHeight > b.maxs) {
				y = b.top + b.bottom - b.maxs - split.offsetHeight;
				h = b.maxs;
			} else if (b.top + b.bottom - b.mins - split.offsetHeight <= y) {
				y = b.top + b.bottom - b.mins - split.offsetHeight;
				h = b.mins;
			} else h = b.top - y + b.bottom - split.offsetHeight;
			w = x;
			break;
		case $Layout.WEST:
			if (x > b.maxs + b.left) x = b.maxs + b.left;
			if (x < b.mins + b.left) x = b.mins + b.left;
			w = x - b.left;
			h = y;
			break;		
		case $Layout.EAST:			
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
		dg._point = [w, h];
		return [x, y];
	},
	_ghosting: function (dg, ofs, evt) {
		var el = dg.node,
			html = '<div id="zk_layoutghost" style="font-size:0;line-height:0;background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;cursor:'+el.style.cursor+';"></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return zDom.$("zk_layoutghost");
	}
});
