/* IImagemap.java

	Purpose:

	Description:

	History:
		Tue Oct 26 12:36:44 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.MouseData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Imagemap;

/**
 * Immutable {@link Imagemap} component
 *
 * <p>An {@link IImagemap} component is a special image. It accepts whatever attribute an {@link IImage} component accepts.
 * However, unlike {@link IImage}, it support {@link IArea} as its children.
 * The {@link IImagemap} component will translate the mouse pointer coordinates into a logical name, which is the id of the {@link IArea} that user clicked.
 * For example:
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IImagemap.of(
 *         IArea.ofId("left").withCoords("0, 0, 45, 80"),
 *         IArea.ofId("right").withCoords("46, 0, 90, 80")
 *     ).withSrc("/stateless/ZK-Logo.gif").withAction(this::doClick);
 * }
 *
 * {@literal @}{@code Action}(type = Events.ON_CLICK)
 * public void doClick(MouseData mouseData) {
 *     Clients.log(mouseData.getArea());
 * }
 * </code>
 * </pre>
 * <p>The above example shows how to use {@link Imagemap} with {@link IArea}.
 * The ZK-Logo.gif is a 90x80 image, we divide the image into 2 parts left and right by using {@link IArea#withCoords(String)}.
 * Once you clicked on the area of {@link Imagemap}, you can get the id of the {@link IArea} through {@link MouseData}.
 *
 * Note: Don't try to use CSS background as your image, the image map need a real image or it won't work.
 * @author katherine
 * @see Imagemap
 * @see IArea
 */

@StatelessStyle
public interface IImagemap extends IImageBase<IImagemap>, IChildable<IImagemap, IArea>, IAnyGroup<IImagemap> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IImagemap DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Imagemap> getZKType() {
		return Imagemap.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Imagemap"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Imagemap";
	}

	/**
	 * Returns the instance with the given {@code src}.
	 * @param src The URI of the image source.
	 */
	static IImagemap of(String src) {
		return new IImagemap.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given {@code areas}.
	 * @param areas The {@link IArea} list as the children of this component.
	 */
	static <I extends IArea> IImagemap of(Iterable<? extends I> areas) {
		return new Builder().setChildren(areas).build();
	}

	/**
	 * Returns the instance with the given {@code areas}.
	 * @param areas The {@link IArea} as the children of this component.
	 */
	static <I extends IArea> IImagemap of(I... areas) {
		Objects.requireNonNull(areas, "Children cannot be null");
		return of(Arrays.asList(areas));
	}

	/**
	 * Returns the instance with the given {@code width} and {@code height}.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 */
	static IImagemap ofSize(String width, String height) {
		return new IImagemap.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IImagemap ofId(String id) {
		return new IImagemap.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IImagemap IImagemap}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIImagemap.Builder {
	}

	/**
	 * Builds an updater of type {@link IImagemap} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IImagemapUpdater {}
}