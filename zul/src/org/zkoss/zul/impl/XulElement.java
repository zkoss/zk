/* XulElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 16:01:40     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.MCommon;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.in.GenericCommand;
import org.zkoss.zul.Popup;
import org.zkoss.zul.au.in.ColSizeCommand;
import org.zkoss.zul.au.in.PagingCommand;
import org.zkoss.zul.au.in.PageSizeCommand;
import org.zkoss.zul.event.ZulEvents;

/**
 * The fundamental class for XUL elements.
 *
 * <p>Events:<br/>
 * 	onOK, onCacnel and onCtrlKey.<br/>
 * 
 * @author tomyeh
 * @since 3.0.6 supports onOK event.
 * @since 3.0.6 supports onCancel event.
 * @since 3.0.6 supports onCtrlKey event.
 */
abstract public class XulElement extends HtmlBasedComponent implements org.zkoss.zul.impl.api.XulElement {
	static {
		//register commands
		new ColSizeCommand(ZulEvents.ON_COL_SIZE, 0);
			//Don't use Command.IGNORE_OLD_EQUIV since users might drag diff borders
		new PagingCommand(ZulEvents.ON_PAGING, Command.SKIP_IF_EVER_ERROR);
		new PageSizeCommand(ZulEvents.ON_PAGE_SIZE, Command.SKIP_IF_EVER_ERROR);
		new GenericCommand("onRenderAtScroll", Command.IGNORE_OLD_EQUIV);
	}

	/** The popup ID that will be shown when click. */
	private String _popup;
	/** The context ID that will be shown when right-click. */
	private String _ctx;
	/** The tooltip ID that will be shown when mouse-over. */
	private String _tooltip;
	/** The action. */
	private String _action;
	/** What control and function keys to intercepts. */
	private String _ctrlKeys;
	/** The value passed to the client; parsed from _ctrlKeys. */
	private String _ctkeys;


	/** Returns what keystrokes to intercept.
	 * <p>Default: null.
	 * @since 3.0.6
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
	 * <dt>$n</dt>
	 * <dd>A shift key, i.e., Shift+n, where n could be #n.
	 * Note: $a ~ $z are not supported.</dd>
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
	 * @since 3.0.6
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
					throw new WrongValueException("$a - $z not supported ("+keys+"). Supported includes $#f1, $#home and so on.");

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

	/** Returns the ID of the popup ({@link Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>Default: null (no context menu).
	 */
	public String getContext() {
		return _ctx;
	}
	/** Sets the ID of the popup ({@link Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>An onOpen event is sent to the context menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in 
	 * the same ID space or not.
	 *
	 * <p>(since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 *
	 * <p>Example:<br/>
	 * <pre><code>
	 * &lt;label context="some"&gt;
	 * &lt;label context="uuid(${some.uuid})"/&gt;
	 * </code></pre>
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 * 
	 * 
	 * <p> (since 3.6.3) the context menu can be shown by a position from {@link Popup#open(org.zkoss.zk.ui.Component, String)}
	 * or the location of <code>x</code> and <code>y</code>, you can specify the following format:</br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * &lt;button label="show" context="id, start_before"/>
	 * </pre>
	 * @see #setContext(Popup)
	 */
	public void setContext(String context) {
		if (!Objects.equals(_ctx, context)) {
			_ctx = context;
			smartUpdate("z.ctx", _ctx);
		}
	}
	/** Sets the UUID of the popup that should appear 
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>Note: it actually invokes
	 * <code>setContext("uuid(" + popup.getUuid() + ")")</code>
	 * @since 3.0.2
	 * @see #setContext(String)
	 * @see Popup#open(org.zkoss.zk.ui.Component, String)
	 */
	public void setContext(Popup popup) {
		setContext(popup != null ? "uuid(" + popup.getUuid() + ")": null);
	}
	/** Returns the ID of the popup ({@link Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: null (no poppup).
	 */
	public String getPopup() {
		return _popup;
	}
	/** Sets the ID of the popup ({@link Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in 
	 * the same ID space or not.
	 *
	 * <p>(since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * <p> (since 3.6.3) the popup can be shown by a position from {@link Popup#open(org.zkoss.zk.ui.Component, String)}
	 * or the location of <code>x</code> and <code>y</code>, you can specify the following format:</br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * &lt;button label="show" popup="id, start_before"/>
	 * </pre>
	 * @see #setPopup(Popup)
	 * @see Popup#open(org.zkoss.zk.ui.Component, String)
	 */
	public void setPopup(String popup) {
		if (!Objects.equals(_popup, popup)) {
			_popup = popup;
			smartUpdate("z.pop", _popup);
		}
	}
	/** Sets the UUID of the popup that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Note: it actually invokes
	 * <code>setPopup("uuid(" + popup.getUuid() + ")")</code>
	 * @since 3.0.2
	 * @see #setPopup(String)
	 */
	public void setPopup(Popup popup) {
		setPopup(popup != null ? "uuid(" + popup.getUuid() + ")": null);
	}
	/** Returns the ID of the popup ({@link Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 * The tooltip will automatically disappear when the mouse is moved.
	 *
	 * <p>Default: null (no tooltip).
	 */
	public String getTooltip() {
		return _tooltip;
	}
	/** Sets the ID of the popup ({@link Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 *
	 * <p>An onOpen event is sent to the tooltip if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in 
	 * the same ID space or not.
	 *
	 * <p>(since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * <p> (since 3.6.3) the tooltip can be shown by a position from
	 * {@link Popup#open(org.zkoss.zk.ui.Component, String)}
	 * or the location of <code>x</code> and <code>y</code>, and can be specified
	 * with a delay time (in millisecond), you can specify the following format:
	 * </br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start, delay=500</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid2), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * &lt;button label="show" tooltip="id, start_before"/>
	 * </pre>
	 * 
	 * @see #setTooltip(Popup)
	 * @see Popup#open(org.zkoss.zk.ui.Component, String)
	 */
	public void setTooltip(String tooltip) {
		if (!Objects.equals(_tooltip, tooltip)) {
			_tooltip = tooltip;
			smartUpdate("z.tip", _tooltip);
		}
	}
	/** Sets the UUID of the popup that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 *
	 * <p>Note: it actually invokes
	 * <code>setTooltip("uuid(" + popup.getUuid() + ")")</code>
	 * @since 3.0.2
	 * @see #setTooltip(String)
	 */
	public void setTooltip(Popup popup) {
		setTooltip(popup != null ? "uuid(" + popup.getUuid() + ")": null);
	}

