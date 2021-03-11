/* globals.d.ts

	Purpose:
		Type definitions for ZK
	Description:

	History:
		Mon Apr 01 14:39:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
interface String {
    $camel(): string;
    $inc(diff: number): string;
    $sub(cc: string): number;
    startsWith(search: string, position?: number): boolean;
    endsWith(search: string, position?: number): boolean;
}

interface Array<T> {
    $indexOf(o: T): number;
    $contains(o: T): boolean;
    $equals(o: Record<string, unknown>): boolean;
    $remove(o: T): boolean;
    $addAll(o: T[]): number;
    $clone(): T[];
}

interface Window {
    zkservice: zk.ZKServiceStatic;
    onIframeURLChange(uuid: string, url: string): void;
}

// mount.js
declare function zkdt(dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string): zk.Desktop;
declare function zkx(wi, extra, aucmds, js?: string): void;
declare function zkx_(args: any, stub: (child: zk.Widget) => void, filter?): void;
declare function zkac(): void;
declare function zkmx(): void;
declare function zkmb(bindOnly: boolean): void;
declare function zkme(): void;
declare function zkdh(name: string, script: string): void;
// zk.wpd
declare function $eval(x: string): any;
// widget.ts
declare function zkreg(pkg: string, load: boolean): void;

declare var _zkf: any; // temp object holder

declare class DateImpl extends Date {
}