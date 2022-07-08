/* Rows.ts

	Purpose:

	Description:

	History:
		Tue Dec 23 15:26:20     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _isPE = (function () {
	var _isPE_ = zk.feature.pe;
	return function () {
			return _isPE_ && zk.isLoaded('zkex.grid');
		};
})();
function _syncFrozen(wgt: zul.grid.Rows): void {
	var grid = wgt.getGrid(),
		frozen: zul.mesh.Frozen | null | undefined;
	if (grid && grid._nativebar && (frozen = grid.frozen))
		frozen._syncFrozen();
}

@zk.WrapClass('zul.grid.Rows')
export class Rows extends zul.Widget<HTMLTableSectionElement> {
	public override parent!: zul.grid.Grid | null;
	public override firstChild!: zul.grid.Row | null;
	public override lastChild!: zul.grid.Row | null;

	private _visibleItemCount = 0;
	private _groupsInfo: zkex.grid.Group[];
	private _shallStripe?: boolean;
	public _musout?: zul.grid.Row;
	private _offset?: number;

	public constructor() {
		super(); // FIXME: params?
		this._groupsInfo = [];
	}

	/** Returns the number of visible descendant {@link Row}.
	 * @return int
	 */
	public getVisibleItemCount(): number {
		return this._visibleItemCount;
	}

	public setVisibleItemCount(v: number): this {
		this._visibleItemCount = v;
		return this;
	}

	/** Returns the grid that contains this rows.
	 * @return zul.grid.Grid
	 */
	public getGrid(): zul.grid.Grid | null {
		return this.parent;
	}

	/** Returns the number of groups.
	 * @return int
	 */
	public getGroupCount(): number {
		return this._groupsInfo.length;
	}

	/** Returns a list of all {@link Group}.
	 * @return Array
	 */
	public getGroups(): zkex.grid.Group[] {
		return this._groupsInfo.$clone();
	}

	/** Returns whether Group exists.
	 * @return boolean
	 */
	public hasGroup(): boolean {
		return this._groupsInfo.length !== 0;
	}

	protected override bind_(desktop: zk.Desktop | null | undefined, skipper: zk.Skipper | null | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var grid = this.getGrid();
		if (grid) // bind ebodyrows for MeshWidget
			grid.ebodyrows = this.$n();
		zWatch.listen({onResponse: this});
		var w = this;
		after.push(function () {
			w.stripe();
			_syncFrozen(w);
		});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	public onResponse(): void {
		if (this.desktop) {
			if (this._shallStripe) { //since bind_(...after)
				this.stripe();
				this.getGrid()!.onSize();
				this.getGrid()!._afterCalcSize();
			}
		}
	}

	protected override replaceChildHTML_(child: zk.Widget, n: HTMLElement | string, desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, _trim_?: boolean): void {
		if (child._renderdefer) {
			var scOdd = this.getGrid()!.getOddRowSclass(),
				isOdd = jq(n).hasClass(scOdd); // supers will change this result, we need to cache it

			super.replaceChildHTML_(child, n, desktop, skipper, _trim_);
			if (isOdd) jq(child).addClass(scOdd);
		} else
			super.replaceChildHTML_(child, n, desktop, skipper, _trim_);
	}

	public _syncStripe(): void {
		this._shallStripe = true;
	}

	/**
	 * Stripes the class for each row.
	 */
	public stripe(): void {
		var grid = this.getGrid()!,
			scOdd = grid.getOddRowSclass();
		if (!scOdd) return;
		var n = this.$n();
		if (!n) return; //Bug #2873478. Rows might not bounded yet

		for (var j = 0, w = this.firstChild, even = !(this._offset! & 1); w; w = w.nextSibling, ++j) {
			if (w.isVisible() && w.isStripeable_()) {
				// check whether is a legal Row or not for zkex.grid.Detail
				for (; n.rows[j] && n.rows[j].id != w.uuid; ++j);

				jq(n.rows[j])[even ? 'removeClass' : 'addClass'](scOdd);
				w.fire('onStripe');
				even = !even;
			}
		}
		this._shallStripe = false;
	}

	protected override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (_isPE() && child instanceof zkex.grid.Group)
			this._groupsInfo.push(child);

		const g = this.getGrid();
		if (g) {
			g._syncEmpty();
		}
		this._syncStripe();

		if (this.desktop)
			_syncFrozen(this);

		if (g && g._cssflex && g.isChildrenFlex())
			g._syncSize();
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (_isPE() && child instanceof zkex.grid.Group)
			this._groupsInfo.$remove(child);
		if (!this.childReplacing_)
			this._syncStripe();

		var g = this.getGrid();
		if (g) g._syncEmpty();
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tbody', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tbody>');
	}
}