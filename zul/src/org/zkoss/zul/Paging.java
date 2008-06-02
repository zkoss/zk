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

import org.zkoss.mesg.Messages;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;

/**
 * Paging of long content.
 *
 * <p>Default {@link #getSclass}: paging.
 *
 * @author tomyeh
 */
public class Paging extends XulElement implements Paginal {
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

	public Paging() {
		setSclass("paging");
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

	public void smartUpdate(String attr, String value) {
		super.smartUpdate(attr, value);
		invalidateWholeIfAny();
	}
	public void smartUpdate(String attr, int value) {
		super.smartUpdate(attr, Integer.toString(value));
		invalidateWholeIfAny();
	}
	public void smartUpdate(String attr, boolean value) {
		super.smartUpdate(attr, Boolean.toString(value));
		invalidateWholeIfAny();
	}
	public void invalidate() {
		if (isBothPaging())
			getParent().invalidate();
		else
			super.invalidate();
	}
	protected void invalidateWholeIfAny() {
		if (isBothPaging())
			getParent().invalidate();
	}
	private boolean isBothPaging () {
		Component parent = getParent();
		if (parent instanceof Listbox)
			return "both".equals(((Listbox)parent).getPagingPosition());
		else if (parent instanceof Grid)
			return "both".equals(((Grid)parent).getPagingPosition());
		return false;
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
			updatePageNum();
			if (_detailed) invalidate();
		}
	}
	private void updatePageNum() {
		int v = (_ttsz - 1) / _pgsz + 1;
		if (v == 0) v = 1;
		if (v != _npg) {
			_npg = v;
			if (_actpg >= _npg)
				_actpg = _npg - 1;

			invalidate();
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
			invalidate();
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
			invalidate();
		}
	}

	public boolean isDetailed() {
		return _detailed;
	}
	public void setDetailed(boolean detailed) {
		if (_detailed != detailed) {
			_detailed = detailed;
			invalidate();
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
			if (_npg == 1) invalidate();
		}
	}

	/**
	 * Returns the HTML tags of paging information.
	 * 
	 * @since 3.1.0
	 * @author gracelin
	 */
	public String getInfoTags() {
		final StringBuffer sb = new StringBuffer(512);
		if (_ttsz == 0)
			return sb.append("No topics to display").toString();
			
		int lastItem = (_actpg+1) * _pgsz;
		sb.append("<div class=\"z-paging-info\">[ ")
			.append(_actpg * _pgsz + 1).append(" - ").append(lastItem > _ttsz ? _ttsz : lastItem)
			.append(" / ").append(_ttsz).append(" ]</div>");
		return sb.toString();
	}
	
	/**
	 * 
	 * @since 3.1.0
	 * @author gracelin
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.actpg", _actpg);
		HTMLs.appendAttribute(sb, "z.numpg", _npg);
		return sb.toString();
	}

	// -- Component --//
	public boolean isVisible() {
		return super.isVisible() && (_npg > 1 || !_autohide);
	}
	public boolean isChildable() {
		return false;
	}
}
