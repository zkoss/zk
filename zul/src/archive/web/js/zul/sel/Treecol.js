/* Treecol.js

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _updCells(tch, jcol) {
		if (tch)
			for (var w = tch.firstChild, tr; w; w = w.nextSibling) {
				if ((tr = w.treerow) && jcol < tr.nChildren)
					tr.getChildAt(jcol).rerender();

				_updCells(w.treechildren, jcol); //recursive
			}
	}

	function _sort0(treechildren, col, dir, sorting, isNumber) {
		var d = [];
		for (var i = 0, z = 0, w = treechildren.firstChild; w; w = w.nextSibling, z++) {
			if (w.treechildren)
				_sort0(w.treechildren, col, dir, sorting, isNumber);
			for (var k = 0, cell = w.getFirstCell(); cell; cell = cell.nextSibling, k++)
				if (k == col) {
					d[i++] = {
						wgt: cell,
						index: z
					};
				}
		}
		var dsc = dir == 'ascending' ? -1 : 1;
		d.sort(function (a, b) {
			var v = sorting(a.wgt, b.wgt, isNumber) * dsc;
			if (v == 0) {
				v = (a.index < b.index ? -1 : 1);
			}
			return v;
		});
		for (var i = 0, k = d.length; i < k; i++) {
			treechildren.appendChild(d[i].wgt.parent.parent);
		}
	}

/**
 * A treecol.
 * <p>Default {@link #getZclass}: z-treecol
 */
zul.sel.Treecol = zk.$extends(zul.mesh.SortWidget, {
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent ? this.parent.parent : null;
	},
	/** Returns the mesh body that this belongs to.
	 * @since 5.0.6
	 * @return Tree
	 */
	getMeshBody: function () {
		var tree = this.getTree();
		return tree ? tree.treechildren : null;
	},
	checkClientSort_: function (ascending) {
		var tree;
		return !(!this.getMeshBody() || !(tree = this.getTree()) || ('paging' == tree._mold))
				&& this.$supers('checkClientSort_', arguments);
	},
	replaceCavedChildrenInOrder_: function (ascending) {
		var mesh = this.getMeshWidget(),
			body = this.getMeshBody(),
			desktop = body.desktop;
		try {
			body.unbind();
			_sort0(body, this.getChildIndex(), this.getSortDirection(), this.sorting,
				(this[ascending ? '_sortAscending' : '_sortDescending'] == 'client(number)'));
			this._fixDirection(ascending);
		} finally {
			var old = mesh._syncingbodyrows;
			mesh._syncingbodyrows = true;
			try {
				mesh.clearCache();
				jq(mesh.$n('rows')).replaceWith(body.redrawHTML_());
				body.bind(desktop);
				mesh._bindDomNode();
			} finally {
				mesh._syncingbodyrows = old;
			}
		}
	},
	$define: {
		/** Returns the maximal length of each item's label.
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
	updateCells_: function () {
		var tree = this.getTree();
		if (tree) {
			var jcol = this.getChildIndex(),
				tf = tree.treefoot;

			_updCells(tree.treechildren, jcol);

			if (tf && jcol < tf.nChildren)
				tf.getChildAt(jcol).rerender();
		}
	},
	bind_: function () {
		this.$supers(zul.sel.Treecol, 'bind_', arguments);
		var n, cm;
		if (n = this.$n())
			this.domListen_(n, 'onMouseOver', '_doSortMouseEvt')
				.domListen_(n, 'onMouseOut', '_doSortMouseEvt');
		if (cm = this.$n('cm')) {
			var tree = this.getTree();
			if (tree) tree._headercm = cm;
			this.domListen_(cm, 'onClick', '_doClick');
		}
	},
	unbind_: function () {
		var n, cm;
		if (n = this.$n())
			this.domUnlisten_(n, 'onMouseOver', '_doSortMouseEvt')
				.domUnlisten_(n, 'onMouseOut', '_doSortMouseEvt');
		if (cm = this.$n('cm')) {
			var tree = this.getTree();
			if (tree) tree._headercm = null;
			this._checked = null;
			this.domUnlisten_(cm, 'onClick', '_doClick');
		}
		this.$supers(zul.sel.Treecol, 'unbind_', arguments);
	},
	_doSortMouseEvt: function (evt) {
		var sort = this.getSortAscending();
		if (sort != 'none')
			jq(this.$n())[evt.name == 'onMouseOver' ? 'addClass' : 'removeClass'](this.getZclass() + '-sort-over');
	},
	//@Override
	domContent_: function () {
		var s = this.$supers('domContent_', arguments),
			tree = this.getTree();
		if (this._hasCheckbox())
			s = '<span id="' + this.uuid + '-cm" class="' + this.$s('checkable')
				+ (tree.$$selectAll ? ' ' + this.$s('checked') : '') + '"><i class="' + this.$s('icon') + ' z-icon-check"></i></span>'
				+ (s ? '&nbsp;' + s : '');
		return s;
	},
	_hasCheckbox: function () {
		var tree = this.getTree();
		return tree != null && this.parent.firstChild == this
			&& tree._checkmark && tree._multiple && !tree._tree$noSelectAll;
	},
	//@Override
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this._maxlength});
	},
	_doClick: function (evt) {
		this._checked = !this._checked;
		var tree = this.getTree(),
			cm = this.$n('cm'),
			$n = jq(cm);
		if (this._checked) {
			$n.addClass(this.$s('checked'));
			tree.selectAll(true, evt);
		} else {
			$n.removeClass(this.$s('checked'));
			tree._select(null, evt);
		}
		tree.fire('onCheckSelectAll', this._checked, {toServer: true});

	},
	doClick_: function (evt) {
		var tree = this.getTree(),
			cm = this.$n('cm');
		if (tree && tree._checkmark) {
			var n = evt.domTarget;
			if (n == cm || n.parentNode == cm) {
				return;
			}
			this.$supers('doClick_', arguments);
		}

	},
});

})();