	/** Returns the client-side action (CSA).
	 * <p>The format: <br>
	 * action1: javascript1; javascript2; action2: javascript3
	 *
	 * <p>You could specify any action as long as JavaScript supports,
	 * such as onfocus, onblur, onmouseover and onmousout.
	 */
	public String getAction() {
		return _action;
	}
	/** Sets the client-side action.
	 */
	public void setAction(String action) {
		if (action != null && action.length() == 0) action = null;
		if (!Objects.equals(_action, action)) {
			_action = action;
			invalidate();
				//action is rarely changed dynamically, so we
				//don't use smartUpdate (it requires two for-loop to
				//replace and remove actions)
		}
	}

	/** Returns the HTML attributes representing the client-side action,
	 * or "" if no client-side action is defined.
	 * Used only for component development.
	 *
	 * <p>Override this method if you want to customize the generation
	 * of the client-side action (though rarely).
	 *
	 * @since 3.0.0
	 */
	protected String getActionAttrs() {
		if (_action == null)
			return "";

		//To have smaller footprint for each component, we don't cache
		//the parsed result
		final StringBuffer sb = new StringBuffer(100);
		for (Iterator it = parseAction(_action).entrySet().iterator();
		it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			HTMLs.appendAttribute(sb,
				(String)me.getKey(), toJavaScript((String)me.getValue()));
		}
		return sb.toString();
	}

	/**
	 * @deprecated As of release 3.0.5, replaced with {@link HtmlBasedComponent#getAllOnClickAttrs}.
	 * If you want to generate only onDoubleClick and onRightClick, use
	 * <pre><code>
	 *appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
	 *appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
	 *</code></pre>
	 */
	protected String getAllOnClickAttrs(boolean ignoreOnClick) {
		StringBuffer sb = null;
		if (!ignoreOnClick) sb = appendAsapAttr(sb, Events.ON_CLICK);
		sb = appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
		sb = appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
		return sb != null ? sb.toString():  null;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String ctx = getContext(), popup = getPopup(), tip = getTooltip();
			//Let derives (e.g., treerow has a chance to override it)

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		if (ctx != null) HTMLs.appendAttribute(sb, "z.ctx", ctx);
		if (popup != null) HTMLs.appendAttribute(sb, "z.pop", popup);
		if (tip != null) HTMLs.appendAttribute(sb, "z.tip", tip);
		appendAsapAttr(sb, Events.ON_OK);
		appendAsapAttr(sb, Events.ON_CANCEL);
		appendAsapAttr(sb, Events.ON_CTRL_KEY);
		HTMLs.appendAttribute(sb, "z.ctkeys", _ctkeys);
		return sb.toString();
	}

	/** Generates the Client-Side-Action attributes to the interior tag.
	 * Reason: onfocus is the main use.
	 */
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		return _action == null ? attrs: attrs + getActionAttrs();
	}

	/** Returns a map of actions (String name, String javascript).
	 */
	private static final Map parseAction(String action) {
		//1. Look for the first ':'
		final Map map = new HashMap();
		int k = action.indexOf(':');
		if (k < 0) throw new WrongValueException("Unknown action: "+action);

		int j = 0, len = action.length();
		for (;;) {
			String actnm = action.substring(j, k).trim();
			if (actnm.length() == 0) throw new WrongValueException("Unknown action: "+action);

			//2. next ':'
			int l;
			char quote = (char)0; //no quote
			for (j = ++k; ; ++k) {
				if (k >= len) {
					l = len; //next ':'
					break;
				}

				final char cc = action.charAt(k);
				if (cc == '\\')
					continue;

				if (quote != (char)0) {
					if (quote == cc)
						quote = (char)0;
				} else if (cc == '\'' || cc == '"') {
					quote = cc;
				} else if (cc == ';') {
					l = Strings.skipWhitespaces(action, k + 1);
					for (; l < len; ++l) {
						final char c2 = action.charAt(l);
						if ((c2 < 'a' || c2 > 'z') && (c2 < 'A' || c2 > 'Z'))
							break; //inner loop
					}

					l = Strings.skipWhitespaces(action, l);
					if (l >= len) {
						k = len;
						break; //no more action
					}
					if (action.charAt(l) == ':') {
						++k; //after ';'
						break; //found (and there is another action)
					}

					k = l - 1; //since l point the next non-apha
				}
			}

			//3. generate it
			final String val = action.substring(j, k).trim();
			if (val.length() > 0) {
				String nm = actnm.toLowerCase();
				if ("onshow".equals(nm) || "onhide".equals(nm))
					actnm = "z.c" + nm;
				map.put(actnm, val);
			}

			if (l >= len) return map; //done
			j = k;
			k = l;
		}
	}
	/** Converts an action to JavaScript by interpreting #{} properly.
	 */
	private final String toJavaScript(String action) {
		return action != null ?
			ComponentsCtrl.parseClientScript(this, action): null;
	}
}
