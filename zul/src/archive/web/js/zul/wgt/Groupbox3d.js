/* Groupbox3d.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:43:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox3d = zk.$extends(zul.wgt.Groupbox, {
	legend: false,

	//super//
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var panel = this.epanel;
			if (panel) {
				if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
				else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});

				if (!fromServer) this.fire2('onOpen', open);
			}
		}
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: "z-groupbox";
	},
	bind_: function (desktop) {
		this.$super('bind_', desktop);

		var uuid = this.uuid;
		this.epanel = zDom.$(uuid + '$panel');
		this.ecave = zDom.$(uuid + '$cave');
		this.esdw = zDom.$(uuid + '$sdw');

		this.onSize(); //fix height and shadow
	},
	unbind_: function () {
		this.epanel = e.ecave = e.esdw = null;

		this.$super('unbind_');
	},

	//watch//
	onSize: function () {
		var n = this.ecave;
		if (n) {
			var hgh = this.node.style.height;
			if (hgh && hgh != "auto") {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(zDom.vflexHeight(n.parentNode), true);
					//we use n.parentNode(=this.epanel) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		var sdw = this.esdw;
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(n, "border-bottom-width")) ? "": "none";
			//if no border-bottom, hide the shadow
	},
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", -1, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", -1, this);
	},

	//private//
	_contentAttrs: function () {
		var html = ' class="', s = this._cntSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';
		s = this._cntStyle;
		if (this.caption) s = 'border-top:0;' + (s ? s: '');
		if (s) html += ' style="' + s + '"';
		return html;
	}
});