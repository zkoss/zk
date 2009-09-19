/* SimpleLocator.java


	Purpose: 
	Description: 
	History:
	2001/10/25 15:34:49, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.util;

import java.io.Serializable;

import org.zkoss.xml.Locator;

/**
 * The locator implementation. Useful to assign the location information
 * to vertices.
 *
 * @author tomyeh
 * @see org.zkoss.idom.Item
 */
public class SimpleLocator implements Locator, Serializable {
    private static final long serialVersionUID = 20060622L;

	protected int _colno, _lnno;
	protected String _pubId, _sysId;

	/**
	 * Constructor with another locator.
	 */
	public SimpleLocator(org.xml.sax.Locator loc) {
		_colno = loc.getColumnNumber();
		_lnno  = loc.getLineNumber();
		_pubId = loc.getPublicId();
		_sysId = loc.getSystemId();
	}
	/**
	 * Constructor with another locator.
	 */
	public SimpleLocator(javax.xml.transform.SourceLocator loc) {
		_colno = loc.getColumnNumber();
		_lnno  = loc.getLineNumber();
		_pubId = loc.getPublicId();
		_sysId = loc.getSystemId();
	}
	/**
	 * Constructor.
	 */
	public SimpleLocator(int colno, int lnno, String pubId, String sysId) {
		_colno = colno;
		_lnno  = lnno;
		_pubId = pubId;
		_sysId = sysId;
	}

	//-- Extra utilities --//
	public static final String toString(org.xml.sax.Locator loc) {
		if (loc == null)
			return "";

		StringBuffer sb = new StringBuffer().append('[');
		String s = loc.getPublicId();
		if (s != null && s.length() > 0)
			sb.append("PUB ").append(s).append(' ');
		s = loc.getSystemId();
		if (s != null && s.length() > 0)
			sb.append("SYS ").append(s).append(' ');

		sb.append("line ").append(loc.getLineNumber());
		if (loc.getColumnNumber() > 0) //some parser does not support it
			sb.append(" col ").append(loc.getColumnNumber());
		return sb.append(']').toString();
	}

	//-- Locator --//
	public final int getColumnNumber() {
		return _colno;
	}
	public final int getLineNumber() {
		return _lnno;
	}
	public final String getPublicId() {
		return _pubId;
	}
	public final String getSystemId() {
		return _sysId;
	}

	//-- Object --//
	public final String toString() {
		return toString(this);
	}
}
