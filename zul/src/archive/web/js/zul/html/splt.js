/* splt.js

{{IS_NOTE
	Purpose:
		Splitter
	Description:
		
	History:
		Sat Jun 10 12:42:15     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function zkSplt() {}

zkSplt._drags = {};
zkSplt.init = function (cmp) {
	if (zk.agtIe) cmp.onselectstart = function () {return false;}
	else if (zk.agtNav) cmp.style["-moz-user-select"] = "none";

	var snap = function (x, y) {return zkSplt._snap(cmp, x, y);};
	var vert = cmp.getAttribute("zk_vert");
	zkSplt._drags[cmp.id] = {
		vert: vert,
		drag: new Draggable(cmp, {
			constraint: vert ? "vertical": "horizontal",
			snap: snap,
			starteffect: zkSplt._startDrag, change: zkSplt._dragging,
			endeffect: zkSplt._endDrag})
	};

	var btn = $(cmp.id + "!btn");
	Event.observe(btn, "click", function () {zkSplt._colps(cmp, btn);});

	cmp.style.backgroundImage = "url(" +zk.getUpdateURI(
		"/web/zul/img/splt/"+(vert?"v":"h")+"splt.gif") + ")";

	zkSplt._fixbtn(cmp);

	var exc = "zkSplt._resize('" + cmp.id + "')";
	Event.observe(window, "resize", function () {setTimeout(exc, 120);});
	setTimeout(exc, 120);
};
zkSplt.cleanup = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		zkSplt._drags[cmp.id] = null;
		drag.drag.destroy();
	}
};
zkSplt._resize = function (cmp) {
	cmp = $(cmp);
	if (cmp) {
		zkSplt._fixsz(cmp);

		//we have to convert auto-adjust to fix-width, or table
		//will affect the sliding
		var nd = $(cmp.id + "!chdextr");
		var tn = zk.tagName(nd);
		var vert = cmp.getAttribute("zk_vert");
		for (nd = nd.parentNode.firstChild; nd; nd = nd.nextSibling)
			if (tn == zk.tagName(nd))
				if (vert) nd.style.height = nd.offsetHeight + "px";
				else nd.style.width = nd.offsetWidth + "px";
	}
};
zkSplt._fixbtn = function (cmp) {
	var btn = $(cmp.id + "!btn");
	var colps = cmp.getAttribute("zk_colps")
	if (!colps || "none" == colps) {
		btn.style.display = "none";
	} else {
		var vert = cmp.getAttribute("zk_vert");
		var before = colps == "before";
		if (cmp.getAttribute("zk_open") == "false") before = !before;
		btn.src = zk.renType(btn.src,
			vert ? before ? 't': 'b': before ? 'l': 'r');
		btn.style.display = "";
	}
};
zkSplt._startDrag = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		drag.org = Position.cumulativeOffset(cmp);
		var nd = $(cmp.id + "!chdextr");
		var tn = zk.tagName(nd);
		for (drag.prev = nd;;) {
			drag.prev = drag.prev.previousSibling;
			if (!drag.prev) return;
			if (zk.tagName(drag.prev) == tn) break; //found
		}
		for (drag.next = nd;;) {
			drag.next = drag.next.nextSibling;
			if (!drag.next) return;
			if (zk.tagName(drag.next) == tn) break; //found
		}

		drag.box = zkau.getParentByType(nd, "Box");
	}
};
zkSplt._endDrag = function (cmp) {
	cmp.style.left = cmp.style.top = "";
		//reset since table might adjust width later
};
zkSplt._snap = function (cmp, x, y) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var ofs = Position.cumulativeOffset(drag.box);
		ofs = zk.toStylePos(cmp, ofs[0], ofs[1]);
		if (drag.vert) {
			if (y <= ofs[1]) {
				y = ofs[1];
			} else {
				var max = ofs[1] + drag.box.clientHeight - cmp.offsetHeight;
				if (y > max) y = max;
			}
		} else {
			if (x <= ofs[0]) {
				x = ofs[0];
			} else {
				var max = ofs[0] + drag.box.clientWidth - cmp.offsetWidth;
				if (x > max) x = max;
			}
		}
	}
	return [x, y];
};
zkSplt._dragging = function (drag) {
	var cmp = drag.element;
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var ofs = Position.cumulativeOffset(cmp);
		if (drag.vert) {
			var diff = ofs[1] - drag.org[1];
			if (drag.next) zkSplt._adj(drag.next, "height", -diff);
			if (drag.prev) zkSplt._adj(drag.prev, "height", diff);
		} else {
			var diff = ofs[0] - drag.org[0];
			if (drag.next) zkSplt._adj(drag.next, "width", -diff);
			if (drag.prev) zkSplt._adj(drag.prev, "width", diff);
		}
		drag.org = ofs;
		zkSplt._fixszDesc(document.body); //a lot of cell must be adjusted
	}
};
zkSplt._adj = function (n, fd, diff) {
	zkSplt._adjSplt(n, fd, diff);
	if (n) {
		var val = parseInt(n.style[fd] || "0") + diff;
		n.style[fd] = (val > 0 ? val: 0) + "px";
	}
};
/** Adjusts the width of the splitter in the opposite dir. */
zkSplt._adjSplt = function (n, fd, diff) {
	if (zk.getCompType(n) == "Splt") {
		var vert = n.getAttribute("zk_vert") != null;
		if (vert != (fd == "height")) {
			var val = parseInt(n.style[fd] || "0") + diff;
			n.style[fd] = (val > 0 ? val: 0) + "px";
		}
	}
	for (n = n.firstChild; n; n = n.nextSibling)
		zkSplt._adjSplt(n, fd, diff);
};
/** Fixes the height (wd) of any descendant splitters. */
zkSplt._fixszDesc = function (n) {
	if (zk.getCompType(n) == "Splt") zkSplt._fixsz(n);
	for (n = n.firstChild; n; n = n.nextSibling)
		zkSplt._fixszDesc(n);
};
/** Fixes the height (wd) of the specified splitter. */
zkSplt._fixsz = function (cmp) {
	var vert = cmp.getAttribute("zk_vert");
	var parent = cmp.parentNode;
	if (parent) {
		//Note: when window resize, it might adjust splitter's wd (hgh)
		//if box's width is nn%. So we have to reset it to 8px
		if (vert) {
			parent = parent.parentNode; //TR
			cmp.style.height = parent.style.height = "8px";
			cmp.style.width = parent.clientWidth + "px";
		} else {
			cmp.style.width = parent.style.width = "8px";
			cmp.style.height = parent.clientHeight + "px";
		}
	}

	var btn = $(cmp.id + "!btn");
	if (vert) btn.style.marginLeft = ((cmp.offsetWidth - btn.offsetWidth) / 2)+"px";
	else btn.style.marginTop = ((cmp.offsetHeight - btn.offsetHeight) / 2)+"px";
};


/** Collapse. */
zkSplt._colps = function (cmp, btn) {
};
