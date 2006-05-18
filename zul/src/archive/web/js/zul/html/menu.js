/* menu.js

{{IS_NOTE
	$Id: menu.js,v 1.14 2006/05/15 05:30:02 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 10:43:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.html.zul");

zk.FloatMenu = Class.create();
zk.FloatMenu.prototype = {
	initialize: function () {
		this._popupIds = new Array();
	},

	/** Whether the mousedown event shall be ignored for the specified
	 * event.
	 */
	focusInFloats: function (el) {
		if (!el) return false;

		if (el.parentNode && el.parentNode.getAttribute
		&& el.parentNode.getAttribute("zk_popup") != null)
			return true;

		for (var j = 0; j < this._popupIds.length; ++j) {
			var pp = $(this._popupIds[j]);
			if (pp != null && zk.isAncestor(pp, el))
				return true;
		}
		return false;
	},

	/** Closes (hides) all menus. */
	closeFloats: function() {
		var closed;
		for (;;) {
		//reverse order is important if pp contains another
		//otherwise, IE might have bug to handle them correctly
			var uuid = this._popupIds.pop();
			if (!uuid) break;

			closed = true;
			zkMenu._close(uuid);
		}
		return closed;
	},

	/** Adds elements that we have to hide what they covers.
	 */
	addHideCovered: function (ary) {
		for (var j = 0; j < this._popupIds.length; ++j) {
			var el = $(this._popupIds[j]);
			if (el) ary.push(el);
		}
	},

	getPopupIds: function () {
		return this._popupIds;
	},
	addPopupId: function (id) {
		this._popupIds.push(id);
	},
	removePopupId: function (id) {
		this._popupIds.remove(id);
	}
};

if (!zkau._menu)
	zkau.floats.push(zkau._menu = new zk.FloatMenu()); //hook to zkau.js

////
// menu //
function zkMenu() {}

zkMenu.onover = function (target) {
	var menubar = zkau.getParentByType(target, "Menubar");
	var autoPopup = menubar && menubar.getAttribute("zk_autoPopup") == "true";
	if (autoPopup) zkMenu._closeRequired = false;
		//turn off pending auto-close

	var popupIds = zkau._menu.getPopupIds();
	if (!autoPopup && popupIds.length == 0) return;

	//Close non-child menu
	for (var j = popupIds.length; --j >= 0;) {
		var pp = $(popupIds[j]);
		if (!zk.isAncestor(target, pp)
		&& !zk.isAncestor(pp, target))
			zkMenu.close(pp);
	}

	zkMenu.open(target, false);
};
zkMenu.onout = function (target) {
	if (zkau._menu.getPopupIds().length == 0) return; //nothing to do

	var menubar = zkau.getParentByType(target, "Menubar");
	if (menubar && menubar.getAttribute("zk_autoPopup") == "true") {
		zkMenu._closeRequired = true;
		setTimeout("if (zkMenu._closeRequired) zkau.closeFloats();", 500);
	}
};
zkMenu.onclick = function (target) {
	if (!target.getAttribute("zk_popup")) { //menu item
		zkau.closeFloats(); //including popups if visible

		var uuid = target.id;
		if (target.getAttribute("zk_autock")) {
			var newval = target.getAttribute("zk_checked") != "true";
			zkau.send({uuid: uuid, cmd: "onCheck", data: [newval]}, -1);
		}
		zkau.send({uuid: uuid, cmd: "onClick", data: null}, 0);
	} else {
		zkMenu.open(target, target.getAttribute("zk_top") == "true");
	}
};
/** Opens a menu. If toggle, menus are closed first and then
 * toggle the specified menu.
 */
