/* INorth.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:04:29 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.North;

/**
 * Immutable {@link North} component
 * <p>A north region of a border layout.</p>
 *
 * @author katherine
 * @see North
 */
@StatelessStyle
public interface INorth<I extends IAnyGroup> extends ILayoutRegion<INorth<I>>,
		ISingleChildable<INorth<I>, I> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	INorth<IAnyGroup> DEFAULT = new INorth.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<North> getZKType() {
		return North.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.North"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.North";
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
	default INorth<I> withSize(@Nullable String size) {
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
	INorth<I> withCmargins(String cmargins);

	/**
	 * Return the instance with the given child.
	 * @param child The child from any group
	 */
	static <I extends IAnyGroup> INorth<I> of(I child) {
		return new INorth.Builder<I>().setChild(child).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> INorth<I> ofId(String id) {
		return new INorth.Builder<I>().setId(id).build();
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
	 * Builds instances of type {@link INorth INorth}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableINorth.Builder<I> {}

	/**
	 * Builds an updater of type {@link INorth} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends INorthUpdater {}
}