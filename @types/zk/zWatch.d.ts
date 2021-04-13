/* zWatch.d.ts

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
    interface ClientActivity {
        _beforeSizeForRead: any;
        beforeSize: any;
        afterSize: any;
        onBindLevelChange: any;
        onBindLevelMove: any;
        onFitSize: any;
        onHide: any;
        onFloatUp: any;
        onResponse: any;
        onCommandReady: any;
        onRestore: any;
        onScroll: any;
        onSend: any;
        onSize: any;
        onShow: any;
        onVParent: any;
    }

    interface FireOptions {
        reverse: boolean;
        timeout: number;
        triggerByFocus: boolean;
        triggerByClick: number;
        rtags: {[key: string]: any};
    }

    interface ZWatch {
        fire(name: string, origin?: any, opts?: Partial<FireOptions>, ...vararg: any[]): void;
        fireDown(name: string, origin: any, opts?: Partial<FireOptions> | null, ...vararg: any[]): void;
        listen(infs: Partial<ClientActivity>): void;
        unlisten(infs: Partial<ClientActivity>): void;
        unlistenAll(name: string): void;
    }
}

declare var zWatch: zk.ZWatch;
