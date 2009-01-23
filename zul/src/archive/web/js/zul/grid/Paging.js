/* Paging.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 15:00:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Paging = zk.$extends(zul.Widget, {
	_pgsz: 20,
	_ttsz: 0,
	_npg: 1,
	_actpg: 0,
	_pginc: 10,
	_autohide: 10,
	replaceHTML: function () {
		if (this.isBothPaging())
			this.parent.rerender();
		else
			this.$supers('replaceHTML', arguements);
	},
	isBothPaging: function () {
		return this.parent && this.parent.getPagingPosition
					&& "both" == this.parent.getPagingPosition();
	},
	getPageSize: function () {
		return this._pgsz;
	},
	setPageSize: function (size) {
		if (this._pgsz != size) {
			this._pgsz = size;
			this._updatePageNum();
			this.fire('onPagingImpl', _actpg);
		}
	},
	getTotalSize: function () {
		return this._ttsz;
	},
	setTotalSize: function (size) {
		if (this._ttsz != size) {
			this._ttsz = size;
			this._updatePageNum();
			if (this._detailed) rerender();
		}
	},
	_updatePageNum: function () {
		var v = Math.floor((this._ttsz - 1) / this._pgsz + 1);
		if (v == 0) v = 1;
		if (v != this._npg) {
			this._npg = v;
			if (this._actpg >= this._npg)
				this._actpg = this._npg - 1;
		}
	},
	getPageCount: function () {
		return this._npg;
	},
	setPageCount: function (npg) {
		this._npg = npg;
	},
	getActivePage: function () {
		return this._actpg;
	},
	setActivePage: function (pg) {
		if (this._actpg != pg) {
			this._actpg = pg;
			this.fire('onPagingImpl', _actpg);
		}
	},
	getPageIncrement: function () {
		return this._pginc;
	},
	setPageIncrement: function (pginc) {
		if (_pginc != pginc) {
			_pginc = pginc;
			this.rerender();
		}
	},
	isDetailed: function () {
		return this._detailed;
	},
	setDetailed: function (detailed) {
		if (this._detailed != detailed) {
			this._detailed = detailed;
			this.rerender();
		}
	},
	isAutohide: function () {
		return this._autohide;
	},
	setAutohide: function (autohide) {
		if (this._autohide != autohide) {
			this._autohide = autohide;
			if (this._npg == 1) this.rerender();
		}
	},
	_infoTags: function () {
		if (this._ttsz == 0)
			return "";
		var lastItem = (this._actpg+1) * this._pgsz,
			out = [];
		out.push('<div class="', this.getZclass(), '-info">[ ', lastItem + 1,
				' - ', lastItem > this._ttsz ? this._ttsz : lastItem, ' / ',
				this._ttsz, ' ]</div>');
		return out.join('');
	},
	_innerTags: function () {
		var out = [];

		var half = this._pginc / 2,
			begin, end = this._actpg + half - 1;
		if (end >= this._npg) {
			end = this._npg - 1;
			begin = end - this._pginc + 1;
			if (begin < 0) begin = 0;
		} else {
			begin = this._actpg - half;
			if (begin < 0) begin = 0;
			end = begin + this._pginc - 1;
			if (end >= this._npg) end = this._npg - 1;
		}
		var zcs = this.getZclass();
		if (this._actpg > 0) {
			if (begin > 0) //show first
				this.appendAnchor(zcs, out, msgzul.FIRST, 0);
			this.appendAnchor(zcs, out, msgzul.PREV, this._actpg - 1);
		}

		var bNext = this._actpg < this._npg - 1;
		for (; begin <= end; ++begin) {
			if (begin == this._actpg) {
				this.appendAnchor(zcs, out, begin + 1, begin, true);
			} else {
				this.appendAnchor(zcs, out, begin + 1, begin);
			}
		}

		if (bNext) {
			this.appendAnchor(zcs, out, msgzul.NEXT, this._actpg + 1);
			if (end < this._npg - 1) //show last
				this.appendAnchor(zcs, out, msgzul.LAST, this._npg - 1);
		}
		if (this._detailed)
			out.push('<span>[', this._actpg * this._pgsz + 1, '/', this._ttsz, "]</span>");
		return out.join('');
	},
	appendAnchor: function (zclass, out, label, val, seld) {
		zclass += "-cnt" + (seld ? " " + zclass + "-seld" : "");
		out.push('<a class="', zclass, '" href="javascript:;" onclick="zul.grid.Paging.go(this,',
				val, ')">', label, '</a>&nbsp;');
	},
	getZclass: function () {
		var added = "os" == this.getMold() ? "-os" : "";
		return this._zclass == null ? "z-paging" + added : this._zclass;
	},
	isVisible: function () {
		var visible = this.$supers('isVisible', arguments);
		return visible && (this._npg > 1 || !this._autohide);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var uuid = this.uuid,
			inputs = zDom.$$(uuid, 'real'),
			$Paging = this.$class,
			zcls = this.getZclass(),
			first = zDom.$$(uuid, 'first'),
			last = zDom.$$(uuid, 'last'),
			prev = zDom.$$(uuid, 'prev'),
			next = zDom.$$(uuid, 'next');
		
		//event for input
		for (var i = inputs.length; --i>=0;) {
			zEvt.listen(inputs[i], "keydown", $Paging._doKeyDown);
			zEvt.listen(inputs[i], "blur", $Paging._doBlur);
		}
		
		for (var i = first.length; --i>=0;) {
			zEvt.listen(first[i], "click", $Paging._doFirstClick);
			zEvt.listen(first[i], "mouseover", $Paging._doMouseOver);
			zEvt.listen(first[i], "mouseout", $Paging._doMouseOut);
			zEvt.listen(first[i], "mousedown", $Paging._doMouseDown);
			
			zEvt.listen(prev[i], "click", $Paging._doPrevClick);
			zEvt.listen(prev[i], "mouseover", $Paging._doMouseOver);
			zEvt.listen(prev[i], "mouseout", $Paging._doMouseOut);
			zEvt.listen(prev[i], "mousedown", $Paging._doMouseDown);
			
			zEvt.listen(next[i], "click", $Paging._doNextClick);
			zEvt.listen(next[i], "mouseover", $Paging._doMouseOver);
			zEvt.listen(next[i], "mouseout", $Paging._doMouseOut);
			zEvt.listen(next[i], "mousedown", $Paging._doMouseDown);
			
			zEvt.listen(last[i], "click", $Paging._doLastClick);
			zEvt.listen(last[i], "mouseover", $Paging._doMouseOver);
			zEvt.listen(last[i], "mouseout", $Paging._doMouseOut);
			zEvt.listen(last[i], "mousedown", $Paging._doMouseDown);
						
			if (this._npg == 1) {
				zDom.addClass(first[i], zcls + "-btn-disd");
				zDom.addClass(prev[i], zcls + "-btn-disd");
				zDom.addClass(next[i], zcls + "-btn-disd");
				zDom.addClass(last[i], zcls + "-btn-disd");
			} else {
				if (cmp.actpg == 0) {
					zk.addClass(first[i], zcls + "-btn-disd");
					zk.addClass(prev[i], zcls + "-btn-disd");
				} else if (cmp.actpg == cmp.npg - 1) {
					zk.addClass(next[i], zcls + "-btn-disd");
					zk.addClass(last[i], zcls + "-btn-disd");
				}
			}
		}
	},
	unbind_: function () {
		
		var uuid = this.uuid,
			inputs = zDom.$$(uuid, 'real'),
			$Paging = this.$class,
			first = zDom.$$(uuid, 'first'),
			last = zDom.$$(uuid, 'last'),
			prev = zDom.$$(uuid, 'prev'),
			next = zDom.$$(uuid, 'next');
			
		for (var i = inputs.length; --i>=0;) {
			zEvt.listen(inputs[i], "keydown", $Paging._doKeyDown);
			zEvt.listen(inputs[i], "blur", $Paging._doBlur);
		}
		
		for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >=0; ) {
			var btn = zDom.$$(uuid, postfix[k]);
			for (var j = btn.length; --j>=0;) {
				zEvt.listen(btn[j], "mouseover", $Paging._doMouseOver);
				zEvt.listen(btn[j], "mouseout", $Paging._doMouseOut);
				zEvt.listen(btn[j], "mousedown", $Paging._doMouseDown);
				zEvt.listen(btn[j], "click", $Paging['_do' + postfix[k] + 'Click']);
				if (this._npg == 1)
					zDom.addClass(btn[j], zcls + "-btn-disd");
				else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (this._actpg == 0) zDom.addClass(btn[i], zcls + "-btn-disd");
				} else if (this._actpg == this._npg - 1) {
					zk.addClass(btn[i], zcls + "-btn-disd");
				}
			}
		}
		
		this.$supers('unbind_', arguments);
	}
}, {
	go: function (anc, pgno) {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$(anc);
		if (wgt)
			wgt.fire('onPaging', pgno);
	},
	_doKeyDown: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
	
		var code =zEvt.keyCode(evt);
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
			break;		
		case 37://left
			break;		
		case 38: case 33: //up, PageUp
			wgt.$class._increase(inp, wgt, 1);
			zEvt.stop(evt);
			break;
		case 39://right
			break;		
		case 40: case 34: //down, PageDown
			wgt.$class._increase(inp, wgt, -1);
			zEvt.stop(evt);
			break;
		case 36://home
			wgt.$class.go(wgt,0);
			zEvt.stop(evt);
			break;
		case 35://end
			wgt.$class.go(wgt, wgt._npg - 1);
			zEvt.stop(evt);
			break;
		case 9: case 8: case 46: //tab, backspace, delete 
			break;
		case 13: //enter
			wgt.$class._increase(inp, wgt, 0);
			wgt.$class.go(wgt, inp.value-1);
			zEvt.stop(evt);
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				zEvt.stop(evt);
		}
	},
	_doBlur: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
		
		this._increase(inp, wgt, 0);
		this.go(wgt, inp.value-1);
		zEvt.stop(evt);
	},
	_increase: function (inp, wgt, add){
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1) value = 1;
		else if (value > wgt._npg) value = wgt._npg;
		inp.value = value;
	}
});
