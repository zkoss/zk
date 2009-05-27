/* widget.js

	Purpose:
		Widget - the UI object at the client
	Description:
		
	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.Widget = zk.$extends(zk.Object, {
	_visible: true,
	nChildren: 0,
	bindLevel: -1,
	_mold: 'default',
	className: 'zk.Widget',

	$init: function (props) {
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._$lsns = {}; //listners registered by setListener(evtnm, fn)
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)

		this.$afterInit(function () {
			if (props) {
				var mold = props.mold;
				if (mold != null) {
					if (mold) this._mold = mold;
					delete props.mold; //avoid setMold being called
				}
				for (var nm in props)
					this.set(nm, props[nm]);
			}

			if (zk.light) { //id == uuid
				if (this.id) this.uuid = this.id; //setId was called
				else this.uuid = this.id = zk.Widget.nextUuid();
			} else if (!this.uuid) this.uuid = zk.Widget.nextUuid();
		});
	},

	$define: {
		mold: function () {
			this.rerender();
		},
		style: function () {
			this.updateDomStyle_();
		},
		sclass: _zkf = function () {
			this.updateDomClass_();
		},
		zclass: _zkf,
		width: function (v) {
			var n = this.getNode();
			if (n) n.style.width = v || '';
		},
		height: function (v) {
			var n = this.getNode();
			if (n) n.style.height = v || '';
		},
		left: function (v) {
			var n = this.getNode();
			if (n) n.style.left = v || '';
		},
		top: function (v) {
			var n = this.getNode();
			if (n) n.style.top = v || '';
		},
		tooltiptext: function (v) {
			var n = this.getNode();
			if (n) n.title = v || '';
		},

		draggable: [
			_zkf = function (v) {
				return v && "false" != v ? v: null;
			},
			function (v) {
				var n = this.getNode();
				if (this.desktop)
					if (v) this.initDrag_();
					else this.cleanDrag_();
			}
		],
		droppable: [
			_zkf,
			function (v) {
				var dropTypes;
				if (v && v != "true") {
					dropTypes = v.split(',');
					for (var j = dropTypes.length; --j >= 0;)
						if (!(dropTypes[j] = dropTypes[j].trim()))
							dropTypes.$removeAt(j);
				}
				this._dropTypes = dropTypes;
			}
		]
	},
	getSpaceOwner: function () {
		for (var w = this; w; w = w.parent)
			if (w._fellows) return w;
		return null;
	},
	getFellow: function (id, global) {
		var ow = this.getSpaceOwner();
		if (!ow) return null;

		var f = ow._fellows[id];
		return f || !global || zk.light ? f: zk.Widget._global[id];
	},
	getId: function () {
		return this.id;
	},
	setId: function (id) {
		if (id != this.id) {
			if (zk.light && this.desktop)
				throw 'id cannot be changed after bound'; //since there might be subnodes

			var $Widget = zk.Widget, old = this.id;
			if (old) {
				if (!zk.light) delete $Widget._global[id];
				$Widget._rmIdSpace(this);
			}

			this.id = id;
			if (zk.light) this.uuid = id;

			if (id) {
				if (!zk.light) $Widget._global[id] = this;
				$Widget._addIdSpace(this);
			}
		}
	},

	set: function (name, value, extra) {
		var cc;
		if (name.length > 4 && name.startsWith('$$on')) {
			var cls = this.$class,
				ime = cls._importantEvts;
			(ime || (cls._importantEvts = {}))[name.substring(2)] = value;
		} else if (name.length > 3 && name.startsWith('$on'))
			this._asaps[name.substring(1)] = value;
		else if (name.length > 2 && name.startsWith('on')
		&& (cc = name.charAt(2)) >= 'A' && cc <= 'Z')
			this.setListener([name, value]);
		else if (arguments.length >= 3)
			zk.set(this, name, value, extra);
		else
			zk.set(this, name, value);
	},
	getChildAt: function (j) {
		if (j >= 0)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
		return null;
	},
	getChildIndex: function () {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	},
	setChildren: function (children) {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
	},
	appendChild: function (child) {
		if (child == this.lastChild)
			return false;

		var oldpt = child.parent;
		if (oldpt != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			if (this._moveChild(child)) return true; //done
			oldpt.removeChild(child);
		}

		child.parent = this;
		var p = this.lastChild;
		if (p) {
			p.nextSibling = child;
			child.previousSibling = p;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}
		++this.nChildren;

		zk.Widget._addIdSpaceDown(child);

		var dt = this.desktop;
		if (dt) this.insertChildHTML_(child, null, dt);

		this.onChildAdded_(child);
		return true;
	},
	insertBefore: function (child, sibling) {
		if (!sibling || sibling.parent != this)
			return this.appendChild(child);

		if (child == sibling || child.nextSibling == sibling)
			return false;

		if (child.parent != this)
			child.beforeParentChanged_(this);

		if (child.parent) {
			if (this._moveChild(child, sibling)) return true;
			child.parent.removeChild(child);
		}

		child.parent = this;
		var p = sibling.previousSibling;
		if (p) {
			child.previousSibling = p;
			p.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		++this.nChildren;

		zk.Widget._addIdSpaceDown(child);

		var dt = this.desktop;
		if (dt) this.insertChildHTML_(child, sibling, dt);

		this.onChildAdded_(child);
		return true;
	},
	removeChild: function (child) {
		if (!child.parent)
			return false;
		if (this != child.parent)
			return false;

		child.beforeParentChanged_(null);

		var p = child.previousSibling, n = child.nextSibling;
		if (p) p.nextSibling = n;
		else this.firstChild = n;
		if (n) n.previousSibling = p;
		else this.lastChild = p;
		child.nextSibling = child.previousSibling = child.parent = null;

		--this.nChildren;

		zk.Widget._rmIdSpaceDown(child);

		if (child.desktop)
			this.removeChildHTML_(child, p);
		this.onChildRemoved_(child);
		return true;
	},
	detach: function () {
		if (this.parent) this.parent.removeChild(this);
		else {
			var cf = zk.currentFocus;
			if (cf && zUtl.isAncestor(this, cf))
				zk.currentFocus = null;
			var n = this.getNode();
			if (n) {
				this.unbind();
				zDom.remove(n);
			}
		}
	},
	_replaceWgt: function (newwgt) { //called by au's outer
		var node = this.getNode(),
			p = newwgt.parent = this.parent,
			s = newwgt.previousSibling = this.previousSibling;
		if (s) s.nextSibling = newwgt;
		else if (p) p.firstChild = newwgt;

		s = newwgt.nextSibling = this.nextSibling;
		if (s) s.previousSibling = newwgt;
		else if (p) p.lastChild = newwgt;

		if (this.desktop) {
			if (!newwgt.desktop) newwgt.desktop = this.desktop;
			if (node) newwgt.replaceHTML(node, newwgt.desktop);

			zk.Widget._fixBindLevel(newwgt, p ? p.bindLevel + 1: 0);
			zWatch.fire('onBindLevelMove', null, newwgt);
		}

		if (p) {
			p.onChildRemoved_(this);
			p.onChildAdded_(newwgt);
		}
	},
	beforeParentChanged_: function () {
	},
	domMovable_: function () {
		return true;
	},
	_moveChild: function (child, moveBefore) {
		if (child._floating || !child.domMovable_() || !this.domMovable_()
		|| !this.desktop || !child.desktop)
			return false;

		var beforeNode = null;
		if (moveBefore && !(beforeNode = moveBefore.getNode()))
			return false;

		var node = this.getNode(), kidnode = child.getNode(),
			dt = this.desktop, kiddt = child.desktop,
			oldpt = child.parent;
		child._node = this._node = child.desktop = this.desktop = null; //avoid bind_ and unbind_
		try {
			oldpt.removeChild(child);
			this.insertBefore(child, moveBefore);

			zDom.remove(kidnode);
			node.parentNode.insertBefore(kidnode, beforeNode);

			//Not calling unbind and bind, so handle bindLevel here
			var v = this.bindLevel + 1;
			if (child.bindLevel != v) {
				zk.Widget._fixBindLevel(child, v);
				zWatch.fire('onBindLevelMove', null, child);
			}
		} finally {
			this.desktop = dt; child.desktop = kiddt;
			this._node = node; child._node = kidnode;
		}

		oldpt.onChildRemoved_(child);
		this.onChildAdded_(child);
			//they are called if parent is the same
		return true;
	},

	isRealVisible: function () {
		for (var wgt = this; wgt; wgt = wgt.parent) {
			if (!wgt.isVisible()) return false;
			var n = wgt.getNode();
			if (n && !zDom.isVisible(n)) return false; //possible (such as in a hbox)
		}
		return true;
	},
	isVisible: function (strict) {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.getNode();
		return !n || zDom.isVisible(n);
	},
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent;
			if (p && visible) p.onChildVisible_(this, true); //becoming visible
			if (this.desktop) this._setVisible(visible);
			if (p && !visible) p.onChildVisible_(this, false); //become invisible
		}
	},
	_setVisible: function (visible) {
		var parent = this.parent,
			parentVisible = !parent || parent.isRealVisible(),
			node = this.getNode(),
			floating = this._floating;

		if (!parentVisible) {
			if (!floating) this.setDomVisible_(node, visible);
			return;
		}

		if (visible) {
			var zi;
			if (floating)
				this._setZIndex(zi = this._topZIndex(), true);

			this.setDomVisible_(node, true);

			//from parent to child
			for (var fs = zk.Widget._floatings, j = 0, fl = fs.length; j < fl; ++j) {
				var w = fs[j].widget;
				if (this != w && this._floatVisibleDependent(w)) {
					zi = zi >= 0 ? ++zi: w._topZIndex();
					var n = fs[j].node;
					if (n != w.getNode()) w.setFloatZIndex_(n, zi); //only a portion
					else w._setZIndex(zi, true);

					w.setDomVisible_(n, true, {visibility:1});
				}
			}

			zWatch.fireDown('onShow', {visible:true}, this);
		} else {
			zWatch.fireDown('onHide', {visible:true}, this);

			for (var fs = zk.Widget._floatings, j = fs.length,
			bindLevel = this.bindLevel; --j >= 0;) {
				var w = fs[j].widget;
				if (bindLevel >= w.bindLevel)
					break; //skip non-descendant (and this)
				if (this._floatVisibleDependent(w))
					w.setDomVisible_(fs[j].node, false, {visibility:1});
			}

			this.setDomVisible_(node, false);
		}
	},
	/** Returns if the specified widget's visibility depends this widget. */
	_floatVisibleDependent: function (wgt) {
		for (; wgt; wgt = wgt.parent)
			if (wgt == this) return true;
			else if (!wgt.isVisible()) break;
		return false;
	},
	setDomVisible_: function (n, visible, opts) {
		if (!opts || opts.display)
			n.style.display = visible ? '': 'none';
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible': 'hidden';
	},
	onChildAdded_: function (/*child*/) {
	},
	onChildRemoved_: function (/*child*/) {
	},
	onChildVisible_: function (/*child, visible*/) {
	},
	setTopmost: function () {
		if (!this.desktop) return -1;

		for (var wgt = this, Widget = zk.Widget; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				var zi = wgt._topZIndex();
				wgt._setZIndex(zi, true);

				for (var fs = Widget._floatings, j = 0, fl = fs.length;
				j < fl; ++j) { //parent first
					var w = fs[j].widget;
					if (wgt != w && zUtl.isAncestor(wgt, w) && w.isVisible()) {
						var n = fs[j].node
						if (n != w.getNode()) w.setFloatZIndex_(n, ++zi); //only a portion
						else w._setZIndex(++zi, true);
					}
				}
				return zi;
			}
		return -1;
	},
	/** Returns the topmost z-index for this widget.*/
	_topZIndex: function () {
		var zi = 1; //we have to start from 1 (since IE7 considers 0 as ignore)
		for (var fs = zk.Widget._floatings, j = fs.length; --j >= 0;) {
			var w = fs[j].widget;
			if (w._zIndex >= zi && !zUtl.isAncestor(this, w) && w.isVisible())
				zi = w._zIndex + 1;
		}
		return zi;
	},
	isFloating_: function () {
		return this._floating;
	},
	setFloating_: function (floating, opts) {
		if (this._floating != floating) {
			var fs = zk.Widget._floatings;
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node? opts.node: this.getNode()},
					bindLevel = this.bindLevel;
				for (var j = fs.length;;) {
					if (--j < 0) {
						fs.unshift(inf);
						break;
					}
					if (bindLevel >= fs[j].widget.bindLevel) { //parent first
						fs.$addAt(j + 1, inf);
						break;
					}
				}
				this._floating = true;
			} else {
				for (var j = fs.length; --j >= 0;)
					if (fs[j].widget == this)
						fs.$removeAt(j);
				this._floating = false;
			}
		}
	},

	getZIndex: _zkf = function () {
		return this._zIndex;
	},
	getZindex: _zkf,
	setZIndex: _zkf = function (zIndex) { //2nd arg is fromServer
		this._setZIndex(zIndex);
	},
	setZindex: _zkf,
	_setZIndex: function (zIndex, fire) {
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.getNode();
			if (n) {
				n.style.zIndex = zIndex = zIndex >= 0 ? zIndex: '';
				if (fire) this.fire('onZIndex', zIndex, {ignorable: true});
			}
		}
	},

	getScrollTop: function () {
		var n = this.getNode();
		return n ? n.scrollTop: 0;
	},
	getScrollLeft: function () {
		var n = this.getNode();
		return n ? n.scrollLeft: 0;
	},
	setScrollTop: function (val) {
		var n = this.getNode();
		if (n) n.scrollTop = val;
	},
	setScrollLeft: function (val) {
		var n = this.getNode();
		if (n) n.scrollLeft = val;
	},
	scrollIntoView: function () {
		var n = this.getNode();
		if (n) n.scrollIntoView();
	},

	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		for (var p = this, mold = this._mold; p; p = p.superclass) {
			var f = p.$class.molds[mold];
			if (f) return f.apply(this, arguments);
		}
		throw "mold "+mold+" not found in "+this.className;
	},
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this.getNode();
			if (n) n.className = this.domClass_();
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var s = zDom.parseStyle(this.domStyle_());
			zDom.setStyles(this.getNode(), s);

			var n = this.getTextNode_();
			if (n) zDom.setStyles(n, zDom.filterTextStyle(s));
		}
	},
	getTextNode_: function () {
	},

	domStyle_: function (no) {
		var style = '';
		if (!this.isVisible() && (!no || !no.visible))
			style = 'display:none;';
		if (!no || !no.style) {
			var s = this.getStyle(); 
			if (s) {
				style += s;
				if (s.charAt(s.length - 1) != ';') style += ';';
			}
		}
		if (!no || !no.width) {
			var s = this.getWidth();
			if (s) style += 'width:' + s + ';';
		}
		if (!no || !no.height) {
			var s = this.getHeight();
			if (s) style += 'height:' + s + ';';
		}
		if (!no || !no.left) {
			var s = this.getLeft();
			if (s) style += 'left:' + s + ';';
		}
		if (!no || !no.top) {
			var s = this.getTop();
			if (s) style += 'top:' + s + ';';
		}
		if (!no || !no.zIndex) {
			var s = this.getZIndex();
			if (s >= 0) style += 'z-index:' + s + ';';
		}
		return style;
	},
	domClass_: function (no) {
		var scls = '';
		if (!no || !no.sclass) {
			var s = this.getSclass();
			if (s) scls = s;
		}
		if (!no || !no.zclass) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s;
		}
		return scls;
	},
	domAttrs_: function (no) {
		var html = !no || !no.id ? ' id="' + this.uuid + '"': '';
		if (!no || !no.domStyle) {
			var s = this.domStyle_(no);
			if (s) html += ' style="' + s + '"';
		}
		if (!no || !no.domclass) {
			var s = this.domClass_();
			if (s) html += ' class="' + s + '"';
		}
		if (!no || !no.tooltiptext) {
			var s = this._tooltiptext;
			if (s) html += ' title="' + s + '"';
		}
		return html;
	},
	domTextStyleAttr_: function () {
		var s = this.getStyle();
		if (s) {
			s = zDom.filterTextStyle(s);
			if (s) s = ' style="' + s + '"';
		}
		return s;
	},

	replaceHTML: function (n, desktop, skipper) {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zkboot('z_auto');
		}

		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(this, cf)) {
			zk.currentFocus = null;
		} else
			cf = null;

		var p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper);
		else {
			var oldwgt = zk.Widget.$(n);
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			zDom.setOuterHTML(n, this._redrawHTML(skipper));
			this.bind(desktop, skipper);
		}

		if (cf && !zk.currentFocus) cf.focus();

		if (!skipper) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	insertHTML: function (n, where, desktop) {
		n.insertAdjacentHTML(where, this._redrawHTML());
		this.bind(desktop);
	},
	_redrawHTML: function (skipper) {
		var out = [];
		this.redraw(out, skipper);
		return out.join('');
	},
	rerender: function (skipper) {
		if (this.desktop) {
			var n = this.getNode();
			if (n) {
				if (skipper) {
					var skipInfo = skipper.skip(this);
					if (skipInfo) {
						this.replaceHTML(n, null, skipper);

						skipper.restore(this, skipInfo);

						zWatch.fireDown('beforeSize', null, this);
						zWatch.fireDown('onSize', null, this);
						return; //done
					}
				}
				this.replaceHTML(n);
			}
		}
	},

	replaceChildHTML_: function (child, n, desktop, skipper) {
		var oldwgt = zk.Widget.$(n);
		if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
		zDom.setOuterHTML(n, child._redrawHTML(skipper));
		child.bind(desktop, skipper);
	},
	insertChildHTML_: function (child, before, desktop) {
		var bfn, ben;
		if (before) {
			bfn = before._getBeforeNode();
			if (!bfn) before = null;
		}
		if (!before)
			for (var w = this;;) {
				ben = w.getCaveNode_();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2) {
					bfn = w2._getBeforeNode();
					if (bfn) break;
				}

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (bfn)
			zDom.insertHTMLBefore(bfn, child._redrawHTML());
		else
			zDom.insertHTMLBeforeEnd(ben, child._redrawHTML());
		child.bind(desktop);
	},
	getCaveNode_: function () {
		return this.getSubnode('cave') || this.getNode();
	},
	_getBeforeNode: function () {
		for (var w = this; w; w = w.nextSibling) {
			var n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	_getFirstNodeDown: function () {
		var n = this.getNode();
		if (n) return n;
		for (var w = this.firstChild; w; w = w.nextSibling) {
			n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	removeChildHTML_: function (child, prevsib) {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = null;

		var n = child.getNode();
		if (!n) child._prepareRemove(n = []);

		child.unbind();

		if (n.$array)
			for (var j = n.length; --j >= 0;)
				zDom.remove(n[j]);
		else
			zDom.remove(n);
	},
	_prepareRemove: function (ary) {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			var n = w.getNode();
			if (n) ary.push(n);
			else w._prepareRemove(ary);
		}
	},
	getSubnode: function (name) {
		if (!name)
			return this.getNode();
		var n = this._subnodes[name];
		if (!n && this.desktop)
			n = this._subnodes[name] = zDom.$(this.uuid, name);
		return n;
	},
	getNode: function () {
		var n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = zDom.$(this.uuid);
			this._nodeSolved = true;
		}
		return n;
	},
	getPage: function () {
		if (this.desktop && this.desktop.nChildren == 1)
			return this.desktop.firstChild;
			
		for (var page = this.parent; page; page = page.parent)
			if (page.$instanceof(zk.Page))
				return page;
				
		return null;
	},
	bind: function (desktop, skipper) {
		var after = [];
		this.bind_(desktop, skipper, after);
		for (var j = 0, len = after.length; j < len;)
			after[j++].call(this);
	},
	unbind: function (skipper) {
		var after = [];
		this.unbind_(skipper, after);
		for (var j = 0, len = after.length; j < len;)
			after[j++].call(this);
	},

	bind_: function (desktop, skipper, after) {
		zk.Widget._binds[this.uuid] = this;

		if (!desktop) desktop = zk.Desktop.$(this.uuid);
		this.desktop = desktop;

		var p = this.parent;
		this.bindLevel = p ? p.bindLevel + 1: 0;

		if (this._draggable) this.initDrag_();

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.bind_(desktop, null, after); //don't pass skipper
	},
	unbind_: function (skipper, after) {
		delete zk.Widget._binds[this.uuid];

		this._node = this.desktop = null;
		this._subnodes = {};
		this._nodeSolved = false;
		this.bindLevel = -1;

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.unbind_(null, after); //don't pass skipper

		if (this._draggable) this.cleanDrag_();
	},

	initDrag_: function () {
		var WDD = zk.WgtDD;
		this._drag = new zk.Draggable(this, this.getDragNode_(), {
			starteffect: zk.$void, //see bug #1886342
			endeffect: WDD.enddrag, change: WDD.dragging,
			ghosting: WDD.ghosting, endghosting: WDD.endghosting,
			constraint: WDD.constraint,
			ignoredrag: WDD.ignoredrag,
			zIndex: 88800
		});
	},
	cleanDrag_: function () {
		var drag = this._drag;
		if (drag) {
			this._drag = null;
			drag.destroy();
		}
	},
	getDragNode_: function () {
		return this.getNode();
	},
	ingoreDrag_: function (pt) {
		return false;
	},
	dropEffect_: function (over) {
		var n = this.getNode();
		if (n) zDom[over ? "addClass" : "rmClass"] (n, "z-drag-over");
	},
	getDragMessage_: function () {
		var n = this.getSubnode('cave') || this.getSubnode('real')
			|| this.getNode();
		return n ? n.textContent || n.innerText: '';
	},
	shallDragMessage_: function () {
		var tn = zDom.tag(this.getNode());
		return "TR" == tn || "TD" == tn || "TH" == tn;
	},
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244

		var WDD = zk.WgtDD, dgelm;
		if (this.shallDragMessage_()) {
			var msg = this.getDragMessage_();
			if (msg.length > 10) msg = msg.substring(0,10) + "...";
			dgelm = WDD.ghostByMessage(drag, ofs, msg);
		}else {
			dgelm = WDD.ghostByClone(drag, ofs);
		}

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = "pointer";
		zDom.addClass(this.getNode(), 'z-dragged'); //after clone
		return dgelm;
	},
	uncloneDrag_: function (drag) {
		document.body.style.cursor = drag._orgcursor || '';

		var n = this.getNode();
		if (n)
			zDom.rmClass(n, 'z-dragged');
	},

	focus: function (timeout) {
		var node;
		if (this.isVisible() && this.canActivate({checkOnly:true})
		&& (node = this.getNode())) {
			if (zDom.focus(node, timeout)) {
				zk.currentFocus = this;
				this.setTopmost();
				return true;
			}
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w.isVisible() && w.focus(timeout))
					return true;
		}
		return false;
	},
	canActivate: function (opts) {
		var modal = zk.currentModal;
		if (modal && !zUtl.isAncestor(modal, this)) {
			if (!opts || !opts.checkOnly) {
				var cf = zk.currentFocus;
				//Note: browser might change focus later, so delay a bit
				if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
				else modal.focus(0);
			}
			return false;
		}
		return true;
	},

	//server comm//
	smartUpdate: function (nm, val, timeout) {
		zAu.send(new zk.Event(this, 'setAttr', [nm, val]),
			timeout >= 0 ? timeout: -1);
	},

	//widget event//
	fireX: function (evt, timeout) {
		evt.currentTarget = this;
		var evtnm = evt.name,
			lsns = this._lsns[evtnm],
			len = lsns ? lsns.length: 0;
		if (len) {
			for (var j = 0; j < len;) {
				var inf = lsns[j++], o = inf[1],
					oldevt = o.$event;
				o.$event = evt;
				try {
					(inf[2] || o[evtnm]).call(o, evt);
				} finally {
					o.$event = oldevt;
				}
				if (evt.stopped) return evt; //no more processing
			}
		}

		if (!evt.auStopped) {
			var toServer = evt.opts && evt.opts.toServer;
			if (toServer || (this.inServer && this.desktop)){
				var asap = toServer || this._asaps[evtnm];
				if (asap == null) {
					var ime = this.$class._importantEvts;
					if (ime) {
						var ime = ime[evtnm];
						if (ime != null)
							asap = ime;
					}
				}
				if (asap != null) //true or false
					zAu.send(evt, asap ? timeout >= 0 ? timeout: 38: -1);
			}
		}
		return evt;
	},
	fire: function (evtnm, data, opts, timeout) {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	},
	listen: function (evtnm, listener, fn, priority) {
		if (!priority) priority = 0;
		var inf = [priority, listener, fn],
			lsns = this._lsns[evtnm];
		if (!lsns) lsns = this._lsns[evtnm] = [inf];
		else
			for (var j = lsns.length; --j >= 0;)
				if (lsns[j][0] >= priority) {
					lsns.$addAt(j + 1, inf);
					break;
				}
	},
	unlisten: function (evtnm, listener, fn) {
		var lsns = this._lsns[evtnm];
		for (var j = lsns ? lsns.length: 0; --j >= 0;)
			if (lsns[j][1] == listener && lsns[j][2] == fn) {
				lsns.$removeAt(j);
				return true;
			}
		return false;
	},
	isListen: function (evtnm, opts) {
		var v = this._asaps[evtnm];
		if (v) return true;
		if (opts && opts.asapOnly) {
			v = this.$class._importantEvts;
			return v && v[evtnm];
		}
		if (opts && opts.any) {
			if (v != null) return true;
			v = this.$class._importantEvts;
			if (v && v[evtnm] != null) return true;
		}

		var lsns = this._lsns[evtnm];
		return lsns && lsns.length;
	},
	setListeners: function (infs) {
		for (var evtnm in infs)
			this._setListener(evtnm, infs[evtnm]);
	},
	setListener: function (inf) {
		this._setListener(inf[0], inf[1]);
	},
	_setListener: function (evtnm, fn) {
		var lsns = this._$lsns,
			oldfn = lsns[evtnm];
		if (oldfn) { //unlisten first
			delete lsns[evtnm];
			this.unlisten(evtnm, this, oldfn);
		}
		if (fn) {
			if (typeof fn != 'function') fn = new Function(fn);
			this.listen(evtnm, this, lsns[evtnm] = fn);
		}
	},
	setMethods: function (infs) {
		for (var mtdnm in infs)
			this._setMethod(mtdnm, infs[mtdnm]);
	},
	setMethod: function (inf) {
		this._setMethod(inf[0], inf[1]);
	},
	_setMethod: function (mtdnm, fn) {
		if (fn) {
			if (typeof fn != 'function') fn = eval(fn);
			var oldnm = '$' + mtdnm;
			if (!this[oldnm]) this[oldnm] = this[mtdnm]; //only once
			this[mtdnm] = fn;
				//use eval, since complete func decl
		} else {
			var oldnm = '$' + mtdnm;
			this[mtdnm] = this[oldnm]; //restore
			delete this[oldnm];
		}
	},

	//ZK event handling//
	doClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}	
	},
	doDoubleClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}	
	},
	doRightClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	},
	doMouseOver_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	},
	doMouseOut_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	},
	doMouseDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	},
	doMouseUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	},
	doMouseMove_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	},
	doKeyDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	},
	doKeyUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	},
	doKeyPress_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	},

	doFocus_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	},
	doBlur_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	},

	//DOM event handling//
	domListen_: function (n, evtnm, fn) {
		var inf;
		if (inf = zk.Widget._domevti(this, evtnm, fn))
			zEvt.listen(n, inf[0], inf[1]);
	},
	domUnlisten_: function (n, evtnm, fn) {
		var inf;
		if (inf = zk.Widget._domevti(this, evtnm, fn))
			zEvt.unlisten(n, inf[0], inf[1]);
	},
	toJSON: function () {
		return this.uuid;
	}

}, {
	_floatings: [], //[{widget,node}]
	$: function (n, strict) {
		var binds = zk.Widget._binds;
		if (typeof n == 'string') {
			var j = n.indexOf('$');
			return binds[j >= 0 ? n.substring(0, j): n];
		}

		if (!n || zk.Widget.isInstance(n)) return n;
		else if (!n.nodeType) //skip Element
			n = n.z_target || n.target || n.srcElement || n; //check DOM event first
				//z_target: used if we have to override the default

		for (; n; n = zDom.parentNode(n)) {
			var id = n.id;
			if (id) {
				var j = id.indexOf('$');
				if (j >= 0) {
					id = id.substring(0, j);
					if (strict) {
						var wgt = binds[id];
						if (wgt) {
							var n2 = wgt.getNode();
							if (n2 && zDom.isAncestor(n2, n)) return wgt;
							continue;
						}
					}
				}
				wgt = binds[id];
				if (wgt) return wgt;
			}
		}
		return null;
	},
	_binds: {}, //Map(uuid, wgt): bind but no node

	//Event Handling//
	_domevti: function (wgt, evtnm, fn) { //proxy event listener
		evtnm = evtnm.substring(2);
		if (!fn) {
			fn = '_do';
			if (wgt.inDesign) fn += 'Design';
			fn += evtnm;
		} else if (wgt.inDesign)
			fn = fn.startsWith('_do') ? '_doDesign' + fn.substring(3):
				'doDesign' + (fn.startsWith('do') ? fn.substring(2): fn);

		var f = wgt[fn];
		if (!f) {
			if (!wgt.inDesign)
				throw 'Listener ' + fn + ' not found in ' + wgt.className;
			return null;
		}
		return [evtnm == 'DoubleClick' ? 'dblclick': evtnm.toLowerCase(),
			zk.Widget._domevtproxy(wgt, f)];
	},
	_domevtproxy: function (wgt, f) {
		var fps = wgt._$evproxs, fp;
		if (!fps) wgt._$evproxs = fps = {};
		else if (fp = fps[f]) return fp;
		return fps[f] = zk.Widget._domevtproxy0(wgt, f);
	},
	_domevtproxy0: function (wgt, f) {
		return function (devt) {
			if (!devt) devt = window.event;
			var args = [], evt;
			for (var j = arguments.length; --j > 0;)
				args.unshift(arguments[j]);
			args.unshift(evt = zEvt.toEvent(devt, wgt));

			switch (devt.type){
			case 'focus':
				if (wgt.canActivate()) {
					zk.currentFocus = wgt;
					zWatch.fire('onFloatUp', null, wgt); //notify all
					break;
				}
				return; //ignore it
			case 'blur':
				//due to _domMouseDown called, zk.currentFocus already corrected,
				//so we clear it only if caused by other case
				if (!zk._cfByMD) zk.currentFocus = null;
				break;
			case 'click':
			case 'dblclick':
			case 'mouseup': //we cannot simulate mousedown:(
				if (zk.Draggable.ignoreClick())
					return;
			}

			var ret = f.apply(wgt, args);
			if (typeof ret == 'undefined') ret = evt.returnValue;
			if (evt.domStopped) zEvt.stop(devt);
			return devt.type == 'dblclick' && typeof ret == 'undefined' ? false: ret;
		};
	},

	_domMouseDown: function (wgt, fake) { //called by mount
		var modal = zk.currentModal;
		if (modal && !wgt) {
			var cf = zk.currentFocus;
			//Note: browser might change focus later, so delay a bit
			//(it doesn't work if we stop event instead of delay - IE)
			if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
			else modal.focus(0);
		} else if (!wgt || wgt.canActivate()) {
			if (!fake) {
				zk.currentFocus = wgt;
				zk._cfByMD = true;
				setTimeout(zk.Widget._clearCFByMD, 0);
					//turn it off later since onBlur_ needs it
			}
			if (wgt) zWatch.fire('onFloatUp', null, wgt); //notify all
		}
	},
	_clearCFByMD: function () {
		zk._cfByMD = false;
	},

	//uuid//
	uuid: function (id) {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('$');
		return j >= 0 ? uuid.substring(0, j): id;
	},
	nextUuid: function () {
		return '_z_' + zk.Widget._nextUuid++;
	},
	_nextUuid: 0,

	isAutoId: function (id) {
		return !id || id.startsWith('_z_') || id.startsWith('z_');
	},

	_fixBindLevel: function (wgt, v) {
		var $Widget = zk.Widget;
		wgt.bindLevel = v++;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			$Widget._fixBindLevel(wgt, v);
	},

	_addIdSpace: function (wgt) {
		if (wgt._fellows) wgt._fellows[wgt.id] = wgt;
		var p = wgt.parent;
		if (p) {
			p = p.getSpaceOwner();
			if (p) p._fellows[wgt.id] = wgt;
		}
	},
	_rmIdSpace: function (wgt) {
		if (wgt._fellows) delete wgt._fellows[wgt.id];
		var p = wgt.parent;
		if (p) {
			p = p.getSpaceOwner();
			if (p) delete p._fellows[wgt.id];
		}
	},
	_addIdSpaceDown: function (wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.getSpaceOwner(): null;
		if (ow) {
			var fn = zk.Widget._addIdSpaceDown0;
			fn(wgt, ow, fn);
		}
	},
	_addIdSpaceDown0: function (wgt, owner, fn) {
		if (wgt.id) owner._fellows[wgt.id] = wgt;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			fn(wgt, owner, fn);
	},
	_rmIdSpaceDown: function (wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.getSpaceOwner(): null;
		if (ow) {
			var fn = zk.Widget._rmIdSpaceDown0;
			fn(wgt, ow, fn);
		}
	},
	_rmIdSpaceDown0: function (wgt, owner, fn) {
		if (wgt.id) delete owner._fellows[wgt.id];
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			fn(wgt, owner, fn);
	},

	_global: {}, //a global ID space

	_wgtcs: {},
	register: function (cls, clsnm, blankprev) {
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		zk.Widget._wgtcs[clsnm.substring(0,1).toLowerCase()+clsnm.substring(1)] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	},
	newInstance: function (wgtnm) {
		var cls = zk.Widget._wgtcs[wgtnm.toLowerCase()];
		if (!cls)
			throw 'widget not found: '+wgtnm;
		return new cls();
	}
});

