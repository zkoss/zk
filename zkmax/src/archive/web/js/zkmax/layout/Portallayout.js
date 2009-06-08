/* Portallayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 15:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkmax.layout.Portallayout = zk.$extends(zul.Widget, {
	drags: {},
	getZclass: function(){
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-portal-layout";
	},
	bind_: function() {
		this.$supers('bind_', arguments); 
		zWatch.listen({onSize: this, onShow: this});
		
		for(var portalChildren = this.firstChild; portalChildren; portalChildren = portalChildren.nextSibling)
			for(var panel = portalChildren.firstChild; panel; panel = panel.nextSibling)
				this._initDrag(panel);
			
	},
	unbind_: function (cmp) {
		this._cleanupDrag();			
		zWatch.unlisten({onSize: this, onShow: this});
		
		for(var portalChildren = this.firstChild; portalChildren; portalChildren = portalChildren.nextSibling)
			for(var panel = portalChildren.firstChild; panel; panel = panel.nextSibling)
				this._cleanDrag(panel.uuid);
		this.$supers('unbind_', arguments);
	},
	_initDrag: function (panel) {
		var n = panel.getNode();
		
		if(this.drags[n.id]) return;
		
		var header = panel.getSubnode("cap"); 
		if (!header) return;
		this.drags[n.id] = new zk.Draggable(panel, n , {
			handle: header,
			zindex: 12000,
			stackup: true,
			starteffect: zul.wnd.Panel._startmove,
			ghosting: this._ghostMove, 
			ignoredrag: this._ignoreMove,
			endeffect: this._endMove,
			change: this._changeMove
		});
		header.style.cursor = "move";
		zDom.disableSelection(header);
	},
	_cleanupDrag: function (id) {
		if(this.drags[id]){
			this.drags[id].destroy();
			this.drags[id] = null;
		}
	},
	_getColWidths: function () {
		var len = this.nChildren,
			widths = [];
		for(var i = 0; i<len; i++ )
			widths.push(zDom.revisedOffset(this.getChildAt(i).getNode())[0]);
		
		return widths;
	},
	_getColHeights: function (widget) {
		var len = widget.nChildren;
		var heights = [];
		for(var i = 0; i < len; i++){
			var panel = widget.getChildAt(i),
				n = panel.getNode();			
			heights.push(zDom.revisedOffset(n)[1] + n.offsetHeight/2);
		}
		return heights;
	},
	_changeMove: function(dg, pointer, event){
		var panel = dg.control,
			portalchild = panel.parent,
			portallayout = portalchild.parent,
			proxyNode = zDom.$(panel.uuid + "$proxy");
		
		var widths = dg._widths,
			cIndex = widths.length,
			xy = zEvt.pointer(event);

			
		for (; --cIndex >= 0;)
			if (widths[cIndex] <= xy[0]) break; 
		
		if (cIndex < 0) cIndex = 0;
		
		var dstPortalchildren = portallayout.getChildAt(cIndex),
			dstPanel = dstPortalchildren.firstChild,
			heights = portallayout._getColHeights(dstPortalchildren),
			rIndex = 0,
			lenth = heights.length;
		
		while(rIndex < lenth) {
			if (heights[rIndex] > xy[1])
				break;
			rIndex++;
		}
		if (dstPanel = dstPortalchildren.getChildAt(rIndex))
			zDom[rIndex < lenth ? 'insertBefore' : 'insertAfter'](proxyNode, dstPanel.getNode());
		else 
			dstPortalchildren.getSubnode("cave").appendChild(proxyNode);
	},
	_ignoreMove: function (cmp, pointer, event) {
		//TODO wait the panel to be finished , the maximized funtion
		//return getZKAttr(cmp, "maximized") == "true" || zul.wnd.Panel._ignoremove(cmp, pointer, event);
		return zul.wnd.Panel._ignoremove(cmp, pointer, event);
	},
	_initProxy: function (cmp) {
		var proxy = document.createElement("DIV"),
			s = proxy.style,
			cs = cmp.style;
			
		s.width = "auto";
		proxy.id = cmp.id + "$proxy";
		s.marginTop = cs.marginTop;
		s.marginLeft = cs.marginLeft;
		s.marginRight = cs.marginRight;
		s.marginBottom = cs.marginBottom;
		zDom.addClass(proxy, "z-panel-move-block");
		
		s.height = zDom.revisedHeight(proxy, cmp.offsetHeight, true) + "px";
		
		cmp.parentNode.insertBefore(proxy, cmp.previousSibling);
		zDom.hide(cmp);			
	},
	_cleanupProxy: function (cmp) {
		zDom.remove(zDom.$(cmp.id+"$proxy"));
		zDom.show(cmp);
	},
	_ghostMove: function (dg, ofs, evt) {
		var widget = dg.control.parent.parent;
		dg.node = zk.Widget.$(dg.handle.id.split("$")[0]).getNode();
		var top = zDom.firstChild(dg.node, "DIV"),
		header = zDom.nextSibling(top, 'DIV'),
		fakeT = top.cloneNode(true),
		fakeH = header.cloneNode(true);
		var html = '<div id="zk_ddghost" class="z-panel-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(dg.node)+'px;height:'+zDom.offsetHeight(dg.node)
			+'px;z-index:'+dg.node.style.zIndex+'"><dl></dl></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		dg._zoffs = ofs;
		dg._cns = widget.firstChild.getNode();
		dg._widths = widget._getColWidths();
		widget._initProxy(dg.node);
		var h = dg.node.offsetHeight - top.offsetHeight - header.offsetHeight;
		dg.node = zDom.$("zk_ddghost");
		dg.node.firstChild.style.height = zDom.revisedHeight(dg.node.firstChild, h, true) + "px";
		dg.node.insertBefore(fakeT, dg.node.firstChild);
		dg.node.insertBefore(fakeH, dg.node.lastChild);
		return dg.node;
	},
	_endMove: function (dg, evt) {
		var panel = dg.control,
			panelNode = panel.getNode(),
			fromCol = panel.parent,
			proxy = zDom.$(panelNode.id + "$proxy"), 
			toCol = zk.Widget.$(proxy.parentNode.id.split("$")[0]),
			change = zDom.nextSibling(dg.node, "DIV") != proxy,
			portallayout = panel.parent.parent;
		if (change) {
			if(proxy.nextSibling){
				var nextSibling = zk.Widget.$(proxy.nextSibling.id.split("$")[0]);
				toCol.insertBefore(panel, nextSibling);
			}else{
				toCol.appendChild(panel);
			}
			
			for(var i = 0; i < toCol.nChildren; i++){
				var relatedPanel = toCol.getChildAt(i);
				if(relatedPanel.getMaximized())
					relatedPanel.maximize;//TODO wait for the panel to be finished
			}
			portallayout.fire("onPortalMove", {from:fromCol.uuid,to:toCol.uuid,dragged:panel.uuid,index:portallayout._indexOf(proxy)},{toServer:true},0);
		}
		portallayout._cleanupProxy(panel.getNode());
		dg._cns = dg._columns = null;
	},
	_indexOf: function(el){
		var parent = el.parentNode,
			child = parent.firstChild,
			index = 0;
		while(child != el){
			index++;
			child = child.nextSibling;
		}
		return index;
	},
	getPanel: function(col, row){
		if(col < 0 || row < 0 || this.nChildren <= col) return null;
		var child = this.getChildAt(col);
		
		if(child.nChildren <= row) return null;
		
		return child.getChildAt(row);
	},
	setPanel: function(panel, col, row){
		if(col < 0 || row < 0 || panel==null || this.nChildren <= col) return false;
		var child = this.getChildAt(col);
		
		if(child.nChildren >= row) 
			return child.appendChild(panel);
		else
			return child.insertBefore(panel, child.getChildAt(row));
	},
	getPosition: function(panel){
		var pos = [-1, -1],
			portalchildren = panel.parent;
		
		if(panel == null || portalchildren == null) return pos;
		
		for(var i = 0; i < portalchildren.nChildren; i++){
			if(panel == portalchildren.getChildAt(i))
				pos[1]=i;
		}
		
		for(var i = 0; i < this.nChildren; i++){
			if(portalchildren == this.getChildAt(i))
				pos[0]=i;
		}
		return pos;
	},
	_isLegalChild: function (n) {
		return n.id && zDom.tag(n) == "DIV";  
	},
	_isVisibleChild: function (n) {
		return zDom.tag(n) == "DIV" && zDom.isVisible(n); 
	},
	onChildAdded_: function(child){
		this.$supers('onChildAdded_', arguments);
		
		if(this.desktop)
			for(var panel = child.firstChild; panel; panel = panel.nextSibling)
				if(panel.getNode())
					this._initDrag(panel);
		
		this.render();
	},
	onChildRemoved_: function(child){
		this.$supers('onChildRemoved_', arguments);
		if(this.desktop)
			for(var panel = child.firstChild; panel; panel = panel.nextSibling)
				this._cleanupDrag(panel.uuid);
		this.render();
	},
	render: _zkf = function() {
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
			var oriWidth = zk.Widget.$(cns.id)._oriWidth;
			if (this._isLegalChild(cns) && oriWidth.endsWith("px") > 0)
				total -= (zk.parseInt(oriWidth) + zDom.padBorderWidth(cns));
		}while(cns = zDom.nextSibling(cns,"DIV"))
		
		total = Math.max(0, total);
		
		cns = zDom.firstChild(cave, "DIV")
		do{
			var oriWidth = zk.Widget.$(cns.id)._oriWidth;
			if (this._isLegalChild(cns) && oriWidth.indexOf("%") > 0) {
				cns.style.width = (total ? Math.max(0, Math.floor(zk.parseInt(oriWidth) / 100 * total) - zDom.padBorderWidth(cns)) : 0) + "px";
			}
		}while(cns = zDom.nextSibling(cns,"DIV"))
		zDom.cleanVisibility(cmp);
	},
	onSize: _zkf,
	onVisi: _zkf,
	setWidth: function(){
		this.$supers('setWidth', arguments);
		this.onSize();
	}
});