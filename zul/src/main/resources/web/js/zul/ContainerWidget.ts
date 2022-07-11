/* ContainerWidget.ts

	Purpose:

	Description:

	History:
		Thu Feb 13 12:31:44     2014, Created by NoahHuang

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The container related widgets, such as window, groupbox, tabpanel, panelchildren.
 */
//zk.$package('zul');

/**
 * 	A ContainerWidget.
 *  <p>Note: it remains empty extension for compatibility after ZK-4894
 *  @see zul.wnd.Window
 *  @see zul.wnd.Panelchildren
 *  @see zul.wgt.Groupbox
 *  @see zul.tab.Tabpanel
 *
 */
@zk.WrapClass('zul.ContainerWidget')
export class ContainerWidget extends zul.Widget {
	public constructor(props?: Record<string, unknown> | typeof zkac) {
		super(props);
	}
}