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
	override firstChild!: zul.sel.Option | undefined;
	override lastChild!: zul.sel.Option | undefined;
	_open = true;
	_disabled?: boolean;

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.disabled = (disabled ? 'disabled' : '') as unknown as boolean;
		}

		return this;
	}

	/** Returns whether this container is open.
	 * <p>Default: true.
	 * @return boolean
	 */
	isOpen(): boolean {
		return this._open;
	}

	/** Sets whether this container is open.
	 * @param boolean open
	 */
	// FIXME: can a defSet generated setter accept more than one arguments before `opts`?
	setOpen(open: boolean, fromServer: boolean, opts?: Record<string, boolean>): this {
		const o = this._open;
		this._open = open;

		if (o !== open || (opts && opts.force)) {
			if (this.desktop)
				this.parent!.requestRerender_(fromServer);
		}

		return this;
	}

	/** Returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	getLabel(): string | undefined {
		return this.firstChild ? this.firstChild.domLabel_() : undefined;
	}

	updateLabel_(): void {
		var n = this.$n();
		if (n) n.label = this.getLabel()!;
	}

	//@Override
	override setVisible(visible: boolean, fromServer?: boolean): this {
		if (this._visible != visible) {
			this._visible = visible;
			if (this.desktop)
				this.parent!.requestRerender_(fromServer);
		}
		return this;
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no),
			label = this.getLabel(),
			disabled = this.isDisabled();
		if (label)
			attr += ' label="' + label + '"';
		if (disabled)
			attr += ' disabled="disabled"';
		return attr;
	}
}