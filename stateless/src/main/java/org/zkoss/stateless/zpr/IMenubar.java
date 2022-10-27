/* IMenubar.java

	Purpose:

	Description:

	History:
		Tue Oct 19 10:33:46 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Menubar;

/**
 * Immutable {@link Menubar} component
 * <p>A container usually contains more than one menu elements.</p>
 * @author katherine
 * @see Menubar
 */
@StatelessStyle
public interface IMenubar extends IXulElement<IMenubar>, IChildable<IMenubar, IChildrenOfMenupopup>,
		IAnyGroup<IMenubar> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IMenubar DEFAULT = new IMenubar.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Menubar> getZKType() {
		return Menubar.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.menu.Menubar"}</p>
	 */
	default String getWidgetClass() {
		return "zul.menu.Menubar";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkOrient() {
		IComponentChecker.checkOrient(getOrient());
	}

	/**
	 * Returns whether it is opened automatically when the mouse cursor is near
	 * the page edge.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isAutodrop() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autodrop}.
	 *
	 * <p>Sets whether it is opened automatically when the mouse cursor is near
	 * the page edge.
	 *
	 * @param autodrop Whether it is opened automatically.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenubar withAutodrop(boolean autodrop);

	/** Returns the orient.
	 * <p>Default: {@code "horizontal"}.
	 */
	default String getOrient() {
		return "horizontal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of component
	 *
	 * @param orient Either {@code "horizontal"} or {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenubar withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of component
	 *
	 * @param orient The {@link Orient orient}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IMenubar withOrient(Orient orient) {
		Objects.requireNonNull(orient);
		return withOrient(orient.value);
	}

	/**
	 * Returns whether the menubar scrolling is enabled.
	 * <p>Default: {@code false}.
	 */
	default boolean isScrollable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code scrollable}.
	 *
	 * <p> Sets whether the menubar scrolling is enabled.
	 *
	 * @param scrollable Whether the menubar scrolling is enabled.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenubar withScrollable(boolean scrollable);

	/**
	 * Returns the instance with the given {@link IChildrenOfMenupopup} children.
	 * @param children The children of {@link IChildrenOfMenupopup}
	 */
	static IMenubar of(Iterable<? extends IChildrenOfMenupopup> children) {
		return new IMenubar.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IChildrenOfMenupopup} children.
	 * @param children The children of {@link IChildrenOfMenupopup}
	 */
	static IMenubar of(IChildrenOfMenupopup... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IMenubar ofId(String id) {
		return new IMenubar.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "autodrop", isAutodrop());
		String _orient = getOrient();
		if ("vertical".equals(_orient))
			render(renderer, "orient", _orient);
		boolean _scrollable = isScrollable();
		if ("horizontal".equals(_orient) && _scrollable)
			render(renderer, "scrollable", _scrollable);
	}

	/**
	 * Builds instances of type {@link IMenubar IMenubar}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIMenubar.Builder {}

	/**
	 * Builds an updater of type {@link IMenubar} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IMenubarUpdater {}

	/**
	 * Specifies the orient of {@link IMenubar} component
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