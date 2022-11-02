/* Radiogroup.ts

	Purpose:

	Description:

	History:
		Wed Dec 17 09:25:17     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _concatItem(group: zul.wgt.Radiogroup): zul.wgt.Radio[] {
	var sum = _concatItem0(group);
	sum.$addAll(group._externs);
	return sum;
}
function _concatItem0(cmp: zk.Widget): zul.wgt.Radio[] {
	var sum: zul.wgt.Radio[] = [];
	for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
		if (wgt instanceof zul.wgt.Radio)
			sum.push(wgt);
		else if (!(wgt instanceof zul.wgt.Radiogroup)) //skip nested radiogroup
			sum = sum.concat(_concatItem0(wgt));
	}
	return sum;
}

function _getAt(group: zul.wgt.Radiogroup, cur: {value: number}, index: number): zul.wgt.Radio | undefined {
	var r = _getAt0(group, cur, index);
	if (!r)
		for (var extns = group._externs, j = 0, l = extns.length; j < l; ++j)
			if (!_redudant(group, extns[j]) && cur.value++ == index)
				return extns[j];
	return r;
}
function _getAt0(cmp: zk.Widget, cur: {value: number}, index: number): zul.wgt.Radio | undefined {
	for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
		if (wgt instanceof zul.wgt.Radio) {
			if (cur.value++ == index) return wgt;
		} else if (!(wgt instanceof zul.wgt.Radiogroup)) {
			var r = _getAt0(wgt, cur, index);
			if (r != null) return r;
		}
	}
}

function _fixSelIndex(group: zul.wgt.Radiogroup, cur: {value: number}): number {
	var jsel = _fixSelIndex0(group, cur);
	if (jsel < 0)
		for (var extns = group._externs, j = 0, l = extns.length; j < l; ++j) {
			const radio = extns[j];
			if (!_redudant(group, radio)) {
				if (radio.isSelected())
					return cur.value;
				++cur.value;
			}
		}
	return jsel;
}
function _fixSelIndex0(cmp: zk.Widget, cur: {value: number}): number {
	for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
		if (wgt instanceof zul.wgt.Radio) {
			if (wgt.isSelected())
				return cur.value;
			++cur.value;
		} else if (!(wgt instanceof zul.wgt.Radiogroup)) {
			var jsel = _fixSelIndex0(wgt, cur);
			if (jsel >= 0) return jsel;
		}
	}
	return -1;
}

function _redudant(group: zul.wgt.Radiogroup, radio: zul.wgt.Radio): boolean {
	for (var p = radio.parent; p != null; p = p.parent)
		if (p instanceof zul.wgt.Radiogroup)
			return p == group;
	return false;
}

/**
 * A radio group.
 *
 * <p>Note: To support the versatile layout, a radio group accepts any kind of
 * children, including {@link Radio}. On the other hand, the parent of
 * a radio, if any, must be a radio group.
 *
 */
@zk.WrapClass('zul.wgt.Radiogroup')
export class Radiogroup extends zul.Widget {
	/** @internal */
	_orient = 'horizontal';
	/** @internal */
	_jsel = -1;
	/** @internal */
	_name?: string;
	/** @internal */
	_disabled?: boolean;
	/** @internal */
	_externs: zul.wgt.Radio[];

	constructor() {
		super(); // FIXME: params?
		this._externs = [];
	}

	/**
	 * @returns the orient.
	 * @defaultValue `"horizontal"`.
	 */
	getOrient(): string {
		return this._orient;
	}

	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * @defaultValue automatically generated an unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	getName(): string | undefined {
		return this._name;
	}

	/**
	 * Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			for (var items = this.getItems(), i = items.length; i--;)
				items[i].setName(name);
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 * @since 10.0.0
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			this.getItems().forEach((r) => {
				if (r.desktop) {
					r.setDisabled(disabled);
				}
			});
		}

		return this;
	}

	/**
	 * Sets whether to disable all radios.
	 * @defaultValue `false`.
	 * @since 10.0.0
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * @returns the radio button at the specified index.
	 */
	getItemAtIndex(index: number): zul.wgt.Radio | undefined {
		return index >= 0 ? _getAt(this, {value: 0}, index) : undefined;
	}

	/**
	 * @returns (int) the number of radio buttons in this group.
	 */
	getItemCount(): number {
		return this.getItems().length;
	}

	/**
	 * @returns the all of radio buttons in this group.
	 */
	getItems(): zul.wgt.Radio[] {
		return _concatItem(this);
	}

	/**
	 * @returns (int) the index of the selected radio button (-1 if no one is selected).
	 */
	getSelectedIndex(): number {
		return this._jsel;
	}

	/**
	 * Deselects all the currently selected radio button and selects
	 * the radio button with the given index.
	 */
	setSelectedIndex(selectedIndex: number): this {
		if (selectedIndex < 0) selectedIndex = -1;
		if (this._jsel != selectedIndex) {
			this._jsel = selectedIndex;
			if (this.desktop) {
				if (selectedIndex < 0) {
					this.getSelectedItem()!.setSelected(false);
				} else {
					this.getItemAtIndex(selectedIndex)!.setSelected(true);
				}
			}
		}
		return this;
	}

	/**
	 * @returns the selected radio button.
	 */
	getSelectedItem(): zul.wgt.Radio | undefined {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel) : undefined;
	}

	/**
	 * Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 */
	setSelectedItem(selectedItem?: zul.wgt.Radio): this {
		if (selectedItem == null)
			this.setSelectedIndex(-1);
		else if (selectedItem instanceof zul.wgt.Radio)
			selectedItem.setSelected(true);
		return this;
	}

	appendItem(label: string, value: string): zul.wgt.Radio {
		var item = new zul.wgt.Radio();
		item.setLabel(label);
		item.setValue(value);
		this.appendChild(item);
		return item;
	}

	/**
	 * Removes the child radio button in the list box at the given index.
	 * @returns Radio the removed radio button.
	 */
	removeItemAt(index: number): zul.wgt.Radio | undefined {
		var item = this.getItemAtIndex(index);
		if (item && !this._rmExtern(item)) {
			var p = item.parent;
			if (p) p.removeChild(item);
		}
		return item;
	}

	/** @internal */
	_fixSelectedIndex(): void {
		this._jsel = _fixSelIndex(this, {value: 0});
	}

	/** @internal */
	_fixOnAdd(child: zul.wgt.Radio): void {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call _fixSelectedIndex()
		} else {
			this._fixSelectedIndex();
		}
		// handle this disabled state for stateless.
		child.setDisabled(this.isDisabled());
	}

	/** @internal */
	_fixOnRemove(child: zul.wgt.Radio): void {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this._fixSelectedIndex();
		}
	}

	/** @internal */
	_addExtern(radio: zul.wgt.Radio): void {
		this._externs.push(radio);
		if (!_redudant(this, radio))
			this._fixOnAdd(radio);
	}

	/** @internal */
	_rmExtern(radio: zul.wgt.Radio): boolean {
		if (this._externs.$remove(radio)) {
			if (!_redudant(this, radio))
				this._fixOnRemove(radio);
			return true;
		}
		return false;
	}

	// ZK-4989
	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			const added = this.$s(this.getOrient());
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this.listen({onCheck: this});
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this.unlisten({onCheck: this});
		super.unbind_(skipper, after, keepRod);
	}

	onCheck(evt: zk.Event): void {
		this._fixSelectedIndex();
	}
}