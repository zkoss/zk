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
			title = this.parent ? this.parent._title: '',
			iconSclass = this.domIcon_();
		if (title) label = label ? title + ' - ' + label: title;
		label = zUtl.encodeXML(label);
		if (!img && !iconSclass) return label;

		if (!img) img = iconSclass;
		else img = '<img id="' + this.uuid + '-img" src="' + img + '" class="' + this.$s('image') + '" />' + (iconSclass ? ' ' + iconSclass : '');
		return label ? img + ' ' + label: img;
	},
	updateDomContent_: function () { // B50-ZK-313: only replace innerHTML
		var cnt = this.domContent_(),
			dn = this.$n('cave');
		if (dn) 
			jq(dn).html(cnt ? cnt : '&nbsp;');
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			parent = this.parent;
			
		if (!parent.$instanceof(zul.wgt.Groupbox))
			return sc;
			
		return sc + (parent._closable ? '': ' ' + this.$s('readonly'));
	},
	doClick_: function () {
		if (this.parent.$instanceof(zul.wgt.Groupbox))
			this.parent.setOpen(!this.parent.isOpen());
		this.$supers('doClick_', arguments);
	},
	//private//
	_getBlank: function () {
		return '&nbsp;';
	},
	/** Whether to generate a collapsible button. */
	_isCollapsibleVisible: function () {
		var parent = this.parent;
		return parent.isCollapsible && parent.isCollapsible();
	},
	/** Whether to generate a close button. */
	_isCloseVisible: function () {
		var parent = this.parent;
		return parent.isClosable && parent.isClosable()
			&& !parent.$instanceof(zul.wgt.Groupbox);
	},
	/** Whether to generate a minimize button. */
	_isMinimizeVisible: function () {
		var parent = this.parent;
		return parent.isMinimizable && parent.isMinimizable();
	},
	/** Whether to generate a maximize button. */
	_isMaximizeVisible: function () {
		var parent = this.parent;
		return parent.isMaximizable && parent.isMaximizable();
	},
	beforeMinFlex_: function (o) { // Fixed for B50-3343388.zul
		if (o == 'w')
			this.$n().width = '';
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