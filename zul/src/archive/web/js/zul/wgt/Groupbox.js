/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	legend: true,
	_open: true,
	_closable: true,

	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			if (this.node) {
				zDom[open ? 'rmClass': 'addClass'](this.node, this.getZclass() + "-collapsed");
				if (!fromServer) this.fire2('onOpen', open);
				if (open) zWatch.fireDown('onSize', -1, this);
			}
		}
	},
	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		this._closable = closable;
	},
	getContentStyle: function () {
		return this._cntStyle;
	},
	setContentStyle: function (style) {
		if (this._cntStyle != style) {
			this._cntStyle = style;
			//TODO
		}
	},
	getContentSclass: function () {
		return this._cntSclass;
	},
	setContentSclass: function (sclass) {
		if (this._cntSclass != sclass) {
			this._cntSclass = sclass;
			//TODO
		}
	},

	_contentAttrs: function () {
		var html = ' class="', s = this._cntSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';
		s = this._cntStyle;
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//super//
	focus: function () {
		if (this.node) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus())
					return true;
			return cap && cap.focus();
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: "z-fieldset";
	},

	appendChild: function (child) {
		if (this.$super('appendChild', child)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	insertBefore: function (child, sibling) {
		if (this.$super('insertBefore', child, sibling)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	removeChild: function (child) {
		if (this.$super('removeChild', child)) {
			if (child == this.caption)
				this.caption = null;
			return true;
		}
		return false;
	},

	domClass_: function (no) {
		var html = this.$super('domClass_', no);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-collapsed';
		}
		return html;
	}
});