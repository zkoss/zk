/* wnd2.js

{{IS_NOTE
	Purpose:
		
	Description:
		New trendy mold for Window component
	History:
		Thu May 15 15:33:58 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.zul"); //zul and msgzul

////
// window //
/**
 * A new trendy mold for Window component
 * @since 3.5.0
 */
zkWnd2 = {
	ztype: "Wnd2",
	_szs: {}, //Map(id, zDraggable)
	_clean2: {}, //Map(id, mode): to be cleanup the modal effect
	_modal2: {}, //Map(id, todo): to do 2nd phase modaling (disable)
	// tool button event
	onCloseMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2");
		zk.addClass(btn, getZKAttr(cmp, "zcls") + "-close-over");
	},
	onCloseMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2");
		zk.rmClass(btn, getZKAttr(cmp, "zcls") + "-close-over");
	},
	onCloseClick: function (evt) {
		if (!evt) evt = window.event;
		zkau.sendOnClose($parentByType(Event.element(evt), "Wnd2"), true);
		Event.stop(evt);
	},
	onMinimizeMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2");
		zk.addClass(btn, getZKAttr(cmp, "zcls") + "-minimize-over");
	},
	onMinimizeMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2");
		zk.rmClass(btn, getZKAttr(cmp, "zcls") + "-minimize-over");
	},
	onMaximizeMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2"), 
			cls = getZKAttr(cmp, "zcls");
		if (getZKAttr(cmp, "maximized") == "true")
			zk.addClass(btn, cls + "-maximized-over");
		zk.addClass(btn, cls + "-maximize-over");
	},
	onMaximizeMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Wnd2"), 
			cls = getZKAttr(cmp, "zcls");
		if (getZKAttr(cmp, "maximized") == "true")
			zk.rmClass(btn, cls + "-maximized-over");
		zk.rmClass(btn, cls + "-maximize-over");
	}
};
zkWnd2.init = function (cmp) {
	zkWnd2._initBtn(cmp);

	zk.listen(cmp, "mousemove", function (evt) {if(window.zkWnd2) zkWnd2.onmouseover(evt, cmp);});
		//FF: at the moment of browsing to other URL, listen is still attached but
		//our js are unloaded. It causes JavaScript error though harmlessly
		//This is a dirty fix (since onclick and others still fail but hardly happen)
	zkWnd2.setSizable(cmp, zkWnd2.sizable(cmp));	
	
	//Bug #1840866
	zkWnd2._initMode(cmp);
		// But, for a Sun's bug, we need to invoke initMode directly to prevent 
		// that the outline of page is gone.
		// Note: we fixed bug #1830668 bug by using addInitLater to invoke
		// _initMode later, but, with ZK 3.0.4, the problem is already resolved
		// without using addInitLater

	if (getZKAttr(cmp, "maximizable") == "true" && getZKAttr(cmp, "maximized") == "true")
		zk.addInitLater(function () {zkWnd2.maximize(cmp, true, true);});		
};
zkWnd2._initBtn = function (cmp) {
	if (getZKAttr(cmp, "closable") == "true") {
		var close = $e(cmp, "close");
		if (close) {
			zk.listen(close, "click", this.onCloseClick);
			zk.listen(close, "mouseover", this.onCloseMouseover);
			zk.listen(close, "mouseout", this.onCloseMouseout);
			if (!close.style.cursor) close.style.cursor = "default";
		}
	}
	if (getZKAttr(cmp, "maximizable") == "true") {
		var max = $e(cmp, "maximize");
		if (max) {
			zk.listen(max, "click", zkWnd2.onMaximize);
			zk.listen(max, "mouseover", this.onMaximizeMouseover);
			zk.listen(max, "mouseout", this.onMaximizeMouseout);
			if (!max.style.cursor) max.style.cursor = "default";
		}
	}
	if (getZKAttr(cmp, "minimizable") == "true") {
		var min = $e(cmp, "minimize");
		if (min) {
			zk.listen(min, "click", zkWnd2.onMinimize);
			zk.listen(min, "mouseover", this.onMinimizeMouseover);
			zk.listen(min, "mouseout", this.onMinimizeMouseout);
			if (!min.style.cursor) min.style.cursor = "default";
		}
	}
};
zkWnd2.onMaximize = function (evt) {
	if (!evt) evt = window.event;
	var cmp = $parentByType(Event.element(evt), "Wnd2");
	if (cmp) {
		if (getZKAttr(cmp, "maximizable") != "true")
			return;

		zkWnd2.maximize(cmp, getZKAttr(cmp, "maximized") != "true", false, true);
	}
};
zkWnd2.maximize = function (cmp, maximized, silent) {
	var cmp = $e(cmp);
	if (cmp) {
		var isRealVisible = zk.isRealVisible(cmp);
		if (!isRealVisible && maximized) return;
		
		var l, t, w, h, s = cmp.style, cls = getZKAttr(cmp, "zcls");
		if (maximized) {
			zk.addClass($e(cmp, "maximize"), cls + "-maximized");
			zkWnd2.hideShadow(cmp);
			var floated = !zkWnd2._embedded(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode;
			l = s.left;
			t = s.top;
			w = s.width;
			h = s.height;
			
			// prevent the scroll bar.
			s.top = "-10000px";
			s.left = "-10000px";
			
			// Sometimes, the clientWidth/Height in IE6 is wrong. 
			var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.getBorderWidth(op)) : op.clientWidth;
			var sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.getBorderHeight(op)) : op.clientHeight;
			if (!floated) {
				sw -= zk.getPaddingWidth(op);
				sw = zk.revisedSize(cmp, sw);
				sh -= zk.getPaddingHeight(op);
				sh = zk.revisedSize(cmp, sh, true);
			}
			if (sw < 0) sw = 0;
			if (sh < 0) sh = 0;
			
			s.width = sw + "px";
			s.height = sh + "px";
			cmp._lastSize = {l:l, t:t, w:w, h:h};
			
			// restore.
			s.top = "0px";
			s.left = "0px";
		} else {
			var max = $e(cmp, "maximize");
			zk.rmClass(max, cls + "-maximized");
			zk.rmClass(max, cls + "-maximized-over");
			if (cmp._lastSize) {
				s.left = cmp._lastSize.l;
				s.top = cmp._lastSize.t;
				s.width = cmp._lastSize.w;
				s.height = cmp._lastSize.h;
				cmp._lastSize = null;
			}
			l = s.left;
			t = s.top;
			w = s.width;
			h = s.height;
			var body = $e(cmp, "cave");
			if (body)
				body.style.width = body.style.height = "";
		}

		setZKAttr(cmp, "maximized", maximized ? "true" : "false");
		if (!silent)
			zkau.sendasap({uuid: cmp.id, cmd: "onMaximize", data: [l, t, w, h, maximized]});
		
		if (isRealVisible) {
			cmp._maximized = true;
			zk.beforeSizeAt(cmp);
			zk.onSizeAt(cmp);
		}
	}
};
zkWnd2.onMinimize = function (evt) {
	if (!evt) evt = window.event;

	var cmp = $parentByType(Event.element(evt), "Wnd2");
	if (cmp) {
		if (getZKAttr(cmp, "minimizable") != "true")
			return;

		zkWnd2.minimize(cmp, getZKAttr(cmp, "minimized") != "true", false, true);
	}
};
zkWnd2.minimize = function (cmp, minimized, silent) {
	var cmp = $e(cmp);
	if (cmp) {
		var s = cmp.style, l = s.left, t = s.top, w = s.width, h = s.height;
		if (minimized) {
			zkWnd2.hideShadow(cmp);
			zk.hide(cmp);
		} else {
			zkWnd2._show(cmp);
			zk.onSizeAt(cmp);
		}

		setZKAttr(cmp, "minimized", minimized ? "true" : "false");
		if (!silent)
			zkau.sendasap({uuid: cmp.id, cmd: "onMinimize", data: [l, t, w, h, minimized]});
	}
};
zkWnd2.cleanup = function (cmp) {
	if (cmp._lastSize) {
		cmp._lastSize = null;
	}
	zkWnd2.setSizable(cmp, false);
	zkWnd2._cleanMode(cmp);
	zkWnd2.cleanupShadow(cmp);
};
/** Fixed the content div's height. */
zkWnd2.onVisi = zkWnd2.onSize = function (cmp) {
	zkWnd2.hideShadow(cmp);
	if (getZKAttr(cmp, "maximized") == "true") {
		if (!cmp._maximized)
			zkWnd2.syncMaximized(cmp);
		cmp._maximized = false;
	}
	zkWnd2._fixWdh(cmp);
	zkWnd2._fixHgh(cmp);
	zkWnd2.syncShadow(cmp);
};
zkWnd2.syncMaximized = function (cmp) {
	if (!cmp._lastSize) return;
	var floated = !zkWnd2._embedded(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode,
		s = cmp.style;
		
	// Sometimes, the clientWidth/Height in IE6 is wrong.
	var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.getBorderWidth(op)) : op.clientWidth;
	var sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.getBorderHeight(op)) : op.clientHeight;
	if (!floated) {
		sw -= zk.getPaddingWidth(op);
		sw = zk.revisedSize(cmp, sw);
		sh -= zk.getPaddingHeight(op);
		sh = zk.revisedSize(cmp, sh, true);
	}
	if (sw < 0) sw = 0;
	if (sh < 0) sh = 0;
	
	s.width = sw + "px";
	s.height = sh + "px";
};
zkWnd2._fixWdh = zk.ie7 ? function (cmp) {
	if (zkWnd2._embedded(cmp) || zkWnd2._popup(cmp) || !zk.isRealVisible(cmp)) return;
	var wdh = cmp.style.width,
		tl = zk.firstChild(cmp, "DIV"),
		hl = $e(cmp, "caption") ? zk.nextSibling(tl, "DIV") : null,
		bl = zk.lastChild(cmp, "DIV"),
		n = $e(cmp.id + "!cave").parentNode;
	if (!wdh || wdh == "auto") {
		var diff = zk.getPadBorderWidth(n.parentNode) + zk.getPadBorderWidth(n.parentNode.parentNode);
		if (tl) {
			tl.firstChild.style.width = n.offsetWidth + diff + "px";
		}
		if (hl) {
			hl.firstChild.firstChild.style.width = n.offsetWidth - (zk.getPadBorderWidth(hl)
				+ zk.getPadBorderWidth(hl.firstChild) - diff) + "px";
		}
		if (bl) {
			bl.firstChild.style.width = n.offsetWidth + diff + "px";
		}
	} else {
		if (tl) tl.firstChild.style.width = "";
		if (hl) hl.firstChild.style.width = "";
		if (bl) bl.firstChild.style.width = "";
	}
} : zk.voidf;
zkWnd2._fixHgh = function (cmp) {
	if (!zk.isRealVisible(cmp)) return; //Bug #1944729
	var hgh = cmp.style.height;
	var n = $e(cmp.id + "!cave");
	if (zk.ie6Only && ((hgh && hgh != "auto" )|| n.style.height)) n.style.height = "0px";
	if (hgh && hgh != "auto")
		zk.setOffsetHeight(n, zkWnd2.getOffsetHeight(cmp));
};
/**
 * Returns the offsetHeight of window
 * @since 3.6.0
 */
