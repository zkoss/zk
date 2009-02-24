/* db.js

{{IS_NOTE
	Purpose:
		datebox
	Description:

	History:
		Mon Oct 17 15:24:01     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul"); //msgzul
zk.load("zul.vd");

//Calendar//
zkCal = {};

zk.Cal = zClass.create();
zk.Cal.prototype = {
	initialize: function (cmp, popup) {
		this.id = cmp.id;
		this.popup = popup;
		this.input = $e(cmp.id + "!real");
		this._newCal();
		this.init();
	},
	cleanup: function ()  {
		if (this.fnSubmit)
			zk.unlisten(this.form, "submit", this.fnSubmit);
		this.element = this.fnSubmit = null;
	},
	_newCal: function() {
		this.element = $e(this.id);
		if (!this.element) return;
		var zcls = getZKAttr(this.element, "zcls"),
			compact = getZKAttr(this.element, "compact") == "true",
			html = this.popup ? '<table border="0" cellspacing="0" cellpadding="0" tabindex="-1">': '';

		html += '<tr><td><table class="'+zcls+'-calyear" width="100%" border="0" cellspacing="0" cellpadding="0"><tr><td width="5"></td><td align="right"><img src="'
			+zk.getUpdateURI('/web/zul/img/cal/arrowL.gif')
			+'" style="cursor:pointer" onclick="zkCal.onyearofs(event,-1)" id="'
			+this.id+'!ly"/></td>';

		if (compact)
			html += '<td align="right"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrow2L.gif')
				+'" style="cursor:pointer" onclick="zkCal.onmonofs(event,-1)" id="'
				+this.id+'!lm"/></td>';

		html += '<td width="5"></td><td id="'+this.id+'!title"></td><td width="5"></td>';

		if (compact)
			html += '<td align="left"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrow2R.gif')
				+'" style="cursor:pointer" onclick="zkCal.onmonofs(event,1)" id="'
				+this.id+'!rm"/></td>';

		html += '<td align="left"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrowR.gif')
			+'" style="cursor:pointer" onclick="zkCal.onyearofs(event,1)" id="'
			+this.id+'!ry"/></td><td width="5"></td></tr></table></td></tr>';

		if (!compact) {
			html += '<tr><td><table class="'+zcls+'-calmon" width="100%" border="0" cellspacing="0" cellpadding="0"><tr>';
			for (var j = 0 ; j < 12; ++j) {
				html += '<td id="'+this.id+'!m'+j
					+'" onclick="zkCal.onmonclk(event)" onmouseover="zkCal.onover(event)" onmouseout="zkCal.onout(event)">'
					+ zk.S2MON[j] + '</td>';
				if (j == 5) html += '</tr><tr>';
			}
			html += '</tr></table></td></tr>';
		}
		if (this.popup) html += '<tr><td height="3px"></td></tr>';

		html += '<tr><td><table class="'+zcls+'-calday" width="100%" border="0" cellspacing="0" cellpadding="0"><tr class="'+zcls+'-caldow">';
		var sun = (7 - zk.DOW_1ST) % 7, sat = (6 + sun) % 7;
		for (var j = 0 ; j < 7; ++j) {
			html += '<td';
			if (j == sun || j == sat) html += ' style="color:red"';
			html += '>' + zk.S2DOW[j] + '</td>';
		}
		html += '</tr>';
		for (var j = 0; j < 6; ++j) { //at most 7 rows
			html += '<tr class="'+zcls+'-calday" id="'+this.id+'!w'+j
				+'" onclick="zkCal.ondayclk(event)" onmouseover="zkCal.onover(event)" onmouseout="zkCal.onout(event)">';
			for (var k = 0; k < 7; ++k)
				html += '<td></td>';
			html += '</tr>'
		}
		html += '</table></td></tr>';
		if (this.popup) html += '</table>';
		zk.setInnerHTML(this.popup || this.element, html);

		this.form = zk.formOf(this.element);
		if (this.form && !this.fnSubmit) {
			var meta = this;
			this.fnSubmit = function () {
				meta.onsubmit();
			};
			zk.listen(this.form, "submit", this.fnSubmit);
		}
	},
	init: function () {
		this.element = $e(this.id);
		if (!this.element) return;

		var val = this.input ? this.input.value: getZKAttr(this.element, "value"),
			bd = getZKAttr(this.element, "bd"),
			ed = getZKAttr(this.element, "ed");
		if (val) val = zk.parseDate(val, this.getFormat());
		this.date = val ? val: this.today();
		if (bd) this.begin = new Date($int(bd) * 1000);
		if (ed) this.end = new Date($int(ed) * 1000);
		this._output();
	},
	getFormat: function () {
		var fmt = getZKAttr(this.element, "fmt");
		return fmt ? fmt: "yyyy/MM/dd";
	},
	today: function () {
		var d = new Date();
		return new Date(d.getFullYear(), d.getMonth(), d.getDate());
	},
	_output: function () {
		//year
		var val = this.date,
			m = val.getMonth(),
			d = val.getDate(),
			y = val.getFullYear(),
			el = $e(this.id + "!title");

		zk.setInnerHTML(el, zk.SMON[m] + ', ' + y);
		var zcls = getZKAttr(this.element, "zcls");
		//month
		for (var j = 0; j < 12; ++j) {
			el = $e(this.id + "!m" + j);
			if (el) { //omitted if compact
				if (m == j) {
					zk.addClass(el, zcls + "-seld");
					zk.rmClass(el, zcls + "-over");
				} else
					zk.rmClass(el, zcls + "-seld") ;
				el.setAttribute("zk_mon", j);
			}
		}

		var last = new Date(y, m + 1, 0).getDate(), //last date of this month
			prev = new Date(y, m, 0).getDate(), //last date of previous month
			v = new Date(y, m, 1).getDay()- zk.DOW_1ST;

		if (v < 0) v += 7;
		for (var j = 0, cur = -v + 1; j < 6; ++j) {
			el = $e(this.id + "!w" +j);
			for (var k = 0; k < 7; ++k, ++cur) {
				v = cur <= 0 ? prev + cur: cur <= last ? cur: cur - last;
				if (k == 0 && cur > last) el.style.display = "none";
				else {
					if (k == 0) el.style.display = "";
					var cell = el.cells[k];
					var monofs = cur <= 0 ? -1: cur <= last ? 0: 1;
					cell.style.textDecoration = "";
					cell.setAttribute("zk_day", v);
					cell.setAttribute("zk_monofs", monofs);
					this._outcell(cell, cur == d, this._invalid(new Date(y, m + monofs, v)));
				}
			}
		}
	},
	_invalid: function (d) {
		return zkDtbox._invalid(d, this.begin, this.end);
	},
	_outcell: function (cell, sel, disd) {
		if (sel) this.curcell = cell;
		var zcls = getZKAttr(this.element, "zcls");

		zk.rmClass(cell, zcls+"-over");
		zk.rmClass(cell, zcls+"-over-seld");
		zk[sel ? "addClass" : "rmClass"](cell, zcls+"-seld");
		zk[disd ? "addClass" : "rmClass"](cell, zcls+"-disd");

		var d = cell.getAttribute("zk_day");
		zk.setInnerHTML(cell,
			!sel || this.popup ? d:
			'<a href="javascript:;" onkeyup="zkCal.onup(event)" onkeydown="zkCal.onkey(event)" onblur="zkCal.onblur(event)">'+d+'</a>');
			//IE: use keydown. otherwise, it causes the window to scroll
	},
	_ondayclk: function (cell) {
		var y = this.date.getFullYear(),
			m = this.date.getMonth(),
			d = zk.getIntAttr(cell, "zk_day");
		if (!zkCal._seled(cell)) { //!selected
			var monofs = zk.getIntAttr(cell, "zk_monofs"),
				now = new Date(y, m + monofs, d);
			if (this._invalid(now)) {
				if (this.popup) {
					var pp = $e(this.id + "!pp");
					if (pp) // Bug #1912363
						zkDtbox.close(pp, true);
				}
				return;
			}
			this.date = now;
			if (!this.popup) {
				if (monofs != 0) this._output();
				else {
					this._outcell(this.curcell, false);
					this._outcell(cell, true);
				}
			}
		}
		this._onupdate(true);
	},
	_onmonclk: function (cell) {
		if (!zkCal._seled(cell)) { //!selected
			var y = this.date.getFullYear(),
				d = this.date.getDate();
			this._setDateMonChg(y, zk.getIntAttr(cell, "zk_mon"), d);
			this._output();
			this._onupdate(false);
		}
	},
	_onyearofs: function (ofs) {
		var y = this.date.getFullYear(),
			m = this.date.getMonth(),
			d = this.date.getDate();
		this.date = new Date(y + ofs, m, d);
		this._output();
		this._onupdate(false);
	},
	_onmonofs: function (ofs) {
		var y = this.date.getFullYear(),
			m = this.date.getMonth(),
			d = this.date.getDate();
		this._setDateMonChg(y, m + ofs, d);
		this._output();
		this._onupdate(false);
	},
	/** Sets date caused by the change of month (it fixed 6/31 issue).
	 */
	_setDateMonChg: function (y, m, d) {
		this.date = new Date(y, m, d);
		if (m >= 0) { //just in case
			m %= 12;
			while (this.date.getMonth() != m) //6/31 -> 7/1
				this.date = new Date(y, m, --d);
		}
	},
	setDate: function (val) {
		if (val != this.date) {
			var old = this.date,
				year = val.getFullYear(), mon = val.getMonth();
			if (old.getFullYear() != year || old.getMonth() != mon) {
				this.date = val;
				this._output();
			} else {
				this.date = val;
				this._outcell(this.curcell, false, this._invalid(val));

				var d = val.getDate();
				for (var j = 0; j < 6; ++j) {
					el = $e(this.id + "!w" +j);
					for (var k = 0; k < 7; ++k) {
						var cell = el.cells[k];
						if (zk.getIntAttr(cell, "zk_monofs") == 0
						&& zk.getIntAttr(cell, "zk_day") == d) {
							this._outcell(cell, true,
								this._invalid(new Date(year, mon, d)));
							break;
						}
					}
				}
			}
		}
	},
	/** Calls selback or onchange depending on this.popup. */
	_onupdate: function (close) {
		this._output();
		if (this.popup) {
			this.selback(close);
			if (this.input) {
				//Request 1551019: better responsive
				this.onchange();
				zk.asyncFocus(this.input.id);
			}
		} else {
			this.onchange();
			zk.asyncFocusDown(this.id, zk.ie ? 50: 0);
		}
	},
	onchange: function () {
		if (this.popup) {
			zkTxbox.updateChange(this.input, false);
		} else {
			var y = this.date.getFullYear(),
				m = this.date.getMonth(),
				d = this.date.getDate();
			setZKAttr(this.element, "value", this.getDateString());
			zkau.sendasap({uuid: this.id, cmd: "onChange",
				data: [y+'/'+(m+1)+'/'+d]});
			this._changed = false;
		}
	},
	selback: function (close) {
		if (this.input) {
			this.input.value = this.getDateString();
			zk.asyncFocus(this.input.id);
			zk.asyncSelect(this.input.id);
		}
		if (close) zkau.closeFloats(this.element);
	},
	getDateString: function () {
		return zk.formatDate(this.date, this.getFormat());
	},
	shift: function (days) {
		var val = this.date;
		this.setDate(new Date(
			val.getFullYear(), val.getMonth(), val.getDate() + days));
	},
	onsubmit: function () {
		var nm = getZKAttr(this.element, "name");
		if (!nm || !this.form) return;

		var val = getZKAttr(this.element, "value"),
			el = this.form.elements[nm];
		if (el) el.value = val;
		else zk.newHidden(nm, val, this.form);
	}
};

