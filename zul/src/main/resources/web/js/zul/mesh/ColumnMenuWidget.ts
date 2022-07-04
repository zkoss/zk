/* ColumnMenuWidget.ts

	Purpose:

	Description:

	History:
		Thu, Jun 14, 2012 11:12:58 AM, Created by jumperchen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a column menu widget.
 * @since 6.5.0
 */
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
@zk.WrapClass('zul.mesh.ColumnMenuWidget')
export abstract class ColumnMenuWidget extends zul.mesh.HeadWidget {
	public override _menupopup = 'none';
	private _columnshide = true;
	private _columnsgroup = true;
	public override _mpop?: zul.mesh.ColumnMenupopup | null;
	private _shallColMenu?: boolean | null;
	private _mref?: zul.mesh.HeaderWidget;

	/** Returns whether to enable hiding of the widget with the header context menu.
	 * <p>Default: true.
	 * @return boolean
	 */
	public isColumnshide(): boolean {
		return this._columnshide;
	}

	/** Sets whether to enable hiding of the widget with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto.
	 * @param boolean columnshide
	 */
	public setColumnshide(columnshide: boolean, opts?: Record<string, boolean>): this {
		const o = this._columnshide;
		this._columnshide = columnshide;

		if (o !== columnshide || (opts && opts.force)) {
			if (this.desktop)
				this._initColMenu();
		}

		return this;
	}

	/** Returns whether to enable grouping of the widget with the header context menu.
	 * <p>Default: true.
	 * @return boolean
	 */
	public isColumnsgroup(): boolean {
		return this._columnsgroup;
	}

	/** Sets whether to enable grouping of the widget with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto.
	 * @param boolean columnsgroup
	 */
	public setColumnsgroup(columnsgroup: boolean, opts?: Record<string, boolean>): this {
		const o = this._columnsgroup;
		this._columnsgroup = columnsgroup;

		if (o !== columnsgroup || (opts && opts.force)) {
			if (this.desktop)
				this._initColMenu();
		}

		return this;
	}

	/** Returns the ID of the Menupopup ({@link zul.menu.Menupopup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: none (a default menupoppup).
	 * @return String
	 */
	public getMenupopup(): string {
		return this._menupopup;
	}

	/** Sets the ID of the menupopup ({@link zul.menu.Menupopup}) that should appear
	 * when the user clicks on the element of each column.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in
	 * the same ID space or not.
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 *
	 * @param String mpop an ID of the menupopup component, "none", "auto" or "auto-keep".
	 * 	"none" is assumed by default, "auto" means the menupopup component is
	 *  created automatically, "auto-keep" means the menupopup component is
	 *  created automatically and keep the menupopup open after setting column visibility.
	 * @see #setMenupopup(String)
	 */
	public setMenupopup(mpop: string, opts?: Record<string, boolean>): this {
		const o = this._menupopup;
		this._menupopup = mpop;

		if (o !== mpop || (opts && opts.force)) {
			if (this._menupopup != 'auto' && this._menupopup != 'auto-keep')
				this._mpop = null;
			this.rerender();
		}

		return this;
	}

	protected override bind_(desktop: zk.Desktop | null | undefined, skipper: zk.Skipper | null | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
		var w = this;
		if (this._menupopup == 'auto' || this._menupopup == 'auto-keep') {
			after.push(function () {
				w._initColMenu();
			});
		}
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		if (this._mpop) {
			if (this._menupopup != 'auto-keep')
				this._mpop.parent!.removeChild(this._mpop);
			this._shallColMenu = this._mpop = null;
		}
		super.unbind_(skipper, after, keepRod);
	}

	public onResponse(): void {
		if (this._shallColMenu)
			this.syncColMenu();
	}

	private _syncColMenu(): void {
		this._shallColMenu = true;
	}

	private _initColMenu(): void {
		if (this._mpop)
			this._mpop.parent!.removeChild(this._mpop);
		this._mpop = new zul.mesh.ColumnMenupopup({columns: this});
		if (this._menupopup == 'auto-keep')
			this._mpop._keepOpen = true; //ZK-4059: prevent menupopup closed after menuitem doClick
	}

	/** Synchronizes the menu of this widget.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and change the content of the column's menu.
	 */
	public syncColMenu(): void {
		this._shallColMenu = false;
		if (this._mpop) //it shall do even if !this.desktop
			this._mpop.syncColMenu();
	}

	public _onColVisi(evt: zk.Event): void {
		var item = evt.currentTarget as zul.menu.Menuitem,
			pp = item.parent!;

		if (this._menupopup != 'auto-keep')
			pp.close({sendOnOpen: true});
		var checked = 0;
		for (var w = pp.firstChild; w; w = w.nextSibling) {
			if (w instanceof zul.menu.Menuitem && w.isChecked())
				checked++;
		}
		if (checked == 0)
			item.setChecked(true);

		var col = zk.Widget.$(item._col);
		if (col && col.parent == this) {
			var mesh = this.getMeshWidget();
			if (mesh && mesh.isSizedByContent())
				mesh.clearCachedSize_(); //Bug ZK-1315: clear cached size before column show/hide
			col.setVisible(item.isChecked());
		}
	}

	public _onGroup(evt: zk.Event): void {
		var ungroup: zul.menu.Menuitem | undefined;
		if ((ungroup = (evt.target!.parent as zul.mesh.ColumnMenupopup)._ungroup))
			ungroup.setVisible(true);
		//since 6.5.0 onGroup is not listened anymore, always fire event to server
		this._mref!.fire('onGroup', 'ascending' != this._mref!.getSortDirection(), {toServer: true});
	}

