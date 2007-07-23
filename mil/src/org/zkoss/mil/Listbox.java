/* Listbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 14, 2007 5:03:14 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import java.util.AbstractSequentialList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Selectable;


/**
 * The Listbox item component that can be layout ouder Frame.
 * 
 * @author henrichen
 */
public class Listbox extends Item {
	private static final long serialVersionUID = 200706151102L;
	
	private static final int EXCLUSIVE = 1;
	private static final int MULTIPLE = 2;
	private static final int POPUP = 4;
	
	private String _wrap; //item's wrapping policy
	private boolean _multiple; //whether select multiple item
	private int _jsel = -1; //index of the selected item
	private transient Command _command;
	private transient List _items;
	private transient Set _selItems;
	private transient Set _roSelItems; //read only selected items
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;
	private int _commandSize; //how many commands children
	
	public Listbox() {
		init();
	}

	private void init() {
		_items = new AbstractSequentialList () {
			public ListIterator listIterator(int index) {
				return new ItemIter(index);
			}
			public Object get(int j) {
				final Object o = Listbox.this.getChildren().get(j);
				if (!(o instanceof Listitem))
					throw new IndexOutOfBoundsException("Wrong index: "+j);
				return o;
			}
			public int size() {
				int sz = getChildren().size();
				return sz - _commandSize;
			}
		};
		_selItems = new LinkedHashSet(5);
		_roSelItems = Collections.unmodifiableSet(_selItems);
	}
	
	/** whether allow select multiple line */
	public boolean isMultiple() {
		return _multiple;
	}
	
	public void setMultiple(boolean b) {
		if (b != _multiple) {
			_multiple = b;
			
			if (!"select".equals(getMold())) {
				smartUpdate("tp", getChoiceType());
			}
		}
	}
	
	/**
	 * Get the wrap policy ("on", "off", "default").
	 * @return current wrap policy.
	 */
	public String getWrap() {
		return _wrap;
	}
	/** 
	 * set the Listitem text wrapping policy.
	 * @param wrap can be "on", "off", or "default". default is mobile device implementation decided.
	 */
	public void setWrap(String wrap) {
		if (wrap != null && wrap.length() == 0)
			wrap = null;

		if (!Objects.equals(_wrap, wrap)) {
			_wrap = wrap;
			smartUpdate("fp", getFitPolicy());
		}
	}

