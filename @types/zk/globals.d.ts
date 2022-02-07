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
type ZKObject = import('@zk/zk').ZKObject;
type Widget = import('@zk/widget').Widget;
type ZKEvent = import('@zk/evt').Event;
type EventOptions = import('@zk/evt').EventOptions;
type Moment = import('moment-timezone').Moment;

interface Window {

    zkservice: any;
    onIframeURLChange(uuid: string, url: string): void;
    zkpe: import('@zk/zk').$void;
	zkpi(nm: string, wv: boolean): Record<string, unknown> | null;
	zkpb(pguid: string, dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string, props: Record<string, string>): void;
	zkver(ver: string, build: string, ctxURI: string, updURI: string, modVers: Record<string, string>, opts: Record<string, unknown>): void;
	zkmld(wgtcls: Record<string, unknown>, molds: Record<string, (() => void)>): void;
	zkamn(pkg: string, fn: (() => void)): void;
	zkopt(opts: Record<string, any>): void;
	Dates: any;
	DateImpl: any;
	zkdt(dtid: string | undefined, contextURI: string | undefined, updateURI: string | undefined, resourceURI: string | undefined, reqURI: string | undefined): zk.Desktop;
	zkx(wi: any[], extra?: string | null, aucmds?, js?: string): void;
	zkx_(args: any, stub: (child: zk.Widget) => void, filter?): void;
	zkac(): void;
	zkmx(): void;
	zkmb(bindOnly?: boolean): void;
	zkme(): void;
	zkdh(name: string, script: string): void;
}

// zk.wpd
declare function $eval(x: string): any;
// widget.ts
declare function zkreg(pkg: string, load: boolean): void;

declare var zDebug: ZKObject;

// Workaround for ActiveXObject
declare var ActiveXObject: (type: string) => void;

declare var zAu: import('@zk/au').AUEngine;

declare var msgzk: Record<string, string>;

declare var zWatch: import('@zk/evt').ZWatch;

declare var zUtl: import('@zk/utl').ZUtl;