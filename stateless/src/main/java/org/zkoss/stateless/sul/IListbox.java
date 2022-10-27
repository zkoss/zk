/* IListbox.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:37:55 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

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
import org.zkoss.stateless.action.data.CheckData;
import org.zkoss.stateless.action.data.PageSizeData;
import org.zkoss.stateless.action.data.SelectData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Selectable;

/**
 * Immutable {@link Listbox} component
 * <p><b>Note:</b> unlike {@link Listbox}, there is no {@code "select"} mold in stateless API,
 * please use {@link ISelectbox} instead.</p>
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
 *          <td>onSelect</td>
 *          <td><strong>ActionData</strong>: {@link SelectData}
 *          <br> Notifies one that the user has selected a new item in the listbox.</td>
 *       </tr>
 *       <tr>
 *          <td>onFocus</td>
 *          <td>Denotes when a component gets the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed when
 *          the event listener for {@code onFocus} got executed.</td>
 *       </tr>
 *       <tr>
 *          <td>onBlur</td>
 *          <td>Denotes when a component loses the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed
 *          when the event listener for {@code onBlur} got executed.</td>
 *       </tr>
 *       <tr>
 *          <td>onPageSize</td>
 *          <td><strong>ActionData</strong>: {@link PageSizeData}
 *          <br>Notifies the paging size has been changed when the autopaging
 *          ({@code iListbox.withAutopaging(boolean)}) is enabled and a user changed
 *          the size of the content.</td>
 *       </tr>
 *       <tr>
 *          <td>onCheckSelectAll</td>
 *          <td><strong>ActionData</strong>: {@link CheckData}
 *          <br>Notifies the checkbox on a listheader is checked to select all checkable items.</td>
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
 *          <td><img src="doc-files/IListbox_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "paging"}</td>
 *          <td><img src="doc-files/IListbox_mold_paging.png"/></td>
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
 *     &lt;name&gt;org.zkoss.zul.listbox.autoSort&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 * <li>
 * <p>To set to prefer to deselect all other items and select the
 * item being clicked when {@link #isCheckmark()} is enabled, you have to specify
 * {@link #withCheckmarkDeselectOther(boolean)} to {@code true}.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.listbox.checkmarkDeselectOthers&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 * <li>
 * <p>To set whether to disable select functionality when highlighting
 * text content with mouse dragging or not, you have to specify
 * {@link #withSelectOnHighlightDisabled(boolean)} to {@code true}.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.listbox.selectOnHighlight.disabled&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 * <li>
 * <p>To set whether to disable the selection will be toggled when
 * the user right clicks item, only if {@link #isCheckmark()} is enabled. You have
 * to specify {@link #withRightSelect(boolean)} to {@code false}.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.listbox.rightSelect&lt;/name/&gt;
 *     &lt;value&gt;false&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 * <li>
 * <p>To set whether to enable the selection will be toggled when
 * the user clicks listgroup item, only if {@link IListgroupChild} in used. You have
 * to specify {@link #withListgroupSelectable(boolean)} to {@code true}.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.listbox.groupSelect&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 *</ul>
 * <b>Note:</b> with zk.xml setting, it will affect to all tree components.
 *
 *
 * @author katherine
 * @see Listbox
 */
