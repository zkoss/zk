/* Cell.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 31, 2009 4:24:00 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * The generic cell component to be embedded into {@link Row} or {@link Vbox}
 * or {@link Hbox} for fully control style and layout .
 * @author jumperchen
 * @since 5.0.0
 */
public class Cell extends XulElement implements org.zkoss.zul.api.Cell {
	private String _align, _valign;
	private int _colspan = 1, _rowspan = 1;

	/** Returns the horizontal alignment.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment.
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the vertical alignment.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of this grid.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	/** Returns number of columns to span.
	 * Default: 1.
	 */
	public int getColspan() {
		return _colspan;
	}
	/** Sets the number of columns to span.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setColspan(int colspan) throws WrongValueException {
		if (colspan <= 0)
			throw new WrongValueException("Positive only");
		if (_colspan != colspan) {
			_colspan = colspan;
			smartUpdate("colspan", _colspan);
		}
	}

	/** Returns number of rows to span.
	 * Default: 1.
	 */
	public int getRowspan() {
		return _rowspan;
	}
	/** Sets the number of rows to span.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 */
	public void setRowspan(int rowspan) throws WrongValueException {
		if (rowspan <= 0)
			throw new WrongValueException("Positive only");
		if (_rowspan != rowspan) {
			_rowspan = rowspan;
			smartUpdate("rowspan", _rowspan);
		}
	}
	
	//super//
	public String getZclass() {
		return _zclass == null ? "z-cell" : _zclass;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (_colspan != 1)
			renderer.render("colspan", _colspan);
		if (_rowspan != 1)
			renderer.render("rowspan", _rowspan);
		
		renderer.render("align", _align);
		renderer.render("valign", _valign);
	}

}
