/* FormatWidget.ts

	Purpose:

	Description:

	History:
		Fri Jan 16 12:54:29     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for an input box with format.
 */
@zk.WrapClass('zul.inp.FormatWidget')
export class FormatWidget<ValueType> extends zul.inp.InputWidget<ValueType> {
	/** @internal */
	_format?: string;
	/** @internal */
	_shortcut?: string;
	/** @internal */
	_localizedSymbols?: zk.LocalizedSymbols;
	getTimeZone?(): string | undefined;

	setFormat(format: string, opts?: Record<string, boolean>): this {
		const o = this._format;
		this._format = format;

		if (o !== format || opts?.force) {
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.coerceToString_(this._value);
		}
		return this;
	}

	/**
	 * @returns the format.
	 * Always return null when input type is number (including Intbox, Spinner, Doublebox, Doublespinner, Longbox and Decimalbox) on tablet device.
	 * @defaultValue `null` (used what is defined in the format sheet).
	 */
	getFormat(): string | undefined {
		return this._format;
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);
		if (this._shortcut)
			this.getInputNode()!.value = this._shortcut;
	}

	/** @internal */
	override updateChange_(clear?: boolean): boolean {
		var upd = super.updateChange_();
		if (clear)
			delete this._shortcut;
		return upd;
	}
}