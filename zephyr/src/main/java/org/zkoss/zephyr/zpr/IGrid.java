/* IGrid.java

	Purpose:

	Description:

	History:
		Tue Dec 28 15:51:18 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.zephyr.immutable.ZephyrOnly;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;

/**
 * Immutable {@link Grid} component
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
 *          <td>onPageSize</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.PageSizeData}
 *          <br>Notifies the paging size has been changed when the autopaging
 *          ({@code iGrid.withAutopaging(boolean)}) is enabled and a user changed
 *          the size of the content.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Molds</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "default"}</td>
 *          <td><img src="doc-files/IGrid_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "paging"}</td>
 *          <td><img src="doc-files/IGrid_mold_paging.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Application Library Properties</h3>
 *
 * <ul>
 * <li>
 * <p>To turn on the auto-sort facility to sort the model for this component, you have to specify
 * {@link #withAutosort(Autosort)} to {@link Autosort#ENABLE} or {@link Autosort#IGNORE_CHANGE}.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.grid.autoSort&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 *</ul>
 * <b>Note:</b> with zk.xml setting, it will affect to all tree components.
 *
 * @author katherine
 * @see Grid
 */
@ZephyrStyle
public interface IGrid extends IMeshElement<IGrid>, IComposite<IMeshComposite>, IAnyGroup<IGrid> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IGrid DEFAULT = new IGrid.Builder().build();

	/**
	 * Constant for {@code "paging"} mold attributes of this immutable component.
	 */
	IGrid PAGING = new IGrid.Builder().setMold("paging").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Grid> getZKType() {
		return Grid.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Grid"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Grid";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkVisibleRows() {
		int visibleRows = getVisibleRows();
		if (visibleRows < 0) {
			throw new WrongValueException("Illegal rows: " + visibleRows);
		} else if (visibleRows > 0) {
			if (getVflex() != null)
				throw new UiException("Not allowed to set vflex and visibleRows at the same time");
			if (getHeight() != null)
				throw new UiException("Not allowed to set height and visibleRows at the same time");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IGrid checkMold() {
		IPaging pgi = getPagingChild();
		if ("paging".equals(getMold())) {
			if (pgi == null) {
				return withPagingChild(IPaging.DEFAULT.withDetailed(true));
			}
			return this;
		} else if ("default".equals(getMold())) {
			if (pgi != null) {
				return withMold("paging");
			}
			return this;
		}
		return this;
	}

	/** Returns the rows.
	 */
	@Nullable
	IRows getRows();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rows}.
	 *
	 * <p>Sets the rows as a child to this component
	 *
	 * @param rows The rows child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withRows(@Nullable IRows rows);

	/** Returns the columns.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IColumns getColumns();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code columns}.
	 *
	 * <p>Sets the columns as a child to this component
	 *
	 * @param columns The columns child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withColumns(@Nullable IColumns columns);

	/** Returns the foot.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IFoot getFoot();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code foot}.
	 *
	 * <p>Sets the foot as a child to this component
	 *
	 * @param foot The foot child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withFoot(@Nullable IFoot foot);

	/**
	 * Returns the frozen child.
	 * <p>Default: {@code null}.</p>
	 */
	@Nullable
	IFrozen getFrozen();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code frozen}.
	 *
	 * <p>Sets the frozen as a child to this component
	 *
	 * @param frozen The foot child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withFrozen(@Nullable IFrozen frozen);

	/**
	 * Returns the message to display when there are no items
	 */
	@Nullable
	String getEmptyMessage();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code emptyMessage}.
	 *
	 * <p>Sets the message to display when there are no items
	 *
	 * @param emptyMessage The message to display when there are no items
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withEmptyMessage(@Nullable String emptyMessage);

	/** Returns the visible rows. Zero means no limitation.
	 * <p>Default: {@code 0}.
	 */
	default int getVisibleRows() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code visibleRows}.
	 *
	 * <p>Sets the visible rows. Zero means no limitation.
	 *
	 * @param visibleRows The visible rows. Zero means no limitation.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withVisibleRows(int visibleRows);

	/**
	 * Returns the inner width of this component.
	 * The inner width is the width of the inner table.
	 * <p>Default: "100%"
	 */
	default String getInnerWidth() {
		return "100%";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code innerWidth}.
	 *
	 * <p> Sets the inner width of this component.
	 * The inner width is the width of the inner table.
	 * By default, it is 100%. That is, it is the same as the width
	 * of this component. However, it is changed when the user
	 * is sizing the column's width.
	 *
	 * <p>Application developers rarely call this method, unless
	 * they want to preserve the widths of sizable columns
	 * changed by the user.
	 * To preserve the widths, the developer have to store the widths of
	 * all columns and the inner width ({@link #getInnerWidth}),
	 * and then restore them when re-creating this component.
	 *
	 * @param innerWidth The inner width of this component.
	 * <p>Default: {@code "100%"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withInnerWidth(String innerWidth);

	/**
	 * Returns whether to sort all items when model or sort direction be changed.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.grid.autoSort"}
	 * library property is not set in zk.xml.</p>
	 * <p><b>Note:</b> it's meaningless if {@link ListModel} is not set.</p>
	 */
	@Nullable
	default String getAutosort() {
		return Library.getProperty("org.zkoss.zul.grid.autoSort");
	}
	/**
	 * Returns whether to sort all items when the model or the sort direction of
	 * the {@link IColumn} be changed. (Internal only)
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default boolean isAutosort() {
		String autosort = getAutosort();
		return autosort != null ?
				"true".equals(autosort) || "ignore.change".equals(autosort) :
				false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autosort}.
	 *
	 * <p>Sets to enable the auto-sort facility to sort the model for this component.
	 * Meaningless if {@link ListModel} is not set.
	 *
	 * @param autosort The allowed values are {@code null}, {@code "false"},
	 * {@code "true"}, and {@code "ignore.change"}.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withAutosort(@Nullable String autosort);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autosort}.
	 *
	 * <p>Sets to enable the auto-sort facility to sort the model for this component.
	 * Meaningless if {@link ListModel} is not set.
	 *
	 * @param autosort The allowed values are {@code null}, {@code "false"},
	 * {@code "true"}, and {@code "ignore.change"}.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IGrid withAutosort(@Nullable Autosort autosort) {
		if (autosort == null) {
			return withAutosort((String) null);
		} else {
			return withAutosort(autosort.value);
		}
	}

	/** Returns the style class for the odd rows.
	 * <p>Default: {@link #getZclass()}-odd.
	 */
	default String getOddRowSclass() {
		return getZclass() + "-odd";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code oddRowSclass}.
	 *
	 * <p>Sets the style class for the odd rows.
	 * If the style class doesn't exist, the striping effect disappears.
	 * You can provide different effects by providing the proper style
	 * classes.
	 *
	 * @param oddRowSclass The style class for the odd rows.
	 * <p>Default: {@link #getZclass()}-odd .</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withOddRowSclass(String oddRowSclass);

	/**
	 * Internal use for Model case
	 * @hidden for Javadoc
	 */
	@ZephyrOnly
	default Map<String, Object> getAuxInfo() {
		return Collections.emptyMap();
	}

	/**
	 * Returns the auxhead child.
	 */
	@ZephyrOnly
	default List<IAuxhead> getAuxhead() {
		return new ArrayList<>();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code auxhead}.
	 *
	 * <p>Sets the list of auxhead as children to this component
	 *
	 * @param auxhead The auxhead children.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGrid withAuxhead(Iterable<? extends IAuxhead> auxhead);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code auxhead}.
	 *
	 * <p>Sets the auxhead as a child to this component
	 *
	 * @param auxhead The auxhead child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IGrid withAuxhead(IAuxhead... auxhead) {
		return withAuxhead(Arrays.asList(auxhead));
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IMeshComposite> getAllComponents() {
		ArrayList<IMeshComposite> components = new ArrayList<>();
		List<IAuxhead> auxhead = getAuxhead();
		if (auxhead != null && !auxhead.isEmpty()) {
			components.addAll(auxhead);
		}
		IColumns columns = getColumns();
		if (columns != null) {
			components.add(columns);
		}
		IRows rows = getRows();
		if (rows != null) {
			components.add(rows);
		}
		IFrozen frozen = getFrozen();
		if (frozen != null) {
			components.add(frozen);
		}
		IFoot foot = getFoot();
		if (foot != null) {
			components.add(foot);
		}
		IPaging pagingChild = getPagingChild();
		if (pagingChild != null) {
			components.add(pagingChild);
		}
		return components;
	}

	/**
	 * Returns the instance with the given {@link IRowBase} children.
	 * @param children The children of {@link IRowBase}
	 */
	static IGrid of(Iterable<? extends IRowBase> children) {
		return new IGrid.Builder().setRows(IRows.of(children)).build();
	}

	/**
	 * Returns the instance with the given {@link IRowBase} children.
	 * @param children The children of {@link IRowBase}
	 */
	static IGrid of(IRowBase... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link IColumns} child.
	 * @param columns The {@link IColumns} child
	 */
	static IGrid ofColumns(IColumns columns) {
		return new IGrid.Builder().setColumns(columns).build();
	}

	/**
	 * Returns the instance with the given {@link IColumn} column children.
	 * <p>a shortcut of {@link IColumns#withChildren}</p>
	 * @param children The {@link IColumn} children
	 */
	static IGrid ofColumns(Iterable<? extends IColumn<IAnyGroup>> children) {
		return new IGrid.Builder().setColumns(IColumns.of(children)).build();
	}

	/**
	 * Returns the instance with the given {@link IColumn} column children.
	 * <p>a shortcut of {@link IColumns#withChildren}</p>
	 * @param children The {@link IColumn} children
	 */
	static IGrid ofColumns(IColumn<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofColumns(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link IFoot} child.
	 * @param foot The {@link IFoot} child
	 */
	static IGrid ofFoot(IFoot foot) {
		return new IGrid.Builder().setFoot(foot).build();
	}

	/**
	 * Returns the instance with the given {@link IFooter} footer children.
	 * <p>a shortcut of {@link IFoot#withChildren}</p>
	 * @param children The {@link IFooter} children
	 */
	static IGrid ofFooters(Iterable<? extends IFooter<IAnyGroup>> children) {
		return new IGrid.Builder().setFoot(IFoot.of(children)).build();
	}

	/**
	 * Returns the instance with the given {@link IFooter} footer children.
	 * <p>a shortcut of {@link IFoot#withChildren}</p>
	 * @param children The {@link IFooter} children
	 */
	static IGrid ofFooters(IFooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofFooters(children);
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IGrid ofId(String id) {
		return new IGrid.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IMeshElement.super.renderProperties(renderer);

		render(renderer, "oddRowSclass", getOddRowSclass());

		String _innerWidth = getInnerWidth();
		if (!"100%".equals(_innerWidth))
			render(renderer, "innerWidth", _innerWidth);

		// ModelInfo includes model, _currentTop, _currentLeft, _topPad, _totalSize, _offset, _grid$rod
		Map<String, Object> map = getAuxInfo();
		if (!map.isEmpty()) {
			for (Map.Entry<String, ?> entry: map.entrySet()) {
				renderer.render(entry.getKey(), entry.getValue());
			}
		}

		renderer.render("emptyMessage", getEmptyMessage());

		int _visibleRows = getVisibleRows();
		if (_visibleRows > 0)
			renderer.render("visibleRows", _visibleRows);
	}

	/**
	 * Builds instances of type {@link IGrid IGrid}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends org.zkoss.zephyr.zpr.ImmutableIGrid.Builder {
	}

	/**
	 * Builds an updater of type {@link IGrid} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IGridUpdater {}

	/**
	 * Specifies whether to sort the model when the following cases:
	 *
	 * <ul>
	 *     <li>With {@link ListModel} case and {@link IColumn#withSortDirection(String)} is set.</li>
	 *     <li>{@link IColumn#withSortDirection(String)} is called.</li>
	 *     <li>Model receives {@link ListDataEvent} and {@link IColumn#withSortDirection(String)} is set.</li>
	 * </ul>
	 * If you want to ignore sorting when receiving ListDataEvent, you can specify
	 * the value as {@link #IGNORE_CHANGE}.
	 */
	enum Autosort {
		/**
		 * Enables the auto-sort facility.
		 */
		ENABLE("true"),
		/**
		 * Disables the auto-sort facility.
		 */
		NONE("false"),
		/**
		 * Ignores to sort when the {@link ListDataEvent} is sent.
		 */
		IGNORE_CHANGE("ignore.change");

		private final String value;

		Autosort(String value) {
			this.value = value;
		}
	}
}