/* inp.zul

	Purpose:
		testing textbox.intbox.spinner,timebox,doublebox,longbox and decimalbox on zk5
	Description:

	History:
		Thu June 11 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An input box for holding a time (a Date Object, but only Hour & Minute are used.
 *
 * <p>Default {@link #getZclass}: z-timebox.
 *
 * <p>timebox doens't support customized format. It support HH:mm formate, where HH is hour of day and mm is minute of hour.
 * 
 * <p>Like {@link Combobox} and {@link zul.db.Datebox},
 * the value of a read-only time box ({@link #isReadonly}) can be changed
 * by clicking the up or down button (though users cannot type anything
 * in the input box).
 *
 */
zul.inp.Timebox = zk.$extends(zul.inp.FormatWidget, {
	LEGAL_CHARS: 'ahKHksm',
    /**Useful constant for MINUTE (m) field alignment.
     * @type int
     */
    MINUTE_FIELD: 1,
    /**Useful constant for SECOND (s) field alignment.
     * @type int
     */
    SECOND_FIELD: 2,
    /**Useful constant for AM_PM (a) field alignment.
     * @type int
     */
    AM_PM_FIELD: 3,
    /**Useful constant for HOUR0 (H) field alignment. (Hour in day (0-23))
     * @type int
     */
    HOUR0_FIELD: 4,
    /**Useful constant for HOUR1 (k) field alignment. (Hour in day (1-24))
     * @type int
     */
    HOUR1_FIELD: 5,
    /**Useful constant for HOUR2 (h) field alignment. (Hour in am/pm (1-12))
     * @type int
     */
    HOUR2_FIELD: 6,
    /**Useful constant for HOUR3 (K) field alignment. (Hour in am/pm (0-11))
     * @type int
     */
    HOUR3_FIELD: 7,
	_buttonVisible: true,
	_format: 'HH:mm',
	$define: {
		/** Returns whether the button (on the right of the textbox) is visible.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether the button (on the right of the textbox) is visible.
		 * @param boolean buttonVisible
		 */
		buttonVisible: function(v){
			var n = this.$n('btn'),
				zcls = this.getZclass();
			if (n) {
				if (!this.inRoundedMold())
					v ? jq(n).show() : jq(n).hide();
				else {
					var fnm = v ? 'removeClass': 'addClass';
					jq(n)[fnm](zcls + '-btn-right-edge');
					
					if (zk.ie6_) {
						jq(n)[fnm](zcls + 
							(this._readonly ? '-btn-right-edge-readonly': '-btn-right-edge'));
						
						if (jq(this.getInputNode()).hasClass(zcls + "-text-invalid"))
							jq(n)[fnm](zcls + "-btn-right-edge-invalid");
					}
				}
				this.onSize();
			}
		},
		format: function (fmt, fromServer) {
			this._parseFormat(fmt);
			var inp = this.getInputNode();
			if (inp) {
				inp.value = this.coerceToString_(this._value);
				if (fromServer) inp.defaultValue = inp.value; //not clear error if by client app
			}
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
			return zk.fmt.Text.format(msgzul.DATE_REQUIRED + out.join(''));
	},
	_parseFormat: function (fmt) {
		var index = [];
		for (var i = 0, j = fmt.length; i < j; i++) {
			var c = fmt.charAt(i);
			switch (c) {
			case 'a':
				var len = zk.APM[0].length;
				index.push(new zul.inp.AMPMHandler([i, i + len - 1], this.AM_PM_FIELD));
				break;
			case 'K':
				var start = i,
					end = fmt.charAt(i+1) == 'K' ? ++i : i;
				index.push(new zul.inp.HourHandler2([start, end], this.HOUR3_FIELD));
				break;
			case 'h':
				var start = i,
					end = fmt.charAt(i+1) == 'h' ? ++i : i;
				index.push(new zul.inp.HourHandler([start, end], this.HOUR2_FIELD));
				break;
			case 'H':
				var start = i,
					end = fmt.charAt(i+1) == 'H' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler([start, end], this.HOUR0_FIELD));
				break;;
			case 'k':
				var start = i,
					end = fmt.charAt(i+1) == 'k' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler2([start, end], this.HOUR1_FIELD));
				break;
			case 'm':
				var start = i,
					end = fmt.charAt(i+1) == 'm' ? ++i : i;
				index.push(new zul.inp.MinuteHandler([start, end], this.MINUTE_FIELD));
				break;
			case 's':
				var start = i,
					end = fmt.charAt(i+1) == 's' ? ++i : i;
				index.push(new zul.inp.SecondHandler([start, end], this.SECOND_FIELD));
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

		var date = zUtl.today(true),
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
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-timebox" + (this.inRoundedMold() ? "-rounded": "");
	},
	onSize: _zkf = function () {
		var width = this.getWidth(),
			inp = this.getInputNode();
		if (!width || width.indexOf('%') != -1)
			inp.style.width = '';

		if (inp && this._value && !inp.value) {
			if (!this._fmthdler)
				this._parseFormat(this._format);
			inp.value = this.coerceToString_(this._value);
		}

		this.syncWidth();
		this._auxb.fixpos();
	},
	onShow: _zkf,
	onHide: zul.inp.Textbox.onHide,
	validate: zul.inp.Intbox.validate,
	doClick_: function(evt) {
		if (evt.domTarget == this.getInputNode())
			this._doCheckPos(this._getPos());
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
			evt.stop();
			return;
		case 37://left
			this._doLeft();
			evt.stop();
			return;
		case 38://up
			this._doUp();
			evt.stop();
			return;
		case 39://right
			this._doRight();
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
		case 35://end
		case 36://home
			this._doCheckPos(code == 36 ? 0 : inp.value.length);
			evt.stop();
			return;
		case 13: case 27://enter,esc,tab
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				evt.stop();
		}
		this.$supers('doKeyDown_', arguments);
	},
	_dodropbtnup: function (evt) {
		jq(this._currentbtn).removeClass(this.getZclass() + "-btn-clk");
		this.domUnlisten_(document.body, "onMouseup", "_dodropbtnup");
		this._currentbtn = null;
	},
	_btnDown: function(evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		var inp = this.getInputNode(),
			btn = this.$n("btn");

		if(!inp || inp.disabled) return;
		if (this._currentbtn)
			this._dodropbtnup(evt);
		jq(btn).addClass(this.getZclass() + "-btn-clk");
		this.domListen_(document.body, "onMouseup", "_dodropbtnup");
		this._currentbtn = btn;
		if (!this.inRoundedMold())
			btn = zk.opera || zk.safari ? btn : btn.firstChild;

		if (!this._fmthdler)
			this._parseFormat(this._format);

		if (!inp.value)
			inp.value = this.coerceToString_();
			
		var ofs = zk(btn).revisedOffset();
		if ((evt.pageY - ofs[1]) < btn.offsetHeight / 2) { //up
			this._doUp();
			this._startAutoIncProc(true);
		} else {
			this._doDown();
			this._startAutoIncProc(false);
		}
		
		// cache it for IE
		this._lastPos = this._getPos();
		this._changed = true;
	},
	_btnUp: function(evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		var inp = this.getInputNode();
		if(inp.disabled || zk.dragging) return;

		this._onChanging();
		this._stopAutoIncProc();
		
		if ((zk.ie || zk.safari) && this._lastPos)
			zk(inp).setSelectionRange(this._lastPos, this._lastPos);

		inp.focus();
	},
	_btnOut: function(evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		var inp = this.getInputNode();
		if(!inp || inp.disabled || zk.dragging) return;

		jq(this.$n("btn")).removeClass(this.getZclass()+"-btn-over");
		this._stopAutoIncProc();
	},
	_btnOver: function(evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		if (this.getInputNode() && !this.getInputNode().disabled && !zk.dragging)
			jq(this.$n("btn")).addClass(this.getZclass()+"-btn-over");
	},
	_getPos: function () {
		return zk(this.getInputNode()).getSelectionRange()[1];
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
		zk(inp).setSelectionRange(pos, pos);
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

		zk(inp).setSelectionRange(pos, pos);
		this.lastPos = pos;
	},
	_doRight: function() {
		var inp = this.getInputNode(), pos = this.lastPos + 1, hdler = this.getTimeHandler();
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
		zk(inp).setSelectionRange(pos, pos);

		this.lastPos = pos;
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
			if ((zk.ie || zk.safari) && self._lastPos)
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
	syncWidth: function () {
		var node = this.$n();
		if (!zk(node).isRealVisible() || (!this._inplace && !node.style.width))
			return;

		if (this._buttonVisible && this._inplace) {
			if (!node.style.width) {
				var $n = jq(node),
					inc = this.getInplaceCSS();
				$n.removeClass(inc);
				if (zk.opera)
					node.style.width = jq.px0(zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth());
				else
					node.style.width = jq.px0(zk(node).revisedWidth(node.offsetWidth));
				$n.addClass(inc);
			}
		}
		var width = zk.opera ? zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth()
							 : zk(node).revisedWidth(node.offsetWidth),
			btn = this.$n('btn'),
			inp = this.getInputNode();
		inp.style.width = jq.px0(zk(inp).revisedWidth(width - (btn ? btn.offsetWidth : 0)));
	},
	doFocus_: function (evt) {
		var n = this.$n(),
			inp = this.getInputNode(),
			selrng = zk(inp).getSelectionRange();
		if (this._inplace)
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));

		this.$supers('doFocus_', arguments);	

		if (!this._fmthdler)
			this._parseFormat(this._format);
			
		if (!inp.value)
			inp.value = this.coerceToString_();

		this._doCheckPos(this._getPos());
		
		// Bug 2688620
		if (selrng[0] !== selrng[1]) {
			zk(inp).setSelectionRange(selrng[0], selrng[1]);
			this.lastPos = selrng[1];
		}
		if (this._inplace) {
			if (jq(n).hasClass(this.getInplaceCSS())) {
				jq(n).removeClass(this.getInplaceCSS());
				this.onSize();
			}
		}
	},
	doBlur_: function (evt) {
		var n = this.$n();
		if (this._inplace && this._inplaceout) {
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
		}
		
		// skip onchange, Bug 2936568
		if (!this.getValue() && !this._changed)
			this.getInputNode().value = this._lastRawValVld = '';
		
		this.$supers('doBlur_', arguments);
		if (this._inplace && this._inplaceout) {
			jq(n).addClass(this.getInplaceCSS());
			this.onSize();
			n.style.width = this.getWidth() || '';
		}
	},
	afterKeyDown_: function (evt) {
		if (this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);

		this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var inp = this.getInputNode(),
			btn = this.$n("btn");
		zWatch.listen({onSize: this, onShow: this});

		if (this._inplace)
			jq(inp).addClass(this.getInplaceCSS());

		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, "onmousedown", "_btnDown");
			this.domListen_(btn, "onmouseup", "_btnUp");
			this.domListen_(btn, "onmouseout", "_btnOut");
			this.domListen_(btn, "mouseover", "_btnOver");
		}
		this.syncWidth();
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		zWatch.unlisten({onSize: this, onShow: this});
		var btn = this.$n("btn");
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, "onmousedown", "_btnDown");
			this.domUnlisten_(btn, "onmouseup", "_btnUp");
			this.domUnlisten_(btn, "onmouseout", "_btnOut");
			this.domUnlisten_(btn, "mouseover", "_btnOver");
		}
		this._changed = false;
		this.$supers('unbind_', arguments);
	}

});
zul.inp.TimeHandler = zk.$extends(zk.Object, {
	maxsize: 59,
	minsize: 0,
	digits: 2,
	$init: function (index, type) {
		this.index = index;
		this.type = type;
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
			text = val.substring(start, end);

		text = zk.parseInt(text.replace(/ /g, '0'));
		text += up;
		var max = this.maxsize + 1;
		if (text < this.minsize)
			text = this.maxsize;
		else if (text >= max)
			text = this.minsize;

		if (/* TODO: this.digits == 2 && */text < 10) text = "0" + text;
		inp.value = val.substring(0, start) + text + val.substring(end, val.length);

		zk(inp).setSelectionRange(start, end);
	},
	deleteTime: function (wgt, backspace) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
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

		zk(inp).setSelectionRange(pos, pos);
	},
	addTime: function (wgt, num) {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
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
				if (zk.parseInt(text1) <= this.maxsize)
					inp.value = val.substring(0, start) + text1 + val.substring(end, val.length);
			}
		}
		zk(inp).setSelectionRange(sel[1], sel[1]);
	},
	getText: function (val) {
		var start = this.index[0],
			end = this.index[1] + 1;
		return val.substring(start, end);
	}
});
zul.inp.HourInDayHandler = zk.$extends(zul.inp.TimeHandler, {
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
			else if (h < 10)
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
			else if (h < 10)
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
		var hours = zk.parseInt(this.getText(val));
		date.setHours(am ? hours : hours + 12);
		return date;
	}
});
zul.inp.MinuteHandler = zk.$extends(zul.inp.TimeHandler, {
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
		date.setMinutes(zk.parseInt(this.getText(val)));
		return date;
	}
});
zul.inp.SecondHandler = zk.$extends(zul.inp.TimeHandler, {
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
		date.setSeconds(zk.parseInt(this.getText(val)));
		return date;
	}
});
zul.inp.AMPMHandler = zk.$extends(zul.inp.TimeHandler, {
	format: function (date) {
		if (!date)
			return zk.APM[0];
		var h = date.getHours();
		return zk.APM[h < 12 ? 0 : 1];
	},
	unformat: function (date, val) {
		return zk.APM[0] == this.getText(val);
	},
	deleteTime: zk.$void,
	increase: function (wgt, up) {
		var inp = wgt.getInputNode(),
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end);

		text = zk.APM[0] == text ? zk.APM[1] : zk.APM[0];
		inp.value = val.substring(0, start) + text + val.substring(end, val.length);
		zk(inp).setSelectionRange(start, end);
	}
});