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
    zkpe: zk.$void;
	zkpi(nm: string, wv: boolean): Record<string, unknown> | null;
	zkpb(pguid: string, dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string, props: Record<string, string>): void;
	zkver(ver: string, build: string, ctxURI: string, updURI: string, modVers: Record<string, string>, opts: Record<string, unknown>): void;
	zkmld(wgtcls: Record<string, zk.Class>, molds: Record<string, (() => void)>): void;
	zkamn(pkg: string, fn: (() => void)): void;
	zkopt(opts: Record<string, any>): void;
	Dates: any;
	DateImpl: any;
}

// mount.ts
declare function zkdt(dtid: string | undefined, contextURI: string | undefined, updateURI: string | undefined, resourceURI: string | undefined, reqURI: string | undefined): zk.Desktop;
declare function zkx(wi: any[], extra?: number, aucmds?, js?: string): void;
declare function zkx_(args: any, stub: (child: zk.Widget) => void, filter?): void;
declare function zkac(): void;
declare function zkmx(): void;
declare function zkmb(bindOnly?: boolean): void;
declare function zkme(): void;
declare function zkdh(name: string, script: string): void;

// zk.wpd
declare function $eval(x: string): any;
// widget.ts
declare function zkreg(pkg: string, load: boolean): void;

declare var _zkf: any; // temp object holder

declare class DateImpl extends Date {
    _moment?: any;
    _timezone?: any;
    tz(v): any;
    _getTzMoment(): any;
    _getUTCMoment(): any;
    getTimeZone(): any;
    getYear(): number;
    setYear(v: number): number;
}

declare var zDebug: zk.Object;

// Workaround for ActiveXObject
declare var ActiveXObject: (type: string) => void;