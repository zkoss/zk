/* IDiv.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:49:53 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Div;

/**
 * Immutable {@link Div} component.
 *
 * <p> The same as HTML DIV tag. It is one of the most lightweight containers
 * to group child component for, say, assigning CSS or making more sophisticated
 * layout. Div is displayed as block that the following sibling won't be displayed
 * in the same vertical position; as if there is a line break before and after it.
 *
 * @author katherine
 * @see Div
 */

@StatelessStyle
public interface IDiv<I extends IAnyGroup> extends IXulElement<IDiv<I>>,
		IAnyGroup<IDiv<I>>, IChildable<IDiv<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IDiv<IAnyGroup> DEFAULT = new IDiv.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Div> getZKType() {
		return Div.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Div"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Div";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IDiv<I> of(Iterable<? extends I> children) {
		return new IDiv.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IDiv<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IDiv<I> ofId(String id) {
		return new IDiv.Builder<I>().setId(id).build();
	}

	/**
	 * Returns the instance with the given size, width and height.
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static <I extends IAnyGroup> IDiv<I> ofSize(String width, String height) {
		return new IDiv.Builder<I>().setWidth(width).setHeight(height).build();
	}

	/**
	 * Builds instances of type {@link IDiv IDiv}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIDiv.Builder<I> {}

	/**
	 * Builds an updater of type {@link IDiv} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IDivUpdater {}
}