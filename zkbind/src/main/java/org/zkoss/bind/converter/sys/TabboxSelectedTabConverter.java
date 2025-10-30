/* TabboxSelectedTabConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Nov 13 14:39:14     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.bind.converter.sys;

import java.util.Iterator;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert tabbox selected tab and vice versa.
 *
 * @author Dennis
 * @since 6.0.0
 */
public class TabboxSelectedTabConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 200808190445L;

	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Tabbox tbx = (Tabbox) comp;
		final ListModel<?> model = tbx.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
		if (model != null && !(model instanceof Selectable)) {
			//model has to implement Selectable if binding to selectedItem
			throw new UiException("model doesn't implement Selectable");
		}

		if (val != null) {
			if (model != null) {
				((Selectable<Object>) model).addToSelection(val);
				return IGNORED_VALUE;
			} else {
				//no model case, //iterate to find the selected tab via the value
				Tabs tabs = ((Tabbox) comp).getTabs();
				if (tabs != null) {
					for (Iterator<Component> it = tabs.getChildren().iterator(); it.hasNext();) {
						final Component child = it.next();
						if (child instanceof Tab) {
							if (val.equals(((Tab) child).getLabel())) {
								return child;
							}
						}
					}
				}
			}
			//not in the item list
		}

		//nothing matched, clean the old selection
		if (model != null) {
			Set<Object> sels = ((Selectable<Object>) model).getSelection();
			if (sels != null && sels.size() > 0)
				((Selectable<Object>) model).clearSelection();
			return IGNORED_VALUE;
		}
		return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		if (val != null) {
			final Tabbox tbx = (Tabbox) comp;
			final ListModel<?> model = tbx.getModel();
			if (model != null && !(model instanceof Selectable)) {
				throw new UiException("model doesn't implement Selectable");
			}
			if (model != null) {
				Set<?> selection = ((Selectable<?>) model).getSelection();
				if (selection == null || selection.size() == 0)
					return null;
				return selection.iterator().next();
			} else { //no model
				return ((Tab) val).getLabel();
			}
		}
		return null;
	}
}
