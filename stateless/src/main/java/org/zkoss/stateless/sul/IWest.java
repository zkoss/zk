/* IWest.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:20:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.West;

/**
 * Immutable {@link West} component
 * <p>A west region of a border layout.</p>
 *
 * @author katherine
 * @see West
 */
@StatelessStyle
public interface IWest<I extends IAnyGroup> extends ILayoutRegion<IWest<I>>,
		ISingleChildable<IWest<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IWest<IAnyGroup> DEFAULT = new IWest.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<West> getZKType() {
		return West.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.West"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.West";
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getWidth()}.
	 */
	@Value.Lazy
	@Nullable
	default String getSize() {
		return getWidth();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code size}.
	 *
	 * <p>This method is shortcut for {@link #withWidth(String)}.
	 *
	 * @param size The width of the component.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IWest<I> withSize(@Nullable String size) {
		return withWidth(size);
	}

	/**
	 * Returns the collapsed margins, which is a list of numbers separated by comma.
	 * <p>Default: {@code "0,3,3,0"}</p>
	 */
	default String getCmargins() {
		return "0,3,3,0";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code cmargins}.
	 *
	 * <p> Sets the collapsed margins for the component "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 *
	 * @param cmargins The collapsed margins for the component
	 * <p>Default: {@code "0,3,3,0"}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWest<I> withCmargins(String cmargins);

	/**
	 * Return the instance with the given child.
	 * @param child The child from any group
	 */
	static <I extends IAnyGroup> IWest<I> of(I child) {
		return new IWest.Builder<I>().setChild(child).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IWest<I> ofId(String id) {
		return new IWest.Builder<I>().setId(id).build();
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
		if (!"0,3,3,0".equals(_cms)) {
			render(renderer, "cmargins", _cms);
		}
	}

	/**
	 * Builds instances of type {@link IWest IWest}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIWest.Builder<I> {}

	/**
	 * Builds an updater of type {@link IWest} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IWestUpdater {}
}