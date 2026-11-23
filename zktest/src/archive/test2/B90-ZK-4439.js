var MyComp4439 = zk.$extends(zul.Widget, {
	bind_: function () {
		this.$supers('bind_', arguments);
		var evtName = zk.mobile ? 'onZMouseDown' : 'onClick';
		this.domListen_(this.$n('btnA'), evtName, this.proxy(this._doClickA))
			.domListen_(this.$n('btnB'), evtName, this.proxy(this._doClickB));
	},
	unbind_: function () {
		var evtName = zk.mobile ? 'onZMouseDown' : 'onClick';
		this.domUnlisten_(this.$n('btnB'), evtName, this.proxy(this._doClickB))
			.domUnlisten_(this.$n('btnA'), evtName, this.proxy(this._doClickA));
		this.$supers('unbind_', arguments);
	},
	_doClickA: function () {
		zk.log('Click A', this);
	},
	_doClickB: function () {
		zk.log('Click B', this);
	},
	redraw: function (out) {
		out.push('<div', this.domAttrs_(), '>');
		out.push('<button id="', this.uuid, '-btnA">A</button><button id="', this.uuid, '-btnB">B</button>');
		out.push('</div>');
	}
});