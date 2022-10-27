/* ITree.java

	Purpose:

	Description:

	History:
		2:36 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.*;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.action.data.PageSizeData;
import org.zkoss.stateless.action.data.SelectData;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.ext.TreeSelectableModel;

/**
 * Immutable {@link Tree} component.
 *
 * <p>A tree consists of three parts, the set of columns, the set of footers,
 * and the tree body. The set of columns is defined by a number of {@link ITreecol}
 * components, one for each column. Each column will appear as a header at the top
 * of the tree. The second part, the set of footers is defined by a number of
 * {@link ITreefooter} components, one for each column also.
 * Each column will appear as a footer at the bottom of the tree.
 * The third part, the tree body, contains the data to appear in the tree and
 * is created with a {@link ITreechildren} component.
 *
 * <p> Although {@link ITreecols} is optional, if it exists, notice that
 * the number of its child ({@link ITreecol}) should equal the number of {@link ITreecell},
 * so that tree can display its content correctly. If {@link ITreecols} contains
 * no {@link ITreecol}, the tree will display nothing in its content.</p>
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
 *          <br> Notifies one that the user has selected a new item in the tree.</td>
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
 *          ({@code iTree.withAutopaging(boolean)}) is enabled and a user changed
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
 *          <td><img src="doc-files/ITree_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "paging"}</td>
 *          <td><img src="doc-files/ITree_mold_paging.png"/></td>
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
 *     &lt;name&gt;org.zkoss.zul.tree.autoSort&lt;/name/&gt;
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
 *     &lt;name&gt;org.zkoss.zul.tree.checkmarkDeselectOthers&lt;/name/&gt;
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
 *     &lt;name&gt;org.zkoss.zul.tree.selectOnHighlight.disabled&lt;/name/&gt;
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
 *     &lt;name&gt;org.zkoss.zul.tree.rightSelect&lt;/name/&gt;
 *     &lt;value&gt;false&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 * <li>
 * <p>To set the millisecond of scrolling throttling in Tree render on-demand (ROD),
 * you have to specify {@link #withThrottleMillis(int)} (ZK EE only).
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.tree.throttleMillis&lt;/name/&gt;
 *     &lt;value&gt;300&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 *</ul>
 * <b>Note:</b> with zk.xml setting, it will affect to all tree components.
 *
 * <br>
 * <br>
 * <h2>Example</h2>
 * <h4>2-Column Tree</h4>
 * <img src="doc-files/ITree_mold_default.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("")
 * public IComponent index() {
 *     return IWindow.ofTitle("tree demo").withBorder("normal").withWidth("500px")
 *         .withChildren(
 *         ITree.ofId("tree").withRows(5).withTreecols(
 *             ITreecols.of(
 *                 ITreecol.of("Name"),
 *                 ITreecol.of("Description")
 *             )).withTreechildren(ITreechildren.of(
 *                 ITreeitem.ofTreecells(
 *                     ITreecell.of("Item 1"),
 *                     ITreecell.of("Item 1 description")
 *                 ),
 *                 ITreeitem.ofTreecells(
 *                     ITreecell.of("Item 2"),
 *                     ITreecell.of("Item 2 description"))
 *                         .withTreechildren(
 *                             ITreechildren.of(
 *                             ITreeitem.of("Item 2.1"),
 *                             ITreeitem.ofTreecells(
 *                                 ITreecell.of("Item 2.2"),
 *                                 ITreecell.of("Item 2.2 is something who cares")),
 *                     ITreeitem.of("Item 3")))))
 *                 .withTreefoot(
 *                     ITreefoot.of(
 *                         ITreefooter.of("Count"),
 *                         ITreefooter.of("Summary")
 *                     )
 *                 )
 *             );
 * }
 * </code>
 * </pre>
 * <h4>Default Selection</h4>
 * <img src="doc-files/ITree_default_selection.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/defaultSelection")
 * public IComponent defaultSelection() {
 *     return ITree.of(
 *             ITreeitem.of("David"),
 *             ITreeitem.of("Thomas"),
 *             ITreeitem.of("Steven").withSelected(true)
 *     ).withWidth("300px");
 * }
 * </code>
 * </pre>
 * <h4>Frozen Component</h4>
 * <img src="doc-files/ITree_with_frozen.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/frozen")
 * public IComponent frozen() {
 *     return ITree.ofId("tree").withRows(5).withWidth("600px").withFrozen(
 *         IFrozen.ofColumns(2).withStart(1)
 *     ).withTreecols(
 *         ITreecols.of(
 *             ITreecol.of("ID").withHflex("min"),
 *             ITreecol.of("Priority").withHflex("min"),
 *             ITreecol.of("Status").withHflex("min"),
 *             ITreecol.of("Summary").withHflex("min"),
 *             ITreecol.of("Detail").withHflex("min")
 *         ).withSizable(true)
 *    ).withTreechildren(
 *         ITreechildren.of(
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("0001"),
 *                 ITreecell.of("1"),
 *                 ITreecell.of("closed"),
 *                 ITreecell.of("Fix login issue"),
 *                 ITreecell.of("Login does not work at all")
 *             ),
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("0002"),
 *                 ITreecell.of("3"),
 *                 ITreecell.of("open"),
 *                 ITreecell.of("Button style broken"),
 *                 ITreecell.of("Check main.css")
 *                 ).withTreechildren(
 *                     ITreechildren.of(
 *                         ITreeitem.ofTreecells(
 *                         ITreecell.of("00021"),
 *                         ITreecell.of("1"),
 *                         ITreecell.of("closed"),
 *                         ITreecell.of("Fix logout issue"),
 *                         ITreecell.of("Logout does not work at all")
 *                     )
 *                 )
 *             ),
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("0003"),
 *                 ITreecell.of("2"),
 *                 ITreecell.of("open"),
 *                 ITreecell.of("Client search result"),
 *                 ITreecell.of("Search service returns incomplete result")
 *             )
 *         )
 *     );
 * }
 * </code>
 * </pre>
 * <h4>Scrollable Tree</h4>
 * <p>A tree will be scrollable if you specify the rows attribute or the height
 * attribute and there is not enough space to display all the tree items.</p>
 * <img src="doc-files/ITree_scrollable.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/scrollable")
 * public IComponent scrollable() {
 *     return ITree.DEFAULT.withRows(4).withTreecols(
 *         ITreecols.of(
 *             ITreecol.of("Name"),
 *             ITreecol.of("Description")
 *         )
 *     ).withTreechildren(
 *         ITreechildren.of(
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("Item 1"),
 *                 ITreecell.of("Item 1 description")
 *             ),
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("Item 2"),
 *                 ITreecell.of("Item 2 description")
 *             ).withTreechildren(
 *                 ITreechildren.of(
 *                     ITreeitem.of("Item 2.1",
 *                     ITreeitem.of("Item 2.1.1"),
 *                     ITreeitem.of("Item 2.1.2")
 *                 ),
 *                 ITreeitem.ofTreecells(
 *                     ITreecell.of("Item 2.2"),
 *                     ITreecell.of("Item 2.2 is something who cares")
 *                 )
 *             )
 *         ),
 *         ITreeitem.of("Item 3")
 *        )
 *     );
 * }
 * </code>
 * </pre>
 *
 * <h4>Create-on-Open for Tree Controls</h4>
 * <p>As illustrated below, you could listen to the {@code onOpen} event, and then
 * load the children of a tree item. You can do the same thing using group boxes.</p>
 *
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/createOnOpen")
 * public IComponent createOnOpen() {
 *     return ITree.DEFAULT.withWidth("400px")
 *         .withTreecols(
 *             ITreecols.of(
 *                 ITreecol.of("Subject"),
 *                 ITreecol.of("From")
 *         )
 *     ).withTreechildren(
 *         ITreechildren.of(
 *             ITreeitem.ofTreecells(
 *                 ITreecell.of("Intel Snares XML"),
 *                 ITreecell.of("David Needle")
 *             ).withTreechildren(
 *                 ITreechildren.DEFAULT
 *             ).withOpen(false).withAction(this::doCreateOnOpen)
 *         )
 *     );
 * }
 *
 * {@literal @}{@code Action}(type = Events.ON_OPEN)
 * public void doCreateOnOpen(Self self, {@literal @}{@code ActionVariable}(targetId = ActionTarget.SELF, field = "empty") boolean isEmpty) {
 *     if (isEmpty) {
 *         UiAgent.getCurrent()
 *             .replaceChildren(self.findChild(ITreechildren.class),
 *                 ITreeitem.of("New added"));
 *     }
 * }
 *
 * </code>
 * </pre>
 * <p>As you can see above, the field {@code "empty"} of the {@link ActionVariable}
 * is used to detect whether the treeitem has an empty children, which receives the
 * returned result from {@link ITreeitem#isEmpty()} at client widget.</p>
 *
 * <h2>Mouse-less Entry Tree</h2>
 * <ul>
 * <li>Press <b>UP</b> and <b>DOWN</b> to move the selection up and down by one tree item.</li>
 * <li>Press <b>PgUp</b> and <b>PgDn</b> to move the selection up and down by one page.</li>
 * <li>Press <b>HOME</b> to move the selection to the first item, and <b>END</b> to the last item.</li>
 * <li>Press <b>RIGHT</b> to open a tree item, and <b>LEFT</b> to close a tree item.</li>
 * <li>Press <b>Ctrl+UP</b> and <b>Ctrl+DOWN</b> to move the focus up and down by one tree item without changing the selection.</li>
 * <li>Press <b>SPACE</b> to select the item in focus</li>
 * </ul>
 * @author jumperchen
 * @see Tree
 */
