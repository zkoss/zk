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
		if (el) zk.focusById(el.id);

		Event.stop(evt);
	},
	/** Opens an item */
	_openItem: function (row, img, toOpen) {
		img.src = zk.renType(img.src, toOpen ? "open": "close");
		setZKAttr(row, "open", toOpen ? "true": "false"); //change it value

		this._showChildren(row, toOpen);

		if (toOpen && this.realsize() == 0)
			this._calcSize();
			//_calcSize depends on the current size, so it is not easy
			//to make it smaller when closing some items.
			//Thus, we only handle 'enlargement', i.e., toOpen is true

		zkau.send({uuid: getZKAttr(row, "item"),
			cmd: "onOpen", data: [getZKAttr(row, "open")]},
			zkau.asapTimeout(row, "onOpen"));
	},
	/** Shows or hides all children
	 * @param toOpen whether to toOpen
	 */
	_showChildren: function (row, toOpen, silent) {
		var uuid = getZKAttr(row, "item");
		do {
			var r = row.nextSibling;
			if ($tag(r) == "TR") {
				var pid = getZKAttr(r, "ptitem");
				if (uuid != pid) return row; //not my child

				if (!silent)
					r.style.display = toOpen ? "": "none";
				r = this._showChildren(r, toOpen,
					toOpen && (silent || getZKAttr(r, "open") != "true"));
			}
			row = r;
		} while (row);
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

		new zk.Tree(cmp);
	}
};
zkTree.cleanup = zkau.cleanupMeta;
zkTree.childchg = zkTree.init;

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
};

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
	zkTcol.resize = function (cmp, icol, wd1, wd2, keys) {
		var tree = $parentByType(cmp, "Tree");
		if (tree) {
			var meta = zkau.getMeta(tree);
			if (meta) meta.resizeCol(icol, wd1, wd2, keys);
		}
	};

	//Treecols
	zkTcols = zulHdrs;
});
