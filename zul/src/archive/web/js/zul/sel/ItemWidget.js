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
	_selectable: true,
	$define: {
		/** @deprecated As of release 8.0.0, please use {@link #isSelectable()}
		 * @return boolean
		 */
		/** @deprecated As of release 8.0.0, please use {@link #setSelectable(boolean)}
		 * @param boolean checkable
		 */
		checkable: function (checkable) {
			this.setSelectable(checkable);
		},
		/** Returns whether it is selectable.
		 * <p>Default: true.
		 * @return boolean
		 * @since 8.0.0
		 */
		/** Sets whether it is selectable.
		 * <p>Default: true.
		 * @param boolean selectable
		 * @since 8.0.0
		 */
		selectable: function () {
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

		// do this before _updHeaderCM(), otherwise, it will call too many times to sync the state.
		this._selected = selected;

		if (n) {
			jq(n)[selected ? 'addClass' : 'removeClass'](this.$s('selected'));
			// B70-ZK-2050: Replace icon with image in IE8.
            //zk(n).redoCSS(-1, {'fixFontIcon': true});
			this._updHeaderCM();
		}
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
				scls += (scls ? ' ' : '') + this.$s('disabled');
			//Bug ZK-1998: only apply selected style if groupSelect is true
			if (_isListgroup(this) || _isListgroupfoot(this)) {
				if (this.getMeshWidget().groupSelect && this.isSelected())
					scls += (scls ? ' ' : '') + this.$s('selected');
			} else {
				if (this.isSelected())
					scls += (scls ? ' ' : '') + this.$s('selected');
			}
		}
		return scls;
	},
	focus_: function (timeout) {
		var mesh = this.getMeshWidget();
		this._doFocusIn();
		mesh._syncFocus(this);
		mesh.focusA_(mesh.$n('a'), timeout);
		return true;
	},
	_doFocusIn: function () {
		var n = this.$n(),
			mesh = this.getMeshWidget();
		if (n) {
			var cls = this.$s('focus'),
				last = mesh ? mesh._focusItem : null,
				lastn;
			// ZK-3077: focus out the last focused item first (for draggable issue)
			if (last && (lastn = last.$n()))
				jq(lastn).removeClass(cls);
			// Bugfix: add focus class on itself, not on its children elements
			jq(n).addClass(cls);
		}

		if (mesh)
			mesh._focusItem = this;
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

 			// only update for user's selection or sharable model case (ZK-2969 test case)
			if (!this.isSelected() && (box.$$selectAll == undefined || this._userSelection))
				$headercm.removeClass(zcls);
			else if (!$headercm.hasClass(zcls))
				box._updHeaderCM(); //update in batch since we have to examine one-by-one
		}
	},
	getDragMessage_: function () {
		var iterator = this.getMeshWidget().itemIterator();
		var cnt = 2;
		var msg;
		if (!this.isSelected())	return this.getLabel();
		while(iterator.hasNext()) {
			var item = iterator.next();
			if(item.isSelected()) {
				var label = item.getLabel();
				if (label.length > 9)
					label = label.substring(0, 9) + '...';
				if (!msg)
					msg = label;
				else
					msg += '</div><div class="z-drop-content"><span id="zk_ddghost-img'
						+ (cnt++) + '" class="z-drop-icon"></span>&nbsp;'
						+ label;
			}
		}
		return msg;
	},
	// override it because msg cut in getDragMessage_,
	// do not want cut again here, and change _dragImg to array
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244
		var msg = this.getDragMessage_();
		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = 'pointer';
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		// has multi drag image
		drag._dragImg = jq('span[id^="zk_ddghost-img"]');
		return dgelm;
	},
	//@Override
	beforeParentChanged_: function (newp) {
		if (!newp) {//remove
			var mesh = this.getMeshWidget();
			if (mesh) mesh._shallSyncCM = true;
		}
		this.$supers('beforeParentChanged_', arguments);
	},
	//@Override
	afterParentChanged_: function () {
		if (this.parent) {//add
			var mesh = this.getMeshWidget();
			if (mesh) mesh._shallSyncCM = true;
		}
		this.$supers('afterParentChanged_', arguments);
	},

	// event
	doSelect_: function (evt) {
		if (this.isDisabled() || !this.isSelectable()) return;
		try {
			this._userSelection = true;
			if (!evt.itemSelected) {
				this.getMeshWidget()._doItemSelect(this, evt);
				evt.itemSelected = true;
			}
			this.$supers('doSelect_', arguments);
		} finally {
			this._userSelection = null;
		}
	},
	doKeyDown_: function (evt) {
		var mesh = this.getMeshWidget();

		// disable item's content selection excluding input box and textarea
		if (!jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			this._disableSelection_ = true;
			zk(mesh.$n()).disableSelection();
		}
		mesh._doKeyDown(evt);
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		var mesh = this.getMeshWidget();
		if (this._disableSelection_) {
			zk(mesh.$n()).enableSelection();
			this._disableSelection_ = false;
		}
		mesh._doKeyUp(evt);
		this.$supers('doKeyUp_', arguments);
	},
	deferRedrawHTML_: function (out) {
		out.push('<tr', this.domAttrs_({domClass: 1}), ' class="z-renderdefer"></tr>');
	}
});
})();