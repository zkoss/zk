/* portallayout.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 8 17:31:37 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/**
 * A portal layout lays out a container which can have multiple columns, and each
 * column may contain one or more panel.
 * @since 3.5.0
 */
zkPortalLayout = {
	drags: {},
	init: function(cmp) {
		for (var cols = zk.childNodes($real(cmp), this._isLegalChild), i = cols.length; --i >= 0;)
			for (var cns = zk.childNodes($e(cols[i], "cave")), j = cns.length; --j >= 0;) 
				this._initDrag(cns[j]);
	},
	_initDrag: function (n) {
		if (!n || !n.id) return;
		var header = $e(n, "caption");
		if (!header) return;
		this.drags[n.id] = new zDraggable(n, {
			handle: header,
			zindex: 12000,
			overlay: true,
			starteffect: zkPanel._startMove,
			ghosting: this._ghostMove, 
			ignoredrag: this._ignoreMove,
			endeffect: this._endMove,
			change: this._changeMove
		});
		header.style.cursor = "move";
		zk.disableSelection(header);
	},
	_getColWidths: function (cns) {
		var widths = [];
		cns.forEach(function (n) {
			widths.push(zk.revisedOffset(n)[0]);
		});
		return widths;
	},
	_getColHeights: function (cns) {
		var heights = [];
		cns.forEach(function (n) {
			var h = $real(n).offsetHeight;
			if (h)
				heights.push(zk.revisedOffset(n)[1] + h/2);
		});
		return heights;
	},
	_changeMove: function(dg, pointer, event){
		var xy = Event.pointer(event),
			cns = dg._cns,
			widths = dg._widths,
			cIndex = widths.length;
			
		for (; --cIndex >= 0;)
			if (widths[cIndex] <= xy[0]) break; 
		
		if (cIndex < 0) cIndex = 0;
		
		var panels = zk.childNodes($e(cns[cIndex], "cave"), zkPortalLayout._isLegalChild),
			heights = zkPortalLayout._getColHeights(panels),
			rIndex = 0,
			lenth = heights.length;
		
		while(rIndex < lenth) {
			if (heights[rIndex] > xy[1])
				break;
			rIndex++;
		}
		if (panels[rIndex])
			zk[rIndex < lenth ? 'insertBefore' : 'insertAfter']($e(zkau.getGhostOrgin(dg), "proxy"), panels[rIndex]);
		else $e(cns[cIndex], "cave").appendChild($e(zkau.getGhostOrgin(dg), "proxy"));
	},
	_ignoreMove: function (cmp, pointer, event) {
		return getZKAttr(cmp, "maximized") == "true" || zkPanel._ignoreMove(cmp, pointer, event);
	},
	_initProxy: function (cmp) {
		var proxy = document.createElement("DIV"),
			s = proxy.style,
			cs = cmp.style;
			
		s.width = "auto";
		proxy.id = $uuid(cmp) + "!proxy";
		s.marginTop = cs.marginTop;
		s.marginLeft = cs.marginLeft;
		s.marginRight = cs.marginRight;
		s.marginBottom = cs.marginBottom;
		zk.addClass(proxy, "z-panel-move-block");
		
		s.height = zk.revisedSize(proxy, cmp.offsetHeight, true) + "px";
		
		cmp.parentNode.insertBefore(proxy, cmp.previousSibling);
		action.hide(cmp);			
	},
	_cleanupProxy: function (cmp) {
		zk.remove($e(cmp, "proxy"));
		action.show(cmp);
	},
	_ghostMove: function (dg, ghosting, pointer) {
		if (ghosting) {
			var ofs = zkau.beginGhostToDIV(dg), title = zk.firstChild(dg.element, "DIV"),
				fakeT = title.cloneNode(true);
			var html = '<div id="zk_ddghost" class="z-panel-move-ghost" style="position:absolute;top:'
				+ofs[1]+'px;left:'+ofs[0]+'px;width:'
				+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
				+'px;z-index:'+dg.element.style.zIndex+'"><ul></ul></div></div>';
			document.body.insertAdjacentHTML("afterbegin", html);
			dg._zoffs = ofs;
			dg._cns = zk.childNodes($real($parentByType(dg.element.parentNode, "PortalLayout")), zkPortalLayout._isLegalChild);
			dg._widths = zkPortalLayout._getColWidths(dg._cns);
			zkPortalLayout._initProxy(dg.element);
			var h = dg.element.offsetHeight - title.offsetHeight;
			dg.element = $e("zk_ddghost");
			dg.element.firstChild.style.height = zk.revisedSize(dg.element.firstChild, h, true) + "px";
			dg.element.insertBefore(fakeT, dg.element.firstChild);
		} else {
			zkau.endGhostToDIV(dg);
		}
	},
	_endMove: function (cmp, evt) {
		var dg = zkPortalLayout.drags[cmp.id];
		if (!dg) return;
		var proxy = $e(dg.element, "proxy"),
			fromCol = $parentByType(dg.element, "PortalChildren"),
			toCol = $parentByType(proxy, "PortalChildren"),
			change = zk.nextSibling(dg.element, "DIV") != proxy;
		if (change) {
			proxy.parentNode.insertBefore(dg.element, proxy);
			zkau.sendasap({
				uuid: $uuid($parentByType(fromCol, "PortalLayout")),
				cmd: "onPortalMove", data: [fromCol.id, toCol.id, dg.element.id, zkPortalLayout.indexOf(proxy)]});
		}
		zkPortalLayout._cleanupProxy(dg.element);
		dg._cns = dg._columns = null;
	},
	indexOf: function (el) {
		return zk.childNodes(el.parentNode, this._isVisibleChild).indexOf(el);
	},
	_cleanupDrag: function (n) {
		if (!n) return;
		if (typeof n == 'object') n = n.id;
		var dg = this.drags[n];
		if (dg) {
			delete this.drags[n];
			dg.destroy();
		}
	},
	cleanup: function (cmp) {
		for (var cols = zk.childNodes($real(cmp), this._isLegalChild), i = cols.length; --i >= 0;)
			for (var cns = zk.childNodes($e(cols[i], "cave")), j = cns.length; --j >= 0;) 
				this._cleanupDrag(cns[j]);
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.childchg":
				if (val)
					zkPortalLayout.render(cmp, true);
				return true;
			case "z.reset":
				for (var id in this.drags) {
					this._cleanupDrag(id);
				}
				for (var cols = zk.childNodes($real(cmp), this._isLegalChild), i = cols.length; --i >= 0;)
					for (var cns = zk.childNodes($e(cols[i], "cave")), j = cns.length; --j >= 0;) 
						this._initDrag(cns[j]);
				return true;
		}
		return false;
	},
	_isLegalChild: function (n) {
		return n.id && $tag(n) == "DIV" && n.id.indexOf("!") == -1; 
	},
	_isVisibleChild: function (n) {
		return $tag(n) == "DIV" && zk.isVisible(n); 
	},
	render: function(cmp, broadcast) {
		if (!zk.isRealVisible(cmp)) 
			return;
		
		var w = zk.revisedSize(cmp, cmp.offsetWidth),
			h = zk.revisedSize(cmp, cmp.offsetHeight, true),
			total = w,
			real = $real(cmp),
			cns = zk.childNodes(real, this._isLegalChild);
			
		real.style.width = total + "px";
		
		cns.forEach(function (n) {
			if (n._width.endsWith("px") > 0)
				total -= ($int(n._width) + zk.getFrameWidth(n));
		});
		
		total = Math.max(0, total);
		
		cns.forEach(function (n) {
			if (n._width.indexOf("%") > 0) {
				n.style.width = (total ? Math.max(0, Math.floor($int(n._width) / 100 * total) - zk.getFrameWidth(n)) : 0) + "px";
				if (broadcast) zk.onSizeAt(n);
			}
		});
		
		zk.cleanVisibility(cmp);
	}
};
zkPortalLayout.onVisi = zkPortalLayout.onSize = zkPortalLayout.render;
if (zk.ie6Only) 
	zkPortalLayout.beforeSize = function (cmp) {
		$real(cmp).style.width = "0px";
	};

