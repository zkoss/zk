/* Decimalbox.ts

	Purpose:

	Description:

	History:
		Fri June 11 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding BigDecimal.
 * <p>Default {@link #getZclass}: z-decimalbox.
 */
@zk.WrapClass('zul.inp.Decimalbox')
export class Decimalbox extends zul.inp.NumberInputWidget<zk.BigDecimal> {
	_scale?: number;

	// zk.def
	setScale(v: number): this {
		this._scale = v;
		return this;
	}

	/** Returns the precision scale.
	 * @return int
	 */
	getScale(): number | undefined {
		return this._scale;
	}

	override coerceFromString_(value: string | undefined): zul.inp.CoerceFromStringResult | zk.BigDecimal | undefined {
		if (!value) return undefined;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			val = new zk.BigDecimal(info.raw),
			sval = val.$toString();
		if (info.raw != sval && info.raw != '-' + sval) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};

		if (this._rounding == 7 && (this._errmsg/*server has to clean up*/
			|| zk.fmt.Number.isRoundingRequired(value, this.getFormat()!, this._localizedSymbols)))
			return {server: true};

		if (info.divscale) val.setPrecision(val.getPrecision() + info.divscale);
		if (this._scale! > 0) //bug #3089502: setScale in decimalbox not working
			val = zk.fmt.Number.setScale(val, this._scale!, this._rounding!);
		return val;
	}

	override coerceToString_(value?: zk.BigDecimal | string): string {
		var fmt = this._format;
		return value != null ? typeof value == 'string' ? value :
			fmt ? zk.fmt.Number.format(fmt, value.$toString(), this._rounding!, this._localizedSymbols)
			: value.$toLocaleString() : '';
	}

	override marshall_(val: zk.BigDecimal | undefined): string | undefined {
		return val ? val.$toString() : val;
	}

	override unmarshall_(val: string | number): zk.BigDecimal | '' | 0 {
		return val ? new zk.BigDecimal(val) : val as '' | 0;
	}

	override getAllowedKeys_(): string {
		var symbols = this._localizedSymbols;
		return super.getAllowedKeys_()
			+ (symbols ? symbols : zk).DECIMAL; //not support scientific expression
	}
}