/* Codeeditor.ts

	Purpose:

	Description:

	History:
		Wed Jun 17 22:25:01 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
import { EditorState, Compartment, Prec, type Extension } from '@codemirror/state';
import { EditorView, lineNumbers, keymap } from '@codemirror/view';
import { defaultKeymap, history, historyKeymap, indentWithTab } from '@codemirror/commands';
import { syntaxHighlighting, defaultHighlightStyle, HighlightStyle, indentUnit } from '@codemirror/language';
import { tags } from '@lezer/highlight'; // CodeMirror's highlight-tag vocabulary; a hard peer of @codemirror/language
import { xml } from '@codemirror/lang-xml';
import { java } from '@codemirror/lang-java';
import { javascript } from '@codemirror/lang-javascript';
import { css } from '@codemirror/lang-css';
import { json } from '@codemirror/lang-json';
import { html } from '@codemirror/lang-html';
import { sql } from '@codemirror/lang-sql';
import { markdown } from '@codemirror/lang-markdown';

/**
 * Dark-surface syntax palette, used when the editor renders dark; light mode
 * keeps CodeMirror's `defaultHighlightStyle`. Colors are VS Code Dark+ inspired
 * to read well on the `#1e1e1e` surface, and each is themeable through a
 * `--zk-codeeditor-token-*` custom property (see the component LESS), with the
 * literal as the fallback when no theme overrides it.
 */
const darkHighlightStyle = HighlightStyle.define([
	{ tag: tags.keyword, color: 'var(--zk-codeeditor-token-keyword, #569cd6)' },
	{ tag: [tags.string, tags.special(tags.string)], color: 'var(--zk-codeeditor-token-string, #ce9178)' },
	{ tag: [tags.comment, tags.lineComment, tags.blockComment], color: 'var(--zk-codeeditor-token-comment, #6a9955)', fontStyle: 'italic' },
	{ tag: [tags.number, tags.bool, tags.null, tags.atom], color: 'var(--zk-codeeditor-token-number, #b5cea8)' },
	{ tag: [tags.function(tags.variableName), tags.function(tags.propertyName)], color: 'var(--zk-codeeditor-token-function, #dcdcaa)' },
	{ tag: [tags.typeName, tags.className, tags.namespace], color: 'var(--zk-codeeditor-token-type, #4ec9b0)' },
	{ tag: [tags.variableName, tags.propertyName], color: 'var(--zk-codeeditor-token-variable, #9cdcfe)' },
	{ tag: tags.tagName, color: 'var(--zk-codeeditor-token-tag, #569cd6)' },
	{ tag: tags.attributeName, color: 'var(--zk-codeeditor-token-attribute, #9cdcfe)' },
	{ tag: [tags.meta, tags.regexp, tags.escape], color: 'var(--zk-codeeditor-token-meta, #d16969)' },
]);

/**
 * A code editor that loads and edits source code with syntax highlighting,
 * the client half of the server-side `org.zkoss.zul.Codeeditor` component.
 *
 * <p>The Community Edition build wraps a self-assembled CodeMirror 6 editor
 * (line numbers, history, syntax highlighting, editable toggle) and reports
 * edits to the server as `onChanging` (while typing) and `onChange` (on blur).
 * The grammars for all supported languages are bundled into this package, so
 * only pages that actually use `<codeeditor>` pay for the editor weight.
 *
 * @author peakerlee
 * @since 10.4.0
 */
@zk.WrapClass('zul.code.Codeeditor')
export class Codeeditor extends zul.Widget {
	/** @internal */
	_value = '';
	/** @internal */
	_language = 'plain';
	/** @internal */
	_readonly = false;
	/** @internal */
	_disabled = false;
	/** @internal */
	_lineNumbers = true;
	/** @internal unset → derived from `zk.themeName` (a dark page theme yields the dark editor). */
	_theme?: string;
	/** @internal */
	_cm?: EditorView;
	/** @internal value last synchronized with the server (initial, after a commit, or after a programmatic set); the onChange/blur commit baseline. */
	_committed = '';
	/** @internal true while `_setDoc` pushes a server/programmatic value, so the doc-change listener does not echo it back to the server as `onChanging`. */
	_settingDoc = false;
	/** @internal page CSP nonce, captured once in `bind_` before the bootstrap scripts are pruned, then reused across rerenders. */
	_nonce?: string;
	/** @internal */
	_langComp = new Compartment();
	/** @internal */
	_roComp = new Compartment();
	/** @internal */
	_lnComp = new Compartment();
	/** @internal */
	_themeComp = new Compartment();
	/** @internal toggles `indentWithTab` in/out — see `_toggleTabIndent`. */
	_tabComp = new Compartment();
	/** @internal false (default) → Tab moves focus (no keyboard trap); true → Tab indents. Toggled by Escape. */
	_tabIndent = false;
	/** @internal reconfigures the tab display width — see `setTabSize`. */
	_tabSizeComp = new Compartment();
	/** @internal columns a tab character is rendered as (CM `EditorState.tabSize`). */
	_tabSize = 4;

