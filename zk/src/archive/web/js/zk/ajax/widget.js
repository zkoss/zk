/* widget.js

	Purpose:
		Widget - the UI object at the client
	Description:
		
	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
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
	//inServer: false,

	_visible: true,

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

		var dt = this.desktop;
		if (dt)
			this.insertChildHTML_(child, null, dt);
			
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

		child.parent = this;
		var p = sibling.previousSibling;
		if (p) {
			child.previousSibling = p;
			p.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		var dt = this.desktop;
		if (dt)
			this.insertChildHTML_(child, sibling, dt);
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

		if (child.desktop) {
			n = child.node;
			child.unbind_();
			zDom.remove(n);
		}
	},

	/** Returns whether this widget is visible. */
	isVisible: function () {
		return this._visible;
	},
	/** Sets whether this widget is visible.
	 * <p>Deriving note: it invokes {@link #setChildDomVisible_}
	 * to chage the visibility of the DOM node, if it has parent.
	 * So, override {@link #setChildDomVisible_} if you eclose
	 * with addition tags (such as TD).
	 */
	setVisible: function (visible) {
		this._visible = visible;
		var n = this.node;
		if (n) {
			var p = this.parent;
			if (p) p.setChildDomVisible_(this, visible);
			else n.style.display = visible ? '': 'none';
		}
	},
	/** Sets the visibility of the DOM node of the specified child.
	 * When it is called, this.node must be non-null.
	 * @see #setVisible
	 */
	setChildDomVisible_: function (child, visible) {
		child.node.style.display = visible ? '': 'none';
	},

	/** Returns the width of this widget. */
	getWidth: function () {
		return this._width;
	},
	/** Sets the width of this widget. */
	setWidth: function (width) {
		this._width = width;
		var n = this.node;
		if (n) n.style.width = width ? width: '';
	},
	/** Returns the height of this widget. */
	getHeight: function () {
		return this._height;
	},
	/** Sets the height of this widget. */
	setHeight: function (height) {
		this._height = height;
		var n = this.node;
		if (n) n.style.height = height ? height: '';
	},
	/** Returns the zIndex of this widget. */
	getZIndex: function () {
		return this._zIndex;
	},
	/** Sets the zIndex of this widget. */
	setZIndex: function (zIndex) {
		this._zIndex = zIndex;
		var n = this.node;
		if (n) n.style.zIndex = zIndex >= 0 ? zIndex: '';
	},
	/** Returns the left of this widget. */
	getLeft: function () {
		return this._left;
	},
	/** Sets the left of this widget. */
	setLeft: function (left) {
		this._left = left;
		var n = this.node;
		if (n) n.style.left = left ? left: '';
	},
	/** Returns the right of this widget. */
	getRight: function () {
		return this._right;
	},
	/** Sets the right of this widget. */
	setRight: function (right) {
		this._right = right;
		var n = this.node;
		if (n) n.style.right = right ? right: '';
	},
	/** Returns the tooltip text of this widget. */
	getTooltiptext: function () {
		return this._tooltiptext;
	},
	/** Sets the tooltip text of this widget. */
	setTooltiptext: function (tooltiptext) {
		this._tooltiptext = tooltiptext;
		var n = this.node;
		if (n) n.title = tooltiptext ? tooltiptext: '';
	},

	/** Returns the style of this widget. */
	getStyle: function () {
		return this._style;
	},
	/** Sets the style of this widget. */
	setStyle: function (style) {
		this._style = style;
		this.updateDomStyle_();
	},
	/** Returns the zclass (the mold's style class) of this widget. */
	getZclass: function () {
		return this._zclass;
	},
	/** Sets the zclass of this widget. */
	setZclass: function (zclass) {
		this._zclass = zclass;
		this.updateDomClass_();
	},

	/** Generates the HTML content. */
	redraw: function () {
		var s = this.$class.molds[this.mold].call(this);
		return this.prolog ? this.prolog + s: s;
	},

	/** Updates the DOM element's class.
	 */
	updateDomClass_: function () {
		var n = this.node;
		if (n) n.className = this.getDomClass_();
	},
	/** Updates the DOM element's style.
	 */
	updateDomStyle_: function () {
		var n = this.node;
		if (n) zDom.setStyle(n, zDom.parseStyle(this.getDomStyle_()));
	},

	/** Returns the style used for the DOM element.
	 * <p>Default: it is a catenation of style, width, visible and height.
	 * @param no specify properties to exclude. If omitted, it means none.
	 * For example, you don't want width to generate, call
	 * getDomStyle_({width: true});
	 */
	getDomStyle_: function (no) {
		var html = '';
		if (!this.isVisible() && (!no || !no.visible))
			html = 'display:none;';
		if (!no || !no.style) {
			var s = this.getStyle(); 
			if (s) {
				html += s;
				if (s.charAt(s.length - 1) != ';') html += ';';
			}
		}
		if (!no || !no.width) {
			var s = this.getWidth();
			if (s) html += 'width:' + s + ';';
		}
		if (!no || !no.height) {
			var s = this.getHeight();
			if (s) html += 'height:' + s + ';';
		}
		if (!no || !no.left) {
			var s = this.getLeft();
			if (s) html += 'left:' + s + ';';
		}
		if (!no || !no.right) {
			var s = this.getRight();
			if (s) html += 'right:' + s + ';';
		}
		if (!no || !no.zIndex) {
			var s = this.getZIndex();
			if (s >= 0) html += 'zIndex:' + s + ';';
		}
		return html;
	},

	/** Returns the class name used for the DOM element.
	 * <p>Default: it is a catenation of {@link #getZclass} and {@link #sclass}.
	 * @param no specify properties to exclude. If omitted, it means none.
	 * For example, you don't want zclass to generate, call
	 * getDomClass_({zclass: true});
	 */
	getDomClass_: function (no) {
		var html = '';
		if (!no || !no.sclass) {
			var s = this.sclass;
			if (s) html = s;
		}
		if (!no || !no.zclass) {
			var s = this.getZclass();
			if (s) html += (html ? ' ': '') + s;
		}
		return html;
	},
	/** An utilities to generate the attributes used in the enclosing tag
	 * of the HTML content.
	 * <p>Default: generate id, style, class, tooltiptext.
	 * @param no specify properties to exclude. If omitted, it means none.
	 * For example, you don't want DOM class and style to generate, call
	 * getDomClass_({domclass: 1, style: 1});
	 */
	getOuterAttrs_: function (no) {
		var html = !no || !no.id ? ' id="' + this.uuid + '"': '';
		if (!no || !no.style) {
			var s = this.getDomStyle_();
			if (s) html += ' style="' + s + '"';
		}
		if (!no || !no.domclass) {
			var s = this.getDomClass_();
			if (s) html += ' class="' + s + '"';
		}
		if (!no || !no.tooltiptext) {
			var s = this.getTooltiptext();
			if (s) html += ' title="' + s + '"';
		}
		return html;
	},

	/** Replaces the specified DOM element with the HTML content
	 * generated this widget.
	 * <p>In most cases, you shall use {@link #appendChild} or
	 * {@link #insertBefore} to add a wiget to the DOM tree (if
	 * the parent is already in the DOM tree).
	 * On the other hand, this method is used to replace a branch of
	 * the DOM tree (that is usually not part of widgets).
	 * @param desktop the desktop the DOM element belongs to.
	 * Optional. If null, ZK will decide it automatically.
	 */
	replaceHTML: function (n, desktop) {
		var p = this.parent;
		if (p)
			p.replaceChildHTML_(this, n, desktop);
		else {
			if (n.z_wgt) n.z_wgt.unbind_(); //unbind first (w/o removal)
			zDom.setOuterHTML(n, this.redraw());
			this.bind_(desktop);
		}
	},

	/** Replace the specified DOM element with the HTML content generated
	 * by the specified child widget ({@link #redraw}).
	 * <p>Deriving classes might override this method to modify
	 * the HTML content, such as enclosing with TD.
	 * @param child the child widget
	 * @param n the DOM element to replace with
	 */
	replaceChildHTML_: function (child, n, desktop) {
		if (n.z_wgt) n.z_wgt.unbind_(); //unbind first (w/o removal)
		zDom.setOuterHTML(n, child.redraw());
		child.bind_(desktop);
	},
	/** Inserts the HTML content generated by the specified child widget
	 * before the specified DOM element.
	 * <p>Deriving classes might override this method to modify
	 * the HTML content, such as enclosing with TD.
	 * @param child the child widget to insert
	 * @param before the child widget as the reference to insert the new child
	 * before. If null, the HTML content will be appended as child of this.node.
	 */
	insertChildHTML_: function (child, before, desktop) {
		if (before)
			zDom.insertHTMLBefore(before.node, child.redraw());
		else
			zDom.insertHTMLBeforeEnd(this.node, child.redraw());
		child.bind_(desktop);
	},

	/** Binds (aka., attaches) the widget to the DOM tree.
	 * Deriving classes might override this method to initialize the DOM
	 * elements, such as adding a DOM event listener.
	 *
	 * @param desktop the desktop the DOM element belongs to.
	 * Optional. If null, ZK will decide it automatically.
	 */
	bind_: function (desktop) {
		var n = zDom.$(this.uuid);
		if (n) {
			n.z_wgt = this;
			this.node = n;
			if (!desktop) desktop = zk.Desktop.ofNode(n);
		}
		this.desktop = desktop;

		for (var wgt = this.firstChild; wgt; wgt = wgt.nextSibling)
			wgt.bind_(desktop);
	},
	/** Detaches the widget from the DOM tree.
	 * @param remove whether to remove the associated node
	 */
	unbind_: function () {
		var n = this.node;
		if (n) {
			n.z_wgt = null;
			this.node = null;
		}
		this.desktop = null;

		for (var wgt = this.firstChild; wgt; wgt = wgt.nextSibling)
			wgt.unbind_();
	},

	/** Sets the focus to this widget.
	 * <p>Default: call child widget's focus until it returns true,
	 * or no child at all.
	 * @return whether the focus is gained to this widget.
	 */
	focus: function () {
		if (this.desktop)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w.focus())
					return true;
		return false;
	},

	//ZK event//
	/** An array of important events. An import event is an event
	 * that must be sent to the server even without event listener.
	 * It is usually about state-updating, such as onChange and onSelect.
	 * <p>Default: null if no event at all.
	 */
	//importantEvents: null,
	/** Fires a Widget event.
	 * Note: the event will be sent to the server if it is in server
	 * (@{link #inServer}), and belongs to a desktop.
	 *
	 * @param evt an instance of zk.Event
	 * @param timeout the delay before sending the non-deferrable AU request
	 * (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server.
	 */
	fire: function (evt, timeout) {
		var lsns = this._lsns[name],
			len = lsns ? lsns.length: 0;
		if (len) {
			for (var j = 0; j < len;) {
				var o = lsns[j++];
				o[name].apply(o, evt);
				if (evt.stop) return; //no more processing
			}
		}

		if (this.inServer && this.desktop) {
			var ies = this.importantEvents,
				evtnm = evt.name,
				asap = this[evtnm];
			if (asap != zk.undefined
			|| (ies != null && ies.contains(evtnm)))
				zAu.send(evt,
					asap ? timeout >= 0 ? timeout: 38: this.auDelay());
		}
	},
	/** A simpler way to fire an event. */
	fire2: function (evtnm, data, implicit, ignorable) {
		this.fire(new zk.Event(this, evtnm, data, implicit, ignorable));
	},
	/** Adds a listener to the specified event.
	 * The listener must have a method having the same name as the event.
	 * For example, if wgt.listen("onChange", lsn) is called, then
	 * lsn.onChange(evt) will be called when onChange event is fired
	 * (by {@link zk.Widget#fire}.
	 * @param overwrite whether to overwrite if the watch was added.
	 * @return true if added successfully.
	 */
	listen: function (evtnm, listener, overwrite) {
		var lsns = this._lsns[name];
		if (!lsns) lsns = this._lsns[name] = [];
		lsns.add(listener, overwrite);
	},
	/** Removes a listener from the sepcified event.
	 */
	unlisten: function (evtnm, listener) {
		var lsns = this._lsns[name];
		return lsns && lsns.remove(watch);
	},
	_lsns: {}, //listeners Map(evtnm,listener)
	/** Returns the delay before sending a deferrable event.
	 * <p>Default: -1.
	 */
	auDelay: function () {
		return -1;
	},

	//AU//
	/** Sets an attribute that is caused by an AU response (smartUpdate).
	 * <p>Default: <code>zk.set(this, nm, val)</code>
	 */
	setAttr: function (nm, val) {
		zk.set(this, nm, val);
	}
}, {
	/** Returns the widget of the specified ID, or null if not found,
	 * or the widget is attached to the DOM tree.
	 * <p>Note: null is returned if the widget is not attached to the DOM tree
	 * (i.e., not associated with an DOM element).
	 */
	$: function (uuid) {
		//No map from uuid to widget directly. rather, go thru DOM
		var n = zDom.$(uuid);
		for (; n; n = n.parentNode) {
			var wgt = n.z_wgt;
			if (wgt) return wgt;
		}
		return null;
	},

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
	/** The AU request that shall be sent. Used by au.js */
	_aureqs: [],

	$init: function (dtid, updateURI) {
		var zdt = zk.Desktop, dt = zdt.all[dtid];
		if (!dt) {
			this.id = dtid;
			this.updateURI = updateURI;
			zdt.all[dtid] = this;
			++zdt._ndt;
			if (!zdt._dt) zdt._dt = this; //default desktop
		} else if (updateURI)
			dt.updateURI = updateURI;

		zdt.cleanup();
	}
},{
	/** Returns the desktop of the specified desktop ID.
	 */
	$: function (dtid) {
		return dtid ? typeof dtid == 'string' ?
			zk.Desktop.all[dtid]: dtid: zk.Desktop._dt;
	},
	/** Returns the desktop that the specified element belongs to.
	 */
	ofNode: function (n) {
		var zdt = zk.Desktop, dts = zdt.all;
		if (zdt._ndt > 1) {
			var wgt = zk.Widget.$(n);
			if (wgt)
				for (; wgt; wgt = wgt.parent)
					if (wgt.desktop)
						return wgt.desktop;
		}
		if (zdt._dt) return zdt._dt;
		for (var dtid in dts)
			return dts[dtid];
	},
	/** A map of (String dtid, zk.Desktop dt) (readonly). */
	all: {},
	_ndt: 0,
	/** Remove desktops that are no longer valid.
	 */
	cleanup: function () {
		var zdt = zk.Desktop, dts = zdt.all;
		if (zdt._dt && zdt._dt.pgid && !zDom.$(zdt._dt.pgid)) //removed
			zdt._dt = null;
		for (var dtid in dts) {
			var dt = dts[dtid];
			if (dt.pgid && !zDom.$(dt.pgid)) { //removed
				delete dts[dtid];
				--zdt._ndt;
			} else if (!zdt._dt)
				zdt._dt = dt;
		}
	}
});

/** A ZK page. */
zk.Page = zk.$extends(zk.Widget, {//unlik server, we derive from Widget!
	/** The type (always "#p")(readonly). */
	type: "#p",

	_style: "width:100%;height:100%",

	$init: function (pgid, contained) {
		this.uuid = pgid;
		if (contained)
			zk.Page.contained.add(this, true);
	},
	redraw: function () {
		var html = '<div id="' + this.uuid + '" style="' + this.getStyle() + '">';
		for (var w = this.firstChild; w; w = w.nextSibling)
			html += w.redraw();
		return html + '</div>';
	}

},{
	/** An list of contained page (i.e., standalone but not covering
	 * the whole browser window.
	 */
	contained: []
});
