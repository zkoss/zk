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
 * @defaultValue {@link getZclass}: z-comboitem.
 *
 * @see Combobox
 */
@zk.WrapClass('zul.inp.Comboitem')
export class Comboitem extends zul.LabelImageWidget implements zul.LabelImageWidgetWithDisable {
	override parent!: zul.inp.Combobox;
	override nextSibling!: zul.inp.Comboitem | undefined;
	override previousSibling!: zul.inp.Comboitem | undefined;
	/** @internal */
	_maxFlexWidth = true; //ZK-5044
	/** @internal */
	_description?: string;
	/** @internal */
	_content?: string;
	/** @internal */
	declare _value: unknown;
	/** @internal */
	_disabled = false;
	/** @internal */
	_autodisable?: string;

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			var n = this.$n();
			if (n) {
				var disd = this.$s('disabled');
				disabled ? jq(n).addClass(disd) : jq(n).removeClass(disd);
			}
		}

		return this;
	}

	/**
	 * @returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * @defaultValue `""`.
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	getDescription(): string | undefined {
		return this._description;
	}

	/**
	 * Sets the description.
	 */
	setDescription(description: string, opts?: Record<string, boolean>): this {
		const o = this._description;
		this._description = description;

		if (o !== description || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * @defaultValue empty ("").
	 *
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 * @see {@link getDescription}
	 */
	getContent(): string | undefined {
		return this._content;
	}

	/**
	 * Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 * @see {@link setDescription}
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || opts?.force) {
			this.rerender();
		}

		return this;
	}

	// since 10.0.0 for Client Bind to use
	setValue(value: unknown): this {
		this._value = value;
		return this;
	}

	// since 10.0.0 for Client Bind to use
	getValue(): unknown {
		return this._value;
	}

	//super
	/** @internal */
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {pre: true});
	}

	/** @internal */
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

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ scls = super.domClass_(no);
		if (this._disabled && (!no || !no.zclass)) {
			scls += ' ' + this.$s('disabled');
		}
		return scls;
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<li', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></li>');
	}
}