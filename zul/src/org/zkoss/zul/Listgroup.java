/* Listgroup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 23, 2008 10:34:35 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zul.impl.XulElement;

/**
 * Adds the ability for single level grouping to the Listbox.
 * 
 * <p>Event:
 * <ol>
 * 	<li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 * 
 * <p>Default {@link #getSclass}: listgroup.
 * @author jumperchen
 * @since 3.1.0
 */
public class Listgroup extends Listitem {
	private boolean _open = true;
	private transient List _items;
	public Listgroup() {
		setSclass("listgroup");
		init();
	}
	public Listgroup(String label) {
		this();
		setLabel(label);
	}
	public Listgroup(String label, Object value) {
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
				final Listbox lb = getListbox();
				if (lb == null) return null;
				return lb.getItemAtIndex(getIndex() + index + 1);
			}
		};
	}
	private void applyImageIfAny() {
		if (getFirstChild() != null) {
			final Listcell lc = (Listcell)getFirstChild();
			if (lc.getImage() == null)
			lc.setImageDirectly(isOpen() ? "~./zul/img/tree/open.png" : "~./zul/img/tree/close.png");
		}
	}
	/** 
	 * Returns a list of all {@link Listitem} are grouped by this listgroup.
	 */
	public List getItems() {
		return _items;
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		final Listbox lb = getListbox();
		if (lb != null) {
			int[] g = lb.getGroupsInfoAtIndex(getIndex(), true);
			if (g != null) return g[1] - 1;
		}
		return 0;
	}
	protected void setIndex(int index) {
		final int old = getIndex();
		super.setIndex(index);
		final Listbox lb = getListbox();
		if (lb != null) lb.fixGroupIndex(old, index);
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
		}
	}
	public String getOuterAttrs() {
		applyImageIfAny();
		final StringBuffer sb = new StringBuffer(64).append( super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.open", isOpen());
		appendAsapAttr(sb, Events.ON_OPEN);
		return sb.toString();
	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Openable {
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;
		}
	}
	/**
	 * An iterator used by _items.
	 */
	private class IterItems implements Iterator {
		private final Iterator _it = getListbox().getItems().listIterator(getIndex()+1);
		private int _j;

		public boolean hasNext() {
			return _j < getItemCount();
		}
		public Object next() {
			final Object o = _it.next();
			++_j;
			return o;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
