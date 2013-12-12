/* Listcell.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _isListgroup(wgt) {
		return zk.isLoaded('zkex.sel') && wgt.$instanceof(zkex.sel.Listgroup);
	}	
	function _isListgroupfoot(wgt) {
		return zk.isLoaded('zkex.sel') && wgt.$instanceof(zkex.sel.Listgroupfoot);
	}	
/**
 * A list cell.
 * 
 * <p>Default {@link #getZclass}: z-listcell
 */
zul.sel.Listcell = zk.$extends(zul.LabelImageWidget, {
	_colspan: 1,
	$define: {
    	/** Returns number of columns to span this cell.
    	 * Default: 1.
    	 * @return int
    	 */
    	/** Sets the number of columns to span this cell.
    	 * <p>It is the same as the colspan attribute of HTML TD tag.
    	 * @param int colspan
    	 */
		colspan: [
			function (colspan) {
				return colspan > 1 ? colspan: 1;
			},
			function () {
				var n = this.$n();
				if (n) n.colSpan = this._colspan;
			}]
	},
	setLabel: function () {
		this.$supers('setLabel', arguments);
		if (this.desktop) {
			var p = this.parent;
	 		if (_isListgroup(p))
				p.rerender();
			else if (p.$instanceof(zul.sel.Option))
				this.getListbox().rerender(); // for IE, we cannot use this.parent.rerender();
		}
	},
	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	getListbox: function () {
		var p = this.parent;
		return p ? p.parent: null;
	},

	//super//
	getTextNode: function () {
		return jq(this.$n()).find('>div:first')[0];
	},
	/** Returns the maximal length for this cell.
	 * If listbox's mold is "select", it is the same as
	 * {@link Select#getMaxlength}
	 * If not, it is the same as the correponding {@link #getListheader}'s 
	 * {@link Listheader#getMaxlength}.
	 *
	 * <p>Note: {@link Option#getMaxlength} is the same as {@link Select#getMaxlength}.
	 * @return int
	 */
	getMaxlength: function () {
		var box = this.getListbox();
		if (!box) return 0;
		if (box.getMold() == 'select')
			return box.getMaxlength();
		var lc = this.getListheader();
		return lc ? lc.getMaxlength() : 0;
	},
	/** Returns the list header that is in the same column as
	 * this cell, or null if not available.
	 * @return Listheader
	 */
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
		var scls = this.$supers('domClass_', arguments),
			p = this.parent;
		
		if ((!no || !no.zclass) && (_isListgroup(p) || _isListgroupfoot(p)))
			scls += ' ' + p.$s('inner');
		
		return scls;
	},
	_colHtmlPre: function () {
		var s = '',
			box = this.getListbox(),
			p = this.parent;
		if (box != null && p.firstChild == this) {
			var isGrp = _isListgroup(p);
			// insert checkmark
			//B70-ZK-2053:make sure checkmark won't display on multiple listgroup
			if (box.isCheckmark() && !_isListgroupfoot(p) 
					&& (!isGrp || (box.groupSelect && box.isMultiple()))) {
				var chkable = p.isCheckable(),
					multi = box.isMultiple();
				s += '<span id="' + p.uuid + '-cm" class="' + p.$s('checkable') 
					+ ' ' + (multi ? p.$s('checkbox') : p.$s('radio'));
				
				if (!chkable || p.isDisabled())
					s += ' ' + p.$s('disabled');
				
				s += '"';
				if (!chkable)
					s += ' style="visibility:hidden"';
				
				s += '><i class="' + p.$s('icon') + ' ' 
					+ (multi ? 'z-icon-check' : 'z-icon-radio') + '"></i></span>';
			}
			// insert toggle icon
			if (isGrp) {
				var cls = p._open ? 
						p.getIconOpenClass_() + ' ' + p.$s('icon-open') : 
						p.getIconCloseClass_() + ' ' + p.$s('icon-close');
				s += '<span id="' + p.uuid + '-img" class="' + p.$s('icon') + 
					'"><i class="' + cls + '"></i></span>';
			}
			if (s) return s;
		}
		return (!this.getImage() && !this.getLabel() && !this.firstChild) ? '&nbsp;': '';
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);
		//sync frozen
		var box = this.getListbox(),
			frozen = box ? box.frozen : null,
			node = this.$n(),
			td, tds;
		if (frozen && node)
			box._moveToHidingFocusCell(node.cellIndex);
	},
	doMouseOver_: function(evt) {
		if (zk.gecko && (this._draggable || this.parent._draggable)
		&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			var n = this.$n();
			if (n) n.firstChild.style.MozUserSelect = 'none';
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		if (zk.gecko && (this._draggable || this.parent._draggable)) {
			var n = this.$n();
			if (n) n.firstChild.style.MozUserSelect = ''; // Bug ZK-580
		}
		this.$supers('doMouseOut_', arguments);
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments)
			+ (this._colspan > 1 ? ' colspan="' + this._colspan + '"' : '');
	},
	//-- super --//
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments),
			head = this.getListheader();
		if (head) {
			if (!head.isVisible())
				style += 'display:none;';
			if (head._align)
				style += 'text-align:' + head._align + ';';
			if (head._valign)
				style += 'vertical-align:' + head._valign + ';';
		}
		return style;
	},
	bindChildren_: function () {
		var p;
		if (!(p = this.parent) || !p.$instanceof(zul.sel.Option))
			this.$supers('bindChildren_', arguments);
	},
	unbindChildren_: function () {
		var p;
		if (!(p = this.parent) || !p.$instanceof(zul.sel.Option))
			this.$supers('unbindChildren_', arguments);
	},
	deferRedrawHTML_: function (out) {
		out.push('<td', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></td>');
	}
});
})();