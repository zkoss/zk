/* Locator.java

	Purpose:

	Description:

	History:
		6:32 PM 2021/10/12, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.Objects;

import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.function.CheckedConsumer2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;

/**
 * An interface to indicate an opaque reference to the location information of {@link IComponent}s
 * at client side.
 * @author jumperchen
 */
public interface Locator {

	/**
	 * Traverses the locator and its parents until it finds a widget that matches
	 * the provided selector iClass.
	 * Will return the locator of itself or the matching ancestor. If no such widget exists,
	 * it means nothing.
	 * @return
	 */
	Locator closest(Class<? extends IComponent> iClass);

	/**
	 * Converts this locator to ZK Component for {@link org.zkoss.zk.ui.util.Clients}
	 * API to use.
	 * @return
	 */
	Component toComponent();

	/**
	 * Converts this locator to ZK Component for {@link org.zkoss.zk.ui.event.Events#postEvent(Event)}
	 * API to use.
	 */
	Component toComponent(CheckedConsumer2<Event, Scope> eventHandlers);

	/**
	 * Returns the string representation of a specified locator
	 */
	String toExternalForm();

	/**
	 * Returns the first locator that is a descendant of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator find(Class<? extends IComponent> selector);

	/**
	 * Returns the first locator that is a direct child of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator findChild(Class<? extends IComponent> selector);

	/**
	 * Returns the first locator that is the nth child of the locator.
	 * @param nth 0-based
	 * @return
	 */
	Locator child(int nth);

	/**
	 * Returns the previous sibling of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator previousSibling();

	/**
	 * Returns the next sibling of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator nextSibling();

	/**
	 * Returns the first child of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator firstChild();

	/**
	 * Returns the last child of
	 * the locator on which it is invoked that matches the specified class.
	 * @return
	 */
	Locator lastChild();

	/**
	 * Returns a locator to the given {@link IComponent}.
	 * Note: The id of the component cannot be null.
	 * @return
	 */
	static <I extends IComponent> Locator of(I iComponent) {
		Objects.requireNonNull(iComponent.getId(), "ID of IComponent cannot be null");
		return Self.ofId(iComponent.getId());
	}

	/**
	 * Returns a locator to the given {@link Component}.
	 * Note: The uuid of the component cannot be null.
	 * @return
	 */
	static <T extends Component> Locator of(T component) {
		Objects.requireNonNull(component.getUuid(), "Uuid of Component cannot be null");
		return Self.of(component.getUuid());
	}

	/**
	 * Returns a locator to the given uuid.
	 * Note: The uuid cannot be null.
	 * @return
	 */
	static <T extends Component> Locator of(String uuid) {
		Objects.requireNonNull(uuid);
		return Self.of(uuid);
	}

	/**
	 * Returns a locator to the given id of {@link IComponent}.
	 * Note: The id cannot be null.
	 * @return
	 */
	static <T extends Component> Locator ofId(String id) {
		Objects.requireNonNull(id);
		return Self.ofId(id);
	}
}