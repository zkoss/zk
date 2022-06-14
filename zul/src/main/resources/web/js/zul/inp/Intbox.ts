/* Intbox.ts

	Purpose:

	Description:

	History:
		Fri Jan 16 12:33:22     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-intbox.
 *
 */
export class Intbox extends zul.inp.NumberInputWidget {
	/** Returns the value in int. If null, zero is returned.
	 * @return int
	 */
	public intValue(): string | undefined { // FIXME: return type mismatch with comment
		return super.getValue();
	}

	protected override coerceFromString_(value: string | null | undefined): {error: string} | number | null {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			val = parseInt(info.raw, 10),
			sval;
		if (info.raw.length < 17)
			sval = val.toString();
		else
			sval = new zk.BigDecimal(info.raw).$toString(); // Parse raw input by big decimal to avoid scientific notation

		// B65-ZK-1907: Should compare raw input string instead of parsed number(may contain scientific notation)
		if (isNaN(val) || (info.raw != sval && info.raw != '-' + sval))
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (val > 2147483647 || val < -2147483648)
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE + '(−2147483648 - 2147483647)')};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	}

	protected override coerceToString_(value: unknown): string {
		var fmt = this._format;
		return fmt ? zk.fmt.Number.format(fmt, value as string, this._rounding!, this._localizedSymbols)
					: value != null ? '' + value : '';
	}
}
zul.inp.Intbox = zk.regClass(Intbox);