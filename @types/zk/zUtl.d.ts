/* zUtl.d.ts

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
    interface EncodeXmlOptions {
        pre: boolean;
        multiline: boolean;
        maxlength: number;
    }
    
    interface GoOptions {
        target: string;
        overwrite: boolean;
    }
    
    interface IsCharOptions {
        digit: boolean | number;
        upper: boolean | number;
        lower: boolean | number;
        whitespace: boolean | number;
        [char: string]: boolean | number | undefined;
    }
    
    interface ProgressboxOptions {
        busy: boolean;
    }
    
    interface ZUtl {
        cellps0: string;
        i0: string;
        img0: string;
    
        appendAttr(nm: string, val: any, force?: boolean): string;
        convertDataURLtoBlob(dataURL: string): Blob;
        decodeXML(txt: string): string;
        destroyProgressbox(id: string, opts?: Partial<ProgressboxOptions>): void;
        encodeXML(txt: string, opts?: Partial<EncodeXmlOptions>): string;
        encodeXMLAttribute(txt: string): string;
        fireShown(wgt: zk.Widget, bfsz?: number): void;
        fireSized(wgt: zk.Widget, bfsz?: number): void;
        frames(w: Window): Window[];
        getDevicePixelRatio(): number;
        getUserMedia(constraints: MediaStreamConstraints): Promise<MediaStream>;
        throttle<T, A extends any[], R>(func: (this: T, ...args: A) => R, wait: number): (this: T, ...args: A) => R;
        debounce<T, A extends any[], R>(func: (this: T, ...args: A) => R, wait: number, immediate?: boolean): (this: T, ...args: A) => R;
        getWeekOfYear(year: number, month: number, date: number, firstDayOfWeek: number, minimalDaysInFirstWeek: number): number;
        go(url: string, opts?: Partial<GoOptions>): void;
        intsToString(ary: number[]): string;
        isAncestor(p: zk.Widget, c: zk.Widget): boolean;
        isChar(cc: string, opts: Partial<IsCharOptions>): boolean;
        isImageLoading(): boolean;
        loadImage(url: string): void;
        mapToString(map: Record<string, string>, assign?: string, separator?: string): string;
        /** @deprecated */ now(): number;
        parseMap(text: string, separator?: string, quote?: string): {[key: string]: string};
        progressbox(id: string, msg: string, mask?: boolean, icon?: string | null, opts?: Partial<ProgressboxOptions>): void;
        stringToInts(text: string | null, defaultValue: number): number[] | null;
        today(full: boolean | null, tz: string): Date;
        today(fmt: string, tz: string): Date;
        throttle<T, A extends any[], R>(func: (this: T, ...args: A) => R, wait: number): (this: T, ...args: A) => R;
        debounce<T, A extends any[], R>(func: (this: T, ...args: A) => R, wait: number, immediate?: boolean): (this: T, ...args: A) => R;
    }
}

declare var zUtl: zk.ZUtl;
