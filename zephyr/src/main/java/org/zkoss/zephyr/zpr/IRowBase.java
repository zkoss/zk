/* IRowBase.java

	Purpose:

	Description:

	History:
		Tue Dec 28 10:32:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.zkoss.zephyr.ui.util.IComponentChecker;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Row;

/**
 * Immutable {@link Row} component
 *
 * @author katherine
 * @see Row
 */
public interface IRowBase<I extends IRowBase> extends IXulElement<I> {

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void check() {
		IComponentChecker.checkValue(getAlign(), "not allowed values of align",
				null, "right", "left", "center", "justify", "char");
		IComponentChecker.checkValue(getValign(), "not allowed values of valign",
				null, "top", "middle", "bottom", "baseline");
	}

	/** Returns the horizontal alignment of the whole row.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	@Nullable
	String getAlign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code align}.
	 *
	 * <p>Sets the horizontal alignment of this whole row.
	 * @param align The horizontal alignment of this row.
	 * @return A modified copy of the {@code this} object
	 */
	I withAlign(@Nullable String align);

	/** Returns the vertical alignment of the whole row.
	 * <p>Default: null (system default: top).
	 */
	@Nullable
	String getValign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code align}.
	 *
	 * <p>Sets the vertical alignment of the whole row.
	 * @param valign The vertical alignment of this row.
	 * @return A modified copy of the {@code this} object
	 */
	I withValign(@Nullable String valign);

	/** Returns the nowrap.
	 * <p>Default: {@code false} (system default: wrap).
	 */
	default boolean isNowrap() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code align}.
	 *
	 * <p>Sets the nowrap.
	 * @param nowrap The nowrap of the text.
	 * @return A modified copy of the {@code this} object
	 */
	I withNowrap(boolean nowrap);

	/**
	 * Return true whether all children of this row, if any, is loaded at client
	 * <p>Default: {@code false}</p>
	 */
	default boolean isLoaded() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code loaded}.
	 *
	 * <p> Sets whether the component is loaded. (Internal use)
	 *
	 * @param loaded {@code true} to indicate this component is loaded at client.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 * @hidden for Javadoc
	 */
	I withLoaded(boolean loaded);

	/**
	 * Return the index of this item
	 * <p>Default: {@code -1}, meaning not set</p>
	 */
	default int getIndex() {
		return -1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code index}.
	 *
	 * <p>Sets the index of this component.
	 *
	 * @param index The index of the child from its parent component.
	 * <p>Default: {@code -1}, meaning not set.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withIndex(int index);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "align", getAlign());
		render(renderer, "valign", getValign());
		render(renderer, "nowrap", isNowrap());
		render(renderer, "_loaded", isLoaded());
		int _index = getIndex();
		if (_index >= 0)
			renderer.render("_index", _index);
	}
}