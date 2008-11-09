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
	setOpen: function(open) {
		if (this._open != open) {
			this._open = open;
			var n = this.node;
			if (n) {
				//TODO
			}
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
	bind_: function (desktop) {
		//watch before bind_, so the parent's onXxx will be called first
		zWatch.watch("onSize", this);
		zWatch.watch("beforeSize", this);
		zWatch.watch("onVisible", this);

		this.$super('bind_', desktop);

		var btn = this.button = zDom.$(this.uuid + '$btn');
		this.node.style.cursor = this.isOpen() ?
			this.isVertical() ? "s-resize": "e-resize": "default";
		btn.style.cursor = "pointer";

		if (zk.ie) {
			zEvt.listen(btn, "mouseover", zul.box.Splitter.onover);
			zEvt.listen(btn, "mouseout", zul.box.Splitter.onout);
		}
		zEvt.listen(btn, "click", zul.box.Splitter.onclick);

		this._fixbtn();
	},
	unbind_: function () {
		zWatch.unwatch("onSize", this);
		zWatch.unwatch("beforeSize", this);
		zWatch.unwatch("onVisible", this);

		this.$super('unbind_');
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
	}

},{
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zEvt.widget(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + "-btn-visi");
		wgt.setOpen(!wgt.isOpen());
	}
});

if (zk.ie) {
	zul.box.Splitter.onover = function (evt) {
		if (!evt) evt = window.event;
		var wgt = zEvt.widget(evt);
		zDom.addClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
	zul.box.Splitter.onout = function (evt) {
		if (!evt) evt = window.event;
		var wgt = zEvt.widget(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
}
