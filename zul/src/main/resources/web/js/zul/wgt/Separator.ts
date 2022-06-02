/* Separator.ts

	Purpose:

	Description:

	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

let _shallFixPercent: (wgt: zul.wgt.Separator) => boolean =
	zk.gecko
		? (wgt) => {
			let s = wgt._spacing;
			return s != null && s.endsWith('%');
		}
		: zk.$void;

/**
 * A separator.
 *  <p>Default {@link #getZclass} is "z-separator".
 */
 export class Separator extends zul.Widget {
	private _orient = 'horizontal';
	private _bar = false;
	public _spacing?: string;

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	public getOrient(): string {
		return this._orient;
	}
	/** Sets the orient.
	 * @param String orient either "horizontal" or "vertical".
	 */
	public setOrient(v: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = v;
		if (o !== v || (opts && opts.force)) {
			this.updateDomClass_();
		}
		return this;
	}
	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: false
	 * @return boolean
	 */
	public isBar(): boolean {
		return this._bar;
	}
	/** Sets  whether to display a visual bar as the separator.
	 * @param boolean bar
	 */
	public setBar(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._bar;
		this._bar = v;
		if (o !== v || (opts && opts.force)) {
			this.updateDomClass_();
		}
		return this;
	}
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 * @return String
	 */
	public getSpacing(): string | undefined {
		return this._spacing;
	}
	/** Sets the spacing.
	 * @param String spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	public setSpacing(v: string, opts?: Record<string, boolean>): this {
		const o = this._spacing;
		this._spacing = v;
		if (o !== v || (opts && opts.force)) {
			this.updateDomStyle_();
		}
		return this;
	}

	/** Returns whether it is a vertical separator.
	 * @return boolean
	 */
	public isVertical(): boolean {
		return this._orient == 'vertical';
	}

	//super//
	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
	}
	public override getZclass(): string {
		return 'z-separator';
	}
	protected override domClass_(no?: Partial<zk.DomClassOptions>): string {
		var sc = super.domClass_(no),
			bar = this.isBar();
		if (!no || !no.zclass) {
			sc += ' ' + this.$s((this.isVertical() ? 'vertical' + (bar ? '-bar' : '') :
				'horizontal' + (bar ? '-bar' : '')));
		}
		return sc;
	}
	protected override domStyle_(no?: Partial<zk.DomStyleOptions>): string {
		var s = super.domStyle_(no);
		if (!_shallFixPercent(this))
			return s;

		//_spacing contains %
		var space = this._spacing!,
			v = zk.parseInt(space.substring(0, space.length - 1).trim());
		if (v <= 0) return s;
		let percent = v >= 2 ? (v / 2) + '%' : '1%';

		return 'margin:' + (this.isVertical() ? '0 ' + percent : percent + ' 0')
			+ ';' + s;
	}
	public override getWidth(): string | null | undefined {
		let wd = super.getWidth();
		return !this.isVertical() || (wd != null && wd.length > 0)
			|| _shallFixPercent(this) ? wd : this._spacing;

	}
	public override getHeight(): string | null | undefined {
		let hgh = super.getHeight();
		return this.isVertical() || (hgh != null && hgh.length > 0)
			|| _shallFixPercent(this) ? hgh : this._spacing;
	}
}
zul.wgt.Separator = Separator;