@StatelessStyle
public interface ITree extends IMeshElement<ITree>, IComposite<IMeshComposite>,
		IAnyGroup<ITree> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITree DEFAULT = new ITree.Builder().build();

	/**
	 * Constant for {@code "paging"} mold attributes of this immutable component.
	 */
	ITree PAGING = new ITree.Builder().setMold("paging").build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tree> getZKType() {
		return Tree.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Tree"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Tree";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkHeightAndRows() {
		if (getHeight() != null & getRows() != 0)
			throw new UiException("Not allowed to set height and rows at the same time");
	}

	/**
	 * Returns the {@link ITreecols} component, if any.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	ITreecols getTreecols();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code treecols}.
	 *
	 * <p>Sets the {@link ITreecols} component as the set of columns of the tree</p>
	 *
	 * @param treecols The {@link ITreecols} component to contain all tree columns
	 *                 <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withTreecols(@Nullable ITreecols treecols);

	/**
	 * Returns the {@link ITreefoot} component, if any.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	ITreefoot getTreefoot();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code treefoot}.
	 *
	 * <p>Sets the {@link ITreefoot} component as the set of footers of the tree</p>
	 *
	 * @param treefoot The {@link ITreefoot} component to contain all tree footers
	 *                 <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withTreefoot(@Nullable ITreefoot treefoot);

	/** Returns a readonly list of all descending {@link ITreeitem}
	 * (children's children and so on).
	 *
	 * <p><b>Note:</b> the performance of the size method of returned collection
	 * is no good.
	 */
	@Value.Lazy
	default Collection<ITreeitem> getItems() {
		ITreechildren treechildren = getTreechildren();
		if (treechildren != null) {
			return treechildren.getItems();
		}
		return Collections.emptyList();
	}

	/**
	 * Internal use for {@link IComposite}
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IMeshComposite> getAllComponents() {
		ArrayList<IMeshComposite> components = new ArrayList<>();
		List<IAuxhead> auxhead = getAuxhead();
		if (auxhead != null && !auxhead.isEmpty()) {
			components.addAll(auxhead);
		}
		ITreecols treecols = getTreecols();
		if (treecols != null) {
			components.add(treecols);
		}
		ITreechildren treechildren = getTreechildren();
		if (treechildren != null) {
			components.add(treechildren);
		}
		IFrozen frozen = getFrozen();
		if (frozen != null) {
			components.add(frozen);
		}
		ITreefoot treefoot = getTreefoot();
		if (treefoot != null) {
			components.add(treefoot);
		}
		IPaging pagingChild = getPagingChild();
		if (pagingChild != null) {
			components.add(pagingChild);
		}
		return components;
	}

	/**
	 * Returns the {@link IAuxhead} component, if any.
	 * <p>Default: {@code null}</p>
	 */
	@StatelessOnly
	default List<IAuxhead> getAuxhead() {
		return new ArrayList<>();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code auxhead}.
	 *
	 * <p>Sets the {@link IAuxhead} component as the set of auxiliary headers of the tree</p>
	 *
	 * @param auxhead The {@link IAuxhead} component to contain all auxiliary headers of the tree
	 *                <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withAuxhead(Iterable<? extends IAuxhead> auxhead);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code auxhead}.
	 *
	 * <p>Sets the {@link IAuxhead} component as the set of auxiliary headers of the tree</p>
	 *
	 * @param auxhead The {@link IAuxhead} component to contain all auxiliary headers of the tree
	 *                <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITree withAuxhead(IAuxhead... auxhead) {
		return withAuxhead(Arrays.asList(auxhead));
	}

	/**
	 * Returns the root {@link ITreechildren}, which contains all tree items in a
	 * hierarchical set of rows of components.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	ITreechildren getTreechildren();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code treechildren}.
	 *
	 * <p>Sets the {@link ITreechildren} component that the tree owns</p>
	 *
	 * @param treechildren The {@link ITreechildren} component to contain
	 *                     all tree items in a hierarchical set of rows of components.
	 *                     <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withTreechildren(@Nullable ITreechildren treechildren);

	/**
	 * Returns a {@link IFrozen} component to enable a frozen column or row facility.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IFrozen getFrozen();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code frozen}.
	 *
	 * <p>Sets the {@link IFrozen} component that the tree owns to enable a
	 * frozen column or row facility.</p>
	 *
	 * @param frozen The {@link IFrozen} component
	 *               <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withFrozen(@Nullable IFrozen frozen);

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
	 * <p>Sets the number of the rows of the tree to display at a screen, which
	 * means the height of the tree is based on the calculation of the number of the rows
	 * by multiplying with each row height.
	 * <br><br>
	 * <b>Note:</b> Not allowed to set {@code rows} and {@code height/vflex} at the same time.
	 *
	 * @param rows The number of rows to display.
	 *             <p>Default: {@code 0}. (Meaning no limitation)</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withRows(int rows);

	/**
	 * Returns whether to sort all items when model or sort direction be changed.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.tree.autoSort"}
	 * library property is not set in zk.xml.</p>
	 * <p><b>Note:</b> it's meaningless if {@link TreeModel} is not set.</p>
	 */
	@Nullable
	default String getAutosort() {
		return Library.getProperty("org.zkoss.zul.tree.autoSort");
	}

	/**
	 * Returns whether to sort all items when the model or the sort direction of
	 * the {@link ITreecol} be changed. (Internal only)
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
	 * Meaningless if {@link TreeModel} is not set.
	 *
	 * @param autosort The allowed values are {@code null}, {@code "false"},
	 * {@code "true"}, and {@code "ignore.change"}.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withAutosort(@Nullable String autosort);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autosort}.
	 *
	 * <p>Sets to enable the auto-sort facility to sort the model for this component.
	 * Meaningless if {@link TreeModel} is not set.
	 *
	 * @param autosort The {@link Autosort} attribute.
	 * @return A modified copy of the {@code this} object
	 * @see Autosort
	 */
	default ITree withAutosort(@Nullable Autosort autosort) {
		if (autosort == null) {
			return withAutosort((String) null);
		} else {
			return withAutosort(autosort.value);
		}
	}

	/**
	 * Returns whether to ignore sort all items when model or sort direction
	 * be changed. (Internal only)
	 * <p>Default: {@code true}</p>
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default boolean isIgnoreSortWhenChanged() {
		String autosort = getAutosort();
		return autosort == null ? true : "ignore.change".equals(autosort);
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
	 * state (by {@link TreeSelectableModel#setMultiple}).
	 *
	 * @param multiple True to allow multiple selections.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withMultiple(boolean multiple);

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
	ITree withCheckmark(boolean checkmark);

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
	ITree withNonselectableTags(@Nullable String nonselectableTags);

	/**
	 * Returns whether to toggle the selection if clicking on a tree item
	 * with a checkmark.
	 * Meaningless if {@link #isCheckmark()} is false
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.tree.checkmarkDeselectOthers"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isCheckmarkDeselectOther() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.tree.checkmarkDeselectOthers", "false"));
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
	ITree withCheckmarkDeselectOther(boolean checkmarkDeselectOther);

	/**
	 * Returns whether to disable select functionality when highlighting text
	 * content with mouse dragging or not.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.tree.selectOnHighlight.disabled"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isSelectOnHighlightDisabled() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.tree.selectOnHighlight.disabled", "false"));
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
	ITree withSelectOnHighlightDisabled(boolean selectOnHighlightDisabled);

	/**
	 * Returns whether to toggle a list item selection on right click, only
	 * if {@link #isCheckmark()} is enabled.
	 *
	 * <p>Default: {@code true}, if the {@code "org.zkoss.zul.tree.rightSelect"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isRightSelect() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.tree.rightSelect", "true"));
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
	ITree withRightSelect(boolean rightSelect);

	/**
	 * Returns the millisecond of scrolling throttling in Tree render on-demand (ROD).
	 * <p>Default: {@link Tree#DEFAULT_THROTTLE_MILLIS}, if the {@code ""org.zkoss.zul.tree.throttleMillis"}
	 * property is not set in zk.xml when run with ZK EE.</p>
	 */
	default int getThrottleMillis() {
		if (WebApps.getFeature("ee")) {
			return Library.getIntProperty("org.zkoss.zul.tree.throttleMillis", Tree.DEFAULT_THROTTLE_MILLIS);
		}
		return Tree.DEFAULT_THROTTLE_MILLIS;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code throttleMillis}.
	 *
	 * <p>Sets the millisecond of scrolling throttling in Tree render on-demand (ROD).
	 *
	 * @param throttleMillis The millisecond of scrolling throttling
	 * <p>Default: {@link Tree#DEFAULT_THROTTLE_MILLIS}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITree withThrottleMillis(int throttleMillis);

	/**
	 * Internal use for Model case
	 *
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default Map<String, Object> getAuxInfo() {
		return Collections.emptyMap();
	}

	/**
	 * Internal use
	 *
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
	 *
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITree checkMold() {
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
	 * Returns the instance with the given tree items which belong to root level.
	 * @param children The tree items of the root level.
	 */
	static ITree of(Iterable<? extends ITreeitem> children) {
		return new ITree.Builder().setTreechildren(ITreechildren.of(children))
				.build();
	}

	/**
	 * Returns the instance with the given tree items which belong to root level.
	 * @param children The tree items of the root level.
	 */
	static ITree of(ITreeitem... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return new ITree.Builder().setTreechildren(ITreechildren.of(children))
				.build();
	}

	/**
	 * Returns the instance with the given tree columns.
	 * @param treecols The {@link ITreecols} component to contain all tree columns.
	 */
	static ITree ofTreecols(ITreecols treecols) {
		return new ITree.Builder().setTreecols(treecols).build();
	}

	/**
	 * Returns the instance with the given tree columns.
	 * @param children The children of {@link ITreecols} component.
	 */
	static ITree ofTreecols(Iterable<? extends ITreecol<IAnyGroup>> children) {
		return new ITree.Builder().setTreecols(ITreecols.of(children)).build();
	}

	/**
	 * Returns the instance with the given tree columns.
	 * @param children The children of {@link ITreecols} component.
	 */
	static ITree ofTreecols(ITreecol<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofTreecols(children);
	}

	/**
	 * Returns the instance with the given tree foot.
	 * @param treefoot The {@link ITreefoot} component to contain all tree footers.
	 */
	static ITree ofTreefoot(ITreefoot treefoot) {
		return new ITree.Builder().setTreefoot(treefoot).build();
	}

	/**
	 * Returns the instance with the given tree footers.
	 * @param children The tree footers of the tree foot.
	 */
	static ITree ofTreefooters(Iterable<? extends ITreefooter<IAnyGroup>> children) {
		return new ITree.Builder().setTreefoot(ITreefoot.of(children)).build();
	}

	/**
	 * Returns the instance with the given tree footers.
	 * @param children The tree footers of the tree foot.
	 */
	static ITree ofTreefooters(ITreefooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return ofTreefooters(children);
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component.
	 */
	static ITree ofId(String id) {
		return new ITree.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IMeshElement.super.renderProperties(renderer);

		int rows = getRows();
		if (rows > 0)
			renderer.render("rows", rows);

		render(renderer, "multiple", isMultiple());
		render(renderer, "checkmark", isCheckmark());

		// ModelInfo includes _currentTop, _currentLeft, _anchorTop, _anchorLeft, preloadSize
		Map<String, Object> map = getAuxInfo();
		if (!map.isEmpty()) {
			for (Map.Entry<String, ?> entry : map.entrySet()) {
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
		if (isSelectOnHighlightDisabled()) // F70-ZK-2433
			renderer.render("selectOnHighlightDisabled", true);

		int throttleMillis = getThrottleMillis();
		if (throttleMillis != 300)
			render(renderer, "throttleMillis", throttleMillis);
	}

	/**
	 * Builds instances of type {@link ITree ITree}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITree.Builder {
	}

	/**
	 * Builds an updater of type {@link ITree} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 *
	 * @see SmartUpdater
	 */
	class Updater extends ITreeUpdater {
	}

	/**
	 * Specifies whether to sort the model when the following cases:
	 *
	 * <ul>
	 *     <li>With {@link TreeModel} case and {@link ITreecol#withSortDirection(String)} is set.</li>
	 *     <li>{@link ITreecol#withSortDirection(String)} is called.</li>
	 *     <li>Model receives {@link TreeDataEvent} and {@link ITreecol#withSortDirection(String)} is set.</li>
	 * </ul>
	 * If you want to ignore sorting when receiving TreeDataEvent, you can specify
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
		 * Ignores to sort when the {@link TreeDataEvent} is sent.
		 */
		IGNORE_CHANGE("ignore.change");

		private final String value;

		Autosort(String value) {
			this.value = value;
		}
	}
}