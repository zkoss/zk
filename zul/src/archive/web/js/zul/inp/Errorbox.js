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
		this.setSclass('z-errbox');
	},
	show: function (owner, msg) {
		this.parent = owner; //fake
		this.msg = msg;
		this.insertHTML(document.body, "beforeEnd");
		this.open(owner, null, "end_before", {overflow:true});
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

		var $Errorbox = zul.inp.Errorbox;
		this._drag = new zk.Draggable(this, null, {
			starteffect: zk.$void,
			endeffect: $Errorbox._enddrag,
			ignoredrag: $Errorbox._ignoredrag,
			change: $Errorbox._change
		});
		zWatch.listen('onScroll', this);
	},
	unbind_: function () {
		this._drag.destroy();
		zWatch.unlisten('onScroll', this);

		this.$supers('unbind_', arguments);
		this._drag = null;
	},
	onScroll: function (wgt) {
		if (wgt) { //scroll requires only if inside, say, borderlayout
			this.position(this.parent, null, "end_before", {overflow:true});
			this._fixarrow();
		}
	},
	setDomVisible_: function (node, visible) {
		this.$supers('setDomVisible_', arguments);
		var stackup = this._stackup;
		if (stackup) stackup.style.display = visible ? '': 'none';
	},
	doMouseMove_: function (evt, devt) {
		var el = zEvt.target(devt);
		if (el == this.getSubnode('c')) {
			var y = zEvt.y(devt),
				size = zk.parseInt(zDom.getStyle(el, 'padding-right'))
				offs = zDom.revisedOffset(el);
			if (y >= offs[1] && y < offs[1] + size)	zDom.addClass(el, 'z-errbox-close-over');
			else zDom.rmClass(el, 'z-errbox-close-over');
		} else this.$supers('doMouseMove_', arguments);
	},
	doMouseOut_: function (evt, devt) {
		var el = zEvt.target(devt);
		if (el == this.getSubnode('c'))
			zDom.rmClass(el, 'z-errbox-close-over');
		else
			this.$supers('doMouseOut_', arguments);
	},
	doClick_: function (evt, devt) {
		var el = zEvt.target(devt);
		if (el == this.getSubnode('c') && zDom.hasClass(el, 'z-errbox-close-over'))
			this.parent._destroyerrbox();
		else {
			this.$supers('doClick_', arguments);
			this.parent.focus(0);
		}
	},
	open: function () {
		this.$supers('open', arguments);
		this.setTopmost();
		this._fixarrow();
	},
	prologHTML_: function (out) {
		var id = this.uuid;
		out.push('<div id="', id);
		out.push('$a" class="z-errbox-left z-arrow" title="')
		out.push(zUtl.encodeXML(mesg.GOTO_ERROR_FIELD));
		out.push('"><div id="', id, '$c" class="z-errbox-right z-errbox-close"><div class="z-errbox-center">');
		out.push(zUtl.encodeXML(this.msg, true)); //Bug 1463668: security
		out.push('</div></div></div>');
	},
	onFloatUp: function (wgt) {
		if (wgt == this) {
			this.setTopmost();
			return;
		}
		if (!wgt || wgt == this.parent || !this.isVisible())
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
			arrow = this.getSubnode('a'),
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
		arrow.className = 'z-errbox-left z-arrow-' + dir;
	}
},{
	_enddrag: function (dg) {
		var errbox = dg.control;
		errbox.setTopmost();
		errbox._fixarrow();
	},
	_ignoredrag: function (dg, pointer, evt) {
		return zEvt.target(evt) == dg.control.getSubnode('c') && zDom.hasClass(dg.control.getSubnode('c'), 'z-errbox-close-over');
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