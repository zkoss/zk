/* Portalchildren.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 15:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkmax.layout.Portalchildren = zk.$extends(zul.Widget, {
	getZclass: function(){
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-portal-children";
	},
	/*wait the panel to finish
	bind_: function() {
		this.$supers('bind_', arguments);
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
	},*/
	onChildAdded_: function(child){
		this.$supers('onChildAdded_', arguments);
		if(this.desktop && this.parent)
			this.parent._initDrag(child);
	},
	onChildRemoved_: function(child){
		this.$supers('onChildRemoved_', arguments);	
		if(this.desktop && this.parent)
			this.parent._cleanupDrag(child.uuid);
	}
});