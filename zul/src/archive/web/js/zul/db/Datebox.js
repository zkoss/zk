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
	_compact: false,
	_buttonVisible: true,
	_lenient: true,
	$init: function (){
		this.$supers('$init', arguments);
		this.$afterInit(this._init);
	},
	$define: {
		buttonVisible: function () {
			var n = this.$n('btn');
			if (n)
				v ? jq(n).show(): jq(n).hide();
		}
	},
	onSize: _zkf = function () {
		this._auxb.fixpos();
	},
	onShow: _zkf,
	onFloatUp: function (wgt) {
		if (!zUtl.isAncestor(this, wgt))
			this.close({sendOnOpen:true});
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-datebox";
	},
	getDate: function () {
		var pop = this._pop;
		return pop.getDate();
	},
	getDateFormat: function () {
		//@TODO
	},
	setConstraint : function () {
		//@TODO
	},
	setOpen: function(open) {
		var pp = this.$n("pp");
		if (pp) {
			if (!jq(pp).zk.isVisible()) this.open();
			else this.close(true);
		}
	},
	open: function () {
		//this.$supers('open', arguments);
		//@TODO
		//zkau.closeFloats(pp); //including popups
		//zkau._dtbox.setFloatId(pp.id);
		var uuid = this.id,
			db = this.$n(),
			pp = this.$n("pp");
		if (!db || !pp) return;
		var zcls = this.getZclass();
		pp.className=db.className+" "+pp.className;
		jq(pp).removeClass(zcls);

		//if (meta) meta.init();
		//else zkau.setMeta(db, new zk.Cal(db, pp));


		pp.style.width = pp.style.height = "auto";
		pp.style.position = "absolute"; //just in case
		pp.style.overflow = "auto"; //just in case
		pp.style.display = "block";
		pp.style.zIndex = "88000";
		//No special child, so no need to: zk.onVisiAt(pp);

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
			if (wd < db.offsetWidth) wd = db.offsetWidth;
			if (pp.offsetWidth > wd) pp.style.width = wd;
		}
		var input = this.$n("real"),
			dbobj = this;
		jq(pp).zk.position(input, "after_start");
		setTimeout(function () {
			dbobj._reposition(uuid);
		}, 150);
		//IE, Opera, and Safari issue: we have to re-position again because some dimensions
		//in Chinese language might not be correct here.

	},
	close: function (focus) {
		var uuid = this.id,
			db = this.$n(),
			pp = this.$n("pp");

		if (!db || !jq(pp).zk.isVisible()) return;
		var zcls = this.getZclass();
		if (pp._shadow) pp._shadow.hide();

		pp.style.display = "none";
		pp.className = zcls + "-pp";

		jq(pp).zk.undoVParent();

		//zkau._dtbox.setFloatId(null);
		//No special child, so no need to: zk.onHideAt(pp);
		//zkau.hideCovered();

		var btn = this.$n("btn");
		if (btn)
			jq(btn).removeClass(zcls + "-btn-over");

		if (focus)
			//zk.asyncFocus(uuid + "!real");
			;
		//this.$supers('close', arguments);
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
		this.domListen_(inp, "onBlur", '_inpBlur');
		zWatch.listen({onSize: this, onShow: this, onFloatUp: this});
	},
	unbind_: function () {
		var btn = this.$n('btn');
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}
		zWatch.unlisten({onSize: this, onShow: this, onFloatUp: this});
		this.$supers('unbind_', arguments);
	},
	_inpBlur: function (evt) {
		this.$supers('doBlur_', arguments);
		this.fire('onChange', {value: this._pop._date});
	},
	_init : function () {
		var pop = new zul.db.CalendarPop({db:this});
		this._pop = pop;
		this.appendChild(pop);
	},
	_reposition : function (uuid) {
		var db = zk(uuid).jq[0];
		if (!db) return;
		var dbobj = zk.Widget.$(db),
			pp = dbobj.$n("pp"),
			input = dbobj.$n("real");

		if(pp) {
			jq(pp).zk.position(input, "after_start");
			if (!pp._shadow)
				pp._shadow =  new zk.eff.Shadow(pp,
					{left: -4, right: 4, top: 2, bottom: 3, autoShow: true, stackup: (zk.useStackup === undefined ? zk.ie6Only: zk.useStackup)});
			else
				pp._shadow.sync();
//			zkau.hideCovered();
//			zk.asyncFocus(inpId);
		}
		return;
	},
	_doBtnClick: function (evt) {
		this.setOpen();
		evt.stop();
	}
});
zul.db.CalendarPop = zk.$extends(zul.db.Calendar, {
	$init: function (obj) {
		this.$supers('$init', arguments);
	},
	_choiceData: function (evt) {
		this.$supers('_choiceData', arguments);
		var db = this.parent,
		    input = db.$n("real");
		input.value = this._date;
		db.fire('onChange', {value: this._date});
	}
});
