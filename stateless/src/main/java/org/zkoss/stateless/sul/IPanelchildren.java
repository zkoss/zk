/* IPanelchildren.java

	Purpose:

	Description:

	History:
		Thu Oct 21 11:44:09 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Panelchildren;

/**
 * Immutable {@link Panelchildren} component
 *
 * <p> Panelchildren is used for {@link IPanel} component to manage each child who will
 * be shown in the body of Panel.
 * <b>Note</b> that the size of Panelchildren is automatically calculated by Panel so
 * {@link #withWidth(String)}, {@link #withHeight(String)}, {@link #withVflex(String)}
 * and {@link #withHflex(String)} are read-only.
 *
 * </p>
 * @author katherine
 * @see Panelchildren
 */
@StatelessStyle
public interface IPanelchildren<I extends IAnyGroup> extends IXulElement<IPanelchildren<I>>,
		IChildable<IPanelchildren<I>, I>, IChildrenOfPanel<IPanelchildren<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IPanelchildren<IAnyGroup> DEFAULT = new IPanelchildren.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkSize() {
		if (getWidth() != null) {
			throw new UnsupportedOperationException("Width is readonly");
		}
		if (getHeight() != null) {
			throw new UnsupportedOperationException("Height is readonly");
		}
		if (getHflex() != null) {
			throw new UnsupportedOperationException("Hflex is readonly, use IPanel.withHflex() instead.");
		}
		if (getVflex() != null) {
			throw new UnsupportedOperationException("Vflex is readonly, use IPanel.withVflex() instead.");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Panelchildren> getZKType() {
		return Panelchildren.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wnd.Panelchildren"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wnd.Panelchildren";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IPanelchildren<I> of(Iterable<? extends I> children) {
		return new IPanelchildren.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IPanelchildren<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IPanelchildren<I> ofId(String id) {
		return new IPanelchildren.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IPanelchildren IPanelchildren}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIPanelchildren.Builder<I> {}

	/**
	 * Builds an updater of type {@link IPanelchildren} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IPanelchildrenUpdater {}
}