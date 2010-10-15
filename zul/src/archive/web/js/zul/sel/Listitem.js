/* Listitem.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _isPE() {
		return zk.isLoaded('zkex.sel');
	}
/**
 * A list item.
 *
 * <p>Default {@link #getZclass}: z-listitem
 */
zul.sel.Listitem = zk.$extends(zul.sel.ItemWidget, {
	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	getListbox: zul.sel.ItemWidget.prototype.getMeshWidget,
	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @return zkex.sel.Listgroup
	 */
	getListgroup: function () {
		// TODO: this performance is not good.
		if (_isPE() && this.parent && this.parent.hasGroup())
			for (var w = this.previousSibling; w; w = w.previousSibling)
				if (w.$instanceof(zkex.sel.Listgroup)) return w;
				
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
	getZclass: function () {
		return this._zclass == null ? "z-listitem" : this._zclass;
	},
	domStyle_: function (no) {
		if (_isPE() && (this.$instanceof(zkex.sel.Listgroup) || this.$instanceof(zkex.sel.Listgroupfoot))
				|| (no && no.visible))
			return this.$supers('domStyle_', arguments);
			
		var style = this.$supers('domStyle_', arguments),
			group = this.getListgroup();
		return group && !group.isOpen() ? style + "display:none;" : style;
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
	_syncListitems: function (newwgt) {
		var list = this.getListbox();
		if (list) {
			if (list.firstItem.uuid == newwgt.uuid)
				list.firstItem = newwgt;
			if (list.lastItem.uuid == newwgt.uuid)
				list.lastItem = newwgt;
			if (this.isSelected()) {
				var items = list._selItems;
				if (items && items.$remove(this))
					items.push(newwgt);
			}
		}
	}
});
})();