/* Timebox.zul

	Purpose:
		testing textbox.intbox.spinner,timebox,doublebox,longbox and decimalbox on zk5
	Description:

	History:
		Thu June 11 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var LEGAL_CHARS = 'ahKHksmz',
		/*constant for MINUTE (m) field alignment.
		 * @type int
		 */
		MINUTE_FIELD = 1,
		/*constant for SECOND (s) field alignment.
		 * @type int
		 */
		SECOND_FIELD = 2,
		/*constant for AM_PM (a) field alignment.
		 * @type int
		 */
		AM_PM_FIELD = 3,
		/*constant for HOUR0 (H) field alignment. (Hour in day (0-23))
		 * @type int
		 */
		HOUR0_FIELD = 4,
		/*constant for HOUR1 (k) field alignment. (Hour in day (1-24))
		 * @type int
		 */
		HOUR1_FIELD = 5,
		/*constant for HOUR2 (h) field alignment. (Hour in am/pm (1-12))
		 * @type int
		 */
		HOUR2_FIELD = 6,
		/*constant for HOUR3 (K) field alignment. (Hour in am/pm (0-11))
		 * @type int
		 */
		HOUR3_FIELD = 7;
	function _updFormat(wgt, fmt) {
		var index = [],
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM : zk.APM;
		for (var i = 0, l = fmt.length; i < l; i++) {
			var c = fmt.charAt(i);
			switch (c) {
			case 'a':
				var len = APM[0].length;
				index.push(new zul.inp.AMPMHandler([i, i + len - 1], AM_PM_FIELD, wgt));
				break;
			case 'K':
				var start = i,
					end = fmt.charAt(i+1) == 'K' ? ++i : i;
				index.push(new zul.inp.HourHandler2([start, end], HOUR3_FIELD));
				break;
			case 'h':
				var start = i,
					end = fmt.charAt(i+1) == 'h' ? ++i : i;
				index.push(new zul.inp.HourHandler([start, end], HOUR2_FIELD));
				break;
			case 'H':
				var start = i,
					end = fmt.charAt(i+1) == 'H' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler([start, end], HOUR0_FIELD));
				break;;
			case 'k':
				var start = i,
					end = fmt.charAt(i+1) == 'k' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler2([start, end], HOUR1_FIELD));
				break;
			case 'm':
				var start = i,
					end = fmt.charAt(i+1) == 'm' ? ++i : i;
				index.push(new zul.inp.MinuteHandler([start, end], MINUTE_FIELD));
				break;
			case 's':
				var start = i,
					end = fmt.charAt(i+1) == 's' ? ++i : i;
				index.push(new zul.inp.SecondHandler([start, end], SECOND_FIELD));
				break;
			case 'z':
				index.push({index:[i, i],format:(function(text){
					return function(){
						return text;
					};
				})(wgt._timezone)});
				break;
			default:
				var ary = [],
					start = i,
					end = i;

				while ((ary.push(c)) && ++end < l) {
					c = fmt.charAt(end);
					if (LEGAL_CHARS.indexOf(c) != -1) {
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
		for (var shift, i = 0, l = index.length; i < l; i++) {
			if (index[i].type == AM_PM_FIELD) {
				shift = index[i].index[1] - index[i].index[0];
				if (!shift) break; // no need to shift.
			} else if (shift) {
				index[i].index[0] += shift;
				index[i].index[1] += shift;
			}
		}
		wgt._fmthdler = index;
	}
	function _cleanSelectionText (wgt, startHandler) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			selEnd = sel[1],
			fmthdler = wgt._fmthdler,
			index = fmthdler.$indexOf(startHandler),
			text = [],
			hdler = startHandler,
			isFirst = true,
			prevStart, ofs, hStart, hEnd, posOfs;
		
		//restore separator
		do {
			hStart = hdler.index[0];
			hEnd = hdler.index[1] + 1;
			
			if (hdler.type && 
				(posOfs = hdler.isSingleLength())) {
				//sync handler index
				hdler._doShift(wgt, posOfs);
				selEnd--;					
			}
			
			//latest one
			if (hEnd >= selEnd && hdler.type) {
				ofs = selEnd - hStart;
				while (ofs-- > 0) //replace by space (after)
					text.push(' ');
				break;
			}
			
			if (hdler.type) {
				prevStart = isFirst ? pos: hStart;
				isFirst = false
				continue;
			}
			ofs = hStart - prevStart;
			while (ofs-- > 0) //replace by space (before)
				text.push(' ');
									
			text.push(hdler.format());
			
		} while (hdler = fmthdler[++index]);
		return text.join('');
	}
	function _getMaxLen (wgt) {
		var val = wgt.getInputNode().value,
			len = 0, th, lastTh;
		for (var i = 0, f = wgt._fmthdler, l = f.length; i < l; i++) {
			th = f[i];
			if (i == l-1) {
				len += th.format().length;
			} else
				len += (th.type ? th.getText(val): th.format()).length;
			if (th.type) lastTh = th;
		}
		return (lastTh.digits == 1) ? ++len: len;
	}
	var globallocalizedSymbols = {};

var Timebox = 
/**
 * An input box for holding a time (a Date Object, but only Hour & Minute are used.
 *
 * <p>Default {@link #getZclass}: z-timebox.
 *
 * <p>timebox doens't support customized format. It support HH:mm formate, where HH is hour of day and mm is minute of hour.
 * 
 * <p>Like {@link zul.inp.Combobox} and {@link Datebox},
 * the value of a read-only time box ({@link #isReadonly}) can be changed
 * by clicking the up or down button (though users cannot type anything
 * in the input box).
 */
zul.db.Timebox = zk.$extends(zul.inp.FormatWidget, {
	_buttonVisible: true,
	_format: 'HH:mm',
	_timezone: '',
	$init: function() {		
		this.$supers('$init', arguments);
		_updFormat(this, this._format);
	},
	$define: {
		timezone: function (v) {
			_updFormat(this, this._format);
		},
		/** Returns whether the button (on the right of the textbox) is visible.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether the button (on the right of the textbox) is visible.
		 * @param boolean buttonVisible
		 */
		buttonVisible: function (v) {
			zul.inp.RoundUtl.buttonVisible(this, v);
		},
		/** Sets the unformater function. This method is called from Server side.
		 * @param String unf the unformater function
		 */
		/** Returns the unformater.
		 * @return String the unformater function
		 */
		unformater: function (unf) {
			eval('Timebox._unformater = ' + unf);
		},
		localizedSymbols: [
			function (val) {
				if(val) {
					if (!globallocalizedSymbols[val[0]])
						globallocalizedSymbols[val[0]] = val[1];
					return globallocalizedSymbols[val[0]];
				} 
				return val;
			}
		]
	},
	inRoundedMold: function () {
		return true;
	},
	setFormat: function (fmt) {
		fmt = fmt ? fmt.replace(/\'/g, '') : fmt;
		_updFormat(this, fmt);
		this.$supers('setFormat', arguments);
	},
	setValue: function (value, fromServer) {
		if (fromServer && value === null) //Bug ZK-1322: if from server side, return empty string
			this._changed = false;
		this.$supers('setValue', arguments);
	},
	coerceToString_: function (date) {
		if (!this._changed && !date && arguments.length) return '';
		var out = [], th, text, offset;
		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			th = f[i];
			text = th.format(date);
			out.push(text);
			//sync handler index
			if (th.type && (offset = th.isSingleLength()) !== false && 
				(offset += text.length - 1))
				th._doShift(this, offset);
		}
		return out.join('');
	},
	coerceFromString_: function (val) {
		var unf = Timebox._unformater;
		if (unf && jq.isFunction(unf)) {
			var cusv = unf(val);
			if (cusv) {
				this._shortcut = val;
				return cusv;
			}
		}
		if (!val) return null;

		// F65-ZK-1825: use this._value instead of "today"
		// We cannot use this._value in this case, which won't trigger onChange
		// event. Using clone date instead.
		var date = this._value ? new Date(this._value.getTime()) : zUtl.today(this._format),
			hasAM, isAM, hasHour1,
			fmt = [], emptyCount = 0;
		date.setSeconds(0);
		date.setMilliseconds(0);

		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			if (f[i].type == AM_PM_FIELD) {
				hasAM = true;
				isAM = f[i].unformat(date, val);
				if (!f[i].getText(val).trim().length)
					emptyCount++;
			} else if (f[i].type) {
				fmt.push(f[i]);
				if (!f[i].getText(val).trim().length)
					emptyCount++;
			}
		}
		
		if (fmt.length == 
			(hasAM ? --emptyCount: emptyCount)) {
			this._changed = false;//for return empty string
			return;
		}

		for (var i = 0, l = fmt.length; i < l; i++) {
			if (!hasAM && (fmt[i].type == HOUR2_FIELD || fmt[i].type == HOUR3_FIELD))
				isAM = true;
			date = fmt[i].unformat(date, val, isAM);
		}
		return date;
	},
	onSize: function () {
		var inp = this.getInputNode();
		if (inp && this._value && !inp.value)
			inp.value = this.coerceToString_(this._value);
		
		zul.inp.RoundUtl.onSize(this, this.$n('btn'));
	},
	onHide: zul.inp.Textbox.onHide,
	validate: zul.inp.Intbox.validate,
	doClick_: function(evt) {
		if (evt.domTarget == this.getInputNode())
			this._doCheckPos();
		this.$supers('doClick_', arguments);
	},
	doKeyPress_: function (evt) {
		if (zk.opera && evt.keyCode != 9) {
			evt.stop();
			return;
		}
		this.$supers('doKeyPress_', arguments);
	},
	doKeyDown_: function(evt) {
		var inp = this.getInputNode();
		if (inp.disabled || inp.readOnly)
			return;

		// control input keys only when no custom unformater is given
		if (!Timebox._unformater) {
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
				evt.stop();
				return;
			case 35://end
				this.lastPos = inp.value.length;
				return;
			case 36://home
				this.lastPos = 0;
				return;
			case 37://left
				if (this.lastPos > 0)
					this.lastPos--;
				return;
			case 39://right
				if (this.lastPos < inp.value.length)
					this.lastPos++;
				return;
			case 38://up
				this._doUp();
				evt.stop();
				return;
			case 40://down
				this._doDown();
				evt.stop();
				return;
			case 46://del
				this._doDel();
				evt.stop();
				return;
			case 8://backspace
				this._doBack();
				evt.stop();
				return;
			case 9:
				// do nothing
				break
			case 13: case 27://enter,esc,tab
				break;
			default:
				if (!(code >= 112 && code <= 123) //F1-F12
				&& !evt.ctrlKey && !evt.altKey)
					evt.stop();
			}
		}
		this.$supers('doKeyDown_', arguments);
	},
	_ondropbtnup: function (evt) {
		this.domUnlisten_(document.body, "onZMouseup", "_ondropbtnup");
		this._stopAutoIncProc();
		this._currentbtn = null;
	},
	_btnDown: function(evt) { // TODO: format the value first
		if (!this._buttonVisible || this._disabled) return;
		
		var btn = this.$n("btn"),
			inp = this.getInputNode();
			
		if (!zk.dragging) {
			if (this._currentbtn) // just in case
				this._ondropbtnup(evt);
			
			this.domListen_(document.body, "onZMouseup", "_ondropbtnup");
			this._currentbtn = btn;
		}
		
		// if btn down before blur, needs to convert to real time string first
		if (inp.value && Timebox._unformater)
			inp.value = this.coerceToString_(this.coerceFromString_(inp.value));
		if (!inp.value)
			inp.value = this.coerceToString_();
			
		// cache it for IE
		this._lastPos = this._getPos();
			
		var ofs = zk(btn).revisedOffset(),
			isOverUpBtn = (evt.pageY - ofs[1]) < btn.offsetHeight/2;
		if (zk.webkit)
			zk(inp).focus(); //Bug ZK-1527: chrome and safari will trigger focus if executing setSelectionRange, focus it early here
		if (isOverUpBtn) { //up
			this._doUp();
			this._startAutoIncProc(true);
		} else {
			this._doDown();
			this._startAutoIncProc(false);
		}
		
		this._changed = true;
		delete this._shortcut;
		
		zk.Widget.mimicMouseDown_(this); //set zk.currentFocus
		zk(inp).focus(); //we have to set it here; otherwise, if it is in popup of
			//datebox, datebox's onblur will find zk.currentFocus is null

		// disable browser's text selection
		evt.stop();
	},
	_btnUp: function(evt) {
		if (!this._buttonVisible || this._disabled || zk.dragging) return;

		if (zk.opera) zk(inp).focus();
			//unfortunately, in opera, it won't gain focus if we set in _btnDown

		this._onChanging();
		this._stopAutoIncProc();
		
		if ((zk.ie || zk.webkit) && this._lastPos)
			zk(this.getInputNode()).setSelectionRange(this._lastPos, this._lastPos);
	},
	_getPos: function () {
		return zk(this.getInputNode()).getSelectionRange()[0];
	},
	_doCheckPos: function () {
		this.lastPos = this._getPos();
	},
	_doUp: function() {
		this._changed = true;
		this.getTimeHandler().increase(this, 1);
		this._onChanging();
	},
	_doDown: function() {
		this._changed = true;
		this.getTimeHandler().increase(this, -1);
		this._onChanging();
	},
	_doBack: function () {
		this._changed = true;
		this.getTimeHandler().deleteTime(this, true);
	},
	_doDel: function () {
		this._changed = true;
		this.getTimeHandler().deleteTime(this, false);
	},
	_doType: function (val) {
		this._changed = true;
		this.getTimeHandler().addTime(this, val);
	},
	getTimeHandler: function () {
		var sr = zk(this.getInputNode()).getSelectionRange(),
			start = sr[0],
			end = sr[1];
			//don't use [0] as the end variable, it may have a bug when the format is aHH:mm:ss 
			//when use UP/Down key to change the time
		
		// Bug ZK-434
		var hdler;
		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			if (!f[i].type) continue;
			if (f[i].index[0] <= start) {
				hdler = f[i]; 
				if (f[i].index[1] + 1 >= end)
					return f[i];
			}
		}
		return hdler || this._fmthdler[0];
	},
	getNextTimeHandler: function (th) {
		var f = this._fmthdler,
			index = f.$indexOf(th),
			lastHandler;
			
		while ((lastHandler = f[++index]) &&
			(!lastHandler.type || lastHandler.type == AM_PM_FIELD));
		
		return lastHandler;
	},
	_startAutoIncProc: function(up) {
		if (this.timerId)
			clearInterval(this.timerId);
		var self = this,
			fn = up ? '_doUp' : '_doDown';
		this.timerId = setInterval(function() {
			if ((zk.ie || zk.webkit) && self._lastPos)
				zk(self.getInputNode()).setSelectionRange(self._lastPos, self._lastPos);
			self[fn]();
		}, 300);
	},
	_stopAutoIncProc: function() {
		if (this.timerId)
			clearTimeout(this.timerId);
		this.currentStep = this.defaultStep;
		this.timerId = null;
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);
		var n = this.$n(),
			inp = this.getInputNode(),
			selrng = zk(inp).getSelectionRange();
		
		if (!inp.value)
			inp.value = Timebox._unformater ? '' : this.coerceToString_();

		this._doCheckPos();
		
		// Bug 2688620
		if (selrng[0] !== selrng[1]) {
			zk(inp).setSelectionRange(selrng[0], selrng[1]);
			this.lastPos = selrng[1];
		}

		zul.inp.RoundUtl.doFocus_(this);
	},
	doBlur_: function (evt) {
		// skip onchange, Bug 2936568
		if (!this._value && !this._changed && !Timebox._unformater)
			this.getInputNode().value = this._defRawVal = '';

		this.$supers('doBlur_', arguments);

		zul.inp.RoundUtl.doBlur_(this);
	},
	afterKeyDown_: function (evt,simulated) {
		if (!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);

		return this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {
		this.$supers(zul.db.Timebox, 'bind_', arguments);
		var btn;
		
		if (btn = this.$n('btn'))
			this.domListen_(btn, "onZMouseDown", "_btnDown")
				.domListen_(btn, "onZMouseUp", "_btnUp");
		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		zWatch.unlisten({onSize: this});
		var btn = this.$n("btn");
		if (btn) {
			this.domUnlisten_(btn, "onZMouseDown", "_btnDown")
				.domUnlisten_(btn, "onZMouseUp", "_btnUp");
		}
		this._changed = false;
		this.$supers(zul.db.Timebox, 'unbind_', arguments);
	},
	getBtnUpIconClass_: function () {
		return 'z-icon-angle-up';
	},
	getBtnDownIconClass_: function () {
		return 'z-icon-angle-down';
	}
});
zul.inp.TimeHandler = zk.$extends(zk.Object, {
	maxsize: 59,
	minsize: 0,
	digits: 2,
	$init: function (index, type, wgt) {
		this.index = index;
		this.type = type;
		if (index[0] == index[1])
			this.digits = 1;
		this.wgt = wgt;
	},
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
			text = this.getText(val),
			singleLen = this.isSingleLength() !== false,
			ofs;

		text = zk.parseInt(singleLen ? text: 
				text.replace(/ /g, '0')) + up;
		
		var max = this.maxsize + 1;
		if (text < this.minsize) {
			text = this.maxsize;
			ofs = 1;
		} else if (text >= max) {
			text = this.minsize;
			ofs = -1;
		} else if (singleLen) 
			ofs = (up > 0) ? 
					(text == 10) ? 1: 0:
					(text == 9) ? -1: 0;

		if (text < 10 && !singleLen)
			 text = "0" + text;
		
		inp.value = val.substring(0, start) + text + val.substring(end);
		
		if (singleLen && ofs) {
			this._doShift(wgt, ofs);
			end += ofs; 
		}

		zk(inp).setSelectionRange(start, end);
	},
	deleteTime: function (wgt, backspace) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			val = inp.value,
			maxLength = _getMaxLen(wgt);
		
		// clean over text	
		if (val.length > maxLength) {
			val = inp.value = val.substr(0, maxLength);
			sel = [Math.min(sel[0], maxLength), Math.min(sel[1], maxLength)];
			pos = sel[0];
		}
		
		if (pos != sel[1]) { 
			//select delete
			inp.value = val.substring(0, pos) + _cleanSelectionText(wgt, this)
							+ val.substring(sel[1]);
		} else {
			var fmthdler = wgt._fmthdler,
				index = fmthdler.$indexOf(this),
				ofs = backspace? -1: 1,
				ofs2 = backspace? 0: 1,
				ofs3 = backspace? 1: 0,
				hdler, posOfs;
			if (pos == this.index[ofs2] + ofs2) {// on start or end
				//delete by sibling handler
				if (hdler = fmthdler[index + ofs * 2]) 
					pos = hdler.index[ofs3] + ofs3 + ofs;
			} else {// delete self
				pos += ofs;
				hdler = this;
			}
			if (hdler) {
				posOfs = hdler.isSingleLength();
				inp.value = val.substring(0, (ofs3 += pos)-1) + 
					(posOfs ? '': ' ') + val.substring(ofs3);
				if (posOfs)	
					hdler._doShift(wgt, posOfs);
			}
			if (posOfs && !backspace) pos--;
		}
		zk(inp).setSelectionRange(pos, pos);
	},
	_addNextTime: function (wgt, num) {
		var inp = wgt.getInputNode(),
			index, NTH;
		if (NTH = wgt.getNextTimeHandler(this)) {
			index = NTH.index[0];
			zk(inp).setSelectionRange(index, 
				Math.max(index, 
					zk(inp).getSelectionRange()[1]));
			NTH.addTime(wgt, num);
		}
	},
	addTime: function (wgt, num) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
			val = inp.value,
			pos = sel[0],
			maxLength = _getMaxLen(wgt),
			posOfs = this.isSingleLength();
			
		// clean over text	
		if (val.length > maxLength) {
			val = inp.value = val.substr(0, maxLength);
			sel = [Math.min(sel[0], maxLength), Math.min(sel[1], maxLength)];
			pos = sel[0];
		}
		
		if (pos == maxLength)
			return;
		
		// first number (hendle max bound)
		if (pos == this.index[0]) {
			var text = this.getText(val)
						.substring((posOfs === 0)? 0: 1).trim(),
				i;
			if (!text.length) text = '0';
			
			if ((i = zk.parseInt(num + text)) > this.maxsize) {
				if (posOfs !== 0) {
					val = inp.value = val.substring(0, pos) + (posOfs ? '0': '00')
						+ val.substring(pos + 2);
					if (!posOfs) pos++;
					zk(inp).setSelectionRange(pos, Math.max(sel[1], pos));
					sel = zk(inp).getSelectionRange();
				}
				if (posOfs)
					this._doShift(wgt, posOfs);
			}
		} else if (pos == (this.index[1] + 1)) {//end of handler
			var i;
			if (posOfs !== false) {
				var text = this.getText(val);
				if ((i = zk.parseInt(text + num)) <= this.maxsize) {//allow add number
					if (i && i < 10) // 1-9
						pos--;
					else if (i || posOfs) { // 0 or larger then 10, except zero and non-posOfs
						val = inp.value = val.substring(0, (pos + posOfs)) +
							(posOfs ? '' : '0') + val.substring(pos);
						if (i) // larger then 10
							this._doShift(wgt, 1);
						else { // 0
							zk(inp).setSelectionRange(pos, Math.max(sel[1], pos));
							if (posOfs)//2 digits zero
								this._doShift(wgt, posOfs);
						}
					}
				}
			}
			
			if (!i || i > this.maxsize) {
				this._addNextTime(wgt, num);
				return;
			}
		}
		
		if (pos != sel[1]) {
			//select edit
			var s = _cleanSelectionText(wgt, this),
				ofs;
			//in middle position
			if (posOfs !== false && (ofs = pos - this.index[1]))
				this._doShift(wgt, ofs);
				
			inp.value = val.substring(0, pos++) + num 
				+ s.substring(ofs ? 0: 1)
				+ val.substring(sel[1]);
		} else {
			inp.value = val.substring(0, pos) 
				+ num + val.substring(++pos);
		}
		wgt.lastPos = pos;
		zk(inp).setSelectionRange(pos, pos);
	},
	getText: function (val) {
		var start = this.index[0],
			end = this.index[1] + 1;
		return val.substring(start, end);
	},
	_doShift: function (wgt, shift) {
		var f = wgt._fmthdler,
			index = f.$indexOf(this),
			NTH;
		this.index[1] += shift;	
		while (NTH = f[++index]) {
			NTH.index[0] += shift;
			NTH.index[1] += shift;
		}
	},
	isSingleLength: function () {
		return this.digits == 1 && (this.index[0] - this.index[1]);
	}
});
zul.inp.HourInDayHandler = zk.$extends(zul.inp.TimeHandler, {
	maxsize: 23,
	minsize: 0,
	format: function (date) {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0': '00';
		else {
			var h = date.getHours();
			if (!singleLen && h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val) {
		date.setHours(zk.parseInt(this.getText(val)));
		return date;
	}
});
zul.inp.HourInDayHandler2 = zk.$extends(zul.inp.TimeHandler, {
	maxsize: 24,
	minsize: 1,
	format: function (date) {
		if (!date) return '24';
		else {
			var h = date.getHours();
			if (h == 0)
				h = '24';
			else if (this.digits == 2 && h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val) {
		var hours = zk.parseInt(this.getText(val));
		if (hours == 24)
			hours = 0;
		date.setHours(hours);
		return date;
	}
});
zul.inp.HourHandler = zk.$extends(zul.inp.TimeHandler, {
	maxsize: 12,
	minsize: 1,
	format: function (date) {
		if (!date) return '12';
		else {
			var h = date.getHours();
			h = (h % 12);
			if (h == 0)
				h = '12';
			else if (this.digits == 2 && h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val, am) {
		var hours = zk.parseInt(this.getText(val));
		if (hours == 12)
			hours = 0;
		date.setHours(am ? hours : hours + 12);
		return date;
	}
});
zul.inp.HourHandler2 = zk.$extends(zul.inp.TimeHandler, {
	maxsize: 11,
	minsize: 0,
	format: function (date) {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0': '00';
		else {
			var h = date.getHours();
			h = (h % 12);
			if (!singleLen && h < 10)
				h = '0' + h;
			return h.toString();
		}
	},
	unformat: function (date, val, am) {
		var hours = zk.parseInt(this.getText(val));
		date.setHours(am ? hours : hours + 12);
		return date;
	}
});
zul.inp.MinuteHandler = zk.$extends(zul.inp.TimeHandler, {
	format: function (date) {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0': '00';
		else {
			var m = date.getMinutes();
			if (!singleLen && m < 10)
				m = '0' + m;
			return m.toString();
		}
	},
	unformat: function (date, val) {
		date.setMinutes(zk.parseInt(this.getText(val)));
		return date;
	}
});
zul.inp.SecondHandler = zk.$extends(zul.inp.TimeHandler, {
	format: function (date) {
		var singleLen = this.digits == 1;
		if (!date) return  singleLen ? '0': '00';
		else {
			var s = date.getSeconds();
			if (!singleLen && s < 10)
				s = '0' + s;
			return s.toString();
		}
	},
	unformat: function (date, val) {
		date.setSeconds(zk.parseInt(this.getText(val)));
		return date;
	}
});
zul.inp.AMPMHandler = zk.$extends(zul.inp.TimeHandler, {
	format: function (date) {
		var APM = this.wgt._localizedSymbols ? this.wgt._localizedSymbols.APM : zk.APM;
		if (!date)
			return APM[0];
		var h = date.getHours();
		return APM[h < 12 ? 0 : 1];
	},
	unformat: function (date, val) {
		var text = this.getText(val).trim(),
			APM = this.wgt._localizedSymbols ? this.wgt._localizedSymbols.APM : zk.APM;
		return (text.length == APM[0].length) ? 
			APM[0] == text : true;
	},
	addTime: function (wgt, num) {
		var inp = wgt.getInputNode(),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end),
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM : zk.APM;
		//restore A/PM text
		if (text != APM[0] && text != APM[1]) {
			text = APM[0];
			inp.value = val.substring(0, start) + text + val.substring(end);
		}
		this._addNextTime(wgt, num);
	},
	// Bug ZK-434, we have to delete a sets of "AM/PM", rather than a single word "A/P/M"
	deleteTime: function (wgt, backspace) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			pos1 = sel[1],
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value;
		if (pos1 - pos > end - start) 
			return this.$supers('deleteTime', arguments);
			
		var t = [''];
		for (var i = end - start; i > 0; i--)
			t.push(' ');
		
		inp.value = val.substring(0, start) + t.join('') + val.substring(end);
		zk(inp).setSelectionRange(start, start);
	},
	increase: function (wgt, up) {
		var inp = wgt.getInputNode(),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end),
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM : zk.APM;

		text = APM[0] == text ? APM[1] : APM[0];
		inp.value = val.substring(0, start) + text + val.substring(end);
		zk(inp).setSelectionRange(start, end);
	}
});

})();