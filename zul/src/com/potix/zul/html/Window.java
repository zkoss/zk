/* Window.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 19:29:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashSet;

import com.potix.lang.D;
import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.IdSpace;
import com.potix.zk.ui.event.Events;
import com.potix.zk.au.*;

import com.potix.zul.html.impl.XulElement;
import com.potix.zul.au.*;

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
 * <p>Events:<br>
 * onMove, onShow, onOK, onCacnel and onCtrlKey.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Window extends XulElement implements IdSpace  {
	private transient Caption _caption;

	private String _border = "none";
	private String _title = "";
	/** What control and function keys to intercepts. */
	private String _ctrlKeys;
	/** One of MODAL, EMBEDDED, OVERLAPPED. */
	private int _mode = EMBEDDED;
	/** Used for doModal. */
	private transient Object _mutex;
	/** Whether this window is in a special mode that need to be ended. */
	private boolean _moding;
	/** Whether to show a close button. */
	private boolean _closable;

	/** Embeds the window as normal component. */
	private static final int EMBEDDED = 0;
	/** Makes the window as a modal dialog. */
	private static final int MODAL = 1;
	/** Makes the window as overlap other components.
	 */
	private static final int OVERLAPPED = 2;
	/** Makes the window as popup.
	 * It is similar to {@link #OVERLAPPED}, except it is auto hidden
	 * when user clicks outside of the window.
	 */
	private static final int POPUP = 3;

	public Window() {
		init();
	}
	private void init() {
		_mutex = new Object();
	}

	/** Returns the caption of this window.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns the border.
	 * <p>The border actually controls what CSS class to use:
	 * If border is null, it implies "none".
	 * If border is "normal", the class called "window" is used.
	 * If not, the class called "window-<i>border</i>" (e.g., "window-none").
	 *
	 * <p>If you also specify the CSS class ({@link #setClass}), it
	 * overwrites whatever border you specify here.
	 *
	 * <p>Default: "none".
	 */
	public String getBorder() {
		return _border;
	}
	/** Sets the border (either none or normal).
	 */
	public void setBorder(String border) {
		if (border == null) border = "none";
		if (!Objects.equals(_border, border)) {
			_border = border;
			smartUpdate("class", getSclass());
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
			if (_caption != null) _caption.invalidate(INNER);
			else invalidate(INNER);
		}
	}

	/** Returns what control and function keys to intercept.
	 * <p>Default: null.
	 */
	public String getCtrlKeys() {
		return _ctrlKeys;
	}
	/** Sets what control and function keys to intercept.
	 * To intercept a control or function key, you have to set
	 * what keys to intercept by use of {@link #setCtrlKeys}, and
	 * then add listener for the onCtrlKey event.
	 *
	 * <p>For example, If ctrlKeys="GW2" is specified, it means Ctrl+G,
	 * Ctrl+W and F2 are all intercepted.
	 * Note: 0 means F10.
	 */
	public void setCtrlKeys(String ctrlKeys) {
		if (ctrlKeys != null && ctrlKeys.length() == 0)
			ctrlKeys = null;
		if (!Objects.equals(_ctrlKeys, ctrlKeys)) {
			_ctrlKeys = ctrlKeys != null ? ctrlKeys.toUpperCase(): null;
			smartUpdate("zk_ctkeys", _ctrlKeys);
		}
	}

	/** Returns the current mode.
	 * One of "modal", "embedded", "overlapped" and "popup".
	 */
	public String getMode() {
		switch (_mode) {
		case MODAL: return "modal";
		case POPUP: return "popup";
		case OVERLAPPED: return "overlapped";
		default: return "embedded";
		}
	}
	/** Sets the mode.
	 *
	 * <p>Notice that you cannot specify "modal" to this method.
	 * Rather, you can use {@link #doModal} if it is called in an event listener.
	 * Or, you can use {@link com.potix.zk.ui.event.Events#postEvent} to
	 * post the onModal event. For example, in a ZUML page, you can put a window
	 * into modal immediately after rendered as follows.
	 *
	 * <pre><code>
	 *&lt;window title="..."&gt;
	 *...
	 *  &lt;zscript&gt;
	 *    Events.postEvent(Events.ON_MODAL, spaceOwner, null);
	 *  &lt;/zscript&gt;
	 *...
	 *
	 * @param name the mode which could be one of
	 * "embedded", "overlapped" and "popup".
	 * Note: it cannot be "modal". Use {@link #doModal} instead.
	 */
	public void setMode(String name) throws InterruptedException {
		if ("popup".equals(name)) doPopup();
		else if ("overlapped".equals(name)) doOverlapped();
		else if ("embedded".equals(name)) doEmbedded();
		else if ("modal".equals(name))
			throw new WrongValueException("setMode() doesn't suport modal. Use doModal(), or post the onModal event, instead.");
		else throw new WrongValueException("Uknown mode: "+name);
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

	/** Makes this window as a modal dialog.
	 * It will automatically center the window (ignoring {@link #getLeft} and
	 * {@link #getTop}).
	 */
	public void doModal() throws InterruptedException {
		checkOverlappable();

		if (_mode != MODAL || !_moding) {
			endModing();

			if (_mode != MODAL) {
				_mode = MODAL;
				invalidate(OUTER);
			}

			response("doModal", new AuDoModal(this));
			_moding = true;
			setVisible(true); //after _molding = true to avoid dead-loop

			//no need to synchronized (_mutex) because no racing is possible
			Executions.wait(_mutex);
		}
	}
	/** Makes this window as overlapped with other components.
	 */
	public void doOverlapped() {
		checkOverlappable();

		if (_mode != OVERLAPPED || !_moding) {
			endModing();

			if (_mode != OVERLAPPED) {
				_mode = OVERLAPPED;
				invalidate(OUTER);
			}

			response("doOverlapped", new AuDoOverlapped(this));
			_moding = true;
			setVisible(true); //after _molding = true to avoid dead-loop
		}
	}
	/** Makes this window as popup, which is overlapped with other component
	 * and auto-hiden when user clicks outside of the window.
	 */
	public void doPopup() {
		checkOverlappable();

		if (_mode != POPUP || !_moding) {
			endModing();

			if (_mode != POPUP) {
				_mode = POPUP;
				invalidate(OUTER);
			}

			response("doPopup", new AuDoPopup(this));
			_moding = true;
			setVisible(true); //after _molding = true to avoid dead-loop
		}
	}
	/** Makes this window as embeded with other components (Default).
	 */
	public void doEmbedded() {
		if (_mode != EMBEDDED) {
			endModing();
			_mode = EMBEDDED;
			invalidate(OUTER);
		}
		setVisible(true);
	}
	/** Makes sure it is not draggable. */
	private void checkOverlappable() {
		if (!"false".equals(getDraggable()))
			throw new UiException("Draggable window cannot be modal, overlapped or popup: "+this);
		for (Component comp = this; (comp = comp.getParent()) != null;)
			if (!comp.isVisible())
				throw new UiException("One of its ancestors, "+comp+", is not visible, so unable to be modal, overlapped or popup");
	}
	/** Returns whether to show a close button on the title bar.
	 */
	public boolean isClosable() {
		return _closable;
	}
	/** Sets whether to show a close button on the title bar.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			invalidate(OUTER); //re-init is required
		}
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

	/** Ends the modal mode. */
	private void endModing() {
		if (_moding) {
			assert D.OFF || _mode != EMBEDDED;

			if (_mode == MODAL)
				Executions.notifyAll(_mutex);

			if (_mode == MODAL)
				response(null, new AuEndModal(this));
			else if (_mode == POPUP)
				response(null, new AuEndPopup(this));
			else
				response(null, new AuEndOverlapped(this));
			_moding = false;
		}
	}

	//-- super --//
	/** Returns the style class.
	 * If the style class is not defined ({@link #setSclass} is not called
	 * or called with null or empty), it returns the style class based
	 * on {@link #getMode} and {@link #getBorder}.
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;
		return "normal".equals(getBorder()) ?
			getMode(): getMode() + '-' + getBorder();
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
			if (!getChildren().isEmpty())
				insertBefore = (Component)getChildren().get(0);
				//always makes caption as the first child
			_caption = (Caption)child;
			invalidate(INNER);
		} else if (insertBefore instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate(INNER);
		}
		super.onChildRemoved(child);
	}

	public void setPage(Page page) {
		super.setPage(page);
		if (page == null) {
			endModing();
			_mode = EMBEDDED;
		}
	}

	/** If it becomes invisible, the modal mode ends automatically.
	 */
	public boolean setVisible(boolean visible) {
		if (!visible) endModing();

		final boolean ret = super.setVisible(visible);
		if (!ret && visible && !_moding)
			switch (_mode) {
			case MODAL:
				try {
					doModal(); return false;
				} catch (InterruptedException ex) {
					throw UiException.Aide.wrap(ex);
				}
			case POPUP:
				doPopup();
				return false;
			case OVERLAPPED:
				doOverlapped();
				return false;
			}
		return ret;
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
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_MOVE);
		appendAsapAttr(sb, Events.ON_Z_INDEX);
		appendAsapAttr(sb, Events.ON_OK);
		appendAsapAttr(sb, Events.ON_CANCEL);
		appendAsapAttr(sb, Events.ON_CTRL_KEY);

		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null) sb.append(clkattrs);
			//though widget.js handles onclick (if 3d), it is useful
			//to support onClick for groupbox

		if (_closable)
			HTMLs.appendAttribute(sb, "zk_closable", true);
		HTMLs.appendAttribute(sb, "zk_ctkeys", _ctrlKeys);
		return sb.toString();
	}

	//--ComponentCtrl--//
	public boolean inDifferentBranch(Component child) {
		return child instanceof Caption; //in different branch
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
}
