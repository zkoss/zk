/* ITextbox.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:15:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Textbox;

/**
 * Immutable {@link Textbox} component
 *
 * <p>A textbox is used to let users input textual data.
 * <p>You could assign {@code value}, {@code type}, {@code constraint}, {@code rows},
 * {@code cols} to a textbox using the corresponding APIs. When you assign the attribute
 * {@link #withType(String) type} to a string value {@code "password"} when {@link #isMultiline() multiline}
 * is false ({@link #isMultiline() multiline} will be true if you set rows larger than 1 or set {@link #withMultiline(boolean) multiline}
 * to true directly) then any character in this component will replace by '*'.
 *
 * <p>You could also assign a constraint value with a regular expression string
 * or a default constraint expression (available value is {@code "no empty"}).
 * When user change the value of textbox, it will cause a validating process to validate the value
 * at client. If the validation fails, then a notification will pop up.
 * </p>
 *
 * <h2>Example</h2>
 * <img src="doc-files/ITextbox_example.png"/>
 *
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IHlayout.of(
 *         ITextbox.of("text..."),
 *         ITextbox.of("secret").withType(ITextboxBase.Type.PASSWORD),
 *         ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: Please enter an e-mail address"),
 *         ITextbox.of("text line1...\ntext line2...").withRows(5).withCols(40)
 *     );
 * }
 * @author katherine
 * @see Textbox
 */
@StatelessStyle
public interface ITextbox extends ITextboxBase<ITextbox>, IAnyGroup<ITextbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITextbox DEFAULT = new ITextbox.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Textbox> getZKType() {
		return Textbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Textbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Textbox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITextbox checkRows() {
		int rows = getRows();
		if (rows != 1) {
			if (this.getVflex() != null) {
				throw new UiException("Not allowed to set rows and vflex at the same time");
			} else if (this.getHeight() != null) {
				throw new UiException("Not allowed to set rows and height at the same time");
			}
		} else if(rows <= 0)
			throw new WrongValueException("Illegal rows: " + rows);

		if (rows > 1) {
			return withMultiline(true); //auto-enable
		}
		return this;
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The textbox value
	 */
	static ITextbox of(String value) {
		return new ITextbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given multiline.
	 * @param multiline Whether to enable multiline or not
	 */
	static ITextbox ofMultiline(boolean multiline) {
		return new ITextbox.Builder().setMultiline(multiline).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static ITextbox ofCols(int cols) {
		return new ITextbox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The textbox constraint
	 */
	static ITextbox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITextbox ofId(String id) {
		return new ITextbox.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITextbox ITextbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITextbox.Builder {}

	/**
	 * Builds an updater of type {@link ITextbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITextboxUpdater {}
}
