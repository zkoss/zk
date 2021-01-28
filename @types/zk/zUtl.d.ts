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
        getWeekOfYear(year: number, month: number, date: number, firstDayOfWeek: number, minimalDaysInFirstWeek: number): number;
        go(url: string, opts?: Partial<GoOptions>): void;
        intsToString(ary: number[]): string;
        isAncestor(p: zk.Widget, c: zk.Widget): boolean;
        isChar(cc: string, opts?: Partial<IsCharOptions>): boolean;
        isImageLoading(): boolean;
        loadImage(url: string): void;
        mapToString(map: Record<string, unknown>, assign?: string, separator?: string): string;
        /** @deprecated */ now(): number;
        parseMap(text: string, separator?: string, quote?: string): {[key: string]: string};
        progressbox(id: string, msg: string, mask?: boolean, icon?: string | null, opts?: Partial<ProgressboxOptions>): void;
        stringToInts(text: null, defaultValue: number): null;
        stringToInts(text: string, defaultValue: number): number[];
        today(full: boolean, tz: string): Date;
        today(fmt: string, tz: string): Date;
    }
}

declare var zUtl: zk.ZUtl;
