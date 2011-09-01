package org.zkoss.zul.api;

import org.zkoss.zk.ui.IdSpace;//for javadoc
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Events;//for javadoc
import org.zkoss.zk.ui.event.MinimizeEvent;//for javadoc

import org.zkoss.zul.ext.Framable;

/**
 * A window.
 * 
 * <p>
 * Unlike other elements, each {@link Window} is an independent ID space (by
 * implementing {@link IdSpace}). It means a window and all its descendants
 * forms a ID space and the ID of each of them is unique in this space. You
 * could retrieve any of them in this space by calling {@link #getFellow}.
 * 
 * <p>
 * If a window X is a descendant of another window Y, X's descendants are not
 * visible in Y's space. To retrieve a descendant, say Z, of X, you have to
 * invoke Y.getFellow('X').getFellow('Z').
 * 
 * <p>
 * Events:<br/>
 * onMove, onOpen, onMaximize, onMinimize, and onClose.<br/>
 * Note: to have better performance, onOpen is sent only if a non-deferrable
 * event listener is registered (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * <p>
 * <code>onMaximize</code> and <code>onMinimize</code> are supported. (since
 * 3.5.0)
 * 
 * <p>
 * <code>onClose</code> is sent when the close button is pressed (if
 * {@link #isClosable} is true). The window has to detach or hide the window. By
 * default, {@link org.zkoss.zul.Window#onClose} detaches the window. To prevent
 * it from detached, you have to call
 * {@link org.zkoss.zk.ui.event.Event#stopPropagation} to prevent
 * {@link org.zkoss.zul.Window#onClose} is called.
 * 
 * <p>
 * On the other hand, <code>onOpen</code> is sent when a popup window (i.e.,
 * {@link #getMode} is popup) is closed due to user's activity (such as press
 * ESC). This event is only a notification. In other words, the popup is hidden
 * before the event is sent to the server. The application cannot prevent the
 * window from being hidden.
 * 
 * <p>
 * Default {@link #getZclass}: z-window-{@link #getMode()}.(since 3.5.0)
 * 
 * @author tomyeh
 * 
 * @since 3.5.2
 */
public interface Window extends org.zkoss.zul.impl.api.XulElement, Framable, IdSpace {
	/**
	 * Returns whether the window is maximized.
	 * 
	 */
	public boolean isMaximized();

	/**
	 * Sets whether the window is maximized, and then the size of the window
	 * will depend on it to show a appropriate size. In other words, if true,
	 * the size of the window will count on the size of its offset parent node
	 * whose position is absolute (by not {@link #inEmbedded()}) or its parent
	 * node. Otherwise, its size will be original size. Note that the maximized
	 * effect will run at client's sizing phase not initial phase.
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
	 * maximize the window.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isMaximizable();

	/**
	 * Sets whether to display the maximizing button and allow the user to
	 * maximize the window, when a window is maximized, the button will
	 * automatically change to a restore button with the appropriate behavior
	 * already built-in that will restore the window to its previous size.
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * Note: the maximize button won't be displayed if no title or caption at
	 * all.
	 * 
	 */
	public void setMaximizable(boolean maximizable);

	/**
	 * Returns whether the window is minimized.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isMinimized();

	/**
	 * Sets whether the window is minimized.
	 * <p>
	 * Default: false.
	 * 
	 * @exception UiException
	 *                if {@link #isMinimizable} is false.
	 */
	public void setMinimized(boolean minimized);

	/**
	 * Returns whether to display the minimizing button and allow the user to
	 * minimize the window.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isMinimizable();

	/**
	 * Sets whether to display the minimizing button and allow the user to
	 * minimize the window. Note that this button provides no implementation --
	 * the behavior of minimizing a window is implementation-specific, so the
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
	 * Sets the minimum height in pixels allowed for this window. If negative,
	 * 100 is assumed.
	 * <p>
	 * Default: 100.
	 * <p>
	 * Note: Only applies when {@link #isSizable()} = true.
	 * 
	 */
	public void setMinheight(int minheight);