zk.Page = zk.$extends(zk.Widget, {//unlik server, we derive from Widget!
	_style: "width:100%;height:100%",
	className: 'zk.Page',

	$init: function (props, contained) {
		this._fellows = {};

		this.$super('$init', props);

		if (contained) zk.Page.contained.push(this);
	},
	redraw: function (out, skipper) {
		out.push('<div id="', this.uuid, '" style="', this.getStyle(), '">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out, skipper);
		out.push('</div>');
	}

},{
	contained: []
});

zk.Desktop = zk.$extends(zk.Widget, {
	bindLevel: 0,
	className: 'zk.Desktop',

	$init: function (dtid, updateURI) {
		this.$super('$init', {uuid: dtid}); //id also uuid

		this._aureqs = [];

		var zkdt = zk.Desktop, dts = zkdt.all, dt = dts[dtid];
		if (!dt) {
			this.uuid = this.id = dtid;
			this.updateURI = updateURI;
			dts[dtid] = this;
			++zkdt._ndt;
			if (!zkdt._dt) zkdt._dt = this; //default desktop
		} else if (updateURI)
			dt.updateURI = updateURI;

		zkdt.sync();
	},
	_exists: function () {
		var id = this._pguid; //_pguid not assigned at beginning
		return !id || zDom.$(id);
	},
	bind_: zk.$void,
	unbind_: zk.$void,
	setId: zk.$void
},{
	$: function (dtid) {
		var zkdt = zk.Desktop, dts = zkdt.all, w;
		if (zkdt._ndt > 1) {
			if (typeof dtid == 'string') {
				w = dts[dtid];
				if (w) return w;
			}
			w = zk.Widget.$(dtid);
			if (w)
				for (; w; w = w.parent) {
					if (w.desktop)
						return w.desktop;
					if (w.$instanceof(zkdt))
						return w;
				}
		}
		if (w = zkdt._dt) return w;
		for (dtid in dts)
			return dts[dtid];
	},
	all: {},
	_ndt: 0,
	sync: function () {
		var zkdt = zk.Desktop, dts = zkdt.all;
		if (zkdt._dt && !zkdt._dt._exists()) //removed
			zkdt._dt = null;
		for (var dtid in dts) {
			var dt = dts[dtid];
			if (!dt._exists()) { //removed
				delete dts[dtid];
				--zkdt._ndt;
			} else if (!zkdt._dt)
				zkdt._dt = dt;
		}
	}
});

zk.Skipper = zk.$extends(zk.Object, {
	skipped: function (wgt, child) {
		return wgt.caption != child;
	},
	skip: function (wgt, skipId) {
		var skip = zDom.$(skipId || (wgt.uuid + '$cave'));
		if (skip && skip.firstChild) {
			zDom.remove(skip);
			return skip;
		}
		return null;
	},
	restore: function (wgt, skip) {
		if (skip) {
			var loc = zDom.$(skip.id);
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);
			}
		}
	}
});
zk.Skipper.nonCaptionSkipper = new zk.Skipper();

