/* Listheader.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The list header which defines the attributes and header of a column
 * of a list box.
 * Its parent must be {@link Listhead}.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-listheader.
 */
zul.sel.Listheader = zk.$extends(zul.mesh.SortWidget, {
	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	getListbox: _zkf = function () {
		return this.parent ? this.parent.parent : null;
	},

	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onGroup: this}, -1000);
	},
	/** Returns the mesh body that this belongs to.
	 * @return Listbox
	 */
	getMeshBody: _zkf,
	checkClientSort_: function (ascending) {
		var body;
		return !(!(body = this.getMeshBody()) || body.hasGroup()) && 
			this.$supers('checkClientSort_', arguments);
	},
	$define: {
		/** Returns the maximal length of each item's label.
		 * Default: 0 (no limit).
		 * @return int
		 */
		/** Sets the maximal length of each item's label.
		 * @param int maxlength
		 */
		maxlength: [function (v) {
			return !v || v < 0 ? 0 : v; 
		}, function () {
			if (this.desktop) {
				this.rerender();
				this.updateCells_();
			}
		}]
	},
	/** Groups and sorts the items ({@link Listitem}) based on
	 * {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * 
	 * @param boolean ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param zk.Event evt the event causes the group
	 * @return boolean whether the items are grouped.
	 * @since 6.5.0
	 */
	group: function (ascending, evt) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ('ascending' == dir) return false;
		} else {
			if ('descending' == dir) return false;
		}

		var sorter = ascending ? this._sortAscending: this._sortDescending;
		if (sorter == 'fromServer')
			return false;
		else if (sorter == 'none') {
			evt.stop();
			return false;
		}
		
		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel() || !zk.feature.pe || !zk.isLoaded('zkex.sel')) return false;
			// if in model, the sort should be done by server
		
		var	body = this.getMeshBody();
		if (!body) return false;
		evt.stop();
		
		var desktop = body.desktop,
			node = body.$n();
		try {
			body.unbind();
			if (body.hasGroup()) {
				for (var gs = body.getGroups(), len = gs.length; --len >= 0;) 
					body.removeChild(gs[len]);
			}
			
			var d = [], col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w; (w = it.next()); z++) 
				for (var k = 0, cell = w.firstChild; cell; cell = cell.nextSibling, k++) 
					if (k == col) {
						d[i++] = {
							wgt: cell,
							index: z
						};
					}
			
			var dsc = dir == 'ascending' ? -1 : 1,
				fn = this.sorting,
				isNumber = sorter == 'client(number)';
			d.sort(function(a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});
			
			// clear all items
			for (var item = body.firstItem; item; item = body.nextItem(item))
				body.removeChild(item);
			
			for (var previous, row, index = this.getChildIndex(), i = 0, k = d.length; i < k; i++) {
				row = d[i];
				if (!previous || fn(previous.wgt, row.wgt, isNumber) != 0) {
					//new group
					var group, cell = row.wgt.parent.getChildAt(index);
					if (cell) {
						if (cell.getLabel()) {
							group = new zkex.sel.Listgroup({
								label: cell.getLabel()
							});
						} else {
							var cc = cell.firstChild;
							if (cc && cc.$instanceof(zul.wgt.Label)) {
								group = new zkex.sel.Listgroup({
									label: cc.getValue()
								});
							} else {
								group = new zkex.sel.Listgroup({
									label: msgzul.GRID_OTHER
								});
							}
						}
					}
					body.appendChild(group);
				}
				body.appendChild(row.wgt.parent);
				previous = row;
			}
			this._fixDirection(ascending);
		} finally {
			body.replaceHTML(node, desktop);
		}
		return true;
	},
	/** It invokes {@link #group} to group list items and maintain
	 * {@link #getSortDirection}.
	 * @since 6.5.0
	 */
	onGroup: function (evt) {
		var dir = this.getSortDirection();
		if ('ascending' == dir)
			this.group(false, evt);
		else if ('descending' == dir)
			this.group(true, evt);
		else if (!this.group(true, evt))
			this.group(false, evt);
	},
	/**
	 * Updates the cells according to the listheader
	 */
	updateCells_: function () {
		var box = this.getListbox();
		if (box == null || box.getMold() == 'select')
			return;

		var jcol = this.getChildIndex(), w;
		for (var it = box.getBodyWidgetIterator(); (w = it.next());)
			if (jcol < w.nChildren)
				w.getChildAt(jcol).rerender();

		w = box.listfoot;
		if (w && jcol < w.nChildren)
			w.getChildAt(jcol).rerender();
	},
	//super//
	bind_: function () {
		this.$supers(zul.sel.Listheader, 'bind_', arguments);
		var cm = this.$n('cm'),
			n = this.$n();
		if (cm) {
			var box = this.getListbox();
			if (box) box._headercm = cm;
			this.domListen_(cm, 'onClick', '_doClick');
		}
		if (n)
			this.domListen_(n, 'onMouseOver', '_doMouseOver')
				.domListen_(n, 'onMouseOut', '_doMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domListen_(btn, 'onClick', '_doMenuClick');
	},
	unbind_: function () {
		var cm = this.$n('cm'),
			n = this.$n();
		if (cm) {
			var box = this.getListbox();
			if (box) box._headercm = null;
			this._checked = null;
			this.domUnlisten_(cm, 'onClick', '_doClick');
		}
		if (n)
			this.domUnlisten_(n, 'onMouseOver', '_doMouseOver')
				.domUnlisten_(n, 'onMouseOut', '_doMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domUnlisten_(btn, 'onClick', '_doMenuClick');
		this.$supers(zul.sel.Listheader, 'unbind_', arguments);
	},
	_doMouseOver: function (evt) {
		if (this.isSortable_() ||
				(this.parent._menupopup && this.parent._menupopup != 'none'))
			jq(this.$n()).addClass(this.$s('hover'));
	},
	_doMouseOut: function (evt) {
		if (this.isSortable_() ||
				(this.parent._menupopup && this.parent._menupopup != 'none')) {
			var $n = jq(this.$n());
			if (!$n.hasClass(this.$s('visited')))
				$n.removeClass(this.$s('hover'));
		}
	},
	_doClick: function (evt) {
		this._checked = !this._checked;
		var box = this.getListbox(),
			cm = this.$n('cm'),
			$n = jq(cm);
		if (this._checked) {
			$n.addClass(this.$s('checked'));
			zk(cm).redoCSS(-1, {'fixFontIcon': true});
			box.selectAll(true, evt);
		} else {
			$n.removeClass(this.$s('checked'));
			box._select(null, evt);
		}
	},
	//@Override
	doClick_: function (evt) {
		var box = this.getListbox();
		if (box && box._checkmark) {
			var n = evt.domTarget;
			if (n.id && n.id.endsWith('-cm'))
				return; //ignore it (to avoid sort or other activity)
		}
		this.$supers('doClick_', arguments);
	},
	//@Override
	domContent_: function () {
		var s = this.$supers('domContent_', arguments),
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this 
				&& box._checkmark && box._multiple && !box._listbox$noSelectAll) // B50-ZK-873
			s = '<span id="' + this.uuid + '-cm" class="' + this.$s('checkable') + 
				'"><i class="' + this.$s('icon') + ' z-icon-ok"></i></span>'
				+ (s ? '&nbsp;' + s:'');
		return s;
	},
	//@Override
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this._maxlength});
	}
});
