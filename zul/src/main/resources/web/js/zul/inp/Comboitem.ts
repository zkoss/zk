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
@zk.WrapClass('zul.inp.Comboitem')
export class Comboitem extends zul.LabelImageWidget implements zul.LabelImageWidgetWithDisable {
	override parent!: zul.inp.Combobox;
	override nextSibling!: zul.inp.Comboitem | undefined;
	override previousSibling!: zul.inp.Comboitem | undefined;
	_maxFlexWidth = true; //ZK-5044
	_description?: string;
	_content?: string;
	declare _value: unknown;
	_disabled = false;
	_autodisable?: string;

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				var disd = this.$s('disabled');
				disabled ? jq(n).addClass(disd) : jq(n).removeClass(disd);
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
	getDescription(): string | undefined {
		return this._description;
	}

	/** Sets the description.
	 * @param String desc
	 */
	setDescription(desc: string, opts?: Record<string, boolean>): this {
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
	getContent(): string | undefined {
		return this._content;
	}

	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 * @param String content
	 * @see #setDescription
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	// since 10.0.0 for Zephyr to use
	setValue(val: unknown): this {
		this._value = val;
		return this;
	}

	// since 10.0.0 for Zephyr to use
	getValue(): unknown {
		return this._value;
	}

	//super
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {pre: true});
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
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

	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (this._disabled && (!no || !no.zclass)) {
			scls += ' ' + this.$s('disabled');
		}
		return scls;
	}

	override deferRedrawHTML_(out: string[]): void {
		out.push('<li', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></li>');
	}
}