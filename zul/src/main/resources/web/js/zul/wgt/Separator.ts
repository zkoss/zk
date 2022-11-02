/* Separator.ts

	Purpose:

	Description:

	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function _shallFixPercent(wgt: zul.wgt.Separator): boolean {
	return !!(zk.gecko && wgt._spacing?.endsWith('%'));
}

/**
 * A separator.
 * @defaultValue {@link getZclass} is "z-separator".
 */
@zk.WrapClass('zul.wgt.Separator')
export class Separator extends zul.Widget {
	/** @internal */
	_orient = 'horizontal';
	/** @internal */
	_bar = false;
	/** @internal */
	_spacing?: string;

	/**
	 * @returns the orient.
	 * @defaultValue `"horizontal"`.
	 */
	getOrient(): string {
		return this._orient;
	}
	/**
	 * Sets the orient.
	 * @param orient - either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;
		if (o !== orient || opts?.force) {
			this.updateDomClass_();
		}
		return this;
	}
	/**
	 * @returns whether to display a visual bar as the separator.
	 * @defaultValue `false`
	 */
	isBar(): boolean {
		return this._bar;
	}
	/**
	 * Sets  whether to display a visual bar as the separator.
	 */
	setBar(bar: boolean, opts?: Record<string, boolean>): this {
		const o = this._bar;
		this._bar = bar;
		if (o !== bar || opts?.force) {
			this.updateDomClass_();
		}
		return this;
	}
	/**
	 * @returns the spacing.
	 * @defaultValue `null` (depending on CSS).
	 */
	getSpacing(): string | undefined {
		return this._spacing;
	}
	/**
	 * Sets the spacing.
	 * @param spacing - the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	setSpacing(spacing: string, opts?: Record<string, boolean>): this {
		const o = this._spacing;
		this._spacing = spacing;
		if (o !== spacing || opts?.force) {
			this.updateDomStyle_();
		}
		return this;
	}

	/**
	 * @returns whether it is a vertical separator.
	 */
	isVertical(): boolean {
		return this._orient == 'vertical';
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
	}
	override getZclass(): string {
		return 'z-separator';
	}
	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no),
			bar = this.isBar();
		if (!no || !no.zclass) {
			sc += ' ' + this.$s((this.isVertical() ? 'vertical' + (bar ? '-bar' : '') :
				'horizontal' + (bar ? '-bar' : '')));
		}
		return sc;
	}
	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		var s = super.domStyle_(no);
		if (!_shallFixPercent(this))
			return s;

		//_spacing contains %
		var space = this._spacing!,
			v = zk.parseInt(space.substring(0, space.length - 1).trim());
		if (v <= 0) return s;
		const percent = `${v >= 2 ? v / 2 : 1}%`;

		return 'margin:' + (this.isVertical() ? '0 ' + percent : percent + ' 0')
			+ ';' + s;
	}
	override getWidth(): string | undefined {
		const wd = super.getWidth();
		return !this.isVertical() || (wd != null && wd.length > 0)
			|| _shallFixPercent(this) ? wd : this._spacing;

	}
	override getHeight(): string | undefined {
		const hgh = super.getHeight();
		return this.isVertical() || (hgh != null && hgh.length > 0)
			|| _shallFixPercent(this) ? hgh : this._spacing;
	}
}