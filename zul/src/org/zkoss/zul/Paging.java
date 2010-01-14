/* Paging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 15:26:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;

/**
 * Paging of long content.
 *
 * <p>Default {@link #getZclass}: z-paging. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Paging extends XulElement implements org.zkoss.zul.api.Paging, Paginal {
	/** # of items per page. */
	private int _pgsz = 20;
	/** total # of items. */
	private int _ttsz = 0;
	/** # of pages. */
	private int _npg = 1;
	/** the active page. */
	private int _actpg = 0;
	/** # of page anchors are visible */
	private int _pginc = 10;
	/** Whether to hide automatically if only one page is available. */
	private boolean _autohide;
	/** Whether to show detailed info. */
	private boolean _detailed;

	static {
		addClientEvent(Paging.class, ZulEvents.ON_PAGING, CE_IMPORTANT);
	}
	public Paging() {
	}

	/** Contructor.
	 *
	 * @param totalsz the total # of items
	 * @param pagesz the # of items per page
	 */
	public Paging(int totalsz, int pagesz) {
		this();
		setTotalSize(totalsz);
		setPageSize(pagesz);
	}
	
	//Paginal//
	public int getPageSize() {
		return _pgsz;
	}
	public void setPageSize(int size) throws WrongValueException {
		if (size <= 0)
			throw new WrongValueException("positive only");

		if (_pgsz != size) {
			_pgsz = size;
			smartUpdate("pageSize", _pgsz);
			updatePageNum();
			Events.postEvent(new PagingEvent("onPagingImpl", this, _actpg));
				//onPagingImpl is used for implementation purpose only
		}
	}
	public int getTotalSize() {
		return _ttsz;
	}
	public void setTotalSize(int size) throws WrongValueException {
		if (size < 0)
			throw new WrongValueException("non-negative only");

		if (_ttsz != size) {
			_ttsz = size;
			smartUpdate("totalSize", _ttsz);
			updatePageNum();
		}
	}
	private void updatePageNum() {
		int v = (_ttsz - 1) / _pgsz + 1;
		if (v == 0) v = 1;
		if (v != _npg) {
			_npg = v;
			if (_actpg >= _npg)
				_actpg = _npg - 1;
		}
	}

	public int getPageCount() {
		return _npg;
	}
	public int getActivePage() {
		return _actpg;
	}
	public void setActivePage(int pg) throws WrongValueException {
		if (pg >= _npg || pg < 0)
			throw new WrongValueException("Unable to set active page to "+pg+" since only "+_npg+" pages");
		if (_actpg != pg) {
			_actpg = pg;
			smartUpdate("activePage", pg);
			Events.postEvent(new PagingEvent("onPagingImpl", this, _actpg));
				//onPagingImpl is used for implementation purpose only
		}
	}

	public int getPageIncrement() {
		return _pginc;
	}
	public void setPageIncrement(int pginc) throws WrongValueException {
		if (pginc <= 0)
			throw new WrongValueException("Nonpositive is not allowed: "+pginc);
		if (_pginc != pginc) {
			_pginc = pginc;
			smartUpdate("pageIncrement", pginc);
		}
	}

	public boolean isDetailed() {
		return _detailed;
	}
	public void setDetailed(boolean detailed) {
		if (_detailed != detailed) {
			_detailed = detailed;
			smartUpdate("detailed", detailed);
		}
	}

	//extra//
	/** Returns whether to automatically hide this component if
	 * there is only one page available.
	 * <p>Default: false.
	 */
	public boolean isAutohide() {
		return _autohide;
	}
	/** Sets whether to automatically hide this component if
	 * there is only one page available.
	 */
	public void setAutohide(boolean autohide) {
		if (_autohide != autohide) {
			_autohide = autohide;
			smartUpdate("autohide", autohide);
		}
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_ttsz != 0) renderer.render("totalSize", _ttsz);
		if (_pgsz != 20) renderer.render("pageSize", _pgsz);
		if (_actpg != 0) renderer.render("activePage", _actpg);
		if (_npg != 1) renderer.render("pageCount", _npg);
		if (_pginc != 10) renderer.render("pageIncrement", _pginc);
		render(renderer, "detailed", _detailed);
		render(renderer, "autohide", _autohide);
	}
	
	public String getZclass() {
		String added = "os".equals(getMold()) ? "-os" : "";
		return _zclass == null ? "z-paging" + added : _zclass;
	}

	// -- Component --//
	public boolean isVisible() {
		return super.isVisible() && (_npg > 1 || !_autohide);
	}
	protected boolean isChildable() {
		return false;
	}
	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getCommand();
		if (name.equals(ZulEvents.ON_PAGING)) {
			PagingEvent evt = PagingEvent.getPagingEvent(request);
			setActivePage(evt.getActivePage());
			Events.postEvent(PagingEvent.getPagingEvent(request));
		} else
			super.service(request, everError);
	}
}
