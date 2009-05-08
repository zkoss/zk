/* Comboitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getZclass}: z-combo-item. (since 3.5.0)
 *
 * @author tomyeh
 * @see Combobox
 */
public class Comboitem extends LabelImageElement implements org.zkoss.zul.api.Comboitem {
	private String _desc = "";
	private Object _value;
	private String _content = "";
	private boolean _disabled = false;

	public Comboitem() {
	}
	public Comboitem(String label) {
		this();
		setLabel(label);
	}
	public Comboitem(String label, String image) {
		this();
		setLabel(label);
		setImage(image);
	}


	// super
	protected String getRealSclass() {
		final String scls = super.getRealSclass();
		final String added = isDisabled() ? getZclass() + "-disd": "";
		return scls != null && scls.length() > 0 ? scls + " " + added
				: added;
	}

	public String getZclass() {
		return _zclass == null ? "z-combo-item" : _zclass;
	}
	
	public void setLabel(String label) {
		final String old = getLabel();
		if (!Objects.equals(old, label)) {
			final Combobox cb = (Combobox)getParent();
			final boolean reIndex = cb != null && cb.getSelectedItem() == this;

			super.setLabel(label);
			
			if (reIndex) {
				final Constraint constr = cb.getConstraint();
				if (constr != null && constr instanceof SimpleConstraint 
						&& (((SimpleConstraint)constr).getFlags() & SimpleConstraint.STRICT) != 0) {
					cb.setValue(label);
				} else {
					cb.reIndex();
				}
			}
		}
	}
	
	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			invalidate();
		}
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	
	/** Returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * <p>Default: "".
	 */
	public String getDescription() {
		return _desc;
	}
	/** Sets the description.
	 */
	public void setDescription(String desc) {
		if (desc == null) desc = "";
		if (!_desc.equals(desc)) {
			_desc = desc;
			invalidate();
		}
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: empty ("").
	 *
	 * @see #getDescription
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * @see #setDescription
	 * @since 3.0.0
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			invalidate();
		}
	}

	/** Returns the value associated with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * <p>It is usually used with {@link Combobox#getSelectedItem}.
	 * For example,
	 * <code>combobox.getSelectedItem().getValue()</code>
	 *
	 * @see Combobox#getSelectedItem
	 * @see #setValue
	 * @since 2.4.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Associate the value with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * @see Combobox#getSelectedItem
	 * @see #getValue
	 * @since 2.4.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	//-- super --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Combobox))
			throw new UiException("Comboitem's parent must be Combobox");		
		super.beforeParentChanged(parent);
	}
	public void setParent(Component parent) {
		final Combobox old = (Combobox)getParent();
		final boolean reIndex =
			parent != old && old != null && old.getSelectedItem() == this;

		super.setParent(parent);
		
		if (reIndex) postOnReIndex(old);
	}
	
	/** re-index later */
	private void postOnReIndex(Combobox old) {
		Events.postEvent("onReIndex", this, old);
	}
	
	public void onReIndex (Event evt) {
		final Combobox cb = (Combobox) evt.getData();
		if (cb != null) cb.reIndex();
	} 

	/** No child is allowed. */
	public boolean isChildable() {
		return false;
	}
	
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		if (isDisabled()) {
			final StringBuffer sb = new StringBuffer(60).append(attrs);
			HTMLs.appendAttribute(sb, "z.disd", true);
			return sb.toString();
		}
		return attrs;
	}
}
