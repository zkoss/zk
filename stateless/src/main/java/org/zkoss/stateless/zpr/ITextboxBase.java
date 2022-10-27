/* ITextboxBase.java

	Purpose:

	Description:

	History:
		4:38 PM 2021/10/18, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Textbox;

/**
 * Immutable {@link Textbox} base component.
 * @author jumperchen
 */
public interface ITextboxBase<I extends ITextboxBase> extends IInputElement<I, String> {

	// override super's method for generate Javadoc in Updater
	I withValue(@Nullable String value);

	/** Returns whether it is multiline.
	 * <p>Default: {@code false}.
	 */
	default boolean isMultiline() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code multiline}.
	 *
	 * <p> Sets whether it is multiline.
	 *
	 * @param multiline {@code true} to enable multiline.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withMultiline(boolean multiline);

	/** Returns the rows.
	 * <p>Default: {@code 1}.
	 */
	default int getRows() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rows}.
	 *
	 * <p>Sets the rows.
	 * <p><b>Note:</b> Not allowed to set rows and height/vflex at the same time
	 *
	 * @param rows The number of the row to display.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withRows(int rows);

	/** Returns whether TAB is allowed.
	 * If true, the user can enter TAB in the textbox, rather than change focus.
	 * <p>Default: {@code false}.
	 */
	default boolean isTabbable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tabbable}.
	 *
	 * <p>Sets whether TAB is allowed. If true, the user can enter TAB in the
	 * textbox, rather than change focus.
	 *
	 * @param tabbable {@code true} to enable TAB in the textbox.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTabbable(boolean tabbable);

	/** Returns the type.
	 * <p>Default: {@code "text"}.
	 */
	default String getType() {
		return ITextboxCtrl.TEXT;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code type}.
	 *
	 * <p>Sets the type of the textbox. Acceptable values are {@code "text"},
	 * {@code "password"}, {@code "tel"}, {@code "email"}, and {@code "url"}
	 *
	 * @param type The type of the textbox.
	 * <p>Default: {@code "text"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withType(String type);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code type}.
	 *
	 * <p>Sets the type of the textbox. Acceptable values are {@link Type}
	 *
	 * @param type The type of the textbox.
	 * <p>Default: {@code "text"}.</p>
	 * @return A modified copy of the {@code this} object
	 * @see Type
	 */
	default I withType(Type type) {
		Objects.requireNonNull(type);
		return withType(type.value);
	}

	/** Returns whether it is submitByEnter,
	 * If submitByEnter is true, press enter will fire onOK event instead of move to next line,
	 * you should press shift + enter if you want to move to next line.
	 * <p>Default: false.
	 */
	default boolean isSubmitByEnter() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code submitByEnter}.
	 *
	 * <p>Sets whether it is submitByEnter.
	 * If submitByEnter is true, press enter will fire onOK event instead of move to next line,
	 * you should press shift + enter if you want to move to next line.
	 *
	 * @param submitByEnter {@code true} to enable whether it is submitByEnter
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSubmitByEnter(boolean submitByEnter);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkType() {
		final String type = getType();
		if (!ITextboxCtrl.TEXT.equals(type) && !"password".equals(type) && !"tel".equals(type) && !"email".equals(type)
				&& !"url".equals(type))
			throw new WrongValueException("Illegal type: " + type);
	}

	/**
	 * Internal use
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IInputElement.super.renderProperties(renderer);
		render(renderer, "multiline", isMultiline());
		final int rows = getRows();
		if (rows > 1)
			renderer.render("rows", rows);
		render(renderer, "tabbable", isTabbable());
		final String type = getType();
		if (!ITextboxCtrl.TEXT.equals(type))
			renderer.render("type", type);
		render(renderer, "submitByEnter", isSubmitByEnter());
	}

	/**
	 * Specifies the values to the textbox's type.
	 */
	enum Type {
		/**
		 * Used for textbox that should contain a text.
		 */
		TEXT("text"),
		/**
		 * Used for textbox that should contain a password.
		 */
		PASSWORD("password"),
		/**
		 * Used for textbox that should contain a telephone number.
		 */
		TELEPHONE("tel"),
		/**
		 * Used for textbox that should contain an email.
		 */
		EMAIL("email"),
		/**
		 * Used for textbox that should contain an url.
		 */
		URL("url");

		final private String value;
		Type(String value) {
			this.value = value;
		}
	}
}
