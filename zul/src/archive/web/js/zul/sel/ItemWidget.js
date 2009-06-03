/* ItemWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri May 22 21:50:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.ItemWidget = zk.$extends(zul.Widget, {
	_checkable: true,
	$define: {
		checkable: function () {
			if (this.desktop)
				this.rerender();
		},
		disabled: function () {
			if (this.desktop)
				this.rerender();
		},
		value: null
	},
	setSelected: function (selected) {
		if (this._selected != selected) {
			if (this.parent)
				this.parent.toggleItemSelection(this);
				
			this._setSelectedDirectly(selected);
		}
	},
	_setSelectedDirectly: function (selected) {
		var n = this.getNode();
		if (n) {
			zDom[selected ? 'addClass' : 'rmClass'](n, this.getZclass() + '-seld');
			var cm = this.getSubnode('cm');
			if (cm) {
				cm.checked = selected;
				this._checkClick();
			}				
		}
		this._selected = selected;
	},
	getLabel: function () {
		return this.firstChild ? this.firstChild.getLabel() : null; 
	},
	isSelected: function () {
		return this._selected;
	},
	isStripeable_: function () {
		return true;
	},
	getMeshWidget: function () {
		return this.parent;
	},
	_getVisibleChild: function (row) {
		for (var i = 0, j = row.cells.length; i < j; i++)
			if (zDom.isVisible(row.cells[i])) return row.cells[i];
		return row;
	},
	//super//
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.isStripeable_()) {
				var p = this.getMeshWidget();
				if (p) p.stripe();
			}
		}
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass(),
				added = this.isDisabled() ? zcls + '-disd' : this.isSelected() ? zcls + '-seld' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	_toggleEffect: function (undo) {
		var n = this.getNode(),
			zcls = this.getZclass();
		if (undo) {
			zDom.rmClass(n, zcls + "-over-seld");
			zDom.rmClass(n, zcls + "-over");
		} else {
			zDom.addClass(n, zDom.hasClass(n, zcls + "-seld") ? zcls + "-over-seld" : zcls + "-over");	
		}
	},
	focus: function (timeout) {
		this.getMeshWidget()._focusItem = this;
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			var cm = this.getSubnode('cm');
			if (cm) {
				zDom.focus(cm, timeout);
				return true;
			} else 
				this._doFocusIn();
		}
		return false;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var box = this.getMeshWidget();
		if (box.isCheckmark() && this.isCheckable()) {
			var cm = this.getSubnode('cm');
			this.domListen_(cm, 'onFocus');
			this.domListen_(cm, 'onBlur', '_doFocusOut');
		}
	},
	unbind_: function () {
		var cm = this.getSubnode('cm');
		if (cm) {
			this.domUnlisten_(cm, 'onFocus');
			this.domUnlisten_(cm, 'onBlur', '_doFocusOut');
		}
		this.$supers('unbind_', arguments);
	},
	_doFocusIn: function () {
		var n = this.getNode();
		if (n)
			zDom.addClass(this._getVisibleChild(n), this.getZclass() + "-focus");
	},
	_doFocusOut: function () {
		var n = this.getNode();
		if (n) {
			var zcls = this.getZclass();
			zDom.rmClass(n, zcls + "-focus");
			for (var i = n.cells.length; --i >= 0;)
				zDom.rmClass(n.cells[i], zcls + "-focus");
		}
	},
	_doFocus: function (evt) {
		if (this.canActivate({checkOnly:true})) {
			this.doFocus_(evt);
			this._doFocusIn();
		}
	},
	_checkAll: function () {
		var box = this.getMeshWidget();		
		if (!box || !box._headercm) return;
		var cm = this.getSubnode('cm');
		if (cm && !cm.checked) {
			box._headercm.checked = false;
			return;
		}
		var checked;
		for (var it = box.getBodyWidgetIterator(), w; (w = it.next());)
			if (!w.isDisabled())
				if (!(checked = (w.getSubnode('cm') || {}).checked)) break;
		
		if (checked) box._headercm.checked = true;
	},
	_checkClick: function (evt) {
		if (this.getMeshWidget().isMultiple()) {
			this._checkAll();
		} else {
			var r = this.getSubnode('cm');
			for (var nms = zDom.$$(r.name), i = nms.length; --i >= 0;)
				nms[i].defaultChecked = false;
			r.defaultChecked = r.checked;
		}
	},
	// event
	doClick_: function(evt) {
		if (this.isDisabled()) return;
		// make sure the target is the ItemWidget
		evt.target = this;
		this.getMeshWidget()._doClick(evt);
		this.$supers('doClick_', arguments);
	},
	doMouseOver_: function(evt) {
		if (this.isDisabled()) return;
		this._toggleEffect();
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		if (this.isDisabled()) return;
		this._toggleEffect(true);
		this.$supers('doMouseOut_', arguments);
	},
	doKeyDown_: function (evt) {
		var tag = zDom.tag(evt.domTarget),
			mate = this.getMeshWidget();
		if (!zk.gecko3 || (tag != "INPUT" && tag != "TEXTAREA"))
			zDom.disableSelection(mate.getNode());
		mate._doKeyDown(evt);
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		zDom.enableSelection(this.getMeshWidget().getNode());
		this.$supers('doKeyUp_', arguments);
	}
});