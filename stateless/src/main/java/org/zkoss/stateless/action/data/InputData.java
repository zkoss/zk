/* InputData.java

	Purpose:

	Description:

	History:
		11:53 AM 2021/10/18, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.zpr.ICombobox;
import org.zkoss.stateless.zpr.IDoublebox;

/**
 * Represents input data cause by user's input something at the client.
 * @author jumperchen
 */
public class InputData implements ActionData {
	private String value;

	@ActionVariable(targetId = SELF, field = "value")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Object oldValue;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean bySelectBack;
	private int start = 0;

	/** Returns the value that user input.
	 */
	public String getValue() {
		return value;
	}

	/** Returns the previous value before user's input.
	 * Notice that the class of the return value depends on the component.
	 * For example, an instance of Double is returned if {@link IDoublebox}
	 * is used.
	 */
	public Object getPreviousValue() {
		return oldValue;
	}

	/** Returns whether this data is caused by an action that is <code>onChanging</code> type, and caused by
	 * user's selecting a list of predefined values (a.k.a., items).
	 *
	 * <p>It is always false if it is caused by an action with the <code>onChange</code> type.
	 *
	 * <p>Currently, only {@link ICombobox} might set it to true for the onChanging
	 * action.
	 */
	public boolean isChangingBySelectBack() {
		return bySelectBack;
	}

	/**
	 * Returns the start position of the cursor from the input element.
	 * @return the start position {@code >}= 0
	 */
	public int getStart() {
		return start;
	}
}