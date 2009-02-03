_z='zul.box';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.box.Box = zk.$extends(zul.Widget, {
	_mold: 'vertical',
	_align: 'start',
	_pack: 'start',

	/** Returns if it is a vertical box. */
	isVertical: function () {
		return 'vertical' == this._mold;
	},
	/** Returns the orient. */
	getOrient: function () {
		return this._mold;
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
			//TODO
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
			//TODO
		}
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
		var n = child.getSubnode('chdex');
		if (n) n.style.display = visible ? '': 'none';
		n = child.getSubnode('chdex2');
		if (n) n.style.display = visible ? '': 'none';

		if (this.lastChild == child) {
			n = child.previousSibling;
			if (n) {
				n = n.getSubnode('chdex2');
				if (n) n.style.display = visible ? '': 'none';
			}
		}
	},

	insertChildHTML_: function (child, before, desktop) {
		if (before) {
			zDom.insertHTMLBefore(before.getSubnode('chdex'), this.encloseChildHTML_(child));
		} else {
			var n = this.getNode();
			if (this.isVertical())
				n = n.tBodies[0];
			else
				n = n.tBodies[0].rows[0];
			zDom.insertHTMLBeforeEnd(n, this.encloseChildHTML_(child, true));
		}
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdex');
		zDom.remove(child.uuid + '$chdex2');
		if (prevsib && this.lastChild == prevsib) //child is last
			zDom.remove(prevsib.uuid + '$chdex2');
	},
	encloseChildHTML_: function (child, prefixSpace, out) {
		var oo = [];
		if (this.isVertical()) {
			oo.push('<tr id="', child.uuid, '$chdex"',
				this._childOuterAttrs(child),
				'><td', this._childInnerAttrs(child),
				'>');
			child.redraw(oo);
			oo.push('</td></tr>');

			if (child.nextSibling)
				oo.push(this._spacingHTML(child));
			else if (prefixSpace) {
				var pre = child.previousSibling;
				if (pre) oo.unshift(this._spacingHTML(pre));
			}
		} else {
			oo.push('<td id="', child.uuid, '$chdex"',
				this._childOuterAttrs(child),
				this._childInnerAttrs(child),
				'>');
			child.redraw(oo);
			oo.push('</td>');

			if (child.nextSibling)
				oo.push(this._spacingHTML(child));
			else if (prefixSpace) {
				var pre = child.previousSibling;
				if (pre) oo.unshift(this._spacingHTML(pre));
			}
		}
		if (!out) return oo.join('');

		for (var j = 0, len = oo.length; j < len; ++j)
			out.push(oo[j]);
	},
	_spacingHTML: function (child) {
		var oo = [],
			spacing = this.spacing,
			spacing0 = spacing && spacing.startsWith('0')
				&& (spacing.length == 1 || zk.isDigit(spacing.charAt(1))),
			vert = this.isVertical(),
			spstyle = spacing ? (vert?'height:':'width:') + spacing: '';

		oo.push('<t', vert?'r':'d', ' id="', child.uuid,
			'$chdex2" class="', this.getZclass(), '-sep"');

		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none' + s;
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
		if (v) html += ' align="' + zul.box.Box._toHalign(v) + '"'

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
			zWatch.listen("onSize", this);
			zWatch.listen("onVisible", this);
			zWatch.listen("onHide", this);
		}
	},
	unbind_: function () {
		if (this._watchBound) {
			this._watchBound = false;
			zWatch.unlisten("onSize", this);
			zWatch.unlisten("onVisible", this);
			zWatch.unlisten("onHide", this);
		}

		this.$supers('unbind_', arguments);
	},
	onSize: _zkf = function () {
		if (!this.isRealVisible()) return;

		var $Splitter = zul.box.Splitter;
		for (var c = this.firstChild;; c = c.nextSibling) {
			if (!c) return; //no splitter
			if (c.$instanceof($Splitter)) //whether the splitter has been dragged
				break;
		}

		var vert = this.isVertical(), node = this.getNode();

		//Bug 1916473: with IE, we have make the whole table to fit the table
		//since IE won't fit it even if height 100% is specified
		if (zk.ie) {
			var p = node.parentNode;
			if (zDom.tag(p) == "TD") {
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
			total = vert ? zDom.revisedHeight(node, node.offsetHeight):
				zDom.revisedWidth(node, node.offsetWidth);

		for (var i = nd.length; --i >= 0;) {
			var d = nd[i];
			if (zDom.isVisible(d))
				if (vert) {
					var diff = d.offsetHeight;
					if(d.id && !d.id.endsWith("$chdex2")) { //TR
						//Bug 1917905: we have to manipulate height of TD in Safari
						if (d.cells.length) {
							var c = d.cells[0];
							c.style.height = zDom.revisedHeight(c, i ? diff: total) + "px";
						}
						d.style.height = ""; //just-in-case
					}
					total -= diff;
				} else {
					var diff = d.offsetWidth;
					if(d.id && !d.id.endsWith("$chdex2")) //TD
						d.style.width = zDom.revisedWidth(d, i ? diff: total) + "px";
					total -= diff;
				}
		}
	},
	onVisible: _zkf,
	onHide: _zkf
},{
	_toValign: function (v) {
		return v ? "start" == v ? "top": "center" == v ? "middle":
			"end" == v ? "bottom": v: null;
	},
	_toHalign: function (v) {
		return v ? "start" == v ? "left": "end" == v ? "right": v: null;
	}
});

