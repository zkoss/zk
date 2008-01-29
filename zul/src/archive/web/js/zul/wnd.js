/* wnd.js

{{IS_NOTE
	Purpose:
		
	Description:
		Window
	History:
		Thu Mar 15 16:00:23     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.zul"); //zul and msgzul

////
// window //
zkWnd = {};
zkWnd._szs = {} //Map(id, Draggable)
zkWnd._clean2 = {}; //Map(id, mode): to be cleanup the modal effect
zkWnd._modal2 = {}; //Map(id, todo): to do 2nd phase modaling (disable)

zkWnd.init = function (cmp) {
	zkWnd._fixHgh(cmp);
	cmp.fnResize = function () {zkWnd._fixHgh(cmp)};
	zk.addOnResize(cmp.fnResize);
	var btn = $e(cmp.id + "!close");
	if (btn) {
		zk.listen(btn, "click", function (evt) {zkau.sendOnClose(cmp, true); Event.stop(evt);});
		zk.listen(btn, "mouseover", function () {if (window.zkau) zkau.onimgover(btn);});
			//FF: at the moment of browsing to other URL, listen is still attached but
			//our js are unloaded. It causes JavaScript error though harmlessly
			//This is a dirty fix (since onclick and others still fail but hardly happen)
		zk.listen(btn, "mouseout", function () {zkau.onimgout(btn);});
		if (!btn.style.cursor) btn.style.cursor = "default";
	}

	zk.listen(cmp, "mousemove", function (evt) {if(window.zkWnd) zkWnd.onmouseove(evt, cmp);});
		//FF: at the moment of browsing to other URL, listen is still attached but
		//our js are unloaded. It causes JavaScript error though harmlessly
		//This is a dirty fix (since onclick and others still fail but hardly happen)
	zkWnd.setSizable(cmp, zkWnd.sizable(cmp));	
	
	//Bug #1840866
	var mode = getZKAttr(cmp, "mode");
	if (mode == "modal" || mode == "highlighted")
		zkWnd._initMode(cmp);
	else 
		zk.addInitLater(function () {zkWnd._initMode(cmp);}, true);	//Bug #1830668 we have to invoke initMode later.
};
zkWnd.cleanup = function (cmp) {
	zkWnd.setSizable(cmp, false);
	zkWnd._cleanMode(cmp);
	zk.rmOnResize(cmp.fnResize);
};
/** Fixed the content div's height. */
zkWnd.onVisi = zkWnd._fixHgh = function (cmp) {
	var hgh = cmp.style.height;
	if (hgh && hgh != "auto") {
		var n = $e(cmp.id + "!cave");
		if (n) zk.setOffsetHeight(n, zk.getVflexHeight(n));
	}
};
zkWnd._embedded = function (cmp) {
	var v = getZKAttr(cmp, "mode");
	return !v || v == "embedded";
};
zkWnd.setAttr = function (cmp, nm, val) {
	switch (nm) {
	case "visibility":
		var visible = val == "true",
			embedded = zkWnd._embedded(cmp),
			order = embedded ? 0: 1;

		//three cases:
		//order=0: cmp and all its ancestor are embedded
		//1: cmp is the first non-embedded, i.e., all its ancestors are embeded
		//2: cmp has an ancesor is non-embedded
		//
		//Since vparent is used if order=1, we have to handle visibility diff
		for (var n = cmp; n = $parent(n);) {
			if ($type(n) == "Wnd" && !zkWnd._embedded(n)) {
				order = 2;
				break;
			}
		}

		if (order == 1) { //with vparent
			setZKAttr(cmp, "vvisi", visible ? 't': 'f');
			visible = visible && zk.isRealVisible($parent($childExterior(cmp))); //Bug #1831534
			zkau.setAttr(cmp, nm, visible ? "true": "false");
			if (visible) zk.setVParent(cmp); //Bug 1816451
		} else {
			//order=0: might have a child with vparent, and realVisi changed
			if (order == 0 && (visible != zk.isRealVisible(cmp))) {
				for (var id in zk._vpts)
					if (zk.isAncestor(cmp, id)) {
						var n = $e(id);
						if (n) {
							var vvisi = getZKAttr(n, "vvisi");
							if (vvisi != 'f') {
								var nvisi = $visible(n);
								if (nvisi != visible) {
									if (!vvisi)
										setZKAttr(n, "vvisi", nvisi ? 't': 'f');
									zkau.setAttr(n, nm, val);
								}
							}
						}
					}
			}

			rmZKAttr(cmp, "vvisi"); //just in case
			zkau.setAttr(cmp, nm, val);
		}
		if (!embedded) zkau.hideCovered(); //Bug 1719826
		return true;

	case "z.sizable":
		zkau.setAttr(cmp, nm, val);
		zkWnd.setSizable(cmp, val == "true");
		return true;

	case "z.cntStyle":
		var n = $e(cmp.id + "!cave");
		if (n) {
			zk.setStyle(n, val != null ? val: "");
			zkWnd._fixHgh(cmp); //border's dimension might be changed
		}
		return true;  //no need to store z.cntType
	case "z.cntScls":
		var n = $e(cmp.id + "!cave");
		if (n) {
			n.className = val != null ? val: "";
			zkWnd._fixHgh(cmp); //border's dimension might be changed
		}
		return true; //no need to store it

	case "z.pos":
		var pos = getZKAttr(cmp, "pos");
		zkau.setAttr(cmp, nm, val);
		if (val && !zkWnd._embedded(cmp)) {
			if (pos == "parent" && val != pos) {
				var left = cmp.style.left, top = cmp.style.top;
				var xy = getZKAttr(cmp, "offset").split(",");
				left = $int(left) - $int(xy[0]) + "px";
				top = $int(top) - $int(xy[1]) + "px";
				cmp.style.left = left;
				cmp.style.top = top;
				rmZKAttr(cmp, "offset");
			} else if (val == "parent") {
				var parent = zk.isVParent(cmp);
				if (parent) {
					var xy = zk.revisedOffset(parent),
						left = $int(cmp.style.left), top = $int(cmp.style.top);
					setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
					cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
					cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
				}
			}
			zkWnd._center(cmp, null, val);
			//if val is null, it means no change at all
			zkau.hideCovered(); //Bug 1719826 
		}
		return true;

	case "style":
	case "style.height":
		zkau.setAttr(cmp, nm, val);
		zkWnd._fixHgh(cmp);
		if (nm == "style.height") {
			zk.onResize(0, cmp);// Note: IE6 is broken, because its offsetHeight doesn't update.	
		}
		return true;
	case "style.width":
		zk.onResize(0, cmp);
		return false;
	case "style.top":
	case "style.left":
		if (!zkWnd._embedded(cmp) && getZKAttr(cmp, "pos") == "parent") {
			var offset = getZKAttr(cmp, "offset");
			if (offset) {
				var xy = offset.split(",");
				if (nm == "style.top") {
					cmp.style.top = $int(xy[1]) + $int(val) + "px";
				} else {
					cmp.style.left = $int(xy[0]) + $int(val) + "px";
				}
				return true;
			}
		}
	}
	return false;
};

