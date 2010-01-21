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
		zkMpop2.syncSize(cmp);
	},
	cleanup: zk.ie7 ? function (cmp) {
		zkMpop2.syncSize(cmp, true);
	} : zk.voidf,
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
		zkMenuit2.cleanup(menu);
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

			if (zkau.asap(cmp, "lfclk")) {
				var arrorWidth = 12, //note : /img/menu/btn-arrow.gif : width = 12
					clk = zk.firstChild(cmp, 'TABLE'),
					clkDim = zk.getDimension(clk),
					clickArea =  clkDim[0] - arrorWidth,
					ofs = zk.revisedOffset(clk),
					clickOffsetX = evt.clientX - ofs[0];

				if (clickOffsetX > clickArea) {
					zkMenu2.open(cmp, zkMenu2.isTop(cmp));
					Event.stop(evt);
				} else {
					zkau.onclick(evt);
					if (zkMenu2.isTop(cmp)) {
						zk.rmClass($e(cmp, "a"), getZKAttr(cmp, "zcls") + "-body-seld");
					}
				}
			} else {
				zkMenu2.open(cmp, zkMenu2.isTop(cmp));
			}
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
					zkau.currentFocus = anc; // Bug 2807475
				} catch (e){}},10); // Bug 2614901
			else {
				anc.focus();
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
		zkMpop2.syncSize(cmp);
	},
	onover: function (evt) {
		zkMenu2._shallClose = false;
			//turn off pending auto-close
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var el = Event.element(evt);
		zkMenuit2._onclick(el, evt);
	},
	_onclick: function (cmp, evt) {
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

			if (zk.gecko3 && zkMenu2.isTop(cmp) && cmp.id != anc.id) {
				zk.go(anc.href, overwrite, t);
				Event.stop(evt);
				// Bug #2154611 we shall eat the onclick event, if it is FF3.
			}
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
//Bug 2925964
zkMenuit2.cleanup = zkMenusp2.cleanup = function (cmp) {
	if (!zkMenu2.isTop(cmp)) {
		var shadow = $outer(cmp.parentNode)._shadow;
		if (shadow) {
			zk.addCleanupLater(function () {
				shadow.sync();
			}, false, cmp.parentNode.id + "pp");
		}
		zkMpop2.syncSize(cmp, true);
	}
};
zkMpop2 = { //menupopup
	syncSize: zk.ie7 ? function (cmp, isClean) {
		if (!zkMenu2.isTop(cmp)) {
			var p = $outer(cmp.parentNode);
			if (p && zk.isRealVisible(p)) {
				if (!p._syncSize)
					p._syncSize = function () {
						var ul = $e(p, "cave");
						if (ul && ul.childNodes.length) {
							p.style.width = '';
							p.style.width = ul.offsetWidth
									+ zk.getPadBorderWidth(p) + "px";
							if (p._shadow)
								p._shadow.sync();
						}
					};
				if (isClean)
					zk.addCleanupLater(p._syncSize, false, p.id + "Mpop2");
				else
					zk.addInitLater(p._syncSize, false, p.id + "Mpop2");
			}
		}
	}: zk.voidf,
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
		if (ctx && ctx._syncSize)
			ctx._syncSize = null;
	}
};

