/* tree.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 12:57:05     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.sel");

////
//Customization
/** Returns HTML for the pagination.
 *
 * @param row the parent treerow owns this paging
 * @param pgc # of pages
 * @param pgi the active page
 * @param end whether it is the end pagination or the begin pagination
 */
if (!window.Tree_paging) { //not customized
	window.Tree_paging = function (row, pgc, pgi, pgsz, end) {
		var html = "";
		if (end) {
			html += zkTrow._genimg("dn", "zkTrow._onpg(1");
			var v = pgc - pgi;
			if (v > 2) {
				html += zkTrow._genimg(v > 3 ? "dn2": "btm", "zkTrow._onpg(2");
				if (v > 3)
					html += zkTrow._genimg("btm", "zkTrow._onpg(9");
			}
		} else {
			html += zkTrow._genimg("up", "zkTrow._onpg(-1");
			if (pgi > 1) {
				html += zkTrow._genimg(pgi > 2 ? "up2": "top", "zkTrow._onpg(-2");
				if (pgi > 2)
					html += zkTrow._genimg("top", "zkTrow._onpg(-9");
			}
		}

		html += '<img src="' + zk.getUpdateURI("/web/zul/img/tree/spacer.gif") + '" width="15" height="1"/>';

		if (pgsz < 50)
			html += zkTrow._genimg("zoomin", "zkTrow._onzoom(10");
		if (pgsz > 10)
			html += zkTrow._genimg("zoomout", "zkTrow._onzoom(-10");
		return html;
	};
}

//when this executes, sel.js might not be loaded yet, so we have to delay
//the creation of zk.Tree until zkTree.init
function zkTreeNewClass() {
	if (zk.Tree) return;

zk.Tree = Class.create();
Object.extend(Object.extend(zk.Tree.prototype, zk.Selectable.prototype), {
	/** Overrides what is defined in zk.Selectable. */
	getItemUuid: function (row) {
		return getZKAttr(row, "pitem");
	},
	/** Returns the type of the row. */
	_rowType: function () {
		return "Trow";
	},
	/** Overrides what is defined in zk.Selectable. */
	_doLeft: function (row) {
		if (zkTree.isOpen(row))
			this._openItem(row, null, false);
	},
	/** Overrides what is defined in zk.Selectable. */
	_doRight: function (row) {
		if (!zkTree.isOpen(row))
			this._openItem(row, null, true);
	},
	/** Toggle the open/close status. */
	toggleOpen: function (evt, target) {
		var row = zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		var toOpen = !zkTree.isOpen(row); //toggle
		this._openItem(row, target, toOpen);

		var el = $e(row.id + "!sel");
		if (!el) el = $e(el + "!cm");
		if (el) zk.asyncFocus(el.id);

		Event.stop(evt);
	},
	/** Opens an item */
	_openItem: function (row, img, toOpen) {
		if (!img) {
			img = $e(row.id + "!open");
			if (!img) return;
		}

		img.className = zk.renType(img.className, toOpen ? "open": "close");
		setZKAttr(row, "open", toOpen ? "true": "false"); //change it value

		this._showKids(row, toOpen);

		if (toOpen && this.realsize() == 0)
			this._calcSize();
			//_calcSize depends on the current size, so it is not easy
			//to make it smaller when closing some items.
			//Thus, we only handle 'enlargement', i.e., toOpen is true

		zkau.send({uuid: getZKAttr(row, "pitem"),
			cmd: "onOpen", data: [toOpen]},
			toOpen && getZKAttr(row, "lod") ? 38: //load-on-demand
				zkau.asapTimeout(row, "onOpen"));
			//always send since the client has to update Openable
	},
	/** Shows or hides all children
	 * @param toOpen whether to toOpen
	 */
	_showKids: function (row, toOpen, silent) {
		var uuid = getZKAttr(row, "pitem");
		do {
			var r = row.nextSibling;
			if ($tag(r) == "TR") {
				var pid = getZKAttr(r, "gpitem");
				if (!uuid || uuid != pid) return row; //not my child. Bug #1834900.

				if (!silent)
					r.style.display = toOpen ? "": "none";
				r = this._showKids(r, toOpen,
					toOpen && (silent || !zkTree.isOpen(r)));
			}
		} while (row = r);
	},
	stripe: function () { //disable stripe
	}
});
}

////
// tree //
zkTree = {};
/** Init (and re-init) a tree. */
zkTree.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else {
		zkTreeNewClass();

		meta = new zk.Tree(cmp);
		if (meta.body)
			zk.listen(meta.body, "keydown", zkTree.bodyonkeydown);
	}

	//we have re-paginate since treechild might be invalidated
	if (meta.bodytbl)
		zkTrow._pgnt(cmp, meta.bodytbl.rows);
};

zkTree.cleanup = function (cmp) {
	zkTrow._pgclean(cmp);
};

