/* Caption.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 13:01:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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
		if (this.parent && this.parent.$instanceof(zul.wgt.Groupbox)
				&& this.parent.isLegend())
			this.parent.rerender();
		else
			this.$supers('rerender', arguments);
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-caption";
	},
	domContent_: function () {
		var label = this.getLabel(),
			img = this.getImage(),
			title = this.parent ? this.parent._title: '';
		if (title) label = label ? title + ' - ' + label: title;
		label = zUtl.encodeXML(label);
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			parent = this.parent;
			
		if (!parent.$instanceof(zul.wgt.Groupbox))
			return sc;
			
		return sc + (parent._closable ? '': ' ' + this.getZclass() + '-readonly');
	},
	doClick_: function () {
		if (this.parent.$instanceof(zul.wgt.Groupbox))
			this.parent.setOpen(!this.parent.isOpen());
		this.$supers('doClick_', arguments);
	},
	//private//
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
	//bug #3005284: (Chrome)Groupbox hflex="min" in borderlayout wrong sized
	//legend in fieledset, the margin in safari/chrome will be huge, 
	//shall ignore it when calculate width. @see widget#setMinFlexSize
	_isIgnoreMargin: function () {
		var parent = this.parent;
		return zk.safari && parent && parent.$instanceof(zul.wgt.Groupbox) && parent.isLegend();  
	}
});