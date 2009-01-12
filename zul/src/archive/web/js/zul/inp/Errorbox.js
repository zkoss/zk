/* Errorbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan 11 21:17:56     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Errorbox = zk.$extends('zul.wgt.Popup', {
	$init: function () {
		this.$supers('$init', arguments);
		this.setWidth("260px");
	},
	show: function (owner, msg) {
		this.parent = owner; //fake
		this.msg = msg;
		this.insertHTML(document.body, "beforeEnd");
		this.open(owner, null, "end_before");
	},
	destroy: function () {
		this.close();
		var n = this.getNode();
		this.unbind_();
		zDom.remove(n);
		this.parent = null;
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		var uuid = this.uuid,
			n = this.getNode();

		this.earrow = zDom.$(uuid + '$a');

		zEvt.listen(n, "click", this.proxy(this._clk, '_pclk'));
		n = this.eclose = zDom.$(uuid + '$c');
		zEvt.listen(n, "click", this.proxy(this._close, '_pclose'));

		var $Errorbox = zul.inp.Errorbox;
		this._drag = new zk.Draggable(this, null, {
			starteffect: zk.$void,
			endeffect: $Errorbox._enddrag,
			change: $Errorbox._change
		});
	},
	unbind_: function () {
		this._drag.destroy();

		var n = this.getNode();
		zEvt.unlisten(n, "click", this._pclk);
		n = this.eclose;
		zEvt.listen(n, "click", this._pclose);

		this.$supers('unbind_', arguments);
		this._drag = this.earrow = this.eclose = null;
	},
	doMouseOver_: function (evt, devt) {
		var el = zEvt.target(devt);
		if (el == this.eclose)
			zDom.addClass(el, 'z-errbox-close-over');
		else
			this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt, devt) {
		var el = zEvt.target(devt);
		if (el == this.eclose)
			zDom.rmClass(el, 'z-errbox-close-over');
		else
			this.$supers('doMouseOut_', arguments);
	},
	_clk: function () {
		this.parent.focus(0);
	},
	_close: function (evt) {
		this.parent._destroyerrbox();
		zEvt.stop(evt); //avoid _clk being called
	},
	open: function () {
		this.$supers('open', arguments);
		this.setTopmost();
		this._fixarrow();
	},
	prologHTML_: function (out) {
		var id = this.uuid;
		out.push('<table width="100%" border="0" cellpadding="0" cellspacing="0"><tr valign="top"><td width="17"><span id="');
		out.push(id);
		out.push('$a" class="z-arrow" title="')
		out.push(zUtl.encodeXML(mesg.GOTO_ERROR_FIELD));
		out.push('"></span></td><td width="3">');
		out.push(zUtl.encodeXML(this.msg, true)); //Bug 1463668: security
		out.push('</td><td width="17"><div id="');
		out.push(id);
		out.push('$c" class="z-close z-errbox-close"></div></td></tr></table>');
	},
	onFloatUp: function (wgt) {
		if (!wgt || wgt == this || wgt == this.parent || !this.isVisible())
			return;

		var top1 = this, top2 = wgt;
		while ((top1 = top1.parent) && !top1.isFloating_())
			;
		for (; top2 && !top2.isFloating_(); top2 = top2.parent)
			;
		if (top1 == top2) { //uncover if sibling
			var n = wgt.getNode();
			if (n) this._uncover(n);
		}
	},
	_uncover: function (el) {
		var elofs = zDom.cmOffset(el),
			node = this.getNode(),
			nodeofs = zDom.cmOffset(node);

		if (zDom.isOverlapped(
		elofs, [el.offsetWidth, el.offsetHeight],
		nodeofs, [node.offsetWidth, node.offsetHeight])) {
			var parent = this.parent.getNode(), y;
			var ptofs = zDom.cmOffset(parent),
				pthgh = parent.offsetHeight,
				ptbtm = ptofs[1] + pthgh;
			y = elofs[1] + el.offsetHeight <=  ptbtm ? ptbtm: ptofs[1] - node.offsetHeight;
				//we compare bottom because default is located below

			var ofs = zDom.toStyleOffset(node, 0, y);
			node.style.top = ofs[1] + "px";
			this._fixarrow();
		}
	},
	_fixarrow: function () {
		var parent = this.parent.getNode(),
			node = this.getNode(),
			arrow = this.earrow,
			ptofs = zDom.revisedOffset(parent),
			nodeofs = zDom.revisedOffset(node);
		var dx = nodeofs[0] - ptofs[0], dy = nodeofs[1] - ptofs[1], dir;
		if (dx >= parent.offsetWidth - 2) {
			dir = dy < -10 ? "ld": dy >= parent.offsetHeight - 2 ? "lu": "l";
		} else if (dx < 0) {
			dir = dy < -10 ? "rd": dy >= parent.offsetHeight - 2 ? "ru": "r";
		} else {
			dir = dy < 0 ? "d": "u";
		}
		arrow.className = 'z-arrow z-arrow-' + dir;
	}
},{
	_enddrag: function (dg) {
		var errbox = dg.control;
		errbox.setTopmost();
		errbox._fixarrow();
	},
	_change: function (dg) {
		var errbox = dg.control,
			stackup = errbox._stackup;
		if (stackup) {
			var el = errbox.getNode();
			stackup.style.top = el.style.top;
			stackup.style.left = el.style.left;
		}
		errbox._fixarrow();
	}
});