/** Called when a tree becomes visible because of its parent. */
zkTree.onVisi = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
};

/** Called when the body got a key stroke. */
zkTree.bodyonkeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	return !meta || meta.dobodykeydown(evt, target);
};
/** Called when a listitem got a key stroke. */
zkTree.onkeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	return !meta || meta.dokeydown(evt, target);
};
/** Called when mouse click. */
zkTree.onclick = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	if (meta) meta.doclick(evt, target);
};

/** Called when focus command is received. */
zkTree.focus = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._refocus();
	return true;
};
/** Process the setAttr cmd sent from the server, and returns whether to
 * continue the processing of this cmd
 */
zkTree.setAttr = function (cmp, nm, val) {
	var meta = zkau.getMeta(cmp);
	if (meta) {
		if ("z.pgInfo" == nm) {
			zkTrow._setPgInfo(cmp, val);
			if (meta.bodytbl)
				zkTrow._pgnt(cmp, meta.bodytbl.rows);
			return true;
		}
		return meta.setAttr(nm, val);
	}
};

/** Called when the +/- button is clicked. */
zkTree.ontoggle = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	if (meta) meta.toggleOpen(evt, target);
};
zkTree.isOpen = function (row) {
	return getZKAttr(row, "open") == "true";
};

zkTrow = {}; //Treerow
zkTrow.init = function (cmp) {
	//zk.disableSelection(cmp);
	//Tom Yeh: 20060106: side effect: unable to select textbox if turned on

	zk.listen(cmp, "keydown", zkTree.onkeydown);
	if (getZKAttr(cmp, "disd") != "true") {
		zk.listen(cmp, "click", zkTree.onclick);
		zk.listen(cmp, "mouseover", zkSel.onover);
		zk.listen(cmp, "mouseout", zkSel.onout);
	}
	_zktrx.init(cmp, "ptch");
	_zktrx.init(cmp, "pitem");

	var sib = getZKAttr(cmp, "tchsib");
	if (sib) _zktrx.sib[sib] = cmp.id;

	zkTrow._pgnt(cmp);
};
zkTrow.cleanup = zkTrow._pgclean = function (cmp) {
	zk.remove($e(cmp.id + "!ph"));
	zk.remove($e(cmp.id + "!pt"));

	_zktrx.cleanup(cmp, "ptch");
	_zktrx.cleanup(cmp, "pitem");
	delete _zktrx.sib[getZKAttr(cmp, "tchsib")];
};
zkTrow.setAttr = function (cmp, nm, val) {
	if ("open" == nm) {
		var toOpen = "true" == val;
		if (toOpen != zkTree.isOpen(cmp)) {
			var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
			if (meta)
				meta._openItem(cmp, null, toOpen);
		}
		return true; //no more processing
	} else if ("z.pgInfo" == nm) {
		zkTrow._setPgInfo(cmp, val);
		zkTrow._pgnt(cmp);
		return true;
	}
	return false;
};
zkTrow._setPgInfo = function (cmp, pgInfo) {
	var j = pgInfo.indexOf(','), k = pgInfo.indexOf(',', j + 1);
	setZKAttr(cmp, "pgc", pgInfo.substring(0, j).trim());
	setZKAttr(cmp, "pgi", pgInfo.substring(j + 1, k).trim());
	setZKAttr(cmp, "pgsz", pgInfo.substring(k + 1).trim());
}

/** Opens a treeitem.
 * @param {String or Element} n the ID of treeitem or treerow, or the treerow
 * itself.
 * @since 3.0.0
 */
zkTrow.open = function (n, open) {
	if (typeof n == 'string') {
		var p = $e(n);
		n = p ? p: $e(_zktrx.sib[n]);
	}
	var tree = getZKAttr(cmp, "rid") || $parentByType(n, "Tree");
	var meta = zkau.getMeta(tree);
	if (meta)
		meta._openItem(n, null, open != false);
};

/** Called when _onDocCtxMnu is called. */
zkTrow.onrtclk = function (cmp) {
	var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
	if (meta && !meta._isSelected(cmp)) meta.doclick(null, cmp);
};
/* Paginate.
 * @param cmp treerow or tree
 * @param rows all rows of the tree, null if cmp is treerow
 */
