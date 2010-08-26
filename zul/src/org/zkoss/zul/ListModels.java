/* ListModels.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 25, 2010 4:37:32 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListSubModel;
import org.zkoss.zul.event.ListDataListener;

/**
 * A utility for {@link ListModel}
 *  
 * @author jumperchen
 * @since 5.0.4
 */
public class ListModels {
	
	private ListModels() {}
	
	/**
	 *  A String comparator for {@link ListSubModel} to match the value of
	 *  the model from the given key that user typed.
	 *  It will return 0 when the value starts with the key(no empty). 
	 */
	public final static Comparator STRING_COMPARATOR = new Comparator() {
		public int compare(Object key, Object value) {
			String idx = Objects.toString(key);
			return idx != null && value != null && idx.length() > 0 &&
					Objects.toString(value).startsWith(idx) ? 0 : 1;
		}
	};

	/**
	 *  A Map comparator for {@link ListSubModel} to match the value of
	 *  the model from the given key that user typed.
	 *  It will return 0 when the value starts with the key(no empty). 
	 */
	public final static Comparator MAP_COMPARATOR = new Comparator() {
		public int compare(Object key, Object value) {
			String idx = Objects.toString(key);
			return idx != null && value != null && idx.length() > 0 &&
					Objects.toString(((Map.Entry) value).getValue()).startsWith(idx) ? 0 : 1;
		}
	};
	
	/**
	 * Returns a proxy instance of the given model that implements
	 * {@link ListSubModel} and {@link ListModel} interface.
	 * @param model a {@link ListModel}
	 * @param comparator if return 0 means the value of the model is matched from
	 *  the given key that user typed
	 * @param nRows the maximal allowed number of matched items.
	 */
	public static ListModel toListSubModel(ListModel model, Comparator comparator, int nRows) {
		return new ListModels0(model, comparator, nRows);
	}
	
	/**
	 * Returns a proxy instance of the given model that implements
	 * {@link ListSubModel} and {@link ListModel} interface.
	 * <p> The default comparator is depended on the type of the model, if the
	 * model is a type of {@link ListModelMap}, the {@link #MAP_COMPARATOR} is used,
	 * unless the {@link #STRING_COMPARATOR} is used.
	 * <p>Default: <code>nRows is 15</code>.
	 * @param model a {@link ListModel}
	 * @see #toListSubModel(ListModel, Comparator, int)
	 */
	public static ListModel toListSubModel(ListModel model) {
		return new ListModels0(model, (model instanceof ListModelMap) ? MAP_COMPARATOR
					: STRING_COMPARATOR, 15);
	}
	
	private static class ListModels0 implements ListModel, ListSubModel,
			java.io.Serializable {
		private final ListModel _model;

		private final Comparator _comparator;

		private final int _nRows;

		private ListModels0(ListModel model, Comparator comparator, int nRows) {
			_model = model;
			_comparator = comparator;
			_nRows = nRows;
		}

		/**
		 * Returns the subset of the list model data that matches the specified
		 * value. It is usually used for implementation of auto-complete.
		 * 
		 * @param value
		 *            the value to retrieve the subset of the list model. It is
		 *            the key argument when invoking {@link #inSubModel}. this
		 *            string.
		 * @param nRows
		 *            the maximal allowed number of matched items. If negative,
		 *            it means the caller allows any number, but the
		 *            implementation usually limits to a certain number (for
		 *            better performance).
		 */
		public ListModel getSubModel(Object value, int nRows) {
			final LinkedList data = new LinkedList();
			nRows = nRows < 0 ? _nRows : nRows;
			for (int i = 0, j = _model.getSize(); i < j; i++) {
				Object o = _model.getElementAt(i);
				if (_comparator.compare(value, o) == 0) {
					data.add(o);
					if (--nRows <= 0)
						break; // done
				}
			}
			return new ListModelList(data);
		}

		public Object getElementAt(int index) {
			return _model.getElementAt(index);
		}

		public int getSize() {
			return _model.getSize();
		}

		public void addListDataListener(ListDataListener l) {
			_model.addListDataListener(l);
		}

		public void removeListDataListener(ListDataListener l) {
			_model.removeListDataListener(l);
		}
	}
	
}
