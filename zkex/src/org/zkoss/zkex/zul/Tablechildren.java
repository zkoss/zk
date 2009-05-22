/* Tablechildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 14, 2008 10:08:09 AM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul;

import java.io.IOException;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

/**
 * The cell of Tablelayout. <br> 
 * The child component of Tablechildren only can be Panel.
 * 
 * <p>Default {@link #getZclass}: z-table-children.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Tablechildren extends XulElement implements org.zkoss.zkmax.zul.api.Tablechildren {
	private int _colspan = 1, _rowspan = 1;
	
	public Tablechildren() {
		setZclass("z-table-children");
	}
	
	/** Returns number of columns to span this header.
	 * Default: 1.
	 */
	public int getColspan() {
		return _colspan;
	}
	/** Sets the number of columns to span this header.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setColspan(int colspan) throws WrongValueException {
		if (colspan <= 0)
			throw new WrongValueException("Positive only");
		if (_colspan != colspan) {
			_colspan = colspan;			
			
			smartUpdate("colspan", Integer.toString(_colspan));
				
		}
	}

	/** Returns number of rows to span this header.
	 * Default: 1.
	 */
	public int getRowspan() {
		return _rowspan;
	}
	/** Sets the number of rows to span this header.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 */
	public void setRowspan(int rowspan) throws WrongValueException {
		if (rowspan <= 0)
			throw new WrongValueException("Positive only");
		if (_rowspan != rowspan) {
			_rowspan = rowspan;
			
			smartUpdate("rowspan", Integer.toString(_rowspan));
			
		}
	}

	//super//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tablelayout))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Panel))
			throw new UiException("Unsupported child for Tablechildren: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);
		if(_rowspan != 1)
			render(renderer, "rowspan", new Integer(_rowspan));
		if(_colspan != 1)
			render(renderer, "colspan", new Integer(_colspan));
	}
}
