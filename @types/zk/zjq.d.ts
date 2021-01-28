/* zjq.d.ts

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
    interface ZJQ {
        eventTypes: {[key: string]: string};
        src0: string;
        prototype: zk.JQZK;

        new (ret: JQuery | HTMLElement): zk.JQZK;
        _afterOuter(o: Element): void;
        _beforeOuter(el: Element): Node;
        _cleanVisi(n: Element): void;
        _fixClick(el: Event): void;
        _fixCSS(el: Element): void;
        _fixedVParent(el: Element, option?: boolean): void;
        _fixIframe(el: Element): void;
        fixInput(el: Element): void;
        minWidth(el: Element): number;
    }
}

declare var zjq: zk.ZJQ;
