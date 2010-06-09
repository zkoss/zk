/* XulElement.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 16:01:40     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
import org.zkoss.zul.Popup;

/**
 * The fundamental class for XUL elements.
 * 
 * @author tomyeh
 */
abstract public class XulElement extends HtmlBasedComponent implements org.zkoss.zul.impl.api.XulElement {

	/** The popup ID that will be shown when click. */
	private String _popup;
	/** The context ID that will be shown when right-click. */
	private String _ctx;
	/** The tooltip ID that will be shown when mouse-over. */
	private String _tooltip;
	/** What control and function keys to intercepts. */
	private String _ctrlKeys;


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
			_ctrlKeys = ctrlKeys;
			smartUpdate("ctrlKeys", _ctrlKeys);
		}
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
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
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
			smartUpdate("context", _ctx);
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
	 * <p>Default: null (no popup).
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
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
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
			smartUpdate("popup", _popup);
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
	 * The tooltip will automatically disappear when the mouse is moved away.
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
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
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
			smartUpdate("tooltip", _tooltip);
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

	/** @deprecated since 5.0.0, use client-side event listener instead.
	 */
	public String getAction() {
		return null;
	}
	/** @deprecated since 5.0.0, use client-side event listener instead.
	 */
	public void setAction(String action) {
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "popup", _popup);
		render(renderer, "context", _ctx);
		render(renderer, "tooltip", _tooltip);
		render(renderer, "ctrlKeys", _ctrlKeys);
	}
}