zkWnd2.getOffsetHeight = function (cmp) {
	var h = cmp.offsetHeight - 1;
	h -= zkWnd2.getTitleHeight(cmp);
	if(!zkWnd2._embedded(cmp) && !zkWnd2._popup(cmp)) {
		var n = $e(cmp.id + "!cave"), bl = zk.lastChild(cmp, "DIV"), title = $e(cmp.id + "!caption");
		h -= bl.offsetHeight;
		if (n)
			h -= zk.getPadBorderHeight(n.parentNode);
		if (title)
			h -= zk.getPadBorderHeight(title.parentNode);
	}
	return h - zk.getPadBorderHeight(cmp);
};
/**
 * Returns the title height of the specified element.
 * @since 3.5.0
 */
zkWnd2.getTitleHeight = function (cmp) {
	var title = $e(cmp.id + "!caption"),
		tl = zk.firstChild(cmp, "DIV"),
		top = title ? tl.offsetHeight : 0;
	if (!zkWnd2._embedded(cmp) && !zkWnd2._popup(cmp) && !title) 
		top += zk.nextSibling(tl, "DIV").offsetHeight;
	return title ? title.offsetHeight + top : top;
};
/**
 * Returns the shadow instance of the specified component.
 * @param {Object} cmp a window component.
 * @since 3.5.0
 */