zkMenubar2 = { //menubar
	init: function (cmp) {
		zkMenubar2.onVisi = zkMenubar2.onSize;
		if (!getZKAttr(cmp, "scrollable")) return;

		if (zk.ie6Only && !cmp.style.width) {
			cmp._fixWidth = "100%";
		}

		var left = $e(cmp, "left"),
			right = $e(cmp, "right");
		zk.listen(left, "click", zkMenubar2._doScroll);
		zk.listen(left, "mouseover", zkMenubar2.onover);
		zk.listen(left, "mouseout", zkMenubar2.onout);

		zk.listen(right, "click", zkMenubar2._doScroll);
		zk.listen(right, "mouseover", zkMenubar2.onover);
		zk.listen(right, "mouseout", zkMenubar2.onout);
	},
	onSize: function (cmp) {
		if (!getZKAttr(cmp, "scrollable") || !zk.isRealVisible(cmp)) return;
	
		zkMenubar2._checkScrolling(cmp);
	},
	_fixWidth: function (cmp) {
		if (cmp._fixWidth) {
			zkMenubar2._forceStyle(cmp, cmp._fixWidth);
			zkMenubar2._forceStyle(cmp, zk.revisedSize(cmp, cmp.offsetWidth) + "px");
		} else {
			zkMenubar2._forceStyle(cmp, zk.revisedSize(cmp, cmp.offsetWidth) + "px");
		}
	},
	_forceStyle: function (cmp, value) {
		if ($int(value) < 0)
			return;
		cmp.style.width = zk.ie6Only ? "0px" : "";
		cmp.style.width = value;
	},
	childchg: function (cmp) {
		if (!getZKAttr(cmp, "scrollable")) return;

		zkMenubar2._checkScrolling(cmp);
	},
	onover: function (evt) {
		if (!evt) evt = window.event;
		var evtCmp = Event.element(evt),
		 	cmp = $outer(evtCmp),
			left = $e(cmp, "left"),
			right = $e(cmp, "right"),
			zcls = getZKAttr(cmp, "zcls");

		if (!getZKAttr(cmp, "scrollable")) return;

		if (left == evtCmp) {
			zk.addClass(left, zcls + "-left-scroll-over");
		} else if (right == evtCmp) {
			zk.addClass(right, zcls + "-right-scroll-over");
		}
	},
	onout: function (evt) {
		if (!evt) evt = window.event;
		var evtCmp = Event.element(evt),
			cmp = $outer(evtCmp),
			left = $e(cmp, "left"),
			right = $e(cmp, "right"),
			zcls = getZKAttr(cmp, "zcls");

		if (!getZKAttr(cmp, "scrollable")) return;

	    if (left == evtCmp) {
			zk.rmClass(left, zcls + "-left-scroll-over");
		} else if (right == evtCmp) {
			zk.rmClass(right, zcls + "-right-scroll-over");
		}
	},
	_checkScrolling: function (cmp) {
		zk.addClass(cmp, getZKAttr(cmp, "zcls") + "-scroll");
		if (zk.ie6Only) zkMenubar2._fixWidth(cmp);
		
		var cmpWidth = zk.offsetWidth(cmp),
			body = $e(cmp, "body"),
			childs = zk.childNodes($e(cmp, "cave")),
			totalWidth = 0;

		for (var i = childs.length; i-- ;) {
			totalWidth += zk.offsetWidth(childs[i]);
		}
		var fixedSize = cmpWidth -
						zk.offsetWidth($e(cmp, "left")) -
						zk.offsetWidth($e(cmp, "right"));
		if (cmp._scrolling) {
			if (totalWidth <= cmpWidth) {
				cmp._scrolling = false;
				body.scrollLeft = 0;
				zkMenubar2._fixButtonPos(cmp);
			} else {
				body.style.width = zk.px(fixedSize);
				zkMenubar2._fixScrollPos(cmp);
			}
		} else {
			if (totalWidth > cmpWidth) {
				cmp._scrolling = true;
				zkMenubar2._fixButtonPos(cmp);
				body.style.width = zk.px(fixedSize);
			}
		}
	},
	_fixScrollPos: function (cmp) {
		var body = $e(cmp, "body"),
			childs = zk.childNodes($e(cmp, "cave"));
		if (childs[childs.length - 1].offsetLeft < body.scrollLeft) {
			var movePos = childs[childs.length - 1].offsetLeft;
			body.scrollLeft = movePos;
		}
	},
	_fixButtonPos: function (cmp) {
		var zcls = getZKAttr(cmp, "zcls"),
			body = $e(cmp, "body"),
			left = $e(cmp, "left"),
			right = $e(cmp, "right"),
			css = cmp._scrolling ? "addClass" : "rmClass";

		zk[css](cmp, zcls + "-scroll");
		zk[css](body, zcls + "-body-scroll");
		zk[css](left, zcls + "-left-scroll");
		zk[css](right, zcls + "-right-scroll");
	},
	_doScroll: function (evt) {
		if (!evt) evt = window.event;
		var target = Event.element(evt),
			cmp = $outer(target);
		zkMenubar2._scroll(cmp, target.id.endsWith("left") ? "left" : "right");
	},
	_scroll: function (cmp, direction) {

		var body = $e(cmp, "body"),
			currScrollLeft = body.scrollLeft,
			childs = zk.childNodes($e(cmp, "cave")),
			childLen = childs.length,
			movePos = 0;

		if (!getZKAttr(cmp, "scrollable") || cmp._runId || !childLen) return;

		switch (direction) {
		case "left":
			for (var i=0; i < childLen; i++) {
				if (childs[i].offsetLeft >= currScrollLeft) {
					var preChild = zk.previousSibling(childs[i], 'TD');
					if (!preChild)	return;
					movePos = currScrollLeft - (currScrollLeft - preChild.offsetLeft);
					if (isNaN(movePos)) return;
					cmp._runId = setInterval(function () {
						if(!zkMenubar2._moveTo(body, movePos)){
							clearInterval(cmp._runId);
							cmp._runId = null;
						}
					}, 10);
					return;
				}
			}
			break;
		case "right":
			for (var i=0; i < childLen; i++) {
				var currChildRight =  childs[i].offsetLeft + childs[i].offsetWidth,
					currRight = currScrollLeft + body.offsetWidth;

				if (currChildRight > currScrollLeft + body.offsetWidth) {
					movePos = currScrollLeft + (currChildRight - currRight);
					if (isNaN(movePos)) return;
					cmp._runId = setInterval(function () {
						if (!zkMenubar2._moveTo(body, movePos)) {
							clearInterval(cmp._runId);
							cmp._runId = null;
						}
					}, 10);
					return;
				}
			}
			break;
		}
	},
	_moveTo: function (body, moveDest) {
		var currPos = body.scrollLeft,
			step = 5;
		if (currPos == moveDest) return false;

		if (currPos > moveDest) {
			var setTo = currPos - step;
			body.scrollLeft = setTo < moveDest ?  moveDest : setTo;
			return true;
		} else {
			var setTo = currPos + step;
			body.scrollLeft = setTo > moveDest ? moveDest : setTo;
			return true;
		}
		return false;
	}
};