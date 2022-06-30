/* SortWidget.ts

	Purpose:

	Description:

	History:
		Tue May 26 14:51:17     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export interface SortableWidget extends zk.Widget {
	getLabel?(): string | undefined;
	getValue?(): unknown;
}

/**
 * A skeletal implementation for a sortable widget.
 */
export abstract class SortWidget extends zul.mesh.HeaderWidget {
	public override parent!: zul.mesh.ColumnMenuWidget | null;
	protected override _sortDirection: zul.mesh.SortDirection = 'natural';
	protected _sortAscending = 'none';
	protected _sortDescending = 'none';
	
	public abstract getMeshBody(): zk.Widget | null;

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 * @return String
	 */
	public getSortDirection(): zul.mesh.SortDirection {
		return this._sortDirection;
	}

	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the widget is sorted.
	 *
	 * <p>If you use {@link #sort(String, jq.Event)} to sort rows,
	 * the sort direction is maintained automatically.
	 * If you want to sort it in customized way, you have to set the
	 * sort direction manually.
	 *
	 * @param String sortDir one of "ascending", "descending" and "natural"
	 */
	public setSortDirection(v: zul.mesh.SortDirection, opts?: Record<string, boolean>): this {
		const o = this._sortDirection;
		this._sortDirection = v;

		if (o !== v || (opts && opts.force)) {
			if (this.desktop) {
				var $n = jq(this.$n_('sort-icon'));
				$n.removeClass();
				switch (v) {
				case 'ascending':
					$n.addClass('z-icon-caret-up');
					break;
				case 'descending':
					$n.addClass('z-icon-caret-down');
				}
			}
		}

		return this;
	}

	/** Returns the ascending sorter, or null if not available.
	 * @return String
	 */
	public getSortAscending(): string {
		return this._sortAscending;
	}

	/** Sets the ascending sorter with "client", "auto", or null for
	 * no sorter for the ascending order.
	 * @param String sortAscending
	 */
	public setSortAscending(v: string, opts?: Record<string, boolean>): this {
		const o = this._sortAscending;
		this._sortAscending = v;

		if (o !== v || (opts && opts.force)) {
			if (!v)
				this._sortAscending = v = 'none';

			if (this.desktop) {
				this.setSortDirection('natural');
				jq(this.$n_()).toggleClass(this.$s('sort'), this.isSortable_());
			}
		}

		return this;
	}

	/** Returns the descending sorter, or null if not available.
	 * @return String
	 */
	public getSortDescending(): string {
		return this._sortDescending;
	}

	/** Sets the descending sorter with "client", "auto", or null for
	 * no sorter for the descending order.
	 * @param String sortDescending
	 */
	public setSortDescending(v: string, opts?: Record<string, boolean>): this {
		const o = this._sortDescending;
		this._sortDescending = v;

		if (o !== v || (opts && opts.force)) {
			if (!v)
				this._sortDescending = v = 'none';

			if (this.desktop) {
				this.setSortDirection('natural');
				jq(this.$n_()).toggleClass(this.$s('sort'), this.isSortable_());
			}
		}

		return this;
	}

	public constructor() {
		super(); // FIXME: params to be decided
		this.listen({onSort: this}, -1000);
	}

	/** Sets the type of the sorter.
	 * You might specify either "auto", "client", or "none".
	 *
	 * <p>If "client" or "client(number)" is specified,
	 * the sort functionality will be done by Javascript at client without notifying
	 * to server, that is, the order of the component in the row is out of sync.
	 * <ul>
	 * <li> "client" : it is treated by a string</li>
	 * <li> "client(number)" : it is treated by a number</li>
	 * </ul>
	 * <p>Note: client sorting cannot work in model case.
	 * @param String type
	 */
	public setSort(type: string): void {
		if (type && type.startsWith('client')) {
			this.setSortAscending(type);
			this.setSortDescending(type);
		} else {
			this.setSortAscending('none');
			this.setSortDescending('none');
		}
	}

	protected override isSortable_(): boolean {
		return this._sortAscending != 'none' || this._sortDescending != 'none';
	}

	/**
	 * Sorts the data.
	 * @param String ascending
	 * @param jq.Event evt
	 * @return boolean
	 * @disable(zkgwt)
	 */
	public sort(ascending: boolean, evt: JQuery.Event): boolean {
		if (!this.checkClientSort_(ascending))
			return false;

		evt.stop();

		this.replaceCavedChildrenInOrder_(ascending);

		return true;
	}

	/** Check the status whether can be sort in client side.
	 * @param String ascending
	 * @return boolean
	 * @since 5.0.6
	 * @see #sort
	 */
	protected checkClientSort_(ascending: boolean): boolean {
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
			return false;
		}

		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel()) return false;
			// if in model, the sort should be done by server

