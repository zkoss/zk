/* Label.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 13, 2007 10:46:20 AM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.grid;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.zkoss.xel.fn.CommonFns;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Inputable;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;

/**
 * This class is implemented for Ext JS. And it extends the original
 * {@link org.zkoss.zul.Label}.
 * 
 * @author jumperchen
 * 
 */
public class Label extends org.zkoss.zul.Label {
	private String _value = "";

	public Label() {
	}
	public Label(String value) {
		setValue(value);
	}
	
	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		if (getParent() != null && getParent() instanceof Row) {
			if (((Row) getParent()).isSmartUpdate()) {
				super.addMoved(oldparent, oldpg, newpg);
			}
		}else {
			super.addMoved(oldparent, oldpg, newpg);
		}
	}

	/**
	 * Returns the value.
	 * <p>
	 * Default: "".
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Sets the value.
	 */
	public void setValue(String value) throws WrongValueException {
		if (value == null)
			value = "";
		Column col = getRelatedColumn();
		if (col != null)
			validate(value, col.getEditortype(), col.getFormat());

		if (!Objects.equals(_value, value)) {
			_value = value;
			invalidate();
		}
	}

	protected void validate(String value, String type, String format)
			throws WrongValueException {
		if (Column.EDITOR_TYPE_NUMBERFIELD.equals(type)) {
			try {
				new BigDecimal(value);
			} catch (NumberFormatException ex) {
				throw new WrongValueException(this, MZul.NUMBER_REQUIRED, value);
			}
		}else if (Column.EDITOR_TYPE_DATEFIELD.equals(type)) {
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				sdf.parse(value);
			} catch (ParseException e) {
				throw new WrongValueException(this, MZul.DATE_REQUIRED,
						new Object[] {value, format});
			}
		}
	}

	// -- super --//
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_CHANGE);
		return sb.toString();
	}

	/**
	 * Returns the native object by the editor type of column for label value.
	 * If the editor type of column is
	 * {@link Column#EDITOR_TYPE_NUMBERFIELD}, it will return a
	 * {@link BigDecimal} object. If the editor type of column is
	 * {@link Column#EDITOR_TYPE_CHECKBOX}, it will return a {@link Boolean}
	 * object. If the editor type of column is
	 * {@link Column#EDITOR_TYPE_DATEFIELD}, it will return a {@link Date}
	 * object. Otherwise, it will return a <code>string</code> value.
	 * <p>Note: Never null.
	 */
	public Object getNativeObject() {
		Column col = getRelatedColumn();
		if (col == null)
			return getValue();
		final String type = col.getEditortype();
		if (Column.EDITOR_TYPE_TEXTFIELD.equals(type)
				|| Column.EDITOR_TYPE_COMBOBOX.equals(type))
			return getValue();
		if (Column.EDITOR_TYPE_NUMBERFIELD.equals(type))
			return CommonFns.toDecimal(getValue());
		if (Column.EDITOR_TYPE_CHECKBOX.equals(type))
			return new Boolean(getValue());
		if (Column.EDITOR_TYPE_DATEFIELD.equals(type)) {
			final SimpleDateFormat sdf = new SimpleDateFormat(col.getFormat());
			try {
				return sdf.parse(getValue());
			} catch (ParseException e) {
			}
		}
		return getValue();
	}

	private Column getRelatedColumn() {
		final Grid grid;
		if (getParent() == null || !(getParent() instanceof Row)
				|| (grid = ((Row) getParent()).getGrid()) == null
				|| grid.getColumns() == null)
			return null;
		int index = getParent().getChildren().indexOf(this);
		if (grid.getColumns().getChildren().size() <= index)
			return null;
		return (Column) grid.getColumns().getChildren().get(index);
	}

	/**
	 * Returns the text for generating HTML tags (Internal Use Only).
	 * 
	 * <p>
	 * Used only for component generation. Not for applications.
	 */
	public String getEncodedText() {
		StringBuffer sb = null;
		final int len = getValue().length();
		if (isPre() || isMultiline()) {
			for (int j = 0, k;; j = k + 1) {
				k = getValue().indexOf('\n', j);
				if (k < 0) {
					sb = encodeLine(sb, j, len);
					break;
				}

				if (sb == null) {
					assert j == 0;
					sb = new StringBuffer(getValue().length() + 10);
				}
				sb = encodeLine(sb, j, k > j
						&& getValue().charAt(k - 1) == '\r' ? k - 1 : k);
				sb.append("<br/>");
			}
		} else {
			sb = encodeLine(null, 0, len);
		}
		return sb != null ? sb.toString() : getValue();
	}

	/**
	 * This is a clone method.
	 * 
	 * @see org.zkoss.zul.Label#encodeLine
	 */
	private StringBuffer encodeLine(StringBuffer sb, int b, int e) {
		boolean prews = isPre() || isMultiline();
		int maxword = 0;
		if (getMaxlength() > 0) {
			int deta = e - b;
			if (deta > getMaxlength()) {
				if (isHyphen()) {
					maxword = getMaxlength();
				} else if (!prews) {
					assert b == 0;
					int j = getMaxlength();
					while (j > 0
							&& Character.isWhitespace(getValue().charAt(j - 1)))
						--j;
					return new StringBuffer(j + 3).append(
							getValue().substring(0, j)).append("...");
				}
			}
		}

		for (int cnt = 0, j = b; j < e; ++j) {
			final char cc = getValue().charAt(j);
			String val = null;
			if (cc == '\t') {
				cnt = 0;
				if (prews)
					val = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			} else if (cc == ' ' || cc == '\f') {
				cnt = 0;
				if (prews)
					val = "&nbsp;";
			} else {
				if (isMultiline())
					prews = false;

				if (maxword > 0 && ++cnt > maxword) {
					sb = alloc(sb, j);
					int ofs = -1;
					for (int k = sb.length(), n = maxword; --n >= 0;) {
						final char c2 = sb.charAt(--k);
						if (n > 0 && isEndSeparator(c2)) {
							sb.insert(ofs = k + 1, ' ');
							break;
						} else if (isSeparator(c2)) {
							sb.insert(ofs = k, ' ');
							break;
						}
					}
					if (ofs >= 0) {
						cnt = sb.length() - ofs;
					} else {
						if (isSeparator(cc))
							sb.append(' ');
						else
							sb.append("-<br/>");
						cnt = 1;
					}
				}
				switch (cc) {
				case '<':
					val = "&lt;";
					break;
				case '>':
					val = "&gt;";
					break;
				case '&':
					val = "&amp;";
					break;
				}
			}

			if (val != null)
				sb = alloc(sb, j).append(val);
			else if (sb != null)
				sb.append(cc);
		}
		return sb;
	}

	/**
	 * This is a clone method.
	 * 
	 * @see org.zkoss.zul.Label#isSeparator
	 */
	private static boolean isSeparator(char cc) {
		return cc <= 0x7f && (cc < '0' || cc > '9') && (cc < 'a' || cc > 'z')
				&& (cc < 'A' || cc > 'Z');
	}

	/**
	 * This is a clone method.
	 * 
	 * @see org.zkoss.zul.Label#isEndSeparator
	 */
	private static boolean isEndSeparator(char cc) {
		return cc == ',' || cc == ';' || cc == '.' || cc == '?' || cc == '!';
	}

	/**
	 * This is a clone method.
	 * 
	 * @see org.zkoss.zul.Label#alloc
	 */
	private StringBuffer alloc(StringBuffer sb, int e) {
		if (sb == null) {
			sb = new StringBuffer(getValue().length() + 10);
			sb.append(getValue().substring(0, e));
		}
		return sb;
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Inputable {
		// -- Inputable --//
		public void setTextByClient(String value) throws WrongValueException {
			_value = value;
		}
	}
}
