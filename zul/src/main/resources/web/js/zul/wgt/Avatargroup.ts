/* Avatargroup.ts

        Purpose:

        Description:

        History:
                Wed May 13 13:11:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A group container for {@link Avatar} widgets.
 * Renders overlapping avatars; when `maxCount` is set, excess children are
 * hidden and a "+N" indicator is appended.
 * @defaultValue {@link getZclass}: "z-avatargroup".
 */
@zk.WrapClass('zul.wgt.Avatargroup')
export class Avatargroup extends zul.Widget {
	/** @internal */ _maxCount = 0;
	/** @internal */ _size?: string;
	/** @internal */ _shape?: string;
	/** @internal */ _overflowNode?: HTMLElement;

	/**
	 * Returns the maximum number of visible avatars. 0 means unlimited.
	 * @defaultValue `0`.
	 */
	getMaxCount(): number { return this._maxCount; }
	/**
	 * Sets the maximum number of visible avatars. Excess avatars are hidden and
	 * replaced with a "+N" overflow indicator; 0 means unlimited. A negative
	 * value is clamped to 0 (unlimited).
	 * @param maxCount - the maximum number of visible avatars, or 0 for unlimited.
	 */
	setMaxCount(maxCount: number, opts?: Record<string, boolean>): this {
		// Mirror Avatargroup.java#setMaxCount, which throws on a negative value.
		// A negative slips past _applyOverflow's `if (!max) return` unlimited
		// guard and makes every `idx >= max` test true, hiding ALL avatars
		// behind a "+N" badge. Clamp to 0 (unlimited) + warn rather than throw
		// so a bad MVVM binding doesn't collapse the whole load() pass.
		if (maxCount < 0) {
			zk.error('Avatargroup: maxCount cannot be negative — '
					+ maxCount + ' clamped to 0 (unlimited)');
			maxCount = 0;
		}
		const o = this._maxCount;
		this._maxCount = maxCount;
		if (o !== maxCount || opts?.force) this._applyOverflow();
		return this;
	}

	/**
	 * Returns the size override applied to all child avatars, or undefined if not
	 * overriding.
	 * @defaultValue `undefined`.
	 */
	getSize(): string | undefined { return this._size; }
	/**
	 * Sets a uniform size for all child avatars. Applies via a CSS cascade class
	 * on the group element; the individual avatar's size is not changed.
	 * @param size - "small", "medium" or "large".
	 */
	setSize(size: string, opts?: Record<string, boolean>): this {
		const o = this._size;
		this._size = size;
		if (o !== size || opts?.force) {
			this.updateDomClass_();
			// The "+N" overflow indicator carries the cascaded size class
			// applied at creation time — rebuild so its size matches the
			// new cascade.
			this._applyOverflow();
		}
		return this;
	}

	/**
	 * Returns the shape override applied to all child avatars, or undefined if not
	 * overriding.
	 * @defaultValue `undefined`.
	 */
	getShape(): string | undefined { return this._shape; }
	/**
	 * Sets a uniform shape for all child avatars. Applies via a CSS cascade class
	 * on the group element.
	 * @param shape - either "circle" or "square".
	 */
	setShape(shape: string, opts?: Record<string, boolean>): this {
		const o = this._shape;
		this._shape = shape;
		if (o !== shape || opts?.force) {
			this.updateDomClass_();
			// Same reasoning as setSize — rebuild overflow so it inherits
			// the new shape cascade.
			this._applyOverflow();
		}
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-avatargroup`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-avatargroup' : this._zclass;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			if (this._size) sc += ' ' + this.$s(this._size);
			if (this._shape) sc += ' ' + this.$s(this._shape);
		}
		return sc;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._applyOverflow();
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._removeOverflow();
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// Re-run overflow accounting so the `+N` indicator and the
		// data-ag-hidden flags reflect the new child count. Without this,
		// a server-side appendChild after bind leaves the indicator stale
		// (showing an old `+M` over a different visible set).
		if (this.desktop) this._applyOverflow();
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop) this._applyOverflow();
	}

	/** @internal */
	_applyOverflow(): void {
		var groupNode = this.$n();
		if (!groupNode) return;

		// Always clean up previous overflow state first
		this._removeOverflow();

		var max = this._maxCount;
		if (!max) return;

		// Only count children that are actually rendered. A server-side
		// appendChild that fires onChildAdded_ before its DOM is in place
		// would otherwise consume a maxCount slot with an unrendered ghost,
		// pushing a real avatar into the hidden bucket and stranding it
		// behind a "+N" indicator that doesn't match the visible count.
		var idx = 0, hidden = 0;
		for (var w: zk.Widget | undefined = this.firstChild; w; w = w.nextSibling) {
			var n = w.$n();
			if (!n) continue;
			if (idx >= max) {
				// Hide via the data attribute (CSS rule in avatargroup.less)
				// rather than inline style, so an author's inline `display`
				// survives an overflow re-accounting pass untouched.
				n.setAttribute('data-ag-hidden', 'true');
				hidden++;
			}
			idx++;
		}

		if (hidden > 0) {
			// Inherit size/shape from group; fall back to medium/circle
			var ov = document.createElement('span'),
				sizeClass = 'z-avatar-' + (this._size || 'medium'),
				shapeClass = 'z-avatar-' + (this._shape || 'circle');
			ov.className = 'z-avatar ' + sizeClass + ' ' + shapeClass
					+ ' ' + this.$s('overflow');
			ov.textContent = '+' + hidden;
			// role="img" + an "N more" aria-label for this overflow indicator
			// are layered on by the za11y add-on (EE).
			groupNode.appendChild(ov);
			this._overflowNode = ov;
		}
	}

	/** @internal */
	_removeOverflow(): void {
		var groupNode = this.$n();
		if (groupNode) {
			// Scope to direct children: avatars render as immediate children of
			// the group root (avatargroup.js redraws them into it), and the
			// marker is ours alone — :scope > avoids ever clearing a marker on a
			// deeper, unrelated node should the subtree gain nesting later.
			var hidden = groupNode.querySelectorAll(':scope > [data-ag-hidden]');
			for (var i = 0; i < hidden.length; i++) {
				var n = hidden[i] as HTMLElement;
				n.removeAttribute('data-ag-hidden');
			}
		}
		if (this._overflowNode) {
			this._overflowNode.remove();
			this._overflowNode = undefined;
		}
	}
}
