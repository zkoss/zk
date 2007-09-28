/* Columns.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jun 22, 2007 2:59:03 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.grid;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Selectable;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.yuiext.ColumnLockChangeCommand;
import org.zkoss.yuiext.grid.Column;
import org.zkoss.yuiext.grid.Grid;
import org.zkoss.zul.impl.HeadersElement;
import org.zkoss.zul.impl.XulElement;

/**
 * Defines the columns of a grid. Each child of a columns element should be a
 * {@link Column} element.
 * <p>
 * Default: sizable=ture.
 * 
 *  * <p>Events:<br/>
 * onColumnLockChange.
 * 
 * @author jumperchen
 * 
 */
public class Columns extends HeadersElement {
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;

	public Columns() {
		setSizable(true);
	}

	// -- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: " + parent);
		final boolean changed = getParent() != parent;
		super.setParent(parent);
		if (changed && parent != null)
			((Grid) parent).initAtClient();
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: " + child);
		if (super.insertBefore(child, insertBefore)) {
			final Component parent = getParent();
			try {
				checkCondition();
			} catch (UiException e) {
				getChildren().remove(child);
				throw e;
			} finally {
				if (!_noSmartUpdate && parent != null)
					parent.invalidate();

			}
			// FUTURE: optimize the invalidation to attributes of
			// certain cells
			// It implies initAtClient
			return true;
		}
		return false;
	}

	public boolean removeChild(Component child) {
		if (super.removeChild(child)) {
			final Component parent = getParent();
			if (parent != null)
				parent.invalidate();
			// FUTURE: optimize the invalidation to attributes of
			// certain cells
			// It implies initAtClient
			return true;
		}
		return false;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		appendAsapAttr(sb, "onColumnLockChange");
		return sb.toString();
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/* package */final void setColumnLockable(Column col, boolean lockable) {
		if (col.getParent() != this)
			throw new UiException("Not a child: " + col);
		col.setLockableDirectly(lockable);
	}

	protected class ExtraCtrl extends XulElement.ExtraCtrl implements
			Selectable, Updatable {
		public void selectItemsByClient(Set lockedItems) {
			int j = 0;
			for (Iterator it = getChildren().iterator(); it.hasNext(); ++j) {
				final Column col = (Column) it.next();
				setColumnLockable(col, lockedItems.contains(col));
			}
		}

		public void setResult(Object result) {
			_noSmartUpdate = ((Boolean) result).booleanValue();
		}
	}

	/* package */final boolean isSmartUpdate() {
		return !_noSmartUpdate;
	}

	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		if (!_noSmartUpdate) {
			super.addMoved(oldparent, oldpg, newpg);
		}
	}

	public void redraw(Writer out) throws IOException {
		if (getChildren().isEmpty())
			throw new UiException(
					"The Column component is necessay for grid according to the specification of Ext JS!");
		super.redraw(out);
	}

	private void checkCondition() {
		List list = getChildren();
		for (int i = list.size() - 1; i >= 0; i--) {
			boolean lockable = ((Column) list.get(i)).isLockable();
			if (lockable && i == list.size() - 1) {
				if (lockable && --i >= 0
						&& !((Column) list.get(i)).isLockable()) {
					throw new UiException(
							"Unsupported condition : The pervious child is unlocked!");
				} else {
					throw new UiException(
							"Unsupported condition : All children is locked!");
				}
			} else {
				if (lockable && --i >= 0
						&& !((Column) list.get(i)).isLockable())
					throw new UiException(
							"Unsupported condition : The pervious child is unlocked!");
			}
		}
	}

	static {
		new ColumnLockChangeCommand("onColumnLockChange", 0);
	}
}
