/* SortWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue May 26 14:51:17     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a sortable widget.
 */
zul.mesh.SortWidget = zk.$extends(zul.mesh.HeaderWidget, {
	_sortDirection: 'natural',
	_sortAscending: 'none',
	_sortDescending: 'none',

	$define: {
    	/** Returns the sort direction.
    	 * <p>Default: "natural".
    	 * @return String
    	 */
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
		sortDirection: function (v) {
			if (this.desktop) {
				var $n = jq(this.$n('sort-icon'));
				$n.removeClass();
				switch (v) {
				case 'ascending':
					$n.addClass('z-icon-caret-up');
					break;
				case 'descending':
					$n.addClass('z-icon-caret-down');
				}
			}
		},
		/** Returns the ascending sorter, or null if not available.
		 * @return String
		 */
		/** Sets the ascending sorter with "client", "auto", or null for
		 * no sorter for the ascending order.
		 * @param String sortAscending
		 */
		sortAscending: function (v) {
			if (!v)
				this._sortAscending = v = 'none';
			
			if (this.desktop) {
				var $n = jq(this.$n('sort-icon'));
				if (v == 'none') {
					$n.removeClass();
				} else
					$n.addClass('z-icon-caret-up');
			}
		},
		/** Returns the descending sorter, or null if not available.
		 * @return String
		 */
		/** Sets the descending sorter with "client", "auto", or null for
		 * no sorter for the descending order.
		 * @param String sortDescending
		 */
		sortDescending: function (v) {
			if (!v)
				this._sortDescending = v = 'none';
			
			if (this.desktop) {
				var $n = jq(this.$n('sort-icon'));
				if (v == 'none') {
					$n.removeClass();
				} else
					$n.addClass('z-icon-caret-down');
			}
		}
	},
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onSort: this}, -1000);
	},
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
	setSort: function (type) {
		if (type && type.startsWith('client')) {
			this.setSortAscending(type);
			this.setSortDescending(type);
		} else {
			this.setSortAscending('none');
			this.setSortDescending('none');
		}
	},
	isSortable_: function () {
		return this._sortAscending != 'none' || this._sortDescending != 'none';
	},
	/**
	 * Sorts the data.
	 * @param String ascending
	 * @param jq.Event evt
	 * @return boolean
	 * @disable(zkgwt)
	 */
	sort: function (ascending, evt) {
		if (!this.checkClientSort_(ascending))
			return false;
		
		evt.stop();
		
		this.replaceCavedChildrenInOrder_(ascending);
		
		return true;
	},
	/** Check the status whether can be sort in client side.
	 * @param String ascending
	 * @return boolean
	 * @since 5.0.6
	 * @see #sort
	 */
	checkClientSort_: function (ascending) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ('ascending' == dir) return false;
		} else {
			if ('descending' == dir) return false;
		}

		var sorter = ascending ? this._sortAscending: this._sortDescending;
		if (sorter == 'fromServer')
			return false;
		else if (sorter == 'none') {
			evt.stop();
			return false;
		}
		
		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel()) return false;
			// if in model, the sort should be done by server
			
		return true;
	},
	/** Replaced the child widgets with the specified order.
	 * @param String ascending
	 * @since 5.0.6
	 * @see #sort
	 */
	replaceCavedChildrenInOrder_: function (ascending) {
		var mesh = this.getMeshWidget(),
			body = this.getMeshBody(),
			dir = this.getSortDirection(),
			sorter = ascending ? this._sortAscending: this._sortDescending,
			desktop = body.desktop,
			node = body.$n();
		try {
			body.unbind();
			var d = [], col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w; (w = it.next()); z++) 
				for (var k = 0, cell = w.firstChild; cell; cell = cell.nextSibling, k++) 
					if (k == col) {
						d[i++] = {
							wgt: cell,
							index: z
						};
					}
			
			var dsc = dir == 'ascending' ? -1 : 1, fn = this.sorting, isNumber = sorter == 'client(number)';
			d.sort(function(a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});
			for (var i = 0, k = d.length; i < k; i++) {
				body.appendChild(d[i].wgt.parent);
			}
			this._fixDirection(ascending);
			
		} finally {
			body.replaceHTML(node, desktop);
		}
	},
	/**
	 * The default implementation to compare the data.
     * @param Object o1 the first object to be compared.
     * @param Object o2 the second object to be compared.
     * @param boolean isNumber
     * @return int
	 */
	sorting: function(a, b, isNumber) {
		var v1, v2;
		if (typeof a.getLabel == 'function')
			v1 = a.getLabel();
		else if (typeof a.getValue == 'function')
			v1 = a.getValue();
		else v1 = a;
		
		if (typeof b.getLabel == 'function')
			v2 = b.getLabel();
		else if (typeof b.getValue == 'function')
			v2 = b.getValue();
		else v2 = b;
		
		if (isNumber) return v1 - v2;
		return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
	},
	_fixDirection: function (ascending) {
		//maintain
		var direction = ascending ? 'ascending' : 'descending';
		for (var w = this.parent.firstChild; w; w = w.nextSibling)
			w.setSortDirection(w == this ? direction : 'natural');
	},
	onSort: function (evt) {
		var dir = this.getSortDirection();
		if ('ascending' == dir)
			this.sort(false, evt);
		else if ('descending' == dir)
			this.sort(true, evt);
		else if (!this.sort(true, evt))
			this.sort(false, evt);
	},
	bind_: function () {
		this.$supers(zul.mesh.SortWidget, 'bind_', arguments);
		if (this._sortAscending != 'none' || this._sortDescending != 'none') {
			var $n = jq(this.$n()),
				$sortIcon = jq(this.$n('sort-icon'));
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
	},
	unbind_: function () {
		this.$supers(zul.mesh.SortWidget, 'unbind_', arguments);
	},
	getColumnMenuPopup_: zk.$void,
	_doMenuClick: function (evt) {
		if (this.parent._menupopup && this.parent._menupopup != 'none') {
			var pp = this.parent._menupopup,
				btn = this.$n('btn'),
				zcls = this.getZclass();
			
			//for not removing hover effect when moving mouse on menupopup
			jq(this.$n()).addClass(this.$s('visited'));
			
			if (pp == 'auto' && this.parent._mpop)
				pp = this.parent._mpop;
			else
				pp = this.$f(this.parent._menupopup);

			if (zul.menu.Menupopup.isInstance(pp)) {
				var ofs = zk(btn).revisedOffset(),
					asc = this.getSortAscending() != 'none',
					desc = this.getSortDescending() != 'none',
					mw = this.getMeshWidget();
				if (pp.$instanceof(zul.mesh.ColumnMenupopup)) {
					pp.getAscitem().setVisible(asc);
					pp.getDescitem().setVisible(desc);
					var model = mw.getModel();
					if (zk.feature.pe && pp.getGroupitem()) {
						if (model == 'group' || !model || this.isListen('onGroup', {asapOnly: 1}))
							pp.getGroupitem().setVisible((asc || desc));
						else
							pp.getGroupitem().setVisible(false);
					}
					if (zk.feature.ee && pp.getUngroupitem()) {
						var visible = !model || this.isListen('onUngroup', {asapOnly: 1});
						pp.getUngroupitem().setVisible(visible && mw.hasGroup());
					}
					
					var sep = pp.getDescitem().nextSibling;
					if (sep) 
						sep.setVisible((asc || desc));
				} else {
					pp.listen({onOpen: [this.parent, this.parent._onMenuPopup]});
				}
				pp.open(btn, [ofs[0], ofs[1] + btn.offsetHeight - 4], null, {sendOnOpen: true});
			}
			evt.stop(); // avoid onSort event.
		}
	}
});