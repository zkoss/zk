/* Chip.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:04:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A display-only chip.
 * @defaultValue {@link getZclass}: "z-chip".
 */
@zk.WrapClass('zul.wgt.Chip')
export class Chip extends zul.LabelImageWidget {
	/** @internal */ _severity = 'info';
	/** @internal */ _size = 'medium';
	/** @internal */ _rounded = false;
	/** @internal */ _closable = false;
	/** @internal */ _disabled = false;

	/**
	 * Returns the severity (color theme).
	 * @defaultValue `info`.
	 */
	getSeverity(): string { return this._severity; }
	/**
	 * Sets the severity (color theme). null restores the default (`info`).
	 * @param severity - one of "info", "success", "warning", "danger" or
	 * "secondary".
	 */
	setSeverity(severity: string, opts?: Record<string, boolean>): this {
		// Mirror Avatar/Java null→default coercion: a null/undefined from a
		// stateless MVVM binding would otherwise reach $s() and emit a dead
		// `z-chip-null` class with no LESS rule.
		if (severity == null) severity = 'info';
		const o = this._severity;
		this._severity = severity;
		if (o !== severity || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * Returns the size of this chip. "medium" maps to the default row height;
	 * "small" reduces it for dense layouts.
	 * @defaultValue `medium`.
	 */
	getSize(): string { return this._size; }
	/**
	 * Sets the size of this chip. null restores the default (`medium`).
	 * @param size - "small" or "medium".
	 */
	setSize(size: string, opts?: Record<string, boolean>): this {
		if (size == null) size = 'medium';
		const o = this._size;
		this._size = size;
		if (o !== size || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * Returns whether this chip is non-interactive. Disabled chips ignore close
	 * clicks and render at reduced opacity.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean { return this._disabled; }
	/**
	 * Sets whether this chip is non-interactive. Disabled chips ignore close
	 * clicks and render at reduced opacity.
	 * @param disabled - whether the chip is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;
		if (o !== disabled || opts?.force) {
			this.updateDomClass_();
			// The mold renders <button disabled> at HTML time based on
			// _disabled, but a runtime setDisabled(true) only flips the
			// CSS class — leaving the close button still keyboard-/click-
			// activatable, which surprises a screen-reader user navigating
			// a "disabled" chip. Sync the native attribute too.
			var closeBtn = this.$n('close');
			if (closeBtn) {
				if (disabled) closeBtn.setAttribute('disabled', 'disabled');
				else closeBtn.removeAttribute('disabled');
			}
		}
		return this;
	}

	/**
	 * Returns whether this chip is rounded (pill-shaped).
	 * @defaultValue `false`.
	 */
	isRounded(): boolean { return this._rounded; }
	/**
	 * Sets whether this chip is rounded (pill-shaped).
	 * @param rounded - whether the chip is rounded.
	 */
	setRounded(rounded: boolean, opts?: Record<string, boolean>): this {
		const o = this._rounded;
		this._rounded = rounded;
		if (o !== rounded || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * Returns whether this chip displays a close button.
	 * @defaultValue `false`.
	 */
	isClosable(): boolean { return this._closable; }
	/**
	 * Sets whether this chip displays a close button.
	 * @param closable - whether the chip displays a close button.
	 */
	setClosable(closable: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = closable;
		if (o !== closable || opts?.force) this.rerender();
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-chip`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-chip' : this._zclass;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._severity);
			if (this._size !== 'medium') sc += ' ' + this.$s(this._size);
			if (this._rounded) sc += ' ' + this.$s('rounded');
			if (this._closable) sc += ' ' + this.$s('closable');
			if (this._disabled) sc += ' ' + this.$s('disabled');
		}
		return sc;
	}

	/** @internal */
	override doClick_(evt: zk.Event): void {
		if (this._disabled) {
			evt.stop();
			return;
		}
		if (this._closable) {
			var closeNode = this.$n('close'),
				target = evt.domTarget as HTMLElement | undefined;
			// Match the close button itself OR anything inside it (the
			// icon-font glyph child <i> receives the click in real browsers).
			if (closeNode && target && (target === closeNode || closeNode.contains(target))) {
				this._fireClose();
				evt.stop();
				return;
			}
		}
		super.doClick_(evt);
	}

	/** @internal */
	_fireClose(): void {
		// Let the server decide visibility: the default onClose handler
		// detaches the chip (the client gets an invalidate response), but a
		// listener may call event.stopPropagation() to suppress that detach
		// — see Chip.java onClose(). Optimistically hiding here would leave
		// the chip invisible but still attached in the suppressed case.
		this.fire('onClose', {}, {toServer: true});
	}
}
