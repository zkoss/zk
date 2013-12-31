/* Slider.js

	Purpose:
		
	Description:
		
	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _getBtnNewPos(wgt) {
		var btn = wgt.$n('btn');
		
		btn.title = wgt._curpos;
		wgt.updateFormData(wgt._curpos);
		
		var isVertical = wgt.isVertical(),
			ofs = zk(wgt.$n()).cmOffset(),
			totalLen = isVertical ? wgt._getHeight(): wgt._getWidth(),
			x = totalLen > 0 ? Math.round((wgt._curpos * totalLen) / wgt._maxpos) : 0;
			
		ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
		ofs = isVertical ? [0, (ofs[1] + x)]: [(ofs[0] + x), 0];
		ofs = wgt._snap(ofs[0], ofs[1]);
		
		return ofs[(isVertical ? 1: 0)];
	}
	function _getNextPos(wgt, offset) {
		var $btn = jq(wgt.$n('btn')),
			fum = wgt.isVertical()? ['top', 'height']: ['left', 'width'],
			newPosition = {};
		
		newPosition[fum[0]] = jq.px0(offset ? 
			(offset + zk.parseInt($btn.css(fum[0])) - $btn[fum[1]]() / 2):
			_getBtnNewPos(wgt));
		
		return newPosition;
	}
	
/**
 * A slider.
 *  <p>Default {@link #getZclass}: z-slider.
 */
