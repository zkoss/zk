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
	override parent!: zul.sel.Listbox | undefined;
	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	getListbox(): zul.sel.Listbox | undefined {
		return this.parent;
	}

	override getGroupPackage_(): string {
		return 'zkex.sel';
	}

	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (!(child instanceof zul.sel.Listheader)) {
			zk.error('Unsupported child for listhead: ' + child.className);
			return false;
		}
		return true;
	}
}