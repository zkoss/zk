/* IHtmlBasedComponent.java

	Purpose:

	Description:

	History:
		3:30 PM 2021/9/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.AfterSizeData;
import org.zkoss.stateless.action.data.DropData;
import org.zkoss.stateless.action.data.KeyData;
import org.zkoss.stateless.action.data.MouseData;
import org.zkoss.stateless.action.data.SwipeData;
import org.zkoss.zk.ui.UiException;

/**
 * Immutable {@link org.zkoss.zk.ui.HtmlBasedComponent} interface.
 * Provides a skeleton for HTML based immutable components.
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
 *          <td>onClick</td>
 *          <td><strong>ActionData</strong>: {@link MouseData}
 *           <br>
 *          Represents an action triggered by the user clicks on a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onDoubleClick</td>
 *          <td><strong>ActionData</strong>: {@link MouseData}
 *          <br> Represents an action triggered by the user double-clicks on a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onRightClick</td>
 *          <td><strong>ActionData</strong>: {@link MouseData}
 *          <br> Represents an action triggered by the user right-clicks on a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMouseOver</td>
 *          <td><strong>ActionData</strong>: {@link MouseData}
 *          <br> Represents an action triggered by the user moves the mouse pointer onto a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onMouseOut</td>
 *          <td><strong>ActionData</strong>: {@link MouseData}
 *          <br> Represents an action triggered by the user moves the mouse pointer out a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onOK</td>
 *          <td><strong>ActionData</strong>: {@link KeyData}
 *          <br> Represents an action triggered by the user presses the {@code ENTER} key on a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onCancel</td>
 *          <td><strong>ActionData</strong>: {@link KeyData}
 *          <br> Represents an action triggered by the user presses the {@code ESC} key on a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onDrop</td>
 *          <td><strong>ActionData</strong>: {@link DropData}
 *          <br> Represents an action triggered by user’s dragging and dropping a component.</td>
 *       </tr>
 *       <tr>
 *          <td>onSwipe</td>
 *          <td><strong>ActionData</strong>: {@link SwipeData}
 *          <br> Represents an action that indicates swipe on a component and provides information about the swipe displacement, duration and direction. (Tablet only)</td>
 *       </tr>
 *       <tr>
 *          <td>onAfterSize</td>
 *          <td><strong>ActionData</strong>: {@link AfterSizeData}
 *          <br> Represents an action that resizes a component and provides the new size of the component</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author jumperchen
 */
