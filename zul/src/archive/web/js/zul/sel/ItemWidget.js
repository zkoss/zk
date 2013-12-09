/* ItemWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri May 22 21:50:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _isListgroup(w) {
		return zk.isLoaded('zkex.sel') && w.$instanceof(zkex.sel.Listgroup);
	}
	function _isListgroupfoot(w) {
		return zk.isLoaded('zkex.sel') && w.$instanceof(zkex.sel.Listgroupfoot);
	}
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
			var box = this.getMeshWidget();
			if (box)
				box.toggleItemSelection(this);
				
			this._setSelectedDirectly(selected);
		}
	},
	_setSelectedDirectly: function (selected) {
		var n = this.$n();
		if (n) {
			jq(n)[selected ? 'addClass' : 'removeClass'](this.$s('selected'));
			// B70-ZK-2050: Replace icon with image in IE8.
            //zk(n).redoCSS(-1, {'fixFontIcon': true});
			this._updHeaderCM();
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
		if (this._visible != visible) { // not to use isVisible()
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
			var zcls = this.getZclass();
			if (this.isDisabled())
				scls += (scls ? ' ': '') + this.$s('disabled');
			//Bug ZK-1998: only apply selected style if groupSelect is true
			if (_isListgroup(this) || _isListgroupfoot(this)) {
				if (this.getMeshWidget().groupSelect && this.isSelected())
					scls += (scls ? ' ': '') + this.$s('selected');
			} else {
				if (this.isSelected())
					scls += (scls ? ' ': '') + this.$s('selected');
			}
		}
		return scls;
	},
	focus_: function (timeout) {
		var mesh = this.getMeshWidget();
			mesh._focusItem = this;
		this._doFocusIn();
		mesh._syncFocus(this);
		mesh.focusA_(mesh.$n('a'), timeout);
		return true;
	},
	_doFocusIn: function () {
		var n = this.$n();
		if (n)
			jq(this._getVisibleChild(n)).addClass(this.$s('focus'));
		
		if (n = this.getMeshWidget())
			n._focusItem = this;			
	},
	_doFocusOut: function () {
		var n = this.$n();
		if (n) {
			var cls = this.$s('focus');
			jq(n).removeClass(cls);
			jq(n.cells).removeClass(cls);
		}
	},
	_updHeaderCM: function (bRemove) { //update header's checkmark
		var box;
		if ((box = this.getMeshWidget()) && box._headercm && box._multiple) {
			if (bRemove) {
				box._updHeaderCM();
				return;
			}

			var zcls = zk.Widget.$(box._headercm).$s('checked'),
				$headercm = jq(box._headercm);

			if (!this.isSelected())
				$headercm.removeClass(zcls);
			else if (!$headercm.hasClass(zcls))
				box._updHeaderCM(); //update in batch since we have to examine one-by-one
		}
	},
	//@Override
	beforeParentChanged_: function (newp) {
		if (!newp) //remove
			this._updHeaderCM(true);
		this.$supers('beforeParentChanged_', arguments);
	},
	//@Override
	afterParentChanged_: function () {
		if (this.parent) //add
			this._updHeaderCM();
		this.$supers('afterParentChanged_', arguments);
	},

	// event
	doSelect_: function(evt) {
		if (this.isDisabled() || !this.isCheckable()) return;
		if (!evt.itemSelected) {
			this.getMeshWidget()._doItemSelect(this, evt);
			evt.itemSelected = true;
		}
		this.$supers('doSelect_', arguments);
	},
	doKeyDown_: function (evt) {
		var mesh = this.getMeshWidget();
		if (!zk.gecko || !jq.nodeName(evt.domTarget, 'input', 'textarea'))
			zk(mesh.$n()).disableSelection();
		mesh._doKeyDown(evt);
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		var mesh = this.getMeshWidget();
		zk(mesh.$n()).enableSelection();
		mesh._doKeyUp(evt);
		this.$supers('doKeyUp_', arguments);
	},
	deferRedrawHTML_: function (out) {
		out.push('<tr', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></tr>');
	}
});
})();