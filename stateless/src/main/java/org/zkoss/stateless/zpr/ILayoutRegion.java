/* ILayoutRegion.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:41:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.stateless.action.data.SizeData;
import org.zkoss.stateless.action.data.SlideData;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.LayoutRegion;

/**
 * Immutable {@link LayoutRegion} interface
 *
 * <p>This class represents a region in a layout manager.</p>
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
 *          <td>onOpen</td>
 *          <td><strong>ActionData</strong>: {@link OpenData}
 *          <br>When a layout is collapsed or opened by a user, the {@code onOpen}
 *          action is sent to the application.</td>
 *       </tr>
 *       <tr>
 *          <td>onSize</td>
 *          <td><strong>ActionData</strong>: {@link SizeData}
 *          <br>When a layout is resized by a user, the {@code onSize} action is sent to the application..</td>
 *       </tr>
 *       <tr>
 *          <td>onSlide</td>
 *          <td><strong>ActionData</strong>: {@link SlideData}
 *          <br>When a collapsed layout is slided (preview) by a user,
 *          the {@code onSlide} action is sent to the application.</td>
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
 */
public interface ILayoutRegion<I extends ILayoutRegion> extends IXulElement<I> {

	/**
	 * Returns the title.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getTitle();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code title}.
	 *
	 * <p>Sets the title of this component.
	 *
	 * @param title The title of this component.
	 * <p>Default: {@code null} (empty).</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTitle(@Nullable String title);

	/**
	 * Returns the border.
	 * <p>
	 * The border actually controls what CSS class to use: If border is null, it
	 * implies {@code "none"}.
	 *
	 * <p>
	 * If you also specify the CSS class ({@link #withSclass}), it overwrites
	 * whatever border you specify here.
	 *
	 * <p> Default: {@code "normal"}.
	 */
	default String getBorder() {
		return "normal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code border}.
	 *
	 * <p> Sets the border.
	 * Allowed values include <code>none</code> (default), and <code>normal</code>.
	 * @param border the border. If null, {@code "0"} or {@code "false"}, {@code "none"} is assumed.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withBorder(String border);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code border}.
	 *
	 * <p> Sets the border with the given {@link Border border}.
	 * @param border The border
	 *
	 * @return A modified copy of the {@code this} object
	 */
	default I withBorder(Border border) {
		Objects.requireNonNull(border);
		return withBorder(border.value);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default I checkBorder() {
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
		return (I) this;
	}

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isAutoscroll() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autoscroll}.
	 *
	 * <p> Sets whether enable overflow scrolling.
	 * @param autoscroll Whether enable overflow scrolling.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withAutoscroll(boolean autoscroll);

	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 *
	 * <p>
	 * Default: "0,0,0,0".
	 */
	default String getMargins() {
		return "0,0,0,0";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code margins}.
	 *
	 * <p> Sets margins for the element "0,1,2,3" that direction is
	 * 	"top,left,right,bottom"
	 * @param margins The margins of the region.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withMargins(String margins);

	/**
	 * Returns the maximum size of the resizing component.
	 * <p>
	 * Default: {@code 2000}.
	 */
	default int getMaxsize() {
		return 2000;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxsize}.
	 *
	 * <p> Sets the maximum size of the resizing component.
	 * @param maxsize The maximum size of the resizing component.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withMaxsize(int maxsize);

	/**
	 * Returns the minimum size of the resizing component.
	 * <p>
	 * Default: {@code 0}.
	 */
	default int getMinsize() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code minsize}.
	 *
	 * <p> Sets the minimum size of the resizing component.
	 * @param minsize The minimum size of the resizing component.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withMinsize(int minsize);

	/**
	 * Returns whether enable the split functionality.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isSplittable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code splittable}.
	 *
	 * <p> Sets whether enable the split functionality.
	 * @param splittable Whether enable the split functionality.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withSplittable(boolean splittable);

	/**
	 * Returns whether set the initial display to collapse.
	 * <p>It only applied when {@link #getTitle()} is not null.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isCollapsible() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code collapsible}.
	 *
	 * <p> Sets whether set the initial display to collapse.
	 * <p>It only applied when {@link #getTitle()} is not null.
	 *
	 * @param collapsible Whether set the initial display to collapse.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withCollapsible(boolean collapsible);

	/**
	 * Returns whether it is open (i.e., not collapsed. Meaningful only if
	 * {@link #isCollapsible} is not false).
	 * <p>
	 * Default: {@code true}.
	 */
	default boolean isOpen() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code open}.
	 *
	 * <p> Sets whether it is open (i.e., not collapsed. Meaningful only if
	 * {@link #isCollapsible} is not false).
	 *
	 * @param open Whether to open.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withOpen(boolean open);

	/**
	 * Returns whether users can slide (preview) the region when clicked on a
	 * collapsed region. In other words, if false, clicking on a collapsed region
	 * will open it instead of sliding.
	 * <p>Default: {@code true}.
	 */
	default boolean isSlidable() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code slidable}.
	 *
	 * <p> Sets whether users can slide (preview) the region when clicked on a
	 * collapsed region. In other words, if false, clicking on a collapsed region
	 * will open it instead of sliding.
	 *
	 * @param slidable Whether users can slide this region.
	 * <p>Default: {@code true}.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withSlidable(boolean slidable);

	/**
	 * Returns whether it is slide down.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isSlide() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code slidable}.
	 *
	 * <p> Sets whether slides down or up the region. Meaningful only if
	 * {@link #isCollapsible} is not false and {@link #isOpen} is false.
	 *
	 * @param slide Whether slides down or up the region
	 * <p>Default: {@code false}.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withSlide(boolean slide);

	/**
	 * Returns whether users can open or close the region.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking the button on the bar).
	 * <p>Default: {@code true}.
	 */
	default boolean isClosable() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closable}.
	 *
	 * <p> Sets whether users can open or close the region.
	 *
	 * @param closable Whether users can open or close the region.
	 * <p>Default: {@code true}.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withClosable(boolean closable);

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
	I withNativeScrollbar(boolean nativeScrollbar);

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		String _border = getBorder();
		if (!"normal".equals(_border))
			render(renderer, "border", _border);
		render(renderer, "autoscroll", isAutoscroll());

		if (!"0,0,0,0".equals(getMargins()))
			render(renderer, "margins", getMargins());

		render(renderer, "title", getTitle());

		int _maxsize = getMaxsize();
		if (_maxsize != 2000)
			renderer.render("maxsize", _maxsize);
		int _minsize = getMinsize();
		if (_minsize != 0)
			renderer.render("minsize", _minsize);

		render(renderer, "splittable", isSplittable());
		render(renderer, "collapsible", isCollapsible());

		//always generate since different region might have different default
		boolean _open = isOpen();
		if (!_open)
			renderer.render("open", _open);

		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);

		if (!isSlidable())
			renderer.render("slidable", false);
		if (!isClosable())
			renderer.render("closable", false);
	}
	/**
	 * Specifies the border to {@link ILayoutRegion} component.
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