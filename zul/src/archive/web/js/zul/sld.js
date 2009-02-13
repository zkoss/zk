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
//Slider
zk.Slider = Class.create();
zk.Slider.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);
		this.init();
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
	init: function() {
		this.cleanup();

		this.element = $e(this.id);
		if (!this.element) return; //removed

		this.button = $e(this.id+"!btn");

		//calc the snap
		var meta = this; //such that snap() could access it
		var snap = function (x, y) {return meta._snap(x, y);};
		this.draggable = new Draggable(this.button, {
			constraint: "horizontal", snap: snap,
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
		var ofs = Position.cumulativeOffset(this.element);
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
			var max = ofs[1] + this.element.clientHeight;
			if (y > max) y = max;
		}
		return [x, y];
	},
	_fixPos: function () {
		var wd = this._width();
		var x = wd > 0 ? Math.round((this._curpos() * wd)/this._maxpos()): 0;
		var ofs = Position.cumulativeOffset(this.element);
		ofs = zk.toStyleOffset(this.button, ofs[0], ofs[1]);
		ofs = this._snap(ofs[0] + x, 0);
		this.button.style.left = ofs[0] + "px";
	},
	_startDrag: function () {
		this.button.title = ""; //to avoid annoying effect
		this.slidepos = this._curpos();

		document.body.insertAdjacentHTML("beforeend",
			'<div id="zul_slidetip" style="position:absolute;display:none;z-index:60000;background-color:white;border: 1px outset">'
			+this.slidepos+'</div>');

		this.slidetip =  $e("zul_slidetip");
		if (this.slidetip) {
			this.slidetip.style.display = "block";
			zk.position(this.slidetip, this.element,"after-start");
		}
	},
	_dragging: function () {
		var pos = this._realpos();
		if (pos != this.slidepos) {
			this.slidepos = pos;
			if (this.slidetip)
				this.slidetip.innerHTML = getZKAttr(this.element, "slidingtext").replace(/\{0\}/g, pos);
			if (zkau.asap(this.element, "onScrolling"))
				zkau.send({uuid: this.element.id, 
					cmd: "onScrolling", data: [pos], ignorable: true},
					100);
		}
	},
	_endDrag: function () {
		var pos = this._realpos();
		var curpos = this._curpos();
		if (pos != curpos) {
			setZKAttr(this.element, "curpos", pos);
			zkau.sendasap({uuid: this.element.id, cmd: "onScroll", data: [pos]});
		}
		this._fixPos();
		this.button.title = pos;
		zk.remove(this.slidetip);
		this.slidetip = null;
	},
	_realpos: function () {
		var btnofs = Position.cumulativeOffset(this.button);
		var refofs = Position.cumulativeOffset(this.element);
		var maxpos = this._maxpos();
		var wd = this._width();
		var pos = wd ? Math.round(((btnofs[0] - refofs[0]) * maxpos) / wd): 0;
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
		return this.element.clientWidth - this.button.offsetWidth;
			//button shall not exceed the right edge
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
zkSld = {};
zkSld.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else new zk.Slider(cmp);
};

/** Starts dragging. */
zkSld._startDrag = function (button) {
	var meta = zkSld._metaByBtn(button);
	return meta ? meta._startDrag(): null;
};
/** Ends dragging. */
zkSld._endDrag = function (button) {
	var meta = zkSld._metaByBtn(button);
	return meta ? meta._endDrag(): null;
};
/** Dragging. */
zkSld._dragging = function (draggable) {
	var meta = zkSld._metaByBtn(draggable.element);
		//draggable is registered to the button
	return meta ? meta._dragging(): null;
};
/** Returns meta by button. */
zkSld._metaByBtn = function (button) {
	var btnid = button.id;
	return zkau.getMeta(btnid.substring(0, btnid.length-4));
};
zkSld.setAttr = function (cmp, nm, val) {
	if ("z.curpos" == nm) {
		setZKAttr(cmp, "curpos", val);
		var meta = zkau.getMeta(cmp);
		if (meta) meta._fixPos();
		return true;
	} else if ("z.slidingtext" == nm) {
		setZKAttr(cmp, "slidingtext", val);
		return true;
	}
	return false;
};
zkSld.onVisi = zkSld.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp); //cmp or id both OK
	if (meta) meta._fixPos();
};
