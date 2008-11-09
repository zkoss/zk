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
	_align: 'start',
	_pack: 'start',

	/** Returns if it is a vertical box. */
	isVertical: function () {
		return 'vertical' == this.mold;
	},
	/** Returns the orient. */
	getOrient: function () {
		return this.mold;
	},

	/** Returns the align of this button.
	 */
	getAlign: function () {
		return this._align;
	},
	/** Sets the align of this button.
	 */
	setAlign: function(align) {
		if (this._align != align) {
			this._align = align;
			var n = this.node;
			if (n) {
				//TODO
			}
		}
	},
	/** Returns the pack of this button.
	 */
	getPack: function () {
		return this._pack;
	},
	/** Sets the pack of this button.
	 */
	setPack: function(pack) {
		if (this._pack != pack) {
			this._pack = pack;
			var n = this.node;
			if (n) {
				//TODO
			}
		}
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: this.isVertical() ? "z-vbox" : "z-hbox";
	},

	setChildDomVisible_: function (child, visible) {
		this.$super('setChildDomVisible_', child, visible);
		this._fixChildDomVisible(child, visible);
	},
	replaceChildHTML_: function (child, n, desktop) {
		this.$super('replaceChildHTML_', child, n, desktop);
		this._fixChildDomVisible(child, visible);
	},
	_fixChildDomVisible: function (child, visible) {
		var n = zDom.$(child.uuid + '$chdex');
		if (n) n.style.display = visible ? '': 'none';
		n = zDom.$(child.uuid + '$chdex2');
		if (n) n.style.display = visible ? '': 'none';

		if (this.lastChild == child) {
			n = child.previousSibling;
			if (n) {
				n = zDom.$(n + '$chdex2')
				if (n) n.style.display = visible ? '': 'none';
			}
		}
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
			html = '<td id="' + child.uuid + '$chdex"';
			if (!child.isVisible())
				html += ' style="display:none"';
			html += '>' + childhtml + '</td>';

			if (child.nextSibling)
				html += this._spacingHTML(child);
			else if (last) {
				var pre = child.previousSibling;
				if (pre) html = this._spacingHTML(pre) + html;
			}
		}
		return html;
	},
	_spacingHTML: function (child) {
		var spacing = this.spacing,
			spacing0 = spacing && spacing.startsWith('0')
				&& (spacing.length == 1 || zk.isDigit(spacing.charAt(1))),
			vert = this.isVertical(),
			spstyle = spacing ? (vert?'height:':'width:') + spacing: '';

		html = '<t' + (vert?'r':'d') + ' id="' + child.uuid
			+ '$chdex2" class="' + this.getZclass() + '-sep"';
		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none' + s;
		if (s) html += ' style="' + s + '"';
		return html + '>' + (vert?'<td>':'') + zUtl.img0
			+ (vert?'</td></tr>':'</td>');
	},
	_childOuterAttrs: function (child) {
	},
	_childInnerAttrs: function (child) {
	}
},{
	_toValign: function (v) {
		return v ? "start" == v ? "top": "center" == v ? "middle":
			"end" == v ? "bottom": v: null;
	}
});
