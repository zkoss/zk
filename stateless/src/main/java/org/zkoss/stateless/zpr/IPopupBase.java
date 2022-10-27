/* IPopupBase.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:11:54 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.json.JSONAware;
import org.zkoss.lang.Strings;
import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.zul.Popup;

/**
 * Immutable {@link Popup} base component
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
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br>Denotes user has opened or closed a component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h2>Position</h2>
 * <p>You can simply specify a popup's position when attaching to a component by</p>
 * <ul>
 *     <li>built-in position</li>
 *     <li>x, y coordinate</li>
 * </ul>
 * <p>ZK supports the following position string:</p>
 * <img src="doc-files/IPopup_position.png"/>
 *
 * @author katherine
 */
public interface IPopupBase<I extends IPopupBase> extends IXulElement<I> {

	/**
	 * Returns whether this component is visible at client.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isVisible() {
		return false;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkVisible() {
		if (isVisible()) {
			throw new UnsupportedOperationException("Use open/close instead");
		}
	}

	/**
	 * Returns the open options that passed from {@link #withOpen(OpenOptionBuilder)}
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	@StatelessOnly
	OpenOptionBuilder getOpen();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code span}.
	 *
	 * <p>Sets the given open options to this popup to open.
	 *
	 * @param builder The allowed values of {@link OpenOptionBuilder}
	 * <p>Default: {@code null}.</p>
	 * @see OpenOptionBuilder
	 * @return A modified copy of the {@code this} object
	 */
	I withOpen(@Nullable OpenOptionBuilder builder);

	/**
	 * Internal use. (for generating updater's API)
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default boolean isClose() {
		return isVisible();
	}

	/**
	 * Sets to close for this popup. (The javadoc is used for Updater to refer)
	 * @param close True to close the popup by manual programmatically.
	 * @hidden for Javadoc
	 */
	I withClose(boolean close);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		OpenOptionBuilder open = getOpen();
		if (open != null) {
			render(renderer, "open", open);
		}
	}

	/**
	 * An OpenOption builder to add options for {@link #withOpen(OpenOptionBuilder)}
	 * to manipulate the open state programmatically.
	 */
	class OpenOptionBuilder implements JSONAware {
		private int[] xy;
		private String ref;
		private Position position;
		public OpenOptionBuilder() {
		}

		/**
		 * Specifies the x and y coordinates to open the popup at the client.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 */
		public OpenOptionBuilder xy(int x, int y) {
			xy = new int[]{x, y};
			return this;
		}

		/**
		 * Specifies the given reference component id to position the popup.
		 * <p>By default the position {@link Position#AT_POINTER} is assumed.
		 * @param refId the reference component id.
		 */
		public OpenOptionBuilder reference(String refId) {
			this.ref = refId;
			return this;
		}

		/**
		 * Specifies this popup open with the given position.
		 * @param position The allowed position {@link Position}
		 */
		public OpenOptionBuilder position(Position position) {
			this.position = position;
			return this;
		}

		/**
		 * Internal use
		 * @hidden for Javadoc
		 */
		public String toJSONString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			if (!Strings.isBlank(ref)) {
				sb.append("\"$").append(ref).append('"');
			}
			sb.append(',');
			if (xy != null) {
				sb.append("[").append(xy[0]).append(',').append(xy[1])
						.append("]");
			}
			sb.append(',');

			if (position != null) {
				sb.append('"').append(position.value).append('"');
			}

			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * Specify a popup's position when attaching to a component.
	 */
	enum Position {
		/**
		 * The component appears above the anchor, aligned to the left
		 */
		BEFORE_START("before_start"),
		/**
		 * The component appears above the anchor, aligned to the center
		 */
		BEFORE_CENTER("before_center"),
		/**
		 * The component appears above the anchor, aligned to the right
		 */
		BEFORE_END("before_end"),
		/**
		 * The component appears below the anchor, aligned to the left
		 */
		AFTER_START("after_start"),
		/**
		 * The component appears below the anchor, aligned to the center
		 */
		AFTER_CENTER("after_center"),
		/**
		 * The component appears below the anchor, aligned to the right
		 */
		AFTER_END("after_end"),
		/**
		 * The component appears to the left of the anchor, aligned to the top
		 */
		START_BEFORE("start_before"),
		/**
		 * The component appears to the left of the anchor, aligned to the middle
		 */
		START_CENTER("start_center"),
		/**
		 * The component appears to the left of the anchor, aligned to the bottom
		 */
		START_AFTER("start_after"),
		/**
		 * The component appears to the right of the anchor, aligned to the top
		 */
		END_BEFORE("end_before"),
		/**
		 * The component appears to the right of the anchor, aligned to the middle
		 */
		END_CENTER("end_center"),
		/**
		 * The component appears to the right of the anchor, aligned to the bottom
		 */
		END_AFTER("end_after"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at top-left
		 */
		TOP_LEFT("top_left"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at top-center
		 */
		TOP_CENTER("top_center"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at top-right
		 */
		TOP_RIGHT("top_right"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at middle-left
		 */
		MIDDLE_LEFT("middle_left"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at middle-center
		 */
		MIDDLE_CENTER("middle_center"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at middle-right
		 */
		MIDDLE_RIGHT("middle_right"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at bottom-left
		 */
		BOTTOM_LEFT("bottom_left"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at bottom-center
		 */
		BOTTOM_CENTER("bottom_center"),
		/**
		 * The component overlaps the anchor, with anchor and component aligned at bottom-right
		 */
		BOTTOM_RIGHT("bottom_right"),
		/**
		 * The component appears with the upper-left aligned with the mouse cursor
		 */
		AT_POINTER("at_pointer"),
		/**
		 * The component appears with the top aligned with the bottom of the mouse
		 * cursor, with the left side of the component at the horizontal position
		 * of the mouse cursor.
		 */
		AFTER_POINTER("after_pointer");

		final String value;

		Position(String value) {
			this.value = value;
		}
	}
}