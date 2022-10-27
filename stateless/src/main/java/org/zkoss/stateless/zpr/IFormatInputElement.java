/* IFormatInputElement.java

	Purpose:

	Description:

	History:
		Tue Oct 26 12:56:58 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Immutable {@link org.zkoss.zul.impl.FormatInputElement} interface.
 *
 * <p>A skeletal implementation for an input box with format.</p>
 * @author katherine
 */
public interface IFormatInputElement<I extends IFormatInputElement, ValueType> extends IInputElement<I, ValueType> {

	/**
	 * Internal use.
	 * @hidden for Javadoc
	 */
	@Nullable
	@Value.Lazy
	default String getRealFormat() {
		return getFormat();
	}

	/** Returns the format of the input component.
	 * <p>Default: {@code null} (used what is defined in the inherited implementation).
	 */
	@Nullable
	String getFormat();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code format}.
	 *
	 * <p>Sets the format to the input component.
	 *
	 * @param format The format of the input component (used what is defined
	 * in the inherited implementation).
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withFormat(@Nullable String format);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		render(renderer, "format", getRealFormat()); //value might depend on format (though it shall not)
		IInputElement.super.renderProperties(renderer);
	}
}