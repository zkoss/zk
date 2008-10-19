/* widget.js

{{IS_NOTE
	Purpose:
		Widget - the UI object at the client
	Description:
		
	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.Widget = zk.$extends(zk.Object, {
	/** The UUID (readonly if inServer). */
	//uuid: null,
	/** The next sibling (readonly). */
	//nextSibling: null,
	/** The previous sibling widget (readonly). */
	//previousSibling: null,
	/** The parent (readonly).
	 */
	//parent: null,
	/** The first child widget (readonly). */
	//firstChild: null,
	/** The last child widget (readonly). */
	//lastChild: null,
	/** The page that this widget belongs to (readonly). */
	//page: null,

	/** Whether this widget has a copy at the server (readonly). */
	inServer: false,

	/** Constructor. */
	$init: function (uuid, mold) {
		this.uuid = uuid ? uuid: zk.Widget.nextUuid();
		this.mold = mold ? mold: "default";
	},
	/** Appends a child widget.
	 */
	appendChild: function (child) {
		if (child == this.lastChild)
			return;

		if (child.parent)
			child.parent.removeChild(child);

		child.parent = this;
		var p = this.lastChild;
		if (p) {
			p.nextSibling = child;
			child.previousSibling = p;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}

		//TODO: if parent belongs to DOM
	},
	/** Inserts a child widget before the specified one.
	 */
	insertBefore: function (child, sibling) {
		if (!sibling || sibling.parent != this) {
			this.appendChild(child);
			return;
		}

		if (child == sibling || child.nextSibling == sibling)
			return;

		if (child.parent)
			child.parent.removeChild(child);

		var p = sibling.previousSibling;
		if (p) {
			child.previousSibling = p;
			p.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		child.desktop = this.desktop;

		//TODO: if parent belongs to DOM
	},
	/** Removes the specified child.
	 */
	removeChild: function (child) {
		if (!child.parent)
			return;
		if (this != child.parent)
			throw "Not a child: "+child;

		var p = child.previousSibling, n = child.nextSibling;
		if (p) p.nextSibling = n;
		else this.firstChild = n;
		if (n) n.previousSibling = p;
		else this.lastChild = p;
		child.nextSibling = child.previousSibling = child.parent = null;

		child.desktop = null;

		//TODO: if parent belongs to DOM
	},

	/** Attaches the widget to the DOM tree.
	 * @param id the DOM element's ID.
	 */
	attach: function (id, options) {
		zk.debug("attach", this.uuid, id);
	}
}, {
	/** Returns the next unquie widget UUID.
	 */
	nextUuid: function () {
		return "_z_" + this._nextUuid++;
	},
	_nextUuid: 0
});

/** A ZK desktop. */
zk.Desktop = zk.$extends(zk.Object, {
	/** The type (always "#d")(readonly). */
	type: "#d",
	$init: function (dtid, updateURI) {
		var zdt = zk.Desktop, dt = zdt._dts[dtid];
		if (!dt) {
			this.id = dtid;
			this.updateURI = updateURI;
			zdt._dts[dtid] = this;
			if (!zdt._dt) zdt._dt = this; //default desktop
		} else if (updateURI)
			dt.updateURI = updateURI;
	}
},{
	/** Returns the desktop of the specified ID.
	 */
	of: function (dtid) {
		return dtid ? zk.Desktop._dts[dtid]: zk.Desktop._dt;
	},
	_dts: {}
});

/** A ZK page. */
zk.Page = zk.$extends(zk.Object, {
	/** The type (always "#p")(readonly). */
	type: "#p",
	/** The style (readonly). */
	style: "width:100%;height:100%",
	$init: function (pgid, contained) {
		this.id = pgid;
		this.node = zkDom.$(pgid); //might null
		if (contained)
			zk.Page.contained.add(this, true);
	}
},{
	/** An list of contained page (i.e., standalone but not covering
	 * the whole browser window.
	 */
	contained: []
});
