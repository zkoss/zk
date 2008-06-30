/* panel.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 17 10:44:03 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.zul"); //zul and msgzul

/**
 * A Panel component
 * @since 3.5.0
 */
zkPanel = {
	init: function (cmp) {
		zkPanel.onVisi = zkPanel.onSize;
		zkPanel._initBtn(cmp);
		if (zkPanel.isFloatable(cmp))
			zkPanel._initFloat(cmp);
		if (getZKAttr(cmp, "maximizable") == "true" && getZKAttr(cmp, "maximized") == "true")
			zk.addInitLater(function () {zkPanel.maximize(cmp, true, true);});
	},
	_initBtn: function (cmp) {
		if (getZKAttr(cmp, "closable") == "true") {
			var close = $e(cmp.id + "!close");
			if (close) {
				zk.listen(close, "click", function (evt) {zkau.sendOnClose(cmp, true); Event.stop(evt);});
				zk.listen(close, "mouseover", function () {zk.addClass(close, "z-panel-close-over");});
				zk.listen(close, "mouseout", function () {zk.rmClass(close, "z-panel-close-over");});
				if (!close.style.cursor) close.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "collapsible") == "true") {
			var toggle = $e(cmp.id + "!toggle");
			if (toggle) {
				zk.listen(toggle, "click", zkPanel.onOpen);
				zk.listen(toggle, "mouseover", function () {zk.addClass(toggle, "z-panel-toggle-over");});
				zk.listen(toggle, "mouseout", function () {zk.rmClass(toggle, "z-panel-toggle-over");});
				if (!toggle.style.cursor) toggle.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "maximizable") == "true") {
			var max = $e(cmp.id + "!maximize");
			if (max) {
				zk.listen(max, "click", zkPanel.onMaximize);
				zk.listen(max, "mouseover", function () {
						if (getZKAttr(cmp, "maximized") == "true")
							zk.addClass(max, "z-panel-maximized-over");
						zk.addClass(max, "z-panel-maximize-over");
					});
				zk.listen(max, "mouseout", function () {
						if (getZKAttr(cmp, "maximized") == "true")
							zk.rmClass(max, "z-panel-maximized-over");
						zk.rmClass(max, "z-panel-maximize-over");
					});
				if (!max.style.cursor) max.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "minimizable") == "true") {
			var min = $e(cmp.id + "!minimize");
			if (min) {
				zk.listen(min, "click", zkPanel.onMinimize);
				zk.listen(min, "mouseover", function () {zk.addClass(min, "z-panel-minimize-over");});
				zk.listen(min, "mouseout", function () {zk.rmClass(min, "z-panel-minimize-over");});
				if (!min.style.cursor) min.style.cursor = "default";
			}
		}
	},
	_initFloat: function (cmp) {
		if (!cmp.style.top && !cmp.style.left) {		
			var xy = zk.revisedOffset(cmp);
			cmp.style.left = xy[0] + "px";
			cmp.style.top = xy[1] + "px";
		}
		zkau.fixZIndex(cmp);
		cmp.style.position = "absolute";
		if (zkPanel.isMovable(cmp))
			zkPanel._initMove(cmp);
		zkPanel.initShadow(cmp);
		zkPanel._show(cmp);
	},
	_initMove: function (cmp) {
		var handle = $e(cmp.id + "!caption");
		if (handle) {
			handle.style.cursor = "move";
			cmp.style.position = "absolute"; //just in case
			zul.initMovable(cmp, {
				handle: handle, starteffect: zkPanel._startMove, overlay: true,
				change: zkau.hideCovered, ignoredrag: zkPanel._ignoreMove,
				endeffect: zkPanel._endMove});
		}
	},
	// Drag&Drop
	_ignoreMove: function (cmp, pointer, event) {
		var target = Event.element(event);
		if (!target || target.id.indexOf("!close") > -1
			|| target.id.indexOf("!toggle") > -1 || target.id.indexOf("!minimize") > -1
			|| target.id.indexOf("!maximize") > -1) return true;
		return false;
	},
	_startMove: function (cmp, handle) {
		zkPanel.hideShadow(cmp);
		if(cmp.style.top && cmp.style.top.indexOf("%") >= 0)
			 cmp.style.top = cmp.offsetTop + "px";
		if(cmp.style.left && cmp.style.left.indexOf("%") >= 0)
			 cmp.style.left = cmp.offsetLeft + "px";
		zkau.closeFloats(cmp, handle);
	},
	/** Called back when overlapped and popup is moved. */
	_endMove: function (cmp, evt) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}
		zkPanel.syncShadow(cmp);
		zkau.sendOnMove(cmp, keys);
	},
	cleanup: function (cmp) {
		if (cmp._lastSize) {
			cmp._lastSize = null;
		}
		zkPanel.cleanupShadow(cmp);
	},
	isFloatable: function (cmp) {
		return getZKAttr(cmp, "floatable") == "true";
	},
	isMovable: function (cmp) {
		return getZKAttr(cmp, "movable") == "true";
	},
	isClosable: function (cmp) {
		return getZKAttr(cmp, "closable") == "true";
	},
	onOpen: function (evt) {
		if (!evt) evt = window.event;
		
		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "collapsible") != "true")
				return;
	
			var bwrap = $e(cmp.id + "!bwrap");
			if (bwrap)
				zkPanel.open(cmp, !$visible(bwrap), false, true);
		}
	},
	open: function (cmp, open, silent, ignorable) {
		var cmp = $e(cmp);
		if (cmp) {
			var bwrap = $e(cmp.id + "!bwrap");
			if (bwrap && open != $visible(bwrap)
			&& (!ignorable || !getZKAttr(bwrap, "animating"))) {
				if (open) {
					zk.rmClass(cmp, "z-panel-collapsed");
					anima.slideDown(bwrap);
				} else {
					zk.addClass(cmp, "z-panel-collapsed");
					zkPanel.hideShadow(cmp);
					anima.slideUp(bwrap);
				}
	
				if (!silent)
					zkau.sendasap({uuid: cmp.id, cmd: "onOpen", data: [open]});
					setZKAttr(cmp, "open", open ? "true" : "false");
				if (open) 
					setTimeout(function(){
						zkPanel.hideShadow(cmp);
						zkPanel._fixHgh(cmp);
						zkPanel.syncShadow(cmp);
					}, 500); //after slide down
			}
		}
	},
	onMaximize: function (evt) {
		if (!evt) evt = window.event;
	
		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "maximizable") != "true")
				return;
	
			zkPanel.maximize(cmp, getZKAttr(cmp, "maximized") != "true", false, true);
		}
	},
	maximize: function (cmp, maximized, silent) {
		var cmp = $e(cmp);
		if (!zk.isRealVisible(cmp)) return;
		if (cmp) {
			var l, t, w, h, s = cmp.style;
			if (maximized) {
				zk.addClass($e(cmp.id + "!maximize"), "z-panel-maximized");
				zkPanel.hideShadow(cmp);
				var op = zkPanel.isFloatable(cmp) ? Position.offsetParent(cmp) : cmp.parentNode;
				l = s.left;
				t = s.top;
				w = s.width;
				h = s.height;
				
				// prevent the scroll bar.
				s.top = "-10000px";
				s.left = "-10000px";
				
				s.width = op.clientWidth + "px";
				s.height = op.clientHeight + "px";
				cmp._lastSize = {l:l, t:t, w:w, h:h};
				
				// restore.
				s.top = "0px";
				s.left = "0px";
				
				if (zkPanel.isClosable(cmp) && getZKAttr(cmp, "open") != "true") {
					zk.rmClass(cmp, "z-panel-collapsed");
					var bwrap = $e(cmp.id + "!bwrap");
					if (bwrap) bwrap.style.display = "";
				}
			} else {
				var max = $e(cmp.id + "!maximize");
				zk.rmClass(max, "z-panel-maximized");
				zk.rmClass(max, "z-panel-maximized-over");
				if (cmp._lastSize) {
					s.left = cmp._lastSize.l;
					s.top = cmp._lastSize.t;
					s.width = cmp._lastSize.w;
					s.height = cmp._lastSize.h;
				}
				l = s.left;
				t = s.top;
				w = s.width;
				h = s.height;
				if (zkPanel.isClosable(cmp) && getZKAttr(cmp, "open") != "true") {
					zk.addClass(cmp, "z-panel-collapsed");
					var bwrap = $e(cmp.id + "!bwrap");
					if (bwrap) bwrap.style.display = "none";
				}
				var body = $e(getZKAttr(cmp, "children"));
				if (body)
					body.style.width = body.style.height = "";
			}

			setZKAttr(cmp, "maximized", maximized ? "true" : "false");
			if (!silent)
				zkau.sendasap({uuid: cmp.id, cmd: "onMaximize", data: [l, t, w, h, maximized == true]});
				
			zk.beforeSizeAt(cmp);
			zk.onSizeAt(cmp);
		}
	},
	onMinimize: function (evt) {
		if (!evt) evt = window.event;
	
		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "minimizable") != "true")
				return;
	
			zkPanel.minimize(cmp, getZKAttr(cmp, "minimized") != "true", false, true);
		}
	},
	minimize: function (cmp, minimized, silent) {
		var cmp = $e(cmp);
		if (cmp) {
			var s = cmp.style, l = s.left, t = s.top, w = s.width, h = s.height;
			if (minimized) {
				zkPanel._hide(cmp);
			} else {
				zkPanel._show(cmp);
				zk.onSizeAt(cmp);
			}

			setZKAttr(cmp, "minimized", minimized ? "true" : "false");
			if (!silent)
				zkau.sendasap({uuid: cmp.id, cmd: "onMinimize", data: [l, t, w, h, minimized == true]});
		}
	},
	/** Fixed the content div's height. */
	onSize: function (cmp) {
		zkPanel.hideShadow(cmp);
		zkPanel._fixHgh(cmp);
		zkPanel._fixWdh(cmp);
		zkPanel.syncShadow(cmp);
	}, 
	_fixWdh: zk.ie7 ? function (cmp) {
		if (getZKAttr(cmp, "framable") != "true" || !zk.isRealVisible(cmp)) return;
		var body = $e(getZKAttr(cmp, "children"));
		if (!body) return;
		var wdh = cmp.style.width, fir = zk.firstChild(cmp, "DIV"), last = zk.lastChild(zk.lastChild(cmp, "DIV"), "DIV"),
			n = body.parentNode;
		if (!wdh || wdh == "auto") {
			var diff = zk.getFrameWidth(n.parentNode) + zk.getFrameWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.firstChild.style.width = n.offsetWidth - (zk.getFrameWidth(fir)
					+ zk.getFrameWidth(fir.firstChild) - diff) + "px";
			}
			if (last) {
				last.firstChild.firstChild.style.width = n.offsetWidth - (zk.getFrameWidth(last)
					+ zk.getFrameWidth(last.firstChild) - diff) + "px";
			}
		} else {
			if (fir) fir.firstChild.firstChild.style.width = "";
			if (last) last.firstChild.firstChild.style.width = "";
		}
	} : zk.voidf,
	_fixHgh: function (cmp) {
		if (!zk.isRealVisible(cmp)) return;
		var body = $e(getZKAttr(cmp, "children"));
		if (!body) return;
		var hgh = cmp.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0px";
		if (hgh && hgh != "auto")
			zk.setOffsetHeight(body, cmp.offsetHeight - zkPanel.getFrameHeight(cmp) - 1);
	},
	getFrameHeight: function (cmp) {
		var h = zk.getFrameHeight(cmp);
	    h += zkPanel.getTitleHeight(cmp);
		var tbar = $e(cmp.id + "!tbar"), bbar = $e(cmp.id + "!bbar");
	    if (getZKAttr(cmp, "framable") == "true") {
			var body = $e(getZKAttr(cmp, "children")), 
				ft = zk.lastChild($e(cmp.id + "!bwrap"), "DIV"), title = $e(cmp.id + "!caption");
	        h += ft.offsetHeight;
			if (body)
				h += zk.getFrameHeight(body.parentNode);
			if (title)
		        h += zk.getFrameHeight(title.parentNode);
	    } else {
			var fbar = $e(cmp.id + "!fbar");
			if (fbar)h += fbar.offsetHeight;
		}
		if (tbar) h += tbar.offsetHeight;
		if (bbar) h += bbar.offsetHeight;
	    return h;
	},
	getTitleHeight: function (cmp) {
		var title = $e(cmp.id + "!caption"), top = 0;
		if (getZKAttr(cmp, "framable") == "true" && !title)
			top = zk.firstChild(cmp, "DIV").firstChild.firstChild.offsetHeight;
		return title ? title.offsetHeight : top;
	},
	_show: function (cmp) {
		if (getZKAttr(cmp, "visible") == "true")
			cmp.style.visibility = "visible";
		zk.show(cmp);
		zkPanel.syncShadow(cmp);
	},
	_hide: function (cmp) {
		zk.hide(cmp);
		zkPanel.hideShadow(cmp);
	},
	// Panel Shadow
	/**
	 * Returns the shadow instance of the specified component.
	 */
	getShadow: function (cmp) {
		return cmp._shadow;
	},
	hideShadow: function (cmp) {
		if (zkPanel.getShadow(cmp)) zkPanel.getShadow(cmp).hide();
	},
	/**
	 * Initializes the shadow object for the specified component.
	 */
	initShadow: function (cmp) {
		cmp._shadow = new zk.Shadow(cmp);
	},
	/**
	 * Clean the shadow object for the specified component.
	 */
	cleanupShadow: function (cmp) {
		if (cmp._shadow) {
			cmp._shadow.cleanup();
			cmp._shadow = null;
		}
	},
	/**
	 * Sync the region of the shadow from the specified component.
	 */
	syncShadow: function (cmp) {
		if (!zkPanel.isFloatable(cmp) || !zkPanel.getShadow(cmp)) return;
		if (getZKAttr(cmp, "maximized") == "true" || getZKAttr(cmp, "minimized") == "true") {
			zkPanel.hideShadow(cmp);
			return;
		}
		var bwrap = $e(cmp.id + "!bwrap");
		if (bwrap && $visible(bwrap)) // if only title, nothing to do.
			zkPanel.getShadow(cmp).sync();
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.open":
				zkPanel.open(cmp, val == "true", true);
				return true; //no need to store z.open
			case "z.maximized":
				zkPanel.maximize(cmp, val == "true", true);
				return true;
			case "z.minimized":
				zkPanel.minimize(cmp, val == "true", true);
				return true;
			case "style":
			case "style.height":
				zkPanel.hideShadow(cmp);
				zkau.setAttr(cmp, nm, val);
				if (nm == "style.height") {
					zk.beforeSizeAt(cmp);
					zk.onSizeAt(cmp); // Note: IE6 is broken, because its offsetHeight doesn't update.
				} else {
					zkPanel._fixHgh(cmp);
					zkPanel.syncShadow(cmp);
				}
				return true;
			case "style.width":
				zkau.setAttr(cmp, nm, val);
				zk.beforeSizeAt(cmp);
				zk.onSizeAt(cmp);
				return true;
			case "style.top":
			case "style.left":
				zkPanel.hideShadow(cmp);
				if (nm == "style.top") {
					cmp.style.top = val;
				} else {
					cmp.style.left = val;
				}
				zkPanel.syncShadow(cmp);
				return true;
			}
		return false;
	}
};
/**
 * A Panelchildren component.
 * @since 3.5.0
 */
zkPanelchild = {
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "style":
				var panel = $parentByType(cmp, "Panel");
				zkPanel.hideShadow(panel);
				zkau.setAttr(cmp, nm, val);
				zkPanel._fixHgh(panel);
				zkPanel.syncShadow(panel);
				return true;
			}
		return false;
	}
};
