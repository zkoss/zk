/* constraint.ts

	Purpose:

	Description:

	History:
		Fri Jan  9 10:32:19     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _posAllowed = [
		'before_start', 'before_end', 'end_before', 'end_after',
		'after_end', 'after_start', 'start_after', 'start_before',
		'overlap', 'overlap_end', 'overlap_before', 'overlap_after',
		'at_pointer', 'after_pointer'
	],
	_baseConstraints = {
		'no positive': 'NO_POSITIVE', 'no negative': 'NO_NEGATIVE', 'no zero': 'NO_ZERO', 'no empty': 'NO_EMPTY',
		'no future': 'NO_FUTURE', 'no past': 'NO_PAST', 'no today': 'NO_TODAY', 'strict': 'STRICT', 'server': 'SERVER'
	} as const;

export type SimpleConstraintFlags = Partial<Record<typeof _baseConstraints[keyof typeof _baseConstraints], boolean>>
export type SimpleConstraintValidateStrict = (val: string) => string | null;
export type SimpleConstraintErrorMessages = Record<string, string | undefined>

/**
 * The default constraint supporting no empty, regular expressions and so on.
 * <p>Depending on the component (such as {@link Intbox} and {@link zul.db.Datebox}).
 * @disable(zkgwt)
 */
export class SimpleConstraint extends zk.Object {
	private _finishParseCst = true;
	private _regex?: RegExp;
	private _flags: SimpleConstraintFlags;
	public _pos?: string;
	private _errmsg: SimpleConstraintErrorMessages;
	private _cst?: string;
	public serverValidate?: boolean;
	private _cstArr!: string[];

	/** Constructor.
	 * @param Object a
	 * It can be String or number, the number or name of flag,
	 * such as "no positive", 0x0001.
	 * @param String b the regular expression
	 * @param String c the error message
	 */
	public constructor(a: unknown, b?: RegExp | string, c?: string) {
		super();
		if (typeof a == 'string') {
			this._flags = {};
			this._errmsg = {};
			this._cstArr = [];
			this._cst = a;
			this._finishParseCst = false;
			this._init(a);
		} else {
			this._flags = typeof a == 'number' ? this._cvtNum(a) : a as SimpleConstraintFlags || {};
			this._regex = typeof b == 'string' ? new RegExp(b, 'g') : b;
			this._errmsg = {};

			if (this._regex) {
				this._errmsg['regex'] = c;
			}
			for (var flag in this._flags) {
				this._errmsg[flag] = c;
			}

			if (this._flags.SERVER)
				this.serverValidate = true;
		}
	}

	private _init(cst: string): void {
		l_out:
		for (var j = 0, k = 0, len = cst.length; k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) return; //done

				var cc = cst.charAt(j);
				if (cc == '/') {
					var hasEndingSlash = false;
					for (k = ++j; ; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = cst.charAt(k);
						if (cc == '/') {
							hasEndingSlash = true;
							break; //ending / found
						}
						if (cc == '\\') ++k; //skip one
					}

					if (hasEndingSlash) {
						var restCst = cst.substring(k + 1),
							// match zero-or-more character, until reaching a comma or a semicolon or end of string.
							regexFlags: string | undefined = restCst.match(/.*?(?=,|:|$)/)![0].trim();
						if (regexFlags) {
							if (regexFlags.indexOf('d') != -1 || regexFlags.indexOf('y') != -1)
								zk.error('unsupported regex flags in constraint: ' + cst);
							if (regexFlags.indexOf('g') == -1)
								regexFlags += 'g'; // always use global match
						}
					}

					try {
						this._regex = new RegExp(k >= 0 ? cst.substring(j, k) : cst.substring(j), regexFlags || 'g');
					} catch (e) {
						zk.error(e.message || e);
					}
					
					this._cstArr[this._cstArr.length] = 'regex';
					continue l_out;
				}
				if (cc == ':') {
					var leftBraceIdx = 0, rightBraceIdx = len;
					for (k = ++j; ; ++k) { //look for ending
						if (k >= len) { //no ending
							k = -1;
							break;
						}

						cc = cst.charAt(k);
						if (cc == '{') { // ZK-2641: in order to support comma, enclose with curly braces
							leftBraceIdx = k + 1;
							for (++k; ; ++k) {
								if (cst.charAt(k) == '}') { //find enclosing '}'
									rightBraceIdx = k;
									break;
								}
							}
						} else if (cc == ',') break; //msg ending found
					}
					this._errmsg[this._cstArr[this._cstArr.length - 1]] = leftBraceIdx ?
							cst.substring(leftBraceIdx, rightBraceIdx).trim() : k >= 0 ?
							cst.substring(j, k).trim() : cst.substring(j).trim();
					continue l_out;
				}
				if (!zUtl.isChar(cc, {whitespace: 1}))
					break;
			}

			var s;
			for (k = j; ; ++k) {
				if (k >= len) {
					s = cst.substring(j);
					k = -1;
					break;
				}
				var cc = cst.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					if (this._regex && j == k) {
						j++;
						continue;
					}
					s = cst.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}