	/**
	 * Returns the editor content.
	 */
	getValue(): string {
		return this._value;
	}

	/**
	 * Sets the editor content.
	 * @param value - the source code.
	 * @param opts - pass `{force: true}` to push the value even when unchanged.
	 */
	setValue(value: string, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value ?? '';
		if (this._cm && (o !== this._value || opts?.force))
			this._setDoc(this._value);
		return this;
	}

	/**
	 * Returns the syntax-highlight language.
	 */
	getLanguage(): string {
		return this._language;
	}

	/**
	 * Sets the syntax-highlight language and reconfigures the live editor.
	 * @param language - one of the supported grammar tokens, or `plain`.
	 */
	setLanguage(language: string, opts?: Record<string, boolean>): this {
		const o = this._language;
		this._language = language || 'plain';
		if (this._cm && (o !== this._language || opts?.force))
			this._cm.dispatch({ effects: this._langComp.reconfigure(this._langExtension()) });
		return this;
	}

	/**
	 * Returns whether the editor is read-only.
	 */
	isReadonly(): boolean {
		return this._readonly;
	}

	/**
	 * Sets whether the editor is read-only and reconfigures the live editor.
	 * @param readonly - whether to forbid editing.
	 */
	setReadonly(readonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._readonly;
		this._readonly = readonly;
		if (this._cm && (o !== readonly || opts?.force))
			this._applyEditable();
		return this;
	}

	/**
	 * Returns whether the editor is disabled.
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/**
	 * Sets whether the editor is disabled: not editable, not focusable, dimmed.
	 * @param disabled - whether to disable the editor.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;
		if (this._cm && (o !== disabled || opts?.force)) {
			this._applyEditable();
			this._applyDisabledClass();
		}
		return this;
	}

	/** @internal reconfigures the live editor's editable/read-only state from `_readonly || _disabled`. */
	_applyEditable(): void {
		this._cm!.dispatch({ effects: this._roComp.reconfigure(this._editableExtension()) });
	}

	/**
	 * Returns whether the gutter line numbers are shown.
	 */
	isLineNumbers(): boolean {
		return this._lineNumbers;
	}

	/**
	 * Sets whether the gutter line numbers are shown and reconfigures the editor.
	 * @param lineNumbers - whether to show the line-number gutter.
	 */
	setLineNumbers(lineNumbers: boolean, opts?: Record<string, boolean>): this {
		const o = this._lineNumbers;
		this._lineNumbers = lineNumbers;
		if (this._cm && (o !== lineNumbers || opts?.force))
			this._cm.dispatch({ effects: this._lnComp.reconfigure(this._lineNumbersExtension()) });
		return this;
	}

	/**
	 * Returns the number of columns a tab character is rendered as.
	 */
	getTabSize(): number {
		return this._tabSize;
	}

	/**
	 * Sets how many columns a tab character is rendered as and reconfigures the editor.
	 * @param tabSize - a positive column count (default 4); the indent character itself stays a tab.
	 */
	setTabSize(tabSize: number, opts?: Record<string, boolean>): this {
		const o = this._tabSize;
		this._tabSize = tabSize;
		if (this._cm && (o !== tabSize || opts?.force))
			this._cm.dispatch({ effects: this._tabSizeComp.reconfigure(this._tabSizeExtension()) });
		return this;
	}

	/**
	 * Returns the explicitly set editor theme, or `undefined` when it follows
	 * the page's `zk.themeName`.
	 */
	getTheme(): string | undefined {
		return this._theme;
	}

