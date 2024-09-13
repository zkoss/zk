/* Group.java

	Purpose:

	Description:

	History:
		Apr 25, 2008 4:15:11 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.image.Images;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.XulElement;

/**
 * Adds the ability for single level grouping to the Grid.
 * <p>Available in ZK PE and EE.
 *
 * <p>Event:
 * <ol>
 * 	<li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 *
 * <p>Default {@link #getZclass}: z-group.
 *
 * <p>Note: All the child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * @author jumperchen
 * @since 3.5.0
 */
public class Group extends Row {
	private boolean _open = true;
	private transient List<Row> _items;

	static {
		addClientEvent(Group.class, Events.ON_OPEN, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
	}

	public Group() {
		init();
	}

	public Group(String label) {
		this();
		setLabel(label);
	}

	public <T> Group(String label, T value) {
		this();
		setLabel(label);
		setValue(value);
	}

	private void init() {
		_items = new AbstractList<Row>() {
			public int size() {
				return getItemCount();
			}

			public Iterator<Row> iterator() {
				return new IterItems();
			}

			public Row get(int index) {
				final Rows rows = (Rows) getParent();
				if (rows != null) {
					int i = 0;
					for (Iterator<Component> it = rows.getChildren().listIterator(getIndex() + 1); it.hasNext()
							&& i <= index; i++) {
						if (i == index)
							return (Row) it.next();
						it.next();
					}
				}
				throw new IndexOutOfBoundsException("Index: " + index);
			}
		};
	}

	/**
	 * Returns a list of all {@link Row} are grouped by this group.
	 */
	public List<Row> getItems() {
		return _items;
	}

	/** Returns the number of items.
	 */
	public int getItemCount() {
		final Rows rows = (Rows) getParent();
		if (rows != null) {
			int[] g = rows.getGroupsInfoAt(getIndex(), true);
			if (g != null) {
				if (g[2] == -1)
					return g[1] - 1;
				else
					return g[1] - 2;
			}

		}
		return 0;
	}

	public Group getGroup() {
		return this;
	}

	/**
	 * Returns the number of visible descendant {@link Row}.
	 * @since 3.5.1
	 */
	public int getVisibleItemCount() {
		int count = getItemCount();
		int visibleCount = 0;
		Row row = (Row) getNextSibling();
		while (count-- > 0 && row != null) {
			if (row.isVisible())
				visibleCount++;
			row = (Row) row.getNextSibling();
		}
		return visibleCount;
	}

	/**
	 * Returns the index of Groupfoot
	 * <p> -1: no Groupfoot
	 */
	public int getGroupfootIndex() {
		final Rows rows = (Rows) getParent();
		if (rows != null) {
			int[] g = rows.getGroupsInfoAt(getIndex(), true);
			if (g != null)
				return g[2];
		}
		return -1;
	}

	/**
	 * Returns the Groupfoot, if any. Otherwise, null is returned.
	 */
	public Groupfoot getGroupfoot() {
		int index = getGroupfootIndex();
		if (index < 0)
			return null;
		final Rows rows = (Rows) getParent();
		return (Groupfoot) rows.getChildren().get(index);
	}

	/** Returns whether this container is open.
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}

	/** Sets whether this container is open.
	 *
	 * <p>Note: if you use a model as the data to render, don't use setOpen(). It'll tangle the lifecycle with model
	 * You should control the model directly.
	 * For example, you can use setClose() of GroupsModelArray
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("open", _open);
			final Rows rows = (Rows) getParent();
			if (rows != null)
				rows.addVisibleItemCount(isOpen() ? getVisibleItemCount() : -getVisibleItemCount());
		}
	}

	/** Returns the HTML IMG tag for the image part, or null
	 * if no image is assigned.
	 *
	 * <p>Used only for component template, not for application developers.
	 *
	 */
	public String getImgTag() {
		final StringBuffer sb = new StringBuffer(64).append("<img src=\"")
				.append(Images.BASE64SPACERIMAGE).append("\" class=\"")
				.append(getZclass()).append("-img ").append(getZclass()).append(isOpen() ? "-img-open" : "-img-close")
				.append("\" align=\"absmiddle\"/>");

		final String label = getLabel();
		if (label != null && label.length() > 0)
			sb.append(' ');

		return sb.toString(); //keep a space
	}

	/** Returns the value of the {@link Label} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Component cell = getFirstChild();
		if (cell != null) {
			if (cell instanceof Label)
				return ((Label) cell).getValue();
			else if (cell instanceof Cell) {
				final Component child = cell.getFirstChild();
				return child instanceof Label ? ((Label) child).getValue() : null;
			}
		}
		return null;
	}

	/** Sets the value of the {@link Label} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setValue(label);
	}

	private Label autoFirstCell() {
		Component cell = getFirstChild();
		if (cell == null || cell instanceof Label || cell instanceof Cell) {
			if (cell == null)
				cell = new Label();
			if (cell instanceof Cell) {
				final Component child = cell.getFirstChild();
				if (child != null && child instanceof Label)
					cell = child;
				else
					cell = new Label();
			}
			cell.applyProperties();
			cell.setParent(this);
			return (Label) cell;
		}
		throw new UiException("Unsupported child for setLabel: " + cell);
	}

	public String getZclass() {
		return _zclass == null ? "z-group" : _zclass;
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (!isOpen())
			renderer.render("open", false);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();

			// Fix B70-ZK-2812.zul, that bind_clean should run after normal AU event.
			Events.postEvent(evt);

			final Rows rows = (Rows) getParent();
			if (rows != null) {
				rows.addVisibleItemCount(_open ? getVisibleItemCount() : -getVisibleItemCount());
				final Grid grid = getGrid();
				if (grid != null) {
					final ListModel model = grid.getModel();
					if (model instanceof GroupsListModel) {
						int gindex = rows.getGroupIndex(grid.getDataLoader().getOffset() + getIndex());
						GroupsListModel groupsListModel = (GroupsListModel) model;
						GroupsModel gmodel = groupsListModel.getGroupsModel();
						int offset = grid.getDataLoader().getOffset();
						if (offset > 0) {
							List groupsInfos = groupsListModel.getGroupsInfos();
							gindex += getGroupIndex(groupsInfos, offset) + 1;
						}
						if (_open)
							gmodel.addOpenGroup(gindex);
						else
							gmodel.removeOpenGroup(gindex);
					}
				}
			}
		} else
			super.service(request, everError);
	}

	private static int getGroupIndex(List<int[]> groupInfo, int index) {
		int j = 0, gindex = -1;
		int[] g = null;
		for (Iterator<int[]> it = groupInfo.iterator(); it.hasNext(); ++j) {
			g = it.next();
			if (index == g[0])
				gindex = j;
			else if (index < g[0])
				break;
		}
		return gindex != -1 ? gindex
				: g != null && index < (g[0] + g[1]) ? (j - 1)
						: g != null && index == (g[0] + g[1]) && g[2] == -1 ? (j - 1) : gindex;
	}

	/**
	 * An iterator used by _items.
	 */
	private class IterItems implements Iterator<Row> {
		private final Iterator<Component> _it = getParent().getChildren().listIterator(getIndex() + 1);
		private int _j;

		public boolean hasNext() {
			return _j < getItemCount();
		}

		public Row next() {
			++_j;
			return (Row) _it.next();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	//Cloneable//
	public Object clone() {
		final Group clone = (Group) super.clone();
		clone.init();
		return clone;
	}

	//-- Serializable --//
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}
}