/**
 * The children of Portallayout.
 * @since 3.5.0
 */
zkPortalChildren = {
	init: function(cmp) {
		cmp._width = cmp.style.width;
		var cave = $e(cmp, "cave"),
			cns = zk.childNodes(cave);
		for (var i = cns.length; --i >= 0;) {
			zk.on(cns[i], "maximize", this.onChildMaximize);
			zk.on(cns[i], "onOuter", this.onChildOuter);
		}
	},
	onChildOuter: function (child) {
		zk.on(child, "maximize", zkPortalChildren.onChildMaximize);
	},
	onChildMaximize: function (child, maximized) {
		var cave = child.parentNode, 
			cns = zk.childNodes(cave);
		for (var i = cns.length; --i >= 0;) {
			if (cns[i] != child) {
				if (maximized) {
					cns[i].style.display = "none";
				} else if (getZKAttr(cns[i], "visible") == "true") {
					cns[i].style.display = "";
					var header = $e(cns[i], "caption");
					if (header)
						header.style.cursor = "move";
					if (getZKAttr(cns[i], "maximized") == "true") {
						zkPanel.maximize(cns[i], true, true);
					} else {
						// restore the size of the panel
						zk.beforeSizeAt(cns[i]);
						zk.onSizeAt(cns[i]);
					}
				}
			}
		}
		var header = $e(child, "caption");
		if (header)
			header.style.cursor = maximized ? "default" : "move";
	},
	cleanup: function(cmp) {
		if (cmp._width) cmp._width = null;
		for (var cns = zk.childNodes($e(cmp, "cave")), j = cns.length; --j >= 0;) 
			zkPortalLayout._cleanupDrag(cns[j]);
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "style.width":
			case "style":
			zkau.setAttr(cmp, nm, val);
			cmp._width = cmp.style.width;
			zkPortalLayout.render($parentByType(cmp, "PortalLayout"), true);
			return true;
		}
		return false;
	}
};