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

zkColumnLayout = {
	
	init: function(cmp) {
		zkColumnLayout.onVisi = zkColumnLayout.onSize;
	},
	
	onSize: function(cmp) {
		zkColumnLayout.render(cmp);
    },
	
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.childchg" :
				if (val)
					zkColumnLayout.render(cmp);
				return true;
		}
		return false;
	},
	render: function(cmp){
		if (!zk.isRealVisible(cmp)) 
			return;
		
		var w = cmp.offsetWidth - zk.getFrameWidth(cmp),
			h = cmp.offsetHeight - zk.getFrameHeight(cmp),
			pw = w;
		
		var inner = $real(cmp);
		inner.style.width = w + "px";
		
		var child;
		
		for (var i = 0, j = inner.childNodes.length; i < j; i++) {
			if ($tag(inner.childNodes[i]) != "DIV" || inner.childNodes[i].id == "") 
				continue;
			child = $real(inner.childNodes[i]);
			
			var widx = child._width.indexOf("px");
			if (widx > 0) {
				var px_width = $int(child._width.substring(0, widx));
				pw -= (px_width + zk.getFrameWidth(child));
			}
		}
		
		pw = pw < 0 ? 0 : pw;
		
		for (var i = 0, j = inner.childNodes.length; i < j; i++) {
			if ($tag(inner.childNodes[i]) != "DIV" || inner.childNodes[i].id == "") 
				continue;
			child = $real(inner.childNodes[i]);
			
			var widx = child._width.indexOf("%");
			if (widx > 0) {
				var percentage_width = $int(child._width.substring(0, widx));
				var result = (Math.floor(percentage_width / 100 * pw) - zk.getFrameWidth(child));
				child.style.width = (result > 0 ? result : 0) + "px";
			}
		}
		cmp.style.visibility = "visible";
	}
};
if (zk.ie6Only) 
	zkColumnLayout.beforeSize = function (cmp) {
		$real(cmp).style.width = "0px";
	};
zkColumnChildren = {
	init: function(cmp){
		cmp = $real(cmp);
		cmp._width = cmp.style.width;
	},
	
	cleanup: function(cmp){
		cmp = $real(cmp);
		var layout = $parentByType(cmp, "ColumnLayout");
		if (layout) {
			zk.beforeSizeAt(layout);
			zk.onSizeAt(layout);
		}
	}
};