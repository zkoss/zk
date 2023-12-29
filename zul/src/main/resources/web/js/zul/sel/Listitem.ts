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
	const dragImg = drag._dragImg as JQuery | undefined;
	if (dragImg) {
		// update drag image
		const allow = jq(drag.node).hasClass('z-drop-allow');
		// eslint-disable-next-line @typescript-eslint/prefer-for-of
		for (let len = 0; len < dragImg.length; len++) {
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
 * @defaultValue {@link getZclass}: z-listitem
 */
@zk.WrapClass('zul.sel.Listitem')
export class Listitem extends zul.sel.ItemWidget {
	// Parent could be null as asserted by `getListgroup`.
	override parent?: zul.sel.Listbox;
	override nextSibling?: zul.sel.Listitem;
	override previousSibling?: zul.sel.Listitem;
	override firstChild?: zul.sel.Listcell;
	override lastChild?: zul.sel.Listcell;

	/**
	 * @returns the list box that it belongs to.
	 */
	getListbox(): zul.sel.Listbox | undefined {
		return this.parent;
	}

	/**
	 * @returns the listgroup that this item belongs to, or null.
	 */
	getListgroup(): zkex.sel.Listgroup | undefined {
		// TODO: this performance is not good.
		if (_isPE() && this.parent?.hasGroup())
			// eslint-disable-next-line @typescript-eslint/no-this-alias
			for (let w: zul.sel.Listitem | undefined = this; w; w = w.previousSibling)
				if (w instanceof zkex.sel.Listgroup)
					return w;

		return undefined;
	}

	/**
	 * Sets the label of the {@link Listcell} it contains.
	 * <p>If it is not created, we automatically create it.
	 */
	setLabel(label: string): this {
		this._autoFirstCell().setLabel(label);
		return this;
	}

	// replace the origional DD_dragging
	/** @internal */
	override getDragOptions_(map: zk.DraggableOptions): zk.DraggableOptions {
		const old = map.change!;
		map.change = function (drag, pt, evt) {
			old(drag, pt, evt);
			// update drag image after origional function
			updateImg(drag);
		};
		return super.getDragOptions_(map);
	}

	/**
	 * Sets the image of the {@link Listcell} it contains.
	 * <p>If it is not created, we automatically create it.
	 */
	setImage(image: string): this {
		this._autoFirstCell().setImage(image);
		return this;
	}

	/** @internal */
	_autoFirstCell(): zul.sel.Listcell {
		if (!this.firstChild)
			this.appendChild(new zul.sel.Listcell());
		return this.firstChild!; // guaranteed to exist because appended in the previous line
	}
	
	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		if (_isPE() && (this instanceof zkex.sel.Listgroup || this instanceof zkex.sel.Listgroupfoot)
			|| no?.visible)
			return super.domStyle_(no);

		const /*safe*/ style = super.domStyle_(no),
			group = this.getListgroup();
		return group && !group.isOpen() ? style + 'display:none;' : style;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		const /*safe*/ cls = super.domClass_(no),
			list = this.getListbox();
		// NOTE: The following `this.$n()` could be null. This behavior is verified on old code.
		if (list) {
			const sclass = zUtl.encodeXML(list.getOddRowSclass());
			if (jq(this.$n()).hasClass(sclass))
				return cls + ' ' + sclass;
		}
		return cls;
	}

	override replaceWidget(newwgt: zul.sel.Listitem, skipper?: zk.Skipper): void {
		this._syncListitems(newwgt);
		super.replaceWidget(newwgt, skipper);
	}

	override scrollIntoView(): this {
		const bar = this.getListbox()!._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n_());
		} else {
			super.scrollIntoView();
		}
		return this;
	}

	/** @internal */
	_syncListitems(newwgt: zul.sel.Listitem): void {
		const box = this.getListbox();
		if (box) {
			if (box.firstItem!.uuid == newwgt.uuid)
				box.firstItem = newwgt;
			if (box.lastItem!.uuid == newwgt.uuid)
				box.lastItem = newwgt;

			const items = box._selItems,
				b1 = this.isSelected();
			if (b1)
				items.$remove(this);
			const b2 = newwgt.isSelected();
			if (b2)
				items.push(newwgt);
			if (b1 != b2)
				box._updHeaderCM();
		}
	}

	/** @internal */
	override compareItemPos_(item: zul.sel.Listitem): number {
		const thisIndex = this._index!, itemIndex = item._index!;
		return thisIndex == itemIndex ? 0 : thisIndex > itemIndex ? -1 : 1;
	}

	/** @internal */
	override shallFireSizedLaterWhenAddChd_(): boolean {
		if (this.getListbox()!._model == 'group') {
			zWatch.listen({
				onCommandReady: this
			});
			return true;
		}
		super.shallFireSizedLaterWhenAddChd_();
		return false;
	}

	// ZK-3733
	onCommandReady(ctl: zk.ZWatchController): void {
		zUtl.fireSized(this);
		zWatch.unlisten({
			onCommandReady: this
		});
	}
}