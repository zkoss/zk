/* menu.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 10:43:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

////
zkMenu = {};

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

		if (getZKAttr(el.parentNode, "mpop") != null)
			return true;

		for (var j = 0; j < this._popupIds.length; ++j) {
			var pp = $e(this._popupIds[j]);
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
			var el = $e(this._popupIds[j]);
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
zkMenu.onover = function (evt) {
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt));

	var menubar = $parentByType(cmp, "Menubar");
	var autodrop = !menubar || getZKAttr(menubar, "autodrop") == "true";
	if (autodrop) zkMenu._shallClose = false;
		//turn off pending auto-close

	var popupIds = zkMenu._pop.getPopupIds();
	if (!autodrop && popupIds.length == 0) return;

	//Close non-child menu
	for (var j = popupIds.length; --j >= 0;) {
		var pp = $e(popupIds[j]);
		if (!zk.isAncestor(cmp, pp) && !zk.isAncestor(pp, cmp))
			zkMenu.close(pp);
	}

	zkMenu.open(cmp, false);
};
zkMenu.onout = function (evt) {
	if (zkMenu._pop.getPopupIds().length == 0) return; //nothing to do

	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt));

	var menubar = $parentByType(cmp, "Menubar");
	if (menubar && getZKAttr(menubar, "autodrop") == "true") {
		zkMenu._shallClose = true;
		setTimeout("if (zkMenu._shallClose) zkau.closeFloats('"+menubar.id+"');", 500);
	}
};
zkMenu.onclick = function (evt) {
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt));
	if ("Menu" == $type(cmp)) //note: Menuit also go thru this method
		zkMenu.open(cmp, getZKAttr(cmp, "top") == "true");
};

/** Opens a menupopup belong to the specified menu.
 * @param toggle whether to close all menu first and then open the specified menu
 */
zkMenu.open = function (menu, toggle) {
	if (toggle) zkau.closeFloats(menu); //including popups

	var popupId = getZKAttr(menu, "mpop");
	if (!popupId) return; //menuitem

	var pp = $e(popupId);
	if (!pp) {
		zk.error(mesg.INVALID_STRUCTURE+"z.mpop not exists");
		return;
	}

	var top = getZKAttr(menu, "top") == "true"; //top-level menu
	var ref = top || $tag(menu) != "TD" ? menu: menu.parentNode; //use TR if not top
	var pos = top && getZKAttr(menu, "vert") == null ? "after-start": "end_before";
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
	if (zk.gecko) { //Bug 1486840
		setZKAttr(pp, "vparent", uuid); //used by zkTxbox._noonblur
		document.body.appendChild(pp);
	}*/


	pp.style.display = "block";
	pp.style.position = "absolute"; //just in case
	if (ref) zk.position(pp, ref, pos);

	zkMenu._pop.addPopupId(pp.id);
	zkau.hideCovered();
	if (zk.gecko)
		setTimeout("zkMenu._fixWidth('"+pp.id+"')", 10);
};
/** Fixes a Mozilla bug that div's width might be smaller than
 * the table it contains.
 */
zkMenu._fixWidth = function (popupId) {
	var pp = $e(popupId);
	if (pp) {
		var tbl = pp.firstChild;
		for (;; tbl = tbl.nextSibling) {
			if (!tbl) return;
			if ($tag(tbl) == "TABLE")
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
	pp = $e(pp);
	if (pp) {
		/*if (zk.gecko) { //Bug 1486840
			$e(uuid).appendChild(pp); //Bug 1486840
			rmZKAttr(pp, "vparent");
		}*/
		pp.style.display = "none";
	}
};

zkMenu.init = function (cmp) {
	var anc = $e(cmp.id + "!a");
	if (getZKAttr(cmp, "top") == "true") {
		zk.listen(anc, "click", zkMenu.onclick);
		zk.listen(anc, "mouseover", zkMenu.onover);
		zk.listen(anc, "mouseout", zkMenu.onout);
	} else {
		zk.listen(cmp, "click", zkMenu.onclick);
		zk.listen(cmp, "mouseover", zkMenu.onover);
		zk.listen(cmp, "mouseout", zkMenu.onout);

		zk.listen(anc, "focus", function () {zkau.onfocus(anc);});
		zk.listen(anc, "blur", function () {zkau.onblur(anc);});
	}
};

////
// menubar, menuitem //
zkMenubar = {};
zkMenuit = {}; //menuitem
zkMenusp = {}; //menuseparator

zkMenuit.init = function (cmp) {
	zk.listen(cmp, "click", zkMenuit.onclick);
	zk.listen(cmp, "mouseover", zkMenu.onover);
	zk.listen(cmp, "mouseout", zkMenu.onout);

	if (getZKAttr(cmp, "top") != "true") { //non-topmost
		var anc = $e(cmp.id + "!a");
		zk.listen(anc, "focus", function () {zkau.onfocus(anc);});
		zk.listen(anc, "blur", function () {zkau.onblur(anc);});
	}
};
zkMenuit.onclick = function (evt) {
	if (!evt) evt = window.event;
	var cmp = $parentByType(Event.element(evt), "Menuit");
	zkau.closeFloats(cmp);//including popups if visible
	var anc = $e(cmp.id + "!a");
	if ("javascript:;" == anc.href) {
		var cmp = $outer(anc);
		var uuid = cmp.id;
		if (getZKAttr(cmp, "autock")) {
			var newval = getZKAttr(cmp, "checked") != "true";
			zkau.send({uuid: uuid, cmd: "onCheck", data: [newval]}, -1);
		}
		zkau.send({uuid: uuid, cmd: "onClick", data: null});
	} else {
		var t = anc.getAttribute("target");
		if (anc.href && !zk.isNewWindow(anc.href, t))
			zk.progress();
		//Note: we cannot eat onclick. or, <a> won't work
	}
};

zkMenusp.init = function (cmp) {
	zk.listen(cmp, "mouseover", zkMenu.onover);
	zk.listen(cmp, "mouseout", zkMenu.onout);
};

//menupopup//
zkMpop = {};

/** Called by au.js's context menu. */
zkMpop.context = function (ctx, ref) {
	if (getZKAttr(ctx, "onOpen"))
		zkau.send({uuid: ctx.id, cmd: "onOpen", data: [true, ref.id]});
	zkMenu._open(ctx, true);
};
