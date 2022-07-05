/* Longbox.ts

	Purpose:

	Description:

	History:
		Sun Mar 29 20:43:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-longbox.
 */
@zk.WrapClass('zul.inp.Longbox')
export class Longbox extends zul.inp.NumberInputWidget<zk.Long> {
	//bug #2997037, cannot enter large long integer into longbox
	protected override coerceFromString_(value: string | null | undefined): zul.inp.CoerceFromStringResult | zk.Long | null {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			val = new zk.Long(info.raw),
			sval = val.$toString();
		if (info.raw != sval && info.raw != '-' + sval) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (info.divscale)
			val.scale(-info.divscale);
		if (this._isOutRange(val.$toString()))
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE + '(−9223372036854775808 - 9223372036854775807)')};
		return val;
	}

	protected override coerceToString_(value?: zk.Long | string | null): string {
		var fmt = this._format;
		return value != null ? typeof value == 'string' ? value :
			fmt ? zk.fmt.Number.format(fmt, value.$toString(), this._rounding!, this._localizedSymbols)
				 : value.$toLocaleString() : '';
	}

	private _isOutRange(val: string): boolean {
		var negative = val.charAt(0) == '-';
		if (negative)
			val = val.substring(1);
		if (val.length > 19)
			return true;
		if (val.length < 19)
			return false;
		var maxval = negative ? '9223372036854775808' : '9223372036854775807';
		for (var j = 0; j < 19; ++j) {
			if (val.charAt(j) > maxval.charAt(j))
				return true;
			if (val.charAt(j) < maxval.charAt(j))
				return false;
		}
		return false;
	}

	protected override marshall_(val: zk.Long | undefined): string | undefined {
		return val ? val.$toString() : val;
	}

	protected override unmarshall_(val: string | number): zk.Long | '' | 0 {
		return val ? new zk.Long(val) : val as '' | 0;
	}
}