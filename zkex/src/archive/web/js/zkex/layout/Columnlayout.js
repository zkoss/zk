/* Columnlayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zkex.layout.Columnlayout = zk.$extends(zul.Widget, {
	getZclass: function(){
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-column-layout";
	},
	_isLegalChild: function (n) {
		return n.id && zDom.tag(n) == "DIV"; 
	},
	//the columnchild will call it
	render: _zkf = function(){
		var cmp = this.getNode();
		if (!zDom.isRealVisible(cmp)) 
			return;
			
		if(zk.ie6Only && cmp)
			this.getSubnode("cave").style.width = "0px";
			
		var w = zDom.revisedWidth(cmp, cmp.offsetWidth),
			h = zDom.revisedHeight(cmp, cmp.offsetHeight, true),
			total = w,
			cave = this.getSubnode("cave"),
			cns = zDom.firstChild(cave, "DIV");
			
		cave.style.width = total + "px";
		
		do{
			if (this._isLegalChild(cns) && cns._width.endsWith("px") > 0)
				total -= (zk.parseInt(cns._width) + zDom.padBorderWidth(cns));
		}while(cns = zDom.nextSibling(cns,"DIV"))
		
		total = Math.max(0, total);
		
		cns = zDom.firstChild(cave, "DIV")
		do{
			if (this._isLegalChild(cns) && cns._width.indexOf("%") > 0) {
				cns.style.width = (total ? Math.max(0, Math.floor(zk.parseInt(cns._width) / 100 * total) - zDom.padBorderWidth(cns)) : 0) + "px";
			}
		}while(cns = zDom.nextSibling(cns,"DIV"))
		zDom.cleanVisibility(cmp);
	
	},
	onSize: _zkf,
	onShow: _zkf,
	onChildAdded_: function(child){
		this.$supers('onChildAdded_', arguments);	
		this.render();
	},
	onChildRemoved_: function(child){
		this.$supers('onChildRemoved_', arguments);	
		this.render();
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	}
});


