/* box.js

{{IS_NOTE
	Purpose:
		box and splitter
	Description:

	History:
		Sat Jun 10 12:42:15     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
////
//Box
zkBox = {};
zkBox.setAttr = function (cmp, nm, val) {
	if ("valign" == nm) {
		var n = $e(cmp.id + "!cave");
		if (n) {
			n.vAlign = val;
			if (zk.opera) zk.reOuter(n);
			return true;
		}
	}
	return false;
};
zkBox.rmAttr = function (cmp, nm) {
	if ("valign" == nm) {
		var n = $e(cmp.id + "!cave");
		if (n) {
			n.vAlign = "";
			return true;
		}
	}
	return false;
};
zkBox.onVisi = zkBox.onHide = zkBox.onSize = function (cmp) {
	if (!getZKAttr(cmp, "hasSplt")) {
		//Note: if a child is added, box is invalidated, so safe to unwatch
		zk.unwatch(cmp, "onVisi", "onHide", "onSize");
		return;
	}

	var vert = getZKAttr(cmp, "vert");

	//Bug 1916473: with IE, we have make the whole table to fit the table
	//since IE won't fit it even if height 100% is specified
	if (zk.ie) {
		var p = cmp.parentNode;
		if ($tag(p) == "TD") {
			var nm = vert ? "height": "width",
				sz = vert ? p.clientHeight: p.clientWidth;
			if ((cmp.style[nm] == "100%" || getZKAttr(cmp, "box100")) && sz) {
				cmp.style[nm] = sz + "px";
				setZKAttr(cmp, "box100", "true");
			}
		}
	}

	//Note: we have to assign width/height fisrt
	//Otherwise, the first time dragging the splitter won't be moved
	//as expected (since style.width/height might be "")

	var nd = vert ? cmp.rows : cmp.rows[0].cells,
		total = vert ? zk.revisedSize(cmp, cmp.offsetHeight, true) :
					zk.revisedSize(cmp, cmp.offsetWidth);

	for (var i = nd.length; --i >= 0;) {
		var d = nd[i];
		if ($visible(d))
			if (vert) {
				var diff = d.offsetHeight;
				if(d.id && getZKAttr(d, "coexist")) { //TR
					//Bug 1917905: we have to manipulate height of TD in Safari
					if (d.cells.length) {
						var c = d.cells[0];
						c.style.height = zk.revisedSize(c, i ? diff: total, true) + "px";
					}
					d.style.height = ""; //just-in-case
				}
				total -= diff;
			} else {
				var diff = d.offsetWidth;
				if(d.id && getZKAttr(d, "coexist")) //TD
					d.style.width = zk.revisedSize(d, i ? diff: total) + "px";
				total -= diff;
			}
	}
};

////
zkSplt = {};

zkSplt._drags = {};
zkSplt.init = function (cmp) {
	var vert = getZKAttr(cmp, "vert"),
		p = cmp.parentNode;
	if (p) {
	//Only Splitter invalidated if sclass is changed, so we have to
	//change chdextr (see Bug 1921830)
		if (vert) p = p.parentNode; //TR
		if (p && p.id.endsWith("!chdextr")) {
			p.className = getZKAttr(cmp, "zcls") + "-outer";
			if (vert)
				cmp.parentNode.className = getZKAttr(cmp, "zcls") + "-outer-td";
		}
	}
	var snap = function (x, y) {return zkSplt._snap(cmp, x, y);};
	var drag = zkSplt._drags[cmp.id] = {
		vert: vert,
		drag: new zDraggable(cmp, {
			constraint: vert ? "vertical": "horizontal", ignoredrag: zkSplt._ignoresizing,
			ghosting: zkSplt._ghostsizing, stackup: true,
			snap: snap, endeffect: zkSplt._endDrag})
	};

	var btn = $e(cmp.id + "!btn"),
		zcls = getZKAttr(cmp, "zcls");
	if (zk.ie) {
		zk.listen(btn, "mouseover", function () {zk.addClass(btn, zcls + "-btn-visi")});
	 	zk.listen(btn, "mouseout", function () {zk.rmClass(btn, zcls + "-btn-visi")});
	}
	zk.listen(btn, "click", function () {
		zk.rmClass(btn, zcls + "-btn-visi");
		zkSplt.open(cmp, getZKAttr(cmp, "open") == "false");
	});

	zkSplt._fixbtn(cmp);
	if (getZKAttr(cmp, "open") == "false")
		zkSplt._closeAtInit(cmp);

	cmp.style.cursor = getZKAttr(cmp, "open") == "false" ? "default" : vert ? "s-resize": "e-resize";
	btn.style.cursor = "pointer";
};
zkSplt._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var pointer = zkau.beginGhostToDIV(dg);
		var html = '<div id="zk_ddghost" style="background:#AAA;position:absolute;font-size:0;line-height:0;top:'
			+pointer[1]+'px;left:'+pointer[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;"></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		dg.element = $e("zk_ddghost");
	} else {
		zkau.endGhostToDIV(dg);
	}
};
zkSplt.cleanup = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		delete zkSplt._drags[cmp.id];
		drag.drag.destroy();
	}
};
zkSplt.setAttr = function (cmp, nm, val) {
	if ("z.open" == nm) {
		zkSplt.open(cmp, val == "true", true);
		return true; //no need to store the z.open attribute
	} else if ("z.colps" == nm) {
		setZKAttr(cmp, "colps", val);
		zkSplt._fixbtn(cmp);
		zkSplt._fixsz(cmp);
		return true;
	} else if (zk.opera && "visibility" == nm) {
		zkau.setAttr(cmp, nm, val);
		var outer = $childExterior(cmp);
		if (zk.isVisible(outer)) {
			var old = outer.style.display;
			outer.style.display = "none";
			outer.style.display = old;
		}
		return true;
	}
	return false;
};
zkSplt._fixbtn = function (cmp) {
	var btn = $e(cmp.id + "!btn"),
		colps = getZKAttr(cmp, "colps");
	if (!colps || "none" == colps) {
		btn.style.display = "none";
	} else {
		var zcls = getZKAttr(cmp, "zcls"),
			vert = getZKAttr(cmp, "vert"),
			before = colps == "before";
		if (getZKAttr(cmp, "open") == "false") before = !before;

		if (vert) {
			zk.rmClass(btn, zcls + "-btn-" + (before ? "b" : "t"));
			zk.addClass(btn, zcls + "-btn-" + (before ? "t" : "b"));
		} else {
			zk.rmClass(btn, zcls + "-btn-" + (before ? "r" : "l"));
			zk.addClass(btn, zcls + "-btn-" + (before ? "l" : "r"));
		}
		btn.style.display = "";
	}
};
zkSplt._ignoresizing = function (cmp, pointer, event) {
	if (getZKAttr(cmp, "open") == "false") return true;
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var el = Event.element(event);
		if (!el || !el.id || $type(el) != "Splt") return true;
		var run = drag.run = {};
		run.org = zPos.cumulativeOffset(cmp);
		var nd = $e(cmp.id + "!chdextr");
		var tn = $tag(nd);
		run.prev = zkSplt._prev(nd, tn);
		run.next = zkSplt._next(nd, tn);
		run.z_offset = zPos.cumulativeOffset(cmp);
	}
	return false;
};
zkSplt._endDrag = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var fl = zkSplt._fixLayout(cmp);

		var run = drag.run, diff, fd;

		if (drag.vert) {
			diff = run.z_point[1];
			fd = "height";

			//We adjust height of TD if vert
			if (run.next && run.next.cells.length) run.next = run.next.cells[0];
			if (run.prev && run.prev.cells.length) run.prev = run.prev.cells[0];
		} else {
			diff = run.z_point[0];
			fd = "width";
		}
		if (!diff) return; //nothing to do

		if (run.next) zk.beforeSizeAt(run.next);
		if (run.prev) zk.beforeSizeAt(run.prev);

		if (run.next) {
			var s = $int(run.next.style[fd]);
			s -= diff;
			if (s < 0) s = 0;
			run.next.style[fd] = s + "px";
		}
		if (run.prev) {
			var s = $int(run.prev.style[fd]);
			s += diff;
			if (s < 0) s = 0;
			run.prev.style[fd] = s + "px";
		}

		if (run.next) zk.onSizeAt(run.next);
		if (run.prev) zk.onSizeAt(run.prev);

		zkSplt._unfixLayout(fl);
			//Stange (not know the cause yet): we have to put it
			//befor _fixszAll and after onSizeAt

		zkSplt._fixszAll(cmp);

		//fix all splitter's size because table might be with %
		drag.run = null;//free memory
	}
};
zkSplt._snap = function (cmp, x, y) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var run = drag.run;
		if (drag.vert) {
			if (y <= run.z_offset[1] - run.prev.offsetHeight) {
				y = run.z_offset[1] - run.prev.offsetHeight;
			} else {
				var max = run.z_offset[1] + run.next.offsetHeight - cmp.offsetHeight;
				if (y > max) y = max;
			}
		} else {
			if (x <= run.z_offset[0] - run.prev.offsetWidth) {
				x = run.z_offset[0] - run.prev.offsetWidth;
			} else {
				var max = run.z_offset[0] + run.next.offsetWidth - cmp.offsetWidth;
				if (x > max) x = max;
			}
		}
		run.z_point = [x - run.z_offset[0], y - run.z_offset[1]];
	}
	return [x, y];
};
/** Fixes the height (wd) of the specified splitter. */
zkSplt.onVisi = zkSplt.onSize = zkSplt._fixsz = function (cmp) {
	if (!zk.isRealVisible(cmp))return;

	var parent = cmp.parentNode;
	if (parent) {
		var btn = $e(cmp.id + "!btn"),
			bfcolps = "before" == getZKAttr(cmp, "colps");
		if (getZKAttr(cmp, "vert")) {
			//Note: when the browser resizes, it might adjust splitter's wd/hgh
			//Note: the real wd/hgh might be bigger than 8px (since the width
			//of total content is smaller than box's width)
			//We 'cheat' by align to top or bottom depending on z.colps
			if (bfcolps) {
				parent.vAlign = "top";
				parent.style.backgroundPosition = "top left";
			} else {
				parent.vAlign = "bottom";
				parent.style.backgroundPosition = "bottom left";
			}

			cmp.style.width = ""; // clean width
			cmp.style.width = parent.clientWidth + "px"; //all wd the same
			btn.style.marginLeft = ((cmp.offsetWidth - btn.offsetWidth) / 2)+"px";
		} else {
			if (bfcolps) {
				parent.align = "left";
				parent.style.backgroundPosition = "top left";
			} else {
				parent.align = "right";
				parent.style.backgroundPosition = "top right";
			}

			cmp.style.height = ""; // clean height
			cmp.style.height =
				(zk.safari ? parent.parentNode.clientHeight: parent.clientHeight)+"px";
				//Bug 1916332: TR's clientHeight is correct (not TD's) in Safari
			btn.style.marginTop = ((cmp.offsetHeight - btn.offsetHeight) / 2)+"px";
		}
	}
};
zkSplt.beforeSize = function (cmp) {
	cmp.style[getZKAttr(cmp, "vert") ? "width": "height"] = "";
};

