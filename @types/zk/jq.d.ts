/* jq.d.ts

	Purpose:
		Type definitions for ZK
	Description:

	History:
		Mon Apr 01 14:39:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/// <reference types="jquery"/>
/// <reference types="jquery.transit"/>
/// <reference types="jquery-mousewheel"/>

declare namespace zk {
    type Offset = [number, number];

    interface JQueryStaticExtension extends JQueryStatic {
        borders: {l: string; r: string; t: string; b: string};
        browser: {
            chrome?: boolean;
            mozilla?: boolean;
            msie?: boolean;
            opera?: boolean;
            safari?: boolean;
            version: string;
            webkit?: boolean;
        };
        margins: {l: string; r: string; t: string; b: string};
        paddings: {l: string; r: string; t: string; b: string};
        isReady: boolean; // expose jQuery undocumented property
    
        $$(id: '', subId?: string): null;
        $$(id: string, subId?: string): NodeList;
        $$<T>(id: T, subId?: string): T;
        alert(msg: string, opts?: Partial<AlertOptions>): void;
        clearSelection(): boolean;
        confirm(msg: string): boolean;
        css(elem: Node, name: string): string;
        css(elem: Node, name: string, numeric: true): number;
        css(elem: Node, name: string, extra: 'styleonly', styles?: CSSStyleDeclaration): number;
        d2j(d: Date | DateImpl): string;
        doSyncScroll(): void;
        evalJSON(s: string): any;
        filterTextStyle(style: string, plus?: string[]): string;
        filterTextStyle(style: {[key: string]: any}, plus?: string[]): {[key: string]: any};
        focusOut(): void;
        head(): HTMLElement | null;
        innerHeight(): number;
        innerWidth(): number;
        innerX(): number;
        innerY(): number;
        isAncestor(p: Node, c: Node): boolean;
        isOverlapped(ofs1: Offset, dim1: Offset, ofs2: Offset, dim2: Offset, tolerant?: number): boolean;
        j2d(s: string): Date;
        newFrame(id: string, src?: string, style?: string | null): HTMLIFrameElement;
        newHidden(nm: string, val: string, parent?: Node): HTMLInputElement;
        newStackup(el: Node, id: string, anchor?: Node): HTMLIFrameElement;
        nodeName(el: Node): string;
        nodeName(el: Node, ...tag: string[]): boolean;
        onSyncScroll(wgt: any): void;
        onzsync(obj: any): void;
        parseStyle(style: string): {[key: string]: string};
        px(v: number): string;
        px0(v: number): string;
        scrollbarWidth(): number;
        toJSON(obj: any, replace?: (key: any, value: any) => any): string;
        uaMatch(ua: string): { browser: string; version: string };
        unSyncScroll(wgt: any): void;
        unzsync(obj: any): void;
        zsync(org: any): void;
    }

    interface EventMetaData {
        altKey?: true;
        ctrlKey?: true;
        shiftKey?: true;
        metaKey?: true;
        which: number;
    }

    interface EventKeyData extends EventMetaData {
        keyCode: number | undefined;
        charCode: number | undefined;
        key: string | undefined;
    }

    interface EventMouseData extends EventMetaData {
        pageX: number | undefined;
        pageY: number | undefined;
    }

    interface AlertOptions {
        mode: 'os' | 'modal' | 'embedded' | 'overlapped' | 'popup' | 'highlighted';
        title: string;
        icon: 'QUESTION' | 'EXCLAMATION' | 'INFORMATION' | 'ERROR' | 'none' | string;
        button: string | Record<string, unknown>;
        desktop: zk.Desktop;
    }
}

// extension of JQuery
interface JQuery {
    selector?: string; // expose
    zk: zk.JQZK;

    after(widget: zk.Widget, dt?: zk.Desktop): this;
    append(widget: zk.Widget, dt?: zk.Desktop): this;
    before(widget: zk.Widget, dt?: zk.Desktop): this;
    prepend(widget: zk.Widget, dt?: zk.Desktop): this;
}

declare namespace JQuery {
    interface Event {
        stop(): void;
        mouseData(): zk.EventMouseData;
        keyData(): zk.EventKeyData;
        metaData(): zk.EventMetaData;
    }

    interface EventStatic {
        filterMetaData(data: Record<string, unknown>): zk.EventMetaData;
        fire(el: Element, evtnm: string): void;
        stop(evt: Event): void;
        zk(evt: Event, wgt: zk.Widget): zk.Event;
    }
}

declare var jq: zk.JQueryStaticExtension;
