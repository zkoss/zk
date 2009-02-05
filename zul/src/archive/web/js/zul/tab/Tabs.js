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
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	getTabbox: function() {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.tab.Tabbox))
				return p;
		return null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabs" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : ""):
		this._zclass;
	},
	onSize: function() {
		var tabbox = this.getTabbox();
		if (tabbox.getNode())
			zDom.cleanVisibility(tabbox.getNode());
	},
	insertChildHTML_: function (child, before, desktop) {
		var cave = this.getSubnode("cave"),
			last = child.previousSibling;
		if (before) {
			zDom.insertHTMLBefore(before.getNode(), child._redrawHTML());
		} else {
			zDom.insertHTMLAfter(last.getNode(), child._redrawHTML());
		}
		child.bind_(desktop);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		this.$supers('unbind_', arguments);
	}
});