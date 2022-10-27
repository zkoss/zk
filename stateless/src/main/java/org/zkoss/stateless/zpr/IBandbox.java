/* IBandbox.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:14:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Bandbox;

/**
 * Immutable {@link Bandbox} component.
 *
 * <p>A bandbox is a special text box that embeds a customizable popup window
 * (aka., a dropdown window). Like comboboxes, a bandbox consists of an input box
 * and a popup window. The popup window is opened automatically, when a user presses
 * {@code Alt+DOWN} or clicks the magnifier button.
 * <p> Unlike comboboxes, the popup window of a bandbox could be anything.
 * It is designed to give developers the maximal flexibility. A typical use is to
 * represent the popup window as a search dialog.
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
 *          <br>Denotes user has opened or closed a component.
 *          <b>Note:</b> unlike {@code onClose}, this action is only a notification.
 *          The client sends this action after opening or closing the component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h2>Mouseless Entry</h2>
 * <ul>
 *     <li>{@code Alt+DOWN} to pop up the list.</li>
 *     <li>{@code Alt+UP} or {@code ESC} to close the list.</li>
 * </ul>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be {@code no empty},
 * and/or a regular expression.</p>
 * <p>To specify two or more constraints, use comma to separate them as follows.</p>
 * <pre><code>IBandbox.ofConstraint("no empty,/^A/");</code></pre>
 *
 * <p>To specify a regular expression, you may have to use the character <b>{@code /}</b>
 * to enclose the regular expression as follows.</p>
 * <pre><code>IBandbox.ofConstraint("/^A/");</code></pre>
 *
 * <p>If you prefer to display different message to the default one, you can
 * append the error message to the constraint with a colon.</p>
 * <pre><code>IBandbox.ofConstraint("/^A/: only allowed the item start with A");</code></pre>
 *
 * @author katherine
 * @see Bandbox
 */
@StatelessStyle
public interface IBandbox extends ITextboxBase<IBandbox>,
		ISingleChildable<IBandbox, IBandpopup>, IAnyGroup<IBandbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IBandbox DEFAULT = new IBandbox.Builder().build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Bandbox> getZKType() {
		return Bandbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Bandbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Bandbox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMultiline() {
		if (this.isMultiline())
			throw new UnsupportedOperationException("Bandbox doesn't support multiline");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkRows() {
		int rows = this.getRows();
		if (rows != 1)
			throw new UnsupportedOperationException("Bandbox doesn't support multiple rows, " + rows);
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
	IBandbox withPopupWidth(@Nullable String popupWidth);

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
	IBandbox withAutodrop(boolean autodrop);

	/** Returns whether the button (on the right of the timebox) is visible.
	 * <p>Default: {@code true}.
	 */
	default boolean isButtonVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code buttonVisible}.
	 *
	 * <p>Sets whether the button (on the right of the bandbox) is visible.
	 *
	 * @param buttonVisible {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBandbox withButtonVisible(boolean buttonVisible);

	/**
	 * Returns the iconSclass name of this Bandbox.
	 * <p>Default: {@link Bandbox#ICON_SCLASS}</p>
	 */
	default String getIconSclass() { return Bandbox.ICON_SCLASS; };

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code iconSclass}.
	 *
	 * <p>Sets the iconSclass name of this Bandbox.
	 *
	 * @param iconSclass The iconSclass name of this Bandbox.
	 * <p>Default: {@link Bandbox#ICON_SCLASS}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBandbox withIconSclass(String iconSclass);

	/** Returns whether this bandbox is open.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sclass}.
	 *
	 * <p>Sets whether this bandbox is open or not.
	 *
	 * @param open {@code true} to open the dropdown.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IBandbox withOpen(boolean open);

	/**
	 * Return the instance of the given value.
	 * @param value The value of the text box.
	 */
	static IBandbox of(String value) {
		return new IBandbox.Builder().setValue(value).build();
	}

	/**
	 * Return the instance of the given value and bandpopup child.
	 * @param value The value of the text box.
	 * @param bandpopup The popup child of the bandbox
	 */
	static IBandbox of(String value, IBandpopup bandpopup) {
		return new IBandbox.Builder().setValue(value).setChild(bandpopup).build();
	}

	/**
	 * Return the instance of the given bandpopup child.
	 * @param bandpopup The popup child of the bandbox
	 */
	static IBandbox of(IBandpopup bandpopup) {
		return new IBandbox.Builder().setChild(bandpopup).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static IBandbox ofCols(int cols) {
		return new IBandbox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The bandbox constraint
	 */
	static IBandbox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IBandbox ofId(String id) {
		return new IBandbox.Builder().setId(id).build();
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
		if (!isButtonVisible())
			renderer.render("buttonVisible", false);
		if (getPopupWidth() != null)
			renderer.render("popupWidth", getPopupWidth());
		String _iconSclass = getIconSclass();
		if (!Bandbox.ICON_SCLASS.equals(_iconSclass))
			renderer.render("iconSclass", _iconSclass);

		if (isOpen())
			renderer.render("open", true);
	}

	/**
	 * Builds instances of type {@link IBandbox IBandbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIBandbox.Builder {}

	/**
	 * Builds an updater of type {@link IBandbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IBandboxUpdater {}
}