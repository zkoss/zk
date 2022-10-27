/* IListhead.java

	Purpose:

	Description:

	History:
		Tue Jan 04 10:38:14 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Listhead;

/**
 * Immutable {@link Listhead} component
 * <p> A list headers used to define multi-columns and/or headers.</p>
 *
 * @author katherine
 * @see Listhead
 */
@StatelessStyle
public interface IListhead extends IHeadersElement<IListhead>, IChildable<IListhead, IListheader>, IListboxComposite<IListhead> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListhead DEFAULT = new IListhead.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listhead> getZKType() {
		return Listhead.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listhead"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.sel.Listhead";
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
	 * components, please refer to {@link IListbox} and {@link IListhead} instead.
	 */
	default IListhead withWidth(@Nullable String width) {
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
	 * components, please refer to {@link IListbox} and {@link IListhead} instead.
	 */
	default IListhead withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getHeight() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IListbox} and {@link IListhead} instead.
	 */
	default IListhead withHeight(@Nullable String height) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getVflex() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IListbox} and {@link IListhead} instead.
	 */
	default IListhead withVflex(@Nullable String vflex) {
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
	IListhead withColumnshide(boolean columnshide);

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
	IListhead withColumnsgroup(boolean columnsgroup);

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
	IListhead withMenupopup(String menupopup);

	/**
	 * Returns the instance with the given {@link IColumn} children.
	 * @param children The children of {@link IColumn}
	 */
	static IListhead of(Iterable<? extends IListheader<IAnyGroup>> children) {
		return new IListhead.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IColumn} children.
	 * @param children The children of {@link IColumn}
	 */
	static IListhead of(IListheader<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IListhead ofId(String id) {
		return new IListhead.Builder().setId(id).build();
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
	 * Builds instances of type {@link IListhead IListhead}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIListhead.Builder {
	}

	/**
	 * Builds an updater of type {@link IListhead} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IListheadUpdater {}
}