/* IRating.java

	Purpose:

	Description:

	History:
		Wed Nov 03 16:53:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.RequestData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Rating;

/**
 * Immutable {@link Rating} component
 *
 * <p>
 * A rating component provides a icon based rating input.
 * The default icon is Unicode {@code "\u2605"} star icon.
 * Icons could be set to unicode icons by specifying Unicode.
 * (for example: {@code "\u260E"})
 * Or even Font Awesome icons with the prefix z-icon.
 * (for example: {@code "z-icon-home"})
 * </p>
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onChange</td>
 *          <br> Denotes user has rated, the value can be received from {@link RequestData#getData()} with the key {@code "rating"}</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Rating
 */
@StatelessStyle
public interface IRating extends IHtmlBasedComponent<IRating>, IDisable<IRating>, IReadonly<IRating> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IRating DEFAULT = new IRating.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Rating> getZKType() {
		return Rating.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Rating"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Rating";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkRating() {
		int rating = getRating();
		int _max = getMax();
		if (_max < rating) {
			throw new UiException("max should be larger than rating");
		}
	}

	/**
	 * Returns the max rating of this rating component.
	 * Default: {@code 5}
	 */
	default int getMax() {
		return 5;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code max}.
	 *
	 * <p> Sets the max rating of this rating component
	 *
	 * @param max The max rating of this rating component
	 * <p>Default: {@code 5}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withMax(int max);

	/**
	 * Returns the rating.
	 * <p>Default: {@code 0}</p>
	 */
	default int getRating() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code rating}.
	 *
	 * <p> Sets the rating.
	 *
	 * @param rating Sets the rating.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withRating(int rating);

	/**
	 * Returns if this rating component is readonly.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isReadonly() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code readonly}.
	 *
	 * <p> Sets if this rating component is readonly.
	 *
	 * @param readonly Whether this rating component is readonly.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withReadonly(boolean readonly);

	/**
	 * Returns whether this rating component is cancelable.
	 * If true, the rating could be cancelled by clicking the rated-rating again.
	 * <p>Default: {@code true}
	 */
	default boolean isCancelable() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code cancelable}.
	 *
	 * <p> Sets whether this rating component is cancelable.
	 *
	 * @param cancelable Whether this rating component is cancelable.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withCancelable(boolean cancelable);

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
	 * <p> Sets the orient.
	 *
	 * @param orient Either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient.
	 *
	 * @param orient Either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IRating withOrient(Orient orient) {
		return withOrient(orient.value);
	}

	/**
	 * Returns the iconSclass name of this rating.
	 * <p>Default: {@code "z-icon-star"}</p>
	 */
	default String getIconSclass() {
		return "z-icon-star";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code iconSclass}.
	 *
	 * <p> Sets the iconSclass name of this rating.
	 *
	 * @param iconSclass The iconSclass name of this rating.
	 * <p>Default: {@code "z-icon-star"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRating withIconSclass(String iconSclass);

	/**
	 * Returns the instance with the given rating.
	 * @param rating The rating of the component.
	 */
	static  IRating of(int rating) {
		return new IRating.Builder().setRating(rating).build();
	}

	/**
	 * Returns the instance with the given max
	 * @param max The maximum value of the rating.
	 */
	static  IRating ofMax(int max) {
		return new IRating.Builder().setMax(max).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IRating ofId(String id) {
		return new IRating.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		int _max = getMax();
		int _rating = getRating();

		if (_max <= 0)
			throw new UiException("max should be > 0");
		if (_rating > _max) {
			throw new UiException("Out of bound: " + _rating + " while max is" + _max);
		}
		if (_rating != 0) {
			render(renderer, "rating", _rating);
		}
		if (_max != 5) {
			render(renderer, "_max", _max);
		}
		if (isDisabled()) {
			renderer.render("disabled", true);
		}
		if (isReadonly()) {
			renderer.render("readonly", true);
		}
		if (!isCancelable()) {
			renderer.render("_cancelable", false);
		}
		if (!"horizontal".equals(getOrient())) {
			render(renderer, "_orient", "vertical");
		}
		String _iconSclass = getIconSclass();
		if (!"z-icon-star".equals(_iconSclass)) {
			render(renderer, "iconSclass", _iconSclass);
		}
	}

	/**
	 * Builds instances of type {@link IRating IRating}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIRating.Builder {
	}

	/**
	 * Builds an updater of type {@link IRating} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IRatingUpdater {}

	/**
	 * Specifies the orient with {@link #withOrient(Orient)}
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