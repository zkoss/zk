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
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;

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
		if (parent instanceof Paginated)
			return "both".equals(((Paginated)parent).getPagingPosition());
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
			if (_detailed) {
				smartUpdate("z.info", getInfoText());
			}
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
	 * Returns the information text of the paging, if {@link #isDetailed()} is enabled.
	 * @since 3.6.2
	 */
	protected String getInfoText() {
		int lastItem = (_actpg+1) * _pgsz;
		return "[ " + (_actpg * _pgsz + 1) + (!"os".equals(getMold()) ? " - " + (lastItem > _ttsz ? _ttsz : lastItem) : "")+ " / " + _ttsz + " ]";
	}
	/**
	 * Returns the HTML tags of paging information.
	 * <p>Default: <code>active-page-number / total-numbers-of-pages</code>
	 * <p>Developers can override this method to show different information.
	 * @since 3.5.0
	 * @see #getInfoText()
	 */
	public String getInfoTags() {
		if (_ttsz == 0)
			return "";

		final StringBuffer sb = new StringBuffer(512);
		sb.append("<div id=\"").append(getUuid()).append("!info\" class=\"").append(getZclass()).append("-info\">").append(getInfoText()).append("</div>");
		return sb.toString();
	}
	
	/** Returns the inner HTML tags of this component.
	 * <p>Used only for component development. Not accessible by
	 * application developers.
	 * @since 3.5.2
	 * @see #getInfoText()
	 */
	public String getInnerTags() {
		final StringBuffer sb = new StringBuffer(512);

		int half = _pginc / 2;
		int begin, end = _actpg + half - 1;
		if (end >= _npg) {
			end = _npg - 1;
			begin = end - _pginc + 1;
			if (begin < 0) begin = 0;
		} else {
			begin = _actpg - half;
			if (begin < 0) begin = 0;
			end = begin + _pginc - 1;
			if (end >= _npg) end = _npg - 1;
		}
		String zcs = getZclass();
		if (_actpg > 0) {
			if (begin > 0) //show first
				appendAnchor(zcs, sb, Messages.get(MZul.FIRST), 0);
			appendAnchor(zcs, sb, Messages.get(MZul.PREV), _actpg - 1);
		}

		boolean bNext = _actpg < _npg - 1;
		for (; begin <= end; ++begin) {
			if (begin == _actpg) {
				appendAnchor(zcs, sb, Integer.toString(begin + 1), begin, true); //sb.append(begin + 1).append("&nbsp;");
			} else {
				appendAnchor(zcs, sb, Integer.toString(begin + 1), begin);
			}
		}

		if (bNext) {
			appendAnchor(zcs, sb, Messages.get(MZul.NEXT), _actpg + 1);
			if (end < _npg - 1) //show last
				appendAnchor(zcs, sb, Messages.get(MZul.LAST), _npg - 1);
		}
		if (_detailed)
			sb.append("<span id=\"" + getUuid() + "!info\">").append(getInfoText()).append("</span>");
		return sb.toString();
	}
	private static final void appendAnchor(String zclass, StringBuffer sb, String label, int val) {
		appendAnchor(zclass, sb, label, val, false);
	}
	private static final void appendAnchor(String zclass, StringBuffer sb, String label, int val, boolean seld) {
		zclass += "-cnt" + (seld ? " " + zclass + "-seld" : "");
		sb.append("<a class=\"").append(zclass).append("\" href=\"javascript:;\" onclick=\"zkPgOS.go(this,")
			.append(val).append(")\">").append(label).append("</a>&nbsp;");
	}
	// super
	public String getZclass() {
		String added = "os".equals(getMold()) ? "-os" : "";
		return _zclass == null ? "z-paging" + added : _zclass;
	}
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
