/* ComboitemCollectionItem.java

	Purpose:
		
	Description:
		
	History:
		Jan 3, 2008 10:51:34 AM , Created by jumperchen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModel;

/**
 * @author jumperchen
 * @since 3.0.2
 */
/* package */ class ComboitemCollectionItem implements CollectionItemExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191454L;

	public Component getComponentCollectionOwner(Component comp) {
		if (comp instanceof Comboitem) {
			final Comboitem item = (Comboitem) comp;
			return item.getParent();
		} else {
			throw new UiException(
					"Unsupported type for ComboitemCollectionItem: " + comp);
		}
	}

	public ListModel getModelByOwner(Component comp) {
		if (comp instanceof Combobox) {
			final Combobox cbbox = (Combobox) comp;
			return cbbox.getModel();
		} else {
			throw new UiException(
					"Unsupported type for ComboitemCollectionItem: " + comp);
		}
	}

	public Component getComponentAtIndexByOwner(Component comp, int index) {
		if (comp instanceof Combobox) {
			//bug #2966241, since 5.0, combobox fire onChange then onSelect event(In 3.x it fire onSelect then onChange)
			//This causes comboitem label to be modified in onChange if binding "selected.value" in 
			//combobox "value" property and "selected" in "selectedItem" property simultaneously.
			//We thus kept the trigger component in execution attribute and 
			//ComboitemCollectionItems#getComponentAtIndexByOwner can decide whether to return
			//the Comboitem in such case.
			return !comp.equals(Executions.getCurrent().getAttribute(DataBinder.LOAD_ON_SAVE_TRIGGER_COMPONENT)) ?
				((Combobox) comp).getItemAtIndex(index) : null;
		} else {
			throw new UiException(
					"Unsupported type for ComboitemCollectionItem: " + comp);
		}
	}

	public void setupBindingRenderer(Component comp, DataBinder binder) {
		if (comp instanceof Comboitem) {
			final Comboitem li = (Comboitem) comp;
			final Combobox cbbox = (Combobox) li.getParent();
			if (cbbox.getItemRenderer() == null) {
				cbbox.setItemRenderer(new BindingComboitemRenderer(li, binder));
			}
		}
	}

	public List getItems(Component comp) {
		if (comp instanceof Combobox) {
			final Combobox cbbox = (Combobox) comp;
			return cbbox.getItems();
		} else {
			throw new UiException(
					"Unsupported type for ComboitemCollectionItem: " + comp);
		}
	}
}
