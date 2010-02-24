/* ItemWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri May 22 21:50:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The item widget for {@link Treeitem} and {@link Listitem}
 */
zul.sel.ItemWidget = zk.$extends(zul.Widget, {
	_checkable: true,
	$define: {
    	/** Returns whether it is checkable.
    	 * <p>Default: true.
    	 * @return boolean
    	 */
    	/** Sets whether it is checkable.
    	 * <p>Default: true.
    	 * @param boolean checkable
    	 */
		checkable: function () {
			if (this.desktop)
				this.rerender();
		},
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function () {
			if (this.desktop)
				this.rerender();
		},
		/** Returns the value.
		 * <p>Default: null.
		 * <p>Note: the value is application dependent, you can place
		 * whatever value you want.
		 * <p>If you are using listitem/treeitem with HTML Form (and with
		 * the name attribute), it is better to specify a String-typed
		 * value.
		 * @return String
		 */
		/** Sets the value.
		 * @param String value the value.
		 * <p>Note: the value is application dependent, you can place
		 * whatever value you want.
		 * <p>If you are using listitem/treeitem with HTML Form (and with
		 * the name attribute), it is better to specify a String-typed
		 * value.
		 */
		value: null
	},
	/** Sets whether it is selected.
	 * @param boolean selected
	 */
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
			if (this.$n('cm'))
				this._checkClick();				
		}
		this._selected = selected;
	},
	/** Returns the label of the {@link Listcell} or {@link Treecell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	getLabel: function () {
		return this.firstChild ? this.firstChild.getLabel() : null; 
	},
	/** Returns whether it is selected.
	 * <p>Default: false.
	 * @return boolean
	 */
	isSelected: function () {
		return this._selected;
	},
	/**
	 * Returns whether is stripeable or not.
	 * <p>Default: true.
	 * @return boolean
	 */
	isStripeable_: function () {
		return true;
	},
	/**
	 * Returns the mesh widget.
	 * @return zul.mesh.MeshWidget
	 */
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
		var mesh = this.getMeshWidget();
			mesh._focusItem = this;
		if (this.isVisible() && this.canActivate({checkOnly: true})) {
			this._doFocusIn();
			if (zk.currentFocus != mesh.$n('a'))
				zk(mesh.$n('a')).focus(timeout);
		}
		return false;
	},
	_doFocusIn: function () {
		var n = this.$n();
		if (n)
			jq(this._getVisibleChild(n)).addClass(this.getZclass() + "-focus");
	},
	_doFocusOut: function () {
		var n = this.$n();
		if (n) {
			var zcls = this.getZclass();
			jq(n).removeClass(zcls + "-focus");
			jq(n.cells).removeClass(zcls + "-focus");
		}
	},
	_checkAll: function () {
		var box = this.getMeshWidget();		
		if (!box || !box._headercm) return;
		var cm = this.$n('cm');
		if (cm && !this.isSelected()) {
			var header = zk.Widget.$(box._headercm),
				zcls = header.getZclass();
			jq(box._headercm).removeClass(zcls + '-img-seld');
			return;
		}
		var checked;
		for (var it = box.getBodyWidgetIterator(), w; (w = it.next());) 
			if (w.isVisible() && !w.isDisabled() && !w.isSelected()) {
				checked = false;
				break;
			} else
				checked = true;
		
		if (checked) {
			var header = zk.Widget.$(box._headercm),
				zcls = header.getZclass();
			jq(box._headercm).addClass(zcls + '-img-seld');
		}
	},
	_checkClick: function (evt) {
		if (this.getMeshWidget().isMultiple())
			this._checkAll();
	},
	// event
	doSelect_: function(evt) {
		if (this.isDisabled()) return;
		// make sure the target is the ItemWidget
		evt.target = this;
		if (!evt.itemSelected) {
			this.getMeshWidget()._doSelect(evt);
			evt.itemSelected = true;
		}
		this.$supers('doSelect_', arguments);
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
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		var mate = this.getMeshWidget();
		zk(mate.$n()).enableSelection();
		mate._doKeyUp(evt);
		this.$supers('doKeyUp_', arguments);
	}
});