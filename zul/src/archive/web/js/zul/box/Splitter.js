/* Splitter.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov  9 17:15:35     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
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

			var node = this.node;
			if (!node) return;
			var colps = this.getCollapse();
			if (!colps || "none" == colps) return; //nothing to do

			var nd = zDom.$(node.id + "$chdex"),
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
					var sp = zDom.$(this.uuid + '$chdex2');
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
				if (open) zWatch.fireDown('onSize', -1, sibwgt);
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
			var n = this.node;
			if (n) {
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
	setZclass: function (zcls) {
		this.$super('setZclass', zcls);
		if (this.node)
			this._fixDomClass(true);
	},

	bind_: function (desktop) {
		this.$super('bind_', desktop);

		zWatch.listen("onSize", this);
		zWatch.listen("beforeSize", this);
		zWatch.listen("onVisible", this);

		this._fixDomClass();
			//Bug 1921830: if spiltter is invalidated...

		var node = this.node,
			$Splitter = this.$class;
			vert = this.isVertical();
			btn = this.button = zDom.$(this.uuid + '$btn');
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
			var nd = zDom.$(node.id + "$chdex"), tn = zDom.tag(nd),
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
		this.$super('unbind_');
	},

	/** Fixed DOM class for the enclosing TR/TD tag. */
	_fixDomClass: function (inner) {
		p = this.node.parentNode;
		if (p) {
			var vert = this.isVertical(),
				zcls = this.getZclass();;
			if (vert) p = p.parentNode; //TR
			if (p && p.id.endsWith("$chdex")) {
				p.className = zcls + "-outer";
				if (vert)
					this.node.parentNode.className = zcls + "-outer-td";
			}
		}
		if (inner) this._fixbtn();
	},
	_fixNSDomClass: function () {
		var node = this.node,
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

		var node = this.node, pn = node.parentNode;
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
		this.node.style[this.isVertical() ? "width": "height"] = "";
	},

	_fixszAll: function () {
		//1. find the topmost box
		var box = this.parent;
		if (box) this.$class._fixKidSplts(box.node);
		else this._fixsz();
	},

	isImportantEvent_: function (evtnm) {
		return 'onOpen' == evtnm;
	}

},{
	onclick: function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + "-btn-visi");
		wgt.setOpen(!wgt.isOpen());
	},

	//drag&drop
	_ignoresizing: function (draggable, pointer, evt) {
		var wgt = draggable.widget;
		if (!wgt.isOpen()) return true;

		var run = draggable.run = {},
			node = wgt.node;
		run.org = zDom.cmOffset(node);
		var nd = zDom.$(node.id + "$chdex"),
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
		var wgt = draggable.widget,
			node = wgt.node,
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

		if (run.nextwgt) zWatch.fireDown('beforeSize', -1, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('beforeSize', -1, run.prevwgt);
		
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

		if (run.nextwgt) zWatch.fireDown('onSize', -1, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('onSize', -1, run.prevwgt);

		$Splitter._unfixLayout(flInfo);
			//Stange (not know the cause yet): we have to put it
			//befor _fixszAll and after onSize

		wgt._fixszAll();
			//fix all splitter's size because table might be with %
		draggable.run = null;//free memory
	},
	_snap: function (draggable, pos) {
		var run = draggable.run,
			wgt = draggable.widget,
			x = pos[0], y = pos[1];
		if (wgt.isVertical()) {
			if (y <= run.z_offset[1] - run.prev.offsetHeight) {
				y = run.z_offset[1] - run.prev.offsetHeight;
			} else {
				var max = run.z_offset[1] + run.next.offsetHeight - wgt.node.offsetHeight;
				if (y > max) y = max;
			}
		} else {
			if (x <= run.z_offset[0] - run.prev.offsetWidth) {
				x = run.z_offset[0] - run.prev.offsetWidth;
			} else {
				var max = run.z_offset[0] + run.next.offsetWidth - wgt.node.offsetWidth;
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
		var box = wgt.parent.node;
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
