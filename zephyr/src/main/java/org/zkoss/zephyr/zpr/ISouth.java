/* ISouth.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:21:41 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.South;

/**
 * Immutable {@link South} component
 * <p>A south region of a border layout.</p>
 *
 * @author katherine
 * @see South
 */
@ZephyrStyle
public interface ISouth<I extends IAnyGroup> extends ILayoutRegion<ISouth<I>>,
		ISingleChildable<ISouth<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISouth<IAnyGroup> DEFAULT = new ISouth.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<South> getZKType() {
		return South.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.South"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.South";
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()}.
	 */
	@Value.Lazy
	@Nullable
	default String getSize() {
		return getHeight();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code size}.
	 *
	 * <p>This method is shortcut for {@link #withHeight(String)}.
	 *
	 * @param size The height of the component.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ISouth<I> withSize(@Nullable String size) {
		return withHeight(size);
	}

	/**
	 * Returns the collapsed margins, which is a list of numbers separated by comma.
	 * <p>Default: {@code "3,0,0,3"}</p>
	 */
	default String getCmargins() {
		return "3,0,0,3";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code cmargins}.
	 *
	 * <p> Sets the collapsed margins for the component "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 *
	 * @param cmargins The collapsed margins for the component
	 * <p>Default: {@code "3,0,0,3"}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISouth<I> withCmargins(String cmargins);

	/**
	 * Return the instance with the given child.
	 * @param child The child from any group
	 */
	static <I extends IAnyGroup> ISouth<I> of(I child) {
		return new ISouth.Builder<I>().setChild(child).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ISouth<I> ofId(String id) {
		return new ISouth.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILayoutRegion.super.renderProperties(renderer);

		String _cms = getCmargins();
		if (!"3,0,0,3".equals(_cms)) {
			render(renderer, "cmargins", _cms);
		}
	}

	/**
	 * Builds instances of type {@link ISouth ISouth}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableISouth.Builder<I> {}

	/**
	 * Builds an updater of type {@link ISouth} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ISouthUpdater {}
}