/* zul.d.ts

	Purpose:
		Type definitions for ZK
	Description:

	History:
		Mon Apr 01 14:39:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
type ZKWidget = import('@zk/widget').Widget;
type ZKWidgetClass = typeof import('@zk/widget').Widget;

declare namespace zk {
	interface ZUL {
		Widget: WidgetStatic;
		[key: string]: any;
		wgt: {
			Caption: ZKWidgetClass,
			Notification: ZKWidgetClass & {show(...args: unknown[])},
			Image: ZKWidgetClass
		}
		mesh: {
			Auxheader: ZKWidgetClass,
			HeaderWidget: ZKWidgetClass,
			MeshWidget: ZKWidgetClass
		}
	}
    interface Widget extends ZKWidget {
        afterKeyDown_(evt: ZKEvent, simulated?: boolean): boolean;
        beforeCtrlKeys_(evt: ZKEvent): boolean;
        getContext(): string;
        getCtrlKeys(): string;
        getPopup(): string;
        getTooltip(): string;
        setContext(context: string): Widget;
        setContext(context: any): Widget;
        setCtrlKeys(keys: string): Widget;
        setPopup(popup: string): Widget;
        setPopup(popup: any): Widget;
        setTooltip(tooltip: string): Widget;
        setTooltip(popup: any): Widget;
    }

    interface WidgetStatic extends ZKWidget {
        getOpenTooltip(): Widget | null;
    }
}

declare var zul: zk.ZUL;
