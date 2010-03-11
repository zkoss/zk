/* db.js

{{IS_NOTE
	Purpose:
		datebox
	Description:

	History:
		Mon Oct 17 15:24:01	 2005, Created by tomyeh
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
		var zcls = getZKAttr(cmp.id, "zcls");
		
		this.id = cmp.id;
		this.popup = popup;
		this.input = $e(cmp.id + "!real");
		this._tm = new zkTimebox(this.id, zcls);
		this._newCal();
		this.init();
	},
	setValue: function (date) {
		if (!date) return;
		this.date = date;
	},
	getValue: function () {
		return this.date;
	},
	getRawText: function () {
		return this.coerceToString_(this.date);
	},
	cleanup: function ()  {
		if (this.fnSubmit)
			zk.unlisten(this.form, "submit", this.fnSubmit);
		if (this.popup) {
			var uuid = this.id;
			this._tm.unbind_();
		}
		this.element = this.fnSubmit = null;
	},
	_newCal: function() {
		this.element = $e(this.id);
		if (!this.element) return;
		var uuid = this.id;
			zcls = getZKAttr(this.element, "zcls"),
			compact = getZKAttr(this.element, "compact") == "true",
			html = this.popup ? '<table border="0" cellspacing="0" cellpadding="0" tabindex="-1">': '';
			dtzones = getZKAttr(this.element, "dtzones");

		html += '<tr><td><table class="'+zcls+'-calyear" width="100%" border="0" cellspacing="0" cellpadding="0"><tr><td width="5"></td><td align="right"><img src="'
			+zk.getUpdateURI('/web/zul/img/cal/arrowL.gif')
			+'" style="cursor:pointer" onclick="zkCal.onyearofs(event,-1)" id="'
			+uuid+'!ly"/></td>';

		if (compact)
			html += '<td align="right"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrow2L.gif')
				+'" style="cursor:pointer" onclick="zkCal.onmonofs(event,-1)" id="'
				+uuid+'!lm"/></td>';

		html += '<td width="5"></td><td id="'+uuid+'!title"></td><td width="5"></td>';

		if (compact)
			html += '<td align="left"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrow2R.gif')
				+'" style="cursor:pointer" onclick="zkCal.onmonofs(event,1)" id="'
				+uuid+'!rm"/></td>';

		html += '<td align="left"><img src="'+zk.getUpdateURI('/web/zul/img/cal/arrowR.gif')
			+'" style="cursor:pointer" onclick="zkCal.onyearofs(event,1)" id="'
			+uuid+'!ry"/></td><td width="5"></td></tr></table></td></tr>';

		if (!compact) {
			html += '<tr><td><table class="'+zcls+'-calmon" width="100%" border="0" cellspacing="0" cellpadding="0"><tr>';
			for (var j = 0 ; j < 12; ++j) {
				html += '<td id="'+uuid+'!m'+j
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
			html += '<tr class="'+zcls+'-calday" id="'+uuid+'!w'+j
				+'" onclick="zkCal.ondayclk(event)" onmouseover="zkCal.onover(event)" onmouseout="zkCal.onout(event)">';
			for (var k = 0; k < 7; ++k)
				html += '<td></td>';
			html += '</tr>'
		}
		html += '</table></td></tr></table>';

		if (this.popup) {
			html += '<span id="' + uuid + '!timebox" class="' + zcls + '-timebox">'+
			'<input id="' + uuid + '!timebox-real" class="' + zcls + '-timebox-inp" type="text" value="" autocomplete="off"/>'+
			'<span id="' + uuid + '!timebox-btn" class="' + zcls + '-timebox-btn" onclick="zkCal.block(event)">'+
			'<span class="' + zcls + '-timebox-img">'+
			'</span></span></span>';
	
			html += '<div class="' + zcls + '-timezone">';
			if (dtzones) html += zkDtbox.getTimeZoneLabel();
			html += '<select id="' + uuid + '!dtzones" class="' + zcls + '-timezone-body" onchange="zkCal.ondtzoneschg(event)" onclick="zkCal.block(event)">'
			if (dtzones) {
				var dtzonenames = dtzones.split(",");
				for (var i = 0, len = dtzonenames.length; i < len; i++) {
					html += '<option value="' + dtzonenames[i] + '" class="' + zcls + '-timezone-item">' + dtzonenames[i] + '</option>';
				}
			}
			html += '</select></div>';		
		}

		zk.setInnerHTML(this.popup || this.element, html);
		if (this.popup) {
			this._tm.bind_();
			this.setDtzones();
			this._updateCal(this.element);
		}
		this.form = zk.formOf(this.element);
		if (this.form && !this.fnSubmit) {
			var meta = this;
			this.fnSubmit = function () {
				meta.onsubmit();
			};
			zk.listen(this.form, "submit", this.fnSubmit);
		}
	},
	setDtzones: function () {
		var selectCmp = $e(this.element, "dtzones"),
			dtimezone = getZKAttr(this.element, "dtimezone"),
			isDisplayTimezones = false;
			
		if (selectCmp) {
			var	optionChilds = zk.childNodes(selectCmp);
			if (dtimezone && optionChilds) {
				for (var i = optionChilds.length; i--;) {
					if (dtimezone == optionChilds[i].text) {
						selectCmp.selectedIndex = i;
						isDisplayTimezones = true;
						break;
					}
				}
				action[isDisplayTimezones ? "show" : "hide"](selectCmp);
			} else {
				action.hide(selectCmp);
			}	
		}
	},
	init: function () {
		this.element = $e(this.id);
		if (!this.element) return;

		var val = this.input ? this.input.value: getZKAttr(this.element, "value"),
			bd = getZKAttr(this.element, "bd"),
			ed = getZKAttr(this.element, "ed");
			
		if (this.popup) this.setDtzones();
		if (val) val = zk.parseDate(val, this.getFormat());
		this.date = val ? val: new Date();
		if (bd) this.begin = new Date($int(bd) * 1000);
		if (ed) this.end = new Date($int(ed) * 1000);
		this._updateCal(this.element);
		this._output();
	},
	getFormat: function () {
		var fmt = getZKAttr(this.element, "fmt");
		return fmt ? fmt: "yyyy/MM/dd";
	},
	getTimeFormat: function () {
		var fmt = this.getFormat(),
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
			if ((hh != -1 && aa < hh) || (kk != -1 && aa < kk)) {
				var f = hh < KK ? 'a KK' : 'a hh';
				return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
			} else {
				var f = hh < KK ? 'KK' : 'hh';
				f = f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
				return f + ' a';
			}
		} else {
			var f = HH < kk ? 'kk' : HH > -1 ? 'HH' : '';
			return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
		}
	},
	getDateFormat: function () {
		return this.getFormat().replace(/[ahKHksm]/g, '');
	},
	coerceToString_: function (val) {
		return val ? zk.formatDate(val, this.getFormat()) : '';
	},
	today: function () {
		var d = new Date();
		return new Date(d.getFullYear(), d.getMonth(), d.getDate());
	},
	_updateCal: function(cmp) {
		var zhr = getZKAttr(cmp, "hr") == "true",
			zmin = getZKAttr(cmp, "min") == "true",
			za = getZKAttr(cmp, "ampm") == "true",
			selectCmp = $e(this.element, "dtzones"),
			timeCmp = $e(this.element, "timebox"),
			dTimezonesReadonly = getZKAttr(this.element, "dtzonesReadonly") == "true";
		
		if (selectCmp)
			selectCmp.disabled = dTimezonesReadonly ? "disabled" : "";
		if (timeCmp)
			this._tm.setVisible(zhr || zmin || za ? true : false);
	},
	_output: function () {
		//year
		var val = this.date,
			m = val.getMonth(),
			d = val.getDate(),
			y = val.getFullYear(),
			el = $e(this.id + "!title"),
			zcls = getZKAttr(this.element, "zcls"),
			fmt = getZKAttr(this.element, "fmt");
		zk.setInnerHTML(el, zk.SMON[m] + ', ' + y);
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
	getInputNode: function () {
		return $e(this.id, 'real') || this.element;
	},
	getTimeInputNode: function () {
		return $e(this.id, "timebox-real");
	},
	_invalid: function (d) {
		// clean the date time. Bug #2788618
		var d2 = new Date(d.getTime());

		d2.setHours(0);
		d2.setMinutes(0);
		d2.setMilliseconds(0);
		return zkDtbox._invalid(d2, this.begin, this.end);
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
			d = zk.getIntAttr(cell, "zk_day"),
			hr = this.date.getHours(),
			min = this.date.getMinutes(),
			sec = this.date.getSeconds();

		if (!zkCal._seled(cell)) { //!selected
			var monofs = zk.getIntAttr(cell, "zk_monofs"),
				now = new Date(y, m + monofs, d, hr, min, sec);
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

		this._onupdate({close: true});
	},
	_onmonclk: function (cell) {
		if (!zkCal._seled(cell)) { //!selected
			var y = this.date.getFullYear(),
				d = this.date.getDate(),
				hr = this.date.getHours(),
				min = this.date.getMinutes(),
				sec = this.date.getSeconds();
			this._setDateMonChg(y, zk.getIntAttr(cell, "zk_mon"), d, hr, min, sec);
			this._output();
			this._onupdate({close: false});
			zkDtbox._repos($uuid(this));
		}
	},
	_onyearofs: function (ofs) {
		var y = this.date.getFullYear(),
			m = this.date.getMonth(),
			d = this.date.getDate(),
			hr = this.date.getHours(),
			min = this.date.getMinutes(),
			sec = this.date.getSeconds();
		this.date = new Date(y + ofs, m, d, hr, min, sec);
		this._output();
		this._onupdate({close: false});
		zkDtbox._repos($uuid(this));
	},
	_onmonofs: function (ofs) {
		var y = this.date.getFullYear(),
			m = this.date.getMonth(),
			d = this.date.getDate(),
			hr = this.date.getHours(),
			min = this.date.getMinutes(),
			sec = this.date.getSeconds();
		this.date = new Date(y, m + ofs, d, hr, min, sec);
		this._output();
		this._onupdate({close: false});
		zkDtbox._repos($uuid(this));

	},
	_ondtzoneschg: function(target) {
		var cmp = $(this.id),
			selectCmp = $e(this.id, "dtzones"),
			childs = zk.childNodes(selectCmp),
			idx = selectCmp.selectedIndex,
			timezone = childs[idx].text;

		this._onupdate({close: false});
		if (zkau.asap(cmp, "onTimeZoneChange")) {
			zkau.send({uuid: this.id, cmd: "onTimeZoneChange", data: [timezone]});
		}
	},
	/** Sets date caused by the change of month (it fixed 6/31 issue).
	 */
	_setDateMonChg: function (y, m, d, hr, min, sec) {
		this.date = new Date(y, m, d, hr, min, sec);
		if (m >= 0) { //just in case
			m %= 12;
			while (this.date.getMonth() != m) //6/31 -> 7/1
				this.date = new Date(y, m, --d, hr, min, sec);
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

				var d = val.getDate(),
					hr = this.date.getHours(),
					min = this.date.getMinutes();
				for (var j = 0; j < 6; ++j) {
					el = $e(this.id + "!w" +j);
					for (var k = 0; k < 7; ++k) {
						var cell = el.cells[k];
						if (zk.getIntAttr(cell, "zk_monofs") == 0
						&& zk.getIntAttr(cell, "zk_day") == d) {
							this._outcell(cell, true,
								this._invalid(new Date(year, mon, d, hr, min)));
							break;
						}
					}
				}
			}
		}
		this.getInputNode().value = this.getDateString();
		this._tm.setTime(this.date);
	},
	/** Calls selback or onchange depending on this.popup. */
	_onupdate: function (opts) {
		this._output();
		if (this.popup) {
			this.selback(opts && opts.close);
			if (this.input) {
				//Request 1551019: better responsive
				if (!opts || opts.sendOnChange !== false) {
					this.onchange();
					zk.asyncFocus(this.input.id);
				}
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
				d = this.date.getDate(),
				hr = this.date.getHours(),
				min = this.date.getMinutes();
			setZKAttr(this.element, "value", this.getDateString());
			zkau.sendasap({uuid: this.id, cmd: "onChange",
				data: [y+'/'+(m+1)+'/'+d+'/'+hr+'/'+min]});
			this._changed = false;
		}
	},
	selback: function (close) {
		if (this.input) {
			this.input.value = this.getDateString();
			if (close) {
				zk.asyncFocus(this.input.id);
				zk.asyncSelect(this.input.id);
			}
		}
		if (close) {
			zkau.closeFloats(this.element);
		}
	},
	getDateString: function () {
		return zk.formatDate(this.date, this.getFormat());
	},
	shift: function (days) {
		var val = this.date;
		this.setDate(new Date(
			val.getFullYear(), val.getMonth(), val.getDate() + days, val.getHours(), val.getMinutes(), val.getSeconds()));
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
zkCal.ondtzoneschg = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
		meta = zkau.getMeta($uuid(target));
	if (meta) meta._ondtzoneschg(target);
};
zkCal.block = function (evt) {
	if (!evt) evt = window.event;
	Event.stop(evt);
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

	var inp = $real(cmp);
	zkTxbox.init(inp);
	zk.listen(inp, "keydown", zkDtbox.onkey);
		//IE: use keydown. otherwise, it causes the window to scroll
	zkDtbox.setTimeAttr(cmp, getZKAttr(cmp,'fmt'));
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
zkDtbox.cleanup = function (cmp) {
	var pp = $e(cmp.id + "!pp");
	if (pp && pp._shadow) {
		pp._shadow.cleanup();
		pp._shadow = null;
	}
	zkTxbox.cleanup(cmp);
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

			// clean the date time. Bug #2788618
			var d2 = new Date(d.getTime());

			d2.setHours(0);
			d2.setMinutes(0);
			d2.setSeconds(0);
			d2.setMilliseconds(0);
			if (zkDtbox._invalid(d2, bd, ed)) {
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
/**Check format whether have hHkKma*/
zkDtbox.setTimeAttr = function(cmp, fmt) {
	var chk = fmt.toLowerCase();
	setZKAttr(cmp, "hr", (chk.indexOf("k") > -1 || chk.indexOf("h") > -1 ) ? 'true' : 'false');
	setZKAttr(cmp, "min", (fmt.indexOf("m") > -1 ? 'true' : 'false'));//the minutes only in lowercase
	setZKAttr(cmp, "ampm", (chk.indexOf("a") > -1  ? 'true' : 'false'));

}
/** Handles setAttr. */
zkDtbox.setAttr = function (cmp, nm, val) {
	if ("z.fmt" == nm) {
		zkau.setAttr(cmp, nm, val);
		var inp = $real(cmp);
		zkDtbox.setTimeAttr(cmp, val);
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
	} else if ("z.sel" == nm) {
		return zkTxbox.setAttr(cmp, nm, val);
	} else if ("z.btnVisi" == nm) {
		var btn = $e(cmp.id + "!btn");
		if (btn) btn.style.display = val == "true" ? "": "none";
		return true;
	} else if ("z.value" == nm) {
		var meta = zkau.getMeta(cmp);
		if (meta) {
			var date = zk.parseDate(val, meta.getFormat());
			meta.setDate(date);
		}
		return true;
	} else if ("z.dtimezone" == nm) {
		var meta = zkau.getMeta(cmp);
		if (meta) {
			setZKAttr(cmp, "dtimezone", val);
			meta.setDtzones();
		}
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
	
	meta = zkau.getMeta(db);
	if (meta) {
		var fmt = meta.getTimeFormat(),
			tm = meta._tm;
		if (fmt) {
			tm.setVisible(true);
			tm.setFormat(fmt);
			tm.setValue(meta.getValue());
		}
		else {
			tm.setVisible(false);
		}
	}

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
		//pp.style.height = "200px"; commented by the bug #2796461
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

	setTimeout("zkDtbox._repos('"+uuid+"')", 150);
		//IE, Opera, and Safari issue: we have to re-position again because some dimensions
		//in Chinese language might not be correct here.
	zkDtbox.fixbtnpos(uuid);
};
zkDtbox.fixbtnpos = function (cmp) {
	var btn = $e(cmp, "timebox-btn");

	if (zk.isRealVisible(btn) && btn.style.position != 'relative') {
		var ref = $e(cmp, "timebox-real"),
			img = zk.firstChild(btn, 'SPAN');
			refh = ref.offsetHeight,
			imgh = img.offsetHeight;
		if (!refh || !imgh) {
			return;
		}

		//Bug 1738241: don't use align="xxx"
		var v = refh - imgh;
		if (v) {
			var imghgh = $int(Element.getStyle(img, "height")) + v;
			img.style.height = (imghgh < 0 ? 0 : imghgh) + "px";
		}

		v = ref.offsetTop - img.offsetTop;
		btn.style.position = "relative";
		btn.style.top = v + "px"; //might be negative
		if (zk.safari) btn.style.left = "-2px";
	}
};
/** Re-position the popup. */
zkDtbox._repos = function (uuid) {
	var db = $e(uuid);
	if (!db) return;

	var pp = $e(uuid + "!pp");
	var inpId = db.id + "!real";
	var inp = $e(inpId);

	if(pp) {
		zk.position(pp, inp, "after-start");

		if (!pp._shadow)
			pp._shadow = new zk.Shadow(pp, {left: -4, right: 4, top: 2, bottom: 3, autoShow: true, stackup: (zk.useStackup === undefined ? zk.ie6Only: zk.useStackup)});
		else
			pp._shadow.sync();
		zkau.hideCovered();
		zk.asyncFocus(inpId);
	}
};
//Remove Class
zkDtbox.close = function (pp, focus) {
	var uuid = $uuid(pp.id);
	if (pp._shadow) pp._shadow.hide();
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

	var meta = zkau.getMeta(uuid);
	if (meta._changed) {
		meta.onchange();
	}
};
zkDtbox.closepp = function (evt) {
	if (!evt) evt = window.event;
	var pp = Event.element(evt);
	for (; pp; pp = pp.parentNode) {
		//the up/down arrow, marker, and input
		if (pp.onclick || $tag(pp) == "INPUT" || $tag(pp) == "BUTTON") return;
		if (pp.id && pp.id.endsWith("!pp")) {
			zkDtbox.close(pp, true);
			return;//Done
		}
	}
};
zkDtbox.getTimeZoneLabel = function () {
	return "";
};
zk.FloatDatebox = zClass.create();
Object.extend(Object.extend(zk.FloatDatebox.prototype, zk.Float.prototype), {
	_close: function (el) {
		zkDtbox.close(el);
	}
});
if (!zkau._dtbox)
	zkau.floats.push(zkau._dtbox = new zk.FloatDatebox()); //hook to zkau.js

function zkTimebox (uuid, zcls) {
	this.uuid = uuid;
	this._zclass = zcls + '-timebox';
}
zkTimebox.prototype = {
	LEGAL_CHARS: 'ahKHksm',
	/**Useful constant for MINUTE (m) field alignment.*/
	MINUTE_FIELD: 1,
	/**Useful constant for SECOND (s) field alignment.*/
	SECOND_FIELD: 2,
	/**Useful constant for AM_PM (a) field alignment.*/
	AM_PM_FIELD: 3,
	/**Useful constant for HOUR0 (H) field alignment. (Hour in day (0-23))*/
	HOUR0_FIELD: 4,
	/**Useful constant for HOUR1 (k) field alignment. (Hour in day (1-24))*/
	HOUR1_FIELD: 5,
	/**Useful constant for HOUR2 (h) field alignment. (Hour in am/pm (1-12))*/
	HOUR2_FIELD: 6,
	/**Useful constant for HOUR3 (K) field alignment. (Hour in am/pm (0-11))*/
	HOUR3_FIELD: 7,
	_format: 'HH:mm',
	setVisible: function (visible) {
		var inp = this.getInputNode(),
			btn = $e(this.uuid, "timebox-btn");
		visible ? action.show(inp) : action.hide(inp);
		visible ? action.show(btn) : action.hide(btn);
	},
	setFormat: function (fmt, date) {
		var inp = this.getInputNode();
		if (fmt != this._fmt) {
			var old = this._fmt;
			this._fmt = fmt;
			this._parseFormat(fmt);
			if (date)
				inp.value = this.coerceToString_(date);
		}
	},
	setValue: function (date) {
		var inp = this.getInputNode();
		if (date)
			inp.value = this.coerceToString_(date);
	},
	setTime: function (date) {
		var inp = this.getInputNode();
		if (date && this._fmthdler)
			inp.value = this.coerceToString_(date);
	},
	getRawText: function () {
		return this.coerceToString_(this._value);
	},
	_checkFormat: function (fmt) {
		var error, out = [];
		for (var i = 0, j = fmt.length; i < j; i++) {
			var c = fmt.charAt(i);
			switch (c) {
			case 'K':
			case 'h':
			case 'H':
			case 'k':
			case 'm':
			case 's':
				if (fmt.charAt(i+1) == c)
					i++;
				else
					error = true;
				out.push(c + c);
				break;
			default:
				out.push(c);
			}
		}
		if (error)
			return msgzul.DATE_REQUIRED + out.join('');
	},
	_parseFormat: function (fmt) {
		var index = [];
		for (var i = 0, j = fmt.length; i < j; i++) {
			var c = fmt.charAt(i);
			switch (c) {
			case 'a':
				var len = zk.APM[0].length;
				index.push(new AMPMHandler([i, i + len - 1], this.AM_PM_FIELD));
				break;
			case 'K':
				var start = i,
					end = fmt.charAt(i+1) == 'K' ? ++i : i;
				index.push(new HourHandler2([start, end], this.HOUR3_FIELD));
				break;
			case 'h':
				var start = i,
					end = fmt.charAt(i+1) == 'h' ? ++i : i;
				index.push(new HourHandler([start, end], this.HOUR2_FIELD));
				break;
			case 'H':
				var start = i,
					end = fmt.charAt(i+1) == 'H' ? ++i : i;
				index.push(new HourInDayHandler([start, end], this.HOUR0_FIELD));
				break;;
			case 'k':
				var start = i,
					end = fmt.charAt(i+1) == 'k' ? ++i : i;
				index.push(new HourInDayHandler2([start, end], this.HOUR1_FIELD));
				break;
			case 'm':
				var start = i,
					end = fmt.charAt(i+1) == 'm' ? ++i : i;
				index.push(new MinuteHandler([start, end], this.MINUTE_FIELD));
				break;
			case 's':
				var start = i,
					end = fmt.charAt(i+1) == 's' ? ++i : i;
				index.push(new SecondHandler([start, end], this.SECOND_FIELD));
				break;
			default:
				var ary = [],
					start = i,
					end = i;

				while ((ary.push(c)) && ++end < j) {
					c = fmt.charAt(end);
					if (this.LEGAL_CHARS.indexOf(c) != -1) {
						end--;
						break;
					}
				}
				index.push({index: [start, end], format: (function (text) {
					return function() {
						return text;
					};
				})(ary.join(''))});
				i = end;
			}
		}
		for (var shift, i = 0, j = index.length; i < j; i++) {
			if (index[i].type == this.AM_PM_FIELD) {
				shift = index[i].index[1] - index[i].index[0];
				if (!shift) break; // no need to shift.
			} else if (shift) {
				index[i].index[0] += shift;
				index[i].index[1] += shift;
			}
		}
		this._fmthdler = index;
	},
	coerceToString_: function (date) {
		if (!this._fmthdler) return '';
		var out = [];
		for (var i = 0, j = this._fmthdler.length; i < j; i++)
			out.push(this._fmthdler[i].format(date));
		return out.join('');
	},
	coerceFromString_: function (val) {
		if (!val) return null;

		var error;
		if ((error = this._checkFormat(this._format)))
			return {error: error};

		if (!this._fmthdler)
			this._parseFormat(this._format);

		var date = new Date(),
			hasAM, isAM, hasHour1,
			fmt = [];

		for (var i = 0, j = this._fmthdler.length; i < j; i++) {
			if (this._fmthdler[i].type == this.AM_PM_FIELD) {
				hasAM = true;
				isAM = this._fmthdler[i].unformat(date, val);
			} else if (this._fmthdler[i].type)
				fmt.push(this._fmthdler[i]);
		}

		if (hasAM) {
			for (var i = 0, j = fmt.length; i < j; i++) {
				if (fmt[i].type == this.HOUR2_FIELD || fmt[i].type == this.HOUR3_FIELD) {
					hasHour1 = true;
					break;
				}
			}
		}

		if (hasHour1) {
			for (var i = 0, j = fmt.length; i < j; i++) {
				if (fmt[i] != this.HOUR0_FIELD && fmt[i] != this.HOUR1_FIELD)
					date = fmt[i].unformat(date, val, isAM);
			}
		} else {
			for (var i = 0, j = fmt.length; i < j; i++) {
				if (fmt[i] != this.HOUR2_FIELD && fmt[i].type != this.HOUR3_FIELD)
					date = fmt[i].unformat(date, val);
			}
		}
		return date;
	},
	getZclass: function () {
		if (!this._zclass) {
			this._zclass = getZKAttr(cmp.id, "zcls") + "-timebox";
		}
		return this._zclass;
	},
	getInputNode: function(){
		return $e(this.uuid, "timebox-real");
	},
	doClick_: function (evt) {
		var target = Event.element(evt),
			inp = this.getInputNode();
		if (target == inp) this._doCheckPos(this._getPos());
	},
	doKeyDown_: function(evt){
		var inp = this.getInputNode();
		if (inp.disabled || inp.readOnly)
			return;

		this.lastPos = this._getPos();
		var code = evt.keyCode;
		switch(code){
		case 48:case 96://0
		case 49:case 97://1
		case 50:case 98://2
		case 51:case 99://3
		case 52:case 100://4
		case 53:case 101://5
		case 54:case 102://6
		case 55:case 103://7
		case 56:case 104://8
		case 57:case 105://9
			code = code - (code>=96?96:48);
			this._doType(code);
			Event.stop(evt);
			return;
		case 37://left
			this._doLeft();
			Event.stop(evt);
			return;
		case 38://up
			this._doUp();
			Event.stop(evt);
			return;
		case 39://right
			this._doRight();
			Event.stop(evt);
			return;
		case 40://down
			this._doDown();
			Event.stop(evt);
			return;
		case 46://del
			this._doDel();
			Event.stop(evt);
			return;
		case 8://backspace
			this._doBack();
			Event.stop(evt);
			return;
		case 9:
			// do nothing
			break;
		case 35://end
		case 36://home
			this._doCheckPos(code == 36 ? 0 : inp.value.length);
			Event.stop(evt);
			return;
		case 13: case 27://enter,esc,tab
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				Event.stop(evt);
		}
	},
	_dodropbtnup: function (evt) {
		zk.rmClass(this._currentbtn, this.getZclass() + "-btn-clk");
		zk.unlisten(document.body, "mouseup", zkTimebox.dodropbtnup);
		this._currentbtn = null;
	},
	_btnDown: function(evt) {
		var inp = this.getInputNode(),
			btn = $e(this.uuid, "timebox-btn"),
			cmp = $e(this.uuid, "timebox");

		if(!inp || inp.disabled) return;
		if (this._currentbtn)
			this._dodropbtnup(evt);

		zk.addClass(btn, this.getZclass() + "-btn-clk");
		zk.listen(document.body, "mouseup", zkTimebox.dodropbtnup);
		this._currentbtn = btn;

		btn = zk.opera || zk.safari ? btn : btn.firstChild;

		if (!this._fmthdler)
			this._parseFormat(this._format);

		if (!inp.value)
			inp.value = this.coerceToString_();

		var ofs = zk.revisedOffset(btn),
			clkY = zk.ie ? evt.clientY + document.body.scrollTop - ofs[1]: evt.pageY - ofs[1],
			offsetY = btn.offsetHeight / 2;

		if (clkY < offsetY) { //up
			this._doUp();
			this._startAutoIncProc(true);
		} else {
			this._doDown();
			this._startAutoIncProc(false);
		}

		// cache it for IE
		this._lastPos = this._getPos();
	},
	_btnUp: function(evt){
		var inp = this.getInputNode();
		if(inp.disabled || zk.dragging) return;

		this.onChanging();
		this._stopAutoIncProc();

		if (zk.ie && this._lastPos)
			zk.setSelectionRange(inp, this._lastPos, this._lastPos);

		inp.focus();
	},
	_btnOut: function(evt){
		var inp = this.getInputNode(),
			btn = $e(this.uuid, "timebox-btn");
		if(!inp || inp.disabled || zk.dragging) return;

		zk.rmClass(btn, this.getZclass()+"-btn-over");
		this._stopAutoIncProc();
	},
	_btnOver: function(evt){
		var btn = $e(this.uuid, "timebox-btn");
		if (this.getInputNode() && !this.getInputNode().disabled && !zk.dragging)
			zk.addClass(btn, this.getZclass()+"-btn-over");
	},
	_getPos: function (){
		return zk.getSelectionRange(this.getInputNode())[1];
	},
	_doCheckPos: function (pos) {
		var inp = this.getInputNode();

		if (!this._fmthdler)
			this._parseFormat(this._format);

		for (var i = 0, j = this._fmthdler.length; i < j; i++) {
			var idx = this._fmthdler[i];
			if (idx.index[1] + 1 == pos) {
				if (idx.type) break;// in a legal area
				var end = i;
				while(this._fmthdler[++end]) {
					if (this._fmthdler[end].type == this.AM_PM_FIELD) {
						pos = this._fmthdler[end].index[1] + 1;
						break;
					} else if (this._fmthdler[end].type) {
						pos = this._fmthdler[end].index[0] + 1;
						break;
					}
				}
				break;
			} else if (idx.index[0] <= pos && idx.index[1] + 1 >= pos) {
				if (!idx.type) {
					var end = i;

					// check if it is end
					if (this._fmthdler[end + 1]) {
						while (this._fmthdler[++end]) {
							if (this._fmthdler[end].type) {
								pos = this._fmthdler[end].index[0] + 1;
								break;
							}
						}
					} else {
						while (this._fmthdler[--end]) {
							if (this._fmthdler[end].type) {
								pos = this._fmthdler[end].index[1] + 1;
								break;
							}
						}
					}
				}  else if (idx.type == this.AM_PM_FIELD) {
					pos = idx.index[1] + 1;
					break;
				} else {
					if (idx.index[0] == pos) pos++;
					break;// in a legal area
				}
			}
		}

		zk.setSelectionRange(inp, pos, pos);
		this.lastPos = pos;
	},
	_doLeft: function () {
		var inp = this.getInputNode(),
			pos = this.lastPos - 1,
			hdler = this.getTimeHandler();
		for (var i = 0, j = this._fmthdler.length; i < j; i++) {
			var idx = this._fmthdler[i];
			if (idx.index[0] == pos) {
				var end = i;
				pos++;
				while (this._fmthdler[--end]) {
					if (this._fmthdler[end].type) {
						pos = this._fmthdler[end].index[1] + 1;
						break;
					}
				}
				break;
			} else if (idx.index[0] < pos && idx.index[1] >= pos) {
				if (!idx.type || idx.type == this.AM_PM_FIELD) {
					var end = i;
					pos++;
					while (this._fmthdler[--end]) {
						if (this._fmthdler[end].type) {
							pos = this._fmthdler[end].index[1] + 1;
							break;
						}
					}
				} else
					break;// in a legal area
			}
		}
		if (hdler.type && hdler.type != this.AM_PM_FIELD) {
			if (pos <= hdler.index[0] || pos > hdler.index[1] + 1) {
				var val = inp.value, text = val.substring(hdler.index[0], hdler.index[1] + 1);
				text = text.replace(/ /g, '0');
				inp.value = val.substring(0, hdler.index[0]) + text + val.substring(hdler.index[1] + 1, val.length);
			}
		}

		zk.setSelectionRange(inp, pos, pos);
		this.lastPos = pos;
	},
	_doRight: function() {
		var inp = this.getInputNode(), 
			pos = this.lastPos + 1, 
			hdler = this.getTimeHandler();
		
		for (var i = 0, j = this._fmthdler.length; i < j; i++) {
			var idx = this._fmthdler[i];
			if (idx.index[1] + 2 == pos) {
				var end = i;
				pos--;
				while (this._fmthdler[++end]) {
					if (this._fmthdler[end].type == this.AM_PM_FIELD) {
						pos = this._fmthdler[end].index[1] + 1;
						break;
					} else if (this._fmthdler[end].type) {
						pos = this._fmthdler[end].index[0] + 1;
						break;	
					}
				}
				break;
			} else if (idx.index[0] < pos && idx.index[1] + 1 >= pos) {
				if (!idx.type || idx.type == this.AM_PM_FIELD) {
					var end = i;
					pos--;
					while (this._fmthdler[++end]) {
						if (this._fmthdler[end].type) {
							pos = this._fmthdler[end].index[0] + 1;
							break;
						}
					}
				} else
					break;// in a legal area
			}
		}
		if (hdler.type && hdler.type != this.AM_PM_FIELD) {
			if (pos <= hdler.index[0] || pos > hdler.index[1] + 1) {
				var val = inp.value, text = val.substring(hdler.index[0], hdler.index[1] + 1);
				text = text.replace(/ /g, '0');
				inp.value = val.substring(0, hdler.index[0]) + text + val.substring(hdler.index[1] + 1, val.length);
			}
		}
		zk.setSelectionRange(inp, pos, pos);
		this.lastPos = pos;
	},
	_doUp: function() {
		this.getTimeHandler().increase(this, 1);
		this.onChanging();
	},
	_doDown: function() {
		this.getTimeHandler().increase(this, -1);
		this.onChanging();
	},
	_doBack: function () {
		this.getTimeHandler().deleteTime(this, true);
	},
	_doDel: function () {
		this.getTimeHandler().deleteTime(this, false);
	},
	_doType: function (val) {
		this.getTimeHandler().addTime(this, val);
		this.onChanging();
	},
	getTimeHandler: function () {
		var pos = this._getPos(),
			lastHdler;
		for (var i = 0, f = this._fmthdler, j = f.length; i < j; i++) {
			if (!f[i].type) continue;
			if (f[i].index[0] < pos && f[i].index[1] + 1 >= pos)
				return f[i];
			lastHdler = f[i];
		}
		return lastHdler;
	},
	_startAutoIncProc: function(up) {
		if (this.timerId)
			clearInterval(this.timerId);
		var self = this,
			fn = up ? '_doUp' : '_doDown';
		this.timerId = setInterval(function() {
			self[fn]();
		}, 300);
	},
	_stopAutoIncProc: function() {
		if (this.timerId)
			clearTimeout(this.timerId);
		this.currentStep = this.defaultStep;
		this.timerId = null;
	},
	bind_: function () {
		var inp = this.getInputNode(),
			btn = $e(this.uuid, "timebox-btn");

		if (btn) {
			zk.listen(btn, "mousedown", zkTimebox.btnDown);
			zk.listen(btn, "mouseup", zkTimebox.btnUp);
			zk.listen(btn, "mouseout", zkTimebox.btnOut);
			zk.listen(btn, "mouseover", zkTimebox.btnOver);
			zk.listen(inp, "keydown", zkTimebox.keyDown);
			zk.listen(inp, "click", zkTimebox.doClick);
		}
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		var inp = this.getInputNode(),
			btn = $e(this.uuid, "timebox-btn");
		if (btn) {
			zk.unlisten(btn, "mousedown", zkTimebox.btnDown);
			zk.unlisten(btn, "mouseup", zkTimebox.btnUp);
			zk.unlisten(btn, "mouseout", zkTimebox.btnOut);
			zk.unlisten(btn, "mouseover", zkTimebox.btnOver);
			zk.unlisten(inp, "keydown", zkTimebox.keyDown);
			zk.unlisten(inp, "click", zkTimebox.doClick);
		}
	},
	onChanging: function () {
		var meta = zkau.getMeta(this.uuid);
		if (!meta) return;
		var val = this.getInputNode().value,
			date = this.coerceFromString_(val),
			oldDate = meta.getValue();
		if (oldDate) {
			oldDate.setHours(date.getHours());
			oldDate.setMinutes(date.getMinutes());
			oldDate.setSeconds(date.getSeconds());
		} else
			meta.setValue(date);

		meta._changed = true;
		meta.getInputNode().value = meta.getRawText();
		meta._output();
		meta._onupdate({close: false, sendOnChange: false});
	}
};
zkTimebox.dodropbtnup = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm._dodropbtnup();
};
zkTimebox.btnDown = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm._btnDown(evt);
};
zkTimebox.btnUp = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm._btnUp(evt);
};
zkTimebox.btnOut = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm._btnOut(evt);
};
zkTimebox.btnOver = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm._btnOver(evt);
};
zkTimebox.keyDown = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm.doKeyDown_(evt);
	Event.stop(evt);
};
zkTimebox.doClick = function (evt) {
	if (!evt) evt = window.event;
	var el = Event.element(evt),
		meta = zkau.getMeta($uuid(el));
	if (meta) meta._tm.doClick_(evt);
};
function TimeHandler (index, type) {
	this.index = index;
	this.type = type;
}
TimeHandler.prototype = {

	maxsize: 59,
	minsize: 0,
	digits: 2,
	format: function (date) {
		return '00';
	},
	unformat: function (date, val) {
		return date;
	},
	increase: function (wgt, up) {
		var inp = wgt.getInputNode(),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end);

		text = $int(text.replace(/ /g, '0'));
		text += up;
		var max = this.maxsize + 1;
		if (text < this.minsize)
			text = this.maxsize;
		else if (text >= max)
			text = this.minsize;

		if (/** TODO: this.digits == 2 && */text < 10) text = "0" + text;
		inp.value = val.substring(0, start) + text + val.substring(end, val.length);

		zk.setSelectionRange(inp, start, end);
	},
	deleteTime: function (wgt, backspace) {
		var inp = wgt.getInputNode(),
			sel = zk.getSelectionRange(inp),
			pos = sel[1],
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value;
		if (sel[0] != sel[1]) {
			inp.value = val.substring(0, start) + '  ' + val.substring(end, val.length);
			pos = end;
		} else if (pos == start + 1) {
			if (backspace)
				inp.value = val.substring(0, start) + ' ' + val.substring(start + 1, val.length);
			else {
				inp.value = val.substring(0, start + 1) + ' ' + val.substring(start + 2, val.length);
				pos++;
			}
		} else if (backspace) {
			inp.value = val.substring(0, start) + ' ' + val.substring(start, start + 1) + val.substring(end, val.length);
		}

		zk.setSelectionRange(inp, pos, pos);
	},
	addTime: function (wgt, num) {
		var inp = wgt.getInputNode(),
			sel = zk.getSelectionRange(inp),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end);

		if (sel[1] - sel[0] > 2) {
			sel[0] = sel[1] - 2;
		}

		var seld = val.substring(sel[0], sel[1]);
		if (seld) {
			if (sel[1] - sel[0] > 1)
				seld = ' ' + num;
			inp.value = val.substring(0, sel[0]) + seld + val.substring(sel[1], val.length);
		} else {
			var text1 = '';
			if (sel[1] == end) {
				if (text.startsWith(' ')) {
					if (text.endsWith(' '))
						text1 = ' ' + num;
					else
						text1 = text.charAt(1) + num;
				} else if (text.endsWith(' '))
					text1 = text.charAt(0) + num;
			} else {
				if (text.startsWith(' '))
					text1 = num + text.charAt(1);
			}
			if (text1 && text1 != text) {
				if ($int(text1) <= this.maxsize)
					inp.value = val.substring(0, start) + text1 + val.substring(end, val.length);
			}
		}
		zk.setSelectionRange(inp, sel[1], sel[1]);
	},
	getText: function (val) {
		var start = this.index[0],
			end = this.index[1] + 1;
		return val.substring(start, end);
	}
};
function HourInDayHandler (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(HourInDayHandler.prototype, TimeHandler.prototype), {
	maxsize: 23,
	minsize: 0,
	format: function (date) {
		if (!date) return '00';
		else {
			var h = date.getHours();
			if (h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val) {
		date.setHours($int(this.getText(val)));
		return date;
	}
});
function HourInDayHandler2 (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(HourInDayHandler2.prototype, TimeHandler.prototype), {
	maxsize: 24,
	minsize: 1,
	format: function (date) {
		if (!date) return '24';
		else {
			var h = date.getHours();
			if (h == 0)
				h = '24';
			else if (h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val) {
		var text = this.getText(val);
		var hours = $int(this.getText(val));
		if (hours == 24)
			hours = 0;
		date.setHours(hours);
		return date;
	}
});
function HourHandler (index, type) {
	this.index = index;
	this.type = type;

}
Object.extend(Object.extend(HourHandler.prototype, TimeHandler.prototype), {
	maxsize: 12,
	minsize: 1,
	format: function (date) {
		if (!date) return '12';
		else {
			var h = date.getHours();
			h = (h % 12);
			if (h == 0)
				h = '12';
			else if (h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val, am) {
		var hours = $int(this.getText(val));
		if (hours == 12)
			hours = 0;
		date.setHours(am ? hours : hours + 12);
		return date;
	}
});
function HourHandler2 (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(HourHandler2.prototype, TimeHandler.prototype), {
	maxsize: 11,
	minsize: 0,
	format: function (date) {
		if (!date) return '00';
		else {
			var h = date.getHours();
			h = (h % 12);
			if (h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val, am) {
		var hours = $int(this.getText(val));
		date.setHours(am ? hours : hours + 12);
		return date;
	}
});
function MinuteHandler (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(MinuteHandler.prototype, TimeHandler.prototype), {
	format: function (date) {
		if (!date) return '00';
		else {
			var m = date.getMinutes();
			if (m < 10)
				m = '0' + m;
			return m.toString();
		}
	},
	unformat: function (date, val) {
		var min = $int(this.getText(val));
		date.setMinutes($int(this.getText(val)));
		return date;
	}
});
function SecondHandler (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(SecondHandler.prototype, TimeHandler.prototype), {
	format: function (date) {
		if (!date) return '00';
		else {
			var s = date.getSeconds();
			if (s < 10)
				s = '0' + s;
			return s.toString();
		}
	},
	unformat: function (date, val) {
		date.setSeconds($int(this.getText(val)));
		return date;
	}
});
function AMPMHandler (index, type) {
	this.index = index;
	this.type = type;
}
Object.extend(Object.extend(AMPMHandler.prototype, TimeHandler.prototype), {
	format: function (date) {
		if (!date)
			return zk.APM[0];
		var h = date.getHours();
		return zk.APM[h < 12 ? 0 : 1];
	},
	unformat: function (date, val) {
		return zk.APM[0] == this.getText(val);
	},
	increase: function (wgt, up) {
		var inp = wgt.getInputNode(),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end);

		text = zk.APM[0] == text ? zk.APM[1] : zk.APM[0];
		inp.value = val.substring(0, start) + text + val.substring(end, val.length);
		zk.setSelectionRange(inp, start, end);
	}
});