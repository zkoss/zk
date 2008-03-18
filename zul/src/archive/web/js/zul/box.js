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
	if (!getZKAttr(cmp, "hasSplt")) return;

	var vert = getZKAttr(cmp, "vert");
	var nd = vert ? cmp.rows : cmp.rows[0].cells;
	var total = vert ? cmp.offsetHeight : cmp.offsetWidth;
	for (var i = nd.length; --i >= 0;)
		if (nd[i].id && zk.isVisible(nd[i])) {
			var d = nd[i];
			if (vert) {
				if(!d.id.endsWith("!chdextr2"))
					d.style.height = zk.revisedSize(d, i == 0 ? total : d.offsetHeight, true) + "px";
				total -= d.offsetHeight;
			} else {
				if(!d.id.endsWith("!chdextr2"))
					d.style.width = zk.revisedSize(d, i == 0 ? total : d.offsetWidth) + "px";
				total -= d.offsetWidth;
			}
		}
};
////
zkSplt = {};

zkSplt._drags = {};
zkSplt.init = function (cmp) {
	var snap = function (x, y) {return zkSplt._snap(cmp, x, y);};
	var vert = getZKAttr(cmp, "vert");
	var drag = zkSplt._drags[cmp.id] = {
		vert: vert,
		drag: new Draggable(cmp, {
			constraint: vert ? "vertical": "horizontal", ignoredrag: zkSplt._ignoresizing,
			ghosting: zkSplt._ghostsizing,
			snap: snap, endeffect: zkSplt._endDrag})
	};

	var btn = $e(cmp.id + "!btn");
	if (zk.ie) {
		zk.listen(btn, "mouseover", function () {zk.addClass(btn, "splitter-btn-visi")});
	 	zk.listen(btn, "mouseout", function () {zk.rmClass(btn, "splitter-btn-visi")});
	}
	zk.listen(btn, "click", function () {		
		zk.rmClass(btn, "splitter-btn-visi");
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
		var html = '<div id="zk_ddghost" style="background:#AAA;position:absolute;top:'
			+pointer[1]+'px;left:'+pointer[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;"><img src="'+zk.getUpdateURI('/web/img/spacer.gif')
					+'"/></div>';
		document.body.insertAdjacentHTML("afterbegin", html);
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
	}
	return false;
};
zkSplt._fixbtn = function (cmp) {
	var btn = $e(cmp.id + "!btn");
	var colps = getZKAttr(cmp, "colps");
	if (!colps || "none" == colps) {
		btn.style.display = "none";
	} else {
		var vert = getZKAttr(cmp, "vert");
		var before = colps == "before";
		if (getZKAttr(cmp, "open") == "false") before = !before;
		btn.className = zk.renType(btn.className,
			vert ? before ? 't': 'b': before ? 'l': 'r');
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
		run.org = Position.cumulativeOffset(cmp);
		var nd = $e(cmp.id + "!chdextr");
		var tn = $tag(nd);
		run.prev = zkSplt._prev(nd, tn);
		run.next = zkSplt._next(nd, tn);
		run.z_offset = Position.cumulativeOffset(cmp);
	}
	return false;
};
zkSplt._endDrag = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var fl = zkSplt._fixLayout(cmp);

		var run = drag.run,
			diff = run.z_point[drag.vert ? 1 : 0],
			fd = drag.vert ? "height" : "width";

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

	var vert = getZKAttr(cmp, "vert");
	var parent = cmp.parentNode;
	if (parent) {
		//Note: when window resize, it might adjust splitter's wd (hgh)
		//if box's width is nn%. So we have to reset it to 8px
		if (vert) {
			var tr = parent.parentNode; //TR
			cmp.style.height = tr.style.height = "8px";
			cmp.style.width = ""; // clean width
			cmp.style.width = parent.clientWidth + "px"; //all wd the same
		} else {
			cmp.style.width = parent.style.width = "8px";
			var hgh = parent.clientHeight;
			if (zk.safari) { //safari: each cell has diff height and tr's hgh is 0
				for (var cells = parent.parentNode.cells, j = cells.length; --j >= 0;) {
					var h = cells[j].clientHeight;
					if (h > hgh) hgh = h;
				}
			}
			cmp.style.height = "0px"; // clean height
			cmp.style.height = hgh + "px";
		}
	}

	var btn = $e(cmp.id + "!btn");
	if (vert) btn.style.marginLeft = ((cmp.offsetWidth - btn.offsetWidth) / 2)+"px";
	else btn.style.marginTop = ((cmp.offsetHeight - btn.offsetHeight) / 2)+"px";
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

	var type = $type(n);
	if (type == "Splt") zkSplt._fixsz(n);
	else if (type == "Box") {
		var p = n.parentNode;
		if ($tag(p) == "TD") {
			var vert = getZKAttr(n, "vert"),
				nm = vert ? "height": "width",
				sz = vert ? p.clientHeight: p.clientWidth;
			if (n.style[nm] && sz)
				n.style[nm] = sz + "px";
		}
	}

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

	var vert = getZKAttr(cmp, "vert"), sib = colps == "before" ? 
		zkSplt._prev(nd, tn): zkSplt._next(nd, tn);
	sib.style.display = "none";
	zkSplt._updcls(cmp);
};
zkSplt._updcls = function (cmp, open) {
	var nm = cmp.className, j = nm.indexOf("-ns");
	if (open) {
		if (j >= 0) cmp.className = nm.substring(0, j);
	} else {
		if (j < 0) cmp.className = nm + "-ns";
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
		sib = colps == "before" ? zkSplt._prev(nd, tn): zkSplt._next(nd, tn);
	if (sib) zk.show(sib, open);
		//it will call zk.onVisi(sib) or zk.onHide(sib)

	sib = colps == "before" ? zkSplt._next(nd, tn): zkSplt._prev(nd, tn);
	if (sib) zk.onSizeAt(sib);	

	setZKAttr(cmp, "open", open ? "true": "false");

	zkSplt._fixbtn(cmp);
	zkSplt._fixszAll(cmp);
	
	cmp.style.cursor = !open ? "default" : vert ? "s-resize": "e-resize";
	zkSplt._updcls(cmp, open);
	if (!silent)
		zkau.send({uuid: cmp.id, cmd: "onOpen", data: [open]},
			zkau.asapTimeout(cmp, "onOpen"));
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