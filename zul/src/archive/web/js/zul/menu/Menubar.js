/* Menubar.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:32     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The menu related widgets, such as menubar and menuitem.
 */
//zk.$package('zul.menu');

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getZclass}: z-menubar-hor, if {@link #getOrient()} == vertical,
 *  z-menubar-ver will be added.
 */
zul.menu.Menubar = zk.$extends(zul.Widget, {
	_orient: "horizontal",

	$define: {
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either horizontal or vertical
		 */
		orient: function () {
			this.rerender();
		},
		/** Returns whether the menubar scrolling is enabled. 
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether to enable the menubar scrolling
		 * @param boolean scrollable
		 */
		scrollable: function (scrollable) {
			if (this.checkScrollable())
				this.rerender();	
		},
		/** Returns whether to automatically drop down menus if user moves mouse
		 * over it.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether to automatically drop down menus if user moves mouse
		 * over it.
		 * @param boolean autodrop
		 */
		autodrop: null
	},
	getZclass: function () {
		return this._zclass == null ? "z-menubar" +
				("vertical" == this.getOrient() ? "-ver" : "-hor") : this._zclass;
	},
	unbind_: function () {
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domUnlisten_(left, 'onClick', '_doScroll')
					.domUnlisten_(left, 'onMouseover', '_onOver')
					.domUnlisten_(left, 'onMouseout', '_onOut')
					.domUnlisten_(right, 'onClick', '_doScroll')
					.domUnlisten_(right, 'onMouseover', '_onOver')
					.domUnlisten_(right, 'onMouseout', '_onOut');
			}
			zWatch.unlisten({onSize: this, onShow: this});
		}
		var node = this.$n();
		this.domUnlisten_(node, 'onMouseover', '_onMenuOver')
			.domUnlisten_(node, 'onMouseout', '_onMenuOut');
		
		this._lastTarget = null;
		this.$supers(zul.menu.Menubar, 'unbind_', arguments);
	},
	bind_: function () {
		this.$supers(zul.menu.Menubar, 'bind_', arguments);
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domListen_(left, 'onClick', '_doScroll')
					.domListen_(left, 'onMouseover', '_onOver')
					.domListen_(left, 'onMouseout', '_onOut')
					.domListen_(right, 'onClick', '_doScroll')
					.domListen_(right, 'onMouseover', '_onOver')
					.domListen_(right, 'onMouseout', '_onOut');
			}
			zWatch.listen({onSize: this, onShow: this});
		}
		var node = this.$n();
		this.domListen_(node, 'onMouseover', '_onMenuOver')
			.domListen_(node, 'onMouseout', '_onMenuOut');
	},
	/** Returns whether the menubar scrolling is enabled in horizontal orient.
	 * @return boolean
	 */
	checkScrollable: function () {
		return this._scrollable && ("horizontal" == this.getOrient());
	},
	onSize: _zkf = function () {
		this._checkScrolling();
	},
	onShow: _zkf,
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._checkScrolling();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (!this.childReplacing_)
			this._checkScrolling();
	},
	_checkScrolling: function () {	
		if (!this.checkScrollable()) return;
		
		var node = this.$n();
		if (!node) return;
		jq(node).addClass(this.getZclass() + "-scroll");
		if (zk.ie6_) this._doFixWidth(node);
		
		var nodeWidth = zk(node).offsetWidth(),
			body = this.$n('body'),
			childs = jq(this.$n('cave')).children();
			totalWidth = 0;
		
		for (var i = childs.length; i-- ;)
			totalWidth += childs[i].offsetWidth;

		var fixedSize = nodeWidth -
						zk(this.$n('left')).offsetWidth() -
						zk(this.$n('right')).offsetWidth();
		if (this._scrolling) {
			if (totalWidth <= nodeWidth) {
				this._scrolling = false;
				body.scrollLeft = 0;
				if (zk.ie7_)
					zk(body).redoCSS();
			} else {
				body.style.width = jq.px0(fixedSize);
				this._fixScrollPos(node);
			}
			this._fixButtonPos(node);
		} else {
			if (totalWidth > nodeWidth) {
				this._scrolling = true;
				this._fixButtonPos(node);
				body.style.width = jq.px0(fixedSize);
			}
		}
	},
	_fixScrollPos: function () {
		var body = this.$n('body'),
			childs = jq(this.$n('cave')).children();
		if (childs[childs.length - 1].offsetLeft < body.scrollLeft) {
			var movePos = childs[childs.length - 1].offsetLeft;
			body.scrollLeft = movePos;
		}
	},
	_fixButtonPos: function (node) {
		var zcls = this.getZclass(),
			body = this.$n('body'),
			left = this.$n('left'),
			right = this.$n('right'),
			css = this._scrolling ? "addClass" : "removeClass";

		jq(node)[css](zcls + "-scroll");
		jq(body)[css](zcls + "-body-scroll");
		jq(left)[css](zcls + "-left-scroll");
		jq(right)[css](zcls + "-right-scroll");
	},
	_doFixWidth: function () {
		var node = this.$n(),
			width = node.style.width;
		if (zk.ie6_ && (!width || "auto" == width))
			this._forceStyle(node, "100%");
	},
	_forceStyle: function (node, value) {
		if (zk.parseInt(value) < 0)
			return;
		node.style.width = zk.ie6_ ? "0px" : "";
		node.style.width = value;
	},
	_onOver: function (evt) {
		if (!this.checkScrollable()) return;
		var evtNode = evt.domTarget,
			node = this.$n(),
			left = this.$n('left'),
			right = this.$n('right'),
			zcls = this.getZclass();

		if (left == evtNode) {
			jq(left).addClass(zcls + "-left-scroll-over");
		} else if (right == evtNode) {
			jq(right).addClass(zcls + "-right-scroll-over");
		}
	},
	_onOut: function (evt) {
		if (!this.checkScrollable()) return;
		var evtNode = evt.domTarget,
			node = this.$n(),
			left = this.$n('left'),
			right = this.$n('right'),
			zcls = this.getZclass();

	    if (left == evtNode) {
	    	jq(left).removeClass(zcls + "-left-scroll-over");
		} else if (right == evtNode) {
			jq(right).removeClass(zcls + "-right-scroll-over");
		}
	},
	_onMenuOver: function (evt) {
		this._noFloatUp = true;
	},
	_onMenuOut: function (evt) {
//		if (zk.Widget.$(evt.domTarget).$instanceof(zul.menu.Menuitem))
//			return;
		this._noFloatUp = false;
		var menu;
		if (menu = this._lastTarget)
		menu._doPopupClose();
	},
	_doScroll: function (evt) {
		var target = evt.domTarget;
		this._scroll(target.id.endsWith("left") ? "left" : "right");
	},
	_scroll: function (direction) {
		if (!this.checkScrollable() || this._runId) return;
		var self = this;
			body = this.$n('body'),
			currScrollLeft = body.scrollLeft,
			childs = jq(this.$n('cave')).children(),
			childLen = childs.length,
			movePos = 0;

		if (!childLen) return;
		switch (direction) {
		case "left":
			for (var i = 0; i < childLen; i++) {
				if (childs[i].offsetLeft >= currScrollLeft) {
					var preChild = childs[i].previousSibling;
					if (!preChild)	return;
					movePos = currScrollLeft - (currScrollLeft - preChild.offsetLeft);
					if (isNaN(movePos)) return;
					self._runId = setInterval(function () {
						if(!self._moveTo(body, movePos)){
							clearInterval(self._runId);
							self._runId = null;
						}
					}, 10);
					return;
				}
			}
			break;
		case "right":
			var currRight = currScrollLeft + body.offsetWidth;
			for (var i = 0; i < childLen; i++) {
				var currChildRight =  childs[i].offsetLeft + childs[i].offsetWidth;
				if (currChildRight > currRight) {
					movePos = currScrollLeft + (currChildRight - currRight);
					if (isNaN(movePos)) return;
					self._runId = setInterval(function () {
						if (!self._moveTo(body, movePos)) {
							clearInterval(self._runId);
							self._runId = null;
						}
					}, 10);
					return;
				}
			}
			break;
		}
	},
	_moveTo: function (body, moveDest) {
		var currPos = body.scrollLeft,
			step = 5;
		if (currPos == moveDest) return false;

		if (currPos > moveDest) {
			var setTo = currPos - step;
			body.scrollLeft = setTo < moveDest ?  moveDest : setTo;
			return true;
		} else {
			var setTo = currPos + step;
			body.scrollLeft = setTo > moveDest ? moveDest : setTo;
			return true;
		}
		return false;
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before)
			jq(before.$n('chdextr') || before.$n()).before(
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));
		else
			jq(this.$n('cave')).append(
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));

		child.bind(desktop);
	},
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdextr', zk).remove();
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child,
			isVert = opts.vertical;
		if (isVert) {
			out.push('<tr id="', child.uuid, '-chdextr"');
			if (child.getHeight())
				out.push(' height="', child.getHeight(), '"');
			out.push('>');
		}
		child.redraw(out);
		if (isVert)
			out.push('</tr>');
		if (!opts.out) return out.join('');
	}
});