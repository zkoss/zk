/* AbstractTextual.java


Purpose: 
Description: 
History:
C2001/10/22 20:48:47, reate, Tom M. Yeh

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.impl;

import java.util.List;
import org.w3c.dom.CharacterData;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.idom.*;

/**
 * Represents a textual item.
 *
 * <p>Note: any deriving class's getText shall not return null.
 *
 * <p>Element.getText uses this class's isPartOfParentText to know
 * whether a child's text shall be catenated.
 *
 * @author tomyeh
 * @see Element
 */
public abstract class AbstractTextual extends AbstractItem
implements Textual, CharacterData {
	/** The text. */
	protected String _text;

	/** Constructor.
	 */
	protected AbstractTextual(String text) {
		setText(text);
	}
	/** Constructor.
	 */
	protected AbstractTextual() {
		_text = "";
	}

	//-- Textual --//
	/**
	 * Returns true if this text object is part of the parent's text.
	 * Default: true.
	 */
	public boolean isPartOfParentText() {
		return true;
	}
	/**
	 * Returns true if this textual object is allowed to be coalesced with
	 * its siblings with the same type (class).
	 * It is used by Group.coalesce.
	 * <p>Default: false. Right now only Text override it to true.
	 */
	public boolean isCoalesceable() {
		return false;
	}
	public Textual split(int offset) {
		String s = getText();
		if (offset < s.length())
			return null;

		Textual vtx;
		try {
			vtx = (Textual)Classes.newInstance(getClass(),
				new Class[] {String.class},
				new Object[] {s.substring(offset)});
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex,
					getClass() + " must have a String constructor");
		}
		setText(s.substring(0, offset));

		if (getParent() != null) {
			List list = getParent().getChildren();
			list.add(list.indexOf(this) + 1, vtx);
		}
		return vtx;
	}

	//-- utilities --//
	/**
	 * Checks whether the text is valid.
	 * It is usually overridden by the deriving classes to check
	 * more specifically.
	 */
	protected void checkText(String text) {
		Verifier.checkCharacterData(text, getLocator());
	}

	//-- Item --//
	public String getText() {
		return _text;
	}
	public void setText(String text) {
		checkWritable();

		if (text == null)
			text = "";

		if (!Objects.equals(_text, text)) {
			checkText(text);
			_text = text;
			setModified();
		}
	}

	//-- CharacterData --//
	public final int getLength() {
		return getText().length(); //not use _text since it might be overred
	}
	public final String getData() {
		return getText();
	}
	public final void setData(String data) {
		setText(data);
	}
	public final String substringData(int offset, int count) {
		return getText().substring(offset, offset + count);
	}
	public final void appendData(String newData) {
		if (newData != null && newData.length() != 0)
			setText(getText() + newData);
	}
	public final void insertData(int offset, String arg) {
		if (arg != null && arg.length() != 0)
			setText(new StringBuffer(getText())
				.insert(offset, arg).toString());
	}
	public final void deleteData(int offset, int count) {
		if (offset < getLength())
			setText(new StringBuffer(getText())
				.delete(offset, offset + count).toString());
	}
	public final void replaceData(int offset, int count, String arg) {
		setText(new StringBuffer(getText())
			.replace(offset, offset + count, arg).toString());
	}

	//-- Text if implemented by derived class --//
	public final org.w3c.dom.Text splitText(int offset) {
		return (org.w3c.dom.Text)split(offset);
	}

	public boolean isElementContentWhitespace() {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public String getWholeText() {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public org.w3c.dom.Text replaceWholeText(String content)
	throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}

	//Node//
	public String getTextContent() {
		return getText();
	}

	//-- Object --//
	/**
	 * Gets the textual representation for debug.
	 */
	public String toString() {
		String clsName = getClass().getName();
		int j = clsName.lastIndexOf('.');
		if (j >= 0)
			clsName = clsName.substring(j + 1);
		return '[' + clsName + ": \"" + getText() + "\"]";
	}
}
