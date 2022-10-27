/* ICenter.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:22:24 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Center;

/**
 * Immutable {@link Center} component
 *
 * <p>
 * A center region of a borderlayout.
 * <strong>Note:</strong> This component doesn't support the following method,
 * including {@link #withSplittable(boolean)}, {@link #withOpen(boolean)},
 * {@link #withCollapsible(boolean)}, {@link #withMaxsize(int)},
 * {@link #withMinsize(int)}, {@link #withHeight(String)},
 * {@link #withWidth(String)}, {@link #withSlidable(boolean)}, {@link #withSlide(boolean)},
 * {@link #withClosable(boolean)} and {@link #withVisible(boolean)}.
 * </p>
 * @author katherine
 * @see Center
 */
@StatelessStyle
public interface ICenter<I extends IAnyGroup> extends ILayoutRegion<ICenter<I>>,
		ISingleChildable<ICenter<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICenter<IAnyGroup> DEFAULT = new ICenter.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Center> getZKType() {
		return Center.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.layout.Center"}</p>
	 */
	default String getWidgetClass() {
		return "zul.layout.Center";
	}

	/**
	 * Return the instance with the given child.
	 * @param child The child from any group
	 */
	static <I extends IAnyGroup> ICenter<I> of(I child) {
		return new ICenter.Builder<I>().setChild(child).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ICenter<I> ofId(String id) {
		return new ICenter.Builder<I>().setId(id).build();
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isVisible() {
		return ILayoutRegion.super.isVisible();
	}

	/**
	 * This component can't be hidden.
	 */
	default ICenter withVisible(boolean visible) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isSplittable() {
		return ILayoutRegion.super.isSplittable();
	}

	/**
	 * Center region can't be enabled the split functionality.
	 */
	default ICenter withSplittable(boolean splittable) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isOpen() {
		return ILayoutRegion.super.isOpen();
	}

	/**
	 * Center region can't be closed.
	 */
	default ICenter withOpen(boolean open) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isCollapsible() {
		return ILayoutRegion.super.isCollapsible();
	}

	/**
	 * Center region can't be enabled the collapse functionality.
	 */
	default ICenter withCollapsible(boolean collapsible) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default int getMaxsize() {
		return ILayoutRegion.super.getMaxsize();
	}

	/**
	 * Center region can't be enabled the maxsize.
	 */
	default ICenter withMaxsize(int maxsize) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default int getMinsize() {
		return ILayoutRegion.super.getMinsize();
	}

	/**
	 * Center region can't be enabled the minsize.
	 */
	default ICenter withMinsize(int minsize) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getHeight() {
		return null;
	}

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link INorth} or {@link ISouth}).
	 */
	default ICenter withHeight(@Nullable String height) {
		throw new UnsupportedOperationException("readonly");
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
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link IWest} or {@link IEast}).
	 */
	default ICenter withWidth(@Nullable String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isSlide() {
		return ILayoutRegion.super.isSlide();
	}

	/**
	 * Center region can't be slided.
	 */
	default ICenter withSlide(boolean slide) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	default boolean isSlidable() {
		return ILayoutRegion.super.isSlidable();
	}

	/**
	 * Center region can't be slided.
	 */
	default ICenter withSlidable(boolean slidable) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Builds instances of type {@link ICenter ICenter}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableICenter.Builder<I> {}

	/**
	 * Builds an updater of type {@link ICenter} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ICenterUpdater {}
}