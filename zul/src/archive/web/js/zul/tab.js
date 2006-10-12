/* tab.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 17:12:50     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
////
// tab //
function zkTab() {};

/** Selects the specified tab. */
zkTab.onclick = function (evt) {
	if (!evt) evt = window.event;
	var tab = zkau.getParentByType(Event.element(evt), "Tab");
	if (!zkTab._sliding(tab)) //Bug 1571408
		zkTab.selTab(tab);
};

/** Returns the selected tab by giving any tab as the reference point. */
zkTab._getSelTab = function (tab) {
	var tabboxId = getZKAttr(tab, "box");
	if (tabboxId) {
		var tabbox = $e(tabboxId);
		if (getZKAttr(tabbox, "accd") == "true")
			return zkTab._getSelTabFromTop(tabbox, tabboxId);
	}

	for (var node = tab; (node = node.nextSibling) != null;)
		if (getZKAttr(node, "sel") == "true")
			return node;
	for (var node = tab; (node = node.previousSibling) != null;)
		if (getZKAttr(node, "sel") == "true")
			return node;
};
/** Whether any tab is sliding. */
zkTab._sliding = function (tab) {
	var tabboxId = getZKAttr(tab, "box");
	if (!tabboxId) return false;

	var tabbox = $e(tabboxId);
	if (getZKAttr(tabbox, "accd") != "true")
		return false;

	for (var node = tab; (node = node.nextSibling) != null;)
		if (getZKAttr(node, "sliding"))
			return true;
	for (var node = tab; (node = node.previousSibling) != null;)
		if (getZKAttr(node, "sliding"))
			return true;
	return false;
};
/** Returns the selected tab by specified any HTML tag containing it. */
zkTab._getSelTabFromTop = function (node, tabboxId) {
	if (zk.getCompType(node) == "Tab"
	&& getZKAttr(node, "box") == tabboxId)
		return getZKAttr(node, "sel") == "true" ? node: null;

	for (var node = node.firstChild; node != null; node = node.nextSibling) {
		var n = zkTab._getSelTabFromTop(node, tabboxId);
		if (n) return n;
	}
	return null;
};

/** Selects the specified tab (and unselect the current tab). */
zkTab.selTab = function (tab) {
	if (!tab) return;
	tab = $e(tab);

	var old = zkTab._getSelTab(tab);
	if (old != tab) {
		if (old) zkTab._setTabSel(old, false);
		zkTab._setTabSel(tab, true);
	}
};

/** Selects o unselect the specified tab. */
zkTab._setTabSel = function (tab, toSel) {
	if ((getZKAttr(tab, "sel") == "true") == toSel)
		return; //nothing changed

	setZKAttr(tab, "sel", toSel ? "true": "false");
	var tabreal = $e(tab.id + "!real");
	if (tabreal) {
		if (toSel) {
			tabreal.className = tabreal.className + "sel";
		} else {
			var len = tabreal.className.length;
			if (len > 3)
				tabreal.className = tabreal.className.substring(0, len - 3);
		}
	}

	zkTab._changeBkgnd(tab, toSel);

	var tabbox = getZKAttr(tab, "box");
	if (tabbox) tabbox = $e(tabbox);
	var accd = tabbox && getZKAttr(tabbox, "accd") == "true";
	var panel = $e(getZKAttr(tab, "panel"));
	if (panel)
		if (accd) action.slideDown($e(panel.id + "!real"), toSel);
		else action.show(panel, toSel);

	if (!accd) {
		var tabs = zk.parentNode(zk.parentNode(tab, "TABLE"), "THEAD");
		if (tabs)
			zkTab.fixWidth(tabs.id + "!last");
	}

	if (toSel && tabbox)
		zkau.send({uuid: tabbox.id, cmd: "onSelect", data: [tab.id]},
				zkau.asapTimeout(tabbox, "onSelect"));
};
/** Changes the images in the background. */
zkTab._changeBkgnd = function (node, toSel) {
	if (node.style) {
		var url = node.style.backgroundImage;
		if (url && url.indexOf(toSel ? "-uns": "-sel") >= 0)
			node.style.backgroundImage = zk.renType(url, toSel ? "sel": "uns");
	}

	for (node = node.firstChild; node; node = node.nextSibling)
		zkTab._changeBkgnd(node, toSel);
};

/** Fix the width of the last column in tabs. */
zkTab.fixWidth = function (uuid) {
	var n = $e(uuid);
	if (!n) return;

	var tbl = zk.parentNode(zk.parentNode(n, "TABLE"), "TABLE");
	var tabs = zk.parentNode(tbl, "TABLE");
		//Safari: THEAD's width and TD/TR's height is 0, so use TABLE instead
	if (tabs) {
		if ("TD" == zk.tagName(n)) { //horizontal
			var v = tabs.offsetWidth - tbl.offsetWidth + n.offsetWidth;
			if (v < 0) v = 0;
			n.style.width = v + "px";
		} else { //vertical
			if (n.cells && n.cells.length) n = n.cells[0];
			var v = tabs.offsetHeight - tbl.offsetHeight + n.offsetHeight;
			if (v < 0) v = 0;
			n.style.height = v + "px";
		}
	}
};

zkTab.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {zkTab.onclick(evt);});

	var anc = $e(cmp.id + "!a");
	if (anc) {
		Event.observe(anc, "focus", function () {zkau.onfocus(anc);});
		Event.observe(anc, "blur", function () {zkau.onblur(anc);});
	}
};

////
// tabs //
function zkTabs() {}

zkTabs.init = function (cmp) {
	setTimeout("zkTab.fixWidth('"+cmp.id+"!last')", 30);
};
zkTabs.onVisi = function (cmp) {
	zkTabs.init(cmp);
};
