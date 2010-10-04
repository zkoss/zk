/* Textbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 15:48:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.InputElement;

/**
 * A textbox.
 *
 * <p>See <a href="package-summary.html">Specification</a>.</p>
 * <p>Default {@link #getZclass}: z-textbox.(since 3.5.0)
 * @author tomyeh
 */
public class Textbox extends InputElement implements org.zkoss.zul.api.Textbox {
	private AuxInfo _auxinf;

	static {
		addClientEvent(Textbox.class, Events.ON_FOCUS, 0);
		addClientEvent(Textbox.class, Events.ON_BLUR, 0);
	}

	public Textbox() {
		setValueDirectly("");
	}
	public Textbox(String value) throws WrongValueException {
		setValue(value);
	}

	/** Returns the value.
	 * The same as {@link #getText}.
	 * <p>Default: "".
	 * @exception WrongValueException if user entered a wrong value
	 */
	public String getValue() throws WrongValueException {
		return getText();
	}
	/** Sets the value.
	 *
	 * @param value the value; If null, it is considered as empty.
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(String value) throws WrongValueException {
		setText(value);
	}

	//-- super --//
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 */
	protected Object coerceFromString(String value) throws WrongValueException {
		return value != null ? value: "";
	}
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 */
	protected String coerceToString(Object value) {
		return value != null ? (String)value: "";
	}

	/** Returns the type.
	 * <p>Default: text.
	 */
	public String getType() {
		return _auxinf != null ? _auxinf.type: TEXT;
	}
	/** Sets the type.
	 * @param type the type. Acceptable values are "text" and "password".
	 * Unlike XUL, "timed" is redudant because it is enabled as long as
	 * onChanging is added.
	 */
	public void setType(String type) throws WrongValueException {
		if (!TEXT.equals(type) && !"password".equals(type))
			throw new WrongValueException("Illegal type: "+type);

		if (!type.equals(_auxinf != null ? _auxinf.type: TEXT)) {
			initAuxInfo().type = type;
			smartUpdate("type", getType());
		}
	}

	/** Returns the rows.
	 * <p>Default: 1.
	 */
	public int getRows() {
		return _auxinf != null ? _auxinf.rows: 1;
	}
	/** Sets the rows.
	 */
	public void setRows(int rows) throws WrongValueException {
		if (rows <= 0)
			throw new WrongValueException("Illegal rows: "+rows);

		if ((_auxinf != null ? _auxinf.rows: 1) != rows) {
			initAuxInfo().rows = rows;
			if (rows > 1)
				setMultiline(true); //auto-enable
			smartUpdate("rows", getRows());
		}
	}
	/** Returns whether it is multiline.
	 * <p>Default: false.
	 */
	public boolean isMultiline() {
		return _auxinf != null && _auxinf.multiline;
	}
	/** Sets whether it is multiline.
	 */
	public void setMultiline(boolean multiline) {
		if ((_auxinf != null && _auxinf.multiline) != multiline) {
			initAuxInfo().multiline = multiline;
			smartUpdate("multiline", isMultiline());
		}
	}

	/** Returns whether TAB is allowed.
	 * If true, the user can enter TAB in the textbox, rather than change
	 * focus.
	 * <p>Default: false.
	 * @since 3.6.0
	 */
	public boolean isTabbable() {
		return _auxinf != null && _auxinf.tabbable;
	}
	/** Sets whether TAB is allowed.
	 * If true, the user can enter TAB in the textbox, rather than change
	 * focus.
	 * <p>Default: false.
	 * @since 3.6.0
	 */
	public void setTabbable(boolean tabbable) {
		if ((_auxinf != null && _auxinf.tabbable) != tabbable) {
			initAuxInfo().tabbable = tabbable;
			smartUpdate("tabbable", isTabbable());
		}
	}

	//Cloneable//
	public Object clone() {
		final Textbox clone = (Textbox)super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo)_auxinf.clone();
		return clone;
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "multiline", isMultiline());
		final int rows = getRows();
		if (rows > 1) renderer.render("rows", rows);
		render(renderer, "tabbable", isTabbable());
		final String type = getType();
		if (!TEXT.equals(type)) renderer.render("type", type);
	}
	public String getZclass() {
		return _zclass != null ? _zclass: "z-textbox";
	}

	private static final String TEXT = "text";
	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}
	private static class AuxInfo implements java.io.Serializable, Cloneable  {
		private String type = TEXT;
		private int rows = 1;
		private boolean multiline;
		private boolean tabbable;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
