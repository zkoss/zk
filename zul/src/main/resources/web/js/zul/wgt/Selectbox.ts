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
 * <p>Default {@link #getZclass}: z-selectbox.
 * @author jumperchen
 * @since 6.0.0
 */
@zk.WrapClass('zul.wgt.Selectbox')
export class Selectbox extends zul.Widget<HTMLSelectElement> {
	_selectedIndex?: number;
	_disabled?: boolean;
	_multiple?: boolean;
	_maxlength?: number;
	_selectedIndexes?: number[];
	_name?: string;

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 * @return int
	 */
	getSelectedIndex(): number | undefined {
		return this._selectedIndex;
	}

	/**
	 * Selects the item with the given index.
	 * @param int selectedIndex
	 */
	setSelectedIndex(selectedIndex: number, opts?: Record<string, boolean>): this {
		const o = this._selectedIndex;
		this._selectedIndex = selectedIndex;

		if (o !== selectedIndex || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.selectedIndex = selectedIndex;
		}

		return this;
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean | undefined {
		return this._disabled;
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
			if (n) n.disabled = disabled;
		}

		return this;
	}

	/**
	 * Returns whether it is multiple selections.
	 * <p>
	 * Default: false.
	 * @return boolean
	 * @since 10.0.0 for Zephyr
	 */
	isMultiple(): boolean | undefined {
		return this._multiple;
	}

	/**
	 * Sets whether multiple selections are allowed.
	 * @param boolean multiple
	 * @since 10.0.0 for Zephyr
	 */
	setMultiple(multiple: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiple;
		this._multiple = multiple;

		if (o !== multiple || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.multiple = multiple;
		}

		return this;
	}

	/**
	 * Returns the maximal length of each item's label.
	 * @return int
	 * @since 10.0.0 for Zephyr
	 */
	getMaxlength(): number | undefined {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of each option's label.
	 * @param int maxlength
	 * @since 10.0.0 for Zephyr
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns all the selected indexes or null if no selections.
	 * @return int[] selectedIndexes
	 * @since 10.0.0 for Zephyr
	 */
	getSelectedIndexes(): number[] | undefined {
		return this._selectedIndexes;
	}

	/**
	 * Sets all the selected indexes.
	 * @param int[] selectedIndexes
	 * @since 10.0.0 for Zephyr
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

		if (o !== selectedIndexes || (opts && opts.force)) {
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
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * @return String
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
	 * @param String name
	 *            the name of this component.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.name = name;
		}

		return this;
	}

	_fixSelIndex(): void {
		if (this._selectedIndex! < 0)
			this.$n_().selectedIndex = -1;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n = this.$n_();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');

		if (!zk.gecko) {
			const fn: [zul.wgt.Selectbox, zk.Callable] = [this, this._fixSelIndex];
			zWatch.listen({onRestore: fn, onVParent: fn});
		}

		this._fixSelIndex();
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n_();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		super.unbind_(skipper, after, keepRod);

		const fn: [zul.wgt.Selectbox, zk.Callable] = [this, this._fixSelIndex];
		zWatch.unlisten({onRestore: fn, onVParent: fn});
	}

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
	override doBlur_(evt: zk.Event): void {
		this._doChange(evt);
		return super.doBlur_(evt);
	}

	//Bug 1756559: ctrl key shall fore it to be sent first
	override beforeCtrlKeys_(evt: zk.Event): void {
		this._doChange(evt);
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		const index = this.getSelectedIndex()!, name = this.getName();
		return super.domAttrs_(no)
			+ (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (index > -1 ? ' selectedIndex="' + index + '"' : '')
			+ (name ? ' name="' + name + '"' : '')
			+ (this._multiple ? ' multiple="multiple" style="height: auto; padding: 0;"' : '');
	}
}