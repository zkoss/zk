/* FooterWidget.ts

	Purpose:

	Description:

	History:
		Tue Jul 27 09:24:17 TST 2010, Created by jimmy

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a footer.
 */
export abstract class FooterWidget extends zul.LabelImageWidget {
	public override parent!: zul.mesh.HeadWidget | null;
	private _span = 1;
	private _align?: string;
	private _valign?: string;

	/** Returns number of columns to span this footer.
	 * Default: 1.
	 * @return int
	 */
	public getSpan(): number {
		return this._span;
	}

	/** Sets the number of columns to span this footer.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 * @param int span
	 */
	public setSpan(v: number, opts?: Record<string, boolean>): this {
		const o = this._span;
		this._span = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n() as HTMLTableCellElement | null | undefined;
			if (n) n.colSpan = v;
		}

		return this;
	}

	/** Returns the horizontal alignment of this footer.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @return String
	 */
	public getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the horizontal alignment of this footer.
	 * @param String align
	 */
	public setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n() as HTMLTableCellElement | null | undefined;
			if (n) n.align = v;
		}

		return this;
	}

	/** Returns the vertical alignment of this footer.
	 * <p>Default: null (system default: top).
	 * @return String
	 */
	public getValign(): string | undefined {
		return this._valign;
	}

	/** Sets the vertical alignment of this footer.
	 * @param String valign
	 */
	public setValign(v: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n() as HTMLTableCellElement | null | undefined;
			if (n) n.vAlign = v;
		}

		return this;
	}

	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 */
	public getMeshWidget(): zul.mesh.MeshWidget | null | undefined {
		return this.parent ? this.parent.parent : null;
	}

	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @return zul.mesh.HeaderWidget
	 */
	public getHeaderWidget(): zul.mesh.HeaderWidget | null | undefined {
		var meshWidget = this.getMeshWidget();
		if (meshWidget) {
			var cs = meshWidget.getHeadWidget();
			if (cs)
				return cs.getChildAt(this.getChildIndex()) as zul.mesh.HeaderWidget | undefined;
		}
		return null;
	}

	//super
	protected override domStyle_(no?: zk.DomStyleOptions): string {
		var style = '',
			header = this.getHeaderWidget();
		if (this._align)
			style += 'text-align:' + this._align + ';';
		else if (header && header._align)
			style += 'text-align:' + header._align + ';';
		if (this._valign)
			style += 'vertical-align:' + this._align + ';';
		else if (header && header._valign)
			style += 'vertical-align:' + header._valign + ';';

		return super.domStyle_(no) + style;
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '');
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}
}
zul.mesh.FooterWidget = zk.regClass(FooterWidget);
