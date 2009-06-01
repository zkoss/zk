/* Option.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		2009/6/1 下午 3:54:58 , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelElement;

/**
 * The Option object represents an option in a dropdown list in an HTML form.
 * 
 * For each instance of an <code>option</code> tag in an HTML form, an Option
 * object is created.
 * <p>
 * Default {@link #getZclass}: z-option
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public class Option extends LabelElement {
	private boolean _selected, _disabled;
	public String getZclass() {
		return _zclass == null ? "z-option" : _zclass;
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
	/** Returns whether it is selected.
	 * <p>Default: false.
	 */
	public boolean isSelected() {
		return _selected;
	}
	/*package*/ final void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}
	/** Sets whether it is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Select select = (Select)getParent();
			if (select != null) {
				//Note: we don't update it here but let its parent does the job
				select.toggleItemSelection(this);
			} else {
				_selected = selected;
			}
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "selected", isSelected());
		render(renderer, "disabled", isDisabled());
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Select))
			throw new UiException("Listitem's parent must be Select, not "+parent);
		super.beforeParentChanged(parent);
	}
	
	protected boolean isChildable() {
		return false;
	}

	protected void smartUpdate(String name, Object value) { //make it accessible in this package
		super.smartUpdate(name, value);
	}
	protected void smartUpdate(String name, boolean value) { //make it accessible in this package
		super.smartUpdate(name, value);
	}
}
