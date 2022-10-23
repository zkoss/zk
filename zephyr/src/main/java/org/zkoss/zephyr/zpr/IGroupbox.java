/* IGroupbox.java

	Purpose:

	Description:

	History:
		Wed Oct 20 17:03:28 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Groupbox;

/**
 * Immutable {@link Groupbox} component.
 *
 * <p>A group box is used to group components together. A border is typically
 * drawn around the components to show that they are related. The label across
 * the top of the group box can be created by using {@link ICaption Caption} component.
 * It works much like the HTML legend element. Unlike {@link IWindow}, a group box
 * cannot be overlapped or popup.</p>
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
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.OpenData}
 *          <br>Denotes user has opened or closed a component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 *
 * <h3>Support Molds</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "default"}</td>
 *          <td><img src="doc-files/IGroupbox_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "3d"}</td>
 *          <td><img src="doc-files/IGroupbox_mold_3d.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Groupbox
 */
@ZephyrStyle
public interface IGroupbox<I extends IAnyGroup> extends IXulElement<IGroupbox<I>>,
		IChildable<IGroupbox<I>, I>, IAnyGroup<IGroupbox<I>>, IComposite<IAnyGroup> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IGroupbox<IAnyGroup> DEFAULT = new IGroupbox.Builder().build();

	/**
	 * Constant for 3d attributes of this immutable component.
	 */
	IGroupbox<IAnyGroup> THREE_D = new IGroupbox.Builder().setMold("3d").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Groupbox> getZKType() {
		return Groupbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Groupbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Groupbox";
	}

	/** Returns the caption child of this groupbox.
	 */
	@Nullable
	ICaption getCaption();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code caption}.
	 *
	 * <p>Sets the caption child of this component.
	 *
	 * @param caption The caption child of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withCaption(@Nullable ICaption caption);

	/** Returns the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not {@code "default"}.
	 */
	@Nullable
	String getContentStyle();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code contentStyle}.
	 *
	 * <p>Sets the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not {@code "default"}.
	 *
	 * @param contentStyle The CSS style for the content block of the groupbox.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withContentStyle(@Nullable String contentStyle);

	/** Returns the style class used for the content block of the groupbox.
	 * Used only if {@link #getMold} is not {@code "default"}.
	 */
	@Nullable
	String getContentSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code contentSclass}.
	 *
	 * <p>Sets the style class used for the content block of the groupbox.
	 *
	 * @param contentSclass The style class used for the content block of the groupbox
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withContentSclass(@Nullable String contentSclass);

	/** Returns the title.
	 * Besides this attribute, you could use {@link ICaption} to define
	 * a more sophisticated caption (a.k.a., title).
	 * <p> It will be displayed before {@code caption} if both are applied.
	 * <p>Default: {@code ""} (empty).
	 */
	default String getTitle() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code title}.
	 *
	 * <p>Sets the title.
	 * Besides this attribute, you could use {@link ICaption} to define
	 * a more sophisticated caption (a.k.a., title).
	 * <p> It will be displayed before {@code caption} if both are applied.
	 *
	 * @param title The title of the component.
	 * <p>Default: {@code ""} (empty).</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withTitle(String title);

	/** Returns whether this groupbox is open.
	 *
	 * <p>Default: {@code true}.
	 */
	default boolean isOpen() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code open}.
	 *
	 * <p>Sets to open or close this groupbox.
	 *
	 * @param open Whether to open or close this groupbox
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withOpen(boolean open);

	/** Returns whether user can open or close the group box.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking on the title).
	 *
	 * <p>Default: {@code true}.
	 */
	default boolean isClosable() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closable}.
	 *
	 * <p>Sets whether user can open or close the group box.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking on the title).
	 *
	 * @param closable Whether user can open or close the group box.
	 *
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox<I> withClosable(boolean closable);

	/**
	 * Returns whether to use Browser's scrollbar or a floating scrollbar (if with {@code false}).
	 * <p>Default: {@code true} to use Browser's scrollbar, if the {@code "org.zkoss.zul.nativebar"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isNativeScrollbar() {
		return Boolean.parseBoolean(
				Library.getProperty("org.zkoss.zul.nativebar", "true"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code nativeScrollbar}.
	 *
	 * <p>Sets to use Browser's scrollbar or a floating scrollbar</p>
	 * @param nativeScrollbar {@code true} to use Browser's scrollbar, or {@code false} to
	 * use a floating scrollbar.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IGroupbox withNativeScrollbar(boolean nativeScrollbar);

	/**
	 * Internal use for {@link IComposite}
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IAnyGroup> getAllComponents() {
		ArrayList<IAnyGroup> components = new ArrayList<>();
		ICaption caption = getCaption();

		// as first child
		if (caption != null) {
			components.add(caption);
		}
		List<I> children = getChildren();
		if (children != null) {
			// ignore Caption child if any.
			components.addAll(
					children.stream().filter((c) -> !(c instanceof ICaption))
							.collect(Collectors.toList()));
		}
		return components;
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IGroupbox<I> of(Iterable<? extends I> children) {
		return new IGroupbox.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IGroupbox<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given caption.
	 * @param caption The caption child
	 */
	static <I extends IAnyGroup> IGroupbox<I> ofCaption(ICaption caption) {
		return new IGroupbox.Builder<I>().setCaption(caption).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IGroupbox<I> ofId(String id) {
		return new IGroupbox.Builder().setId(id).build();
	}

	/**
	 * Returns the instance with the given title.
	 * @param title The title of the groupbox.
	 */
	static <I extends IAnyGroup> IGroupbox<I> ofTitle(String title) {
		return new IGroupbox.Builder().setTitle(title).build();
	}

	/**
	 * Returns the instance with the given size, width and height
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static <I extends IAnyGroup> IGroupbox<I> ofSize(String width, String height) {
		return new IGroupbox.Builder<I>().setWidth(width).setHeight(height).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "contentStyle", getContentStyle());
		render(renderer, "contentSclass", getContentSclass());
		render(renderer, "title", getTitle());
		if (!isOpen())
			renderer.render("open", false);
		if (!isClosable())
			renderer.render("closable", false);
		//ZK-3678: Provide a switch to enable/disable iscroll
		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);
	}

	/**
	 * Builds instances of type {@link IGroupbox IGroupbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIGroupbox.Builder {}

	/**
	 * Builds an updater of type {@link IGroupbox} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IGroupboxUpdater {}
}