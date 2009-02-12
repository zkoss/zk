/* RowCollectionItem.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 31, 2007 3:24:34 PM , Created by jumperchen
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Row;

/* package */class RowCollectionItem implements CollectionItemExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191436L;

	public Component getComponentAtIndexByOwner(Component comp, int index) {
		return (Component) getItems(comp).get(index);
	}

	public Component getComponentCollectionOwner(Component comp) {
		if (comp instanceof Row) {
			final Row row = (Row) comp;
			return row.getGrid();
		} else {
			throw new UiException("Unsupported type for RowCollectionItem: "
					+ comp);
		}
	}

	public ListModel getModelByOwner(Component comp) {
		if (comp instanceof Grid) {
			final Grid grid = (Grid) comp;
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
	
	public List getItems(Component comp) {
		if (comp instanceof Grid) {
			final Grid grid = (Grid) comp;
			return grid.getRows().getChildren();
		} else {
			throw new UiException("Unsupported type for RowCollectionItem: "
					+ comp);
		}
	}
}
