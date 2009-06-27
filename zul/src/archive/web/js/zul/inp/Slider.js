/* Slider.js

	Purpose:
		
	Description:
		
	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Slider = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_curpos: 0,
	_maxpos: 100,
	_pageIncrement: 10,
	_slidingtext: "{0}",

	$define: {
		orient: function() {
			if ("vertical" == this._orient) {
				this.setWidth("");
				this.setHeight("207px");
			} else {
				this.setWidth("207px");
				this.setHeight("");
			}
			this.rerender();
		},
		curpos: _zkf = function(){
			if(this.desktop){
				this._fixPos();
				this.inp.value = this._curpos;
			}
		},
		maxpos: function(){
			if(this._curpos > this._maxpos){
				this.inp.value = this._curpos = this._maxpos;
				if(this.desktop)
					this._fixPos();
			}
		},
		slidingtext: _zkf = function(){
		},
		pageIncrement: _zkf,
		name: function(){
			if(this.desktop)
				this.inp.name = this._name;
		}
		
	},
	getZclass: function () {
		var name = "z-slider";
		if(this._zclass != null){
			return "z-slider";
		}else{
			if (this.inScaleMold())
				return name + "-scale";
			else if (this.inSphereMold())
				return name + ("horizontal" == this._orient ? "-sphere-hor" : "-sphere-ver");
			else
				return name + ("horizontal" == this._orient ? "-hor" : "-ver");
		}
	},
	$init: function (){
			this.$supers('$init', arguments);
			this.inner = this.getSubnode("inner");
			this.button = this.getSubnode("btn");
	},
	doMouseOver_: function (evt) {
		this.$supers('doMouseOver_', arguments);
		jq(this.btn).addClass(this.getZclass() + "-btn-over");
	},
	doMouseOut_: function (evt) {
		this.$supers('doMouseOut_', arguments);
		jq(this.btn).removeClass(this.getZclass() + "-btn-over");
	},
	onup_: function(evt){
		var btn = zul.sld.Slider.down_btn;
		var uuid = btn.id.split("$")[0];
		var widget = zk.Widget.$(uuid);
		var zcls = widget.getZclass();
		if (btn)
			jq(btn).removeClass(zcls + "-btn-drag")
				.removeClass(zcls + "-btn-over");

		zul.sld.Slider.down_btn=null;
		jq(document.body).unbind("mouseup", widget.onup_);
		
	},
	doMouseDown_: function(evt) {
		this.$supers('doMouseDown_', arguments);
		jq(this.btn).addClass(this.getZclass() + "-btn-drag");
		jq(document.body).mouseup(this.onup_);
		zul.sld.Slider.down_btn = this.btn;
	},
	_makeDraggable: function(){
		this._drag = new zk.Draggable(this, this.btn, {
			constraint: this._orient == "vertical" ? "vertical": "horizontal", 
			starteffect: this._startDrag, 
			change: this._dragging,
			endeffect: this._endDrag});
	},
	_snap: function (dg, x, y) {
		if((typeof dg ) == "number"){
			var	widget = this,
			y=x,
			x=dg;
		}else{
			var	widget = dg.control;
		}
		var btn = widget.getSubnode("btn");
		var ofs = zk(widget.getNode()).cmOffset();
		ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
		if (x <= ofs[0]) {
			x = ofs[0];
		} else {
			var max = ofs[0] + widget._getWidth();
			if (x > max) x = max;
		}
		if (y <= ofs[1]) {
			y = ofs[1];
		} else {
			var max = ofs[1] + widget._getHeight();
			if (y > max) y = max;
		}
		return [x, y];
	},
	_startDrag: function (dg) {
		var	widget=dg.control;
		widget.btn.title = ""; //to avoid annoying effect
		widget.slidepos = widget._curpos;

		jq(document.body).append(
			'<div id="zul_slidetip" class="z-slider-pp" style="position:absolute;display:none;z-index:60000;background-color:white;border: 1px outset">'
			+ widget.slidepos+'</div>');

		widget.slidetip =  document.getElementById("zul_slidetip");
		if (widget.slidetip) {
			widget.slidetip.style.display = "block";
			zk(widget.slidetip).position(widget.getNode(), widget.isVertical()? "end_before" : "after_start");
		}
	},
	_dragging: function (dg) {
		var	widget=dg.control;
		var pos = widget._realpos();
		if (pos != widget.slidepos) {
			if(pos > widget._maxpos)
				pos = widget._maxpos;
			widget.slidepos = pos;
			if (widget.slidetip)
				widget.slidetip.innerHTML = widget._slidingtext.replace(/\{0\}/g, pos);
			widget.fire("onScrolling", pos);
		}
		widget._fixPos();
	},
	_endDrag: function (dg) {
		var	widget = dg.control;
		var pos = widget._realpos();
		
		widget.fire("onScroll", pos);
		
		widget._fixPos();
		widget.btn.title = pos;
		widget.inp.value = pos;
		jq(widget.slidetip).remove();
		widget.slidetip = null;
	},
	_realpos: function (dg) {
		var	btnofs = zk(this.btn).cmOffset(),
			refofs = zk(this.node).cmOffset(),
			maxpos = this._maxpos,
			pos;
		if (this.isVertical()) {
			var ht = this._getHeight();
			pos = ht ? Math.round(((btnofs[1] - refofs[1]) * maxpos) / ht): 0;
		} else {
			var wd = this._getWidth();
			pos = wd ? Math.round(((btnofs[0] - refofs[0]) * maxpos) / wd): 0;
		}
		return this._curpos = (pos >= 0 ? pos: 0);
	},
	_getWidth: function(){
		return this.node.clientWidth - this.btn.offsetWidth+7;
	},
	_getHeight: function(){
		return this.node.clientHeight - this.btn.offsetHeight+7;
	},
	_fixPos: _zkf = function () {
		if (this.isVertical()) {
			var ht = this._getHeight(),
				x = ht > 0 ? Math.round((this._curpos * ht)/this._maxpos): 0,
				ofs = zk(this.node).cmOffset();
			ofs = zk(this.btn).toStyleOffset(ofs[0], ofs[1]);
			ofs = this._snap(0, ofs[1] + x);
			this.btn.style.top = ofs[1] + "px";
		} else {
			var wd = this._getWidth(),
				x = wd > 0 ? Math.round((this._curpos * wd)/this._maxpos): 0,
				ofs = zk(this.node).cmOffset();
			ofs = zk(this.btn).toStyleOffset(ofs[0], ofs[1]);
			ofs = this._snap(ofs[0] + x, 0);
			this.btn.style.left = ofs[0] + "px";
		}
	},
	onSize: _zkf,
	onVisi: _zkf,
	inScaleMold: function () {
		if(this.getMold() == "scale")
			return true;
		return false;
	},
	inSphereMold: function(){
		if(this.getMold() == "sphere")
			return true;
		return false;
	},
	isVertical: function(){
		return "vertical" == this._orient;
	},
	bind_: function () {
		this.$supers('bind_', arguments); 
		this.inner = this.getSubnode("inner");
		this.btn = this.getSubnode("btn");
		this.inp = this.getSubnode("inp");
		if(this._name)
			this.inp.name = this._name;
		if(this.inScaleMold()){
			this.node = this.getSubnode("real");
		}else
			this.node = this.getNode();
		
		if (this._orient == "vertical") {
			this.btn.style.top="0px";
			var het = this.node.clientHeight;
			if(het>0)
				this.inner.style.height = (het + 7) + "px";
			else
				this.inner.style.height = "214px";
		}
		this._makeDraggable();
		
		zWatch.listen({onSize: this, onShow: this});
		
	},
	unbind_: function (){
		this.inp = this.inner = this.btn = this.node  = null;
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments); 
	}

});