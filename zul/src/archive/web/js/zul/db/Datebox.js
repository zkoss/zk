/* Datebox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:34 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.db.Datebox = zk.$extends(zul.inp.FormatWidget, {
	_buttonVisible: true,
	_lenient: true,
	$init: (function() {
		function initPopup () {
			this._pop = new zul.db.CalendarPop();
			this._tm = new zul.db.CalendarTime();
			this.appendChild(this._pop);
			this.appendChild(this._tm);
		}
		return function() {
			this.$supers('$init', arguments);
			this.$afterInit(initPopup);
			this.listen({onChange: this}, -1000);
		}
	})(),
	$define: {
		buttonVisible: function () {
			var n = this.$n('btn');
			if (n)
				v ? jq(n).show(): jq(n).hide();
		},
		format: function () {
			if (this._pop) {
				this._pop.setFormat(this._format);
				if (this._value)
					this._value = this._pop.getTime();
			}
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.coerceToString_(this._value);
		}
	},
	setValue: function (val) {
		var args;
		if (val) {
			args = [];
			for (var j = arguments.length; --j > 0;)
				args.unshift(arguments[j]);

			args.unshift((typeof val == 'string') ? this.coerceFromString_(val) : val);
		} else
			args = arguments;
		this.$supers('setValue', args);
	},
	onSize: _zkf = function () {
		this._auxb.fixpos();
	},
	onShow: _zkf,
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-datebox";
	},
	getRawText: function () {
		return this.coerceToString_(this._value);
	},
	getTimeFormat: function () {
		var fmt = this._format,
			aa = fmt.indexOf('a'),
			hh = fmt.indexOf('h'),
			KK = fmt.indexOf('K'),
			HH= fmt.indexOf('HH'),
			kk = fmt.indexOf('k'),
			mm = fmt.indexOf('m'),
			ss = fmt.indexOf('s'),
			hasAM = aa > -1,
			hasHour1 = hasAM ? hh > -1 || KK > -1 : false;

		if (hasHour1) {
			var f = hh < KK ? 'a KK' : 'a hh';
			return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
		} else {
			var f = HH < kk ? 'kk' : HH > -1 ? 'HH' : '';
			return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
		}
	},
	getDateFormat: function () {
		return this._format.replace(/[ahKHksm]/g, '');
	},
	setOpen: function(open) {
		var pp = this.$n("pp");
		if (pp) {
			if (!jq(pp).zk.isVisible()) this._pop.open();
			else this._pop.close();
		}
	},
	coerceFromString_: function (val) {
		return val ? zDateFormat.parseDate(val, this.getFormat()) : val;
	},
	coerceToString_: function (val) {
		return val ? zDateFormat.formatDate(val, this.getFormat()) : '';
	},
	getInputNode: function () {
		return this.$n('real');
	},
	bind_: function (){
		this.$supers('bind_', arguments);
		var btn = this.$n('btn'),
			inp = this.getInputNode();
		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, 'onClick', '_doBtnClick');
		}
		zWatch.listen({onSize: this, onShow: this});
		this._pop.setFormat(this.getDateFormat());
	},
	unbind_: function () {
		var btn = this.$n('btn');
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		this.setOpen();
		evt.stop();
	},
	onChange: function (evt) {
		if (this._pop)
			this._pop._value = evt.data.value;
	}
});
zul.db.CalendarPop = zk.$extends(zul.db.Calendar, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChange: this}, -1000);
	},
	setFormat: function (fmt) {
		if (fmt != this._fmt) {
			var old = this._fmt;
			this._fmt = fmt;
			if (this.getValue())
				this._value = zDateFormat.formatDate(zDateFormat.parseDate(this.getValue(), old), fmt);
		}
	},
	close: function (silent) {
		var db = this.parent,
			pp = db.$n("pp");

		if (!pp || !zk(pp).isVisible()) return;
		if (this._shadow) this._shadow.hide();

		var zcls = db.getZclass();
		pp.style.display = "none";
		pp.className = zcls + "-pp";

		jq(pp).zk.undoVParent();

		var btn = this.$n("btn");
		if (btn)
			jq(btn).removeClass(zcls + "-btn-over");

		if (!silent)
			jq(db.getInputNode()).focus();
	},
	open: (function() {
		function reposition(wgt) {
			var db = wgt.$n();
			if (!db) return;
			var pp = wgt.$n("pp"),
				inp = wgt.getInputNode();

			if(pp) {
				zk(pp).position(inp, "after_start");
				wgt._pop.syncShadow();
				zk(inp).focus();
			}
			return;
		}
		return function() {
			var wgt = this.parent,
				db = wgt.$n(), pp = wgt.$n("pp");
			if (!db || !pp)
				return;
			var zcls = wgt.getZclass();

			pp.className = db.className + " " + pp.className;
			jq(pp).removeClass(zcls);

			pp.style.width = pp.style.height = "auto";
			pp.style.position = "absolute"; //just in case
			pp.style.overflow = "auto"; //just in case
			pp.style.display = "block";
			pp.style.zIndex = "88000";

			//FF: Bug 1486840
			//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
			jq(pp).zk.makeVParent();


			if (pp.offsetHeight > 200) {
				//pp.style.height = "200px"; commented by the bug #2796461
				pp.style.width = "auto"; //recalc
			} else if (pp.offsetHeight < 10) {
				pp.style.height = "10px"; //minimal
			}
			if (pp.offsetWidth < db.offsetWidth) {
				pp.style.width = db.offsetWidth + "px";
			} else {
				var wd = jq.innerWidth() - 20;
				if (wd < db.offsetWidth)
					wd = db.offsetWidth;
				if (pp.offsetWidth > wd)
					pp.style.width = wd;
			}
			zk(pp).position(wgt.getInputNode(), "after_start");
			setTimeout(function() {
				reposition(wgt);
			}, 150);
			//IE, Opera, and Safari issue: we have to re-position again because some dimensions
			//in Chinese language might not be correct here.

			var fmt = wgt.getTimeFormat();
			if (fmt) {
				var tm = wgt._tm;
				tm.setVisible(true);
				tm.setFormat(fmt);
				tm.setValue(wgt.getValue());
				tm.onShow();
			} else {
				wgt._tm.setVisible(false);
			}

		}
	})(),
	syncShadow: function () {
		if (!this._shadow) {
			this._shadow = new zk.eff.Shadow(this.parent.$n('pp'), {
				left: -4,
				right: 4,
				top: 2,
				bottom: 3,
				autoShow: true,
				stackup: (zk.useStackup === undefined ? zk.ie6Only : zk.useStackup)
			});
		} else
			this._shadow.sync();
	},
	onChange: function (evt) {
		var date = this.getTime(),
			oldDate = this.parent.getValue();
		if (oldDate) {
			oldDate.setFullYear(date.getFullYear());
			oldDate.setMonth(date.getMonth());
			oldDate.setDate(date.getDate());
		} else
			this.parent.setValue(date);
		this.parent.getInputNode().value = evt.data.value = this.parent.getRawText();
		this.parent.fire(evt.name, evt.data);
		if (this._view == 'day' && evt.data.shallClose !== false)
			this.close();
		evt.stop();
	},
	onFloatUp: function (wgt) {
		if (!zUtl.isAncestor(this.parent, wgt))
			this.close(true);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onFloatUp: this});
	},
	unbind_: function () {
		zWatch.unlisten({onFloatUp: this});

		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		this.$supers('unbind_', arguments);
	},
	_setView: function (val) {
		this.parent._tm.setVisible(val == 'day');
		this.$supers('_setView', arguments);
	}
});
zul.db.CalendarTime = zk.$extends(zul.inp.Timebox, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChanging: this}, -1000);
	},
	onChanging: function (evt) {
		var date = this.coerceFromString_(evt.data.value),
			oldDate = this.parent.getValue();
		if (oldDate) {
			oldDate.setHours(date.getHours());
			oldDate.setMinutes(date.getMinutes());
			oldDate.setSeconds(date.getSeconds());
		} else
			this.parent.setValue(date);
		this.parent.getInputNode().value = evt.data.value = this.parent.getRawText();
		this.parent.fire(evt.name, evt.data);
		if (this._view == 'day' && evt.data.shallClose !== false)
			this.close();
		evt.stop();
	}
});