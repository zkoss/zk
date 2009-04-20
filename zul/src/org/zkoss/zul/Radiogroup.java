/* Radiogroup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:41     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;
import org.zkoss.lang.MutableInteger;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;

import org.zkoss.zul.impl.XulElement;

/**
 * A radio group.
 *
 * <p>Note: To support the versatile layout, a radio group accepts any kind of
 * children, including {@link Radio}. On the other hand, the parent of
 * a radio, if any, must be a radio group.
 *
 * @author tomyeh
 */
public class Radiogroup extends XulElement implements org.zkoss.zul.api.Radiogroup {
	private String _orient = "horizontal";
	/** The name of all child radio buttons. */
	private String _name;
	private int _jsel = -1;

	public Radiogroup() {
		_name = genGroupName();
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
			invalidate();
		}
	}

	/** Returns the radio button at the specified index.
	 */
	public Radio getItemAtIndex(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Wrong index: "+index);

		final MutableInteger cur = new MutableInteger(0);
		final Radio radio = getAt(this, cur, index);
		if (radio == null)
			throw new IndexOutOfBoundsException(index+" out of 0.."+(cur.value-1));
		return radio;
	}
	/** Returns the radio button at the specified index.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Radio getItemAtIndexApi(int index) {
		return getItemAtIndex(index);
	}
	private static Radio getAt(Component comp, MutableInteger cur, int index) {
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio) {
				if (cur.value++ == index)
					return (Radio)child;
			} else if (!(child instanceof Radiogroup)) { //skip nested radiogroup
				Radio r = getAt(child, cur, index);
				if (r != null) return r;
			}
		}
		return null;
	}

	/** Returns the number of radio buttons in this group.
	 */
	public int getItemCount() {
		return countItems(this);
	}
	private static int countItems(Component comp) {
		int sum = 0;
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio)
				++sum;
			else if (!(child instanceof Radiogroup)) //skip nested radiogroup
				sum += countItems(child);
		}
		return sum;
	}
	 
	/** Returns the index of the selected radio button (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}
	/** Deselects all of the currently selected radio button and selects
	 * the radio button with the given index.
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
	/** Returns the selected radio button.
	 */
	public Radio getSelectedItem() {
		return _jsel >= 0 ? getItemAtIndex(_jsel): null;
	}
	/** Returns the selected radio button.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Radio getSelectedItemApi() {
		return getSelectedItem();
	}
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 */
	public void setSelectedItem(Radio item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getRadiogroup() != this)
				throw new UiException("Not a child: "+item);
			item.setSelected(true);
		}
	}
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 * @param itemApi assume as a {@link org.zkoss.zul.Radio}   
	 * @since 3.5.2
	 */
	public void setSelectedItemApi(org.zkoss.zul.api.Radio itemApi) {
		Radio item = (Radio) itemApi;
		setSelectedItem(item);
	}

	/** Appends a radio button.
	 */
	public Radio appendItem(String label, String value) {
		final Radio item = new Radio();
		item.setLabel(label);
		item.setValue(value);
		item.setParent(this);
		return item;
	}
	/** Appends a radio button.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Radio appendItemApi(String label, String value) {
		return appendItem(label, value);
	}
	/**  Removes the child radio button in the list box at the given index.
	 * @return the removed radio button.
	 */
	public Radio removeItemAt(int index) {
		final Radio item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}
	/**  Removes the child radio button in the list box at the given index.
	 * @return the removed radio button.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Radio removeItemAtApi(int index) {
		return removeItemAt(index);
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

	//utilities for radio//
	/** Called when a radio is added to this group.
	 */
	/*package*/ void fixOnAdd(Radio child) {
		if (_jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call fixSelectedIndex
		} else {
			fixSelectedIndex();
		}
	}
	/** Called when a radio is removed from this group.
	 */
	/*package*/ void fixOnRemove(Radio child) {
		if (child.isSelected()) {
			_jsel = -1;
		} else if (_jsel > 0) { //excluding 0
			fixSelectedIndex();
		}
	}
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the radio button.
	 */
	/*package*/ void fixSelectedIndex() {
		_jsel = fixSelIndex(this, new MutableInteger(0));
	}
	private static int fixSelIndex(Component comp, MutableInteger cur) {
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio) {
				if (((Radio)child).isSelected())
					return cur.value;
				++cur.value;
			} else if (!(child instanceof Radiogroup)) { //skip nested radiogroup
				int jsel = fixSelIndex(child, cur);
				if (jsel >= 0) return jsel;
			}
		}
		return -1;
	}

	/** Generates the group name for child radio buttons.
	 */
	private String genGroupName() {
		return Strings.encode(new StringBuffer(16).append("_pg"),
			System.identityHashCode(this)).toString();
	}

	//Cloneable//
	public Object clone() {
		final Radiogroup clone = (Radiogroup)super.clone();
		fixClone(clone);
		return clone;
	}
	private static void fixClone(Radiogroup clone) {
		if (clone._name.startsWith("_pg")) clone._name = clone.genGroupName();
	}


}
