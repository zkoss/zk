/* SelectionEvent.java


 Purpose:
 
 Description:
 
 History:
 Wed April 18 15:18:32     2007, Created by jumperchen


 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by user's the active selection which is a
 * highlighted block of text.
 * 
 * @author jumperchen
 */
public class SelectionEvent extends Event {
	private final int _start, _end;
	private final String _txt;

	/** Converts an AU request to a selection event.
	 * @since 5.0.0
	 */
	public static final SelectionEvent getSelectionEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		return new SelectionEvent(request.getCommand(), comp,
			AuRequests.getInt(data, "start", 0),
			AuRequests.getInt(data, "end", 0),
			(String)data.get("selected"));
	}

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
