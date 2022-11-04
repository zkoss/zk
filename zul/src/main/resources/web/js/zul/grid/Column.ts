/* Column.ts

	Purpose:

	Description:

	History:
		Wed Dec 24 15:25:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A single column in a {@link Columns} element.
 * Each child of the {@link Column} element is placed in each successive
 * cell of the grid.
 * The column with the most child elements determines the number of rows
 * in each column.
 *
 * <p>The use of column is mainly to define attributes for each cell
 * in the grid.
 *
 * @defaultValue {@link getZclass}: z-column.
 */
@zk.WrapClass('zul.grid.Column')
export class Column extends zul.mesh.SortWidget {
	override parent!: zul.grid.Columns | undefined;

	/**
	 * @returns the grid that contains this column.
	 */
	getGrid(): zul.grid.Grid | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	constructor() {
		super(); // FIXME: params?
		this.listen({onGroup: this}, -1000);
	}

	/**
	 * @returns the rows of the grid that contains this column.
	 */
	getMeshBody(): zul.grid.Rows | undefined {
		var grid = this.getGrid();
		return grid ? grid.rows : undefined;
	}

	/** @internal */
	override checkClientSort_(ascending: boolean): boolean {
		const body = this.getMeshBody();
		return !(!body || body.hasGroup()) && super.checkClientSort_(ascending);
	}

	/**
	 * Groups and sorts the rows ({@link Row}) based on
	 * {@link getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 *
	 * @param ascending - whether to use {@link getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param evt - the event causes the group
	 * @returns boolean whether the rows are grouped.
	 */
	group(ascending: boolean, evt: zk.Event): boolean {
		var dir = this.getSortDirection();
		if (ascending) {
			if ('ascending' == dir) return false;
		} else {
			if ('descending' == dir) return false;
		}

		var sorter = ascending ? this._sortAscending : this._sortDescending;
		if (sorter == 'fromServer')
			return false;
		else if (sorter == 'none') {
			evt.stop();
			return false;
		}

		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel() || !zk.feature.pe || !zk.isLoaded('zkex.grid'))
			return false;
			// if in model, the sort should be done by server

		var	body = this.getMeshBody();
		if (!body) return false;
		evt.stop();

		var desktop = body.desktop,
			node = body.$n_();
		try {
			body.unbind();
			if (body.hasGroup()) {
				for (var gs = body.getGroups(), len = gs.length; --len >= 0;)
					body.removeChild(gs[len]);
			}

			interface Data {
				wgt: zk.Widget;
				index: number;
			}
			var d: Data[] = [],
				col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w: zul.mesh.Item | undefined; (w = it.next()); z++)
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
			d.sort(function (a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});

			// clear all
			for (;body.firstChild;)
				body.removeChild(body.firstChild);

			for (var previous: Data | undefined, index = this.getChildIndex(), i = 0, k = d.length; i < k; i++) {
				const row = d[i];
				if (!previous || fn(previous.wgt, row.wgt, isNumber) != 0) {
					//new group
					let group: zkex.grid.Group,
						cell = row.wgt.parent!.getChildAt(index);
					if (cell && cell instanceof zul.wgt.Label) {
						group = new zkex.grid.Group();
						group.appendChild(new zul.wgt.Label({
							value: cell.getValue()
						}));
					} else {
						var cc = cell!.firstChild;
						if (cc && cc instanceof zul.wgt.Label) {
							group = new zkex.grid.Group();
							group.appendChild(new zul.wgt.Label({
								value: cc.getValue()
							}));
						} else {
							group = new zkex.grid.Group();
							group.appendChild(new zul.wgt.Label({
								value: msgzul.GRID_OTHER
							}));
						}
					}
					body.appendChild(group);
				}
				body.appendChild(row.wgt.parent!);
				previous = row;
			}
			this._fixDirection(ascending);
		} finally {
			body.replaceHTML(node, desktop);
		}
		return true;
	}

	override setLabel(label: string, opts?: Record<string, boolean>): this {
		super.setLabel(label, opts);
		if (this.parent)
			this.parent._syncColMenu();
		return this;
	}

	override setVisible(visible: boolean): this {
		if (this.isVisible() != visible) {
			super.setVisible(visible);
			if (this.parent)
				this.parent._syncColMenu();
		}
		return this;
	}

	/**
	 * It invokes {@link group} to group list items and maintain {@link getSortDirection}.
	 */
	onGroup(evt: zk.Event): void {
		var dir = this.getSortDirection();
		if ('ascending' == dir)
			this.group(false, evt);
		else if ('descending' == dir)
			this.group(true, evt);
		else if (!this.group(true, evt))
			this.group(false, evt);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n = this.$n_();
		this.domListen_(n, 'onMouseOver')
			.domListen_(n, 'onMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domListen_(btn, 'onClick', '_doMenuClick');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n_();
		this.domUnlisten_(n, 'onMouseOver')
			.domUnlisten_(n, 'onMouseOut');
		var btn = this.$n('btn');
		if (btn)
			this.domUnlisten_(btn, 'onClick', '_doMenuClick');
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_doMouseOver(evt: zk.Event): void {
		if (this.isSortable_()
				|| (this.parent!._menupopup && this.parent!._menupopup != 'none'))
			jq(this.$n_()).addClass(this.$s('hover'));
	}

	/** @internal */
	_doMouseOut(evt: zk.Event): void {
		if (this.isSortable_()
				|| (this.parent!._menupopup && this.parent!._menupopup != 'none')) {
			var $n = jq(this.$n_());
			if (!$n.hasClass(this.$s('visited')))
				$n.removeClass(this.$s('hover'));
		}
	}
}