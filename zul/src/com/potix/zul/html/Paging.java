/* Paging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 15:26:06     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zul.html.impl.XulElement;
import com.potix.zul.html.ext.Paginal;

/**
 * Paging of long content.
 *
 * <p>Default {@link #getSclass}: paging.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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

	//Paginal//
	public int getPageSize() {
		return _pgsz;
	}
	public void setPageSize(int size) {
		if (size <= 0)
			throw new IllegalArgumentException("positive only");

		if (_pgsz != size) {
			_pgsz = size;
			updatePageNum();
		}
	}
	public int getTotalSize() {
		return _ttsz;
	}
	public void setTotalSize(int size) {
		if (size < 0)
			throw new IllegalArgumentException("non-negative only");

		if (_ttsz != size) {
			_ttsz = size;
			updatePageNum();
		}
	}
	public int getPageCount() {
		return _npg;
	}
	public int getActivePage() {
		return _actpg;
	}

	private void updatePageNum() {
		int v = (_ttsz - 1) / _pgsz + 1;
		if (v == 0) v = 1;
		if (v != _npg) {
			_npg = v;
			if (_actpg >= _npg)
				_actpg = _npg - 1;

			invalidate(INNER);
		}
	}

	/** Returns the inner HTML tags of this component.
	 * <p>Used only for component development. Not accessible by
	 * application developers.
	 */
	public String getInnerTags() {
		final StringBuffer sb = new StringBuffer(128);
		sb.append("<a href=\"javascript:;\">First</a>&nbsp;<a href=\"javascript:;\">1</a>");
		return sb.toString();
	}
	private static void appendAnchor(StringBuffer sb, String label, int val) {
		sb.append("<a href=\"javascript:;\" onClick=\"zkPg.go(")
			.append(val).append(")\">").append(label)
			.append("</a>");
	}

	//-- Component --//
	public boolean isChildable() {
		return false;
	}
}
