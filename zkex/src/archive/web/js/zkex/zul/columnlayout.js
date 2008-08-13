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
				if (val)
					zkColumnLayout.render(cmp);
				return true;
		}
		return false;
	},
	_isLegalChild: function (n) {
		return n.id && $tag(n) == "DIV"; 
	},
	render: function(cmp){
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
			}
		}
		cmp.style.visibility = "inherit";
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
		}
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
		var layout = $parentByType(cmp, "ColumnLayout");
		if (layout) {
			zk.beforeSizeAt(layout);
			zk.onSizeAt(layout);
		}
	}
};