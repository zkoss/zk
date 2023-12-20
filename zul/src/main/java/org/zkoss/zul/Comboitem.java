/* Comboitem.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.Serializable;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xml.XMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SafeHtmlValue;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getZclass}: z-comboitem. (since 5.0.0)
 *
 * @author tomyeh
 * @see Combobox
 */
public class Comboitem extends LabelImageElement implements org.zkoss.zk.ui.ext.Disable {
	private String _desc = "";
	private transient Object _value;
	private SafeHtmlValue _content = SafeHtmlValue.EMPTY;
	private boolean _disabled;
	private transient int _index;

	public Comboitem() {
	}

	public Comboitem(String label) {
		super(label);
	}

	public Comboitem(String label, String image) {
		super(label, image);
	}

	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", disabled);
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
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	public String getDescription() {
		return _desc;
	}

	/** Sets the description.
	 */
	public void setDescription(String desc) {
		if (desc == null)
			desc = "";
		if (!_desc.equals(desc)) {
			_desc = desc;
			smartUpdate("description", getDescription()); //allow overriding
		}
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * @see #getDescription
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content.toString();
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * @see #getDescription
	 * @since 10.0.0
	 */
	public SafeHtmlValue getRawContent() {
		return _content;
	}

	/** Sets the embedded content that is
	 * shown as part of the description.
	 *
	 * <h3>Security Note</h3>
	 * <p>Since 10.0.0, the content assigned to this method will be escaped by default.
	 * To avoid escaping, use {@link #setContent(SafeHtmlValue)} instead.
	 * @see #setDescription
	 * @since 3.0.0
	 */
	public void setContent(String content) {
		if (content == null)
			content = "";
		content = Strings.escapeJavaScript(XMLs.escapeXML(content));
		if (!Objects.equals(_content, SafeHtmlValue.valueOf(content))) {
			_content = SafeHtmlValue.valueOf(content);
			smartUpdate("content", getRawContent());
		}
	}

	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 * @since 10.0.0
	 */
	public void setContent(SafeHtmlValue content) {
		if (content == null)
			content = SafeHtmlValue.EMPTY;
		if (!Objects.equals(_content, content)) {
			_content = content;
			smartUpdate("content", getRawContent());
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
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) _value;
	}

	/** Associate the value with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * @see Combobox#getSelectedItem
	 * @see #getValue
	 * @since 2.4.0
	 */
	public <T> void setValue(T value) {
		_value = value;
	}

	/** Returns the index of this Comboitem.
	 * @since 6.0.0
	 */
	public int getIndex() {
		final Combobox cb = (Combobox) getParent();
		if (cb != null)
			cb.syncItemIndices();
		return _index;
	}

	/*package*/ void setIndexDirectly(int index) { //called by Combobox
		_index = index;
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-comboitem" : _zclass;
	}

	public void setLabel(String label) {
		final String old = getLabel();
		if (!Objects.equals(old, label)) {
			final Combobox cb = (Combobox) getParent();
			final boolean syncValueToSelection = cb != null && cb.getSelectedItemDirectly() == this;

			super.setLabel(label);

			if (syncValueToSelection) {
				final Constraint constr = cb.getConstraint();
				if (constr != null && constr instanceof SimpleConstraint
						&& (((SimpleConstraint) constr).getFlags() & SimpleConstraint.STRICT) != 0) {
					cb.setValue(label);
				} else {
					cb.schedSyncValueToSelection();
				}
			}
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "disabled", _disabled);
		render(renderer, "description", getDescription()); //allow overriding getDescription()
		render(renderer, "content", getRawContent()); //allow overriding getContent()

		if (_value instanceof String) {
			render(renderer, "value", _value);
		}
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Combobox))
			throw new UiException("Comboitem's parent must be Combobox");
		super.beforeParentChanged(parent);
	}

	/** No child is allowed. */
	protected boolean isChildable() {
		return false;
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

	//Cloneable//
	public Object clone() {
		final Comboitem clone = (Comboitem) super.clone();
		clone._index = 0;
		return clone;
	}
}
