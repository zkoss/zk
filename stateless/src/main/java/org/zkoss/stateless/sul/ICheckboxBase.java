/* ICheckboxBase.java

	Purpose:

	Description:

	History:
		Fri Dec 10 09:29:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Immutable {@link ICheckbox} base component.
 * @author katherine
 */
public interface ICheckboxBase<I extends ICheckboxBase> extends ILabelImageElement<I> {

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMold() {
		String mold = this.getMold();
		if (this.isIndeterminate() && ("switch".equals(mold) || "toggle".equals(mold))) {
			throw new UiException("Checkbox switch/toggle mold does not support indeterminate yet.");
		}
	}

	/** Returns the value.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	Object getValue();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code value}.
	 *
	 * <p>Sets the value of the component.
	 *
	 * @param value The value of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withValue(@Nullable Object value);

	/**
	 * Returns a list of component IDs that shall be disabled when the user
	 * clicks this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getAutodisable();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autodisable}.
	 *
	 * <p>Sets a list of component IDs that shall be disabled when the user
	 * clicks this component.
	 *
	 * @param autodisable The value of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withAutodisable(@Nullable String autodisable);

	/** Returns the name of this component.
	 * <p>Default: {@code null}.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code name}.
	 *
	 * <p>Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name The value of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withName(@Nullable String name);

	/** Sets whether it is disabled.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isDisabled() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code disabled}.
	 *
	 * <p>Sets whether it is disabled.
	 *
	 * @param disabled {@code true} to disable this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withDisabled(boolean disabled);

	/**
	 * Return whether component is in indeterminate state.
	 * Default: {@code false}.
	 *
	 * @return true if the component is indeterminate
	 */
	default boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code indeterminate}.
	 *
	 * <p>Sets whether the component is in indeterminate state.
	 *
	 * @param indeterminate Whether the component is in indeterminate state.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withIndeterminate(boolean indeterminate);

	/** Returns whether it is checked.
	 * <p>Default: {@code false}.
	 */
	default boolean isChecked() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code checked}.
	 *
	 * <p>Sets whether it is checked.
	 *
	 * @param checked Whether it is checked.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withChecked(boolean checked);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);
		Object _value = getValue();
		String _autodisable = getAutodisable();
		boolean _checked = isChecked();

		if (_value != null)
			render(renderer, "value", _value);
		if (_autodisable != null)
			render(renderer, "autodisable", _autodisable);

		render(renderer, "disabled", isDisabled());
		render(renderer, "name", getName());
		render(renderer, "indeterminate", isIndeterminate());
		if (_checked)
			render(renderer, "checked", _checked);
	}
}