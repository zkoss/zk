/* UiAgent.java

	Purpose:
		
	Description:
		
	History:
		12:28 PM 2021/10/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.function.CheckedConsumer;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * Utilities to manipulate {@link IComponent} to the client.
 *
 * @author jumperchen
 */
public interface UiAgent {

	/**
	 * Insert Adjacent position for {@link #insertAdjacentComponent(Locator, Position, IComponent)}
	 */
	enum Position {
		/**
		 * Before the targetComponent itself.
		 */
		beforeBegin,
		/**
		 * Just inside the targetComponent, after its last child.
		 */
		beforeEnd,
		/**
		 * Just inside the targetComponent, before its first child.
		 */
		afterBegin,
		/**
		 * After the targetComponent itself.
		 */
		afterEnd
	}

	/**
	 * Adds the specified child component as the last child to the current locator.
	 */
	<I extends IComponent> UiAgent appendChild(Locator locator, I newChild);

	/**
	 * Inserts the given newChild just inside the locator, before its n-th child.
	 */
	<I extends IComponent> UiAgent insertBefore(Locator locator, I newChild, int childIndex);

	/**
	 * Replaces the given newChild just inside the locator with its n-th child.
	 */
	<I extends IComponent> UiAgent replaceChild(Locator locator, I newChild, int childIndex);

	/**
	 * Replaces the given locator component in the children list of its parent with a given new {@link IComponent}.
	 */
	<I extends IComponent> UiAgent replaceWith(Locator locator, I newComp);

	/**
	 * Removes the existing children of the given locator component.
	 */
	<I extends IComponent> UiAgent replaceChildren(Locator locator);

	/**
	 * Replaces the existing children of the given locator component with a specified
	 * new set of children {@link IComponent}s.
	 */
	<I extends IComponent> UiAgent replaceChildren(Locator locator, I... children);

	/**
	 * Replaces the existing children of the given locator component with a specified
	 * new set of children {@link IComponent}s.
	 */
	<I extends IComponent> UiAgent replaceChildren(Locator locator, List<I> children);

	/**
	 * Inserts a given newChild at a given {@link Position} relative to the locator component
	 * it is invoked upon.
	 */
	<I extends IComponent> UiAgent insertAdjacentComponent(Locator locator, Position position, I newChild);

	/**
	 * Inserts a given newChild before the locator itself.
	 * @see #insertAdjacentComponent(Locator, Position, IComponent)
	 */
	<I extends IComponent> UiAgent insertBeforeBegin(Locator locator, I newChild);

	/**
	 * Inserts a given newChild just inside the locator after its last child.
	 * The same as {@link #appendChild(Locator, IComponent)}.
	 * @see #insertAdjacentComponent(Locator, Position, IComponent)
	 */
	<I extends IComponent> UiAgent insertBeforeEnd(Locator locator, I newChild);

	/**
	 * Inserts a given newChild just inside the locator before its first child.
	 * @see #insertAdjacentComponent(Locator, Position, IComponent)
	 */
	<I extends IComponent> UiAgent insertAfterBegin(Locator locator, I newChild);

	/**
	 * Inserts a given newChild after the locator itself.
	 * @see #insertAdjacentComponent(Locator, Position, IComponent)
	 */
	<I extends IComponent> UiAgent insertAfterEnd(Locator locator, I newChild);

	/**
	 * A special smart update to update all the new data in the given updater.
	 */
	UiAgent smartUpdate(Locator locator, SmartUpdater updater);

	/**
	 * Removes the given locator component.
	 * @param locator
	 */
	UiAgent remove(Locator locator);

	/**
	 * Returns whether this execution of UiAgent is activated.
	 * @return
	 */
	boolean isActivated();

	/**
	 * Runs the given callback that is asynchronously completed by retrieving
	 * a new {@link UiAgent} instance in a CompletableFuture.
	 */
	CompletableFuture<UiAgent> runAsync(CheckedConsumer<UiAgent> consumer);

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by retrieving
	 * a new {@link UiAgent} instance in a CompletableFuture.
	 */
	CompletableFuture<UiAgent> ofAsync();

	/**
	 * Returns a new UiAgent from the given execution, which is activated.
	 */
	static UiAgent of(Execution execution) {
		Objects.requireNonNull(execution);
		if (!((ExecutionCtrl)execution).isActivated()) {
			throw new IllegalStateException("The execution is not activated.");
		}
		return UiAgentImpl.of(execution);
	}

	/**
	 * Returns the current UiAgent if any.
	 */
	static UiAgent getCurrent() {
		return of(Executions.getCurrent());
	}
}
