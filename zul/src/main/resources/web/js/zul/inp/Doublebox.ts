/* Doublebox.ts

	Purpose:

	Description:

	History:
		Fri Jan 16 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding an float point value (double).
 * <p>Default {@link #getZclass}: z-doublebox.
 */
export class Doublebox extends zul.inp.NumberInputWidget {
	protected override coerceFromString_(value: string | null | undefined): {error?: string; server?: boolean} | number | null {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			raw = info.raw,
			val = parseFloat(raw),
			valstr = '' + val,
			valind = valstr.indexOf('.'),
			rawind = raw.indexOf('.');

		if (isNaN(val) || valstr.indexOf('e') < 0) {
			if (rawind == 0) {
				raw = '0' + raw;
				++rawind;
			}

			if (rawind >= 0 && raw.substring(raw.substring(rawind + 1) as unknown as number) && valind < 0) {
				valind = valstr.length;
				valstr += '.';
			}

			var len = raw.length,
				vallen = valstr.length;

			//pre zeros
			if (valind >= 0 && valind < rawind) {
				vallen -= valind;
				len -= rawind;
				for (var zerolen = rawind - valind; zerolen-- > 0;)
					valstr = '0' + valstr;
			}

			//post zeros
			if (vallen < len) {
				for (var zerolen = len - vallen; zerolen-- > 0;)
					valstr += '0';
			}

			if (isNaN(val) || (raw != valstr && raw != '-' + valstr && raw.indexOf('e') < 0)) { //1e2: assumes OK
				if (!isNaN(val) && raw != valstr) //Bug ZK-1218: show Illegal value instead if input is number but too long
					return {error: zk.fmt.Text.format(msgzul.ILLEGAL_VALUE)};
				return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
			}
		}

		if (this._rounding == 7 && (this._errmsg/*server has to clean up*/
			|| zk.fmt.Number.isRoundingRequired(value, this.getFormat()!, this._localizedSymbols)))
					return {server: true};

		if (info.divscale) val = val / Math.pow(10, info.divscale);
		return val;
	}

	public _allzero(val: string): boolean {
		for (var len = val.length; len-- > 0;)
			if (val.charAt(len) != '0') return false;
		return true;
	}

	protected override coerceToString_(value: number | string | null | undefined): string {
		var fmt = this._format,
			symbols = this._localizedSymbols,
			DECIMAL = (symbols ? symbols : zk).DECIMAL;
		return value == null ? '' : fmt ?
			zk.fmt.Number.format(fmt, value as string, this._rounding!, symbols) :
			DECIMAL == '.' ? ('' + value) : ('' + value).replace('.', DECIMAL!);
	}

	protected override getAllowedKeys_(): string {
		var symbols = this._localizedSymbols;
		return super.getAllowedKeys_()
			+ (symbols ? symbols : zk).DECIMAL + 'e';
		//supports scientific expression such as 1e2
	}
}
zul.inp.Doublebox = zk.regClass(Doublebox);