/* ITreecol.java

	Purpose:

	Description:

	History:
		4:51 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.zephyr.immutable.ZephyrOnly;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Treecol;

/**
 * Immutable {@link Treecol} component
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
 *          <td>onSort</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.SortData}
 *          <br>Denotes user has sorted the treeitem of this treecol.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author jumperchen
 * @see Treecol
 */
@ZephyrStyle
public interface ITreecol<I extends IAnyGroup>
		extends IHeaderElement<ITreecol<I>>, IChildable<ITreecol<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreecol<IAnyGroup> DEFAULT = new ITreecol.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treecol> getZKType() {
		return Treecol.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treecol"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treecol";
	}

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	default String getSortDirection() {
		return "natural";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortDirection}.
	 *
	 * <p> Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the tree is sorted. (unless the tree has
	 * {@link ITree#isAutosort()} attribute for model case)
	 *
	 * @param sortDirection One of {@code "ascending",} {@code "descending"}
	 * and {@code "natural"}
	 * <p>Default: {@code "natural"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreecol<I> withSortDirection(String sortDirection);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortDirection}.
	 *
	 * <p> Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the tree is sorted. (unless the tree has
	 * {@link ITree#isAutosort()} attribute for model case)
	 *
	 * @param sortDirection One of {@link SortDirection}
	 * <p>Default: {@link SortDirection#NATURAL}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITreecol<I> withSortDirection(SortDirection sortDirection) {
		Objects.requireNonNull(sortDirection);
		return withSortDirection(sortDirection.value);
	}

	/** Returns the maximal length of each item's label.
	 * <p>Default: {@code 0} (no limit).
	 */
	default int getMaxlength() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code maxlength}.
	 *
	 * <p> Sets the maximal length of each item's label.
	 * <p>Default: 0 (no limit).
	 * <p>Notice that maxlength will be applied to this header and all
	 * listcell of the same column.
	 *
	 * @param maxlength One of {@link SortDirection}
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreecol<I> withMaxlength(int maxlength);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@ZephyrOnly
	default String getSortAscendingString() {
		return "none";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	ITreecol<I> withSortAscendingString(String sortAscendingString);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@ZephyrOnly
	default String getSortDescendingString() {
		return "none";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	ITreecol<I> withSortDescendingString(String sortDescendingString);

	/**
	 * Returns the ascending sorter, or null if not available.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	Comparator<?> getSortAscending();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortAscending}.
	 *
	 * <p> Sets the ascending sorter, or null for no sorter for the ascending order.
	 *
	 * @param sortAscending The comparator used to sort the ascending order.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreecol<I> withSortAscending(@Nullable Comparator<?> sortAscending);

	/**
	 * Returns the descending sorter, or null if not available.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	Comparator<?> getSortDescending();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortDescending}.
	 *
	 * <p> Sets the descending sorter, or null for no sorter for the descending order.
	 *
	 * @param sortDescending The comparator used to sort the descending order.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreecol<I> withSortDescending(@Nullable Comparator<?> sortDescending);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortAscending}
	 * class name.
	 *
	 * <p>Sets the ascending sorter with the class name, or null for
	 * no sorter for the ascending order.
	 *
	 * @param sortAscending The comparator used to sort the ascending order.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITreecol<I> withSortAscending(String sortAscending)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (!Strings.isBlank(sortAscending) && sortAscending.startsWith("client")
				&& !sortAscending.equals(getSortAscendingString())) {
			return new ITreecol.Builder().from(this)
					.setSortAscendingString(sortAscending).setSortAscending(null)
					.build();
		} else
			return new ITreecol.Builder().from(this)
					.setSortAscendingString("fromServer")
					.setSortAscending(ITreecolCtrl.toComparator(sortAscending))
					.build();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortDescending}
	 * class name.
	 *
	 * <p>Sets the descending sorter with the class name, or null for
	 * no sorter for the descending order.
	 *
	 * @param sortDescending The comparator used to sort the descending order.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITreecol<I> withSortDescending(String sortDescending)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (!Strings.isBlank(sortDescending) && sortDescending.startsWith("client")
				&& !sortDescending.equals(getSortDescendingString())) {
			return new ITreecol.Builder().from(this)
					.setSortDescendingString(sortDescending).setSortDescending(null)
					.build();
		} else
			return new ITreecol.Builder().from(this)
					.setSortDescendingString("fromServer")
					.setSortDescending(ITreecolCtrl.toComparator(sortDescending))
					.build();
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreecol<I> checkSortAscendingAndDescending() {
		Comparator<?> sortAscending = getSortAscending();
		Comparator<?> sortDescending = getSortDescending();

		// Comparator is higher priority in Zephyr.
		Builder builder = new Builder();
		boolean hasChanged = false;
		if (sortAscending != null && !"fromServer".equals(getSortAscendingString())) {
			hasChanged = true;
			builder = builder.from(this).setSortAscendingString("fromServer");
		}
		if (sortDescending != null && !"fromServer".equals(getSortDescendingString())) {
			if (!hasChanged) {
				builder = builder.from(this);
			}
			hasChanged = true;
			builder.setSortDescendingString("fromServer");
		}
		if (hasChanged) {
			return builder.build();
		} else {
			return this;
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkSortDirection() throws WrongValueException {
		String sortDir = getSortDirection();
		if (sortDir == null
				|| (!"ascending".equals(sortDir) && !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: " + sortDir);
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label belongs to this column.
	 */
	static <I extends IAnyGroup> ITreecol<I> of(String label) {
		return new ITreecol.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the column holds.
	 * @param image The image that the column holds.
	 */
	static <I extends IAnyGroup> ITreecol<I> of(String label, String image) {
		return new Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the column holds.
	 */
	static <I extends IAnyGroup> ITreecol<I> ofImage(String image) {
		return new Builder().setImage(image).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreecol<I> of(Iterable<? extends I> children) {
		return new Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreecol<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ITreecol<I> ofId(String id) {
		return new ITreecol.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IHeaderElement.super.renderProperties(renderer);

		if (!"none".equals(getSortDescendingString()))
			render(renderer, "sortDescending", getSortDescendingString());

		if (!"none".equals(getSortAscendingString()))
			render(renderer, "sortAscending", getSortAscendingString());

		String _sortDir = getSortDirection();
		if (!"natural".equals(_sortDir))
			render(renderer, "sortDirection", _sortDir);

		int _maxlength = getMaxlength();
		if (_maxlength > 0)
			renderer.render("maxlength", _maxlength);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	/**
	 * Builds instances of type {@link ITreecol ITreecol}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableITreecol.Builder<I> {}

	/**
	 * Builds an updater of type {@link ITreecol} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITreecolUpdater {}

	/**
	 * Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the tree is sorted. (unless the tree has
	 * {@link ITree#isAutosort()} attribute for model case)
	 */
	enum SortDirection {
		/**
		 * Indicates the ascending sorting.
		 */
		ASCENDING("ascending"),
		/**
		 * Indicates the descending sorting.
		 */
		DESCENDING("descending"),
		/**
		 * Indicates the natural sorting. (Default value)
		 */
		NATURAL("natural");

		private final String value;

		SortDirection(String value) {
			this.value = value;
		}
	}
}