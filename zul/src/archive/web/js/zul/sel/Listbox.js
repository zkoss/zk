/* Listbox.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:16:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _isListgroup(wgt) {
		return zk.isLoaded('zkex.sel') && wgt.$instanceof(zkex.sel.Listgroup);
	}
	function _syncFrozen(wgt) {
		if (wgt._nativebar && (wgt = wgt.frozen))
			wgt._syncFrozen();
	}
	function _fixForEmpty(wgt) {
		if (wgt.desktop) {
			var $jq = jq(wgt.$n('empty')),
				colspan = 0;
			if (wgt._nrows) {
				$jq.hide();
			} else {
				if (wgt.listhead) {
					for (var w = wgt.listhead.firstChild; w; w = w.nextSibling)
						if (w.isVisible())
							colspan++;
				}
				$jq.attr('colspan', colspan || 1);
				$jq.show();
			}
		}
		wgt._shallFixEmpty = false;
	}

var Listbox =
/**
 * A listbox.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>onSelect event is sent when user changes the selection.</li>
 * </ol>
 * 
 * <p>
 * Default {@link #getZclass}: z-listbox.
 * 
 * <p>
 * To have a list box without stripping, you can specify a non-existent style
 * class to {@link #setOddRowSclass}.
 * @import zkex.sel.Listgroup
 */
