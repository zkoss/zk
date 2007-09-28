/* BindingRowDecorator.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 31, 2007 5:00:21 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkforge.yuiext.zkplus.databind;

import org.zkforge.yuiext.grid.Grid;
import org.zkforge.yuiext.grid.Row;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zkplus.databind.CollectionItem;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.ListModel;

/* package */class RowCollectionItem implements CollectionItem {

	public Component getComponentAtIndexByOwner(Component comp, int index) {
		if (comp instanceof Grid) {
			Grid grid = (Grid) comp;
			return (Component) grid.getRows().getChildren().get(index);
		} else {
			throw new UiException("Unsupported type for RowCollectionItem: "
					+ comp);
		}
	}

	public Component getComponentCollectionOwner(Component comp) {
		if (comp instanceof Row) {
			Row row = (Row) comp;
			return row.getGrid();
		} else {
			throw new UiException("Unsupported type for RowCollectionItem: "
					+ comp);
		}
	}

	public ListModel getModelByOwner(Component comp) {
		if (comp instanceof Grid) {
			Grid grid = (Grid) comp;
			return grid.getModel();
		} else {
			throw new UiException("Unsupported type for RowCollectionItem: "
					+ comp);
		}
	}

	public void setupBindingRenderer(Component comp, DataBinder binder) {
		if (comp instanceof Row) {
			final Row row = (Row) comp;
			final Grid grid = row.getGrid();
			if (grid.getRowRenderer() == null) {
				grid.setRowRenderer(new BindingRowRenderer(row, binder));
			}
		}
	}
}
