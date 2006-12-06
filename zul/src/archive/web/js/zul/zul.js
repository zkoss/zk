/* zul.js

{{IS_NOTE
	Purpose:
		Common utilities for zul.
	Description:
		
	History:
		Thu Jul 14 15:02:27     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.lang.mesg*");

////
zul = {};

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
	zkau.wndmode[cmp.id] = "modal";

	zkau.closeFloats(cmp);

	var maskId = cmp.id + ".mask";
	var mask = $e(maskId);
	if (!mask) {
		//Note: a modal window might be a child of another
		var bMask = true;
		for (var j = 0; j < nModals; ++j) {
			var n = $e(zkau._modals[j]);
			if (n && zk.isAncestor(n, cmp)) {
				bMask = false;
				break;
			}
		}
		if (bMask) {
			//bug 1510218: we have to make it as a sibling to cmp
			cmp.insertAdjacentHTML(
				"beforebegin", '<div id="'+maskId+'" class="modal_mask"></div>');
			mask =  $e(maskId);
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

	var caption = $e(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "move";

	zkau._modals.push(cmp.id);
	if (nModals == 0) {
		zk.listen(window, "resize", zul.doMoveMask);
		zk.listen(window, "scroll", zul.doMoveMask);
	}

	zkau.floatWnd(cmp, null, zkau.onWndMove);
	zk.disableAll(cmp);
	zk.restoreDisabled(cmp); //there might be two or more modal dlgs
	zk.focusDownById(cmp.id);
};

/** Makes the modal component as normal. */
zul.endModal = function (uuid) {
	var caption = $e(uuid + "!caption");
	if (caption && caption.style.cursor == "move") caption.style.cursor = "";

	var maskId = uuid + ".mask";
	var mask = $e(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		Element.remove(mask);
	}

	zkau._modals.remove(uuid);
	for (;;) {
		if (zkau._modals.length == 0) {
			zk.unlisten(window," resize", zul.doMoveMask);
			zk.unlisten(window, "scroll", zul.doMoveMask);
			window.onscroll = null;
			zk.restoreDisabled();
			break;
		}

		var lastid = zkau._modals[zkau._modals.length - 1];
		var last = $e(lastid);
		if (last) {
			zk.restoreDisabled(last);
			if (!prevfocusId) zk.focusDownById(lastid, 10);
			if (!prevfocusId) zk.focusDownById(lastid, 10);
			break;
		}
	}

	var cmp = $e(uuid);
	if (cmp) {
		delete zkau.wndmode[cmp.id];
		zkau.fixWnd(cmp);
	}

	if (prevfocusId) zk.focusById(prevfocusId, 10);
	if (prevfocusId) zk.focusById(prevfocusId, 10);
};
/** Handles onsize to re-position mask. */
zul.doMoveMask = function (evt) {
	for (var j = zkau._modals.length; --j >= 0;) {
		var mask = $e(zkau._modals[j] + ".mask");
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

//For sortable header, e.g., Column and Listheader (TH or TD is assumed)
zulHdr = {}; //listheader
zulHdr._szs = {};
zulHdr.init = function (cmp) {
	zulHdr._show(cmp);
	zk.listen(cmp, "click", function (evt) {zulHdr.onclick(evt, cmp);});
	zk.listen(cmp, "mousemove", function (evt) {zulHdr.onmove(evt, cmp);});
	zulHdr._szs[cmp.id] = new Draggable(cmp, {
		starteffect: Prototype.emptyFunction,
		endeffect: zulHdr._endsizing, ghosting: zulHdr._ghostsizing,
		revert: zulHdr._revertsizing, ignoredrag: zulHdr._notsizing,
		constraint: "horizontal"
	});
};
zulHdr.cleanup = function (cmp) {
	var id = cmp.id;
	if (zulHdr._szs[id]) {
		zulHdr._szs[id].destroy();
		delete zulHdr._szs[id];
	}
};
zulHdr.onclick = function (evt, cmp) {
	if (zulHdr._sortable(cmp) && zkau.insamepos(evt))
		zkau.send({uuid: cmp.id, cmd: "onSort", data: null}, 10);
};
zulHdr.onmove = function (evt, cmp) {
	var ofs = Position.cumulativeOffset(cmp);
	if (zulHdr._insizer(cmp, Event.pointerX(evt) - ofs[0])) {
		zk.backupStyle(cmp, "cursor");
		cmp.style.cursor = "move";
	} else {
		zk.restoreStyle(cmp, "cursor");
	}
};
/** Called by zkau._ignoredrag (in au.js) for generic drag&drop. */
zulHdr.ignoredrag = function (cmp, pointer) {
	var ofs = Position.cumulativeOffset(cmp);
	return zulHdr._insizer(cmp, pointer[0] - ofs[0]);
};
/** Returns 1 if right, -1 if left, 0 if none. */
zulHdr._insizer = function (cmp, x) {
	var cells = cmp.parentNode.cells;
	if (cells[0] != cmp && x <= 5) return -1;
	if (cells[cells.length-1] != cmp && x >= cmp.offsetWidth - 5) return 1;
	return 0;
};
/** Called by zulHdr._szs[]'s ignoredrag for resizing column. */
zulHdr._notsizing = function (cmp, pointer) {
	var ofs = Position.cumulativeOffset(cmp);
	var v = zulHdr._insizer(cmp, pointer[0] - ofs[0]);
	if (!v) return true;

	var dg = zulHdr._szs[cmp.id];
	if (dg) dg.z_left = v == -1;
	return false;
};
zulHdr._endsizing = function (n, pointer, evt) {
};
/* @param ghosting whether to create or remove the ghosting
 */
zulHdr._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		zk.dragging = true;
		dg.delta = dg.currentDelta();

		//Store scrolling offset first since Draggable.draw not handle DIV well
		//after we transfer TR to DIV
		var pos = Position.cumulativeOffset(dg.element);
		dg.z_scrl = Position.realOffset(dg.element);
		pos[0] -= dg.z_scrl[0]; pos[1] -= dg.z_scrl[1];
		document.body.insertAdjacentHTML("afterbegin",
			'<div id="zk_ddghost" style="position:absolute;top:'
			+pos[1]+'px;left:'+pos[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;border-'+(dg.z_left?'left':'right')+':3px solid darkgray"></div>');

		dg.zk_old = dg.element;
		dg.element = $e("zk_ddghost");
	} else {
		setTimeout("zk.dragging=false", 0);
			//we have to reset it later since onclick is fired later (after onmouseup)
		Element.remove(dg.element);
		dg.element = dg.zk_old;
		dg.zk_old = null;
	}
};
zulHdr._revertsizing = function (n, pointer) {
	return true;
};
/** Tests whether it is sortable.
 */
zulHdr._sortable = function (cmp) {
	return getZKAttr(cmp, "asc") || getZKAttr(cmp, "dsc");
};
/** Shows the hint, ascending or descending image.
 */
zulHdr._show = function (cmp) {
	switch (getZKAttr(cmp, "sort")) {
	case "ascending": zulHdr._renCls(cmp, "asc"); break;
	case "descending": zulHdr._renCls(cmp, "dsc"); break;
	case "natural": zulHdr._renCls(cmp); break;
	}
};
zulHdr._renCls = function (cmp, ext) {
	var clsnm = cmp.className || "";
	if (clsnm.endsWith("-asc") || clsnm.endsWith("-dsc"))
		clsnm = clsnm.substring(0, clsnm.length - 4);
	if (ext) clsnm += '-' + ext;
	if (clsnm != cmp.className) cmp.className = clsnm;
};
zulHdr.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if (nm == "z.sort") zulHdr._show(cmp);
	return true;
};
