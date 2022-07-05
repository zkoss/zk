/* Option.ts

	Purpose:

	Description:

	History:
		Mon Jun  1 16:43:59     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML option tag.
 */
@zk.WrapClass('zul.sel.Option')
export class Option extends zul.Widget<HTMLOptionElement> {
	// Parent could be null as asserted by `focus`.
	public override parent!: zul.sel.Select | null; // FIXME: could parent be optgroup?
	// The type of firstChild is determined by the comment in getLabel
	public override firstChild!: zul.sel.Listcell | null;
	public override lastChild!: zul.sel.Listcell | null;
	public _selected = false;
	private _disabled?: boolean;
	private _value?: string;
	private __updating__?: boolean;

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * @param boolean disabled
	 */
	public setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.disabled = (disabled ? 'disabled' : '') as unknown as boolean;
		}

		return this;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 * @return String
	 */
	public getValue(): string | undefined {
		return this._value;
	}

	/** Sets the value.
	 * @param String value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * <p>If you are using listitem with HTML Form (and with
	 * the name attribute), it is better to specify a String-typed
	 * value.
	 */
	public setValue(value: string): this {
		this._value = value;
		return this;
	}

	//@Override
	public override focus(timeout?: number): boolean {
		var p = this.parent;
		if (p) p.focus(timeout);
		// NOTE: Returning false agrees with the original logic (returning nothing,
		// which is will evaluate to undefined, which is falsey).
		return false;
	}

	//@Override
	public override setVisible(visible: boolean | undefined, fromServer?: boolean): void {
		if (this._visible != visible) {
			this._visible = visible;
			if (this.desktop)
				this.parent!.requestRerender_(fromServer);
		}
	}

	/** Sets whether it is selected.
	 * @param boolean selected
	 */
	public setSelected(selected: boolean): void {
		if (this.__updating__) { // for B50-3012466.zul
			delete this.__updating__;
			return; //nothing to do for second loop triggered by this.parent.toggleItemSelection
		}
		try {
			selected = selected || false;
			this.__updating__ = true;
			if (this._selected != selected) {
				if (this.parent)
					this.parent.toggleItemSelection(this);
				this._setSelectedDirectly(selected); // always setting for B50-3012466.zul
			}
		} finally {
			delete this.__updating__;
		}
	}

	public _setSelectedDirectly(selected: boolean): void {
		var n = this.$n();
		// Bug ZK-2285, ignore if the status is the same for IE's issue
		if (n && n.selected != selected) {
			n.selected = (selected ? 'selected' : '') as unknown as boolean;
		}
		this._selected = selected;
	}

	/** Returns whether it is selected.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isSelected(): boolean {
		return this._selected;
	}

	/** Returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	public getLabel(): string | null {
		return this.firstChild ? this.firstChild.getLabel() : null;
	}

	public updateLabel_(): void {
		var n = this.$n();
		if (n) jq(n).html(this.domLabel_());
	}

	/** Returns the maximal length of each item's label.
	 * It is a shortcut of {@link Select#getMaxlength}.
	 * @return int
	 */
	public getMaxlength(): number | undefined {
		return this.parent ? this.parent.getMaxlength() : 0;
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		//B60-ZK-1303: force update parent's selected index.
		if (this.isSelected())
			this.parent!._selectedIndex = this.getOptionIndex_();
	}

	public override doClick_(evt: zk.Event /*, popupOnly?: boolean */): void {
		evt.stop(); // Eats the non-standard onclick event
	}

	/**
	 * The index for option widget only , not including the listhead.etc
	 * @since 6.0.1
	 */
	public getOptionIndex_(): number {
		var parent = this.parent, ret = -1;
		if (parent) {
			for (var w = parent.firstChild; w; w = w.nextSibling) {
				if (w.$instanceof(zul.sel.Option)) {
					ret++;
					if (w == this) break;
				}
			}
		}
		return ret;
	}

	public domLabel_(): string {
		return zUtl.encodeXML(this.getLabel()!, {maxlength: this.getMaxlength()!});
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		var value = this.getValue();
		return super.domAttrs_(no) + (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (this.isSelected() ? ' selected="selected"' : '') + (value ? ' value="' + value + '"' : '');
	}

	public override replaceWidget(newwgt: zul.sel.Option, skipper?: zk.Skipper): void {
		this._syncItems(newwgt);
		super.replaceWidget(newwgt, skipper);
	}

	private _syncItems(newwgt: zul.sel.Option): void {
		if (this.parent && this.isSelected()) {
			var items = this.parent._selItems;
			if (items && items.$remove(this))
				items.push(newwgt);
		}
	}
}