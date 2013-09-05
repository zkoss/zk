/* Rows.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:20     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	
	function _isPE() {
		return zk.feature.pe && zk.isLoaded('zkex.grid');
	}
	function _syncFrozen(wgt) {
		var grid = wgt.getGrid(),
			frozen;
		if (grid && grid._nativebar && (frozen = grid.frozen))
			frozen._syncFrozen();
	}
	
var Rows =
/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 * <p>Default {@link #getZclass}: z-rows.
 * @import zkex.grid.Group
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
	 * @return Array
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
	bind_: function (desktop, skipper, after) {
		this.$supers(Rows, 'bind_', arguments);
		var grid = this.getGrid();
		if (grid) // bind ebodyrows for MeshWidget
			grid.ebodyrows = this.$n();
		zWatch.listen({onResponse: this});
		var w = this;
		after.push(function () {
			w.stripe();
			_syncFrozen(w);
		});
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers(Rows, 'unbind_', arguments);
	},
	onResponse: function () {
		if (this.desktop){
			if (this._shallStripe) { //since bind_(...after)
				this.stripe();
				this.getGrid().onSize();
			}
		}
	},
	_syncStripe: function () {
		this._shallStripe = true;
	},
	/**
	 * Stripes the class for each row.
	 */
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
		if (_isPE() && child.$instanceof(zkex.grid.Group))
			this._groupsInfo.push(child);
		
		var g;
		if ((g = this.getGrid())) {
			if (g.fixForRowAdd_) 
				g.fixForRowAdd_();
			g._syncEmpty();
		}
		this._syncStripe();
		
		if (this.desktop)
			_syncFrozen(this);
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (_isPE() && child.$instanceof(zkex.grid.Group))
			this._groupsInfo.$remove(child);
		if (!this.childReplacing_)
			this._syncStripe();
		
		var g = this.getGrid();
		if (g) g._syncEmpty();
	},
	deferRedrawHTML_: function (out) {
		out.push('<tbody', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></tbody>');
	}
});
})();