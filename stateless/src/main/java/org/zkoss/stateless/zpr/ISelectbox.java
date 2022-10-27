/* ISelectbox.java

	Purpose:

	Description:

	History:
		Thu Jan 13 11:59:28 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.SelectData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Selectbox;

/**
 * Immutable {@link Selectbox} component
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
 *          <br> Notifies one that the user has selected a new item in the selectbox.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Selectbox
 */
@StatelessStyle
public interface ISelectbox extends IHtmlBasedComponent<ISelectbox>, IDisable<ISelectbox>, IAnyGroup<ISelectbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISelectbox DEFAULT = new ISelectbox.Builder().build();

	/**
	 * Internal use
	 *
	 * @return
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Selectbox> getZKType() {
		return Selectbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Selectbox"}</p>
	 *
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.wgt.Selectbox";
	}

	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: {@code null}.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code name}.
	 *
	 * <p>Sets the name of this component.
	 *
	 * @param name The name of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISelectbox withName(String name);

	/**
	 * Returns the children of this component.
	 */
	@StatelessOnly
	List<String> getChildren();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code children}.
	 *
	 * <p> Sets the text children.
	 *
	 * @param children Text children.
	 * <p>Default: {@code empty}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISelectbox withChildren(String... children);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code children}.
	 *
	 * <p> Sets the text children.
	 *
	 * @param children A list of text child.
	 * <p>Default: {@code empty}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISelectbox withChildren(Iterable<String> children);

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>
	 * Default: {@code false}.
	 */
	@StatelessOnly
	default boolean isMultiple() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code multiple}.
	 *
	 * <p> Sets whether multiple selections are allowed.
	 *
	 * @param multiple Whether multiple selections are allowed.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISelectbox withMultiple(boolean multiple);

	/**
	 * Returns the maximal length of each item's label.
	 */
	@StatelessOnly
	default int getMaxlength() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxlength}.
	 *
	 * <p> Sets the maximal length of each item's label.
	 *
	 * @param maxlength The maximal length of each item's label.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISelectbox withMaxlength(int maxlength);

	/**
	 * Internal use for Model case
	 * @hidden for Javadoc
	 */
	@StatelessOnly
	default Map<String, Object> getAuxInfo() {
		return Collections.emptyMap();
	}

	/**
	 * Returns the instance with the given text children.
	 * @param children The text children
	 */
	static ISelectbox of(Iterable<String> children) {
		return new ISelectbox.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given text children.
	 * @param children The text children
	 */
	static ISelectbox of(String... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component.
	 */
	static ISelectbox ofId(String id) {
		return new ISelectbox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		render(renderer, "name", getName());
		render(renderer, "disabled", isDisabled());

		List<String> children = getChildren();
		if (!children.isEmpty()) {
			render(renderer, "items", children);
		}

		render(renderer, "_multiple", isMultiple());
		int _maxlength = getMaxlength();
		if (_maxlength > 0)
			renderer.render("_maxlength", _maxlength);

		// ModelInfo includes items, selectedIndex
		Map<String, Object> map = getAuxInfo();
		if (!map.isEmpty()) {
			for (Map.Entry<String, ?> entry: map.entrySet()) {
				renderer.render(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Builds instances of type {@link ISelectbox ISelectbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableISelectbox.Builder {
	}

	/**
	 * Builds an updater of type {@link ISelectbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 *
	 * @see SmartUpdater
	 */
	class Updater extends ISelectboxUpdater {
	}
}