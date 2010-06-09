/* Frozen.js

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 */
zul.mesh.Frozen = zk.$extends(zul.Widget, {
	_start: 0,
	
	$define: {
    	/**
    	 * Returns the number of columns to freeze.
    	 * <p>Default: 0
    	 * @return int
    	 */
    	/**
    	 * Sets the number of columns to freeze.(from left to right)
    	 * @param int columns positive only
    	 */
		columns: [function(v) {
			return v < 0 ? 0 : v;
		}, function(v) {
			if (this._columns) {
				if (this.desktop) {
					this.onShow();
					this.syncScorll();
				}
			} else this.rerender();
		}],
		/**
		 * Returns the start position of the scrollbar.
		 * <p>Default: 0
		 * @return int
		 */
		/**
		 * Sets the start position of the scrollbar.
		 * <p> Default: 0
		 * @param int start the column number
		 */
		start: function () {
			this.syncScorll();
		}
	},
	/**
	 * Synchronizes the scrollbar according to {@link #getStart}.
	 */
	syncScorll: function () {
		var scroll = this.$n('scrollX');
		if (scroll)
			scroll.scrollLeft = this._start * 100;
	},
	getZclass: function () {
		return this._zclass == null ? "z-frozen" : this._zclass;
	},
	onShow: _zkf = function () {
		if (!this._columns) return;
		var bdfaker = this.parent.ebdfaker;
		if (!bdfaker) {
			bdfaker = this.parent.ebodyrows[0];
			if (bdfaker)
				bdfaker = bdfaker.$n();
		}
		if (bdfaker) {
			var leftWidth = 0;
			for (var i = this._columns, n = bdfaker.firstChild; n && i--; n = n.nextSibling)
				leftWidth += n.offsetWidth;

			this.$n('cave').style.width = jq.px0(leftWidth);
			var scroll = this.$n('scrollX'),
				width = this.parent.$n('body').offsetWidth;
				width -= leftWidth;
			scroll.style.width = jq.px0(width);
			var scrollScale = bdfaker.childNodes.length - this._columns - 1;
			scroll.firstChild.style.width = jq.px0(width + 100 * scrollScale);
			this.syncScorll();
		}
	},
	onSize: _zkf,
	_onScroll: function (evt) {
		if (!evt.data) return;
		this.setStart(this._start + 2);
		this.parent.ebody.scrollLeft = 0;
		evt.stop();
	},
	bind_: function () {
		this.$supers(zul.mesh.Frozen, 'bind_', arguments);
		zWatch.listen({onShow: this, onSize: this});
		var scroll = this.$n('scrollX'),
			gbody = this.parent.$n('body');

		this.$n().style.height = this.$n('cave').style.height = scroll.style.height
			 = scroll.firstChild.style.height = jq.px0(jq.scrollbarWidth());

		this.parent.listen({onScroll: this.proxy(this._onScroll)}, -1000);
		this.domListen_(scroll, 'onScroll');

		if (gbody)
			gbody.style.overflowX = 'hidden';
	},
	unbind_: function () {
		zWatch.unlisten({onShow: this, onSize: this});
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	beforeParentChanged_: function (np) {
		if (!np) {
			var gbody = this.parent.$n('body');
			if (gbody)
				gbody.style.overflowX = '';
		}
		this.$supers("beforeParentChanged_", arguments);
	},
	_doScroll: function (evt) {
		var scroll = this.$n('scrollX'),
			num = Math.ceil(scroll.scrollLeft / 100);
		if (this._lastScale == num)
			return;
		this._lastScale = num;
		this._doScrollNow(num);
		this.smartUpdate('start', num);
		this._start = num;
	},
	_doScrollNow: function (num) {
		var width = this.$n('cave').offsetWidth,
			mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows;

		if (!mesh.head && (!rows || rows.length))
			return;

		if (mesh.head) {

			// set fixed size
			for (var faker, w = mesh.head.firstChild.$n('hdfaker'); w;
					w = w.nextSibling) {
				if (w.style.width.indexOf('px') == -1) {
					var sw = w.style.width = jq.px0(w.offsetWidth),
						wgt = zk.Widget.$(w);
					if ((faker = wgt.$n('bdfaker')))
						faker.style.width = sw;
					if ((faker = wgt.$n('ftfaker')))
						faker.style.width = sw;
				}
			}
			for (var invisible, faker, index = this._columns,
					tail = mesh.head.nChildren - index,
					w = mesh.head.getChildAt(index).$n();
					w; w = w.nextSibling, index++, tail--) {
				invisible = cnt-- <= 0 ? '' : 'none';
				if (w.style.display != invisible) {
					w.style.display = invisible;
					if ((faker = jq('#' + w.id + '-hdfaker')[0]))
						faker.style.display = invisible;
					if ((faker = jq('#' + w.id + '-bdfaker')[0]))
						faker.style.display = invisible;
					if ((faker = jq('#' + w.id + '-fdfaker')[0]))
						faker.style.display = invisible;


					for (var r = rows[0], len; r && (len = r.childNodes.length) > tail;
							r = r.nextSibling)
						r.cells[len - tail].style.display = invisible;
				}
			}

			for (var w = mesh.head.getChildAt(this._columns + num).$n('hdfaker');
					w; w = w.nextSibling)
				width += zk.parseInt(w.style.width);

		} else {
			
			// set fixed size
			for (var index = this._columns, c = rows[0].firstChild; c; c = c.nextSibling) {
				if (c.style.width.indexOf('px') == -1)
					c.style.width = jq.px0(zk(c).revisedWidth(c.offsetWidth));
			}

			for (var first = rows[0], invisible, index = this._columns,
					len = first.childNodes.length; index < len; index++) {
				invisible = cnt-- <= 0 ? '' : 'none';
				for (var r = first; r; r = r.nextSibling)
					r.cells[index].style.display = invisible;
			}

			for (var c = rows[0].cells[this._columns + num]; c; c = c.nextSibling)
				width += zk.parseInt(c.style.width);
		}

		width = jq.px0(width);
		if (mesh.eheadtbl)
			mesh.eheadtbl.style.width = width;
		if (mesh.ebodytbl)
			mesh.ebodytbl.style.width = width;
		if (mesh.efoottbl)
			mesh.efoottbl.style.width = width;
	}
});