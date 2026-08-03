/* Carouselitem.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:04:09 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A single slide within a {@link zul.wgt.Carousel}.
 * @defaultValue {@link getZclass}: "z-carouselitem".
 */
@zk.WrapClass('zul.wgt.Carouselitem')
export class Carouselitem extends zul.LabelImageWidget {
	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-carouselitem`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-carouselitem' : this._zclass;
	}
}
