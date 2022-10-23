/* ILabelElement.java

	Purpose:

	Description:

	History:
		Tue Oct 05 15:15:24 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * Immutable {@link org.zkoss.zul.impl.LabelElement} interface
 * @author katherine
 */
public interface ILabelElement<I extends ILabelElement> extends IXulElement<I> {

	/**
	 * Returns the {@code label} (never null).
	 * <p>Default: {@code ""}.
	 */
	default String getLabel() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code label}.
	 *
	 * <p>Sets the label of the component.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withLabel(String label);

	/**
	 * Internal use
	 * @param label
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderCrawlable(String label) throws java.io.IOException {
		org.zkoss.zul.impl.Utils.renderCrawlableText(label);
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "label", getLabel());
		renderCrawlable(getLabel());
	}
}
