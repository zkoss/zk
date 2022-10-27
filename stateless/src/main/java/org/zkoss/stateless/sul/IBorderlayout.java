/* IBorderlayout.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:34:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Borderlayout;

/**
 * Immutable {@link Borderlayout} component
 *
 * <p>The layout component is a nested component.
 * The parent component is {@link IBorderlayout borderlayout}, and its children components include
 * {@link INorth north}, {@link ISouth south}, {@link IWest west}, {@link IEast east},
 * and {@link ICenter center}. All extra space is placed in the center area.
 * The combination of children components of borderlayout is free.
 * <p> A borderlayout could be nested to another borderlayout (actually, almost all kinds of components)
 * to form a complicated layout.</p>
 *
 * <h3>Support Application Library Properties</h3>
 *
 * <ul>
 * <li>
 * <p>To set to true to disable the animation effects of this component. ({@link #withAnimationDisabled(boolean)})
 * </p>
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.borderlayout.animation.disable&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 *</ul>
 *
 * @author katherine
 * @see Borderlayout
 */
@StatelessStyle
public interface IBorderlayout extends IHtmlBasedComponent<IBorderlayout>,
		IAnyGroup<IBorderlayout>,
		IComposite<ILayoutRegion> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IBorderlayout DEFAULT = new IBorderlayout.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Borderlayout> getZKType() {
		return Borderlayout.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.Borderlayout"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.Borderlayout";
	}

	/**
	 * Returns the north child.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	INorth getNorth();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code north}.
	 * <p> Sets the north child.
	 *
	 * @param north The north child
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withNorth(@Nullable INorth north);

	/**
	 * Returns the south child.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	ISouth getSouth();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code south}.
	 * <p> Sets the south child.
	 *
	 * @param south The south child
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withSouth(@Nullable ISouth south);

	/**
	 * Returns the west child.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IWest getWest();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code west}.
	 * <p> Sets the west child.
	 *
	 * @param west The west child
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withWest(@Nullable IWest west);

	/**
	 * Returns the east child.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IEast getEast();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code east}.
	 * <p> Sets the east child.
	 *
	 * @param east The east child
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withEast(@Nullable IEast east);

	/**
	 * Returns the center child.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	ICenter getCenter();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code center}.
	 * <p> Sets the center child.
	 *
	 * @param center The center child
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withCenter(@Nullable ICenter center);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<ILayoutRegion> getAllComponents() {
		return Stream.of(getNorth(), getWest(), getCenter(), getEast(),
						getSouth()).filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	/**
	 * Returns whether disable animation effects
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.borderlayout.animation.disabled"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isAnimationDisabled() {
		return Boolean.parseBoolean(
				Library.getProperty("org.zkoss.zul.borderlayout.animation.disabled", "false"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code animationDisabled}.
	 * <p> Sets to disable animation effects.
	 *
	 * @param animationDisabled Whether to disable animation effects.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBorderlayout withAnimationDisabled(boolean animationDisabled);

	/**
	 * Returns the instance with the given children (i.e. {@link INorth north},
	 * {@link ISouth south}, {@link IWest west}, {@link IEast east}, and {@link ICenter center}).
	 * Each region can only be once.
	 *
	 * @param children The children belong to any {@link ILayoutRegion}
	 */
	static IBorderlayout of(Iterable<? extends ILayoutRegion> children) {
		boolean hasNorth = false, hasEast = false, hasWest = false, hasSouth = false, hasCenter = false;
		Builder builder = new Builder();
		for (ILayoutRegion child : children) {
			if (hasNorth && child instanceof INorth) throw new UiException("Only one north child is allowed.");
			if (hasEast && child instanceof IEast) throw new UiException("Only one east child is allowed.");
			if (hasWest && child instanceof IWest) throw new UiException("Only one west child is allowed.");
			if (hasSouth && child instanceof ISouth) throw new UiException("Only one south child is allowed.");
			if (hasCenter && child instanceof ICenter) throw new UiException("Only one center child is allowed.");

			if (child instanceof INorth) {
				builder.setNorth((INorth) child);
				hasNorth = true;
			} else if (child instanceof IEast) {
				builder.setEast((IEast) child);
				hasEast = true;
			} else if (child instanceof IWest) {
				hasWest = true;
				builder.setWest((IWest) child);
			} else if (child instanceof ISouth) {
				hasSouth = true;
				builder.setSouth((ISouth) child);
			} else if (child instanceof ICenter) {
				hasCenter = true;
				builder.setCenter((ICenter) child);
			}
		}
		return builder.build();
	}

	/**
	 * Returns the instance with the given children (i.e. {@link INorth north},
	 * {@link ISouth south}, {@link IWest west}, {@link IEast east}, and {@link ICenter center}).
	 * Each region can only be once.
	 *
	 * @param children The children belong to any {@link ILayoutRegion}
	 */
	static IBorderlayout of(ILayoutRegion... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given size, width and height.
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static IBorderlayout ofSize(String width, String height) {
		return new IBorderlayout.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given flex, hflex and vflex.
	 * @param hflex The horizontal flex hint
	 * @param vflex The vertical flex hint
	 */
	static IBorderlayout ofFlex(String hflex, String vflex) {
		return new IBorderlayout.Builder().setHflex(hflex).setVflex(vflex).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IBorderlayout ofId(String id) {
		return new IBorderlayout.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		render(renderer, "_animationDisabled", isAnimationDisabled());
	}

	/**
	 * Builds instances of type {@link IBorderlayout IBorderlayout}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIBorderlayout.Builder {}

	/**
	 * Builds an updater of type {@link IBorderlayout} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IBorderlayoutUpdater {}
}