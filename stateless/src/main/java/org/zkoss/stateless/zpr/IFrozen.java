/* IFrozen.java

	Purpose:

	Description:

	History:
		5:42 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Frozen;

/**
 * Immutable {@link org.zkoss.zul.Frozen} component
 * @author jumperchen
 * @see Frozen
 */
@StatelessStyle
public interface IFrozen<I extends IAnyGroup> extends IXulElement<IFrozen<I>>,
		IChildable<IFrozen<I>, I>, IMeshComposite<IFrozen<I>>, IAnyGroup<IFrozen<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IFrozen<IAnyGroup> DEFAULT = new IFrozen.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Frozen> getZKType() {
		return Frozen.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.mesh.Frozen"}</p>
	 */
	default String getWidgetClass() {
		return "zul.mesh.Frozen";
	}

	/**
	 * Returns the number of columns to freeze.
	 * <p>Default: {@code 0}
	 */
	default int getColumns() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code columns}.
	 *
	 * <p> Sets the number of columns to freeze.
	 *
	 * @param columns The number of columns to freeze.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IFrozen<I> withColumns(int columns);

	/**
	 * Returns the start position of the scrollbar.
	 * <p>Default: {@code 0}
	 */
	default int getStart() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code start}.
	 *
	 * <p> Sets the start position of the scrollbar.
	 *
	 * @param start The start position of the scrollbar.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IFrozen<I> withStart(int start);

	/**
	 * Returns the number of right columns to freeze.
	 * Note: only useful when using smooth Frozen and browsers that supports CSS position: sticky.
	 * <p>Default: {@code 0}
	 */
	default int getRightColumns() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code rightColumns}.
	 *
	 * <p> Sets the number of right columns to freeze.
	 *
	 * @param rightColumns The number of right columns to freeze.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IFrozen<I> withRightColumns(int rightColumns);

	/**
	 * Returns the frozen is smooth or not.
	 * <p>Default: {@code true}
	 */
	default boolean isSmooth() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code smooth}.
	 *
	 * <p> Sets the frozen is smooth or not.
	 *
	 * @param smooth The number of right columns to freeze.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IFrozen<I> withSmooth(boolean smooth);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkColumns() {
		if (getColumns() < 0) {
			throw new WrongValueException("Positive only");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkStart() {
		if (getStart() < 0) {
			throw new WrongValueException("Positive only");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkRightColumns() {
		if (getRightColumns() < 0) {
			throw new WrongValueException("Positive only");
		}
	}

	/**
	 * Return the instance with the given columns
	 * @param columns The number of columns to freeze.(from left to right)
	 */
	static <I extends IAnyGroup> IFrozen<I> ofColumns(int columns) {
		return new IFrozen.Builder<I>().setColumns(columns).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component.
	 */
	static <I extends IAnyGroup> IFrozen<I> ofId(String id) {
		return new IFrozen.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		int _columns = getColumns();
		if (_columns > 0)
			renderer.render("columns", _columns);

		int _start = getStart();
		if (_columns > 0 && _start > 0)
			renderer.render("start", _start);

		//F85-ZK-3525: frozen support smooth mode (ee only)
		if (!isSmooth()) {
			renderer.render("smooth", false);
		} else {
//			Todo:
//			renderer.render("currentLeft", _currentLeft);
		}
		int _rightColumns = getRightColumns();
		if (_rightColumns > 0)
			renderer.render("rightColumns", _rightColumns);
	}

	/**
	 * Builds instances of type {@link IFrozen IFrozen}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIFrozen.Builder<I> {}

	/**
	 * Builds an updater of type {@link IFrozen} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IFrozenUpdater {}
}