	/**
	 * Returns the minimum height.
	 * <p>
	 * Default: 100.
	 * 
	 */
	public int getMinheight();

	/**
	 * Sets the minimum width in pixels allowed for this window. If negative,
	 * 200 is assumed.
	 * <p>
	 * Default: 200.
	 * <p>
	 * Note: Only applies when {@link #isSizable()} = true.
	 * 
	 */
	public void setMinwidth(int minwidth);

	/**
	 * Returns the minimum width.
	 * <p>
	 * Default: 200.
	 * 
	 */
	public int getMinwidth();

	/**
	 * Returns the caption of this window.
	 */
	public org.zkoss.zul.api.Caption getCaptionApi();

	/**
	 * Returns the border. The border actually controls what the content style
	 * class is is used. In fact, the name of the border (except "normal") is
	 * generate as part of the style class used for the content block. Refer to
	 * {@link #getContentSclass} for more details.
	 * 
	 * <p>
	 * Default: "none".
	 */
	public String getBorder();

	/**
	 * Sets the border (either none or normal).
	 * 
	 * @param border
	 *            the border. If null or "0", "none" is assumed. Since 2.4.1, We
	 *            assume "0" to be "none".
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
	 * Returns the current mode. One of "modal", "embedded", "overlapped",
	 * "popup", and "highlighted".
	 */
	public String getMode();

	/**
	 * Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 * 
	 * <p>
	 * Notice: {@link Events#ON_MODAL} is posted if you specify "modal" to this
	 * method. Unlike {@link #doModal}, {@link Events#ON_MODAL} is posted, so
	 * the window will become modal later (since 3.0.4). In other words,
	 * setMode("modal") never suspends the execution of the current thread. On
	 * the other hand, {@link #doModal} will suspends the execution if executed
	 * in an event listener, or throws an exception if <em>not</em> executed in
	 * an event listener.
	 * 
	 * @param name
	 *            the mode which could be one of "embedded", "overlapped",
	 *            "popup", "modal", "highlighted". Note: it cannot be "modal".
	 *            Use {@link #doModal} instead.
	 * 
	 * @exception InterruptedException
	 *                thrown if "modal" is specified, and one of the following
	 *                conditions occurs: 1) the desktop or the Web application
	 *                is being destroyed, or 2)
	 *                {@link org.zkoss.zk.ui.sys.DesktopCtrl#ceaseSuspendedThread}
	 *                . To tell the difference, check the getMessage method of
	 *                InterruptedException.
	 */
	public void setMode(String name) throws InterruptedException;

	/**
	 * Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 * 
	 * @see #setMode(String)
	 */
	public void setMode(int mode) throws InterruptedException;

	/**
	 * Returns whether this is a modal dialog.
	 */
	public boolean inModal();

	/**
	 * Returns whether this is embedded with other components (Default).
	 * 
	 * @see #doEmbedded
	 */
	public boolean inEmbedded();

	/**
	 * Returns whether this is a overlapped window.
	 */
	public boolean inOverlapped();

	/**
	 * Returns whether this is a popup window.
	 */
	public boolean inPopup();

	/**
	 * Returns whether this is a highlighted window.
	 */
	public boolean inHighlighted();

	/**
	 * Makes this window as a modal dialog. It will automatically center the
	 * window (ignoring {@link #getLeft} and {@link #getTop}).
	 * 
	 * <p>
	 * Notice: though both setMode("modal") and doModal() both causes the window
	 * to become modal, they are a bit different. doModal causes the event
	 * listener to suspend immediately, while setMode("modal") posts an event (
	 * {@link Events#ON_MODAL}). That is, {@link #setMode} won't suspend the
	 * execution immediately, but {@link #doModal} will. {@link #doModal} can be
	 * called only in an event listener, while {@link #setMode} can be called
	 * anytime.
	 * 
	 * @exception SuspendNotAllowedException
	 *                if 1) not in an event listener;<br/>
	 *                2) the event thread is disabled.<br/>
	 *                3) there are too many suspended processing thread than the
	 *                deployer allows. By default, there is no limit of # of
	 *                suspended threads.
	 * @exception InterruptedException
	 *                thrown if the desktop or the Web application is being
	 *                destroyed, or
	 *                {@link org.zkoss.zk.ui.sys.DesktopCtrl#ceaseSuspendedThread}
	 *                . To tell the difference, check the getMessage method of
	 *                InterruptedException.
	 */
	public void doModal() throws InterruptedException,
			SuspendNotAllowedException;

