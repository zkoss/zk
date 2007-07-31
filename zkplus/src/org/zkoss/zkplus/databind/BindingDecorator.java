/* BindingDecorator.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 31, 2007 2:51:58 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;

/**
 * The <i>BindingDecorator</i> is used by {@link DataBinder} and provides a
 * better way to develop a collection component for supporting <i>DataBinding</i>.
 * Such as <i>Grid</i> or <i>Listbox</i>.
 * 
 * @author jumperchen
 * @see DataBinder
 */
public interface BindingDecorator {
	/**
	 * <p>
	 * Returns the comp's owner.
	 * </p>
	 * For example: we assume that this comp is a <i>Row</i> component. It will
	 * return the <i>Grid</i> component.
	 * 
	 * @param comp A component as <i>Row</i> or <i>Listitem</i>.
	 * @return Component the comp's owner.
	 */
	public Component getComponentCollectionOwner(Component comp);

	/**
	 * <p>
	 * Returns the component by the index in the comp's children.
	 * </p>
	 * @param comp Owner of a component as <i>Grid</i>.
	 * @param index index of the element to return
	 * @return Component the component at the specified position in the list of comp's children.
	 */
	public Component getComponentAtIndexByOwner(Component comp, int index);

	/**
	 * <p>Returns the component model as {@link ListModel}</p>
	 * @param comp Owner of a component as <i>Grid</i>.
	 * @return ListModel
	 */
	public ListModel getModelByOwner(Component comp);

	/**
	 * <p>Sets the binding renderer for the template component as <i>listitem</i> or <i>row</i>.</p>
	 * @param comp A component as <i>Row</i> or <i>Listitem</i>.
	 * @param binder
	 */
	public void setupBindingRenderer(Component comp, DataBinder binder);
}
