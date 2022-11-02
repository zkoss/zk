/* Selectbox.ts

	Purpose:

	Description:

	History:
		Fri Sep 30 10:51:52 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A light weight dropdown list.
 * @defaultValue {@link getZclass}: z-selectbox.
 * @author jumperchen
 * @since 6.0.0
 */
@zk.WrapClass('zul.wgt.Selectbox')
export class Selectbox extends zul.Widget<HTMLSelectElement> {
	/** @internal */
	_selectedIndex?: number;
	/** @internal */
	_disabled?: boolean;
	/** @internal */
	_multiple?: boolean;
	/** @internal */
	_maxlength?: number;
	/** @internal */
	_selectedIndexes?: number[];
	/** @internal */
	_name?: string;

	/**
	 * @returns the index of the selected item (-1 if no one is selected).
	 */
	getSelectedIndex(): number | undefined {
		return this._selectedIndex;
	}

	/**
	 * Selects the item with the given index.
	 */
	setSelectedIndex(selectedIndex: number, opts?: Record<string, boolean>): this {
		const o = this._selectedIndex;
		this._selectedIndex = selectedIndex;

		if (o !== selectedIndex || opts?.force) {
			var n = this.$n();
			if (n)
				n.selectedIndex = selectedIndex;
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * <p>
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
			if (n) n.disabled = disabled;
		}

		return this;
	}

	/**
	 * @returns whether it is multiple selections.
	 * @defaultValue `false`.
	 * @since 10.0.0 for Stateless
	 */
	isMultiple(): boolean {
		return !!this._multiple;
	}

	/**
	 * Sets whether multiple selections are allowed.
	 * @since 10.0.0 for Stateless
	 */
	setMultiple(multiple: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiple;
		this._multiple = multiple;

		if (o !== multiple || opts?.force) {
			var n = this.$n();
			if (n) n.multiple = multiple;
		}

		return this;
	}

	/**
	 * @returns the maximal length of each item's label.
	 * @since 10.0.0 for Stateless
	 */
	getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of each option's label.
	 * @since 10.0.0 for Stateless
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns all the selected indexes or null if no selections.
	 * @since 10.0.0 for Stateless
	 */
	getSelectedIndexes(): number[] | undefined {
		return this._selectedIndexes;
	}

	/**
	 * Sets all the selected indexes.
	 * @since 10.0.0 for Stateless
	 */
	setSelectedIndexes(selectedIndexes: number[], opts?: Record<string, boolean>): this {
		function doSelection(node: HTMLSelectElement, selectedIndexes: number[]): void {
			node.selectedIndex = -1; // deselected all options
			const options = node.options, n = options.length;
			for (const i of selectedIndexes) {
				if (i < n) {
					options[i].selected = true;
				}
			}
		}

		const o = this._selectedIndexes;
		this._selectedIndexes = selectedIndexes;

		if (o !== selectedIndexes || opts?.force) {
			if (!this.isMultiple()) return this;
			if (this.desktop) {
				doSelection(this.$n_(), selectedIndexes);
			} else {
				zk.afterMount(() => doSelection(this.$n_(), selectedIndexes));
			}
		}

		return this;
	}


	/**
	 * @returns the name of this component.
	 * @defaultValue `null`.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	getName(): string | undefined {
		return this._name;
	}

	/**
	 * Sets the name of this component.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 *
	 * @param name - the name of this component.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			var n = this.$n();
			if (n) n.name = name;
		}

		return this;
	}

	/** @internal */
	_fixSelIndex(): void {
		if (this._selectedIndex! < 0)
			this.$n_().selectedIndex = -1;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n = this.$n_();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');

		if (!zk.gecko) {
			const fn: [zul.wgt.Selectbox, CallableFunction] = [this, this._fixSelIndex];
			zWatch.listen({onRestore: fn, onVParent: fn});
		}

		this._fixSelIndex();
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n_();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		super.unbind_(skipper, after, keepRod);

		const fn: [zul.wgt.Selectbox, CallableFunction] = [this, this._fixSelIndex];
		zWatch.unlisten({onRestore: fn, onVParent: fn});
	}

	/** @internal */
	_doChange(evt: zk.Event): void {
		const n = this.$n_();
		if (!this._multiple) {
			var v = n.selectedIndex;
			if (zk.opera) n.selectedIndex = v; //ZK-396: opera displays it wrong (while it is actually -1)
			if (this._selectedIndex == v)
				return;
			this.setSelectedIndex(n.selectedIndex);
			this.fire('onSelect', n.selectedIndex);
		} else {
			const n = this.$n_(),
				opts = n.options,
				selIndexes: number[] = [],
				oldSelIndexes = this.getSelectedIndexes()!;
			for (var j = 0, ol = opts.length; j < ol; ++j) {
				var opt = opts[j];
				if (opt.selected) {
					selIndexes.push(j);
				}
			}
			selIndexes.sort();
			oldSelIndexes.sort();
			if (JSON.stringify(selIndexes) == JSON.stringify(oldSelIndexes)) return;
			this._selectedIndexes = selIndexes;
			this.fire('onSelect', selIndexes);
		}
	}

	//Bug 3304408: IE does not fire onchange
	/** @internal */
	override doBlur_(evt: zk.Event): void {
		this._doChange(evt);
		return super.doBlur_(evt);
	}

	//Bug 1756559: ctrl key shall fore it to be sent first
	/** @internal */
	override beforeCtrlKeys_(evt: zk.Event): void {
		this._doChange(evt);
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		const index = this.getSelectedIndex()!, name = this.getName();
		return super.domAttrs_(no)
			+ (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (index > -1 ? ` selectedIndex="${index}"` : '')
			+ (name ? ' name="' + name + '"' : '')
			+ (this._multiple ? ' multiple="multiple" style="height: auto; padding: 0;"' : '');
	}
}