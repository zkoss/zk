/* Tabpanel.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:33:02 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabpanel = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	isVisible: function() {
		return this.$supers('isVisible', arguments) && this.isSelected();
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanel" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	getLinkedTab: function() {
		var tabbox =  this.getTabbox(),
			tabs = tabbox.getTabs(),
			index = this.getIndex();
		return tabs ? tabs.getChildAt(index) : null;
	},
	getIndex:function() {
		return this.getChildIndex();
	},
	isSelected: function() {
		var tab = this.getLinkedTab();
		return tab != null && tab.isSelected();
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox();
		if (!tabbox.inAccordionMold()) {
			var tbx = tabbox.$n(),
				n = this.$n(),
				hgh = tbx.style.height,
				pos;
			
			if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary.
				if (n.style.position)
					pos = n.style.position;
				n.style.position = "relative";
			}
			if (hgh && hgh != "auto") {//tabbox has height
				hgh = zk(n.parentNode).vflexHeight();
				zk(n).setOffsetHeight(hgh);
			}
			//let real div 100% height
			jq(this.$n("real")).addClass(this.getZclass() + "-cnt");
			if (zk.ie && pos)
				n.style.position = pos;
		}
	},
	onSize: _zkf = function() {
		this._fixPanelHgh();		//Bug 2104974
		if (zk.ie) zk(this.getTabbox().$n()).redoCSS(); //Bug 2526699 - (add zk.ie7)
	},
	onShow: _zkf,
	bind_: function() {
		this.$supers('bind_', arguments);
		if (this.getTabbox().isHorizontal())
			zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	}

});