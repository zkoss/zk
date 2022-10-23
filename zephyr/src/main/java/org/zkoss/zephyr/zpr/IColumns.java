/* IColumns.java

	Purpose:

	Description:

	History:
		Tue Dec 28 14:34:50 CST 2021, Created by katherine

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
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Columns;

/**
 * Immutable {@link Columns} component
 *
 * <p> Defines the columns of a grid.
 * Each child of a columns element should be a {@link IColumn} element.
 * </p>
 *
 * @author katherine
 * @see Columns
 */
@ZephyrStyle
public interface IColumns extends IHeadersElement<IColumns>,
		IChildable<IColumns, IColumn<IAnyGroup>>, IGridComposite<IColumns> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IColumns DEFAULT = new IColumns.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Columns> getZKType() {
		return Columns.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Columns"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Columns";
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getWidth() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IColumns withWidth(@Nullable String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getHflex() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IColumns withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns whether to enable hiding of columns with the header context menu.
	 * <p>Default: {@code true}.
	 */
	default boolean isColumnshide() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code columnshide}.
	 *
	 * <p>Sets whether to enable hiding of columns with the header context menu.
	 *
	 * @param columnshide Whether to enable hiding of columns with the header context menu.
	 *
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IColumns withColumnshide(boolean columnshide);

	/**
	 * Returns whether to enable grouping of columns with the header context menu.
	 * <p>Default: {@code true}.
	 */
	default boolean isColumnsgroup() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code columnsgroup}.
	 *
	 * <p>Sets whether to enable grouping of columns with the header context menu.
	 *
	 * @param columnsgroup Whether to enable grouping of columns with the header context menu.
	 *
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IColumns withColumnsgroup(boolean columnsgroup);

	/** Returns the ID of the Menupopup ({@link IMenupopup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: {@code "none"} (a default menupopup).
	 */
	default String getMenupopup() {
		return "none";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code menupopup}.
	 *
	 * <p>Sets the ID of the menupopup ({@link IMenupopup}) that should appear
	 * when the user clicks on the element of each column.
	 *
	 * @param menupopup The ID of the menupopup ({@link IMenupopup}) that should appear
	 * when the user clicks on the element of each column.
	 *
	 * <p>Default: {@code "none"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IColumns withMenupopup(String menupopup);

	/**
	 * Returns the instance with the given {@link IColumn} children.
	 * @param children The children of {@link IColumn}
	 */
	static IColumns of(Iterable<? extends IColumn<IAnyGroup>> children) {
		return new IColumns.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IColumn} children.
	 * @param children The children of {@link IColumn}
	 */
	static IColumns of(IColumn<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static  IColumns ofId(String id) {
		return new IColumns.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHeadersElement.super.renderProperties(renderer);

		boolean _columnsgroup = isColumnsgroup();
		if (!_columnsgroup)
			renderer.render("columnsgroup", false);
		boolean _columnshide = isColumnshide();
		if (!_columnshide)
			renderer.render("columnshide", false);
		String _mpop = getMenupopup();
		if (!"none".equals(_mpop))
			renderer.render("menupopup", _mpop);
	}

	/**
	 * Builds instances of type {@link IColumns IColumns}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIColumns.Builder {
	}

	/**
	 * Builds an updater of type {@link IColumns} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IColumnsUpdater {}
}