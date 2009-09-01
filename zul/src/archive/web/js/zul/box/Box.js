/* Box.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 12:10:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.box.Box = zk.$extends(zul.Widget, {
	_mold: 'vertical',
	_align: 'start',
	_pack: 'start',

	$define: {
		align: _zkf = function () {
			this.rerender(); //TODO: a better algoithm
		},
		pack: _zkf,
		spacing: _zkf
	},

	/** Returns if it is a vertical box. */
	isVertical: function () {
		return 'vertical' == this._mold;
	},
	/** Returns the orient. */
	getOrient: function () {
		return this._mold;
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: this.isVertical() ? "z-vbox" : "z-hbox";
	},

	onChildVisible_: function (child, visible) {
		this.$supers('onChildVisible_', arguments);
		if (this.desktop) this._fixChildDomVisible(child, visible);
	},
	replaceChildHTML_: function (child) {
		this.$supers('replaceChildHTML_', arguments);
		this._fixChildDomVisible(child, child._visible);
	},
	_fixChildDomVisible: function (child, visible) {
		var n = this._getChdextr(child);
		if (n) n.style.display = visible ? '': 'none';
		n = child.$n('chdex2');
		if (n) n.style.display = visible ? '': 'none';

		if (this.lastChild == child) {
			n = child.previousSibling;
			if (n) {
				n = n.$n('chdex2');
				if (n) n.style.display = visible ? '': 'none';
			}
		}
	},
	_getChdextr: function (child) {
		return child.$n('chdex') || child.$n();
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before) {
			jq(this._getChdextr(before)).before(this.encloseChildHTML_(child));
		} else {
			var n = this.$n(), tbs = n.tBodies;
			if (!tbs || !tbs.length)
				n.appendChild(document.createElement("TBODY"));
			jq(this.isVertical() ? tbs[0]: tbs[0].rows[0]).append(
				this.encloseChildHTML_(child, true));
		}
		child.bind(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		jq(this._getChdextr(child)).remove();
		jq(child.uuid + '-chdex2', zk).remove();
		if (prevsib && this.lastChild == prevsib) //child is last
			jq(prevsib.uuid + '-chdex2', zk).remove();
	},
	encloseChildHTML_: function (child, prefixSpace, out) {
		var oo = [],
			isCell = child.$instanceof(zul.wgt.Cell);
		if (this.isVertical()) {
			oo.push('<tr id="', child.uuid, '-chdex"',
				this._childOuterAttrs(child), '>');
				
			if (!isCell) 
				oo.push('<td', this._childInnerAttrs(child), '>');
				
			child.redraw(oo);
			
			if (!isCell) oo.push('</td>');
			
			oo.push('</tr>');

		} else {
			if (!isCell) {
				oo.push('<td id="', child.uuid, '-chdex"',
				this._childOuterAttrs(child),
				this._childInnerAttrs(child),
				'>');
			}
			child.redraw(oo);
			if (!isCell)
				oo.push('</td>');
		}
		
		if (child.nextSibling)
			oo.push(this._spacingHTML(child));
		else if (prefixSpace) {
			var pre = child.previousSibling;
			if (pre) oo.unshift(this._spacingHTML(pre));
		}
		
		if (!out) return oo.join('');

		for (var j = 0, len = oo.length; j < len; ++j)
			out.push(oo[j]);
	},
	_spacingHTML: function (child) {
		var oo = [],
			spacing = this._spacing,
			spacing0 = spacing && spacing.startsWith('0')
				&& (spacing.length == 1 || zUtl.isChar(spacing.charAt(1),{digit:1})),
			vert = this.isVertical(),
			spstyle = spacing ? (vert?'height:':'width:') + spacing: 'px';

		oo.push('<t', vert?'r':'d', ' id="', child.uuid,
			'-chdex2" class="', this.getZclass(), '-sep"');

		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none;' + s;
		if (s) oo.push(' style="', s, '"');

		oo.push('>', vert?'<td>':'', zUtl.img0, vert?'</td></tr>':'</td>');
		return oo.join('');
	},
	_childOuterAttrs: function (child) {
		var html = '';
		if (child.$instanceof(zul.box.Splitter))
			html = ' class="' + child.getZclass() + '-outer"';
		else if (this.isVertical()) {
			var v = this.getPack();
			if (v) html = ' valign="' + zul.box.Box._toValign(v) + '"';
		} else
			return ''; //if hoz and not splitter, display handled in _childInnerAttrs

		if (!child.isVisible()) html += ' style="display:none"';
		return html;
	},
	_childInnerAttrs: function (child) {
		var html = '',
			vert = this.isVertical(),
			$Splitter = zul.box.Splitter;
		if (child.$instanceof($Splitter))
			return vert ? ' class="' + child.getZclass() + '-outer-td"': '';
				//spliter's display handled in _childOuterAttrs

		var v = vert ? this.getAlign(): this.getPack();
		if (v) html += ' align="' + zul.box.Box._toHalign(v) + '"';

		var style = '', szes = this._sizes;
		if (szes) {
			for (var j = 0, len = szes.length, c = this.firstChild;
			c && j < len; c = c.nextSibling) {
				if (child == c) {
					style = (vert ? 'height:':'width:') + szes[j];
					break;
				}
				if (!c.$instanceof($Splitter))
					++j;
			}
		}

		if (!vert && !child.isVisible()) style += ';display:none';
		return style ? html + ' style="' + style + '"': html;
	},

	//called by Splitter
	_bindWatch: function () {
		if (!this._watchBound) {
			this._watchBound = true;
			zWatch.listen({onSize: this, onShow: this, onHide: this});
		}
	},
	unbind_: function () {
		if (this._watchBound) {
			this._watchBound = false;
			zWatch.unlisten({onSize: this, onShow: this, onHide: this});
		}

		this.$supers('unbind_', arguments);
	},
	onSize: _zkf = function () {

		var $Splitter = zul.box.Splitter;
		for (var c = this.firstChild;; c = c.nextSibling) {
			if (!c) return; //no splitter
			if (c.$instanceof($Splitter)) //whether the splitter has been dragged
				break;
		}

		var vert = this.isVertical(), node = this.$n();

		//Bug 1916473: with IE, we have make the whole table to fit the table
		//since IE won't fit it even if height 100% is specified
		if (zk.ie) {
			var p = node.parentNode;
			if (p.tagName == "TD") {
				var nm = vert ? "height": "width",
					sz = vert ? p.clientHeight: p.clientWidth;
				if ((node.style[nm] == "100%" || this._box100) && sz) {
					node.style[nm] = sz + "px";
					this._box100 = true;
				}
			}
		}

		//Note: we have to assign width/height fisrt
		//Otherwise, the first time dragging the splitter won't be moved
		//as expected (since style.width/height might be "")

		var nd = vert ? node.rows: node.rows[0].cells,
			total = vert ? zk(node).revisedHeight(node.offsetHeight):
				zk(node).revisedWidth(node.offsetWidth);

		for (var i = nd.length; i--;) {
			var d = nd[i];
			if (zk(d).isVisible())
				if (vert) {
					var diff = d.offsetHeight;
					if(d.id && !d.id.endsWith("-chdex2")) { //TR
						//Bug 1917905: we have to manipulate height of TD in Safari
						if (d.cells.length) {
							var c = d.cells[0];
							c.style.height = zk(c).revisedHeight(i ? diff: total) + "px";
						}
						d.style.height = ""; //just-in-case
					}
					total -= diff;
				} else {
					var diff = d.offsetWidth;
					if(d.id && !d.id.endsWith("-chdex2")) //TD
						d.style.width = zk(d).revisedWidth(i ? diff: total) + "px";
					total -= diff;
				}
		}
	},
	onShow: _zkf,
	onHide: _zkf
},{ //static
	_toValign: function (v) {
		return v ? "start" == v ? "top": "center" == v ? "middle":
			"end" == v ? "bottom": v: null;
	},
	_toHalign: function (v) {
		return v ? "start" == v ? "left": "end" == v ? "right": v: null;
	}
});
