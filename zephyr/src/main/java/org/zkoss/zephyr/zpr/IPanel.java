/* IPanel.java

	Purpose:

	Description:

	History:
		Thu Oct 21 10:29:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Panel;

/**
 * Immutable {@link Panel} component
 *
 * <p> Panel is a container that has specific functionality and structural components
 * that make it the perfect building block for application-oriented user interfaces.
 * The Panel contains bottom, top, and foot toolbars, along with separate header,
 * footer and body sections. It also provides built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool
 * buttons that can be wired up to provide other customized behavior. Panels can
 * be easily embedded into any kind of {@link IComponent} component that is allowed
 * to have children or layout component. Panels also provide specific features like
 * float and move. Unlike {@link IWindow}, Panels can only be floated and moved
 * inside its parent node, which is not using {@code zk.setVParent()} function at client
 * side. In other words, if Panel's parent node is an relative position, the floated
 * panel is only inside its parent, not the whole page.
 *
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
 *          <td>onMove</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.MoveData}
 *          <br>Denotes the position of the window is moved by a user.</td>
 *       </tr>
 *       <tr>
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.OpenData}
 *          <br>Denotes user has opened or closed a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMaximize</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.MaximizeData}
 *          <br>Denotes user has maximize a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMinimize</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.MinimizeData}
 *          <br>Denotes user has minimize a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onClose</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.OpenData}
 *          <br>Denotes the close button is pressed by a user, and the
 *          component shall detach itself.</td>
 *       </tr>
 *       <tr>
 *          <td>onSize</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.SizeData}
 *          <br>Denotes the panel's size is updated by a user.</td>
 *       </tr>
 *       <tr>
 *          <td>onZIndex</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.ZIndexData}
 *          <br>Denotes the panel's zindex is updated by a user.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Application Library Properties</h3>
 *
 * <ul>
 * <li>
 * <p>To set to use Browser's scrollbar or not, you have to specify {@link #withNativeScrollbar(boolean)}.
 * </p>
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.nativebar&lt;/name/&gt;
 *     &lt;value&gt;false&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * </li>
 *</ul>
 *
 * @author katherine
 * @see Panel
 */