zul.inp.Slider = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_height: '200px',
	_width: '200px',
	_curpos: 0,
	_maxpos: 100,
	_pageIncrement: 10,
	_slidingtext: '{0}',
	_pageIncrement: -1,
	
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
		 *
		 * <p>Default: -1 (means it will scroll to the position the user clicks).
		 */
		/** Sets the amount that the value of {@link #getCurpos}
		 * changes by when the tray of the scroll bar is clicked.
		 * <p>Default: -1 (means it will scroll to the position the user clicks).
		 * @param int pginc the page increment. If negative, slider will scroll
		 * to the position that user clicks.
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
	domClass_: function() {
		var scls = this.$supers('domClass_', arguments),
			isVertical = this.isVertical();
		if (isVertical)
			scls += ' ' + this.$s('vertical');
		else
			scls += ' ' + this.$s('horizontal');
		if (this.inSphereMold())
            scls += ' ' + this.$s('sphere');
		else if (this.inScaleMold() && !isVertical) 
			scls += ' ' + this.$s('scale');
		
		return scls;
	},
	onup_: function(evt) {
		var btn = zul.inp.Slider.down_btn, widget;
		if (btn) {
			widget = zk.Widget.$(btn);
		}
		
		zul.inp.Slider.down_btn = null;
		if (widget)
			jq(document).unbind('zmouseup', widget.onup_);
	},
	doMouseDown_: function(evt) {
		jq(document).bind('zmouseup', this.onup_);
		zul.inp.Slider.down_btn = this.$n('btn');
		this.$supers('doMouseDown_', arguments);
	},
	doClick_: function(evt) {
		var $btn = jq(this.$n('btn')),
			pos = $btn.zk.revisedOffset(),
			wgt = this,
			pageIncrement = this._pageIncrement,
			moveToCursor = pageIncrement < 0,
			isVertical = this.isVertical(),
			height = this._getHeight(),
			width = this._getWidth(),
			offset = isVertical ? evt.pageY - pos[1]: evt.pageX - pos[0];
		
		if (!$btn[0] || $btn.is(':animated')) return;
		
		if (!moveToCursor) {
			this._curpos += offset > 0? pageIncrement: - pageIncrement;
			offset = null; // update by _curpos
		}
		// B65-ZK-1884: Avoid button's animation out of range
		var nextPos = _getNextPos(this, offset);
		if (isVertical && zk.parseInt(nextPos.top) > height)
			nextPos.top = jq.px0(height);
		if (!isVertical && zk.parseInt(nextPos.left) > width)
			nextPos.left = jq.px0(width);
		$btn.animate(nextPos, 'slow', function() {
			pos = moveToCursor ? wgt._realpos(): wgt._curpos;
			if (pos > wgt._maxpos) 
				pos = wgt._maxpos;
			wgt.fire('onScroll', pos);
			if (moveToCursor)
				wgt._fixPos();
		});
		this.$supers('doClick_', arguments);
	},
	_makeDraggable: function() {
		this._drag = new zk.Draggable(this, this.$n('btn'), {
			constraint: this._orient || 'horizontal',
			starteffect: this._startDrag,
			change: this._dragging,
			endeffect: this._endDrag
		});
	},
	_snap: function(x, y) {
		var btn = this.$n('btn'), ofs = zk(this.$n()).cmOffset();
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
		widget.$n('btn').title = ''; //to avoid annoying effect
		widget.slidepos = widget._curpos,
		vert = widget.isVertical();
		
		jq(document.body)
			.append('<div id="zul_slidetip" class="z-slider-popup"'
			+ 'style="position:absolute;display:none;z-index:60000;'
			+ 'background-color:white;border: 1px outset">' + widget.slidepos +
			'</div>');
		
		widget.slidetip = jq('#zul_slidetip')[0];
		if (widget.slidetip) {
			var slideStyle = widget.slidetip.style;
			if (zk.webkit) { //give initial position to avoid browser scrollbar
				slideStyle.top = '0px';
				slideStyle.left = '0px';
			}
			slideStyle.display = 'block';
			zk(widget.slidetip).position(widget.$n(), vert ? 'end_before' : 'after_start');
		}
	},
	_dragging: function(dg) {
		var widget = dg.control,
			pos = widget._realpos();
		if (pos != widget.slidepos) {
			if (pos > widget._maxpos) 
				pos = widget._maxpos;
			widget.slidepos = pos;
			if (widget.slidetip) // B70-ZK-2081: Replace "{0}" with the position.
				widget.slidetip.innerHTML = widget._slidingtext.replace(/\{0\}/g, pos);
			widget.fire('onScrolling', pos);
		}
		widget._fixPos();
	},
	_endDrag: function(dg) {
		var widget = dg.control, pos = widget._realpos();
		
		widget.fire('onScroll', pos);
		
		widget._fixPos();
		jq(widget.slidetip).remove();
		widget.slidetip = null;
	},
	_realpos: function(dg) {
		var btnofs = zk(this.$n("btn")).revisedOffset(),
			refofs = zk(this.$n()).revisedOffset(),
			maxpos = this._maxpos,
			pos;
		if (this.isVertical()) {
			var ht = this._getHeight();
			pos = ht ? Math.round(((btnofs[1] - refofs[1]) * maxpos) / ht) : 0;
		} else {
			var wd = this._getWidth();
			pos = wd ? Math.round(((btnofs[0] - refofs[0]) * maxpos) / wd) : 0;
		}
		return this._curpos = pos > 0 ? pos : 0;
	},
	_getWidth: function() {
		return this.$n().clientWidth - this.$n('btn').offsetWidth;
	},
	_getHeight: function() {
		return this.$n().clientHeight - this.$n('btn').offsetHeight;
	},
	_fixSize: function() {
		var n = this.$n(),
			btn = this.$n('btn'),
			inners = this.$n('inner').style;
		if (this.isVertical()) {
			btn.style.top = '-' + btn.offsetHeight / 2 + 'px';
			var het = n.clientHeight;
			inners.height = jq.px0(het > 0 ? het : this._height - btn.offsetHeight);
		} else { 
			btn.style.left = '-' + btn.offsetWidth / 2 + 'px';
			var wd = n.clientWidth;
			inners.width = jq.px0(wd > 0 ? wd : this._width - btn.offsetWidth);
		}
	},
	_fixPos: function() {
		this.$n('btn').style[this.isVertical()? 'top': 'left'] = jq.px0(_getBtnNewPos(this));
	},
	onSize: function() {
		this._fixSize();
		this._fixPos();
	},
	/** Return whether this widget in scale mold
	 * @return boolean
	 */
	inScaleMold: function() {
		return this.getMold() == 'scale';
	},
	/** Return whether this widget in sphere mold
	 * @return boolean
	 */
	inSphereMold: function() {
		return this.getMold() == 'sphere';
	},
	/** Returns whether it is a vertical slider.
	 * @return boolean
	 */
	isVertical: function() {
		return 'vertical' == this._orient;
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
	bind_: function() {
		this.$supers(zul.inp.Slider, 'bind_', arguments);
		this._fixSize();
		this._makeDraggable();
		
		zWatch.listen({onSize: this});
		this.updateFormData(this._curpos);
		this._fixPos();
	},
	unbind_: function() {
		this.efield = null;
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		zWatch.unlisten({onSize: this});
		this.$supers(zul.inp.Slider, 'unbind_', arguments);
	}
});
})();