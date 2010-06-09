package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A button.
 * <p>
 * Default {@link #getZclass}: z-button.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Button extends org.zkoss.zul.impl.api.LabelImageElement,
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
	 * Returns the orient.
	 * <p>
	 * Default: "horizontal".
	 */
	public String getOrient();

	/**
	 * Sets the orient.
	 * 
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException;

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
