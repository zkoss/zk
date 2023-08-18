/* Listgroup.java

	Purpose:
		
	Description:
		
	History:
		Apr 23, 2008 10:34:35 AM , Created by jumperchen

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

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.XulElement;

/**
 * Adds the ability for single level grouping to the Listbox.
 * <p>Available in ZK PE and EE.
 * 
 * <p>Event:
 * <ol>
 * 	<li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 * 
 * <p>Default {@link #getZclass}: z-listgroup (since 5.0.0)
 * @author jumperchen
 * @since 3.5.0
 */
public class Listgroup extends Listitem {
	private boolean _open = true;
	private transient List<Listitem> _items;

	static {
		addClientEvent(Listgroup.class, Events.ON_OPEN, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
	}

	public Listgroup() {
		init();
	}

	public Listgroup(String label) {
		this();
		setLabel(label);
	}

	public <T> Listgroup(String label, T value) {
		this();
		setLabel(label);
		setValue(value);
	}

	private void init() {
		_items = new AbstractList<Listitem>() {
			public int size() {
				return getItemCount();
			}

			public Iterator<Listitem> iterator() {
				return new IterItems();
			}

			public Listitem get(int index) {
				final Listbox lb = getListbox();
				if (lb == null)
					throw new IndexOutOfBoundsException("Index: " + index);
				return lb.getItemAtIndex(getIndex() + index + 1);
			}
		};
	}

	/** 
	 * Returns a list of all {@link Listitem} are grouped by this listgroup.
	 */
	public List<Listitem> getItems() {
		return _items;
	}

	/** Returns the number of items.
	 */
	public int getItemCount() {
		final Listbox lb = getListbox();
		if (lb != null) {
			int[] g = lb.getGroupsInfoAt(getIndex(), true);
			if (g != null) {
				if (g[2] == -1)
					return g[1] - 1;
				else
					return g[1] - 2;
			}
		}
		return 0;
	}

	public Listgroup getListgroup() {
		return this;
	}

	/**
	 * Returns the number of visible descendant {@link Listitem}.
	 * @since 3.5.1
	 */
	public int getVisibleItemCount() {
		int count = getItemCount();
		int visibleCount = 0;
		if (getNextSibling() instanceof Listitem) {
			Listitem item = (Listitem) getNextSibling();
			while (count-- > 0 && item != null) {
				if (item.isVisible())
					visibleCount++;
				if (!(item.getNextSibling() instanceof Listitem))
					break;
				item = (Listitem) item.getNextSibling();
			}
		}
		return visibleCount;
	}

	/**
	 * Returns the index of Listgroupfoot
	 * <p> -1: no Listgroupfoot
	 */
	public int getListgroupfootIndex() {
		final Listbox lb = (Listbox) getParent();
		if (lb != null) {
			int[] g = lb.getGroupsInfoAt(getIndex(), true);
			if (g != null)
				return g[2];
		}
		return -1;
	}

	/**
	 * Returns the Listfoot, if any. Otherwise, null is returned.
	 */
	public Listfoot getListfoot() {
		int index = getListgroupfootIndex();
		if (index < 0)
			return null;
		final Listbox lb = (Listbox) getParent();
		return (Listfoot) lb.getChildren().get(index);
	}

	/** Returns whether this container is open.
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}

	/** Sets whether this container is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("open", _open);
			final Listbox listbox = getListbox();
			if (listbox != null)
				listbox.addVisibleItemCount(isOpen() ? getVisibleItemCount() : -getVisibleItemCount());
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-listgroup" : _zclass;
	}

	//Cloneable//
	public Object clone() {
		final Listgroup clone = (Listgroup) super.clone();
		clone.init();
		return clone;
	}

	//-- Serializable --//
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}

	//-- ComponentCtrl --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (!isOpen())
			renderer.render("open", false);
	}

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

			final Listbox listbox = getListbox();
			if (listbox != null) {
				listbox.addVisibleItemCount(_open ? getVisibleItemCount() : -getVisibleItemCount());
				final ListModel model = listbox.getModel();
				if (model instanceof GroupsListModel) {
					int gindex = listbox.getGroupIndex(getIndex());
					GroupsModel gmodel = ((GroupsListModel) model).getGroupsModel();
					if (_open)
						gmodel.addOpenGroup(gindex);
					else
						gmodel.removeOpenGroup(gindex);
				}
			}
		} else
			super.service(request, everError);
	}

	/**
	 * An iterator used by _items.
	 */
	private class IterItems implements Iterator<Listitem> {
		private final Iterator<Listitem> _it = getListbox().getItems().listIterator(getIndex() + 1);
		private int _j;

		public boolean hasNext() {
			return _j < getItemCount();
		}

		public Listitem next() {
			++_j;
			return _it.next();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
