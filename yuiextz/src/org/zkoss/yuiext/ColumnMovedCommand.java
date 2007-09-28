/* ColumnMovedCommand.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed July 4 11:41:32     2007, Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext;

import java.util.Iterator;

import org.zkoss.yuiext.event.ColumnMovedEvent;
import org.zkoss.yuiext.grid.Column;
import org.zkoss.yuiext.grid.Columns;
import org.zkoss.yuiext.grid.Grid;
import org.zkoss.yuiext.grid.Row;
import org.zkoss.yuiext.grid.Rows;
import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link ColumnMovedEvent}
 * relevant command.
 * 
 * @author jumperchen
 */
public class ColumnMovedCommand extends Command {
	public ColumnMovedCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	// -- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);

		final String[] data = request.getData();
		if (data == null || data.length != 3)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[] {
					Objects.toString(data), this });
		final Grid grid = (Grid) comp;
		final Columns cols = grid.getColumns();
		final Rows rows = grid.getRows();
		final Desktop desktop = request.getDesktop();
		final Column col = (Column) desktop.getComponentByUuid(data[0]);
		final int oldIndex = Integer.parseInt(data[1]);
		final int newIndex = Integer.parseInt(data[2]);
		try {
			((Updatable) (cols).getExtraCtrl()).setResult(Boolean.TRUE);
			if (newIndex == cols.getChildren().size() - 1) {
				cols.insertBefore(col, null);
			} else {
				cols.insertBefore(col, (Column) cols.getChildren().get(
						oldIndex < newIndex ? newIndex + 1 : newIndex));
			}
		} finally {
			((Updatable) (cols).getExtraCtrl()).setResult(Boolean.FALSE);
		}
		for (Iterator it = rows.getChildren().iterator(); it.hasNext();) {
			final Row row = (Row) it.next();
			if (row.getChildren().size() != cols.getChildren().size())
				throw new UiException(
						"Unsupported condition: The row size is not equal to the size of top row. ");
			try {
				((Updatable) (row).getExtraCtrl()).setResult(Boolean.TRUE);
				Component oldCmp = null, newCmp = null;
				if (row.getChildren().size() - 1 > newIndex)
					newCmp = (Component) row.getChildren().get(
							oldIndex < newIndex ? newIndex + 1 : newIndex);
				if (row.getChildren().size() > oldIndex) {
					oldCmp = (Component) row.getChildren().get(oldIndex);
				}
				row.insertBefore(oldCmp, newCmp);
			} finally {
				((Updatable) (row).getExtraCtrl()).setResult(Boolean.FALSE);
			}
		}
		Events.postEvent(new ColumnMovedEvent(getId(), comp, col, Integer
				.parseInt(data[1]), Integer.parseInt(data[2])));
	}
}