zkMenu.open = function (menu, toggle) {
	var popupId = menu.getAttribute("zk_popup");
	if (!popupId) return; //menuitem

	var pp = $(popupId);
	if (!pp) {
		alert(mesg.INVALID_STRUCTURE+"zk_popup not exists");
		return;
	}

	var visible = pp.style.display != "none";
	if (toggle)
		zkau.closeFloats(); //including popups
	if (visible)
		return; //nothing to do

	/* not yet: we have to adjust CSS and some codes
	if (zk.agtNav) { //Bug 1486840
		pp.setAttribute("zk_combo_parent", uuid); //used by zkTxbox._noonblur
		document.body.appendChild(pp);
	}*/

	var top = menu.getAttribute("zk_top") == "true"; //top-level menu
	var ref = top || zk.tagName(menu) != "TD" ? menu: menu.parentNode;
		//use TR if not top
	pp.style.display = "block";
	pp.style.position = "absolute"; //just in case
	zk.position(pp, ref,
		top && menu.getAttribute("zk_vert") == null ? "after-start": "end_before");

	zkau._menu.addPopupId(pp.id);
	zkau.hideCovered();
	if (zk.agtNav)
		setTimeout("zkMenu._fixWidth('"+popupId+"')", 10);
};
/** Fixes a Mozilla bug that div's width might be smaller than
 * the table it contains.
 */
zkMenu._fixWidth = function (popupId) {
	var pp = $(popupId);
	if (pp) {
		var tbl = pp.firstChild;
		for (;; tbl = tbl.nextSibling) {
			if (!tbl) return;
			if (zk.tagName(tbl) == "TABLE")
				break;
		}
		if (pp.offsetWidth < tbl.offsetWidth)
			pp.style.width = (tbl.offsetWidth + 4) + "px";
	}
};

/** Closes the menu. */
zkMenu.close = function (pp) {
	zkau._menu.removePopupId(pp.id);
	zkMenu._close(pp);
	zkau.hideCovered();
};
zkMenu._close = function (pp) {
	pp = $(pp);
	if (pp) {
		/*if (zk.agtNav) { //Bug 1486840
			$(uuid).appendChild(pp); //Bug 1486840
			pp.removeAttribute("zk_combo_parent");
		}*/
		pp.style.display = "none";
	}
};

zkMenu.init = function (cmp) {
	var anc = $(cmp.id + "!a");
	if (cmp.getAttribute("zk_top") == "true") {
		Event.observe(anc, "click", function () {zkMenu.onclick(cmp);});
		Event.observe(anc, "mouseover", function () {zkMenu.onover(cmp);});
		Event.observe(anc, "mouseout", function () {zkMenu.onout(cmp);});
	} else {
		Event.observe(cmp, "click", function () {zkMenu.onclick(cmp);});
		Event.observe(cmp, "mouseover", function () {zkMenu.onover(cmp);});
		Event.observe(cmp, "mouseout", function () {zkMenu.onout(cmp);});

		Event.observe(anc, "focus", function () {zkau.onfocus(anc);});
		Event.observe(anc, "blur", function () {zkau.onblur(anc);});
	}
};

////
// menubar, menuitem //
function zkMenubar() {}
function zkMenuit() {} //menuitem
function zkMenusp() {} //menuseparator

zkMenuit.init = function (cmp) {
	var anc = $(cmp.id + "!a");
	Event.observe(cmp, "click", function (evt) {
		if ("javascript:;" == anc.href) zkMenu.onclick(cmp);
		else {
			zkau.closeFloats();

			var t = anc.getAttribute("target");
			if (anc.href && !zk.isNewWindow(anc.href, t))
				zk.progress();
		}
		Event.stop(evt); //otherwise, zkMenu's onclick will be called, too
	});
	Event.observe(cmp, "mouseover", function () {zkMenu.onover(cmp);});
	Event.observe(cmp, "mouseout", function () {zkMenu.onout(cmp);});

	if (cmp.getAttribute("zk_top") != "true") { //non-topmost
		Event.observe(anc, "focus", function () {zkau.onfocus(anc);});
		Event.observe(anc, "blur", function () {zkau.onblur(anc);});
	}
};

zkMenusp.init = function (cmp) {
	Event.observe(cmp, "mouseover", function () {zkMenu.onover(cmp);});
	Event.observe(cmp, "mouseout", function () {zkMenu.onout(cmp);});
};
