/* Codeeditor.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:05:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zk.ui.ext.Readonly;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A code editor that loads and edits source code with syntax highlighting.
 *
 * <p>The Community Edition build provides basic editing: setting/loading the
 * content ({@link #setValue}), choosing the highlight language
 * ({@link #setLanguage}), toggling read-only ({@link #isReadonly}) and line
 * numbers ({@link #setLineNumbers}), and an optional light/dark editor theme
 * ({@link #setTheme}). Edits are reported to the server through the standard
 * {@code onChange}/{@code onChanging} events carrying an {@link InputEvent}, so
 * {@code value} participates in data binding the same way as other input
 * components.
 *
 * <p>Advanced capabilities (autocomplete, lint, code folding, multi-cursor,
 * search/replace, diff) are not part of the CE build; they are layered on by
 * the EE module through a client-side extension hook, so an application can
 * adopt them later without changing the component or the ZUL.
 *
 * @author peakerlee
 * @since 10.4.0
 */
public class Codeeditor extends XulElement implements Disable, Readonly {
	/** The accepted {@link #setLanguage} tokens. The default {@code "plain"}
	 * must be present, otherwise explicitly setting {@code language="plain"}
	 * would be rejected by {@link Utils#checkEnum}.
	 */
	private static final String[] LANGS = { "plain", "html", "xml", "java", "javascript", "css", "json", "sql",
			"markdown" };

	private String _value = "";
	private String _language = "plain";
	private boolean _readonly;
	private boolean _disabled;
	private boolean _lineNumbers = true;
	/** Unset by default: when null the client derives the editor theme from
	 * {@code zk.themeName}; only an explicitly set value is rendered. */
	private String _theme;

	static {
		addClientEvent(Codeeditor.class, Events.ON_CHANGE, CE_IMPORTANT | CE_REPEAT_IGNORE);
		addClientEvent(Codeeditor.class, Events.ON_CHANGING, CE_DUPLICATE_IGNORE);
		addClientEvent(Codeeditor.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Codeeditor.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
	}

	public Codeeditor() {
	}

	public Codeeditor(String value) {
		setValue(value);
	}

	/**
	 * Returns the editor content.
	 * <p>Default: an empty string.
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Sets the editor content.
	 *
	 * @param value the source code; {@code null} is treated as an empty string.
	 * @since 10.4.0
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	/**
	 * Returns the syntax-highlight language.
	 * <p>Default: {@code "plain"}.
	 */
	public String getLanguage() {
		return _language;
	}

	/**
	 * Sets the syntax-highlight language.
	 *
	 * @param language one of {@code plain}, {@code html}, {@code xml},
	 * {@code java}, {@code javascript}, {@code css}, {@code json}, {@code sql},
	 * {@code markdown}; {@code null} resets it to the default {@code "plain"}.
	 * @exception WrongValueException if {@code language} is non-null and not an
	 * accepted token.
	 * @since 10.4.0
	 */
	public void setLanguage(String language) {
		language = Utils.checkEnum(language, "plain", "Unknown language: ", LANGS);
		if (!Objects.equals(_language, language)) {
			_language = language;
			smartUpdate("language", _language);
		}
	}

	/**
	 * Returns whether the editor is read-only.
	 * <p>Default: false.
	 */
	public boolean isReadonly() {
		return _readonly;
	}

	/**
	 * Sets whether the editor is read-only.
	 *
	 * @param readonly whether to forbid editing the content.
	 * @since 10.4.0
	 */
	public void setReadonly(boolean readonly) {
		if (_readonly != readonly) {
			_readonly = readonly;
			smartUpdate("readonly", _readonly);
		}
	}

	/**
	 * Returns whether the editor is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether the editor is disabled. A disabled editor cannot be edited
	 * or focused and is shown dimmed; unlike {@link #setReadonly}, which keeps
	 * the content focusable and selectable.
	 *
	 * @param disabled whether to disable the editor.
	 * @since 10.4.0
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Returns whether the gutter line numbers are shown.
	 * <p>Default: true.
	 */
	public boolean isLineNumbers() {
		return _lineNumbers;
	}

	/**
	 * Sets whether the gutter line numbers are shown.
	 *
	 * @param lineNumbers whether to show the line-number gutter.
	 * @since 10.4.0
	 */
	public void setLineNumbers(boolean lineNumbers) {
		if (_lineNumbers != lineNumbers) {
			_lineNumbers = lineNumbers;
			smartUpdate("lineNumbers", _lineNumbers);
		}
	}

	/**
	 * Returns the editor theme, or {@code null} when unset.
	 * <p>Default: {@code null}. When unset the client derives the theme from
	 * the page's {@code zk.themeName} (a dark ZK theme yields the dark editor
	 * theme, otherwise light), so the editor follows the page without the
	 * server pushing a value the client already knows.
	 */
	public String getTheme() {
		return _theme;
	}

	/**
	 * Sets the editor theme to explicitly override the {@code zk.themeName}
	 * derived default.
	 *
	 * @param theme {@code light} or {@code dark}; {@code null} restores the
	 * client-derived default.
	 * @exception WrongValueException if {@code theme} is non-null and neither
	 * {@code light} nor {@code dark}.
	 * @since 10.4.0
	 */
	public void setTheme(String theme) {
		if (theme != null)
			theme = Utils.checkEnum(theme, null, "Unknown theme: ", "light", "dark");
		if (!Objects.equals(_theme, theme)) {
			_theme = theme;
			smartUpdate("theme", _theme);
		}
	}

	protected boolean isChildable() {
		return false;
	}

	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		if (!_value.isEmpty())
			render(renderer, "value", _value);
		if (!"plain".equals(_language))
			render(renderer, "language", _language);
		if (_readonly)
			render(renderer, "readonly", true);
		if (_disabled)
			render(renderer, "disabled", true);
		if (!_lineNumbers)
			// lineNumbers defaults to true, so the non-default is false; the
			// boolean render(...) helper skips false, so send it explicitly.
			renderer.render("lineNumbers", false);
		if (_theme != null)
			render(renderer, "theme", _theme);
	}

	//-- ComponentCtrl --//
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHANGE) || cmd.equals(Events.ON_CHANGING)) {
			// Rebuild the payload through the factory (reads only data.get("value"));
			// never trust request.getData() wholesale.
			final InputEvent evt = InputEvent.getInputEvent(request, _value);
			if (cmd.equals(Events.ON_CHANGE)) {
				if (Objects.equals(_value, evt.getValue()))
					return; //Bug 1881557: don't post onChange when the value wasn't modified
				_value = evt.getValue(); // keep the server-side value in sync on commit
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