/** Fixes the height (width) of all related splitter. */
zkSplt._fixszAll = function (cmp) {
	//1. find the topmost box
	var box;
	for (var p = cmp; p = p.parentNode;) //no need to use $parent
		if ($type(p) == "Box")
			box = p; //continue to the topmost one

	if (box) zkSplt._fixKidSplts(box);
	else zkSplt._fixsz(cmp);
};
zkSplt._fixKidSplts = function (n) {
	if (!$visible(n)) return;

	if ($type(n) == "Splt") zkSplt._fixsz(n);

	for (n = n.firstChild; n; n = n.nextSibling)
		zkSplt._fixKidSplts(n);
};

/** Use fix table layout */
if (zk.opera) { //only opera needs it
	zkSplt._fixLayout = function (cmp) {
		var box = $parentByType(cmp, "Box");
		if (box.style.tableLayout != "fixed") {
			var fl = [box, box.style.tableLayout];
			box.style.tableLayout = "fixed";
			return fl;
		}
	};
	zkSplt._unfixLayout = function (fl) {
		if (fl) fl[0].style.tableLayout = fl[1];
	};
} else
	zkSplt._fixLayout = zkSplt._unfixLayout = zk.voidf;

/**
 * For best performance, this function doesn't need to compute anything.
 */
