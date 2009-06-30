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

	$define: {
		title: _zkf = function () {
			this.rerender();
		},
		flex: _zkf,
		border: function (border) {
			if (!border || '0' == border)
				this._border = border = "none";
			this.updateDomClass_();
		},
		splittable: function (splittable) {
			if (this.parent && this.desktop)
				this.parent.resize();
		},
		maxsize: null,
		minsize: null,

		collapsible: function (collapsible) {
			var btn = this.getSubnode(this.isOpen() ? 'btn' : 'btned');
			if (btn)
				btn.style.display = collapsible ? '' : 'none';
		},
		autoscroll: function (autoscroll) {
			var cave = this.getSubnode('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.firstChild ?
						this.firstChild.getNode() : cave;
				if (autoscroll) {
					bodyEl.style.overflow = "auto";
					bodyEl.style.position = "relative";
					this.domListen_(bodyEl, "onScroll");
				} else {
					bodyEl.style.overflow = "hidden";
					bodyEl.style.position = "";
					this.domUnlisten_(bodyEl, "onScroll");
				}
			}
		},
		open: function (open, fromServer, nonAnima) {
			if (!this.getNode() || !this.isCollapsible())
				return; //nothing changed
	
			var colled = this.getSubnode('colled'),
				real = this.getSubnode('real');
			if (open) {
				if (colled) {
					if (!nonAnima) 
						zk(colled).slideOut(this, {
							anchor: this.sanchor,
							duration: 200,
							afterAnima: this.$class.afterSlideOut
						});
					else {
						jq(real)[open ? 'show' : 'hide']();
						jq(colled)[!open ? 'show' : 'hide']();
						zWatch.fireDown(open ? 'onShow' : 'onHide', {visible:true}, this);
					}
				}
			} else {
				if (colled && !nonAnima) 
					zk(real).slideOut(this, {
							anchor: this.sanchor,
							beforeAnima: this.$class.beforeSlideOut,
							afterAnima: this.$class.afterSlideOut
						});
				else {
					if (colled)
						jq(colled)[!open ? 'show' : 'hide']();
					jq(real)[open ? 'show' : 'hide']();
				}
			}
			if (nonAnima) this.parent.resize();
			if (!fromServer) this.fire('onOpen', {open:open});
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
				zk(real).setStyles(jq.parseStyle(this.domStyle_()));
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(true);
			jq(this.getNode()).addClass(this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(false);
			jq(this.getNode()).removeClass(this.getZclass() + "-nested");
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
					var colled = this.getSubnode('colled'),
						real = this.getSubnode('real');
					if (colled)
						jq(colled).show();
					jq(real).hide();
				}
			}
		}
		var n = this.getNode(),
			real = n.firstChild;
					
		if (this.isOpen() && !this.isVisible()) n.style.display = "none";
		
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			this.domListen_(bodyEl, "onScroll");
		}
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			this.domUnlisten_(bodyEl, "onScroll");
		}
		if (this.getSubnode('split')) {			
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}
		this.$supers('unbind_', arguments);
	},
	_doScroll: function () {
		zWatch.fireDown('onScroll', null, this);
	},
	doMouseOver_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.domTarget) {
			case this.getSubnode('btn'):
				jq(this.getSubnode('btn')).addClass(this.getZclass() + '-colps-over');
				break;
			case this.getSubnode('btned'):
				jq(this.getSubnode('btned')).addClass(this.getZclass() + '-exp-over');
				// don't break
			case this.getSubnode('colled'):
				jq(this.getSubnode('colled')).addClass(this.getZclass() + '-colpsd-over');
				break;
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.domTarget) {
			case this.getSubnode('btn'):
				jq(this.getSubnode('btn')).removeClass(this.getZclass() + '-colps-over');
				break;
			case this.getSubnode('btned'):
				jq(this.getSubnode('btned')).removeClass(this.getZclass() + '-exp-over');
				// don't break
			case this.getSubnode('colled'):
				jq(this.getSubnode('colled')).removeClass(this.getZclass() + '-colpsd-over');
				break;
			}
		}
		this.$supers('doMouseOut_', arguments);		
	},
	doClick_: function (evt) {
		if (this.getSubnode('btn')) {
			var target = evt.domTarget;
			switch (target) {
			case this.getSubnode('btn'):
			case this.getSubnode('btned'):
				if (this._isSlide || zk.animating()) return;
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
				zk(real).slideDown(this, {
					anchor: this.sanchor,
					afterAnima: this.$class.afterSlideDown
				});
				break;
			}
		}
		this.$supers('doClick_', arguments);		
	},
	_docClick: function (evt) {
		var target = evt.target;
		if (this._isSlide && !jq.isAncestor(this.getSubnode('real'), target)) {
			if (this.getSubnode('btned') == target) {
				this.$class.afterSlideUp.apply(this, [target]);
				this.setOpen(true, false, true);
			} else 
				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid) || !zk.animating()) {
					this._isSlideUp = true;
					zk(this.getSubnode('real')).slideUp(this, {
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
			center = {
				x: 0,
				y: 0,
				w: width,
				h: height
			};
		
		this._open = true;
		
		for (var region, ambit, margin,	rs = ['north', 'south', 'west', 'east'],
				j = 0, k = rs.length; j < k; ++j) {
			region = layout[rs[j]];
			if (region && (zk(region.getNode()).isVisible()
					|| zk(region.getSubnode('colled')).isVisible())) {
				var ignoreSplit = region == this,
				ambit = layout._getAmbit(region, ignoreSplit);
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
						ambit.w = this.getSubnode('colled').offsetWidth;
						if (inclusive) {
							var cmars = layout._arrayToObject(this._cmargins);
							ambit.w += cmars.left + cmars.right;
						}
						layout._resizeWgt(region, ambit, true);
						this._open = false;
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
						ambit.h = this.getSubnode('colled').offsetHeight;
						if (inclusive) {
							var cmars = layout._arrayToObject(this._cmargins);
							ambit.h += cmars.top + cmars.bottom;
						}
						layout._resizeWgt(region, ambit, true);
						this._open = false;
						return;
					}
					break;
				}
			}
		}
	},
	_fixSplit: function () {
		jq(this.getSubnode('split'))[this.isSplittable() ? 'show' : 'hide']();	
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
			zk(this.getSubnode('real')).slideIn(this, {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideIn
			});
		else {
			var colled = this.getSubnode('colled'),
				s = colled.style;
			s.zIndex = ""; // reset z-index refered to the beforeSlideOut()
			s.visibility = "";
			zk(colled).slideIn(this, {
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
		jq(document).click(this.proxy(this._docClick));
	},
	// a callback function after the collapsed region slides up
	afterSlideUp: function (n) {
		var s = n.style;
		s.left = this._original[0];
		s.top = this._original[1];
		n._lastSize = null;// reset size for Borderlayout
		s.zIndex = "";
		this.getSubnode('btn').style.display = "";
		jq(document).unbind("click", this.proxy(this._docClick));
		this._isSlideUp = this._isSlide = false;
	},
	//drag
	_ignoredrag: function (dg, pointer, evt) {
			var target = evt.domTarget,
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
					pbw = zk(real).padBorderWidth(),
					lr = pbw + (pos == $Layout.WEST ? mars.left : mars.right),
					tb = pbw + (pos == $Layout.NORTH ? mars.top : mars.bottom),
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
									+ zk(r.getSubnode('real')).revisedWidth(r.getSubnode('real').offsetWidth))- min);
						} else {
							maxs = Math.min(maxs, ol.getNode().offsetWidth
									- r.getSubnode('real').offsetWidth - r.getSubnode('split').offsetWidth - wgt.getSubnode('split').offsetWidth - min); 
						}
					} else {
						maxs = ol.getNode().offsetWidth - wgt.getSubnode('split').offsetWidth;
					}
					break;						
				}
				var ofs = zk(real).cmOffset();
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
		wgt.fire('onSize', zk.copy({
			width: wgt.getSubnode('real').style.width,
			height: wgt.getSubnode('real').style.height
		}, evt.data));
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
		var el = dg.node, $el = zk(el);
		jq(document.body).prepend('<div id="zk_layoutghost" style="font-size:0;line-height:0;background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+$el.offsetWidth()+'px;height:'+$el.offsetHeight()
			+'px;cursor:'+el.style.cursor+';"></div>');
		return jq("#zk_layoutghost")[0];
	}
});
