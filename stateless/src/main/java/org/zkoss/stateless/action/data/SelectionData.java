/* SelectionData.java

	Purpose:

	Description:

	History:
		2:38 PM 2022/2/15, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an action cause by user's the active selection which is a
 * highlighted block of text.
 * @author jumperchen
 */
public class SelectionData implements ActionData {
	private int start;
	private int end;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String selected;

	/**
	 * Returns the selected text's end position.
	 *
	 * @return the end position {@code >}= 0
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Returns the selected text's start position.
	 *
	 * @return the start position {@code >}= 0
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the selected text contained in this text. If the
	 * selection is null or the document empty, returns empty string.
	 */
	public String getSelectedText() {
		return selected;
	}
}