////////
// Handle sizable window //
zkWnd.sizable = function (cmp) {
	return getZKAttr(cmp, "sizable") == "true";
};
zkWnd.setSizable = function (cmp, sizable) {
	var id = cmp.id;
	if (sizable) {
		if (!zkWnd._szs[id]) {
			var orgpos = cmp.style.position; //Bug 1679593
			zkWnd._szs[id] = new Draggable(cmp, {
				starteffect: zkau.closeFloats,
				endeffect: zkWnd._endsizing, ghosting: zkWnd._ghostsizing,
				revert: true, reverteffect: zk.voidf,
				ignoredrag: zkWnd._ignoresizing
			});
			cmp.style.position = orgpos;
		}
	} else {
		if (zkWnd._szs[id]) {
			zkWnd._szs[id].destroy();
			delete zkWnd._szs[id];
		}
	}
};
/** 0: none, 1: top, 2: right-top, 3: right, 4: right-bottom, 5: bottom,
 * 6: left-bottom, 7: left, 8: left-top
 */
zkWnd._insizer = function (cmp, x, y) {
	var ofs = Position.cumulativeOffset(cmp);
	var r = ofs[0] + cmp.offsetWidth, b = ofs[1] + cmp.offsetHeight;
	if (x - ofs[0] <= 5) {
		if (y - ofs[1] <= 5) return 8;
		else if (b - y <= 5) return 6;
		else return 7;
	} else if (r - x <= 5) {
		if (y - ofs[1] <= 5) return 2;
		else if (b - y <= 5) return 4;
		else return 3;
	} else {
		if (y - ofs[1] <= 5) return 1;
		else if (b - y <= 5) return 5;
	}
};
zkWnd.onmouseove = function (evt, cmp) {
	var target = Event.element(evt);
	if (zkWnd.sizable(cmp)) {
		var c = zkWnd._insizer(cmp, Event.pointerX(evt), Event.pointerY(evt));
		if (c) {
			zk.backupStyle(cmp, "cursor");
			cmp.style.cursor = c == 1 ? 'n-resize': c == 2 ? 'ne-resize':
				c == 3 ? 'e-resize': c == 4 ? 'se-resize':
				c == 5 ? 's-resize': c == 6 ? 'sw-resize':
				c == 7 ? 'w-resize': 'nw-resize';
		} else {
			zk.restoreStyle(cmp, "cursor");
		}
	}
};
/** Called by zkWnd._szs[]'s ignoredrag for resizing window. */
zkWnd._ignoresizing = function (cmp, pointer) {
	var dg = zkWnd._szs[cmp.id];
	if (dg) {
		var v = zkWnd._insizer(cmp, pointer[0], pointer[1]);
		if (v) {
			switch (dg.z_dir = v) {
			case 1: case 5: dg.options.constraint = 'vertical'; break;
			case 3: case 7: dg.options.constraint = 'horizontal'; break;
			default: dg.options.constraint = null;
			}
			dg.z_orgzi = cmp.style.zIndex;
			return false;
		}
	}
	return true;
};
zkWnd._endsizing = function (cmp, evt) {
	var dg = zkWnd._szs[cmp.id];
	if (!dg) return;

	if (dg.z_orgzi != null) {
		cmp.style.zIndex = dg.z_orgzi; //restore it (Bug 1619349)
		dg.z_orgzi = null
	}

	if (dg.z_szofs && (dg.z_szofs[0] || dg.z_szofs[1])) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}

		//adjust size
		setTimeout("zkWnd._resize($e('"+cmp.id+"'),"+dg.z_dir+","
			+dg.z_szofs[0]+","+dg.z_szofs[1]+",'"+keys+"')", 50);
		dg.z_dir = dg.z_szofs = null;
	}
};
zkWnd._resize = function (cmp, dir, ofsx, ofsy, keys) {
	var l, t, w = cmp.offsetWidth, h = cmp.offsetHeight;
	if (ofsy) {
		if (dir == 8 || dir <= 2) {
			h -= ofsy;
			if (h < 0) {
				ofsy = cmp.offsetHeight;
				h = 0;
			}
			t = $int(cmp.style.top) + ofsy;
		}
		if (dir >= 4 && dir <= 6) {
			h += ofsy;
			if (h < 0) h = 0;
		}
	}
	if (ofsx) {
		if (dir >= 6 && dir <= 8) {
			w -= ofsx;
			if (w < 0) {
				ofsx = cmp.offsetWidth;
				w = 0;
			}
			l = $int(cmp.style.left) + ofsx;
		}
		if (dir >= 2 && dir <= 4) {
			w += ofsx;
			if (w < 0) w = 0;
		}
	}
	if (w != cmp.offsetWidth || h != cmp.offsetHeight) {
		if (w != cmp.offsetWidth) cmp.style.width = w + "px";
		if (h != cmp.offsetHeight) {
			cmp.style.height = h + "px";
			zkWnd._fixHgh(cmp);
		}
		zkau.sendOnSize(cmp, keys);
	}
	if (l != null || t != null) {
		if (l != null) cmp.style.left = l + "px";
		if (t != null) cmp.style.top = t + "px";
		zkau.sendOnMove(cmp, keys);
	}

	if (!zkWnd._embedded(cmp))
		zkau.hideCovered();
};

