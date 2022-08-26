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
@zk.WrapClass('zul.mesh.FooterWidget')
export abstract class FooterWidget extends zul.LabelImageWidget<HTMLTableCellElement> {
	override parent!: zul.mesh.HeadWidget | undefined;
	_span = 1;
	_align?: string;
	_valign?: string;

	/** Returns number of columns to span this footer.
	 * Default: 1.
	 * @return int
	 */
	getSpan(): number {
		return this._span;
	}

	/** Sets the number of columns to span this footer.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 * @param int span
	 */
	setSpan(span: number, opts?: Record<string, boolean>): this {
		const o = this._span;
		this._span = span;

		if (o !== span || opts?.force) {
			var n = this.$n();
			if (n) n.colSpan = span;
		}

		return this;
	}

	/** Returns the horizontal alignment of this footer.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @return String
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the horizontal alignment of this footer.
	 * @param String align
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.$n();
			if (n) n.align = align;
		}

		return this;
	}

	/** Returns the vertical alignment of this footer.
	 * <p>Default: null (system default: top).
	 * @return String
	 */
	getValign(): string | undefined {
		return this._valign;
	}

	/** Sets the vertical alignment of this footer.
	 * @param String valign
	 */
	setValign(valign: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = valign;

		if (o !== valign || opts?.force) {
			var n = this.$n();
			if (n) n.vAlign = valign;
		}

		return this;
	}

	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 */
	getMeshWidget(): zul.mesh.MeshWidget | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @return zul.mesh.HeaderWidget
	 */
	getHeaderWidget(): zul.mesh.HeaderWidget | undefined {
		var meshWidget = this.getMeshWidget();
		if (meshWidget) {
			var cs = meshWidget.getHeadWidget();
			if (cs)
				return cs.getChildAt<zul.mesh.HeaderWidget>(this.getChildIndex());
		}
		return undefined;
	}

	//super
	override domStyle_(no?: zk.DomStyleOptions): string {
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

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '');
	}

	override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}
}