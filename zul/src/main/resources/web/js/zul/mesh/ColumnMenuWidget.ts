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
@zk.WrapClass('zul.mesh.ColumnMenuWidget')
export abstract class ColumnMenuWidget extends zul.mesh.HeadWidget {
	/** @internal */
	override _menupopup = 'none';
	/** @internal */
	_columnshide = true;
	/** @internal */
	_columnsgroup = true;
	/** @internal */
	override _mpop?: zul.mesh.ColumnMenupopup;
	/** @internal */
	_shallColMenu?: boolean;
	/** @internal */
	_mref?: zul.mesh.HeaderWidget;

	/**
	 * @returns whether to enable hiding of the widget with the header context menu.
	 * @defaultValue `true`.
	 */
	isColumnshide(): boolean {
		return this._columnshide;
	}

	/**
	 * Sets whether to enable hiding of the widget with the header context menu.
	 * <p>Note that it is only applied when {@link getMenupopup} is auto.
	 */
	setColumnshide(columnshide: boolean, opts?: Record<string, boolean>): this {
		const o = this._columnshide;
		this._columnshide = columnshide;

		if (o !== columnshide || opts?.force) {
			if (this.desktop)
				this._initColMenu();
		}

		return this;
	}

	/**
	 * @returns whether to enable grouping of the widget with the header context menu.
	 * @defaultValue `true`.
	 */
	isColumnsgroup(): boolean {
		return this._columnsgroup;
	}

	/**
	 * Sets whether to enable grouping of the widget with the header context menu.
	 * <p>Note that it is only applied when {@link getMenupopup} is auto.
	 */
	setColumnsgroup(columnsgroup: boolean, opts?: Record<string, boolean>): this {
		const o = this._columnsgroup;
		this._columnsgroup = columnsgroup;

		if (o !== columnsgroup || opts?.force) {
			if (this.desktop)
				this._initColMenu();
		}

		return this;
	}

	/**
	 * @returns the ID of the Menupopup ({@link zul.menu.Menupopup}) that should appear
	 * when the user clicks on the element.
	 * @defaultValue none (a default menupoppup).
	 */
	getMenupopup(): string {
		return this._menupopup;
	}

	/**
	 * Sets the ID of the menupopup ({@link zul.menu.Menupopup}) that should appear
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
	 * `uuid(comp_uuid)`
	 *
	 * @param mpop - an ID of the menupopup component, "none", "auto" or "auto-keep".
	 * 	"none" is assumed by default, "auto" means the menupopup component is
	 *  created automatically, "auto-keep" means the menupopup component is
	 *  created automatically and keep the menupopup open after setting column visibility.
	 * @see {@link setMenupopup}
	 */
	setMenupopup(menupopup: string, opts?: Record<string, boolean>): this {
		const o = this._menupopup;
		this._menupopup = menupopup;

		if (o !== menupopup || opts?.force) {
			if (this._menupopup != 'auto' && this._menupopup != 'auto-keep')
				this._mpop = undefined;
			this.rerender();
		}

		return this;
	}