	/**
	 * Sets the editor theme to override the `zk.themeName` derived default.
	 * @param theme - `light`, `dark`, or `undefined` to follow the page.
	 */
	setTheme(theme: string, opts?: Record<string, boolean>): this {
		const o = this._theme;
		this._theme = theme;
		if (this._cm && (o !== theme || opts?.force)) {
			this._cm.dispatch({ effects: this._themeComp.reconfigure(this._themeExtension()) });
			this._applyDarkClass();
		}
		return this;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// Read once and keep it: the page nonce is constant, and the bootstrap
		// scripts (where it is read from) are pruned during afterMount, so a later
		// rerender's bind_ can no longer find it — reuse the first good read.
		if (this._nonce === undefined)
			this._nonce = this._cspNonce();
		zk.afterMount(() => this._mount()); // CM needs a stable DOM node
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._cm) {
			// null _cm BEFORE destroy: CM's destroy() blurs a focused contentDOM,
			// firing a synchronous focusout — the handler's `if (!this._cm)` guard
			// must already see undefined so it doesn't commit/fire onBlur mid-unbind.
			const cm = this._cm;
			this._cm = undefined;
			cm.destroy();
		}
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal builds the fixed CE editor set; EE layers more via `_extraExtensions`. */
	_baseExtensions(): Extension[] {
		return [
			history(),
			indentUnit.of('\t'), // indent unit = a tab character (Tab-indent and Ctrl-]/Ctrl-[)
			keymap.of([...defaultKeymap, ...historyKeymap]),
			// Escape toggles "editing mode": Tab moves focus by default (no keyboard
			// trap), indents after Escape, and returns to focus-nav on Escape again.
			// Prec.low so a future EE popup (autocomplete/search) consumes Escape first.
			Prec.low(keymap.of([{ key: 'Escape', run: (): boolean => { this._toggleTabIndent(); return true; } }])),
			EditorView.updateListener.of((u) => {
				if (u.docChanged)
					this._onDocChanged(u.state.doc.toString());
			}),
			this._lnComp.of(this._lineNumbersExtension()),
			this._roComp.of(this._editableExtension()),
			this._langComp.of(this._langExtension()),
			this._themeComp.of(this._themeExtension()),
			this._tabComp.of(this._tabExtension()),
			this._tabSizeComp.of(this._tabSizeExtension()),
			...this._extraExtensions(),
		];
	}

	/**
	 * @internal Sanctioned extension point for CodeMirror extensions contributed
	 * by subclasses (e.g. a markdown editor's bold/italic keymap) and by the EE
	 * build (autocomplete/lint/fold/…). CE itself contributes none.
	 *
	 * Contract: an override MUST chain — `return super._extraExtensions().concat(...)` —
	 * and never replace without calling super, so that a subclass's extensions and
	 * the EE augmentation compose instead of shadowing one another.
	 */
	_extraExtensions(): Extension[] {
		return [];
	}

	/** @internal */
	_mount(): void {
		if (this._cm || !this.desktop)
			return;
		const cave = this.$n('cave');
		if (!cave)
			return;
		const exts = this._baseExtensions();
		if (this._nonce)
			exts.push(EditorView.cspNonce.of(this._nonce)); // CSP strict-dynamic: tag CM's injected <style>
		// forward an app-supplied accessible name (ca:aria-label / ca:aria-labelledby on
		// the root) onto CM's editable textbox, where screen readers actually need it.
		const root = this.$n(),
			// always describe the Tab/Escape keyboard model; add an app-supplied name when present.
			aria: Record<string, string> = { 'aria-describedby': this.uuid + '-status' },
			label = root?.getAttribute('aria-label'),
			labelledby = root?.getAttribute('aria-labelledby');
		if (label) aria['aria-label'] = label;
		if (labelledby) aria['aria-labelledby'] = labelledby;
		exts.push(EditorView.contentAttributes.of(aria));
		this._cm = new EditorView({
			state: EditorState.create({ doc: this._value, extensions: exts }),
			parent: cave,
		});
		// CodeMirror normalizes line endings (CRLF/CR → LF) in its doc. Baseline
		// _committed/_value from the actual doc, not the raw server string, so a
		// CRLF value doesn't look "changed" on the first blur and fire a spurious
		// onChange that rewrites the server value to LF.
		this._committed = this._value = this._cm.state.doc.toString();
		this._applyDarkClass(); // surface theming is applied via the z-codeeditor-dark LESS class
		this._applyDisabledClass();
		const dom = this._cm.contentDOM;
		// CM6 has no native blur-commit: commit onChange when the editor loses focus.
		// Compare against _committed, not _value — _value tracks every onChanging
		// keystroke, so guarding on it would suppress the commit entirely.
		dom.addEventListener('focusout', () => {
			if (!this._cm)
				return; // editor was destroyed (detach/unbind) while focused
			const v = this._cm.state.doc.toString();
			if (v !== this._committed) {
				this._committed = this._value = v;
				this.fire('onChange', { value: v });
			}
			this.fire('onBlur');
		});
		dom.addEventListener('focusin', () => this.fire('onFocus'));
		this._onMounted();
	}

	/**
	 * @internal Called at the end of `_mount`, only after the CodeMirror instance
	 * has been created and bound. Subclasses override this — not `_mount` — to wire
	 * post-mount behavior (a toolbar, an initial preview, …) without having to guard
	 * against `_mount`'s early returns.
	 */
	_onMounted(): void {
		// no-op in CE; a sanctioned post-mount extension point for subclasses.
	}

