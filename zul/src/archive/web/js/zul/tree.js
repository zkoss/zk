/* tree.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 12:57:05     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.sel");

////
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
	/** Returns whether the type of the row is "Trow". */
	_isRowType: function (row) {
		return $type(row) == "Trow";
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
		zk.fixOverflow(this.element);
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

				if (!silent && (r.id.indexOf("!") > -1 || getZKAttr(r, "visible") == "true"))
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
};

/** Called when a tree becomes visible because of its parent. */
zkTree.childchg = zkTree.onVisi = zkTree.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._recalcSize();
};
zkTree.beforeSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._beforeSize();
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

};
zkTrow.cleanup = zkTrow._pgclean = function (cmp) {

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
	}
	return false;
};

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

zk.addBeforeInit(function () {
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

zk.override(zkau.cmd1, "outer", _zktrx.au, function (uuid, cmp, html) {
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
});

zk.override(zkau.cmd1, "addAft", _zktrx.au, function (uuid, cmp, html) {
	if (!cmp) {
		cmp = $e(_zktrx.sib[uuid]);
		if (cmp) cmp = zkTrow._lastKid(cmp);
	}
	_zktrx.au.addAft(uuid, cmp, html);
});

zk.override(zkau.cmd1, "addBfr", _zktrx.au, function (uuid, cmp, html) {
	_zktrx.au.addBfr(uuid, cmp ? cmp: $e(_zktrx.sib[uuid]), html);
});

zk.override(zkau.cmd1, "addChd",  _zktrx.au, function (uuid, cmp, html) {
	if (cmp)
		_zktrx.au.addChd(uuid, cmp, html);
	else
		_zktrx.au.addAft(uuid, $e(_zktrx.sib[uuid]), html);
});

zk.override(zkau.cmd1, "rm",  _zktrx.au, function (uuid, cmp) {
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
});
_zktrx._rmKids = function (trow) {
	var tchsib = trow ? getZKAttr(trow, "tchsib"): null;
	if (tchsib)
		zkau.cmd1.rm(tchsib, $e(tchsib));
};
