/* tab.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 17:12:50     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
////
// tabbox //
zkTabbox = {};

zkTabbox.setAttr = function (cmp, name, value) {
	switch (name) {
	case "style":
	case "style.width":
	case "style.height":
		var uuid = getZKAttr(cmp, "tabs");
		if (uuid) {
			zkau.setAttr(cmp, name, value);
			zkTabs.fixWidth(uuid);
		}
		return true;
	}
	return false;
};

////
// tab //
zkTab = {};

/** Selects the specified tab. */
zkTab.onclick = function (evt) {
	if (!evt) evt = window.event;
	var tab = $parentByType(Event.element(evt), "Tab");
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

	//non-accordion: we can use sibling directly
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

	//accordion: we must go to panel firs, and then browse its sibling
	var panel = $e(getZKAttr(tab, "panel"));
	for (var node = panel; (node = node.nextSibling) != null;)
		if (getZKAttr($real(node), "sliding"))
			return true;
	for (var node = panel; (node = node.previousSibling) != null;)
		if (getZKAttr($real(node), "sliding"))
			return true;
	return false;
};
/** Returns the selected tab by specified any HTML tag containing it. */
zkTab._getSelTabFromTop = function (node, tabboxId) {
	if ($type(node) == "Tab" && getZKAttr(node, "box") == tabboxId)
		return getZKAttr(node, "sel") == "true" ? node: null;

	for (var node = node.firstChild; node != null; node = node.nextSibling) {
		var n = zkTab._getSelTabFromTop(node, tabboxId);
		if (n) return n;
	}
	return null;
};

/** Selects the specified tab (and unselect the current tab). */
zkTab.selTab = function (tab) {
	tab = $e(tab);
	if (!tab) return;

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
	if (toSel) {
		tab.className = tab.className + "sel";
	} else {
		var len = tab.className.length;
		if (len > 3)
			tab.className = tab.className.substring(0, len - 3);
	}

	zkTab._changeBkgnd(tab, toSel);

	var tabbox = getZKAttr(tab, "box");
	if (tabbox) tabbox = $e(tabbox);
	var accd = tabbox && getZKAttr(tabbox, "accd") == "true";
	var panel = $e(getZKAttr(tab, "panel"));
	if (panel)
		if (accd) anima.slideDown($real(panel), toSel);
		else action.show(panel, toSel);

	if (!accd) {
		var tabs = zk.parentNode(zk.parentNode(tab, "TABLE"), "THEAD");
		if (tabs)
			zkTabs.fixWidth(tabs.id);
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

zkTab.init = function (cmp) {
	zk.listen(cmp, "click", zkTab.onclick);

	var anc = $e(cmp.id + "!a");
	if (anc) {
		zk.listen(anc, "focus", function () {zkau.onfocus(anc);});
		zk.listen(anc, "blur", function () {zkau.onblur(anc);});
	}
	var btn = $e(cmp.id + "!close");
	if (btn) {
		zk.listen(btn, "click", function (evt) {zkau.sendOnClose(cmp, true); Event.stop(evt);});
		zk.listen(btn, "mouseover", function () {zkau.onimgover(btn);});
		zk.listen(btn, "mouseout", function () {zkau.onimgout(btn);});
		if (!btn.style.cursor) btn.style.cursor = "default";
	}
};

////
// tabs //
zkTabs = {};

zkTabs.init = function (cmp) {
	setTimeout("zkTabs.fixWidth('"+cmp.id+"')", 30);
};
zkTabs.onVisi = zkTabs.onSize = function (cmp) {
	zkTabs.init(cmp);
};

/** Fix the width of the last column in tabs. */
zkTabs.fixWidth = function (uuid) {
	var n = $e(uuid + "!last");
	if (!n) return;

	var tbl = zk.parentNode(zk.parentNode(n, "TABLE"), "TABLE");
	var tabs = zk.parentNode(tbl, "TABLE");
		//Safari: THEAD's width and TD/TR's height is 0, so use TABLE instead
	if (tabs) {
		if ("TD" == $tag(n)) { //horizontal
			n.style.width = "1px"; //let tab's width be re-calc
			setTimeout(function () {
				var v = tabs.offsetWidth - tbl.offsetWidth + n.offsetWidth;
				if (v < 0) v = 0;
				n.style.width = v + "px";
			}, 0);
		} else { //vertical
			n.style.height = "1px"; //let tab's height be re-calc
			setTimeout(function () {
				if (n.cells && n.cells.length) n = n.cells[0];
				var v = tabs.offsetHeight - tbl.offsetHeight + n.offsetHeight;
				if (v < 0) v = 0;
				n.style.height = v + "px";
			}, 0);
		}
	}
};