/* @param ghosting whether to create or remove the ghosting
 */
zkWnd._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var ofs = zkau.beginGhostToDIV(dg);
		var html =
			'<div id="zk_ddghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;';
		if (dg.z_dir == 8 || dg.z_dir <= 2)
			html += 'border-top:3px solid darkgray;';
		if (dg.z_dir >= 2 && dg.z_dir <= 4)
			html += 'border-right:3px solid darkgray;';
		if (dg.z_dir >= 4 && dg.z_dir <= 6)
			html += 'border-bottom:3px solid darkgray;';
		if (dg.z_dir >= 6 && dg.z_dir <= 8)
			html += 'border-left:3px solid darkgray;';
		document.body.insertAdjacentHTML("afterbegin", html + '"></div>');
		dg.element = $e("zk_ddghost");
	} else {
		var org = zkau.getGhostOrgin(dg);
		if (org) {
			//calc how much window is resized
			var ofs1 = Position.cumulativeOffset(dg.element);
			var ofs2 = Position.cumulativeOffset(org);
			dg.z_szofs = [ofs1[0] - ofs2[0], ofs1[1] - ofs2[1]];
		} else {
			dg.z_szofs = null;
		}
		zkau.endGhostToDIV(dg);
	}
};

////////
// Handling Overlapped, Modal, Popup and Embedded //
zkWnd._initMode = function (cmp) {
	var mode = getZKAttr(cmp, "mode");
	var replace = zkWnd._clean2[cmp.id] == mode;
	if (replace) //replace with the same mode
		delete zkWnd._clean2[cmp.id]; //and _doXxx will handle it
	else if (zkWnd._clean2[cmp.id])
		zkWnd._cleanMode2(cmp.id, true); //replace with a new mode
	switch (mode) {
	case "modal":
	case "highlighted":
		zkWnd._doModal(cmp, replace);
		break;
	case "overlapped":
		zkWnd._doOverlapped(cmp, replace);
		break;
	case "popup":
		zkWnd._doPopup(cmp, replace);
	//default: embedded
	}
};
zkWnd._cleanMode = function (cmp) {
	var mode = getZKAttr(cmp, "mode");
	if (mode) {
		zkWnd._stick(cmp); //cleanup draggable or so
		zkWnd._clean2[cmp.id] = mode;
		setTimeout("zkWnd._cleanMode2('"+cmp.id+"')", 5);
			//don't clean immediately since it might be replaced
			//(due to invalidate)
	}
};
/** 2nd phase of cleaning mode. */
zkWnd._cleanMode2 = function (uuid, replace) {
	var mode = zkWnd._clean2[uuid];
	if (mode) {
		delete zkWnd._clean2[uuid];

		switch (mode) {
		case "modal":
		case "highlighted":
			zkWnd._endModal(uuid, replace);
			break;
		case "overlapped":
			zkWnd._endOverlapped(uuid, replace);
			break;
		case "popup":
			zkWnd._endPopup(uuid, replace);
	//default: embedded
		}
	}
};
/** Shows the window with the anima effect, if any. */
zkWnd._show = function (cmp) {
	if (getZKAttr(cmp, "conshow")) //enforce the anima effect, if any
		cmp.style.display = "none";
	zk.show(cmp);
};