zkCal.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else zkau.setMeta(cmp, new zk.Cal(cmp, null));
};
zkCal.setAttr = function (cmp, nm, val) {
	if ("z.value" == nm) {
		var meta = zkau.getMeta(cmp);
		if (meta) meta.setDate(zk.parseDate(val, "yyyy/MM/dd"));
	}
	zkau.setAttr(cmp, nm, val);
	return true;
};

zkCal.onyearofs = function (evt, ofs) {
	var meta = zkau.getMeta($uuid(Event.element(evt)));
	if (meta) meta._onyearofs(ofs);
};
zkCal.onmonofs = function (evt, ofs) {
	var meta = zkau.getMeta($uuid(Event.element(evt)));
	if (meta) meta._onmonofs(ofs);
};
zkCal.onmonclk = function (evt) {
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._onmonclk(el);
};
zkCal.ondayclk = function (evt) {
	var el = Event.element(evt);
	if ($tag(el) == "A") el = el.parentNode;
	var meta = zkau.getMeta($uuid(el));
	if (meta) meta._ondayclk(el);
};
zkCal.onup = function (evt) {
	var meta = zkau.getMeta($uuid(Event.element(evt)));
	if (meta && meta._changed) meta.onchange(); //delay onchange here to avoid too many reqs
	return true;
};
zkCal.onkey = function (evt) {
	if (!evt.altKey && Event.keyCode(evt) >= 37 && Event.keyCode(evt) <= 40) {
		var meta = zkau.getMeta($uuid(Event.element(evt)));
		if (meta) {
			ofs = Event.keyCode(evt) == 37 ? -1: Event.keyCode(evt) == 39 ? 1:
				Event.keyCode(evt) == 38 ? -7: 7;
			meta.shift(ofs);
			zk.focusDown(meta.element);
			meta._changed = true;
		}

		Event.stop(evt);
		return false;
	}
	return true;
};
zkCal.onblur = function (evt) {
	//onup is not called if onblur happens first
	var meta = zkau.getMeta($uuid(Event.element(evt)));
	if (meta && meta._changed) meta.onchange();
};

