/* Combobox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:01     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;

import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.event.Events;

/**
 * A combo box.
 *
 * <p>Non-XUL extension. It is used to replace XUL menulist. This class
 * is more flexible than menulist, such as {@link #setAutocomplete}
 * {@link #setAutodrop}.
 *
 * <p>Default {@link #getSclass}: combobox.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see Comboitem
 */
public class Combobox extends Textbox {
	private boolean _autodrop, _autocomplete;

	public Combobox() {
		setSclass("combobox");
	}
	public Combobox(String value) throws WrongValueException {
		this();
		setValue(value);
	}

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}
	/** Sets whether to automatically drop the list if users is changing
	 * this text box.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("zk_adr", autodrop);
		}
	}
	/** Returns whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 *
	 * <p>Default: false.
	 *
	 * <p>If true, the nearest item will be searched and the text box is
	 * updated automatically.
	 * If false, user has to click the item or use the DOWN or UP keys to
	 * select it back.
	 *
	 * <p>Note: this feature is reserved and not yet implemented.
	 * Don't confuse it with the auto-completion feature mentioned by
	 * other framework. Such kind of auto-completion is supported well
	 * by listening to the onChanging event.
	 */
	public boolean isAutocomplete() {
		return _autocomplete;
	}
	/** Sets whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 */
	public void setAutocomplete(boolean autocomplete) {
		if (_autocomplete != autocomplete) {
			_autocomplete = autocomplete;
			smartUpdate("zk_aco", autocomplete);
		}
	}

	/** Returns a 'live' list of all {@link Comboitem}.
	 * By live we mean you can add or remove them directly with
	 * the List interface.
	 *
	 * <p>Currently, it is the same as {@link #getChildren}. However,
	 * we might add other kind of children in the future.
	 */
	public List getItems() {
		return getChildren();
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		return getChildren().size();
	}
	/** Returns the item at the specified index.
	 */
	public Comboitem getItemAtIndex(int index) {
		return (Comboitem)getItems().get(index);
	}

	/** Appends an item.
	 */
	public Comboitem appendItem(String label) {
		final Comboitem item = new Comboitem(label);
		item.setParent(this);
		return item;
	}
	/**  Removes the child item in the list box at the given index.
	 * @return the removed item.
	 */
	public Comboitem removeItemAt(int index) {
		final Comboitem item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	//-- super --//
	public void setMultiline(boolean multiline) {
		if (multiline)
			throw new UnsupportedOperationException("multiline");
	}
	public void setRows(int rows) throws WrongValueException {
		if (rows != 1)
			throw new UnsupportedOperationException("rows");
	}

	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final boolean aco = isAutocomplete(), adr = isAutodrop();
		if (!isAsapRequired(Events.ON_OPEN) && !aco && !adr)
			return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		appendAsapAttr(sb, Events.ON_OPEN);
		if (aco) HTMLs.appendAttribute(sb, "zk_aco", "true");
		if (adr) HTMLs.appendAttribute(sb, "zk_adr", "true");
		return sb.toString();
	}
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final String style = getInnerStyle();
		return style.length() > 0 ? attrs+" style=\""+style+'"': attrs;
	}
	private String getInnerStyle() {
		final StringBuffer sb = new StringBuffer(32)
			.append(HTMLs.getTextRelevantStyle(getRealStyle()));
		HTMLs.appendStyle(sb, "width", getWidth());
		HTMLs.appendStyle(sb, "height", getHeight());
		return sb.toString();
	}
	/** Returns RS_NO_WIDTH|RS_NO_HEIGHT.
	 */
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags()|RS_NO_WIDTH|RS_NO_HEIGHT;
	}

	//-- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		if (!(newChild instanceof Comboitem))
			throw new UiException("Unsupported child for Combobox: "+newChild);
		return super.insertBefore(newChild, refChild);
	}
	/** Childable. */
	public boolean isChildable() {
		return true;
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		smartUpdate("repos", "true");
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		smartUpdate("repos", "true");
	}
}