			this.parseConstraint_(s.trim().toLowerCase());
		}
	}

	/** Returns the constraint flags Object which has many attribute about constraint,
	 *  For example, f.NO_POSITIVE = true.
	 *
	 * @return Object
	 */
	public getFlags(): SimpleConstraintFlags {
		return this._flags;
	}

	/** Parses a constraint into an Object attribute.
	 * For example, "no positive" is parsed to f.NO_POSITIVE = true.
	 *
	 * <p>Deriving classes might override this to provide more constraints.
	 * @param String cst
	 */
	protected parseConstraint_(cst: string): void {
		var f = this._flags,
			arr = this._cstArr,
			bsCst = _baseConstraints[cst];
		if (cst && bsCst) {
			if (cst == 'server') this.serverValidate = true;
			f[bsCst] = true;
			arr[arr.length] = bsCst;
		} else if (cst && _posAllowed.$contains(cst))
			this._pos = cst;
		else if (!arr.length && zk.debugJS)
			zk.error('Unknown constraint: ' + cst);
	}

	private _cvtNum(v: number): SimpleConstraintFlags { //compatible with server side
		var f: SimpleConstraintFlags = {};
		if (v & 1)
			f.NO_POSITIVE = f.NO_FUTURE = true;
		if (v & 2)
			f.NO_NEGATIVE = f.NO_PAST = true;
		if (v & 4)
			f.NO_ZERO = f.NO_TODAY = true;
		if (v & 0x100)
			f.NO_EMPTY = true;
		if (v & 0x200)
			f.STRICT = true;
		if (v = (v & 0xf000))
			this._pos = _posAllowed[(v >> 12) - 1];
		return f;
	}

	/** validation for flag, validate date if val is date
	 * @param zk.Widget wgt
	 * @param Object val a String, a number, or a date, the number or name of flag,
	 * such as "no positive", 0x0001.
	 */
	public validate(wgt: zk.Widget & {validateStrict?: SimpleConstraintValidateStrict}, val: unknown): SimpleConstraintErrorMessages | string | undefined {
		if (!this._finishParseCst) {
			this._cst && this._init(this._cst);
			this._finishParseCst = true;
		}
		const f = this._flags,
			msg = this._errmsg;

		interface NumberLike {
			$toNumber(): number;
		}
		if (val && (val as NumberLike).$toNumber)
			val = (val as NumberLike).$toNumber();

		switch (typeof val) {
		case 'string':
			if (f.NO_EMPTY && (!val || !val.trim()))
				return msg['NO_EMPTY'] || msgzul.EMPTY_NOT_ALLOWED;
			var regex = this._regex;
			if (regex) {
				// Bug 3214754
				var val2 = val.match(regex);
				if (!val2 || val2.join('') != val)
					return msg['regex'] || msgzul.ILLEGAL_VALUE;
			}
			if (f.STRICT && val && wgt.validateStrict) {
				const msg = wgt.validateStrict(val);
				if (msg) return msg;
			}
			return;
		case 'number':
			if (val > 0) {
				if (f.NO_POSITIVE) return msg['NO_POSITIVE'] || this._msgNumDenied();
			} else if (val == 0) {
				if (f.NO_ZERO) return msg['NO_ZERO'] || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg['NO_NEGATIVE'] || this._msgNumDenied();
			return;
		}

		if (val && (val as DateImpl).getFullYear) {
			var date = val as DateImpl,
				tz = date.getTimeZone(),
				today = zUtl.today(null, tz),
				date = window.Dates.newInstance([date.getFullYear(), date.getMonth(), date.getDate()], tz);
			if ((+today - +date) / 86400000 < 0) {
				if (f.NO_FUTURE) return msg['NO_FUTURE'] || this._msgDateDenied();
			} else if (+date - +today == 0) {
				if (f.NO_TODAY) return msg['NO_TODAY'] || this._msgDateDenied();
			} else
				if (f.NO_PAST) return msg['NO_PAST'] || this._msgDateDenied();
			return;
		}

		if (!val && f.NO_EMPTY) return msg['NO_EMPTY'] || msgzul.EMPTY_NOT_ALLOWED;
	}

	private _msgNumDenied(): SimpleConstraintErrorMessages | string {
		var f = this._flags,
			msg = this._errmsg;
		if (f.NO_POSITIVE)
			return msg['NO_POSITIVE'] || (f.NO_ZERO ?
				f.NO_NEGATIVE ? msgzul.NO_POSITIVE_NEGATIVE_ZERO : msgzul.NO_POSITIVE_ZERO :
				f.NO_NEGATIVE ? msgzul.NO_POSITIVE_NEGATIVE : msgzul.NO_POSITIVE);
		else if (f.NO_NEGATIVE)
			return msg['NO_NEGATIVE'] || (f.NO_ZERO ? msgzul.NO_NEGATIVE_ZERO : msgzul.NO_NEGATIVE);
		else if (f.NO_ZERO)
			return msg['NO_ZERO'] || msgzul.NO_ZERO;
		return msg || msgzul.ILLEGAL_VALUE;
	}

	private _msgDateDenied(): SimpleConstraintErrorMessages | string {
		var f = this._flags,
			msg = this._errmsg;
		if (f.NO_FUTURE)
			return msg['NO_FUTURE'] || (f.NO_TODAY ?
				f.NO_PAST ? msgzul.NO_FUTURE_PAST_TODAY : msgzul.NO_FUTURE_TODAY :
				f.NO_PAST ? msgzul.NO_FUTURE_PAST : msgzul.NO_FUTURE);
		else if (f.NO_PAST)
			return msg['NO_PAST'] || (f.NO_TODAY ? msgzul.NO_PAST_TODAY : msgzul.NO_PAST);
		else if (f.NO_TODAY)
			return msg['NO_TODAY'] || msgzul.NO_TODAY;
		return msg || msgzul.ILLEGAL_VALUE;
	}

	public reparseConstraint(): void {
		this._finishParseCst = false;
	}
}
zul.inp.SimpleConstraint = zk.regClass(SimpleConstraint);