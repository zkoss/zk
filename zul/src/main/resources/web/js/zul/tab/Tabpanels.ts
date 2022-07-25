/* Tabpanels.ts

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:57 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function _syncSelectedPanels(panels: zul.tab.Tabpanels): void {
	//Note: _selTab is in tabbox, while _selPnl is in tabpanels
	const box = panels.getTabbox();
	if (panels.desktop && box) {
		var oldSel = panels._selPnl,
			sel = box._selTab?.getLinkedPanel();
		if (oldSel != sel) {
			if (oldSel?.desktop) oldSel._sel(false, true);
			if (sel) sel._sel(true, true);
			panels._selPnl = sel;
		}
	}
}

/**
 * A collection of tab panels.
 *
 * <p>Default {@link #getZclass}: z-tabpanels.
 */
@zk.WrapClass('zul.tab.Tabpanels')
export class Tabpanels extends zul.Widget {
	override parent!: zul.tab.Tabbox | undefined;

	_selPnl?: zul.tab.Tabpanel;
	_shallSync?: boolean;

	/** Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	getTabbox(): zul.tab.Tabbox | undefined {
		return this.parent;
	}

	//bug #3014664
	override setVflex(vflex?: boolean | string): this { //vflex ignored for Tabpanels
		if (vflex != 'min') vflex = false;
		return super.setVflex(vflex);
	}

	//bug #3014664
	override setHflex(hflex?: boolean | string): this { //hflex ignored for Tabpanels
		if (hflex != 'min') hflex = false;
		return super.setHflex(hflex);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		this._shallSync = true;
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// sync select status if tabbox not in accordion mold or
		// the child cave is already visible
		let tabbox = this.getTabbox(),
			cave: HTMLElement | undefined;
		if (tabbox && (!tabbox.inAccordionMold()
				|| (cave = child.$n('cave')) && cave.style.display != 'none'))
			this._shallSync = true;
	}

	onResponse(): void {
		//bug B65-ZK-1785 synchronize selection only once in the end after all removes have finished
		if (this._shallSync) {
			_syncSelectedPanels(this);
			this._shallSync = false;
		}
	}
}