(_zkwg=_zkpk.Box).prototype.className='zul.box.Box';_zkmd={};
_zkmd['vertical']=
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</table>');
}
_zkmd['horizontal']=
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '><tr');
	
	var	v = this.getAlign();
	if (v) out.push(' valign="', zul.box.Box._toValign(v), '"');
	out.push('>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</tr></table>');
}
zkmld(_zkwg,_zkmd);
zul.box.Splitter = zk.$extends(zul.Widget, {
	_collapse: "none",
	_open: true,

	/** Returns if it is a vertical box. */
	isVertical: function () {
		var p = this.parent;
		return !p || p.isVertical();
	},
	/** Returns the orient. */
	getOrient: function () {
		var p = this.parent;
		return p ? p.getOrient(): "vertical";
	},

	/** Returns whether it is open.
	 */
	isOpen: function () {
		return this._open;
	},
	/** Sets whther it is open.
	 */
	setOpen: function(open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var node = this.getNode();
			if (!node) return;
			var colps = this.getCollapse();
			if (!colps || "none" == colps) return; //nothing to do

			var nd = this.getSubnode('chdex'),
				tn = zDom.tag(nd),
				vert = this.isVertical(),
				$Splitter = this.$class,
				before = colps == "before",
				sib = before ? $Splitter._prev(nd, tn): $Splitter._next(nd, tn),
				sibwgt = zk.Widget.$(sib),
				fd = vert ? "height": "width", diff;
			if (sib) {
				sibwgt.setDomVisible_(sib, open); //fire onVisible/onHide
				sibwgt.parent._fixChildDomVisible(sibwgt, open);

				diff = zk.parseInt(sib.style[fd]);

				if (!before && sibwgt && !sibwgt.nextSibling) {
					var sp = this.getSubnode('chdex2');
					if (sp) {
						sp.style.display = open ? '': 'none'; //no onVisible/onHide
						diff += zk.parseInt(sp.style[fd]);
					}
				}
			}

			sib = before ? $Splitter._next(nd, tn): $Splitter._prev(nd, tn);
			if (sib) {
				diff = zk.parseInt(sib.style[fd]) + (open ? -diff: diff);
				if (diff < 0) diff = 0;
				sib.style[fd] = diff + "px";
				if (open) zWatch.fireDown('onSize', null, sibwgt);
			}

			node.style.cursor = !open ? "default" : vert ? "s-resize": "e-resize";
			this._fixNSDomClass();

			this._fixbtn();
			this._fixszAll();

			if (!fromServer) this.fire('onOpen', open);
		}
	},
	/** Returns the collapse of this button.
	 */
	getCollapse: function () {
		return this._collapse;
	},
	/** Sets the collapse of this button.
	 */
	setCollapse: function(collapse) {
		if (this._collapse != collapse) {
			this._collapse = collapse;
			if (this.desktop) {
				this._fixbtn();
				this._fixsz();
			}
		}
	},

	//super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls:
			"z-splitter" + (this.isVertical() ? "-ver" : "-hor");
	},
	setZclass: function () {
		this.$supers('setZclass', arguments);
		if (this.desktop)
			this._fixDomClass(true);
	},

	bind_: function () {
		this.$supers('bind_', arguments);

		var box = this.parent;
		if (box) box._bindWatch();

		zWatch.listen("onSize", this);
		zWatch.listen("beforeSize", this);
		zWatch.listen("onVisible", this);

		this._fixDomClass();
			//Bug 1921830: if spiltter is invalidated...

		var node = this.getNode(),
			$Splitter = this.$class;
			vert = this.isVertical();
			btn = this.button = this.getSubnode('btn');
		node.style.cursor = this.isOpen() ?
			vert ? "s-resize": "e-resize": "default";
		btn.style.cursor = "pointer";

		if (zk.ie) {
			zEvt.listen(btn, "mouseover", $Splitter.onover);
			zEvt.listen(btn, "mouseout", $Splitter.onout);
		}
		zEvt.listen(btn, "click", $Splitter.onclick);

		this._fixbtn();

		this._drag = new zk.Draggable(this, node, {
			constraint: this.getOrient(), ignoredrag: $Splitter._ignoresizing,
			ghosting: $Splitter._ghostsizing, overlay: true,
			snap: $Splitter._snap, endeffect: $Splitter._endDrag});

		if (!this.isOpen()) {
			var nd = this.getSubnode('chdex'), tn = zDom.tag(nd),
				colps = this.getCollapse();
			if (!colps || "none" == colps) return; //nothing to do

			var sib = colps == "before" ? $Splitter._prev(nd, tn): $Splitter._next(nd, tn);
			zDom.hide(sib); //no onHide at bind_
			var sibwgt = zk.Widget.$(sib);
			sibwgt.parent._fixChildDomVisible(sibwgt, false);

			this._fixNSDomClass();
		}
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("beforeSize", this);
		zWatch.unlisten("onVisible", this);

		var $Splitter = this.$class,
			btn = this.button;
		if (btn) {
			if (zk.ie) {
				zEvt.unlisten(btn, "mouseover", $Splitter.onover);
				zEvt.unlisten(btn, "mouseout", $Splitter.onout);
			}
			zEvt.unlisten(btn, "click", $Splitter.onclick);
		}

		this._drag.destroy();
		this._drag = null;
		this.$supers('unbind_', arguments);
	},

	/** Fixed DOM class for the enclosing TR/TD tag. */
	_fixDomClass: function (inner) {
		var node = this.getNode(),
			p = node.parentNode;
		if (p) {
			var vert = this.isVertical(),
				zcls = this.getZclass();;
			if (vert) p = p.parentNode; //TR
			if (p && p.id.endsWith("$chdex")) {
				p.className = zcls + "-outer";
				if (vert)
					node.parentNode.className = zcls + "-outer-td";
			}
		}
		if (inner) this._fixbtn();
	},
	_fixNSDomClass: function () {
		var node = this.getNode(),
			zcls = this.getZclass(),
			open = this.isOpen();
		if(open && zDom.hasClass(node, zcls+"-ns"))
			zDom.rmClass(node, zcls+"-ns");
		else if (!open && !zDom.hasClass(node, zcls+"-ns"))
			zDom.addClass(node, zcls+"-ns");
	},
	_fixbtn: function () {
		var btn = this.button,
			colps = this.getCollapse();
		if (!colps || "none" == colps) {
			btn.style.display = "none";
		} else {
			var zcls = this.getZclass(),
				before = colps == "before";
			if (!this.isOpen()) before = !before;

			if (this.isVertical()) {
				zDom.rmClass(btn, zcls + "-btn-" + (before ? "b" : "t"));
				zDom.addClass(btn, zcls + "-btn-" + (before ? "t" : "b"));
			} else {
				zDom.rmClass(btn, zcls + "-btn-" + (before ? "r" : "l"));
				zDom.addClass(btn, zcls + "-btn-" + (before ? "l" : "r"));
			}
			btn.style.display = "";
		}
	},
	_fixsz: _zkf = function () {
		if (!this.isRealVisible()) return;

		var node = this.getNode(), pn = node.parentNode;
		if (pn) {
			var btn = this.button,
				bfcolps = "before" == this.getCollapse();
			if (this.isVertical()) {
				//Note: when the browser resizes, it might adjust splitter's wd/hgh
				//Note: the real wd/hgh might be bigger than 8px (since the width
				//of total content is smaller than pn's width)
				//We 'cheat' by align to top or bottom depending on z.colps
				if (bfcolps) {
					pn.vAlign = "top";
					pn.style.backgroundPosition = "top left";
				} else {
					pn.vAlign = "bottom";
					pn.style.backgroundPosition = "bottom left";
				}

				node.style.width = ""; // clean width
				node.style.width = pn.clientWidth + "px"; //all wd the same
				btn.style.marginLeft = ((node.offsetWidth - btn.offsetWidth) / 2)+"px";
			} else {
				if (bfcolps) {
					pn.align = "left";
					pn.style.backgroundPosition = "top left";
				} else {
					pn.align = "right";
					pn.style.backgroundPosition = "top right";
				}

				node.style.height = ""; // clean height
				node.style.height =
					(zk.safari ? pn.parentNode.clientHeight: pn.clientHeight)+"px";
					//Bug 1916332: TR's clientHeight is correct (not TD's) in Safari
				btn.style.marginTop = ((node.offsetHeight - btn.offsetHeight) / 2)+"px";
			}
		}
	},
	onVisible: _zkf,
	onSize: _zkf,
	beforeSize: function () {
		this.getNode().style[this.isVertical() ? "width": "height"] = "";
	},

	_fixszAll: function () {
		//1. find the topmost box
		var box = this.parent;
		if (box) this.$class._fixKidSplts(box.getNode());
		else this._fixsz();
	}
},{
	onclick: function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + "-btn-visi");
		wgt.setOpen(!wgt.isOpen());
	},

	//drag&drop
	_ignoresizing: function (draggable, pointer, evt) {
		var wgt = draggable.control;
		if (!wgt.isOpen()) return true;

		var run = draggable.run = {},
			node = wgt.getNode();
		run.org = zDom.cmOffset(node);
		var nd = wgt.getSubnode('chdex'),
			tn = zDom.tag(nd),
			$Splitter = zul.box.Splitter;
		run.prev = $Splitter._prev(nd, tn);
		run.next = $Splitter._next(nd, tn);
		run.prevwgt = wgt.previousSibling;
		run.nextwgt = wgt.nextSibling;
		run.z_offset = zDom.cmOffset(node);
		return false;
	},
	_ghostsizing: function (draggable, ofs, evt) {
		var node = draggable.node;
		var html = '<div id="zk_ddghost" style="background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(node)+'px;height:'+zDom.offsetHeight(node)
			+'px;"><img src="'+zAu.comURI('/web/img/spacer.gif')
					+'"/></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return zDom.$("zk_ddghost");
	},
	_endDrag: function (draggable) {
		var wgt = draggable.control,
			node = wgt.getNode(),
			$Splitter = zul.box.Splitter,
			flInfo = $Splitter._fixLayout(wgt),
			run = draggable.run, diff, fd;

		if (wgt.isVertical()) {
			diff = run.z_point[1];
			fd = "height";

			//We adjust height of TD if vert
			if (run.next && run.next.cells.length) run.next = run.next.cells[0];
			if (run.prev && run.prev.cells.length) run.prev = run.prev.cells[0];
		} else {
			diff = run.z_point[0];
			fd = "width";
		}
		if (!diff) return; //nothing to do

		if (run.nextwgt) zWatch.fireDown('beforeSize', null, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('beforeSize', null, run.prevwgt);
		
		if (run.next) {
			var s = zk.parseInt(run.next.style[fd]);
			s -= diff;
			if (s < 0) s = 0;
			run.next.style[fd] = s + "px";
		}
		if (run.prev) {
			var s = zk.parseInt(run.prev.style[fd]);
			s += diff;
			if (s < 0) s = 0;
			run.prev.style[fd] = s + "px";
		}

		if (run.nextwgt) zWatch.fireDown('onSize', null, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('onSize', null, run.prevwgt);

		$Splitter._unfixLayout(flInfo);
			//Stange (not know the cause yet): we have to put it
			//befor _fixszAll and after onSize

		wgt._fixszAll();
			//fix all splitter's size because table might be with %
		draggable.run = null;//free memory
	},
	_snap: function (draggable, pos) {
		var run = draggable.run,
			wgt = draggable.control,
			x = pos[0], y = pos[1];
		if (wgt.isVertical()) {
			if (y <= run.z_offset[1] - run.prev.offsetHeight) {
				y = run.z_offset[1] - run.prev.offsetHeight;
			} else {
				var max = run.z_offset[1] + run.next.offsetHeight - wgt.getNode().offsetHeight;
				if (y > max) y = max;
			}
		} else {
			if (x <= run.z_offset[0] - run.prev.offsetWidth) {
				x = run.z_offset[0] - run.prev.offsetWidth;
			} else {
				var max = run.z_offset[0] + run.next.offsetWidth - wgt.getNode().offsetWidth;
				if (x > max) x = max;
			}
		}
		run.z_point = [x - run.z_offset[0], y - run.z_offset[1]];

		return [x, y];
	},

	_next: function (n, tn) {
		return zDom.nextSibling(zDom.nextSibling(n, tn), tn);
	},
	_prev: function (n, tn) {
		return zDom.previousSibling(zDom.previousSibling(n, tn), tn);
	},

	_fixKidSplts: function (n) {
		if (zDom.isVisible(n)) { //n might not be an element
			var wgt = n.z_wgt, //don't use zk.Widget.$ since we check each node
				$Splitter = zul.box.Splitter;
			if (wgt && wgt.$instanceof($Splitter))
				wgt._fixsz();

			for (n = n.firstChild; n; n = n.nextSibling)
				$Splitter._fixKidSplts(n);
		}
	}
});

if (zk.ie) {
	zul.box.Splitter.onover = function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.addClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
	zul.box.Splitter.onout = function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
}
/** Use fix table layout */
if (zk.opera) { //only opera needs it
	zul.box.Splitter._fixLayout = function (wgt) {
		var box = wgt.parent.getNode();
		if (box.style.tableLayout != "fixed") {
			var fl = [box, box.style.tableLayout];
			box.style.tableLayout = "fixed";
			return fl;
		}
	};
	zul.box.Splitter._unfixLayout = function (fl) {
		if (fl) fl[0].style.tableLayout = fl[1];
	};
} else
	zul.box.Splitter._fixLayout = zul.box.Splitter._unfixLayout = zk.$void;

(_zkwg=_zkpk.Splitter).prototype.className='zul.box.Splitter';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '><span id="',
			this.uuid, '$btn" style="display:none"></span></div>');
}
zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}