zul.sel.Listbox = zk.$extends(zul.sel.SelectWidget, {
	_nrows: 0,
	/** 
	 * Whether to allow Listgroup to be selected
	 * <p>Default: false
	 * @since 5.0.7
	 * @type boolean
	 */
	groupSelect: false,
	_scrollbar: null,
	$define:{
		emptyMessage: function(msg) {
			if(this.desktop)
				jq(this.$n('empty')).html(msg);
		}
	},	
	$init: function () {
		this.$supers(Listbox, '$init', arguments);
		this._groupsInfo = [];
	},
	/**
	 * Returns the number of listgroup
	 * @return int
	 */
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	/**
	 * Returns a list of all {@link Listgroup}.
	 * @return Array
	 */
	getGroups: function () {
		return this._groupsInfo.$clone();
	},
	/**
	 * Returns whether listgroup exists.
	 * @return boolean
	 */
	hasGroup: function () {
		return this._groupsInfo.length;
	},
	/**
	 * Returns the next item.
	 * @return Listitem
	 * @param zk.Widget item
	 */
	nextItem: function (p) {
		if (p)
			while ((p = p.nextSibling) && !p.$instanceof(zul.sel.Listitem));
		return p;
	},
	/**
	 * Returns the previous item.
	 * @return Listitem
	 * @param zk.Widget item
	 */
	previousItem: function (p) {
		if (p)
			while ((p = p.previousSibling) && !p.$instanceof(zul.sel.Listitem));
		return p;
	},
	/**
	 * Returns the style class for the odd rows.
	 * <p>
	 * Default: {@link #getZclass()}-odd.
	 * @return String
	 */
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.$s('odd') : this._scOddRow;
	},
	/**
	 * Sets the style class for the odd rows. If the style class doesn't exist,
	 * the striping effect disappears. You can provide different effects by
	 * providing the proper style classes.
	 * @param String oddRowSclass
	 * @return Listbox
	 */
	setOddRowSclass: function (scls) {
		if (!scls) scls = null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.$n();
			if (n && this.rows)
				this.stripe();
		}
		return this;
	},
	/**
	 * Returns whether the HTML's select tag is used.
	 * @return boolean
	 */
	inSelectMold: function () {
		return 'select' == this.getMold();
	},
	// bug ZK-56 for non-ROD to scroll after onSize ready
	onSize: function () {
		this.$supers(Listbox, 'onSize', arguments);
		var self = this,
			canInitScrollbar =  this.desktop && !this.inSelectMold() && !this._nativebar;
		if (!this._scrollbar && canInitScrollbar)
			this._scrollbar = zul.mesh.Scrollbar.init(this); // 1823278: should show scrollbar here
		setTimeout(function () {
			if (canInitScrollbar) {
				if (!self._listbox$rod || self.inPagingMold()) {
					self.refreshBar_();
					self._syncSelInView();
				}
			}
		}, 300);
	},
	refreshBar_: function (showBar, scrollToTop) {
		var bar = this._scrollbar;
		if (bar) {
			bar.syncSize(showBar || this._shallShowScrollbar);
			this._shallShowScrollbar = false;
			if (scrollToTop)
				bar.scrollTo(0, 0);
			else
				bar.scrollTo(this._currentLeft, this._currentTop);
			//sync frozen
			var frozen = this.frozen,
				start;
			if (frozen && (start = frozen._start) != 0) {
				frozen._doScrollNow(start);
				bar.setBarPosition(start);
			}
		}
	},
	destoryBar_: function () {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(Listbox, 'bind_', arguments); //it might invoke replaceHTML and then call bind_ again
		this._shallStripe = true;
		var w = this;
		after.push(function () {
			w.stripe();
			_syncFrozen(w);
			_fixForEmpty(w);
		});
		this._shallScrollIntoView = true;
	},
	unbind_: function () {
		this.destoryBar_();
		this.$supers(Listbox, 'unbind_', arguments);
	},
	_syncSelInView: function () {
		if (this._shallScrollIntoView) {
			var index = this.getSelectedIndex();
			if (index >= 0) { // B50-ZK-56
				var si, top = jq(this.$n()).offset().top;
				for (var it = this.getBodyWidgetIterator(); index-- >= 0;)
					si = it.next();
				
				if (si)
					this._scrollbar.scrollToElement(si.$n());
			}
			// do only once
			this._shallScrollIntoView = false;
		}
	},
	_onRender: function () {
		this.$supers(Listbox, '_onRender', arguments);
		this._shallShowScrollbar = true;
	},
	onResponse: function (ctl, opts) {
		if (this.desktop) {
			if (this._shallStripe)
				this.stripe();
			if (this._shallFixEmpty) 
				_fixForEmpty(this);
			var rtags = opts ? opts.rtags : null;
			if (rtags && rtags.onDataLoading)
				this._shallShowScrollbar = true;
		}
		this.$supers(Listbox, 'onResponse', arguments);
	},
	_syncStripe: function () {
		this._shallStripe = true;
	},
	/**
	 * Stripes the class for each item.
	 * @return Listbox
	 */
	stripe: function () {
		var scOdd = this.getOddRowSclass();
		if (!scOdd) return;
		var odd = this._offset & 1,
			even = !odd,
			it = this.getBodyWidgetIterator();
		for (var j = 0, w; w = it.next(); j++) {
			if (w.isVisible() && w.isStripeable_()) {
				jq(w.$n())[even ? 'removeClass' : 'addClass'](scOdd);
				even = !even;
			}
		}
		this._shallStripe = false;
		return this;
	},
	rerender: function () {
		this.$supers(Listbox, 'rerender', arguments);
		this._syncStripe();
		return this;
	},

	//-- super --//
	getFocusCell: function (el) {
		var tbody = this.getCaveNode();
		if (jq.isAncestor(tbody, el)) {
			var tds = jq(el).parents('td'), td;
			for (var i = 0, j = tds.length; i < j; i++) {
				td = tds[i];
				if (td.parentNode.parentNode == tbody) {
					return td;
				}
			}
		}
	},
	getCaveNode: function () {
		return this.$n('rows') || this.$n('cave');
	},	
	insertChildHTML_: function (child, before, desktop) {
		if (before = before && (!child.$instanceof(zul.sel.Listitem) || before.$instanceof(zul.sel.Listitem)) ? before.getFirstNode_(): null)
			jq(before).before(child.redrawHTML_());
		else
			jq(this.getCaveNode()).append(child.redrawHTML_());
		child.bind(desktop);
	},
	insertBefore: function (child, sibling, ignoreDom) {
		if (this.$super('insertBefore', child, sibling,
		ignoreDom || (!this.z_rod && !child.$instanceof(zul.sel.Listitem)))) {
			this._fixOnAdd(child, ignoreDom);
			return true;
		}
	},
	appendChild: function (child, ignoreDom) {
		if (this.$super('appendChild', child,
		ignoreDom || (!this.z_rod && !child.$instanceof(zul.sel.Listitem)))) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom);
			return true;
		}
	},
	_fixOnAdd: function (child, ignoreDom, stripe, ignoreAll) {
		var noRerender;
		if (child.$instanceof(zul.sel.Listitem)) {
			if (_isListgroup(child))
				this._groupsInfo.push(child);
			if (!this.firstItem || !this.previousItem(child))
				this.firstItem = child;
			if (!this.lastItem || !this.nextItem(child))
				this.lastItem = child;
			++this._nrows;
			
			if (child.isSelected() && !this._selItems.$contains(child))
				this._selItems.push(child);
			noRerender = stripe = true;
		} else if (child.$instanceof(zul.sel.Listhead)) {
			this.listhead = child;
		} else if (child.$instanceof(zul.mesh.Paging)) {
			this.paging = child;
		} else if (child.$instanceof(zul.sel.Listfoot)) {
			this.listfoot = child;
		} else if (child.$instanceof(zul.mesh.Frozen)) {
			this.frozen = child;
		}
		
		this._syncEmpty();
		
		if (!ignoreAll) {
			if (!ignoreDom && !noRerender)
				return this.rerender();
			if (stripe)
				this._syncStripe();
			if (!ignoreDom)
				this._syncSize();
			if (this.desktop)
				_syncFrozen(this);
		}
	},
	removeChild: function (child, ignoreDom) {
		if (this.$super('removeChild', child, ignoreDom)) {
			this._fixOnRemove(child, ignoreDom);
			return true;
		}
	},
	_fixOnRemove: function (child, ignoreDom) {
		var stripe;
		if (child == this.listhead)
			this.listhead = null;
		else if (child == this.paging)
			this.paging = null;
		else if (child == this.frozen) {
			this.frozen = null;
			this.destroyBar_();
		} else if (child == this.listfoot)
			this.listfoot = null;
		else if (!child.$instanceof(zul.mesh.Auxhead)) {
			if (child == this.firstItem) {
				for (var p = this.firstChild, Listitem = zul.sel.Listitem;
				p && !p.$instanceof(Listitem); p = p.nextSibling)
					;
				this.firstItem = p;
			}
			if (child == this.lastItem) {
				for (var p = this.lastChild, Listitem = zul.sel.Listitem;
				p && !p.$instanceof(Listitem); p = p.previousSibling)
					;
				this.lastItem = p;
			}
			if (_isListgroup(child))
				this._groupsInfo.$remove(child);
			--this._nrows;
			
			if (child.isSelected())
				this._selItems.$remove(child);
			stripe = true;
		}
		this._syncEmpty();
		if (!ignoreDom) { //unlike _fixOnAdd, it ignores strip too (historical reason; might be able to be better)
			if (stripe) this._syncStripe();
			this._syncSize();
		}
	},
	/**
	 * A redraw method for the empty message , if you want to customize the message ,
	 * you could overwrite this.
	 * @param Array out A array that contains html structure ,
	 * 			it usually come from mold(redraw_). 
	 */
	redrawEmpty_: function (out) {
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="', 
				this.uuid, '-empty" style="display:none">',
				this._emptyMessage ,'</td></tr></tbody>');
	},
	// this function used for Listbox, Listhead
	_syncEmpty: function () {
		this._shallFixEmpty = true;
	},
	onChildReplaced_: function (oldc, newc) {
		this.$supers(Listbox, 'onChildReplaced_', arguments);

		if (oldc) this._fixOnRemove(oldc, true);
		if (newc) this._fixOnAdd(newc, true, false, true); //ignoreAll: no sync stripe...

		if ((oldc && oldc.$instanceof(zul.sel.Listitem))
				|| (newc && newc.$instanceof(zul.sel.Listitem)))
			this._syncStripe();
		this._syncSize();
		if (this.desktop)
			_syncFrozen(this);
	},
	/**
	 * Returns the head widget class
	 * @return zul.sel.Listhead
	 */
	getHeadWidgetClass: function () {
		return zul.sel.Listhead;
	},
	/**
	 * Returns the list item iterator.
	 * @return zul.sel.ItemIter
	 * @disable(zkgwt)
	 */
	itemIterator: _zkf = function (opts) {
		return new zul.sel.ItemIter(this, opts);
	},
	/**Returns the list item iterator.
	 * @return zul.sel.ItemIter
	 * @see #itemIterator
	 * @disable(zkgwt)
	 */
	getBodyWidgetIterator: _zkf,
	_updHeaderCM: function () {
		// B50-3322970: need to clear Listheader _check cache
		var lh;
		if (this._headercm && this._multiple 
			&& (lh = this.listhead) && (lh = lh.firstChild))
			lh._checked = this._isAllSelected();
		this.$supers(Listbox, '_updHeaderCM', arguments);
	}
});
/**
 * The listitem iterator.
 * @disable(zkgwt)
 */
zul.sel.ItemIter = zk.$extends(zk.Object, {
	/** Constructor
	 * @param Listbox listbox the widget that the iterator belongs to
	 */
	$init: function (box, opts) {
		this.box = box;
		this.opts = opts;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			var p = this.box.firstItem;
			if(this.opts && this.opts.skipHidden)
				for (; p && !p.isVisible(); p = p.nextSibling) {}
			this.p = p;
		}
	},
	 /**
     * Returns <tt>true</tt> if the iteration has more elements
     * @return boolean
     */
	hasNext: function () {
		this._init();
		return this.p;
	},
	/**
     * Returns the next element in the iteration.
     *
     * @return Listitem the next element in the iteration.
     */
	next: function () {
		this._init();
		var p = this.p,
			q = p ? p.parent.nextItem(p) : null;
		if (this.opts && this.opts.skipHidden)
			for (; q && !q.isVisible(); q = q.parent.nextItem(q)) {}
		if (p) 
			this.p = q;
		return p;
	}
});

})();
