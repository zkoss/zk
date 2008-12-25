/* Auxheader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 10:07:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.HeaderElement;

/**
 * An auxiliary header.
 * <p>Default {@link #getZclass}: z-auxheader.(since 3.5.0)
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Auxheader extends HeaderElement implements org.zkoss.zul.api.Auxheader {
	private int _colspan = 1, _rowspan = 1;

	public Auxheader() {
	}
	public Auxheader(String label) {
		setLabel(label);
	}
	public Auxheader(String label, String src) {
		setLabel(label);
		setImage(src);
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
			final Execution exec = Executions.getCurrent();
			if (exec != null && exec.isExplorer())
				invalidate();
			else smartUpdate("colspan", _colspan);
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
			final Execution exec = Executions.getCurrent();
			if (exec != null && exec.isExplorer())
				invalidate();
			else smartUpdate("rowspan", _rowspan);
		}
	}

	//super//
	public String getZclass() {
		return _zclass == null ? "z-auxheader" : _zclass;
	}

	//Component//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Auxhead))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