zkCal.onover = function (evt) {
	var el = Event.element(evt),
		cmp = $outer(Event.element(evt)),
		zcls = getZKAttr(cmp, "zcls");

	if(! zk.hasClass(el, zcls+"-disd")) {
		if(zk.hasClass(el, zcls+"-seld") || zk.hasClass(el, zcls+"-over-seld"))
			zk.addClass(el, zcls+"-over-seld");
		else
			zk.addClass(el, zcls+"-over");
	}
};
zkCal.onout = function (evt) {
	var el = Event.element(evt),
		cmp = $outer(Event.element(evt)),
		zcls = getZKAttr(cmp, "zcls");

	if(zk.hasClass(el, zcls+"-seld") || zk.hasClass(el, zcls+"-over-seld"))
		zk.rmClass(el, zcls+"-over-seld");
	else
		zk.rmClass(el, zcls+"-over");
};
/** Returns if a cell is selected. */
zkCal._seled = function (cell) {
	var zcls = getZKAttr($outer(cell), "zcls");
	return zk.hasClass(cell, zcls+"-seld") || zk.hasClass(cell, zcls+"-over-seld");
};

//Datebox//
zkDtbox = {};
zkDtbox.init = function (cmp) {
	zkDtbox.onVisi = zkDtbox.onSize = zul.onFixDropBtn;
	zkDtbox.onHide = zkTxbox.onHide; //widget.js is ready now
	zkDtbox.cleanup = zkTxbox.cleanup;

	var inp = $real(cmp);
	zkTxbox.init(inp);
	zk.listen(inp, "keydown", zkDtbox.onkey);
		//IE: use keydown. otherwise, it causes the window to scroll

	var btn = $e(cmp.id + "!btn");
	if (btn) {
		zk.listen(btn, "click", function (evt) {
			if (!inp.disabled && !zk.dragging)
				zkDtbox.onbutton(cmp, evt);
		});
		zk.listen(btn, "mouseover", zul.ondropbtnover);
		zk.listen(btn, "mouseout", zul.ondropbtnout);
		zk.listen(btn, "mousedown", zul.ondropbtndown);
	}
	var pp = $e(cmp.id + "!pp");
	if (pp) // Bug #1912363
		zk.listen(pp, "click", zkDtbox.closepp);
};
zkDtbox.validate = function (cmp) {
	var inp = $e(cmp.id+"!real");
	if (inp.value) {
		var fmt = getZKAttr(cmp, "fmt"),
			bd = getZKAttr(cmp, "bd"),
			ed = getZKAttr(cmp, "ed"),
			d = zk.parseDate(inp.value, fmt, getZKAttr(cmp, "lenient") == "false");

		if (!d) return msgzul.DATE_REQUIRED+fmt;

		if (bd || ed) {
			if (bd) bd = new Date($int(bd) * 1000);
			if (ed) ed = new Date($int(ed) * 1000);
			if (zkDtbox._invalid(d, bd, ed)) {
				var s = msgzul.OUT_OF_RANGE + " (";
				if (bd) bd = zk.formatDate(bd, fmt);
				if (ed) ed = zk.formatDate(ed, fmt);
				if (bd && ed) s += bd + " ~ " + ed;
				else if (bd) s += ">= " +bd;
				else s += "<= " + ed;
				return s + ")";
			}
		}
		inp.value = zk.formatDate(d, fmt); //meta might not be ready
	}
	return null;
};
zkDtbox._invalid = function (d, begin, end) {
	return (begin && (d - begin)/86400000/*1000*60*60*24*/ < 0)
		|| (end && (end - d)/86400000 < 0);
};

