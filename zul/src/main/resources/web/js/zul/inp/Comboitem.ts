/* Comboitem.ts

	Purpose:

	Description:

	History:
		Sun Mar 29 20:53:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 *
 * <p>Default {@link #getZclass}: z-comboitem.
 *
 * @see Combobox
 */
export class Comboitem extends zul.LabelImageWidget {
	public override parent!: zul.inp.Combobox<never>;
	public _maxFlexWidth = true; //ZK-5044
	private _description?: string;
	private _content?: string;
	declare private _value: unknown;

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	public setDisabled(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				var disd = this.$s('disabled');
				v ? jq(n).addClass(disd) : jq(n).removeClass(disd);
			}
		}

		return this;
	}

	/** Returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * <p>Default: "".
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 * @return String
	 */
	public getDescription(): string | undefined {
		return this._description;
	}

	/** Sets the description.
	 * @param String desc
	 */
	public setDescription(desc: string, opts?: Record<string, boolean>): this {
		const o = this._description;
		this._description = desc;

		if (o !== desc || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: empty ("").
	 *
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 * @return String
	 * @see #getDescription
	 */
	public getContent(): string | undefined {
		return this._content;
	}

	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 * @param String content
	 * @see #setDescription
	 */
	public setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	// since 10.0.0 for Zephyr to use
	public setValue(val: unknown): void {
		this._value = val;
	}

	// since 10.0.0 for Zephyr to use
	public getValue(): unknown {
		return this._value;
	}

	//super
	protected override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {pre: true});
	}

	public override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (!this._disabled) {

			var cb = this.parent;
			cb._select(this, {sendOnSelect: true, sendOnChange: true});
			this._updateHoverImage();
			cb.close({sendOnOpen: true, focus: true});

			// Fixed the onFocus event is triggered too late in IE.
			cb._shallClose = true;
			if (zul.inp.InputCtrl.isPreservedFocus(this))
				zk(cb.getInputNode()).focus();
			evt.stop();
		}
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (this._disabled && (!no || !no.zclass)) {
			scls += ' ' + this.$s('disabled');
		}
		return scls;
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<li', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></li>');
	}
}
zul.inp.Comboitem = zk.regClass(Comboitem);