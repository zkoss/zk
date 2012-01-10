/* ListboxModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2011/12/12 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.ListSelectionModel;

/**
 * The {@link Converter} implementation of the listbox for converting collection to ListModel and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class ListboxModelConverter extends AbstractModelConverter{
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
		final Binder binder = ctx.getBinder();
		if(model instanceof ListSelectionModel && ((BinderCtrl)binder).hasPropertyLoadBinding(comp, "selectedItems")){
			((ListSelectionModel)model).setMultiple(true);
		}
		return model;
	}
}
