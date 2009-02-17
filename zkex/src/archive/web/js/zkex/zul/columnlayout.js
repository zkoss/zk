/* columnlayout.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 10:56:21 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/**
 * A columnlayout lays out a container which can have multiple columns, and each
 * column may contain one or more panel.
 * @since 3.5.0
 */
zkColumnLayout = {	
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.childchg" :
				if (val) {
					zkColumnLayout.render(cmp, true);
				}
				return true;
		}
		return false;
	},
	_isLegalChild: function (n) {
		return n.id && $tag(n) == "DIV"; 
	},
	render: function(cmp, broadcast){
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
zkColumnLayout.onVisi = zkColumnLayout.onSize = zkColumnLayout.render;
if (zk.ie6Only) 
	zkColumnLayout.beforeSize = function (cmp) {
		$real(cmp).style.width = "0px";
	};

/**
 * The column of Columnlayout.
 * @since 3.5.0
 */
zkColumnChildren = {
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
		zk.on(child, "maximize", zkColumnChildren.onChildMaximize);
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
	},
	cleanup: function(cmp) {
		if (cmp._width) cmp._width = null;
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "style.width":
			case "style":
			zkau.setAttr(cmp, nm, val);
			cmp._width = cmp.style.width;
			zkColumnLayout.render($parentByType(cmp, "ColumnLayout"), true);
			return true;
		}
		return false;
	}
};