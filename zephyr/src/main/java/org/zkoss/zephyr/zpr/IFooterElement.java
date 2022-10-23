/* IFooterElement.java

	Purpose:

	Description:

	History:
		5:29 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import javax.annotation.Nullable;

/**
 * A skeletal implementation for a footer.
 * @author jumperchen
 */
public interface IFooterElement<I extends IFooterElement> extends ILabelImageElement<I> {

	/** Returns number of columns to span this footer.
	 * Default: {@code 1}.
	 */
	default int getSpan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code span}.
	 *
	 * <p> Sets number of columns to span this footer.
	 *
	 * @param span Number of columns to span this footer.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSpan(int span);

	/** Returns the horizontal alignment of this footer.
	 * <p>Default: {@code null} (system default: {@code "left"} unless CSS specified).
	 */
	@Nullable
	String getAlign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code align}.
	 *
	 * <p> Sets the horizontal alignment of this footer.
	 *
	 * @param align The horizontal alignment of this footer.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withAlign(@Nullable String align);

	/** Returns the vertical alignment of this footer.
	 * <p>Default: {@code null} (system default: {@code "top"}).
	 */
	@Nullable
	String getValign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code valign}.
	 *
	 * <p> Sets the vertical alignment of this footer.
	 *
	 * @param valign The vertical alignment of this footer.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withValign(@Nullable String valign);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		ILabelImageElement.super.renderProperties(renderer);
		render(renderer, "valign", getValign());
		render(renderer, "align", getAlign());

		int _span = getSpan();
		if (_span > 1)
			renderer.render("span", _span);
	}
}
