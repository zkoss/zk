/* Badge.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:03:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A badge (count or status indicator). May be used standalone or wrap a child.
 * @defaultValue {@link getZclass}: "z-badge".
 */
@zk.WrapClass('zul.wgt.Badge')
export class Badge extends zul.Widget {
	/** @internal */
	_value?: string;
	/** @internal */
	_count = 0;
	/** @internal */
	_max = 99;
	/** @internal */
	_showZero = false;
	/** @internal */
	_dot = false;
	/** @internal */
	_severity = 'info';
	/** @internal */
	_placement = 'top_right';

	/**
	 * Returns the text value of this badge. When non-null, it takes precedence
	 * over the count.
	 * @defaultValue `null`.
	 */
	getValue(): string | undefined { return this._value; }
	/**
	 * Sets the text value of this badge. When non-null, it takes precedence over
	 * the count.
	 * @param value - the text value; an empty string clears it (normalized to
	 * undefined).
	 */
	setValue(value: string, opts?: Record<string, boolean>): this {
		// Mirror Badge.java#setValue: empty string normalizes to undefined
		// so getValue() and _shouldRenderIndicator() return consistent state
		// on both sides of the wire.
		const v: string | undefined = value === '' ? undefined : value,
			o = this._value;
		this._value = v;
		if (o !== v || opts?.force) this._updateIndicator();
		return this;
	}

	/**
	 * Returns the count of this badge.
	 * @defaultValue `0`.
	 */
	getCount(): number { return this._count; }
	/**
	 * Sets the count of this badge. A badge is a counter, not a signed value: a
	 * negative count is clamped to 0.
	 * @param count - the count; must be non-negative.
	 */
	setCount(count: number, opts?: Record<string, boolean>): this {
		// Mirror Badge.java#setCount: a counter badge has no signed value.
		// Clamp + warn rather than throw — MVVM load() would otherwise
		// collapse the entire pass on a single bad binding.
		if (count < 0) {
			zk.error('Badge: count cannot be negative — ' + count + ' clamped to 0');
			count = 0;
		}
		const o = this._count;
		this._count = count;
		if (o !== count || opts?.force) this._updateIndicator();
		return this;
	}

	/**
	 * Returns the maximum count to display before showing an overflow
	 * indicator (e.g. "99+").
	 * @defaultValue `99`.
	 */
	getMax(): number { return this._max; }
	/**
	 * Sets the maximum count to display before showing an overflow indicator
	 * (e.g. "99+"). The cap must be at least 1 for the overflow indicator to
	 * make sense: a value below 1 is clamped to 1.
	 * @param max - the maximum count; must be at least 1.
	 */
	setMax(max: number, opts?: Record<string, boolean>): this {
		// Mirror Badge.java#setMax: cap < 1 makes the "{max}+" overflow
		// indicator nonsensical (e.g., "0+" on count=1).
		if (max < 1) {
			zk.error('Badge: max must be >= 1 — ' + max + ' clamped to 1');
			max = 1;
		}
		const o = this._max;
		this._max = max;
		if (o !== max || opts?.force) this._updateIndicator();
		return this;
	}

	/**
	 * Returns whether the badge is displayed when the count is zero.
	 * @defaultValue `false`.
	 */
	isShowZero(): boolean { return this._showZero; }
	/**
	 * Sets whether the badge is displayed when the count is zero.
	 * @param showZero - whether the badge shows when the count is zero.
	 */
	setShowZero(showZero: boolean, opts?: Record<string, boolean>): this {
		const o = this._showZero;
		this._showZero = showZero;
		if (o !== showZero || opts?.force) this._updateIndicator();
		return this;
	}

