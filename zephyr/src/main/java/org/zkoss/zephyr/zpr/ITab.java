/* ITab.java

	Purpose:

	Description:

	History:
		Wed Oct 27 17:39:26 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Tab;

/**
 * Immutable {@link Tab} component
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
 *          <td>onClose</td>
 *          <td>Denotes the close button is pressed by a user, and the component shall detach itself.</td>
 *       </tr>
 *       <tr>
 *          <td>onSelect</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.SelectData}
 *          <br>Denotes user has selected a tab. onSelect is sent to both tab and tabbox.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Tab
 */
@ZephyrStyle
public interface ITab extends ILabelImageElement<ITab> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITab DEFAULT = new ITab.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tab> getZKType() {
		return Tab.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.tab.Tab"}</p>
	 */
	default String getWidgetClass() {
		return "zul.tab.Tab";
	}

	/**
	 * Returns whether this tab is disabled.
	 * <p> Default: {@code false}.
	 *
	 */
	default boolean isDisabled() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code disabled}.
	 *
	 * <p> Sets whether it is disabled.
	 *
	 * @param disabled {@code ture} to disable this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITab withDisabled(boolean disabled);

	/**
	 * Returns whether this tab is closable. If closable, a button is displayed
	 * and the {@code onClose} action is sent if a user clicks the button.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isClosable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closable}.
	 *
	 * <p> Sets whether this tab is closable. If closable, a button is displayed and
	 * the onClose action is sent if a user clicks the button.
	 *
	 * @param closable {@code ture} to disable this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITab withClosable(boolean closable);

	/**
	 * Returns whether this tab is selected.
	 */
	default boolean isSelected() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code selected}.
	 *
	 * <p> Sets whether this tab is selected.
	 *
	 * @param selected {@code ture} to select this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITab withSelected(boolean selected);

	/** Returns the caption of this tab.
	 */
	@Nullable
	ICaption<IAnyGroup> getCaption();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code caption}.
	 *
	 * <p>Sets the caption child of this component.
	 *
	 * @param caption The caption child of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITab withCaption(@Nullable ICaption<IAnyGroup> caption);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the tab holds.
	 */
	static ITab of(String label) {
		return new ITab.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the tab holds.
	 * @param image The image that the tab holds.
	 */
	static ITab of(String label, String image) {
		return new ITab.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the tab holds.
	 */
	static ITab ofImage(String image) {
		return new ITab.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given {@link ICaption}.
	 * @param caption The caption container of this component.
	 */
	static ITab ofCaption(ICaption caption) {
		return new ITab.Builder().setCaption(caption).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITab ofId(String id) {
		return new ITab.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);
		boolean _disabled = isDisabled();
		if (_disabled)
			render(renderer, "disabled", _disabled);
		boolean _selected = isSelected();
		if (_selected)
			render(renderer, "selected", _selected);
		boolean _closable = isClosable();
		if (_closable)
			render(renderer, "closable", _closable);
	}

	/**
	 * Builds instances of type {@link ITab ITab}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITab.Builder {
	}

	/**
	 * Builds an updater of type {@link ITab} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITabUpdater {}
}