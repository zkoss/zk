/* Binary.java


	Purpose: 
	Description: 
	History:
	2001/10/21 21:32:27, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.zkoss.lang.Objects;
import org.zkoss.idom.impl.*;

/**
 * The binary item. It is iDOM's extension to W3C/DOM, which allows
 * only String-type value. However, XML files doesn't convey the type
 * information, so, when loading back an XML file, Binary vertices
 * become CData vertices.
 *
 * <p>To be compatible with W3C/DOM utility, it fakes as Text.
 * Thus, getNodeName returns "#text", rather than getName ("#binary").
 *
 * @author tomyeh
 * @see CData
 */
public class Binary extends AbstractTextual
implements org.w3c.dom.Text, Binable {
	/** The value. */
	Object _value;

	/** Constructor.
	 */
	 public Binary(String value) {
	 	setValue(value);
	 }
	/** Constructor.
	 */
	 public Binary(Object value) {
	 	setValue(value);
	 }
	/** Constructor.
	 */
	 public Binary() {
	 }

	//-- AbstractTextual extras --//
	protected void checkText(String text) {
	}

	//-- Binable --//
	public final Object getValue() {
		return _value;
	}
	public final void setValue(Object o) {
		_value = o;
	}

	//-- Item --//
	/**
	 * Gets the text representation of the value. Never null.
	 */
	public final String getText() {
		String s = _value != null ? _value.toString(): null;
		return s != null ? s: "";
	}
	public final void setText(String text) {
		setValue(text);
	}
	public final String getName() {
		return "#binary";
	}

	//-- Node --//
	public final String getNodeName() {
		return "#text"; //fake as TEXT_NODE
	}
	public final short getNodeType() {
		return TEXT_NODE; //fake as TEXT_NODE
	}

	//-- Object --//
	/**
	 * Gets the textual representation for debug.
	 */
	public String toString() {
		String s = super.toString();
		if (_value == null)
			return s;

		int len = s.length();
		char cc = s.charAt(len - 1);
		return s.substring(0, len - 1) + ' ' + _value.getClass().getName() + cc;
	}
}
