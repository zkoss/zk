/* menu2.js

{{IS_NOTE
	Purpose:

	Description:
		New trendy mold for Menu component
	History:
		Thu May 22 17:14:57 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
////
zk.FloatMenu = zClass.create();
Object.extend(Object.extend(zk.FloatMenu.prototype, zk.Floats.prototype), {
	_close: function (el) {
		// No longer need to invoke zkMenu._close later because it will cause another issue
     	// when you click the item to show the menu popup, it will close at second time.
		zkMenu2._close(el);
		// zkMenu2._close2(el); // Bug #1852304. invoke zkMenu2._close function later.
	}
});
zkMenu2 = { // menu
	init: function (cmp) {
		var anc = $e(cmp.id + "!a");
		if (zkMenu2.isTop(cmp)) {
			zk.listen(anc, "click", zkMenu2.onclick);
			zk.listen(anc, "mouseover", zkMenu2.onover);
			zk.listen(anc, "mouseout", zkMenu2.onout);
		} else {
			zk.listen(cmp, "click", zkMenu2.onclick);
			zk.listen(cmp, "mouseover", zkMenu2.onover);
			zk.listen(cmp, "mouseout", zkMenu2.onout);

			zk.listen(anc, "focus", zkau.onfocus);
			zk.listen(anc, "blur", zkau.onblur);
		}

		var pp = $e(getZKAttr(cmp, "mpop"));
		if (pp)
			zk.listen(pp, "keydown", zkMenu2.onkeydown);
		zkMenu2.fixBtn(cmp); // IE only
	},
	/**
	 * Returns the index within this UL tag of the first occurrence of the
     * specified LI tag. If it does not occur -1 is returned.
	 * @param {Object} li a menuitem as a li tag.
	 */
	indexOf: function (li) {
		for (var n = li.parentNode.firstChild, i = 0; n; n = n.nextSibling) {
			if (n == li) return i;
			if (zkMenu2.isItemActive(n)) i++;
		}
		return -1;
	},
	/**
	 * Returns whether this item is activable.
	 * @param {Object} item
	 */
	isItemActive: function (item) {
		return item && item.nodeType == 1 && $type(item) != "Menusp2" && !getZKAttr(item, "disd");
	},
	/**
	 * Removes the zcls + "-over" class name of the specified cmp, this function
	 * is only invoked on the popup-list.
	 */
	onPopupOut: function (cmp) {
		if (cmp) {
			zk.rmClass(cmp, getZKAttr(cmp, "zcls") + "-over");
		}
	},
	/**
	 * Applies the zcls + "-over" class name of the specified cmp, this function
	 * is only invoked on the popup-list.
	 */
	onPopupOver: function (cmp, index) {
		var pp = cmp.parentNode.parentNode;
		var seld = getZKAttr(pp, "seld");
		if (seld || seld === 0)
			zkMenu2.onPopupOut(zkMenu2.getItemAt(cmp.parentNode, $int(seld)).el);

		setZKAttr(pp, "seld", index === undefined ? zkMenu2.indexOf(cmp) : index);
		zk.addClass(cmp, getZKAttr(cmp, "zcls") + "-over");
	},
	/**
	 * Returns the object whose index is matched from the ul items.
	 * The object includes both el, a LI tag, and index properties.
	 * @param {Object} ul a UL tag.
	 * @param {Number} index an integer.
	 */
	getItemAt: function (ul, index) {
		for (var n = ul.firstChild, k = -1; n; n = n.nextSibling) {
			if (zkMenu2.isItemActive(n) && ++k === index)
				return {el: n, index: k};
		}
		return {};
	},
	/**
	 * Returns whether the cmp is top.
	 */
	isTop: function (cmp) {
		return cmp && getZKAttr(cmp, "top") == "true";
	},
	/** Removes the extra space (IE only) */
	fixBtn: zk.ie ? function (cmp) {
		if (zkMenu2.isTop(cmp)) {
			var btn = $e(cmp.id + "!b");
			if (!btn || !btn.innerHTML.trim()) return;
			btn.style.width = zk.getTextSize(btn, btn.innerHTML)[0] + zk.getPadBorderWidth(btn) + "px";
		}
	} : zk.voidf,
	cleanup: function (menu) {
		var pp = $e(getZKAttr(menu, "mpop"));
		if (pp && pp._shadow) {
			pp._shadow.cleanup();
			pp._shadow = null;
		}
	},
	onkeydown: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		if (!cmp) return;
		var ul = $e(cmp.id + "!cave"), idx = getZKAttr(cmp, "seld");
		zkMenu2.navigate(ul, Event.keyCode(evt), idx ? $int(idx): idx);
		Event.stop(evt);
		return false;
	},
	/**
	 * Navigates the popup-list via keyboard.
	 * @param {Object} ul a UL tag.
	 * @param {Object} keycode a code of the key event.
	 * @param {Object} index the index of the current item.
	 */
	navigate: function (ul, keycode, index) {
		var item = index || index === 0 ? zkMenu2.getItemAt(ul, index) : {};
		switch (keycode) {
			case 38: //UP
				if (item.el)
					for (item.el = item.el.previousSibling; item.el; item.el = item.el.previousSibling) {
						if (zkMenu2.isItemActive(item.el)) {
							item.index--;
							break;
						}
					}
				if (!item.el) {
					for (var n = ul.lastChild; n; n = n.previousSibling)
						if (zkMenu2.isItemActive(n)) {
							item.el = n;
							break;
						}
					item.index = -1;
					for (var n = ul.firstChild; n; n = n.nextSibling)
						if (zkMenu2.isItemActive(n)) item.index++;
				}
				zkMenu2.onPopupOver(item.el, item.index);
				return true;
			case 40: //DOWN
				if (item.el)
					for (item.el = item.el.nextSibling; item.el; item.el = item.el.nextSibling) {
						if (zkMenu2.isItemActive(item.el)) {
							item.index++;
							break;
						}
					}
				if (!item.el) {
					for (var n = ul.firstChild; n; n = n.nextSibling)
						if (zkMenu2.isItemActive(n)) {
							item.el = n;
							break;
						}
					item.index = 0;
				}
				zkMenu2.onPopupOver(item.el, item.index);
				return true;
			case 37: //LEFT
				var pp = $outer(ul);
				if (pp) {
					zkMenu2.close(pp);
					var menu = $e(getZKAttr(pp, "menuId"));
					if (menu && !zkMenu2.isTop(menu)) {
						pp = menu.parentNode.parentNode; // assume menu is a li tag.
						if (pp) {
							var anc = $e(pp.id +"!a");
							if (anc) anc.focus();
						}
					}
				}
				return true;
			case 39: //RIGHT
				if (item.el && "Menu2" == $type(item.el))
					zkMenu2.open(item.el);
				return true;
			case 13: //ENTER
				if (item.el && "Menuit2" == $type(item.el))
					zkMenuit2._onclick(item.el);
				return true;
		}
	},
	onover: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));

		if ($type(cmp) != "Menusp2") {
			if (zkMenu2.isTop(cmp))
				zk.addClass($e(cmp.id + "!a"), getZKAttr(cmp, "zcls") + "-body-over");
			else
				zkMenu2.onPopupOver(cmp);
		}
		var menubar = $parentByType(cmp, "Menubar2"),
			autodrop = !menubar || getZKAttr(menubar, "autodrop") == "true";
		if (autodrop) zkMenu2._shallClose = false;
			//turn off pending auto-close

		var popupIds = zkMenu2._pop.getFloatIds();
		if (!autodrop && popupIds.length == 0) return;

		//Close non-child menu
		for (var j = popupIds.length; --j >= 0;) {
			var pp = $e(popupIds[j]);
			if (!zk.isAncestor(cmp, pp) && !zk.isAncestor(pp, cmp))
				zkMenu2.close(pp);
		}

		zkMenu2.open(cmp, false);
	},
	onout: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		if (!zk.ie || zkMenu2.isTop(cmp) || zkMenu2._shouldDeactivate(cmp, evt))
			zkMenu2._onout(cmp);
	},
	_shouldDeactivate: function (cmp, evt) {
		var xy = zk.revisedOffset(cmp),
			x = Event.pointerX(evt),
			y = Event.pointerY(evt);
		return !(x >= xy[0] && x <= xy[0] + cmp.offsetWidth && y >= xy[1] &&
					y <= xy[1] + cmp.offsetHeight);
	},
	_onout: function (cmp, noAutoClose) {
		if ($type(cmp) != "Menusp2") {
			if (zkMenu2.isTop(cmp))
				zk.rmClass($e(cmp.id + "!a"), getZKAttr(cmp, "zcls") + "-body-over");
			else
				zkMenu2.onPopupOut(cmp);
		}

		if (!noAutoClose && zkMenu2._pop.getFloatIds().length != 0) { //nothing to do
			var menubar = $parentByType(cmp, "Menubar2");
			if (menubar && getZKAttr(menubar, "autodrop") == "true") {
				zkMenu2._shallClose = true;
				setTimeout("if (zkMenu2._shallClose) zkau.closeFloatsOf('"+menubar.id+"');", 500);
					//Bug 1852304: we use closeFloatsOf instead of closeFloats
			}
		}
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));

		if ($type(cmp) != "Menusp2")
			zk.addClass($e(cmp.id + "!a"), getZKAttr(cmp, "zcls") + "-body-seld");

		if ("Menu2" == $type(cmp)) { //note: Menuit also go thru this method
			zkMenu2.open(cmp, zkMenu2.isTop(cmp));
		}
	},
	/** Opens a menupopup belong to the specified menu.
	 * @param toggle whether to close all menu first and then open the specified menu
	 */
	open: function (menu, toggle) {
		if (toggle) zkau.closeFloats(menu);

		var popupId = getZKAttr(menu, "mpop");
		if (!popupId) {
			if ($type(menu) != "Menusp2")
				zk.rmClass($e(menu.id + "!a"), getZKAttr(menu, "zcls") + "-body-seld");
			return; //menuitem
		}

		var pp = $e(popupId);
		if (!pp) {
			zk.error(mesg.INVALID_STRUCTURE+"z.mpop not exists");
			return;
		}
		setZKAttr(pp, "menuId", menu.id);
		if (!$visible(pp)) {
			var top = zkMenu2.isTop(menu), //top-level menu
				pos = top && getZKAttr(menu, "vert") == null ? "after-start": "end_before";

			pp.style.position = "absolute"; //just in case
			zk.setVParent(pp);
			zkMenu2._open(pp, top, $e(menu, "a"), pos);

			if (zkau.asap(pp, "onOpen"))
				zkau.send({uuid: pp.id, cmd: "onOpen", data: [true, menu.id]});
		}
	},
	/** Opens the specified menupopup
	 * @param pp menupopup
	 * @param top whether it belongs to the top-level menu
	 * @param ref the reference element to position menu.
	 * @param pos how to position the menu
	 */
	_open: function (pp, top, ref, pos) {
		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		if (ref) zk.position(pp, ref, pos);
		zk.show(pp); //animation effect, if any

		if (zk.ie7 && !pp.style.width) { // Bug 2105158 and Bug 1911129
			var ul = $e(pp.id + "!cave");
			if (ul.childNodes.length)
				pp.style.width = ul.offsetWidth + zk.getPadBorderWidth(pp) + "px";
		}

		if (!pp._shadow)
			pp._shadow = new zk.Shadow(pp, {autoShow: true, stackup:  (zk.useStackup === undefined ? zk.ie6Only: zk.useStackup)});
		else pp._shadow.sync();

		zkMenu2._pop.addFloatId(pp.id);
		zkau.hideCovered();
		var anc = $e(pp.id + "!a");
		if (anc) {
			var menu = $e(getZKAttr(pp, "menuId"));
			if (zk.ie && zkMenu2.isTop(menu))
				setTimeout(function (){try {
					anc.focus();
					zkau.currentFocus = anc;
				} catch (e){}},10); // Bug 2614901
			else {
				anc.focus();
				zkau.currentFocus = anc;
			}
		}
	},
	/** Closes the menu. */
	close: function (pp) {
		zkMenu2._pop.removeFloatId(pp.id);
		zkMenu2._close(pp);
		zkau.hideCovered();
	},
	_close: function (pp) {
		pp = $e(pp);
		if (pp) {
			if (pp._shadow) pp._shadow.hide();
			pp.style.display = "none";
			zk.unsetVParent(pp);
			rmZKAttr(pp, "owner"); //it is set by au.js after calling zkMpop.context
			var menu = $e(getZKAttr(pp, "menuId"));
			if (zkMenu2.isTop(menu))
				zk.rmClass($e(menu.id + "!a"), getZKAttr(menu, "zcls") + "-body-seld");

			if (zkau.asap(pp, "onOpen"))
				zkau.send({uuid: pp.id, cmd: "onOpen", data: [false]});
				//for better performance, sent only if non-deferable

			var seld = getZKAttr(pp, "seld");
			if (seld || seld === 0) {
				zkMenu2.onPopupOut(zkMenu2.getItemAt($e(pp.id + "!cave"), $int(seld)).el);
				rmZKAttr(pp, "seld");
			}
			zk.fire(pp, "close");
		}
	}
};
if (!zkMenu2._pop)
	zkau.floats.push(zkMenu2._pop = new zk.FloatMenu()); //hook to zkau.js

