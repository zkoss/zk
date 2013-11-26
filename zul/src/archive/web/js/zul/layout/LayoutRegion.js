/* LayoutRegion.js

	Purpose:

	Description:

	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _setFirstChildFlex(wgt, flex, ignoreMin) {
		var cwgt = wgt.getFirstChild();
		if (cwgt) {
			if (flex) {
				wgt._fcvflex = cwgt.getVflex();
				wgt._fchflex = cwgt.getHflex();
				if (!ignoreMin || cwgt._vflex != 'min') // B50-ZK-237
					cwgt.setVflex(true);
				if (!ignoreMin || cwgt._hflex != 'min') // B50-ZK-237
					cwgt.setHflex(true);
			} else {
				cwgt.setVflex(wgt._fcvflex);
				cwgt.setHflex(wgt._fchflex);
				delete wgt._fcvflex;
				delete wgt._fchflex;
			}
		}
	}

/**
 * A layout region in a border layout.
 * <p>
 * Events:<br/> onOpen, onSize.<br/>
 */
zul.layout.LayoutRegion = zk.$extends(zul.Widget, {
	_open: true,
	_border: 'normal',
	_maxsize: 2000,
	_minsize: 0,
	_scrollbar: null,

	$init: function () {
		this.$supers('$init', arguments);
		this._margins = [0, 0, 0, 0];
		this._cmargins = [3, 3, 3, 3]; //center
	},

	$define: {
		/**
		 * Sets whether to grow and shrink vertical/horizontal to fit their given
		 * space, so called flexibility.
		 * @param boolean flex
		 */
		/**
		 * Returns whether to grow and shrink vertical/horizontal to fit their given
		 * space, so called flexibility.
		 *
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		flex: function (v) {
			_setFirstChildFlex(this, v);
			this.rerender();
		},
		/**
		 * Sets the border (either none or normal).
		 *
		 * @param String border the border. If null or "0", "none" is assumed.
		 */
		/**
		 * Returns the border.
		 * <p>
		 * The border actually controls what CSS class to use: If border is null, it
		 * implies "none".
		 *
		 * <p>
		 * If you also specify the CSS class ({@link #setSclass}), it overwrites
		 * whatever border you specify here.
		 *
		 * <p>
		 * Default: "normal".
		 * @return String
		 */
		border: function (border) {
			if (!border || '0' == border)
				this._border = border = 'none';

			if (this.desktop)
				(this.$n('real') || {})._lastSize = null;

			this.updateDomClass_();
		},
		/**
		 * Sets the title.
		 * @param String title
		 */
		/**
		 * Returns the title.
		 * <p>Default: null.
		 * @return String
		 */
		title: function () {
			this.rerender();
		},
		/**
		 * Sets whether enable the split functionality.
		 * @param boolean splittable
		 */
		/**
		 * Returns whether enable the split functionality.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		splittable: function (splittable) {
			if (this.parent && this.desktop)
				this.parent.resize();
		},
		/**
		 * Sets the maximum size of the resizing element.
		 * @param int maxsize
		 */
		/**
		 * Returns the maximum size of the resizing element.
		 * <p>
		 * Default: 2000.
		 * @return int
		 */
		maxsize: null,
		/**
		 * Sets the minimum size of the resizing element.
		 * @param int minsize
		 */
		/**
		 * Returns the minimum size of the resizing element.
		 * <p>
		 * Default: 0.
		 * @return int
		 */
		minsize: null,
		/**
		 * Sets whether set the initial display to collapse.
		 *
		 * <p>It only applied when {@link #getTitle()} is not null.
		 * @param boolean collapsible
		 */
		/**
		 * Returns whether set the initial display to collapse.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		collapsible: function (collapsible) {
			var btn = this.$n(this._open ? 'btn' : 'btned');
			if (btn)
				btn.style.display = collapsible ? '' : 'none';
		},
		/**
		 * Sets whether enable overflow scrolling.
		 * @param boolean autoscroll
		 */
		/**
		 * Returns whether enable overflow scrolling.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		autoscroll: function (autoscroll) {
			var cave = this.$n('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.getFirstChild() ?
						this.getFirstChild().$n() : cave;
				if (autoscroll) {
					if (zk.ie <= 8)
						this._nativebar = true;
					if (this._nativebar) {
						bodyEl.style.overflow = 'auto';
						bodyEl.style.position = 'relative';
						this.domListen_(bodyEl, 'onScroll');
					} else {
						zWatch.listen({onSize: this});
					}
				} else {
					if (this._nativebar) {
						bodyEl.style.overflow = 'hidden';
						bodyEl.style.position = '';
						this.domUnlisten_(bodyEl, 'onScroll');
					} else {
						zWatch.unlisten({onSize: this});
					}
				}
			}
		},
		/**
		 * Opens or collapses the splitter. Meaningful only if
		 * {@link #isCollapsible} is not false.
		 * @param boolean open
		 */
		/**
		 * Returns whether it is open (i.e., not collapsed. Meaningful only if
		 * {@link #isCollapsible} is not false.
		 * <p>
		 * Default: true.
		 * @return boolean
		 */
		open: function (open, fromServer, nonAnima) {
			if (!this.$n() || !this.isCollapsible() || !this.parent) {
				return; //nothing changed
			}

			nonAnima = this.parent._animationDisabled || nonAnima;

			var colled = this.$n('colled'),
				real = this.$n('real');
			if (open) {
				// Bug 2994592
				if (fromServer) {

					// Bug 2995770
					if (!zk(this.$n()).isRealVisible()) {
						if (colled) {
							jq(real).show();
							jq(colled).hide();
						}
						return;
					}
					var s = this.$n('real').style;
					s.visibility = 'hidden';
					s.display = '';
					this._syncSize(true);
					s.visibility = '';
					s.display = 'none';
					this._open = true;
				}
				if (colled) {
					if (!nonAnima)
						zk(colled).slideOut(this, {
							anchor: this.sanchor,
							duration: 200,
							afterAnima: fromServer ? this.$class.afterSlideOut :
								this.$class._afterSlideOutX
						});
					else {
						jq(real).show();
						jq(colled).hide();
						zUtl.fireShown(this);
					}
				}
			} else {
				if (colled && !nonAnima)
					zk(real).slideOut(this, {
							anchor: this.sanchor,
							beforeAnima: this.$class.beforeSlideOut,
							afterAnima: fromServer ? this.$class.afterSlideOut :
								this.$class._afterSlideOutX
						});
				else {
					if (colled)
						jq(colled).show();
					jq(real).hide();
				}
			}
			if (nonAnima) this.parent.resize();
			if (!fromServer && nonAnima) // B50-ZK-301: onOpen is fire after animation
				this.fire('onOpen', {open:open});
		}
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for LayoutRegion
		if (v != 'min') v = false;
		this.$super(zul.layout.LayoutRegion, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for LayoutRigion
		if (v != 'min') v = false;
		this.$super(zul.layout.LayoutRegion, 'setHflex', v);
	},
	/**
	 * Returns the collapsed margins, which is a list of numbers separated by comma.
	 *
	 * <p>
	 * Default: "3,3,3,3".
	 * @return String
	 */
	getCmargins: function () {
		return zUtl.intsToString(this._open ? this._margins : this._cmargins);
	},
	/**
	 * Sets the collapsed margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 * @param String cmargins
	 */
	setCmargins: function (cmargins) {
		if (this.getCmargins() != cmargins) {
			this._cmargins = zUtl.stringToInts(cmargins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	/**
	 * Returns the current margins.
	 * @return Dimension
	 */
	getCurrentMargins_: function () {
		return zul.layout.LayoutRegion._aryToObject(this._open ? this._margins : this._cmargins);
	},
	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 * <p>
	 * Default: "0,0,0,0".
	 * @return String
	 */
	getMargins: function () {
		return zUtl.intsToString(this._margins);
	},
	/**
	 * Sets margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 * @param String margins
	 */
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
			var added = 'normal' == this.getBorder() ? '' : this.$s('noborder');
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? 'z-' + this.getPosition() : this._zclass;
	},
	//-- super --//
	getMarginSize_: function (attr) {
		return zk(this.$n('real')).sumStyles(attr == 'h' ? 'tb' : 'lr', jq.margins);
	},
	setWidth: function (width) {
		this._width = width;
		var real = this.$n('real');
		if (real) {
			real.style.width = width ? width : '';
			real._lastSize = null;
			this.parent.resize();
		}
		return this;
	},
	setHeight: function (height) {
		this._height = height;
		var real = this.$n('real');
		if (real) {
			real.style.height = height ? height : '';
			real._lastSize = null;
			this.parent.resize();
		}
		return this;
	},
	setVisible : function(visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			var real = this.$n('real'),
				colled = this.$n('colled');
			if (real) {
				if (this._visible) {
					if (this._open) {
						jq(real).show();
						if (colled)
							jq(colled).hide();
					} else {
						jq(real).hide();
						if (colled)
							jq(colled).show();
					}
				} else {
					jq(real).hide();
					if (colled)
						jq(colled).hide();
				}
				this.parent.resize();
			}
		}
		return this;
	},
	//@Override to apply the calculated value on xxx-real element
	setFlexSize_ : function(sz) {
		var n = this.$n('real'),
			ns = n.style;
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				ns.height = '';
			else if (sz.height == '')
				ns.height = this._height ? this._height : '';
			else {
				var cave = this.$n('cave'),
					cap = this.$n('cap'),
					hgh = cave && this._vflex != 'min' ? (cave.offsetHeight + cave.offsetTop)
						: sz.height - zk(n).marginHeight();
				if (cap) // B50-ZK-236: add header height
					hgh += cap.offsetHeight;
				ns.height = jq.px0(hgh);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width == '')
				n.style.width = this._width ? this._width : '';
			else {
				var wdh = sz.width - zk(n).marginWidth();
				n.style.width = jq.px0(wdh);
			}
		}
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
				zk(real).clearStyles().jq.css(jq.parseStyle(this.domStyle_()));
				if (this.parent)
					this.parent.resize();
			}
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(true);
			jq(this.$n()).addClass(this.$s('nested'));
		}

		// Bug for B36-2841185.zul, resync flex="true"
		if (this.isFlex())
			_setFirstChildFlex(this, true, true);

		// reset
		(this.$n('real') || {})._lastSize = null;
		if (this.parent && this.desktop) {
			// B65-ZK-1076 for tabpanel, should fix in isRealVisible() when zk 7
			if (this.parent.isRealVisible({dom: true}))
				this.parent.resize();
		}
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);

		// check before "if (child.$instanceof(zul.layout.Borderlayout)) {"
		if (this.isFlex())
			_setFirstChildFlex(this, false);

		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(false);
			jq(this.$n()).removeClass(this.$s('nested'));
		}

		// reset
		(this.$n('real') || {})._lastSize = null;
		if (this.parent && this.desktop && !this.childReplacing_) {
			// B65-ZK-1076 for tabpanel, should fix in isRealVisible() when zk 7
			if (this.parent.isRealVisible({dom: true}))
				this.parent.resize();
		}
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.parent)
			this.parent.resize();
		return this;
	},
	bind_: function(){
		this.$supers(zul.layout.LayoutRegion, 'bind_', arguments);
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
					initSensitivity: 0,
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

				if(!this._visible){
					var colled = this.$n('colled'),
						real = this.$n('real');
					jq(real).hide();
					if (colled)
						jq(colled).hide();
				}
			}
		}

		if (this._open && !this.isVisible()) this.$n().style.display = 'none';

		if (this.isAutoscroll()) {
			if (zk.ie <= 8)
				this._nativebar = true;
			if (this._nativebar) {
				var bodyEl = this.isFlex() && this.getFirstChild() ? 
						this.getFirstChild().$n() : this.$n('cave');
				this.domListen_(bodyEl, 'onScroll');
			} else {
				zWatch.listen({onSize: this});
			}
		}

		if (this.isFlex())
			_setFirstChildFlex(this, true, true);
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			if (this._nativebar) {
				var bodyEl = this.isFlex() && this.getFirstChild() ? 
						this.getFirstChild().$n() : this.$n('cave');
				this.domUnlisten_(bodyEl, 'onScroll');
			} else {
				zWatch.unlisten({onSize: this});
			}
		}
		
		this.destroyBar_();
		
		if (this.$n('split')) {
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}

		if (this.isFlex())
			_setFirstChildFlex(this, false);

		this.$supers(zul.layout.LayoutRegion, 'unbind_', arguments);
	},
	onSize: function () {
		var wgt = this;
		setTimeout(function () {
			if (wgt.desktop) {
				if (!wgt._scrollbar && !wgt._nativebar)
					wgt._scrollbar = wgt.initScrollbar_();
				wgt.refreshBar_();
			}
		}, 200);
	},
	initScrollbar_: function () {
		var wgt = this,
			embed = jq(wgt.$n('real')).data('embedscrollbar'),
			cave = wgt.$n('cave');
			scrollbar = new zul.Scrollbar(cave, cave.firstChild, {
				embed: embed,
				onScrollEnd: function() {
					wgt._doScroll();
				}
			});
		return scrollbar;
	},
	refreshBar_: function (showBar, scrollToTop) {
		var bar = this._scrollbar;
		if (bar && this._open) {
			var p = this.$n('cave'),
				fc = this.firstChild,
				fch = fc ? fc.getHeight() : 0,
				c = p.firstChild,
				ph = p.offsetHeight,
				pw = p.offsetWidth;
			
			while (c && c.nodeType == 3)
				c = c.nextSibling;
			if (c) {
				var cs = c.style;
				
				if (!fch || !fch.indexOf('px')) { // only recalculate size if no fixed height
					// force to recalculate size
					cs.height = '';
					if (c.offsetHeight);
					
					cs.height = jq.px(c.scrollHeight >= ph ? c.scrollHeight : ph);
				}
				bar.scroller = c;
				bar.syncSize(showBar);
				if (scrollToTop)
					bar.scrollTo(0, 0);
			}
		}
	},
	destroyBar_: function () {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	},
	doResizeScroll_: function () {
		this.$supers('doResizeScroll_', arguments);
		this.refreshBar_(true);
	},
	doClick_: function (evt) {
		var target = evt.domTarget;
		if (!target.id)
			target = target.parentNode;
		switch (target) {
		case this.$n('btn'):
		case this.$n('btned'):
		case this.$n('splitbtn'):
			if (!this.isCollapsible() || this._isSlide || zk.animating()) 
				return;
			if (this.$n('btned') == target) {
				var s = this.$n('real').style;
				s.visibility = 'hidden';
				s.display = '';
				this._syncSize(true);
				s.visibility = '';
				s.display = 'none';
			}
			this.setOpen(!this._open);
			break;
		case this.$n('colled'):
			if (this._isSlide) 
				return;
			this._isSlide = true;
			var real = this.$n('real'),
				s = real.style;
			s.visibility = 'hidden';
			s.display = '';
			this._syncSize();
			this._original = [s.left, s.top];
			this._alignTo();
			s.zIndex = 100;

			if (this.$n('btn'))
				this.$n('btn').style.display = 'none';
			s.visibility = '';
			s.display = 'none';
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
			var btned = this.$n('btned');
			if (btned == target || btned == target.parentNode) {
				this.$class.afterSlideUp.apply(this, [this.$n('real')]);
				this.setOpen(true, false, true);
				this.$n('real').style.zIndex = ''; //reset
			} else
 				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid)
						|| !zk.animating()) {
					this._isSlideUp = true;
					zk(this.$n('real')).slideUp(this, {
						anchor : this.sanchor,
						afterAnima : this.$class.afterSlideUp
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
			ts = to.style,
			BL = zul.layout.Borderlayout;
		switch (this.getPosition()) {
		case BL.NORTH:
			ts.top = jq.px(from.offsetTop + from.offsetHeight);
			ts.left = jq.px(from.offsetLeft);
			break;
		case BL.SOUTH:
			ts.top = jq.px(from.offsetTop - to.offsetHeight);
			ts.left = jq.px(from.offsetLeft);
			break;
		case BL.WEST:
			ts.left = jq.px(from.offsetLeft + from.offsetWidth);
			ts.top = jq.px(from.offsetTop);
			break;
		case BL.EAST:
			ts.left = jq.px(from.offsetLeft - to.offsetWidth);
			ts.top = jq.px(from.offsetTop);
			break;
		}
	},
	_doScroll: function () {
		zWatch.fireDown('onScroll', this);
	},
	_fixSplit: function () {
		jq(this.$n('split'))[this._splittable ? 'show' : 'hide']();
	},
	_fixFontIcon: function () {
		zk(this).redoCSS(-1, {'fixFontIcon': true, 'selector': '.z-borderlayout-icon'});
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
			if (zk.ie > 8 && (region == 'west' || region == 'east') && !this._width && !this._hflex)
				ambit.w++; // B50-ZK-641: text wrap in IE
		}
		var split = ignoreSplit ? {offsetHeight:0, offsetWidth:0}: this.$n('split') || {offsetHeight:0, offsetWidth:0};
		if (!ignoreSplit) this._fixSplit();

		this._ambit2(ambit, mars, split);
		return ambit;
	},
	_ambit2: zk.$void,
	setBtnPos_: function (ambit, ver) {
		var sbtn = this.$n('splitbtn');
		if (ver)
			sbtn.style.marginLeft = jq.px0(((ambit.w - sbtn.offsetWidth) / 2));
		else
			sbtn.style.marginTop = jq.px0(((ambit.h - sbtn.offsetHeight) / 2));
	},
	_reszSplt: function (ambit) {
		var split = this.$n('split'),
			sbtn = this.$n('splitbtn');
		if (zk(split).isVisible()) {
			if (zk(sbtn).isVisible()) {
				this.setBtnPos_(ambit, this._isVertical());
			}
			zk.copy(split.style, this._reszSp2(ambit, {
				w: split.offsetWidth,
				h: split.offsetHeight
			}));
		}
		return ambit;
	},
	_reszSp2: zk.$void,
	getIconClass_: function (collapsed) {
		var BL = zul.layout.Borderlayout;
		switch(this.getPosition()) {
		case BL.NORTH:
			return collapsed ? 'z-icon-chevron-down' : 'z-icon-chevron-up';
		case BL.SOUTH:
			return collapsed ? 'z-icon-chevron-up' : 'z-icon-chevron-down';
		case BL.WEST:
			return collapsed ? 'z-icon-chevron-right' : 'z-icon-chevron-left';
		case BL.EAST:
			return collapsed ? 'z-icon-chevron-left' : 'z-icon-chevron-right';
		}
		return ''; // no icon
	},
	titleRenderer_: function (out) {
		if (this._title) {
			var uuid = this.uuid,
				pos = this.getPosition(),
				noCenter = pos != zul.layout.Borderlayout.CENTER,
				parent = this.parent;

			out.push('<div id="', uuid, '-cap" class="', this.$s('header'), '">');
			if (noCenter) {
				out.push('<i id="', uuid, '-btn" class="', parent.$s('icon'),
						' ', this.getIconClass_(), '"');
				if (!this._collapsible)
					out.push(' style="display:none;"');
				out.push('></i>');
			}
			out.push(zUtl.encodeXML(this._title), '</div>');
		}
	},
	getFirstChild: function () {
		return this.firstChild;
	}
},{
	_aryToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	},

	// invokes border layout's renderer before the component slides out
	beforeSlideOut: function (n) {
		var s = this.$n('colled').style;
		s.display = '';
		s.visibility = 'hidden';
		s.zIndex = 1;
		this.parent.resize();
	},
	_afterSlideOutX: function (n) {
		// B50-ZK-301: fire onOpen after animation
		this.$class.afterSlideOut.call(this, n, true);
		this._fixFontIcon();
	},
	// a callback function after the component slides out.
	afterSlideOut: function (n, fireOnOpen) {
		if (this._open)
			zk(this.$n('real')).slideIn(this, {
				anchor: this.sanchor,
				afterAnima: fireOnOpen ? this.$class._afterSlideInX : this.$class.afterSlideIn
			});
		else {
			var colled = this.$n('colled'),
				s = colled.style;
			s.zIndex = ''; // reset z-index refered to the beforeSlideOut()
			s.visibility = '';
			zk(colled).slideIn(this, {
				anchor: this.sanchor,
				duration: 200,
				// B50-ZK-301: fire onOpen after animation
				afterAnima: fireOnOpen ? function (n) {this.fire('onOpen', {open: this._open});} : zk.$void
			});
		}
		this._fixFontIcon();
	},
	_afterSlideInX: function (n) {
		// B50-ZK-301: fire onOpen after animation
		this.$class.afterSlideIn.call(this, n);
		this.fire('onOpen', {open: this._open});
		this._fixFontIcon();
	},
	// recalculates the size of the whole border layout after the component sildes in.
	afterSlideIn: function (n) {
		this.parent.resize();
		this._fixFontIcon();
	},
	// a callback function after the collapsed region slides down
	afterSlideDown: function (n) {
		jq(document).click(this.proxy(this._docClick));
		this._fixFontIcon();
	},
	// a callback function after the collapsed region slides up
	afterSlideUp: function (n) {
		var s = n.style;
		s.left = this._original[0];
		s.top = this._original[1];
		n._lastSize = null;// reset size for Borderlayout
		s.zIndex = '';
		if (this.$n('btn'))
			this.$n('btn').style.display = '';
		jq(document).unbind('click', this.proxy(this._docClick));
		this._isSlideUp = this._isSlide = false;
		this._fixFontIcon();
	},
	//drag
	_ignoredrag: function (dg, pointer, evt) {
			var target = evt.domTarget,
				wgt = dg.control,
				split = wgt.$n('split');
			if (!target || (split != target && !jq.contains(split, target))) return true;
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
									+ r.$n('real').offsetWidth)- min);
						} else {
							maxs = Math.min(maxs, ol.$n().offsetWidth
									- r.$n('real').offsetWidth - r.$n('split').offsetWidth
									- wgt.$n('split').offsetWidth - min);
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
					top: ofs[1] + tb,
					left : ofs[0] + lr,
					right : real.offsetWidth,
					bottom: real.offsetHeight
				};
				return false;
			}
		return true;
	},
	_endeffect: function (dg, evt) {
		var wgt = dg.control,
			keys = '';
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
		return jq('#zk_layoutghost')[0];
	}
});

})();