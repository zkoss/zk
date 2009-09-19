/* Panel.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.IdSpace;//for javadoc
import org.zkoss.zk.ui.event.MinimizeEvent;//for javadoc
/**
 * Panel is a container that has specific functionality and structural
 * components that make it the perfect building block for application-oriented
 * user interfaces. The Panel contains bottom, top, and foot toolbars, along
 * with separate header, footer and body sections. It also provides built-in
 * collapsible, closable, maximizable, and minimizable behavior, along with a
 * variety of pre-built tool buttons that can be wired up to provide other
 * customized behavior. Panels can be easily embedded into any kind of ZUL
 * component that is allowed to have children or layout component. Panels also
 * provide specific features like float and move. Unlike {@link Window}, Panels
 * can only be floated and moved inside its parent node, which is not using
 * zk.setVParent() function at client side. In other words, if Panel's parent
 * node is an relative position, the floated panel is only inside its parent,
 * not the whole page. The second difference of {@link Window} is that Panel is
 * not an independent ID space (by implementing {@link IdSpace}), so the ID of
 * each child can be used throughout the panel.
 * 
 * <p>
 * Events:<br/>
 * onMove, onOpen, onZIndex, onMaximize, onMinimize, and onClose.<br/>
 * 
 * <p>
 * Default {@link #getZclass}: z-panel.
 * 
 * @author jumperchen
 * @since 3.5.2
 */
