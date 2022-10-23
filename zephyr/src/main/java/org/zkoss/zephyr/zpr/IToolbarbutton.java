/* IToolbarbutton.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:10:22 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import org.immutables.value.Value;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Toolbarbutton;

/**
 * Immutable {@link Toolbarbutton} component
 * <p>The behavior of Toolbarbutton is similar to the button except the appearance
 * is different. The button component uses HTML BUTTON tag, while the toolbarbutton
 * component uses HTML DIV tag.
 * <p>A toolbarbutton could be placed outside a toolbar. However toolbarbuttons change
 * their appearance if they are placed inside a toolbar.
 * <p> Toolbarbutton supports getHref(). If getHref() is not null, the onClick handler
 * is ignored and this element is degenerated to HTML's A tag.
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
 *          <td>onCheck</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.CheckData}
 *          <br>Denotes when toolbarbutton is checked , only available in toggle mode.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Toolbarbutton
 */
@ZephyrStyle
public interface IToolbarbutton extends WithIToolbarbutton, IButtonBase<IToolbarbutton>, IAnyGroup<IToolbarbutton> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IToolbarbutton DEFAULT = new IToolbarbutton.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Toolbarbutton> getZKType() {
		return Toolbarbutton.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Toolbarbutton"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Toolbarbutton";
	}

	/** Returns the current mode.
	 *  <p>Default: {@code "default"}</p>
	 */
	default String getMode() {
		return "default";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code mode}.
	 *
	 * <p>Sets the mode to {@code "default"} and {@code "toggle"}
	 *
	 * @param mode The mode of this component.
	 * <p>Default: {@code "default"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbarbutton withMode(String mode);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code mode}.
	 *
	 * <p>Sets the mode to {@code "default"} and {@code "toggle"}
	 *
	 * @param mode The mode of this component.
	 * <p>Default: {@code "default"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IToolbarbutton withMode(Mode mode) {
		return withMode(mode.value);
	}

	/** Returns whether it is checked.
	 * <p>Default: {@code false}.
	 */
	default boolean isChecked() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code checked}.
	 *
	 * <p>Sets whether it is checked.
	 *
	 * @param checked Whether it is checked.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbarbutton withChecked(boolean checked);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static IToolbarbutton of(String label) {
		return new IToolbarbutton.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the button holds.
	 * @param image The image that the button holds.
	 */
	static IToolbarbutton of(String label, String image) {
		return new IToolbarbutton.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the button holds.
	 */
	static IToolbarbutton ofImage(String image) {
		return new IToolbarbutton.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given mode.
	 * @param mode The button mode
	 */
	static IToolbarbutton ofMode(Mode mode) {
		return new IToolbarbutton.Builder().setMode(mode.value).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The button orient
	 */
	static IToolbarbutton ofOrient(Orient orient) {
		return new IToolbarbutton.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given dir.
	 * @param dir The button dir
	 */
	static IToolbarbutton ofDir(Direction dir) {
		return new IToolbarbutton.Builder().setDir(dir.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IToolbarbutton ofId(String id) {
		return new IToolbarbutton.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IButtonBase.super.renderProperties(renderer);
		render(renderer, "mode", getMode());
		render(renderer, "checked", isChecked());
	}

	/**
	 * Builds instances of type {@link IToolbarbutton IToolbarbutton}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIToolbarbutton.Builder {}

	/**
	 * Builds an updater of type {@link IToolbarbutton} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IToolbarbuttonUpdater {}

	/**
	 * Specifies the mode with {@link #withMode(Mode)}.
	 */
	enum Mode {
		/**
		 * The default mode
		 */
		DEFAULT("default"),

		/**
		 * The toggle mode
		 */
		TOGGLE("toggle");

		final String value;
		Mode(String value) {
			this.value = value;
		}
	}
}