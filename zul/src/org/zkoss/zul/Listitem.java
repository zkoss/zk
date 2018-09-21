/* Listitem.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 17:38:52     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.Serializable;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LoadStatus;
import org.zkoss.zul.impl.XulElement;

/**
 * A list item.
 *
 * <p>Default {@link #getZclass}: z-listitem (since 5.0.0)
 *
 * @author tomyeh
 */
public class Listitem extends XulElement {

	private transient Object _value;
	/** The index in the parent (only for implementation purpose). */
	private int _index = -1; //no parent at beginning
	private boolean _selected, _disabled, _selectable = true;
	/** whether the content of this item is loaded; used if
	 * the listbox owning this item is using a list model.
	 */
	private boolean _loaded;

	private boolean _itemInvalid = false; // for ROD

	public Listitem() {
	}

	public Listitem(String label) {
		setLabel(label);
	}

	public <T> Listitem(String label, T value) {
		setLabel(label);
		setValue(value);
	}

	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Listbox getListbox() {
		return (Listbox) getParent();
	}

	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @since 3.5.0
	 */
	public Listgroup getListgroup() {
		final Listbox lb = getListbox();
		if (lb != null)
			return lb.getListgroupAt(getIndex());
		return null;
	}

	public String getZclass() {
		return _zclass == null ? "z-listitem" : _zclass;
	}

	/**
	 * @deprecated As of release 8.0.0, please use {@link #isSelectable()}
	 */
	public boolean isCheckable() {
		return isSelectable();
	}

	/**
	 * @deprecated As of release 8.0.0, please use {@link #setSelectable(boolean)}
	 */
	public void setCheckable(boolean checkable) {
		setSelectable(checkable);
	}

	/**
	 * Returns whether it is selectable.
	 * <p>Default: true.</p>
	 * @since 8.0.0
	 */
	public boolean isSelectable() {
		return _selectable;
	}

	/** Sets whether it is selectable.
	 *
	 * <p>If the listbox is in a checkmark mode, the selectable state will affect
	 * the checkable icon to display or not.</p>
	 * <p>Default: true.</p>
	 * @param selectable
	 */
	public void setSelectable(boolean selectable) {
		if (_selectable != selectable) {
			_selectable = selectable;

			// non-checkable cannot be selected
			if (!_selectable)
				setSelected(false);
			smartUpdate("selectable", selectable);
		}
	}

