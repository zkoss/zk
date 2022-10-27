/* ITreeitem.java

	Purpose:

	Description:

	History:
		2:56 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Treeitem;

/**
 * Immutable {@link Treeitem} component
 * <p> Treeitem contains a row of data (treerow), and an optional treechildren.
 * <p> If the component doesn't contain a treechildren, it is a leaf node that
 * doesn't accept any child items.
 * <p> If it contains a treechildren, it is a branch node that might contain other items.
 * For a branch node, an {@code +/-} button will appear at the beginning of the row,
 * such that user could open and close the item by clicking on the {@code +/-} button.
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
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br>Denotes user has opened or closed a component. It is useful to implement
 *          load-on-demand by listening to the onOpen action, and creating components
 *          when the first time the component is opened.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author jumperchen
 * @see Treeitem
 */
@StatelessStyle
public interface ITreeitem extends IXulElement<ITreeitem>,
		IDisable<ITreeitem>, IComposite<ITreeitemComposite> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreeitem DEFAULT = new ITreeitem.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treeitem> getZKType() {
		return Treeitem.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treeitem"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treeitem";
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
	 * To control the size of Treeitem related
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreeitem withWidth(@Nullable String width) {
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
	 * To control the size of Treeitem related
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreeitem withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}
	/**
	 * Internal use for {@link IComposite}
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<ITreeitemComposite> getAllComponents() {
		List<ITreeitemComposite> components = new ArrayList<>(2);
		ITreerow treerow = getTreerow();
		if (treerow != null) {
			components.add(treerow);
		}
		ITreechildren treechildren = getTreechildren();
		if (treechildren != null) {
			components.add(treechildren);
		}
		return components;
	}

	/** Returns the treerow that this tree item owns (might null).
	 * Each tree items has exactly one tree row.
	 */
	@Nullable
	ITreerow getTreerow();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code treerow}.
	 *
	 * <p>Sets the treerow as a child to this component
	 *
	 * @param treerow The treerow child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withTreerow(@Nullable ITreerow treerow);

	/** Returns the treechildren that this tree item owns, or null if
	 * doesn't have any child.
	 */
	@Nullable
	ITreechildren getTreechildren();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code treechildren}.
	 *
	 * <p>Sets the treechildren as a child to this component
	 *
	 * @param treechildren The treechildren child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withTreechildren(@Nullable ITreechildren treechildren);

	/**
	 * Return the index of this item
	 * <p>Default: {@code -1}, meaning not set</p>
	 */
	default int getIndex() {
		return -1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code index}.
	 *
	 * <p>Sets the index of this component.
	 *
	 * @param index The index of the child from its parent component.
	 * <p>Default: {@code -1}, meaning not set.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withIndex(int index);

	/** Returns the image of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	default String getImage() {
		ITreerow treerow = getTreerow();
		return treerow != null ? treerow.getImage() : null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code image}.
	 *
	 * <p> Sets the image of the first {@link ITreecell} it contains.
	 * <p>If treerow and treecell are not created, this method create them automatically.
	 *
	 * @param image The image of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withImage(@Nullable String image);

	/** Returns the label of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	default String getLabel() {
		ITreerow treerow = getTreerow();
		return treerow != null ? treerow.getLabel() : null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code label}.
	 *
	 * <p> Sets the label of the first {@link ITreecell} it contains.
	 * <p>If treerow and treecell are not created, this method create them automatically.
	 *
	 * @param label The label of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withLabel(@Nullable String label);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreeitem checkImage() {
		String image = getImage();
		if (!Strings.isEmpty(image)) {
			ITreerow treerow = getTreerow();
			if (treerow == null) {
				return new Builder().from(this).setImage(null).setTreerow(ITreerow.ofImage(image)).build();
			} else {
				return new Builder().from(this).setImage(null).setTreerow(treerow.withImage(image)).build();
			}
		} else {
			return this;
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreeitem checkLabel() {
		String label = getLabel();
		if (!Strings.isEmpty(label)) {
			ITreerow treerow = getTreerow();
			if (treerow == null) {
				return new Builder().from(this).setLabel(null).setTreerow(ITreerow.of(label)).build();
			} else {
				return new Builder().from(this).setLabel(null).setTreerow(treerow.withLabel(label)).build();
			}
		} else {
			return this;
		}
	}

	/** Returns whether this container is open.
	 * <p>Default: {@code true}.
	 */
	default boolean isOpen() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code open}.
	 *
	 * <p> Sets whether this container is open.
	 *
	 * @param open {@code false} to hide all sublevel items.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withOpen(boolean open);

	/** Returns whether this item is selected.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isSelected() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code selected}.
	 *
	 * <p> Sets whether the component is selected.
	 *
	 * @param selected {@code true} to select this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withSelected(boolean selected);

	/**
	 * Return true whether all children of this tree item, if any, is loaded at client
	 * <p>Default: {@code false}</p>
	 */
	default boolean isLoaded() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code loaded}.
	 *
	 * <p> Sets whether the component is loaded. (Internal use)
	 *
	 * @param loaded {@code true} to indicate this component is loaded at client.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 * @hidden for Javadoc
	 */
	ITreeitem withLoaded(boolean loaded);

	/**
	 * Returns whether it is selectable.
	 * <p>Default: {@code true}.</p>
	 */
	default boolean isSelectable() {
		return true;
	}

	/** Returns whether the component is to act as a container
	 * which can have sublevel items.
	 */
	@Value.Lazy
	default boolean isContainer() {
		return getTreechildren() != null;
	}

	/** Returns whether to contain any sublevel tree items or not.
	 */
	@Value.Lazy
	default boolean isEmpty() {
		ITreechildren treechildren = getTreechildren();
		return treechildren == null || treechildren.getChildren().isEmpty();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code selectable}.
	 *
	 * <p>If the tree is in a checkmark mode, the selectable state will affect
	 * the checkable icon to display or not.</p>
	 *
	 * @param selectable {@code false} to indicate the checkable icon is not displayed.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreeitem withSelectable(boolean selectable);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreeitem checkSelectable() {
		// non-checkable cannot be selected
		if (!isSelectable() && isSelected()) {
			return withSelected(false);
		} else {
			return this;
		}
	}

	/**
	 * Returns true whether this tree item is rendered at client. Unlike {@link #isLoaded()}
	 * which is used to check whether all children of this tree item are loaded.
	 * <p>Default: {@code true}
	 */
	default boolean isRendered() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rendered}.
	 *
	 * <p> Sets whether this tree item is rendered at client. (Internal use)
	 *
	 * @param rendered {@code false} to indicate this component is not rendered at client.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 * @hidden for Javadoc
	 */
	ITreeitem withRendered(boolean rendered);


	/**
	 * Returns the instance with the given label.
	 * @param label The label that the first tree cell holds.
	 */
	static ITreeitem of(String label) {
		return new Builder().setTreerow(ITreerow.of(label)).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITreeitem ofId(String id) {
		return new Builder().setId(id).build();
	}

	/**
	 * Returns the instance with the given label and all sublevel items.
	 * @param label The label that the first tree cell holds.
	 * @param items All sublevel items belong to this item
	 */
	static ITreeitem of(String label, ITreeitem... items) {
		return new Builder().setTreerow(ITreerow.of(label))
				.setTreechildren(ITreechildren.of(items)).build();
	}

	/**
	 * Returns the instance with the given label and all sublevel items.
	 * @param label The label that the first tree cell holds.
	 * @param items All sublevel items belong to this item
	 */
	static ITreeitem of(String label, Iterable<? extends ITreeitem> items) {
		return new Builder().setTreerow(ITreerow.of(label))
				.setTreechildren(ITreechildren.of(items)).build();
	}

	/**
	 * Returns the instance with the given treerow.
	 * @param treerow The treerow belongs to this component.
	 */
	static ITreeitem ofTreerow(ITreerow treerow) {
		return new Builder().setTreerow(treerow).build();
	}

	/**
	 * Returns the instance with the given tree cells.
	 * @param treecells All tree cells belong to this component.
	 */
	static ITreeitem ofTreecells(ITreecell<IAnyGroup>... treecells) {
		return new Builder().setTreerow(ITreerow.of(treecells)).build();
	}

	/**
	 * Returns the instance with the given tree cells.
	 * @param treecells All tree cells belong to this component.
	 */
	static ITreeitem ofTreecells(Iterable<? extends ITreecell<IAnyGroup>> treecells) {
		return new Builder().setTreerow(ITreerow.of(treecells)).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "selected", isSelected());
		render(renderer, "disabled", isDisabled());
		if (!isOpen())
			renderer.render("open", false);
		if (!isSelectable())
			renderer.render("checkable", false);
		render(renderer, "_loadedChildren", isLoaded());

		render(renderer, "_loaded", isRendered());
		render(renderer, "_index", getIndex());
	}

	/**
	 * Builds instances of type {@link ITreeitem ITreeitem}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITreeitem.Builder {}

	/**
	 * Builds an updater of type {@link ITreeitem} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITreeitemUpdater {}
}
