/* IWindow.java

	Purpose:

	Description:

	History:
		Thu Oct 21 16:25:54 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.action.data.MaximizeData;
import org.zkoss.stateless.action.data.MinimizeData;
import org.zkoss.stateless.action.data.MoveData;
import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.action.data.SizeData;
import org.zkoss.stateless.action.data.ZIndexData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Window;

/**
 * Immutable {@link Window} component
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
 *          <td><strong>ActionData</strong>: {@link MoveData}
 *          <br>Denotes the position of the window is moved by a user.</td>
 *       </tr>
 *       <tr>
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br>Denotes user has opened or closed a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMaximize</td>
 *          <td><strong>ActionData</strong>: {@link MaximizeData}
 *          <br>Denotes user has maximize a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMinimize</td>
 *          <td><strong>ActionData</strong>: {@link MinimizeData}
 *          <br>Denotes user has minimize a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onClose</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br>Denotes the close button is pressed by a user, and the
 *          component shall detach itself.</td>
 *       </tr>
 *       <tr>
 *          <td>onSize</td>
 *          <td><strong>ActionData</strong>: {@link SizeData}
 *          <br>Denotes the panel's size is updated by a user.</td>
 *       </tr>
 *       <tr>
 *          <td>onZIndex</td>
 *          <td><strong>ActionData</strong>: {@link ZIndexData}
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
 * <h2>Modes</h2>
 * <img src="doc-files/IWindow_example.png"/>
 * <h4>Embedded</h4>
 * <p>An embedded window is placed inline with other components.
 * In this mode, you cannot change its position, since the position is decided by the browser.
 * It is the default mode since it is the most common appearance.</p>
 *
 * <h4>Overlapped</h4>
 * <p>An overlapped window is overlapped with other components, such that users could
 * drag it around and developer could set its position by {@link #withLeft(String)}
 * and {@link #withTop(String)}.</p>
 *
 * <h4>Popup</h4>
 * <p>A popup window is similar to {@code overlapped} windows, except it is automatically
 * closed when user clicks on any component other than the popup window itself
 * or any of its descendants. Of course, you could dismiss it manually by making it invisible or detaching it.
 * </p>
 * <p> As its name suggested, it is designed to implement the popup windows.
 * A typical application is to display information that won't obscure the current
 * operation and are easy to close. A popup window is usually position around the focal
 * point (such as a button). It can be done by use of {@link #withPosition(String)} with {@code "parent"}.</p>
 *
 * <h4>Modal and Highlighted</h4>
 * <p>By default, a modal window is the same as a highlighted window in stateless.</p>
 * <p>A modal window provides the so-called modal effect that limits a user from accessing
 * components other than the modal window. Users cannot access anything outside of the modal window,
 * including clicking or tabbing.</p>
 *
 * <h2>Position</h2>
 * <p>In addition to the {@code left} and {@code top} properties, you can control
 * the position of an {@code overlapped}/{@code popup}/{@code modal}/{@code highlighted}
 * window by the use of the position attribute. For example, the following code snippet positions
 * the window to the right-bottom corner.</p>
 * <pre>
 * <code>
 * IWindow.ofMode(Mode.OVERLAPPED).withWidth("300px").withPosition("right,bottom");
 * </code></pre>
 * <p>The value of the {@code position} attribute can be a combination of
 * the following constants by separating them with comma ({@code ,}).</p>
 * <table border="1" cellspacing="0">
 * <tbody><tr>
 * <th><center>Constant</center>
 * </th>
 * <th><center>Description</center>
 * </th></tr>
 * <tr>
 * <td><center>center</center>
 * </td>
 * <td>Position the window at the center. If <code>left</code> or <code>right</code> is also specified, it means the vertical center. If <code>top</code> or <code>bottom</code> is also specified, it means the horizontal center. If none of <code>left</code>, <code>right</code>, <code>top</code> and <code>bottom</code> is specified, it means the center in both directions.
 * <p>Both the <code>left</code> and <code>top</code> property are ignored.
 * </p>
 * </td></tr>
 * <tr>
 * <td><center>left</center>
 * </td>
 * <td>Position the window at the left edge.
 * <p>The <code>left</code> property is ignored.
 * </p>
 * </td></tr>
 * <tr>
 * <td><center>right</center>
 * </td>
 * <td>Position the window at the right edge.
 * <p>The <code>left</code> property is ignored.
 * </p>
 * </td></tr>
 * <tr>
 * <td><center>top</center>
 * </td>
 * <td>Position the window at the top.
 * <p>The <code>top</code> property is ignored.
 * </p>
 * </td></tr>
 * <tr>
 * <td><center>bottom</center>
 * </td>
 * <td>Position the window at the bottom.
 * <p>The <code>top</code> property is ignored.
 * </p>
 * </td></tr></tbody></table>
 *
 * @author katherine
 * @see Window
 */