zkWnd2.getShadow = function (cmp) {
	return cmp._shadow;
};
/**
 * Initializes the shadow object for the specified component.
 * @since 3.5.0
 */
zkWnd2.initShadow = function (cmp) {
	if (!cmp._shadow && getZKAttr(cmp, "shadow") != 'false')
		cmp._shadow = new zk.Shadow(cmp, {left: -4, right: 4, top: -2, bottom: 3});
	return cmp._shadow;
};
/**
 * Clean the shadow object for the specified component.
 * @since 3.5.0
 */
zkWnd2.cleanupShadow = function (cmp) {
	if (cmp._shadow) {
		cmp._shadow.cleanup();
		cmp._shadow = null;
	}
};
/**
 * Sync the region of the shadow from the specified component.
 * @param {Object} cmp a window component.
 * @since 3.5.0
 */
zkWnd2.syncShadow = function (cmp) {
	if (zkWnd2._embedded(cmp)) return;
	if (getZKAttr(cmp, "maximized") == "true" || getZKAttr(cmp, "minimized") == "true") {
		zkWnd2.hideShadow(cmp);
		return;
	}
	var sw = zkWnd2.getShadow(cmp);
	if (sw) sw.sync();
};
/**
 * Hides the region of the shadow from the specified component.
 * @param {Object} cmp a window component.
 * @since 3.5.0
 */
