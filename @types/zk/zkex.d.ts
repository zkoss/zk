/* zkex.d.ts

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
    type ZKEX = Record<string, any>;
	interface Widget extends Object {
		listen(infos: string, Widget?): Widget;
		unlisten(infos: string, Widget?): Widget;
	}
}

declare var zkex: zk.ZKEX;
declare var msgzul: any;