	/** @internal pushes a new document into the live editor, replacing its content. */
	_setDoc(value: string): void {
		const cm = this._cm!,
			cur = cm.state.doc.toString();
		if (cur !== value) {
			this._settingDoc = true; // this is a server/programmatic write, not a user edit
			try {
				cm.dispatch({ changes: { from: 0, to: cur.length, insert: value } });
			} finally {
				this._settingDoc = false;
			}
		}
		this._committed = value; // a programmatic set is authoritative — no blur commit needed
	}

	/** @internal high-frequency keystroke notification; AU queue may drop stale ones. */
	_onDocChanged(value: string): void {
		this._value = value;
		// A server/programmatic `_setDoc` also fires CodeMirror's doc-change listener;
		// never echo that back as if the user typed it. And, like InputWidget, only
		// notify the server when something listens (the binder saves on onChange).
		if (this._settingDoc || !this.isListen('onChanging'))
			return;
		this.fire('onChanging', { value: value }, { ignorable: true, rtags: { onChanging: 1 } });
	}

	/** @internal */
	_langExtension(): Extension {
		switch (this._language) {
		case 'xml': return xml();
		case 'java': return java();
		case 'javascript': return javascript();
		case 'css': return css();
		case 'json': return json();
		case 'html': return html();
		case 'sql': return sql();
		case 'markdown': return markdown();
		default: return []; // plain
		}
	}

	/** @internal editing is blocked when either read-only or disabled. */
	_editableExtension(): Extension {
		const locked = this._readonly || this._disabled;
		return [EditorView.editable.of(!locked), EditorState.readOnly.of(locked)];
	}

	/** @internal `indentWithTab` only in editing mode; otherwise Tab is unbound so it moves focus. */
	_tabExtension(): Extension {
		return this._tabIndent ? keymap.of([indentWithTab]) : [];
	}

	/**
	 * @internal Flips Tab between focus navigation (default) and 4-space indentation,
	 * driven by Escape. Default focus-nav means the editor is never a keyboard trap
	 * (WCAG 2.1.2); the user opts into indentation with Escape and leaves with Escape
	 * again. The change is announced through the `aria-live` status node.
	 */
	_toggleTabIndent(): void {
		this._tabIndent = !this._tabIndent;
		this._cm?.dispatch({ effects: this._tabComp.reconfigure(this._tabExtension()) });
		this._announceTabMode();
	}

	/** @internal updates the visually-hidden `aria-live` region so the current Tab behavior is announced. */
	_announceTabMode(): void {
		const status = this.$n('status');
		if (status)
			status.textContent = this._tabIndent
				? 'Tab key now indents. Press Escape to return to focus navigation.'
				: 'Tab key now moves focus. Press Escape to enable indentation.';
	}

	/** @internal */
	_lineNumbersExtension(): Extension {
		return this._lineNumbers ? lineNumbers() : [];
	}

	/** @internal renders a tab character as `_tabSize` columns wide. */
	_tabSizeExtension(): Extension {
		return EditorState.tabSize.of(this._tabSize);
	}

	/** @internal whether the editor uses its dark surface: explicit `theme="dark"`, else derived from a dark page theme. */
	_isDark(): boolean {
		return this._theme ? this._theme === 'dark' : !!zk.themeName?.includes('dark');
	}

	/**
	 * @internal Selects the syntax-highlight palette for the current surface, and
	 * in dark mode also tells CodeMirror it is dark so its built-in extensions
	 * (selection, matching-bracket…) pick their dark variants. Lives in the theme
	 * compartment so a live `setTheme` recolors tokens, not just the chrome. The
	 * surface chrome (background/gutter/cursor) stays in the component LESS
	 * (`.z-codeeditor-dark`); only the token palette is chosen here.
	 */
	_themeExtension(): Extension {
		return this._isDark()
			? [EditorView.theme({}, { dark: true }), syntaxHighlighting(darkHighlightStyle)]
			: syntaxHighlighting(defaultHighlightStyle);
	}

	/** @internal toggles the `z-codeeditor-dark` modifier so the LESS surface rules apply. */
	_applyDarkClass(): void {
		this._toggleClass('dark', this._isDark());
	}

	/** @internal toggles a `z-codeeditor-<name>` modifier class on the root, when bound. */
	_toggleClass(name: string, on: boolean): void {
		this.$n()?.classList.toggle(this.$s(name), on);
	}

	/** @internal toggles the `z-codeeditor-disabled` modifier (dim + non-interactive in LESS). */
	_applyDisabledClass(): void {
		this._toggleClass('disabled', this._disabled);
	}

	/** @internal reads the page nonce from a still-present nonced node for CM's StyleModule. */
	_cspNonce(): string | undefined {
		const n = document.querySelector('script[nonce]') ?? document.querySelector('style[nonce]');
		return (n as unknown as { nonce?: string })?.nonce || undefined;
	}
}
