/* Optgroup.ts

	Purpose:

	Description:

	History:
		Mon Sep 03 13:01:21 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML optgroup tag.
 * @since 8.6.0
 */
@zk.WrapClass('zul.sel.Optgroup')
export class Optgroup extends zul.Widget<HTMLOptGroupElement> {
	override parent!: zul.sel.Select | undefined;
	override firstChild!: zul.sel.Listcell | undefined;
	override lastChild!: zul.sel.Listcell | undefined;
	/** @internal */
	_open = true;
	/** @internal */
	_disabled?: boolean;

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			var n = this.$n();
			if (n) n.disabled = (disabled ? 'disabled' : '') as unknown as boolean;
		}

		return this;
	}

	/**
	 * @returns whether this container is open.
	 * @defaultValue `true`.
	 */
	isOpen(): boolean {
		return this._open;
	}

	/**
	 * Sets whether this container is open.
	 */
	setOpen(open: boolean, fromServer: boolean, opts?: Record<string, boolean>): this {
		const o = this._open;
		this._open = open;

		if (o !== open || opts?.force) {
			if (this.desktop)
				this.parent!.requestRerender_(fromServer);
		}

		return this;
	}

	/**
	 * @returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 */
	getLabel(): string | undefined {
		return this.firstChild ? this.firstChild.domLabel_() : undefined;
	}

	/**
	 * Sets the label of the {@link Optgroup} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @since 10.0.0
	 */
	// To treat as "value" attribute from "label" at client side
	setLabel(label: string): this {
		this._autoFirstCell().setLabel(label);
		return this;
	}

	/** @internal */
	_autoFirstCell(): zul.sel.Listcell {
		if (!this.firstChild)
			this.appendChild(new zul.sel.Listcell(), true);
		return this.firstChild!; // guaranteed to exist because appended in the previous line
	}

	/** @internal */
	updateLabel_(): void {
		var n = this.$n();
		if (n) n.label = this.getLabel()!;
	}

	override setVisible(visible: boolean, fromServer?: boolean): this {
		if (this._visible != visible) {
			this._visible = visible;
			if (this.desktop)
				this.parent!.requestRerender_(fromServer);
		}
		return this;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var /*safe*/ attr = super.domAttrs_(no),
			label = this.getLabel(),
			disabled = this.isDisabled();
		if (label)
			attr += ' label="' + zUtl.encodeXML(label) + '"';
		if (disabled)
			attr += ' disabled="disabled"';
		return attr;
	}
}