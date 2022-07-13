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
	_orient = 'horizontal';
	_jsel = -1;
	_name?: string;
	_disabled?: boolean;
	_externs: zul.wgt.Radio[];

	constructor() {
		super(); // FIXME: params?
		this._externs = [];
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	setOrient(v: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = v;

		if (o !== v || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Default: automatically generated an unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @return String
	 */
	getName(): string | undefined {
		return this._name;
	}

	/** Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @param String name
	 */
	setName(v: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = v;

		if (o !== v || (opts && opts.force)) {
			for (var items = this.getItems(), i = items.length; i--;)
				items[i].setName(v);
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 10.0.0
	 */
	setDisabled(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			this.getItems().forEach((r) => {
				if (r.desktop) {
					r.setDisabled(v);
				}
			});
		}

		return this;
	}

	/** Sets whether to disable all radios.
	 * <p>Default: false.
	 * @since 10.0.0
	 */
	isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Returns the radio button at the specified index.
	 * @param int index
	 * @return Radio
	 */
	getItemAtIndex(index: number): zul.wgt.Radio | null | undefined {
		return index >= 0 ? _getAt(this, {value: 0}, index) : null;
	}

	/** Returns the number of radio buttons in this group.
	 * @return int
	 */
	getItemCount(): number {
		return this.getItems().length;
	}

	/** Returns the all of radio buttons in this group.
	 * @return Array
	 */
	getItems(): zul.wgt.Radio[] {
		return _concatItem(this);
	}

	/** Returns the index of the selected radio button (-1 if no one is selected).
	 * @return int
	 */
	getSelectedIndex(): number {
		return this._jsel;
	}

	/** Deselects all the currently selected radio button and selects
	 * the radio button with the given index.
	 * @param int selectedIndex
	 */
	setSelectedIndex(jsel: number): void {
		if (jsel < 0) jsel = -1;
		if (this._jsel != jsel) {
			this._jsel = jsel;
			if (this.desktop) {
				if (jsel < 0) {
					this.getSelectedItem()!.setSelected(false);
				} else {
					this.getItemAtIndex(jsel)!.setSelected(true);
				}
			}
		}
	}

	/** Returns the selected radio button.
	 * @return Radio
	 */
	getSelectedItem(): zul.wgt.Radio | null | undefined {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel) : null;
	}

	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 * @param Radio selectedItem
	 */
	setSelectedItem(item: zul.wgt.Radio | null): void {
		if (item == null)
			this.setSelectedIndex(-1);
		else if (item instanceof zul.wgt.Radio)
			item.setSelected(true);
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
	 * @param int index
	 * @return Radio the removed radio button.
	 */
	removeItemAt(index: number): zul.wgt.Radio | null | undefined {
		var item = this.getItemAtIndex(index);
		if (item && !this._rmExtern(item)) {
			var p = item.parent;
			if (p) p.removeChild(item);
		}
		return item;
	}

	/** method */
	_fixSelectedIndex(): void {
		this._jsel = _fixSelIndex(this, {value: 0});
	}

	_fixOnAdd(child: zul.wgt.Radio): void {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call _fixSelectedIndex()
		} else {
			this._fixSelectedIndex();
		}
		// handle this disabled state for zephyr.
		child.setDisabled(this.isDisabled());
	}

	_fixOnRemove(child: zul.wgt.Radio): void {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this._fixSelectedIndex();
		}
	}

	_addExtern(radio: zul.wgt.Radio): void {
		this._externs.push(radio);
		if (!_redudant(this, radio))
			this._fixOnAdd(radio);
	}

	_rmExtern(radio: zul.wgt.Radio): boolean {
		if (this._externs.$remove(radio)) {
			if (!_redudant(this, radio))
				this._fixOnRemove(radio);
			return true;
		}
		return false;
	}

	// ZK-4989
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			let added = this.$s(this.getOrient());
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this.listen({onCheck: this});
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		this.unlisten({onCheck: this});
		super.unbind_(skipper, after, keepRod);
	}

	onCheck(evt: zk.Event): void {
		this._fixSelectedIndex();
	}
}