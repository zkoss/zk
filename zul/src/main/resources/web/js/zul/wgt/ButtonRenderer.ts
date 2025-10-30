/* ButtonRenderer.ts

	Purpose:

	Description:

	History:
		Fri Jun 12 14:45:38 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** @class zul.wgt.ButtonRenderer
 * The renderer used to render buttons of a window.
 * It is designed to be overridden
 * @since 9.5.0
 */
export var ButtonRenderer = {
	/**
	 * Redraws the collapse button HTML.
	 *
	 * @param wgt - the panel
	 * @param out - the buffer
	 * @param tabindex - the tabindex, can be omitted
	 */
	redrawCollapseButton(wgt: zul.wnd.Panel, out: string[], tabindex?: number): void {
		var uuid = wgt.uuid,
			icon = wgt.$s('icon'),
			isOpen = wgt._open,
			openIcon = isOpen ? wgt.getCollapseOpenIconClass_() : wgt.getCollapseCloseIconClass_(),
			collapsibleLabel = isOpen ? msgzul.PANEL_COLLAPSE : msgzul.PANEL_EXPAND;
		out.push('<button id="', /*safe*/ uuid, '-exp" class="', /*safe*/ icon, ' ', wgt.$s('expand'), '"');
		if (tabindex != undefined) out.push(' tabindex="', tabindex as unknown as string, '"');
		out.push(' title="', /*safe*/ collapsibleLabel, '" aria-label="', /*safe*/ collapsibleLabel, '">');
		out.push('<i class="', /*safe*/ openIcon, '" aria-hidden="true"></i></button>');
	},
	/**
	 * Redraws the minimize button HTML.
	 *
	 * @param wgt - the window
	 * @param out - the buffer
	 * @param tabindex - the tabindex, can be omitted
	 */
	redrawMinimizeButton(wgt: zul.wnd.Window, out: string[], tabindex?: number): void {
		var uuid = wgt.uuid,
			icon = wgt.$s('icon'),
			minLabel = msgzul.PANEL_MINIMIZE;
		out.push('<button id="', /*safe*/ uuid, '-min" class="', /*safe*/ icon, ' ', wgt.$s('minimize'), '"');
		if (tabindex != undefined) out.push(' tabindex="', tabindex as unknown as string, '"');
		out.push(' title="', /*safe*/ minLabel, '" aria-label="', /*safe*/ minLabel, '">');
		out.push('<i class="', /*safe*/ wgt.getMinimizableIconClass_(), '" aria-hidden="true"></i></button>');
	},
	/**
	 * Redraws the maximize button HTML.
	 *
	 * @param wgt - the window
	 * @param out - the buffer
	 * @param tabindex - the tabindex, can be omitted
	 */
	redrawMaximizeButton(wgt: zul.wnd.Window, out: string[], tabindex?: number): void {
		var uuid = wgt.uuid,
			icon = wgt.$s('icon'),
			maxd = wgt._maximized,
			maxLabel = msgzul.PANEL_MAXIMIZE;
		out.push('<button id="', /*safe*/ uuid, '-max" class="', /*safe*/ icon, ' ', wgt.$s('maximize'));
		if (maxd) out.push(' ', wgt.$s('maximized'));
		var maxIcon = maxd ? wgt.getMaximizedIconClass_() : wgt.getMaximizableIconClass_();
		if (tabindex != undefined) out.push('" tabindex="', tabindex as unknown as string);
		out.push('" title="', /*safe*/ maxLabel, '" aria-label="', /*safe*/ maxLabel, '">');
		out.push('<i class="', /*safe*/ maxIcon, '" aria-hidden="true"></i></button>');
	},
	/**
	 * Redraws the close button HTML.
	 *
	 * @param wgt - the window
	 * @param out - the buffer
	 * @param tabindex - the tabindex, can be omitted
	 */
	redrawCloseButton(wgt: zul.wnd.Window, out: string[], tabindex?: number): void {
		var uuid = wgt.uuid,
			icon = wgt.$s('icon'),
			closeLabel = msgzul.PANEL_CLOSE;
		out.push('<button id="', /*safe*/ uuid, '-close" class="', /*safe*/ icon, ' ', wgt.$s('close'), '"');
		if (tabindex != undefined) out.push(' tabindex="', tabindex as unknown as string, '"');
		out.push(' title="', /*safe*/ closeLabel, '" aria-label="', /*safe*/ closeLabel, '">');
		out.push('<i class="', /*safe*/ wgt.getClosableIconClass_(), '" aria-hidden="true"></i></button>');
	}
};
zul.wgt.ButtonRenderer = ButtonRenderer;