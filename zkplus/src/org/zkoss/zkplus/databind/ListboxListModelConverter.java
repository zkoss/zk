/* ListboxListmodelConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/3/8 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.Selectable;

/**
 * The {@link TypeConverter} implementation for converting collection to ListModel of a listbox
 * @author dennis
 * @since 6.0.1
 */
public class ListboxListModelConverter extends ListModelConverter{

	private static final long serialVersionUID = 1L;

	@Override
	protected ListModel<?> getComponentModel(Component comp){
		return ((Listbox)comp).getModel();
	}
	
	@Override
	protected BindingListModel<?> handleWrappedNonListModel(Component comp, BindingListModel<?> wrappedModel){
		final Listbox listbox = (Listbox)comp;
		if(wrappedModel instanceof Selectable){
			if(((Selectable<?>)wrappedModel).isMultiple() != listbox.isMultiple());
			//since the model was wrapped. I should respect the setting on the component
			//user might set the multiple on the listbox by <listbox multiple="true" 
			//or <listbox multiple="@bind(true)" or <listbox multiple="@bind(vm.multiple)"
			((Selectable<?>)wrappedModel).setMultiple(listbox.isMultiple());
		}
		return wrappedModel;
	}
}
