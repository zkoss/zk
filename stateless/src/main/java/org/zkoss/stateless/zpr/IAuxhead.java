/* IAuxhead.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:06:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Auxhead;

/**
 * Immutable {@link Auxhead} component
 * <p>Used to define a collection of auxiliary headers</p>
 * @author katherine
 * @see Auxhead
 */
@StatelessStyle
public interface IAuxhead extends IXulElement<IAuxhead>,
		IChildable<IAuxhead, IAuxheader>, IMeshComposite<IAuxhead> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IAuxhead DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Auxhead> getZKType() {
		return Auxhead.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.mesh.Auxhead"}</p>
	 */
	default String getWidgetClass() {
		return "zul.mesh.Auxhead";
	}

	/**
	 *
	 * Returns the instance with the given {@link IAuxheader} children.
	 * @param children The children of {@link IAuxheader}
	 */
	static IAuxhead of(Iterable<? extends IAuxheader> children) {
		return new IAuxhead.Builder().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link IAuxheader} children.
	 * @param children The children of {@link IAuxheader}
	 */
	static IAuxhead of(IAuxheader... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IAuxhead ofId(String id) {
		return new Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IAuxhead IAuxhead}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIAuxhead.Builder {}

	/**
	 * Builds an updater of type {@link IAuxhead} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IAuxheadUpdater {}
}