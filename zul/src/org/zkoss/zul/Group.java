/* Group.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 25, 2008 4:15:11 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.XulElement;

/**
 * Adds the ability for single level grouping to the Grid.
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
public class Group extends Row implements org.zkoss.zul.api.Group {
	private boolean _open = true;	
	private transient List _items;
	
	public Group() {
		init();
	}
	public Group(String label) {
		this();
		setLabel(label);
	}
	public Group(String label, Object value) {
		this();
		setLabel(label);
		setValue(value);
	}

	private void init() {
		_items =  new AbstractList() {
			public int size() {
				return getItemCount();
			}
			public Iterator iterator() {
				return new IterItems();
			}
			public Object get(int index) {
				final Rows rows = (Rows)getParent();
				if (rows != null) {
					int i = 0;
					for (Iterator it = rows.getChildren().listIterator(getIndex() + 1); 
						it.hasNext() && i <= index; i++) {
						if (i == index) 
							return it.next();
						it.next();	
					}
				}
				throw new IndexOutOfBoundsException("Index: "+index);
			}
		};
	}
	/** 
	 * Returns a list of all {@link Row} are grouped by this group.
	 */
	public List getItems() {
		return _items;
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		final Rows rows = (Rows)getParent();
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
	/**
	 * Returns the number of visible descendant {@link Row}.
	 * @since 3.5.1
	 */
	public int getVisibleItemCount() {
		int count = getItemCount();
		int visibleCount = 0;
		Row row = (Row) getNextSibling();
		while (count-- > 0) {
			if(row.isVisible())
				visibleCount++;
			row = (Row) row.getNextSibling();
		}
		return visibleCount;
	}
	/**
	 * Returns the index of Groupfoot
	 * <p> -1: no Groupfoot
	 */
	public int getGroupfootIndex(){
		final Rows rows = (Rows)getParent();
		if (rows != null) {
			int[] g = rows.getGroupsInfoAt(getIndex(), true);
			if (g != null) return g[2];
		}
		return -1;
	}

	/**
	 * Returns the Groupfoot, if any. Otherwise, null is returned.
	 */
	public Groupfoot getGroupfoot() {
		int index = getGroupfootIndex();
		if (index < 0) return null;
		final Rows rows = (Rows)getParent();
		return (Groupfoot) rows.getChildren().get(index);
	}
	/**
	 * Returns the Groupfoot, if any. Otherwise, null is returned.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Groupfoot getGroupfootApi() {
		return getGroupfoot();
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
			smartUpdate("z.open", _open);
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
		final StringBuffer sb = new StringBuffer(64)
			.append("<img src=\"")
			.append(getDesktop().getExecution().encodeURL("~./img/spacer.gif"))
			.append("\" class=\"").append(getZclass()).append("-img ")
			.append(getZclass()).append(isOpen() ? "-img-open" : "-img-close")
			.append("\" align=\"absmiddle\"/>");

		final String label = getLabel();
		if (label != null && label.length() > 0) sb.append(' ');

		return sb.toString(); //keep a space
	}
	/** Returns the value of the {@link Label} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Component cell = getFirstChild();
		return cell != null && cell instanceof Label ? ((Label)cell).getValue(): null;
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
		if (cell == null || cell instanceof Label) {
			if (cell == null) cell = new Label();
			cell.applyProperties();
			cell.setParent(this);
			return (Label)cell;
		}
		throw new UiException("Unsupported child for setLabel: "+cell);
	}

	public String getZclass() {
		return _zclass == null ? "z-group" : _zclass;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getName();
		if (name.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			final Rows rows = (Rows) getParent();
			if (rows != null)
				rows.addVisibleItemCount(_open ? getVisibleItemCount() : -getVisibleItemCount());
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	/**
	 * An iterator used by _items.
	 */
	private class IterItems implements Iterator {
		private final Iterator _it = getParent().getChildren().listIterator(getIndex()+1);
		private int _j;

		public boolean hasNext() {
			return _j < getItemCount();
		}
		public Object next() {
			++_j;
			return _it.next();
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		invalidate();
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}
	//Cloneable//
	public Object clone() {
		final Group clone = (Group)super.clone();
		clone.init();
		return clone;
	}
	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}
}
