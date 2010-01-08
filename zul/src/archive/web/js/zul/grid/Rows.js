/* Rows.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:20     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 * <p>Default {@link #getZclass}: z-rows.
 */
zul.grid.Rows = zk.$extends(zul.Widget, {
	_visibleItemCount: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._groupsInfo = [];
	},
	$define: {
		/** Returns the number of visible descendant {@link Row}.
		 * @return int
		 */
		visibleItemCount: null
	},
	/** Returns the grid that contains this rows.
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent;
	},
	/** Returns the number of groups.
	 * @return int
	 */
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	/** Returns a list of all {@link Group}.
	 * @return zul.grid.Groups
	 */
	getGroups: function () {
		return this._groupsInfo.$clone();
	},
	/** Returns whether Group exists.
	 * @return boolean
	 */
	hasGroup: function () {
		return this._groupsInfo.length;
	},
	getZclass: function () {
		return this._zclass == null ? "z-rows" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onResponse: this});
		zk.afterMount(this.proxy(this.onResponse));
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this.desktop && this._shallStripe) { //since afterMount 
			this.stripe();
			this.getGrid().onSize();
		}
	},
	_syncStripe: function () {
		this._shallStripe = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	stripe: function () {
		var grid = this.getGrid(),
			scOdd = grid.getOddRowSclass();
		if (!scOdd) return;
		var n = this.$n();
		if (!n) return; //Bug #2873478. Rows might not bounded yet

		for (var j = 0, w = this.firstChild, even = !(this._offset & 1); w; w = w.nextSibling, ++j) {
			if (w.isVisible() && w.isStripeable_()) {
				// check whether is a legal Row or not for zkex.grid.Detail
				for (;n.rows[j] && n.rows[j].id != w.uuid;++j);

				jq(n.rows[j])[even ? 'removeClass' : 'addClass'](scOdd);
				w.fire("onStripe");
				even = !even;
			}
		}
		this._shallStripe = false;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Group))
			this._groupsInfo.push(child);
		this._syncStripe();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zul.grid.Group))
			this._groupsInfo.$remove(child);
		this._syncStripe();
	}
});
