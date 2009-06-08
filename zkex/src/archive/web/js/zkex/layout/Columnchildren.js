/* Columnchildren.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.layout.Columnchildren = zk.$extends(zul.Widget, {
    /** TODO the panel child not implement the fire maximize yet
	onChildOuter: function (child) {
		this.listen()
		zk.on(child, "maximize", this.onChildMaximize);
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
						zul.wnd.Panel.maximize(cns[i], true, true);
					} else {
						// restore the size of the panel
						zk.beforeSizeAt(cns[i]);
						zk.onSizeAt(cns[i]);
					}
				}
			}
		}
	},*/
	getZclass: function(){
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-column-children";
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments);
		var n = this.getNode();
		n._width = n.style.width;
		/*
		var cave = this.getSubnode("cave"),
			cns = cave;
		do{
			zWatch.listen({onChildMaximize: cns, onChildOuter: cns});
		}while(cns = zDom.nextSibling(cns,"DIV"))*/
	},
	/*unbind_: function () {
		var cave = this.getSubnode("cave"),
			cns = cave;
		do{
			zWatch.unlisten({maximize: cns, onOuter: cns});
		}while(cns = zDom.nextSibling(cns,"DIV"));
		this.$supers('unbind_', arguments);
	},*/
	setWidth: function () {
		this.$supers('setWidth', arguments);
		var n;
		if(n = this.getNode())
			n._width = n.style.width;
		var p;
		if(p = this.parent)
			p.render();
	}
});

//getter and setter part

