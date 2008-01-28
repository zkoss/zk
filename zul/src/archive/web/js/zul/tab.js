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
zkTabbox.childchg = function (cmp) {
	var uuid = getZKAttr(cmp, "tabs");
	if (uuid)
		setTimeout("zkTabs.fixWidth('"+uuid+"')", 0);
			//Bug 1688071: width might not be ready right after visible
};

////
// tab //
zkTab = {};
zkTab.setAttr = function (cmp, name, value) {
	switch (name) {
	case "z.disabled":
		zkTab._disable(cmp,value);
		return true;
	}
	return false;
};
/** Selects the specified tab. */
zkTab.onclick = function (evt) {
	if (!evt) evt = window.event;
	var tab = $parentByType(Event.element(evt), "Tab");
	if(getZKAttr(tab, "disabled")=="true") return;//return when disabled
	if (!zkTab._sliding(tab)) //Bug 1571408
		zkTab.selTab(tab, true);
};

/** Returns the selected tab by giving any tab as the reference point. */
zkTab._getSelTab = function (tab) {
	var tabboxId = getZKAttr(tab, "box");
	if (tabboxId) {
		var tabbox = $e(tabboxId);
		if (zk.isAccord(tabbox))
			return zkTab._getSelTabFromTop(tabbox, tabboxId);
	}

	//non-accordion: we can use sibling directly
	for (var node = tab; node = node.nextSibling;)
		if (getZKAttr(node, "sel") == "true")
			return node;
	for (var node = tab; node = node.previousSibling;)
		if (getZKAttr(node, "sel") == "true")
			return node;
};
/** Whether any tab is sliding. */
zkTab._sliding = function (tab) {
	var tabboxId = getZKAttr(tab, "box");
	if (!tabboxId) return false;

	var tabbox = $e(tabboxId);
	if (!zk.isAccord(tabbox))
		return false;

	//accordion: we must go to panel firs, and then browse its sibling
	var panel = $e(getZKAttr(tab, "panel"));
	for (var node = panel; node = node.nextSibling;)
		if (getZKAttr($real(node), "animating"))
			return true;
	for (var node = panel; node = node.previousSibling;)
		if (getZKAttr($real(node), "animating"))
			return true;
	return false;
};
/** Returns the selected tab by specified any HTML tag containing it. */
zkTab._getSelTabFromTop = function (node, tabboxId) {
	if ($type(node) == "Tab" && getZKAttr(node, "box") == tabboxId)
		return getZKAttr(node, "sel") == "true" ? node: null;

	for (var node = node.firstChild; node; node = node.nextSibling) {
		var n = zkTab._getSelTabFromTop(node, tabboxId);
		if (n) return n;
	}
	return null;
};

/** Selects the specified tab (and unselect the current tab). */
zkTab.selTab = function (tab, notify) {
	tab = $e(tab);
	if (!tab) return;

	var old = zkTab._getSelTab(tab);
	if (old != tab) {
		if (old) zkTab._setTabSel(old, false);
		zkTab._setTabSel(tab, true, notify);
	}
};

/** Selects o unselect the specified tab. */
zkTab._setTabSel = function (tab, toSel, notify) {
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
	var accd = tabbox && zk.isAccord(tabbox);
	var panel = $e(getZKAttr(tab, "panel"));
	if (panel)
		if (accd) {
			if (toSel) anima.slideDown($real(panel));
			else anima.slideUp($real(panel));
		} else
			zk.show(panel, toSel);

	if (!accd) {
		var tabs = $parentByType(tab, "Tabs");
		if (tabs)
			zkTabs.fixWidth(tabs.id);
	}

	if (notify)
		zkau.send({uuid: tab.id, cmd: "onSelect", data: [tab.id]},
				zkau.asapTimeout(tab, "onSelect"));
};
/** Changes the style class. */
zkTab._changeBkgnd = function (node, toSel) {
	if (node.className && node.className.endsWith(toSel ? "-uns": "-sel"))
		node.className = zk.renType(node.className, toSel ? "sel": "uns");

	for (node = node.firstChild; node; node = node.nextSibling)
		zkTab._changeBkgnd(node, toSel);
};