/** Handles setAttr. */
zkDtbox.setAttr = function (cmp, nm, val) {
	if ("z.fmt" == nm) {
		zkau.setAttr(cmp, nm, val);

		var inp = $real(cmp);
		if (inp) {
			var d = zk.parseDate(inp.value, val);
			if (d) inp.value = zk.formatDate(d, val);
		}
		return true;
	} else if ("style" == nm) {
		var inp = $real(cmp);
		if (inp) zkau.setAttr(inp, nm, zk.getTextStyle(val, true, true));
	} else if ("style.width" == nm) {
		var inp = $real(cmp);
		if (inp) {
			inp.style.width = val;
			return true;
		}
	} else if ("style.height" == nm) {
		var inp = $real(cmp);
		if (inp) {
			inp.style.height = val;
			return true;
		}
	} else if ("z.sel" == nm ) {
		return zkTxbox.setAttr(cmp, nm, val);
	} else if ("z.btnVisi" == nm) {
		var btn = $e(cmp.id + "!btn");
		if (btn) btn.style.display = val == "true" ? "": "none";
		return true;
	}
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkDtbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var inp = $real(cmp);
		if (inp) zkau.rmAttr(inp, nm);
	} else if ("style.width" == nm) {
		var inp = $real(cmp);
		if (inp) inp.style.width = "";
	} else if ("style.height" == nm) {
		var inp = $real(cmp);
		if (inp) inp.style.height = "";
	}
	zkau.rmAttr(cmp, nm);
	return true;
};

