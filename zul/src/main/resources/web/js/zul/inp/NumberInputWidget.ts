/* NumberInputWidget.ts

	Purpose:

	Description:

	History:
		Fri May 27 16:12:42 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

var _allowKeys: string,
	globallocalizedSymbols: Record<string, zk.LocalizedSymbols> = {};

// Fixed merging JS issue
zk.load('zul.lang', function () {
	_allowKeys = '0123456789-' + zk.MINUS + zk.PERCENT + (zk.groupingDenied ? '' : zk.GROUPING);
});
/**
 * A skeletal implementation for number-type input box.
 * @since 5.0.8
 */
@zk.WrapClass('zul.inp.NumberInputWidget')
export class NumberInputWidget<ValueType> extends zul.inp.FormatWidget<ValueType> {
	_rounding?: number;
	_localizedSymbols?: zk.LocalizedSymbols;
	_allowKeys?: string | null;

	/** Returns the rounding mode.
	 * <ul>
	 * <li>0: ROUND_UP</li>
	 * <li>1: ROUND_DOWN</li>
	 * <li>2: ROUND_CEILING</li>
	 * <li>3: ROUND_FLOOR</li>
	 * <li>4: ROUND_HALF_UP</li>
	 * <li>5: ROUND_HALF_DOWN</li>
	 * <li>6: ROUND_HALF_EVEN</li>
	 * <li>7: ROUND_UNNECESSARY</li>
	 * </ul>
	 * @return int
	 */
	getRounding(): number | undefined {
		return this._rounding;
	}

	/** Sets the rounding mode.
	 * <ul>
	 * <li>0: ROUND_UP</li>
	 * <li>1: ROUND_DOWN</li>
	 * <li>2: ROUND_CEILING</li>
	 * <li>3: ROUND_FLOOR</li>
	 * <li>4: ROUND_HALF_UP</li>
	 * <li>5: ROUND_HALF_DOWN</li>
	 * <li>6: ROUND_HALF_EVEN</li>
	 * <li>7: ROUND_UNNECESSARY</li>
	 * </ul>
	 * @param int rounding mode
	 */
	setRounding(v: number): this {
		this._rounding = v;
		return this;
	}

	getLocalizedSymbols(): zk.LocalizedSymbols | undefined {
		return this._localizedSymbols;
	}

	setLocalizedSymbols(val: string | undefined, opts?: Record<string, boolean>): this {
		const o = this._localizedSymbols;

		if (val) {
			var ary = jq.evalJSON(val) as [string, zk.LocalizedSymbols];
			if (!globallocalizedSymbols[ary[0]])
				globallocalizedSymbols[ary[0]] = ary[1];
			this._localizedSymbols = globallocalizedSymbols[ary[0]];
		} else {
			this._localizedSymbols = val as undefined;
		}

		if (o !== val || (opts && opts.force)) {
			var symbols = this._localizedSymbols;
			this._allowKeys = symbols ?
				'0123456789' + symbols.MINUS + symbols.PERCENT
				+ (zk.groupingDenied ? '' : symbols.GROUPING) : null;
			this.rerender();
		}

		return this;
	}

	/** Returns a string of keystrokes that are allowed.
	 * @return String
	 * @since 5.0.8
	 */
	getAllowedKeys_(): string {
		return this._allowKeys || _allowKeys;
	}

	override doKeyPress_(evt: zk.Event): void {
		//Bug ZK-1373: ALTGR + 3 key in Spanish keyboard is a combination of Ctrl + Alt + 3 for € sign.
		if (evt.ctrlKey && evt.altKey)
			evt.stop();
		if (!this._shallIgnore(evt, this.getAllowedKeys_()))
			super.doKeyPress_(evt);
	}

	override doPaste_(evt: zk.Event): void {
		//Bug ZK-3838: add a paste event dealer
		var inp = this.getInputNode(),
			val = (evt.domEvent!.originalEvent as ClipboardEvent).clipboardData!.getData('text').trim();
		if (new RegExp('^[' + this.getAllowedKeys_().replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&') + ']+$').test(val))
			inp!.value = val;
		evt.stop();
		super.doPaste_(evt);
	}

	override getType(): string {
		return this._type;
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if ((!no || !no.text) && zk.mobile)
			attr += ' inputmode="decimal"';
		return attr;
	}
}