/* Select.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/6/1 下午 3:39:18 , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.impl.XulElement;

/**
 * The Select object represents a dropdown list in an HTML form.
 * 
 * For each instance of an HTML <code>select</code> tag in a form, a Select
 * object is created.
 * <p>
 * Default {@link #getZclass}: z-select.
 * 
 * @author jumperchen
 * @since 5.0.0
 * 
 */
public class Select extends XulElement {
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;
	private boolean _multiple, _disabled;
	private int _size, _jsel = -1;;
	/** A list of selected items. */
	private transient Set _selItems;
	/** A readonly copy of {@link #_selItems}. */
	private transient Set _roSelItems;
	private int _tabindex = -1;
	/** The name. */
	private String _name;
	
	static {
		addClientEvent(Select.class, Events.ON_SELECT, CE_IMPORTANT);
	}
	public Select() {
		init();
	}
	private void init() {
		_selItems = new LinkedHashSet(5);
		_roSelItems = Collections.unmodifiableSet(_selItems);
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
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public final boolean isDisabled() {
		return _disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}
	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			smartUpdate("tabindex", _tabindex);
		}
	}
	/** Returns the size. Zero means no limitation.
	 * <p>Default: 0.
	 */
	public int getSize() {
		return _size;
	}
	/** Sets the size.
	 */
	public void setSize(int size) throws WrongValueException {
		if (size < 0)
			throw new WrongValueException("Illegal rows: "+size);

		if (_size != size) {
			_size = size;
			smartUpdate("size", _size);
		}
	}

	/** Returns whether multiple selections are allowed.
	 * <p>Default: false.
	 */
	public boolean isMultiple() {
		return _multiple;
	}
	/** Sets whether multiple selections are allowed.
	 */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			if (!_multiple && _selItems.size() > 1) {
				final Option item = getSelectedItem();
				for (Iterator it = _selItems.iterator(); it.hasNext();) {
					final Option li = (Option)it.next();
					if (li != item) {
						li.setSelectedDirectly(false);
						it.remove();
					}
				}
				//No need to update selId because multiple will do the job at client
			}

			smartUpdate("multiple", _multiple);
		}
	}

	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the list box
	 * that are selected are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Option item) {
		if (item.isSelected()) removeItemFromSelection(item);
		else addItemToSelection(item);
	}

	/**  Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelection(Option item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				fixSelectedIndex(0);
				item.smartUpdate("selected", false);				
			}
		}
	}

	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the item.
	 */
	private void fixSelectedIndex(int j) {
		if (!_selItems.isEmpty()) {
			for (Iterator it = getChildren().listIterator(j); it.hasNext(); ++j) {
				final Option item = (Option)it.next();
				if (item.isSelected()) {
					_jsel = j;
					return;
				}
			}
		}
		_jsel = -1;
	}
	
	/** Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Option item = (Option)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_jsel = -1;
			smartUpdate("selectedIndex", -1);
		}
	}
	/** Selects the given item, without deselecting any other items
	 * that are already selected..
	 */
	public void addItemToSelection(Option item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (!item.isSelected()) {
			if (!_multiple) {
				selectItem(item);
			} else {
				int index = getChildren().indexOf(item);
				if (index < _jsel || _jsel < 0) {
					_jsel = index;
				}
				item.setSelectedDirectly(true);
				_selItems.add(item);
				item.smartUpdate("selected", true);
				
			}
		}
	}
	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", name);
		}
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
		if (jsel >= getChildren().size())
			throw new UiException("Out of bound: "+jsel+" while size="+getChildren().size());

		if (jsel < -1) 
			jsel = -1;
		if (jsel < 0) { //unselct all
			clearSelection();
		} else if (jsel != _jsel
		|| (_multiple && _selItems.size() > 1)) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Option item = (Option)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();

			_jsel = jsel;
			final Option item = (Option)getChildren().get(_jsel);
			item.setSelectedDirectly(true);
			_selItems.add(item);
			smartUpdate("selectedIndex", _jsel);
		}
	}
	

	public String getZclass() {
		return _zclass == null ? "z-select" : _zclass;
	}

	/** Returns the selected item.
	 */
	public Option getSelectedItem() {
		return  _jsel >= 0 ?
			_jsel > 0 && _selItems.size() == 1 ? //optimize for performance
				(Option)_selItems.iterator().next():
				(Option)getChildren().get(_jsel): null;
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Option item) {
		selectItem(item);
	}

	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #setSelectedItem}.
	 * @param item the item to select. If null, all items are deselected.
	 */
	public void selectItem(Option item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getParent() != this)
				throw new UiException("Not a child: "+item);
			if (_multiple || !item.isSelected())
				setSelectedIndex(getChildren().indexOf(item));
		}
	}
	
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		render(renderer, "name", _name);
		if (_tabindex > 0)
			renderer.render("size", getSize());
		render(renderer, "multiple", isMultiple());
		render(renderer, "disabled", isDisabled());
		if (_tabindex >= 0)
			renderer.render("tabindex", getTabindex());
	}
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SELECT)) {
			SelectEvent evt = SelectEvent.getSelectEvent(request);
			Set selItems = evt.getSelectedItems();
			_noSmartUpdate = true;
			try {
				if (!_multiple
				|| (selItems == null || selItems.size() <= 1)) {
					final Option item =
						selItems != null && selItems.size() > 0 ?
							(Option)selItems.iterator().next(): null;
					selectItem(item);
				} else {
					int j = 0;
					for (Iterator it = getChildren().iterator(); it.hasNext(); ++j) {
						final Option item = (Option)it.next();
						if (selItems.contains(item)) {
							addItemToSelection(item);
						} else 
							removeItemFromSelection(item);
					}
				}
			} finally {
				_noSmartUpdate = false;
			}

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Option))
			throw new UiException("Unsupported child for select: "+child);
		super.beforeChildAdded(child, refChild);
	}
	
	public boolean insertBefore(Component newChild, Component refChild) {
		final Option newItem = (Option)newChild;
		final int jfrom = newItem.getParent() == this ? getChildren().indexOf(newItem): -1;
		if (super.insertBefore(newChild, refChild)) {
			//Maintain _items
			final int
				jto = refChild instanceof Option ?
						getChildren().indexOf(refChild): -1;
						
			//Maintain selected
			final int newIndex = getChildren().indexOf(newItem);
			if (newItem.isSelected()) {
				if (_jsel < 0) {
					_jsel = newIndex;
					_selItems.add(newItem);
				} else if (_multiple) {
					if (_jsel > newIndex) {
						_jsel = newIndex;
					}
					_selItems.add(newItem);
				} else { //deselect
					newItem.setSelectedDirectly(false);
				}
			} else {
				if (jfrom < 0) { //no existent child
					if (_jsel >= newIndex) ++_jsel;
				} else if (_jsel >= 0) { //any selected
					if (jfrom > _jsel) { //from below
						if (jto >= 0 && jto <= _jsel) ++_jsel;
					} else { //from above
						if (jto < 0 || jto > _jsel) --_jsel;
					}
				}
			}
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		int index = getChildren().indexOf(child);
		if (!super.removeChild(child))
			return false;
		Option item = (Option) child;
		//Maintain selected
		if (item.isSelected()) {
			_selItems.remove(item);
			if (_jsel == index) 
				fixSelectedIndex(index);
		} else {
			if (_jsel >= index) {
				--_jsel;
			}
		}
		return true;
	}
	
	//Cloneable//
	public Object clone() {
		final Select clone = (Select)super.clone();
		clone.init();
		clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Option) {
				final Option li = (Option)child;
				if (li.isSelected()) {
					_selItems.add(li);
				}
			}
		}
	}

	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		afterUnmarshal();
	}
}