zkMenuit2 = { //menuitem
	fixBtn: zkMenu2.fixBtn,
	init: function (cmp) {
		if (!getZKAttr(cmp, "disd")) {
			zk.listen(cmp, "click", zkMenuit2.onclick);
			zk.listen(cmp, "mouseover", zkMenu2.onover);
			zk.listen(cmp, "mouseout", zkMenu2.onout);
			if (zkMenu2.isTop(cmp)) { //topmost
				var anc = $e(cmp.id + "!a");
				zk.listen(anc, "focus", zkau.onfocus);
				zk.listen(anc, "blur", zkau.onblur);
			}
		} else {
			var menubar = $parentByType(cmp, "Menubar");
			if (!menubar || getZKAttr(menubar, "autodrop") == "true") {
				zk.listen(cmp, "mouseover", zkMenuit2.onover);
				zk.listen(cmp, "mouseout", zkMenu2.onout);
			}
			zk.listen($e(cmp.id + "!a"), "click", Event.stop);
		}
		zkMenuit2.fixBtn(cmp);
	},
	onover: function (evt) {
		zkMenu2._shallClose = false;
			//turn off pending auto-close
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var el = Event.element(evt);
		zkMenuit2._onclick(el);
	},
	_onclick: function (cmp) {
		cmp = $parentByType(cmp, "Menuit2");
		zkMenu2._onout(cmp, true); //Bug 1822720
			//Bug 1852304: theorectically, popup shall not appear since 'owner'
			//is hidden, but owner is menu -- so popup still show

		var anc = $e(cmp.id + "!a");
		if (zkMenu2.isTop(cmp)) anc = anc.parentNode;
		if ("javascript:;" == anc.href) {
			var cmp = $outer(anc),
				uuid = cmp.id;
			if (getZKAttr(cmp, "autock")) {
				var newval = getZKAttr(cmp, "checked") != "true";
				zkau.send({uuid: uuid, cmd: "onCheck", data: [newval]}, -1);
			}
			zkau.send({uuid: uuid, cmd: "onClick", ctl: true});
		} else {
			var t = anc.getAttribute("target"),
				overwrite = false;
			if (anc.href && !zk.isNewWindow(anc.href, t)) {
				zk.progress();
				overwrite = true;
			}
			if (zk.ie && zkMenu2.isTop(cmp) && cmp.id != anc.id) zk.go(anc.href, overwrite, t);
				// Bug #1886352 and #2154611
			//Note: we cannot eat onclick. or, <a> won't work
		}
		if (!getZKAttr(cmp, "pop")) // Bug 1852304
			zkau.closeFloats(cmp); //bug 1711822: fire onClick first
	}
};

