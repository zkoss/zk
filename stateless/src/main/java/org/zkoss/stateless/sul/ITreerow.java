/* ITreerow.java

	Purpose:

	Description:

	History:
		3:22 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zul.Treerow;

/**
 * Immutable {@link Treerow} component
 * @author jumperchen
 * @see Treerow
 */
@StatelessStyle
public interface ITreerow extends IXulElement<ITreerow>,
		IChildable<ITreerow, ITreecell<IAnyGroup>>, ITreeitemComposite<ITreerow> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreerow DEFAULT = new ITreerow.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treerow> getZKType() {
		return Treerow.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treerow"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treerow";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void check() {
		IComponentChecker.checkWidth(getWidth());
		IComponentChecker.checkHflex(getHflex());
	}

	/** Returns the image of the first {@link ITreecell} it contains, or null
	 * if no such treecell.
	 */
	@Nullable
	default String getImage() {
		List<ITreecell<IAnyGroup>> children = getChildren();
		if (children == null || children.isEmpty()) {
			return null;
		} else {
			return children.get(0).getImage();
		}
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code image}.
	 *
	 * <p>Sets the image of the first {@link ITreecell} it contains.
	 * <p><b>Note:</b> if no any treecell exists, this method creates one automatically.
	 *
	 * @param image The image of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreerow withImage(@Nullable String image);

	/** Returns the label of the first {@link ITreecell} it contains, or null
	 * if no such treecell.
	 */
	@Nullable
	default String getLabel() {
		List<ITreecell<IAnyGroup>> children = getChildren();
		if (children == null || children.isEmpty()) {
			return null;
		} else {
			return children.get(0).getLabel();
		}
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code label}.
	 *
	 * <p>Sets the label of the first {@link ITreecell} it contains.
	 * <p><b>Note:</b> if no any treecell exists, this method creates one automatically.
	 *
	 * @param label The label of the first {@link ITreecell} it contains.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreerow withLabel(@Nullable String label);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreerow checkImage() {
		String image = getImage();
		if (!Strings.isEmpty(image)) {
			List<ITreecell<IAnyGroup>> children = getChildren();
			if (children == null || children.isEmpty()) {
				return new Builder().from(this).setImage(null).addChildren(ITreecell.ofImage(image)).build();
			} else {
				Builder builder = new Builder().from(this).setImage(null);
				List<ITreecell<IAnyGroup>> newChildren = new ArrayList<>(children.size());
				newChildren.addAll(children);
				newChildren.set(0, children.get(0).withImage(image));
				return builder.setChildren(newChildren).build();
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
	default ITreerow checkLabel() {
		String label = getLabel();
		if (!Strings.isEmpty(label)) {
			List<ITreecell<IAnyGroup>> children = getChildren();
			if (children == null || children.isEmpty()) {
				return new Builder().from(this).setLabel(null).addChildren(ITreecell.of(label)).build();
			} else {
				Builder builder = new Builder().from(this).setLabel(null);
				List<ITreecell<IAnyGroup>> newChildren = new ArrayList<>(children.size());
				newChildren.addAll(children);
				newChildren.set(0, children.get(0).withLabel(label));
				return builder.setChildren(newChildren).build();
			}
		} else {
			return this;
		}
	}

	/**
	 * Returns the instance with a {@link ITreecell} holding the given label.
	 * @param label The label of the first {@link ITreecell}
	 */
	static ITreerow of(String label) {
		return new Builder().addChildren(ITreecell.of(label)).build();
	}

	/**
	 * Returns the instance with a {@link ITreecell} holding the given label
	 * and image.
	 * @param label The label of the first {@link ITreecell}
	 * @param image The image of the first {@link ITreecell}
	 */
	static ITreerow of(String label, String image) {
		return new Builder().addChildren(ITreecell.of(label, image)).build();
	}

	/**
	 * Returns the instance with the given {@link ITreecell treecells}.
	 * @param children The tree cells of the component
	 */
	static ITreerow of(ITreecell<IAnyGroup>... children) {
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link ITreecell treecells}.
	 * @param children The tree cells of the component
	 */
	static ITreerow of(Iterable<? extends ITreecell<IAnyGroup>> children) {
		return new Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with a {@link ITreecell} holding the given image.
	 * @param image The image of the first {@link ITreecell}
	 */
	static ITreerow ofImage(String image) {
		return new Builder().addChildren(ITreecell.ofImage(image)).build();
	}

	/**
	 * Builds instances of type {@link ITreerow ITreerow}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITreerow.Builder {}

	/**
	 * Builds an updater of type {@link ITreerow} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITreerowUpdater {}
}
