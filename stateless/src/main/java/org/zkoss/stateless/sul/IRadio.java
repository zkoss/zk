/* IRadio.java

	Purpose:

	Description:

	History:
		Fri Dec 10 09:35:32 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.stateless.action.data.CheckData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.StatelessContentRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Radio;

/**
 * Immutable {@link Radio} component
 *
 * <p> A radio button.
 *
 * <p>Radio buttons without a ancestor {@link IRadiogroup} is considered
 * as the same group.
 * The nearest ancestor {@link IRadiogroup} is the group that the radio
 * belongs to.
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
 * @author katherine
 * @see Radio
 */
@StatelessStyle
public interface IRadio extends ICheckboxBase<IRadio>, IAnyGroup<IRadio> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IRadio DEFAULT = new IRadio.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Radio> getZKType() {
		return Radio.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Radio"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Radio";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IRadio checkSelected() {
		if (isSelected() && !isChecked()) {
			return new IRadio.Builder().from(this).setChecked(true).build();
		}
		return this;
	}

	/**
	 * Returns the id of the {@link IRadiogroup} that groups this radio
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	@StatelessOnly
	String getRadiogroup();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code radiogroupId}.
	 *
	 * <p>Sets the id of the {@link IRadiogroup} that groups this radio
	 *
	 * @param radiogroupId The id of {@link IRadiogroup}.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRadio withRadiogroup(@Nullable String radiogroupId);

	/**
	 * Returns whether it is selected.
	 * <p>Default: {@code false}.
	 */
	@Value.Lazy
	default boolean isSelected() {
		return isChecked();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code selected}.
	 *
	 * <p>Sets to select the radio or not.
	 *
	 * @param selected Whether to select the radio.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IRadio withSelected(boolean selected) {
		return withChecked(selected);
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label of the component
	 */
	static IRadio of(String label) {
		return new IRadio.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label of the component
	 * @param image The image of the component
	 */
	static IRadio of(String label, String image) {
		return new IRadio.Builder().setLabel(label).setImage(image).build();
	}


	/**
	 * Returns the instance with the given image.
	 * @param image The image of the component
	 */
	static IRadio ofImage(String image) {
		return new IRadio.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IRadio ofId(String id) {
		return new IRadio.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ICheckboxBase.super.renderProperties(renderer);
		String group = getRadiogroup();
		if (group != null) {
			Component fellow = ((StatelessContentRenderer) renderer).getBinding()
					.getFellow(group);
			if (fellow != null) {
				render(renderer, "radiogroup", fellow);
			} else {
				// if not found, we use JS to find it at client (this only works with Ajax update)
				render(renderer, "radiogroup",
						new JavaScriptValue("zk.$('$" + group + "')"));
			}
		}
	}

	/**
	 * Builds instances of type {@link IRadio IRadio}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIRadio.Builder {
	}

	/**
	 * Builds an updater of type {@link IRadio} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IRadioUpdater {}
}