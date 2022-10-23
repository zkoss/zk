/* IRow.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:19:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Row;

/**
 * Immutable {@link Row} component
 *
 * <p> A single row in a {@link IRows} element.
 * Each child of the {@link IRow} component is placed in each successive cell
 * of the grid. The row with the most child components determines the number
 * of columns in each row.
 * </p>
 *
 * @author katherine
 * @see Row
 */
@ZephyrStyle
public interface IRow<I extends IAnyGroup>
		extends IRowBase<IRow<I>>, IChildable<IRow<I>, I>,
		IComposite<IComponent> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IRow<IAnyGroup> DEFAULT = new IRow.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Row> getZKType() {
		return Row.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Row"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Row";
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
	default IRow withWidth(@Nullable String width) {
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
	default IRow withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the child detail component.
	 */
	@Nullable
	IDetailChild<? extends IDetailChild> getDetailChild();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkDetailChild() {
		if (getChildren().stream().filter(i -> i instanceof IDetailChild).findAny().isPresent()) {
			throw new UiException("Only one detail is allowed: " + this);
		}
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code detailChild}
	 * class name.
	 *
	 * <p>Sets the detail child (EE only)
	 *
	 * @param detailChild The detail child
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IRow<I> withDetailChild(@Nullable IDetailChild<? extends IDetailChild> detailChild);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IComponent> getAllComponents() {
		IDetailChild<? extends IDetailChild> detailChild = getDetailChild();
		if (detailChild != null) {
			List<I> children = getChildren();
			if (children.isEmpty()) {
				return Arrays.asList(detailChild);
			} else {
				List<IComponent> r = new ArrayList<>();
				r.add(detailChild); // first one
				r.addAll(children);
				return r;
			}
		}
		return  (List<IComponent>) (List<?>) getChildren();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IRow<I> of(Iterable<? extends I> children) {
		return new IRow.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IRow<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IRow<I> ofId(String id) {
		return new IRow.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IRow IRow}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIRow.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link IRow} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IRowUpdater {}
}