zkDtbox.onkey = function (evt) {
	var inp = Event.element(evt);
	if (!inp) return true;

	var uuid = $uuid(inp.id);
	var pp = $e(uuid + "!pp");
	if (!pp) return true;

	var opened = $visible(pp);
	if (Event.keyCode(evt) == 9) { //TAB; IE: close now to show covered SELECT
		if (opened) zkDtbox.close(pp);
		return true; //don't eat
	}

	if (Event.keyCode(evt) == 38 || Event.keyCode(evt) == 40) {//UP/DN
		if (evt.altKey) {
			if (Event.keyCode(evt) == 38) { //UP
				if (opened) zkDtbox.close(pp);
			} else {
				if (!opened) zkDtbox.open(pp);
			}
			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			if (zk.ie) {
				Event.stop(evt);
				return false;
			}
			return true;
		}
		if (!opened) {
			zkDtbox.open(pp);
			Event.stop(evt);
			return false;
		}
	}

	if (opened) {
		var meta = zkau.getMeta(uuid);
		if (meta) {
			//Request 1551019: better responsive
			if (Event.keyCode(evt) == 13) { //ENTER
				meta.onchange();
				return true;
			}

			var ofs = Event.keyCode(evt) == 37 ? -1: Event.keyCode(evt) == 39 ? 1:
				Event.keyCode(evt) == 38 ? -7: Event.keyCode(evt) == 40 ? 7: 0;
			if (ofs) {
				meta.shift(ofs);
				inp.value = meta.getDateString();
				zk.asyncSelect(inp.id);
				Event.stop(evt);
				return false;
			}
		}
	}
	return true;
};

