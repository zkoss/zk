/* IArea.java

	Purpose:

	Description:

	History:
		Thu Oct 07 10:57:00 CST 2021, Created by katherine

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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Area;

/**
 * Immutable {@link Area} component
 *
 * <p>An area of an {@link IImagemap}. Instead of the application processing the coordinates,
 * developers can add the {@link IArea} components as children of a {@link IImagemap} component thus defining a target.
 * The {@link IImagemap} component will translate the mouse pointer coordinates into a logical name e.g. The id of the area the user clicked.
 *
 * If the coordinates in one area component overlap with another, the first one takes precedence.
 * @author katherine
 * @see Area
 */
@StatelessStyle
public interface IArea extends IComponent<IArea> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	IArea DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Area> getZKType() {
		return Area.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Area"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.wgt.Area";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkShape() {
		String shape = getShape();
		if (shape != null) {
			if (!"rect".equals(shape) && !"rectangle".equals(shape) && !"circle".equals(shape)
					&& !"circ".equals(shape) && !"polygon".equals(shape) && !"poly".equals(shape)) {
				throw new WrongValueException("Unknown shape: " + shape);
			}
		}
	}

	/**
	 * Returns the coordination of this area.
	 */
	@Nullable
	String getCoords();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code coords}.
	 *
	 * <p>Sets the {@code coords} of this area.
	 * Its content depends on {@link #getShape}:
	 * <dl>
	 * <dt>circle</dt>
	 * <dd>coords="x,y,r"</dd>
	 * <dt>polygon</dt>
	 * <dd>coords="x1,y1,x2,y2,x3,y3..."<br/>
	 * The polygon is automatically closed, so it is not necessary to repeat
	 * the first coordination.</dd>
	 * <dt>rectangle</dt>
	 * <dd>coords="x1,y1,x2,y2"</dd>
	 * </dl>
	 *
	 * <p>Note: (0, 0) is the upper-left corner.
	 *
	 * If the coordinates in one IArea component overlap with another, the first one takes precedence.
	 * @return A modified copy of the {@code this} object
	 */
	IArea withCoords(@Nullable String coords);

	/**
	 * Returns the shape of this area.
	 * <p>Default: {@code null} (means rectangle).
	 */
	@Nullable
	String getShape();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code shape}.
	 *
	 * <p>Sets the {@code shape} of this IArea.
	 *
	 * @param shape {@code "rectangle"}, {@code "rect"}, {@code "circle"}, {@code "cric"}, {@code "polygon"}, {@code "poly"} or {@code null}.
	 * @return A modified copy of the {@code this} object
	 */
	IArea withShape(@Nullable String shape);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code shape}.
	 *
	 * <p>Sets the {@code shape} of this IArea.
	 * @param shape {@link Shape#RECTANGLE}, {@link Shape#CIRCLE} or {@link Shape#POLYGON}.
	 * @return A modified copy of the {@code this} object
	 */
	default IArea withShape(@Nullable Shape shape) {
		return withShape(shape.value);
	}

	/**
	 * Returns the text as the tooltip.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getTooltiptext();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tooltiptext}.
	 *
	 * <p>Sets the text as the tooltip.
	 * @param tooltiptext The text as the tooltip.
	 * @return A modified copy of the {@code this} object
	 */
	IArea withTooltiptext(@Nullable String tooltiptext);

	/**
	 * Returns {@code null} if not set.
	 * @return the tab order of this component
	 */
	@Nullable
	Integer getTabindex();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tabindex}.
	 *
	 * <p>Sets the tab order of this component.
	 * Removes the tabindex attribute if it's set to {@code null}.
	 * @param tabindex The tab order of this component.
	 * @return A modified copy of the {@code this} object
	 */
	IArea withTabindex(@Nullable Integer tabindex);

	/**
	 * Returns the instance with the given coordinates.
	 * @param coords The coordinates of this area.
	 */
	static IArea of(String coords) {
		return new IArea.Builder().setCoords(coords).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IArea ofId(String id) {
		return new IArea.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IComponent.super.renderProperties(renderer);

		render(renderer, "coords", getCoords());
		render(renderer, "shape", getShape());
		render(renderer, "tooltiptext", getTooltiptext());
		Integer _tabindex = getTabindex();
		if (_tabindex != null)
			renderer.render("tabindex", _tabindex);
	}

	/**
	 * Builds instances of type {@link IArea IArea}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIArea.Builder {}

	/**
	 * Builds an updater of type {@link IArea} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IAreaUpdater {}

	/**
	 * Insert Adjacent {@code direction} for {@link #withShape(Shape)}}
	 */
	enum Shape {
		/**
		 * Treat the {@code coords} format as a rectangle.
		 * {@code withCoords("x1, y1, x2, y2")}
		 * Where the first coordinate pair is one corner of the rectangle and the other pair is the corner diagonally opposite.
		 * A rectangle is just a shortened way of specifying a polygon with four vertices.
		 */
		RECTANGLE("rectangle"),
		/**
		 * Treat the {@code coords} format as a circle.
		 * {@code withCoords("x, y, r")}
		 * Where x and y define the position of the circle’s center and r is the radius in pixels.
		 */
		CIRCLE("circle"),
		/**
		 * Treat the {@code coords} format as a rectangle.
		 * {@code withCoords("x1, y1, x2, y2, x3, y3...")}
		 * Where each pair of x and y define a point of the polygon. At least three pairs of coordinates are required to define a triangle.
		 * The polygon is automatically closed, so it is not necessary to repeat the first coordinate at the end of the list to close the region.
		 */
		POLYGON("polygon");

		private final String value;

		Shape(String value) {
			this.value = value;
		}
	}
}