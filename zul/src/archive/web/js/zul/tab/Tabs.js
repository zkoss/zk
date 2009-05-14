/* Tabs.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:43 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabs = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabs" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : ""):
		this._zclass;
	},
	onSize: _zkf = function () {
		var tabbox = this.getTabbox();
		if (tabbox.getNode())
			zDom.cleanVisibility(tabbox.getNode());
	},
	onShow: _zkf, 
	insertChildHTML_: function (child, before, desktop) {
		var last = child.previousSibling;
		if (before || !last) {
			zDom.insertHTMLBefore(before.getNode(), child._redrawHTML());
		} else {
			zDom.insertHTMLAfter(last.getNode(), child._redrawHTML());
		}
		child.bind(desktop);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen("onSize", this);
		zWatch.listen("onShow", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onShow", this);
		this.$supers('unbind_', arguments);
	}
});