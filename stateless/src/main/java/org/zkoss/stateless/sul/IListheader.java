/* IListheader.java

	Purpose:

	Description:

	History:
		Tue Jan 04 10:40:40 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.action.data.SortData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Listheader;

/**
 * Immutable {@link Listheader} component
 *
 * <p>
 * The list header which defines the attributes and header of a column
 * of a list box.
 * </p>
 *
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
 *          <td><strong>ActionData</strong>: {@link SortData}
 *          <br>Denotes user has sorted the row of this column.</td>
 *       </tr>
 *       <tr>
 *          <td>onGroup</td>
 *          <td><strong>ActionData</strong>: {@link SortData}
 *          <br>Denotes user has grouped all the cells under a column. [ZK PE] </td>
 *       </tr>
 *       <tr>
 *          <td>onUngroup</td>
 *          <td><strong>ActionData</strong>: {@link SortData}
 *          <br>Denotes user has ungrouped all the cells under a column. [ZK EE] </td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Listheader
 */
@StatelessStyle
public interface IListheader<I extends IAnyGroup>
		extends IHeaderElement<IListheader<I>>, IChildable<IListheader<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListheader<IAnyGroup> DEFAULT = new IListheader.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listheader> getZKType() {
		return Listheader.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listheader"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.sel.Listheader";
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
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IListheader<I> checkSortAscendingAndDescending() {
		Comparator<?> sortAscending = getSortAscending();
		Comparator<?> sortDescending = getSortDescending();

		IListheader.Builder builder = new IListheader.Builder();
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
	 * as an indicator as to how the listbox is sorted. (unless the listbox has
	 * {@link IListbox#isAutosort()} attribute for model case)
	 *
	 * @param sortDirection One of {@code "ascending",} {@code "descending"}
	 * and {@code "natural"}
	 * <p>Default: {@code "natural"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListheader<I> withSortDirection(String sortDirection);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sortDirection}.
	 *
	 * <p> Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the listbox is sorted. (unless the listbox has
	 * {@link IListbox#isAutosort()} attribute for model case)
	 *
	 * @param sortDirection One of {@link SortDirection}
	 * <p>Default: {@link SortDirection#NATURAL}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IListheader<I> withSortDirection(SortDirection sortDirection) {
		Objects.requireNonNull(sortDirection);
		return withSortDirection(sortDirection.value);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default String getSortAscendingString() {
		return "none";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	IListheader<I> withSortAscendingString(String sortAscendingString);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default String getSortDescendingString() {
		return "none";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	IListheader<I> withSortDescendingString(String sortDescendingString);

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
	IListheader<I> withSortAscending(@Nullable Comparator<?> sortAscending);

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
	IListheader<I> withSortDescending(@Nullable Comparator<?> sortDescending);

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
	default IListheader<I> withSortAscending(String sortAscending)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (!Strings.isBlank(sortAscending) && sortAscending.startsWith("client")
				&& !sortAscending.equals(getSortAscendingString())) {
			return new IListheader.Builder().from(this)
					.setSortAscendingString(sortAscending).setSortAscending(null)
					.build();
		} else
			return new IListheader.Builder().from(this)
					.setSortAscendingString("fromServer")
					.setSortAscending(IListheaderCtrl.toComparator(sortAscending))
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
	default IListheader<I> withSortDescending(String sortDescending)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (!Strings.isBlank(sortDescending) && sortDescending.startsWith("client")
				&& !sortDescending.equals(getSortDescendingString())) {
			return new IListheader.Builder().from(this)
					.setSortDescendingString(sortDescending).setSortDescending(null)
					.build();
		} else
			return new IListheader.Builder().from(this)
					.setSortDescendingString("fromServer")
					.setSortDescending(IListheaderCtrl.toComparator(sortDescending))
					.build();
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label belongs to this column.
	 */
	static <I extends IAnyGroup> IListheader<I> of(String label) {
		return new IListheader.Builder<I>().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the column holds.
	 * @param image The image that the column holds.
	 */
	static <I extends IAnyGroup> IListheader<I> of(String label, String image) {
		return new IListheader.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the column holds.
	 */
	static <I extends IAnyGroup> IListheader<I> ofImage(String image) {
		return new IListheader.Builder().setImage(image).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListheader<I> of(Iterable<? extends I> children) {
		return new IListheader.Builder<I>().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListheader<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IListheader<I> ofId(String id) {
		return new IListheader.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHeaderElement.super.renderProperties(renderer);

		String _sortDscNm = getSortDescendingString();
		if (!"none".equals(_sortDscNm))
			render(renderer, "sortDescending", _sortDscNm);

		String _sortAscNm = getSortAscendingString();
		if (!"none".equals(_sortAscNm))
			render(renderer, "sortAscending", _sortAscNm);

		String _sortDir = getSortDirection();
		if (!"natural".equals(_sortDir))
			render(renderer, "sortDirection", _sortDir);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	/**
	 * Builds instances of type {@link IListheader IListheader}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIListheader.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link IListheader} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IListheaderUpdater {}

	/**
	 * Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the listbox is sorted. (unless the listbox has
	 * {@link IListbox#isAutosort()} attribute for model case)
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