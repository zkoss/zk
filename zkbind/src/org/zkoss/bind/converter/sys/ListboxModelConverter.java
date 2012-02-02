/* ListboxModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2011/12/12 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.Selectable;

/**
 * The {@link Converter} implementation of the listbox for converting collection to ListModel and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class ListboxModelConverter extends AbstractListModelConverter{
	private static final long serialVersionUID = 1463169907348730644L;
	@Override
	protected ListModel<?> getComponentModel(Component comp) {
		if(!(comp instanceof Listbox)){
			throw new IllegalArgumentException("not a listbox, is "+comp);
		}
		return ((Listbox)comp).getListModel();
	}
	
	@Override
	protected ListModel<?> handleWrappedModel(BindContext ctx, Component comp, ListModel<?> model){
		if(!(comp instanceof Listbox)){
			throw new IllegalArgumentException("not a listbox, is "+comp);
		}
		final Listbox listbox = (Listbox)comp;
		if(model instanceof Selectable){
			if(((Selectable<?>)model).isMultiple() != listbox.isMultiple());
			//since the model was wrapped. I should respect the setting on the component
			//user might set the multiple on the listbox by <listbox multiple="true" 
			//or <listbox multiple="@bind(true)" or <listbox multiple="@bind(vm.multiple)"
			((Selectable<?>)model).setMultiple(listbox.isMultiple());
		}
		return model;
	}
}
