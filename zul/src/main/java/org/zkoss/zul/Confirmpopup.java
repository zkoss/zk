/* Confirmpopup.java

	Purpose:

	Description:

	History:
		Thu Apr 23 2026, Created by yuehfeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.Utils;

/**
 * An inline confirmation popup — lighter than {@link Messagebox}, anchored to
 * a trigger component. Fires {@link Events#ON_OK} when the user confirms,
 * {@link Events#ON_CANCEL} when they cancel or dismiss.
 *
 * <p>Default {@link #getZclass}: "z-confirmpopup".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Confirmpopup extends Popup {
	private static final long serialVersionUID = 9019884629051101091L;
	static {
		addClientEvent(Confirmpopup.class, Events.ON_OK, 0);
		addClientEvent(Confirmpopup.class, Events.ON_CANCEL, 0);
	}

	private static final String DEFAULT_ICON_SCLASS = "z-icon-exclamation-triangle";
	private static final String DEFAULT_SEVERITY = "warning";
	private static final String DEFAULT_PLACEMENT = "top";
	private static final String DEFAULT_FOCUS = "ok";

	private String _header;
	private String _message;
	private String _iconSclass = DEFAULT_ICON_SCLASS;
	private String _severity = DEFAULT_SEVERITY;
	private String _placement = DEFAULT_PLACEMENT;
	private String _defaultFocus = DEFAULT_FOCUS;

	public Confirmpopup() {
	}

	public Confirmpopup(String message) {
		setMessage(message);
	}

	/** Returns the optional header (title row) shown above the message body.
	 * Mirrors PrimeNG Confirmpopup's <code>header</code> property.
	 * @since 10.4.0
	 */
	public String getHeader() {
		return _header;
	}

	/** Sets the optional header (title row) shown above the message body.
	 * @param header the header text; {@code null} or an empty string clears it
	 *        (the popup renders with no title row). Pushes the change to the
	 *        client via {@code smartUpdate}.
	 * @since 10.4.0
	 */
	public void setHeader(String header) {
		if (header != null && header.isEmpty())
			header = null;
		if (!Objects.equals(_header, header)) {
			_header = header;
			smartUpdate("header", _header);
		}
	}

	/** Returns the confirmation message shown in the popup body.
	 * <p>Default: {@code null} (no message).
	 * @since 10.4.0
	 */
	public String getMessage() {
		return _message;
	}

	/** Sets the confirmation message shown in the popup body.
	 * @param message the message text; {@code null} or an empty string clears
	 *        it. Pushes the change to the client via {@code smartUpdate}.
	 * @since 10.4.0
	 */
	public void setMessage(String message) {
		if (message != null && message.isEmpty())
			message = null;
		if (!Objects.equals(_message, message)) {
			_message = message;
			smartUpdate("message", _message);
		}
	}

	/** Returns the icon CSS class shown beside the message.
	 * <p>Default: {@value #DEFAULT_ICON_SCLASS}. An empty string means the
	 * icon is explicitly suppressed (no icon node is rendered).
	 * @since 10.4.0
	 */
	public String getIconSclass() {
		return _iconSclass;
	}

	/** Sets the icon CSS class.
	 * <ul>
	 *   <li>{@code null} restores the default ({@value #DEFAULT_ICON_SCLASS}).</li>
	 *   <li>An empty string explicitly suppresses the icon — the popup renders
	 *       with no icon node.</li>
	 *   <li>Any other value is used as the icon's CSS class.</li>
	 * </ul>
	 */
	public void setIconSclass(String iconSclass) {
		// null and "" are intentionally distinct: null means "use default",
		// "" means "no icon". The empty string is preserved as the cleared
		// sentinel; the renderer + client widget treat it as suppression.
		String next = (iconSclass == null) ? DEFAULT_ICON_SCLASS : iconSclass;
		if (!Objects.equals(_iconSclass, next)) {
			_iconSclass = next;
			smartUpdate("iconSclass", _iconSclass);
		}
	}

	/** Returns the severity, which drives the icon/color styling of the popup.
	 * <p>Default: {@value #DEFAULT_SEVERITY}. One of "info", "success",
	 * "warning", "danger" or "secondary".
	 * @since 10.4.0
	 */
	public String getSeverity() {
		return _severity;
	}

	/** Sets the severity, which drives the icon/color styling of the popup.
	 * @param severity one of "info", "success", "warning", "danger" or
	 *        "secondary"; {@code null} restores the default
	 *        ({@value #DEFAULT_SEVERITY}). Pushes the change to the client via
	 *        {@code smartUpdate}.
	 * @throws WrongValueException if {@code severity} is non-null and is not one
	 *         of the accepted tokens.
	 * @since 10.4.0
	 */
	public void setSeverity(String severity) throws WrongValueException {
		severity = Utils.checkEnum(severity, DEFAULT_SEVERITY,
				"severity must be info/success/warning/danger/secondary: ",
				"info", "success", "warning", "danger", "secondary");
		if (!Objects.equals(_severity, severity)) {
			_severity = severity;
			smartUpdate("severity", _severity);
		}
	}

	/**
	 * @return the placement of this popup relative to the trigger element.
	 *         One of "top" (default) / "bottom" / "left" / "right". The
	 *         arrow points from this popup toward the trigger.
	 * @since 10.4.0
	 */
	public String getPlacement() {
		return _placement;
	}

	/** Sets the placement of this popup relative to the trigger element.
	 * @param placement one of "top" (default), "bottom", "left" or "right";
	 *        {@code null} restores the default ({@value #DEFAULT_PLACEMENT}).
	 *        Pushes the change to the client via {@code smartUpdate}.
	 * @throws WrongValueException if {@code placement} is non-null and is not
	 *         one of the accepted tokens.
	 * @since 10.4.0
	 */
	public void setPlacement(String placement) throws WrongValueException {
		placement = Utils.checkEnum(placement, DEFAULT_PLACEMENT,
				"placement must be top/bottom/left/right: ",
				"top", "bottom", "left", "right");
		if (!Objects.equals(_placement, placement)) {
			_placement = placement;
			smartUpdate("placement", _placement);
		}
	}

	/**
	 * @return which button gets keyboard focus when the popup opens — either
	 *         "ok" (default) or "cancel". For destructive operations
	 *         (severity="danger"), prefer "cancel" so an accidental Enter
	 *         keypress does not commit the action.
	 * @since 10.4.0
	 */
	public String getDefaultFocus() {
		return _defaultFocus;
	}

	/** Sets which button gets keyboard focus when the popup opens.
	 * @param defaultFocus either "ok" (default) or "cancel"; {@code null}
	 *        restores the default ({@value #DEFAULT_FOCUS}). For destructive
	 *        operations (severity="danger"), prefer "cancel" so an accidental
	 *        Enter keypress does not commit the action. Pushes the change to
	 *        the client via {@code smartUpdate}.
	 * @throws WrongValueException if {@code defaultFocus} is non-null and is
	 *         neither "ok" nor "cancel".
	 * @since 10.4.0
	 */
	public void setDefaultFocus(String defaultFocus) throws WrongValueException {
		defaultFocus = Utils.checkEnum(defaultFocus, DEFAULT_FOCUS, "defaultFocus must be ok or cancel: ", "ok", "cancel");
		if (!Objects.equals(_defaultFocus, defaultFocus)) {
			_defaultFocus = defaultFocus;
			smartUpdate("defaultFocus", _defaultFocus);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-confirmpopup" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "header", _header);
		render(renderer, "message", _message);
		// "" is the "explicitly cleared" sentinel — the inherited render()
		// helper would skip it (AbstractComponent.render treats empty as
		// no-op), so call renderer.render() directly to push the empty
		// string across the wire and let the client widget suppress the icon.
		if (!DEFAULT_ICON_SCLASS.equals(_iconSclass)) {
			if (_iconSclass.isEmpty())
				renderer.render("iconSclass", "");
			else
				render(renderer, "iconSclass", _iconSclass);
		}
		if (!DEFAULT_SEVERITY.equals(_severity))
			render(renderer, "severity", _severity);
		if (!DEFAULT_PLACEMENT.equals(_placement))
			render(renderer, "placement", _placement);
		if (!DEFAULT_FOCUS.equals(_defaultFocus))
			render(renderer, "defaultFocus", _defaultFocus);
	}
}
