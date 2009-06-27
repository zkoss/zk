/* Listcell.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listcell = zk.$extends(zul.LabelImageWidget, {
	_colspan: 1,
	$define: {
		colspan: [
			function (colspan) {
				return colspan > 1 ? colspan: 1;
			},
			function () {
				var n = this.getNode();
				if (n) n.colSpan = this._colspan;
			}]
	},
	getListbox: function () {
		var p = this.parent;
		return p ? p.parent: null;
	},

	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listcell" : this._zclass;
	},
	getTextNode_: function () {
		return jq(this.getNode()).find('>div:first')[0];
	},
	getMaxlength: function () {
		var box = this.getListbox();
		if (!box) return 0;
		if (box.getMold() == 'select')
			return box.getMaxlength();
		var lc = this.getListheader();
		return lc ? lc.getMaxlength() : 0;
	},
	getListheader: function () {
		var box = this.getListbox();
		if (box && box.listhead) {
			var j = this.getChildIndex();
			if (j < box.listhead.nChildren)
				return box.listhead.getChildAt(j);
		}
		return null;
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	},
	domContent_: function () {
		var s1 = this.$supers('domContent_', arguments),
			s2 = this._colHtmlPre();
		return s1 ? s2 ? s2 + '&nbsp;' + s1: s1: s2;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if ((!no || !no.zclass) && (this.parent.$instanceof(zul.sel.Listgroup)
			|| this.parent.$instanceof(zul.sel.Listgroupfoot))) {
			var zcls = this.parent.getZclass();
			scls += ' ' + zcls + '-inner';
		}
		return scls;
	},
	_colHtmlPre: function () {
		var s = '',
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this) {
			if (this.parent.$instanceof(zul.sel.Listgroup)) {
				var zcls = this.parent.getZclass();
				s = '<span id="' + this.parent.uuid + '-img" class="' + zcls + '-img ' + zcls
					+ '-img-' + (this.parent._open ? 'open' : 'close') + '"></span>';
			}
				
			if (box.isCheckmark()) {
				var item = this.parent,
					chkable = item.isCheckable();
				s += '<input type="' + (box.isMultiple() ? 'checkbox': 'radio')
					+ '" id="' + item.uuid + '-cm"';
				if (!chkable || item.isDisabled())
					s += ' disabled="disabled"';
				if (item.isSelected())
					s += ' checked="checked"';
				if (!box.isMultiple()) 
					s += ' name="' + box.uuid + '"';
				if (!chkable)
					s += ' style="visibility:hidden"';
				s += '/>';
			}
		}
		return s;
	},
	doMouseOver_: function(evt) {
		if (zk.gecko && (this._draggable || this.parent._draggable)) {
			var tag = evt.domTarget.tagName;
			if (tag != "INPUT" && tag != "TEXTAREA") {
				var n = this.getNode();
				if (n) n.firstChild.style.MozUserSelect = "none";
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		if (zk.gecko && (this._draggable || this.parent._draggable)) {
			var n = this.getNode();
			if (n) n.firstChild.style.MozUserSelect = "none";
		}
		this.$supers('doMouseOut_', arguments);
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments)
			+ (this._colspan > 1 ? ' colspan="' + this._colspan + '"' : '');
	}
});
