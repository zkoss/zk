/* ICombobox.java

	Purpose:

	Description:

	History:
		Fri Oct 15 11:52:30 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.action.data.SelectData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Combobox;

/**
 * Immutable {@link Combobox} component
 *
 * <p>A combobox is a special text box that embeds a drop-down list.
 * With comboboxes, users are allowed to select from a drop-down list, in
 * addition to entering the text manually.</p>
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
 *          <td>onSelect</td>
 *          <td><strong>ActionData</strong>: {@link SelectData}
 *          <br> Represents an action caused by user's the list selection is changed
 *          at the client.</td>
 *       </tr>
 *       <tr>
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br> Denotes that the user has opened or closed a component.
 *          <b>Note:</b> unlike {@code onClose}, this action is only a notification.
 *          The client sends this action after opening or closing the component.
 *          <br> It is useful to implement load-on-demand by listening to the
 *          {@code onOpen} action, and creating components when the first time
 *          the component is opened.
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h2>Mouseless Entry</h2>
 * <ul>
 *     <li>{@code Alt+DOWN} to pop up the list.</li>
 *     <li>{@code Alt+UP} or {@code ESC} to close the list.</li>
 *     <li>{@code UP} and {@code DOWN} to change the selection of the items from the list.</li>
 *     <li>{@code ENTER} to confirm the change of selection.</li>
 *     <li>{@code ESC} to abort the change of selection. It is meaningful if
 *     {@code instantSelect} is false.</li>
 * </ul>
 *
 * <h2>Autocomplete</h2>
 * <p>By default, it will autocomplete your input with the first item in the list
 * that has the same starting string in a case-insensitive way.</p>
 *
 * @author katherine
 * @see Combobox
 */
