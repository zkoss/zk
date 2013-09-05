/* ColumnMenuWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu, Jun 14, 2012 11:12:58 AM, Created by jumperchen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
/**
 * A skeletal implementation for a column menu widget.
 * @since 6.5.0
 */
zul.mesh.ColumnMenuWidget = zk.$extends(zul.mesh.HeadWidget, {
	_menupopup: 'none',
	_columnshide: true,
	_columnsgroup: true,

	$define: {
		/** Returns whether to enable hiding of the widget with the header context menu.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether to enable hiding of the widget with the header context menu.
		 * <p>Note that it is only applied when {@link #getMenupopup()} is auto.
		 * @param boolean columnshide
		 */
		columnshide: _zkf = function () {
			if (this.desktop)
				this._initColMenu();
		},
		/** Returns whether to enable grouping of the widget with the header context menu.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether to enable grouping of the widget with the header context menu.
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
	bind_: function (dt, skipper, after) {
		this.$supers(zul.mesh.ColumnMenuWidget, 'bind_', arguments);
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
			this._shallColMenu = this._mpop = null;
		}
		this.$supers(zul.mesh.ColumnMenuWidget, 'unbind_', arguments);
	},
	onResponse: function () {
		if (this._shallColMenu)
			this.syncColMenu();
	},
	_syncColMenu: function () {
		this._shallColMenu = true;
	},
	_initColMenu: function () {
		if (this._mpop)
			this._mpop.parent.removeChild(this._mpop);
		this._mpop = new zul.mesh.ColumnMenupopup({columns: this});
	},
	/** Synchronizes the menu of this widget.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and change the content of the column's menu.
	 */
	syncColMenu: function () {
		this._shallColMenu = false;
		if (this._mpop) //it shall do even if !this.desktop
			this._mpop.syncColMenu();
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
		if (col && col.parent == this) {
			var mesh = this.getMeshWidget();
			if (mesh && mesh.isSizedByContent())
				mesh.clearCachedSize_(); //Bug ZK-1315: clear cached size before column show/hide
			col.setVisible(item.isChecked());
		}
	},
	_onGroup: function (evt) {
		var ungroup;
		if ((ungroup = evt.target.parent._ungroup))
			ungroup.setVisible(true);
		//since 6.5.0 onGroup is not listened anymore, always fire event to server
		this._mref.fire('onGroup', 'ascending' != this._mref.getSortDirection(), {toServer: true});
	},
	_onUngroup: zk.$void,
	_onAsc: function (evt) {
		this._mref.fire('onSort', true); // B50-ZK-266, always fire
	},
	_onDesc: function (evt) {
		this._mref.fire('onSort', false); // B50-ZK-266, always fire
	},
	_onMenuPopup: function (evt) {
		var mref = this._mref;
		if (mref)
			jq(mref.$n()).removeClass(mref.$s('visited')).removeClass(mref.$s('hover'));
		
		this._mref = evt.data.reference; 
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._syncColMenu();
		var mesh = this.getMeshWidget();
		if (mesh && mesh._syncEmpty)
			mesh._syncEmpty();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (!this.childReplacing_)
			this._syncColMenu();
		var mesh = this.getMeshWidget();
		if (mesh) mesh._syncEmpty();
	},
	getGroupPackage_: zk.$void
});

/**
 * The Columns' Menu popup
 * @since 6.5.0
 */
zul.mesh.ColumnMenupopup = zk.$extends(zul.menu.Menupopup, {
	$define: {
		columns: null
	},
	/** Constructor
	 */
	$init: function () {
		this.$supers('$init', arguments);
		this.afterInit(this._init);
	},
	/** Returns the  menuitem with ascending label
	 * @return zul.menu.Menuitem
	 */
	getAscitem: function () {
		return this._asc;
	},
	/** Returns the  menuitem with descending label
	 * @return zul.menu.Menuitem
	 */
	getDescitem: function () {
		return this._desc;
	},
	/** Returns the  menuitem with group label
	 * @return zul.menu.Menuitem
	 */
	getGroupitem: function () {
		return this._group;
	},
	getUngroupitem: zk.$void,
	_init: function () {
		var w = this._columns;
		
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
		w.getPage().appendChild(this);
	},
	/** Synchronizes the menu
	 */
	syncColMenu: function () {
		var w = this._columns;
		for (var c = this.lastChild, p; c != this._desc;) {
			p = c.previousSibling;
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
})();