zkTrow._pgnt = function (cmp, rows) {
	var head = $e(cmp.id + "!ph"), tail = $e(cmp.id + "!pt");
	var pgc = getZKAttr(cmp, "pgc");
	if (pgc > 1) {
		var ncol = (rows ? rows[0].cells: cmp.cells).length;
		var pgsz = getZKAttr(cmp, "pgsz");
		var pgi = getZKAttr(cmp, "pgi");
		if (pgi > 0) { //head visible
			if (!head || pgc != getZKAttr(head, "pgc")
			|| pgi != getZKAttr(head, "pgi")) {
				if (!head) head = zkTrow._genpg(cmp, rows, false);
				zk.setInnerHTML($e(cmp.id + "!pch"),
					Tree_paging(cmp, pgc, pgi, pgsz, false));
			} else
				zkTrow._fixpgspan(head, ncol);
			head = null; //so it won't be removed later
		}

		if (pgi < pgc - 1) { //tail visible
			if (!tail || pgc != getZKAttr(tail, "pgc")
			|| pgi != getZKAttr(tail, "pgi")) {
				if (!tail) tail = zkTrow._genpg(cmp, rows, true);
				zk.setInnerHTML($e(cmp.id + "!pct"),
					Tree_paging(cmp, pgc, pgi, pgsz, true));
			} else
				zkTrow._fixpgspan(tail, ncol);
			tail = null; //so it won't be removed later
		}
	}

	zk.remove(head);
	zk.remove(tail);
};
/** Fixes colspan of pagination. */
zkTrow._fixpgspan = function (n, ncol) {
	if (ncol != getZKAttr(n, "ncol")) {
		n.colSpan = ncol;
		setZKAttr(n, "ncol", ncol);
	}
};
/** Generate the pagination tags.
 * @param end whether it is the end pagination or the begin pagination
 */
zkTrow._genpg = function (cmp, rows, end) {
	var tr = document.createElement("TR");
	tr.id = cmp.id + (end ? "!pt": "!ph");
	if (!rows //!rows => parent is treeitem
	&& (!zkTree.isOpen(cmp) || !$visible(cmp))) //not visible if ancestor is not open
		tr.style.display = "none"
	var td = document.createElement("TD");
	tr.appendChild(td);

	if (rows) {
		if (end) zk.insertAfter(tr, rows[rows.length - 1]);
		else zk.insertBefore(tr, rows[0]);
	} else {
		setZKAttr(tr, "gpitem", getZKAttr(cmp, "pitem"));
		zk.insertAfter(tr, end ? zkTrow._lastKid(cmp): cmp);
	}

	//clone images with z.fc
	//Note: we don't clone the last image
	if (!rows) {
		var n = zk.nextSibling(end ? cmp: tr, "TR");
		if (n.id.endsWith("!ph")) n = zk.nextSibling(n, "TR"); //skip head
		var last = null;
		for (n = n.cells[0].firstChild, n = n && n.firstChild ? n.firstChild: n; //TD/DIV/icons
		n; n = n.nextSibling) {
			if (n.getAttribute) {
				if (!getZKAttr(n, "fc") && !n.id.endsWith("!cm"))
					break;
				if (last) {
					var cloned = last.cloneNode(true);
					if (cloned.id.endsWith("!cm")) {
						cloned.id = "";
						cloned.style.visibility = "hidden";
					}
					td.appendChild(cloned);
				}
				last = n;
			}
		}
	}

	//create content
	var cnt = document.createElement("SPAN");
	cnt.id = cmp.id + "!pc" + (end ? 't': 'h');
	cnt.className = "treeitem-paging";
	cnt.style.width = "100%";
	td.appendChild(cnt);

	return tr;
};
zkTrow._genimg = function (uri, js) {
	return '<img src="'+zk.getUpdateURI("/web/zul/img/tree/" + uri + "-off.gif")
		+'" onmouseover="zkau.onimgover(event)" onmouseout="zkau.onimgout(event)" align="top" onclick="'
		+js+',event)"/>';
};
/** page up or down
 * @param index -9: top page, -1: up one page, -2: up two pages
 * 9: bottom, 1: down one page, 2: down two pages
 */
zkTrow._onpg = function (index, evt) {
	if (!evt) evt = window.event;
	var n = $outer($parent(Event.element(evt)));

	zkau.send({uuid: getZKAttr(n, "tchsib"), cmd: "onPaging",
		data: [index == -9 ? 0:
			index == 9 ? $int(getZKAttr(n, "pgc")) - 1:
			$int(getZKAttr(n, "pgi")) + index]});
};
/** Zoom in or out
 * @param index -1: zoom out, 1: zoom in
 */
zkTrow._onzoom = function (index, evt) {
	if (!evt) evt = window.event;
	var n = $outer($parent(Event.element(evt)));

	zkau.send({uuid: getZKAttr(n, "tchsib"), cmd: "onPageSize",
		data: [$int(getZKAttr(n, "pgsz")) + index]});
};
/** Returns the last direct child.
 * It returns itself if there is no child at all.
 */
zkTrow._lastKid = function (row) {
	var uuid = getZKAttr(row, "pitem");
	var n = row;
	do {
		var r = n.nextSibling;
		if ($tag(r) == "TR") {
			var pid = getZKAttr(r, "gpitem");
			if (uuid != pid) return row; //not my child

			row = r = zkTrow._lastKid(r);
		}
	} while (n = r);
	return row;
}

