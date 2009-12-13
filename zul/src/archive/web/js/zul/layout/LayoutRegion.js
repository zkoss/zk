/* LayoutRegion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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
		flex: function () {
			this.rerender();
		},
		border: function (border) {
			if (!border || '0' == border)
				this._border = border = "none";
				
			if (this.desktop)
				(this.$n('real') || {})._lastSize = null;
				
			this.updateDomClass_();
		},
		title: function () {
			this.rerender();
		},
		splittable: function (splittable) {
			if (this.parent && this.desktop)
				this.parent.resize();
		},
		maxsize: null,
		minsize: null,

		collapsible: function (collapsible) {
			var btn = this.$n(this._open ? 'btn' : 'btned');
			if (btn)
				btn.style.display = collapsible ? '' : 'none';
		},
		autoscroll: function (autoscroll) {
			var cave = this.$n('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.firstChild ?
						this.firstChild.$n() : cave;
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
			if (!this.$n() || !this.isCollapsible())
				return; //nothing changed
	
			var colled = this.$n('colled'),
				real = this.$n('real');
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
						zWatch.fireDown(open ? 'onShow' : 'onHide', this);
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

	getCmargins: function () {
		return zUtl.intsToString(this._open ? this._margins : this._cmargins);
	},
	setCmargins: function (cmargins) {
		if (this.getCmargins() != cmargins) {
			this._cmargins = zUtl.stringToInts(cmargins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},

	getCurrentMargins_: function () {
		return zul.layout.LayoutRegion._aryToObject(this._open ? this._margins : this._cmargins);
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
		var real = this.$n('real');
		if (real) {
			real.style.width = width ? width : '';
			real._lastSize = null;
		}
		return this;
	},
	setHeight: function (height) {
		this._height = height;
		var real = this.$n('real');
		if (real) {
			real.style.height = height ? height : '';
			real._lastSize = null;
		}
		return this;
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			var real = this.$n('real');
			if (real) {
				real.style.display = real.parentNode.style.display;
				this.parent.resize();
			}
		}
		return this;
	},
	//@Override to apply the calculated value on xxx-real element
	setFlexSize_: function(sz) {
		var n = this.$n('real');
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height == '')
				n.style.height = this._height ? this._height : '';
			else {
				var cave = this.$n('cave'),
					hgh = cave ? (cave.offsetHeight + cave.offsetTop) : zk(n).revisedHeight(sz.height, true);   
				if (zk.ie) n.style.height = '';
				n.style.height = jq.px0(hgh);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width == '')
				n.style.width = this._width ? this._width : '';
			else {
				var wdh = zk(n).revisedWidth(sz.width, true);
				if (zk.ie) n.style.width = '';
				n.style.width = jq.px0(wdh);
			}
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	updateDomClass_: function () {
		if (this.desktop) {
			var real = this.$n('real');
			if (real) {
				real.className = this.domClass_();
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var real = this.$n('real');
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
			jq(this.$n()).addClass(this.getZclass() + "-nested");
		}
		
		// reset
		(this.$n('real') || {})._lastSize = null;
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(false);
			jq(this.$n()).removeClass(this.getZclass() + "-nested");
		}
		
		// reset
		(this.$n('real') || {})._lastSize = null;
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.parent)
			this.parent.resize();
		return this;
	},
	bind_: function(){
		this.$supers('bind_', arguments);
		if (this.getPosition() != zul.layout.Borderlayout.CENTER) {
			var split = this.$n('split');			
			if (split) {
				this._fixSplit();
				var vert = this._isVertical(),
					LR = this.$class;
				
				this._drag = new zk.Draggable(this, split, {
					constraint: vert ? 'vertical': 'horizontal',
					ghosting: LR._ghosting,
					snap: LR._snap,
					zIndex: 12000,
					overlay: true,
					ignoredrag: LR._ignoredrag,
					endeffect: LR._endeffect
				});
				
				if (!this._open) {
					var colled = this.$n('colled'),
						real = this.$n('real');
					if (colled)
						jq(colled).show();
					jq(real).hide();
				}
			}
		}
				
		var n = this.$n(),
			real = n.firstChild;
					
		if (this._open && !this.isVisible()) n.style.display = "none";
		
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.$n() : this.$n('cave');
			this.domListen_(bodyEl, "onScroll");
		}
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.$n() : this.$n('cave');
			this.domUnlisten_(bodyEl, "onScroll");
		}
		if (this.$n('split')) {			
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}
		this.$supers('unbind_', arguments);
	},
	doMouseOver_: function (evt) {
		switch (evt.domTarget) {
		case this.$n('btn'):
			jq(this.$n('btn')).addClass(this.getZclass() + '-colps-over');
			break;
		case this.$n('btned'):
			jq(this.$n('btned')).addClass(this.getZclass() + '-exp-over');
			// don't break
		case this.$n('colled'):
			jq(this.$n('colled')).addClass(this.getZclass() + '-colpsd-over');
			break;
		case this.$n('splitbtn'):
			jq(this.$n('splitbtn')).addClass(this.getZclass() + '-splt-btn-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.domTarget) {
		case this.$n('btn'):
			jq(this.$n('btn')).removeClass(this.getZclass() + '-colps-over');
			break;
		case this.$n('btned'):
			jq(this.$n('btned')).removeClass(this.getZclass() + '-exp-over');
			// don't break
		case this.$n('colled'):
			jq(this.$n('colled')).removeClass(this.getZclass() + '-colpsd-over');
			break;
		case this.$n('splitbtn'):
			jq(this.$n('splitbtn')).removeClass(this.getZclass() + '-splt-btn-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);		
	},
	doClick_: function (evt) {
		var target = evt.domTarget;
		switch (target) {
		case this.$n('btn'):
		case this.$n('btned'):
		case this.$n('splitbtn'):
			if (this._isSlide || zk.animating()) return;
			if (this.$n('btned') == target) {
				var s = this.$n('real').style;
				s.visibility = "hidden";
				s.display = "";
				this._syncSize(true);
				s.visibility = "";
				s.display = "none";
			}
			this.setOpen(!this._open);
			break;
		case this.$n('colled'):					
			if (this._isSlide) return;
			this._isSlide = true;
			var real = this.$n('real'),
				s = real.style;
			s.visibility = "hidden";
			s.display = "";
			this._syncSize();
			this._original = [s.left, s.top];
			this._alignTo();
			s.zIndex = 100;

			if (this.$n('btn'))
				this.$n('btn').style.display = "none"; 
			s.visibility = "";
			s.display = "none";
			zk(real).slideDown(this, {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideDown
			});
			break;
		}
		this.$supers('doClick_', arguments);		
	},
	_docClick: function (evt) {
		var target = evt.target;
		if (this._isSlide && !jq.isAncestor(this.$n('real'), target)) {
			if (this.$n('btned') == target) {
				this.$class.afterSlideUp.apply(this, [target]);
				this.setOpen(true, false, true);
			} else 
				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid) || !zk.animating()) {
					this._isSlideUp = true;
					zk(this.$n('real')).slideUp(this, {
						anchor: this.sanchor,
						afterAnima: this.$class.afterSlideUp
					});
				}
		}
	},
	_syncSize: function (inclusive) {
		var layout = this.parent,
			el = layout.$n(),
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
			if (region && (zk(region.$n()).isVisible()
			|| zk(region.$n('colled')).isVisible())) {
				var ignoreSplit = region == this,
					ambit = region._ambit(ignoreSplit),
					LR = this.$class;
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
						ambit.w = this.$n('colled').offsetWidth;
						if (inclusive) {
							var cmars = LR._aryToObject(this._cmargins);
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
						ambit.h = this.$n('colled').offsetHeight;
						if (inclusive) {
							var cmars = LR._aryToObject(this._cmargins);
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
	_alignTo: function () {
		var from = this.$n('colled'),
			to = this.$n('real'),
			BL = zul.layout.Borderlayout;
		switch (this.getPosition()) {
		case BL.NORTH:
			to.style.top = jq.px(from.offsetTop + from.offsetHeight);
			to.style.left = jq.px(from.offsetLeft);
			break;
		case BL.SOUTH:
			to.style.top = jq.px(from.offsetTop - to.offsetHeight);
			to.style.left = jq.px(from.offsetLeft);
			break;
		case BL.WEST:
			to.style.left = jq.px(from.offsetLeft + from.offsetWidth);
			to.style.top = jq.px(from.offsetTop);
			break;
		case BL.EAST:
			to.style.left = jq.px(from.offsetLeft - to.offsetWidth);
			to.style.top = jq.px(from.offsetTop);
			break;
		}
	},
	_doScroll: function () {
		zWatch.fireDown('onScroll', this);
	},
	_fixSplit: function () {
		jq(this.$n('split'))[this._splittable ? 'show' : 'hide']();
	},
	_isVertical : function () {
		var BL = zul.layout.Borderlayout;
		return this.getPosition() != BL.WEST &&
				this.getPosition() != BL.EAST;
	},

	// returns the ambit of the specified cmp for region calculation. 
	_ambit: function (ignoreSplit) {
		var ambit, mars = this.getCurrentMargins_(), region = this.getPosition();
		if (region && !this._open) {
			var colled = this.$n('colled');
			ambit = {
				x: mars.left,
				y: mars.top,
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
			ignoreSplit = true;
		} else {
			var pn = this.parent.$n(),
				w = this.getWidth() || '',
				h = this.getHeight() || '',
				pert;
			ambit = {
				x: mars.left,
				y: mars.top,
				w: (pert = w.indexOf('%')) > 0 ?
					Math.max(
						Math.floor(pn.offsetWidth * zk.parseInt(w.substring(0, pert)) / 100),
						0) : this.$n('real').offsetWidth, 
				h: (pert = h.indexOf('%')) > 0 ?
					Math.max(
						Math.floor(pn.offsetHeight * zk.parseInt(h.substring(0, pert)) / 100),
						0) : this.$n('real').offsetHeight
			};
		}
		var split = ignoreSplit ? {offsetHeight:0, offsetWidth:0}: this.$n('split') || {offsetHeight:0, offsetWidth:0};
		if (!ignoreSplit) this._fixSplit();

		this._ambit2(ambit, mars, split);
		return ambit;
	},
	_ambit2: zk.$void,

	_reszSplt: function (ambit) {
		var split = this.$n('split'),
			sbtn = this.$n('splitbtn');
		if (zk(split).isVisible()) {
			if (zk(sbtn).isVisible()) {
				if (this._isVertical()) 
					sbtn.style.marginLeft = jq.px0(((ambit.w - sbtn.offsetWidth) / 2));
				else
					sbtn.style.marginTop = jq.px0(((ambit.h - sbtn.offsetHeight) / 2));
			}
			zk.copy(split.style, this._reszSp2(ambit, {
				w: split.offsetWidth,
				h: split.offsetHeight
			}));
		}
		return ambit;
	},
	_reszSp2: zk.$void
},{
	_aryToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	},
	
	// invokes border layout's renderer before the component slides out
	beforeSlideOut: function (n) {
		var s = this.$n('colled').style;
		s.display = "";
		s.visibility = "hidden";
		s.zIndex = 1;
		this.parent.resize();
	},
	// a callback function after the component slides out.
	afterSlideOut: function (n) {
		if (this._open) 
			zk(this.$n('real')).slideIn(this, {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideIn
			});
		else {
			var colled = this.$n('colled'),
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
		if (this.$n('btn'))
			this.$n('btn').style.display = "";
		jq(document).unbind("click", this.proxy(this._docClick));
		this._isSlideUp = this._isSlide = false;
	},
	//drag
	_ignoredrag: function (dg, pointer, evt) {
			var target = evt.domTarget,
				wgt = dg.control;
			if (!target || target != wgt.$n('split')) return true;
			if (wgt.isSplittable() && wgt._open) {			
				var BL = zul.layout.Borderlayout,
					pos = wgt.getPosition(),
					maxs = wgt.getMaxsize(),
					mins = wgt.getMinsize(),
					ol = wgt.parent,
					real = wgt.$n('real'),
					mars = zul.layout.LayoutRegion._aryToObject(wgt._margins),
					pbw = zk(real).padBorderWidth(),
					lr = pbw + (pos == BL.WEST ? mars.left : mars.right),
					tb = pbw + (pos == BL.NORTH ? mars.top : mars.bottom),
					min = 0,
					uuid = wgt.uuid;
				switch (pos) {
				case BL.NORTH:	
				case BL.SOUTH:
					var r = ol.center || (pos == BL.NORTH ? ol.south : ol.north);
					if (r) {
						if (BL.CENTER == r.getPosition()) {
							var east = ol.east,
								west = ol.west;
							maxs = Math.min(maxs, (real.offsetHeight + r.$n('real').offsetHeight)- min);
						} else {
							maxs = Math.min(maxs, ol.$n().offsetHeight
									- r.$n('real').offsetHeight - r.$n('split').offsetHeight
									- wgt.$n('split').offsetHeight - min); 
						}
					} else {
						maxs = ol.$n().offsetHeight - wgt.$n('split').offsetHeight;
					}
					break;				
				case BL.WEST:
				case BL.EAST:
					var r = ol.center || (pos == BL.WEST ? ol.east : ol.west);
					if (r) {
						if (BL.CENTER == r.getPosition()) {
							maxs = Math.min(maxs, (real.offsetWidth
									+ zk(r.$n('real')).revisedWidth(r.$n('real').offsetWidth))- min);
						} else {
							maxs = Math.min(maxs, ol.$n().offsetWidth
									- r.$n('real').offsetWidth - r.$n('split').offsetWidth - wgt.$n('split').offsetWidth - min); 
						}
					} else {
						maxs = ol.$n().offsetWidth - wgt.$n('split').offsetWidth;
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

		// Bug #1939859
		wgt.$n().style.zIndex = '';
			
		dg._rootoffs = dg._point = null;

		wgt.parent.resize();
		wgt.fire('onSize', zk.copy({
			width: wgt.$n('real').style.width,
			height: wgt.$n('real').style.height
		}, evt.data));
	},
	_snap: function (dg, pointer) {
		var wgt = dg.control,
			x = pointer[0],
			y = pointer[1],
			BL = zul.layout.Borderlayout,
			split = wgt.$n('split'),
			b = dg._rootoffs, w, h;
		switch (wgt.getPosition()) {
		case BL.NORTH:
			if (y > b.maxs + b.top) y = b.maxs + b.top;
			if (y < b.mins + b.top) y = b.mins + b.top;
			w = x;
			h = y - b.top;
			break;				
		case BL.SOUTH:
			if (b.top + b.bottom - y - split.offsetHeight > b.maxs) {
				y = b.top + b.bottom - b.maxs - split.offsetHeight;
				h = b.maxs;
			} else if (b.top + b.bottom - b.mins - split.offsetHeight <= y) {
				y = b.top + b.bottom - b.mins - split.offsetHeight;
				h = b.mins;
			} else h = b.top - y + b.bottom - split.offsetHeight;
			w = x;
			break;
		case BL.WEST:
			if (x > b.maxs + b.left) x = b.maxs + b.left;
			if (x < b.mins + b.left) x = b.mins + b.left;
			w = x - b.left;
			h = y;
			break;		
		case BL.EAST:			
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
