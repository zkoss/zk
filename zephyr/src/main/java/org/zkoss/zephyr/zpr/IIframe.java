/* IIframe.java

	Purpose:

	Description:

	History:
		Wed Nov 03 14:56:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Iframe;

/**
 * Immutable {@link Iframe} component
 *
 * <p>Unlike HTML iframe, this component doesn't have the frameborder
 * property. Rather, use the CSS style to customize the border (like
 * any other components).
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
 *          <td>onURIChange</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.URIData}
 *          <br> Denotes the associated URI (src) has been changed by user.
 *          Use getURI() to retrieve the URI being changed to.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Iframe
 */
@ZephyrStyle
public interface IIframe extends IHtmlBasedComponent<IIframe>, IAnyGroup<IIframe> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IIframe DEFAULT = new IIframe.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Iframe> getZKType() {
		return Iframe.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.utl.Iframe"}</p>
	 */
	default String getWidgetClass() {
		return "zul.utl.Iframe";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkScrolling() {
		String scrolling = getScrolling();
		if (!"true".equals(scrolling) && !"false".equals(scrolling) && !"auto".equals(scrolling)
				&& !"yes".equals(scrolling) && !"no".equals(scrolling))
			throw new WrongValueException("scrolling cannot be " + scrolling);
	}

	/** Returns the source URI.
	 * <p>Default: null.
	 */
	@Nullable
	String getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code src}.
	 *
	 * <p>Sets the source URI of the component.
	 *
	 * @param src The source URI of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IIframe withSrc(@Nullable String src);

	/** Returns the frame name.
	 * <p>Default: {@code null} (use browser default).
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code name}.
	 *
	 * <p>Sets the frame name.
	 *
	 * @param name The frame name
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IIframe withName(@Nullable String name);

	/**
	 * Return the scroll bars.
	 * <p>Default: {@code "auto"}
	 */
	default String getScrolling() {
		return "auto";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code scrolling}.
	 *
	 * <p>Sets the scroll bars.
	 *
	 * @param scrolling The scroll bars. {@code "true"}, {@code "false"}, {@code "yes"},
	 * {@code "no"} or {@code "auto"}
	 * <p>Default: {@code "auto"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IIframe withScrolling(String scrolling);

	/** Returns whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 *
	 * <p>Default: {@code false}.
	 *
	 * <p>If an iframe contains PDF or other non-HTML resource,
	 * it is possible that it obscures the popup that shall be shown
	 * above it. To resolve this, you have to specify {@code withAutohide(true)}
	 * to this component, and specify the following in the page:
	 * <p>Please refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Stackup_and_Shadow">Stackup and Shadow</a>
	 * for more information.
	 */
	default boolean isAutohide() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autohide}.
	 *
	 * <p>Sets whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 *
	 * <p>If an iframe contains PDF or other non-HTML resource,
	 * it is possible that it obscures the popup that shall be shown
	 * above it. To resolve this, you have to specify {@code withAutohide(true)}
	 * to this component, and specify the following in the page:
	 * <p>Please refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Stackup_and_Shadow">Stackup and Shadow</a>
	 * for more information.
	 *
	 * @param autohide Whether to automatically hide this component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IIframe withAutohide(boolean autohide);

	/**
	 * Returns the instance with the given src.
	 * @param src The source URI of the component
	 */
	static <I extends IAnyGroup> IIframe of(String src) {
		return new IIframe.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given width and height.
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static <I extends IAnyGroup> IIframe ofSize(String width, String height) {
		return new IIframe.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IIframe ofId(String id) {
		return new IIframe.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		render(renderer, "src", Executions.getCurrent().encodeURL(getSrc()));
		String _scrolling = getScrolling();
		if (!"auto".equals(_scrolling))
			render(renderer, "scrolling", _scrolling);
		render(renderer, "name", getName());
		render(renderer, "autohide", isAutohide());
	}

	/**
	 * Builds instances of type {@link IIframe IIframe}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIIframe.Builder {
	}

	/**
	 * Builds an updater of type {@link IIframe} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IIframeUpdater {}
}