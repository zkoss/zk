/* Menubar.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:32     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The menu related widgets, such as menubar and menuitem.
 */
//zk.$package('zul.menu');

(function () {
	function _closeOnOut(menubar) {
		//1) _noFloatUp: Bug 1852304: Safari/Chrome unable to popup with menuitem
		//   because popup causes mouseout, and mouseout casues onfloatup
		//2) _bOver: indicates whether it is over some part of menubar
		//3) Test also Bug 3052208
		if (!menubar._noFloatUp && !menubar._bOver && zul.menu._nOpen)
			zWatch.fire('onFloatUp', menubar); //notify all
	}
	
/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getZclass}: z-menubar
 */
zul.menu.Menubar = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

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
	
	setWidth: function () {
		this.$supers('setWidth', arguments);
		this._checkScrolling();
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s("vertical" == this.getOrient() ? 'vertical' : 'horizontal');
		}
		return sc;
	},
	unbind_: function () {
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domUnlisten_(left, 'onClick', '_doScroll')
					.domUnlisten_(right, 'onClick', '_doScroll');
			}
			zWatch.unlisten({onSize: this});
		}
		var n = this.$n();
		this.domUnlisten_(n, 'onMouseEnter').domUnlisten_(n, 'onMouseLeave');

		this._lastTarget = null;
		this.$supers(zul.menu.Menubar, 'unbind_', arguments);
	},
	bind_: function () {
		this.$supers(zul.menu.Menubar, 'bind_', arguments);
		var n = this.$n();
		this.domListen_(n, 'onMouseEnter').domListen_(n, 'onMouseLeave');
		if (this.checkScrollable()) {
			var left = this.$n('left'),
				right = this.$n('right');
			if (left && right) {
				this.domListen_(left, 'onClick', '_doScroll')
					.domListen_(right, 'onClick', '_doScroll');
			}
			zWatch.listen({onSize: this});
		}
	},
	/** Returns whether the menubar scrolling is enabled in horizontal orient.
	 * @return boolean
	 */
	checkScrollable: function () {
		return this._scrollable && ('horizontal' == this.getOrient());
	},
	onSize: function () {
		this._checkScrolling();
	},

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
		jq(node).addClass(this.$s('scroll'));
		
		var nodeWidth = zk(node).offsetWidth(),
			body = this.$n('body'),
			childs = jq(this.$n('cave')).children(),
			totalWidth = 0;
		
		for (var i = childs.length; i-- ;)
			totalWidth += jq(childs[i]).outerWidth(true); //ZK-3095

		if (zk.ie) // child width (text node) is not integer in IE 
			totalWidth += childs.length;

		var fixedSize = nodeWidth -
						zk(this.$n('left')).offsetWidth() -
						zk(this.$n('right')).offsetWidth();
		if (this._scrolling) {
			if (totalWidth < nodeWidth) {
				this._scrolling = false;
				body.scrollLeft = 0;
				if (body.offsetWidth <= totalWidth)
					body.style.width = jq.px0(fixedSize);
			} else {
				body.style.width = jq.px0(fixedSize);
				this._fixScrollPos(node);
			}
		} else {
			if (totalWidth >= nodeWidth) {
				this._scrolling = true;
				body.style.width = jq.px0(fixedSize);
			} else { //ZK-3094: Scrollable menubar body is not properly resized after container resizing.
				body.style.width = '';
			}
		}
		this._fixButtonPos(node);
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
			css = this._scrolling ? 'addClass' : 'removeClass';
		
		body.style.marginLeft = this._scrolling ? jq.px(left.offsetWidth) : '0';
		body.style.marginRight = this._scrolling ? jq.px(right.offsetWidth) : '0';
		left.style.display = right.style.display = this._scrolling ? 'block' : 'none';
		jq(node)[css](this.$s('scroll'));
	},
	_forceStyle: function (node, value) {
		if (zk.parseInt(value) < 0)
			return;
		node.style.width = value;
	},
	_doMouseEnter: function (evt) {
		this._bOver = true;
		this._noFloatUp = false;
	},
	_doMouseLeave: function (evt) {
		this._bOver = false;
		this._closeOnOut();
	},
	_doScroll: function (evt) {
		this._scroll(evt.domTarget == this.$n('left') || evt.domTarget.parentNode == this.$n('left') ? 'left' : 'right');
	},
	_scroll: function (direction) {
		if (!this.checkScrollable() || this._runId) return;
		var self = this,
			body = this.$n('body'),
			currScrollLeft = body.scrollLeft,
			childs = jq(this.$n('cave')).children(),
			childLen = childs.length,
			movePos = 0;

		if (!childLen) return;
		switch (direction) {
		case 'left':
			for (var i = 0; i < childLen; i++) {
				// B50-ZK-381: Menu scrolling bug
				// child width may be larger than body.offsetWidth 
				if (childs[i].offsetLeft >= currScrollLeft ||
						childs[i].offsetLeft+(childs[i].offsetWidth - body.offsetWidth) >= currScrollLeft) {
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
		case 'right':
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
		var currPos = body.scrollLeft;
		if (currPos == moveDest)
			return false;
		
		var step = 5,
			delta = currPos > moveDest ? -step : step,
			setTo = currPos + delta;
		if ((setTo < moveDest && delta < 0) || (setTo > moveDest && delta > 0))
			setTo = moveDest;
		
		body.scrollLeft = setTo;
		return true;
	},
	insertChildHTML_: function (child, before, desktop) {
		var vert = 'vertical' == this.getOrient();
		if (before)
			jq(before.$n('chdextr') || before.$n()).before(
				this.encloseChildHTML_({child: child, vertical: vert}));
		else
			jq(this.$n('cave')).append(
				this.encloseChildHTML_({child: child, vertical: vert}));

		child.bind(desktop);
	},
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.$n('chdextr')).remove();
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || new zk.Buffer(),
			child = opts.child;
		child.redraw(out);
		if (!opts.out) return out.join('');
	},

	//Closes all menupopup when mouse is moved out
	_closeOnOut: function () {
		var self = this;
		if (self._autodrop && !zul.Widget.getOpenTooltip()) //dirty fix: don't auto close if tooltip shown
			setTimeout(function () {_closeOnOut(self);}, 200);
	}
});

})();