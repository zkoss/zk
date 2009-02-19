/* Splitter.java

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

import org.zkoss.zk.ui.WrongValueException;

/**
 * An element which should appear before or after an element inside a box (
 * {@link Box}, {@link Vbox} and {@link Hbox}).
 * 
 * <p>
 * When the splitter is dragged, the sibling elements of the splitter are
 * resized. If {@link #getCollapse} is true, a grippy in placed inside the
 * splitter, and one sibling element of the splitter is collapsed when the
 * grippy is clicked.
 * 
 * <p>
 * Events: onOpen
 * 
 * <p>
 * Default {@link #getZclass} as follows: (since 3.5.0)
 * <ol>
 * <li>Case 1: If {@link #getOrient()} is vertical, "z-splitter-ver" is assumed</li>
 * <li>Case 2: If {@link #getOrient()} is horizontal, "z-splitter-hor" is
 * assumed</li>
 * </ol>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Splitter extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the orientation of the splitter. It is the same as the parent's
	 * orientation ({@link Box#getOrient}.
	 */
	public String getOrient();

	/**
	 * Returns which side of the splitter is collapsed when its grippy is
	 * clicked. If this attribute is not specified, the splitter will not cause
	 * a collapse. If it is collapsed, {@link #isOpen} returns false.
	 * 
	 * <p>
	 * Default: none.
	 * 
	 * <p>
	 * The returned value can be one ofthe following.
	 * 
	 * <dl>
	 * <dt>none</dt>
	 * <dd>No collpasing occurs.</dd>
	 * <dt>before</dt>
	 * <dd>When the grippy is clicked, the element immediately before the
	 * splitter in the same parent is collapsed so that its width or height is
	 * 0.</dd>
	 * <dt>after</dt>
	 * <dd>When the grippy is clicked, the element immediately after the
	 * splitter in the same parent is collapsed so that its width or height is
	 * 0.</dd>
	 * </dl>
	 * 
	 * <p>
	 * Unlike XUL, you don't have to put a so-called grippy component as a child
	 * of the spiltter.
	 */
	public String getCollapse();

	/**
	 * Sets which side of the splitter is collapsed when its grippy is clicked.
	 * If this attribute is not specified, the splitter will not cause a
	 * collapse.
	 * 
	 * @param collapse
	 *            one of none, before and after. If null or empty is specified,
	 *            none is assumed.
	 * @see #getCollapse
	 */
	public void setCollapse(String collapse) throws WrongValueException;

	/**
	 * Returns whether it is opne (i.e., not collapsed. Meaningful only if
	 * {@link #getCollapse} is not "none".
	 */
	public boolean isOpen();

	/**
	 * Opens or collapses the splitter. Meaningful only if {@link #getCollapse}
	 * is not "none".
	 */
	public void setOpen(boolean open);

}