if (!zk.safari) {
	zkTcfc = {}; //checkmark or the first hyperlink of treecell
	zkTcfc.init = function (cmp) {
		zk.listen(cmp, "focus", zkSel.cmonfocus);
		zk.listen(cmp, "blur", zkSel.cmonblur);
	}
}

zkTcop = {}; //the image as the open button
zkTcop.init = function (cmp) {
	zk.listen(cmp, "click", zkTree.ontoggle);
};

zk.addModuleInit(function () {
	//Treecol
	//init it later because zul.js might not be loaded yet
	zkTcol = {}
	Object.extend(zkTcol, zulHdr);

	/** Resize the column. */
	zkTcol.resize = function (col1, icol, wd1, keys) {
		var meta = zkau.getMeta(getZKAttr(col1.parentNode, "rid"));
		if (meta)
			meta.resizeCol(
				$parentByType(col1, "Tcols"), icol, col1, wd1, keys);
	};

	//Treecols
	zkTcols = zulHdrs;
	
	zkTrow.initdrag = zkLit.initdrag;
	zkTrow.cleandrag = zkLit.cleandrag;
});

//Upgrade AU Engine to handle treeitem and treechildren
var _zktrx = {};
_zktrx.dom = {}; //Map(treechildren/treeitem, [treerows])
_zktrx.sib = {}; //Map(treechildren, treerow)
_zktrx.au = {};

_zktrx.init = function (trow, attr) {
	var pt = getZKAttr(trow, attr);
	if (pt) {
		var dom = _zktrx.dom[pt];
		if (!dom) dom = _zktrx.dom[pt] = [];
		dom.push(trow.id);

		if ("pitem" == attr)
			_zktrx.sib[pt] = trow.id;
	}
};
_zktrx.cleanup = function (trow, attr) {
	var pt = getZKAttr(trow, attr);
	var dom = pt ? _zktrx.dom[pt]: null;
	if (dom) {
		dom.remove(trow.id);
		if (!dom.length) delete _zktrx.dom[pt];

		if ("pitem" == attr)
			delete _zktrx.sib[pt];
	}
};

_zktrx.au.outer = zkau.cmd1.outer;
zkau.cmd1.outer = function (uuid, cmp, html) {
	if (!cmp) {
		var dom = _zktrx.dom[uuid];
		if (dom) {
			for (var j = dom.length; --j >= 0;) {
				var id = dom[j];
				var trow = $e(id);
				_zktrx._rmKids(trow); //deep first since it cleanup dom
				if (j == 0) {
					uuid = id;
					cmp = trow;
				} else {
					_zktrx.au.rm(id, trow);
				}
			}
			dom.length = 0; //clear
		} else {
			//Bug 1753216: it causes invalidate if adding more than one page
			//of treeitems to an empty treechild
			var sib = _zktrx.sib[uuid];
			if (sib) { //update an empty treechildren
				_zktrx.au.addAft(uuid, $e(sib), html);
				return; //done
			}
		}
	}
	if (cmp && html.trim()) //if treechildren has no children at all
		_zktrx.au.outer(uuid, cmp, html);
};

_zktrx.au.addAft = zkau.cmd1.addAft;
zkau.cmd1.addAft = function (uuid, cmp, html) {
	if (!cmp) {
		cmp = $e(_zktrx.sib[uuid]);
		if (cmp) cmp = zkTrow._lastKid(cmp);
	}
	_zktrx.au.addAft(uuid, cmp, html);
};
_zktrx.au.addBfr = zkau.cmd1.addBfr;
zkau.cmd1.addBfr = function (uuid, cmp, html) {
	_zktrx.au.addBfr(uuid, cmp ? cmp: $e(_zktrx.sib[uuid]), html);
};
_zktrx.au.addChd = zkau.cmd1.addChd;
zkau.cmd1.addChd = function (uuid, cmp, html) {
	if (cmp)
		_zktrx.au.addChd(uuid, cmp, html);
	else
		_zktrx.au.addAft(uuid, $e(_zktrx.sib[uuid]), html);
};

_zktrx.au.rm = zkau.cmd1.rm;
zkau.cmd1.rm = function (uuid, cmp) {
	var dom = _zktrx.dom[uuid];
	if (dom) {
		for (var j = dom.length; --j >= 0;) {
			var id = dom[j];
			var trow = $e(id);
			_zktrx._rmKids(trow); //deep first since it cleanup dom
			_zktrx.au.rm(id, trow);
		}
		dom.length = 0; //clear (just in case)
		return;
	}
	_zktrx.au.rm(uuid, cmp);
};
_zktrx._rmKids = function (trow) {
	var tchsib = trow ? getZKAttr(trow, "tchsib"): null;
	if (tchsib)
		zkau.cmd1.rm(tchsib, $e(tchsib));
};
