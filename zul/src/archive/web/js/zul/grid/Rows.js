/* Rows.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:20     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Rows = zk.$extends(zul.Widget, {
	_visibleItemCount: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._groupsInfo = [];
		this._groups = [];
	},
	getGrid: function () {
		return this.parent;
	},
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	getGroups: function () {
		return this._groups;
	},
	hasGroup: function () {
		return this._groupsInfo.length != 0;
	},
	getZclass: function () {
		return this._zclass == null ? "z-rows" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onResponse', this);
		zk.afterMount(this.proxy(this.onResponse));
	},
	unbind_: function () {
		zWatch.unlisten('onResponse', this);
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this._shallStripe) this.stripe();
	},
	_syncStripe: function () {
		this._shallStripe = true;
		if (!this.inServer && this.desktop)	this.stripe();
	},
	stripe: function () {
		var scOdd = this.getGrid().getOddRowSclass();
		if (!scOdd) return;
		var n = this.getNode();
		for (var j = 0, w = this.firstChild, even = true; w; w = w.nextSibling, ++j) {
			if (w.isVisible() && w.isStripeable_()) {
				zDom[even ? 'rmClass' : 'addClass'](n.rows[j], scOdd);
				w.fire("onStripe");
				even = !even;
			}
		}
		this._shallStripe = false;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._syncStripe();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this._syncStripe();
	},
	//Paging//
	getVisibleItemCount: function () {
		return this._visibleItemCount;
	},
	_addVisibleItemCount: function (count) {
		if (count) {
			this._visibleItemCount += count;
			if (this.parent != null && this.parent.inPagingMold()) {
				var pgi = this.parent.getPaginal();
				pgi.setTotalSize(this._visibleItemCount);
				// TODO invalidate(); // the set of visible items might change
			}
		}
	},
	_fixGroupIndex: function (j, to, infront) {
		//TODO:
		/**for (Iterator it = getChildren().listIterator(j);
		it.hasNext() && (to < 0 || j <= to); ++j) {
			Object o = it.next();
			if (o instanceof Group) {
				int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
				if (g != null) {
					g[0] = j;
					if (g[2] != -1) g[2] += (infront ? 1 : -1);
				}
			}
		}*/
	},
	getGroup: function (index) {
		if (!this._groupsInfo.length) return null;
		var g = this._getGroupsInfoAt(index);
		if (g != null) return ; // TODO (Group)getChildren().get(g[0]);
		return null;
	},
	/**
	 * Returns the last groups info which matches with the same index.
	 * Because dynamically maintain the index of the groups will occur the same index
	 * at the same time in the loop. 
	 */
	_getLastGroupsInfoAt: function (index) {
		/**
		int [] rg = null;
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (index == g[0]) rg = g;
			else if (index < g[0]) break;
		}
		return rg;*/
	},
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	_getGroupsInfoAt: function (index, isGroup) {
		// TODO:
		/**
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (isGroup) {
				if (index == g[0]) return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;*/
	}
	//-- super --//
	/** TODO
	 * public boolean insertBefore(Component child, Component refChild) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		Row newItem = (Row) child;
		final int jfrom = hasGroup() && newItem.getParent() == this ? newItem.getIndex(): -1;	

		final boolean isReorder = child.getParent() == this;
		if (newItem instanceof Groupfoot){
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null) {
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (isReorder) {
					final int idx = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
				final int[] g = (int[]) _groupsInfo.get(getGroupCount()-1);
				g[2] = getChildren().size() - (isReorder ? 2 : 1);
			} else {
				final int idx = ((Row)refChild).getIndex();				
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");				
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				g[2] = idx-1;
				if (isReorder) {
					final int nindex = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(nindex);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
			}							
		}
		if (super.insertBefore(child, refChild)) {
			if(hasGroup()) {
				final int
					jto = refChild instanceof Row ? ((Row)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
				if (fixFrom >= 0) fixGroupIndex(fixFrom,
					jfrom >=0 && jto >= 0 ? jfrom > jto ? jfrom: jto: -1, !isReorder);
			}
			if (newItem instanceof Group) {
				Group group = (Group) newItem;
				int index = group.getIndex();
				if (_groupsInfo.isEmpty())
					_groupsInfo.add(new int[]{group.getIndex(), getChildren().size() - index, -1});
				else {
					int idx = 0;
					int[] prev = null, next = null;
					for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
						int[] g = (int[])it.next();
						if(g[0] <= index) {
							prev = g;
							idx++;
						} else {
							next = g;
							break;
						}
					}
					if (prev != null) {
						int leng = index - prev[0], 
							size = prev[1] - leng + 1;
						prev[1] = leng;
						_groupsInfo.add(idx, new int[]{index, size, size > 1 ? prev[2] : -1});
						if (size > 1) prev[2] = -1; // reset groupfoot
					} else if (next != null) {
						_groupsInfo.add(idx, new int[]{index, next[0] - index, -1});
					}
				}
			} else if (hasGroup()) {
				int index = newItem.getIndex();
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]++;
					if (g[2] != -1) g[2]++;
				}
				
			}
			
			afterInsert(child);
			return true;
		}
		return false;
	}*/
	/**
	 * If the child is a group, its groupfoot will be removed at the same time.
	 */
	/** TODO
	 * public boolean removeChild(Component child) {
		if (child.getParent() == this)
			beforeRemove(child);
		int index = hasGroup() ? ((Row)child).getIndex() : -1;
		if(super.removeChild(child)) {
			if (child instanceof Group) {
				int[] prev = null, remove = null;
				for(Iterator it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = (int[])it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove !=null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						removeChild((Component) getChildren().get(idx -1));
							// Because the fixGroupIndex will skip the first groupinfo,
							// we need to subtract 1 from the idx variable
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1) g[2]--;
					fixGroupIndex(index, -1, false);
				}
				else fixGroupIndex(index, -1, false);
				if (child instanceof Groupfoot){
					final int[] g1 = getGroupsInfoAt(index);	
					if(g1 != null){ // group info maybe remove cause of grouphead removed in previous op
						g1[2] = -1;
					}
				}
			}
			return true;
		}
		return false;
	}*/
	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void afterInsert(Component comp) {
		updateVisibleCount((Row) comp, false);
		checkInvalidateForMoved(comp, false);
	}*/
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void beforeRemove(Component comp) {
		updateVisibleCount((Row) comp, true);
		checkInvalidateForMoved(comp, true);
	}*/
	/**
	 * Update the number of the visible item before it is removed or after it is added.
	 */
	/**private void updateVisibleCount(Row row, boolean isRemove) {
		if (row instanceof Group || row.isVisible()) {
			final Group g = getGroup(row.getIndex());
			
			// We shall update the number of the visible item in the following cases.
			// 1) If the row is a type of Groupfoot, it is always shown.
			// 2) If the row is a type of Group, it is always shown.
			// 3) If the row doesn't belong to any group.
			// 4) If the group of the row is open.
			if (row instanceof Groupfoot || row instanceof Group || g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);
			
			if (row instanceof Group) {
				final Group group = (Group) row;
				
				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the current group.
				final Row preRow = (Row) row.getPreviousSibling();
				if (preRow == null) {
					if (!group.isOpen()) {
						addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				} else {
					final Group preGroup = preRow instanceof Group ? (Group) preRow : getGroup(preRow.getIndex());
					if (preGroup != null) {
						if (!preGroup.isOpen() && group.isOpen())
							addVisibleItemCount(isRemove ? -group.getVisibleItemCount() : group.getVisibleItemCount());
						else if (preGroup.isOpen() && !group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					} else {
						if (!group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				}
			}
		}
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getVisibleItemCount());
	}*/
	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 */
	/**private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last elem which in act and act has only one elem
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold() && !isInvalidated()) {
			final List children = getChildren();
			final int sz = children.size(),
				pgsz = grid.getPageSize();
			int n = sz - (grid.getActivePage() + 1) * pgsz;
			if (n <= 0) {//must be last page
				n += pgsz; //check in-act (otherwise, check after-act)
				if (bRemove && n <= 1) { //last elem, in act and remove
					invalidate();
					return;
				}
			} else if (n > 50)
				n = 50; //check at most 50 items (for better perf)

			for (ListIterator it = children.listIterator(sz);
			--n >= 0 && it.hasPrevious();)
				if (it.previous() == child)
					return; //no need to invalidate

			invalidate();
		}
	}*/

	/** Returns an iterator to iterate thru all visible children.
	 * Unlike {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * @since 3.5.1
	 */
	/**public Iterator getVisibleChildrenIterator() {
		final Grid grid = getGrid();
		if (grid != null && grid.inSpecialMold())
			return grid.getDrawerEngine().getVisibleChildrenIterator();
		return new VisibleChildrenIterator();
	}*/
	/**
	 * An iterator used by visible children.
	 */
	/**private class VisibleChildrenIterator implements Iterator {
		private final ListIterator _it = getChildren().listIterator();
		private Grid _grid = getGrid();
		private int _count = 0;
		public boolean hasNext() {
			if (_grid == null || !_grid.inPagingMold()) return _it.hasNext();
			
			if (_count >= _grid.getPaginal().getPageSize()) {
				return false;
			}

			if (_count == 0) {
				final Paginal pgi = _grid.getPaginal();
				int begin = pgi.getActivePage() * pgi.getPageSize();
				for (int i = 0; i < begin && _it.hasNext();) {
					getVisibleRow((Row)_it.next());
					i++;
				}
			}
			return _it.hasNext();
		}
		private Row getVisibleRow(Row row) {
			if (row instanceof Group) {
				final Group g = (Group) row;
				if (!g.isOpen()) {
					for (int j = 0, len = g.getItemCount(); j < len
							&& _it.hasNext(); j++)
						_it.next();
				}
			}
			while (!row.isVisible())
				row = (Row)_it.next();
			return row;
		}
		public Object next() {
			if (_grid == null || !_grid.inPagingMold()) return _it.next();
			_count++;
			final Row row = (Row)_it.next();
			return _it.hasNext() ? getVisibleRow(row) : row;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}*/
});
