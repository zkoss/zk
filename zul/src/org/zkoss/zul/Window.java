/* Window.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 19:29:13     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zk.ui.event.MinimizeEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.ext.Framable;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A window.
 *
 * <p>Unlike other elements, each {@link Window} is an independent ID space
 * (by implementing {@link org.zkoss.zk.ui.IdSpace}).
 * It means a window and all its descendants forms a ID space and
 * the ID of each of them is unique in this space.
 * You could retrieve any of them in this space by calling {@link #getFellow}.
 *
 * <p>If a window X is a descendant of another window Y, X's descendants
 * are not visible in Y's space. To retrieve a descendant, say Z, of X, 
 * you have to invoke Y.getFellow('X').getFellow('Z').
 *
 * <p>Events:<br/>
 * onMove, onOpen, onMaximize, onMinimize, and onClose.<br/>
 * Note: to have better performance, onOpen is sent only if a
 * non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * <p><code>onMaximize</code> and <code>onMinimize</code> are supported. (since 3.5.0)
 *
 * <p><code>onClose</code> is sent when the close button is pressed
 * (if {@link #isClosable} is true). The window has to detach or hide
 * the window. By default, {@link #onClose} detaches the window. To prevent
 * it from detached, you have to call {@link org.zkoss.zk.ui.event.Event#stopPropagation}
 * to prevent {@link #onClose} is called.
 *
 * <p>On the other hand, <code>onOpen</code> is sent when a popup
 * window (i.e., {@link #getMode} is popup) is closed due to user's activity
 * (such as press ESC). This event is only a notification.
 * In other words, the popup is hidden before the event is sent to the server.
 * The application cannot prevent the window from being hidden.
 * 
 * <p>Default {@link #getZclass}: z-window.(since 3.5.0)
 * @author tomyeh
 */
public class Window extends XulElement implements Framable, IdSpace {
	private static final Logger log = LoggerFactory.getLogger(Window.class);
	private static final long serialVersionUID = 20100721L;

	private transient Caption _caption;

	private String _border = "none";
	private String _title = "";
	/** One of MODAL, _MODAL_, EMBEDDED, OVERLAPPED, HIGHLIGHTED, POPUP. */
	private int _mode = EMBEDDED;
	/** Used for doModal. */
	private Mutex _mutex = new Mutex();
	/** The style used for the content block. */
	private String _cntStyle;
	/** The style class used for the content block. */
	private String _cntSclass;
	/** How to position the window. */
	private String _pos;
	/** Whether to show a close button. */
	private boolean _closable;
	/** Whether the window is sizable. */
	private boolean _sizable;
	/** Whether to show the shadow. */
	private boolean _shadow = true;

	private boolean _maximizable, _minimizable, _maximized, _minimized;
	private int _minheight = 100, _minwidth = 200, _minzindex = -1;

	/** Embeds the window as normal component. */
	public static final int EMBEDDED = 0;
	/** Makes the window as a modal dialog. once {@link #doModal}
	 * is called, the execution of the event processing thread
	 * is suspended until one of the following occurs.
	 * <ol>
	 * <li>{@link #setMode} is called with a mode other than MODAL.</li>
	 * <li>Either {@link #doOverlapped}, {@link #doPopup},
	 * {@link #doEmbedded}, or {@link #doHighlighted} is called.</li>
	 * <li>{@link #setVisible} is called with false.</li>
	 * <li>The window is detached from the window.</li>
	 * </ol>
	 *
	 * <p>Note: In the last two cases, the mode becomes {@link #OVERLAPPED}.
	 * In other words, one might say a modal window is a special overlapped window.
	 *
	 * @see #HIGHLIGHTED
	 */
	public static final int MODAL = 1;
	//Represent a modal when the event thread is disabled (internal)
	private static final int _MODAL_ = -100;
	/** Makes the window as overlapped other components.
	 */
	public static final int OVERLAPPED = 2;
	/** Makes the window as popup.
	 * It is similar to {@link #OVERLAPPED}, except it is auto hidden
	 * when user clicks outside of the window.
	 */
	public static final int POPUP = 3;
	/** Makes the window as highlighted.
	 * Its visual effect is the same as {@link #MODAL}.
	 * However, from the server side's viewpoint, it is similar to
	 * {@link #OVERLAPPED}. The execution won't be suspended when
	 * {@link #doHighlighted} is called.
	 *
	 * @see #MODAL
	 * @see #OVERLAPPED
	 */
	public static final int HIGHLIGHTED = 4;