	/** Returns a live list of all {@link Listitem}.
	 * By live we mean you can add or remove them directly with
	 * the List interface. In other words, you could add or remove
	 * an item by manipulating the returned list directly.
	 */
	public List getItems() {
		return _items;
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		return getItems().size();
	}
	/** Returns the item at the specified index.
	 */
	public Listitem getItemAtIndex(int index) {
		return (Listitem)getItems().get(index);
	}
	/** Returns the index of the specified item, or -1 if not found.
	 */
	public int getIndexOfItem(Listitem item) {
		return item == null ? -1: getItems().indexOf(item);
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
		final int sz = getItems().size();
		if (jsel >= sz)
			throw new UiException("Out of bound: "+jsel+" while size="+sz);
		if (jsel < -1) 
			jsel = -1;
		if (jsel < 0) { //unselct all
			clearSelection();
		} else if (jsel != _jsel
		|| (_multiple && _selItems.size() > 1)) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();

			_jsel = jsel;
			final Listitem item = getItemAtIndex(_jsel);
			item.setSelectedDirectly(true);
			_selItems.add(item);
			smartUpdate("selectedIndex", Integer.toString(_jsel));
		}
	}

	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #setSelectedItem}.
	 * @param item the item to select. If null, all items are deselected.
	 */
	public void selectItem(Listitem item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getParent() != this)
				throw new UiException("Not a child: "+item);
			if (_multiple || !item.isSelected())
				setSelectedIndex(getIndexOfItem(item));
		}
	}

	/** Selects the given item, without deselecting any other items
	 * that are already selected..
	 */
	public void addItemToSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (!item.isSelected()) {
			if (!_multiple) {
				selectItem(item);
			} else {
				final int index = getIndexOfItem(item);
				if (index < _jsel || _jsel < 0) {
					_jsel = index;
				}
				item.setSelectedDirectly(true);
				_selItems.add(item);
				smartUpdateSelection();
			}
		}
	}
	/**  Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				fixSelectedIndex(0);
				smartUpdateSelection();
			}
		}
	}

	/** Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		int sz = _selItems.size();
		
		if (sz > 0) {
			int j = 0;
			for (Iterator it = getItems().iterator(); it.hasNext() && sz > 0; ++j) {
				final Object item = it.next();
				if (_selItems.contains(item)) {
					if (sb.length() > 0) sb.append(',');
					sb.append(j);
					--sz;
				}
			}
			smartUpdate("chgSel", sb.toString());
		}
	}

	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the list box
	 * that are selected are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Listitem item) {
		if (item.isSelected()) removeItemFromSelection(item);
		else addItemToSelection(item);
	}
	/** Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_jsel = -1;
			smartUpdate("selectedIndex", "-1");
		}
	}
	/** Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "+this);

		if (getChildren().size() != _selItems.size()) {
			for (Iterator it = getChildren().iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				_selItems.add(item);
				item.setSelectedDirectly(true);
			}
			_jsel = getChildren().isEmpty() ? -1: 0;
			smartUpdate("selectAll", "true");
		}
	}

	/** Returns the selected item.
	 */
	public Listitem getSelectedItem() {
		return  _jsel >= 0 ?
			_jsel > 0 && _selItems.size() == 1 ? //optimize for performance
				(Listitem)_selItems.iterator().next():
				getItemAtIndex(_jsel): null;
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Listitem item) {
		selectItem(item);
	}

	/** Returns all selected items.
	 */
	public Set getSelectedItems() {
		return _roSelItems;
	}
	/** Returns the number of items being selected.
	 */
	public int getSelectedCount() {
		return _selItems.size();
	}

	/** Appends an item.
	 */
	public Listitem appendItem(String label, String value) {
		final Listitem item = new Listitem(label, value);
		item.applyProperties();
		item.setParent(this);
		return item;
	}
	/**  Removes the child item in the list box at the given index.
	 *
	 * @return the removed item.
	 */
	public Listitem removeItemAt(int index) {
		final Listitem item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	//-- Component --//
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate) super.smartUpdate(attr, value);
	}

	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "tp",  getChoiceType());
		HTMLs.appendAttribute(sb, "fp", getFitPolicy());
		return sb.toString();
	}

	public String getOuterAttrs() {
		final StringBuffer sb =	new StringBuffer(64).append(super.getOuterAttrs());
		
		appendAsapAttr(sb, "onSelect");

		return sb.toString();
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			//last: Commands
			if (refChild != null && refChild.getParent() != this)
				refChild = null; //Bug 1649625: it becomes the last child

			if (refChild == null) {
				if (_command != null) refChild = _command;
			} else if (refChild instanceof Command) {
				refChild = _command;
			}

			final Listitem newItem = (Listitem)newChild;
			final int jfrom = newItem.getParent() == this ? getIndexOfItem(newItem) : -1;

			if (super.insertBefore(newChild, refChild)) {
				final List children = getChildren();
				int jto = refChild instanceof Listitem ? getIndexOfItem((Listitem)refChild) : -1;
				
				//Maintain selected
				final int newIndex = getIndexOfItem(newItem);
				if (newItem.isSelected()) {
					if (_jsel < 0) {
						_jsel = newIndex;
						smartUpdate("z.selId", getSelectedId());
						_selItems.add(newItem);
					} else if (_multiple) {
						if (_jsel > newIndex) {
							_jsel = newIndex;
							smartUpdate("z.selId", getSelectedId());
						}
						_selItems.add(newItem);
					} else { //deselect
						newItem.setSelectedDirectly(false);
					}
				} else {
					final int oldjsel = _jsel;
					if (jfrom < 0) { //new child
						if (_jsel >= newIndex) ++_jsel;
					} else if (_jsel >= 0) { //any selected
						if (jfrom > _jsel) { //from below
							if (jto >= 0 && jto <= _jsel) ++_jsel;
						} else { //from above
							if (jto < 0 || jto > _jsel) --_jsel;
						}
					}

					if (oldjsel != _jsel)
						smartUpdate("z.selId", getSelectedId());
				}
				return true;
			}
			return false;
		} else if (newChild instanceof Command) {
			if (refChild instanceof Command) {
				_command = (Command)newChild;
			} else if (refChild instanceof Listitem) {
				refChild = _command;
				_command = (Command) newChild;
			} else if (_command == null) { //refChild == null
				_command = (Command) newChild;
			}
			++_commandSize;
			return super.insertBefore(newChild, refChild);
		} else {
			throw new UiException("Unsupported child for Listbox: "+newChild);
		}
	}
	public boolean removeChild(Component child) {
		final int index = child instanceof Listitem ? getIndexOfItem((Listitem)child) : -1;

		if (!super.removeChild(child))
			return false;

		if (child instanceof Command) {
			--_commandSize;
			if (_command == child) {
				_command = _commandSize > 0 ? (Command) getChildren().get(getItems().size()) : null;
			}
		} else if (child instanceof Listitem) {
			//maintain items
			final Listitem item = (Listitem)child;

			//Maintain selected
			if (item.isSelected()) {
				_selItems.remove(item);
				if (_jsel == index) {
					fixSelectedIndex(index);
					smartUpdate("z.selId", getSelectedId());
				}
			} else {
				if (_jsel >= index) {
					--_jsel;
					smartUpdate("z.selId", getSelectedId());
				}
			}
			return true;
		}
		invalidate();
		return true;
	}

	/** Returns the ID of the selected item (it is stored as the z.selId
	 * attribute of the listbox).
	 */
	private String getSelectedId() {
		final Listitem sel = getSelectedItem();
		return sel != null ? sel.getUuid(): "zk_n_a";
	}

	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the item.
	 */
	private void fixSelectedIndex(int j) {
		if (!_selItems.isEmpty()) {
			for (Iterator it = getChildren().listIterator(j); it.hasNext(); ++j) {
				final Listitem item = (Listitem)it.next();
				if (item.isSelected()) {
					_jsel = j;
					return;
				}
			}
		}
		_jsel = -1;
	}

	private int getFitPolicy() {
		if ("on".equals(_wrap) || "yes".equals(_wrap)) { //allow wrapping
			return 1;
		} else if ("off".equals(_wrap) || "no".equals(_wrap)) { //not allow wrapping
			return 2;
		}
		return 0; //default to zero
	}
	
	private int getChoiceType() {
		if ("select".equals(getMold())) {
			return POPUP;
		} else {
			return _multiple ? MULTIPLE : EXCLUSIVE;
		}
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl implements Selectable {
		//-- Selectable --//
		public void selectItemsByClient(Set selItems) {
			_noSmartUpdate = true;
			try {
				if (!_multiple || selItems == null || selItems.size() <= 1) {
					final Listitem item =
						selItems != null && selItems.size() > 0 ?
							(Listitem)selItems.iterator().next(): null;
					selectItem(item);
				} else {
					int j = 0;
					for (Iterator it = getChildren().iterator(); it.hasNext(); ++j) {
						final Listitem item = (Listitem)it.next();
						if (selItems.contains(item)) {
							addItemToSelection(item);
						} else {
							removeItemFromSelection(item);
						}
					}
				}
			} finally {
				_noSmartUpdate = false;
			}
		}
	}
	private class ItemIter implements ListIterator, java.io.Serializable {
		private ListIterator _it;
		private int _j;
		private boolean _bNxt;
		private ItemIter(int index) {
			_j = index;
		}
		public void add(Object o) {
			prepare();
			_it.add(o);
			++_j;
		}
		public boolean hasNext() {
			return _j < _items.size();
		}
		public boolean hasPrevious() {
			return _j > 0;
		}
		public Object next() {
			if (!hasNext())
				throw new NoSuchElementException();

			prepare();
			final Object o = _it.next();
			++_j;
			_bNxt = true;
			return o;
		}
		public Object previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();

			prepare();
			final Object o = _it.previous();
			--_j;
			_bNxt = false;
			return o;
		}
		public int nextIndex() {
			return _j;
		}
		public int previousIndex() {
			return _j - 1;
		}
		public void remove() {
			if (_it == null) throw new IllegalStateException();
			_it.remove();
			if (_bNxt) --_j;
		}
		public void set(Object o) {
			if (_it == null) throw new IllegalStateException();
			_it.set(o);
		}
		private void prepare() {
			if (_it == null) {
				int v = _j;
				_it = getChildren().listIterator(v);
			}
		}
	}
}
