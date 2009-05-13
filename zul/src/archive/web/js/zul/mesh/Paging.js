/* Paging.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 15:00:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.mesh.Paging = zk.$extends(zul.Widget, {
	_pageSize: 20,
	_totalSize: 0,
	_pageCount: 1,
	_activePage: 0,
	_pageIncrement: 10,
	_autohide: 10,
	replaceHTML: function () {
		if (this.isBothPaging())
			this.parent.rerender();
		else
			this.$supers('replaceHTML', arguments);
	},
	isBothPaging: function () {
		return this.parent && this.parent.getPagingPosition
					&& "both" == this.parent.getPagingPosition();
	},
	_updatePageNum: function () {
		var v = Math.floor((this._totalSize - 1) / this._pageSize + 1);
		if (v == 0) v = 1;
		if (v != this._pageCount) {
			this._pageCount = v;
			if (this._activePage >= this._pageCount)
				this._activePage = this._pageCount - 1;
		}
	},
	infoText_: function () {
		var lastItem = (this._activePage+1) * this._pageSize;
		return "[ " + (this._activePage * this._pageSize + 1) + ("os" != this.getMold() ?
			" - " + (lastItem > this._totalSize ? this._totalSize : lastItem) : "")+ " / " + this._totalSize + " ]";
	},
	_infoTags: function () {
		if (this._totalSize == 0)
			return "";
		var lastItem = (this._activePage+1) * this._pageSize,
			out = [];
		out.push('<div id="', this.uuid ,'$info" class="', this.getZclass(), '-info">', this.infoText_(), '</div>');
		return out.join('');
	},
	_innerTags: function () {
		var out = [];

		var half = this._pageIncrement / 2,
			begin, end = this._activePage + half - 1;
		if (end >= this._pageCount) {
			end = this._pageCount - 1;
			begin = end - this._pageIncrement + 1;
			if (begin < 0) begin = 0;
		} else {
			begin = this._activePage - half;
			if (begin < 0) begin = 0;
			end = begin + this._pageIncrement - 1;
			if (end >= this._pageCount) end = this._pageCount - 1;
		}
		var zcs = this.getZclass();
		if (this._activePage > 0) {
			if (begin > 0) //show first
				this.appendAnchor(zcs, out, msgzul.FIRST, 0);
			this.appendAnchor(zcs, out, msgzul.PREV, this._activePage - 1);
		}

		var bNext = this._activePage < this._pageCount - 1;
		for (; begin <= end; ++begin) {
			if (begin == this._activePage) {
				this.appendAnchor(zcs, out, begin + 1, begin, true);
			} else {
				this.appendAnchor(zcs, out, begin + 1, begin);
			}
		}

		if (bNext) {
			this.appendAnchor(zcs, out, msgzul.NEXT, this._activePage + 1);
			if (end < this._pageCount - 1) //show last
				this.appendAnchor(zcs, out, msgzul.LAST, this._pageCount - 1);
		}
		if (this._detailed)
			out.push('<span id="', this.uuid ,'$info">', this.infoText_(), "</span>");
		return out.join('');
	},
	appendAnchor: function (zclass, out, label, val, seld) {
		zclass += "-cnt" + (seld ? " " + zclass + "-seld" : "");
		out.push('<a class="', zclass, '" href="javascript:;" onclick="zul.mesh.Paging.go(this,',
				val, ')">', label, '</a>&nbsp;');
	},
	getZclass: function () {
		var added = "os" == this.getMold() ? "-os" : "";
		return this._zclass == null ? "z-paging" + added : this._zclass;
	},
	isVisible: function () {
		var visible = this.$supers('isVisible', arguments);
		return visible && (this._pageCount > 1 || !this._autohide);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getMold() == "os") return;
		var uuid = this.uuid,
			inputs = zDom.$$(uuid, 'real'),
			zcls = this.getZclass(),
			$Paging = this.$class;

		if (!this.inDesign)
			for (var i = inputs.length; --i>=0;) {
				zEvt.listen(inputs[i], "keydown", $Paging._domKeyDown);
				zEvt.listen(inputs[i], "blur", $Paging._domBlur);
			}
		
		for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >=0; ) {
			var btn = zDom.$$(uuid, postfix[k]);
			for (var j = btn.length; --j>=0;) {
				if (!this.inDesign) {
					zEvt.listen(btn[j], "mouseover", $Paging._domMouseOver);
					zEvt.listen(btn[j], "mouseout", $Paging._domMouseOut);
					zEvt.listen(btn[j], "mousedown", $Paging._domMouseDown);
					zEvt.listen(btn[j], "click", $Paging['_dom' + postfix[k] + 'Click']);
				}

				if (this._pageCount == 1)
					zDom.addClass(btn[j], zcls + "-btn-disd");
				else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (this._activePage == 0) zDom.addClass(btn[j], zcls + "-btn-disd");
				} else if (this._activePage == this._pageCount - 1) {
					zDom.addClass(btn[j], zcls + "-btn-disd");
				}
			}
		}
	},
	unbind_: function () {
		if (this.getMold() != "os") {
			var uuid = this.uuid, inputs = zDom.$$(uuid, 'real'), $Paging = this.$class;
			
			for (var i = inputs.length; --i >= 0;) {
				zEvt.unlisten(inputs[i], "keydown", $Paging._domKeyDown);
				zEvt.unlisten(inputs[i], "blur", $Paging._domBlur);
			}
			
			for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >= 0;) {
				var btn = zDom.$$(uuid, postfix[k]);
				for (var j = btn.length; --j >= 0;) {
					zEvt.unlisten(btn[j], "mouseover", $Paging._domMouseOver);
					zEvt.unlisten(btn[j], "mouseout", $Paging._domMouseOut);
					zEvt.unlisten(btn[j], "mousedown", $Paging._domMouseDown);
					zEvt.unlisten(btn[j], "click", $Paging['_dom' + postfix[k] + 'Click']);
				}
			}
		}
		this.$supers('unbind_', arguments);
	}
}, { //static
	go: function (anc, pgno) {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$(anc);
		if (wgt && wgt.getActivePage() != pgno)
			wgt.fire('onPaging', pgno);
	},
	_domKeyDown: function (evt) {
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
			wgt.$class.go(wgt, wgt._pageCount - 1);
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
	_domBlur: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
		
		wgt.$class._increase(inp, wgt, 0);
		wgt.$class.go(wgt, inp.value-1);
		zEvt.stop(evt);
	},
	_increase: function (inp, wgt, add){
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1) value = 1;
		else if (value > wgt._pageCount) value = wgt._pageCount;
		inp.value = value;
	},
	_domfirstClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() != 0) {
			wgt.$class.go(wgt, 0);
			var uuid = wgt.uuid;
			for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
	},
	_domprevClick: function (evt) {		
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			zcls = wgt.getZclass();
		
		if (ap > 0) {
			wgt.$class.go(wgt, ap - 1);
			if (ap - 1 == 0) {
				var uuid = wgt.uuid;
				for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_domnextClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (ap < pc - 1) {
			wgt.$class.go(wgt, ap + 1);
			if (ap + 1 == pc - 1) {
				var uuid = wgt.uuid;
				for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_domlastClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() < pc - 1) {
			wgt.$class.go(wgt, pc - 1);
			var uuid = wgt.uuid;
			for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
		
	},
	_domMouseOver: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.ancestor(target, "TABLE"),
			zcls = zk.Widget.$(target).getZclass();
		if (!zDom.hasClass(table, zcls + "-btn-disd")) 
			zDom.addClass(table, zcls + "-btn-over");
	},
	_domMouseOut: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.ancestor(target, "TABLE"),
			wgt = zk.Widget.$(target);
		zDom.rmClass(table, wgt.getZclass() + "-btn-over");
	},
	_domMouseDown: function (evt) {		
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.ancestor(target, "TABLE"),
			wgt = zk.Widget.$(target),
			zcls = wgt.getZclass();
		if (zDom.hasClass(table, zcls + "-btn-disd")) return;
		
		zDom.addClass(table, zcls + "-btn-clk");
		wgt.$class._downbtn = table;
		zEvt.listen(document.body, "mouseup", wgt.$class._domMouseUp);
	},
	_domMouseUp: function (evt) {
		if (!evt) evt = window.event;
		if (zul.mesh.Paging._downbtn) {
			var zcls = zk.Widget.$(zul.mesh.Paging._downbtn).getZclass();
			zDom.rmClass(zul.mesh.Paging._downbtn, zcls + "-btn-clk");
		}
		zul.mesh.Paging._downbtn = null;
		zEvt.unlisten(document.body, "mouseup", zul.mesh.Paging._domMouseUp);
	}
}), { //zk.def
	totalSize: function () {
		this._updatePageNum();
		if (this._detailed) {
			if (this.isBothPaging()) this.rerender();
			else {
				var info = this.getSubnode("info");
				if (info) info.innerHTML = this.infoText_();
			}
		}
	},
	pageIncrement: _zkf = function () {
		this.rerender();
	},
	detailed: _zkf,
	pageCount: _zkf, //TODO: smarter algorithm
	activePage: null, //TODO this.fire('onPagingImpl', this._activePage);
	pageSize: function () {
		this._updatePageNum();
		// TODO this.fire('onPagingImpl', this._activePage);
	},
	autohide: function () {
		if (this._pageCount == 1) this.rerender();
	}
});