	/** @internal */
	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({ onResponse: this });
		var w = this;
		if (this._menupopup == 'auto' || this._menupopup == 'auto-keep') {
			after.push(function () {
				w._initColMenu();
			});
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onResponse: this });
		if (this._mpop) {
			if (this._menupopup != 'auto-keep')
				this._mpop.parent!.removeChild(this._mpop);
			this._shallColMenu = this._mpop = undefined;
		}
		super.unbind_(skipper, after, keepRod);
	}

	onResponse(): void {
		if (this._shallColMenu)
			this.syncColMenu();
	}

	/** @internal */
	_syncColMenu(): void {
		this._shallColMenu = true;
	}

	/** @internal */
	_initColMenu(): void {
		if (this._mpop)
			this._mpop.parent!.removeChild(this._mpop);
		this._mpop = new zul.mesh.ColumnMenupopup({ columns: this });
		if (this._menupopup == 'auto-keep')
			this._mpop._keepOpen = true; //ZK-4059: prevent menupopup closed after menuitem doClick
	}

	/**
	 * Synchronizes the menu of this widget.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and change the content of the column's menu.
	 */
	syncColMenu(): void {
		this._shallColMenu = false;
		if (this._mpop) //it shall do even if !this.desktop
			this._mpop.syncColMenu();
	}

	/** @internal */
	_onColVisi(evt: zk.Event): void {
		var item = evt.currentTarget as zul.menu.Menuitem,
			pp = item.parent!;

		if (this._menupopup != 'auto-keep')
			pp.close({ sendOnOpen: true });
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
			if (mesh?.isSizedByContent())
				mesh.clearCachedSize_(); //Bug ZK-1315: clear cached size before column show/hide
			col.setVisible(item.isChecked());
		}
	}

	/** @internal */
	_onGroup(evt: zk.Event): void {
		(evt.target.parent as zul.mesh.ColumnMenupopup)._ungroup?.setVisible(true);
		//since 6.5.0 onGroup is not listened anymore, always fire event to server
		this._mref!.fire('onGroup', 'ascending' != this._mref!.getSortDirection(), { toServer: true });
	}

	/** @internal */
	_onUngroup(evt: zk.Event): void {
		// Empty on purpose. To be overriden.
	}

	/** @internal */
	_onAsc(evt: zk.Event): void {
		this._mref!.fire('onSort', true); // B50-ZK-266, always fire
	}

	/** @internal */
	_onDesc(evt: zk.Event): void {
		this._mref!.fire('onSort', false); // B50-ZK-266, always fire
	}

	/** @internal */
	_onMenuPopup(evt: zk.Event): void {
		var mref = this._mref;
		if (mref)
			jq(mref.$n_()).removeClass(mref.$s('visited')).removeClass(mref.$s('hover'));

		this._mref = (evt.data as { reference: zul.mesh.HeaderWidget }).reference;
	}

	/** @internal */
	override onChildAdded_(child: zul.mesh.HeaderWidget): void {
		super.onChildAdded_(child);
		this._syncColMenu();
		let mesh = this.getMeshWidget();
		if (mesh && mesh._syncEmpty)
			mesh._syncEmpty();
	}

	/** @internal */
	override onChildRemoved_(child: zul.mesh.HeaderWidget): void {
		super.onChildRemoved_(child);
		if (!this.childReplacing_)
			this._syncColMenu();
		let mesh = this.getMeshWidget();
		if (mesh && mesh._syncEmpty)
			mesh._syncEmpty();
	}

	/** @internal */
	getGroupPackage_(): string { // FIXME: orignally, `zk.$void`
		return '';
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
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
	/** @internal */
	_columns?: zul.mesh.ColumnMenuWidget;
	/** @internal */
	_asc?: zul.menu.Menuitem;
	/** @internal */
	_desc?: zul.menu.Menuitem;
	/** @internal */
	_group?: zul.menu.Menuitem;
	/** @internal */
	_ungroup?: zul.menu.Menuitem;

	getColumns(): zul.mesh.ColumnMenuWidget | undefined {
		return this._columns;
	}

	setColumns(columns: zul.mesh.ColumnMenuWidget): this {
		this._columns = columns;
		return this;
	}

	constructor(opts: { columns: zul.mesh.ColumnMenuWidget }) {
		super(opts);
	}

	/** @internal */
	override afterCreated_(opts: { columns: zul.mesh.ColumnMenuWidget }): void {
		super.afterCreated_(opts);
		this._init();
	}

	/**
	 * @returns the menuitem with ascending label
	 */
	getAscitem(): zul.menu.Menuitem | undefined {
		return this._asc;
	}

	/**
	 * @returns the menuitem with descending label
	 */
	getDescitem(): zul.menu.Menuitem | undefined {
		return this._desc;
	}

	/**
	 * @returns the menuitem with group label
	 */
	getGroupitem(): zul.menu.Menuitem | undefined {
		return this._group;
	}

	getUngroupitem(): zul.menu.Menuitem | undefined {
		return undefined;
	}

	/** @internal */
	_init(): void {
		var w = this._columns!;

		this.listen({ onOpen: [w, w._onMenuPopup] });

		if (zk.feature.pe && w.isColumnsgroup()) {
			if (!zk.isLoaded(w.getGroupPackage_()))
				zk.load(w.getGroupPackage_());
			var group = new zul.menu.Menuitem({
				label: msgzul.GRID_GROUP, visible: false
			});
			group.setSclass(w.$s('menugrouping'));
			group.listen({ onClick: [w, w._onGroup] });
			this.appendChild(group);
			this._group = group;
			if (zk.feature.ee) {
				var ungroup = new zul.menu.Menuitem({
					label: msgzul.GRID_UNGROUP, visible: false
				});
				ungroup.setSclass(w.$s('menuungrouping'));
				ungroup.listen({ onClick: [w, w._onUngroup] });
				this.appendChild(ungroup);
				this._ungroup = ungroup;
			}
		}
		var asc = new zul.menu.Menuitem({ label: msgzul.GRID_ASC });
		asc.setSclass(w.$s('menuascending'));
		asc.listen({ onClick: [w, w._onAsc] });
		this._asc = asc;
		this.appendChild(asc);

		var desc = new zul.menu.Menuitem({ label: msgzul.GRID_DESC });
		desc.setSclass(w.$s('menudescending'));
		desc.listen({ onClick: [w, w._onDesc] });
		this._desc = desc;
		this.appendChild(desc);
		this.syncColMenu();
		w.getPage()!.appendChild(this);
	}

	/**
	 * Synchronizes the menu
	 */
	syncColMenu(): void {
		var w = this._columns;
		for (var c = this.lastChild, p: zul.menu.Menuitem | undefined; c != this._desc;) {
			p = c!.previousSibling;
			this.removeChild(c!);
			c = p;
		}
		if (w?.isColumnshide()) {
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
				item.listen({ onClick: [w, w._onColVisi] });
				this.appendChild(item);
			}
		}
	}
}