zkWnd2.hideShadow = function (cmp) {
	if (zkWnd2.getShadow(cmp)) zkWnd2.getShadow(cmp).hide();
};
zkWnd2._embedded = function (cmp) {
	var v = getZKAttr(cmp, "mode");
	return !v || v == "embedded";
};
zkWnd2._popup = function (cmp) {
	var v = getZKAttr(cmp, "mode");
	return !v || v == "popup";
};
// declare this function after zkWnd2.hideShadow is ready.
zkWnd2.onHide = zkWnd2.hideShadow;
zkWnd2.setAttr = function (cmp, nm, val) {
	switch (nm) {
	case "visibility":
		var visible = val == "true",
			embedded = zkWnd2._embedded(cmp),
			order = embedded ? 0: 1;
		if (getZKAttr(cmp, "maximized") == "true") {
			zkWnd2.maximize(cmp, false);
		} else if (getZKAttr(cmp, "minimized") == "true") {
			zkWnd2.minimize(cmp, false);
		}
		
		zkWnd2.hideShadow(cmp);
		//three cases:
		//order=0: cmp and all its ancestor are embedded
		//1: cmp is the first non-embedded, i.e., all its ancestors are embeded
		//2: cmp has an ancesor is non-embedded
		//
		//Since vparent is used if order=1, we have to handle visibility diff
		for (var n = cmp; n = $parent(n);) {
			if ($type(n) == zkWnd2.ztype && !zkWnd2._embedded(n)) {
				order = 2;
				break;
			}
		}

		if (order == 1) { //with vparent
			setZKAttr(cmp, "vvisi", visible ? 't': 'f');
			visible = visible && zk.isRealVisible($parent($childExterior(cmp))); //Bug #1831534
			zk.setVisible(cmp, visible, {anima: true});
			if (visible) zk.setVParent(cmp); //Bug 1816451
		} else {
			//order=0: might have a child with vparent, and realVisi changed
			if (order == 0 && (visible != zk.isRealVisible(cmp))) {
				for (var id in zk._vpts)
					if (zk.isAncestor(cmp, id)) {
						var n = $e(id);
						if (n) {
							var vvisi = getZKAttr(n, "vvisi");
							if (vvisi != 'f') {
								var nvisi = $visible(n);
								if (nvisi != visible) {
									if (!vvisi)
										setZKAttr(n, "vvisi", nvisi ? 't': 'f');
									zk.setVisible(n, visible, {anima: true});
								}
							}
						}
					}
			}

			rmZKAttr(cmp, "vvisi"); //just in case
			zk.setVisible(cmp, visible, {anima: true});
		}
		if (!embedded) zkau.hideCovered(); //Bug 1719826
		
		// sometimes the timing of the sync shadow with the cmp is unreliable
		// when the cmp uses the onshow() or the onhide() to do something with anima.
		if (visible) zkWnd2.syncShadow(cmp); 
		else zkWnd2.hideShadow(cmp);
		return true;

	case "z.sizable":
		zkau.setAttr(cmp, nm, val);
		zkWnd2.setSizable(cmp, val == "true");
		return true;

	case "z.cntStyle":
		var n = $e(cmp.id + "!cave");
		if (n) {
			zk.setStyle(n, val != null ? val: "");
			zkWnd2.onSize(cmp); //border's dimension might be changed
		}
		return true;  //no need to store z.cntType
	case "z.pos":
		var pos = getZKAttr(cmp, "pos");
		zkau.setAttr(cmp, nm, val);
		if (val && !zkWnd2._embedded(cmp)) {
			zkWnd2.hideShadow(cmp);
			if (pos == "parent" && val != pos) {
				var left = cmp.style.left, top = cmp.style.top;
				var xy = getZKAttr(cmp, "offset").split(",");
				left = $int(left) - $int(xy[0]) + "px";
				top = $int(top) - $int(xy[1]) + "px";
				cmp.style.left = left;
				cmp.style.top = top;
				rmZKAttr(cmp, "offset");
			} else if (val == "parent") {
				var parent = zk.isVParent(cmp);
				if (parent) {
					var xy = zk.revisedOffset(parent),
						left = $int(cmp.style.left), top = $int(cmp.style.top);
					setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
					cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
					cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
				}
			}
			zkWnd2._center(cmp, null, val);
			//if val is null, it means no change at all
			zkau.hideCovered(); //Bug 1719826
			zkWnd2.syncShadow(cmp);
		}
		return true;

	case "style":
	case "style.height":
		zkWnd2.hideShadow(cmp);
		zkau.setAttr(cmp, nm, val);
		if (nm == "style.height") {
			zk.beforeSizeAt(cmp);
			zk.onSizeAt(cmp); // Note: IE6 is broken, because its offsetHeight doesn't update.
		} else {
			zkWnd2._fixHgh(cmp);
			zkWnd2.syncShadow(cmp);
		}
		return true;
	case "style.width":
		zkau.setAttr(cmp, nm, val);
		zk.beforeSizeAt(cmp);
		zk.onSizeAt(cmp);
		return true;
	case "style.top":
	case "style.left":
		if (!zkWnd2._embedded(cmp) && getZKAttr(cmp, "pos") == "parent") {
			var offset = getZKAttr(cmp, "offset");
			if (offset) {
				zkWnd2.hideShadow(cmp);
				var xy = offset.split(",");
				if (nm == "style.top") {
					cmp.style.top = $int(xy[1]) + $int(val) + "px";
				} else {
					cmp.style.left = $int(xy[0]) + $int(val) + "px";
				}
				zkWnd2.syncShadow(cmp);
				return true;
			}
		}
		zkau.setAttr(cmp, nm, val);
		if (!zkWnd2._embedded(cmp))
			zkWnd2.syncShadow(cmp);
		return true;
	case "z.maximized":
		zkau.setAttr(cmp, nm, val);
		if (getZKAttr(cmp, "minimized") == "true") {
			zkWnd2.minimize(cmp, false);
		}
		zkWnd2.maximize(cmp, val == "true", true);
		return true;
	case "z.minimized":
		zkau.setAttr(cmp, nm, val);
		if (getZKAttr(cmp, "maximized") == "true") {
			zkWnd2.maximize(cmp, false);
		}
		zkWnd2.minimize(cmp, val == "true", true);
		return true;
	case "z.shadow":
		zkau.setAttr(cmp, nm, val);

		var mode = getZKAttr(cmp, "mode");
		if (mode != 'embedded')
			if (getZKAttr(cmp, "shadow") == "false")
				zkWnd2.cleanupShadow(cmp);
			else
				zkWnd2.initShadow(cmp).sync();
		return true;
	}
	return false;
};

////////
// Handle sizable window //
zkWnd2.sizable = function (cmp) {
	return getZKAttr(cmp, "sizable") == "true";
};
zkWnd2.setSizable = function (cmp, sizable) {
	var id = cmp.id;
	if (sizable) {
		if (!zkWnd2._szs[id]) {
			var orgpos = cmp.style.position; //Bug 1679593
			zkWnd2._szs[id] = new zDraggable(cmp, {
				starteffect: zkau.closeFloats, overlay: true,
				endeffect: zkWnd2._endsizing, ghosting: zkWnd2._ghostsizing,
				revert: true, reverteffect: zk.voidf,
				ignoredrag: zkWnd2._ignoresizing, draw: zkWnd2._draw
			});
			cmp.style.position = orgpos;
		}
	} else {
		if (zkWnd2._szs[id]) {
			zkWnd2._szs[id].destroy();
			delete zkWnd2._szs[id];
		}
	}
};
/** 0: none, 1: top, 2: right-top, 3: right, 4: right-bottom, 5: bottom,
 * 6: left-bottom, 7: left, 8: left-top
 */
