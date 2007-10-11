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
zul._movs = {}; //(id, Draggable): movables

/////////
// Movable
/** Make a component movable (by moving). */
zul.initMovable = function (cmp, options) {
	zul._movs[cmp.id] = new Draggable(cmp, options);
};
/** Undo movable for a component. */
zul.cleanMovable = function (id) {
	if (zul._movs[id]) {
		zul._movs[id].destroy();
		delete zul._movs[id];
	}
};

/////////
// Headers
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
		wd = zk.revisedSize(cmp, wd);
		wd2 = zk.revisedSize(cmp2, wd2);
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
