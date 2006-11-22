/* Radiogroup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:41     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.impl.XulElement;

/**
 * A radio group.
 *
 * @author tomyeh
 */
public class Radiogroup extends XulElement {
	private String _orient = "horizontal";
	/** The name of all child radio buttons. */
	private String _name;
	private int _jsel = -1;
	private transient EventListener _listener;

	public Radiogroup() {
		_name = genGroupName();
		init();
	}
	private void init() {
		_listener = new EventListener() {
			public void onEvent(Event event) {
				Events.sendEvent(Radiogroup.this, event);
			}
			public boolean isAsap() {
				return Events.isListenerAvailable(Radiogroup.this, Events.ON_CHECK, true);
			}
		};
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
		//Note: getChildren().get(index) doesn't perform better due to linked list
		int cnt = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if ((child instanceof Radio) && cnt++ == index)
				return (Radio)child;
		}
		throw new IndexOutOfBoundsException(index+" out of "+cnt);
	}
	/** Returns the number of radio buttons in this group.
	 */
	public int getItemCount() {
		int cnt = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext();)
			if (it.next() instanceof Radio)
				++cnt;
		return cnt;
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
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
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

	/** Appends a radio button.
	 */
	public Radio appendItem(String label, String value) {
		final Radio item = new Radio();
		item.setLabel(label);
		item.setValue(value);
		item.setParent(this);
		return item;
	}
	/**  Removes the child radio button in the list box at the given index.
	 * @return the removed radio button.
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
		if (super.insertBefore(child, insertBefore)) {
		//To have more versatile layout, a radio group accepts any kind of child
		//However, radio's parent must be radiogroup due to difficulty of handling
		//_jsel and event listener
			if (child instanceof Radio) {
				final Radio radio = (Radio)child;
				if (_jsel >= 0 && radio.isSelected()) {
					radio.setSelected(false); //it will call fixSelectedIndex
				} else {
					fixSelectedIndex();
				}
				child.addEventListener(Events.ON_CHECK, _listener);
			}
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		final boolean ret = super.removeChild(child);
		if (ret && (child instanceof Radio)) { //might have diff child in future
			final Radio radio = (Radio)child;
			if (radio.isSelected()) {
				_jsel = -1;
			} else if (_jsel > 0) { //excluding 0
				fixSelectedIndex();
			}
			child.removeEventListener(Events.ON_CHECK, _listener);
		}
		return ret;
	}
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the radio button.
	 */
	/*package*/ void fixSelectedIndex() {
		_jsel = -1;
		int j = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Radio) {
				final Radio radio = (Radio)child;
				if (radio.isSelected()) {
					_jsel = j;
					return;
				}
				++j;
			}
		}
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

		//remove listener from children first
		for (Iterator it = clone.getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Radio) {
				final Radio radio = (Radio)it.next();
				radio.removeEventListener(Events.ON_CHECK, clone._listener);
			}
		}

		//create and add back listener
		clone.init();
		clone.afterUnmarshal();
	}

	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Radio) {
				final Radio radio = (Radio)it.next();
				radio.addEventListener(Events.ON_CHECK, _listener);
			}
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
		afterUnmarshal();
		//Issue:
		//if we re-generate the group name, client will mismatch with
		//the server when the component is deserialized by Web Container.
		//If we don't, the group name might be conflict when developer
		//deserializes explicitly and add back the same desktop
	}
}
