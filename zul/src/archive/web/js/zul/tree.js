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
		return getZKAttr(row, "item");
	},
	/** Process the setAttr command sent from the server. */
	setAttr2: function (name, value) {
		switch (name) {
		case "open":
			var j = value.indexOf(':'), row;
			if (j > 0) row = $e(value.substring(0, j));
			if (!row) {
				alert(mesg.ILLEGAL_RESPONSE+"Illegal tree open: "+value);
				return true;
			}
			var toOpen = value.substring(j + 1) == "true";
			var open = getZKAttr(row, "open") == "true";
			if (toOpen != open) {
				var img = $e(row.id + "!open");
				if (img) this._openItem(row, img, toOpen);
			}
			return true; //no more processing
		}
		return this.setAttr(name, value);
	},
	/** Overrides what is defined in zk.Selectable. */
	_doLeft: function (row) {
		if (getZKAttr(row, "open") == "true") {
			var img = $e(row.id + "!open");
			if (img) this._openItem(row, img, false);
		}
	},
	/** Overrides what is defined in zk.Selectable. */
	_doRight: function (row) {
		if (getZKAttr(row, "open") != "true") {
			var img = $e(row.id + "!open");
			if (img) this._openItem(row, img, true);
		}
	},
	/** Toggle the open/close status. */
	toggleOpen: function (evt, target) {
		var row = zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		var toOpen = getZKAttr(row, "open") != "true"; //toggle
		this._openItem(row, target, toOpen);

		var el = $e(row.id + "!sel");
		if (!el) el = $e(el + "!cm");
		if (el) zk.asyncFocus(el.id);

		Event.stop(evt);
	},
	/** Opens an item */
	_openItem: function (row, img, toOpen) {
		img.src = zk.renType(img.src, toOpen ? "open": "close");
		setZKAttr(row, "open", toOpen ? "true": "false"); //change it value

		this._showKids(row, toOpen);

		if (toOpen && this.realsize() == 0)
			this._calcSize();
			//_calcSize depends on the current size, so it is not easy
			//to make it smaller when closing some items.
			//Thus, we only handle 'enlargement', i.e., toOpen is true

		zkau.send({uuid: getZKAttr(row, "item"),
			cmd: "onOpen", data: [toOpen]},
			zkau.asapTimeout(row, "onOpen"));
			//always send since the client has to update Openable
	},
	/** Shows or hides all children
	 * @param toOpen whether to toOpen
	 */
	_showKids: function (row, toOpen, silent) {
		var uuid = getZKAttr(row, "item");
		do {
			var r = row.nextSibling;
			if ($tag(r) == "TR") {
				var pid = getZKAttr(r, "ptitem");
				if (uuid != pid) return row; //not my child

				if (!silent)
					r.style.display = toOpen ? "": "none";
				r = this._showKids(r, toOpen,
					toOpen && (silent || getZKAttr(r, "open") != "true"));
			}
		} while (row = r);
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

		var bdy = $e(cmp.id + "!body");
		if (bdy)
			zk.listen(bdy, "keydown", zkTree.bodyonkeydown);

		meta = new zk.Tree(cmp);
	}

	zkTrow._pgnt(cmp, meta.bodytbl.rows);
};
zkTree.childchg = zkTree.init;

zkTree.cleanup = function (cmp) {
	zkTrow._pgclean(cmp);
};

/** Called when a tree becomes visible because of its parent. */
zkTree.onVisi = zkTree.onSize = function (cmp) {
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
zkTree.setAttr = function (tree, name, value) {
	var meta = zkau.getMeta(tree);
	return meta && meta.setAttr2(name, value);
};

/** Called when the +/- button is clicked. */
zkTree.ontoggle = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	if (meta) meta.toggleOpen(evt, target);
};

zkTrow = {}; //Treerow
zkTrow.init = function (cmp) {
	//zk.disableSelection(cmp);
	//Tom Yeh: 20060106: side effect: unable to select textbox if turned on

	zk.listen(cmp, "click", zkTree.onclick);
	zk.listen(cmp, "keydown", zkTree.onkeydown);
	zk.listen(cmp, "mouseover", zkSel.onover);
	zk.listen(cmp, "mouseout", zkSel.onout);

	zkTrow._pgnt(cmp);
};
zkTrow.cleanup = zkTrow._pgclean = function (cmp) {
	zk.remove($e(cmp.id + "!ph"));
	zk.remove($e(cmp.id + "!pt"));
};

