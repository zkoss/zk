/* A.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 22, 2009 4:08:00 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.api.LabelImageElement;

/**
 * The same as HTML A tag.
 * @author jumperchen
 * @since 5.0.0
 */
public interface A extends LabelImageElement,
org.zkoss.zk.ui.ext.Disable {

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisabled();

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns the direction.
	 * <p>
	 * Default: "normal".
	 */
	public String getDir();

	/**
	 * Sets the direction.
	 * 
	 * @param dir
	 *            either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException;

	/**
	 * Returns the href that the browser shall jump to, if an user clicks this
	 * button.
	 * <p>
	 * Default: null. If null, the button has no function unless you specify the
	 * onClick event listener.
	 * <p>
	 * If it is not null, the onClick event won't be sent.
	 */
	public String getHref();

	/**
	 * Sets the href.
	 */
	public void setHref(String href);

	/**
	 * Returns the target frame or window.
	 * 
	 * <p>
	 * Note: it is useful only if href ({@link #setHref}) is specified (i.e.,
	 * use the onClick listener).
	 * 
	 * <p>
	 * Default: null.
	 */
	public String getTarget();

	/**
	 * Sets the target frame or window.
	 * 
	 * @param target
	 *            the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target);

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex();

	/**
	 * Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException;
}
