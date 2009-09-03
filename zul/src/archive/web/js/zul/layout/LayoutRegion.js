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

	$init: function () {
		this.$supers('$init', arguments);
		this._margins = [0, 0, 0, 0];
	},

	$define: {
		flex: function () {
			this.rerender();
		},
		border: function (border) {
			if (!border || '0' == border)
				this._border = border = "none";
			this.updateDomClass_();
		},
		autoscroll: null
	},

	getCurrentMargins_: function () {
		return zul.layout.LayoutRegion._aryToObject(this._margins);
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
		if (real) real.style.width = width ? width: '';
		return this;
	},
	setHeight: function (height) {
		this._height = height;
		var real = this.$n('real');
		if (real) real.style.height = height ? height: '';
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
	//@Override
	setFlexSize_: function(sz) {
		var n = this.$n('real');
		if (sz.height !== undefined) {
			if (sz.height !== zk.Widget.CLEAR_FLEX) {
				var cave = this.$n('cave'),
					hgh = cave ? (cave.offsetHeight + cave.offsetTop) : zk(n).revisedHeight(sz.height, true);   
				n.style.height = jq.px(hgh);
			} else
				n.style.height = this._height ? this._height : '';
		}
		if (sz.width !== undefined) {
			if (sz.width !== zk.Widget.CLEAR_FLEX)
				n.style.width = jq.px(zk(n).revisedWidth(sz.width, true));
			else
				n.style.width = this._width ? this._width : '';
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
				var vert = this._isVertical();
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
		this.$supers('unbind_', arguments);
	},
	_doScroll: function () {
		zWatch.fireDown('onScroll', null, this);
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
			LR = zul.layout.LayoutRegion;
		if (zk(split).isVisible())
			zk.copy(split.style,
				this._reszSp2(ambit, {w: split.offsetWidth, h: split.offsetHeight}));
		return ambit;
	},
	_reszSp2: zk.$void
},{
	_aryToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	}
});
