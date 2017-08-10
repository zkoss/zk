/* Listitem.js

	Purpose:

	Description:

	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _isPE() {
		return zk.isLoaded('zkex.sel');
	}
	// _dragImg changed to an array, update image node after original DD_dragging
	function updateImg(drag) {
		var dragImg = drag._dragImg;
		if (dragImg) {
			// update drag image
			var allow = jq(drag.node).hasClass('z-drop-allow');
			for (var len = 0; len < dragImg.length; len++) {
				if (allow)
					jq(dragImg[len]).removeClass('z-icon-times').addClass('z-icon-check');
				else
					jq(dragImg[len]).removeClass('z-icon-check').addClass('z-icon-times');
			}
		}
	}
/**
 * A listitem.
 *
 * <p>Default {@link #getZclass}: z-listitem
 */
zul.sel.Listitem = zk.$extends(zul.sel.ItemWidget, {
	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	getListbox: function () {
		return this.parent;
	},
	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @return zkex.sel.Listgroup
	 */
	getListgroup: function () {
		// TODO: this performance is not good.
		if (_isPE() && this.parent && this.parent.hasGroup())
			for (var w = this; w; w = w.previousSibling)
				if (w.$instanceof(zkex.sel.Listgroup))
					return w;

		return null;
	},
	/** Sets the label of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String label
	 */
	setLabel: function (val) {
		this._autoFirstCell().setLabel(val);
	},
	// replace the origional DD_dragging
	getDragOptions_: function (map) {
		var old = map.change;
		map.change = function (drag, pt, evt) {
			old(drag, pt, evt);
			// update drag image after origional function
			updateImg(drag);
		};
		return this.$supers('getDragOptions_', arguments);
	},
	/** Sets the image of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String image
	 */
	setImage: function (val) {
		this._autoFirstCell().setImage(val);
	},
	_autoFirstCell: function () {
		if (!this.firstChild)
			this.appendChild(new zul.sel.Listcell());
		return this.firstChild;
	},
	//super//
	domStyle_: function (no) {
		if (_isPE() && (this.$instanceof(zkex.sel.Listgroup) || this.$instanceof(zkex.sel.Listgroupfoot))
				|| (no && no.visible))
			return this.$supers('domStyle_', arguments);

		var style = this.$supers('domStyle_', arguments),
			group = this.getListgroup();
		return group && !group.isOpen() ? style + 'display:none;' : style;
	},
	domClass_: function () {
		var cls = this.$supers('domClass_', arguments),
			list = this.getListbox();
		if (list && jq(this.$n()).hasClass(list = list.getOddRowSclass()))
			return cls + ' ' + list;
		return cls;
	},
	replaceWidget: function (newwgt) {
		this._syncListitems(newwgt);
		this.$supers('replaceWidget', arguments);
	},
	scrollIntoView: function () {
		var bar = this.getListbox()._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n());
		} else {
			this.$supers('scrollIntoView', arguments);
		}
	},
	_updHeaderCM: function (bRemove) {
		// B50-3322970: need to clear Listheader _check cache
		var box, lh;
		if (!this.isSelected() && (box = this.getListbox())
			&& box._headercm && box._multiple && box.$$selectAll != undefined
				&& (lh = box.listhead) && (lh = lh.firstChild))
			lh._checked = false;
		this.$supers('_updHeaderCM', arguments);
	},
	_syncListitems: function (newwgt) {
		var box;
		if (box = this.getListbox()) {
			if (box.firstItem.uuid == newwgt.uuid)
				box.firstItem = newwgt;
			if (box.lastItem.uuid == newwgt.uuid)
				box.lastItem = newwgt;

			var items = box._selItems, b1, b2;
			if (b1 = this.isSelected())
				items.$remove(this);
			if (b2 = newwgt.isSelected())
				items.push(newwgt);
			if (b1 != b2)
				box._updHeaderCM();
		}
	},
	//@Override
    compareRowPos: function (item) {
        // return -1 if item is before this,
        // return  0 if item is the same as this,
        // return  1 if item is after this,
        var thisIndex = this._index, itemIndex = item._index;
        return thisIndex == itemIndex ? 0 : thisIndex > itemIndex ? -1 : 1;
    }
});
})();