/* AbstractListModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2011/12/12 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.ext.ListSelectionModel;
import org.zkoss.zul.impl.GroupsListModel;

/**
 * The abstract {@link Converter} implementation for converting collection to ListModel and vice versa.
 * @author dennis
 * @since 6.0.0
 */
/*package*/ abstract class AbstractListModelConverter implements Converter, Serializable{

	private static final long serialVersionUID = 201108171744L;
	
	
	/**
	 * @param comp the component that has listmodel
	 * @return null if no list model for the component
	 */
	abstract protected ListModel<?> getComponentModel(Component comp);
	
	/**
	 * post processing the wrapped model. default return original one
	 * @param ctx the context
	 * @param comp the component of the converter
	 * @param model the wrapped model
	 * @return the list model
	 */
	protected ListModel<?> handleWrappedModel(BindContext ctx, Component comp, ListModel<?> model){
		return model;
	}
	
	/** Convert a Set, Map, List, Object[], Enum, or other kind of ListModel to associated {@link ListModel}.
	 * @param val must be instanceof Set, Map, List, Object[], Enum Class, or other kind of ListModel implementation.
	 * @param comp associated component
	 * @param ctx bind context
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		if (val == null) {
			val = new ArrayList();
		}
		ListModel model = null;
		if (val instanceof ListModel) {
			comp.setAttribute(BinderImpl.MODEL, val); //ZK-758. @see AbstractRenderer#addItemReference
			return val;
		} else if (val instanceof Set) {
			model =  new ListModelSet((Set)val, true);
		} else if (val instanceof List) {
			model =  new ListModelList((List)val, true);
		} else if (val instanceof Map) {
			model =  new ListModelMap((Map)val, true);
		} else if (val instanceof Object[]) {
			model =  new ListModelArray((Object[]) val, true);
		} else if ((val instanceof Class) && Enum.class.isAssignableFrom((Class)val)) {
			model =  new ListModelArray((Object[]) ((Class)val).getEnumConstants(), true);
		} else if (val instanceof GroupsModel) { //feature#2866506: Data Binding shall support GroupsModel with Listbox/Grid
			model =  GroupsListModel.toListModel((GroupsModel) val);
		} else {
			throw new UiException("Expects java.util.Set, java.util.List, java.util.Map, Object[], Enum Class, GroupsModel, or ListModel only. "+val.getClass());
		}
		final Indexer indexer = new Indexer(model);
		
		final ListModel compModel = getComponentModel(comp);
		if(compModel instanceof ListSelectionModel){
			ListSelectionModel smodel = ((ListSelectionModel)compModel);
			ListSelectionModel toSModel = (ListSelectionModel) model;
			toSModel.setMultiple(smodel.isMultiple());
			if (!smodel.isSelectionEmpty()) {
				int maxsel = smodel.getMaxSelectionIndex();
				int sel;
				for(int index = smodel.getMinSelectionIndex();index <= maxsel;index++){
					//get the object from old model
					Object selval = compModel.getElementAt(index);
					if(selval!=null){
						//find the index of the object in new wrapped model
						sel = indexer.indexOf(selval);
						toSModel.addSelectionInterval(sel, sel);
					}
				}
			}
		}
		model = handleWrappedModel(ctx,comp,model);
		comp.setAttribute(BinderImpl.MODEL, model); //ZK-758. @see AbstractRenderer#addItemReference
		
		return model;
	}

	/** Convert a {@link ListModel} to Set, Map, List, or ListModel (itself).
	 * @param val must be ListModelSet, ListModelList, ListModelMap, or other kind of ListModel
	 * @param comp associated component
	 * @param ctx bind context
	 */
	@SuppressWarnings("rawtypes")
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		if (val == null) {
			throw new NullPointerException("val");
		}
		if (val instanceof ListModelSet) {
			return ((ListModelSet)val).getInnerSet();
		} else if (val instanceof ListModelList) {
			return ((ListModelList)val).getInnerList();
		} else if (val instanceof ListModelMap) {
			return ((ListModelMap)val).getInnerMap();
		} else if (val instanceof ListModelArray) {
			return ((ListModelArray)val).getInnerArray();
		} else if (val instanceof GroupsListModel){
			return ((GroupsListModel)val).getGroupsModel();
		}else if (val instanceof ListModel) {
			return val;
		} else {
			throw new UiException("Expects ListModelSet, ListModelList, ListModelMap, GroupsListModel or ListModel only."+val.getClass());
		}
	}
	
	//indexer to handle wrapped model
	private static class Indexer {
		private Object model;
		
		public Indexer(Object model){
			this.model = model;
		}
		@SuppressWarnings("rawtypes")
		public int indexOf(Object obj){
			if (model instanceof ListModelSet) {
				return ((ListModelSet)model).indexOf(obj);
			} else if (model instanceof ListModelList) {
				return ((ListModelList)model).indexOf(obj);
			} else if (model instanceof ListModelMap) {
				return ((ListModelMap)model).indexOf(obj);
			} else if (model instanceof ListModelArray) {
				return ((ListModelArray)model).indexOf(obj);
			} 
			return -1;
		}
	}

}
