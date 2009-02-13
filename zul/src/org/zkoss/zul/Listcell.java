/* Listcell.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:17     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A list cell.
 *
 * @author tomyeh
 */
public class Listcell extends LabelImageElement {
	private Object _value;
	private int _span = 1;

	public Listcell() {
	}
	public Listcell(String label) {
		setLabel(label);
	}
	public Listcell(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox)comp.getParent(): null;
	}
	/** Returns the list item that it belongs to.
	 * @deprecated As of release 2.4.1, due to confusion
	 */
	public Listitem getListitem() {
		return (Listitem)getParent();
	}
	
	protected String getRealStyle() {
		final Listheader h = getListheader();
		return isVisible() && h != null && !h.isVisible() ? super.getRealStyle() +
				"display:none;" : super.getRealStyle();
	}
	
	/** Returns the list header that is in the same column as
	 * this cell, or null if not available.
	 */
	public Listheader getListheader() {
		final Listbox listbox = getListbox();
		if (listbox != null) {
			final Listhead lcs = listbox.getListhead();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Listheader)lcschs.get(j);
			}
		}
		return null;
	}
	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Returns the maximal length for this cell.
	 * If listbox's mold is "select", it is the same as
	 * {@link Listbox#getMaxlength}
	 * If not, it is the same as the correponding {@link #getListheader}'s 
	 * {@link Listheader#getMaxlength}.
	 *
	 * <p>Note: {@link Listitem#getMaxlength} is the same as {@link Listbox#getMaxlength}.
	 */
	public int getMaxlength() {
		final Listbox listbox = getListbox();
		if (listbox == null)
			return 0;
		if (listbox.inSelectMold())
			return listbox.getMaxlength();
		final Listheader lc = getListheader();
		return lc != null ? lc.getMaxlength(): 0;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", Integer.toString(_span));
		}
	}

	//-- super --//
	public void setWidth(String width) {
		throw new UnsupportedOperationException("Set listheader's width instead");
	}

	//-- Internal use only --//
	/** Returns the prefix of the first column (in HTML tags), null if this
	 * is not first column. Called only by listcell.dsp.
	 */
	public String getColumnHtmlPrefix() {
		final Listitem item = getListitem();
		final Listbox listbox = getListbox();
		if (listbox != null && item.getFirstChild() == this) {
			final StringBuffer sb = new StringBuffer(64);
			if (listbox.isCheckmark()) {
				if (item.isCheckable()) {
					sb.append("<input type=\"").append(listbox.isMultiple() ? "checkbox": "radio")
						.append('"');
					if (item.isDisabled())
						sb.append(" disabled=\"disabled\"");
					if (item.isSelected())
						sb.append(" checked=\"checked\"");
					if (!listbox.isMultiple()) 
						sb.append(" name=\"").append(listbox.getUuid()).append("\"");
	
					sb.append(" id=\"").append(item.getUuid())
						.append("!cm\" z.type=\"Lcfc\"/>");
				} else {
					sb.append("<span class=\"checkmark-spacer\"></span>");
				}
				return sb.toString();
			} else if (isFocusRequired(listbox, item)) {
				sb.append("<a href=\"javascript:;\" id=\"").append(item.getUuid())
					.append("!sel\" z.type=\"Lcfc\"> </a>");
				return sb.toString();
			}
		}
		
		//To make the listbox's height more correct, we have to generate &nbsp;
		//for empty cell. Otherwise, IE will make the height too small
		final boolean empty = getImage() == null
		&& getLabel().length() == 0 && getChildren().isEmpty();
		return empty ? "&nbsp;": null;
		
	}
	/** Returns the postfix of the first column (in HTML tags), null if this
	 * is not first column. Called only by listcell.jsp.
	 */
	public String getColumnHtmlPostfix() {
		return null;
	}
	/** Returns whether this cell requires focus.
	 */
	private boolean isFocusRequired(Listbox listbox, Component parent) {
		final Listitem sel = listbox.getSelectedItem();
		return parent == sel
			|| (sel == null && ((Listitem)parent).getIndex() == 0);
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();

		final Listheader header = getListheader();
		final String clkattrs = getAllOnClickAttrs();
		if (header == null && clkattrs == null && _span == 1)
			return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		if (header != null) sb.append(header.getColAttrs());
		if (clkattrs != null) sb.append(clkattrs);
		if (_span != 1) HTMLs.appendAttribute(sb, "colspan", _span);

		return sb.toString();
	}

	/** Returns the attributes used by the embedded HTML LABEL tag.
	 * It returns text-relevant styles only.
	 * <p>Used only by component developer.
	 */
	public String getLabelAttrs() {
		final String style = HTMLs.getTextRelevantStyle(getRealStyle());
		return style.length() > 0 ? " style=\""+style+'"': "";
	}
	
	//-- Component --//
 	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listitem))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public void invalidate() {
		final Listbox listbox = getListbox();
		if (listbox != null && listbox.inSelectMold()) {
			getParent().invalidate();
			//if HTML select, the cell doesn't exists in client
		} else {
			super.invalidate();
		}
	}
}
