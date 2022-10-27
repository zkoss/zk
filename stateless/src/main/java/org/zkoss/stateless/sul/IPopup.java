/* IPopup.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:57:50 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Popup;

/**
 * Immutable {@link Popup} component
 *
 * <p>
 * A container that is displayed as a popup.
 * The popup window does not have any special frame.
 * Popups can be displayed when an element is clicked by assigning
 * the id of the popup to either the {@link #withPopup},
 * {@link #withContext} or {@link #withTooltip} attribute of the component.
 * </p>
 *
 * @author katherine
 * @see Popup
 */
@StatelessStyle
public interface IPopup<I extends IAnyGroup> extends IPopupBase<IPopup<I>>,
		IChildable<IPopup<I>, I>, IAnyGroup<IPopup<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IPopup<IAnyGroup> DEFAULT = new IPopup.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Popup> getZKType() {
		return Popup.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Popup"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Popup";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IPopup<I> of(Iterable<? extends I> children) {
		return new IPopup.Builder<I>().setVisible(false).setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IPopup<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IPopup<I> ofId(String id) {
		return new IPopup.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IPopup IPopup}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIPopup.Builder<I> {}

	/**
	 * Builds an updater of type {@link IPanelchildren} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IPopupUpdater {}
}