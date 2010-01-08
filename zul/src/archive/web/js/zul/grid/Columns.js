/* Columns.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:32     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 * <p>Default {@link #getZclass}: z-columns.
 */
zul.grid.Columns = zk.$extends(zul.mesh.HeadWidget, {
	_menupopup: "none",
	_columnshide: true,
	_columnsgroup: true,

	$define: {
		/** Returns whether to enable hiding of columns with the header context menu.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether to enable hiding of columns with the header context menu.
		 * <p>Note that it is only applied when {@link #getMenupopup()} is auto.
		 * @param boolean columnshide
		 */
		columnshide: _zkf = function () {
			if (this.desktop)
				this._initColMenu();
		},
		/** Returns whether to enable grouping of columns with the header context menu.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether to enable grouping of columns with the header context menu.
		 * <p>Note that it is only applied when {@link #getMenupopup()} is auto.
		 * @param boolean columnsgroup
		 */
		columnsgroup: _zkf,
		/** Returns the ID of the Menupopup ({@link zul.menu.Menupopup}) that should appear
		 * when the user clicks on the element.
		 *
		 * <p>Default: none (a default menupoppup).
		 * @return String
		 */
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
		 * @param String mpop an ID of the menupopup component, "none", or "auto".
		 * 	"none" is assumed by default, "auto" means the menupopup component is 
		 *  created automatically.
		 * @see #setMenupopup(String)
		 */
		menupopup: function () {
			if (this._menupopup != 'auto')
				this._mpop = null;
			this.rerender();		
		}
	},
	/** Returns the grid that contains this columns. 
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent;
	},
	rerender: function () {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else 
				this.$supers('rerender', arguments);
		}
		return this;
	},
	/** Sets the menupopup instance ({@link zul.menu.Menupopup}) that should appear
	 * when the user clicks on the element of each column.
	 * 
	 * @param zul.menu.Menupopup mpop
	 */
	setPopup: function (mpop) {
		if (zk.Widget.isInstance(mpop)) {
			this._menupopup = mpop;
			this._mpop = null;
		}
		return this;
	},
	getZclass: function () {
		return this._zclass == null ? "z-columns" : this._zclass;
	},
	bind_: function (dt, skipper, after) {
		this.$supers('bind_', arguments);
		zWatch.listen({onResponse: this});
		var w = this;
		if (this._menupopup == 'auto') {
			after.push(function() {
				w._initColMenu();
			});
		}
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		if (this._mpop) {
			this._mpop.parent.removeChild(this._mpop);
			this._shallSync = this._mpop = null;			
		}
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this._shallSync)
			this.syncColMenu();
	},
	_syncColMenu: function () {
		this._shallSync = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._syncColMenu();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this._syncColMenu();
	},
	_initColMenu: function () {
		if (this._mpop)
			this._mpop.parent.removeChild(this._mpop);
		this._mpop = new zul.grid.ColumnMenupopup({columns: this});
	},
	syncColMenu: function () {
		this._shallSync = false;
		if (this._mpop)
			this._mpop.syncColMenu();
		return this;
	},
	_onColVisi: function (evt) {
		var item = evt.currentTarget,
			pp = item.parent;
			
		pp.close({sendOnOpen: true});
		var checked = 0;
		for (var w = pp.firstChild; w; w = w.nextSibling) {
			if (w.$instanceof(zul.menu.Menuitem) && w.isChecked())
				checked++;
		}
		if (checked == 0)
			item.setChecked(true);
			
		var col = zk.Widget.$(item._col);
		if (col && col.parent == this)
			col.setVisible(item.isChecked());
	},
	_onGroup: function (evt) {
		this._mref.fire('onGroup');
	},
	_onAsc: function (evt) {
		if (this._mref.getSortDirection() != 'ascending')
			this._mref.fire('onSort');
	},
	_onDesc: function (evt) {
		if (this._mref.getSortDirection() != 'descending')
			this._mref.fire('onSort');
	},
	_onMenuPopup: function (evt) {
		if (this._mref) {
			var zcls = this._mref.getZclass(),
				n = this._mref.$n();
			jq(n).removeClass(zcls + '-visi').removeClass(zcls + '-over');
		}
		this._mref = evt.data.reference; 
	}
});
// Columns' Menu
zul.grid.ColumnMenupopup = zk.$extends(zul.menu.Menupopup, {
	$define: {
		columns: null
	},
	$init: function () {
		this.$supers('$init', arguments);
		this.afterInit(this._init);
	},
	getAscitem: function () {
		return this._asc;
	},
	getDescitem: function () {
		return this._desc;
	},
	getGroupitem: function () {
		return this._group;
	},
	_init: function () {
		var w = this._columns,
			zcls = w.getZclass();

		this.listen({onOpen: [w, w._onMenuPopup]});
		
		if (zk.feature.professional && w.isColumnsgroup()) {
			var group = new zul.menu.Menuitem({label: msgzul.GRID_GROUP});
				group.setSclass(zcls + '-menu-grouping');
				group.listen({onClick: [w, w._onGroup]});
			this.appendChild(group);
			this._group = group;
		}
		var asc = new zul.menu.Menuitem({label: msgzul.GRID_ASC});
			asc.setSclass(zcls + '-menu-asc');
			asc.listen({onClick: [w, w._onAsc]});
		this._asc = asc;
		this.appendChild(asc);
		
		var desc = new zul.menu.Menuitem({label: msgzul.GRID_DESC});
		desc.setSclass(zcls + '-menu-dsc');
		desc.listen({onClick: [w, w._onDesc]});
		this._desc = desc;
		this.appendChild(desc);
		this.syncColMenu();
		w.getPage().appendChild(this);
	},
	syncColMenu: function () {
		var w = this._columns;
		for (var c = this.lastChild, p; c != this._desc;) {
			p = c.previousSibling
			this.removeChild(c);
			c = p;
		}
		if (w && w.isColumnshide()) {
			var sep = new zul.menu.Menuseparator();
			this.appendChild(sep);
			for (var item, c = w.firstChild; c; c = c.nextSibling) {
				item = new zul.menu.Menuitem({
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
});