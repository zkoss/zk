/* tree.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 12:57:05     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.html.sel");

//when this executes, sel.js might not be loaded yet, so we have to delay
//the creation of zk.Tree until zkTree.init
function zkTreeNewClass() {
	if (zk.Tree) return;

zk.Tree = Class.create();
Object.extend(Object.extend(zk.Tree.prototype, zk.Selectable.prototype), {
	/** Overrides what is defined in zk.Selectable. */
	getItemUuid: function (row) {
		return row.getAttribute("zk_item");
	},
	/** Process the setAttr command sent from the server. */
	setAttr2: function (name, value) {
		switch (name) {
		case "open":
			var j = value.indexOf(':'), row;
			if (j > 0) row = $(value.substring(0, j));
			if (!row) {
				alert(mesg.ILLEGAL_RESPONSE+"Illegal tree open: "+value);
				return true;
			}
			var toOpen = value.substring(j + 1) == "true";
			var open = row.getAttribute("zk_open") == "true";
			if (toOpen != open) {
				var img = $(row.id + "!open");
				if (img) this._openItem(row, img, toOpen);
			}
			return true; //no more processing
		}
		return this.setAttr(name, value);
	},
	/** Overrides what is defined in zk.Selectable. */
	_doLeft: function (row) {
		if (row.getAttribute("zk_open") == "true") {
			var img = $(row.id + "!open");
			if (img) this._openItem(row, img, false);
		}
	},
	/** Overrides what is defined in zk.Selectable. */
	_doRight: function (row) {
		if (row.getAttribute("zk_open") != "true") {
			var img = $(row.id + "!open");
			if (img) this._openItem(row, img, true);
		}
	},
	/** Toggle the open/close status. */
	toggleOpen: function (evt, target) {
		var row = zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		var toOpen = row.getAttribute("zk_open") != "true"; //toggle
		this._openItem(row, target, toOpen);

		var el = $(row.id + "!sel");
		if (!el) el = $(el + "!cm");
		if (el) zk.focusById(el.id);

		Event.stop(evt);
	},
	/** Opens an item */
	_openItem: function (row, img, toOpen) {
		img.src = zk.renType(img.src, toOpen ? "open": "close");
		row.setAttribute("zk_open", toOpen ? "true": "false"); //change it value

		this._showChildren(row, toOpen);

		if (toOpen && this.realsize() == 0)
			this._calcSize();
			//_calcSize depends on the current size, so it is not easy
			//to make it smaller when closing some items.
			//Thus, we only handle 'enlargement', i.e., toOpen is true

		zkau.send({uuid: row.getAttribute("zk_item"),
			cmd: "onOpen", data: [row.getAttribute("zk_open")]},
			zkau.asapTimeout(row, "onOpen"));
	},
	/** Shows or hides all children
	 * @param toOpen whether to toOpen
	 */
	_showChildren: function (row, toOpen) {
		var uuid = row.getAttribute("zk_item");
		for (var row = row, last = null; (row = row.nextSibling) != null;) {
			if (zk.tagName(row) == "TR") {
				var pid = row.getAttribute("zk_ptitem");
				if (uuid == pid) {
					row.style.display = toOpen ? "": "none";
					last = row;
				} else if (!last) {
					break;
				} else if (last.getAttribute("zk_item") == pid //child of last
				&& (!toOpen || last.getAttribute("zk_open") == "true")) { //hide or last is open
					this._showChildren(last, toOpen);
				}
			}
		}
	}
});
}


////
// tree //
function zkTree() {}
/** Init (and re-init) a tree. */
zkTree.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else {
		zkTreeNewClass();

		var bdy = $(cmp.id + "!body");
		if (bdy)
			Event.observe(bdy, "keydown",
				function (evt) {return zkTree.bodyonkeydown(evt);});

		new zk.Tree(cmp);
	}
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

/** Process the setAttr cmd sent from the server, and returns whether to
 * continue the processing of this cmd
 */
zkTree.setAttr = function (tree, name, value) {
	var meta = zkau.getMeta(tree);
	if (meta) meta.setAttr2(name, value);
};

/** Called when the +/- button is clicked. */
zkTree.ontoggle = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Tree");
	if (meta) meta.toggleOpen(evt, target);
};

function zkTrow() {} //Treerow
zkTrow.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {zkTree.onclick(evt);});
	Event.observe(cmp, "keydown", function (evt) {return zkTree.onkeydown(evt);});
	Event.observe(cmp, "mouseover", function () {return zkSel.onover(cmp);});
	Event.observe(cmp, "mouseout", function () {return zkSel.onout(cmp);});
};

function zkTcfc() {} //checkmark or the first hyperlink of treecell
zkTcfc.init = function (cmp) {
	Event.observe(cmp, "focus", function () {return zkSel.cmonfocus(cmp);});
	Event.observe(cmp, "blur", function () {return zkSel.cmonblur(cmp);});
}

function zkTcop() {} //the image as the open button
zkTcop.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {zkTree.ontoggle(evt);});
};
