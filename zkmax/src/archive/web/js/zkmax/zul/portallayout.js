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
	_getColumnBoxes: function (cns) {
		var cols = [];
		for (var i = 0, j = cns.length; i < j; i++) {
			cols.push({
				x: zk.revisedOffset(cns[i])[0],
				w: $real(cns[i]).offsetWidth
			});
		}
		return cols;
	},
	_changeMove: function(dg, pointer){
		var cns = dg._cns;
			
		if (!dg._columns)
			dg._columns = zkPortalLayout._getColumnBoxes(cns);
		
		var col = 0,
			cols = dg._columns,
			match;
		for (var i = cols.length; col < i; col++) {
			if ((cols[col].x + cols[col].w) > pointer[0]) {
				match = true;
				break;
			}
		}
		
		if (!match)
			col--;
		
		var p;
		match = false;		
		for (var panels = zk.childNodes($e(cns[col], "cave"), zkPortalLayout._isLegalChild), i = 0, j = panels.length;
			i < j; i++) {
			p = panels[i];
			var h = p.offsetHeight;
			if (h && (zk.revisedOffset(p)[1] + (h / 2)) > pointer[1]) {
				match = true;
				break;
			}
		}
				
		if (p) {
			p.parentNode.insertBefore($e(zkau.getGhosted(dg), "proxy"), match ? p : null);
		} else {
			$e(cns[col], "cave").insertBefore($e(zkau.getGhosted(dg), "proxy"), null);
		}
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
	_ghostMove: function (dg, ofs, evt) {
		var node = dg.node,
			title = zk.firstChild(node, "DIV"),
			fakeT = title.cloneNode(true);
		var html = '<div id="zk_ddghost" class="z-panel-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(node)+'px;height:'+zk.offsetHeight(node)
			+'px;z-index:'+node.style.zIndex+'"><ul></ul></div></div>';
		document.body.insertAdjacentHTML("afterbegin", html);
		dg._zoffs = ofs;
		dg._cns = zk.childNodes($real($parentByType(node.parentNode, "PortalLayout")), zkPortalLayout._isLegalChild);
		zkPortalLayout._initProxy(node);
		var h = node.offsetHeight - title.offsetHeight;

		node = $e("zk_ddghost");
		node.firstChild.style.height = zk.revisedSize(node.firstChild, h, true) + "px";
		node.insertBefore(fakeT, node.firstChild);
		return node;
	},
	_endMove: function (cmp, evt) {
		var dg = zkPortalLayout.drags[cmp.id];
		if (!dg) return;
		var node = dg.node,
			proxy = zDom.$(node, "proxy"),
			fromCol = $parentByType(node, "PortalChildren"),
			toCol = $parentByType(proxy, "PortalChildren"),
			change = zk.nextSibling(node, "DIV") != proxy;
		if (change) {
			proxy.parentNode.insertBefore(node, proxy);
			zkau.sendasap({
				uuid: $uuid($parentByType(fromCol, "PortalLayout")),
				cmd: "onPortalMove", data: [fromCol.id, toCol.id, node.id, zkPortalLayout.indexOf(proxy)]});
		}
		zkPortalLayout._cleanupProxy(node);
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
		
		var w = cmp.offsetWidth - zk.getFrameWidth(cmp),
			h = cmp.offsetHeight - zk.getFrameHeight(cmp),
			pw = w,
			inner = $real(cmp),
			cns = zk.childNodes(inner, this._isLegalChild);
			
		inner.style.width = w + "px";
		
		for (var i = 0, j = cns.length; i < j; i++) {			
			var widx = cns[i]._width.indexOf("px");
			if (widx > 0) {
				var px_width = $int(cns[i]._width.substring(0, widx));
				pw -= (px_width + zk.getFrameWidth(cns[i]));
			}
		}
		
		pw = pw < 0 ? 0 : pw;
		
		for (var i = 0, j = cns.length; i < j; i++) {		
			var widx = cns[i]._width.indexOf("%");
			if (widx > 0) {
				var percentage_width = $int(cns[i]._width.substring(0, widx));
				var result = (Math.floor(percentage_width / 100 * pw) - zk.getFrameWidth(cns[i]));
				cns[i].style.width = (result > 0 ? result : 0) + "px";
				if (broadcast) zk.onSizeAt(cns[i]);
			}
		}
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