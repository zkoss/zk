/* IHlayout.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:38:48 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Hlayout;

/**
 * Immutable {@link Hlayout} component
 *
 * <p>The hlayout component is a simple horizontal oriented layout.
 * It layouts its child components horizontally in a row.</p>
 *
 * @author katherine
 * @see Hlayout
 */
@ZephyrStyle
public interface IHlayout<I extends IAnyGroup> extends ILayout<IHlayout<I>>,
		IChildable<IHlayout<I>, I>, IAnyGroup<IHlayout<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IHlayout<IAnyGroup> DEFAULT = new IHlayout.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Hlayout> getZKType() {
		return Hlayout.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.box.Hlayout"}</p>
	 */
	default String getWidgetClass() {
		return "zul.box.Hlayout";
	}

	/** Returns the vertical-align property used for the inner children.
	 * <p>Default: {@code "top"}.
	 */
	default String getValign() {
		return "top";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code valign}.
	 *
	 * <p> Sets the vertical-align property used for the inner children.
	 *
	 * @param valign The vertical-align, allowed values are {@ocde "top"}, {@code "middle"},
	 * and {@code "bottom"}
	 * <p>Default: {@code "top"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IHlayout<I> withValign(String valign);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code valign}.
	 *
	 * <p> Sets the vertical-align property used for the inner children.
	 *
	 * @param valign The vertical-align of {@link VerticalAlign}
	 * <p>Default: {@code "top"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IHlayout<I> withValign(VerticalAlign valign) {
		return withValign(valign.value);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	default void checkValign() {
		String valign = getValign();
		if(!"top".equals(valign) && !"middle".equals(valign) && !"bottom".equals(valign))
			throw new WrongValueException(valign);
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IHlayout<I> ofId(String id) {
		return new IHlayout.Builder<I>().setId(id).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IHlayout<I> of(Iterable<? extends I> children) {
		return new IHlayout.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IHlayout<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given vflex.
	 * @param vflex The vertical flex hint.
	 */
	static <I extends IAnyGroup> IHlayout<I> ofVflex(String vflex) {
		return new Builder<I>().setVflex(vflex).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILayout.super.renderProperties(renderer);
		String _valign = getValign();
		if (!"top".equals(_valign))
			renderer.render("valign", _valign);
	}

	/**
	 * Builds instances of type {@link IHlayout IHlayout}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIHlayout.Builder<I> {}

	/**
	 * Builds an updater of type {@link IHlayout} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IHlayoutUpdater {}

	/**
	 * Specifies the vertical-align to {@link IHlayout}
	 */
	enum VerticalAlign {
		/**
		 * The top valign.
		 */
		TOP("top"),

		/**
		 * The middle valign.
		 */
		MIDDLE("middle"),

		/**
		 * The bottom valign.
		 */
		BOTTOM("bottom");
		final String value;

		VerticalAlign(String value) {
			this.value = value;
		}
	}
}