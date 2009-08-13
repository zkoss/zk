/* Calendar.js

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
zul.db.Calendar = zk.$extends(zul.Widget, {
	_view : "day", //"day", "month", "year", "decade"
	$define: {
		value: _zkf = function() {
			this.rerender();
		}
	},
	getFormat: function () {
		return this._fmt || "yyyy/MM/dd";
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-calendar";
	},
	today: function () {
		var d = new Date();
		return new Date(d.getFullYear(), d.getMonth(), d.getDate());
	},
	bind_: function (){
		this.$supers('bind_', arguments);
		this._value ? zDateFormat.parseDate(this._value, this.getFormat()) : new Date();
		var title = this.$n("title"),
			mid = this.$n("mid"),
			ly = this.$n("ly"),
			ry = this.$n("ry"),
			zcls = this.getZclass();
		jq(title).hover(
			function () {
				$(this).toggleClass(zcls + "-title-over");
			},
			function () {
			    $(this).toggleClass(zcls + "-title-over");
			}
		);
		this._markCal();
		this.domListen_(title, "onClick", '_changeView')
			.domListen_(mid, "onClick", '_choiceData')
			.domListen_(ly, "onClick", '_doclickArrow')
			.domListen_(ry, "onClick", '_doclickArrow')
			.domListen_(mid, "onMouseOver", '_doMouseEffect')
			.domListen_(mid, "onMouseOut", '_doMouseEffect');
	},
	unbind_: function () {
		var title = this.$n("title"),
			mid = this.$n("mid"),
			ly = this.$n("ly"),
			ry = this.$n("ry");
		this.domUnlisten_(title, "onClick", '_changeView')
			.domUnlisten_(mid, "onClick", '_choiceData')
			.domUnlisten_(ly, "onClick", '_doclickArrow')
			.domUnlisten_(ry, "onClick", '_doclickArrow')
			.domUnlisten_(mid, "onMouseOver", '_doMouseEffect')
			.domUnlisten_(mid, "onMouseOut", '_doMouseEffect')
			.$supers('unbind_', arguments);
	},
	_doclickArrow: function (evt) {
		var node = evt.domTarget,
			ofs = node.id.indexOf("-ly") > 0 ? -1 : 1;
		if (jq(node).hasClass(this.getZclass() + (ofs == -1 ? '-left-icon-disd' : '-right-icon-disd')))
			return;
		switch(this._view) {
			case "day" :
				this._shiftDate("m", ofs);
				break;
			case "month" :
				this._shiftDate("y", ofs);
				break;
			case "year" :
				this._shiftDate("y", ofs*10);
				break;
			case "decade" :
				this._shiftDate("y", ofs*100);
				break;
		}
		this.rerender();
	},
	_doMouseEffect: function (evt) {
		var node = evt.domTarget.tagName == "TD" ? evt.domTarget : evt.domTarget.parentNode,
			zcls = this.getZclass();
		if (jq(node).is("."+zcls+"-seld")) {
			jq(node).removeClass(zcls + "-over");
			jq(node).toggleClass(zcls + "-over-seld");
		}else {
			jq(node).toggleClass(zcls + "-over");
		}
	},
	getTime : function () {
		return this._value ? zDateFormat.parseDate(this._value, this.getFormat()) : new Date();
	},
	_setTime : function (y, m, d, hr, mi) {
		var dateobj = this.getTime(),
			year = y != null ? y  : dateobj.getFullYear(),
			month = m != null ? m : dateobj.getMonth(),
			day = d != null ? d : dateobj.getDate();
		this._value = zDateFormat.formatDate(new Date(year, month, day), this.getFormat());
		this.fire('onChange', {value: this._value});
	},
	_choiceData: function (evt) {
		var target = evt.domTarget;
		target = target.tagName == "TD" ? target : target.parentNode;
		
		var val = jq(target).attr('_dt');
		if (target && !isNaN(val)) {
			var cell = target,
				dateobj = this.getTime();
			switch(this._view) {
				case "day" :
					var oldTime = this.getTime();
					this._setTime(null, cell._monofs != null && cell._monofs != 0 ?
							dateobj.getMonth() + cell._monofs : null, val);
					var newTime = this.getTime();
					if (oldTime.getYear() == newTime.getYear() &&
						oldTime.getMonth() == newTime.getMonth()) {
							this._markCal();
					} else
						this.rerender();
					break;
				case "month" :
					this._setTime(null, val);
					this._setView("day");
					break;
				case "year" :
					this._setTime(val);
					this._setView("month");
					break;
				case "decade" :
					//Decade mode Set Year Also
					this._setTime(val);
					this._setView("year");
					break;
			}
		}
	},
	_shiftDate: function (opt, ofs) {
		var dateobj = this.getTime(),
			year = dateobj.getFullYear(),
			month = dateobj.getMonth(),
			day = dateobj.getDate();
		switch(opt) {
			case "d" :
				day = day + ofs;
				break;
			case "m" :
				month = month + ofs;
				break;
			case "y" :
				year = year + ofs;
				break;
			case "d" :
				year = year + ofs;
				break;
		}
		this._value = zDateFormat.formatDate(new Date(year, month, day), this.getFormat());
		this.fire('onChange', {value: this._value, shallClose: false});
	},
	_changeView : function (evt) {
		var tm = this.$n("tm"),
			ty = this.$n("ty"),
			tyd = this.$n("tyd");
		if (evt.domTarget == tm)
			this._setView("month");
		else if (evt.domTarget == ty)
			this._setView("year");
		else if (evt.domTarget == tyd )
			this._setView("decade");
	},
	_setView : function (view) {
		if (view != this._view) {
			this._view = view;
			this.rerender();
		}
	},
	_markCal : function () {
		var	zcls = this.getZclass(),
		 	val = this.getTime(),
		 	m = val.getMonth(),
			d = val.getDate(),
			y = val.getFullYear(),
			last = new Date(y, m + 1, 0).getDate(), //last date of this month
			prev = new Date(y, m, 0).getDate(), //last date of previous month
			v = new Date(y, m, 1).getDay()- zk.DOW_1ST;
		//hightlight month & year
		for (var j = 0; j < 12; ++j) {
			var mon = this.$n("m" + j),
				year = this.$n("y" + j),
				yy = y % 10 + 1;
			if (mon) {
				if (m == j) {
					jq(mon).addClass(zcls+"-seld");
					jq(mon).removeClass(zcls+"-over");
				} else
					jq(mon).removeClass(zcls+"-seld");
			}
			if (year) {
				if (yy == j) {
					jq(year).addClass(zcls+"-seld");
				    jq(year).removeClass(zcls+"-over");
				} else
					jq(year).removeClass(zcls+"-seld");
			}
		}

		if (v < 0) v += 7;
		for (var j = 0, cur = -v + 1; j < 6; ++j) {
			var week = this.$n("w" + j);
			if ( week != null) {
				for (var k = 0; k < 7; ++k, ++cur) {
					v = cur <= 0 ? prev + cur: cur <= last ? cur: cur - last;
					if (k == 0 && cur > last)
						week.style.display = "none";
					else {
						if (k == 0) week.style.display = "";
						var cell = week.cells[k],
							monofs = cur <= 0 ? -1: cur <= last ? 0: 1,
							sel = cur == d;
						cell.style.textDecoration = "";
						cell._monofs = monofs;
						//hightlight day
						jq(cell).removeClass(zcls+"-over");
						jq(cell).removeClass(zcls+"-over-seld");
						if (sel)
							jq(cell).addClass(zcls+"-seld");
						else
							jq(cell).removeClass(zcls+"-seld");
						jq(cell).html('<a href="javascript:;">' + v + '</a>').attr('_dt', v);
					}
				}
			}
		}
	}

});
