/* ListModelConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec  1 16:55:36     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelList;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Component;

import java.util.Set;
import java.util.Map;
import java.util.List;

/**
 * The {@link TypeConverter} implmentation for converting collection to ListModel and vice versa.
 *
 * @author Henri Chen
 */
public class ListModelConverter implements TypeConverter {
	/** Convert a Set, Map, or List to associated ListModel.
	 * @param val must be instanceof Set, Map, or List.
	 */
	public Object coerceToUi(Object val, Component comp) {
		if (val == null) {
			throw new NullPointerException("val");
		}
		if (val instanceof Set) {
			return new BindingListModelSet((Set)val);
		} else if (val instanceof List) {
			return new BindingListModelList((List)val);
		} else if (val instanceof Map) {
			return new BindingListModelMap((Map)val);
		} else {
			throw new UiException("Expects only java.util.Set, java.util.List, java.util.Map only. "+val.getClass());
		}
	}

	/** Convert a ListModel to Set, Map, or List.
	 * @param val must be ListModelSet, ListModelList, or ListModelMap
	 */
	public Object coerceToBean(Object val, Component comp) {
		if (val == null) {
			throw new NullPointerException("val");
		}
		if (val instanceof ListModelSet) {
			return ((ListModelSet)val).getInnerSet();
		} else if (val instanceof List) {
			return ((ListModelList)val).getInnerList();
		} else if (val instanceof Map) {
			return ((ListModelMap)val).getInnerMap();
		} else {
			throw new UiException("Expects ListModelSet, ListModelList, or ListModelMap only."+val.getClass());
		}
	}
}
