/* IMeshElement.java

	Purpose:

	Description:

	History:
		Tue Oct 19 17:07:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.MeshElement;

/**
 * Immutable {@link MeshElement} interface
 *
 * <p>The fundamental class for mesh components such as
 * {@link IGrid}, {@link IListbox}, and {@link ITree}.</p>
 *
 * <h2>Sticky Header</h2>
 *
 * <p> After adding a sclass {@code "z-sticky-header"}, when we scroll down a page
 * and make a Mesh component's header out of visible range in a viewport, the
 * Mesh component's header becomes floating and sticky on the top of the page.
 * <br>
 * For example,
 * <pre>
 * <code>
 * return meshComp.withSclass("z-sticky-header");
 * </code>
 * </pre>
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
 * <b>Note:</b> with zk.xml setting, it will affect to all subcomponents which
 * extend from {@link IMeshElement}.
 * @author katherine
 */
public interface IMeshElement<I extends IMeshElement> extends IXulElement<I> { //extends Paginated

	/**
	 * Return column span hint of this component.
	 * <p>Default: {@code null}
	 */
	@Nullable
	String getSpan();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code span}.
	 *
	 * <p> Sets column span hint of this component.
	 * String number span indicates how this component distributes remaining empty space to the
	 * specified column(0-based). {@code "0"} means distribute remaining empty space
	 * to the 1st column; {@code "1"} means distribute remaining empty space to
	 * the 2nd column, etc.. The spanning column will grow to fit the extra remaining space.</p>
	 *
	 * <p>Special span hint with {@code "true"} means span <b>ALL</b> columns proportionally per their
	 * original widths while null or {@code "false"} means <b>NOT</b> spanning any column.</p>
	 * <p>Default: {@code null}. That is, <b>NOT</b> span any column.</p>
	 * <p><b>Note:</b> span is meaningful only if there is remaining empty space for columns.</p>
	 *
	 * @param span The column span hint.
	 * @return A modified copy of the {@code this} object
	 */
	I withSpan(@Nullable String span);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code span}.
	 *
	 * <p> Sets whether distributes remaining empty space of this component to ALL columns proportionally.
	 * <p>Default: {@code false}. That is, NOT span any column.</p>
	 * <p><b>Note:</b> span is meaningful only if there is remaining empty space for columns.</p>
	 *
	 * @param span Whether to span the width of <b>ALL</b> columns to occupy the whole mesh element(grid/listbox/tree).
	 * @return A modified copy of the {@code this} object
	 */
	default I withSpan(boolean span) {
		String _span = getSpan();
		if ((span && !"true".equals(_span)) || (!span && _span != null && !"false".equals(_span))) {
			return withSpan(span ? "true" : "false");
		}
		return (I) this;
	}

	/** Returns the instance of the {@link IPaging}
	 * <p>Default: {@code null}, if in {@code "paging"} mold, it won't be null</p>
	 */
	@Nullable
	@StatelessOnly
	IPaging getPagingChild();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pagingChild}.
	 *
	 * <p>Sets the paging child component to this mesh component</p>
	 * @param pagingChild The paging child.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withPagingChild(@Nullable IPaging pagingChild);

	/**
	 * Returns whether sizing grid/listbox/tree column width by its content.
	 * <p>Default: {@code false}.
	 */
	default boolean isSizedByContent() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sizedByContent}.
	 *
	 * <p> Sets whether sizing grid/listbox/tree column width by its content;
	 * it equals to set {@code withHflex("min")} on each column.
	 * @param sizedByContent Whether to enable the column width is sized by its content.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSizedByContent(boolean sizedByContent);

	/**
	 * Returns whether the auto-paging facility is turned on when the mold is
	 * {@code "paging"}. If it is set to {@code true}, the {@code getPagingChild().getPageSize()}
	 * is ignored; rather, the page size is automatically determined by the height
	 * of the mesh component dynamically.
	 * <p>Default: {@code false}</p>
	 * @return whether the auto-paging facility is turned on.
	 */
	default boolean isAutopaging() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autopaging}.
	 *
	 * <p> Sets whether the auto-paging facility is turned on when the mold is
	 * {@code "paging"}. If it is set to {@code true}, the {@code getPagingChild().getPageSize()}
	 * is ignored; rather, the page size is automatically determined by the height of the
	 * mesh component dynamically.
	 * <p> <b>Note:</b> Due to performance concern, Autopaging functionality
	 * does not support {@code IDetail} components.
	 * @param autopaging True to turn on the auto-paging facility.
	 * @return A modified copy of the {@code this} object
	 */
	I withAutopaging(boolean autopaging);

	/**
	 * Returns how to position the paging of mesh component at the client screen.
	 * It is meaningless if the mold is not in {@code "paging"}.
	 * <p>Default: {@code "bottom"}</p>
	 */
	default String getPagingPosition() {
		return "bottom";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pagingPosition}.
	 *
	 * <p> Sets how to position the paging of mesh component at the client screen.
	 * It is meaningless if the mold is not in {@code "paging"}.
	 * @param pagingPosition How to position. It can only be {@code "bottom"} (the default), or
	 * {@code "top"}, or {@code "both"}.
	 * @return A modified copy of the {@code this} object
	 */
	I withPagingPosition(String pagingPosition);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pagingPosition}.
	 *
	 * <p> Sets how to position the paging of mesh component at the client screen.
	 * It is meaningless if the mold is not in {@code "paging"}.
	 * @param pagingPosition How to position.
	 * @return A modified copy of the {@code this} object
	 * @see PagingPosition
	 */
	default I withPagingPosition(PagingPosition pagingPosition) {
		Objects.requireNonNull(pagingPosition);
		return withPagingPosition(pagingPosition.value);
	}

	/**
	 * Returns whether to use Browser's scrollbar or a floating scrollbar (if with {@code false}).
	 * <p>Default: {@code true} to use Browser's scrollbar, if the {@code "org.zkoss.zul.nativebar"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isNativeScrollbar() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.nativebar", "true"));
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
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkPagingPosition() {
		String pagingPosition = getPagingPosition();
		if (pagingPosition == null || (!pagingPosition.equals("top") && !pagingPosition.equals("bottom")
				&& !pagingPosition.equals("both")))
			throw new WrongValueException("Unsupported position : " + pagingPosition);
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		if (isSizedByContent())
			renderer.render("sizedByContent", true);

		String _span = getSpan();
		if (_span != null)
			renderer.render("span", _span);

		if (isAutopaging())
			renderer.render("autopaging", true);

		String _pagingPosition = getPagingPosition();
		if (!"bottom".equals(_pagingPosition))
			render(renderer, "pagingPosition", _pagingPosition);

		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);

		// mark it as stateless widget if it's in client paging case for #78.
		renderer.render("_stateless", true);
	}

	/**
	 * Specifies the paging position when {@code "paging"} mold is used.
	 */
	enum PagingPosition {
		/**
		 * Enables both {@link #TOP} and {@link #BOTTOM} positions to display
		 * the paging control.
		 */
		BOTH("both"),
		/**
		 * Enables only top position to display the paging control.
		 */
		TOP("top"),
		/**
		 * Enables only bottom position to display the paging control.
		 */
		BOTTOM("bottom");

		private String value;
		PagingPosition(String value) {
			this.value = value;
		}
	}
}