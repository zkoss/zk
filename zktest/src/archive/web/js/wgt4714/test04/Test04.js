wgt4714.test04.Test04 = zk.$extends(zk.Widget, {
    redraw: function (out) {
        out.push('<div', this.domAttrs_(), '>', this.widgetName);
        for (var w = this.firstChild; w; w = w.nextSibling) {
            w.redraw(out);
        }
        out.push('</div>');
    }
});