/* Box.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 12:10:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.box.Box = zk.$extends(zul.Widget, {
	mold: 'vertical',

	/** Returns if it is a vertical box. */
	isVertical: function () {
		return 'vertical' == this.mold;
	},

	//super//
	setChildDomVisible_: function (child, visible) {
		this.$super('setChildDomVisible_', child, visible);
		var n = zDom.$(child.uuid + '$chdex');
		if (n) n.style.display = visible ? '': 'none';
		n = zDom.$(child.uuid + '$chdex2');
		if (n) n.style.display = visible ? '': 'none';
	},
	replaceChildHTML_: function (child, n, desktop) {
		this.$super('replaceChildHTML_', child, n, desktop);
		var n = zDom.$(child.uuid + '$chdex');
		if (n) n.style.display = visible ? '': 'none';
		n = zDom.$(child.uuid + '$chdex2');
		if (n) n.style.display = visible ? '': 'none';
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before) {
			zDom.insertHTMLBefore(zDom.$(before.uuid + "$chdex"), this.encloseChildHTML_(child));
		} else {
			var n = this.node;
			if (this.isVertical())
				n = n.tBodies[0];
			else
				n = n.tBodies[0].rows[0];
			zDom.insertHTMLBeforeEnd(n, this.encloseChildHTML_(child, true));
		}
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$super('removeChildHTML_', child, prevsib);
		zDom.remove(child.uuid + '$chdex');
		zDom.remove(child.uuid + '$chdex2');
		if (prevsib && this.lastChild == prevsib) //child is last
			zDom.remove(prevsib.uuid + '$chdex2');
	},
	encloseChildHTML_: function (child, last) {
		var html, childhtml = child.redraw();
		if (this.isVertical()) {
			html = '<tr id="' + child.uuid + '$chdex"';
			if (!child.isVisible())
				html += ' style="display:none"';
			html += '><td>' + childhtml + '</td></tr>';

			if (child.nextSibling)
				html += this._spacingHTML(child);
			else if (last) {
				var pre = child.previousSibling;
				if (pre) html = this._spacingHTML(pre) + html;
			}
		} else {
			//TODO
		}
		return html;
	},
	_spacingHTML: function (child) {
		var spacing = this.spacing,
			spacing0 = spacing && spacing.startsWith('0')
				&& (spacing.length == 1 || zk.isDigit(spacing.charAt(1))),
		spstyle = spacing ? 'height:' + spacing: ''
		html = '<tr id="' + child.uuid + '$chdex2" class="'
			+ this.getZclass() + '-sep"';
		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none' + s;
		if (s) html += ' style="' + s + '"';
		return html + '><td>' + zUtl.img0 + '</td></tr>';
	}
});
