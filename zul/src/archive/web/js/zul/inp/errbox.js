/* errbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Jan 10 16:46:19     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Errbox = zk.$extends(zk.Object, {
	_uri: '/web/zul/img/vd/arrowU.' + (zk.ie6Only ? 'gif':'png'),
	$init: function (wgt, msg) {
		this.widget = wgt;
		var id = wgt.uuid + '$erb',
			html =
	'<div onmousedown="zul.inp.validating=true" onmouseup="zul.inp.validating=false" id="'
	+id+'" class="z-errbox"><div><table width="250" border="0" cellpadding="0" cellspacing="0"><tr valign="top">'
	+'<td width="17" title="'+mesg.GOTO_ERROR_FIELD
	+'"><img id="'+id+'i" src="'+zAu.comURI(this._uri)+'"/></td><td>'
	+zUtl.encodeXML(msg, true) //Bug 1463668: security
	+'</td><td width="16"><img id="' + id + 'c" src="'+zAu.comURI('/web/zul/img/vd/close-off.gif')
	+'"/>'
	+'</td></tr></table></div></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		var n = this.node = zDom.$(id);
		this.eimg = zDom.$(id + 'i');
		this._sync(n);

		this._drag = new zk.Draggable(this);

		zEvt.listen(n, "click", this.proxy(this._clk, '_pclk'));
		n = this.eclose = zDom.$(id + 'c');
		zEvt.listen(n, "click", this.proxy(this._close, '_pclose'));
	},
	destroy: function () {
		delete this.widget._lastValVld; //enforce validation again

		var n = this.node;
		zDom.remove(n);
		zEvt.unlisten(n, "click", this._pclk);
		n = this.eclose;
		zEvt.unlisten(n, "click", this._pclose);

		this.widget = this.ndoe = this.eclose = this.eimg = null;
	},
	_clk: function () {
		this.widget.focus(0);
	},
	_close: function (evt) {
		this.widget._destroyerrbox();
		zEvt.stop(evt); //avoid _clk being called
	},
	_sync: function (unfocused) {
		var wgt = this.widget,
			ewgt = wgt.getNode(),
			ofs = zDom.revisedOffset(ewgt),
			wd = ewgt.offsetWidth,
			hgh = ewgt.offsetHeight,
			box = this.node, atTop;
		if (!unfocused && zk.currentFocus && zk.currentFocus != wgt) {
			var o2 = zDom.revisedOffset(zk.currentFocus.getNode());
			if (o2[0] < ofs[0] + wd
			&& ofs[0] + wd + 220 < zDom.innerX() + zDom.innerWidth()) //Bug 1731646 (box's width unknown, so use 220)
				ofs[0] += wd + 2;
			else if (o2[1] < ofs[1]
			&& ofs[1] + hgh + 50 < zDom.innerY() + zDom.innerHeight())
				ofs[1] += hgh + 2;
			else atTop = true;
		} else {
			ofs[0] += wd + 2;
		}
		box.style.display = "block"; //we need to calculate the size
		if (atTop) ofs[1] -= box.offsetHeight + 1;
		ofs = zDom.toStyleOffset(box, ofs[0], ofs[1]);
		box.style.left = ofs[0] + "px";
		box.style.top = ofs[1] + "px";

		this._fiximg();
	},
	_fiximg: function () {
		var ewgt = this.widget.getNode(),
			box = this.node,
			img = this.eimg;
		if (ewgt && img) {
			var cmpofs = zDom.revisedOffset(ewgt),
				boxofs = zDom.revisedOffset(box);
			var dx = boxofs[0] - cmpofs[0], dy = boxofs[1] - cmpofs[1], dir;
			if (dx > ewgt.offsetWidth) {
				dir = dy < -10 ? "LD": dy > ewgt.offsetHeight + 10 ? "LU": "L";
			} else if (dx < 0) {
				dir = dy < -10 ? "RD": dy > ewgt.offsetHeight + 10 ? "RU": "R";
			} else {
				dir = dy < 0 ? "D": "U";
			}
			img.src = zAu.comURI('/web/zul/img/vd/arrow'+dir+(zk.ie6Only?'.gif':'.png'));	
		}
	}
});