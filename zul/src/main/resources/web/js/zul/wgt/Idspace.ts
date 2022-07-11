/* Idspace.ts

	Purpose:

	Description:

	History:
		Thu Nov 03 12:15:49     2011, Created by benbai

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Just like DIV tag but implements IdSpace.
 * @since 6.0.0
 */
@zk.WrapClass('zul.wgt.Idspace')
export class Idspace extends zul.wgt.Div {
	public constructor() {
		super(); // FIXME: params?
		this._fellows = {};
		// NOTE: Prior to TS migration, super is called after `_fellows` is initialized.
		// In this case, it's safe to initialize `_fellows` after calling super, as
		// the none in the chain of super constructors will access `_fellows`.
	}
}
//ZK-3255: support nodom mold
zk.$intercepts(zul.wgt.Idspace, zk.NoDOM);