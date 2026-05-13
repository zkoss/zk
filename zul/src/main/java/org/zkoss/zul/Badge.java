/* Badge.java

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
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A badge (count or status indicator). May be used standalone for pure
 * visual display, or wrap around a child component to pin an indicator
 * onto the child (e.g., a Button with "3 new" in the top-right corner).
 *
 * <p>Default {@link #getZclass}: "z-badge".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Badge extends XulElement {
	private static final long serialVersionUID = 3949753706866419036L;
	private static final int DEFAULT_MAX = 99;
	private static final String DEFAULT_SEVERITY = "info";
	private static final String DEFAULT_PLACEMENT = "top_right";

	private String _value;
	private int _count;
	private int _max = DEFAULT_MAX;
	private boolean _showZero;
	private boolean _dot;
	private String _severity = DEFAULT_SEVERITY;
	private String _placement = DEFAULT_PLACEMENT;

	public Badge() {
	}

	public Badge(int count) {
		setCount(count);
	}

	public Badge(String value) {
		setValue(value);
	}

	/** Returns the text value of this badge.
	 * <p>Default: null. When non-null, takes precedence over {@link #getCount}.
	 */
	public String getValue() {
		return _value;
	}

	/** Sets the text value of this badge.
	 */
	public void setValue(String value) {
		if (value != null && value.isEmpty())
			value = null;
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	/** Returns the count of this badge.
	 * <p>Default: 0.
	 */
	public int getCount() {
		return _count;
	}

	/** Sets the count of this badge.
	 * @throws WrongValueException if {@code count} is negative — a badge is a
	 *     counter, not a signed value.
	 */
	public void setCount(int count) throws WrongValueException {
		if (count < 0)
			throw new WrongValueException("count cannot be negative: " + count);
		if (_count != count) {
			_count = count;
			smartUpdate("count", _count);
		}
	}

	/**
	 * Returns the string that will actually be rendered inside the badge,
	 * resolved from the current property set:
	 * <ul>
	 *   <li>{@code dot=true} — returns {@code ""} (dot mode shows no text).</li>
	 *   <li>{@code value != null} — returns the value verbatim (takes
	 *       precedence over {@code count}).</li>
	 *   <li>{@code count > max} — returns {@code "{max}+"}.</li>
	 *   <li>{@code count == 0 && !showZero} — returns {@code ""}
	 *       (badge collapses to nothing).</li>
	 *   <li>otherwise — returns the decimal string of {@code count}.</li>
	 * </ul>
	 * Useful for server-side test assertions and for binding the same
	 * visual text into another component without re-implementing the rules.
	 * @since 10.4.0
	 */
	public String getDisplayValue() {
		if (_dot) return "";
		if (_value != null) return _value;
		if (_count > _max) return _max + "+";
		if (_count == 0 && !_showZero) return "";
		return Integer.toString(_count);
	}

	/** Returns the maximum count to display before falling back to "{max}+".
	 * <p>Default: 99.
	 */
	public int getMax() {
		return _max;
	}

	/** Sets the maximum count.
	 * @throws WrongValueException if {@code max < 1} — the cap must be at least
	 *     1 for the "{max}+" overflow indicator to make sense.
	 */
	public void setMax(int max) throws WrongValueException {
		if (max < 1)
			throw new WrongValueException("max must be >= 1: " + max);
		if (_max != max) {
			_max = max;
			smartUpdate("max", _max);
		}
	}

	/** Whether to display when count is zero.
	 * <p>Default: false.
	 */
	public boolean isShowZero() {
		return _showZero;
	}

	/** Sets whether to display when count is zero.
	 * @param showZero whether the badge shows when the count is zero; default false. Calls smartUpdate when changed.
	 */
	public void setShowZero(boolean showZero) {
		if (_showZero != showZero) {
			_showZero = showZero;
			smartUpdate("showZero", _showZero);
		}
	}

	/** Whether to render as a small red dot, ignoring value and count text.
	 * <p>Default: false.
	 */
	public boolean isDot() {
		return _dot;
	}

	/** Sets whether to render as a small red dot, ignoring value and count text.
	 * @param dot whether to render as a dot instead of value/count text; default false. Calls smartUpdate when changed.
	 */
	public void setDot(boolean dot) {
		if (_dot != dot) {
			_dot = dot;
			smartUpdate("dot", _dot);
		}
	}

	/** Returns the severity (color theme).
	 * <p>Default: "info".
	 */
	public String getSeverity() {
		return _severity;
	}

	/** Sets the severity.
	 * @param severity "info", "success", "warning", "danger" or "secondary".
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

	/** Returns the placement of the indicator relative to the wrapped child.
	 * <p>Default: "top_right". Only effective in wrap mode (when the badge has children).
	 */
	public String getPlacement() {
		return _placement;
	}

	/** Sets the placement.
	 * @param placement "top_right", "top_left", "bottom_right" or "bottom_left".
	 */
	public void setPlacement(String placement) throws WrongValueException {
		placement = Utils.checkEnum(placement, DEFAULT_PLACEMENT,
				"placement must be top_right/top_left/bottom_right/bottom_left: ",
				"top_right", "top_left", "bottom_right", "bottom_left");
		if (!Objects.equals(_placement, placement)) {
			_placement = placement;
			smartUpdate("placement", _placement);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-badge" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "value", _value);
		// Guard at the default (0) like every other property here; the client
		// widget's _count also defaults to 0, so an unset badge needs no count
		// in the payload. showZero is rendered separately, so the count==0 +
		// showZero case still resolves to "0" client-side.
		if (_count != 0)
			render(renderer, "count", _count);
		if (_max != DEFAULT_MAX)
			render(renderer, "max", _max);
		if (_showZero)
			render(renderer, "showZero", _showZero);
		if (_dot)
			render(renderer, "dot", _dot);
		if (!DEFAULT_SEVERITY.equals(_severity))
			render(renderer, "severity", _severity);
		if (!DEFAULT_PLACEMENT.equals(_placement))
			render(renderer, "placement", _placement);
	}
}
