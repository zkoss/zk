/* XulElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 16:01:40     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
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
 * @author tomyeh
 */
abstract public class XulElement extends HtmlBasedComponent {
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
	 * @see #setPopup(Popup)
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
	 * @see #setTooltip(Popup)
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

	/** Returns the attributes for onClick, onRightClick and onDoubleClick
	 * by checking whether the corresponding listeners are added,
	 * or null if none is added.
	 *
	 * @param ignoreOnClick whether to ignore onClick
	 */
	protected String getAllOnClickAttrs(boolean ignoreOnClick) {
		StringBuffer sb = null;
		if (!ignoreOnClick) sb = appendAsapAttr(sb, Events.ON_CLICK);
		sb = appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
		sb = appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
		return sb != null ? sb.toString():  null;
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

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String ctx = getContext(), popup = getPopup(), tip = getTooltip();
			//Let derives (e.g., treerow has a chance to override it)
		if (ctx == null &&  tip == null && popup == null)
			return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		HTMLs.appendAttribute(sb, "z.ctx", ctx);
		HTMLs.appendAttribute(sb, "z.pop", popup);
		HTMLs.appendAttribute(sb, "z.tip", tip);
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
