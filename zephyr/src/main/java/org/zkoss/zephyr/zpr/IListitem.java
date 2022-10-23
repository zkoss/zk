/* IListitem.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:44:55 CST 2021, Created by katherine

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
import org.zkoss.zul.Listitem;

/**
 * Immutable {@link Listitem} component
 *
 * @author katherine
 * @see Listitem
 */
@ZephyrStyle
public interface IListitem extends IListitemBase<IListitem>,
		IChildable<IListitem, IListcell<IAnyGroup>> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListitem DEFAULT = new IListitem.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listitem> getZKType() {
		return Listitem.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listitem"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Listitem";
	}

	/** Returns the label of the first {@link IListcell} it contains.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	default String getLabel() {
		List<IListcell<IAnyGroup>> children = getChildren();
		if (!children.isEmpty()) {
			return children.get(0).getLabel();
		}
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code label}.
	 *
	 * <p> Sets the label of the first {@link IListcell} it contains.
	 * <p>If it is not created, we automatically create it.
	 *
	 * @param label The label of the first {@link IListcell} it contains.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListitem withLabel(@Nullable String label);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IListitem checkLabel() {
		String label = getLabel();
		if (label != null) {
			List<IListcell<IAnyGroup>> children = getChildren();
			if (children.isEmpty()) {
				return withChildren(IListcell.of(label));
			} else {
				List<IListcell<IAnyGroup>> newChildren = new ArrayList(getChildren());
				IListcell<IAnyGroup> cell = newChildren.get(0);
				if (!Objects.equals(cell.getLabel(), label)) {
					newChildren.set(0, cell.withLabel(label));
					return withChildren(newChildren);
				}
			}
		}
		return this;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IListitem checkImage() {
		String image = getImage();
		if (image != null) {
			List<IListcell<IAnyGroup>> children = getChildren();
			if (children.isEmpty()) {
				return withChildren(IListcell.ofImage(image));
			} else {
				List<IListcell<IAnyGroup>> newChildren = new ArrayList(getChildren());
				IListcell<IAnyGroup> cell = newChildren.get(0);
				if (!Objects.equals(cell.getImage(), image)) {
					newChildren.set(0, cell.withImage(image));
					return withChildren(newChildren);
				}
			}
		}
		return this;
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the first cell holds.
	 */
	static IListitem of(String label) {
		return new Builder().addChildren(IListcell.of(label)).build();
	}

	/**
	 * Returns the instance with the given {@link IListcell} children.
	 * @param children The children for each {@link IListcell}
	 */
	static IListitem of(Iterable<? extends IListcell<IAnyGroup>> children) {
		return new Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IListcell} children.
	 * @param children The children for each {@link IListcell}
	 */
	static IListitem of(IListcell<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IListitem ofId(String id) {
		return new Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IListitem IListitem}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIListitem.Builder {}

	/**
	 * Builds an updater of type {@link IListitem} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IListitemUpdater {}
}