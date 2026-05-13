/* Chip.java

	Purpose:

	Description:

	History:
		Wed Apr 22 2026, Created by yuehfeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.Utils;

/**
 * A display-only chip used to show categories, statuses, or keywords,
 * with an optional close button and a severity theme.
 *
 * <p><b>There is no {@code color} attribute on this component.</b> To
 * customize appearance, either set the {@code style} attribute (e.g.
 * {@code style="--zk-chip-bg: #6366f1;"}) or override the CSS custom
 * properties {@code --zk-chip-bg} / {@code --zk-chip-color} /
 * {@code --zk-chip-border} declared in {@code chip.less}.
 *
 * <p>Default {@link #getZclass}: "z-chip".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Chip extends LabelImageElement implements Disable {
	private static final long serialVersionUID = -7935159928926565441L;
	static {
		addClientEvent(Chip.class, Events.ON_CLOSE, 0);
	}

	private static final String DEFAULT_SEVERITY = "info";
	private static final String DEFAULT_SIZE = "medium";

	private String _severity = DEFAULT_SEVERITY;
	private String _size = DEFAULT_SIZE;
	private boolean _rounded;
	private boolean _closable;
	private boolean _disabled;

	public Chip() {
	}

	public Chip(String label) {
		super(label);
	}

	/** Returns the severity (color theme).
	 * <p>Default: "info".
	 */
	public String getSeverity() {
		return _severity;
	}

	/** Sets the severity.
	 * @param severity one of "info", "success", "warning", "danger", "secondary".
	 *        Pass null to reset to the default ("info").
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

	/** Returns the size of this chip.
	 * <p>Default: "medium". One of "small" or "medium"; medium maps to the
	 * default 22px row, small reduces the row height for dense layouts.
	 */
	public String getSize() {
		return _size;
	}

	/** Sets the chip size.
	 * @param size "small" or "medium". Pass null to reset to the default ("medium").
	 */
	public void setSize(String size) throws WrongValueException {
		size = Utils.checkEnum(size, DEFAULT_SIZE, "size must be small or medium: ", "small", "medium");
		if (!Objects.equals(_size, size)) {
			_size = size;
			smartUpdate("size", _size);
		}
	}

	/** Whether this chip is non-interactive. Disabled chips ignore close
	 * clicks and render at reduced opacity.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/** Sets whether this chip is non-interactive. Disabled chips ignore close
	 * clicks and render at reduced opacity.
	 * @param disabled whether the chip is disabled. Calls smartUpdate when changed.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/** Whether this chip is rounded (pill-shaped). */
	public boolean isRounded() {
		return _rounded;
	}

	/** Sets whether this chip is rounded (pill-shaped).
	 * @param rounded whether the chip is rounded. Calls smartUpdate when changed.
	 */
	public void setRounded(boolean rounded) {
		if (_rounded != rounded) {
			_rounded = rounded;
			smartUpdate("rounded", _rounded);
		}
	}

	/** Whether this chip displays a close button. */
	public boolean isClosable() {
		return _closable;
	}

	/** Sets whether this chip displays a close button.
	 * @param closable whether the chip displays a close button. Calls smartUpdate when changed.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("closable", _closable);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-chip" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (!"info".equals(_severity))
			render(renderer, "severity", _severity);
		if (!"medium".equals(_size))
			render(renderer, "size", _size);
		if (_rounded)
			render(renderer, "rounded", _rounded);
		if (_closable)
			render(renderer, "closable", _closable);
		if (_disabled)
			render(renderer, "disabled", _disabled);
	}

	/** Handles the onClose event sent when the close button is clicked.
	 * <p>Default: detach this chip. Override or use {@code event.stopPropagation()}
	 * to keep the chip visible. Follows the same pattern as
	 * {@link Window#onClose} — the framework's event-dispatch machinery
	 * skips the default handler when a prior listener stops propagation.
	 * @since 10.4.0
	 */
	public void onClose() {
		// The close button is rendered disabled client-side, but a
		// hand-crafted onClose AU request could still reach here; the server
		// must not trust the client's disabled enforcement.
		if (isDisabled())
			return;
		detach();
	}

	@Override
	public boolean isChildable() {
		return false;
	}
}