//Overlap/Popup//
/** Makes the component as popup. */
zkWnd._doPopup = function (cmp, replace) {
	zkWnd._doOverpop(cmp, zkau._popups, replace);
};
/** Makes the popup component as normal. */
zkWnd._endPopup = function (uuid, replace) {
	zkWnd._endOverpop(uuid, zkau._popups, replace);
};

/** Makes the component as overlapped. */
zkWnd._doOverlapped = function (cmp, replace) {
	zkWnd._doOverpop(cmp, zkau._overlaps, replace);
};
/** Makes the popup component as normal. */
zkWnd._endOverlapped = function (uuid, replace) {
	zkWnd._endOverpop(uuid, zkau._overlaps, replace);
};

zkWnd._doOverpop = function (cmp, storage, replace) {
	var pos = getZKAttr(cmp, "pos");
	var isV = zkWnd.shallVParent(cmp);
	if (!pos && isV && !cmp.style.top && !cmp.style.left) {		
		var xy = zk.revisedOffset(cmp);
		cmp.style.left = xy[0] + "px";
		cmp.style.top = xy[1] + "px";
	} else if (pos == "parent" && isV) {
		var xy = zk.revisedOffset(cmp.parentNode),
			left = $int(cmp.style.left), top = $int(cmp.style.top);
		setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
		cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
		cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
	}
	if (isV) zk.setVParent(cmp);
	
	if (replace) {
		zkau.fixZIndex(cmp);
		zkWnd._float(cmp);
		return;
	}
	
	if (pos) zkWnd._center(cmp, null, pos); //unlike modal, change only if pos

	zkau.closeFloats(cmp);

	zkau.fixZIndex(cmp);
	zkWnd._float(cmp);
	storage.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();

	if ($visible(cmp)) //it happens when closing a modal (becomes overlap)
		zkWnd._show(cmp);

	zk.asyncFocusDown(cmp.id);
};
zkWnd._endOverpop = function (uuid, storage, replace) {
	storage.remove(uuid);		
	var cmp = $e(uuid);
	if (cmp) {
		zk.unsetVParent(cmp);
		zkau.hideCovered();
		if (!replace) zkWnd._stick(cmp);
	}
};
/** Test whether el shall become a virtual parent (when overlap/...).
 * Note: only the first overlap/... need to setVParent
 */