zkTab.init = function (cmp) {
	zk.listen(cmp, "click", zkTab.onclick);

	var anc = $e(cmp.id + "!a");
	if (anc) {
		zk.listen(anc, "focus", zkau.onfocus);
		zk.listen(anc, "blur", zkau.onblur);
	}
	var btn = $e(cmp.id + "!close");
	if(btn){
		zk.listen(btn, "click", zkTab.onCloseBtnClick);
		
		if(getZKAttr(cmp, "disabled")!="true"){
			zk.listen(btn, "mouseover", zkau.onimgover);
			zk.listen(btn, "mouseout", zkau.onimgout);
		}
		if (!btn.style.cursor) btn.style.cursor = "default";
	}
	
	
};

/** close button clicked**/
zkTab.onCloseBtnClick = function(evt){
	if (!evt) evt = window.event;
	var tab = $parentByType(Event.element(evt), "Tab");
	if(getZKAttr(tab, "disabled")=="true") return;//return when disabled
	zkau.sendOnClose(tab, true); 
	Event.stop(evt);
}

/** inner method, disable this tab 
 * @param {Object} cmp tab element
 * @param {string} disabled string "true" or "false"
 */
zkTab._disable = function(cmp, disabled){
	var olddis = getZKAttr(cmp, "disabled");
	if(olddis==disabled) return;
	
	var btn = $e(cmp.id + "!close");
	var sel = getZKAttr(cmp, "sel");
	
	var clzn = cmp.className;
	var len = clzn.length;
	if(disabled=="true"){
		if (btn) {
			zk.unlisten(btn, "mouseover", zkau.onimgover);
			zk.unlisten(btn, "mouseout", zkau.onimgout);
		}
		//change style from tab/tabsel to tabdis/tabdissel
		if(sel=="true"){
			cmp.className = clzn.substring(0, len - 3)+"dis"+"sel";
		}else{
			cmp.className = clzn+"dis";
		}
	}else{
		if (btn) {
			zk.listen(btn, "mouseover", zkau.onimgover);
			zk.listen(btn, "mouseout", zkau.onimgout);
		}
		//change style from tabdis/tabdissel to tab/tabsel
		if(sel=="true"){
			cmp.className = clzn.substring(0, len - 6)+"sel";
		}else{
			cmp.className = clzn.substring(0, len - 3)
		}
	}
	setZKAttr(cmp, "disabled",disabled);
}

////
// tabs //
zkTabs = {};
zkTabs._tabs = []; //all available tabs

zkTabs.init = function (cmp) {
	zkTabs._tabs.push(cmp.id); //used by onResize
	zkTabs._fixWdLater(cmp.id);
};
zkTabs.cleanup = function (cmp) {
	zkTabs._tabs.remove(cmp.id);
};
zkTabs.onVisi = function (cmp) {
	zkTabs.init(cmp);
};

/** Returns whether the tabbox is accordion.
 * @since 3.0.3
 */
zk.isAccord = function (tabbox) {
	return getZKAttr(tabbox, "accd") == "true";
};

zk.addOnResize(function () {
	var tabs = zkTabs._tabs;
	for (var j = tabs.length; --j >=0;)
		zkTabs._fixWdLater(tabs[j]); //FF: we have to fire later (wd not correct yet)
});