zkSplt._closeAtInit = function (cmp) {
	var nd = $e(cmp.id + "!chdextr"), tn = $tag(nd), colps = getZKAttr(cmp, "colps");
	if (!colps || "none" == colps) return; //nothing to do

	var sib = colps == "before" ? zkSplt._prev(nd, tn): zkSplt._next(nd, tn);
	action.hide(sib, {noCallback: true});
	zkSplt._updcls(cmp);
};
// TODO: ns
zkSplt._updcls = function (cmp, open) {
//	var nm = cmp.className, j = nm.indexOf("-ns");
//	if (open) {
//		if (j >= 0) cmp.className = nm.substring(0, j);
//	} else {
//		if (j < 0) cmp.className = nm + "-ns";
//	}
	zcls = getZKAttr(cmp, "zcls");
	if(open && zk.hasClass(cmp, zcls+"-ns")) {
		zk.rmClass(cmp, zcls+"-ns");
	}
	else if (!open && !zk.hasClass(cmp, zcls+"-ns")){
		zk.addClass(cmp, zcls+"-ns");
	}
};
zkSplt.open = function (cmp, open, silent, enforce) {
	var nd = $e(cmp.id + "!chdextr");
	var tn = $tag(nd);
	if (!enforce && (getZKAttr(cmp, "open") != "false") == open)
		return; //nothing changed

	var colps = getZKAttr(cmp, "colps");
	if (!colps || "none" == colps) return; //nothing to do

	var vert = getZKAttr(cmp, "vert"),
		sib = colps == "before" ? zkSplt._prev(nd, tn): zkSplt._next(nd, tn),
		fd = vert ? "height": "width", diff;
	if (sib) {
		zk.show(sib, open); //it will call zk.onVisi(sib) or zk.onHide(sib)
		diff = $int(sib.style[fd]);
	}

	sib = colps == "before" ? zkSplt._next(nd, tn): zkSplt._prev(nd, tn);
	if (sib) {
		diff = $int(sib.style[fd]) + (open ? -diff: diff);
		if (diff < 0) diff = 0;
		sib.style[fd] = diff + "px";
		zk.onSizeAt(sib);
	}

	setZKAttr(cmp, "open", open ? "true": "false");

	cmp.style.cursor = !open ? "default" : vert ? "s-resize": "e-resize";
	zkSplt._updcls(cmp, open);

	zkSplt._fixbtn(cmp);
	zkSplt._fixszAll(cmp);

	if (!silent)
		zkau.sendasap({uuid: cmp.id, cmd: "onOpen", data: [open]});
};
/** Returns the next
 * @param tn the tag name
 */
zkSplt._next = function (n, tn) {
	return zk.nextSibling(zk.nextSibling(n, tn), tn);
};
zkSplt._prev = function (n, tn) {
	return zk.previousSibling(zk.previousSibling(n, tn), tn);
};
