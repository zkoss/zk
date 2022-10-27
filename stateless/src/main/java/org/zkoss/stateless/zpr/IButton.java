/* IButton.java

	Purpose:

	Description:

	History:
		Tue Oct 09:55:22 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Button;

/**
 * Immutable {@link Button} component
 *
 * <p>You could assign a {@code label} and an {@code image} to a button by the  {@code label}
 * and  {@code image} attributes.
 * If both are specified, the {@link #withDir(String) dir} attribute control which
 * is displayed up front, and the {@link #withOrient(String) orient} attribute controls
 * whether the layout is horizontal or vertical.</p>
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
 *          <td>onFocus</td>
 *          <td>Denotes when a component gets the focus.</td>
 *       </tr>
 *       <tr>
 *          <td>onBlur</td>
 *          <td>Denotes when a component loses the focus.</td>
 *       </tr>
 *       <tr>
 *          <td>onUpload</td>
 *          <td><strong>ActionData</strong>: {@link FileData}
 *          <br>Denotes user has uploaded a file to the component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Button
 */
@StatelessStyle
public interface IButton extends IButtonBase<IButton>, IAnyGroup<IButton> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IButton DEFAULT = new IButton.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Button> getZKType() {
		return Button.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Button"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Button";
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static IButton of(String label) {
		return new IButton.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the button holds.
	 * @param image The image that the button holds.
	 */
	static IButton of(String label, String image) {
		return new IButton.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the button holds.
	 */
	static IButton ofImage(String image) {
		return new IButton.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The button orient
	 */
	static IButton ofOrient(Orient orient) {
		return new IButton.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given type.
	 * @param type The button type
	 */
	static IButton ofType(Type type) {
		return new IButton.Builder().setType(type.value).build();
	}

	/**
	 * Returns the instance with the given dir.
	 * @param dir The button dir
	 */
	static IButton ofDir(Direction dir) {
		return new IButton.Builder().setDir(dir.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IButton ofId(String id){
		return new IButton.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IButton IButton}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIButton.Builder {}

	/**
	 * Builds an updater of type {@link IButton} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IButtonUpdater {}
}