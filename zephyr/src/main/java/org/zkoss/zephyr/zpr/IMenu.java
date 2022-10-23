/* IMenu.java

	Purpose:

	Description:

	History:
		Fri Oct 15 09:52:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Menu;

/**
 * Immutable {@link Menu} component
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
 *          <td>onClick</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.MouseData}
 *           <br>
 *          Represents an action triggered by the user clicks on the left hand side of the menu.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Menu
 */
@ZephyrStyle
public interface IMenu extends ILabelImageElement<IMenu>, IDisable<IMenu>, ISingleChildable<IMenu, IMenupopup>, IChildrenOfMenupopup<IMenu> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IMenu DEFAULT = new IMenu.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Menu> getZKType() {
		return Menu.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.menu.Menu"}</p>
	 */
	default String getWidgetClass() {
		return "zul.menu.Menu";
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: {@code ""} (empty).
	 *
	 */
	default String getContent() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code label}.
	 *
	 * <p> Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>There is a way to create Colorbox automatically by using
	 * #color=#RRGGBB, usage example <code>withContent("#color=#FFFFFF")</code>
	 *
	 * @param content The embedded content
	 * <p>Default: {@code ""}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenu withContent(String content);

	/**
	 * Returns the instance with the given {@link IMenupopup} child.
	 * @param child The {@link IMenupopup} child
	 */
	static IMenu of(IMenupopup child) {
		return new IMenu.Builder().setChild(child).build();
	}

	/**
	 * Returns the instance with the given label and a popup child.
	 * @param label The label of the menu.
	 * @param child The menupopup child.
	 */
	static IMenu of(String label, IMenupopup child) {
		return new IMenu.Builder().setLabel(label).setChild(child).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IMenu ofId(String id) {
		return new IMenu.Builder().setId(id).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the menu holds.
	 * @param image The image that the menu holds.
	 */
	static IMenu of(String label, String image) {
		return new IMenu.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the menu holds.
	 */
	static IMenu ofImage(String image) {
		return new IMenu.Builder().setImage(image).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);
		render(renderer, "content", getContent());
		render(renderer, "disabled", isDisabled());
	}

	/**
	 * Builds instances of type {@link IMenu IMenu}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIMenu.Builder {}

	/**
	 * Builds an updater of type {@link IMenu} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IMenuUpdater {}
}