zkWnd2._insizer = function (cmp, x, y) {
	var ofs = zk.revisedOffset(cmp);
	var r = ofs[0] + cmp.offsetWidth, b = ofs[1] + cmp.offsetHeight;
	if (x - ofs[0] <= 5) {
		if (y - ofs[1] <= 5) return 8;
		else if (b - y <= 5) return 6;
		else return 7;
	} else if (r - x <= 5) {
		if (y - ofs[1] <= 5) return 2;
		else if (b - y <= 5) return 4;
		else return 3;
	} else {
		if (y - ofs[1] <= 5) return 1;
		else if (b - y <= 5) return 5;
	}
};
zkWnd2.onmouseover = function (evt, cmp) {
	var target = Event.element(evt);
	if (zkWnd2.sizable(cmp)) {
		var c = zkWnd2._insizer(cmp, Event.pointerX(evt), Event.pointerY(evt));
		var handle = zkWnd2._embedded(cmp) ? false : $e(cmp.id + "!caption");
		if (c) {
			if (getZKAttr(cmp, "maximized") == "true") return; // unsupported this case.
			zk.backupStyle(cmp, "cursor");
			cmp.style.cursor = c == 1 ? 'n-resize': c == 2 ? 'ne-resize':
				c == 3 ? 'e-resize': c == 4 ? 'se-resize':
				c == 5 ? 's-resize': c == 6 ? 'sw-resize':
				c == 7 ? 'w-resize': 'nw-resize';
			if (handle) handle.style.cursor = "";
		} else {
			zk.restoreStyle(cmp, "cursor");
			if (handle) handle.style.cursor = "move";
		}
	}
};
/** Called by zkWnd2._szs[]'s ignoredrag for resizing window. */
zkWnd2._ignoresizing = function (cmp, pointer) {
	if (getZKAttr(cmp, "maximized") == "true") return true;
	var dg = zkWnd2._szs[cmp.id];
	if (dg) {
		var v = zkWnd2._insizer(cmp, pointer[0], pointer[1]);
		if (v) {
			zkWnd2.hideShadow(cmp);
			dg.z_dir = v;
			var offs = zk.revisedOffset(cmp);
			dg.z_box = {
				top: offs[1], left: offs[0] ,height: cmp.offsetHeight,
				width: cmp.offsetWidth, minHeight: $int(getZKAttr(cmp, "minheight")),
				minWidth: $int(getZKAttr(cmp, "minwidth"))
			};
			dg.z_orgzi = cmp.style.zIndex;
			return false;
		}
	}
	return true;
};
zkWnd2._endsizing = function (cmp, evt) {
	var dg = zkWnd2._szs[cmp.id];
	if (!dg) return;

	if (dg.z_orgzi != null) {
		cmp.style.zIndex = dg.z_orgzi; //restore it (Bug 1619349)
		dg.z_orgzi = null
	}

	if (dg.z_szofs) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}

		//adjust size
		setTimeout("zkWnd2._resize($e('"+cmp.id+"'),"+dg.z_szofs.top+","
			+dg.z_szofs.left+","+dg.z_szofs.height+","+dg.z_szofs.width+",'"+keys+"')", 50);
		dg.z_box = dg.z_dir = dg.z_szofs = null;
	}
};
zkWnd2._resize = function (cmp, t, l, h, w, keys) {
	cmp.style.visibility = "hidden";
	if (w != cmp.offsetWidth || h != cmp.offsetHeight) {
		if (w != cmp.offsetWidth) {
			cmp.style.width = w + "px";
			zkWnd2._fixWdh(cmp);
		}
		if (h != cmp.offsetHeight) {
			cmp.style.height = h + "px";
			zkWnd2._fixHgh(cmp);
		}
		zkau.sendOnSize(cmp, keys);
	}
	if (l != cmp.offsetLeft || t != cmp.offsetTop) {
		if (l != null) cmp.style.left = l + "px";
		if (t != null) cmp.style.top = t + "px";
		zkau.sendOnMove(cmp, keys);
	}
	zkWnd2.syncShadow(cmp);
	cmp.style.visibility = "";
	if (!zkWnd2._embedded(cmp))
		zkau.hideCovered();
};

/* @param ghosting whether to create or remove the ghosting
 */
