/* Cell.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 31, 2009 4:24:38 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zul.impl.api.XulElement;

/**
 * The generic cell component to be embedded into {@link Row} or {@link Vbox}
 * or {@link Hbox} for fully control style and layout .
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public interface Cell extends XulElement {

	/**
	 * Sets the horizontal alignment.
	 */
	public void setAlign(String align);

	/**
	 * Returns the horizontal alignment.
	 * <p>
	 * Default: null (system default: left unless CSS specified).
	 */
	public String getAlign();

	/**
	 * Sets the vertical alignment.
	 */
	public void setValign(String valign);

	/**
	 * Returns the vertical alignment.
	 * <p>
	 * Default: null (system default: top).
	 */
	public String getValign();

	/**
	 * Returns number of columns to span. Default: 1.
	 */
	public int getColspan();

	/**
	 * Sets the number of columns to span.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setColspan(int colspan);

	/**
	 * Returns number of rows to span. Default: 1.
	 */
	public int getRowspan();

	/**
	 * Sets the number of rows to span.
	 * <p>
	 * It is the same as the rowspan attribute of HTML TD tag.
	 */
	public void setRowspan(int rowspan);
}
