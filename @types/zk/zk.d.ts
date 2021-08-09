/* zk.d.ts

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
    type Class = any;
    type $void = () => false;

    interface ObjectStatic {
        isAssignableFrom(cls: Class): boolean;
        isInstance(o: any): boolean;
        prototype: zk.Object;
    }

    interface Object {
        _$ais: (() => void)[] | null;
        _$proxies: WeakMap<Function, Function>;
        _$super?: zk.Object;
        _$supers?: Record<string, zk.Object | undefined>;
        $class: Class;
        $copyf: Class;
        $copied: boolean;
        $oid: number;

        new (...args: unknown[]): this;
        $init(value?: number | string): void;
        $instanceof(klass: Class): boolean;
        $super(klass: Class, mtd: string, ...vararg: any[]): any;
        $super(mtd: string, ...vararg: any[]): any;
        $supers(klass: Class, mtd: string, args: ArrayLike<any>): any;
        $supers(mtd: string, args: ArrayLike<any>): any;
        afterInit(func: (this: this) => void): void;
        // ref: https://github.com/Microsoft/TypeScript/pull/27028
        proxy<A extends any[], R>(func: (...args: A) => R): (...args: A) => R;
        proxy<A0, A extends any[], R>(func: (arg0: A0, ...args: A) => R): (arg0: A0, ...args: A) => R;
        proxy<A0, A1, A extends any[], R>(func: (arg0: A0, arg1: A1, ...args: A) => R): (arg0: A0, arg1: A1, ...args: A) => R;
        proxy<A0, A1, A2, A extends any[], R>(func: (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R;
        proxy<A0, A1, A2, A3, A extends any[], R>(func: (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R;
        proxy<AX, R>(func: (...args: AX[]) => R): (...args: AX[]) => R;
    }

    interface Skipper extends Object {
        restore(wgt: Widget, inf: any): void;
        skip(wgt: Widget, skipId?: string): HTMLElement;
        skipped(wgt: Widget, child: Widget): boolean;

        nonCaptionSkipper: this;
    }

    type DesktopAccessor = string | Widget | HTMLElement | null;
    interface DesktopStatic {
        _dt: zk.Desktop | null;
        _ndt: number;
        all: Record<string, zk.Desktop>;

        $(dtid?: DesktopAccessor): zk.Desktop | null;
        isInstance(dtid: DesktopAccessor): boolean;
        sync(timeout?: number): zk.Desktop | null;
        new (dtid: string, contextURI?: string, updateURI?: string, reqURI?: string, stateless?: boolean): zk.Desktop;
    }

    interface Desktop extends Widget {
        _aureqs: zk.Event[];
        _pfDoneIds: string | null;
        _pfRecvIds: string | null;
        bindLevel: number;
        className: 'zk.Desktop';
        contextURI: string;
        obsolete: boolean;
        requestPath: string;
        resourceURI: string;
        stateless: boolean;
        updateURI: string;
        widgetName: 'desktop';
        z_virnd: true;

        new (dtid: string, contextURI: string, updateURI: string, resourceURI: string,
             reqURI: string, stateless: boolean): Desktop;
    }

    export interface Event {
        auStopped: boolean;
        currentTarget: zk.Widget;
        data: any;
        domEvent: JQuery.Event;
        domStopped: boolean;
        domTarget: HTMLElement;
        name: string;
        opts: Partial<EventOptions>;
        stopped: boolean;
        target: zk.Widget;
        [dataKey: string]: any; // If data is an instance of Map, its content is copied to the event instance.

        new(target: zk.Widget | null, name: string, data?: any,
            opts?: Partial<EventOptions> | null,
            domEvent?: JQuery.Event): Event;
        addOptions(opts: Partial<EventOptions>): void;
        stop(opts?: Partial<EventStopOptions>): void;
    }

    interface EventOptions {
        implicit: boolean;
        ignorable: boolean;
        toServer: boolean;
        uri: string;
        defer: boolean;
        serverAlive: boolean;
        forceAjax: boolean;
        rtags: {[key: string]: any};
        start: {
            time: number | Date;
            coords: [number, number];
        };
        stop: {
            time: number | Date;
            coords: [number, number];
        };
        dir: string;
    }

    interface EventStopOptions {
        revoke: boolean;
        propagation: boolean;
        dom: boolean;
        au: boolean;
    }

    interface Widget extends Object {
        _scrollbar?: any;
        $weave: any;
        autag: string;
        bindLevel: number;
        className: string;
        desktop: Desktop;
        effects_: Record<string, zk.Effect>;
        firstChild: Widget | null;
        id: string;
        insertingBefore_: boolean;
        inServer: boolean;
        lastChild: Widget | null;
        nChildren: number;
        nextSibling: Widget | null;
        parent: Widget | null;
        previousSibling: Widget | null;
        uuid: string;
        widgetName: string;
        z_rod?: boolean | number;

        $binder(): zk.Binder | null;
        $f(): {[id: string]: Widget};
        $f(id: string, global?: boolean): Widget;
        $init(props?: {[prop: string]: any}): void;
        $n(): HTMLElement;
        $n(subId: string): HTMLElement;
        $o(): Widget;
        $s(): string;
        $s(subId: string): string;
        $service(): Service | null;
        afterAnima_(visible: boolean): void;
        afterParentChanged_(oldparent: Widget): void;
        appendChild(child: Widget, ignoreDom?: boolean): boolean;
        beforeParentChanged_(newparent: Widget): void;
        beforeSendAU_(wgt: Widget, evt: Event): void;
        bind_(dt?: Desktop| null, skipper?: Skipper | null, after?: (() => void)[]): void;
        bind(dt?: Desktop| null, skipper?: Skipper | null): void;
        bindChildren_(dt?: Desktop| null, skipper?: Skipper| null, after?: (() => void)[]): void;
        bindDoubleTap_(): void;
        bindSwipe_(): void;
        bindTapHold_(): void;
        canActivate(opts?: {checkOnly?: boolean}): boolean;
        cleanDrag_(): void;
        clear(): void;
        clearCache(): void;
        cloneDrag_(drag: any, ofs: zk.Offset): HTMLElement;
        deferRedraw_(out: string[]): void;
        deferRedrawHTML_(out: string[]): void;
        detach(): void;
        doBlur_(evt: Event): void;
        doClick_(evt: Event): void;
        doDoubleClick_(evt: Event): void;
        doFocus_(evt: Event): void;
        doKeyDown_(evt: Event): void;
        doKeyPress_(evt: Event): void;
        doKeyUp_(evt: Event): void;
        domAttrs_(no?: {[noAttr: string]: boolean}): string;
        domClass_(no?: {[noAttr: string]: boolean}): string;
        domListen_(node: Element, evtnm: string, fn?: any, keyword?: string): Widget;
        doMouseDown_(evt: Event): void;
        doMouseMove_(evt: Event): void;
        doMouseOut_(evt: Event): void;
        doMouseOver_(evt: Event): void;
        doMouseUp_(evt: Event): void;
        domStyle_(no?: {[noAttr: string]: boolean}): string;
        domTextStyleAttr_(): string;
        domTooltiptext_(): string;
        domUnlisten_(node: Element, evtnm: string, fn?: any, keyword?: string): Widget;
        doPaste_(evt: Event): void;
        doResizeScroll_(): void;
        doRightClick_(evt: Event): void;
        doSelect_(evt: Event): void;
        doSwipe_(evt: Event): void;
        doTooltipOut_(evt: Event): void;
        doTooltipOver_(evt: Event): void;
        dropEffect_(over: boolean): void;
        extraBind_(uuid: string, add: boolean): void;
        fire(evtnm: string, data?: any, opts?: any, timeout?: number): Event;
        fireX(evt: Event, timeout?: number): Event;
        focus_(timeout: number): boolean;
        focus(timeout?: number): boolean;
        forcerender(): void;
        fromPageCoord(x: number, y: number): zk.Offset;
        get(name: string): any;
        getAction(): string;
        getCaveNode(): HTMLElement;
        getChildAt(j: number): Widget;
        getChildIndex(): number;
        getClass(wgtnm: string): any;
        getDraggable(): string;
        getDragMessage_(): string;
        getDragNode(): HTMLElement;
        getDragOptions_(map: any): any; // TODO
        getDrop_(dragged: Widget): Widget;
        getDroppable(): string;
        getFirstNode_(): HTMLElement;
        getFloatZIndex_(node: Element): number | string;
        getHeight(): string;
        getHflex(): string;
        getId(): string;
        getLeft(): string;
        getMold(): string;
        getOldWidget_(n: Element | string): Widget;
        getPage(): any;
        getRenderdefer(): number;
        getSclass(): string;
        getScrollLeft(): number;
        getScrollTop(): number;
        getStyle(): string;
        getTabindex(): number;
        getTextNode(): HTMLElement | null;
        getTooltiptext(): string;
        getTop(): string;
        getTopWidget(): Widget | null;
        getVflex(): string;
        getWidth(): string;
        getZclass(): string;
        getZIndex(): number;
        hide(): Widget;
        ignoreDescendantFloatUp_(): boolean;
        ignoreDrag_(pt: any): boolean;
        initDrag_(): void;
        insertBefore(child: Widget, sibling: Widget | null): boolean;
        insertChildHTML_(child: Widget, before: Widget | null, desktop: Desktop): void;
        isBinding(): boolean;
        isFloating_(): boolean;
        isListen(evtnm: string, opts?: {any?: boolean; asapOnly?: boolean}): boolean;
        isRealElement(): boolean;
        isRealVisible(opts?: {dom?: boolean; until?: Widget; strict?: boolean; cache?: any}): boolean;
        isVisible(strict?: boolean): boolean;
        isWatchable_(name: string, p: Widget, cache: any): boolean;
        listen(infos: {[event: string]: any}, priority?: number): Widget;
        listenOnFitSize_(): void;
        onAfterSize(): void;
        onChildAdded_(child: Widget): void;
        onChildRemoved_(child: Widget): void;
        onChildRenderDefer_(child: Widget): void;
        onChildReplaced_(oldc: Widget, newc: Widget): void;
        onChildVisible_(child: Widget): void;
        onDrop_(drag: any, evt: Event): void;
        redraw(out: string[]): void;
        redrawHTML_(skipper?: Skipper | null, trim?: boolean): string;
        removeChild(child: Widget, ignoreDom?: boolean): boolean;
        removeChildHTML_(child: Widget, ignoreDom?: boolean): void;
        removeHTML_(n?: any[]): void;
        replaceCavedChildren_(subId: string, wgts: Widget[], tagBeg: string, tagEnd: string): void;
        replaceChildHTML_(child: Widget, n: Element, dt: Desktop | null, skipper: Skipper | null, _trim_?: boolean): void;
        replaceHTML(n: Element | string, desktop: Desktop | null, skipper?: Skipper | null, _trim_?: boolean, _callback_?: (() => void)[]): Widget;
        replaceWidget(newwgt: Widget): void;
        rerender(timeout?: number): Widget;
        rerender(skipper?: Skipper | null): Widget;
        rerenderLater_(): Widget;
        rerenderNow_(skipper?: Skipper | null): void;
        scrollIntoView(): Widget;
        sendAU_(evt: Event, timeout: number): void;
        set(name: string, value: any, extra?: any): Widget;
        setAction(action: string): void;
        setChildren(children: Widget[]): Widget;
        setDomVisible_(n: Element, visible: boolean, opts?: Partial<{display: boolean; visibility: boolean}>): void;
        setDraggable(draggable: string): Widget;
        setDroppable(droppable: string): Widget;
        setFloating_(floating: boolean, opts?: {node?: Element}): Widget;
        setFloatZIndex_(node: Element, zi: number): void;
        setHeight(height: string): Widget;
        setHflex(flex: string): void;
        setId(id: string): Widget;
        setLeft(left: string): Widget;
        setListener(inf: [string, ((evt: Event) => void) | string | null]): void;
        setListener(evt: string, fn: ((evt: Event) => void) | string | null): void;
        setListeners(infos: {[event: string]: ((evt: Event) => void) | string | null}): void;
        setMold(mold: string): Widget;
        setRenderdefer(ms: number): void;
        setSclass(sclass: string): Widget;
        setScrollLeft(left: number): Widget;
        setScrollTop(top: number): Widget;
        setStyle(style: string): Widget;
        setTabindex(tabindex: number): void;
        setTooltiptext(title: string): Widget;
        setTop(top: string): Widget;
        setTopmost(): number;
        setVflex(flex: string): void;
        setVisible(visible: boolean): Widget;
        setWidth(widsth: string): Widget;
        setZclass(zclass: string): Widget;
        setZIndex(zIndex: number, opts?: {}): Widget;
        shallChildROD_(): boolean;
        shallFireSizedLaterWhenAddChd_(): boolean;
        shallIgnoreClick_(evt: Event): boolean;
        show(): Widget;
        smartUpdate(name: string, value: any, timeout?: number): Widget;
        unbind_(skipper?: Skipper | null, after?: (() => void)[], keepRod?: boolean): void;
        unbind(skipper?: Skipper | null, keepRod?: boolean): Widget;
        unbindChildren_(skipper?: Skipper, after?: (() => void)[], keepRod?: boolean): void;
        unbindDoubleTap_(): void;
        unbindSwipe_(): void;
        unbindTapHold_(): void;
        uncloneDrag_(drag: any): void;
        unlisten(infos: {[event: string]: any}): Widget;
        unlistenOnFitSize_(): void;
        updateDomClass_(): void;
        updateDomStyle_(): void;
        zsync(opts?: {}): void;

        [key: string]: any;
    }

    interface Page extends zk.Widget {
        className: 'zk.Page';
        contained: Page[];
        widgetName: 'page';
        z_virnd: true;
    }

    interface NativeStatic {
        new (): Native;
        $redraw(out: zk.Buffer): void;
        replaceScriptContent(str: string): string;
    }

    interface Native extends zk.Widget {
        className: 'zk.Native';
        widgetName: 'native';
        z_virnd: true;

        $redraw(out: zk.Buffer): void;
        replaceScriptContent(str: string): string;
    }

    interface Macro extends zk.Widget {
        className: 'zk.Macro';
        widgetName: 'macro';
    }

    interface NoDOM {
        bind_(this: zk.Widget): void;
        removeHTML_(this: zk.Widget, n: HTMLElement): void;
        setDomVisible_(this: zk.Widget, n: Element, visible: boolean, opts?: Partial<{display: boolean; visibility: boolean}>): void;
        isRealVisible(this: zk.Widget): void;
        getFirstNode_(this: zk.Widget): void;
        insertChildHTML_(this: zk.Widget, child: Widget, before: Widget | null, desktop: Desktop): void;
        detach(this: zk.Widget): void;
        getOldWidget_(this: zk.Widget, n: Element): void;
        replaceHTML(this: zk.Widget, n: Element | string, desktop: Desktop | null, skipper: Skipper | null, _trim_?: boolean, _callback_?: (() => void)[]): void;
        replaceWidget(this: zk.Widget, newwgt: zk.Widget): void;
        $n(this: zk.Widget, subId?: string): void;
        redraw(this: zk.Widget, out: zk.Buffer): void;
        ignoreFlexSize_(this: zk.Widget): void;
        ignoreChildNodeOffset_(this: zk.Widget): void;
        isExcludedHflex_(this: zk.Widget): void;
        isExcludedVflex_(this: zk.Widget): void;
    }

    interface Effect extends Object {
        destroy(): void;
    }

    interface WidgetStatic extends ObjectStatic {
        new (): Widget;
        auDelay: number;

        $(n: any, opts?: any): Widget;
        getClass(wgtnm: string): any;
        getElementsById(id: string): any[];
        getElementsByName(name: string): any[];
        /** @deprecated */ isAutoId(uuid: string): boolean;
        mimicMouseDown_(wgt: Widget, noFocusChange: boolean, which: number): void;
        newInstance(wgtnm: string, props?: any): Widget;
        nextUuid(): string;
        register(clsnm: string, blankPreserved: boolean): void;
        uuid(subId: string): string;
    }

    interface WidgetUtil {
        autohide(): void;
        replace(from: zk.Widget, to: zk.Widget, kids: boolean): void;
        setUuid(wgt: zk.Widget, uuid: string): void;
    }

    type DataHandler = (wgt: zk.Widget, val: unknown) => void;

    interface DragDrop {
        getDropTarget(evt: zk.Event, drag?: any): zk.Widget | null;
        getDrop(drag: any, pt: zk.Offset, evt: zk.Event): zk.Widget | null;
        ghost(drag: any, ofs: zk.Offset, msg?: string): HTMLElement;
    }

    interface ZKServiceStatic {
        $(n: any, opts?: any): Service | null;
    }

    interface Service extends Object {
        new (widget: zk.Widget, currentTarget: zk.Widget): this;
        $doAfterCommand(cmd: string, args: any[]): void;
        after(cmd: string, fn: (...args: any[]) => void): this;
        command(cmd: string, args: any[], opts: Partial<EventOptions>, timeout?: number): this;
        destroy(): void;
        unAfter(cmd: string, fn: (...args: any[]) => void): this;
    }

    interface RefWidget extends Widget {
        new (): this;
        className: 'zk.RefWidget';
        widgetName: 'refWidget';
    }

    interface HistoryState {
        enabled: boolean;
        onPopState(event: PopStateEvent): void;
        register(): void;
    }

    interface DraggableStatic {
        new (control, node, opts): Draggable;
        ignoreClick(): boolean;
        ignoreMouseUp(): boolean;
        /** @deprecated */ ignoreStop(target: HTMLElement): boolean;
    }

    interface Draggable extends Object {
        control: any;
        handle?: HTMLElement | null;
        node?: HTMLElement | null;
        _isScrollChild: boolean;
        delta: zk.Offset;
        opts: any;
        dragging?: boolean;
        _suicide: boolean;
        dead: boolean;
        offset: number[];
        lastScrolled: Date;
        scrollSpeed: number[];
        
        $init(control?: any, node?: HTMLElement, opts?: any): void;
        _currentDelta(): zk.Offset;
        _draw(point, evt?: zk.Event): void;
        _endDrag(evt: zk.Event): void;
        _finishDrag(evt: zk.Event, success): void;
        _getWndScroll(w): zk.Dimension;
        _keypress(devt: jQuery.Event): void;
        _mousedown(devt: jQuery.Event): void;
        _scroll(): void;
        _startDrag(evt: zk.Event): void;
        _startScrolling(speed): void;
        _stopScrolling(): void;
        _syncStackup(): void;
        _updateDrag(pt, evt: zk.Event): void;
        _updateInnerOfs(): void;
        destroy(): void;
        snap_(pos: zk.Offset, opts): zk.Offset;
    }

    interface Canvas {
        create(width?: number, height?: number): HTMLCanvasElement;
    }

    interface UploadUtils {
        ajaxUpload(wgt: zk.Widget, xhr: XMLHttpRequest, formData: FormData, sid?: number): void;
    }

    interface XMLUtils {
        loadXML(url: string, callback: Function): any;
        parseXML(text: string): any;
        renType(url: string, type: string): string;
        getElementValue(el: HTMLElement): string;
    }

    interface BigDecimal extends zk.Object {
        $init(value: number | string): void;
        $toNumber(): number;
        $toString(): string;
        $toLocaleString(): string;
    }

    interface Long extends zk.Object {
        _value: string;
        $init(value: number | string): void;
        scale(digits: number): void;
        $toNumber(): number;
        $toString(): string;
        $toLocaleString(): string;
    }

    interface Swipe extends zk.Object {
        widget: zk.Widget | null;
        node: HTMLElement | null;
        opts: any;

        $init(widget: zk.Widget, node?: HTMLElement | null, opts?): void;
        destroy(node: HTMLElement): void;
        _swipeStart(devt: JQuery.Event): void;
        _swipeMove(devt: JQuery.Event): void;
        _swipeEnd(devt: JQuery.Event): void;
    }

    interface Parser {
        create(parent: zk.Widget | null, doc: string, args, fn: Function): any;
        createAt(node: string, opts, args, fn: Function): any;
    }

    interface Eff {
        shallStackup(): boolean;
        _skuOpts(opts): any;
        _onVParent(evt, opts?): void;

        Shadow?: zk.Effect;
        FullMask?: zk.Effect;
        Mask?: zk.Effect;
        Actions?: zk.EffectActions;
        KeyboardTrap?: zk.Effect;
        Opacity?: any;
        Move?: any;
    }

    interface EffectActions {
        slideDown(n: HTMLElement, opts?: Partial<SlideOptions>): void;
        slideUp(n: HTMLElement, opts?: Partial<SlideOptions>): void;
        slideIn(n: HTMLElement, opts?: Partial<SlideOptions>): void;
        slideOut(n: HTMLElement, opts?: Partial<SlideOptions>): void;
    }

    interface ZKCoreUtilityStatic {
        _avoidRod?: boolean;
        _anique: Record<string, Anima[]>;
        _cfByMD?: boolean;
        _crWgtUuids: string[];
        _focusByClearBusy: boolean;
        _Erbx: any;
        _isReloadingInObsolete: boolean;
        _noESC: number;
        _prevFocus?: zk.Widget | null;
        _wgtutl: zk.WidgetUtil;
        agent: string;
        air: boolean;
        alerting: boolean;
        android: boolean;
        appName: string;
        ausending: boolean;
        BigDecimal: zk.BigDecimal;
        bmk: Record<string, any>;
        Buffer: Buffer;
        Body: Page;
        booted: boolean;
        build: string;
        busy: number;
        canvas: {
            Canvas: zk.Canvas;
        };
        cfrg?: [number, number];
        chrome?: boolean;
        Class: zk.Class;
        classes: Record<number, unknown>;
        clickPointer: zk.Offset;
        clientinfo?: Record<string, unknown>;
        confirmClose?: string;
        contextURI: string;
        cpsp: CPSP;
        css3?: boolean;
        currentFocus?: zk.Widget | null;
        currentModal?: zk.Widget | null;
        currentPointer: zk.Offset;
        dataHandlers?: Record<string, string | DataHandler>;
        debug: {
            Debugger: zk.Object;
            DefaultHandler: zk.Object;
        };
        debugJS: boolean;
        Desktop: zk.DesktopStatic;
        delayQue: Record<string, Function[]>;
        DECIMAL: string;
        DnD: DragDrop;
        Draggable: DraggableStatic;
        dragging?: boolean;
        Event: zk.Event;
        edge?: string | false;
        edge_legacy?: number | string | false;
        eff: zk.Eff;
        feature: {
            standard: true;
            pe?: boolean;
            ee?: boolean;
        };
        ff?: number | string | false;
        focusBackFix?: boolean;
        fmt: {
            Text: {
                format(msg: string): string;
                formatFileSize(bytes: number): string;
            },
            Number: {
                _escapeQuote(fmt: string, localizedSymbols: any): Record<string, any>;
                _extraFmtIndex(fmt: string): number;
                _removePrefixSharps(pre: string, localizedSymbols: any): string;
                format(fmt: string, val: string, rounding: number, localizedSymbols: any): string;
                isRoundingRequired(val: string | number, fmt: string, localizedSymbols: any): boolean;
                rounding(valStr: string, ri: number, rounding: number, minus: boolean): string;
                setScale(val: BigDecimal, scale: number, rounding: number): string | BigDecimal;
                unformat(fmt: string, val: string, ignoreLocale: boolean, localizedSymbols: any);
            }
        };
        gapi: GApi;
        gecko?: number | string | false;
        GROUPING: string;
        groupingDenied?: boolean;
        historystate: HistoryState;
        ie?: number;
        ie6?: boolean;
        ie6_?: boolean;
        ie7?: boolean;
        ie7_?: boolean;
        ie8?: boolean;
        ie8_?: boolean;
        ie8c?: boolean;
        ie9?: boolean;
        ie9_?: boolean;
        ie10?: boolean;
        ie10_?: boolean;
        ie11?: boolean;
        ie11_?: boolean;
        iex?: number | string | false;
        ios: string | boolean;
        ipad: string | boolean;
        isTimeout: boolean;
        keepDesktop?: boolean;
        keyCapture?: zk.Widget | null;
        linux: boolean;
        loading: number;
        Long: zk.Long;
        Macro: Macro;
        MINUS: string;
        mac: boolean;
        mobile: string | boolean;
        mounting: boolean;
        mouseCapture?: zk.Widget | null;
        mm: any;
        Native: NativeStatic;
        NoDOM: NoDOM;
        Object: ObjectStatic;
        opera?: number | string | false;
        Page: zk.Page;
        PER_MILL: string;
        PERCENT: string;
        pfmeter: boolean;
        pi?: number;
        portlet2Data?: Record<string, Portlet2Data>;
        procDelay: number;
        processing: boolean;
        processMask?: boolean;
        progPos?: string;
        RefWidget: RefWidget;
        resendTimeout: number;
        resourceURI: string;
        safari?: boolean;
        scriptErrorHandler?: (evt) => void;
        scriptErrorHandlerEnabled?: boolean;
        scriptErrorHandlerRegistered?: boolean;
        Service: zk.Object;
        Skipper: zk.Skipper;
        Swipe: zk.Swipe;
        rmDesktoping: boolean;
        skipBfUnload: boolean;
        spaceless: boolean;
        timeout: number;
        timerAlive: boolean;
        tipDelay: number;
        unloading: boolean;
        updateURI: string;
        UploadUtils: zk.UploadUtils;
        useStackup?: string | boolean;
        vendor: string;
        vendor_: string;
        version: string;
        visibilitychange: boolean;
        webkit?: boolean;
        Widget: WidgetStatic;
        wgt: {
            WidgetInfo: {
                all: any;
                getClassName(wgtnm: string): string;
                register(infs: string[]): void;
                loadAll(f: (() => void), weave: boolean): void;
            }
        };
        xhrWithCredentials: boolean;
        xml: {
            Utl: zk.XMLUtils;
        };
        zuml: {
            Parser: zk.Parser;
        };

        (selector: string): JQZK;
        (element: Element | Node | null): JQZK;
        (elementArray: Element[]): JQZK;
        (object: JQuery | JQuery<Element>): JQZK;
        (wgt: Widget): JQZK;

        _apac(fn: () => void, _which_?: string): void;
        _set(o, name: string, value, extra?): void;
        _set2(o, mtd: CallableFunction | null, name: string | null, value, extra?): void;
        $(n: any, opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): zk.Widget | null;
        $default<T>(opts: any, defaults: T): T;
        $extends<S extends Class, D, D2>(superclass: S, members: D & ThisType<D & (S extends zul.WidgetStatic ? zul.Widget : Widget)>, staticMembers?: D2): any;
        $import(name: string, fn?: any): any;
        $intercepts(targetClass: Class, interceptor: any): void;
        $package(name: string, end?: boolean, wv?: boolean): any;
        $void: $void;
        addDataHandler(name: string, script: string): void;
        afterAnimate(fn: () => void, delay?: number): boolean;
        afterAuResponse(fn: () => void): void;
        afterLoad(func: () => void): boolean;
        afterLoad(pkgs: string, func: () => void, front?: boolean): void;
        afterMount(fn: () => void, delay?: number): boolean;
        afterResize(fn: () => void): void;
        ajaxResourceURI(uri: string, version?: string, opts?: any): string;
        ajaxURI(uri: string | null, opts?: any): string;
        animating(): boolean;
        beforeUnload(fn: () => string | null, opts?: {remove: boolean}): void;
        copy<T>(dst: T, src: ThisType<T>, backup?: object): object;
        cut(props: any, nm: string): object | undefined;
        debugLog(msg: string): void;
        define(klass: any, props: any): any;
        delayFunction(uuid: string, func: () => void, opts?: Partial<{ timeout: number; urgent: boolean }>): void;
        depends(a: string, b: string): void;
        disableESC(): void;
        doAfterAuResponse(): void;
        doAfterResize(): void;
        enableESC(): void;
        endProcessing(sid?: number): void;
        error(msg: string): void;
        errorDismiss(): void;
        get(o: any, name: string): any;
        getDataHandler(name: string): any;
        getHost(pkg: string, js: boolean): string;
        getVersion(pkg: string): string;
        hasDataHandler(name: string): boolean;
        isClass(cls: any): boolean;
        isLoaded(pkg: string, loading?: boolean): boolean;
        isObject(o: any): boolean;
        load(pkg: string, dt: any, func: Function): boolean;
        load(pkg: string, func: Function): boolean;
        load(pkg: string): boolean;
        _load(pkg: string, dt: any): boolean;
        loadCSS(href: string, id?: string, media?: string): ZKCoreUtilityStatic;
        loadScript(src: string, name?: string, charset?: string, force?: boolean): ZKCoreUtilityStatic;
        log(...detailed: any[]): void;
        override<T>(oldfunc: T, newfunc: Function & ThisType<T>): T;
        override<T>(dst: T, backup: any, src: ThisType<T>): T;
        override<T>(dst: T, nm: string, val: ThisType<T>): T;
        parseFloat(v: string | number): number;
        parseInt(v: string | number, b?: number): number;
        set(dst: any, src: any, props: any[], ignoreUndefined: boolean): any;
        set(o: any, name: string, value: any, extra: any): any;
        setHost(host: string, updURI: string, pkgs: string[]): void;
        setLoaded(pkg: string): void;
        setScriptLoaded(name: string): void;
        setVersion(pkg: string, ver: string): void;
        stamp(name?: string, noAutoLog?: boolean): void;
        startProcessing(timeout: number, sid?: number): void;
        stateless(dtid?: string, contextURI?: string, updateURI?: string, reqURI?: string): any;
        _zsyncFns(name: string, org: any): void;
    }

    interface Anima {
        anima: string;
        el: HTMLElement;
        wgt: zk.Widget;
        opts: Record<string, unknown>;
    }

    interface Buffer extends Array<string> {
        new (): Buffer;
    }

    interface PositionOptions {
        overflow: boolean;
        dodgeRef: boolean;
    }

    interface RedoCSSOptions {
        fixFontIcon: boolean;
        selector: string;
    }

    interface SlideOptions {
        anchor: string;
        easing: string;
        duration: number;
        afterAnima: () => void;
    }

    interface Dimension {
        width: number;
        height: number;
        left: number;
        top: number;
    }

    interface Portlet2Data {
        namespace: string;
        resourceURL: string;
    }

    interface JQZK {
        jq: JQuery;

        _createWrapper(element: JQuery): JQuery;
        _removeWrapper(element: JQuery): JQuery;
        $(): Widget;
        absolutize(): this;
        beforeHideOnUnbind(): void;
        borderHeight(): number;
        borderWidth(): number;
        cellIndex(): number;
        center(flags?: string): this;
        cleanVisibility(): JQuery;
        clearStyles(): this;
        clientHeightDoubleValue(): number;
        clientWidthDoubleValue(): number;
        cmOffset(): zk.Offset;
        contentHeight(excludeMargin?: boolean): number;
        contentWidth(excludeMargin?: boolean): number;
        defaultAnimaOpts(wgt: zk.Widget, opts: Partial<SlideOptions>, prop: string[], visible?: boolean): this;
        detachChildren(): HTMLElement[] | null;
        dimension(revised?: boolean): Dimension;
        disableSelection(): this;
        enableSelection(): this;
        focus(timeout?: number): boolean;
        getAnimationSpeed(defaultValue?: 'slow' | 'fast' | number): 'slow' | 'fast' | number;
        getSelectionRange(): [number, number];
        hasHScroll(): boolean;
        hasVParent(): boolean;
        hasVScroll(): boolean;
        isInput(): boolean;
        isOverlapped(el: HTMLElement, tolerant?: number): boolean;
        isRealScrollIntoView(): boolean;
        isRealVisible(strict?: boolean): boolean;
        isScrollIntoView(recursive?: boolean): boolean;
        isVisible(strict?: boolean): boolean;
        makeVParent(): this;
        marginHeight(): number;
        marginWidth(): number;
        ncols(visibleOnly?: boolean): number;
        offsetHeight(): number;
        offsetHeightDoubleValue(): number;
        offsetLeft(): number;
        offsetLeftDoubleValue(): number;
        offsetTop(): number;
        offsetTopDoubleValue(): number;
        offsetWidth(): number;
        offsetWidthDoubleValue(): number;
        padBorderHeight(): number;
        padBorderWidth(): number;
        paddingHeight(): number;
        paddingWidth(): number;
        position(dim?: Dimension, where?: string, opts?: Partial<PositionOptions>): this;
        position(el?: Element, where?: string, opts?: Partial<PositionOptions>): this;
        redoCSS(timeout?: number, opts?: Partial<RedoCSSOptions>): this;
        redoSrc(): this;
        relativize(): this;
        revisedHeight(size: number, excludeMargin?: boolean): number;
        revisedOffset(ofs?: zk.Offset): zk.Offset;
        revisedWidth(size: number, excludeMargin?: boolean): number;
        scrollIntoView(parent?: Element): this;
        scrollOffset(): zk.Offset;
        scrollTo(): this;
        select(timeout?: number): boolean;
        setSelectionRange(start: number, end?: number): this;
        /** @deprecated */ setStyles(styles: JQuery.PlainObject<string | number | ((this: HTMLElement, index: number, value: string) => string | number | void | undefined)>): this;
        slideDown(wgt: Widget, opts?: Partial<SlideOptions>): this;
        slideIn(wgt: Widget, opts?: Partial<SlideOptions>): this;
        slideOut(wgt: Widget, opts?: Partial<SlideOptions>): this;
        slideUp(wgt: Widget, opts?: Partial<SlideOptions>): this;
        submit(): this;
        sumStyles(areas: string, styles: {[cssProp: string]: string}): number;
        textSize(text?: string): [number, number];
        textWidth(text?: string): number;
        toStyleOffset(x: number, y: number): zk.Offset;
        undoVParent(): this;
        vflexHeight(): number;
        viewportOffset(): zk.Offset;
        vparentNode(real?: boolean): HTMLElement;
    }

    interface CPSP {
        SPush: SPush;
        start(dtid: string, min: number, max: number, factor: number): void;
        stop(dtid: string): void;
    }

    interface SPush extends Object {
        _do(): void;
        start(dt: Desktop, min: number, max: number, factor: number): void;
        stop(): void;
    }

    interface GApi {
        GOOGLE_API_LOADING_TIMEOUT: number;
        loadAPIs(wgt: Widget, callback: (() => void), msg: string, timeout: number): void;
        waitUntil(wgt: Widget, opts: any): void;
    }
}

declare var zk: zk.ZKCoreUtilityStatic;
