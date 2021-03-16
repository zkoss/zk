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
declare namespace zk {
    interface ZUL {
        Widget: zul.WidgetStatic;
        [key: string]: any;
    }
}

declare namespace zul {
    interface Widget extends zk.Widget {
        afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean;
        beforeCtrlKeys_(evt: zk.Event): boolean;
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

    interface WidgetStatic extends zk.WidgetStatic {
        getOpenTooltip(): zk.Widget | null;
    }
}

declare var zul: zk.ZUL;