		return true;
	}

	/** Replaced the child widgets with the specified order.
	 * @param String ascending
	 * @since 5.0.6
	 * @see #sort
	 */
	protected replaceCavedChildrenInOrder_(ascending: boolean): void {
		var mesh = this.getMeshWidget()!,
			body = this.getMeshBody()!,
			dir = this.getSortDirection(),
			sorter = ascending ? this._sortAscending : this._sortDescending,
			desktop = body.desktop,
			node = body.$n_();
		try {
			body.unbind();
			interface Data {
				wgt: zk.Widget;
				index: number;
			}
			var d: Data[] = [], col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w: zk.Widget | null | undefined; (w = it.next()); z++)
				for (var k = 0, cell = w.firstChild; cell; cell = cell.nextSibling, k++)
					if (k == col) {
						d[i++] = {
							wgt: cell,
							index: z
						};
					}

			var dsc = dir == 'ascending' ? -1 : 1, fn = this.sorting, isNumber = sorter == 'client(number)';
			d.sort(function (a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});
			for (var i = 0, k = d.length; i < k; i++) {
				body.appendChild(d[i].wgt.parent!);
			}
			this._fixDirection(ascending);

		} finally {
			body.replaceHTML(node, desktop);
		}
	}

	/**
	 * The default implementation to compare the data.
	 * @param Object o1 the first object to be compared.
	 * @param Object o2 the second object to be compared.
	 * @param boolean isNumber
	 * @return int
	 */
	public sorting(a: zul.mesh.SortableWidget, b: zul.mesh.SortableWidget, isNumber: boolean): number {
		var v1: never, v2: never;
		if (typeof a.getLabel == 'function')
			v1 = a.getLabel() as never;
		else if (typeof a.getValue == 'function')
			v1 = a.getValue() as never;
		else v1 = a as never;

		if (typeof b.getLabel == 'function')
			v2 = b.getLabel() as never;
		else if (typeof b.getValue == 'function')
			v2 = b.getValue() as never;
		else v2 = b as never;

		if (isNumber) return v1 - v2;
		return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
	}

	protected _fixDirection(ascending: boolean): void {
		//maintain
		var direction: zul.mesh.SortDirection = ascending ? 'ascending' : 'descending';
		for (var w = this.parent!.firstChild; w; w = w.nextSibling)
			w.setSortDirection(w == this ? direction : 'natural');
	}

	public onSort(evt: JQuery.Event): void {
		var dir = this.getSortDirection();
		if ('ascending' == dir)
			this.sort(false, evt);
		else if ('descending' == dir)
			this.sort(true, evt);
		else if (!this.sort(true, evt))
			this.sort(false, evt);
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this._sortAscending != 'none' || this._sortDescending != 'none') {
			var $n = jq(this.$n_()),
				$sortIcon = jq(this.$n_('sort-icon'));
			$n.addClass(this.$s('sort'));
			switch (this._sortDirection) {
			case 'ascending':
				$sortIcon.addClass('z-icon-caret-up');
				break;
			case 'descending':
				$sortIcon.addClass('z-icon-caret-down');
				break;
			default: // "natural"
				break;
			}
		}
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		super.unbind_(skipper, after, keepRod);
	}

	protected getColumnMenuPopup_ = zk.$void;

	public _doMenuClick(evt: JQuery.Event): void {
		if (this.parent!._menupopup && this.parent!._menupopup != 'none') {
			var pp: string | zul.menu.Menupopup = this.parent!._menupopup,
				btn = this.$n_('btn') as HTMLAnchorElement;

			//for not removing hover effect when moving mouse on menupopup
			jq(this.$n_()).addClass(this.$s('visited'));

			if ((pp == 'auto' || pp == 'auto-keep') && this.parent!._mpop)
				pp = this.parent!._mpop;
			else
				pp = this.$f(this.parent!._menupopup) as zul.menu.Menupopup;

			if (zul.menu.Menupopup.isInstance(pp)) {
				var ofs = zk(btn).revisedOffset(),
					asc = this.getSortAscending() != 'none',
					desc = this.getSortDescending() != 'none',
					mw = this.getMeshWidget()!;
				if (pp instanceof zul.mesh.ColumnMenupopup) {
					pp.getAscitem()!.setVisible(asc);
					pp.getDescitem()!.setVisible(desc);
					var model = mw.getModel();
					if (zk.feature.pe && pp.getGroupitem()) {
						if (model as unknown == 'group' || !model || this.isListen('onGroup', {asapOnly: true}))
							pp.getGroupitem()!.setVisible((asc || desc));
						else
							pp.getGroupitem()!.setVisible(false);
					}
					if (zk.feature.ee && pp.getUngroupitem()) {
						var visible = !model || this.isListen('onUngroup', {asapOnly: true});
						pp.getUngroupitem()!.setVisible(visible && mw.hasGroup());
					}

					var sep = pp.getDescitem()!.nextSibling;
					if (sep)
						sep.setVisible((asc || desc));
				} else {
					pp.listen({onOpen: [this.parent, this.parent!._onMenuPopup]});
				}
				pp.open(btn, [ofs[0], ofs[1] + btn.offsetHeight - 4], null, {sendOnOpen: true});
			}
			evt.stop(); // avoid onSort event.
		}
	}
}
zul.mesh.SortWidget = zk.regClass(SortWidget);