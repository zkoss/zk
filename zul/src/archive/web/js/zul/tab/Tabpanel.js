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
		if (!tabbox.inAccordionMold()) {
			var tbx = tabbox.getNode(),
				tabpanels = this.parent.getNode(),
				hgh = jq(tbx).css("height");
			if (tabpanels) {
				for (var pos, n = tabpanels.firstChild; n; n = n.nextSibling) {
					if (n.id) {
						if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary.
							if (n.style.position)
								pos = n.style.position;
							n.style.position = "relative";
						}
						if (hgh && hgh != "auto") {//tabbox has height
							hgh = jq(tabpanels).zk.vflexHeight();
							jq(n).zk.setOffsetHeight(hgh);
						}
						//let real div 100% height
						jq(this.getSubnode("real")).addClass(this.getZclass() + "-cnt");
						if (zk.ie && pos)
							n.style.position = pos;
					}
				}
			}
		}
	},
	onVisi: function() {
		this._fixPanelHgh();		//Bug 2104974
		if (zk.ie) zk.redoCSS(tbx); //Bug 2526699 - (add zk.ie7)
	},
	bind_: function() {
		this.$supers('bind_', arguments);
		zk.afterMount(
			this.proxy(function () {
				this._fixPanelHgh();
			})
		);

	}

});