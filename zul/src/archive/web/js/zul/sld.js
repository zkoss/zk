/* sld.js

{{IS_NOTE
	Purpose:
		Slider's JavaScript
	Description:

	History:
		Fri Sep 30 12:15:24     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

zk.load("zul.widget");

//Slider
zk.Slider = zClass.create();

zk.Slider.prototype = {
	initialize: function (comp) {
		this.id = $uuid(comp);
		zkau.setMeta(comp, this);
		this.init(comp);
	},
	cleanup: function ()  {
		if (this.draggable) {
			this.draggable.destroy();
			this.draggable.slider = null;
			this.draggable = null;
		}
		if (this.fnSubmit)
			zk.unlisten(this.form, "submit", this.fnSubmit);
		this.element = this.fnSubmit = null;
	},
	init: function(cmp) {
		this.cleanup();

		this.element = cmp;
		if (!this.element) return; //removed

		this.button = $e(this.id+"!btn");
		this.vert = getZKAttr(cmp, "vert");

		//calc the snap
		var meta = this; //such that snap() could access it

		var snap = function (x, y) {return meta._snap(x,y)};
		this.draggable = new zDraggable(this.button, {
			constraint: this.vert ? "vertical": "horizontal", snap: snap,
			starteffect: zkSld._startDrag, change: zkSld._dragging,
			endeffect: zkSld._endDrag});

		this.form = zk.formOf(this.element);
		if (this.form && !this.fnSubmit) {
			this.fnSubmit = function () {
				meta.onsubmit();
			};
			zk.listen(this.form, "submit", this.fnSubmit);
		}
	},
	/** (x, y) is in the style's coordination (use zk.toStyleOffset to convert).
	 */
	_snap: function (x, y) {
		var ofs = zPos.cumulativeOffset(this.element);
		ofs = zk.toStyleOffset(this.button, ofs[0], ofs[1]);
		if (x <= ofs[0]) {
			x = ofs[0];
		} else {
			var max = ofs[0] + this._width();
			if (x > max) x = max;
		}
		if (y <= ofs[1]) {
			y = ofs[1];
		} else {
			var max = ofs[1] + this._height();
			if (y > max) y = max;
		}
		return [x, y];
	},
	_fixPos: function () {
		if (this.vert) {
			var ht = this._height(),
				x = ht > 0 ? Math.round((this._curpos() * ht)/this._maxpos()): 0,
				ofs = zPos.cumulativeOffset(this.element);
			ofs = zk.toStyleOffset(this.button, ofs[0], ofs[1]);
			ofs = this._snap(0, ofs[1] + x);
			this.button.style.top = ofs[1] + "px";
		} else {
			var wd = this._width(),
				x = wd > 0 ? Math.round((this._curpos() * wd)/this._maxpos()): 0,
				ofs = zPos.cumulativeOffset(this.element);
			ofs = zk.toStyleOffset(this.button, ofs[0], ofs[1]);
			ofs = this._snap(ofs[0] + x, 0);
			this.button.style.left = ofs[0] + "px";
		}
	},
	_startDrag: function () {
		this.button.title = ""; //to avoid annoying effect
		this.slidepos = this._curpos();

		document.body.insertAdjacentHTML("beforeEnd",
			'<div id="zul_slidetip" class="z-slider-pp" style="position:absolute;display:none;z-index:60000;background-color:white;border: 1px outset">'
			+this.slidepos+'</div>');

		this.slidetip =  $e("zul_slidetip");
		if (this.slidetip) {
			this.slidetip.style.display = "block";
			zk.position(this.slidetip, this.element, this.vert? "end_before" : "after-start");
		}
	},
	_dragging: function () {
		var pos = this._realpos();
		if (pos != this.slidepos) {
			this.slidepos = pos;
			if (this.slidetip)
				this.slidetip.innerHTML = getZKAttr(this.element, "slidingtext").replace(/\{0\}/g, pos);
			if (zkau.asap(this.element, "onScrolling"))
				zkau.send({uuid: this.id,
					cmd: "onScrolling", data: [pos], ignorable: true},
					100);
		}
	},
	_endDrag: function () {
		var pos = this._realpos();
		var curpos = this._curpos();
		if (pos != curpos) {
			setZKAttr(this.element, "curpos", pos);
			if (zkau.asap(this.element, "onScroll"))
				zkau.send({uuid: this.id, cmd: "onScroll", data: [pos]});
		}
		this._fixPos();
		this.button.title = pos;
		zk.remove(this.slidetip);
		this.slidetip = null;
	},
	_realpos: function () {
		var btnofs = zPos.cumulativeOffset(this.button),
			refofs = zPos.cumulativeOffset(this.element),
			maxpos = this._maxpos(),
			pos;
		if (this.vert) {
			var ht = this._height();
			pos = ht ? Math.round(((btnofs[1] - refofs[1]) * maxpos) / ht): 0;
		} else {
			var wd = this._width();
			pos = wd ? Math.round(((btnofs[0] - refofs[0]) * maxpos) / wd): 0;
		}
		return pos >= 0 ? pos: 0;
	},
	_curpos: function () {
		return $int(getZKAttr(this.element, "curpos"));
	},
	_maxpos: function () {
		return $int(getZKAttr(this.element, "maxpos"))
	},
	/** Returns the slider's real width. */
	_width: function () {
		//7 = corner img with
		return this.element.clientWidth - this.button.offsetWidth+7;
			//button shall not exceed the right edge
	},
	/** Returns the slider's real height. */
	_height: function () {
		//7 = corner img height
		return this.element.clientHeight - this.button.offsetHeight+7;
			//button shall not exceed the bottom edge
	},
	onsubmit: function () {
		var nm = getZKAttr(this.element, "name");
		if (!nm || !this.form) return;

		var val = this._curpos(),
			el = this.form.elements[nm];
		if (el) el.value = val;
		else zk.newHidden(nm, val, this.form);
	}
};

