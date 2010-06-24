/* Slider.js

	Purpose:
		
	Description:
		
	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A slider.
 *  <p>Default {@link #getZclass} as follows:
 *  <ol>
 *  	<li>Case 1: If {@link #getOrient()} is vertical, "z-slider-ver" is assumed</li>
 *  	<li>Case 2: If {@link #getOrient()} is horizontal, "z-slider-hor" is assumed</li>
 *  </ol>
 */
zul.inp.Slider = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_curpos: 0,
	_maxpos: 100,
	_pageIncrement: 10,
	_slidingtext: "{0}",
	
	$define: {
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * <p>Default : "horizontal" 
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: function() {
			if (this.isVertical()) {
				this.setWidth("");
				this.setHeight("207px");
			} else {
				this.setWidth("207px");
				this.setHeight("");
			}
			this.rerender();
		},
		/** Returns the current position of the slider.
		 * <p>Default: 0.
		 * @return int
		 */
		/** Sets the current position of the slider.
		 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
		 * {@link #getMaxpos} is assumed.
		 * @param int curpos
		 */
		curpos: function() {
			if (this.desktop)
				this._fixPos();
		},
		/** Returns the maximum position of the slider.
		 * <p>Default: 100.
		 * @return int
		 */
		/** Sets the maximum position of the slider.
		 * @param int maxpos
		 */
		maxpos: function() {
			if (this._curpos > this._maxpos) {
				this._curpos = this._maxpos;
				if (this.desktop) 
					this._fixPos();
			}
		},
		/** Returns the sliding text.
		 * <p>Default : "{0}"
		 * @return String
		 */
		/** Sets the sliding text.
		 * The syntax "{0}" will be replaced with the position at client side.
		 * @param String slidingtext
		 */
		slidingtext: null,
		/** Returns the amount that the value of {@link #getCurpos}
		 * changes by when the tray of the scroll bar is clicked. 
		 * <p>Default: 10.
		 * @return int
		 */
		/** Sets the amount that the value of {@link #getCurpos}
		 * changes by when the tray of the scroll bar is clicked.
		 * @param int pginc
		 */
		pageIncrement: null,
		/** Returns the name of this component.
		 * <p>Default: null.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * @return String
		 */
		/** Sets the name of this component.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 *
		 * @param String name the name of this component.
		 */
		name: function() {
			if (this.efield) 
				this.efield.name = this._name;
		}
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;
		
		var name = "z-slider";
		if (this.inScaleMold()) 
			return name + "-scale";
		else if (this.inSphereMold()) 
			return name + ("horizontal" == this._orient ? "-sphere-hor" : "-sphere-ver");
		else 
			return name + ("horizontal" == this._orient ? "-hor" : "-ver");
	},
	doMouseOver_: function(evt) {
		jq(this.$n("btn")).addClass(this.getZclass() + "-btn-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		jq(this.$n("btn")).removeClass(this.getZclass() + "-btn-over");
		this.$supers('doMouseOut_', arguments);
	},
	onup_: function(evt) {
		var btn = zul.inp.Slider.down_btn;
		if (btn) {
			var widget = zk.Widget.$(btn),
				zcls = widget.getZclass();
			jq(btn).removeClass(zcls + "-btn-drag").removeClass(zcls + "-btn-over");
		}
		
		zul.inp.Slider.down_btn = null;
		jq(document).unbind("mouseup", widget.onup_);
	},
	doMouseDown_: function(evt) {
		var btn = this.$n("btn");
		jq(btn).addClass(this.getZclass() + "-btn-drag");
		jq(document).mouseup(this.onup_);
		zul.inp.Slider.down_btn = btn;
		this.$supers('doMouseDown_', arguments);
	},
	_makeDraggable: function() {
		this._drag = new zk.Draggable(this, this.$n("btn"), {
			constraint: this._orient || "horizontal",
			starteffect: this._startDrag,
			change: this._dragging,
			endeffect: this._endDrag
		});
	},
	_snap: function(x, y) {
		var btn = this.$n("btn"), ofs = zk(this.$n()).cmOffset();
		ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
		if (x <= ofs[0]) {
			x = ofs[0];
		} else {
			var max = ofs[0] + this._getWidth();
			if (x > max) 
				x = max;
		}
		if (y <= ofs[1]) {
			y = ofs[1];
		} else {
			var max = ofs[1] + this._getHeight();
			if (y > max) 
				y = max;
		}
		return [x, y];
	},
	_startDrag: function(dg) {
		var widget = dg.control;
		widget.$n('btn').title = ""; //to avoid annoying effect
		widget.slidepos = widget._curpos;
		
		jq(document.body)
			.append('<div id="zul_slidetip" class="z-slider-pp"'
			+ 'style="position:absolute;display:none;z-index:60000;'
			+ 'background-color:white;border: 1px outset">' + widget.slidepos +
			'</div>');
		
		widget.slidetip = jq("#zul_slidetip")[0];
		if (widget.slidetip) {
			widget.slidetip.style.display = "block";
			zk(widget.slidetip).position(widget.$n(), widget.isVertical() ? "end_before" : "after_start");
		}
	},
	_dragging: function(dg) {
		var widget = dg.control,
			pos = widget._realpos();
		if (pos != widget.slidepos) {
			if (pos > widget._maxpos) 
				pos = widget._maxpos;
			widget.slidepos = pos;
			if (widget.slidetip) 
				widget.slidetip.innerHTML = widget._slidingtext.replace(/\{0\}/g, pos);
			widget.fire("onScrolling", pos);
		}
		widget._fixPos();
	},
	_endDrag: function(dg) {
		var widget = dg.control, pos = widget._realpos();
		
		widget.fire("onScroll", pos);
		
		widget._fixPos();
		jq(widget.slidetip).remove();
		widget.slidetip = null;
	},
	_realpos: function(dg) {
		var btnofs = zk(this.$n("btn")).cmOffset(), refofs = zk(this.getRealNode()).cmOffset(), maxpos = this._maxpos, pos;
		if (this.isVertical()) {
			var ht = this._getHeight();
			pos = ht ? Math.round(((btnofs[1] - refofs[1]) * maxpos) / ht) : 0;
		} else {
			var wd = this._getWidth();
			pos = wd ? Math.round(((btnofs[0] - refofs[0]) * maxpos) / wd) : 0;
		}
		return this._curpos = (pos >= 0 ? pos : 0);
	},
	_getWidth: function() {
		return this.getRealNode().clientWidth - this.$n("btn").offsetWidth + 7;
	},
	_getHeight: function() {
		return this.getRealNode().clientHeight - this.$n("btn").offsetHeight + 7;
	},
	_fixPos: _zkf = function() {
		var btn = this.$n("btn");
		if (this.isVertical()) {
			var ht = this._getHeight(), x = ht > 0 ? Math.round((this._curpos * ht) / this._maxpos) : 0, ofs = zk(this.getRealNode()).cmOffset();
			ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
			ofs = this._snap(0, ofs[1] + x);
			btn.style.top = ofs[1] + "px";
		} else {
			var wd = this._getWidth(), x = wd > 0 ? Math.round((this._curpos * wd) / this._maxpos) : 0, ofs = zk(this.getRealNode()).cmOffset();
			ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
			ofs = this._snap(ofs[0] + x, 0);
			btn.style.left = ofs[0] + "px";
		}
		btn.title = this._curpos;
		this.updateFormData(this._curpos);
	},
	onSize: _zkf,
	onShow: _zkf,
	/** Return whether this widget in scale mold
	 * @return boolean
	 */
	inScaleMold: function() {
		return this.getMold() == "scale";
	},
	/** Return whether this widget in sphere mold
	 * @return boolean
	 */
	inSphereMold: function() {
		return this.getMold() == "sphere";
	},
	/** Returns whether it is a vertical slider.
	 * @return boolean
	 */
	isVertical: function() {
		return "vertical" == this._orient;
	},
	updateFormData: function(val) {
		if (this._name) {
			val = val || 0;
			if (!this.efield) 
				this.efield = jq.newHidden(this._name, val, this.$n());
			else 
				this.efield.value = val;
		}
	},
	getRealNode: function () {
		return this.inScaleMold() ? this.$n("real") : this.$n();
	},
	bind_: function() {
		this.$supers(zul.inp.Slider, 'bind_', arguments);
		var inner = this.$n("inner");
		
		if (this.isVertical()) {
			this.$n("btn").style.top = "0px";
			var het = this.getRealNode().clientHeight;
			if (het > 0) 
				inner.style.height = (het + 7) + "px";
			else 
				inner.style.height = "214px";
		}
		this._makeDraggable();
		
		zWatch.listen({onSize: this, onShow: this});
		this.updateFormData(this._curpos);
	},
	unbind_: function() {
		this.efield = null;
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers(zul.inp.Slider, 'unbind_', arguments);
	}	
});