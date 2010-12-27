package org.zkoss.zul.impl.api;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Popup;

/**
 * The fundamental class for XUL elements.
 * 
 * <p>
 * Events:<br/>
 * onOK, onCacnel and onCtrlKey.<br/>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface XulElement extends org.zkoss.zk.ui.api.HtmlBasedComponent {
	/**
	 * Returns what keystrokes to intercept.
	 * <p>
	 * Default: null.
	 */
	public String getCtrlKeys();

	/**
	 * Sets what keystrokes to intercept.
	 * 
	 * <p>
	 * The string could be a combination of the following:
	 * <dl>
	 * <dt>^k</dt>
	 * <dd>A control key, i.e., Ctrl+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>@k</dt>
	 * <dd>A alt key, i.e., Alt+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>$n</dt>
	 * <dd>A shift key, i.e., Shift+n, where n could be #n. Note: $a ~ $z are
	 * not supported.</dd>
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
	 * <p>
	 * For example,
	 * <dl>
	 * <dt>^a^d@c#f10#left#right</dt>
	 * <dd>It means you want to intercept Ctrl+A, Ctrl+D, Alt+C, F10, Left and
	 * Right.</dd>
	 * <dt>^#left</dt>
	 * <dd>It means Ctrl+Left.</dd>
	 * <dt>^#f1</dt>
	 * <dd>It means Ctrl+F1.</dd>
	 * <dt>@#f3</dt>
	 * <dd>It means Alt+F3.</dd>
	 * </dl>
	 * 
	 * <p>
	 * Note: it doesn't support Ctrl+Alt, Shift+Ctrl, Shift+Alt or
	 * Shift+Ctrl+Alt.
	 */
	public void setCtrlKeys(String ctrlKeys) throws UiException;

	/**
	 * Returns the ID of the popup ({@link Popup}) that should appear when the
	 * user right-clicks on the element (aka., context menu).
	 * 
	 * <p>
	 * Default: null (no context menu).
	 */
	public String getContext();

	/**
	 * Sets the ID of the popup ({@link Popup}) that should appear when the user
	 * right-clicks on the element (aka., context menu).
	 * 
	 * <p>
	 * An onOpen event is sent to the context menu if it is going to appear.
	 * Therefore, developers can manipulate it dynamically (perhaps based on
	 * OpenEvent.getReference) by listening to the onOpen event.
	 * 
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 * 
	 * <p>
	 * (since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following format:
	 * <br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * <p>
	 * Example:<br/>
	 * 
	 * <pre>
	 * &lt;code&gt;
	 * &lt;label context=&quot;some&quot;&gt;
	 * &lt;label context=&quot;uuid(${some.uuid})&quot;/&gt;
	 * &lt;/code&gt;
	 * </pre>
	 * 
	 * Both reference a component whose ID is "some". But, if there are several
	 * components with the same ID, the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space (of
	 * the label component).
	 * 
	 * @see #setContext(Popup)
	 */
	public void setContext(String context);

	/**
	 * Sets the UUID of the popup that should appear when the user right-clicks
	 * on the element (aka., context menu).
	 * 
	 * <p>
	 * Note: it actually invokes
	 * <code>setContext("uuid(" + popup.getUuid() + ")")</code>
	 * 
	 * @see #setContext(String)
	 */
	public void setContext(Popup popup);

	/**
	 * Returns the ID of the popup ({@link Popup}) that should appear when the
	 * user clicks on the element.
	 * 
	 * <p>
	 * Default: null (no poppup).
	 */
	public String getPopup();

	/**
	 * Sets the ID of the popup ({@link Popup}) that should appear when the user
	 * clicks on the element.
	 * 
	 * <p>
	 * An onOpen event is sent to the popup menu if it is going to appear.
	 * Therefore, developers can manipulate it dynamically (perhaps based on
	 * OpenEvent.getReference) by listening to the onOpen event.
	 * 
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 * 
	 * <p>
	 * (since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following format:
	 * <br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * @see #setPopup(Popup)
	 */
	public void setPopup(String popup);

	/**
	 * Sets the UUID of the popup that should appear when the user clicks on the
	 * element.
	 * 
	 * <p>
	 * Note: it actually invokes
	 * <code>setPopup("uuid(" + popup.getUuid() + ")")</code>
	 * 
	 * @see #setPopup(String)
	 */
	public void setPopup(Popup popup);

	/**
	 * Returns the ID of the popup ({@link Popup}) that should be used as a
	 * tooltip window when the mouse hovers over the element for a moment. The
	 * tooltip will automatically disappear when the mouse is moved.
	 * 
	 * <p>
	 * Default: null (no tooltip).
	 */
	public String getTooltip();

	/**
	 * Sets the ID of the popup ({@link Popup}) that should be used as a tooltip
	 * window when the mouse hovers over the element for a moment.
	 * 
	 * <p>
	 * An onOpen event is sent to the tooltip if it is going to appear.
	 * Therefore, developers can manipulate it dynamically (perhaps based on
	 * OpenEvent.getReference) by listening to the onOpen event.
	 * 
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 * 
	 * <p>
	 * (since 3.0.2) If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following format:
	 * <br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * @see #setTooltip(Popup)
	 */
	public void setTooltip(String tooltip);

	/**
	 * Sets the UUID of the popup that should be used as a tooltip window when
	 * the mouse hovers over the element for a moment.
	 * 
	 * <p>
	 * Note: it actually invokes
	 * <code>setTooltip("uuid(" + popup.getUuid() + ")")</code>
	 * 
	 * @see #setTooltip(String)
	 */
	public void setTooltip(Popup popup);
}
