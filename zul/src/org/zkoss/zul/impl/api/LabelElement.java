package org.zkoss.zul.impl.api;

/**
 * A XUL element with a label.
 * 
 * @author tomyeh
 */
public interface LabelElement extends XulElement {
	/**
	 * Returns the label (never null).
	 * <p>
	 * Default: "".
	 */
	public String getLabel();

	/**
	 * Sets the label.
	 * <p>
	 * If label is changed, the whole component is invalidate. Thus, you want to
	 * smart-update, you have to override this method.
	 */
	public void setLabel(String label);
}
