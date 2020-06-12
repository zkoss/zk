/* Caption.js

	Purpose:

	Description:

	History:
		Sun Nov 16 13:01:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 *  A header for a {@link Groupbox}.
 * It may contain either a text label, using {@link #setLabel},
 * or child elements for a more complex caption.
 * <p>Default {@link #getZclass}: z-caption.
 *
 */
zul.wgt.Caption = zk.$extends(zul.LabelImageWidget, {
	$define: {
	},
	//super//
	domDependent_: true, //DOM content depends on parent
	rerender: function () {
		var p = this.parent;
		if (p)
			p.clearCache(); // B50-ZK-244
		this.$supers('rerender', arguments);
	},
	domContent_: function () {
		var label = this.getLabel(),
			img = this.getImage(),
			title = this.parent ? this.parent._title : '',
			iconSclass = this.domIcon_();
		if (title) label = label ? title + ' - ' + label : title;
		label = zUtl.encodeXML(label);
		if (!img && !iconSclass) return label;

		if (!img) img = iconSclass;
		else img = '<img id="' + this.uuid + '-img" src="' + img + '" class="' + this.$s('image') + '" alt="" aria-hidden="true" />' + (iconSclass ? ' ' + iconSclass : '');
		return label ? img + ' ' + '<span class="' + this.$s('label') + '">' + label + '</span>' : img;
	},
	updateDomContent_: function () {
		var cnt = this.domContent_(),
		dn = this.$n('cave');
		if (dn) {
			var firstWgtDom;
			if (this.firstChild) {
				firstWgtDom = this.firstChild.$n();
			}
			for (var child = dn.firstChild, nextChild; child && child !== firstWgtDom; child = nextChild) {
				nextChild = child.nextSibling;
				jq(child).remove();
			}
			this.clearCache(); //B70-ZK-2370: clearCache after remove dom content
			jq(dn).prepend(cnt ? cnt : '&nbsp;');
		}
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			parent = this.parent;

		if (!parent.$instanceof(zul.wgt.Groupbox))
			return sc;

		return sc + (parent._closable ? '' : ' ' + this.$s('readonly'));
	},
	doClick_: function () {
		if (this.parent.$instanceof(zul.wgt.Groupbox) && this.parent.isClosable())
			this.parent.setOpen(!this.parent.isOpen());
		this.$supers('doClick_', arguments);
	},
	//private//
	_getBlank: function () {
		return '&nbsp;';
	},
	/** Whether to generate a collapsible button (determined by parent only). */
	_isCollapsibleVisible: function () {
		var parent = this.parent;
		return parent.isCollapsible && parent.getCollapseOpenIconClass_ && parent.isCollapsible();
	},
	/** Whether to generate a close button (determined by parent only). */
	_isCloseVisible: function () {
		var parent = this.parent;
		return parent.isClosable && parent.getClosableIconClass_ && parent.isClosable();
	},
	/** Whether to generate a minimize button (determined by parent only). */
	_isMinimizeVisible: function () {
		var parent = this.parent;
		return parent.isMinimizable && parent.getMinimizableIconClass_ && parent.isMinimizable();
	},
	/** Whether to generate a maximize button (determined by parent only). */
	_isMaximizeVisible: function () {
		var parent = this.parent;
		return parent.isMaximizable && parent.getMaximizableIconClass_ && parent.isMaximizable();
	},
	beforeMinFlex_: function (o) { // Fixed for B50-3343388.zul
		if (o == 'w')
			this.$n().width = '';
	},
	// override for the bug ZK-1799
	setFlexSizeW_: function (n, zkn, width, isFlexMin) {
		if (isFlexMin) {
			if (this._isCloseVisible()) {
				var close = this.parent.$n('close');
				width += close.offsetWidth + zk(close).marginWidth();
			}
			if (this._isMaximizeVisible()) {
				var max = this.parent.$n('max');
				width += max.offsetWidth + zk(max).marginWidth();
			}
			if (this._isMinimizeVisible()) {
				var min = this.parent.$n('min');
				width += min.offsetWidth + zk(min).marginWidth();
			}
			if (this._isCollapsibleVisible()) {
				var exp = this.parent.$n('exp');
				width += exp.offsetWidth + zk(exp).marginWidth();
			}
		}
		this.$supers('setFlexSizeW_', arguments);
	},
	// override
	// ZK-786
	getImageNode: function () {
		if (!this._eimg && this._image) {
			var n = this.$n('img');
			if (n) this._eimg = n;
		}
		return this._eimg;
	}
});
