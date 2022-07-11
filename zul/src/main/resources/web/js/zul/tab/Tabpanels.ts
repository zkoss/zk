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
	public override parent!: zul.tab.Tabbox | null;

	public _selPnl?: zul.tab.Tabpanel | null;
	private _shallSync?: boolean;

	/** Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	public getTabbox(): zul.tab.Tabbox | null {
		return this.parent;
	}

	//bug #3014664
	public override setVflex(v: boolean | string | null | undefined): void { //vflex ignored for Tabpanels
		if (v != 'min') v = false;
		super.setVflex(v);
	}

	//bug #3014664
	public override setHflex(v: boolean | string | null | undefined): void { //hflex ignored for Tabpanels
		if (v != 'min') v = false;
		super.setHflex(v);
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		this._shallSync = true;
	}

	protected override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// sync select status if tabbox not in accordion mold or
		// the child cave is already visible
		var tabbox = this.getTabbox(),
			cave: HTMLElement | null | undefined;
		if (tabbox && (!tabbox.inAccordionMold()
				|| (cave = child.$n('cave')) && cave.style.display != 'none'))
			this._shallSync = true;
	}

	public onResponse(): void {
		//bug B65-ZK-1785 synchronize selection only once in the end after all removes have finished
		if (this._shallSync) {
			_syncSelectedPanels(this);
			this._shallSync = false;
		}
	}
}