/* Avatar.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:00:37 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A user avatar. Displays an image, or a fallback label (initials), or a
 * fallback icon, depending on which property is set.
 * @defaultValue {@link getZclass}: "z-avatar".
 */
@zk.WrapClass('zul.wgt.Avatar')
export class Avatar extends zul.LabelImageWidget {
	/** @internal */
	_shape = 'circle';
	/** @internal */
	_size = 'medium';
	/** @internal */
	_gap = 4;
	/**
	 * Transient client-only flag: set when the rendered `<img>` fails to load
	 * (404 / DNS / expired CDN). Drives the icon/label fallback in
	 * {@link domContent_} WITHOUT clearing `_image`, so the server-synced URL
	 * stays intact (getImage() keeps agreeing with the server) and a later
	 * {@link setImage} to a different URL retries the image. Cleared whenever
	 * the URL changes.
	 * @internal
	 */
	_imgError = false;

	/**
	 * @returns the shape of this avatar.
	 * @defaultValue `"circle"`.
	 */
	getShape(): string {
		return this._shape;
	}

	/**
	 * Sets the shape of this avatar.
	 * @param shape - either "circle" or "square".
	 */
	setShape(shape: string, opts?: Record<string, boolean>): this {
		// Mirror Java's null→default coercion so programmatic
		// setShape(undefined) doesn't emit a literal class
		// `z-avatar-undefined` that has no LESS rule.
		if (shape == null) shape = 'circle';
		const o = this._shape;
		this._shape = shape;
		if (o !== shape || opts?.force) {
			this.updateDomClass_();
		}
		return this;
	}

	/**
	 * @returns the size of this avatar.
	 * @defaultValue `"medium"`.
	 */
	getSize(): string {
		return this._size;
	}

	/**
	 * Sets the size of this avatar.
	 * @param size - "small", "medium" or "large".
	 */
	setSize(size: string, opts?: Record<string, boolean>): this {
		if (size == null) size = 'medium';
		const o = this._size;
		this._size = size;
		if (o !== size || opts?.force) {
			this.updateDomClass_();
		}
		return this;
	}

	/**
	 * @returns the horizontal padding (in pixels) around the rendered text.
	 * @defaultValue `4` (matches Ant Design Avatar's `gap`).
	 */
	getGap(): number {
		return this._gap;
	}

	/**
	 * Sets the horizontal padding (in pixels) around the rendered text. The value
	 * must be between 0 and 24 inclusive; an out-of-range value is clamped into
	 * that range.
	 * @param gap - the inset, in pixels, that the rendered initials keep from each
	 * side.
	 */
	setGap(gap: number, opts?: Record<string, boolean>): this {
		// Mirror Avatar.java#setGap (0 <= gap <= 24). Clamp + warn rather than
		// throw so a bad MVVM binding doesn't collapse the whole load() pass —
		// the same client-lenient pattern used by Avatargroup#setMaxCount and
		// the Carousel range setters.
		if (gap < 0 || gap > 24) {
			const clamped = Math.max(0, Math.min(24, gap));
			zk.error('Avatar: gap must be between 0 and 24 (px) — '
					+ gap + ' clamped to ' + clamped);
			gap = clamped;
		}
		const o = this._gap;
		this._gap = gap;
		if (o !== gap || opts?.force) {
			// rerender(-1) rebuilds this avatar's DOM IMMEDIATELY. The default
			// rerender() defers to a timer, so a synchronous re-apply below would
			// run against the about-to-be-discarded old node. After the immediate
			// rebuild, re-apply the parent Avatargroup's overflow accounting — the
			// mold doesn't emit the imperative data-ag-hidden marker, so without
			// this an avatar hidden behind "+N" pops back into the visible row
			// while still counted. Duck-typed to avoid an Avatar -> Avatargroup cycle.
			this.rerender(-1);
			const p = this.parent as unknown as { _applyOverflow?: () => void } | undefined;
			if (p && typeof p._applyOverflow === 'function') p._applyOverflow();
		}
		return this;
	}

