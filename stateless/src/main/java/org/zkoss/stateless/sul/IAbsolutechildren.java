/* IAbsolutechildren.java

	Purpose:

	Description:

	History:
		Wed Oct 06 14:35:04 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Absolutechildren;

/**
 * Immutable {@link Absolutechildren} component
 * <p>A container component that can contain any other ZK component and can only
 * be contained as direct child of Absolutelayout component. It can be absolutely
 * positioned within Absolutelayout component by either setting "x" and "y"
 * attribute or calling {@link #withX(int)} and {@link #withY(int)} methods.
 *
 * @author katherine
 * @see Absolutechildren
 * @see IAbsolutelayout
 */
@StatelessStyle
public interface IAbsolutechildren<I extends IAnyGroup>
		extends IXulElement<IAbsolutechildren<I>>,
		IChildable<IAbsolutechildren<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IAbsolutechildren<IAnyGroup> DEFAULT = new Builder().build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Absolutechildren> getZKType() {
		return Absolutechildren.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.Absolutechildren"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.Absolutechildren";
	}

	/**
	 * Returns the current {@code x} position within parent container component
	 * <p>Default: {@code 0}</p>
	 */
	default int getX() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code x}.
	 *
	 * <p>Sets current {@code x} position within parent container component.
	 *
	 * @param x The x position
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAbsolutechildren<I> withX(int x);

	/**
	 * Returns the current {@code y} position within parent container component
	 * <p>Default: {@code 0}</p>
	 */
	default int getY() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code y}.
	 *
	 * <p>Sets current {@code y} position within parent container component.
	 *
	 * @param y The y position
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAbsolutechildren<I> withY(int y);

	/**
	 * Returns the instance with the given x, y, and any group children.
	 * @param x The x position
	 * @param y The y position
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IAbsolutechildren<I> of(int x, int y, Iterable<? extends I> children) {
		return new IAbsolutechildren.Builder<I>().setX(x).setY(y).setChildren(children).build();
	}

	/**
	 * Returns the instance with the given x, y, and any group children.
	 * @param x The x position
	 * @param y The y position
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IAbsolutechildren<I> of(int x, int y, I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(x, y, Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given x and y.
	 * @param x The x position
	 * @param y The y position
	 */
	static <I extends IAnyGroup> IAbsolutechildren of(int x, int y) {
		return new IAbsolutechildren.Builder<I>().setX(x).setY(y).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IAbsolutechildren ofId(String id) {
		return new IAbsolutechildren.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		int _x = getX();
		int _y = getY();
		if (_x != 0)
			render(renderer, "x", _x);
		if (_y != 0)
			render(renderer, "y", _y);
	}

	/**
	 * Builds instances of type {@link IAbsolutechildren IAbsolutechildren}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIAbsolutechildren.Builder<I> {}

	/**
	 * Builds an updater of type {@link IAbsolutechildren} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IAbsolutechildrenUpdater {}
}