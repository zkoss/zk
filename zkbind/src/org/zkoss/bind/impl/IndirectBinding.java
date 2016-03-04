/** IndirectBinding.java.

	Purpose:
		
	Description:
		
	History:
		4:58:52 PM Jun 26, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;

/**
 * An indirect binding, a kind of reference binding, but it won't create a sub-tracking
 * tree to get two way bindings, and it is used for children binding internally.
 * i.e. this class is designed for some specific cases.
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class IndirectBinding implements ReferenceBinding, Serializable {
	private Object _data;

	public IndirectBinding(Object data) {
		_data = data;
	}

	/**
	 * Returns the data
	 */
	public Object getData() {
		return _data;
	}

	protected abstract ListModel getModel();

	/**
	 * Sets the data
	 */
	public void setData(Object data) {
		_data = data;
	}

	/**
	 * do nothing for this method
	 */
	public void load(BindContext ctx) {
		// do nothing
	}

	public void setValue(BindELContext ctx, Object value) {
		ListModel model = getModel();
		if (model instanceof ListModelArray) {
			((ListModelArray<Object>) model).set(((ListModelArray<Object>) model).indexOf(getData()), value);
			_data = value; // update it
		} else if (model instanceof ListModelList<?>) {
			((ListModelList<Object>) model).set(((ListModelList<Object>) model).indexOf(getData()), value);
			_data = value; // update it
		}
	}

	public Object getValue(BindELContext ctx) {
		return getData();
	}

	/**
	 * Null is returned by default. (never be used)
	 */
	public Map<String, Object> getArgs() {
		return null;
	}

	/**
	 * Null is returned by default. (never be used)
	 */
	public String getPropertyString() {
		return null;
	}

	/**
	 * do nothing for this method
	 */
	public void invalidateCache() {
	}

}
