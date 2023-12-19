/* Groupfoot.java

	Purpose:
		
	Description:
		
	History:
		2008/5/21 11:23:09 , Created by robbiecheng

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Groupfoot serves as a summary row of group.
 * <p>Available in ZK PE and EE.
 * 
 * <p>Default {@link #getZclass}: z-groupfoot (since 5.0.0)
 *
 *<p>Note: All the child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Groupfoot extends Row {
	public Groupfoot() {
	}

	public Groupfoot(String label) {
		this();
		setLabel(label);
	}

	public <T> Groupfoot(String label, T value) {
		this();
		setLabel(label);
		setValue(value);
	}

	/** Returns the value of the {@link Label} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Component cell = getFirstChild();
		if (cell != null) {
			if (cell instanceof Label)
				return ((Label) cell).getValue();
			else if (cell instanceof Cell) {
				final Component child = cell.getFirstChild();
				return child instanceof Label ? ((Label) child).getValue() : null;
			}
		}
		return null;
	}

	/** Sets the value of the {@link Label} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setValue(label);
	}

	public String getZclass() {
		return _zclass == null ? "z-groupfoot" : _zclass;
	}

	private Label autoFirstCell() {
		Component cell = getFirstChild();
		if (cell == null || cell instanceof Label || cell instanceof Cell) {
			if (cell == null)
				cell = new Label();
			if (cell instanceof Cell) {
				final Component child = cell.getFirstChild();
				if (child != null && child instanceof Label)
					cell = child;
				else
					cell = new Label();
			}
			cell.applyProperties();
			cell.setParent(this);
			return (Label) cell;
		}
		throw new UiException("Unsupported child for setLabel: " + cell);
	}
}
