/* zul.js

{{IS_NOTE
	Purpose:
		Common utilities for zul.
	Description:
		
	History:
		Thu Jul 14 15:02:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.html.lang.mesg*");

function zul() {}

/** Exetends zkau.js to process additional command.
 * Note: it shall return wehther this command is processed.
 * If false, the default process will continue.
 */
zkau.processExt = function (cmd, uuid, cmp, datanum, data1, data2) {
	if ("doModal" == cmd ) {
		zul.doModal(cmp);
		return true;
	} else if ("endModal" == cmd) {
		zul.endModal(uuid);
		return true;
	} else {
		return false;
	}
};

/** Makes a window in the center. */
zul._center = function (cmp, zi) {
	cmp.style.position = "absolute";
	cmp.style.top = "-10000px"; //avoid annoying effect
	cmp.style.display = "block"; //we need to calculate the size
	zk.center(cmp);
	cmp.style.display = "none"; //avoid Firefox to display it too early
	cmp.style.display = "block";
	cmp.style.zIndex = zi;
}

/** Makes the component as modal. */
zul.doModal = function (cmp) {
	//center component
	var nModals = zkau._modals.length;
	zkau.fixZIndex(cmp, true); //let fixZIndex reset topZIndex if possible
	var zi = ++zkau.topZIndex; //mask also need another index
	zul._center(cmp, zi);
		//show dialog first to have better response.
	cmp.setAttribute("mode", "modal");

	zkau.closeFloats(cmp);

	var maskId = cmp.id + ".mask";
	var mask = $(maskId);
	if (!mask) {
		//Note: a modal window might be a child of another
		var bMask = true;
		for (var j = 0; j < nModals; ++j) {
			var n = $(zkau._modals[j]);
			if (n && zk.isAncestor(n, cmp)) {
				bMask = false;
				break;
			}
		}
		if (bMask) {
			//bug 1510218: we have to make it as a sibling to cmp
			cmp.insertAdjacentHTML(
				"beforebegin", '<div id="'+maskId+'" class="modal_mask"></div>');
			mask =  $(maskId);
			if (!mask) zk.debug(msgzul.FAILED_TO_CREATE_MASK);
		}
	}

	//position mask to be full window
	if (mask) {
		zul.positionMask(mask);
		mask.style.display = "block";
		mask.style.zIndex = zi - 1;
		if (zkau.currentFocus) //store it
			mask.setAttribute("zk_prevfocus", zkau.currentFocus.id);
	}

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "move";

	zkau._modals.push(cmp.id);
	if (nModals == 0) {
		Event.observe(window, "resize", zul.doMoveMask);
		Event.observe(window, "scroll", zul.doMoveMask);
	}

	zkau.enableMoveable(cmp, null, zkau.onWndMove);
	zk.disableAll(cmp);
	zk.restoreDisabled(cmp); //there might be two or more modal dlgs
	zk.focusDownById(cmp.id);
};

/** Makes the modal component as normal. */
zul.endModal = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "move") caption.style.cursor = "";

	var maskId = uuid + ".mask";
	var mask = $(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		Element.remove(mask);
	}

	zkau._modals.remove(uuid);
	for (;;) {
		if (zkau._modals.length == 0) {
			Event.stopObserving(window," resize", zul.doMoveMask);
			Event.stopObserving(window, "scroll", zul.doMoveMask);
			window.onscroll = null;
			zk.restoreDisabled();
			break;
		}

		var lastid = zkau._modals[zkau._modals.length - 1];
		var last = $(lastid);
		if (last) {
			zk.restoreDisabled(last);
			if (!prevfocusId) zk.focusDownById(lastid, 10);
			if (!prevfocusId) zk.focusDownById(lastid, 10);
			break;
		}
	}

	var cmp = $(uuid);
	if (cmp) {
		zkau.disableMoveable(cmp);
		cmp.removeAttribute("mode");
	}

	if (prevfocusId) zk.focusById(prevfocusId, 10);
	if (prevfocusId) zk.focusById(prevfocusId, 10);
};
/** Handles onsize to re-position mask. */
zul.doMoveMask = function (evt) {
	for (var j = zkau._modals.length; --j >= 0;) {
		var mask = $(zkau._modals[j] + ".mask");
		if (mask) {
			zul.positionMask(mask);
			return;
		}
	}
};
/** Position the mask window. */
zul.positionMask = function (mask) {
	mask.style.left = zk.innerX() + "px";
	mask.style.top = zk.innerY() + "px";
	mask.style.width = zk.innerWidth() + "px";
	mask.style.height = zk.innerHeight() + "px";
};

//For sortable header, e.g., Column and Listheader
function zulSHdr() {} //listheader
zulSHdr.init = function (cmp) {
	zulSHdr._show(cmp);
	Event.observe(cmp, "click", function (evt) {zulSHdr.onclick(evt, cmp);});
};
zulSHdr.onclick = function (evt, cmp) {
	if (zulSHdr._sortable(cmp))
		zkau.send({uuid: cmp.id, cmd: "onSort", data: null}, 10);
};

/** Tests whether it is sortable.
 */
zulSHdr._sortable = function (cmp) {
	return cmp.getAttribute("zk_asc") || cmp.getAttribute("zk_dsc");
};
/** Shows the hint, ascending or descending image.
 */
zulSHdr._show = function (cmp) {
	switch (cmp.getAttribute("zk_sort")) {
	case "ascending": zulSHdr._renCls(cmp, "asc"); break;
	case "descending": zulSHdr._renCls(cmp, "dsc"); break;
	case "natural": zulSHdr._renCls(cmp); break;
	}
};
zulSHdr._renCls = function (cmp, ext) {
	var clsnm = cmp.className || "";
	if (clsnm.endsWith("-asc") || clsnm.endsWith("-dsc"))
		clsnm = clsnm.substring(0, clsnm.length - 4);
	if (ext) clsnm += '-' + ext;
	if (clsnm != cmp.className) cmp.className = clsnm;
};
zulSHdr.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if (nm == "zk_sort") zulSHdr._show(cmp);
	return true;
};
