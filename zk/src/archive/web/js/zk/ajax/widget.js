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
