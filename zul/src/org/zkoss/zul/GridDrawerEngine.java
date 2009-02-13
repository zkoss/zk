/* GridDrawerEngine.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Mar 4, 2008 12:37:47 PM , Created by jumperchen
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
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Grid.Renderer;

/**
 * This engine is used to draw the optimum UI of grid to client side.
 * 
 * @author jumperchen
 * @since 3.0.4
 */
/* package */class GridDrawerEngine {

	private Grid _grid;

	private int _curpos, _extent;

	private transient EventListener _scrollListener;

	/* package */GridDrawerEngine(Grid grid) {
		_grid = grid;
		initDataListener();
	}

	private void initDataListener() {
		if (_scrollListener == null)
			_scrollListener = new EventListener() {
				public void onEvent(Event event) {
					if (_grid.getRows() != null && _grid.getModel() != null && 
							_grid.inSpecialMold()) {
						String[] data = ((String) event.getData()).split(",");
						_curpos = Integer.parseInt(data[0]);
						_extent = Integer.parseInt(data[1]);
						if (_grid.getColumns() != null)
							_grid.setInnerWidth(data[2]);
						_grid.setInnerHeight(data[3]);
						_grid.setInnerTop(data[4]);
						_grid.setInnerBottom(data[5]);
						int pgsz = getRenderAmount();

						final Renderer renderer = _grid.new Renderer();
						try {
							for (final Iterator it = _grid.getRows()
									.getChildren().listIterator(
											getRenderBegin()); --pgsz >= 0
									&& it.hasNext();)
								renderer.render((Row) it.next());
						} catch (Throwable ex) {
							renderer.doCatch(ex);
						} finally {
							renderer.doFinally();
						}
					}
					if (_grid.getRows() != null)
						_grid.getRows().invalidate();
				}
			};
		_grid.addEventListener("onRenderAtScroll", _scrollListener);
	}

	/* package */Set getAvailableAtClient() {
		if (!_grid.inSpecialMold())
			return null;

		final Set avail = new LinkedHashSet(32);
		avail.addAll(_grid.getHeads());
		int pgsz = getRenderAmount();
		int ofs = getRenderBegin();
		for (Iterator it = _grid.getRows().getChildren().listIterator(ofs); --pgsz >= 0
				&& it.hasNext();)
			avail.add(it.next());
		return avail;
	}

	/* package */void onInitRender() {
		final Renderer renderer = _grid.new Renderer();
		try {
			int pgsz = getRenderAmount();
			int ofs = getRenderBegin();

			int j = 0;
			for (Iterator it = _grid.getRows().getChildren().listIterator(ofs); j < pgsz
					&& it.hasNext(); ++j)
				renderer.render((Row) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Returns the beginning number of the renderder.
	 */
	/* package */int getRenderBegin() {
		int begin = getCurpos() - _grid.getPreloadSize();
		if (begin < 0)
			begin = 0;
		return begin;
	}

	/**
	 * Returns the current position of the scrollbar, which ranges from 0 to the
	 * value of the {@link #getMaxpos()} attribute. The default value is 0.
	 */
	/* package */int getCurpos() {
		return _curpos;
	}

	/**
	 * Returns the ending number of the renderer. Note: it's inclusive.
	 */
	/* package */int getRenderEnd() {
		return getRenderAmount() + getRenderBegin() - 1;// inclusive
	}

	/**
	 * Returns the renderer's extent.
	 */
	/* package */int getRenderAmount() {
		if (getCurpos() - _grid.getPreloadSize() >= 0)
			return _extent + _grid.getPreloadSize() * 2;
		return _extent + _grid.getPreloadSize() + getCurpos();
	}

	/**
	 * Returns maximum position of the scrollbar. The default value is
	 * {@link Rows#getChildren()}'s size - {@link #getVisibleAmount()}.
	 */
	/* package */int getMaxpos() {
		return _grid.getRows().getChildren().size() - getVisibleAmount();
	}

	/**
	 * Returns the scrollbar's extent, aka its "visibleAmount".
	 */
	/* package */int getVisibleAmount() {
		return _extent;
	}
}