////
//Slider//
zkSld = {
	down_btn : null,
	init: function (cmp) {
		var meta = zkau.getMeta(cmp),
			vert = getZKAttr(cmp, "vert");

		if (meta) meta.init();
		else new zk.Slider(cmp);

		var uuid = $uuid(cmp),
			btn = $e(uuid + "!btn"),
			inner = $e(uuid + "!inner");
		if (vert) {
			//7 = corner width
			var het = cmp.clientHeight+7;
			inner.style.height = het + "px";
		}
		zk.listen(btn, "mouseover", zkSld.onover);
		zk.listen(btn, "mouseout", zkSld.onout);
		zk.listen(btn, "mousedown", zkSld.ondown);
		zk.listen(btn, "mouseup", zkSld.onup);
	},
	onover: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			btn = $e(cmp.id + "!btn");
			zcls = getZKAttr(cmp, "zcls");
		zk.addClass(btn, zcls + "-btn-over");
	},
	onout: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			btn = $e(cmp.id + "!btn");
		if (btn != zkSld.down_btn) {
			var zcls = getZKAttr(cmp, "zcls");
			zk.rmClass(btn, zcls + "-btn-over");
		}
	},
	ondown: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			btn = $e(cmp.id + "!btn"),
			zcls = getZKAttr(cmp, "zcls");
		zk.addClass(btn, zcls + "-btn-drag");
		zkSld.down_btn = btn;
		zk.listen(document.body, "mouseup", zkSld.onup);
	},
	onup: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		var btn = zkSld.down_btn;
		if (btn) {
			var zcls = getZKAttr($real(btn), "zcls");
			zk.rmClass(btn, zcls + "-btn-drag");
			zk.rmClass(btn, zcls + "-btn-over");
		}
		zkSld.down_btn = null;
		zk.unlisten(document.body, "mouseup", zkSld.onup);
	},
	/** Starts dragging. */
	_startDrag: function (button) {
		var meta = zkSld._metaByBtn(button);
		return meta ? meta._startDrag(): null;
	},
	/** Ends dragging. */
	_endDrag: function (button) {
		var meta = zkSld._metaByBtn(button);
		return meta ? meta._endDrag(button): null;
	},
	/** Dragging. */
	_dragging: function (draggable) {
		var meta = zkSld._metaByBtn(draggable.element);
			//draggable is registered to the button
		return meta ? meta._dragging(): null;
	},
	/** Returns meta by button. */
	_metaByBtn: function (button) {
		var btnid = button.id;
		return zkau.getMeta(btnid.substring(0, btnid.length-4));
	},
	setAttr: function (cmp, nm, val) {
		if ("z.curpos" == nm) {
			setZKAttr(cmp, "curpos", val);
			var meta = zkau.getMeta(cmp);
			if (meta) meta._fixPos(cmp);
			return true;
		} else if ("z.slidingtext" == nm) {
			setZKAttr(cmp, "slidingtext", val);
			return true;
		}
		return false;
	}
};
zkSld.onVisi = zkSld.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp); //cmp or id both OK
	if (meta) meta._fixPos(cmp);
};
