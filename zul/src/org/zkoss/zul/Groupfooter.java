/* GroupFooter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2008/5/21 11:23:09 , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;

/**
 * GroupFooter serves as a summary row of group.
 * 
 * <p>Default {@link #getSclass}: groupfooter. * 
 *
 *<p>Note: All the child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.0
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
	public void onChildAdded(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String clx = cmp.getSclass();
		cmp.setSclass(clx != null && clx.length() > 0 ? clx + " groupfooter-cell" : "groupfooter-cell");
	}
	public void onChildRemoved(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String cls = cmp.getSclass();
		cmp.setSclass(cls != null && cls.indexOf("groupfooter-cell") > -1 ? 
		cls.replaceAll("(?:^|\\s+)" + "groupfooter-cell" + "(?:\\s+|$)", " ").trim() : cls);
	}
}