	/**
	 * Makes this window as overlapped with other components.
	 */
	public void doOverlapped();

	/**
	 * Makes this window as popup, which is overlapped with other component and
	 * auto-hiden when user clicks outside of the window.
	 */
	public void doPopup();

	/**
	 * Makes this window as highlited. The visual effect is the similar to the
	 * modal window, but, like overlapped, it doesn't suspend (block) the
	 * execution at the server. In other words, it is more like an overlapped
	 * window from the server side's viewpoint.
	 */
	public void doHighlighted();

	/**
	 * Makes this window as embeded with other components (Default).
	 */
	public void doEmbedded();

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
	 * {@link org.zkoss.zul.Window#onClose}, or listening the onClose event.
	 * 
	 * <p>
	 * Note: the close button won't be displayed if no title or caption at all.
	 */
	public void setClosable(boolean closable);

	/**
	 * Returns whether the window is sizable.
	 */
	public boolean isSizable();

	/**
	 * Sets whether the window is sizable. If true, an user can drag the border
	 * to change the window width.
	 * <p>
	 * Default: false.
	 */
	public void setSizable(boolean sizable);

	/**
	 * Returns how to position the window at the client screen. It is
	 * meaningless if the embedded mode is used.
	 * 
	 * <p>
	 * Default: null which depends on {@link #getMode}: If overlapped or popup,
	 * {@link #setLeft} and {@link #setTop} are assumed. If modal or
	 * highlighted, it is centered.
	 */
	public String getPosition();

	/**
	 * Sets how to position the window at the client screen. It is meaningless
	 * if the embedded mode is used.
	 * 
	 * @param pos
	 *            how to position. It can be null (the default), or a
	 *            combination of the following values (by separating with
	 *            comma).
	 *            <dl>
	 *            <dt>center</dt>
	 *            <dd>Position the window at the center. {@link #setTop} and
	 *            {@link #setLeft} are both ignored.</dd>
	 *            <dt>left</dt>
	 *            <dd>Position the window at the left edge. {@link #setLeft} is
	 *            ignored.</dd>
	 *            <dt>right</dt>
	 *            <dd>Position the window at the right edge. {@link #setLeft} is
	 *            ignored.</dd>
	 *            <dt>top</dt>
	 *            <dd>Position the window at the top edge. {@link #setTop} is
	 *            ignored.</dd>
	 *            <dt>bottom</dt>
	 *            <dd>Position the window at the bottom edge. {@link #setTop} is
	 *            ignored.</dd>
	 *            <dt>parent</dt>
	 *            <dd>Position the window relative to its parent. That is, the
	 *            left and top ({@link #getTop} and {@link #getLeft}) is an
	 *            offset to his parent's let-top corner. (since 3.0.2)</dd>
	 *            </dl>
	 *            <p>
	 *            For example, "left,center" means to position it at the center
	 *            of the left edge.
	 */
	public void setPosition(String pos);

	/**
	 * Returns the CSS style for the content block of the window.
	 */
	public String getContentStyle();

	/**
	 * Sets the CSS style for the content block of the window.
	 * 
	 * <p>
	 * Default: null.
	 */
	public void setContentStyle(String style);

	/**
	 * Returns the style class used for the content block.
	 * 
	 * @see #setContentSclass
	 */
	public String getContentSclass();

	/**
	 * Sets the style class used for the content block.
	 * 
	 * @see #getContentSclass
	 */
	public void setContentSclass(String scls);

}
