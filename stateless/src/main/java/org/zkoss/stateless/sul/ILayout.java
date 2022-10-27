/* ILayout.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:40:54 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import javax.annotation.Nullable;

import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Layout;

/**
 * Immutable {@link Layout} interface.
 * @author katherine
 */
public interface ILayout<I extends ILayout> extends IXulElement<I> {

	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * <p>Default: {@code "0.3em"} (means to use the default spacing).
	 */
	@Nullable
	default String getSpacing() {
		return "0.3em";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code spacing}.
	 *
	 * <p> Sets the spacing between adjacent children.
	 * @param spacing The spacing (such as "0", "5px", "3pt" or "1em").
	 * If the spacing is set to "auto", null, or empty (""),
	 * the DOM style is left intact, so the spacing can be customized from CSS.
	 * <p>Default: {@code "0.3em"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSpacing(@Nullable String spacing);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		String _spacing = getSpacing();
		if (!"0.3em".equals(_spacing))
			renderer.render("spacing", _spacing);
	}
}