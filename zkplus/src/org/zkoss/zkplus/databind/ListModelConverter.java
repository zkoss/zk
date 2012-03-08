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
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ext.Selectable;

/**
 * The {@link TypeConverter} implementation for converting collection to ListModel and vice versa. <p/>
 * Since zk 6.0.1 This class doesn't handle setMultiple and Selectable, you should use corresponding new converter for different component if it has selection.
 * @author Henri Chen
 */
public class ListModelConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808191433L;
	/** Convert a Set, Map, List, Object[], Enum, or other kind of BindingListModel to associated {@link BindingListModel}.
	 * @param val must be instanceof Set, Map, List, Object[], Enum Class, or other kind of BindingListModel implementation.
	 */
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp) {
		if (val == null) {
			val = new ArrayList();
		}
		BindingListModel wrappedModel = null;
		final boolean distinct = isDistinct(comp);
		//since 3.1, 20080416, Henri Chen: allow custom arguments.
		//model can specify distinct=false in annotation to allow handle same-object-in-multiple-items
		if (val instanceof BindingListModel) {
			return val;
		} else if (val instanceof ListModel) { //Bug 3354086: Model attribute will not accept a ListModel
			wrappedModel = new BindingListModelListModel((ListModel) val,distinct);			
		} else if (val instanceof Set) {
			wrappedModel = new BindingListModelSet((Set)val, true);
		} else if (val instanceof List) {
			wrappedModel = new BindingListModelList((List)val, true, distinct);
		} else if (val instanceof Map) {
			wrappedModel = new BindingListModelMap((Map)val, true);
		} else if (val instanceof Object[]) {
			wrappedModel = new BindingListModelArray((Object[]) val, true, distinct);
		} else if ((val instanceof Class) && Enum.class.isAssignableFrom((Class)val)) {
			wrappedModel = new BindingListModelArray(((Class)val).getEnumConstants(), true);
		} else if (val instanceof GroupsModel) { //feature#2866506: Data Binding shall support GroupsModel with Listbox/Grid
			wrappedModel = new BindingGroupsListModel((GroupsModel) val);
		} else {
			throw new UiException("Expects java.util.Set, java.util.List, java.util.Map, Object[], Enum Class, GroupsModel, ListModel,or BindingListModel only. "+val.getClass());
		}
		
		//ZK-927 zkplus databinding1 should auto-wrapping BindingListModelXxx with setMultiple() and Selectable handled
		final ListModel compModel = getComponentModel(comp);
		if(compModel instanceof Selectable && wrappedModel instanceof Selectable){
			Selectable selectable = ((Selectable)compModel);
			((Selectable) wrappedModel).setMultiple(selectable.isMultiple());
			
			for(Object selected:selectable.getSelection()){
				((Selectable) wrappedModel).addToSelection(selected);
			}
			
		}
		if(!(val instanceof ListModel)){
			//for the data that is not listmodel, provide a chance to post process it. see ListboxListModelConverter 
			wrappedModel = handleWrappedNonListModel(comp,wrappedModel);
		}
		
		return wrappedModel;
		
	}
	
	/**
	 * Gets the model of the component, the sub-class should override this method
	 * @since 6.0.1
	 */
	protected ListModel<?> getComponentModel(Component comp){
		return null;
	}
	
	/**
	 * Handles the wrapped non-list-model, by default it return the original one. <p/> 
	 * The sub-class could override this method if it needs to do some post process on the wrapped model.
	 * @since 6.0.1
	 */
	protected BindingListModel<?> handleWrappedNonListModel(Component comp, BindingListModel<?> wrappedModel){
		return wrappedModel;
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
		} else if (val instanceof BindingListModelListModel) {
			return ((BindingListModelListModel) val).getInnerModel();
		} else {
			throw new UiException("Expects BindingListModelSet, BindingListModelList, BindingListModelMap, BindingListModelListModel or BindingListModel only."+val.getClass());
		}
	}
}
