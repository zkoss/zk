/* ListboxDrawerEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 4, 2008 11:34:16 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox.Renderer;

/**
 * This engine is used to draw the optimum UI of listbox to client side. 
 * @author jumperchen
 * @since 3.0.4
 */
/*package*/ class ListboxDrawerEngine implements java.io.Serializable, Cloneable {

	private Listbox _listbox;

	private int _curpos, _extent;

	private transient EventListener _scrollListener;

	/*package*/ ListboxDrawerEngine(Listbox listbox) {
		_listbox = listbox;
		initDataListener();
	}

	private void initDataListener() {
		if (_scrollListener == null)
			_scrollListener = new EventListener() {
				public void onEvent(Event event) {
					if (_listbox.inSpecialMold()) {
						String[] data = ((String)event.getData()).split(",");
						_curpos = Integer.parseInt(data[0]);
						_extent = Integer.parseInt(data[1]);
						if (_listbox.getListhead() != null)
							_listbox.setInnerWidth(data[2]);
						_listbox.setInnerHeight(data[3]);
						_listbox.setInnerTop(data[4]);
						_listbox.setInnerBottom(data[5]);
						int pgsz = getRenderAmount();

						final Renderer renderer = _listbox.new Renderer();
						try {
							for (final Iterator it = _listbox.getItems()
									.listIterator(getRenderBegin()); --pgsz >= 0
									&& it.hasNext();)
								renderer.render((Listitem) it.next());
						} catch (Throwable ex) {
							renderer.doCatch(ex);
						} finally {
							renderer.doFinally();
						}
					}
					_listbox.invalidate();
				}
			};
		_listbox.addEventListener("onRenderAtScroll", _scrollListener);
	}

	/*package*/ Set getAvailableAtClient() {
		if (!_listbox.inSpecialMold())
			return null;

		final Set avail = new LinkedHashSet(32);
		avail.addAll(_listbox.getHeads());
		if (_listbox.getListfoot() != null)
			avail.add(_listbox.getListfoot());
		int pgsz = getRenderAmount();
		int ofs = getRenderBegin();
		for (Iterator it = _listbox.getItems().listIterator(ofs); --pgsz >= 0
		&& it.hasNext();)
			avail.add(it.next());
		return avail;
	}

	/*package*/ void onInitRender() {
		final Renderer renderer = _listbox.new Renderer();
		try {
			if (_listbox.getRows() > 0) {
				_extent = _listbox.getRows();
			}
			int	pgsz = getRenderAmount();
			int ofs = getRenderBegin();

			int j = 0;
			for (Iterator it = _listbox.getItems().listIterator(ofs);
				j < pgsz && it.hasNext(); ++j)
				renderer.render((Listitem) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Returns the beginning number of the renderder.
	 */
	/*package*/ int getRenderBegin() {
		int begin = getCurpos() - _listbox.getPreloadSize();
		if (begin < 0) begin = 0;
		return begin;
	}
	
	/**
	 * Returns the current position of the scrollbar, which ranges from 0 to the value 
	 * of the {@link #getMaxpos()} attribute. The default value is 0.
	 */
	/*package*/ int getCurpos(){
		return _curpos;
	}
	
	/**
	 * Returns the ending number of the renderer.
	 * Note: it's inclusive.
	 */
	/*package*/ int getRenderEnd() {
		return getRenderAmount() + getRenderBegin() - 1;//inclusive
	}
	/*package*/ Iterator getVisibleChildrenIterator() {
		return new VisibleChildrenIterator();
	}
	/**
	 * An iterator used by visible children.
	 * @since 3.5.1
	 */
	private class VisibleChildrenIterator implements Iterator {
		private ListIterator _it = _listbox.getItems().listIterator(getRenderBegin());
		private int _end = getRenderEnd();
		private int _count;
		public boolean hasNext() {
			return _count < _end;
		}
		public Object next() {
			_count++;
			return _it.next();
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	/**
	 * Returns the renderer's extent.
	 */
	/*package*/ int getRenderAmount() {
		if (getCurpos() - _listbox.getPreloadSize() >= 0)
			return _extent + _listbox.getPreloadSize() * 2;
		return _extent + _listbox.getPreloadSize() + getCurpos();
	}
	
	/**
	 * Returns maximum position of the scrollbar. The default value is {@link Listbox#getItemCount()}
	 * - {@link #getVisibleAmount()}.
	 */
	/*package*/ int getMaxpos() {
		return _listbox.getItemCount() - getVisibleAmount();
	}
	
	/**
	 * Returns the scrollbar's extent, aka its "visibleAmount".
	 */
	/*package*/ int getVisibleAmount() {
		return _extent;
	}
	
	//Cloneable//
	public Object clone() {
		final ListboxDrawerEngine clone;
		try {
			clone = (ListboxDrawerEngine)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._scrollListener = null;
		clone.initDataListener();
		return clone;
	}
	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		initDataListener();
	}
	
}