	public _onUngroup = zk.$void;

	public _onAsc(evt: zk.Event): void {
		this._mref!.fire('onSort', true); // B50-ZK-266, always fire
	}

	public _onDesc(evt: zk.Event): void {
		this._mref!.fire('onSort', false); // B50-ZK-266, always fire
	}

	public _onMenuPopup(evt: zk.Event): void {
		var mref = this._mref;
		if (mref)
			jq(mref.$n_()).removeClass(mref.$s('visited')).removeClass(mref.$s('hover'));

		this._mref = (evt.data as {reference: zul.mesh.HeaderWidget}).reference;
	}

	protected override onChildAdded_(child: zul.mesh.HeaderWidget): void {
		super.onChildAdded_(child);
		this._syncColMenu();
		var mesh = this.getMeshWidget();
		if (mesh && mesh._syncEmpty)
			mesh._syncEmpty();
	}

	protected override onChildRemoved_(child: zul.mesh.HeaderWidget): void {
		super.onChildRemoved_(child);
		if (!this.childReplacing_)
			this._syncColMenu();
		var mesh = this.getMeshWidget();
		// FIXME: _syncEmpty was checked for existence in onChildAdded_ but not here
		if (mesh) mesh._syncEmpty!();
	}

	public getGroupPackage_(): '' { // FIXME: orignally, `zk.$void`
		return '';
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var cls = '';
		if (this._menupopup != 'none')
			cls += this.$s('menupopup') + ' ';
		return cls + super.domClass_(no);
	}
}

/**
 * The Columns' Menu popup
 * @since 6.5.0
 */
@zk.WrapClass('zul.mesh.ColumnMenupopup')
export class ColumnMenupopup extends zul.menu.Menupopup {
	private _columns?: zul.mesh.ColumnMenuWidget;
	private _asc?: zul.menu.Menuitem;
	private _desc?: zul.menu.Menuitem;
	private _group?: zul.menu.Menuitem;
	public _ungroup?: zul.menu.Menuitem;

	public getColumns(): zul.mesh.ColumnMenuWidget | undefined {
		return this._columns;
	}

	public setColumns(v: zul.mesh.ColumnMenuWidget): this {
		this._columns = v;
		return this;
	}

	/** Constructor
	 */
	public constructor(opts: {columns: zul.mesh.ColumnMenuWidget}) {
		super(opts);
	}

	public override afterCreated_(opts: {columns: zul.mesh.ColumnMenuWidget}): void {
		super.afterCreated_(opts);
		this._init();
	}

	/** Returns the  menuitem with ascending label
	 * @return zul.menu.Menuitem
	 */
	public getAscitem(): zul.menu.Menuitem | undefined {
		return this._asc;
	}

	/** Returns the  menuitem with descending label
	 * @return zul.menu.Menuitem
	 */
	public getDescitem(): zul.menu.Menuitem | undefined {
		return this._desc;
	}

	/** Returns the  menuitem with group label
	 * @return zul.menu.Menuitem
	 */
	public getGroupitem(): zul.menu.Menuitem | undefined {
		return this._group;
	}

	public getUngroupitem(): zul.menu.Menuitem | null {
		return null;
	}

	private _init(): void {
		var w = this._columns!;

		this.listen({onOpen: [w, w._onMenuPopup]});

		if (zk.feature.pe && w.isColumnsgroup()) {
			if (!zk.isLoaded(w.getGroupPackage_()))
				zk.load(w.getGroupPackage_());
			var group = new zul.menu.Menuitem({
					label: msgzul.GRID_GROUP, visible: false
				});
			group.setSclass(w.$s('menugrouping'));
			group.listen({onClick: [w, w._onGroup]});
			this.appendChild(group);
			this._group = group;
			if (zk.feature.ee) {
				var ungroup = new zul.menu.Menuitem({
						label: msgzul.GRID_UNGROUP, visible: false
					});
				ungroup.setSclass(w.$s('menuungrouping'));
				ungroup.listen({onClick: [w, w._onUngroup]});
				this.appendChild(ungroup);
				this._ungroup = ungroup;
			}
		}
		var asc = new zul.menu.Menuitem({label: msgzul.GRID_ASC});
		asc.setSclass(w.$s('menuascending'));
		asc.listen({onClick: [w, w._onAsc]});
		this._asc = asc;
		this.appendChild(asc);

		var desc = new zul.menu.Menuitem({label: msgzul.GRID_DESC});
		desc.setSclass(w.$s('menudescending'));
		desc.listen({onClick: [w, w._onDesc]});
		this._desc = desc;
		this.appendChild(desc);
		this.syncColMenu();
		w.getPage()!.appendChild(this);
	}

	/** Synchronizes the menu
	 */
	public syncColMenu(): void {
		var w = this._columns;
		for (var c = this.lastChild, p: zul.menu.Menuitem | null; c != this._desc;) {
			p = c!.previousSibling;
			this.removeChild(c!);
			c = p;
		}
		if (w && w.isColumnshide()) {
			var sep = new zul.menu.Menuseparator();
			this.appendChild(sep);
			for (let c = w.firstChild; c; c = c.nextSibling) {
				const item = new zul.menu.Menuitem({
					label: c.getLabel(),
					autocheck: true,
					checkmark: true,
					checked: c.isVisible()
				});
				item._col = c.uuid;
				item.listen({onClick: [w, w._onColVisi]});
				this.appendChild(item);
			}
		}
	}
}