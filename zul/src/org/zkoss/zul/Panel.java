/* Panel.java

	Purpose:
		
	Description:
		
	History:
		Jun 10, 2008 11:39:33 AM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zk.ui.event.MinimizeEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.ext.Framable;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * Panel is a container that has specific functionality and structural components
 * that make it the perfect building block for application-oriented user interfaces.
 * The Panel contains bottom, top, and foot toolbars, along with separate header,
 * footer and body sections. It also provides built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool 
 * buttons that can be wired up to provide other customized behavior. Panels can
 * be easily embedded into any kind of ZUL component that is allowed to have children
 * or layout component. Panels also provide specific features like float and move.
 * Unlike {@link Window}, Panels can only be floated and moved inside its parent
 * node, which is not using zk.setVParent() function at client side. In other words,
 * if Panel's parent node is an relative position, the floated panel is only inside
 * its parent, not the whole page.
 * The second difference of {@link Window} is that Panel is not an independent ID
 * space (by implementing {@link IdSpace}), so the ID of each child can be used 
 * throughout the panel.
 * 
 * <p>Events:<br/>
 * onMove, onOpen, onZIndex, onMaximize, onMinimize, and onClose.<br/>
 * 
 * <p>Default {@link #getZclass}: z-panel.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Panel extends XulElement implements Framable {
	private Toolbar _tbar, _bbar, _fbar;
	private Panelchildren _panelchildren;
	private Caption _caption;

	private String _border = "none";
	private String _title = "";
	private int _minheight = 100, _minwidth = 200, _minzindex = -1;
	private boolean _closable, _collapsible, _floatable, _movable, _maximizable, _minimizable, _maximized, _minimized,
			_sizable, _open = true, _framableBC/*backward compatible*/;

	static {
		addClientEvent(Panel.class, Events.ON_CLOSE, 0);
		addClientEvent(Panel.class, Events.ON_MOVE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Panel.class, Events.ON_SIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Panel.class, Events.ON_OPEN, CE_IMPORTANT);
		addClientEvent(Panel.class, Events.ON_Z_INDEX, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Panel.class, Events.ON_MAXIMIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Panel.class, Events.ON_MINIMIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
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
			smartUpdate("open", _open);
		}
	}

	/**
	 * @deprecated As of release 5.0.6, replaced with {@link #getBorder}.
	 * Returns whether to render the panel with custom rounded borders.
	 * <p>Default: false.
	 */
	public boolean isFramable() {
		return _border.startsWith("rounded"); //rounded or rounded+
	}

	/**
	 * @deprecated As of release 5.0.6, replaced with {@link #setBorder}.
	 * Sets whether to render the panel with custom rounded borders.
	 * 
	 * <p>Default: false.
	 */
	public void setFramable(boolean framable) {
		_framableBC = true;
		boolean bordered = "normal".equals(_border) || "rounded+".equals(_border);
		setBorder0(framable ? bordered ? "rounded+" : "rounded" : bordered ? "normal" : "none");
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
			smartUpdate("movable", _movable);
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

	public boolean setVisible(boolean visible) {
		if (visible == isVisible())
			return visible;
		_maximized = _minimized = false;
		return setVisible0(visible);
	}

	private boolean setVisible0(boolean visible) {
		return super.setVisible(visible);
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
			smartUpdate("floatable", _floatable);
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
	 * @exception UiException if {@link #isMaximizable} is false.
	 */
	public void setMaximized(boolean maximized) {
		if (_maximized != maximized) {
			if (!_maximizable)
				throw new UiException("Not maximizable, " + this);

			_maximized = maximized;
			if (_maximized) {
				_minimized = false;
				setVisible0(true); //avoid dead loop
			}
			smartUpdate("maximized", _maximized);
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
			smartUpdate("maximizable", _maximizable);
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
	 * @exception UiException if {@link #isMinimizable} is false.
	 */
	public void setMinimized(boolean minimized) {
		if (_minimized != minimized) {
			if (!_minimizable)
				throw new UiException("not minimizable, " + this);

			_minimized = minimized;
			if (_minimized) {
				_maximized = false;
				setVisible0(false); //avoid dead loop
			} else
				setVisible0(true);
			smartUpdate("minimized", _minimized);
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
	 * of minimizing a panel is implementation-specific, so the MinimizeEvent
	 * event must be handled and a custom minimize behavior implemented for this
	 * option to be useful.
	 * 
	 * <p>Default: false. 
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 * @see MinimizeEvent
	 */
	public void setMinimizable(boolean minimizable) {
		if (_minimizable != minimizable) {
			_minimizable = minimizable;
			smartUpdate("minimizable", _minimizable);
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
	 * <p>Note: onOpen event will be sent when you click the toggle button
	 */
	public void setCollapsible(boolean collapsible) {
		if (_collapsible != collapsible) {
			_collapsible = collapsible;
			smartUpdate("collapsible", _collapsible);
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
			smartUpdate("closable", _closable);
		}
	}

	/**
	 * Sets the minimum height in pixels allowed for this panel. If negative, 100 is assumed.
	 * <p>Default: 100. 
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @since 5.0.0
	 */
	public void setMinheight(int minheight) {
		if (minheight < 0)
			minheight = 100;
		if (_minheight != minheight) {
			_minheight = minheight;
			smartUpdate("minheight", _minheight);
		}
	}

	/**
	 * Returns the minimum height.
	 * <p>Default: 100.
	 * @since 5.0.0
	 */
	public int getMinheight() {
		return _minheight;
	}

	/**
	 * Sets the minimum width in pixels allowed for this panel. If negative, 200 is assumed.
	 * <p>Default: 200. 
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @since 5.0.0
	 */
	public void setMinwidth(int minwidth) {
		if (minwidth < 0)
			minwidth = 200;
		if (_minwidth != minwidth) {
			_minwidth = minwidth;
			smartUpdate("minwidth", _minwidth);
		}
	}

	/**
	 * Returns the minimum width.
	 * <p>Default: 200.
	 * @since 5.0.0
	 */
	public int getMinwidth() {
		return _minwidth;
	}

	/**
	 * Returns the minimal Z index.
	 * <p>Default: -1 means system default;
	 *
	 * @since 8.5.1
	 */
	public int getMinzindex() {
		return this._minzindex;
	}

	/**
	 * Sets the minimal Z index.
	 *
	 * @since 8.5.1
	 */
	public void setMinzindex(int minzindex) {
		if (minzindex < -1)
			minzindex = -1;
		if (_minzindex != minzindex) {
			_minzindex = minzindex;
			smartUpdate("minzindex", minzindex);
		}
	}

	public void setHflex(String flex) {
		super.setHflex(flex);
		Panelchildren pc = getPanelchildren();
		if (pc != null)
			pc.smartUpdate("hflex", flex);
	}

	public void setVflex(String flex) {
		super.setVflex(flex);
		Panelchildren pc = getPanelchildren();
		if (pc != null)
			pc.smartUpdate("vflex", flex);
	}

	/** Returns whether the panel is sizable.
	 * @since 5.0.0
	 */
	public boolean isSizable() {
		return _sizable;
	}

	/** Sets whether the panel is sizable.
	 * If true, an user can drag the border to change the panel width.
	 * <p>Default: false.
	 * @since 5.0.0
	 */
	public void setSizable(boolean sizable) {
		if (_sizable != sizable) {
			_sizable = sizable;
			smartUpdate("sizable", sizable);
		}
	}

	/** Returns the caption of this panel.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns the border.
	 *
	 * <p>Default: "none".
	 */
	public String getBorder() {
		if (_framableBC && _border.startsWith("rounded")) //backward compatible
			return "rounded".equals(_border) ? "none" : "normal";
		return _border;
	}

	/** Sets the border.
	 * Allowed values include <code>none</code> (default), <code>normal</code>,
	 * <code>rounded</code> and <code>rounded+</code>.
	 * For more information, please refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Panel#Border">ZK Component Reference: Panel</a>.
	 * @param border the border. If null, "0" or "false", "none" is assumed.
	 * If "true", "normal" is assumed (since 5.0.8).
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border) || "false".equals(border))
			border = "none";
		else if ("true".equals(border))
			border = "normal";
		if (_framableBC) {
			if (border.startsWith("rounded")) {
				_framableBC = false;
			} else if ("normal".equals(border)) {
				if (_border.startsWith("rounded"))
					border = "rounded+";
			} else {
				if (_border.startsWith("rounded"))
					border = "rounded";
			}
		}

		setBorder0(border);
	}

	/** Enables or disables the border.
	 * @param border whether to have a border. If true is specified,
	 * it is the same as <code>setBorder("normal")</code>.
	 * @since 5.0.8
	 */
	public void setBorder(boolean border) {
		setBorder(border ? "normal" : "none");
	}

	private void setBorder0(String border) {
		if (!Objects.equals(_border, border)) {
			_border = border;
			smartUpdate("border", _border);
		}
	}

	/** 
	 * Returns the title.
	 * Besides this attribute, you could use {@link Caption} to define
	 * a more sophisticated caption (a.k.a., title).
	 * <p>If a panel has a caption whose label ({@link Caption#getLabel})
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
			smartUpdate("title", _title);
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
		Component refChild = null;
		if ("tbar".equals(name)) {
			if (_tbar != null)
				throw new UiException("Only one top toolbar child is allowed: " + this);
			refChild = this.getFirstChild();
		} else if ("bbar".equals(name)) {
			if (_bbar != null)
				throw new UiException("Only one bottom toolbar child is allowed: " + this);
			refChild = _fbar;
		} else if ("fbar".equals(name)) {
			if (_fbar != null)
				throw new UiException("Only one foot toolbar child is allowed: " + this);
		} else {
			throw new UiException("Unknown toolbar: " + name);
		}

		if (super.insertBefore(toolbar, refChild)) {
			if ("tbar".equals(name)) {
				_tbar = toolbar;
				response(new AuSetAttribute(this, "tbar", toolbar.getUuid()));
			} else if ("bbar".equals(name)) {
				_bbar = toolbar;
				response(new AuSetAttribute(this, "bbar", toolbar.getUuid()));
			} else if ("fbar".equals(name)) {
				_fbar = toolbar;
				response(new AuSetAttribute(this, "fbar", toolbar.getUuid()));
			}
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

	public String getZclass() {
		return _zclass == null ? "z-panel" : _zclass;
	}

	//ZK-3678: Provide a switch to enable/disable iscroll
	/*package*/ boolean isNativeScrollbar() {
		return Utils.testAttribute(this, "org.zkoss.zul.nativebar", true, true);
	}

	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Caption) {
			if (_caption != null && _caption != newChild)
				throw new UiException("Only one caption is allowed: " + this);
		} else if (refChild instanceof Caption) {
			throw new UiException("caption must be the first child");
		} else if (newChild instanceof Panelchildren) {
			if (_panelchildren != null && _panelchildren != newChild)
				throw new UiException("Only one panelchildren child is allowed: " + this);
		} else if (newChild instanceof Toolbar) {
			if (refChild instanceof Panelchildren || (refChild == null && (getChildren().isEmpty()))) {
				if (_tbar != null && _tbar != newChild)
					throw new UiException("Only one top toolbar child is allowed: " + this);
			} else if (refChild == null || refChild == _fbar) {
				if (_bbar != null && _bbar != newChild) {
					if (refChild != null && refChild == _fbar)
						throw new UiException("Only one bottom toolbar child is allowed: " + this);
					if (_fbar != null && _fbar != newChild)
						throw new UiException("Only one foot toolbar child is allowed: " + this);
				}
			} else {
				throw new UiException("Only three toolbars child is allowed: " + this);
			}
		} else {
			throw new UiException("Unsupported child for Panel: " + newChild);
		}
		super.beforeChildAdded(newChild, refChild);
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Caption) {
			refChild = getFirstChild();
			//always makes caption as the first child
			if (super.insertBefore(newChild, refChild)) {
				_caption = (Caption) newChild;
				return true;
			}
		} else if (newChild instanceof Panelchildren) {
			if (super.insertBefore(newChild, refChild)) {
				_panelchildren = (Panelchildren) newChild;
				return true;
			}
		} else if (newChild instanceof Toolbar) {
			if (super.insertBefore(newChild, refChild)) {
				if (refChild instanceof Panelchildren
						|| (refChild == null && (getChildren().size() == (_caption != null ? 2 : 1)))) {
					_tbar = (Toolbar) newChild;
				} else if (refChild == null || refChild == _fbar) {
					if (_bbar != null && _bbar != newChild) {
						_fbar = (Toolbar) newChild;
					} else {
						_bbar = (Toolbar) newChild;
					}
				}
				return true;
			}
		} else {
			return super.insertBefore(newChild, refChild);
			//impossible but to make it more extensible
		}
		return false;
	}

	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (_caption == child)
			_caption = null;
		else if (_tbar == child)
			_tbar = null;
		else if (_bbar == child)
			_bbar = null;
		else if (_panelchildren == child)
			_panelchildren = null;
		else if (_fbar == child)
			_fbar = null;
	}

	//Cloneable//
	public Object clone() {
		final Panel clone = (Panel) super.clone();
		clone.afterUnmarshal();
		return clone;
	}

	private void afterUnmarshal() {
		if (_caption != null)
			_caption = (Caption) getChildren().get(_caption.getParent().getChildren().indexOf(_caption));
		if (_tbar != null)
			_tbar = (Toolbar) getChildren().get(_tbar.getParent().getChildren().indexOf(_tbar));
		if (_panelchildren != null)
			_panelchildren = (Panelchildren) getChildren()
					.get(_panelchildren.getParent().getChildren().indexOf(_panelchildren));
		if (_bbar != null)
			_bbar = (Toolbar) getChildren().get(_bbar.getParent().getChildren().indexOf(_bbar));
		if (_fbar != null)
			_fbar = (Toolbar) getChildren().get(_fbar.getParent().getChildren().indexOf(_fbar));
	}

	//-- Serializable --//
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		//afterUnmarshal(); // B50-ZK-261: no afterUnmarshal() as now the fields are non-transient
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (_title.length() > 0)
			render(renderer, "title", _title);

		render(renderer, "closable", _closable);
		render(renderer, "floatable", _floatable);
		render(renderer, "collapsible", _collapsible);
		render(renderer, "movable", _movable);
		render(renderer, "maximizable", _maximizable);
		render(renderer, "minimizable", _minimizable);
		render(renderer, "maximized", _maximized);
		render(renderer, "minimized", _minimized);
		render(renderer, "sizable", _sizable);

		if (_minheight != 100)
			renderer.render("minheight", _minheight);
		if (_minwidth != 200)
			renderer.render("minwidth", _minwidth);
		if (_minzindex >= 0)
			renderer.render("minzindex", _minzindex);

		if (!_open)
			renderer.render("open", false);

		if (!"none".equals(_border))
			renderer.render("border", _border);

		//ZK-3678: Provide a switch to enable/disable iscroll
		if (isNativeScrollbar())
			renderer.render("_nativebar", true);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_MAXIMIZE)) {
			MaximizeEvent evt = MaximizeEvent.getMaximizeEvent(request);
			setLeftDirectly(evt.getLeft());
			setTopDirectly(evt.getTop());
			setWidthDirectly(evt.getWidth());
			setHeightDirectly(evt.getHeight());
			_maximized = evt.isMaximized();
			if (_maximized)
				setVisibleDirectly(true);
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_MINIMIZE)) {
			MinimizeEvent evt = MinimizeEvent.getMinimizeEvent(request);
			setLeftDirectly(evt.getLeft());
			setTopDirectly(evt.getTop());
			setWidthDirectly(evt.getWidth());
			setHeightDirectly(evt.getHeight());
			_minimized = evt.isMinimized();
			if (_minimized)
				setVisibleDirectly(false);
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