	/**
	 * Sets the image URI. Overrides {@link zul.LabelImageWidget#setImage} to
	 * clear the transient load-failure flag whenever the URL changes, so a
	 * fresh / recovered URL is retried instead of staying on the fallback.
	 */
	override setImage(image: string, opts?: Record<string, boolean>): this {
		// A new URL deserves a fresh load attempt: clear the prior load-failure
		// flag so domContent_ renders the <img> again instead of staying on the
		// icon/label fallback left over from a broken URL.
		if (image !== this._image) this._imgError = false;
		return super.setImage(image, opts);
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-avatar`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-avatar' : this._zclass;
	}

	/** @internal */ _onImgError?: EventListener;

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// Runtime image-load fallback: domContent_ resolves image→icon→label
		// at render time, but once the <img> is in the DOM a 404 / DNS
		// failure / expired CDN just shows the browser's broken-image
		// glyph. Hook onerror so the icon or label fallback takes over
		// — preserves the documented image→icon→label chain at runtime
		// as well as render time.
		//
		// 'error' does NOT bubble, so listen in capture phase on root.
		// A per-img listener with {once:true} would lapse silently on the
		// second swap: LabelImageWidget.setImage mutates img.src directly
		// (no rerender / no re-bind_), so a freshly-broken URL after the
		// first failure wouldn't get a new listener.
		var root = this.$n(), self = this;
		if (root) {
			this._onImgError = function (e: Event): void {
				if (!self.desktop) return;
				if (!(e.target instanceof HTMLImageElement)) return;
				// Flag the failure (without touching _image) so the rerender
				// skips the <img> and falls through to icon/label. _image is
				// left intact so it stays consistent with the server and is
				// not re-rendered as a broken <img> on the next invalidate —
				// avoiding the per-round-trip flicker.
				if (self._imgError) return; // already on fallback; no re-loop
				self._imgError = true;
				// rerender(-1) rebuilds IMMEDIATELY. The default rerender()
				// defers to a timer, so the overflow re-apply below would run
				// against the about-to-be-discarded old node and the rebuilt one
				// would lose the marker (same trap as Avatar#setGap).
				self.rerender(-1);
				// If this avatar was hidden behind an Avatargroup's "+N"
				// overflow, the rerender recreated its DOM without the group's
				// data-ag-hidden marker — re-apply overflow so a broken-image
				// avatar doesn't pop back into view while still counted in +N.
				// Duck-typed to avoid an Avatar -> Avatargroup import cycle.
				var p = self.parent as unknown as { _applyOverflow?: () => void } | undefined;
				if (p && typeof p._applyOverflow === 'function') p._applyOverflow();
			};
			root.addEventListener('error', this._onImgError, true);
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var root = this.$n();
		if (root && this._onImgError)
			root.removeEventListener('error', this._onImgError, true);
		this._onImgError = undefined;
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._shape);
			sc += ' ' + this.$s(this._size);
		}
		return sc;
	}

	/** @internal */
	override domContent_(): string {
		var /*safe*/ img = this.domImage_();
		// Skip the <img> after a runtime load failure (see bind_'s onerror).
		// _image is kept intact for server consistency; _imgError is cleared by
		// setImage() when the URL changes, so a new/recovered URL retries.
		if (img && !this._imgError) return img;
		var /*safe*/ icon = this.domIcon_();
		if (icon) return icon;
		var label = this.getLabel();
		if (label) {
			// keep at most two code points for readable initials. Match with
			// the /u flag so a single iteration step covers a full code
			// point — astral-plane characters (emoji, CJK extensions like
			// 𝕏 U+1D54F) survive as one grapheme. String.substring(0,2)
			// would split the surrogate pair and emit a lone D8xx that the
			// browser renders as the U+FFFD replacement glyph.
			// `s` (dotAll) makes `.` match newlines too, so a label
			// starting with "\n" or "\r" still yields its first visible
			// initial instead of producing empty initials.
			var cps = label.match(/./gus);
			if (cps && cps.length > 2) label = cps.slice(0, 2).join('');
			// Coerce _gap to a finite number before interpolating into the inline
			// style — a hostile setGap("4;background-image:url(...)") otherwise
			// breaks out of the style declaration.
			var /*safe*/ pad = '',
				gap = Number(this._gap);
			// Skip inline padding when the value matches the LESS default
			// (Avatar.java DEFAULT_GAP). Keep these two in lockstep.
			if (Number.isFinite(gap) && gap !== 4)
				pad = ' style="padding:0 ' + gap + 'px"';
			return '<span class="' + this.$s('text') + '"' + pad + '>'
					+ zUtl.encodeXML(label) + '</span>';
		}
		return '';
	}
}
