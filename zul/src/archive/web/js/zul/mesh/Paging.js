/* Paging.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 15:00:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _rerenderIfBothPaging(wgt) {
		if (wgt.isBothPaging()) {
			wgt.parent.rerender();
			return true;
		}
	}
	
	//Returns whether the string is integer or not
	function _isUnsignedInteger(s) {
		  return (s.toString().search(/^[0-9]+$/) == 0);
	}

/**
 * Paging of long content.
 *
 * <p>Default {@link #getZclass}: z-paging.
 */
zul.mesh.Paging = zk.$extends(zul.Widget, {
	_pageSize: 20,
	_totalSize: 0,
	_pageCount: 1,
	_activePage: 0,
	_pageIncrement: zk.mobile ? 5 : 10,

	$define: { //zk.def
    	/** Returns the total number of items.
    	 * @return int
    	 */
    	/** Sets the total number of items.
    	 * @param int totalSize
    	 */
		totalSize: function () {
			this._updatePageNum();
			if (this._detailed) {
				if (!_rerenderIfBothPaging(this)) {
					var info = this.$n('info');
					if (info) {
						info.innerHTML = this.infoText_();
					} else if (this._totalSize) {
						this.rerender(); // recreate infoTag
					}
				}
			}
		},
		/** Returns the number of page anchors shall appear at the client. 
		 *
		 * <p>Default: 10.
		 * @return int
		 */
		/** Sets the number of page anchors shall appear at the client.
		 * @param int pageIncrement
		 */
		pageIncrement: _zkf = function () {
			this.rerender();
		},
		/** Returns whether to show the detailed info, such as {@link #getTotalSize}.
		 * @return boolean
		 */
		/** Sets whether to show the detailed info, such as {@link #getTotalSize}.
		 * @param boolean detailed
		 */
		detailed: _zkf,
		/** Returns the number of pages.
		 * Note: there is at least one page even no item at all.
		 * @return int
		 */
		/** Sets the number of pages.
		 * Note: there is at least one page even no item at all.
		 * @param int pageCount
		 */
		pageCount: _zkf, //TODO: smarter algorithm
		/** Returns the active page (starting from 0).
		 * @return int
		 */
		/** Sets the active page (starting from 0).
		 * @param int activePage
		 */
		activePage: _zkf,
		/** Returns the page size, aka., the number rows per page.
		 * @return int
		 */
		/** Sets the page size, aka., the number rows per page.
		 * @param int pageSize
		 */
		pageSize: function () {
			this._updatePageNum();
		},
		/**
		 * Returns whether to automatically hide this component if there is only one
		 * page available.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether to automatically hide this component if there is only one
		 * page available.
		 * @param boolean autohide
		 */
		autohide: function () {
			if (this._pageCount == 1) this.rerender();
		}
	},
	setStyle: function () {
		this.$supers('setStyle', arguments);
		_rerenderIfBothPaging(this)
	},
	setSclass: function () {
		this.$supers('setSclass', arguments);
		_rerenderIfBothPaging(this);
	},
	setWidth: function () {
		this.$supers('setWidth', arguments);
		_rerenderIfBothPaging(this);
	},
	setHeight: function () {
		this.$supers('setHeight', arguments);
		_rerenderIfBothPaging(this);
	},
	setLeft: function () {
		this.$supers('setLeft', arguments);
		_rerenderIfBothPaging(this);
	},
	setTop: function () {
		this.$supers('setTop', arguments);
		_rerenderIfBothPaging(this);
	},
	setTooltiptext: function () {
		this.$supers('setTooltiptext', arguments);
		_rerenderIfBothPaging(this);
	},
	replaceHTML: function () {
		if (!_rerenderIfBothPaging(this))
			this.$supers('replaceHTML', arguments);
	},
	/**
	 * Returns whether the paging is in both mold. i.e. Top and Bottom
	 * @return boolean
	 */
	isBothPaging: function () {
		return this.parent && this.parent.getPagingPosition
					&& 'both' == this.parent.getPagingPosition();
	},
	_updatePageNum: function () {
		var v = Math.floor((this._totalSize - 1) / this._pageSize + 1);
		if (v == 0) v = 1;
		if (v != this._pageCount) {
			this._pageCount = v;
			if (this._activePage >= this._pageCount)
				this._activePage = this._pageCount - 1;
			if (this.desktop && this.parent) {
				if (!_rerenderIfBothPaging(this)) {
					this.rerender();

					// Bug 2931951
					if (this.parent.$instanceof(zul.mesh.MeshWidget)) {
						var self = this;
						// Bug ZK-2624
						setTimeout(function () {
							if (self.desktop) {
								var n = self.parent.$n();
		
								// reset and recalculate
								if (n && n._lastsz) {
									n._lastsz = null;
									self.parent.onSize();
								}
							}
						});
					}
				}
			}
		}
	},
	/**
	 * Returns the information text of the paging, if {@link #isDetailed()} is enabled.
	 * @return String
	 */
	infoText_: function () {
		var acp = this._activePage,
			psz = this._pageSize,
			tsz = this._totalSize,
			lastItem = (acp + 1) * psz,
			dash = '';
		
		if ('os' != this.getMold())
			dash = ' - ' + (lastItem > tsz ? tsz : lastItem);
		
		return '[ ' + (acp * psz + 1) + dash + ' / ' + tsz + ' ]';
	},
	_infoTags: function (out) {
		if (this._totalSize == 0)
			return;
		out.push('<div class="', this.$s('info'), '"><span ',
				_rerenderIfBothPaging(this) ? 'name' : 'id', // Bug ZK-2280
				'="', this.uuid,
				'-info">', this.infoText_(), '</span></div>');
	},
	_innerTags: function () {
		var out = [],
			pinc = this._pageIncrement,
			pcount = this._pageCount,
			acp = this._activePage,
			half = Math.round(pinc / 2),
			begin,
			end = this._activePage + half - 1;
		
		if (end >= pcount) {
			end = pcount - 1;
			begin = end - pinc + 1;
			if (begin < 0)
				begin = 0;
		} else {
			begin = this._activePage - half;
			if (begin < 0)
				begin = 0;
			end = begin + pinc - 1;
			if (end >= pcount)
				end = pcount - 1;
		}
		out.push('<ul>');
		if (acp > 0) {
			if (begin > 0) //show first
				this.appendAnchor(out, msgzul.FIRST, 0);
			this.appendAnchor(out, msgzul.PREV, acp - 1);
		}

		var bNext = acp < pcount - 1;
		for (; begin <= end; ++begin)
			this.appendAnchor(out, begin + 1, begin, begin == acp);

		if (bNext) {
			this.appendAnchor(out, msgzul.NEXT, acp + 1);
			if (end < pcount - 1) //show last
				this.appendAnchor(out, msgzul.LAST, pcount - 1);
		}
		out.push('</ul>');
		if (this._detailed)
			this._infoTags(out);
		return out.join('');
	},
	appendAnchor: function (out, label, val, seld) {
		var isInt = _isUnsignedInteger(label),
			cls = this.$s('button');
		
		if (!isInt)
			cls += ' ' + this.$s('noborder');
		if (seld)
			cls += ' ' + this.$s('selected');
		
		out.push('<li><a class="', cls,
				'" href="javascript:;" onclick="zul.mesh.Paging.go(this,', val,
				')">', label, '</a></li>');
	},
	domClass_: function () {
		var cls = this.$supers(zul.mesh.Paging, 'domClass_', arguments),
			added = 'os' == this.getMold() ? ' ' + this.$s('os') : '';
		return cls + added;
	},
	isVisible: function () {
		var visible = this.$supers('isVisible', arguments);
		return visible && (this._pageCount > 1 || !this._autohide);
	},
	bind_: function () {
		this.$supers(zul.mesh.Paging, 'bind_', arguments);
		var uuid = this.uuid,
			input = jq.$$(uuid, 'real'),
			Paging = this.$class,
			pcount = this._pageCount,
			acp = this._activePage,
			postfix = ['first', 'prev', 'last', 'next'],
			focusInfo = zul.mesh.Paging._autoFocusInfo;

		if (!this.$weave)
			for (var i = input.length; i--;)
				jq(input[i]).keydown(Paging._domKeyDown).blur(Paging._domBlur);

		for (var k = postfix.length; k--; ) {
			var btn = jq.$$(uuid, postfix[k]);
			for (var j = btn.length; j--;) {
				if (!this.$weave)
					jq(btn[j]).click(Paging['_dom' + postfix[k] + 'Click']);
	
				if (pcount == 1) {
					jq(btn[j]).attr('disabled', true);
				} else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (acp == 0)
						jq(btn[j]).attr('disabled', true);
				} else if (acp == pcount - 1) {
					jq(btn[j]).attr('disabled', true);
				}
			}
		}
		
		if(focusInfo && focusInfo.uuid === this.uuid) {			
			var pos = focusInfo.lastPos,
				zinp = zk(input[focusInfo.inpIdx]);
			zinp.focus();
			zinp.setSelectionRange(pos[0], pos[1]);
			zul.mesh.Paging._autoFocusInfo = null;
		}
	},
	unbind_: function () {
		if (this.getMold() != 'os') {
			var uuid = this.uuid,
				input = jq.$$(uuid, 'real'),
				Paging = this.$class,
				postfix = ['first', 'prev', 'last', 'next'];

			for (var i = input.length; i--;)
				jq(input[i])
					.unbind('keydown', Paging._domKeyDown)
					.unbind('blur', Paging._domBlur);

			for (var k = postfix.length; k--;) {
				var btn = jq.$$(uuid, postfix[k]);
				for (j = btn.length; j--;)
					jq(btn[j]).unbind('click', Paging['_dom' + postfix[k] + 'Click']);
			}
		}
		this.$supers(zul.mesh.Paging, 'unbind_', arguments);
	}
}, { //static
	/**
	 * Goes to the active page according to the page number.
	 * @param DOMElement anc the anchor of the page number
	 * @param int pagenumber the page number
	 */
	go: function (anc, pgno, inp) {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$(anc);
		if (wgt && wgt.getActivePage() != pgno) {
			if(inp) {
				var uuid = wgt.uuid,
					focusInfo = zul.mesh.Paging._autoFocusInfo = {uuid: uuid};
				focusInfo.lastPos = zk(inp).getSelectionRange();
				// concern about _pagingPosition equals "both"
				jq(jq.$$(uuid, 'real')).each(function(idx){
					if(this == inp) {
						focusInfo.inpIdx = idx;
						return false;
					}
				});
			}
			wgt.fire('onPaging', pgno);
		}
	},
	_domKeyDown: function (evt) {
		var inp = evt.target,
			wgt = zk.Widget.$(inp),
			lastPos = zk(inp).getSelectionRange();
		if (inp.disabled || inp.readOnly)
			return;

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
			break;
		case 37://left
			break;
		case 38: //up
			wgt.$class._increase(inp, wgt, 1);
			evt.stop();
			break;
		case 39://right
			break;
		case 40: //down
			wgt.$class._increase(inp, wgt, -1);
			evt.stop();
			break;
		case 33: // PageUp
			wgt.$class._increase(inp, wgt, -1);
			wgt.$class.go(wgt, inp.value-1, inp);
			evt.stop();
			break;
		case 34: // PageDown
			wgt.$class._increase(inp, wgt, +1);
			wgt.$class.go(wgt, inp.value-1, inp);
			evt.stop();
			break;
		case 36://home
			wgt.$class.go(wgt,0, inp);
			evt.stop();
			break;
		case 35://end
			wgt.$class.go(wgt, wgt._pageCount - 1, inp);
			evt.stop();
			break;
		case 9: case 8: case 46: //tab, backspace, delete 
			break;
		case 13: //enter
			wgt.$class._increase(inp, wgt, 0);
			wgt.$class.go(wgt, inp.value-1, inp);
			evt.stop();
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
					&& !evt.ctrlKey && !evt.altKey)
				evt.stop();
		}
	},
	_domBlur: function (evt) {
		var inp = evt.target,
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;

		wgt.$class._increase(inp, wgt, 0);
		wgt.$class.go(wgt, inp.value-1);
		evt.stop();
	},
	_increase: function (inp, wgt, add){
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1)
			value = 1;
		else if (value > wgt._pageCount)
			value = wgt._pageCount;
		inp.value = value;
	},
	_domfirstClick: function (evt) {
		var wgt = zk.Widget.$(evt),
			uuid = wgt.uuid,
			postfix = ['first', 'prev'];
		
		if (wgt.getActivePage() != 0) {
			wgt.$class.go(wgt, 0);
			for (var k = postfix.length; k--;)
				for (var btn = jq.$$(uuid, postfix[k]), i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
	},
	_domprevClick: function (evt) {
		var wgt = zk.Widget.$(evt),
			uuid = wgt.uuid,
			ap = wgt.getActivePage(),
			postfix = ['first', 'prev'];

		if (ap > 0) {
			wgt.$class.go(wgt, ap - 1);
			if (ap - 1 == 0) {
				for (var k = postfix.length; k--;)
					for (var btn = jq.$$(uuid, postfix[k]), i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
	},
	_domnextClick: function (evt) {
		var wgt = zk.Widget.$(evt),
			uuid = wgt.uuid,
			ap = wgt.getActivePage(),
			pc = wgt.getPageCount(),
			postfix = ['last', 'next'];

		if (ap < pc - 1) {
			wgt.$class.go(wgt, ap + 1);
			if (ap + 1 == pc - 1) {
				for (var k = postfix.length; k--;)
					for (var btn = jq.$$(uuid, postfix[k]), i = btn.length; i--;)
						jq(btn[i]).attr('disabled', true);
			}
		}
	},
	_domlastClick: function (evt) {
		var wgt = zk.Widget.$(evt),
			uuid = wgt.uuid,
			pc = wgt.getPageCount(),
			postfix = ['last', 'next'];

		if (wgt.getActivePage() < pc - 1) {
			wgt.$class.go(wgt, pc - 1);
			for (var k = postfix.length; k--;)
				for (var btn = jq.$$(uuid, postfix[k]), i = btn.length; i--;)
					jq(btn[i]).attr('disabled', true);
		}
	}
});

})();