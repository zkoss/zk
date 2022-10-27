/* ICheckbox.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:02:53 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.CheckData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Checkbox;

/**
 * Immutable {@link Checkbox} component
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
 *          <td><strong>ActionData</strong>: {@link CheckData}
 *          <br> Denotes when a component is checked or unchecked.</td>
 *       </tr>
 *       <tr>
 *          <td>onFocus</td>
 *          <td>Denotes when a component gets the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed when
 *          the event listener for {@code onFocus} got executed.</td>
 *       </tr>
 *       <tr>
 *          <td>onBlur</td>
 *          <td>Denotes when a component loses the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed
 *          when the event listener for {@code onBlur} got executed.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Molds</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "default"}</td>
 *          <td><img src="doc-files/ICheckbox_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "switch"}</td>
 *          <td><img src="doc-files/ICheckbox_mold_switch.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "toggle"}</td>
 *          <td><img src="doc-files/ICheckbox_mold_toggle.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "tristate"}</td>
 *          <td> Allowing users to set the {@code indeterminate} state, in addition
 *          to the checked and unchecked states. In {@code tristate} mold, when
 *          users click on the checkbox, it will switch between checked, unchecked
 *          and indeterminate states. This is different from the {@code default}
 *          mold which has only checked and unchecked states. <br>
 *              <img src="doc-files/ICheckbox_mold_tristate.png"/>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author katherine
 * @see Checkbox
 */
@StatelessStyle
public interface ICheckbox extends ICheckboxBase<ICheckbox>, IAnyGroup<ICheckbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICheckbox DEFAULT = new ICheckbox.Builder().build();

	/**
	 * Constant for switch mold attributes of this immutable component.
	 */
	ICheckbox SWITCH = new ICheckbox.Builder().setMold("switch").build();

	/**
	 * Constant for toggle mold attributes of this immutable component.
	 */
	ICheckbox TOGGLE = new ICheckbox.Builder().setMold("toggle").build();

	/**
	 * Constant for tristate mold attributes of this immutable component.
	 */
	ICheckbox TRISTATE = new ICheckbox.Builder().setMold("tristate").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Checkbox> getZKType() {
		return Checkbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Checkbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Checkbox";
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label of the component
	 */
	static ICheckbox of(String label) {
		return new ICheckbox.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label of the component
	 * @param image The image of the component
	 */
	static ICheckbox of(String label, String image) {
		return new ICheckbox.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image of the component
	 */
	static ICheckbox ofImage(String image) {
		return new ICheckbox.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ICheckbox ofId(String id) {
		return new ICheckbox.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ICheckbox ICheckbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableICheckbox.Builder {}

	/**
	 * Builds an updater of type {@link ICheckbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ICheckboxUpdater {}
}