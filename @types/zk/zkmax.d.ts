/* zkmax.d.ts

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
    type ZKMAX = Record<string, any>;

    interface Websocket {
        readonly ready: boolean;

        encode(j: number, aureq, dt: zk.Desktop): Record<string, unknown>;
        send(reqInf): void;
        setRequestHeaders(key: string, value: string): void;
    }
}

declare var zkmax: zk.ZKMAX;
declare var zWs: zk.Websocket;