public interface IHtmlBasedComponent<I extends IHtmlBasedComponent>
		extends IComponent<I> {

	/**
	 * Returns the left position of the {@code positioned} component. If not specified,
	 * {@code null} is assumed.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getLeft();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code left}.
	 *
	 * <p> This attribute specifies the left position of the component at client HTML element including
	 * {@code padding}, {@code scrollbar}, {@code border} and {@code margin}.</p>
	 *
	 * <br>
	 * <b>Tip:</b> A positioned component is a HTML element with the {@code position} attribute set to:
	 * {@code relative}, {@code absolute}, or {@code fixed} in {@link #withStyle(String)}.
	 * <br>
	 * <b>Tip:</b> To set the {@code right} position of a positioned component, use the {@link #withStyle(String)} instead.
	 * </p>
	 * @param left The left position of the component to set.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withLeft(@Nullable String left);

	/**
	 * Returns the top position of the {@code positioned} component. If not specified,
	 * {@code null} is assumed.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getTop();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code top}.
	 * <p> This attribute specifies the top position of the component at client HTML element including
	 * {@code padding}, {@code scrollbar}, {@code border} and {@code margin}.</p>
	 *
	 * <br>
	 * <b>Tip:</b> A positioned component is a HTML element with the {@code position} attribute set to:
	 * {@code relative}, {@code absolute}, or {@code fixed} in {@link #withStyle(String)}.
	 * <br>
	 * <b>Tip:</b> To set the {@code bottom} position of a positioned component, use the {@link #withStyle(String)} instead.
	 * </p>
	 * @param top The top position of the component to set.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTop(@Nullable String top);

	/**
	 * Returns the stack order of a positioned component.
	 *
	 * <p>Default: {@code -1}</p>
	 */
	default int getZIndex() {
		return -1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code zIndex}.
	 * <p> A component with greater stack order ({@code 1}) is always in front of another
	 * component with lower stack order ({@code 0}).</p>
	 *
	 * <p>
	 * <b>Tip:</b> A positioned component is a HTML element with the {@code position} attribute set to:
	 * {@code relative}, {@code absolute}, or {@code fixed} in {@link #withStyle(String)}.
	 * </p>
	 * @param zIndex The stack order of a positioned component.
	 * <p>Default: {@code -1}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withZIndex(int zIndex);

	/**
	 * Returns the height of the component. If not specified,
	 * {@code null} is assumed.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getHeight();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code height}.
	 *
	 * <p>
	 * The {@code height} attribute includes {@code padding} and {@code borders},
	 * but not {@code margins}, and the value can be set to a numeric value
	 * (like pixels, (r)em, percentages). (Same as CSS Height property)
	 * </p>
	 *
	 * @param height The height of the component.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withHeight(@Nullable String height);

	/**
	 * Returns the width of the component. If not specified,
	 * {@code null} is assumed.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getWidth();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code width}.
	 *
	 * <p>
	 * The {@code width} attribute includes {@code padding} and {@code borders},
	 * but not {@code margins}, and the value can be set to a numeric value
	 * (like pixels, (r)em, percentages). (Same as CSS Width property)
	 * </p>
	 * @param width The width of the component.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withWidth(@Nullable String width);

	/**
	 * Returns the text of the tooltip which belongs to this component.
	 *
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getTooltiptext();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tooltiptext}.
	 *
	 * <p>Sets the text as the tooltip to the component. (Same as HTML Title property)</p>
	 * @param tooltiptext A text to set as tooltip on the component.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTooltiptext(@Nullable String tooltiptext);

	/**
	 * Returns the ZK Cascading Style class for this component.
	 * It usually depends on the implementation of the mold ({@link #getMold()}).
	 *
	 * @see #getSclass()
	 */
	@Nullable
	String getZclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code zclass}.
	 *
	 * <p>Sets the ZK Cascading Style class for this component.
	 * It usually depends on the implementation of the {@link #getMold() mold}.
	 * <br>
	 * <br>
	 * <b>Note:</b>The {@code zclass} attribute will completely replace the default style
	 * of a component. In other words, the default style of a component
	 * is associated with the default value of {@link #getZclass()}.
	 * Once it is changed, the default style won't be applied at all.
	 * If you want to perform small adjustments, use {@link #withSclass(String)}
	 * instead.
	 * </p>
	 * @param zclass The ZK Cascading Style class for this component.
	 * <p><b>Note:</b> The value is better to use a single Cascading Style class; multiple values
	 * may cause some unwanted behavior, because the {@code zclass} value will be
	 * used to plus other postfix as its descendant elements at client.
	 * <br><br>
	 * For example,
	 * <br><br>
	 * {@code "z-window-embedded"} or {@code "z-window-content"} for the {@code zclass} value ({@code "z-window"}) on {@link IWindow}.
	 * </p>
	 * <p>Default: {@code null} (the default value depends on element).</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withZclass(@Nullable String zclass);

	/**
	 * Returns the CSS class(es).
	 *
	 * <p>Default: {@code null}.</p>
	 *
	 * @see #getZclass()
	 */
	@Nullable
	String getSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code sclass}.
	 *
	 * <p>The default styles of ZK components doesn't depend on the value
	 * of {@link #getSclass()}. Rather, {@link #withSclass(String)} is provided to
	 * perform small adjustment, e.g., only changing the font size.
	 * In other words, the default style is still applied if you change
	 * the value of {@link #getSclass()}.
	 * To replace the default style completely, use
	 * {@link #withZclass(String)} instead.</p>
	 *
	 * @param sclass The CSS class(es) to apply to this component. With multiple values,
	 * please use a space {@code " "} to separate them. aka {@code "class1 class2 class3"}.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSclass(@Nullable String sclass);

	/**
	 * Returns the CSS style.
	 * <p>Default: {@code null}.</p>
	 */
	@Nullable
	String getStyle();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code style}.
	 *
	 * <p>Sets the CSS style to this component.</p>
	 * @param style The CSS style.
	 * @return A modified copy of the {@code this} object
	 */
	I withStyle(@Nullable String style);

	/**
	 * Returns the identifier of a draggable type of objects, or {@code "false"}
	 * if not draggable (never null nor empty).
	 * <p>Default: {@code "false"}</p>
	 */
	default String getDraggable() {
		return "false";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code draggable}.
	 *
	 * <p> Sets {@code "true"} or {@code "false"} to denote whether a component is draggable,
	 * or an identifier of a draggable type of objects.</p>
	 *
	 * <p>The simplest way to make a component draggable is to set
	 * this attribute to {@code "true"}. To disable it, set this to {@code "false"}.</p>
	 *
	 * <p>If there are several types of draggable objects, you could
	 * assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty.
	 * </p>
	 * @param draggable The value of {@code "false"} and {@code ""}
	 * to denote non-draggable; {@code "true"} for draggable with anonymous identifier;
	 * others for an identifier of draggable.
	 * @return A modified copy of the {@code this} object
	 */
	I withDraggable(String draggable);

	/**
	 * Returns the focus set by {@link #withFocus(boolean)}.
	 * <p>Note: it simply returns what is passed to {@link #withFocus(boolean)}.
	 * <p>Default: {@code false}
	 */
	default boolean isFocus() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code focus}.
	 *
	 * <p>Sets the focus onto this component.</p>
	 * @param focus True to focus on this component.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withFocus(boolean focus);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default I checkDraggable() {
		String draggable = getDraggable();
		if (draggable.length() == 0) {
			return withDraggable("false");
		}
		return (I) this;
	}

	/**
	 * Returns the identifier, or a list of identifiers (split by a comma {@code ","})
	 * of a droppable type of objects, or {@code "false"}
	 * if not droppable (never null nor empty).
	 * <p>Default: {@code "false"}</p>
	 */
	default String getDroppable() {
		return "false";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code droppable}.
	 *
	 * <p>Sets {@code "true"} or {@code "false"} to denote whether a component is droppable,
	 * or a list of identifiers of draggable types of objects that could
	 * be dropped to this component. (split by a comma {@code ","})</p>
	 *
	 * <p>The simplest way to make a component droppable is to set
	 * this attribute to {@code "true"}. To disable it, set this to {@code "false"}.</p>
	 *
	 * <p>If there are several types of draggable objects and this
	 * component accepts only some of them, you could assign a list of
	 * identifiers that this component accepts, separated by comma.
	 * For example, if this component accepts {@code dg1} and {@code dg2}, then
	 * assign {@code "dg1, dg2"} to this attribute.</p>
	 *
	 * @param droppable The value of {@code "false"} or {@code ""}
	 * to denote not-droppable; {@code "true"} for accepting any draggable types; a list of identifiers,
	 * separated by comma for identifiers of {@code draggables} this component
	 * accept (to be dropped in).
	 * @return A modified copy of the {@code this} object
	 */
	I withDroppable(String droppable);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default I checkDroppable() {
		String droppable = getDroppable();
		if (droppable.length() == 0) {
			return withDroppable("false");
		}
		return (I) this;
	}

	/**
	 * Return vertical flex hint of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getVflex();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code vflex}.
	 *
	 * <p>Number flex indicates how this component's container distributes remaining
	 * empty space among its children vertically. Flexible component grow and shrink
	 * to fit their given space. Flexible components with larger flex values will be made
	 * larger than components with lower flex values, at the ratio determined by
	 * all flexible components. The actual flex value is not relevant unless
	 * there are other flexible components within the same container. Once the
	 * default sizes of components in a container are calculated, the remaining
	 * space in the container is divided among the flexible components,
	 * according to their flex ratios.</p>
	 *
	 * <p>Specify a flex value of negative value, {@code 0},
	 * or {@code "false"} has the same effect as leaving the flex attribute out entirely.
	 * Specify a flex value of {@code "true"} has the same effect as a flex value of {@code 1}.</p>
	 *
	 * <p>Special flex hint, <b>{@code "min"}</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p>
	 *
	 * <p>Note: in Stateless, it uses CSS Flex implementation at all,
	 * except the {@code min} value, which calculates the size of the element by JavaScript.</p>
	 * @param vflex The vertical flex hint.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withVflex(@Nullable String vflex);

	/**
	 * Return horizontal flex hint of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getHflex();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code hflex}.
	 *
	 * <p>Number flex indicates how this component's container distributes remaining
	 * empty space among its children horizontal. Flexible component grow and shrink
	 * to fit their given space. Flexible components with larger flex values will be made
	 * larger than components with lower flex values, at the ratio determined by
	 * all flexible components. The actual flex value is not relevant unless
	 * there are other flexible components within the same container. Once the
	 * default sizes of components in a container are calculated, the remaining
	 * space in the container is divided among the flexible components,
	 * according to their flex ratios.</p>
	 *
	 * <p>Specify a flex value of negative value, {@code 0},
	 * or {@code "false"} has the same effect as leaving the flex attribute out entirely.
	 * Specify a flex value of {@code "true"} has the same effect as a flex value of {@code 1}.</p>
	 *
	 * <p>Special flex hint, <b>{@code "min"}</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p>
	 *
	 * <p>Note: in Stateless, it uses CSS Flex implementation at all,
	 * except the {@code min} value, which calculates the size of the element by JavaScript.</p>
	 * @param hflex The horizontal flex hint.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withHflex(@Nullable String hflex);

	/**
	 * Returns the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: {@code -1} (don't wait).
	 */
	default int getRenderdefer() {
		return -1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code renderdefer}.
	 *
	 * <p> Sets the number of milliseconds before rendering this component
	 * at the client.</p>
	 * <p>Default: {@code -1} (don't wait).</p>
	 *
	 * <p>This method is useful if you have a sophisticated page that takes
	 * long to render at a slow client. You can specify a non-negative value
	 * as the render-defer delay such that the other part of the UI can appear
	 * earlier. The styling of the render-deferred widget is controlled by
	 * a CSS class called <code>z-renderdefer</code>.
	 * </p>
	 * <p><b>Note:</b> Be aware of that too many components use this {@code renderdefer}
	 * at client will reduce the browser rendering performance.</p>
	 * @param renderdefer The time to wait in milliseconds before rendering.
	 * Notice: {@code 0} also implies deferring the rendering (just right after all others are rendered).
	 * @return A modified copy of the {@code this} object
	 */
	I withRenderdefer(int renderdefer);

	/**
	 * Returns the client-side action (CSA).
	 * <p>Default: {@code null} (no CSA at all)</p>
	 */
	@Nullable
	// former getAction() before ZK 10.0.0
	String getClientAction();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code clientAction}.
	 *
	 * <p> Sets the client-side action (CSA).
	 * <br>
	 * <br>
	 * Default: {@code null} (no CSA at all)
	 * </p>
	 *
	 * <p>The format:
	 * <br>
	 * <code>action1: action-effect1; action2: action-effect2</code>
	 * <br>
	 * </p>
	 *
	 * <p>Currently, only two actions are {@code show} and {@code hide}.
	 * They are called when the widget is becoming visible (show) and invisible (hide).
	 * </p>
	 * <p>The action effect ({@code action-effect1}) is the name of a method
	 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
	 * such as
	 * {@code show: slideDown; hide: slideUp}
	 * </p>
	 * <p>You could specify the effects as follows:
	 * <br/>
	 * {@code show: slideDown({duration:1000})}
	 * </p>
	 * <p>Security Tips: the {@code client action} is not encoded, it is OK to embed JavaScript,
	 * so, if you want to allow users to specify the action, you have to encode it.
	 * </p>
	 * @param clientAction The client-side action to set.
	 * @return A modified copy of the {@code this} object
	 */
	I withClientAction(@Nullable String clientAction);

	/**
	 * Returns the tab order of this component. (when the {@code "tab"} button is used for navigating)
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	Integer getTabindex();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tabindex}.
	 *
	 * <p>Specifies the {@code tab} order of this component. (when the {@code "TAB"} button is used for navigating)</p>
	 * @param tabindex The order of this component to set.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTabindex(@Nullable Integer tabindex);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tabindex}.
	 *
	 * <p>Specifies the {@code tab} order of this component. (when the {@code "TAB"} button is used for navigating)</p>
	 * @param tabindex The order of this component to set.
	 * <p>Default: {@code null}</p>
	 * @return A modified copy of the {@code this} object
	 * @see #withTabindex(Integer)
	 */
	default I withTabindex(int tabindex) {
		return withTabindex((Integer) tabindex);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkVflexAndHeight() {
		final String vflex = getVflex();
		if (vflex != null && !(getHeight() == null || vflex.equals("min"))) {
			throw new UiException("Not allowed to set vflex and height at the same time except vflex=\"min\"");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkHflexAndWidth() {
		final String hflex = getHflex();
		if (hflex != null && !(getWidth() == null || hflex.equals("min"))) {
			throw new UiException("Not allowed to set hflex and width at the same time except hflex=\"min\"");
		}
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	// same as HtmlBased#renderProperties(ContentRenderer)
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		IComponent.super.renderProperties(renderer);

		render(renderer, "width", getWidth());
		render(renderer, "height", getHeight());
		render(renderer, "left", getLeft());
		render(renderer, "top", getTop());
		render(renderer, "vflex", getVflex());
		render(renderer, "hflex", getHflex());
		render(renderer, "sclass", getSclass());
		render(renderer, "style", getStyle());
		render(renderer, "tooltiptext", getTooltiptext());

		if (getZIndex() >= 0)
			renderer.render("zIndex", getZIndex());
		if (getRenderdefer() >= 0)
			renderer.render("renderdefer", getRenderdefer());
		if (getTabindex() != null)
			renderer.render("tabindex", getTabindex());

		final String draggable = getDraggable();

		// TODO: DragControl at client side
		if (draggable != null
				&& (/*getParent() instanceof DragControl || */!draggable.equals(
				"false")))
			render(renderer, "draggable", draggable);

		final String droppable = getDroppable();
		if (droppable != null && !droppable.equals("false"))
			render(renderer, "droppable", droppable);

		render(renderer, "action", getClientAction());

		render(renderer, "zclass", getZclass());

		render(renderer, "focus", isFocus());

		// Stateless should all use cssflex=true by default
		//renderer.render("cssflex", false);
	}
}
