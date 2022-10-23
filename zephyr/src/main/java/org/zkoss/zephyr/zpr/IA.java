/* IA.java

	Purpose:

	Description:

	History:
		Wed Oct 06 09:42:55 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.A;

/**
 * Immutable {@link A} component
 *
 * <p>The A component with its {@code href} attribute,
 * creates a hyperlink to web pages, files, email addresses, locations in the same page, or anything else a URI can address.
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
 *          <td>onFocus</td>
 *          <td>Represents an action triggered when a component has received focus.</td>
 *       </tr>
 *       <tr>
 *          <td>onBlur</td>
 *          <td>Represents an action triggered when a component has lost focus.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <br>
 * For example, suppose you wants to create a hyperlink,
 * then you can use the IA components as follows.
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IA.of("Visit ZK!").withHref("https://www.zkoss.org");
 * }
 * </code>
 * </pre>
 *
 * @author katherine
 * @see A
 */

@ZephyrStyle
public interface IA<I extends IAnyGroup> extends ILabelImageElement<IA<I>>,
		IChildable<IA<I>, I>, IAnyGroup<IA<I>> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	IA<IAnyGroup> DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @return
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<A> getZKType() {
		return A.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.A"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.wgt.A";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkDir() {
		String dir = getDir();
		if (!"normal".equals(dir) && !"reverse".equals(dir)) {
			throw new UiException("getDir() should be 'normal' or 'reverse'");
		}
	}

	/**
	 * Returns the {@code direction}.
	 * <p>Default: {@code "normal"}.
	 */
	default String getDir() {
		return "normal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code dir}.
	 *
	 * <p>Sets the direction to layout with image.
	 * @param dir Either {@code "normal"} or {@code "reverse"}.
	 * @return A modified copy of the {@code this} object
	 */
	IA<I> withDir(String dir);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code dir}.
	 *
	 * <p>Sets the direction to layout with image.
	 * @param dir Either {@link Direction#NORMAL} or {@link Direction#REVERSE}.
	 * @return A modified copy of the {@code this} object
	 */
	default IA<I> withDir(Direction dir) {
		return withDir(dir.value);
	}

	/**
	 * Returns whether it is disabled.
	 * <p>Default: {@code false}.
	 */
	default boolean isDisabled() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code disabled}.
	 *
	 * <p>Sets whether it is disabled.
	 * @return A modified copy of the {@code this} object
	 */
	IA<I> withDisabled(boolean disabled);

	/**
	 * Returns a list of component IDs that shall be disabled when the user
	 * clicks this anchor.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getAutodisable();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autodisable}.
	 *
	 * <p>Sets a list of component IDs that shall be disabled when the user
	 * clicks this anchor.
	 *
	 * <p>To represent the anchor itself, the developer can specify <code>self</code>.
	 * For example,
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/autodisable")
	 * public IComponent autodisable() {
	 *     return IA.ofId("ok").withLabel("OK").withAutodisable("self,cancel");
	 * }
	 * </code>
	 * </pre>
	 * is the same as
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/autodisable")
	 * public IComponent autodisable() {
	 *     return IA.ofId("ok").withLabel("OK").withAutodisable("ok,cancel");
	 * }
	 * </code>
	 * </pre>
	 * that will disable
	 * both the ok and cancel anchor when a user clicks it.
	 *
	 * <p>The anchor being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if an anchor is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/autodisable")
	 * public IComponent autodisable() {
	 *     return IHlayout.of(
	 *             IA.ofId("ok").withLabel("OK(action)").withAutodisable("self,+cancel").withAction(this::control),
	 *             IA.ofId("cancel").withLabel("CANCEL")
	 *     );
	 * }
	 * </code>
	 * </pre>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre>
	 * <code>{@literal @}{@code Action}(type = Events.ON_BLUR)
	 * public void control() {
	 *     UiAgent.getCurrent().smartUpdate(Locator.ofId("cancel"), new IA.Updater().disabled(false));
	 * }
	 * </code>
	 * </pre>
	 *
	 * <p>Default: {@code null}.
	 * @return A modified copy of the {@code this} object
	 */
	IA<I> withAutodisable(@Nullable String autodisable);

	/**
	 * Returns the target frame or window.
	 *
	 * <p>Note: It is useful only if {@code href} ({@link #withHref}) is specified.
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getTarget();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code target}.
	 *
	 * <p>Sets the target frame or window.
	 * @param target The name of the frame or window to hyperlink.
	 * @return A modified copy of the {@code this} object
	 */
	IA<I> withTarget(@Nullable String target);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code target}.
	 *
	 * <p>Sets the target frame or window.
	 * @param target The {@link Target} of the frame or window to hyperlink.
	 * @return A modified copy of the {@code this} object
	 */
	default IA<I> withTarget(Target target) {
		return withTarget(target.value);
	}

	/**
	 * Returns the URI that the hyperlink points to.
	 * <p>Default: {@code null}. If {@code null}, the hyperlink has no function unless you
	 * specify the {@link #withAction(org.zkoss.zephyr.util.ActionHandler)}.
	 */
	@Nullable
	String getHref();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code href}.
	 *
	 * <p>Sets the URI that the hyperlink points to.
	 * The {@code href} attribute is not restricted to HTTP-based URLs.
	 * For example,
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/example")
	 * public IComponent example() {
	 *     return IA.of("Visit ZK!).withHref(https://www.zkoss.org");
	 * }
	 *
	 * {@literal @}{@code RichletMapping}("/uri")
	 * public IComponent uri() {
	 *     return IHlayout.of(
	 *         IA.of("jump to example (slash)).withHref(/essential_components/ia/example"),
	 *         IA.of("jump to example).withHref(example")
	 *     );
	 * }
	 * </code>
	 * </pre>
	 * If the URI starts with "/", ZK will encode it with the application's context path.
	 * Otherwise, the path is relative to the path given by Desktop.getDirectory().
	 * @return A modified copy of the {@code this} object
	 */
	IA<I> withHref(@Nullable String href);

	/**
	 * Returns the instance with the given label.
	 * @param label The label of this anchor component.
	 */
	static <I extends IAnyGroup> IA<I> of(String label) {
		return new IA.Builder<I>().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id of this anchor component.
	 */
	static <I extends IAnyGroup> IA<I> ofId(String id) {
		return new IA.Builder<I>().setId(id).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IA<I> of(Iterable<? extends I> children) {
		return new Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IA<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		ILabelImageElement.super.renderProperties(renderer);

		String s;
		if (!"normal".equals(s = getDir()))
			render(renderer, "dir", s);

		render(renderer, "disabled", isDisabled());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = Executions.getCurrent().encodeURL(getHref()));
		render(renderer, "target", getTarget());

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	/**
	 * Builds instances of type {@link IA IA}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIA.Builder<I> {}


	/**
	 * Builds an updater of type {@link IA} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IAUpdater {}

	/**
	 * Insert Adjacent {@code direction} for {@link #withDir(Direction)}
	 */
	enum Direction {
		/**
		 * Layout the image before the label.
		 */
		NORMAL("normal"),
		/**
		 * Layout the image after the label.
		 */
		REVERSE("reverse");

		private final String value;

		Direction(String value) {
			this.value = value;
		}
	}

	/**
	 * Insert Adjacent {@code target} for {@link #withTarget(Target)}}
	 */
	enum Target {
		/**
		 * The current browsing context.
		 */
		SELF("_self"),
		/**
		 * Usually a new tab, but users can configure browsers to open a new window instead.
		 */
		BLANK("_blank"),
		/**
		 * The parent browsing context of the current one. If no parent, behaves as {@link Target#SELF}.
		 */
		PARENT("_parent"),
		/**
		 * The topmost browsing context (the "highest" context that’s an ancestor of the current one). If no ancestors, behaves as {@link Target#SELF}.
		 */
		_TOP("_top");

		private final String value;

		Target(String value) {
			this.value = value;
		}
	}
}
