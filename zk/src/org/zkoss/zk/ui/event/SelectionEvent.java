/* SelectionEvent.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed April 18 15:18:32     2007, Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's the active selection which is a
 * highlighted block of text.
 * 
 * @author jumperchen
 */
public class SelectionEvent extends Event {
	private final int _start, _end;

	private final String _txt;

	/**
	 * Constructs a selection event.
	 */
	public SelectionEvent(String name, Component target, int start, int end,
			String txt) {
		super(name, target);
		_start = start;
		_end = end;
		_txt = txt;
	}

	/**
	 * Returns the selected text's end position.
	 * 
	 * @return the end position >= 0
	 */
	public int getEnd() {
		return _end;
	}

	/**
	 * Returns the selected text's start position.
	 * 
	 * @return the start position >= 0
	 */
	public int getStart() {
		return _start;
	}

	/**
	 * Returns the selected text contained in this text. If the
	 * selection is null or the document empty, returns empty string.
	 */
	public String getSelectedText() {
		return _txt;
	}

}
