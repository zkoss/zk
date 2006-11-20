/* Window.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 19:29:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.D;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.render.MultiBranch;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.*;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.au.*;

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
 * @author tomyeh
 */
public class Window extends XulElement implements IdSpace {
	private transient Caption _caption;

	private String _border = "none";
	private String _title = "";
	/** What control and function keys to intercepts. */
	private String _ctrlKeys;
	/** The value passed to the client; parsed from _ctrlKeys. */
	private String _ctkeys;
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
			if (_caption != null) _caption.invalidate();
			else invalidate();
		}
	}

	/** Returns what keystrokes to intercept.
	 * <p>Default: null.
	 */
	public String getCtrlKeys() {
		return _ctrlKeys;
	}
	/** Sets what keystrokes to intercept.
	 *
	 * <p>The string could be a combination of the following:
	 * <dl>
	 * <dt>^k</dt>
	 * <dd>A control key, i.e., Ctrl+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>@k</dt>
	 * <dd>A alt key, i.e., Alt+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>$k</dt>
	 * <dd>A shift key, i.e., Shift+k, where k could be #n</dd>
	 * <dt>#home</dt>
	 * <dd>Home</dd>
	 * <dt>#end</dt>
	 * <dd>End</dd>
	 * <dt>#ins</dt>
	 * <dd>Insert</dd>
	 * <dt>#del</dt>
	 * <dd>Delete</dd>
	 * <dt>#left</dt>
	 * <dd>Left arrow</dd>
	 * <dt>#right</dt>
	 * <dd>Right arrow</dd>
	 * <dt>#up</dt>
	 * <dd>Up arrow</dd>
	 * <dt>#down</dt>
	 * <dd>Down arrow</dd>
	 * <dt>#pgup</dt>
	 * <dd>PageUp</dd>
	 * <dt>#pgdn</dt>
	 * <dd>PageDn</dd>
	 * <dt>#f1 #f2 ... #f12</dt>
	 * <dd>Function keys representing F1, F2, ... F12</dd>
	 * </dl>
	 *
	 * <p>For example,
	 * <dl>
	 * <dt>^a^d@c#f10#left#right</dt>
	 * <dd>It means you want to intercept Ctrl+A, Ctrl+D, Alt+C, F10,
	 * Left and Right.</dd>
	 * <dt>^#left</dt>
	 * <dd>It means Ctrl+Left.</dd>
	 * <dt>^#f1</dt>
	 * <dd>It means Ctrl+F1.</dd>
	 * <dt>@#f3</dt>
	 * <dd>It means Alt+F3.</dd>
	 * </dl>
	 *
	 * <p>Note: it doesn't support Ctrl+Alt, Shift+Ctrl, Shift+Alt or Shift+Ctrl+Alt.
	 */
	public void setCtrlKeys(String ctrlKeys) throws UiException {
		if (ctrlKeys != null && ctrlKeys.length() == 0)
			ctrlKeys = null;
		if (!Objects.equals(_ctrlKeys, ctrlKeys)) {
			parseCtrlKeys(ctrlKeys);
			smartUpdate("z.ctkeys", _ctkeys);
		}
	}
	private void parseCtrlKeys(String keys) throws UiException {
		if (keys == null || keys.length() == 0) {
			_ctrlKeys = _ctkeys = null;
			return;
		}

		final StringBuffer sbctl = new StringBuffer(),
			sbsft = new StringBuffer(), sbalt = new StringBuffer(),
			sbext = new StringBuffer();
		StringBuffer sbcur = null;
		for (int j = 0, len = keys.length(); j < len; ++j) {
			char cc = keys.charAt(j);
			switch (cc) {
			case '^':
			case '$':
			case '@':
				if (sbcur != null)
					throw new WrongValueException("Combination of Shift, Alt and Ctrl not supported: "+keys);
				sbcur = cc == '^' ? sbctl: cc == '@' ? sbalt: sbsft;
				break;
			case '#':
				{
					int k = j + 1;
					for (; k < len; ++k) {
						final char c2 = (char)keys.charAt(k);
						if ((c2 > 'Z' || c2 < 'A') 	&& (c2 > 'z' || c2 < 'a')
						&& (c2 > '9' || c2 < '0'))
							break;
					}
					if (k == j + 1)
						throw new WrongValueException(MCommon.UNEXPECTED_CHARACTER, new Object[] {new Character(cc), keys});

					final String s = keys.substring(j+1, k).toLowerCase();
					if ("pgup".equals(s)) cc = 'A';
					else if ("pgdn".equals(s)) cc = 'B';
					else if ("end".equals(s)) cc = 'C';
					else if ("home".equals(s)) cc = 'D';
					else if ("left".equals(s)) cc = 'E';
					else if ("up".equals(s)) cc = 'F';
					else if ("right".equals(s)) cc = 'G';
					else if ("down".equals(s)) cc = 'H';
					else if ("ins".equals(s)) cc = 'I';
					else if ("del".equals(s)) cc = 'J';
					else if (s.length() > 1 && s.charAt(0) == 'f') {
						final int v;
						try {
							v = Integer.parseInt(s.substring(1));
						} catch (Throwable ex) {
							throw new WrongValueException("Unknown #"+s+" in "+keys);
						}
						if (v == 0 || v > 12)
							throw new WrongValueException("Unsupported function key: #f"+v);
						cc = (char)('O' + v); //'P': F1, 'Q': F2... 'Z': F12
					} else
						throw new WrongValueException("Unknown #"+s+" in "+keys);

					if (sbcur == null) sbext.append(cc);
					else {
						sbcur.append(cc);
						sbcur = null;
					}
					j = k - 1;
				}
				break;
			default:
				if (sbcur == null || ((cc > 'Z' || cc < 'A') 
				&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0')))
					throw new WrongValueException(MCommon.UNEXPECTED_CHARACTER, new Object[] {new Character(cc), keys});
				if (sbcur == sbsft)
					throw new WrongValueException("$"+cc+" not supported: "+keys);

				if (cc <= 'Z' && cc >= 'A')
					cc = (char)(cc + ('a' - 'A')); //to lower case
				sbcur.append(cc);
				sbcur = null;
				break;
			}
		}

		_ctkeys = new StringBuffer()
			.append('^').append(sbctl).append(';')
			.append('@').append(sbalt).append(';')
			.append('$').append(sbsft).append(';')
			.append('#').append(sbext).append(';').toString();
		_ctrlKeys = keys;
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
	 * <p>Notice that you can specify "modal" to this method only in an event
	 * listener ({@link Events#inEventListener}).
	 * Rather, you shall use {@link org.zkoss.zk.ui.event.Events#postEvent} to
	 * post the onModal event. For example, in a ZUML page, you can put a window
	 * into modal immediately after rendered as follows.
	 *
	 * <pre><code>
	 *&lt;window title="..."&gt;
	 *...
	 *  &lt;zscript&gt;
	 *    Events.postEvent(Events.ON_MODAL, self, null);
	 *  &lt;/zscript&gt;
	 *&lt;/window&gt;
	 *
	 * @param name the mode which could be one of
	 * "embedded", "overlapped" and "popup".
	 * Note: it cannot be "modal". Use {@link #doModal} instead.
	 */
	public void setMode(String name) throws InterruptedException {
		if ("popup".equals(name)) doPopup();
		else if ("overlapped".equals(name)) doOverlapped();
		else if ("embedded".equals(name)) doEmbedded();
		else if ("modal".equals(name)) 	doModal();
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
	 *
	 * <p>Notice that this method can be called only in an event listener
	 * ({@link Events#inEventListener}).
	 * Rather, you shall use {@link org.zkoss.zk.ui.event.Events#postEvent} to
	 * post the onModal event.
	 * Refer to {@link #setMode} for more description.
	 */
	public void doModal() throws InterruptedException {
		checkOverlappable();

		if (!Events.inEventListener())
			throw new WrongValueException("doModal() and setMode(\"modal\") can only be called in an event listener, not in page loading");

		if (_mode != MODAL || !_moding) {
			endModing();

			if (_mode != MODAL) {
				_mode = MODAL;
				invalidate();
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
				invalidate();
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
				invalidate();
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
			invalidate();
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
	 * If closable, a button is displayed and the onClose event is sent
	 * if an user clicks the button.
	 * <p>Default: false.
	 * <p>You can intercept the default behavior by either overriding
	 * {@link #onClose}, or listening the onClose event.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			invalidate(); //re-init is required
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
			invalidate();
		} else if (insertBefore instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate();
		}
		super.onChildRemoved(child);
	}

	public void setPage(Page page) {
		final Page old = getPage();
		super.setPage(page);
		if (old != page && (old == null || page == null))
			fixMode(page != null);
	}
	public void setParent(Component parent) {
		final Component old = getParent();
		super.setParent(parent);
		if (old != parent && (old == null || parent == null))
			fixMode(parent != null);
	}
	private void fixMode(boolean attached) {
		if (attached) {
			switch (_mode) {
			case OVERLAPPED: doOverlapped();
			case POPUP: doPopup();
			}
		} else {
			endModing();
			if (_mode == MODAL) _mode = EMBEDDED;
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
			HTMLs.appendAttribute(sb, "z.closable", true);
		HTMLs.appendAttribute(sb, "z.ctkeys", _ctkeys);
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
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements MultiBranch {
		//-- MultiBranch --//
		public boolean inDifferentBranch(Component child) {
			return child instanceof Caption; //in different branch
		}
	}
}
