/* CollectionItem.java


 Purpose:
 
 Description:
 
 History:
 Jul 31, 2007 2:51:58 PM , Created by jumperchen


 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;

/**
 * The <i>CollectionItem</i> is used by {@link DataBinder} and provides an
 * interface for collection component to interact with the <i>DataBinder</i>
 * such as <i>Grid</i> or <i>Listbox</i>.
 * 
 * @author jumperchen
 * @see DataBinder
 * @since 3.0.0
 */
public interface CollectionItem {
	/**
	 * <p>
	 * Returns the component's owner.
	 * </p>
	 * For example: if this comp is a <i>Row</i> component then this method will
	 * return the associated <i>Grid</i> component of the Row.
	 * 
	 * @param comp A component as <i>Row</i> or <i>Listitem</i>.
	 * @return Component the comp's owner.
	 */
	public Component getComponentCollectionOwner(Component comp);

	/**
	 * <p>
	 * Returns the component by the index in the comp's children.
	 * </p>
	 * 
	 * @param comp Collection owner component such as <i>Grid</i>.
	 * @param index index of the element to return
	 * @return Component the component at the specified position in the list of
	 *         comp's children.
	 */
	public Component getComponentAtIndexByOwner(Component comp, int index);

	/**
	 * <p>
	 * Returns the component model as {@link ListModel}
	 * </p>
	 * 
	 * @param comp Collection owner component such as <i>Grid</i>.
	 * @return ListModel
	 */
	public ListModel getModelByOwner(Component comp);

	/**
	 * <p>
	 * Sets the binding renderer for the template component such as <i>Listitem</i>
	 * or <i>Row</i>.
	 * </p>
	 * 
	 * @param comp A component such as <i>Row</i> or <i>Listitem</i>.
	 * @param binder The associated DataBinder
	 */
	public void setupBindingRenderer(Component comp, DataBinder binder);
}
