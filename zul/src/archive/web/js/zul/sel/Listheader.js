/* Listheader.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listheader = zk.$extends(zul.mesh.SortWidget, {
	getListbox: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	getMeshBody: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	$define: {
		maxlength: [function (v) {
			return !v || v < 0 ? 0 : v; 
		}, function () {
			if (this.desktop)
				this.updateCells_();
		}]
	},
	updateCells_: function () {
		var box = this.getListbox();
		if (box == null || box.getMold() == 'select')
			return;

		var jcol = this.getChildIndex();
		for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
			if (jcol < w.nChildren)
				return w.getChildAt(jcol).rerender();
	},
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listheader" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var cm = this.$n('cm');
		if (cm) {
			this.getListbox()._headercm = cm;
			this.domListen_(cm, 'onClick')
				.domListen_(cm, 'onMouseOver')
				.domListen_(cm, 'onMouseOut');
		}
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		var cm = this.$n('cm');
		if (cm) { 
			this._checked = this.getListbox()._headercm = null;
			this.domUnlisten_(cm, 'onClick')
				.domUnlisten_(cm, 'onMouseOver')
				.domUnlisten_(cm, 'onMouseOut');
		}
	},
	_doMouseOver: function (evt) {
		 var cls = this._checked ? '-img-over-seld' : '-img-over';
		 jq(evt.domTarget).addClass(this.getZclass() + cls);
	},
	_doMouseOut: function (evt) {
		 var cls = this._checked ? '-img-over-seld' : '-img-over',
		 	$n = jq(evt.domTarget),
			zcls = this.getZclass();
		 $n.removeClass(zcls + cls);
		 if (this._checked)
		 	$n.addClass(zcls + '-img-seld');
	},
	_doClick: function (evt) {
		this._checked = !this._checked;
		var box = this.getListbox(),
			$n = jq(evt.domTarget),
			zcls = this.getZclass(); 
		if (this._checked) {
			$n.removeClass(zcls + '-img-over').addClass(zcls + '-img-over-seld');
			box.selectAll(true, evt)
		} else {
			$n.removeClass(zcls + '-img-over-seld')
				.removeClass(zcls + '-img-seld')
				.addClass(zcls + '-img-over');
			box._select(null, evt);
		}
	},
	domContent_: function () {
		var s = this.$supers('domContent_', arguments),
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this 
		&& box.isCheckmark() && box.isMultiple())
			s = '<span id="' + this.uuid + '-cm" class="' + this.getZclass() + '-img"/>'
				+ (s ? '&nbsp;' + s:'');
		return s;
	}
});
