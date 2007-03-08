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
zk.load("zul.lang.msgzul*");

////
zul = {};
zul._modal2 = new Array();
	//an array of modal wnd's uuid waiting for 2nd phase processing,
	//e.g. disable

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

	zkau._modals.push(cmp.id);
	if (nModals == 0) {
		zk.listen(window, "resize", zul.doMoveMask);
		zk.listen(window, "scroll", zul.doMoveMask);
	}

	zkau.floatWnd(cmp, null, zkau.onWndMove);
	zk.focusDownById(cmp.id);

	zul._modal2.push(cmp.id);
	setTimeout(zul._doModal2, 8); //process it later for better responsive
};
/** Does the 2nd phase processing of modal. */
zul._doModal2 = function () {
	if (zul._modal2.length) {
		var uuid = zul._modal2.shift();
		var cmp = $(uuid);
		if (cmp) {
			zk.restoreDisabled(cmp); //there might be two or more modal dlgs
			zk.disableAll(cmp);
		}
		if (zul._modal2.length)
			setTimeout(zul._doModal2, 8);
	}
}

/** Makes the modal component as normal. */
zul.endModal = function (uuid) {
	var maskId = uuid + ".mask";
	var mask = $e(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		Element.remove(mask);
	}

	zkau._modals.remove(uuid);
	zul._modal2.remove(uuid);
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
zulHdrs = {};
zulHdrs.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);

	if (nm == "z.sizable") {
		var cells = cmp.cells;
		if (cells) {
			var sizable = val == "true";
			for (var j = 0; j < cells.length; ++j)
				zulHdr.setSizable(cells[j], sizable);
		}
	}
	return true;
};

zulHdr = {}; //listheader
zulHdr._szs = {};
zulHdr.init = function (cmp) {
	zulHdr._show(cmp);
	zk.listen(cmp, "click", function (evt) {zulHdr.onclick(evt, cmp);});
	zk.listen(cmp, "mousemove", function (evt) {if (window.zulHdr) zulHdr.onmove(evt, cmp);});
		//FF: at the moment of browsing to other URL, listen is still attached but
		//our js are unloaded. It causes JavaScript error though harmlessly
		//This is a dirty fix (since onclick and others still fail but hardly happen)

	zulHdr.setSizable(cmp, zulHdr.sizable(cmp));
		//Note: IE6 failed to crop a column if it is draggable
		//Thus we init only necessary (to avoid the IE6 bug)
};
zulHdr.sizable = function (cmp) {
	return cmp.parentNode && getZKAttr(cmp.parentNode, "sizable") == "true";
};
zulHdr.setSizable = function (cmp, sizable) {
	var id = cmp.id;
	if (sizable) {
		if (!zulHdr._szs[id]) {
			zulHdr._szs[id] = new Draggable(cmp, {
				starteffect: zk.voidf,
				endeffect: zulHdr._endsizing, ghosting: zulHdr._ghostsizing,
				revert: true, ignoredrag: zulHdr._ignoresizing,
				constraint: "horizontal"
			});
		}
	} else {
		if (zulHdr._szs[id]) {
			zulHdr._szs[id].destroy();
			delete zulHdr._szs[id];
		}
	}
};
/** Resize all rows. (Utilities used by derived). */
zulHdr.resizeAll = function (rows, cmp, icol, col1, wd1, col2, wd2, keys) {
	var icol2 = icol + 1;
	for (var j = 0; j < rows.length; ++j) {
		var cells = rows[j].cells;
		if (icol < cells.length)
			cells[icol].style.width = wd1;
		if (icol2 < cells.length)
			cells[icol2].style.width = wd2;
	}

	zkau.send({uuid: cmp.id, cmd: "onColSize",
		data: [icol, col1.id, wd1, col2.id, wd2, keys]},
		zkau.asapTimeout(cmp, "onColSize"));
};
zulHdr.cleanup = function (cmp) {
	zulHdr.setSizable(cmp, false);
};
zulHdr.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if (nm == "z.sort") zulHdr._show(cmp);
	return true;
};

zulHdr.onclick = function (evt, cmp) {
	if (zulHdr._sortable(cmp) && zkau.insamepos(evt))
		zkau.send({uuid: cmp.id, cmd: "onSort", data: null}, 10);
};
zulHdr.onmove = function (evt, cmp) {
	var ofs = Position.cumulativeOffset(cmp);
	var v = zulHdr._insizer(cmp, Event.pointerX(evt) - ofs[0]);
	if (v) {
		zk.backupStyle(cmp, "cursor");
		cmp.style.cursor = v == 1 ? "e-resize": "w-resize";
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
	if (zulHdr.sizable(cmp)) {
		var cells = cmp.parentNode.cells;
		if (cells[0] != cmp && x <= 5) return -1;
		if (cells[cells.length-1] != cmp && x >= cmp.offsetWidth - 5) return 1;
	}
	return 0;
};
/** Called by zulHdr._szs[]'s ignoredrag for resizing column. */
zulHdr._ignoresizing = function (cmp, pointer) {
	var dg = zulHdr._szs[cmp.id];
	if (dg) {
		var ofs = Position.cumulativeOffset(cmp);
		var v = zulHdr._insizer(cmp, pointer[0] - ofs[0]);
		if (v) {
			dg.z_szlft = v == -1;
			return false;
		}
	}
	return true;
};
zulHdr._endsizing = function (cmp, evt) {
	var dg = zulHdr._szs[cmp.id];
	if (dg && dg.z_szofs) {
		//Adjust column width
		var cells = cmp.parentNode.cells, j = 0, cmp2;
		for (;; ++j) {
			if (j >= cells.length) return; //impossible, but just in case
			if (cmp == cells[j]) {
				if (dg.z_szlft) {
					if (!j) return; //impossible, but just in case
					cmp2 = cmp;
					cmp = cells[--j];
				} else {
					if (j + 1 >= cells.length) return; //impossible, but just in case
					cmp2 = cells[j + 1];
				}
				break;
			}
		}

		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}

		var wd = zk.offsetWidth(cmp) + dg.z_szofs,
			wd2 = zk.offsetWidth(cmp2) - dg.z_szofs;
		if (wd < 0) {
			wd2 += wd;
			wd = 0;
		} else if (wd2 < 0) {
			wd += wd2;
			wd2 = 0;
		}

		cmp.style.width = wd += "px";
		cmp2.style.width = wd2 += "px";
		setTimeout("zk.eval($e('"+cmp.id+"'),'resize',null,$e('"+cmp2.id+"'),"+j+",'"+wd+"','"+wd2+"','"+keys+"')", 0);
	}
};

/* @param ghosting whether to create or remove the ghosting
 */
zulHdr._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var ofs = zkau.beginGhostToDIV(dg);
		document.body.insertAdjacentHTML("afterbegin",
			'<div id="zk_ddghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;border-'+(dg.z_szlft?'left':'right')+':3px solid darkgray"></div>');
		dg.element = $e("zk_ddghost");
	} else {
		var org = zkau.getGhostOrgin(dg);
		if (org) {
			var ofs1 = Position.cumulativeOffset(dg.element);
			var ofs2 = Position.cumulativeOffset(org);
			dg.z_szofs = ofs1[0] - ofs2[0];
		} else {
			dg.z_szofs = 0;
		}
		zkau.endGhostToDIV(dg);
	}
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
