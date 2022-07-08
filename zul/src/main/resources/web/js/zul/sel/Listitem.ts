/* Listitem.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _isPE(): boolean {
	return zk.isLoaded('zkex.sel');
}
// _dragImg changed to an array, update image node after original DD_dragging
function updateImg(drag: zk.Draggable): void {
	var dragImg = drag._dragImg as JQuery | null;
	if (dragImg) {
		// update drag image
		var allow = jq(drag.node!).hasClass('z-drop-allow');
		// eslint-disable-next-line @typescript-eslint/prefer-for-of
		for (var len = 0; len < dragImg.length; len++) {
			if (allow)
				jq(dragImg[len]).removeClass('z-icon-times').addClass('z-icon-check');
			else
				jq(dragImg[len]).removeClass('z-icon-check').addClass('z-icon-times');
		}
	}
}
/**
 * A listitem.
 *
 * <p>Default {@link #getZclass}: z-listitem
 */
@zk.WrapClass('zul.sel.Listitem')
export class Listitem extends zul.sel.ItemWidget {
	// Parent could be null as asserted by `getListgroup`.
	public override parent!: zul.sel.Listbox | null;
	public override nextSibling!: zul.sel.Listitem | null;
	public override previousSibling!: zul.sel.Listitem | null;
	public override firstChild!: zul.sel.Listcell | null;
	public override lastChild!: zul.sel.Listcell | null;

	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	public getListbox(): zul.sel.Listbox | null {
		return this.parent;
	}

	/**
	 * Returns the listgroup that this item belongs to, or null.
	 * @return zkex.sel.Listgroup
	 */
	public getListgroup(): zkex.sel.Listgroup | null {
		// TODO: this performance is not good.
		if (_isPE() && this.parent && this.parent.hasGroup())
			for (var w: zul.sel.Listitem | null = this; w; w = w.previousSibling)
				if (w instanceof zkex.sel.Listgroup)
					return w;

		return null;
	}

	/** Sets the label of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String label
	 */
	public setLabel(val: string): void {
		this._autoFirstCell().setLabel(val);
	}

	// replace the origional DD_dragging
	public override getDragOptions_(map: zk.DraggableOptions): zk.DraggableOptions {
		var old = map.change!;
		map.change = function (drag, pt, evt) {
			old(drag, pt, evt);
			// update drag image after origional function
			updateImg(drag);
		};
		return super.getDragOptions_(map);
	}

	/** Sets the image of the {@link Listcell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String image
	 */
	public setImage(val: string): void {
		this._autoFirstCell().setImage(val);
	}

	private _autoFirstCell(): zul.sel.Listcell {
		if (!this.firstChild)
			this.appendChild(new zul.sel.Listcell());
		return this.firstChild!; // guaranteed to exists because appended in the previous line
	}

	//super//
	protected override domStyle_(no?: zk.DomStyleOptions): string {
		if (_isPE() && (this instanceof zkex.sel.Listgroup || this instanceof zkex.sel.Listgroupfoot)
				|| (no && no.visible))
			return super.domStyle_(no);

		var style = super.domStyle_(no),
			group = this.getListgroup();
		return group && !group.isOpen() ? style + 'display:none;' : style;
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no),
			list = this.getListbox(),
			sclass: string;
		// NOTE: The following `this.$n()` could be null. This behavior is verified on old code.
		if (list && jq(this.$n()!).hasClass(sclass = list.getOddRowSclass()))
			return cls + ' ' + sclass;
		return cls;
	}

	public override replaceWidget(newwgt: zul.sel.Listitem, skipper?: zk.Skipper): void {
		this._syncListitems(newwgt);
		super.replaceWidget(newwgt, skipper);
	}

	public override scrollIntoView(): this {
		var bar = this.getListbox()!._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n_());
		} else {
			super.scrollIntoView();
		}
		return this;
	}

	private _syncListitems(newwgt: zul.sel.Listitem): void {
		var box: zul.sel.Listbox | null;
		if (box = this.getListbox()) {
			if (box.firstItem!.uuid == newwgt.uuid)
				box.firstItem = newwgt;
			if (box.lastItem!.uuid == newwgt.uuid)
				box.lastItem = newwgt;

			var items = box._selItems, b1, b2;
			if (b1 = this.isSelected())
				items.$remove(this);
			if (b2 = newwgt.isSelected())
				items.push(newwgt);
			if (b1 != b2)
				box._updHeaderCM();
		}
	}

	//@Override
	public override compareItemPos_(item: zul.sel.Listitem): number {
		var thisIndex = this._index!, itemIndex = item._index!;
		return thisIndex == itemIndex ? 0 : thisIndex > itemIndex ? -1 : 1;
	}

	//@Override
	protected override shallFireSizedLaterWhenAddChd_(): boolean {
		if (this.getListbox()!._model == 'group' as unknown) { // FIXME: inconsistent type
			zWatch.listen({
					onCommandReady: this
			});
			return true;
		}
		super.shallFireSizedLaterWhenAddChd_();
		return false;
	}

	// ZK-3733
	public onCommandReady(ctl: zk.ZWatchController): void {
		zUtl.fireSized(this);
		zWatch.unlisten({
			onCommandReady: this
		});
	}
}