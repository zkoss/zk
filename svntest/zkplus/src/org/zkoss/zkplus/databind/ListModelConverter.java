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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * The {@link TypeConverter} implmentation for converting collection to ListModel and vice versa.
 *
 * @author Henri Chen
 */
public class ListModelConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808191433L;
	/** Convert a Set, Map, List, Object[], Enum, or other kind of BindingListModel to associated {@link BindingListModel}.
	 * @param val must be instanceof Set, Map, List, Object[], Enum Class, or other kind of BindingListModel implementation.
	 */
	public Object coerceToUi(Object val, Component comp) {
		if (val == null) {
			val = new ArrayList();
		}
		final boolean distinct = isDistinct(comp);
		//since 3.1, 20080416, Henri Chen: allow custom arguments.
		//model can specify distinct=false in annotation to allow handle same-object-in-multiple-items
		if (val instanceof BindingListModel) {
			return val;
		} else if (val instanceof Set) {
			return new BindingListModelSet((Set)val, true);
		} else if (val instanceof List) {
			return new BindingListModelList((List)val, true, distinct);
		} else if (val instanceof Map) {
			return new BindingListModelMap((Map)val, true);
		} else if (val instanceof Object[]) {
			return new BindingListModelArray((Object[]) val, true, distinct);
		} else if ((val instanceof Class) && Enum.class.isAssignableFrom((Class)val)) {
			return new BindingListModelArray((Object[]) ((Class)val).getEnumConstants(), true);
		} else {
			throw new UiException("Expects java.util.Set, java.util.List, java.util.Map, Object[], Enum Class, or BindingListModel only. "+val.getClass());
		}
	}
	
	/*package*/ static boolean isDistinct(Component comp) {
		Map args = (Map) comp.getAttribute(DataBinder.ARGS);
		boolean distinct = true;
		if (args != null) {
			final String distinctstr = (String) args.get("distinct");
			if (distinctstr != null) {
				distinct = Boolean.valueOf(distinctstr).booleanValue();
			}
		}
		return distinct;
	}

	/** Convert a {@link BindingListModel} to Set, Map, List, or BindingListModel (itself).
	 * @param val must be BindingListModelSet, BindingListModelList, BindingListModelMap, or other kind of BindingListModel
	 */
	public Object coerceToBean(Object val, Component comp) {
		if (val == null) {
			throw new NullPointerException("val");
		}
		if (val instanceof BindingListModelSet) {
			return ((BindingListModelSet)val).getInnerSet();
		} else if (val instanceof BindingListModelList) {
			return ((BindingListModelList)val).getInnerList();
		} else if (val instanceof BindingListModelMap) {
			return ((BindingListModelMap)val).getInnerMap();
		} else if (val instanceof BindingListModelArray) {
			return ((BindingListModelArray)val).getInnerArray();
		} else if (val instanceof BindingListModel) {
			return val;
		} else {
			throw new UiException("Expects BindingListModelSet, BindingListModelList, BindingListModelMap, or BindingListModel only."+val.getClass());
		}
	}
}