zkWnd2._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		zkWnd2.hideShadow(dg.element);
		var ofs = zkau.beginGhostToDIV(dg);
		var html = '<div id="zk_ddghost" class="z-window-resize-faker" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;"></div>';
			document.body.insertAdjacentHTML("afterbegin", html);
		dg.element = $e("zk_ddghost");
	} else {
		var org = zkau.getGhostOrgin(dg);
		if (org) {
			dg.z_szofs = {
				top: dg.element.offsetTop, left: dg.element.offsetLeft, 
				height: zk.revisedSize(dg.element, dg.element.offsetHeight, true), 
				width: zk.revisedSize(dg.element, dg.element.offsetWidth)
				};
		} else {
			dg.z_szofs = null;
		}
		zkau.endGhostToDIV(dg);
	}
};
zkWnd2._draw = function (dg, pointer) {
	if (dg.z_dir == 8 || dg.z_dir <= 2) {
		var h = dg.z_box.height + dg.z_box.top - pointer[1];
		if (h < dg.z_box.minHeight) {
			pointer[1] = dg.z_box.height + dg.z_box.top - dg.z_box.minHeight;
			h = dg.z_box.minHeight;
		}
		dg.element.style.height = h + "px";
		dg.element.style.top = pointer[1] + "px";
	}
	if (dg.z_dir >= 4 && dg.z_dir <= 6) {
		var h = dg.z_box.height + pointer[1] - dg.z_box.top;
		if (h < dg.z_box.minHeight) h = dg.z_box.minHeight;
		dg.element.style.height = h + "px";
	}
	if (dg.z_dir >= 6 && dg.z_dir <= 8) {
		var w = dg.z_box.width + dg.z_box.left - pointer[0];
		if (w < dg.z_box.minWidth) {
			pointer[0] = dg.z_box.width + dg.z_box.left - dg.z_box.minWidth;
			w = dg.z_box.minWidth;
		}
		dg.element.style.width = w + "px";
		dg.element.style.left = pointer[0] + "px";
	}
	if (dg.z_dir >= 2 && dg.z_dir <= 4) {
		var w =  dg.z_box.width + pointer[0] - dg.z_box.left;
		if (w < dg.z_box.minWidth) w = dg.z_box.minWidth;
		dg.element.style.width = w + "px";
	}
};
////////
// Handling Overlapped, Modal, Popup and Embedded //
zkWnd2._initMode = function (cmp) {
	var mode = getZKAttr(cmp, "mode");
	var replace = zkWnd2._clean2[cmp.id] == mode;
	if (replace) {//replace with the same mode
		delete zkWnd2._clean2[cmp.id]; //and _doXxx will handle it
		if (getZKAttr(cmp, "visible") == "true")			
			zk.cleanVisibility(cmp);
	}
	else if (zkWnd2._clean2[cmp.id])
		zkWnd2._cleanMode2(cmp.id, true); //replace with a new mode
	if (mode && mode != "embedded")
		zkWnd2.initShadow(cmp);
	switch (mode) {
	case "modal":
	case "highlighted":
		zkWnd2._doModal(cmp, replace);
		break;
	case "overlapped":
		zkWnd2._doOverlapped(cmp, replace);
		break;
	case "popup":
		zkWnd2._doPopup(cmp, replace);
	//default: embedded
	}
};
zkWnd2._cleanMode = function (cmp) {
	var mode = getZKAttr(cmp, "mode");
	if (mode) {
		zkWnd2._stick(cmp); //cleanup draggable or so
		zkWnd2._clean2[cmp.id] = mode;
		zkau._modals.remove(cmp.id);
			// Bug 2465826 we cannot remove the id from the zkau._modals after invoking
			// zkWnd2._cleanMode2 in a timer.
		setTimeout("zkWnd2._cleanMode2('"+cmp.id+"')", 5);
			//don't clean immediately since it might be replaced
			//(due to invalidate)
	}
};
/** 2nd phase of cleaning mode. */
zkWnd2._cleanMode2 = function (uuid, replace) {
	var mode = zkWnd2._clean2[uuid];
	if (mode) {
		delete zkWnd2._clean2[uuid];

		switch (mode) {
		case "modal":
		case "highlighted":
			zkWnd2._endModal(uuid, replace);
			break;
		case "overlapped":
			zkWnd2._endOverlapped(uuid, replace);
			break;
		case "popup":
			zkWnd2._endPopup(uuid, replace);
	//default: embedded
		}
	}
};
/** Shows the window with the anima effect, if any. */
zkWnd2._show = function (cmp) {
	if (getZKAttr(cmp, "conshow")) //enforce the anima effect, if any
		cmp.style.display = "none";
		
	if (getZKAttr(cmp, "visible") == "true")
		zk.cleanVisibility(cmp);
			//turn it on since Window.getRealStyle turn it off to
			//have the better effect if the window contains a lot of items
	zk.show(cmp);
};

//Overlap/Popup//
/** Makes the component as popup. */
zkWnd2._doPopup = function (cmp, replace) {
	zkWnd2._doOverpop(cmp, zkau._popups, replace);
};
/** Makes the popup component as normal. */
zkWnd2._endPopup = function (uuid, replace) {
	zkWnd2._endOverpop(uuid, zkau._popups, replace);
};

/** Makes the component as overlapped. */
zkWnd2._doOverlapped = function (cmp, replace) {
	zkWnd2._doOverpop(cmp, zkau._overlaps, replace);
};
/** Makes the popup component as normal. */
zkWnd2._endOverlapped = function (uuid, replace) {
	zkWnd2._endOverpop(uuid, zkau._overlaps, replace);
};

zkWnd2._doOverpop = function (cmp, storage, replace) {
	var pos = getZKAttr(cmp, "pos");
	var isV = zkWnd2.shallVParent(cmp);
	if (!pos && isV && !cmp.style.top && !cmp.style.left) {		
		var xy = zk.revisedOffset(cmp);
		cmp.style.left = xy[0] + "px";
		cmp.style.top = xy[1] + "px";
	} else if (pos == "parent" && isV) {
		var xy = zk.revisedOffset(cmp.parentNode),
			left = $int(cmp.style.left), top = $int(cmp.style.top);
		setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
		cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
		cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
	}
	if (isV) zk.setVParent(cmp);
	
	if (replace) {
		zkau.fixZIndex(cmp);
		zkWnd2._float(cmp);
		return;
	}
	
	if (pos) zkWnd2._center(cmp, null, pos); //unlike modal, change only if pos

	zkau.closeFloats(cmp);

	zkau.fixZIndex(cmp);
	zkWnd2._float(cmp);
	storage.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();

	if (zk.isVisible(cmp)) //it happens when closing a modal (becomes overlap)
		zkWnd2._show(cmp);

	//zk.asyncFocusDown(cmp.id, 45); //don't exceed 50 (see au's focus command)
	//20080215 Tom: don't change focus if overlapped (more reasonable spec)
};
zkWnd2._endOverpop = function (uuid, storage, replace) {
	storage.remove(uuid);		
	var cmp = $e(uuid);
	if (cmp) {
		zk.unsetVParent(cmp);
		zkau.hideCovered();
		if (!replace) zkWnd2._stick(cmp);
	}
};
/** Test whether el shall become a virtual parent (when overlap/...).
 * Note: only the first overlap/... need to setVParent
 */
