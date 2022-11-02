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
	/** @internal */
	_span = 1;
	/** @internal */
	_align?: string;
	/** @internal */
	_valign?: string;

	/**
	 * @returns number of columns to span this footer.
	 * @defaultValue `1`.
	 */
	getSpan(): number {
		return this._span;
	}

	/**
	 * Sets the number of columns to span this footer.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
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

	/**
	 * @returns the horizontal alignment of this footer.
	 * @defaultValue `null` (system default: left unless CSS specified).
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/**
	 * Sets the horizontal alignment of this footer.
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

	/**
	 * @returns the vertical alignment of this footer.
	 * @defaultValue `null` (system default: top).
	 */
	getValign(): string | undefined {
		return this._valign;
	}

	/**
	 * Sets the vertical alignment of this footer.
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
	 * @returns the mesh widget that this belongs to.
	 */
	getMeshWidget(): zul.mesh.MeshWidget | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	/**
	 * @returns the column that is in the same column as
	 * this footer, or null if not available.
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
	/** @internal */
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

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '');
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}
}