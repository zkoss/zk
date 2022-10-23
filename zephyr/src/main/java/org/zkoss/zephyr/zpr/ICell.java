/* ICell.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:20:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zephyr.ui.util.IComponentChecker;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Cell;

/**
 * Immutable {@link Cell} component
 *
 * <p>The generic cell component to be embedded into {@link IRow}</p>
 * @author katherine
 * @see Cell
 */
@ZephyrStyle
public interface ICell<I extends IAnyGroup> extends IXulElement<ICell<I>>, IChildable<ICell<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICell<IAnyGroup> DEFAULT = new ICell.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Cell> getZKType() {
		return Cell.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Cell"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Cell";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkValue() {
		if (getAlign() != null) {
			IComponentChecker.checkValue(getAlign(),
					"not allowed values of align", "right", "left", "center",
					"justify", "char");
		}
		if (getValign() != null) {
			IComponentChecker.checkValue(getValign(),
					"not allowed values of valign", "top", "middle", "bottom",
					"baseline");
		}
	}

	/** Returns the horizontal alignment.
	 * <p>Default: {@code null} (system default: left unless CSS specified).
	 */
	@Nullable
	String getAlign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code align}.
	 *
	 * <p> Sets the horizontal alignment.
	 * Allowed values: {@code "left"}, {@code "right}, {@code "center"}, {@code "justify"},
	 * and {@code "char"}.
	 *
	 * @param align the horizontal alignment.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICell<I> withAlign(@Nullable String align);

	/** Returns the vertical alignment.
	 * <p>Default: {@code null} (system default: top).
	 */
	@Nullable
	String getValign();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code valign}.
	 *
	 * <p> Sets the vertical alignment.
	 * Allowed values: {@code "top"}, {@code "middle}, {@code "bottom"}, and {@code "baseline"}.
	 *
	 * @param valign the vertical alignment.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICell<I> withValign(@Nullable String valign);

	/** Returns number of columns to span.
	 * Default: {@code 1}.
	 */
	default int getColspan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code colspan}.
	 *
	 * <p> Sets the number of columns to span.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 *
	 * @param colspan The number of columns to span.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICell<I> withColspan(int colspan);

	/** Returns number of rows to span.
	 * Default: {@code 1}.
	 */
	default int getRowspan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code rowspan}.
	 *
	 * <p> Sets the number of rows to span.
	 * <p>It is the same as the rows attribute of HTML TD tag.
	 *
	 * @param rowspan The number of rows to span.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICell<I> withRowspan(int rowspan);

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ICell<I> of(Iterable<? extends I> children) {
		return new ICell.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ICell<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ICell<I> ofId(String id) {
		return new ICell.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		if (getColspan() != 1)
			renderer.render("colspan", getColspan());
		if (getRowspan() != 1)
			renderer.render("rowspan", getRowspan());

		renderer.render("align", getAlign());
		renderer.render("valign", getValign());
	}

	/**
	 * Builds instances of type {@link ICell ICell}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableICell.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link ICell} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ICellUpdater {}
}