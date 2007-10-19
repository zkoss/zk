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

////
zkSplt = {};

zkSplt._drags = {};
zkSplt.init = function (cmp) {
	var snap = function (x, y) {return zkSplt._snap(cmp, x, y);};
	var vert = getZKAttr(cmp, "vert");
	var drag = zkSplt._drags[cmp.id] = {
		vert: vert,
		drag: new Draggable($e(cmp.id +"!cell"), {
			constraint: vert ? "vertical": "horizontal",
			ghosting: zkSplt._ghostsizing, ignoredrag: zkSplt._ignoresizing,
			snap: snap, endeffect: zkSplt._endDrag}),
		onResize: function () {zkSplt._resize2(cmp);}
	};

	var btn = $e(cmp.id + "!btn");
	zk.listen(btn, "click", function () {
		zkSplt.open(cmp, getZKAttr(cmp, "open") == "false");
	});

	var bgi = getZKAttr(cmp, "bgi");
	if (bgi) {
		bgi = zk.renType(bgi, vert ? 'v': 'h');
		cmp.style.backgroundImage = "url(" + bgi + ")";
	}

	zkSplt._fixbtn(cmp);
	if (getZKAttr(cmp, "open") == "false")
		zkSplt.open(cmp, false, true, true);

	zk.listen(window, "resize", drag.onResize);
	zkSplt._resize2(cmp);

	cmp.style.cursor = getZKAttr(cmp, "open") == "false" ? "default" : vert ? "s-resize": "e-resize";
	btn.style.cursor = "pointer";
};
zkSplt.cleanup = function (cmp) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		zk.unlisten(window, "resize", drag.onResize);
		delete zkSplt._drags[cmp.id];
		drag.drag.destroy();
	}
};
zkSplt.setAttr = function (cmp, nm, val) {
	if ("z.open" == nm) {
		zkSplt.open(cmp, val == "true", true);
		return true; //no need to store the z.open attribute
	}
	return false;
};
zkSplt.onVisi = zkSplt.onSize = zkSplt._resize = function (cmp) {
	cmp = $e(cmp);
	if (cmp) {
		zkSplt._fixsz(cmp);

		//we have to convert auto-adjust to fix-width, or table
		//will affect the sliding
		var nd = $e(cmp.id + "!chdextr");
		
		var tn = $tag(nd);
		var vert = getZKAttr(cmp, "vert");
		var p = nd.parentNode;
		var length = tn == "TR" ? p.rows.length : p.cells.length;
		for (var j = 0; j < length; ++j) {
			nd = tn == "TR" ? p.rows[j] : p.cells[j];
			if (zk.isRealVisible(nd) && $type($real(nd)) != "Splt") {
				var cell = $e($uuid(nd) + "!cell");
				nd.style.height = nd.offsetHeight + "px";
				cell.style.height = zk.revisedSize(cell, nd.offsetHeight) + "px";
				if (!vert) {
					nd.style.width = nd.offsetWidth + "px";					
					cell.style.width = zk.revisedSize(cell, nd.offsetWidth) + "px";
				}
			}
		}	
	}
};
zkSplt._resize2 = function (cmp) {
	setTimeout("zkSplt._resize('" + cmp.id + "')", 120);
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
		btn.src = zk.renType(btn.src,
			vert ? before ? 't': 'b': before ? 'l': 'r');
		btn.style.display = "";
	}
};
zkSplt._ignoresizing = function (cell, pointer) {
	var cmp = $real(cell);
	if (getZKAttr(cmp, "open") == "false") return true;
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var run = drag.run = {};
		var nd = $e(cmp.id+"!chdextr");
		var tn = $tag(nd);
		run.prev = zk.previousSibling(nd, tn);
		run.next = zk.nextSibling(nd, tn);
		var box =  $parentByType(nd, "Box");
		var ofs = zk.revisedOffset(box);
		run.box = { top : ofs[1], left : ofs[0], 
			right: ofs[0] + zk.offsetWidth(box) - zk.offsetWidth(cell),
			bottom : ofs[1] + zk.offsetHeight(box) - zk.offsetHeight(cell)};
	}
	return false;
};
zkSplt._endDrag = function (cell) {
	var cmp = $real(cell);
	var drag = zkSplt._drags[cmp.id];
	
	if (drag) {	
		var run = drag.run;
		var ofs = zk.revisedOffset(cell);
		var btn = $e(cmp.id + "!btn"); 
		if (drag.vert) {
			var diff = ofs[1] - run.z_xy[1];
			var prev = zk.offsetHeight(run.prev), next = zk.offsetHeight(run.next);		
			if (getZKAttr(cmp, "colps") != "after") {
				if (prev - diff >= 30) {
					if(next + diff <= 30) diff += 30;
					if (prev - diff > 30 && next + diff > 30) {
						if (run.next) zkSplt._adj(run.next, "height", diff);
						if (run.prev) zkSplt._adj(run.prev, "height", -diff);
					}
				} else {
					zkSplt.open(cmp, false, true);
				}
			} else {
				if (next + diff >= 30) {
					if(next - diff <= 30) diff -= 30;
					if (prev - diff > 30 && next + diff > 30) {
						if (run.next) zkSplt._adj(run.next, "height", diff);
						if (run.prev) zkSplt._adj(run.prev, "height", -diff);
					}
				} else {
					zkSplt.open(cmp, false, true);
				}
			}
		} else {
			var diff = ofs[0] - run.z_xy[0];
			var prev = zk.offsetWidth(run.prev), next = zk.offsetWidth(run.next);
			if (getZKAttr(cmp, "colps") != "after") {
				if (prev - diff >= 30) {
					if(next + diff <= 30) diff += 30;
					if (prev - diff > 30 && next + diff > 30) {
						if (run.next) zkSplt._adj(run.next, "width", diff);
						if (run.prev) zkSplt._adj(run.prev, "width", -diff);
					}
				} else {
					zkSplt.open(cmp, false, true);
				}
			} else {
				if (next + diff >= 30) {
					if(prev - diff <= 30) diff -= 30;
					if (prev - diff > 30 && next + diff > 30) {
						if (run.next) zkSplt._adj(run.next, "width", diff);
						if (run.prev) zkSplt._adj(run.prev, "width", -diff);
					}
				} else {
					zkSplt.open(cmp, false, true);
				}
			}
		}
		zkSplt._fixszAll();
		drag.run = null; //free memory
	}	
};
zkSplt._snap = function (cmp, x, y) {
	var drag = zkSplt._drags[cmp.id];
	if (drag) {
		var b = drag.run.box;
		if (drag.vert) {
			if (y < b.top) y = b.top;
			if (y > b.bottom) y = b.bottom;
		} else {
			if (x < b.left) x = b.left;
			if (x > b.right) x = b.right;
		}
		drag.run.z_xy = [x, y];
	}
	return [x, y];
};
zkSplt._adj = function (n, fd, diff) {
	zkSplt._adjSplt(n, fd, diff);
	if (n) {		
		var cell = $e($uuid(n) + "!cell");
		var td = cell.parentNode;
		var val = Math.max($int(n.style[fd]) + diff, 30);
		n.style[fd] = val + "px";
		cell.style[fd] = zk.revisedSize(cell, val) + "px";
		zk.onSizeAt(n); //notify descendants
	}
};
/** Adjusts the width of the splitter in the opposite dir. */
zkSplt._adjSplt = function (n, fd, diff) {
	if ($type(n) == "Splt") {
		var vert = getZKAttr(n, "vert") != null;
		if (vert != (fd == "height")) {			
			var cell = $e($uuid(n) + "!cell");
			var val = Math.max($int(n.style[fd]) + diff, 0);
			n.style[fd] = val + "px";
			cell.style[fd] = zk.revisedSize(cell, val) + "px";
			//No need to call zk.onSizeAt(n) since it is handled above
		}
	}
	for (n = n.firstChild; n; n = n.nextSibling)
		zkSplt._adjSplt(n, fd, diff);
};
/** Fixes the height (wd) of the specified splitter. */
zkSplt._fixsz = function (cmp) {
	var vert = getZKAttr(cmp, "vert");
	var uuid = $uuid(cmp);
	var cell = $e(uuid + "!cell");
	var parent = cell.parentNode;
	if (parent && zk.isRealVisible(parent)) {
		//Note: when window resize, it might adjust splitter's wd (hgh)
		//if box's width is nn%. So we have to reset it to 8px
		if (vert) {
			var tr = parent.parentNode; //TR
			cell.style.height = cmp.style.height = tr.style.height = "8px";
			if (!cell.style.width)
				cell.style.width = cmp.style.width = parent.clientWidth + "px"; //all wd the same
		} else {
			cell.style.width = cmp.style.width = parent.style.width = "8px";
			var hgh = parent.clientHeight;
			if (zk.safari) { //safari: each cell has diff height and tr's hgh is 0
				for (var cells = parent.parentNode.cells, j = 0; j < cells.length; ++j) {
					var h = cells[j].clientHeight;
					if (h > hgh) hgh = h;
				}
			}
			if (!cell.style.height)
				cell.style.height = cmp.style.height = hgh + "px";
		}
	}

	var btn = $e(uuid + "!btn");
	if (vert) btn.style.marginLeft = ((cmp.offsetWidth - btn.offsetWidth) / 2)+"px";
	else btn.style.marginTop = ((cmp.offsetHeight - btn.offsetHeight) / 2)+"px";
};
zkSplt._fixszAll = function () {
	for (var id in zkSplt._drags) {
		var cmp = $e(id);
		if (cmp) zkSplt._fixsz(cmp);
	}
};
zkSplt._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var ofs = zkau.beginGhostToDIV(dg);	
		var el  = dg.element.cloneNode(true);
			el.id = "zk_ddghost";
			el.style.position = "absolute";
			el.style.top = ofs[1] + "px";
			el.style.left = ofs[0] + "px";		
			document.body.appendChild(el);
			dg.element = $e("zk_ddghost");
	} else {
		zkau.endGhostToDIV(dg);
	}
};
zkSplt.open = function (cmp, open, silent, enforce) {
	var nd = $e(cmp.id + "!chdextr");
	var tn = $tag(nd);
	if (!enforce && (getZKAttr(cmp, "open") != "false") == open)
		return; //nothing changed

	var colps = getZKAttr(cmp, "colps")
	if (!colps || "none" == colps) return; //nothing to do

	var vert = getZKAttr(cmp, "vert");
	var sib = colps == "before" ?
		zk.previousSibling(nd, tn): zk.nextSibling(nd, tn);
	if (sib) zk.show(sib, open);
	setZKAttr(cmp, "open", open ? "true": "false");

	zkSplt._fixbtn(cmp);
	zkSplt._fixszAll();
	cmp.style.cursor = !open ? "default" : vert ? "s-resize": "e-resize";
	if (!silent)
		zkau.send({uuid: cmp.id, cmd: "onOpen", data: [open]},
			zkau.asapTimeout(cmp, "onOpen"));
};
