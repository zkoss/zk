/* debugger.ts

	Purpose:

	Description:

	History:
		Fri Jan 16 10:19:46     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zk.loadCSS(zk.ajaxURI('/web/js/zk/debug/debugger.css', {resource: true}));

function _space(deep: number): string {
    // ZK-5864 Rewrite the <pre> tag to improve readability.
    return '  '.repeat(deep);
}

function _dumpWgt(out: string[], wgt: zk.Widget, nLevel: number, inf: { cnt: number }): void {
    inf.cnt++;
    out.push(_space(nLevel++),
        (wgt.widgetName == 'widget' ? (wgt.$n() ? wgt.$n()!.tagName : wgt.widgetName) : wgt.widgetName),
        (wgt.id ? '$' + wgt.id : '#' + wgt.uuid), '\n');

    for (wgt = wgt.firstChild!; wgt; wgt = wgt.nextSibling!)
        _dumpWgt(out, wgt, nLevel, inf);
}

const _defaultIgnore = {
    draggable: 'false', droppable: 'false', mold: 'default', colspan: 1,
    scrollTop: 0, scrollLeft: 0, innerWidth: '100%', cols: 0, model: true,
    sortDirection: 'natural', sortAscending: 'none', sortDescending: 'none',
    visible: true,
    columnshide: true, columnsgroup: true
};

const _specialIgnore = {
    treecell: {width: 1},
    rows: {visibleItemCount: 1},
    columns: {menupopup: 1},
    treeitem: {image: 1, label: 1, zclass: 1},
    listitem: {label: 1, zclass: 1, checkable: 1},
    include: {content: 1},
    center: {maxsize: 1, minsize: 1, cmargins: 1, margins: 1, open: 1},
    paging: {pageCount: 1}
};

const _noChildable = {
    datebox: 1
};

const attrsLater = {
    getText: 1,
    getMold: 1
};

function _dumpAttrs0(out: string[], nm: string, wgt: zk.Widget): void {
    if (nm.startsWith('get') && nm.length > 3 && !nm.endsWith('_')) {
        const setting = 's' + nm.substring(1),
            widgetName = wgt.widgetName;
        if (typeof wgt[setting] == 'function') {
            const key = nm.charAt(3).toLowerCase() + nm.substring(4);
            try {
                if (_specialIgnore[widgetName] && _specialIgnore[widgetName][key])
                    return;

                const value = wgt[nm]();
                if (typeof value != 'object' && typeof value != 'function' && value != null && value !== '') {
                    if (_defaultIgnore[key] === undefined) {
                        if (key != 'zclass' || value != 'z-' + widgetName) {
                            if (key == 'selectedIndex')
                                out.push(' onCreate="self.selectedIndex = ', value, '"');
                            else
                                out.push(' ', key, '="', zUtl.encodeXML(value), '"');
                        }
                    } else if (_defaultIgnore[key] !== value && value != 'fromServer') {
                        out.push(' ', key, '="', value, '"');
                    }
                }
            } catch (e) {
                zk.debugLog((e as Error).message || e);
            }
        }
    } else if (nm.startsWith('is') && nm.length > 2 && !nm.endsWith('_')) {
        const setting = 'set' + nm.substring(2),
            getting = 'get' + nm.substring(2),
            widgetName = wgt.widgetName;
        if (typeof wgt[setting] == 'function' && typeof wgt[getting] != 'function') {
            const key = nm.charAt(2).toLowerCase() + nm.substring(3);
            try {
                if (_specialIgnore[widgetName] && _specialIgnore[widgetName][key])
                    return;

                const value = wgt[nm]();
                if (typeof value != 'object' && typeof value != 'function' && value != null && value !== '') {
                    if (_defaultIgnore[key] === undefined) {
                        if (key != 'zclass' || value != 'z-' + widgetName) {
                            if (key == 'selectedIndex')
                                out.push(' onCreate="self.selectedIndex = ', value, '"');
                            else
                                out.push(' ', key, '="', zUtl.encodeXML(value), '"');
                        }
                    } else if (_defaultIgnore[key] !== value && value != 'fromServer') {
                        out.push(' ', key, '="', value, '"');
                    }
                }
            } catch (e) {
                zk.debugLog((e as Error).message || e);
            }
        }
    }
}

function _dumpAttrs(wgt: zk.Widget): string {
    const out: string[] = [],
        later: string[] = [];

    for (const nm in wgt) {
        if (attrsLater[nm]) {
            later.push(nm);
            continue;
        }
        _dumpAttrs0(out, nm, wgt);
    }
    for (let i = 0, j = later.length; i < j; i++)
        _dumpAttrs0(out, later[i], wgt);
    return out.join('');
}

function _dumpWgt4Zul(out: string[], wgt: zk.Widget, nLevel: number, inf: { cnt: number }): void {
    inf.cnt++;
    let nm = wgt.widgetName, noChildable: boolean | undefined;

    if (nm == 'native') {
        const node = wgt.$n();
        if (node) {
            nm = 'h:' + node.tagName;
        } else if ((wgt as any).epilog) {
            const epilogMatch = (wgt as any).epilog.match(/<\/?(.+)>/);
            nm = epilogMatch ? 'h:' + epilogMatch[1] : wgt.widgetName;
        } else {
            nm = wgt.widgetName;
        }

        if (nm == 'native' && (wgt as any).prolog) {
            const prolog = (wgt as any).prolog.trim();
            if (prolog) {
                const prologMatch = prolog.match(/<(.+)\/>/);
                nm = prologMatch ? 'h:' + prologMatch[1] : 'h:span';
            } else {
                nm = 'h:span';
            }
        }
    }

    if (nm == 'script' || (nm == 'paging' && wgt.parent?.$instanceof(zul.mesh.MeshWidget)))
        return;
    else if (nm == 'text')
        nm = 'h:' + nm;
    else if (nm == 'select')
        nm = 'listbox';
    else if (nm == 'option') {
        nm = 'listitem';
        const attrs = _dumpAttrs(wgt).replace('mold="select"', '');
        out.push(_space(nLevel++), '&lt;', nm, attrs);
        if (wgt.firstChild && typeof (wgt.firstChild as any).getLabel === 'function') {
            inf.cnt++;
            out.push(' label="', zUtl.encodeXML((wgt.firstChild as any).getLabel()), '"/&gt;\n');
        } else
            out.push('/&gt;\n');
        return;
    } else if (nm == 'calendar' || nm == 'timebox') {
        const pnm = wgt.parent ? wgt.parent.widgetName : '';
        noChildable = _noChildable[pnm];
    }

    let prefix = noChildable || (nm == 'include' || nm == 'page') ? '&lt;!--' : '';

    out.push(_space(nLevel++), prefix + '&lt;', nm, _dumpAttrs(wgt));

    if (wgt.firstChild) {
        out.push('&gt;');
        const isPage = nm == 'page' || wgt.$instanceof(zul.wgt.Include);

        if (isPage)
            out.push('--&gt;');

        out.push('\n');

        for (let child: zk.Widget | undefined = wgt.firstChild; child; child = child.nextSibling)
            _dumpWgt4Zul(out, child, nLevel, inf);

        prefix = isPage ? '&lt;!--' : '';

        out.push(_space(--nLevel), prefix + '&lt;/', nm, '&gt;');
        if (noChildable || nm == 'include' || nm == 'page')
            out.push('--&gt;');

        out.push('\n');
    } else {
        if (nm == 'style') {
            const n = wgt.$n('css');
            if (n)
                out.push('&gt;\n', n.innerHTML, _space(--nLevel), '&lt;/', nm, '&gt;\n');
            else
                out.push('/&gt;\n');
        } else if (noChildable)
            out.push('/&gt;--&gt;\n');
        else
            out.push('/&gt;\n');
        if (nm == 'include' || nm == 'page')
            out.push('--&gt;');
    }
}

interface HTMLHandler {
    comment(deep: number, content: string): void;

    content(deep: number, content: string): void;

    startTag(deep: number, content: string, isSingle: boolean, isEmpty?: boolean): void;

    endTag(deep: number, content: string, isEmpty?: boolean): void;

    error(content: string): void;
}

function _parseHTML(text: string, handler: HTMLHandler): void {
    let begin: number, content: string, deep = 0, empty: boolean | undefined;

    while (text) {
        text = text.trim();
        begin = text.indexOf('<');
        if (begin == 0 && text.startsWith('<!--')) {
            begin = text.indexOf('-->');
            if (begin != -1) {
                handler.comment(deep, text.substring(0, begin + 3));
                text = text.substring(begin + 3);
            }
            if (text.startsWith('</'))
                deep--;
        } else if (begin >= 0 && text.indexOf('</') == begin) {
            const end = text.indexOf('>');
            if (begin != 0) {
                content = text.substring(0, begin);
                handler.content(deep, content);
                deep--;
            }
            content = text.substring(begin, end + 1);
            text = text.substring(end + 1);
            text = text.trim();
            handler.endTag(deep, content, empty);
            empty = false;
            if (text.startsWith('</'))
                deep--;
        } else if (begin > 0) {
            content = text.substring(0, begin);
            handler.content(deep, content);
            text = text.substring(begin);
        } else if (begin == 0) {
            const mid = text.indexOf('>'), end = text.indexOf('/>');

            if (end >= 0 && end < mid) {
                content = text.substring(0, end + 2);
                handler.startTag(deep, content, true);
                text = text.substring(end + 2).trim();
                if (text.startsWith('</'))
                    deep--;
            } else {
                content = text.substring(0, mid + 1);
                text = text.substring(mid + 1).trim();
                empty = text.startsWith('</');
                handler.startTag(deep, content, false, empty);
                if (!empty)
                    deep++;
            }
        } else {
            handler.error(text);
            break;
        }
    }
}

export var Debugger = {
    outId: 'zk_debugger',

    getConsole(): HTMLElement {
        let console = jq(this.outId, zk)[0];
        if (!console) {
            console = document.createElement('div');
            document.body.appendChild(console);
            jq(console).replaceWith('<div id="' + this.outId + '" class="z-debug"></div>');
            console = jq(this.outId, zk)[0];
        }
        return console;
    },

    dumpDomTree(wgt: any, handler?: DefaultHandler): void {
        let text;
        if (wgt && typeof wgt.$instanceof == 'function' && wgt.$instanceof(zk.Widget)) {
            const out: string[] = [];
            wgt.redraw(out);
            text = out.join('');
        } else if (wgt) {
            text = wgt.toString();
        }
        if (text) {
            if (!handler)
                handler = new DefaultHandler();
            _parseHTML(text, handler);

            this._dump('[' + wgt.className + '] '
                + wgt.uuid + '&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:red;">ErrorNumber: '
                + handler.getErrorNumber() + '</span>', handler.toHTML());
        }
    },

    dumpWidgetTree(wgt: any): void {
        const out: string[] = [], inf = {cnt: 0};
        _dumpWgt(out, wgt, 0, inf);
        this._dump('Total: ' + inf.cnt, out.join(''));
    },

    dumpWidgetTree4Zul(wgt: any): void {
        const out = ['&lt;zk xmlns:h="native"&gt;\n'], inf = {cnt: 0};
        _dumpWgt4Zul(out, wgt, 0, inf);
        out.push('&lt;/zk&gt;');
        this._dump('Total: ' + inf.cnt, out.join(''));
    },

    _dump(header: string, content: string): void {
        const console = this.getConsole();
        // ZK-5864 Rewrite the <pre> tag to improve readability.
        console.innerHTML += '<div class="z-debug-header">'
            + '<div class="z-debug-close" onclick="jq(\'#'
            + this.outId + '\').remove()" onmouseover="jq(this).addClass(\'z-debug-close-over\');"'
            + ' onmouseout="jq(this).removeClass(\'z-debug-close-over\');"></div>' + header
            + '</div><div class="z-debug-body"><pre>' + content + '</pre></div>';
    }
}

export class DefaultHandler {
    _errorNumber: number = 0;
    out: string[] = [];
    stack: string[] = [];

    endTag(deep: number, content: string, isEmpty?: boolean): void {
        const startTag = this.stack.pop();
        const endTag = content.substring(2, content.length - 1);
        if (startTag != endTag) {
            this._errorNumber++;
            this.out.push('<span style="color:red">Unmatched start tag : [<span style="color:blue;">&lt;',
                startTag || '', '&gt;</span>], end tag : [<span style="color:blue;">&lt;/',
                endTag, '&gt;</span>]</span>\n');
            return;
        }
        this.out.push(isEmpty ? '' : _space(deep), zUtl.encodeXML(content), '\n');
    }

    comment(deep: number, content: string): void {
        this.out.push(_space(deep), zUtl.encodeXML(content), '\n');
    }

    startTag(deep: number, content: string, isSingle: boolean, isEmpty?: boolean): void {
        this.out.push(_space(deep), this._parseAttribute(content, isSingle), isEmpty ? '' : '\n');
    }

    _parseAttribute(content: string, isSingle: boolean): string {
        const out: string[] = [];
        let odd, start, c;
        for (let i = 0, j = content.length; i < j; i++) {
            c = content.charAt(i);
            switch (c) {
                case '=':
                    if (!odd)
                        out.push('=<span style="color:#0666FD">');
                    else
                        out.push('=');
                    if (!odd)
                        odd = false;
                    break;
                case '<':
                    if (start) {
                        out.push('<span style="color:red;">', zUtl.encodeXML(content.substring(i)), '</span>');
                        this._errorNumber++;
                        return out.join('');
                    }
                    out.push('&lt;');
                    start = true;
                    break;
                case '>':
                    if (!isSingle)
                        if (start)
                            this.stack.push(content.substring(1, i));
                    out.push('&gt;');
                    break;
                case ' ':
                    if (!isSingle) {
                        isSingle = true;
                        if (start)
                            this.stack.push(content.substring(1, i));
                    }
                    out.push(c);
                    break;
                case '"':
                    if (odd) {
                        odd = false;
                        out.push('"</span>');
                        break;
                    } else odd = true;
                default:
                    out.push(c);
            }
        }
        return out.join('');
    }

    content(deep: number, content: string): void {
        if (content.indexOf('>') > -1)
            this.error(content);
        else
            this.out.push(_space(deep), zUtl.encodeXML(content), '\n');
    }

    error(content: string): void {
        this._errorNumber++;
        this.out.push('<span style="color:red"> Error caused by {', zUtl.encodeXML(content), '}</span>\n');
    }

    toHTML(): string {
        return this.out.join('');
    }

    getErrorNumber(): number {
        return this._errorNumber;
    }
}
zk.debug.Debugger = Debugger;
window.zDebug = Debugger;