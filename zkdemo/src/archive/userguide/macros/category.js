// category.js

zk.$package('userguide');
zPkg.load('zul.wgt', null, function () {
userguide.Categorybar = zk.$extends(zul.wgt.Div, {
	bind_: function () {
		this.$supers('bind_', arguments);
		zDom.disableSelection(this.getNode());
	},
	redraw: function (out) {
		var uuid = this.uuid;
		out.push('<div', this.domAttrs_(), '>',
			'<div id="', uuid, '$right"></div>',
			'<div id="', uuid, '$left"></div>',
			'<div id="', uuid, '$body" class="', this.getZclass(), '-body">',
			'<div id="', uuid, '$cave">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('<div class="z-clear"></div></div></div></div>');
	},
	doMouseOver_: function (evt) {
		//TODO
	},
	doMouseOut_: function (evt) {
	}
});
});

function onSelect(wgt, deselect) {
	wgt = zk.Widget.$(wgt);
	//port onSelect in zk 3
}
