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
package org.zkoss.zkmax.zul;

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
			final Component parent = getParent();
			if (parent != null)
				parent.invalidate();
			else {
				final Execution exec = Executions.getCurrent();
				if (exec != null && exec.isExplorer())
					invalidate();
				else smartUpdate("colspan", Integer.toString(_colspan));
			}
				
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
			final Component parent = getParent();
			if (parent != null)
				parent.invalidate();
			else {
				final Execution exec = Executions.getCurrent();
				if (exec != null && exec.isExplorer())
					invalidate();
				else smartUpdate("rowspan", Integer.toString(_rowspan));
			}
		}
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		if (clkattrs == null && _colspan == 1 && _rowspan == 1)
			return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		if (clkattrs != null) sb.append(clkattrs);
		if (_colspan != 1) HTMLs.appendAttribute(sb, "colspan", _colspan);
		if (_rowspan != 1) HTMLs.appendAttribute(sb, "rowspan", _rowspan);
		return sb.toString();
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tablelayout))
			throw new UiException("Wrong parent: " + parent);
		super.setParent(parent);
	}
	
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Panel))
			throw new UiException("Unsupported child for Tablechildren: "
					+ child);
		return super.insertBefore(child, insertBefore);
	}
}
