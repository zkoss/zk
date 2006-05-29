/* Radiogroup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:41     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.Writer;
import java.io.IOException;

import com.potix.lang.Strings;
import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.EventListener;

import com.potix.zul.html.impl.XulElement;

/**
 * A radio group.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/29 04:28:26 $
 */
public class Radiogroup extends XulElement {
	private String _orient = "horizontal";
	/** The name of all child radio buttons. */
	private String _name;
	private int _jsel = -1;
	private final EventListener _listener = new EventListener() {
		public void onEvent(Event event) {
			Events.sendEvent(Radiogroup.this, event);
		}
		public boolean isAsap() {
			return Events.isListenerAvailable(Radiogroup.this, "onCheck", true);
		}
	};

	public Radiogroup() {
		_name = nextRadioName();
	}
	private String nextRadioName() {
		final String attr = "com.potix.zul.html.radio.name";
		final Integer v = (Integer)getAttribute(attr, DESKTOP_SCOPE);
		final int i = v != null ? v.intValue(): 0;
		setAttribute(attr, new Integer(i + 1), DESKTOP_SCOPE);
		return Strings.encode(new StringBuffer(12).append("_pg"), i).toString();
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate(INNER);
		}
	}

	/** Returns the item at the specified index.
	 */
	public Radio getItemAtIndex(int index) {
		return (Radio)getChildren().get(index);
	}
	/** Returns the index of the selected item (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}
	/** Deselects all of the currently selected items and selects
	 * the item with the given index.
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel < 0) jsel = -1;
		if (_jsel != jsel) {
			if (jsel < 0) {
				getSelectedItem().setSelected(false);
			} else {
				getItemAtIndex(jsel).setSelected(true);
			}
		}
	}
	/** Returns the selected item.
	 */
	public Radio getSelectedItem() {
		return _jsel >= 0 ? getItemAtIndex(_jsel): null;
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 */
	public void setSelectedItem(Radio item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getParent() != this)
				throw new UiException("Not a child: "+item);
			item.setSelected(true);
		}
	}

	/** Appends an item.
	 */
	public Radio appendItem(String label, String value) {
		final Radio item = new Radio();
		item.setLabel(label);
		item.setValue(value);
		item.setParent(this);
		return item;
	}
	/**  Removes the child item in the list box at the given index.
	 * @return the removed item.
	 */
	public Radio removeItemAt(int index) {
		final Radio item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	/** Returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Default: automatically generated an unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Radio) {
			if (super.insertBefore(child, insertBefore)) {
				final Radio childItem = (Radio)child;
				if (_jsel >= 0 && childItem.isSelected()) {
					childItem.setSelected(false); //it will call fixSelectedIndex
				} else {
					fixSelectedIndex();
				}
				child.addEventListener("onCheck", _listener);
				return true;
			}
			return false;
		} else {
			throw new UiException("Unsupported child for Radiogroup: "+child);
		}
	}
	public boolean removeChild(Component child) {
		final boolean ret = super.removeChild(child);
		if (ret && (child instanceof Radio)) { //might have diff child in future
			final Radio childItem = (Radio)child;
			if (childItem.isSelected()) {
				_jsel = -1;
			} else if (_jsel > 0) { //excluding 0
				fixSelectedIndex();
			}
			child.removeEventListener("onCheck", _listener);
		}
		return ret;
	}
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the item.
	 */
	/*package*/ void fixSelectedIndex() {
		_jsel = -1;
		int j = 0;
		for (Iterator it = getChildren().listIterator(j); it.hasNext(); ++j) {
			final Radio item = (Radio)it.next();
			if (item.isSelected()) {
				_jsel = j;
				return;
			}
		}
	}
}