	static {
		addClientEvent(Window.class, Events.ON_CLOSE, 0);
		addClientEvent(Window.class, Events.ON_MOVE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Window.class, Events.ON_SIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Window.class, Events.ON_OPEN, CE_IMPORTANT);
		addClientEvent(Window.class, Events.ON_Z_INDEX, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Window.class, Events.ON_MAXIMIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		addClientEvent(Window.class, Events.ON_MINIMIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT);
	}

	public Window() {
		setAttribute("z$is", Boolean.TRUE); //optional but optimized to mean no need to generate z$is since client handles it
	}

	/**
	 * @param title the window title (see {@link #setTitle}).
	 * @param border the border (see {@link #setBorder}).
	 * @param closable whether it is closable (see {@link #setClosable}).
	 */
	public Window(String title, String border, boolean closable) {
		this();
		setTitle(title);
		setBorder(border);
		setClosable(closable);
	}

	/**
	 * Returns whether the window is maximized.
	 * @since 3.5.0
	 */
	public boolean isMaximized() {
		return _maximized;
	}

	/**
	 * Sets whether the window is maximized, and then the size of the window will depend 
	 * on it to show a appropriate size. In other words, if true, the size of the
	 * window will count on the size of its offset parent node whose position is
	 * absolute (by not {@link #inEmbedded()}) or its parent node. Otherwise, its size
	 * will be original size. Note that the maximized effect will run at client's
	 * sizing phase not initial phase.
	 * 
	 * <p>Default: false.
	 * @exception UiException if {@link #isMaximizable} is false.
	 * @since 3.5.0
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
	 * the window. 
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isMaximizable() {
		return _maximizable;
	}

	/**
	 * Sets whether to display the maximizing button and allow the user to maximize
	 * the window, when a window is maximized, the button will automatically
	 * change to a restore button with the appropriate behavior already built-in
	 * that will restore the window to its previous size.
	 * <p>Default: false.
	 * 
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 * @since 3.5.0
	 */
	public void setMaximizable(boolean maximizable) {
		if (_maximizable != maximizable) {
			_maximizable = maximizable;
			smartUpdate("maximizable", _maximizable);
		}
	}

	/**
	 * Returns whether the window is minimized.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isMinimized() {
		return _minimized;
	}

	/**
	 * Sets whether the window is minimized.
	 * <p>Default: false.
	 * @exception UiException if {@link #isMinimizable} is false.
	 * @since 3.5.0
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
	 * the window. 
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isMinimizable() {
		return _minimizable;
	}

	/**
	 * Sets whether to display the minimizing button and allow the user to minimize
	 * the window. Note that this button provides no implementation -- the behavior
	 * of minimizing a window is implementation-specific, so the MinimizeEvent
	 * event must be handled and a custom minimize behavior implemented for this
	 * option to be useful.
	 * 
	 * <p>Default: false. 
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 * @see MinimizeEvent
	 * @since 3.5.0
	 */
	public void setMinimizable(boolean minimizable) {
		if (_minimizable != minimizable) {
			_minimizable = minimizable;
			smartUpdate("minimizable", _minimizable);
		}
	}

	/**
	 * Sets the minimum height in pixels allowed for this window. If negative, 100 is assumed.
	 * <p>Default: 100. 
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @since 3.5.0
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
	 * @since 3.5.0
	 */
	public int getMinheight() {
		return _minheight;
	}

	/**
	 * Sets the minimum width in pixels allowed for this window. If negative, 200 is assumed.
	 * <p>Default: 200. 
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @since 3.5.0
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
	 * @since 3.5.0
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

	/**
	 * @deprecated As release of 5.0.0, replaced with {@link org.zkoss.zk.ui.HtmlBasedComponent#setAction}.
	 */
	public static void setDefaultActionOnShow(String onshow) {
	}

	/**
	 * @deprecated As release of 5.0.0, replaced with {@link org.zkoss.zk.ui.HtmlBasedComponent#setAction}.
	 */
	public static String getDefaultActionOnShow() {
		return null;
	}

	/** Returns the caption of this window.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns the border.
	 *
	 * <p>Default: "none".
	 */
	public String getBorder() {
		return _border;
	}

	/** Sets the border (either none or normal).
	 *
	 * @param border the border. If null, "0" or "false", "none" is assumed.
	 * If "true", "normal" is assumed (since 5.0.8).
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border) || "false".equals(border))
			border = "none";
		else if ("true".equals(border))
			border = "normal";
		if (!Objects.equals(_border, border)) {
			_border = border;
			smartUpdate("border", border);
		}
	}

	/** Enables or disables the border.
	 * @param border whether to have a border. If true is specified,
	 * it is the same as <code>setBorder("normal")</code>.
	 * @since 5.0.8
	 */
	public void setBorder(boolean border) {
		setBorder(border ? "normal" : "none");
	}

	/** Returns the title.
	 * Besides this attribute, you could use {@link Caption} to define
	 * a more sophisticated caption (a.k.a., title).
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
			smartUpdate("title", title);
		}
	}

	/** Returns the current mode.
	 * One of "modal", "embedded", "overlapped", "popup", and "highlighted".
	 * @see #getModeType
	 */
	public String getMode() {
		return modeToString(_mode);
	}

	private static String modeToString(int mode) {
		switch (mode) {
		case MODAL:
		case _MODAL_:
			return "modal";
		case POPUP:
			return "popup";
		case OVERLAPPED:
			return "overlapped";
		case HIGHLIGHTED:
			return "highlighted";
		default:
			return "embedded";
		}
	}

	/** Sets the mode.
	 * @since 6.0.0
	 */
	public void setMode(Mode mode) {
		setMode(mode.id);
	}
	
	/** Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 *
	 * <p>Notice: {@link Events#ON_MODAL} is posted if you specify
	 * "modal" to this method.
	 * Unlike {@link #doModal}, {@link Events#ON_MODAL} is posted, so
	 * the window will become modal later (since 3.0.4).
	 * In other words, setMode("modal") never suspends the execution
	 * of the current thread. On the other hand, {@link #doModal} will
	 * suspends the execution if executed in an event listener, or
	 * throws an exception if <em>not</em> executed in an event listener.
	 *
	 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Window">Overlapped, Popup, Modal, Highlighted and Embedded</a>
	 * for more information.
	 *
	 * @param name the mode which could be one of
	 * "embedded", "overlapped", "popup", "modal", "highlighted".
	 * Note: it cannot be "modal". Use {@link #doModal} instead.
	 */
	public void setMode(String name) {
		if ("popup".equals(name))
			doPopup();
		else if ("overlapped".equals(name))
			doOverlapped();
		else if ("embedded".equals(name))
			doEmbedded();
		else if ("modal".equals(name)) {
			if (isEventThreadEnabled(false))
				Events.postEvent(Events.ON_MODAL, this, null);
			else
				doModal();
		} else if ("highlighted".equals(name))
			doHighlighted();
		else
			throw new WrongValueException("Unknown mode: " + name);
	}
	
	/** Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 *
	 * @see #setMode(String)
	 */
	public void setMode(int mode) {
		switch (mode) {
		case POPUP:
			doPopup();
			break;
		case OVERLAPPED:
			doOverlapped();
			break;
		case EMBEDDED:
			doEmbedded();
			break;
		case MODAL:
			if (isEventThreadEnabled(false))
				Events.postEvent(Events.ON_MODAL, this, null);
			else
				doModal();
			break;
		case HIGHLIGHTED:
			doHighlighted();
			break;
		default:
			throw new WrongValueException("Unknown mode: " + mode);
		}
	}

	/** Returns the current mode.
	 * @see #getMode
	 * @see #setMode(Mode)
	 * @since 6.0.0
	 */
	public Mode getModeType() {
		return toModeType(_mode);
	}

	private static Mode toModeType(int mode) {
		switch (mode) {
		case MODAL:
		case _MODAL_:
			return Mode.MODAL;
		case POPUP:
			return Mode.POPUP;
		case OVERLAPPED:
			return Mode.OVERLAPPED;
		case HIGHLIGHTED:
			return Mode.HIGHLIGHTED;
		default:
			return Mode.EMBEDDED;
		}
	}

	/** Returns whether this is a modal dialog.
	 */
	public boolean inModal() {
		return _mode == MODAL || _mode == _MODAL_;
	}

	/** Returns whether this is embedded with other components (Default).
	 * @see #doEmbedded
	 */
	public boolean inEmbedded() {
		return _mode == EMBEDDED;
	}

	/** Returns whether this is a overlapped window.
	 */
	public boolean inOverlapped() {
		return _mode == OVERLAPPED;
	}

	/** Returns whether this is a popup window.
	 */
	public boolean inPopup() {
		return _mode == POPUP;
	}

	/** Returns whether this is a highlighted window.
	 */
	public boolean inHighlighted() {
		return _mode == HIGHLIGHTED;
	}

	/** Makes this window as a modal dialog.
	 * It will automatically center the window (ignoring {@link #getLeft} and
	 * {@link #getTop}).
	 *
	 * <p>Notice: though both setMode("modal") and doModal() both
	 * causes the window to become modal, they are a bit different.
	 * doModal causes the event listener to suspend immediately,
	 * while setMode("modal") posts an event ({@link Events#ON_MODAL}).
	 * That is, {@link #setMode} won't suspend the execution immediately,
	 * but {@link #doModal} will.
	 * {@link #doModal} can be called only in an event listener,
	 * while {@link #setMode} can be called anytime.
	 * @since 3.0.4
	 */
	public void doModal() {
		if (!isEventThreadEnabled(true)) {
			checkOverlappable(_MODAL_);
			setNonModalMode(_MODAL_);
			return;
		}

		checkOverlappable(MODAL);

		if (_mode != MODAL) {
			if (!Events.inEventListener())
				throw new SuspendNotAllowedException("doModal must be called in an event listener");

			int oldmode = _mode;
			boolean oldvisi = isVisible();

			setVisible(true); //if MODAL, it must be visible; vice versa

			try {
				enterModal();
			} catch (SuspendNotAllowedException ex) {
				handleFailedModal(oldmode, oldvisi);
				throw ex;
			}
		}
	}

	private void handleFailedModal(int oldmode, boolean oldvisi) {
		try {
			if (Executions.getCurrent().getAttribute("javax.servlet.error.exception") != null) {
				//handle it specially if it is used for displaying err
				setMode(HIGHLIGHTED);
			} else {
				setMode(oldmode); //restore
				setVisible(oldvisi);
			}
		} catch (Throwable ex) {
			log.error("Causing another error", ex);
		}
	}

	/** Makes this window as overlapped with other components.
	 */
	public void doOverlapped() {
		checkOverlappable(OVERLAPPED);
		setNonModalMode(OVERLAPPED);
	}

	/** Makes this window as popup, which is overlapped with other component
	 * and auto-hidden when user clicks outside of the window.
	 */
	public void doPopup() {
		checkOverlappable(POPUP);
		setNonModalMode(POPUP);
	}

	/** Makes this window as highlighted. The visual effect is
	 * the similar to the modal window, but, like overlapped,
	 * it doesn't suspend (block) the execution at the server.
	 * In other words, it is more like an overlapped window from the
	 * server side's viewpoint.
	 */
	public void doHighlighted() {
		checkOverlappable(HIGHLIGHTED);
		setNonModalMode(HIGHLIGHTED);
	}

	/** Makes this window as embedded with other components (Default).
	 */
	public void doEmbedded() {
		setNonModalMode(EMBEDDED);
	}

	/* Set non-modal mode. */
	private void setNonModalMode(int mode) {
		if (_mode != mode) {
			if (_mode == MODAL)
				leaveModal(mode);
			else {
				_mode = mode;
				smartUpdate("mode", modeToString(_mode));
			}
		}
		setVisible(true);
	}

	/** Set mode to MODAL and suspend this thread. */
	private void enterModal() {
		_mode = MODAL;
		smartUpdate("mode", modeToString(_mode));

		//no need to synchronized (_mutex) because no racing is possible
		try {
			Executions.wait(_mutex);
		} catch (InterruptedException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	/** Resumes the suspended thread and set mode to OVERLAPPED. */
	private void leaveModal(int mode) {
		_mode = mode;
		smartUpdate("mode", modeToString(_mode));

		Executions.notifyAll(_mutex);
	}

	private boolean isEventThreadEnabled(boolean attachedRequired) {
		Desktop desktop = getDesktop();
		if (desktop == null) {
			if (attachedRequired)
				throw new SuspendNotAllowedException("Not attached, " + this);

			final Execution exec = Executions.getCurrent();
			if (exec == null || (desktop = exec.getDesktop()) == null)
				return true; //assume enabled (safer)
		}
		return desktop.getWebApp().getConfiguration().isEventThreadEnabled();
	}

	/** Makes sure it is not draggable. */
	private void checkOverlappable(int mode) {
		if (!"false".equals(getDraggable()))
			throw new UiException("Draggable window cannot be modal, overlapped, popup, or highlighted: " + this);

		if (mode == MODAL)
			for (Component comp = this; (comp = comp.getParent()) != null;)
				if (!comp.isVisible())
					throw new UiException(
							"One of its ancestors, " + comp + ", is not visible, so unable to be modal or highlighted");
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
			smartUpdate("closable", closable); //re-init is required
		}
	}

	/** Returns whether the window is sizable.
	 */
	public boolean isSizable() {
		return _sizable;
	}

	/** Sets whether the window is sizable.
	 * If true, an user can drag the border to change the window width.
	 * <p>Default: false.
	 */
	public void setSizable(boolean sizable) {
		if (_sizable != sizable) {
			_sizable = sizable;
			smartUpdate("sizable", sizable);
		}
	}

	/** Returns whether to show the shadow of an overlapped/popup/modal
	 * window. It is meaningless if it is an embedded window.
	 * @since 3.6.0
	 */
	public boolean isShadow() {
		return _shadow;
	}

	/** Sets whether to show the shadow of an overlapped/popup/modal
	 * window. It is meaningless if it is an embedded window.
	 * <p>Default: true.
	 * @since 3.6.0
	 */
	public void setShadow(boolean shadow) {
		if (_shadow != shadow) {
			_shadow = shadow;
			smartUpdate("shadow", shadow);
		}
	}

	/** Returns how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * <p>Default: null which depends on {@link #getMode}:
	 * If overlapped or popup, {@link #setLeft} and {@link #setTop} are
	 * assumed. If modal or highlighted, it is centered.
	 */
	public String getPosition() {
		return _pos;
	}

	/** Sets how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * @param pos how to position. It can be null (the default), or
	 * a combination of the following values (by separating with comma).
	 * <dl>
	 * <dt>center</dt>
	 * <dd>Position the window at the center. {@link #setTop} and {@link #setLeft}
	 * are both ignored.</dd>
	 * <dt>nocenter</dt>
	 * <dd>Not to position the window at the center. A modal window, by default,
	 * will be position at the center. By specifying this value could
	 * prevent it and the real position depends on {@link #setTop} and {@link #setLeft} (since 5.0.4)</dd>
	 * <dt>left</dt>
	 * <dd>Position the window at the left edge. {@link #setLeft} is ignored.</dd>
	 * <dt>right</dt>
	 * <dd>Position the window at the right edge. {@link #setLeft} is ignored.</dd>
	 * <dt>top</dt>
	 * <dd>Position the window at the top edge. {@link #setTop} is ignored.</dd>
	 * <dt>bottom</dt>
	 * <dd>Position the window at the bottom edge. {@link #setTop} is ignored.</dd>
	 * <dt>parent</dt>
	 * <dd>Position the window relative to its parent.
	 * That is, the left and top ({@link #getTop} and {@link #getLeft})
	 * is an offset to his parent's let-top corner. (since 3.0.2)</dd>
	 * </dl>
	 * <p>For example, "left,center" means to position it at the center of
	 * the left edge.
	 */
	public void setPosition(String pos) {
		//Note: we always update since the window might be dragged by an user
		_pos = pos;
		smartUpdate("position", pos);
	}

	/** Process the onClose event sent when the close button is pressed.
	 * <p>Default: detach itself.
	 */
	public void onClose() {
		detach();
	}

	/** Process the onModal event by making itself a modal window.
	 */
	public void onModal() {
		doModal();
	}

	/** Returns the CSS style for the content block of the window.
	 */
	public String getContentStyle() {
		return _cntStyle;
	}

	/** Sets the CSS style for the content block of the window.
	 *
	 * <p>Default: null.
	 */
	public void setContentStyle(String style) {
		if (!Objects.equals(_cntStyle, style)) {
			_cntStyle = style;
			smartUpdate("contentStyle", _cntStyle);
		}
	}

	/** Returns the style class used for the content block.
	 *
	 * @see #setContentSclass
	 */
	public String getContentSclass() {
		return _cntSclass;
	}

	/** Sets the style class used for the content block.
	 *
	 * @see #getContentSclass
	 * @since 3.0.0
	 */
	public void setContentSclass(String scls) {
		if (!Objects.equals(_cntSclass, scls)) {
			_cntSclass = scls;
			smartUpdate("contentSclass", scls);
		}
	}

	/** Makes this window as topmost.
	 * It has no effect if this window is embedded.
	 * @since 5.0.0
	 */
	public void setTopmost() {
		smartUpdate("topmost", true);
	}

	//ZK-3678: Provide a switch to enable/disable iscroll
	/*package*/ boolean isNativeScrollbar() {
		return Utils.testAttribute(this, "org.zkoss.zul.nativebar", true, true);
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "title", _title);
		render(renderer, "maximized", _maximized);
		render(renderer, "maximizable", _maximizable);
		render(renderer, "minimized", _minimized);
		render(renderer, "minimizable", _minimizable);
		render(renderer, "closable", _closable);
		render(renderer, "sizable", _sizable);
		render(renderer, "position", _pos);
		render(renderer, "contentStyle", _cntStyle);
		render(renderer, "contentSclass", _cntSclass);
		if (_minheight != 100)
			renderer.render("minheight", _minheight);
		if (_minwidth != 200)
			renderer.render("minwidth", _minwidth);
		if (_minzindex >= 0)
			renderer.render("minzindex", _minzindex);
		if (!"none".equals(_border))
			renderer.render("border", _border);
		if (!isShadow())
			renderer.render("shadow", false);
		if (_mode != EMBEDDED)
			renderer.render("mode", modeToString(_mode));
		//render mode as the last property

		//ZK-3678: Provide a switch to enable/disable iscroll
		if (isNativeScrollbar())
			renderer.render("_nativebar", true);
	}

	public String getZclass() {
		return _zclass == null ? "z-window" : _zclass;
	}

	//-- Component --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: " + this);
		} else if (refChild instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		super.beforeChildAdded(child, refChild);
	}

	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Caption) {
			refChild = getFirstChild();
			//always makes caption as the first child
			if (super.insertBefore(child, refChild)) {
				_caption = (Caption) child;
				return true;
			}
			return false;
		}
		return super.insertBefore(child, refChild);
	}

	public void onChildRemoved(Component child) {
		if (child instanceof Caption)
			_caption = null;
		super.onChildRemoved(child);
	}

	public void onPageDetached(Page page) {
		if (_mode == MODAL && getPage() == null)
			leaveModal(OVERLAPPED);
	}

	/** Changes the visibility of the window.
	 *
	 * <p>Note if you turned on the event thread:<br/>
	 * If a modal dialog becomes invisible, the modal state
	 * will be ended automatically. In other words, the mode ({@link #getMode})
	 * will become {@link #OVERLAPPED} and the suspending thread is resumed.
	 * In other words, the modal window ({@link #MODAL}) can not be invisible
	 * (while a window in other modes could be invisible).
	 * <p>However, if the event thread is not enabled (default), there is no
	 * such limitation. In other words, it remains the same mode when becoming
	 * invisible.
	 */
	public boolean setVisible(boolean visible) {
		if (visible == isVisible())
			return visible;
		_maximized = _minimized = false;
		return setVisible0(visible);
	}

	private boolean setVisible0(boolean visible) {
		if (!visible && _mode == MODAL) {
			//Hide first to avoid unpleasant effect
			super.setVisible(false);
			leaveModal(OVERLAPPED);
			return true;
		}
		return super.setVisible(visible);
	}

	//-- super --//
	public void setDraggable(String draggable) {
		if (_mode != EMBEDDED) {
			if (draggable != null && (draggable.length() > 0 && !"false".equals(draggable)))
				throw new UiException("Only embedded window could be draggable: " + this);
		}
		super.setDraggable(draggable);
	}

	//Cloneable//
	public Object clone() {
		final Window clone = (Window) super.clone();
		clone._mutex = new Mutex();
		if (clone._caption != null)
			clone.afterUnmarshal();
		return clone;
	}

	private void afterUnmarshal() {
		for (Iterator<Component> it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Caption) {
				_caption = (Caption) child;
				break;
			}
		}
	}

	//Serializable//
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
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
			setVisible(evt.isOpen());
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
			if (_minimized) {
				setVisibleDirectly(false);
				if (_mode == MODAL)
					leaveModal(OVERLAPPED);
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	/**
	 * Always return false.
	 * @since 3.6.2
	 */
	public boolean isCollapsible() {
		return false;
	}

	/** The window's mode used with {@link Window#setMode(Mode)}.
	 * @since 6.0.0
	 */
	public static enum Mode {
		/** Embeds the window as normal component. */
		EMBEDDED(Window.EMBEDDED),
		/** Makes the window as a modal dialog. once {@link #doModal}
		 * is called, the execution of the event processing thread
		 * is suspended until one of the following occurs.
		 * <ol>
		 * <li>{@link #setMode(Mode)} is called with a mode other than MODAL.</li>
		 * <li>Either {@link #doOverlapped}, {@link #doPopup},
		 * {@link #doEmbedded}, or {@link #doHighlighted} is called.</li>
		 * <li>{@link #setVisible} is called with false.</li>
		 * <li>The window is detached from the window.</li>
		 * </ol>
		 *
		 * <p>Note: In the last two cases, the mode becomes {@link #OVERLAPPED}.
		 * In other words, one might say a modal window is a special overlapped window.
		 *
		 * @see #HIGHLIGHTED
		 */
		MODAL(Window.MODAL),
		/** Makes the window as overlapped other components.
		 */
		OVERLAPPED(Window.OVERLAPPED),
		/** Makes the window as popup.
		 * It is similar to {@link #OVERLAPPED}, except it is auto hidden
		 * when user clicks outside of the window.
		 */
		POPUP(Window.POPUP),
		/** Makes the window as highlighted.
		 * Its visual effect is the same as {@link #MODAL}.
		 * However, from the server side's viewpoint, it is similar to
		 * {@link #OVERLAPPED}. The execution won't be suspended when
		 * {@link #doHighlighted} is called.
		 *
		 * @see #MODAL
		 * @see #OVERLAPPED
		 */
		HIGHLIGHTED(Window.HIGHLIGHTED);

		private final int id;

		private Mode(int v) {
			this.id = v;
		}
	}
}

/** Any serializable object. */
/*package*/ class Mutex implements java.io.Serializable {
}
