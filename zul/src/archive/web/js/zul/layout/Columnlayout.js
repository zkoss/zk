/* Columnlayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zul.layout.Columnlayout = zk.$extends(zul.Widget, {
	beforeChildAdded_: function(child, refChild){
		if (!child.$instanceof(zul.layout.Columnchildren))
			alert("Unsupported child for Columnlayout: "+ child.className);
		else
			this.$supers('beforeChildAdded_', arguments);
	},
	getZclass: function(){
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-column-layout";
	},
	_isLegalChild: function (n) {
		return n.id && zDom.tag(n) == "DIV"; 
	},
	render: _zkf = function(n){
		var cmp = this.getNode();
		if (!zDom.isRealVisible(cmp)) 
			return;
			
		if(zk.ie6Only && this.getNode())
			this.getSubnode("real").style.width = "0px";
			
		var w = zDom.revisedWidth(cmp, cmp.offsetWidth),
			h = zDom.revisedHeight(cmp, cmp.offsetHeight, true),
			total = w,
			real = this.getSubnode("real"),
			cns = zDom.firstChild(real, "DIV");
			
		real.style.width = total + "px";
		
		do{
			if (this._isLegalChild(cns) && cns._width.endsWith("px") > 0)
				total -= (zk.parseInt(cns._width) + zDom.padBorderWidth(cns));
		}while(cns = zDom.nextSibling(cns,"DIV"))
		
		total = Math.max(0, total);
		
		cns = zDom.firstChild(real, "DIV")
		do{
			if (this._isLegalChild(cns) && cns._width.indexOf("%") > 0) {
				cns.style.width = (total ? Math.max(0, Math.floor(zk.parseInt(cns._width) / 100 * total) - zDom.padBorderWidth(cns)) : 0) + "px";
			}
		}while(cns = zDom.nextSibling(cns,"DIV"))
		zDom.cleanVisibility(cmp);
	
	},
	insertChildHTML_: function (child, before, desktop) {
 		var bfn, ben;
 		if (before) {
 			bfn = before._getBeforeNode();
 			if (!bfn) before = null;
 		}
 		if(!before)
 			for (var w = this;;) {
 				ben = w.getSubnode("real");
 				if (ben) break;
				var w2 = w.nextSibling;
 				if (w2) {
 					bfn = w2._getBeforeNode();
 					if (bfn) break;
 				}
 				if (!(w = w.parent)) {
 					ben = document.body;
 					break;
 				}
 			}
		if (bfn)
 			zDom.insertHTMLBefore(bfn, child._redrawHTML());
 		else
 			zDom.insertHTMLBeforeEnd(ben, child._redrawHTML());
 		child.bind_(desktop);
 	},
	onSize: _zkf,
	onShow: _zkf,
	onChildAdded_: function(child){
		this.$supers('onChildAdded_', arguments);	
		this.render(this.getNode());
	},
	onChildRemoved_: function(child){
		this.$supers('onChildRemoved_', arguments);	
		this.render(this.getNode());
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		zWatch.listen('onSize', this);
		zWatch.listen('onShow', this);
	},
	unbind_: function () {
		zWatch.unlisten('onSize', this);
		zWatch.unlisten('onShow', this);
		this.$supers('unbind_', arguments);
	}
});