zkWnd2.shallVParent = function (el) {
	while (el = $parent(el))
		if ($type(el) == zkWnd2.ztype && !zkWnd2._embedded(el))
			return false; //only one of them shall become a virtual parent
	return true;
};
//Modal//
/** Makes the window as modal. */
zkWnd2._doModal = function (cmp, replace) {
	if (!getZKAttr(cmp, "conshow")) {
		var onshow = getZKAttr(cmp, "aos") || "appear";
		if (onshow != "z_none")
			setZKAttr(cmp, "conshow", "anima." + onshow + "($e('"+cmp.id+"'));");
	}
	//center component
	var nModals = zkau._modals.length;
	zkau.fixZIndex(cmp, true); //let fixZIndex reset topZIndex if possible
	var zi = ++zkau.topZIndex; //mask also need another index

	var pos = getZKAttr(cmp, "pos");
	if (zkWnd2.shallVParent(cmp)) {
		if (pos == "parent") {
			var xy = zk.revisedOffset(cmp.parentNode),
				left = $int(cmp.style.left), top = $int(cmp.style.top);
			setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
			cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
			cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
		}
		zk.setVParent(cmp);
	}
	
	if (replace) {
		zkWnd2._float(cmp);
		if (!zkau._modals.contains(cmp.id))
			zkau._modals.push(cmp.id); // restore the id, Bug 2465826
		return;
	}
	
	zkWnd2._center(cmp, zi, pos); //called even if pos not defined
		//show dialog first to have better response.
	
	if (!pos) {
		var top = $int(cmp.style.top), y = zk.innerY();
		if (y) {
			var y1 = top - y;
			if (y1 > 100) {
				cmp.style.top = top - (y1 - 100) + "px";
			}
		} else if (top > 100){
			cmp.style.top = "100px";
		}
	}
	
	zkWnd2._show(cmp); //unlike other mode, it must be visible

	zkau.closeFloats(cmp);

	var maskId = cmp.id + ".mask";
	var mask = $e(maskId);
	if (!mask) {
		//Note: a modal window might be a child of another
		var bMask = true;
		for (var j = 0; j < nModals; ++j) {
			var n = $e(zkau._modals[j]);
			if (n && zk.isAncestor(n, cmp)) {
				bMask = false;
				break;
			}
		}
		if (bMask) {
			//bug 1510218: we have to make it as a sibling to cmp
			cmp.insertAdjacentHTML(
				"beforebegin", '<div id="'+maskId+'" class="z-modal-mask"></div>');
			mask =  $e(maskId);
			zk.listen(mask, "mousemove", Event.stop);
		}
	}

	//position mask to be full window
	if (mask) {
		zkWnd2._posMask(mask);
		mask.style.display = "block";
		mask.style.zIndex = zi - 1;
		if (zkau.currentFocus) //store it
			mask.setAttribute("zk_prevfocus", zkau.currentFocus.id);
	}

	zkau._modals.push(cmp.id);
	if (nModals == 0) {
		zk.listen(window, "resize", zkWnd2._onMoveMask);
		zk.listen(window, "scroll", zkWnd2._onMoveMask);
	}

	zkWnd2._float(cmp);
	zk.asyncFocusDown(cmp.id, 45); //don't exceed 50 (see au's focus command)

	zkWnd2._modal2[cmp.id] = true;
	setTimeout("zkWnd2._doModal2('"+cmp.id+"')", 5); //process it later for better responsive
};
/** Does the 2nd phase processing of modal. */
zkWnd2._doModal2 = function (uuid) {
	if (zkWnd2._modal2[uuid]) {
		delete zkWnd2._modal2[uuid];

		var cmp = $e(uuid);
		if (cmp) {
			zk.restoreDisabled(cmp); //there might be two or more modal dlgs
			zk.disableAll(cmp);
		}
	}
};
/** Clean up the modal component. */
zkWnd2._endModal = function (uuid, replace) {
	var maskId = uuid + ".mask";
	var mask = $e(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		zk.remove(mask);
	}

	delete zkWnd2._modal2[uuid];
	
	var cmp = $e(uuid);
	if (cmp) zk.unsetVParent(cmp);
	if (zkau._modals.length == 0) {
		zk.unlisten(window, "resize", zkWnd2._onMoveMask);
		zk.unlisten(window, "scroll", zkWnd2._onMoveMask);
		window.onscroll = null;
		zk.restoreDisabled();
	} else {
		var lastid = zkau._modals[zkau._modals.length - 1];
		var last = $e(lastid);
		if (last) {
			zk.restoreDisabled(last);
			if (!prevfocusId && !zk.inAsyncFocus) zk.asyncFocusDown(lastid, 20);
		}
	}

	if (!replace && cmp) zkWnd2._stick(cmp);

	if (prevfocusId && !zk.inAsyncFocus) zk.asyncFocus(prevfocusId, 20);
};

