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
	
    init: function(cmp){

    },
    
    onSize: function(cmp){
		zkColumnLayout.render(cmp, true);
    },
	
	onVisi: function(cmp){
		zkColumnLayout.render(cmp, true);
    },
	
    childchg: function(cmp){
        zkColumnLayout.render(cmp);
    },
	
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.resize" :
			zkColumnLayout.render(cmp);
			return true;
		}
		return false;
	},
	
    render: function(cmp, isOnSize){
        cmp.isOnSize = isOnSize;
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
                child.style.width = result > 0 ? result + "px" : "0px";
            }
        }
    },
	
    _paserMargin: function(val){
        var ms = val.split(",");
        return "" + $int(ms[0]) + "px " + $int(ms[2]) + "px " + $int(ms[3]) + "px " + $int(ms[1]) + "px;";
    }
};

zkColumnChildren = {
	init: function(cmp){
		bodyEl = $e($uuid(cmp) + "!cave");	
        cmp = $real(cmp);
		cmp._width = cmp.style.width;
		
		var margin = zkColumnLayout._paserMargin(getZKAttr(cmp, "mars") || "0,0,0,0");
		bodyEl.style.padding = margin;
	},
	
	cleanup: function(cmp){
        cmp = $real(cmp);
        var layout = $parentByType(cmp, "ColumnLayout");
        cmp.bodyEl = null;
        if (layout) {
            zk.beforeSizeAt(layout);
            zk.onSizeAt(layout);
        }
	},
	
	setAttr: function(cmp, nm, val){
		cmp = $real(cmp);
		switch (nm) {
			case "z.cid" :
				setZKAttr(cmp, "cid", val); 
				return true;
			case "z.mars" :
				setZKAttr(cmp, "mars", val); 
				var layout = $parentByType(cmp, "ColumnLayout");
				if (layout) layout.render();
				return true;
			case "style.height" :
				cmp.style["height"] = val;
				cmp._height = null;
				return true;				
			case "style.width" :
				cmp.style["width"] = val;			
				cmp._width = null;
				return true;				
		}
		return false;
	}
};