zkTabs._fixWdLater = function (uuid) {
	setTimeout("zkTabs.fixWidth('"+uuid+"')", zk.ie ? 150 : 30);
};
/** Fix the width of the last column in tabs. */
zkTabs.fixWidth = function (uuid) {
	var ft = $e(uuid + "!first");
	var lt = $e(uuid + "!last");
	if (!ft || !lt) return;

	var tabs = $e(uuid);
	var tabbox = $parentByType(tabs, "Tabbox");
	if (!tabbox) return;

	var align = getZKAttr(tabs,"align");
	var tbl = zk.parentNode(zk.parentNode(ft, "TABLE"), "TABLE");
		//Safari: THEAD's width and TD/TR's height are 0, so use TABLE instead

	if (getZKAttr(tabbox, "orient") != "v") { //horizontal tabbox
		zkTabs._fixHgh(tabbox, tabs);

		//let tab's width be re-calc
		switch(align){
		default://start
			lt.style.width = "1px";
			break;
		case 'e'://end
		case 'c'://center
			ft.style.width = ft.offsetWidth+"px";
			break;
		}
		setTimeout(function () {
			switch(align){
			default:
				var v1 = tabbox.offsetWidth - tbl.offsetWidth; 
				var v =  v1+lt.offsetWidth;
				if(zk.gecko && v1==0){//BUG 1825812
					var pt = zk.parentNode(lt, "TABLE");
					var pd = zk.parentNode(pt, "TD");
					v = pd.offsetWidth - pt.offsetWidth + lt.offsetWidth;
				}
				if (v < 0) v = 0;
				lt.style.width = v + "px";
				break;
			case 'e':
				var v = tabbox.offsetWidth - tbl.offsetWidth +ft.offsetWidth;
				if (v < 0) v = 0;
				ft.style.width = v + "px";
				v2 = 0;
				break;
			case 'c':
				var v = tabbox.offsetWidth - tbl.offsetWidth +ft.offsetWidth+lt.offsetWidth;
				if (v < 0) v = 0;
				var v1,v2;
				v1 = Math.floor(v/2);
				v2 = v-v1;
				ft.style.width = v1 + "px";
				lt.style.width = v2 + "px";
				break;
			}
		}, 30);
		return;
	}

	//vertical tabbox
	switch(align){
	default:
		lt.style.height = "1px";
		break;
	case 'c':
	case 'm':
		ft.style.height = ft.offsetHeight+"px";
		break;
	}
	setTimeout(function () {
		if (ft.cells && ft.cells.length) ft = ft.cells[0];
		if (lt.cells && lt.cells.length) lt = lt.cells[0];
		switch(align){
		default:
			var v = tabbox.offsetHeight - tbl.offsetHeight +lt.offsetHeight;
			if (v < 0) v = 0;
			lt.style.height = v + "px";
			break;
		case 'e':
			var v = tabbox.offsetHeight - tbl.offsetHeight +ft.offsetHeight;
			if (v < 0) v = 0;
			ft.style.height = v + "px";
			v2 = 0;
			break;
		case 'c':
			var v = tabbox.offsetHeight - tbl.offsetHeight +ft.offsetHeight+lt.offsetHeight;
			if (v < 0) v = 0;
			var v1,v2;
			v1 = Math.floor(v/2);
			v2 = v-v1;
			ft.style.height = v1 + "px";
			lt.style.height = v2 + "px";
			break;
		}
	}, 30);
};
/** Fixes tabpanel's height if necessary.
 */
zkTabs._fixHgh = function (tabbox, tabs) {
	//fix tabpanels's height if tabbox's height is specified
	//Ignore accordion since its height is controlled by each tabpanel

	var hgh = tabbox.style.height;
	if ((vert || (hgh && hgh != "auto")) && !zk.isAccord(tabbox)){
		var panels = tabs;
		while ((panels = panels.nextSibling) && !panels.id)
			;

		if (panels) {
			//clean the height (to force recalc)
			for (var n = panels.firstChild; n; n = n.nextSibling)
				if (n.id && $visible(n)) {
					n.style.height = "";
					break;
				}

			hgh = zk.getVflexHeight(panels);

			for (var n = panels.firstChild; n; n = n.nextSibling)
				if (n.id)
					zk.setOffsetHeight(n, hgh);
		}
	}
};