zkWnd.shallVParent = function (el) {
	while (el = $parent(el))
		if ($type(el) == "Wnd" && !zkWnd._embedded(el))
			return false; //only one of them shall become a virtual parent
	return true;
};
//Modal//
/** Makes the window as modal. */
zkWnd._doModal = function (cmp, replace) {
	if (replace) {
		zkWnd._float(cmp);
		return;
	}
	if (!getZKAttr(cmp, "conshow")) {
		var onshow = getZKAttr(cmp, "aos") || "appear";
		if (onshow != "z_none")
			setZKAttr(cmp, "conshow", "anima." + onshow + "($e('"+cmp.id+"'));");
	}
	//center component
	var nModals = zkau._modals.length;
	zkau.fixZIndex(cmp, true); //let fixZIndex reset topZIndex if possible
	var zi = ++zkau.topZIndex; //mask also need another index

	var pos = getZKAttr(cmp, "pos");
	if (zkWnd.shallVParent(cmp)) {
		if (pos == "parent") {
			var xy = zk.revisedOffset(cmp.parentNode),
				left = $int(cmp.style.left), top = $int(cmp.style.top);
			setZKAttr(cmp, "offset", xy[0]+ "," + xy[1]);
			cmp.style.left = xy[0] + $int(cmp.style.left) + "px";
			cmp.style.top = xy[1] + $int(cmp.style.top) + "px";
		}
		zk.setVParent(cmp);
	}
	zkWnd._center(cmp, zi, pos); //called even if pos not defined
		//show dialog first to have better response.
	
	if (!pos) {
		var top = $int(cmp.style.top), y = zk.innerY();
		if (y) {
			var y1 = top - y;
			if (y1 > 100) {
				cmp.style.top = top - (y1 - 100) + "px";
			}
		} else if (top > 100){
			cmp.style.top = "100px";
		}
	}
	zkWnd._show(cmp); //unlike other mode, it must be visible

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
		}
	}

	//position mask to be full window
	if (mask) {
		zkWnd._posMask(mask);
		mask.style.display = "block";
		mask.style.zIndex = zi - 1;
		if (zkau.currentFocus) //store it
			mask.setAttribute("zk_prevfocus", zkau.currentFocus.id);
	}

	zkau._modals.push(cmp.id);
	if (nModals == 0) {
		zk.listen(window, "resize", zkWnd._onMoveMask);
		zk.listen(window, "scroll", zkWnd._onMoveMask);
	}

	zkWnd._float(cmp);
	zk.asyncFocusDown(cmp.id);

	zkWnd._modal2[cmp.id] = true;
	setTimeout("zkWnd._doModal2('"+cmp.id+"')", 5); //process it later for better responsive
};
/** Does the 2nd phase processing of modal. */
zkWnd._doModal2 = function (uuid) {
	if (zkWnd._modal2[uuid]) {
		delete zkWnd._modal2[uuid];

		var cmp = $e(uuid);
		if (cmp) {
			zk.restoreDisabled(cmp); //there might be two or more modal dlgs
			zk.disableAll(cmp);
		}
	}
};
/** Clean up the modal component. */
zkWnd._endModal = function (uuid, replace) {
	var maskId = uuid + ".mask";
	var mask = $e(maskId);
	var prevfocusId;
	if (mask) {
		prevfocusId = mask.getAttribute("zk_prevfocus");
		zk.remove(mask);
	}

	zkau._modals.remove(uuid);
	delete zkWnd._modal2[uuid];
	
	var cmp = $e(uuid);
	if (cmp) zk.unsetVParent(cmp);
	if (zkau._modals.length == 0) {
		zk.unlisten(window, "resize", zkWnd._onMoveMask);
		zk.unlisten(window, "scroll", zkWnd._onMoveMask);
		window.onscroll = null;
		zk.restoreDisabled();
	} else {
		var lastid = zkau._modals[zkau._modals.length - 1];
		var last = $e(lastid);
		if (last) {
			zk.restoreDisabled(last);
			if (!prevfocusId && !zk.inAsyncFocus) zk.asyncFocusDown(lastid, 2);
		}
	}

	if (!replace && cmp) zkWnd._stick(cmp);

	if (prevfocusId && !zk.inAsyncFocus) zk.asyncFocus(prevfocusId, 2);
};