/** Handles onsize to re-position mask. */
zkWnd2._onMoveMask = function (evt) {
	for (var j = zkau._modals.length; --j >= 0;) {
		var mask = $e(zkau._modals[j] + ".mask");
		if (mask) {
			zkWnd2._posMask(mask);
			return;
		}
	}
};
/** Position the mask window. */
zkWnd2._posMask = function (mask) {
	var ofs = zk.toStyleOffset(mask, zk.innerX(), zk.innerY());
	mask.style.left = ofs[0] + "px";
	mask.style.top = ofs[1] + "px";
	mask.style.width = zk.innerWidth() + "px";
	mask.style.height = zk.innerHeight() + "px";
};
/** Makes a window in the center. */
zkWnd2._center = function (cmp, zi, pos) {
	if (pos == "parent") return;
	cmp.style.position = "absolute"; //just in case
	zk.center(cmp, pos);
	var sw = zkWnd2.getShadow(cmp);
	if (pos && sw) {
		var opts = sw.opts, l = cmp.offsetLeft, t = cmp.offsetTop,
			s = cmp.style; 
		if (pos.indexOf("left") >= 0 && opts.left < 0)
			s.left = (l - opts.left) + "px";
		else if (pos.indexOf("right") >= 0 && opts.right > 0)
			s.left = (l - opts.right) + "px";

		if (pos.indexOf("top") >= 0 && opts.top < 0)
			s.top = (t - opts.top) + "px";
		else if (pos.indexOf("bottom") >= 0 && opts.bottom > 0)
			s.top = (t - opts.bottom) + "px";
	}
	zkau.sendOnMove(cmp);

	if (zi || zi == 0) {
		cmp.style.zIndex = zi;
		zkau.sendOnZIndex(cmp);
		//let the server know the position. otherwise, invalidate will
		//cause it to be moved to center again
	}
}

//Utilities//
/** Makes a window movable. */
zkWnd2._float = function (cmp) {
	if (cmp) {
		var handle = $e(cmp.id + "!caption");
		if (handle) {
			handle.style.cursor = "move";
			cmp.style.position = "absolute"; //just in case
			zul.initMovable(cmp, {
				handle: handle, starteffect: zkWnd2._startMove, overlay: true,
				change: zkau.hideCovered, ghosting: zkWnd2._ghostmove, 
				ignoredrag: zkWnd2._ignoremove,
				endeffect: zkWnd2._onWndMove});
			//we don't use options.change because it is called too frequently
		}
	}
};
/* @param ghosting whether to create or remove the ghosting
 */
zkWnd2._ghostmove = function (dg, ghosting, pointer) {
	if (ghosting) {
		zkWnd2.hideShadow(dg.element);
		var ofs = zkau.beginGhostToDIV(dg),
			top = zk.firstChild(dg.element, "DIV"),
			header = zk.nextSibling(top, 'DIV'),
			fakeT = top.cloneNode(true),
			fakeH = header.cloneNode(true);
		var html = '<div id="zk_ddghost" class="z-window-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;z-index:'+dg.element.style.zIndex+'"><dl></dl></div>';
		document.body.insertAdjacentHTML("afterbegin", html);
		dg._zoffs = ofs;
		dg.element.style.visibility = "hidden";
		var h = dg.element.offsetHeight - top.offsetHeight - header.offsetHeight;
		dg.element = $e("zk_ddghost");
		dg.element.firstChild.style.height = zk.revisedSize(dg.element.firstChild, h, true) + "px";
		dg.element.insertBefore(fakeT, dg.element.firstChild);
		dg.element.insertBefore(fakeH, dg.element.lastChild);
	} else {
		var org = zkau.getGhostOrgin(dg);
		if (org) {
			org.style.top = org.offsetTop + dg.element.offsetTop - dg._zoffs[1] + "px";
			org.style.left = org.offsetLeft + dg.element.offsetLeft - dg._zoffs[0] + "px";
		}
		zkau.endGhostToDIV(dg);
		document.body.style.cursor = "";
	}
};
zkWnd2._ignoremove = function (cmp, pointer, event) {
	var target = Event.element(event);
	if (!target || target.id.indexOf("!close") > -1 || target.id.indexOf("!minimize") > -1
			|| target.id.indexOf("!maximize") > -1) return true;
	if (!zkWnd2.sizable(cmp) || (cmp.offsetTop + 4 < pointer[1] && cmp.offsetLeft + 4 < pointer[0] 
		&& cmp.offsetLeft + cmp.offsetWidth - 4 > pointer[0])) return false;
	return true;
};
/**
 * For bug #1568393: we have to change the percetage to the pixel.
 */
zkWnd2._startMove = function (cmp, handle) {
	if(cmp.style.top && cmp.style.top.indexOf("%") >= 0)
		 cmp.style.top = cmp.offsetTop + "px";
	if(cmp.style.left && cmp.style.left.indexOf("%") >= 0)
		 cmp.style.left = cmp.offsetLeft + "px";
	var real = $real(handle);
	zkau.closeFloats(real, handle);
};
/** Makes a window un-movable. */
zkWnd2._stick = function (cmp) {
	if (cmp) {
		zul.cleanMovable(cmp.id);
		cmp.style.position = ""; //aculous changes it to relative
	}
};

/** Called back when overlapped and popup is moved. */
zkWnd2._onWndMove = function (cmp, evt) {
	cmp.style.visibility = "";
	var keys = "";
	if (evt) {
		if (evt.altKey) keys += 'a';
		if (evt.ctrlKey) keys += 'c';
		if (evt.shiftKey) keys += 's';
	}
	zkWnd2.syncShadow(cmp);
	zkau.sendOnMove(cmp, keys);
};