zk.Native = zk.$extends(zk.Widget, {
	className: 'zk.Native',

	redraw: function (out) {
		var s = this.prolog;
		if (s) {
			if (zk.ie) this._patchScript(out, s);
			out.push(s);
		}

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}
});

//pacth IE7 bug: script ignored if it is the first child (script2.zul)
if (zk.ie)
	zk.Native.prototype._patchScript = function (out, s) {
		var j;
		if (this.previousSibling || s.indexOf('<script') < 0
		|| (j = out.length) > 20)
			return;
		for (var cnt = 0; --j >= 0;)
			if (out[j].indexOf('<') >= 0 && ++cnt > 1)
				return; //more than one
	 	out.push('<span style="display:none;font-size:0">&#160;</span>');
	};

zk.Macro = zk.$extends(zk.Widget, {
	className: 'zk.Macro',

	redraw: function (out) {
		out.push('<span', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</span>');
	}
});

zk.RefWidget = zk.$extends(zk.Widget, {
	bind_: function () {
		var w = zk.Widget.$(this.uuid);
		if (!w || !w.desktop) throw 'illegal: '+w;

		var p = w.parent, q;
		if (p) { //shall be a desktop
			var dt = w.desktop, n = w._node;
			w.desktop = w._node = null; //avoid unbind/bind
			p.removeChild(w);
			w.desktop = dt; w._node = n;
		}

		p = w.parent = this.parent,
		q = w.previousSibling = this.previousSibling;
		if (q) q.nextSibling = w;
		else if (p) p.firstChild = w;

		q = w.nextSibling = this.nextSibling;
		if (q) q.previousSibling = w;
		else if (p) p.lastChild = w;

		//no need to call super since it is bound
	}
});

zk.WgtDD = {
	_getDrop: function (drag, pt, evt) {
		var dragged = drag.control,
			dragType = dragged._draggable;
			//drag to itself not allowed
		for (var wgt = evt.target; wgt && wgt != dragged; wgt = wgt.parent) {
			var dropType = wgt._droppable;
			if (dropType == 'true') return wgt;
			if (dropType && dragType != "true")
				for (var dropType = wgt._dropTypes, j = dropType.length; --j >= 0;)
					if (dragType == dropType[j])
						return wgt;
		}
	},
	_cleanLastDrop: function (drag) {
		if (drag) {
			var drop;
			if (drop = drag._lastDrop) {
				drag._lastDrop = null;
				drop.dropEffect_();
			}
			drag._lastDropTo = null;
		}
	},
	_pointer: function (evt) {
		return [evt.pageX + 10, evt.pageY + 5];
	},

	enddrag: function (drag, evt) {
		zk.WgtDD._cleanLastDrop(drag);
		var pt = [evt.pageX, evt.pageY],
			wgt = zk.WgtDD._getDrop(drag, pt, evt);
		if (wgt) {
			var data = zk.copy({dragged: drag.control}, evt.data);
			wgt.fire('onDrop', data, null, 38);
		}
	},
	dragging: function (drag, pt, evt) {
		var dropTo;
		if (!evt || (dropTo = evt.domTarget) == drag._lastDropTo)
			return;

		var dropw = zk.WgtDD._getDrop(drag, pt, evt),
			found = dropw && dropw == drag._lastDrop;
		if (!found) {
			zk.WgtDD._cleanLastDrop(drag); //clean _lastDrop
			if (dropw) {
				drag._lastDrop = dropw;
				dropw.dropEffect_(true);
				found = true;
			}
		}

		var dragImg = drag._dragImg;
		if (dragImg)
			dragImg.className = found ? 'z-drop-allow': 'z-drop-disallow';

		drag._lastDropTo = dropTo; //do it after _cleanLastDrop
	},
	ghosting: function (drag, ofs, evt) {
		return drag.control.cloneDrag_(drag, zk.WgtDD._pointer(evt));
	},
	endghosting: function (drag, origin) {
		drag.control.uncloneDrag_(drag);
		drag._dragImg = null;
	},
	ghostByMessage: function (drag, ofs, msg) {
		document.body.insertAdjacentHTML("beforeEnd",
			'<div id="zk_ddghost" class="z-drop-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;"><div class="z-drop-cnt"><span id="zk_ddghost$img" class="z-drop-disallow"></span>&nbsp;'+msg+'</div></div>');
		drag._dragImg = zDom.$("zk_ddghost$img");
		return zDom.$("zk_ddghost");
	},
	ghostByClone: function (drag, ofs) {
		var dgelm = drag.node.cloneNode(true);
		dgelm.id = "zk_ddghost";
		zk.copy(dgelm.style, {
			position: "absolute", left: ofs[0] + "px", top: ofs[1] + "px"
		});
		document.body.appendChild(dgelm);
		return dgelm;
	},
	constraint: function (drag, pt, evt) {
		return zk.WgtDD._pointer(evt);
	},
	ignoredrag: function (drag, pt, evt) {
		return drag.control.ingoreDrag_(pt);
	}
};
