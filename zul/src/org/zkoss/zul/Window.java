/* Window.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 19:29:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.render.MultiBranch;
import org.zkoss.zk.ui.ext.client.Maximizable;
import org.zkoss.zk.ui.ext.client.Minimizable;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zk.ui.ext.render.Floating;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MinimizeEvent;

import org.zkoss.zul.ext.Framable;
import org.zkoss.zul.impl.XulElement;

/**
 * A generic window.
 *
 * <p>Unlike other elements, each {@link Window} is an independent ID space
 * (by implementing {@link IdSpace}).
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
 * <p>Default {@link #getZclass}: z-window-{@link #getMode()}.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Window extends XulElement implements Framable, IdSpace, org.zkoss.zul.api.Window {
	private static final Log log = Log.lookup(Window.class);
	private static String _onshow = null;
	private transient Caption _caption;

	private String _border = "none";
	private String _title = "";
	/** One of MODAL, EMBEDDED, OVERLAPPED, HIGHLIGHTED, POPUP. */
	private int _mode = EMBEDDED;
	/** Used for doModal. */
	private transient Object _mutex;
	/** The style used for the content block. */
	private String _cntStyle;
	/** The style class used for the content block. */
	private String _cntscls;
	/** How to position the window. */
	private String _pos;
	/** Whether to show a close button. */
	private boolean _closable;
	/** Whether the window is sizable. */
	private boolean _sizable;
	/** Whether to show the shadow. */
	private boolean _shadow = true;
	
	private boolean _maximizable, _minimizable, _maximized, _minimized;
	private int _minheight = 100, _minwidth = 200; 

	/** Embeds the window as normal component. */
	private static final int EMBEDDED = 0;
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
	private static final int MODAL = 1;
	/** Makes the window as overlapped other components.
	 */
	private static final int OVERLAPPED = 2;
	/** Makes the window as popup.
	 * It is similar to {@link #OVERLAPPED}, except it is auto hidden
	 * when user clicks outside of the window.
	 */
	private static final int POPUP = 3;
	/** Makes the window as hilighted.
	 * Its visual effect is the same as {@link #MODAL}.
	 * However, from the server side's viewpoint, it is similar to
	 * {@link #OVERLAPPED}. The execution won't be suspended when
	 * {@link #doHighlighted} is called.
	 *
	 * @see #MODAL
	 * @see #OVERLAPPED
	 */
	private static final int HIGHLIGHTED = 4;

	public Window() {
		init();
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
	private void init() {
		_mutex = new Object();
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
				throw new UiException("Not maximizable, "+this);

			_maximized = maximized;
			if (_maximized) {
				_minimized = false;
				setVisible0(true); //avoid dead loop
			}
			smartUpdate("z.maximized", _maximized);
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
			invalidate();
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
				throw new UiException("not minimizable, "+ this);

			_minimized = minimized;
			if (_minimized) {
				_maximized = false;
				setVisible0(false); //avoid dead loop
			} else setVisible0(true);
			smartUpdate("z.minimized", _minimized);
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
			invalidate();
		}
	}
	/**
	 * Sets the minimum height in pixels allowed for this window. If negative, 100 is assumed.
	 * <p>Default: 100. 
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @since 3.5.0
	 */
	public void setMinheight(int minheight) {
		if (minheight < 0) minheight = 100;
		if (_minheight != minheight) {
			_minheight = minheight;
			smartUpdate("z.minheight", _minheight);
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
		if (minwidth < 0) minwidth = 200;
		if (_minwidth != minwidth) {
			_minwidth = minwidth;
			smartUpdate("z.minwidth", _minwidth);
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
	 * Sets the action of window component to show the animating effect by default.
	 * 
	 * <p>Default: null. In other words, if the property is null, it will refer to
	 * the configuration of zk.xml to find the preference with 
	 * "org.zkoss.zul.Window.defaultActionOnShow", if any. For example,
	 * <pre>&lt;preference&gt;
     *   &lt;name&gt;org.zkoss.zul.Window.defaultActionOnShow&lt;/name&gt;
     *   &lt;value&gt;moveDown&lt;/value&gt;
	 * &lt;/preference&gt;</pre>
	 *  Otherwise, the animating 
	 * effect is depended on component itself.</p>
	 * <p>In JavaScript, the property will match the same function name with the
	 * prefix "anima.". For example, if the property is "moveDown", the function name
	 * should be "anima.moveDown" accordingly.</p>
	 * <p><strong>Node:</strong> The method is available in modal mode only. And if 
	 * the onshow command of client-side action has been assigned on the 
	 * component, its priority is higher than this method.<br/>
	 * For example, 
	 * <pre>action="onshow:anima.appear(#{self});"</pre>
	 * </p>
	 * 
	 * @param onshow the function name in JavaScript. You could use the following
	 * animations, e.g. "moveDown", "moveRight", "moveDiagonal", "appear", 
	 * "slideDown", and so forth.
	 * @since 3.0.2
	 */
	public static void setDefaultActionOnShow(String onshow) {
		if (!Objects.equals(_onshow, onshow))
			_onshow = onshow;
	}
	
	/**
	 * Returns the animating name of function.
	 * @since 3.0.2
	 */
	public static String getDefaultActionOnShow() {
		return _onshow;
	}
	
	/** Returns the caption of this window.
	 */
	public Caption getCaption() {
		return _caption;
	}
	/** Returns the caption of this window.
	 *
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Caption getCaptionApi() {
		return getCaption();
	}

	/** Returns the border.
	 * The border actually controls what the content style class is
	 * is used. In fact, the name of the border (except "normal")
	 * is generate as part of the style class used for the content block.
	 * Refer to {@link #getContentSclass} for more details.
	 *
	 * <p>Default: "none".
	 */
	public String getBorder() {
		return _border;
	}
	/** Sets the border (either none or normal).
	 *
	 * @param border the border. If null or "0", "none" is assumed.
	 * Since 2.4.1, We assume "0" to be "none".
	 */
	public void setBorder(String border) {
		if (border == null || "0".equals(border))
			border = "none";
		if (!Objects.equals(_border, border)) {
			_border = border;
			invalidate();
		}
	}

	/** Returns the title.
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

	/** Returns the current mode.
	 * One of "modal", "embedded", "overlapped", "popup", and "highlighted".
	 */
	public String getMode() {
		return modeToString(_mode);
	}
	private static String modeToString(int mode) {
		switch (mode) {
		case MODAL: return "modal";
		case POPUP: return "popup";
		case OVERLAPPED: return "overlapped";
		case HIGHLIGHTED: return "highlighted";
		default: return "embedded";
		}
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
	 * @param name the mode which could be one of
	 * "embedded", "overlapped", "popup", "modal", "highlighted".
	 * Note: it cannot be "modal". Use {@link #doModal} instead.
	 *
	 * @exception InterruptedException thrown if "modal" is specified,
	 * and one of the following conditions occurs:
	 * 1) the desktop or the Web application is being destroyed, or
	 * 2) {@link org.zkoss.zk.ui.sys.DesktopCtrl#ceaseSuspendedThread}.
	 * To tell the difference, check the getMessage method of InterruptedException.
	 */
	public void setMode(String name) throws InterruptedException {
		if ("popup".equals(name)) doPopup();
		else if ("overlapped".equals(name)) doOverlapped();
		else if ("embedded".equals(name)) doEmbedded();
		else if ("modal".equals(name))
			Events.postEvent(Events.ON_MODAL, this, null);
		else if ("highlighted".equals(name)) doHighlighted();
		else throw new WrongValueException("Uknown mode: "+name);
	}
	/** Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 *
	 * @see #setMode(String)
	 */
	public void setMode(int mode) throws InterruptedException {
		switch (mode) {
		case POPUP: doPopup(); break;
		case OVERLAPPED: doOverlapped(); break;
		case EMBEDDED: doEmbedded(); break;
		case MODAL:
			Events.postEvent(Events.ON_MODAL, this, null);
			break;
		case HIGHLIGHTED: doHighlighted(); break;
		default:
			throw new WrongValueException("Unknown mode: "+mode);
		}
	}

	/** Returns whether this is a modal dialog.
	 */
	public boolean inModal() {
		return _mode == MODAL;
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
	 *
	 * @exception SuspendNotAllowedException if
	 * 1) not in an event listener;<br/>
	 * 2) the event thread is disabled.<br/>
	 * 3) there are too many suspended processing thread than the deployer allows.
	 * By default, there is no limit of # of suspended threads.
	 * @exception InterruptedException thrown if the desktop or
	 * the Web application is being destroyed, or
	 * {@link org.zkoss.zk.ui.sys.DesktopCtrl#ceaseSuspendedThread}.
	 * To tell the difference, check the getMessage method of InterruptedException.
	 * @since 3.0.4
	 */
	public void doModal()
	throws InterruptedException, SuspendNotAllowedException {
		Desktop desktop = getDesktop();
		if (desktop == null) desktop = Executions.getCurrent().getDesktop();
		if (!desktop.getWebApp().getConfiguration().isEventThreadEnabled()) {
			handleFailedModal(_mode, isVisible());
			throw new SuspendNotAllowedException("Event processing thread is disabled");
		}

		checkOverlappable(MODAL);

		if (_mode != MODAL) {
			if (!Events.inEventListener())
				throw new SuspendNotAllowedException("doModal must be called in an event listener");

			int oldmode = _mode;
			boolean oldvisi = isVisible();

			invalidate();
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
			if (Executions.getCurrent()
			.getAttribute("javax.servlet.error.exception") != null) {
				//handle it specially if it is used for dispalying err
				setMode(HIGHLIGHTED);
			} else {
				setMode(oldmode); //restore
				setVisible(oldvisi);
			}
		} catch (Throwable ex) {
			log.realCauseBriefly("Causing another error", ex);
		}
	}

	/** Makes this window as overlapped with other components.
	 */
	public void doOverlapped() {
		checkOverlappable(OVERLAPPED);
		setNonModalMode(OVERLAPPED);
	}
	/** Makes this window as popup, which is overlapped with other component
	 * and auto-hiden when user clicks outside of the window.
	 */
	public void doPopup() {
		checkOverlappable(POPUP);
		setNonModalMode(POPUP);
	}
	/** Makes this window as highlited. The visual effect is
	 * the similar to the modal window, but, like overlapped,
	 * it doesn't suspend (block) the execution at the server.
	 * In other words, it is more like an overlapped window from the
	 * server side's viewpoint.
	 */
	public void doHighlighted() {
		checkOverlappable(HIGHLIGHTED);
		setNonModalMode(HIGHLIGHTED);
	}
	/** Makes this window as embeded with other components (Default).
	 */
	public void doEmbedded() {
		setNonModalMode(EMBEDDED);
	}
	/* Set non-modal mode. */
	private void setNonModalMode(int mode) {
		if (_mode != mode) {
			if (_mode == MODAL) leaveModal();
			_mode = mode;
			invalidate();
		}
		setVisible(true);
	}

	/** Set mode to MODAL and suspend this thread. */
	private void enterModal() throws InterruptedException {
		_mode = MODAL;
		//no need to synchronized (_mutex) because no racing is possible
		Executions.wait(_mutex);
	}
	/** Resumes the suspendded thread and set mode to OVERLAPPED. */
	private void leaveModal() {
		_mode = OVERLAPPED;
		Executions.notifyAll(_mutex);
	}
	/** Makes sure it is not draggable. */
	private void checkOverlappable(int mode) {
		if (!"false".equals(getDraggable()))
			throw new UiException("Draggable window cannot be modal, overlapped, popup, or highlighted: "+this);

		if (mode == MODAL || mode == HIGHLIGHTED)
			for (Component comp = this; (comp = comp.getParent()) != null;)
				if (!comp.isVisible())
					throw new UiException("One of its ancestors, "+comp+", is not visible, so unable to be modal or highlighted");
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
			smartUpdate("z.sizable", sizable);
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
			smartUpdate("z.shadow", shadow);
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
		if (_mode != EMBEDDED)
			smartUpdate("z.pos", pos);
	}

	/** Process the onClose event sent when the close button is pressed.
	 * <p>Default: detach itself.
	 */
	public void onClose() {
		detach();
	}
	/** Process the onModal event by making itself a modal window.
	 */
	public void onModal() throws InterruptedException {
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
			smartUpdate("z.cntStyle", _cntStyle);
		}
	}

	/** Returns the style class used for the content block.
	 *
	 * @see #setContentSclass
	 */
	public String getContentSclass() {
		return _cntscls;
	}
	/** Sets the style class used for the content block.
	 *
	 * @see #getContentSclass
	 * @since 3.0.0
	 */
	public void setContentSclass(String scls) {
		if (!Objects.equals(_cntscls, scls)) {
			_cntscls = scls;
			invalidate();
		}
	}

	/** Returns the style class used for the title.
	 *
	 * <p>It returns "wt-<i>sclass</i>" is returned,
	 * where <i>sclass</i> is the value returned by {@link #getSclass}.
	 * @deprecated As of release 3.5.0
	 */
	public String getTitleSclass() {
		return null;
	}
	
	// super
	public String getZclass() {
		return _zclass == null ? "z-window-" + getMode() : _zclass;
	}

	//-- Component --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
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
				_caption = (Caption)child;
				invalidate();
				return true;
			}
			return false;
		}
		return super.insertBefore(child, refChild);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate();
		}
		super.onChildRemoved(child);
	}

	public void onPageDetached(Page page) {
		if (_mode == MODAL && getPage() == null)
			leaveModal();
	}

	/** Changes the visibility of the window.
	 *
	 * <p>Note: If a modal dialog becomes invisible, the modal state
	 * will be ended automatically. In other words, the mode ({@link #getMode})
	 * will become {@link #OVERLAPPED} and the suspending thread is resumed.
	 */
	public boolean setVisible(boolean visible) {
		if (visible == _visible)
			return visible;
		_maximized = _minimized = false;
		return setVisible0(visible);
	}
	private boolean setVisible0(boolean visible) {
		if (!visible && (_mode == MODAL || _mode == HIGHLIGHTED)) {
			if (_mode == MODAL)
				leaveModal();
			else _mode = OVERLAPPED;
			invalidate();
		} else if ( _mode != EMBEDDED) {
			smartUpdate("z.visible", visible);
		}
		return super.setVisible(visible);
	}
	
	//-- super --//
	public void setDraggable(String draggable) {
		if (_mode != EMBEDDED) {
			if (draggable != null
			&& (draggable.length() > 0 && !"false".equals(draggable)))
				throw new UiException("Only embedded window could be draggable: "+this);
		}
		super.setDraggable(draggable);
	}
	protected String getRealStyle() {
		final String style = super.getRealStyle() + (isVisible() && isMinimized() ? "display:none;" : "");
		return _mode != EMBEDDED ? 
			(isVisible() ? "position:absolute;visibility:hidden;" : "position:absolute;") + style: style;
			//If no absolute, Opera ignores left and top
			//
			//If not embedded we always generate visibility:hidden to have
			//better visual effect (the client will turn it on in zkWnd.init)
	}
	
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_MOVE);
		appendAsapAttr(sb, Events.ON_SIZE);
		appendAsapAttr(sb, Events.ON_Z_INDEX);
		appendAsapAttr(sb, Events.ON_OPEN);
		appendAsapAttr(sb, Events.ON_MAXIMIZE);
		
		if (inModal() || inHighlighted()) HTMLs.appendAttribute(sb, "z." + Events.ON_MINIMIZE, true);
		else appendAsapAttr(sb, Events.ON_MINIMIZE);
		
		//no need to generate ON_CLOSE since it is always sent (as ASAP)

		final String clkattrs = getAllOnClickAttrs();
		if (clkattrs != null) sb.append(clkattrs);
			//though widget.js handles onclick (if 3d), it is useful
			//to support onClick for groupbox
		final String aos = getDefaultActionOnShow() != null ? getDefaultActionOnShow() 
				: getDesktop().getWebApp().getConfiguration()
					.getPreference("org.zkoss.zul.Window.defaultActionOnShow", null);
		if (aos != null)
			HTMLs.appendAttribute(sb, "z.aos", aos.length() == 0 ?  "z_none" : aos);
		if (_closable)
			sb.append(" z.closable=\"true\"");
		if (_sizable)
			sb.append(" z.sizable=\"true\"");
		if (!_shadow)
			sb.append(" z.shadow=\"false\"");

		if (_mode != EMBEDDED) {
			if (_pos != null)
				HTMLs.appendAttribute(sb, "z.pos", _pos);
			HTMLs.appendAttribute(sb, "z.mode", getMode());
			HTMLs.appendAttribute(sb, "z.visible", isVisible());
		}

		if (_maximizable)
			sb.append(" z.maximizable=\"true\"");
		if (_minimizable)
			sb.append(" z.minimizable=\"true\"");
		if (_maximized)
			sb.append(" z.maximized=\"true\"");
		if (_minimized)
			sb.append(" z.minimized=\"true\"");

		HTMLs.appendAttribute(sb, "z.minheight", getMinheight());
		HTMLs.appendAttribute(sb, "z.minwidth", getMinwidth());
		return sb.toString();
	}

	//Cloneable//
	public Object clone() {
		final Window clone = (Window)super.clone();
		clone.init();
		if (clone._caption != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Caption) {
				_caption = (Caption)child;
				break;
			}
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
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
			setVisible(open);
		}
		//Floating//
		public boolean isFloating() {
			return _mode != EMBEDDED;
		}
		public void setMaximizedByClient(boolean maximized) {
			_maximized = maximized;
			if (_maximized) _visible = true;
		}
		public void setMinimizedByClient(boolean minimized) {
			_minimized = minimized;
			if (_minimized) {
				_visible = false;
				if (_mode == MODAL) {
					leaveModal();
					invalidate();
				} else if (_mode == HIGHLIGHTED) {
					_mode = OVERLAPPED; // according to leaveModal()
					invalidate();
				}
			}
		}
	}
	
	/**
	 * Unimplemented, just exists to fulfill {@link Framable}
	 * @since 3.6.2
	 */
	public boolean isCollapsible() {
		return false;
	}

}
