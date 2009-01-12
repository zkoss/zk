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
		this.owner = owner;
		this.msg = msg;
		this.uuid = this.owner.uuid + '$erb';
		this.insertHTML(document.body, "beforeEnd");
		this.open(owner, null, "end_before");
	},
	destroy: function () {
		this.close();
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		this.earrow = zDom.$(this.uuid + 'a');
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
	},
	open: function () {
		this.$supers('open', arguments);
		this._fixarrow();
	},
	prologHTML_: function (out) {
		var id = this.uuid;
		out.push('<table width="100%" border="0" cellpadding="0" cellspacing="0"><tr valign="top"><td width="17"><span id="');
		out.push(id);
		out.push('a" class="z-arrow" title="')
		out.push(zUtl.encodeXML(mesg.GOTO_ERROR_FIELD));
		out.push('"></span></td><td width="3">');
		out.push(zUtl.encodeXML(this.msg, true)); //Bug 1463668: security
		out.push('</td><td width="17"><div id="');
		out.push(id);
		out.push('c" class="z-close z-errbox-close"></div></td></tr></table>');
	},
	_fixarrow: function () {
		var owner = this.owner.getNode(),
			node = this.getNode(),
			arrow = this.earrow,
			ownerofs = zDom.revisedOffset(owner),
			nodeofs = zDom.revisedOffset(node);
		var dx = nodeofs[0] - ownerofs[0], dy = nodeofs[1] - ownerofs[1], dir;
		if (dx >= owner.offsetWidth - 2) {
			dir = dy < -10 ? "ld": dy >= owner.offsetHeight - 2 ? "lu": "l";
		} else if (dx < 0) {
			dir = dy < -10 ? "rd": dy >= owner.offsetHeight - 2 ? "ru": "r";
		} else {
			dir = dy < 0 ? "d": "u";
		}
		arrow.className = 'z-arrow z-arrow-' + dir;
	}
});