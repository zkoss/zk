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
			if (oldSel && oldSel.desktop) oldSel._sel(false, true);
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
	override parent!: zul.tab.Tabbox | null;

	_selPnl?: zul.tab.Tabpanel | null;
	_shallSync?: boolean;

	/** Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	getTabbox(): zul.tab.Tabbox | null {
		return this.parent;
	}

	//bug #3014664
	override setVflex(v: boolean | string | null | undefined): this { //vflex ignored for Tabpanels
		if (v != 'min') v = false;
		return super.setVflex(v);
	}

	//bug #3014664
	override setHflex(v: boolean | string | null | undefined): this { //hflex ignored for Tabpanels
		if (v != 'min') v = false;
		return super.setHflex(v);
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
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
		var tabbox = this.getTabbox(),
			cave: HTMLElement | null | undefined;
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