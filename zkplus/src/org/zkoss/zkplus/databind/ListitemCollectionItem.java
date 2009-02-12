/* ListitemCollectionItem.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 31, 2007 3:13:52 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkplus.databind;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/* package */class ListitemCollectionItem implements CollectionItemExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191434L;

	public Component getComponentCollectionOwner(Component comp) {
		if (comp instanceof Listitem) {
			final Listitem item = (Listitem) comp;
			return item.getListbox();
		} else {
			throw new UiException(
					"Unsupported type for ListitemCollectionItem: " + comp);
		}
	}

	public ListModel getModelByOwner(Component comp) {
		if (comp instanceof Listbox) {
			final Listbox listbox = (Listbox) comp;
			return listbox.getModel();
		} else {
			throw new UiException(
					"Unsupported type for ListitemCollectionItem: " + comp);
		}
	}

	public Component getComponentAtIndexByOwner(Component comp, int index) {
		if (comp instanceof Listbox) {
			final Listbox listbox = (Listbox) comp;
			return listbox.getItemAtIndex(index);
		} else {
			throw new UiException(
					"Unsupported type for ListitemCollectionItem: " + comp);
		}
	}

	public void setupBindingRenderer(Component comp, DataBinder binder) {
		if (comp instanceof Listitem) {
			final Listitem li = (Listitem) comp;
			final Listbox lbx = li.getListbox();
			if (lbx.getItemRenderer() == null) {
				lbx.setItemRenderer(new BindingListitemRenderer(li, binder));
			}
		}
	}

	public List getItems(Component comp) {
		if (comp instanceof Listbox) {
			final Listbox listbox = (Listbox) comp;
			return listbox.getItems();
		} else {
			throw new UiException(
					"Unsupported type for ListitemCollectionItem: " + comp);
		}
	}

}
