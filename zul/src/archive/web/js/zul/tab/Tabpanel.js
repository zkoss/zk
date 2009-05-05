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
	bind_: function() {
		this.$supers('bind_', arguments);
		this._fixPanelHgh();
	},
	getLinkedTab: function() {
		var tabbox =  this.getTabbox(),
			tabs = tabbox.getTabs(),
			index = this.getIndex();
		return tabs.getChildAt(index);
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
		//@TODO Fix
		/*if (!tabbox.inAccordionMold()) {
			var hgh = zDom.getStyle(tabbox.getNode(),"height");
			var panels = this.parent;
			if (panels) {
                for (var pos, n = panels.firstChild; n; n = n.nextSibling) {
                    if (n.id) {
                        if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary.
                            pos = n.style.position;
                            n.style.position = "relative";
                        }
                        if (hgh && hgh != "auto") {//tabbox has height
                            hgh = zDom.vflexHeight(panels);
                            zDom.revisedHeight(n, hgh);
                        }
                        //let real div 100% height
                        zk.log(zDom.$(this.uuid, "$real"));
                        zDom.addClass(zDom.$(this.uuid, "$real"), this.getZclass() + "-cnt");
                        if (zk.ie)
                            n.style.position = pos;
                    }
                }
			}
		}*/
	}

});