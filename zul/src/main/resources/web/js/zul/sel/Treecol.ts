/* Treecol.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _updCells(tch: zul.sel.Treechildren | undefined, jcol: number): void {
	if (tch)
		for (let w = tch.firstChild; w; w = w.nextSibling) {
			const tr = w.treerow;
			if (tr && jcol < tr.nChildren)
				tr.getChildAt(jcol)!.rerender();

			_updCells(w.treechildren, jcol); //recursive
		}
}

type Comparator = (a: zul.mesh.SortableWidget, b: zul.mesh.SortableWidget, isNumber: boolean) => number
function _sort0(treechildren: zul.sel.Treechildren, col: number, dir: zul.mesh.SortDirection, sorting: Comparator, isNumber: boolean): void {
	interface Data {
		wgt: zul.sel.Treecell;
		index: number;
	}
	var d: Data[] = [];
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
		treechildren.appendChild(d[i].wgt.parent!.parent!);
	}
}

/**
 * A treecol.
 * <p>Default {@link #getZclass}: z-treecol
 */
@zk.WrapClass('zul.sel.Treecol')
export class Treecol extends zul.mesh.SortWidget {
	override parent!: zul.sel.Treecols | undefined;
	override nextSibling!: zul.sel.Treecol | undefined;
	override previousSibling!: zul.sel.Treecol | undefined;
	_maxlength?: number;

	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree(): zul.sel.Tree | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	/** Returns the mesh body that this belongs to.
	 * @since 5.0.6
	 * @return Tree
	 */
	getMeshBody(): zul.sel.Treechildren | undefined {
		var tree = this.getTree();
		return tree ? tree.treechildren : undefined;
	}

	override checkClientSort_(ascending: boolean): boolean {
		var tree: zul.sel.Tree | undefined;
		return !(!this.getMeshBody() || !(tree = this.getTree()) || ('paging' == tree._mold))
				&& super.checkClientSort_(ascending);
	}

	override replaceCavedChildrenInOrder_(ascending: boolean): void {
		var mesh = this.getMeshWidget()!,
			body = this.getMeshBody()!,
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
				jq(mesh.$n_('rows')).replaceWith(body.redrawHTML_());
				body.bind(desktop);
				mesh._bindDomNode();
			} finally {
				mesh._syncingbodyrows = old;
			}
		}
	}

	/** Returns the maximal length of each item's label.
	 * @return int
	 */
	getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/** Sets the maximal length of each item's label.
	 * @param int maxlength
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength, v = maxlength;
		this._maxlength = maxlength = !v || v < 0 ? 0 : v;

		if (o !== maxlength || (opts && opts.force)) {
			if (this.desktop) {
				this.rerender();
				this.updateCells_();
			}
		}

		return this;
	}

	updateCells_(): void {
		var tree = this.getTree();
		if (tree) {
			var jcol = this.getChildIndex(),
				tf = tree.treefoot;

			_updCells(tree.treechildren, jcol);

			if (tf && jcol < tf.nChildren)
				tf.getChildAt(jcol)!.rerender();
		}
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		const n = this.$n();
		if (n)
			this.domListen_(n, 'onMouseOver', '_doSortMouseEvt')
				.domListen_(n, 'onMouseOut', '_doSortMouseEvt');
		const cm = this.$n('cm');
		if (cm) {
			var tree = this.getTree();
			if (tree) tree._headercm = cm;
			this.domListen_(cm, 'onClick', '_doCheckmarkClick');
		}
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		const n = this.$n();
		if (n)
			this.domUnlisten_(n, 'onMouseOver', '_doSortMouseEvt')
				.domUnlisten_(n, 'onMouseOut', '_doSortMouseEvt');
		const cm = this.$n('cm');
		if (cm) {
			const tree = this.getTree();
			if (tree) tree._headercm = undefined;
			this._checked = undefined;
			this.domUnlisten_(cm, 'onClick', '_doCheckmarkClick');
		}
		super.unbind_(skipper, after, keepRod);
	}

	_doSortMouseEvt(evt: zk.Event): void {
		var sort = this.getSortAscending();
		if (sort != 'none')
			jq(this.$n_())[evt.name == 'onMouseOver' ? 'addClass' : 'removeClass'](this.getZclass() + '-sort-over');
	}

	//@Override
	override domContent_(): string {
		var s = super.domContent_(),
			tree = this.getTree()!;
		if (this._hasCheckbox())
			s = '<span id="' + this.uuid + '-cm" class="' + this.$s('checkable')
				+ (tree.$$selectAll ? ' ' + this.$s('checked') : '') + '"><i class="' + this.$s('icon') + ' z-icon-check"></i></span>'
				+ (s ? '&nbsp;' + s : '');
		return s;
	}

	_hasCheckbox(): boolean {
		var tree = this.getTree();
		return !!(tree != null && this.parent!.firstChild == this
			&& tree._checkmark && tree._multiple && !tree._tree$noSelectAll);
	}

	//@Override
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this._maxlength});
	}

	_doCheckmarkClick(evt: zk.Event<zk.EventMetaData>): void {
		this._checked = !this._checked;
		var tree = this.getTree()!,
			cm = this.$n_('cm'),
			$n = jq(cm);
		if (this._checked) {
			$n.addClass(this.$s('checked'));
			tree.selectAll(true, evt);
		} else {
			$n.removeClass(this.$s('checked'));
			tree._select(undefined, evt);
		}
		tree.fire('onCheckSelectAll', this._checked, {toServer: true});
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var tree = this.getTree(),
			cm = this.$n('cm');
		if (tree && tree._checkmark) {
			var n = evt.domTarget;
			if (n == cm || n.parentNode == cm) {
				return;
			}
		}
		super.doClick_(evt, popupOnly);
	}
}