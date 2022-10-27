/* IHeaderElement.java

	Purpose:

	Description:

	History:
		Thu Oct 07 16:34:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import javax.annotation.Nullable;

import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Immutable {@link org.zkoss.zul.impl.HeaderElement} interface
 * @author katherine
 */
public interface IHeaderElement<I extends IHeaderElement> extends ILabelImageElement<I> {

	/** Returns the vertical alignment of this grid.
	 * <p>Default: {@code null} (system default: {@code top}).
	 */
	@Nullable
	String getValign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code valign}.
	 *
	 * <p>Sets the vertical alignment of this grid.
	 * @param valign The vertical alignment of this grid.
	 * @return A modified copy of the {@code this} object
	 */
	I withValign(@Nullable String valign);

	/** Returns the horizontal alignment of this column.
	 * <p>Default: {@code null} (system default: {@code left} unless CSS specified).
	 */
	@Nullable
	String getAlign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code align}.
	 *
	 * <p>Sets the horizontal alignment of this grid.
	 * @param align The horizontal alignment of this grid.
	 * @return A modified copy of the {@code this} object
	 */
	I withAlign(@Nullable String align);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws java.io.IOException {
		ILabelImageElement.super.renderProperties(renderer);
		render(renderer, "valign", getValign());
		render(renderer, "align", getAlign());
	}
}
