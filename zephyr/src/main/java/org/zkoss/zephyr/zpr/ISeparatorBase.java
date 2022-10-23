/* ISeparatorBase.java

	Purpose:

	Description:

	History:
		Wed Nov 03 16:06:50 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Immutable {@link org.zkoss.zul.Separator} component
 * @author katherine
 */
public interface ISeparatorBase<I extends ISeparatorBase> extends IXulElement<I> {

	/** Returns the spacing.
	 * <p>Default: {@code null} (depending on CSS).
	 */
	@Nullable
	String getSpacing();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code spacing}.
	 *
	 * <p>Sets the spacing.
	 *
	 * @param spacing The spacing (such as "0", "5px", "3pt")
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSpacing(@Nullable String spacing);

	/** Returns the orient.
	 * <p>Default: {@code "horizontal"}.
	 */
	default String getOrient() {
		return "horizontal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code orient}.
	 *
	 * <p>Sets the orient.
	 *
	 * @param orient Either {@code "horizontal"} or {@code "vertical"}.
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient to layout image.
	 *
	 * @param orient the orient to layout image., either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withOrient(Orient orient) {
		return withOrient(orient.value);
	}

	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: {@code false}
	 */
	default boolean isBar() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code bar}.
	 *
	 * <p>Sets whether to display a visual bar as the separator.
	 *
	 * @param bar Whether to display a visual bar as the separator..
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withBar(boolean bar);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "spacing", getSpacing());
		render(renderer, "orient", getOrient());
		render(renderer, "bar", isBar());
	}

	/**
	 * Specifies the orient with {@link #withOrient(Orient)}
	 */
	enum Orient {
		/**
		 * The horizontal orient.
		 */
		HORIZONTAL("horizontal"),

		/**
		 * The vertical orient.
		 */
		VERTICAL("vertical");
		final String value;

		Orient(String value) {
			this.value = value;
		}
	}
}