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
		this._sync(n);
//		if (!zk.opera) zEffect.slideDown(n, null, {duration:0.4,y:0});
			//if we slide, opera will slide it at the top of screen and position it
			//later. No sure it is a bug of script.aculo.us or Opera

		if (zk.ie6Only) this._stackup = zDom.makeStackup(node);

		this.eimg = zDom.$(id + 'i');
		var $Errbox = zul.inp.Errbox;
		this.drag = new zk.Draggable(this, null, {
			starteffect: zk.$void,
			endeffect: $Errbox._enddrag,
			change: $Errbox._change
		});

		zEvt.listen(n, "click", this.proxy(this._clk, '_pclk'));
		n = this.eclose = zDom.$(id + 'c');
		zEvt.listen(n, "click", this.proxy(this._close, '_pclose'));

		zWatch.listen('onFloatUp', this);
		var f = zk.currentFocus;
		if (f) this.onFloatUp(f); //onFloatUp alreay fired when reaching here
	},
	destroy: function () {
		delete this.widget._lastValVld; //enforce validation again

		zWatch.unlisten('onFloatUp', this);

		var n = this.node;
		zDom.remove(n);
		zEvt.unlisten(n, "click", this._pclk);
		n = this.eclose;
		zEvt.unlisten(n, "click", this._pclose);

		this.drag.destroy();
		this.drag = null;

		var stackup = this.stackup;
		if (stackup) {
			this.stackup = null;
			stackup.destroy();
		}

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
			var wgtofs = zDom.revisedOffset(ewgt),
				boxofs = zDom.revisedOffset(box);
			var dx = boxofs[0] - wgtofs[0], dy = boxofs[1] - wgtofs[1], dir;
			if (dx > ewgt.offsetWidth) {
				dir = dy < -10 ? "LD": dy >= ewgt.offsetHeight ? "LU": "L";
			} else if (dx < 0) {
				dir = dy < -10 ? "RD": dy >= ewgt.offsetHeight ? "RU": "R";
			} else {
				dir = dy < 0 ? "D": "U";
			}
			img.src = zAu.comURI('/web/zul/img/vd/arrow'+dir+(zk.ie6Only?'.gif':'.png'));	
		}
	},
	onFloatUp: function (wgt) {
		if (!zUtl.isAncestor(wgt, this.widget)) {
			var n = wgt.getNode();
			if (n) this._uncover(n);
		}
	},
	_uncover: function (el) {
		var elofs = zDom.cmOffset(el),
			box = this.node,
			boxofs = zDom.cmOffset(box);

		if (zDom.isOverlapped(
		elofs, [el.offsetWidth, el.offsetHeight],
		boxofs, [box.offsetWidth, box.offsetHeight])) {
			var ewgt = this.widget.getNode(), y;
			var wgtofs = zDom.cmOffset(ewgt),
				wgthgh = ewgt.offsetHeight,
				wgtbtm = wgtofs[1] + wgthgh;
			y = elofs[1] + el.offsetHeight <=  wgtbtm ? wgtbtm: wgtofs[1] - box.offsetHeight;
				//we compare bottom because default is located below

			var ofs = zDom.toStyleOffset(box, 0, y);
			box.style.top = ofs[1] + "px";
			this._fiximg();
		}
	}

},{
	_enddrag: function (dg) {
		dg.control._fiximg();
	},
	_change: function (dg) {
		var errbox = dg.control,
			stackup = errbox.stackup;
		if (stackup) {
			var el = errbox.node;
			stackup.style.top = el.style.top;
			stackup.style.left = el.style.left;
		}
		errbox._fiximg();
	}
});