public interface Panel extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns whether this Panel is open.
	 * <p>
	 * Default: true.
	 */
	public boolean isOpen();

	/**
	 * Opens or closes this Panel.
	 */
	public void setOpen(boolean open);

	/**
	 * Returns whether to render the panel with custom rounded borders.
	 * <p>
	 * Default: false.
	 */
	public boolean isFramable();

	/**
	 * Sets whether to render the panel with custom rounded borders.
	 * 
	 * <p>
	 * Default: false.
	 */
	public void setFramable(boolean framable);

	/**
	 * Sets whether to move the panel to display it inline where it is rendered.
	 * 
	 * <p>
	 * Default: false;
	 * <p>
	 * Note that this method only applied when {@link #isFloatable()} is true.
	 */
	public void setMovable(boolean movable);

	/**
	 * Returns whether to move the panel to display it inline where it is
	 * rendered.
	 * <p>
	 * Default: false.
	 */
	public boolean isMovable();

	/**
	 * Returns whether to float the panel to display it inline where it is
	 * rendered.
	 * <p>
	 * Default: false.
	 */
	public boolean isFloatable();

	/**
	 * Sets whether to float the panel to display it inline where it is
	 * rendered.
	 * 
	 * <p>
	 * Note that by default, setting floatable to true will cause the panel to
	 * display at default offsets, which depend on the offsets of the embedded
	 * panel from its element to <i>document.body</i> -- because the panel is
	 * absolute positioned, the position must be set explicitly by
	 * {@link #setTop(String)} and {@link #setLeft(String)}. Also, when
	 * floatable a panel you should always assign a fixed width, otherwise it
	 * will be auto width and will expand to fill to the right edge of the
	 * viewport.
	 */
	public void setFloatable(boolean floatable);

	/**
	 * Returns whether the panel is maximized.
	 */
	public boolean isMaximized();

	/**
	 * Sets whether the panel is maximized, and then the size of the panel will
	 * depend on it to show a appropriate size. In other words, if true, the
	 * size of the panel will count on the size of its offset parent node whose
	 * position is absolute (by {@link #isFloatable()}) or its parent node.
	 * Otherwise, its size will be original size. Note that the maximized effect
	 * will run at client's sizing phase not initial phase.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 * @exception UiException
	 *                if {@link #isMaximizable} is false.
	 */
	public void setMaximized(boolean maximized);

	/**
	 * Returns whether to display the maximizing button and allow the user to
	 * maximize the panel.
	 * <p>
	 * Default: false.
	 */
	public boolean isMaximizable();

	/**
	 * Sets whether to display the maximizing button and allow the user to
	 * maximize the panel, when a panel is maximized, the button will
	 * automatically change to a restore button with the appropriate behavior
	 * already built-in that will restore the panel to its previous size.
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * Note: the maximize button won't be displayed if no title or caption at
	 * all.
	 */
	public void setMaximizable(boolean maximizable);

	/**
	 * Returns whether the panel is minimized.
	 * <p>
	 * Default: false.
	 */
	public boolean isMinimized();

	/**
	 * Sets whether the panel is minimized.
	 * <p>
	 * Default: false.
	 * 
	 * @exception UiException
	 *                if {@link #isMinimizable} is false.
	 */
	public void setMinimized(boolean minimized);

	/**
	 * Returns whether to display the minimizing button and allow the user to
	 * minimize the panel.
	 * <p>
	 * Default: false.
	 */
	public boolean isMinimizable();

	/**
	 * Sets whether to display the minimizing button and allow the user to
	 * minimize the panel. Note that this button provides no implementation --
	 * the behavior of minimizing a panel is implementation-specific, so the
	 * MinimizeEvent event must be handled and a custom minimize behavior
	 * implemented for this option to be useful.
	 * 
	 * <p>
	 * Default: false.
	 * <p>
	 * Note: the maximize button won't be displayed if no title or caption at
	 * all.
	 * 
	 * @see MinimizeEvent
	 */
	public void setMinimizable(boolean minimizable);

	/**
	 * Returns whether to show a toggle button on the title bar.
	 * <p>
	 * Default: false.
	 */
	public boolean isCollapsible();

	/**
	 * Sets whether to show a toggle button on the title bar.
	 * <p>
	 * Default: false.
	 * <p>
	 * Note: the toggle button won't be displayed if no title or caption at all.
	 */
	public void setCollapsible(boolean collapsible);

	/**
	 * Returns whether to show a close button on the title bar.
	 */
	public boolean isClosable();

	/**
	 * Sets whether to show a close button on the title bar. If closable, a
	 * button is displayed and the onClose event is sent if an user clicks the
	 * button.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * You can intercept the default behavior by either overriding
	 * {@link org.zkoss.zul.Panel#onClose}, or listening the onClose event.
	 * 
	 * <p>
	 * Note: the close button won't be displayed if no title or caption at all.
	 */
	public void setClosable(boolean closable);

	/**
	 * Returns the caption of this panel.
	 */
	public org.zkoss.zul.api.Caption getCaptionApi();

	/**
	 * Returns the border. In fact, the name of
	 * the border (except "normal") is generate as part of the style class used
	 * for the content block.
	 * 
	 * <p>
	 * Default: "none".
	 */
	public String getBorder();

	/**
	 * Sets the border (either none or normal).
	 * 
	 * @param border
	 *            the border. If null or "0", "none" is assumed.
	 */
	public void setBorder(String border);

	/**
	 * Returns the title. Besides this attribute, you could use {@link Caption}
	 * to define a more sophiscated caption (aka., title).
	 * <p>
	 * If a window has a caption whose label ({@link Caption#getLabel}) is not
	 * empty, then this attribute is ignored.
	 * <p>
	 * Default: empty.
	 */
	public String getTitle();

	/**
	 * Sets the title.
	 */
	public void setTitle(String title);

	/**
	 * Adds the toolbar of the panel by these names, "tbar", "bbar", and "fbar".
	 * "tbar" is the name of top toolbar, and "bbar" the name of bottom toolbar,
	 * and "fbar" the name of foot toolbar.
	 * 
	 * @param name
	 *            "tbar", "bbar", and "fbar".
	 */
	public boolean addToolbarApi(String name, Toolbar toolbar);

	/**
	 * Returns the top toolbar of this panel.
	 */
	public org.zkoss.zul.api.Toolbar getTopToolbarApi();

	/**
	 * Returns the bottom toolbar of this panel.
	 */
	public org.zkoss.zul.api.Toolbar getBottomToolbarApi();

	/**
	 * Returns the foot toolbar of this panel.
	 */
	public org.zkoss.zul.api.Toolbar getFootToolbarApi();

	/**
	 * Returns the panelchildren of this panel.
	 */
	public org.zkoss.zul.api.Panelchildren getPanelchildrenApi();

}
