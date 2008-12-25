/* Listitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 17:38:52     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A list item.
 *
 * <p>Default {@link #getZclass}: z-list-item.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Listitem extends XulElement implements org.zkoss.zul.api.Listitem {
	private static final Log log = Log.lookup(Listitem.class);

	private Object _value;
	/** The index in the parent (only for implementation purpose). */
	private int _index = -1; //no parent at begining
	private boolean _selected, _disabled, _checkable = true;
	/** whether the content of this item is loaded; used if
	 * the listbox owning this item is using a list model.
	 */
	private boolean _loaded;

	public Listitem() {
	}
	public Listitem(String label) {
		setLabel(label);
	}
	public Listitem(String label, Object value) {
		setLabel(label);
		setValue(value);
	}

	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}
	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listbox getListboxApi() {
		return getListbox();
	}
	/** Returns whether the HTML's select tag is used.
	 */
	private final boolean inSelectMold() {
		final Listbox listbox = getListbox();
		return listbox != null && listbox.inSelectMold();
	}
	protected String getRealStyle() {
		if (this instanceof Listgroup || this instanceof Listgroupfoot || !isVisible()) return super.getRealStyle();
		final Listgroup lg = getListgroup();
		return super.getRealStyle() + (lg != null && !lg.isOpen() ? "display:none" : "") ;
	}
	
	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @since 3.5.0
	 */
	public Listgroup getListgroup() {
		if (this instanceof Listgroup) return (Listgroup)this;
		final Listbox lb = getListbox();
		if (lb != null)
			return lb.getListgroupAt(getIndex());
		return null;
	}
	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listgroup getListgroupApi() {
		return getListgroup();
	}
	protected String getRealSclass() {
		String scls = super.getRealSclass();
		final String added = isDisabled() ? getZclass() + "-disd" : isSelected() ? getZclass() + "-seld" : "";
		if (this instanceof Listgroup || this instanceof Listgroupfoot || !isVisible()) return scls;
		final String sclx = (String) getListbox().getAttribute(Attributes.STRIPE_STATE);
		return scls + (sclx != null ? " " + sclx : "") + " " + added ;
	}

	public String getZclass() {
		return _zclass == null ? "z-list-item" : _zclass;
	}
	/** Returns whether it is checkable.
	 * <p>Default: true.
	 * @since 3.0.4
	 */
	public boolean isCheckable() {
		return _checkable;
	}
	/** Sets whether it is checkable.
	 * <p>Default: true.
	 * @since 3.0.4
	 */
	public void setCheckable(boolean checkable) {
		if (_checkable != checkable) {
			_checkable = checkable;
			invalidate();
		}
	}
	
	/** Returns the maximal length of each item's label.
	 * It is a shortcut of getParent().getMaxlength();
	 * Thus, it works only if the listbox's mold is "select".
	 */
	public int getMaxlength() {
		final Listbox listbox = getListbox();
		return listbox != null ? listbox.getMaxlength(): 0;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	public void setValue(Object value) {
		if (!Objects.equals(_value, value)) {
			_value = value;

			final Listbox listbox = getListbox();
			if (listbox != null)
				if (listbox.inSelectMold())
					smartUpdate("value", Objects.toString(_value));
				else if (listbox.getName() != null)
					smartUpdate("z.value", Objects.toString(_value));
		}
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
			if (inSelectMold())
				smartUpdate("disabled", _disabled);
			else
				invalidate();
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
			final Listbox listbox = (Listbox)getParent();
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
		final Listcell cell = (Listcell)getFirstChild();
		return cell != null ? cell.getLabel(): null;
	}
	/** Sets the label of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setLabel(label);
	}
	private Listcell autoFirstCell() {
		Listcell cell = (Listcell)getFirstChild();
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
		final Listcell cell = (Listcell)getFirstChild();
		return cell != null ? cell.getImage(): null;
	}
	/** Sets the image of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setImage(String image) {
		autoFirstCell().setImage(image);
	}

	/** Returns the index of this item (aka., the order in the listbox).
	 */
	public final int getIndex() {
		return _index;
	}

	/** Sets whether the content of this item is loaded; used if
	 * the listbox owning this item is using a list model.
	 */
	/*package*/ final void setLoaded(boolean loaded) {
		if (loaded != _loaded) {
			_loaded = loaded;

			final Listbox listbox = getListbox();
			if (listbox != null && listbox.getModel() != null)
				if (_loaded && !listbox.inPagingMold())
					invalidate();
					//reason: the client doesn't init (for better performance)
					//i.e., z.skipsib is specified for unloaded items
				else
					smartUpdate("z.loaded", _loaded);
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

	//-- Utilities for implementation only (called by Listbox) */
	/*package*/ final void setIndexDirectly(int index) {
		setIndex(index);
	}
	
	protected void setIndex(int index) {
		_index = index;	
	}
	/*package*/ final void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}
	public boolean setVisible(boolean visible) {
		if (isVisible() != visible) {
			smartUpdate("z.visible", visible);
			final Listbox listbox = (Listbox) getParent();
			if (listbox != null) {
				final Listgroup g = listbox.getListgroupAt(getIndex());
				if (g == null || g.isOpen())
					listbox.addVisibleItemCount(visible ? 1 : -1);
			}
		}
		return super.setVisible(visible);
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Listitem's parent must be Listbox, not "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Listcell))
			throw new UiException("Unsupported child for listitem: "+child);
		return super.insertBefore(child, insertBefore);
	}
	public void invalidate() {
		if (inSelectMold()) {
			//Both IE and Mozilla are buggy if we update options by outerHTML
			getParent().invalidate();
			return;
		}
		super.invalidate();
	}
	public void onChildAdded(Component child) {
		if (inSelectMold()) invalidate(); //if HTML-select, Listcell has no client part
		super.onChildAdded(child);
	}
	public void onChildRemoved(Component child) {
		if (inSelectMold()) invalidate(); //if HTML-select, Listcell has no client part
		super.onChildRemoved(child);
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());

		if (inSelectMold()) {
			HTMLs.appendAttribute(sb, "value",  Objects.toString(_value));
			if (isDisabled())
				HTMLs.appendAttribute(sb, "disabled",  "disabled");
			if (isSelected())
				HTMLs.appendAttribute(sb, "selected", "selected");
		} else {
			final Listbox listbox = getListbox();
			if (listbox != null) {
				if (listbox.getName() != null)
					HTMLs.appendAttribute(sb, "z.value",  Objects.toString(_value));
				if (listbox.getModel() != null) {
					HTMLs.appendAttribute(sb, "z.loaded", _loaded);
					if (_loaded && !listbox.inPagingMold()) {
						final Component c = getNextSibling();
						if (c instanceof Listitem && !((Listitem)c)._loaded)
							HTMLs.appendAttribute(sb, "z.skipsib", "true");
					}
				}
			}

			if (isDisabled())
				HTMLs.appendAttribute(sb, "z.disd", true);
			if (isSelected())
				HTMLs.appendAttribute(sb, "z.sel", true);

			final String clkattrs = getAllOnClickAttrs();
			if (clkattrs != null) sb.append(clkattrs);
			HTMLs.appendAttribute(sb, "z.rid", getListbox().getUuid());
			HTMLs.appendAttribute(sb, "z.visible", isVisible());
		}
		return sb.toString();
	}
	//Clone//
	public Object clone() {
		final Listitem clone = (Listitem)super.clone();
		clone._index = -1;
			//note: we have to reset, since listbox.insertBefore assumes
			//that a parent-less listitem's index is -1
		return clone;
	}
}
