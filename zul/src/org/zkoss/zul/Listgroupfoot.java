/* Listgroupfoot.java

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

/**
 * GroupFooter serves as a summary listitem of listgroup.
 * 
 * <p>Default {@link #getSclass}: listgroupfoot.
 *
 *<p>Note: All the {@link Label} child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Listgroupfoot extends Listitem{
	public Listgroupfoot() {
		setSclass("listgroupfoot");		
	}
	public Listgroupfoot(String label) {
		this();
		setLabel(label);
	}
	public Listgroupfoot(String label, Object value) {
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
		autoFirstCell().setLabel(label);
	}
	private Listcell autoFirstCell() {
		Listcell cell = (Listcell)getFirstChild();
		if (cell == null) {
			cell = new Listcell();
			cell.applyProperties();
			cell.setParent(this);
		}
		return cell;
	}
	public void onChildAdded(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String clx = cmp.getSclass();
		cmp.setSclass(clx != null && clx.length() > 0 ? clx + " listgroupfoot-cell" : "listgroupfoot-cell");
	}
	public void onChildRemoved(Component child) {
		final HtmlBasedComponent cmp = (HtmlBasedComponent) child;
		final String cls = cmp.getSclass();
		cmp.setSclass(cls != null && cls.indexOf("listgroupfoot-cell") > -1 ?
			cls.replaceAll("(?:^|\\s+)" + "listgroupfoot-cell" + "(?:\\s+|$)", " ").trim() : cls);
	}
}
