/* HtmlBasedComponent.java

 {{IS_NOTE
 Purpose:

 Description:

 History:
 Tue Oct 23 09:22:13     2008, Created by Flyworld
 }}IS_NOTE

 Copyright (C) 2008 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zk.ui.api;

/**
 * A skeletal implementation for HTML based components. It simplifies to
 * implement methods common to HTML based components.
 * 
 * <p>
 * It supports
 * <ul>
 * <li>{@link #getSclass} and {@link #getStyle}.</li>
 * <li>{@link #getWidth}, {@link #getHeight}, {@link #getLeft}, {@link #getTop},
 * {@link #getZIndex}</li>
 * <li>{@link #getOuterAttrs}</li>
 * <li>{@link #getInnerAttrs}</li>
 * <li>{@link #focus}</li>
 * </ul>
 * 
 * @author tomyeh
 */
public interface HtmlBasedComponent extends
		org.zkoss.zk.ui.api.AbstractComponent {
	/**
	 * Returns the left position.
	 */
	public String getLeft();

	/**
	 * Sets the left position.
	 */
	public void setLeft(String left);

	/**
	 * Returns the top position.
	 */
	public String getTop();

	/**
	 * Sets the top position.
	 */
	public void setTop(String top);

	/**
	 * Returns the Z index.
	 * <p>
	 * Default: -1 (means system default;
	 */
	public int getZIndex();

	/**
	 * Sets the Z index.
	 */
	public void setZIndex(int zIndex);

	/**
	 * Returns the height. If null, the best fit is used.
	 * <p>
	 * Default: null.
	 */
	public String getHeight();

	/**
	 * Sets the height. If null, the best fit is used.
	 */
	public void setHeight(String height);

	/**
	 * Returns the width. If null, the best fit is used.
	 * <p>
	 * Default: null.
	 */
	public String getWidth();

	/**
	 * Sets the width. If null, the best fit is used.
	 */
	public void setWidth(String width);

	/**
	 * Returns the text as the tooltip.
	 * <p>
	 * Default: null.
	 */
	public String getTooltiptext();

	/**
	 * Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext);

	/**
	 * Returns the ZK Cascading Style class(es) for this component. It usually
	 * depends on the implementation of the mold (@{link #getMold}).
	 * 
	 * <p>
	 * Default: null (the default value depends on element).
	 * 
	 * <p>
	 * {@link #setZclass}) will completely replace the default style of a
	 * component. In other words, the default style of a component is associated
	 * with the default value of {@link #getZclass}. Once it is changed, the
	 * default style won't be applied at all. If you want to perform small
	 * adjustments, use {@link #setSclass} instead.
	 * 
	 * @since 3.5.1
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#getSclass
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#getRealSclass
	 */
	public String getZclass();

	/**
	 * Sets the ZK Cascading Style class(es) for this component. It usually
	 * depends on the implementation of the mold (@{link #getMold}).
	 * 
	 * @since 3.5.0
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#setSclass
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#getZclass
	 */
	public void setZclass(String zclass);

	/**
	 * Returns the CSS class.
	 * 
	 * <p>
	 * Default: null.
	 * 
	 * <p>
	 * The default styles of ZK components doesn't depend on the value of
	 * {@link #getSclass}. Rather, {@link #setSclass} is provided to perform
	 * small adjustment, e.g., only changing the font size. In other words, the
	 * default style is still applied if you change the value of
	 * {@link #getSclass}, unless you override it. To replace the default style
	 * completely, use {@link #setZclass} instead.
	 * 
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#getRealSclass
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#getZclass
	 */
	public String getSclass();

	/**
	 * Sets the CSS class.
	 * 
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#setZclass
	 */
	public void setSclass(String sclass);

	/**
	 * Returns the CSS style.
	 * <p>
	 * Default: null.
	 */
	public String getStyle();

	/**
	 * Sets the CSS style.
	 */
	public void setStyle(String style);

	/**
	 * Sets "true" or "false" to denote whether a component is draggable, or an
	 * identifier of a draggable type of objects.
	 * 
	 * <p>
	 * The simplest way to make a component draggable is to set this attribute
	 * to true. To disable it, set this to false.
	 * 
	 * <p>
	 * If there are several types of draggable objects, you could assign an
	 * identifier for each type of draggable object. The identifier could be
	 * anything but empty.
	 * 
	 * @param draggable
	 *            "false", null or "" to denote non-draggable; "true" for
	 *            draggable with anonymous identifier; others for an identifier
	 *            of draggable.
	 */
	public void setDraggable(String draggable);

	/**
	 * Sets "true" or "false" to denote whether a component is droppable, or a
	 * list of identifiers of draggable types of objects that could be droped to
	 * this component.
	 * 
	 * <p>
	 * The simplest way to make a component droppable is to set this attribute
	 * to true. To disable it, set this to false.
	 * 
	 * <p>
	 * If there are several types of draggable objects and this component
	 * accepts only some of them, you could assign a list of identifiers that
	 * this component accepts, separated by comma. For example, if this
	 * component accpets dg1 and dg2, then assign "dg1, dg2" to this attribute.
	 * 
	 * @param droppable
	 *            "false", null or "" to denote not-droppable; "true" for
	 *            accepting any draggable types; a list of identifiers,
	 *            separated by comma for identifiers of draggables this compoent
	 *            accept (to be dropped in).
	 */
	public void setDroppable(String droppable);

	/**
	 * Sets focus to this element. If an element does not accept focus, this
	 * method has no effect.
	 */
	public void focus();

	/**
	 * Sets focus to this element. It is same as {@link #focus}, but used to
	 * allow ZUML to set focus to particular component.
	 * 
	 * <pre>
	 * &lt;code&gt;&lt;textbox focus=&quot;true&quot;/&gt;&lt;/code&gt;
	 * </pre>
	 * 
	 * @param focus
	 *            whether to set focus. If false, this method has no effect.
	 * @since 3.0.5
	 */
	public void setFocus(boolean focus);

	/**
	 * Returns the exterior attributes for generating the enclosing HTML tag;
	 * never return null.
	 * 
	 * <p>
	 * Used only by component developers.
	 * 
	 * <p>
	 * Default: Generates the tooltip text, style, sclass, draggable and
	 * droppable attribute if necessary. In other words, the corresponding
	 * attribute is generated if {@link #getTooltiptext},
	 * {@link org.zkoss.zk.ui.HtmlBasedComponent#getRealStyle},
	 * {@link #getSclass},
	 * {@link org.zkoss.zk.ui.HtmlBasedComponent#getDraggable},
	 * {@link org.zkoss.zk.ui.HtmlBasedComponent#getDroppable} are defined.
	 * 
	 * <p>
	 * You have to call both {@link #getOuterAttrs} and {@link #getInnerAttrs}
	 * to generate complete attributes.
	 * 
	 * <p>
	 * For simple components that all attributes are put on the outest HTML
	 * element, all you need is as follows.
	 * 
	 * <pre>
	 * &lt;code&gt;&lt;xx id=&quot;${self.uuid}&quot;${self.outerAttrs}${self.innerAttrs}&gt;&lt;/code&gt;
	 * </pre>
	 * 
	 * <p>
	 * If you want to put attributes in a nested HTML element, you shall use the
	 * following pattern. Notice: if {@link #getInnerAttrs} in a different tag,
	 * the tag must be named with "${self.uuid}!real".
	 * 
	 * <pre>
	 * &lt;code&gt;&lt;xx id=&quot;${self.uuid}&quot;${self.outerAttrs}&gt;
	 * &lt;yy id=&quot;${self.uuid}!real&quot;${self.innerAttrs}&gt;...
	 * /code&gt;
	 * </pre>
	 * 
	 * <p>
	 * Note: This class handles non-deferrable event listeners automatically.
	 * However, you have to invoke
	 * {@link org.zkoss.zk.ui.HtmlBasedComponent#appendAsapAttr} for each event
	 * the component handles in {@link #getOuterAttrs} as follows.
	 * 
	 * <pre>
	 * &lt;code&gt;
	 * appendAsapAttr(sb, Events.ON_OPEN);
	 *  appendAsapAttr(sb, Events.ON_CHANGE);
	 * /code&gt;
	 * </pre>
	 * 
	 * <p>
	 * Theorectically, you could put any attributes in either
	 * {@link #getInnerAttrs} or {@link #getOuterAttrs}. However, zkau.js
	 * assumes all attributes are put at the outer one. If you want something
	 * different, you have to provide your own setAttr (refer to how checkbox is
	 * implemented).
	 */
	public String getOuterAttrs();

	/**
	 * Returns the interior attributes for generating the inner HTML tag; never
	 * return null.
	 * 
	 * <p>
	 * Used only by component developers.
	 * 
	 * <p>
	 * Default: empty string. Refer to {@link #getOuterAttrs} for more details.
	 */
	public String getInnerAttrs();

}
