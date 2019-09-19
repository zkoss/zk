/* Toast.ts

	Purpose:

	Description:

	History:
		Mon Mar 12 09:43:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(() => {
/**
 * A toast widget.
 */
zul.wgt.Toast = zk.$extends(zul.Widget, {
    _visible: false,
    $init(msg: string, opts: {type: string; dur: number; closable: boolean; animationSpeed: number}) {
        this.$supers(zul.wgt.Toast, '$init', arguments);
        this._msg = msg;
        this._type = opts.type;
        this._dur = opts.dur;
        this._closable = opts.closable;
        this._animationSpeed = opts.animationSpeed;
    },
    bind_() {
        this.$supers(zul.wgt.Toast, 'bind_', arguments);
        zWatch.listen({onFloatUp: this});
        this.setFloating_(true);
    },
    unbind_() {
        this.setFloating_(false);
        zWatch.unlisten({onFloatUp: this});
        this.$supers(zul.wgt.Toast, 'unbind_', arguments);
    },
    redraw(out: zk.Buffer) {
        let uuid = this.uuid,
            icon = this.$s('icon');
        out.push('<div', this.domAttrs_(), '>');
        out.push('<i id="', uuid, '-icon" class="', icon, ' ', (this.$class.iconMap[this._type]), '"></i>');
        out.push('<div id="', uuid, '-cave" class="', this.$s('content'), '">',
                this._msg, '</div>'); // not encoded to support HTML
        if (this._closable)
            out.push('<div id="', uuid, '-cls" class="', this.$s('close'),
                    '"><i id="', uuid, '-clsIcon" class="', icon, ' z-icon-times"></i></div>');
        out.push('</div>');
    },
    domClass_(): string {
        let type = this._type,
            s = this.$supers(zul.wgt.Toast, 'domClass_', arguments);
        if (type)
            s += ' ' + this.$s(zUtl.encodeXML(type));
        return s;
    },
    doClick_(evt: zk.Event) {
        let p = evt.domTarget;
        if (p === this.$n('cls') || p === this.$n('clsIcon')) //may click on font-icon
            this.close();
        else
            this.$supers('doClick_', arguments);
    },
    onFloatUp(ctl, opts) {
        if (opts && opts.triggerByClick) {
            if (!this.isVisible() || ctl.origin === this)
                return;
            if (!this._closable && this._dur <= 0)
                this.close();
        }
    },
    /**
     * Opens the toast.
     */
    open() {
        this.setFloating_(true);
        this.openAnima_();
    },
    openAnima_() {
        jq(this.$n()).fadeIn(this._animationSpeed, () => this.afterOpenAnima_());
    },
    /**
     * The handling after the opening effect of toast.
     */
    afterOpenAnima_() {
        this.setVisible(true);
        //add extra CSS class for easy customize
        jq(this.$n()).addClass(this.$s('open'));
    },
    /**
     * Closes this toast at the client.
     *
     * <p>In most cases, the toast is closed automatically when the user
     * clicks outside of the toast.
     */
    close() {
        this.setFloating_(false);
        this.closeAnima_();
    },
    closeAnima_() {
        jq(this.$n()).fadeOut(this._animationSpeed, () => this.afterCloseAnima_());
    },
    afterCloseAnima_() {
        this.detach();
    }
}, {
    /**
     * Shows a toast.
     */
    show(msg: string, opts: Partial<{pos: string; dur: number}>) {
        if (!opts)
            opts = {};
        let pos = opts.pos || 'top_center',
            dur = opts.dur,
            ntf = new zul.wgt.Toast(msg, opts);

        jq(this.getPositionWrapper_(pos)).append(ntf);
        ntf.open();

        // auto dismiss
        if (dur && dur > 0)
            setTimeout(() => {
                if (ntf.desktop) ntf.close();
            }, dur);
    },
    getPositionWrapper_(pos: string): HTMLElement {
        let cache: HTMLElement = this._positionWrappersCache[pos];
        if (!cache) {
            let wrapper = jq('#_z_toastpositionwrapper');
            if (!wrapper.length) {
                wrapper = jq('<div id="_z_toastpositionwrapper" class="z-toast-position-wrapper"></div>')
                    .appendTo(document.body);
            }
            cache = jq(`<div class="z-toast-position z-toast-position-${pos.replace('_', '-')}"></div>`)
                .appendTo(wrapper)
                .get(0);
            this._positionWrappersCache[pos] = cache;
        }
        return cache;
    },
    iconMap: {
        'warning': 'z-icon-exclamation-circle',
        'info': 'z-icon-info-circle',
        'error': 'z-icon-times-circle'
    },
    _positionWrappersCache: {}
});
})();