/** Handles onsize to re-position mask. */
zkWnd._onMoveMask = function (evt) {
	for (var j = zkau._modals.length; --j >= 0;) {
		var mask = $e(zkau._modals[j] + ".mask");
		if (mask) {
			zkWnd._posMask(mask);
			return;
		}
	}
};
/** Position the mask window. */
zkWnd._posMask = function (mask) {
	var ofs = zk.toStyleOffset(mask, zk.innerX(), zk.innerY());
	mask.style.left = ofs[0] + "px";
	mask.style.top = ofs[1] + "px";
	mask.style.width = zk.innerWidth() + "px";
	mask.style.height = zk.innerHeight() + "px";
};
/** Makes a window in the center. */
zkWnd._center = function (cmp, zi, pos) {
	if (pos == "parent") return;
	cmp.style.position = "absolute"; //just in case
	zk.center(cmp, pos);
	zkau.sendOnMove(cmp);

	if (zi || zi == 0) {
		cmp.style.zIndex = zi;
		zkau.sendOnZIndex(cmp);
		//let the server know the position. otherwise, invalidate will
		//cause it to be moved to center again
	}
}

//Utilities//
/** Makes a window movable. */
zkWnd._float = function (cmp) {
	if (cmp) {
		var handle = $e(cmp.id + "!caption");
		if (handle) {
			cmp.style.position = "absolute"; //just in case
			zul.initMovable(cmp, {
				handle: handle, starteffect: zkau.closeFloats,
				change: zkau.hideCovered,
				endeffect: zkWnd._onWndMove});
			//we don't use options.change because it is called too frequently
		}
	}
};
/** Makes a window un-movable. */
zkWnd._stick = function (cmp) {
	if (cmp) {
		zul.cleanMovable(cmp.id);
		cmp.style.position = ""; //aculous changes it to relative
	}
};

/** Called back when overlapped and popup is moved. */
zkWnd._onWndMove = function (cmp, evt) {
	var keys = "";
	if (evt) {
		if (evt.altKey) keys += 'a';
		if (evt.ctrlKey) keys += 'c';
		if (evt.shiftKey) keys += 's';
	}
	zkau.sendOnMove(cmp, keys);
};