/** Called when _onDocCtxMnu is called. */
zkTrow.onrtclk = function (cmp) {
	var meta = zkau.getMetaByType(cmp, "Tree");
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
				if (!head) {
					//TODO
				}
			} else
				zkTrow._fixpgspan(head, ncol);
			head = null; //so it won't be removed later
		}

		if (pgi < pgc - 1) { //tail visible
			if (!tail || pgc != getZKAttr(tail, "pgc")
			|| pgi != getZKAttr(tail, "pgi")) {
				if (!tail) tail = zkTrow._genpg(cmp, rows, true);
				zk.setInnerHTML($e(cmp.id + "!pc"),
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
	var td = document.createElement("TD");
	tr.appendChild(td);

	if (rows) {
		if (end) zk.insertAfter(tr, rows[rows.length - 1]);
		else zk.insertBefore(tr, rows[0]);
	} else {
		setZKAttr(tr, "ptitem", getZKAttr(cmp, "item"));
		zk.insertAfter(tr, end ? zkTrow._lastKid(cmp): cmp);
	}

	//clone images with z.fc
	//Note: we don't clone the last image
	var n = zk.nextSibling(end ? rows ? rows[0]: cmp: tr, "TR");
	var last = null;
	for (n = n.cells[0].firstChild; n; n = n.nextSibling) {
		if (n.getAttribute) {
			if (!getZKAttr(n, "fc")) break;
			if (last) td.appendChild(last.cloneNode(true));
			last = n;
		}
	}

	//create content
	var cnt = document.createElement("SPAN");
	cnt.id = cmp.id + "!pc";
	cnt.className = "treecell-paging";
	cnt.style.width = "100%";
	td.appendChild(cnt);

	return tr;
};
zkTrow._genimg = function (uri, js) {
	return '<img src="'+zk.getUpdateURI("/web/zul/img/tree/" + uri)
		+'-off.gif" onmouseover="zkau.onimgover(event)" onmouseout="zkau.onimgout(event)" align="top" onclick="'
		+js+',event)"/>';
};
/** page up or down
 * @param index -9: top page, -1: up one page, -2: up two pages
 * 9: bottom, 1: down one page, 2: down two pages
 */
zkTrow._onpg = function (index, evt) {
	if (!evt) evt = window.event;
	var n = $outer(Event.element(evt).parentNode);

	var pgi;
	if (index == -9) pgi = 0;
	else if (index == 9) pgi = parseInt(getZKAttr(n, "pgc")) - 1;
	else {
		pgi = parseInt(getZKAttr(n, "pgi")) + index;
	}

	zkau.send({uuid: getZKAttr(n, "tch"), cmd: "onPaging", data: [pgi]});
};
/** Zoom in or out
 * @param index -1: zoom out, 1: zoom in
 */
zkTrow._onzoom = function (index, evt) {
	if (!evt) evt = window.event;
	var n = $outer(Event.element(evt).parentNode);

	zkau.send({uuid: getZKAttr(n, "tch"), cmd: "onPageSize",
		data: [parseInt(getZKAttr(n, "pgsz"))+index]});
};
/** Returns the last direct child.
 * It returns itself if there is no child at all.
 */
zkTrow._lastKid = function (row) {
	var uuid = getZKAttr(row, "item");
	var n = row;
	do {
		var r = n.nextSibling;
		if ($tag(r) == "TR") {
			var pid = getZKAttr(r, "ptitem");
			if (uuid != pid) return row; //not my child

			row = r = zkTrow._lastKid(r);
		}
	} while (n = r);
	return row;
}

zkTcfc = {}; //checkmark or the first hyperlink of treecell
zkTcfc.init = function (cmp) {
	zk.listen(cmp, "focus", zkSel.cmonfocus);
	zk.listen(cmp, "blur", zkSel.cmonblur);
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
	zkTcol.resize = function (col1, col2, icol, wd1, wd2, keys) {
		var tree = $parentByType(col1, "Tree");
		if (tree) {
			var meta = zkau.getMeta(tree);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Tcols"), icol, col1, wd1, col2, wd2, keys);
		}
	};

	//Treecols
	zkTcols = zulHdrs;
});
