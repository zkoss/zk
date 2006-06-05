/* menu.js

{{IS_NOTE
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

function zkMenu() {}

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
		&& el.parentNode.getAttribute("zk_mpop") != null)
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

if (!zkMenu._pop)
	zkau.floats.push(zkMenu._pop = new zk.FloatMenu()); //hook to zkau.js

////
// menu //
zkMenu.onover = function (target) {
	var menubar = zkau.getParentByType(target, "Menubar");
	var autodrop = !menubar || menubar.getAttribute("zk_autodrop") == "true";
	if (autodrop) zkMenu._closeRequired = false;
		//turn off pending auto-close

	var popupIds = zkMenu._pop.getPopupIds();
	if (!autodrop && popupIds.length == 0) return;

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
	if (zkMenu._pop.getPopupIds().length == 0) return; //nothing to do

	var menubar = zkau.getParentByType(target, "Menubar");
	if (menubar && menubar.getAttribute("zk_autodrop") == "true") {
		zkMenu._closeRequired = true;
		setTimeout("if (zkMenu._closeRequired) zkau.closeFloats('"+menubar.id+"');", 500);
	}
};
zkMenu.onclick = function (target) {
	if (!target.getAttribute("zk_mpop")) { //menu item
		zkau.closeFloats(target); //including popups if visible

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
/** Opens a menupopup belong to the specified menu.
 * @param toggle whether to close all menu first and then open the specified menu
 */
zkMenu.open = function (menu, toggle) {
	if (toggle) zkau.closeFloats(menu); //including popups

	var popupId = menu.getAttribute("zk_mpop");
	if (!popupId) return; //menuitem

	var pp = $(popupId);
	if (!pp) {
		zk.error(mesg.INVALID_STRUCTURE+"zk_mpop not exists");
		return;
	}

	var top = menu.getAttribute("zk_top") == "true"; //top-level menu
	var ref = top || zk.tagName(menu) != "TD" ? menu: menu.parentNode; //use TR if not top
	var pos = top && menu.getAttribute("zk_vert") == null ? "after-start": "end_before";
	zkMenu._open(pp, top, ref, pos);
};
/** Opens the specified menupopup
 * @param pp menupopup
 * @param top whether it belongs to the top-level menu
 * @param ref the reference element to position menu.
 * @param pos how to position the menu
 */
zkMenu._open = function (pp, top, ref, pos) {
	var visible = pp.style.display != "none";
	if (visible)
		return; //nothing to do

	/* not yet: we have to adjust CSS and some codes
	if (zk.agtNav) { //Bug 1486840
		pp.setAttribute("zk_vparent", uuid); //used by zkTxbox._noonblur
		document.body.appendChild(pp);
	}*/


	pp.style.display = "block";
	pp.style.position = "absolute"; //just in case
	if (ref) zk.position(pp, ref, pos);

	zkMenu._pop.addPopupId(pp.id);
	zkau.hideCovered();
	if (zk.agtNav)
		setTimeout("zkMenu._fixWidth('"+pp.id+"')", 10);
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
	zkMenu._pop.removePopupId(pp.id);
	zkMenu._close(pp);
	zkau.hideCovered();
};
zkMenu._close = function (pp) {
	pp = $(pp);
	if (pp) {
		/*if (zk.agtNav) { //Bug 1486840
			$(uuid).appendChild(pp); //Bug 1486840
			pp.removeAttribute("zk_vparent");
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
			zkau.closeFloats(cmp);

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

//menupopup//
function zkMpop() {}

/** Called by au.js's context menu. */
zkMpop.context = function (ctx, ref) {
	zkMenu._open(ctx, true);
};
