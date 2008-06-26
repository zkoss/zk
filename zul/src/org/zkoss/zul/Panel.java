/* Panel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 10, 2008 11:39:33 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Maximizable;
import org.zkoss.zk.ui.ext.client.Minimizable;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zk.ui.ext.render.Floating;
import org.zkoss.zk.ui.ext.render.MultiBranch;
import org.zkoss.zul.impl.XulElement;

/**
 * Panel is a container that has specific functionality and structural components
 * that make it the perfect building block for application-oriented user interfaces.
 * The Panel contains bottom, top, and foot toolbars, along with separate header,
 * footer and body sections. It also provides built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool 
 * buttons that can be wired up to provide other customized behavior. Panels can
 * be easily embedded into any component or layout component. Panels also provide
 * specific behavior like the ability to float and move, unlike {@link Window}, 
 * Panels can only be floated and moved into its parent node not using zk.setVParent()
 * function at client side. In other words, if Panel's parent node is an absolute
 * position, the floated panel is only inside its parent.
 * The second difference of {@link Window} is that Panel is not an independent ID
 * space (by implementing {@link IdSpace}), so the ID of each child can be used 
 * throughout the panel.
 * 
 * <p>Events:<br/>
 * onMove, onOpen, onZIndex, onMaximize, onMinimize, and onClose.<br/>
 * 
 * <p>Default {@link #getSclass}: z-panel.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Panel extends XulElement {
	private transient Toolbar _tbar, _bbar, _fbar;
	private transient Panelchildren _panelchildren;
	private transient Caption _caption;

	private String _border = "none";
	private String _title = "";
	private boolean _closable, _collapsible, _floatable, _framable, _movable, 
		_maximizable, _minimizable, _maximized, _minimized;
	private boolean  _open = true;

	public Panel() {
		setSclass("z-panel");
	}
	/**
	 * Returns whether this Panel is open.
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** 
	 * Opens or closes this Panel.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("z.open", _open);
		}
	}
	/**
	 * Returns whether to render the panel with custom rounded borders.
	 * <p>Default: false.
	 */
	public boolean isFramable() {
		return _framable;
	}
	/**
	 * Sets whether to render the panel with custom rounded borders.
	 * 
	 * <p>Default: false.
	 */
	public void setFramable(boolean framable) {
		if (_framable != framable) {
			_framable = framable;
			invalidate();
		}
	}
	/**
	 * Sets whether to move the panel to display it inline where it is rendered.
	 * 
	 * <p>Default: false;
	 * <p>Note that this method only applied when {@link #isFloatable()} is true.
	 */
	public void setMovable(boolean movable) {
		if (_movable != movable) {
			_movable = movable;
			invalidate();
		}
	}
	/**
	 * Returns whether to move the panel to display it inline where it is rendered.
	 * <p>Default: false.
	 */
	public boolean isMovable() {
		return _movable;
	}
	/**
	 * Returns whether to float the panel to display it inline where it is rendered.
	 * <p>Default: false.
	 */
	public boolean isFloatable() {
		return _floatable;
	}
	/**
	 * Sets whether to float the panel to display it inline where it is rendered.
	 * 
	 * <p>Note that by default, setting floatable to true will cause the
     * panel to display at default offsets, which depend on the offsets of 
     * the embedded panel from its element to <i>document.body</i> -- because the panel
     * is absolute positioned, the position must be set explicitly by {@link #setTop(String)}
     * and {@link #setLeft(String)}. Also, when floatable a panel you should always
     * assign a fixed width, otherwise it will be auto width and will expand to fill
     * to the right edge of the viewport.
	 */
	public void setFloatable(boolean floatable) {
		if (_floatable != floatable) {
			_floatable = floatable;
			invalidate();
		}
	}
	/**
	 * Returns whether the panel is maximized.
	 */
	public boolean isMaximized() {
		return _maximized;
	}
	/**
	 * Sets whether the panel is maximized, and then the size of the panel will depend 
	 * on it to show a appropriate size. In other words, if true, the size of the
	 * panel will count on the size of its offset parent node whose position is
	 * absolute (by {@link #isFloatable()}) or its parent node. Otherwise, its size
	 * will be original size. Note that the maximized effect will run at client's
	 * sizing phase not initial phase.
	 * 
	 * <p>Default: false.
	 * <p>Note: this method only applied when {@link #isMaximizable} is true.
	 */
	public void setMaximized(boolean maximized) {
		if (_maximized != maximized) {
			_maximized = maximized;
			smartUpdate("z.maximized", _maximized);
		}
	}
	/**
	 * Returns whether to display the maximizing button and allow the user to maximize
     * the panel. 
     * <p>Default: false.
	 */
	public boolean isMaximizable() {
		return _maximizable;
	}
	/**
     * Sets whether to display the maximizing button and allow the user to maximize
     * the panel, when a panel is maximized, the button will automatically
     * change to a restore button with the appropriate behavior already built-in
     * that will restore the panel to its previous size.
     * <p>Default: false.
     * 
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 */
	public void setMaximizable(boolean maximizable) {
		if (_maximizable != maximizable) {
			_maximizable = maximizable;
			invalidate();
		}
	}

	/**
	 * Returns whether the panel is minimized.
	 * <p>Default: false.
	 */
	public boolean isMinimized() {
		return _minimized;
	}
	/**
	 * Sets whether the panel is minimized.
	 * <p>Default: false.
	 */
	public void setMinimized(boolean minimized) {
		if (_minimized != minimized) {
			_minimized = minimized;
			smartUpdate("z.minimized", _minimized);
		}
	}
	/**
	 * Returns whether to display the minimizing button and allow the user to minimize
     * the panel. 
     * <p>Default: false.
	 */
	public boolean isMinimizable() {
		return _minimizable;
	}
	/**
     * Sets whether to display the minimizing button and allow the user to minimize
     * the panel. Note that this button provides no implementation -- the behavior
     * of minimizing a panel is implementation-specific, so the {@link MinimizeEvent}
     * event must be handled and a custom minimize behavior implemented for this
     * option to be useful.
     * 
     * <p>Default: false. 
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 */
	public void setMinimizable(boolean minimizable) {
		if (_minimizable != minimizable) {
			_minimizable = minimizable;
			invalidate();
		}
	}
	/**
	 * Returns whether to show a toggle button on the title bar.
	 * <p>Default: false.
	 */
	public boolean isCollapsible() {
		return _collapsible;
	}
	/**
	 * Sets whether to show a toggle button on the title bar.
	 * <p>Default: false.
	 * <p>Note: the toggle button won't be displayed if no title or caption at all.
	 */
	public void setCollapsible(boolean collapsible) {
		if (_collapsible != collapsible) {
			_collapsible = collapsible;
			invalidate(); //re-init is required
		}
	}
	/** Returns whether to show a close button on the title bar.
	 */
	public boolean isClosable() {
		return _closable;
	}
	/** Sets whether to show a close button on the title bar.
	 * If closable, a button is displayed and the onClose event is sent
	 * if an user clicks the button.
	 *
	 * <p>Default: false.
	 *
	 * <p>You can intercept the default behavior by either overriding
	 * {@link #onClose}, or listening the onClose event.
	 *
	 * <p>Note: the close button won't be displayed if no title or caption at all.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			invalidate(); //re-init is required
		}
	}
	/** Returns the caption of this panel.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns the border.
	 * The border actually controls via {@link Panelchildren#getRealSclass()}. 
	 * In fact, the name of the border (except "normal") is generate as part of 
	 * the style class used for the content block.
	 * Refer to {@link Panelchildren#getRealSclass()} for more details.
	 *
	 * <p>Default: "none".
	 */
	public String getBorder() {
		return _border;
	}
	/** Sets the border (either none or normal).
	 *
	 * @param border the border. If null or "0", "none" is assumed.
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border))
			border = "none";
		if (!Objects.equals(_border, border)) {
			_border = border;
			invalidate();
		}
	}

	/** 
	 * Returns the title.
	 * Besides this attribute, you could use {@link Caption} to define
	 * a more sophiscated caption (aka., title).
	 * <p>If a window has a caption whose label ({@link Caption#getLabel})
	 * is not empty, then this attribute is ignored.
	 * <p>Default: empty.
	 */
	public String getTitle() {
		return _title;
	}
	/** Sets the title.
	 */
	public void setTitle(String title) {
		if (title == null)
			title = "";
		if (!Objects.equals(_title, title)) {
			_title = title;
			if (_caption != null) _caption.invalidate();
			else invalidate();
		}
	}
	
	/**
	 * Adds the toolbar of the panel by these names, "tbar", "bbar", and "fbar".
	 * "tbar" is the name of top toolbar, and "bbar" the name of bottom toolbar,
	 * and "fbar" the name of foot toolbar.
	 * 
	 * @param name "tbar", "bbar", and "fbar". 
	 */
	public boolean addToolbar(String name, Toolbar toolbar) {
		if ("tbar".equals(name)) {
			if (_tbar != null)
				throw new UiException("Only one top toolbar child is allowed: "+this);
			_tbar = toolbar;
		} else if ("bbar".equals(name)) {
			if (_bbar != null)
				throw new UiException("Only one bottom toolbar child is allowed: "+this);
			_bbar = toolbar;
		} else if ("fbar".equals(name)) {
			if (_fbar != null)
				throw new UiException("Only one foot toolbar child is allowed: "+this);
			_fbar = toolbar;
		} else {
			throw new UiException("Uknown toolbar: "+name);
		}
		if(super.insertBefore(toolbar, null)) {
			invalidate();
			return true;
		}
		return false;
	}
	
	/** Process the onClose event sent when the close button is pressed.
	 * <p>Default: detach itself.
	 */
	public void onClose() {
		detach();
	}
	public String getRealStyle() {
		return super.getRealStyle() + (isVisible() && isMinimized() ? "display:none;" : "");
	}
	public String getRealSclass() {
		final String scls = super.getRealSclass();
		return scls + ("normal".equals(_border) ? "" : ' ' + scls + '-' + _border)
			+ (_open ? "" : " z-panel-collapsed");
	}

	/** Returns the style class used for the title.
	 *
	 * <p>It returns "<i>sclass</i>-t" is returned,
	 * where <i>sclass</i> is the value returned by {@link #getSclass}.
	 */
	public String getTitleSclass() {
		return getSclass() + "-t";
	}
	
	/**
	 * Returns the top toolbar of this panel.
	 */
	public Toolbar getTopToolbar() {
		return _tbar;
	}

	/**
	 * Returns the bottom toolbar of this panel.
	 */
	public Toolbar getBottomToolbar() {
		return _bbar;
	}

	/**
	 * Returns the foot toolbar of this panel.
	 */
	public Toolbar getFootToolbar() {
		return _fbar;
	}

	/**
	 * Returns the panelchildren of this panel.
	 */
	public Panelchildren getPanelchildren() {
		return _panelchildren;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_MOVE);
		appendAsapAttr(sb, Events.ON_Z_INDEX);
		appendAsapAttr(sb, Events.ON_OPEN);
		appendAsapAttr(sb, Events.ON_MAXIMIZE);
		appendAsapAttr(sb, Events.ON_MINIMIZE);
		//no need to generate ON_CLOSE since it is always sent (as ASAP)

		final String clkattrs = getAllOnClickAttrs();
		if (clkattrs != null) sb.append(clkattrs);
		if (_panelchildren != null)
			HTMLs.appendAttribute(sb, "z.children", _panelchildren.getUuid());
		if (_closable)
			sb.append(" z.closable=\"true\"");
		if (_floatable)
			sb.append(" z.floatable=\"true\"");
		if (_collapsible)
			sb.append(" z.collapsible=\"true\"");
		if (_framable)
			sb.append(" z.framable=\"true\"");
		if (_movable)
			sb.append(" z.movable=\"true\"");
		if (_maximizable)
			sb.append(" z.maximizable=\"true\"");
		if (_minimizable)
			sb.append(" z.minimizable=\"true\"");
		if (_maximized)
			sb.append(" z.maximized=\"true\"");
		if (_minimized)
			sb.append(" z.minimized=\"true\"");
		if (_open)
			sb.append(" z.open=\"true\"");
		
		return sb.toString();
	}
	
	//-- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Caption) {
			if (_caption != null && _caption != newChild)
				throw new UiException("Only one caption is allowed: "+this);
			refChild = getFirstChild();
				//always makes caption as the first child
			_caption = (Caption)newChild;
		} else if (refChild instanceof Caption) {
			throw new UiException("caption must be the first child");
		} else if (newChild instanceof Panelchildren) {
			if (_panelchildren != null && _panelchildren != newChild)
				throw new UiException("Only one panelchildren child is allowed: "+this);
			_panelchildren = (Panelchildren) newChild;
		} else if (newChild instanceof Toolbar) {
			if (refChild instanceof Panelchildren || (refChild == null && (getChildren().isEmpty()))) {
				if(_tbar != null && _tbar != newChild)
					throw new UiException("Only one top toolbar child is allowed: "+this);
				_tbar = (Toolbar) newChild;
			} else if (refChild == null || refChild == _fbar) {
				if (_bbar != null && _bbar != newChild) {
					if (refChild != null && refChild == _fbar) 
						throw new UiException("Only one bottom toolbar child is allowed: "+this);
					if (_fbar != null && _fbar != newChild)
						throw new UiException("Only one foot toolbar child is allowed: "+this);
					_fbar = (Toolbar) newChild;
				} else {
					_bbar = (Toolbar) newChild; 
				}
			} else {
				throw new UiException("Only three toolbars child is allowed: " + this);
			}
			
		} else {
			throw new UiException("Unsupported child for Panel: " + newChild);
		}
		if(super.insertBefore(newChild, refChild)) {
			invalidate();
			return true;
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (_caption == child) _caption = null;
		else if (_tbar == child) _tbar = null;
		else if (_bbar == child) _bbar = null;
		else if (_panelchildren == child) _panelchildren = null;
		else if (_fbar == child) _fbar = null;
		invalidate();
	}
	
	//Cloneable//
	public Object clone() {
		final Panel clone = (Panel) super.clone();
		clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		if (_caption != null)
			_caption = (Caption) getChildren().get(_caption.getParent()
					.getChildren().indexOf(_caption));
		if (_tbar != null)
			_tbar = (Toolbar) getChildren().get(_tbar.getParent()
					.getChildren().indexOf(_tbar));
		if (_panelchildren != null)
			_panelchildren = (Panelchildren) getChildren().get(_panelchildren.getParent()
					.getChildren().indexOf(_panelchildren));
		if (_bbar != null)
			_bbar = (Toolbar) getChildren().get(_bbar.getParent()
					.getChildren().indexOf(_bbar));
		if (_fbar != null)
			_fbar = (Toolbar) getChildren().get(_fbar.getParent()
					.getChildren().indexOf(_fbar));
	}
	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements MultiBranch, Openable, Floating, Maximizable, Minimizable {
		//-- MultiBranch --//
		public boolean inDifferentBranch(Component child) {
			return child instanceof Caption; //in different branch
		}
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open; 
		}
		//Floating//
		public boolean isFloating() {
			return _floatable;
		}
		public void setMaximizedByClient(boolean maximized) {
			_maximized = maximized;
		}
		public void setMinimizedByClient(boolean minimized) {
			_minimized = minimized;
		}
	}
}
