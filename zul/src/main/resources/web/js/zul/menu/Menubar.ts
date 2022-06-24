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
	_bodyScrollLeft: 0,
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
			sc += ' ' + this.$s(this.isVertical() ? 'vertical' : 'horizontal');
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
		n.removeEventListener('mouseleave', this.proxy(this._doMouseLeave));
		n.removeEventListener('mouseenter', this.proxy(this._doMouseEnter));

		this._lastTarget = null;
		this.$supers(zul.menu.Menubar, 'unbind_', arguments);
	},
	bind_: function () {
		this.$supers(zul.menu.Menubar, 'bind_', arguments);
		var n = this.$n();
		n.addEventListener('mouseenter', this.proxy(this._doMouseEnter));
		n.addEventListener('mouseleave', this.proxy(this._doMouseLeave));
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
		return this._scrollable && !this.isVertical();
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

		var nodeWidth = zk(node).contentWidth(),
			body = this.$n('body'),
			children = jq(this.$n('cave')).children().filter(':visible'),
			childrenLen = children.length,
			totalWidth = 0;

		for (var i = childrenLen; i--;)
			totalWidth += jq(children[i]).outerWidth(true); //ZK-3095

		if (zk.ie) // child width (text node) is not integer in IE
			totalWidth += childrenLen;

		if (totalWidth >= nodeWidth)
			this._scrolling = true;
		else {
			this._scrolling = false;
			this._fixBodyScrollLeft(0);
			 //ZK-3094: Scrollable menubar body is not properly resized after container resizing.
			body.style.width = '';
		}
		this._fixButtonPos(node);

		var fixedSize = nodeWidth - zk(this.$n('left')).offsetWidth() - zk(this.$n('right')).offsetWidth();
		if (this._scrolling) {
			body.style.width = jq.px0(fixedSize);
			this._fixScrollPos(children.last()[0]);
		}
	},
	_fixScrollPos: function (lastChild) {
		if (lastChild) {
			var offsetLeft = lastChild.offsetLeft;
			if (offsetLeft < this._bodyScrollLeft) {
				this._fixBodyScrollLeft(offsetLeft);
			}
		}
	},
	_fixButtonPos: function (node) {
		var body = this.$n('body'),
			left = this.$n('left'),
			right = this.$n('right'),
			css = this._scrolling ? 'addClass' : 'removeClass';

		left.style.display = right.style.display = this._scrolling ? 'block' : 'none';
		jq(node)[css](this.$s('scroll'));
		body.style.marginLeft = this._scrolling ? jq.px(left.offsetWidth) : '0';
		body.style.marginRight = this._scrolling ? jq.px(right.offsetWidth) : '0';
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
	_fixBodyScrollLeft: function (scrollLeft) {
		this.$n('body').scrollLeft = this._bodyScrollLeft = scrollLeft;
	},
	_scroll: function (direction) {
		if (!this.checkScrollable() || this._runId) return;
		var self = this,
			body = this.$n('body'),
			currScrollLeft = this._bodyScrollLeft,
			children = jq(this.$n('cave')).children().filter(':visible'),
			childrenLen = children.length,
			movePos = 0;

		if (!childrenLen) return;
		switch (direction) {
		case 'left':
			for (var i = 0; i < childrenLen; i++) {
				// B50-ZK-381: Menu scrolling bug
				// child width may be larger than body.offsetWidth
				if (children[i].offsetLeft >= currScrollLeft
					|| children[i].offsetLeft + (children[i].offsetWidth - body.offsetWidth) >= currScrollLeft) {
					var preChild = children[i].previousSibling;
					if (!preChild)	return;
					movePos = preChild.offsetLeft;
					if (isNaN(movePos)) return;
					self._runId = setInterval(function () {
						if (!self._moveTo(body, movePos)) {
							self._afterMove();
						}
					}, 10);
					return;
				}
			}
			break;
		case 'right':
			var currRight = currScrollLeft + body.offsetWidth;
			for (var i = 0; i < childrenLen; i++) {
				var currChildRight = children[i].offsetLeft + children[i].offsetWidth;
				if (currChildRight > currRight) {
					movePos = currScrollLeft + (currChildRight - currRight);
					if (isNaN(movePos)) return;
					self._runId = setInterval(function () {
						if (!self._moveTo(body, movePos)) {
							self._afterMove();
						}
					}, 10);
					return;
				}
			}
			break;
		}
	},
	_moveTo: function (body, moveDest) {
		var currPos = this._bodyScrollLeft;
		if (currPos == moveDest)
			return false;

		var step = 5,
			delta = currPos > moveDest ? -step : step,
			setTo = currPos + delta;
		if ((setTo < moveDest && delta < 0) || (setTo > moveDest && delta > 0))
			setTo = moveDest;

		this._fixBodyScrollLeft(setTo);
		return true;
	},
	_afterMove: function () {
		clearInterval(this._runId);
		this._runId = null;
	},
	insertChildHTML_: function (child, before, desktop) {
		var vert = this.isVertical();
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
	},
	/**
	 * Returns whether it is a vertical menubar.
	 * @return boolean
	 * @since 9.5.0
	 */
	isVertical: function () {
		return 'vertical' == this.getOrient();
	},
	doKeyDown_: function (evt) {
		var direction = 0,
			isVertical = this.isVertical(),
			currentTarget = evt.target;
		switch (evt.key) {
			case 'ArrowLeft':
				if (!isVertical) direction = -1;
				break;
			case 'ArrowUp':
				if (isVertical) direction = -1;
				break;
			case 'ArrowRight':
				if (!isVertical) direction = 1;
				break;
			case 'ArrowDown':
				if (isVertical) direction = 1;
				break;
		}
		if (direction && currentTarget) {
			var target = direction < 0
				? this._getPrevVisibleMenuTarget(currentTarget)
				: this._getNextVisibleMenuTarget(currentTarget);
			if (target)
				target.focus();
			evt.stop();
		}
		this.$supers('doKeyDown_', arguments);
	},
	_getPrevVisibleMenuTarget: function (currentTarget) {
		var prev = currentTarget.previousSibling;
		if (!prev) {
			prev = this.lastChild;
		}
		return prev ? this._prevVisibleMenu(prev) : null;
	},
	_getNextVisibleMenuTarget: function (currentTarget) {
		var next = currentTarget.nextSibling;
		if (!next) {
			next = this.firstChild;
		}
		return next ? this._nextVisibleMenu(next) : null;
	},
	_nextVisibleMenu: function (menu) {
		for (var m = menu; m; m = m.nextSibling) {
			if (this.$class._isActiveItem(m))
				return m;
		}
		if (this.firstChild == menu)
			return menu;
		return this._nextVisibleMenu(this.firstChild);
	},
	_prevVisibleMenu: function (menu) {
		for (var m = menu; m; m = m.previousSibling) {
			if (this.$class._isActiveItem(m))
				return m;
		}
		if (this.lastChild == menu)
			return menu;
		return this._prevVisibleMenu(this.lastChild);
	}
}, {
	_isActiveItem: function (wgt) {
		return wgt.isVisible()
			&& (wgt.$instanceof(zul.menu.Menu) || wgt.$instanceof(zul.menu.Menuitem))
			&& !wgt.isDisabled();
	}
});

})();
