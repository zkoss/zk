/* Radio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A radio button.
 * 
 * <p>
 * Radio buttons without a ancestor {@link Radiogroup} is considered as the same
 * group. The nearest ancestor {@link Radiogroup} is the group that the radio
 * belongs to. See also {@link #getRadiogroupApi}.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>{@link org.zkoss.zk.ui.event.CheckEvent} is sent when a checkbox is
 * checked or unchecked by user.</li>
 * </ol>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Radio extends Checkbox {

	/**
	 * Returns {@link Radiogroup} that this radio button belongs to. It is the
	 * nearest ancestor {@link Radiogroup}. In other words, it searches up the
	 * parent, parent's parent and so on for any {@link Radiogroup} instance. If
	 * found this radio belongs the found radiogroup. If not, this radio itself
	 * is a group.
	 */
	public org.zkoss.zul.api.Radiogroup getRadiogroupApi();

	/**
	 * Returns the value.
	 * <p>
	 * Default: "".
	 */
	public String getValue();

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value; If null, it is considered as empty.
	 */
	public void setValue(String value);

}