@ZephyrStyle
public interface IPanel extends IXulElement<IPanel>,
		IAnyGroup<IPanel>, IComposite<IChildrenOfPanel> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IPanel DEFAULT = new IPanel.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Panel> getZKType() {
		return Panel.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wnd.Panel"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wnd.Panel";
	}

	/** Returns the caption of this panel.
	 * <p>Default: {@code null}</p>
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
	IPanel withCaption(@Nullable ICaption caption);

	/**
	 * Returns the top toolbar of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IToolbar getTopToolbar();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code topToolbar}.
	 *
	 * <p>Sets the top toolbar of this component.
	 *
	 * @param topToolbar The top toolbar of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withTopToolbar(@Nullable IToolbar topToolbar);

	/**
	 * Returns the bottom toolbar of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IToolbar getBottomToolbar();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code bottomToolbar}.
	 *
	 * <p>Sets the bottom toolbar of this component.
	 *
	 * @param bottomToolbar The bottom toolbar of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withBottomToolbar(@Nullable IToolbar bottomToolbar);

	/**
	 * Returns the foot toolbar of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IToolbar getFootToolbar();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code footToolbar}.
	 *
	 * <p>Sets the foot toolbar of this component.
	 *
	 * @param footToolbar The foot toolbar of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withFootToolbar(@Nullable IToolbar footToolbar);

	/**
	 * Returns the panelchildren of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	IPanelchildren<? extends IAnyGroup> getPanelchildren();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code panelchildren}.
	 *
	 * <p>Sets the panelchildren of this component to contain more child components.
	 *
	 * @param panelchildren The panelchildren of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withPanelchildren(@Nullable IPanelchildren<? extends IAnyGroup> panelchildren);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMaximized() {
		if (isMaximized() && !isMaximizable()) {
			throw new UiException("Not maximizable.");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMinimized() {
		if (isMinimized() && !isMinimizable()) {
			throw new UiException("Not minimizable.");
		}
	}

	/**
	 * Internal use for {@link IComposite}
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<IChildrenOfPanel> getAllComponents() {
		ArrayList<IChildrenOfPanel> components = new ArrayList<>();
		ICaption caption = getCaption();

		// as first child
		if (caption != null) {
			components.add(caption);
		}
		IToolbar topToolbar = getTopToolbar();
		if (topToolbar != null) {
			components.add(topToolbar);
		}
		IPanelchildren panelchildren = getPanelchildren();
		if (panelchildren != null) {
			components.add(panelchildren);
		}
		IToolbar bottomToolbar = getBottomToolbar();
		if (bottomToolbar != null) {
			components.add(bottomToolbar);
		}
		IToolbar footToolbar = getFootToolbar();
		if (footToolbar != null) {
			components.add(footToolbar);
		}
		return components;
	}

	/**
	 * Returns the title.
	 * Besides this attribute, you could use {@link ICaption} to define
	 * a more sophisticated caption (a.k.a., title).
	 * <p>If a panel has a caption whose label ({@link ICaption#getLabel})
	 * is not empty, then this attribute is ignored.
	 * <p>Default: {@code ""} (empty).
	 */
	default String getTitle() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code title}.
	 *
	 * <p>Sets the title of this component.
	 *
	 * @param title The title of this component.
	 * <p>Default: {@code ""} (empty).</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withTitle(String title);

	/**
	 * Returns whether to show a close button on the title bar.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isClosable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closable}.
	 *
	 * <p>Sets whether to show a close button on the title bar.
	 *
	 * @param closable Whether to show a close button on the title bar.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withClosable(boolean closable);

	/**
	 * Returns whether to float the panel to display it inline where it is rendered.
	 * <p>Default: {@code false}.
	 */
	default boolean isFloatable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code floatable}.
	 *
	 * <p>Sets whether to float the panel to display it inline where it is rendered.
	 *
	 * @param floatable Whether to float the panel to display it inline where it is rendered.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withFloatable(boolean floatable);

	/**
	 * Returns whether to show a toggle button on the title bar.
	 * <p>Default: {@code false}.
	 */
	default boolean isCollapsible() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code collapsible}.
	 *
	 * <p>Sets whether to show a toggle button on the title bar.
	 *
	 * @param collapsible Whether to show a toggle button on the title bar.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withCollapsible(boolean collapsible);

	/**
	 * Returns whether to move the panel to display it inline where it is rendered.
	 * <p>Default: {@code false}.
	 */
	default boolean isMovable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code movable}.
	 *
	 * <p>Sets whether to move the panel to display it inline where it is rendered.
	 *
	 * @param movable Whether to move the panel to display it inline where it is rendered.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMovable(boolean movable);

	/**
	 * Returns whether to display the maximizing button and allow the user to maximize
	 * the panel.
	 * <p>Default: {@code false}.
	 */
	default boolean isMaximizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maximizable}.
	 *
	 * <p>Sets whether to display the maximizing button and allow the user to maximize
	 * the panel.
	 *
	 * @param maximizable Whether to display the maximizing button and allow the user to maximize
	 * the panel.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMaximizable(boolean maximizable);

	/**
	 * Returns whether to display the minimizing button and allow the user to minimize
	 * the panel.
	 * <p>Default: {@code false}.
	 */
	default boolean isMinimizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minimizable}.
	 *
	 * <p>Sets whether to display the minimizing button and allow the user to minimize
	 * the panel.
	 *
	 * @param minimizable Whether to display the minimizing button and allow the user to minimize
	 * the panel.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMinimizable(boolean minimizable);

	/**
	 * Returns whether the panel is maximized.
	 */
	default boolean isMaximized() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maximized}.
	 *
	 * <p>Sets whether the panel is maximized, and then the size of the panel will depend
	 * on it to show an appropriate size. In other words, if true, the size of the
	 * panel will count on the size of its offset parent node whose position is
	 * absolute (by {@link #isFloatable()}) or its parent node. Otherwise, its size
	 * will be original size. Note that the maximized effect will run at client's
	 * sizing phase not initial phase.
	 *
	 * @param maximized Whether the panel is maximized.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMaximized(boolean maximized);

	/**
	 * Returns whether the panel is minimized.
	 * <p>Default: {@code false}.
	 */
	default boolean isMinimized() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minimized}.
	 *
	 * <p>Sets whether the panel is minimized.
	 *
	 * @param minimized Whether the panel is minimized.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMinimized(boolean minimized);

	/** Returns whether the panel is sizable.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isSizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code sizable}.
	 *
	 * <p>Sets whether the panel is sizable.
	 *
	 * @param sizable Whether the panel is sizable.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withSizable(boolean sizable);

	/**
	 * Returns the minimum height.
	 * <p>Default: {@code 100}.
	 */
	default int getMinheight() {
		return 100;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minheight}.
	 *
	 * <p>Sets the minimum height in pixels allowed for this panel.
	 * If negative, {@code 100} is assumed.
	 *
	 * @param minheight Whether the panel is sizable.
	 * <p>Default: {@code 100}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMinheight(int minheight);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IPanel checkMinheight() {
		int minheight = getMinheight();
		if (minheight < 0) {
			return withMinheight(100);
		}
		return this;
	}

	/**
	 * Returns the minimum width.
	 * <p>Default: {@code 200}.
	 */
	default int getMinwidth() {
		return 200;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minwidth}.
	 *
	 * <p>Sets the minimum width in pixels allowed for this panel.
	 * If negative, {@code 200} is assumed.
	 *
	 * @param minwidth Whether the panel is sizable.
	 * <p>Default: {@code 200}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withMinwidth(int minwidth);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IPanel checkMinwidth() {
		int minwidth = getMinwidth();
		if (minwidth < 0) {
			return withMinwidth(200);
		}
		return this;
	}

	/**
	 * Returns whether this Panel is open.
	 * <p>Default: {@code true}.
	 */
	default boolean isOpen() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code open}.
	 *
	 * <p>Sets whether this Panel is open.
	 *
	 * @param open Whether this Panel is open.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withOpen(boolean open);

	/** Returns the border.
	 *
	 * <p>Default: {@code "none"}.
	 */
	default String getBorder() {
		return "none";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code border}.
	 *
	 * <p> Sets the border.
	 * Allowed values include <code>none</code> (default), and <code>normal</code>.
	 * For more information, please refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Panel#Border">ZK Component Reference: Panel</a>.
	 * @param border the border. If null, {@code "0"} or {@code "false"}, {@code "none"} is assumed.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	IPanel withBorder(String border);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code border}.
	 *
	 * <p> Sets the border with the given {@link Border border}.
	 * @param border The border
	 *
	 * @return A modified copy of the {@code this} object
	 */
	default IPanel withBorder(Border border) {
		Objects.requireNonNull(border);
		return withBorder(border.value);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IPanel checkBorder() {
		String border = getBorder();
		if (border == null || "0".equals(border) || "false".equals(border)) {
			return withBorder("none");
		} else if ("true".equals(border)) {
			return withBorder("normal");
		}
		switch (border) {
		case "none":
		case "normal":
		case "rounded": // should be deprecated
		case "rounded+": // should be deprecated
			break;
		default:
			throw new WrongValueException("Unknown border: " + border);
		}
		return this;
	}

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
	IPanel withNativeScrollbar(boolean nativeScrollbar);

	/**
	 *
	 * Returns the instance with the given {@link IPanelchildren} child.
	 * @param panelchildren The panelchildren
	 */
	static IPanel of(IPanelchildren panelchildren) {
		return new Builder().setPanelchildren(panelchildren).build();
	}

	/**
	 * Returns the instance with the given caption.
	 * @param caption The caption child
	 */
	static IPanel ofCaption(ICaption caption) {
		return new Builder().setCaption(caption).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link Border} border.
	 * @param border The border of the component
	 */
	static IPanel ofBorder(Border border) {
		return new Builder().setBorder(border.value).build();
	}


	/**
	 *
	 * Returns the instance with the given top {@link IToolbar} child.
	 * @param topToolbar The top toolbar
	 */
	static IPanel ofTopToolbar(IToolbar topToolbar) {
		return new Builder().setTopToolbar(topToolbar).build();
	}

	/**
	 *
	 * Returns the instance with the given bottom {@link IToolbar} child.
	 * @param bottomToolbar The bottom toolbar
	 */
	static IPanel ofBottomToolbar(IToolbar bottomToolbar) {
		return new Builder().setBottomToolbar(bottomToolbar).build();
	}

	/**
	 *
	 * Returns the instance with the given foot {@link IToolbar} child.
	 * @param footToolbar The foot toolbar
	 */
	static IPanel ofFootToolbar(IToolbar footToolbar) {
		return new Builder().setFootToolbar(footToolbar).build();
	}

	/**
	 * Returns the instance with the given vflex.
	 * @param vflex The vertical flex hint.
	 */
	static IPanel ofVflex(String vflex) {
		return new IPanel.Builder().setVflex(vflex).build();
	}

	/**
	 * Returns the instance with the given title.
	 * @param title The title of the component.
	 */
	static IPanel ofTitle(String title) {
		return new IPanel.Builder().setTitle(title).build();
	}

	/**
	 * Returns the instance with the given size, width and height
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static IPanel ofSize(String width, String height) {
		return new IPanel.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IPanel ofId(String id) {
		return new IPanel.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		String _title = getTitle();
		if (_title != null && _title.length() > 0)
			render(renderer, "title", _title);

		render(renderer, "closable", isClosable());
		render(renderer, "floatable", isFloatable());
		render(renderer, "collapsible", isCollapsible());
		render(renderer, "movable", isMovable());
		render(renderer, "maximizable", isMaximizable());
		render(renderer, "minimizable", isMinimizable());
		render(renderer, "maximized", isMaximized());
		render(renderer, "minimized", isMinimized());
		render(renderer, "sizable", isSizable());

		int _minheight = getMinheight();
		if (_minheight != 100)
			renderer.render("minheight", _minheight);
		int _minwidth = getMinwidth();
		if (_minwidth != 200)
			renderer.render("minwidth", _minwidth);

		if (!isOpen())
			renderer.render("open", false);

		String _border = getBorder();
		if (!"none".equals(_border))
			renderer.render("border", _border);

		//ZK-3678: Provide a switch to enable/disable iscroll
		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);
	}

	/**
	 * Builds instances of type {@link IPanel IPanel}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIPanel.Builder {}

	/**
	 * Builds an updater of type {@link IPanel} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IPanelUpdater {}

	/**
	 * Specifies the border to {@link IPanel} component.
	 */
	enum Border {
		/**
		 * The normal border.
		 */
		NORMAL("normal"),
		/**
		 * None border
		 */
		NONE("none");

		final String value;
		Border(String value) {
			this.value = value;
		}
	}
}