/* Whn the button is clicked on button. */
zkDtbox.onbutton = function (cmp, evt) {
	var pp = $e(cmp.id + "!pp");
	if (pp) {
		if (!$visible(pp)) zkDtbox.open(pp);
		else zkDtbox.close(pp, true);

		if (!evt) evt = window.event; //Bug 1911864
		Event.stop(evt);
	}
};
zkDtbox.dropdn = function (cmp, dropdown) {
	var pp = $e(cmp.id + "!pp");
	if (pp) {
		if ("true" == dropdown) zkDtbox.open(pp);
		else zkDtbox.close(pp, true);
	}
};

//SECTION TODO ADD CLASS
zkDtbox.open = function (pp) {
	pp = $e(pp);
	zkau.closeFloats(pp); //including popups
	zkau._dtbox.setFloatId(pp.id);

	var uuid = $uuid(pp.id);
	var db = $e(uuid);
	if (!db) return;

	var zcls = getZKAttr(db,"zcls");
	pp.className=db.className+" "+pp.className;
	zk.rmClass(pp, zcls);

	var meta = zkau.getMeta(db);
	if (meta) meta.init();
	else zkau.setMeta(db, new zk.Cal(db, pp));

	pp.style.width = pp.style.height = "auto";
	pp.style.position = "absolute"; //just in case
	pp.style.overflow = "auto"; //just in case
	pp.style.display = "block";
	pp.style.zIndex = "88000";
	//No special child, so no need to: zk.onVisiAt(pp);

	//FF: Bug 1486840
	//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
	zk.setVParent(pp);

	//fix size
	if (pp.offsetHeight > 200) {
		pp.style.height = "200px";
		pp.style.width = "auto"; //recalc
	} else if (pp.offsetHeight < 10) {
		pp.style.height = "10px"; //minimal
	}
	if (pp.offsetWidth < db.offsetWidth) {
		pp.style.width = db.offsetWidth + "px";
	} else {
		var wd = zk.innerWidth() - 20;
		if (wd < db.offsetWidth) wd = db.offsetWidth;
		if (pp.offsetWidth > wd) pp.style.width = wd;
	}

	var input = $e(db.id + "!real");
	zk.position(pp, input, "after-start");

	setTimeout("zkDtbox._repos('"+uuid+"')", 3);
		//IE issue: we have to re-position again because some dimensions
		//might not be correct here
};
/** Re-position the popup. */
zkDtbox._repos = function (uuid) {
	var db = $e(uuid);
	if (!db) return;

	var pp = $e(uuid + "!pp");
	var inpId = db.id + "!real";
	var inp = $e(inpId);

	zk.position(pp, inp, "after-start");
	zkau.hideCovered();
	zk.asyncFocus(inpId);
};
//TODO Remove Class
zkDtbox.close = function (pp, focus) {
	var uuid = $uuid(pp.id);

	pp.style.display = "none";
	zk.unsetVParent(pp);


	var db = $e(uuid);
	if (!db) return;
	var zcls = getZKAttr(db,"zcls");
	pp.className=zcls+"-pp";

	pp = $e(pp);
	zkau._dtbox.setFloatId(null);
	//No special child, so no need to: zk.onHideAt(pp);
	zkau.hideCovered();

	var btn = $e(uuid + "!btn");
	if (btn) zk.rmClass(btn, getZKAttr($e(uuid), "zcls") + "-btn-over");

	if (focus)
		zk.asyncFocus(uuid + "!real");
};
zkDtbox.closepp = function (evt) {
	if (!evt) evt = window.event;
	var pp = Event.element(evt);
	for (; pp; pp = pp.parentNode) {
		if (pp.id) {
			if (pp.id.endsWith("!pp"))
				zkDtbox.close(pp, true);
			return; //done
		}
		if (pp.onclick) return;
	}
};
zk.FloatDatebox = zClass.create();
Object.extend(Object.extend(zk.FloatDatebox.prototype, zk.Float.prototype), {
	_close: function (el) {
		zkDtbox.close(el);
	}
});
if (!zkau._dtbox)
	zkau.floats.push(zkau._dtbox = new zk.FloatDatebox()); //hook to zkau.js
