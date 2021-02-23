/* Toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A toolbar.
 *
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: this mold is used for {@link zul.wnd.Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-toolbar
 */
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_align: 'start',
	_overflowPopupIconSclass: 'z-icon-ellipsis-h',

	$define: {
		/**
		 * Returns the alignment of any children added to this toolbar. Valid values
		 * are "start", "end" and "center".
		 * <p>Default: "start"
		 * @return String
		 */
		/**
		 * Sets the alignment of any children added to this toolbar. Valid values
		 * are "start", "end" and "center".
		 * <p>Default: "start", if null, "start" is assumed.
		 * @param String align
		 */
		align: _zkf = function () {
			this.rerender();
		},
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: _zkf,
		/**
		 * Return whether toolbar has a button that shows a popup
		 * which contains those content weren't able to fit in the toolbar.
		 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
		 * Default: false.
		 *
		 * @return boolean
		 * @since 8.6.0
		 */
		 /**
		 * Set whether toolbar has a button that shows a popup
		 * which contains those content weren't able to fit in the toolbar.
		 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
		 *
		 * @param boolean overflowPopup whether toolbar has a button that shows a popup
		 * @since 8.6.0
		 */
		overflowPopup: function () {
			this.rerender();
		},
		/**
		 * Returns the overflowPopupIconSclass.
		 * Default: z-icon-ellipsis-h.
		 * @since 9.6.0
		 */
		 /**
		 * Sets the overflowPopupIconSclass.
		 * When overflowPopup is true, toolbar has a button that shows a popup
		 * users can customize the overflow popup icon.
		 * Default: z-icon-ellipsis-h.
		 * @since 9.6.0
		 */
		overflowPopupIconSclass: function () {
			var icon = this.$n('overflowpopup-button');
			if (this.desktop && icon)
				icon.className = this._getOverflowPopupBtnClass();
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.isOverflowPopup()) {
			zWatch.listen({onFloatUp: this, onCommandReady: this, onSize: this});
			this.domListen_(this.$n('overflowpopup-button'), 'onClick', '_openPopup');
		}
	},
	unbind_: function () {
		var popup = this.$n('pp');
		if (popup) {
			this.domUnlisten_(this.$n('overflowpopup-button'), 'onClick', '_openPopup');
			zWatch.unlisten({onFloatUp: this, onCommandReady: this, onSize: this});
		}
		this.$supers('unbind_', arguments);
	},
	_getOverflowPopupBtnClass: function () {
		return this.$s('overflowpopup-button') + ' ' + this.getOverflowPopupIconSclass() + ' z-icon-fw';
	},
	_openPopup: function (evt) {
		if (this._open)
			return;

		this._open = true;
		var popup = this.$n('pp');
		this.setFloating_(true, {node: popup});
		zWatch.fire('onFloatUp', this);
		var topZIndex = this.setTopmost();
		popup.style.zIndex = topZIndex > 0 ? topZIndex : 1;
		jq(popup).removeClass(this.$s('popup-close')).addClass(this.$s('popup-open')).zk.makeVParent();
		this._syncPopupPosition();
	},
	_syncPopupPosition: function () {
		zk(this.$n('pp')).position(this.$n(), 'after_end');
	},
	onFloatUp: function (ctl) {
		if (!zUtl.isAncestor(this, ctl.origin))
			this._closePopup();
	},
	_closePopup: function () {
		if (!this._open)
			return;

		this._open = false;
		var jqPopup = jq(this.$n('pp'));
		jqPopup.removeClass(this.$s('popup-open')).addClass(this.$s('popup-close')).zk.undoVParent();
		this.setFloating_(false);
	},
	onCommandReady: function () {
		if (this.desktop && this.isOverflowPopup())
			this._adjustContent();
	},
	onSize: function () {
		this.$supers('onSize', arguments);
		if (this.desktop && this.isOverflowPopup()) {
			this._adjustContent();
			if (this._open)
				this._syncPopupPosition();
		}
	},
	_adjustContent: function () {
		if (zUtl.isImageLoading()) {
			setTimeout(this.proxy(this._adjustContent), 20);
			return;
		}

		var jqToolbar = jq(this),
			contentWidth = jqToolbar.width() - jq(this.$n('overflowpopup-button')).width(),
			popup = this.$n('pp'),
			cave = this.$n('cave'),
			oldToolbarChildren = jq(cave).children().toArray(),
			oldPopupChildren = jq(popup).children().toArray(),
			children = oldToolbarChildren.concat(oldPopupChildren),
			childrenAmount = children.length,
			tempChildrenWidth = 0,
			newPopupChildrenAmount = 0;

		// Calculate width to decide how many children should be displayed on popup.
		for (var i = 0; i < childrenAmount; i++) {
			tempChildrenWidth += jq(children[i]).outerWidth(true);
			if (tempChildrenWidth >= contentWidth) {
				newPopupChildrenAmount = childrenAmount - i;
				break;
			}
		}

		var popupChildrenDiff = newPopupChildrenAmount - oldPopupChildren.length;
		if (!popupChildrenDiff)
			return;

		// Start to move children
		var overflowpopupOn = this.$s('overflowpopup-on'),
			overflowpopupOff = this.$s('overflowpopup-off');
		if (newPopupChildrenAmount) {
			jqToolbar.removeClass(overflowpopupOff).addClass(overflowpopupOn);
			if (popupChildrenDiff > 0) {
				for (var i = 0; i < popupChildrenDiff; i++)
					popup.insertBefore(oldToolbarChildren.pop(), popup.children[0]);
			} else {
				for (var i = 0; i < -popupChildrenDiff; i++)
					cave.appendChild(oldPopupChildren.shift());
			}
		} else {
			jqToolbar.removeClass(overflowpopupOn).addClass(overflowpopupOff);
			if (this._open)
				this._closePopup();
			while (oldPopupChildren.length)
				cave.appendChild(oldPopupChildren.shift());
		}
	},
	// super
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var tabs = this.parent && zk.isLoaded('zul.tab') && this.parent.$instanceof(zul.tab.Tabbox) ? this.$s('tabs') : '';

			if (tabs)
				sc += ' ' + tabs;
			if (this.inPanelMold())
				sc += ' ' + this.$s('panel');
			if ('vertical' == this.getOrient())
				sc += ' ' + this.$s('vertical');
			if (this.isOverflowPopup())
				sc += ' ' + this.$s('overflowpopup') + ' ' + this.$s('overflowpopup-off');
		}
		return sc;
	},
	// Bug ZK-1706 issue: we have to expand the width of the content div when
	// align="left", others won't support
	setFlexSizeW_: function (n, zkn, width, isFlexMin) {
		this.$supers('setFlexSizeW_', arguments);
		if (!isFlexMin && this.getAlign() == 'start') {
			var cave = this.$n('cave');
			if (cave)
				cave.style.width = jq.px0(zk(this.$n()).contentWidth());
		}
	},
	/**
	 * Returns whether is in panel mold or not.
	 * @return boolean
	 */
	inPanelMold: function () {
		return this._mold == 'panel';
	},
	appendChild: function (child) {
		this.$supers('appendChild', arguments);
		var popup = this.$n('pp');
		if (popup && popup.children.length > 0)
			popup.appendChild(child.$n());
	},
	removeChild: function (child) {
		var popupNode = this.$n('pp'),
			childNode = child.$n();
		if (popupNode && childNode) {
			var childOnPopup = childNode.parentNode == popupNode;
			if (childOnPopup && popupNode.children.length == 1) {
				jq(this.$n()).removeClass(this.$s('overflowpopup-on')).addClass(this.$s('overflowpopup-off'));
				this._closePopup(); // should close popup if overflowpopup turns off
			}
		}
		this.$supers('removeChild', arguments);
	},
	// protected
	onChildAdded_: function () {
		this.$supers('onChildAdded_', arguments);
		if (this.inPanelMold())
			this.rerender();
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (!this.childReplacing_ && this.inPanelMold())
			this.rerender();
	}
});
