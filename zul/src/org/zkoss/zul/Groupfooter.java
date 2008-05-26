/* GroupFooter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2008/5/21 上午 11:23:09 , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * GroupFooter serves as a summary row of group.
 * 
 * <p>Default {@link #getSclass}: groupfooter. * 
 *
 *<p>Note: All the {@link Label} child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * 
 * @author robbiecheng
 * @since 3.1.0
 */
public class Groupfooter extends Row{
	public Groupfooter() {
		setSclass("groupfooter");		
	}
	public Groupfooter(String label) {
		this();
		setLabel(label);
	}
	public Groupfooter(String label, Object value) {
		this();
		setLabel(label);
		setValue(value);
	}
	
	/** Returns the value of the {@link Label} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Component cell = (Component)getFirstChild();
		return cell != null && cell instanceof Label ? ((Label)cell).getValue(): null;
	}
	/** Sets the value of the {@link Label} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setValue(label);
	}
	private Label autoFirstCell() {
		Component cell = (Component)getFirstChild();
		if (cell == null || cell instanceof Label) {
			if (cell == null) cell = new Label();
			cell.applyProperties();
			cell.setParent(this);
			return (Label)cell;
		}
		throw new UiException("Unsupported child for setLabel: "+cell);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (super.insertBefore(child, insertBefore)) {
			if (child instanceof Label) {
				Label label = (Label) child;
				String clx = label.getSclass();
				label.setSclass(clx != null && clx.length() > 0 ? clx + " groupfooter-cell" : "groupfooter-cell");
			}				
			return true;
		}
		return false;
	}
	
	public String getOuterAttrs() {		
		final StringBuffer sb = new StringBuffer(64).append( super.getOuterAttrs());		
		return sb.toString();
	}
}
