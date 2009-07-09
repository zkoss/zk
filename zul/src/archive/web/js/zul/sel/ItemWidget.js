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
			var mesh = this.getMeshWidget();
			if (mesh)
				mesh.toggleItemSelection(this);
				
			this._setSelectedDirectly(selected);
		}
	},
	_setSelectedDirectly: function (selected) {
		var n = this.$n();
		if (n) {
			jq(n)[selected ? 'addClass' : 'removeClass'](this.getZclass() + '-seld');
			var cm = this.$n('cm');
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
			if (zk(row.cells[i]).isVisible()) return row.cells[i];
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
		var n = this.$n(),
			zcls = this.getZclass();
		if (undo) {
			jq(n).removeClass(zcls + "-over-seld")
				.removeClass(zcls + "-over");
		} else {
			var $n = jq(n);
			$n.addClass($n.hasClass(zcls + "-seld") ? zcls + "-over-seld" : zcls + "-over");
		}
	},
	focus: function (timeout) {
		this.getMeshWidget()._focusItem = this;
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			var cm = this.$n('cm');
			if (cm) {
				zk(cm).focus(timeout);
				return true;
			} else 
				this._doFocusIn();
		}
		return false;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var cm = this.$n('cm');
		if (cm)
			this.domListen_(cm, 'onFocus')
				.domListen_(cm, 'onBlur', '_doFocusOut');
	},
	unbind_: function () {
		var cm = this.$n('cm');
		if (cm)
			this.domUnlisten_(cm, 'onFocus')
				.domUnlisten_(cm, 'onBlur', '_doFocusOut');
		this.$supers('unbind_', arguments);
	},
	_doFocusIn: function () {
		var n = this.$n();
		var widget = this;
		if (n){
			jq(widget._getVisibleChild(n)).addClass(this.getZclass() + "-focus");
			jq(n).siblings().each(function(){
				var n = this[0];
				if(n)
					jq(widget._getVisibleChild(n)).removeClass(this.getZclass() + "-focus");
			});
		}
	},
	_doFocusOut: function () {
		var n = this.$n();
		if (n) {
			var zcls = this.getZclass();
			jq(n).removeClass(zcls + "-focus");
			for (var i = n.cells.length; i--;)
				jq(n.cells[i]).removeClass(zcls + "-focus");
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
		var cm = this.$n('cm');
		if (cm && !cm.checked) {
			box._headercm.checked = false;
			return;
		}
		var checked;
		for (var it = box.getBodyWidgetIterator(), w; (w = it.next());)
			if (!w.isDisabled())
				if (!(checked = (w.$n('cm') || {}).checked)) break;
		
		if (checked) box._headercm.checked = true;
	},
	_checkClick: function (evt) {
		if (this.getMeshWidget().isMultiple()) {
			this._checkAll();
		} else {
			var r = this.$n('cm');
			for (var nms = jq.$$(r.name), i = nms.length; i--;)
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
		evt.stop({propagation: true});
		this.$supers('doClick_', arguments);
	},
	doMouseOver_: function(evt) {
		if (this.isDisabled()) return;
		this._toggleEffect();
		evt.stop();
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		if (this.isDisabled() || (zk.ie &&
				jq.isAncestor(this.$n(), evt.domEvent.relatedTarget || evt.domEvent.toElement)))
			return;
			
		this._toggleEffect(true);
		evt.stop({propagation: true});
		this.$supers('doMouseOut_', arguments);
	},
	doKeyDown_: function (evt) {
		var tag = evt.domTarget.tagName,
			mate = this.getMeshWidget();
		if (!zk.gecko3 || (tag != "INPUT" && tag != "TEXTAREA"))
			zk(mate.$n()).disableSelection();
		mate._doKeyDown(evt);
		evt.stop({propagation: true});
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		zk(this.getMeshWidget().$n()).enableSelection();
		evt.stop({propagation: true});
		this.$supers('doKeyUp_', arguments);
	}
});