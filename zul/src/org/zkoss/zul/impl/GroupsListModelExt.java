/* GroupsListModelExt.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 12:10:19 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import java.util.Comparator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ext.GroupsSortableModel;

/*package*/ class GroupsListModelExt<D, G, F> extends GroupsListModel<D, G, F>
		implements GroupsSortableModel<D>, ComponentCloneListener, Cloneable {
	/*package*/ GroupsListModelExt(GroupsModel<D, G, F> model) {
		super(model);
	}

	/**
	 * Groups and sorts the data by the specified column and comparator.
	 * It only called when {@link org.zkoss.zul.Listbox} or {@link org.zkoss.zul.Grid} has the sort function.
	 * @param colIndex the index of the column
	 */
	@SuppressWarnings("unchecked")
	public void group(Comparator<D> cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsSortableModel))
			throw new UiException(GroupsSortableModel.class + " must be implemented in " + _model.getClass());
		((GroupsSortableModel) _model).group(cmpr, ascending, colIndex);
	}

	/** Sorts the data by the specified column and comparator.
	 */
	@SuppressWarnings("unchecked")
	public void sort(Comparator<D> cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsSortableModel))
			throw new UiException(GroupsSortableModel.class + " must be implemented in " + _model.getClass());
		((GroupsSortableModel) _model).sort(cmpr, ascending, colIndex);
	}

	@SuppressWarnings("unchecked")
	public Object willClone(Component comp) {
		if (_model instanceof ComponentCloneListener) {
			GroupsListModelExt clone = (GroupsListModelExt) clone();
			GroupsModel m = (GroupsModel) ((ComponentCloneListener) _model).willClone(comp);
			if (m != null)
				clone._model = m;
			clone.init(); // reset grouping info
			return clone;
		}
		return null; // no need to clone
	}
}
