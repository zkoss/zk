/* Vlayout.ts

	Purpose:

	Description:

	History:
		Fri Aug  6 12:37:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A vertical layout.
 * @defaultValue {@link getZclass}: z-vlayout.
 * @since 5.0.4
 */
@zk.WrapClass('zul.box.Vlayout')
export class Vlayout extends zul.box.Layout {
    /** @internal */
    override isVertical_(): boolean {
		return true;
	}

    /** @internal */
    override getFlexDirection_(): string {
		return 'column';
	}
}