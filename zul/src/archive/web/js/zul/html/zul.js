/* zul.js

{{IS_NOTE
	$Id: zul.js,v 1.15 2006/05/16 04:29:25 tomyeh Exp $
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

if (!zul._modalIds) {
	zul._modalIds = new Array();
}

/** Exetends zkau.js to process additional command.
 * Note: it shall return wehther this command is processed.
 * If false, the default process will continue.
 */
zkau.processExt = function (cmd, uuid, cmp, datanum, data1, data2) {
	if ("doModal" == cmd ) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return true;
		zul.doModal(cmp);
		return true;
	} else if ("endModal" == cmd) {
		if (!zkau._checkResponse(cmd, null, null, 1, datanum))
			return true;
		zul.endModal(uuid);
		return true;
	} else {
		return false;
	}
};

/** Makes a window in the center. */
zul._center = function (cmp, zIdx) {
	cmp.style.position = "absolute";
	cmp.style.top = "-10000px"; //avoid annoying effect
	cmp.style.display = "block"; //we need to calculate the size
	zk.center(cmp);
	cmp.style.display = "none"; //avoid Firefox to display it too early
	cmp.style.display = "block";
	cmp.style.zIndex = zIdx;
}

/** Makes the component as modal. */
zul.doModal = function (cmp) {
	//center component
	var nModals = zul._modalIds.length;
	var zIdx = 20000 + nModals * 2;
	zul._center(cmp, zIdx + 1);
		//show dialog first to have better response.
	cmp.setAttribute("mode", "modal");

	zkau.closeFloats();

	var maskId = cmp.id + ".mask";
	var mask = $(maskId);
	if (!mask) {
		//Note: a modal window might be a child of another
		var bMask = true;
		for (var j = 0; j < nModals; ++j) {
			var n = $(zul._modalIds[j]);
			if (n && zk.isAncestor(n, cmp)) {
				bMask = false;
				break;
			}
		}
		if (bMask) {
			document.body.insertAdjacentHTML(
				"afterbegin", '<div id="'+maskId+'" class="modal_mask"></div>');
			mask =  $(maskId);
			if (!mask) zk.debug(msgzul.FAILED_TO_CREATE_MASK);
		}
	}

	//position mask to be full window
	if (mask) {
		zul.positionMask(mask);
		mask.style.display = "block";
		mask.style.zIndex = zIdx;
		if (zkau.currentFocus) //store it
			mask.setAttribute("zk_prevfocus", zkau.currentFocus.id);
	}

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "pointer";

	zul._modalIds.push(cmp.id);
	if (nModals == 0) {
		Event.observe(window, "resize", zul.doMoveMask);
		Event.observe(window, "scroll", zul.doMoveMask);
	}

	zkau.enableMoveable(cmp, zkau.autoZIndex, zkau.onWndMove);
	zk.disableAll(cmp);
	zk.restoreDisabled(cmp); //there might be two or more modal dlgs
	zk.focusDownById(cmp.id);
};

/** Makes the modal component as normal. */
zul.endModal = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "pointer") caption.style.cursor = "";

	var maskId = uuid + ".mask";
	var mask = $(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		mask.parentNode.removeChild(mask);
	}

	zul._modalIds.remove(uuid);
	for (;;) {
		if (zul._modalIds.length == 0) {
			Event.stopObserving(window," resize", zul.doMoveMask);
			Event.stopObserving(window, "scroll", zul.doMoveMask);
			window.onscroll = null;
			zk.restoreDisabled();
			break;
		}

		var lastid = zul._modalIds[zul._modalIds.length - 1];
		var last = $(lastid);
		if (last) {
			zk.restoreDisabled(last);
			if (!prevfocusId) zk.focusDownById(lastid);
			break;
		}
	}

	var cmp = $(uuid);
	if (cmp) {
		zkau.disableMoveable(cmp);
		cmp.removeAttribute("mode");
	}

	if (prevfocusId) zk.focusById(prevfocusId);
};
/** Handles onsize to re-position mask. */
zul.doMoveMask = function (evt) {
	for (var j = zul._modalIds.length; --j >= 0;) {
		var mask = $(zul._modalIds[j] + ".mask");
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