	/** Returns the maximal length of each item's label.
	 * It is a shortcut of getParent().getMaxlength();
	 * Thus, it works only if the listbox's mold is "select".
	 */
	public int getMaxlength() {
		final Listbox listbox = getListbox();
		return listbox != null ? listbox.getMaxlength() : 0;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) _value;
	}

	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	public <T> void setValue(T value) {
		if (!Objects.equals(_value, value)) {
			_value = value;

			final Listbox listbox = getListbox();
			if (listbox != null)
				if (listbox.inSelectMold())
					smartUpdate("value", _value);
				else if (listbox.getName() != null)
					smartUpdate("value", _value);
		}
	}

	/**
	 * Please use {@link Listcell} or {@link Listbox} instead.
	 * @deprecated as of release 7.0.3.
	 */
	public void setStubonly(String stubonly) {
		super.setStubonly(stubonly);
	}

	/**
	 * Please use {@link Listcell} or {@link Listbox} instead.
	 * @deprecated as of release 7.0.3.
	 */
	public void setStubonly(boolean stubonly) {
		super.setStubonly(stubonly);
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
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

	/** Returns whether it is selected.
	 * <p>Default: false.
	 */
	public boolean isSelected() {
		return _selected;
	}

	/** Sets whether it is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Listbox listbox = (Listbox) getParent();
			if (listbox != null) {
				//Note: we don't update it here but let its parent does the job
				listbox.toggleItemSelection(this);
			} else {
				_selected = selected;
			}
		}
	}

	/** Returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		final Listcell cell = (Listcell) getFirstChild();
		return cell != null ? cell.getLabel() : null;
	}

	/** Sets the label of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setLabel(label);
	}

	private Listcell autoFirstCell() {
		Listcell cell = (Listcell) getFirstChild();
		if (cell == null) {
			cell = new Listcell();
			cell.applyProperties();
			cell.setParent(this);
		}
		return cell;
	}

	/** @deprecated As of release 3.5.0, it is redundant since it
	 * is the same {@link #getImage}.
	 */
	public String getSrc() {
		return getImage();
	}

	/** @deprecated As of release 3.5.0, it is redundant since it
	 * is the same {@link #getSrc}.
	 */
	public void setSrc(String src) {
		setImage(src);
	}

	/** Returns the image of the {@link Listcell} it contains.
	 */
	public String getImage() {
		final Listcell cell = (Listcell) getFirstChild();
		return cell != null ? cell.getImage() : null;
	}

	/** Sets the image of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setImage(String image) {
		autoFirstCell().setImage(image);
	}

	/** Returns the index of this item (a.k.a., the order in the listbox).
	 */
	public int getIndex() {
		return _index;
	}
	/** Sets whether the content of this item is loaded; used if
	 * the listbox owning this item is using a list model.
	 */
	/*package*/ void setLoaded(boolean loaded) {
		if (loaded != _loaded) {
			_loaded = loaded;

			final Listbox listbox = getListbox();
			if (listbox != null && listbox.getModel() != null)
				smartUpdate("_loaded", _loaded);
		}
	}

	/** Returns whether the content of this item is loaded.
	 * It is meaningful only if {@link #getListbox} is live data,
	 * i.e., {@link Listbox#getModel} is not null.
	 *
	 * @since 2.4.0
	 */
	public boolean isLoaded() {
		return _loaded;
	}

	public boolean isItemInvalid() {
		return _itemInvalid;
	}

	public void setItemInvalid(boolean itemInvalid) {
		if (_itemInvalid != itemInvalid) {
			_itemInvalid = itemInvalid;
		}
	}
	
	//-- Utilities for implementation only (called by Listbox) */
	/*package*/ void setIndexDirectly(int index) {
		setIndex(index);
	}

	protected void setIndex(int index) {
		_index = index;
	}

	/*package*/ void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	public boolean setVisible(boolean visible) {
		if (isVisible() == visible)
			return visible;

		final boolean result = super.setVisible(visible);
		final Listbox listbox = (Listbox) getParent();
		if (listbox != null) {
			if (listbox.inSelectMold())
				listbox.invalidate();
			final Listgroup g = listbox.getListgroupAt(getIndex());
			if (g == null || g.isOpen())
				listbox.addVisibleItemCount(visible ? 1 : -1);
		}

		// Bug ZK-485, send after invoking listbox.addVisibleItemCount()
		smartUpdate("visible", visible);
		return result;
	}

	protected void smartUpdate(String name, Object value) { //make it accessible in this package
		Listbox box = getListbox();
		if (isVisible() || box == null || !box.inSelectMold())
			super.smartUpdate(name, value);
	}

	protected void smartUpdate(String name, boolean value) { //make it accessible in this package
		Listbox box = getListbox();
		if (isVisible() || box == null || !box.inSelectMold())
			super.smartUpdate(name, value);
	}

	protected void smartUpdate(String name, int value) {
		Listbox box = getListbox();
		if (isVisible() || box == null || !box.inSelectMold())
			super.smartUpdate(name, value);
	}

	public String getMold() {
		return getParent() != null ? "select".equals(getParent().getMold()) ? "select" : super.getMold()
				: super.getMold();
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Listbox related 
	 * components, please refer to {@link Listbox} and {@link Listheader} instead.
	 */
	public void setWidth(String width) {
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Listbox related 
	 * components, please refer to {@link Listbox} and {@link Listheader} instead.
	 */
	public void setHflex(String flex) {
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "selected", isSelected());
		render(renderer, "disabled", isDisabled());
		render(renderer, "_loaded", _loaded ? _loaded : getListbox().getModel() == null);
		renderer.render("_index", _index);

		if (_value instanceof String && getListbox().getName() != null)
			render(renderer, "value", _value);

		if (!isCheckable())
			renderer.render("checkable", false);
	}

	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		super.addMoved(oldparent, oldpg, newpg);
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Listitem's parent must be Listbox, not " + parent);
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Listcell))
			throw new UiException("Unsupported child for listitem: " + child);
		super.beforeChildAdded(child, refChild);
	}

	// -- Serializable --//
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		if (_value instanceof Serializable) {
			s.writeBoolean(true);
			s.writeObject(_value);
		} else {
			s.writeBoolean(false);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		if (s.readBoolean())
			_value = s.readObject();
	}

	//Clone//
	public Object clone() {
		final Listitem clone = (Listitem) super.clone();
		clone._index = -1;
		//note: we have to reset, since listbox.insertBefore assumes
		//that a parent-less listitem's index is -1
		return clone;
	}

	//-- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements LoadStatus {
		//-- LoadStatus --//
		public boolean isLoaded() {
			return Listitem.this.isLoaded();
		}

		public void setLoaded(boolean loaded) {
			Listitem.this.setLoaded(loaded);
		}

		public void setIndex(int index) {
			Listitem.this.setIndex(index);
		}
	}

}
