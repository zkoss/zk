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
	// _dragImg changed to an array, update image node after origional DD_dragging
	function updateImg(drag) {
		var dragImg = drag._dragImg;
		if (dragImg) {
			// update drag image
			var cls = jq(drag.node).hasClass('z-drop-allow')? 'z-drop-allow': 'z-drop-disallow';
			for (var len = 0; len < dragImg.length; len ++)
				dragImg[len].className = cls;
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
	getListbox: zul.sel.ItemWidget.prototype.getMeshWidget,
	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @return zkex.sel.Listgroup
	 */
	getListgroup: function () {
		// TODO: this performance is not good.
		if (_isPE() && this.parent && this.parent.hasGroup())
			for (var w = this; w; w = w.previousSibling)
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
	// override this functino for multiple z-drop-cnt
	getDragMessage_: function () {
		var p = this.parent,
			p_sel = p._selItems,
			length = p_sel.length,
			inst = zul.sel.Listitem,
			msg,
			cnt = 2;
		// if no other listitem selected or self is not selected,
		// return self label
		// else iterate through all listitems 
		// ZK-803
		if (!this.isSelected() || !length || (length == 1 && p_sel[0] == this))
			return this.getLabel();
		for (var w = p.firstChild; w; w = w.nextSibling)
			if (w.$instanceof(inst) && w.isSelected()) {
				var label = w.getLabel();
				if (label.length > 9)
					label = label.substring(0, 9) + "...";
				if (!msg)
					msg = label;
				else
					msg += '</div><div class="z-drop-cnt"><span id="zk_ddghost-img'
						+ (cnt++) + '" class="z-drop-disallow"></span>&nbsp;'
						+ label;
			}
		return msg;
	},
	// replace the origional DD_dragging
	getDragOptions_: function (map) {
		var old = map.change;
		map.change =  function (drag, pt, evt) {
			old(drag, pt, evt);
			// update drag image after origional function
			updateImg(drag);
		};
		return this.$supers('getDragOptions_', arguments);
	},
	// override it because msg cut in getDragMessage_,
	// do not want cut again here, and change _dragImg to array
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244
		var msg = this.getDragMessage_();
		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = "pointer";
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		// has multi drag image
		drag._dragImg = jq('span[id^="zk_ddghost-img"]');
		return dgelm;
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
	_updHeaderCM: function (bRemove) {
		// B50-3322970: need to clear Listheader _check cache
		var box, lh;
		if (!this.isSelected() && (box = this.getListbox()) 
			&& box._headercm && box._multiple && 
				(lh = box.listhead) && (lh = lh.firstChild))
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
	}
});
})();