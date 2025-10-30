/* Textbox.ts

	Purpose:

	Description:

	History:
		Sat Dec 13 23:30:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A textbox.
 * @defaultValue {@link getZclass}: z-textbox.
 */
@zk.WrapClass('zul.inp.Textbox')
export class Textbox extends zul.inp.InputWidget<string> {
	/** @internal */
	override _value = '';
	/** @internal */
	_rows = 1;
	/** @internal */
	_submitByEnter?: boolean;

	/**
	 * @returns whether it is multiline.
	 * @defaultValue `false`.
	 */
	override isMultiline(): boolean {
		return !!this._multiline;
	}

	/**
	 * Sets whether it is multiline.
	 */
	setMultiline(multiline: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiline;
		this._multiline = multiline;

		if (o !== multiline || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether TAB is allowed.
	 * If true, the user can enter TAB in the textbox, rather than change
	 * focus.
	 * @defaultValue `false`.
	 */
	isTabbable(): boolean {
		return !!this._tabbable;
	}

	/**
	 * Sets whether TAB is allowed.
	 * If true, the user can enter TAB in the textbox, rather than change
	 * focus.
	 * @defaultValue `false`.
	 */
	setTabbable(tabbable: boolean): this {
		this._tabbable = tabbable;
		return this;
	}

	/**
	 * @returns the rows.
	 * @defaultValue `1`.
	 */
	getRows(): number {
		return this._rows;
	}

	/**
	 * Sets the rows.
	 */
	setRows(rows: number, opts?: Record<string, boolean>): this {
		const o = this._rows;
		this._rows = rows;

		if (o !== rows || opts?.force) {
			interface ZULInputElement extends HTMLInputElement {
				rows: number;
			}
			// FIXME: grid?
			var inp = this.getInputNode() as ZULInputElement | undefined;
			if (inp && this.isMultiline())
				inp.rows = rows;
		}

		return this;
	}

	/**
	 * @returns the type.
	 * @defaultValue text.
	 */
	override getType(): string {
		return this._type;
	}

	/**
	 * Sets the type.
	 * @param type - the type. Acceptable values are "text" and "password".
	 * Unlike XUL, "timed" is redudant because it is enabled as long as
	 * onChanging is added.
	 */
	setType(type: string, opts?: Record<string, boolean>): this {
		const o = this._type;
		this._type = type;

		if (o !== type || opts?.force) {
			var inp = this.getInputNode();
			if (inp)
				inp.type = type;
		}

		return this;
	}

	/**
	 * @returns whether it is submitByEnter.
	 * @defaultValue `false`.
	 */
	isSubmitByEnter(): boolean {
		return !!this._submitByEnter;
	}

	/**
	 * Sets whether it is submitByEnter.
	 */
	setSubmitByEnter(submitByEnter: boolean): this {
		this._submitByEnter = submitByEnter;
		return this;
	}

	/** @internal */
	override textAttrs_(): string {
		var html = /*safe*/ super.textAttrs_();
		if (this._multiline)
			html += ' rows="' + /*safe*/ zk.parseInt(this._rows) + '"';
		return html;
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		if (evt.keyCode == 13 && this._submitByEnter && this._multiline && !evt.shiftKey) {
			evt.stop();
			this.fire('onOK');
		}
		super.doKeyDown_(evt);
	}
}