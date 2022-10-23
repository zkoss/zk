/* ICombobutton.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:30:13 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Combobutton;

/**
 * Immutable {@link Combobutton} component
 * <p>A Combobutton is a special Button that embeds a popup or menupopup child.</p>
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
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.OpenData}
 *          <br>Denotes when the child popup is opened or closed by a user action.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Combobutton
 */
@ZephyrStyle
public interface ICombobutton extends IButtonBase<ICombobutton>,
		ISingleChildable<ICombobutton, IPopupBase>, IAnyGroup<ICombobutton> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICombobutton DEFAULT = new ICombobutton.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	@Override
	default Class<Combobutton> getZKType() {
		return Combobutton.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Combobutton"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Combobutton";
	}

	/** Returns whether to automatically drop the popup if user hovers on this
	 * Combobutton.
	 * <p>Default: {@code false}.
	 */
	default boolean isAutodrop() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autodrop}.
	 *
	 * <p> Sets whether to automatically drop the popup if user hovers on this
	 * Combobutton.
	 *
	 * @param autodrop Whether to automatically drop the popup if user hovers on this
	 * Combobutton.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobutton withAutodrop(boolean autodrop);

	/**
	 * Returns the instance with the given label and popup.
	 * @param label The label that the component
	 * @param popup The popup of the component
	 */
	static <I extends IPopupBase> ICombobutton of(String label, I popup) {
		return new ICombobutton.Builder().setLabel(label).setChild(popup).build();
	}
	/**
	 * Returns the instance with the given popup
	 * 	 * @param popup The popup of the component
	 */
	static <I extends IPopupBase> ICombobutton of(I iPopup) {
		return new ICombobutton.Builder().setChild(iPopup).build();
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static ICombobutton of(String label) {
		return new ICombobutton.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the button holds.
	 * @param image The image that the button holds.
	 */
	static ICombobutton of(String label, String image) {
		return new ICombobutton.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the button holds.
	 */
	static ICombobutton ofImage(String image) {
		return new ICombobutton.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The button orient
	 */
	static ICombobutton ofOrient(Orient orient) {
		return new ICombobutton.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given type.
	 * @param type The button type
	 */
	static ICombobutton ofType(Type type) {
		return new ICombobutton.Builder().setType(type.value).build();
	}

	/**
	 * Returns the instance with the given dir.
	 * @param dir The button dir
	 */
	static ICombobutton ofDir(Direction dir) {
		return new ICombobutton.Builder().setDir(dir.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ICombobutton ofId(String id) {
		return new ICombobutton.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IButtonBase.super.renderProperties(renderer);
		render(renderer, "autodrop", isAutodrop());
	}

	/**
	 * Builds instances of type {@link ICombobutton ICombobutton}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableICombobutton.Builder {}

	/**
	 * Builds an updater of type {@link ICombobutton} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ICombobuttonUpdater {}
}