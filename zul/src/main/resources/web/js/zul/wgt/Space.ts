/* Space.js

	Purpose:

	Description:

	History:
		Mon Nov 17 09:33:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Space is a {@link Separator} with the orient default to "vertical".
 */
export class Space extends zul.wgt.Separator {
	public override _orient = 'vertical';
}
zul.wgt.Space = zk.regClass(Space);