@StatelessStyle
public interface ICombobox extends ITextboxBase<ICombobox>,
		IChildable<ICombobox, IComboitem>, IAnyGroup<ICombobox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICombobox DEFAULT = new ICombobox.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Combobox> getZKType() {
		return Combobox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Combobox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Combobox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ICombobox checkSelectedIndex() {
		int selectedIndex = this.getSelectedIndex();
		if (selectedIndex >= 0) {
			// check if index is not out of bound.
			IComboitem iComboitem = this.getChildren().get(selectedIndex);

			// simulate ZK Combobox#setSelectedItem()
			return withValue(iComboitem.getLabel());
		} else {
			return this;
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMultiline() {
		if (isMultiline())
			throw new UnsupportedOperationException("Combobox doesn't support multiline");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkRows() {
		int rows = getRows();
		if (rows != 1)
			throw new UnsupportedOperationException("Combobox doesn't support multiple rows, " + rows);
	}

	/**
	 * Returns the width of the popup of this component
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getPopupWidth();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code popupWidth}.
	 *
	 * <p>Sets the width of the popup of this component.
	 * If the input is a percentage, the popup width will be calculated by multiplying
	 * the width of this component with the percentage.
	 * (e.g. if the input string is {@code 130%}, and the width of this component
	 * is {@code 300px}, the popup width will be {@code 390px = 300px * 130%})
	 * Others will be set directly.
	 * @param popupWidth The width of the popup of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withPopupWidth(@Nullable String popupWidth);

	/**
	 * Returns the empty search message if any.
	 * Default: {@code null}
	 */
	@Nullable
	String getEmptySearchMessage();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code emptySearchMessage}.
	 *
	 * <p>Sets empty search message.
	 * This message would be displayed, when no matching results was found.
	 * Note: it's meaningless if no model case.
	 *
	 * @param emptySearchMessage The empty search message to display.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withEmptySearchMessage(@Nullable String emptySearchMessage);

	/** Returns the index of the selected item, or -1 if not selected.
	 */
	default int getSelectedIndex() {
		return -1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code selectedIndex}.
	 *
	 * <p>Deselects the currently selected items and selects
	 * the item with the given index.
	 * <p>Note: if the label of {@link IComboitem} has the same more than one, the first
	 * {@link IComboitem} will be selected at client side, it is a limitation of
	 * {@link ICombobox} and it is different from {@link IListbox}.</p>
	 *
	 * @param selectedIndex The selected index of the combobox.
	 * <p>Default: {@code -1}, meaning not selected.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withSelectedIndex(int selectedIndex);

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: {@code false}.
	 */
	default boolean isAutodrop() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autodrop}.
	 *
	 * <p>Sets whether to automatically drop the list if users is changing
	 * this text box.
	 *
	 * @param autodrop {@code true} to enable the auto-drop facility.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withAutodrop(boolean autodrop);

	/** Returns whether to automatically complete this text box
	 * by matching the nearest item ({@link IComboitem}.
	 * It is also known as auto-type-ahead.
	 *
	 * <p>If true, the nearest item will be searched and the text box is
	 * updated automatically.
	 * If false, user has to click the item or use the DOWN or UP keys to
	 * select it back.
	 *
	 * <p>Don't confuse it with the auto-completion feature mentioned by
	 * other framework. Such kind of auto-completion is supported well
	 * by listening to the {@code onChanging} action.
	 *
	 * <p>Default: {@code true}</p>
	 */
	default boolean isAutocomplete() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autocomplete}.
	 *
	 * <p>Sets whether to automatically complete this text box
	 * by matching the nearest item ({@link IComboitem}.
	 *
	 * @param autocomplete {@code false} to disable the auto-completion feature.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withAutocomplete(boolean autocomplete);

	/** Returns whether the button (on the right of the timebox) is visible.
	 * <p>Default: {@code true}.
	 */
	default boolean isButtonVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code buttonVisible}.
	 *
	 * <p>Sets whether the button (on the right of the combobox) is visible.
	 *
	 * @param buttonVisible {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withButtonVisible(boolean buttonVisible);

	/**
	 * Returns true if onSelect event is sent as soon as user selects using
	 * keyboard navigation.
	 * <p>Default: {@code true}
	 */
	default boolean isInstantSelect() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code instantSelect}.
	 *
	 * <p> Sets the instantSelect attribute. When the attribute is true, {@code onSelect}
	 * action will be fired as soon as user selects using keyboard navigation.
	 *
	 * If the attribute is false, user needs to press {@code Enter key} to finish
	 * the selection using keyboard navigation.
	 *
	 * @param instantSelect {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withInstantSelect(boolean instantSelect);

	/**
	 * Returns the iconSclass name of this Combobox.
	 * <p>Default: {@link Combobox#ICON_SCLASS}</p>
	 */
	default String getIconSclass() {
		return Combobox.ICON_SCLASS;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code iconSclass}.
	 *
	 * <p>Sets the iconSclass name of this Combobox.
	 *
	 * @param iconSclass The iconSclass name of this Combobox.
	 * <p>Default: {@link Combobox#ICON_SCLASS}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withIconSclass(String iconSclass);

	/** Returns whether this combobox is open.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sclass}.
	 *
	 * <p>Sets whether this combobox is open or not.
	 *
	 * @param open {@code true} to open the dropdown.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICombobox withOpen(boolean open);

	/**
	 *
	 * Returns the instance with the given value.
	 * @param value The value of the component
	 */
	static ICombobox of(String value) {
		return new ICombobox.Builder().setValue(value).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link IComboitem} children.
	 * @param children The children of {@link IComboitem}
	 */
	static ICombobox of(Iterable<? extends IComboitem> children) {
		return new ICombobox.Builder().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link IComboitem} children.
	 * @param children The children of {@link IComboitem}
	 */
	static ICombobox of(IComboitem... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The combobox constraint
	 */
	static ICombobox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ICombobox ofId(String id) {
		return new ICombobox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ITextboxBase.super.renderProperties(renderer);

		render(renderer, "autodrop", isAutodrop());
		if (!isAutocomplete())
			renderer.render("autocomplete", false);
		if (!isButtonVisible())
			renderer.render("buttonVisible", false);
		int selectedIndex = getSelectedIndex();
		if (selectedIndex > -1)
			renderer.render("selectedIndex_", selectedIndex);
		String _popupWidth = getPopupWidth();
		if (_popupWidth != null)
			renderer.render("popupWidth", _popupWidth);
		String _emptySearchMessage = getEmptySearchMessage();
		if (_emptySearchMessage != null)
			renderer.render("emptySearchMessage", _emptySearchMessage);
		if (!isInstantSelect())
			renderer.render("instantSelect", false);
		String _iconSclass = getIconSclass();
		if (!Combobox.ICON_SCLASS.equals(_iconSclass))
			renderer.render("iconSclass", _iconSclass);

		// handle open state here instead of send AuInvoke for Stateless
		if (isOpen())
			renderer.render("open", true);
	}

	/**
	 * Builds instances of type {@link ICombobox ICombobox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableICombobox.Builder {}

	/**
	 * Builds an updater of type {@link ICombobox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IComboboxUpdater {}
}