zkMenusp2 = { //menuseparator
	init: function (cmp) {
		zk.listen(cmp, "mouseover", zkMenu2.onover);
		zk.listen(cmp, "mouseout", zkMenu2.onout);
	}
};

zkMpop2 = { //menupopup
	setAttr: zk.ie7 ? function (cmp, nm, val) {
		if (nm == "z.closemask") { // Bug 2784736
			if (!cmp.style.width) { // Bug 2105158 and Bug 1911129
				var ul = $e(cmp.id + "!cave");
				if (ul.childNodes.length)
					cmp.style.width = ul.offsetWidth + zk.getPadBorderWidth(cmp) + "px";
			}
		}
	}: zk.voidf,
	position: function (el, dim, where) {
		where = where || "overlap";
		var x = dim.left, y = dim.top,
			wd = zk.getDimension(el), hgh = wd[1]; //only width and height
		wd = wd[0];
		switch(where) {
		case "before_start":
			y -= hgh;
			break;
		case "before_end":
			y -= hgh;
			x += dim.width - wd;
			break;
		case "after_start":
			y += dim.height;
			break;
		case "after_end":
			y += dim.height;
			x += dim.width - wd;
			break;
		case "start_before":
			x -= wd;
			break;
		case "start_after":
			x -= wd;
			y += dim.height - hgh;
			break;
		case "end_before":
			x += dim.width;
			break;
		case "end_after":
			x += dim.width;
			y += dim.height - hgh;
			break;
		case "at_pointer":
			var offset = zkau._mspos;
			x = offset[0];
			y = offset[1];
			break;
		case "after_pointer":
			var offset = zkau._mspos;
			x = offset[0];
			y = offset[1] + 20;
			break;
		default: // overlap is assumed
			// nothing to do.
		}

		var scX = zk.innerX(),
			scY = zk.innerY(),
			scMaxX = scX + zk.innerWidth(),
			scMaxY = scY + zk.innerHeight();

		if (x + wd > scMaxX) x = scMaxX - wd;
		if (x < scX) x = scX;
		if (y + hgh > scMaxY) y = scMaxY - hgh;
		if (y < scY) y = scY;
		
		var ofs = zk.toStyleOffset(el, x, y);
		el.style.left = ofs[0] + "px";
		el.style.top = ofs[1] + "px";
	},
	/** Called by au.js's context menu. */
	context: function (ctx, ref, position) {
		if (!$visible(ctx)) {
			if (position) {
				ref = $e(ref);
				if (!ref) return;
				ctx.style.position = "absolute";
				zk.setVParent(ctx);
				var offs = zk.revisedOffset(ref);
				zkMpop2.position(ctx, {top: offs[1], left: offs[0], width: zk.offsetWidth(ref),
					height: zk.offsetHeight(ref)}, position);
			}
			zkMenu2._open(ctx, true);

			if (zkau.asap(ctx, "onOpen"))
				zkau.send({uuid: ctx.id, cmd: "onOpen",
					data: ref ? [true, ref.id]: [true]});
		} else if (ctx._shadow) ctx._shadow.sync();
	},
	cleanup: function (ctx) {
		if (ctx && ctx._shadow) {
			ctx._shadow.cleanup();
			ctx._shadow = null;
		}
	}
};