	/**
	 * Returns whether the badge renders as a small red dot, ignoring value and
	 * count text.
	 * @defaultValue `false`.
	 */
	isDot(): boolean { return this._dot; }
	/**
	 * Sets whether the badge renders as a small red dot, ignoring value and count
	 * text.
	 * @param dot - whether to render as a dot instead of value/count text.
	 */
	setDot(dot: boolean, opts?: Record<string, boolean>): this {
		const o = this._dot;
		this._dot = dot;
		if (o !== dot || opts?.force) {
			this.updateDomClass_();
			this._updateIndicator();
		}
		return this;
	}

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
		if (severity == null) severity = 'info';
		const o = this._severity;
		this._severity = severity;
		if (o !== severity || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * Returns the placement of the indicator relative to the wrapped child. Only
	 * effective in wrap mode (when the badge has children).
	 * @defaultValue `top_right`.
	 */
	getPlacement(): string { return this._placement; }
	/**
	 * Sets the placement of the indicator relative to the wrapped child.
	 * @param placement - one of "top_right", "top_left", "bottom_right" or
	 * "bottom_left".
	 */
	setPlacement(placement: string, opts?: Record<string, boolean>): this {
		// Mirror Java's null→default coercion (Badge.setPlacement) and setSeverity
		// above: a null push would otherwise drop the wrap-mode placement class.
		if (placement == null) placement = 'top_right';
		const o = this._placement;
		this._placement = placement;
		if (o !== placement || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-badge`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-badge' : this._zclass;
	}

	/**
	 * @internal Update the indicator span in place rather than rebuilding the
	 * widget. Numeric setters can fire many times per second (chat counters,
	 * notification badges) — a full rerender would tear down any wrapped child
	 * widget's DOM, restart in-flight image loads and lose focus inside the
	 * wrap subtree.
	 */
	_updateIndicator(): void {
		if (!this.desktop) return;
		var n = this.$n();
		if (!n) return;
		// Find the indicator span by scanning the badge root's direct
		// children for the indicator class. `lastElementChild` is unsafe:
		// a server-driven appendChild of a wrap-mode child after bind lands
		// AFTER the indicator span (the mold renders children first, the
		// indicator last; subsequent zk.Widget.insertChildHTML_ appends to
		// the parent's end). Trusting lastElementChild then creates a
		// SECOND indicator on the next setCount.
		var indicatorCls = this.$s('indicator'),
			indicator: HTMLElement | undefined;
		for (var c = n.firstElementChild; c; c = c.nextElementSibling) {
			if ((c as HTMLElement).classList.contains(indicatorCls)) {
				indicator = c as HTMLElement;
				break;
			}
		}
		var should = this._shouldRenderIndicator();
		if (should) {
			if (!indicator) {
				indicator = document.createElement('span');
				indicator.className = indicatorCls;
				n.appendChild(indicator);
			}
			// role="status" + contextual aria-label are layered on by the
			// za11y add-on (EE); CE updates the visible indicator text only.
			indicator.textContent = this._indicatorText();
		} else if (indicator) {
			indicator.remove();
		}
	}

	/** @internal Whether the indicator should be rendered at all. */
	_shouldRenderIndicator(): boolean {
		if (this._dot) return true;
		if (this._value != null && this._value !== '') return true;
		return this._count !== 0 || this._showZero;
	}

	/** @internal Display text inside the indicator. */
	_indicatorText(): string {
		if (this._dot) return '';
		if (this._value != null) return this._value;
		if (this._count > this._max) return this._max + '+';
		return '' + this._count;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._severity);
			if (this.firstChild)
				sc += ' ' + this.$s(this._placement);
			else
				sc += ' ' + this.$s('standalone');
			if (this._dot)
				sc += ' ' + this.$s('dot');
		}
		return sc;
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// Switching between standalone and wrap mode is class-driven by
		// `this.firstChild` at render time. A server-side appendChild after
		// bind moves the badge into wrap mode but never recomputes the
		// class — refresh it here so the indicator anchors to the placement
		// corner instead of the standalone slot.
		if (this.desktop) this.updateDomClass_();
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop) this.updateDomClass_();
	}
}
