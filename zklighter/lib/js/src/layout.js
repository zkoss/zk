_z='zkex.layout';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

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
		if (region && !wgt.isOpen()) {
			var colled = wgt.getSubnode('colled');
			return {
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
		}
		var w = wgt.getWidth() || '',
			h = wgt.getHeight() || '',
			widx = w.indexOf('%'),
			hidx = h.indexOf('%');

		var ambit = {
			w: widx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetWidth * zk.parseInt(w.substring(0, widx)) / 100),
					0) : wgt.getSubnode('real').offsetWidth, 
			h: hidx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetHeight * zk.parseInt(h.substring(0, hidx)) / 100),
					0) : wgt.getSubnode('real').offsetHeight
		};
		if (region && !ignoreSplit) {
			var split = wgt.getSubnode('split') || {offsetHeight:0, offsetWidth:0};
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
				colled.style.height = zDom.revisedHeight(colled, ambit.h) + "px";
				colled.style.width = zDom.revisedWidth(colled, ambit.w) + "px";
			}
		}
	},
	_resizeSplit: function (wgt, ambit) {
		var split = wgt.getSubnode('split');
		if (!zDom.isVisible(split)) return ambit;
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
			ambit.w = zDom.revisedWidth(el, ambit.w);
			el.style.width = ambit.w + "px";
			ambit.w = zDom.revisedWidth(bodyEl, ambit.w);
			bodyEl.style.width = ambit.w + "px";

			ambit.h = zDom.revisedHeight(el, ambit.h);
			el.style.height = ambit.h + "px";
			ambit.h = zDom.revisedHeight(bodyEl, ambit.h);
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
	onVisible: _zkf
}, {
	NORTH: "north",
	SOUTH: "south",
	EAST: "east",
	WEST: "west",
	CENTER: "center"
});

(_zkwg=_zkpk.Borderlayout).prototype.className='zkex.layout.Borderlayout';
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
	doMouseOver_: function (wevt) {
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
	doMouseOut_: function (wevt) {
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
	doClick_: function (wevt) {
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
				var $Layout = zkex.layout.Borderlayout,
					pos = wgt.getPosition(),
					maxs = wgt.getMaxsize(),
					mins = wgt.getMinsize(),
					ol = wgt.parent,
					real = wgt.getSubnode('real'),
					mars = ol._arrayToObject(wgt._margins),
					lr = zDom.frameWidth(real)
						+ (pos == $Layout.WEST ? mars.left : mars.right),
					tb = zDom.frameWidth(real)
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
			$Layout = zkex.layout.Borderlayout,
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

(_zkwg=_zkpk.LayoutRegion).prototype.className='zkex.layout.LayoutRegion';
zkex.layout.North = zk.$extends(zkex.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 't',
	
	getPosition: function () {
		return zkex.layout.Borderlayout.NORTH;
	},
	getSize: function () {
		return this.getHeight();
	},
	setSize: function (size) {
		this.setHeight(size);
	}
});
(_zkwg=_zkpk.North).prototype.className='zkex.layout.North';
zkex.layout.South = zk.$extends(zkex.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 'b',
	getPosition: function () {
		return zkex.layout.Borderlayout.SOUTH;
	},
	getSize: function () {
		return this.getHeight();
	},
	setSize: function (size) {
		this.setHeight(size);
	}
});
(_zkwg=_zkpk.South).prototype.className='zkex.layout.South';
zkex.layout.Center = zk.$extends(zkex.layout.LayoutRegion, {
	setCmargins: zk.$void,    // readonly
	setSplittable: zk.$void,  // readonly
	setOpen: zk.$void,        // readonly
	setCollapsible: zk.$void, // readonly
	setMaxsize: zk.$void,     // readonly
	setMinsize: zk.$void,     // readonly
	setHeight: zk.$void,      // readonly
	setWidth: zk.$void,       // readonly
	setVisible: zk.$void,     // readonly
	getSize: zk.$void,        // readonly
	setSize: zk.$void,        // readonly
	doMouseOver_: zk.$void,   // do nothing.
	doMouseOut_: zk.$void,    // do nothing.
	doClick_: zk.$void,       // do nothing.
	
	getPosition: function () {
		return zkex.layout.Borderlayout.CENTER;
	}
});
(_zkwg=_zkpk.Center).prototype.className='zkex.layout.Center';
zkex.layout.East = zk.$extends(zkex.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'r',
	
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zkex.layout.Borderlayout.EAST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});
(_zkwg=_zkpk.East).prototype.className='zkex.layout.East';
zkex.layout.West = zk.$extends(zkex.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'l',
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zkex.layout.Borderlayout.WEST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});

(_zkwg=_zkpk.West).prototype.className='zkex.layout.West';
}finally{zPkg.end(_z);}}