wgt4714.test01.Test01 = zk.$extends(zk.Widget, {
    redraw: function (out) {
        out.push('<div', this.domAttrs_(), '>', this.widgetName);
        for (var w = this.firstChild; w; w = w.nextSibling) {
            w.redraw(out);
        }
        out.push('</div>');
    }
});