@StatelessStyle
public interface IListbox extends IMeshElement<IListbox>, IComposite<IMeshComposite>,
		IChildable<IListbox, IListitemBase>, IAnyGroup<IListbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListbox DEFAULT = new IListbox.Builder().build();

	/**
	 * Constant for {@code "paging"} mold attributes of this immutable component.
	 */
	IListbox PAGING = new IListbox.Builder().setMold("paging").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listbox> getZKType() {
		return Listbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Listbox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkRows() {
		int rows = getRows();
		if (rows < 0) {
			throw new WrongValueException("Illegal rows: " + rows);
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IListbox checkMold() {
		IPaging pgi = getPagingChild();
		if ("paging".equals(getMold())) {
			if (getPagingChild() == null) {
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

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkVisibleRows() {
		int rows = getRows();
		if (rows < 0) {
			throw new WrongValueException("Illegal rows: " + rows);
		} else if (rows > 0) {
			if (getVflex() != null)
				throw new UiException("Not allowed to set vflex and visibleRows at the same time");
			if (getHeight() != null)
				throw new UiException("Not allowed to set height and visibleRows at the same time");
		}
	}

	/** Returns the listhead.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IListhead getListhead();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code listhead}.
	 *
	 * <p>Sets the listhead as a child to this component
	 *
	 * @param listhead The listhead child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withListhead(@Nullable IListhead listhead);

	/** Returns the foot.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IListfoot getListfoot();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code listfoot}.
	 *
	 * <p>Sets the listfoot as a child to this component
	 *
	 * @param listfoot The listfoot child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withListfoot(@Nullable IListfoot listfoot);

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
	IListbox withFrozen(@Nullable IFrozen frozen);

	/**
	 * Returns the auxhead child.
	 */
	@StatelessOnly
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
	IListbox withAuxhead(Iterable<? extends IAuxhead> auxhead);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code auxhead}.
	 *
	 * <p>Sets the list of auxhead as children to this component
	 *
	 * @param auxhead The auxhead child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IListbox withAuxhead(IAuxhead... auxhead) {
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
		IListhead listhead = getListhead();
		if (listhead != null) {
			components.add(listhead);
		}
		List<IListitemBase> listitem = getChildren();
		if (listitem != null && !listitem.isEmpty()) {
			components.addAll(listitem);
		}
		IFrozen frozen = getFrozen();
		if (frozen != null) {
			components.add(frozen);
		}
		IListfoot foot = getListfoot();
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
	 * Returns the number of the rows to display at screen. Zero means no limitation.
	 * <p>Default: {@code 0}</p>
	 */
	default int getRows() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rows}.
	 *
	 * <p>Sets the number of the rows of the listbox to display at a screen, which
	 * means the height of the listbox is based on the calculation of the number of the rows
	 * by multiplying with each row height.
	 * <br><br>
	 * <b>Note:</b> Not allowed to set {@code rows} and {@code height/vflex} at the same time.
	 *
	 * @param rows The number of rows to display.
	 *             <p>Default: {@code 0}. (Meaning no limitation)</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withRows(int rows);
	/**
	 * Returns whether to sort all items when model or sort direction be changed.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.listbox.autoSort"}
	 * library property is not set in zk.xml.</p>
	 * <p><b>Note:</b> it's meaningless if {@link ListModel} is not set.</p>
	 */
	@Nullable
	default String getAutosort() {
		return Library.getProperty("org.zkoss.zul.listbox.autoSort");
	}
	/**
	 * Returns whether to sort all items when the model or the sort direction of
	 * the {@link IListheader} be changed. (Internal only)
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
	IListbox withAutosort(@Nullable String autosort);

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
	default IListbox withAutosort(@Nullable Autosort autosort) {
		if (autosort == null) {
			return withAutosort((String) null);
		} else {
			return withAutosort(autosort.value);
		}
	}

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>Default: {@code false}.
	 */
	default boolean isMultiple() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code multiple}.
	 *
	 * <p>Sets whether multiple selections are allowed.
	 * <p>Notice that, if a model is assigned, it will change the model's
	 * state (by {@link Selectable#setMultiple}).
	 *
	 * @param multiple True to allow multiple selections.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withMultiple(boolean multiple);

	/**
	 * Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: {@code false}.
	 */
	default boolean isCheckmark() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code checkmark}.
	 *
	 * <p>Sets whether the check mark shall be displayed in front of each item.
	 * <p>The check mark is a checkbox if {@link #isMultiple} returns true.
	 * It is a radio button if {@link #isMultiple} returns false.
	 *
	 * @param checkmark True to enable the check mark in front of each item.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withCheckmark(boolean checkmark);

	/**
	 * Returns a list of HTML tag names that shall <i>not</i> cause the tree item
	 * being selected if they are clicked.
	 * <p>Default: {@code null} (it means {@code button}, {@code input}, {@code textarea}
	 * and {@code a} HTML elements). If you want to select no matter which tag is
	 * clicked, please specify an empty string.
	 * Specify {@code null} to use the default and {@code ""} to indicate none.
	 */
	@Nullable
	String getNonselectableTags();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code name}.
	 *
	 * <p> Sets a list of HTML tag names that shall <i>not</i> cause the tree item
	 * being selected if they are clicked.
	 * <p>Default: {@code null} (it means {@code button}, {@code input}, {@code textarea}
	 * and {@code a} HTML elements). If you want to select no matter which tag
	 * is clicked, please specify an empty string.
	 *
	 * @param nonselectableTags A list of HTML tag names that will <i>not</i> cause the tree item
	 * being selected if clicked. Specify {@code null} to use the default and {@code ""}
	 *    to indicate none.
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withNonselectableTags(@Nullable String nonselectableTags);

	/**
	 * Returns whether to toggle the selection if clicking on a tree item
	 * with a checkmark.
	 * Meaningless if {@link #isCheckmark()} is false
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.listbox.checkmarkDeselectOthers"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isCheckmarkDeselectOther() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.listbox.checkmarkDeselectOthers", "false"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code checkmarkDeselectOther}.
	 *
	 * <p>Sets {@code true} to prefer to deselect all other items and select the item being clicked.
	 * If {@link #isCheckmark()} is enabled.
	 *
	 * <p>Default: {@code false} (not to deselect other items).
	 *
	 * @param checkmarkDeselectOther True to deselect other items and select the
	 * item being clicked.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withCheckmarkDeselectOther(boolean checkmarkDeselectOther);

	/**
	 * Returns whether to disable select functionality when highlighting text
	 * content with mouse dragging or not.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.listbox.selectOnHighlight.disabled"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isSelectOnHighlightDisabled() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.listbox.selectOnHighlight.disabled", "false"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code selectOnHighlightDisabled}.
	 *
	 * <p>Sets whether to disable select functionality when highlighting text content with mouse dragging or not.
	 *
	 * @param selectOnHighlightDisabled True to disable select functionality when
	 * highlighting text content with mouse dragging or not.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withSelectOnHighlightDisabled(boolean selectOnHighlightDisabled);

	/**
	 * Returns whether to toggle a list item selection on right click, only
	 * if {@link #isCheckmark()} is enabled.
	 *
	 * <p>Default: {@code true}, if the {@code "org.zkoss.zul.listbox.rightSelect"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isRightSelect() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.listbox.rightSelect", "true"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rightSelect}.
	 *
	 * <p>Sets whether to enable the selection will be toggled when the user right
	 * clicks item, only if {@link #isCheckmark()} is enabled.
	 *
	 * @param rightSelect False not to select/deselect item on right click
	 * <p>Default: {@code true}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withRightSelect(boolean rightSelect);

	/**
	 * Returns whether Listgroup is selectable.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isListgroupSelectable() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.listbox.groupSelect", "false"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code listgroupSelectable}.
	 *
	 * <p>Sets whether to enable or disable the selection will be toggled when the user
	 * clicks item on {@link IListgroupChild}.
	 *
	 * @param listgroupSelectable False not to select/deselect item on click
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withListgroupSelectable(boolean listgroupSelectable);

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
	IListbox withInnerWidth(String innerWidth);

	/**
	 * Returns the style class for the odd rows.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getOddRowSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code oddRowSclass}.
	 *
	 * <p> Sets the style class for the odd rows. If the style class doesn't exist,
	 * the striping effect disappears. You can provide different effects by
	 * providing the proper style classes.
	 *
	 * @param oddRowSclass The style class for the odd rows
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListbox withOddRowSclass(@Nullable String oddRowSclass);
	/**
	 * Internal use for Model case
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default Map<String, Object> getAuxInfo() {
		return Collections.emptyMap();
	}

	/**
	 * Returns the instance with the given {@link IListitemBase} children.
	 * @param children The children of {@link IListitemBase}
	 */
	static IListbox of(Iterable<? extends IListitemBase> children) {
		return new Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IListitemBase} children.
	 * @param children The children of {@link IListitemBase}
	 */
	static IListbox of(IListitemBase... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link IListhead} child.
	 * @param listhead The {@link IListhead} child
	 */
	static IListbox ofListhead(IListhead listhead) {
		return new IListbox.Builder().setListhead(listhead).build();
	}

	/**
	 * Returns the instance with the given {@link IListheader} column children.
	 * <p>a shortcut of {@link IListhead#withChildren}</p>
	 * @param children The {@link IListheader} children
	 */
	static IListbox ofListheaders(Iterable<? extends IListheader<IAnyGroup>> children) {
		return new IListbox.Builder().setListhead(IListhead.of(children)).build();
	}

	/**
	 * Returns the instance with the given {@link IListheader} column children.
	 * <p>a shortcut of {@link IListhead#withChildren}</p>
	 * @param children The {@link IListheader} children
	 */
	static IListbox ofListheaders(IListheader<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofListheaders(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link IListfoot} foot child.
	 * @param foot The {@link IListfoot} child
	 */
	static IListbox ofListfoot(IListfoot foot) {
		return new IListbox.Builder().setListfoot(foot).build();
	}

	/**
	 * Returns the instance with the given {@link IListfooter} footer children.
	 * <p>a shortcut of {@link IListfoot#withChildren}</p>
	 * @param children The {@link IListfooter} children
	 */
	static IListbox ofListfooters(Iterable<? extends IListfooter<IAnyGroup>> children) {
		return new IListbox.Builder().setListfoot(IListfoot.of(children)).build();
	}

	/**
	 * Returns the instance with the given {@link IListfooter} footer children.
	 * <p>a shortcut of {@link IListfoot#withChildren}</p>
	 * @param children The {@link IListfooter} children
	 */
	static IListbox ofListfooters(IListfooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofListfooters(children);
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IListbox ofId(String id) {
		return new Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IMeshElement.super.renderProperties(renderer);

		int rows = getRows();
		if (rows > 0)
			renderer.render("rows", rows);

		render(renderer, "checkmark", isCheckmark());
		render(renderer, "multiple", isMultiple());
		render(renderer, "oddRowSclass", getOddRowSclass());

		String _innerWidth = getInnerWidth();
		if (!"100%".equals(_innerWidth))
			render(renderer, "innerWidth", _innerWidth);

		Map<String, Object> map = getAuxInfo();
		if (!map.isEmpty()) {
			for (Map.Entry<String, ?> entry: map.entrySet()) {
				renderer.render(entry.getKey(), entry.getValue());
			}
		}
		String _nonselTags = getNonselectableTags();
		if (_nonselTags != null)
			renderer.render("nonselectableTags", _nonselTags);
		if (isCheckmarkDeselectOther())
			renderer.render("_cdo", true);
		if (!isRightSelect())
			renderer.render("rightSelect", false);
		if (isListgroupSelectable())
			renderer.render("groupSelect", true);
		if (isSelectOnHighlightDisabled()) // F70-ZK-2433
			renderer.render("selectOnHighlightDisabled", true);
	}

	/**
	 * Builds instances of type {@link IListbox IListbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIListbox.Builder {}

	/**
	 * Builds an updater of type {@link IListbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IListboxUpdater {}

	/**
	 * Specifies whether to sort the model when the following cases:
	 *
	 * <ul>
	 *     <li>With {@link ListModel} case and {@link IListheader#withSortDirection(String)} is set.</li>
	 *     <li>{@link IListheader#withSortDirection(String)} is called.</li>
	 *     <li>Model receives {@link ListDataEvent} and {@link IListheader#withSortDirection(String)} is set.</li>
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