/* Listhead.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:25:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A list headers used to define multi-columns and/or headers.
 *
 *  <p>Default {@link #getZclass}: z-listhead.
 */
@zk.WrapClass('zul.sel.Listhead')
export class Listhead extends zul.mesh.ColumnMenuWidget {
	public override parent!: zul.sel.Listbox | null;
	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	public getListbox(): zul.sel.Listbox | null {
		return this.parent;
	}

	public override getGroupPackage_(): string {
		return 'zkex.sel';
	}
}