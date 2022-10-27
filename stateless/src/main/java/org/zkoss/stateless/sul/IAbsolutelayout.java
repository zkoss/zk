/* IAbsolutelayout.java

	Purpose:

	Description:

	History:
		Mon Jan 17 18:09:53 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Absolutelayout;

/**
 * Immutable {@link Absolutelayout} component
 *
 * <p>An Absolutelayout component can contain absolute positioned multiple
 * absolutechildren components.
 *
 * <h2>Example</h2>
 * <img src="doc-files/IAbsolutelayout_example.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IAbsolutelayout.of(IAbsolutechildren.of(60, 100,
 *                 IWindow.ofTitle("X=60, Y=100").withBorder("normal")
 *                     .withChildren(ILabel.of("Window 1"))),
 *             IAbsolutechildren.of(160, 200,
 *                 IWindow.ofTitle("X=160, Y=200").withBorder("normal")
 *                     .withChildren(ILabel.of("Window 2"))),
 *             IAbsolutechildren.of(260, 300,
 *                 IWindow.ofTitle("X=260, Y=300").withBorder("normal")
 *                     .withChildren(ILabel.of("Window 3"))));
 *     }
 * }
 * </code>
 * </pre>
 * @author katherine
 * @see Absolutelayout
 */
@StatelessStyle
public interface IAbsolutelayout
		extends IXulElement<IAbsolutelayout>, IAnyGroup<IAbsolutelayout>,
		IChildable<IAbsolutelayout, IAbsolutechildren<IAnyGroup>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IAbsolutelayout DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Absolutelayout> getZKType() {
		return Absolutelayout.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.Absolutelayout"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.Absolutelayout";
	}

	/**
	 * Returns the instance with the given {@link IAbsolutechildren} children.
	 * @param children The children of {@link IAbsolutechildren}
	 */
	static IAbsolutelayout of(Iterable<? extends IAbsolutechildren<IAnyGroup>> children) {
		return new IAbsolutelayout.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IAbsolutechildren} children.
	 * @param children The children of {@link IAbsolutechildren}
	 */
	static IAbsolutelayout of(IAbsolutechildren<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IAbsolutelayout ofId(String id) {
		return new IAbsolutelayout.Builder().setId(id).build();
	}

	/**
	 * Returns the instance with the given size, width and height.
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static IAbsolutelayout ofSize(String width, String height) {
		return new IAbsolutelayout.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Builds instances of type {@link IAbsolutelayout IAbsolutelayout}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIAbsolutelayout.Builder {
	}

	/**
	 * Builds an updater of type {@link IAbsolutelayout} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 *
	 * @see SmartUpdater
	 */
	class Updater extends IAbsolutelayoutUpdater {
	}
}