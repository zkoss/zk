/* Inputgroup.java

		Purpose:

		Description:

		History:
				Thu Mar 07 16:33:47 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.XulElement;

/**
 * An inputgroup.
 *
 * Inspired by Bootstrap’s Input group and Button group.
 * By prepending or appending some components to the input component,
 * you can merge them like a new form-input component.
 *
 * <h3>Accepted child components</h3>
 * <ul>
 *     <li>Label</li>
 *     <li>InputElement</li>
 *     <li>LabelImageElement</li>
 * </ul>
 *
 * <p>Default {@link #getZclass}: z-inputgroup.
 *
 * @since 9.0.0
 * @author charlesqiu, rudyhuang
 */
public class Inputgroup extends XulElement {
	private boolean _vertical;

	/**
	 * Returns whether it is a vertical orientation.
	 * <p>Default: false
	 *
	 * @return whether it is a vertical orientation
	 */
	public boolean isVertical() {
		return _vertical;
	}

	/**
	 * Returns the orientation.
	 * <p>Default: "horizontal"
	 */
	public String getOrient() {
		return _vertical ? "vertical" : "horizontal";
	}

	/**
	 * Sets the orientation.
	 *
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient)) {
			throw new WrongValueException("orient cannot be " + orient);
		}
		boolean vertical = "vertical".equals(orient);
		if (_vertical != vertical) {
			_vertical = vertical;
			smartUpdate("vertical", _vertical);
		}
	}

	@Override
	public void beforeChildAdded(Component child, Component insertBefore) {
		if (!(child instanceof Label) && !(child instanceof InputElement)
				&& !(child instanceof LabelImageElement)) {
			throw new UiException("Unsupported child: " + child);
		}
		super.beforeChildAdded(child, insertBefore);
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "vertical", _vertical);
	}
}
