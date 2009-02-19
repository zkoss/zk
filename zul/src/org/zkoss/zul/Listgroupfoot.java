/* Listgroupfoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2008/5/21 11:23:09 , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;

/**
 * GroupFooter serves as a summary listitem of listgroup.
 * 
 * <p>Default {@link #getZclass}: z-list-group-foot.
 *
 *<p>Note: All the {@link Label} child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Listgroupfoot extends Listitem implements org.zkoss.zul.api.Listgroupfoot {
	public Listgroupfoot() {
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
		final Component cell = getFirstChild();
		return cell != null && cell instanceof Label ? ((Label)cell).getValue(): null;
	}
	/** Sets the value of the {@link Label} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setLabel(label);
	}

	public String getZclass() {
		return _zclass == null ? "z-list-group-foot" : _zclass;
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
}