@StatelessStyle
public interface IWindow<I extends IAnyGroup> extends IXulElement<IWindow<I>>,
		IChildable<IWindow<I>, I>, IAnyGroup<IWindow<I>>, IComposite<IAnyGroup> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IWindow<IAnyGroup> DEFAULT = new IWindow.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Window> getZKType() {
		return Window.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wnd.Window"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wnd.Window";
	}

	/** Returns the caption of this window.
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
	IWindow<I> withCaption(@Nullable ICaption caption);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkDraggableWithMode() {
		String draggable = getDraggable();
		if (!"embedded".equals(getMode())) {
			if (!"false".equals(draggable))
				throw new UiException("Only embedded window could be draggable");
		}
	}

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
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMode() {
		String mode = getMode();
		if (mode != null)
			IComponentChecker.checkMode(mode, "modal", "popup", "overlapped", "highlighted", "embedded");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IWindow<I> checkBorder() {
		String border = getBorder();
		if (border == null || "0".equals(border) || "false".equals(border)) {
			return withBorder("none");
		} else if ("true".equals(border)) {
			return withBorder("normal");
		}
		switch (border) {
		case "none":
		case "normal":
			break;
		default:
			throw new WrongValueException("Unknown border: " + border);
		}
		return this;
	}

	/**
	 * Returns the title.
	 * Besides this attribute, you could use {@link ICaption} to define
	 * a more sophisticated caption (a.k.a., title).
	 * <p>If a window has a caption whose label ({@link ICaption#getLabel})
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
	IWindow<I> withTitle(String title);

	/** Returns the current mode.
	 *  <p>Default: {@code "embedded"}</p>
	 */
	default String getMode() {
		return "embedded";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code mode}.
	 *
	 * <p>Sets the mode to {@code "overlapped"}, {@code "popup"}, {@code "modal"},
	 * {@code "embedded"} or {@code "highlighted"}.
	 *
	 * @param mode The mode of this component.
	 * <p>Default: {@code "embedded"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withMode(String mode);


	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code mode}.
	 *
	 * <p>Sets the mode to {@link Mode}.
	 *
	 * @param mode The mode of this component.
	 * <p>Default: {@code "embedded"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IWindow<I> withMode(Mode mode) {
		return withMode(mode.value);
	}

	/** Returns how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * <p>Default: {@code null} which depends on {@link #getMode}:
	 * If overlapped or popup, {@link #withLeft} and {@link #withTop} are
	 * assumed. If modal or highlighted, it is centered.
	 */
	@Nullable
	String getPosition();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code position}.
	 *
	 * <p> Sets how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * @param position How to position. It can be {@code null} (the default), or
	 * a combination of the following values (by separating with comma).
	 * <dl>
	 * <dt>center</dt>
	 * <dd>Position the window at the center. {@link #withTop} and {@link #withLeft}
	 * are both ignored.</dd>
	 * <dt>nocenter</dt>
	 * <dd>Not to position the window at the center. A modal window, by default,
	 * will be position at the center. By specifying this value could
	 * prevent it and the real position depends on {@link #withTop} and {@link #withLeft}</dd>
	 * <dt>left</dt>
	 * <dd>Position the window at the left edge. {@link #withLeft} is ignored.</dd>
	 * <dt>right</dt>
	 * <dd>Position the window at the right edge. {@link #withLeft} is ignored.</dd>
	 * <dt>top</dt>
	 * <dd>Position the window at the top edge. {@link #withTop} is ignored.</dd>
	 * <dt>bottom</dt>
	 * <dd>Position the window at the bottom edge. {@link #withTop} is ignored.</dd>
	 * <dt>parent</dt>
	 * <dd>Position the window relative to its parent.
	 * That is, the left and top ({@link #getTop} and {@link #getLeft})
	 * is an offset to his parent's let-top corner.</dd>
	 * </dl>
	 * <p>For example, {@code "left,center"} means to position it at the center of
	 * the left edge.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withPosition(@Nullable String position);

	/** Returns the CSS style for the content block of the window.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getContentStyle();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code contentStyle}.
	 *
	 * <p>Sets the CSS style for the content block of the window.
	 *
	 * @param contentStyle The CSS style for the content block of the window.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withContentStyle(@Nullable String contentStyle);

	/** Returns the style class used for the content block.
	 *
	 *  <p>Default: {@code null}</p>
	 */
	@Nullable
	String getContentSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code contentSclass}.
	 *
	 * <p>Sets the style class used for the content block.
	 *
	 * @param contentSclass The style class used for the content block.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withContentSclass(@Nullable String contentSclass);

	/** Returns whether to show a close button on the title bar.
	 */
	default boolean isClosable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closable}.
	 *
	 * <p> Sets whether to show a close button on the title bar.
	 * If closable, a button is displayed and the {@code onClose} action is sent
	 * if a user clicks the button.
	 *
	 * @param closable Whether to show a close button on the title bar.
	 * <p>Default: {@code false}.</p>
	 * <p><b>Note:</b> the close button won't be displayed if no title or caption at all.
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withClosable(boolean closable);

	/**
	 * Returns whether to display the maximizing button and allow the user to maximize
	 * the window.
	 * <p>Default: {@code false}.
	 */
	default boolean isMaximizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maximizable}.
	 *
	 * <p> Sets whether to display the maximizing button and allow the user to maximize
	 * the window.
	 *
	 * @param maximizable Whether to display the maximizing button
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withMaximizable(boolean maximizable);

	/**
	 * Returns whether to display the minimizing button and allow the user to minimize
	 * the window.
	 * <p>Default: {@code false}.
	 */
	default boolean isMinimizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minimizable}.
	 *
	 * <p> Sets whether to display the minimizing button and allow the user to minimize
	 * the window.
	 *
	 * @param minimizable Whether to show a minimizing button.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withMinimizable(boolean minimizable);

	/**
	 * Returns whether the window is maximized.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isMaximized() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maximized}.
	 *
	 * <p> Sets whether the window is maximized.
	 *
	 * @param maximized Whether the window is maximized.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withMaximized(boolean maximized);

	/**
	 * Returns whether the window is minimized.
	 * <p>Default: {@code false}.
	 */
	default boolean isMinimized() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minimized}.
	 *
	 * <p> Sets whether the window is minimized.
	 *
	 * @param minimized Whether the window is minimized.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withMinimized(boolean minimized);

	/** Returns whether the window is sizable.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isSizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code sizable}.
	 *
	 * <p> Sets whether the window is sizable.
	 *
	 * @param sizable Whether the window is sizable.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withSizable(boolean sizable);

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
	IWindow<I> withMinheight(int minheight);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IWindow<I> checkMinheight() {
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
	IWindow<I> withMinwidth(int minwidth);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IWindow<I> checkMinwidth() {
		int minwidth = getMinwidth();
		if (minwidth < 0) {
			return withMinwidth(200);
		}
		return this;
	}

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
	 * Allowed values include <code>none</code> (default), and <code>normal</code>
	 * For more information, please refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Window#Border">ZK Component Reference: Window</a>.
	 * @param border the border. If null, {@code "0"} or {@code "false"}, {@code "none"} is assumed.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withBorder(String border);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code border}.
	 *
	 * <p> Sets the border with the given {@link Border border}.
	 * @param border The border
	 *
	 * @return A modified copy of the {@code this} object
	 */
	default IWindow<I> withBorder(Border border) {
		Objects.requireNonNull(border);
		return withBorder(border.value);
	}

	/** Returns whether to show the shadow of an overlapped/popup/modal
	 * window. It is meaningless if it is an embedded window.
	 * <p>Default: {@code true}</p>
	 */
	default boolean isShadow() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code shadow}.
	 *
	 * <p> Sets whether to show the shadow of an overlapped/popup/modal
	 * window.
	 * @param shadow Whether to show the shadow of an overlapped/popup/modal
	 * window
	 * <p>Default: {@code true}</p>
	 *
	 * @return A modified copy of the {@code this} object
	 */
	IWindow<I> withShadow(boolean shadow);

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
	IWindow<I> withNativeScrollbar(boolean nativeScrollbar);

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
			components.addAll(children);
		}
		return components;
	}

	/**
	 * Returns the instance with the given vflex.
	 * @param vflex The vertical flex hint.
	 */
	static <I extends IAnyGroup> IWindow<I> ofVflex(String vflex) {
		return new IWindow.Builder<I>().setVflex(vflex).build();
	}

	/**
	 * Returns the instance with the given title.
	 * @param title The title of the component.
	 */
	static <I extends IAnyGroup> IWindow<I> ofTitle(String title) {
		return new IWindow.Builder<I>().setTitle(title).build();
	}

	/**
	 * Returns the instance with the given caption.
	 * @param caption The caption child
	 */
	static <I extends IAnyGroup> IWindow<I> ofCaption(ICaption caption) {
		return new IWindow.Builder<I>().setCaption(caption).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link Border} border.
	 * @param border The border of the component
	 */
	static <I extends IAnyGroup> IWindow<I> ofBorder(Border border) {
		return new IWindow.Builder().setBorder(border.value).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IWindow<I> of(Iterable<? extends I> children) {
		return new IWindow.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IWindow<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given size, width and height
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static <I extends IAnyGroup> IWindow<I> ofSize(String width, String height) {
		return new IWindow.Builder<I>().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IWindow<I> ofId(String id) {
		return new IWindow.Builder<I>().setId(id).build();
	}

	/**
	 * Returns the instance with the given mode.
	 * @param mode The mode to overlapped, popup, modal, embedded or highlighted
	 */
	static <I extends IAnyGroup> IWindow<I> ofMode(Mode mode) {
		return new IWindow.Builder<I>().setMode(mode.value).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "title", getTitle());
		render(renderer, "maximized", isMaximized());
		render(renderer, "maximizable", isMaximizable());
		render(renderer, "minimized", isMinimized());
		render(renderer, "minimizable", isMinimizable());
		render(renderer, "closable", isClosable());
		render(renderer, "sizable", isSizable());
		render(renderer, "position", getPosition());
		render(renderer, "contentStyle", getContentStyle());
		render(renderer, "contentSclass", getContentSclass());
		int _minheight = getMinheight();
		if (_minheight != 100)
			renderer.render("minheight", _minheight);
		int _minwidth = getMinwidth();
		if (_minwidth != 200)
			renderer.render("minwidth", _minwidth);
		String _border = getBorder();
		if (!"none".equals(_border))
			renderer.render("border", _border);
		if (!isShadow())
			renderer.render("shadow", false);
		String _mold = getMode();
		if (_mold != null && _mold != "embedded")
			renderer.render("mode", _mold);
		//render mode as the last property

		//ZK-3678: Provide a switch to enable/disable iscroll
		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);
	}

	/**
	 * Builds instances of type {@link IWindow IWindow}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIWindow.Builder<I> {}

	/**
	 * Builds an updater of type {@link IWindow} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IWindowUpdater {}

	/**
	 * Specifies the border to {@link IWindow} component.
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

	/**
	 * Specifies the mode to {@link IWindow} component
	 */
	enum Mode {
		/** Embeds the window as normal component. */
		EMBEDDED("embedded"),

		/** Makes the window as highlighted.
		 * Its visual effect is the same as {@link #MODAL}.
		 * However, from the server side's viewpoint, it is similar to
		 * {@link #OVERLAPPED}.
		 *
		 * @see #MODAL
		 * @see #OVERLAPPED
		 */
		HIGHLIGHTED("highlighted"),

		/** Makes the window as overlapped other components.
		 */
		OVERLAPPED("overlapped"),

		/** Makes the window as popup.
		 * It is similar to {@link #OVERLAPPED}, except it is auto hidden
		 * when user clicks outside the window.
		 */
		POPUP("popup"),

		/** Makes the window as a modal dialog.
		 * @see #HIGHLIGHTED
		 */
		MODAL("modal");

		final String value;
		Mode(String value) {
			this.value = value;
		}
	}
}