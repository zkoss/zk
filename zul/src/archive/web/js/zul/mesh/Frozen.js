/* Frozen.js

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 */
zul.mesh.Frozen = zk.$extends(zul.Widget, {
	_start: 0,
	_scrollScale: 0,
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
					this.onSize();
					this.syncScroll();
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
		start: null
	},
	bind_: function () {
		this.$supers(zul.mesh.Frozen, 'bind_', arguments);
		var p = this.parent,
			body = p.$n('body'),
			bdfaker;
		if (p.$n('head')) {
			bdfaker = p.head.$n('bdfaker');
			//_scrollScale is used in Scrollbar.js
			if (bdfaker)
				this._scrollScale = bdfaker.childNodes.length - this._columns - 1;
		}
		if (body)
			jq(body).addClass('z-word-nowrap');
	},
	unbind_: function () {
		var p = this.parent,
			body = p.$n('body');
		if (body = p.$n('body'))
			jq(body).removeClass('z-word-nowrap');
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	beforeParentChanged_: function (p) {
		//bug B50-ZK-238
		if (this._lastScale) //if large then 0
			this._doScroll(0);
		
		this.$supers('beforeParentChanged_', arguments);
	},
	_doScroll: function (num) {
		num = Math.ceil(num);
		if (this._lastScale == num)
			return;
		this._lastScale = num;
		this._doScrollNow(num);
		this.smartUpdate('start', num);
		this._start = num;
	},
	_doScrollNow: function (num, force) {
		var mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows;

		if (mesh.head) {
			// set fixed size
			var hdrows = mesh.eheadrows.rows,
				hdcells = mesh.eheadrows.rows[hdrows.length - 1].cells,
				hdcol = mesh.ehdfaker.firstChild;
			
			for (var faker, i = 0; hdcol; hdcol = hdcol.nextSibling) {
				if (hdcol.style.width.indexOf('px') == -1) {
					var sw = hdcol.style.width = jq.px0(hdcells[i].offsetWidth),
						wgt = zk.Widget.$(hdcol);
					if (!wgt.$instanceof(zul.mesh.HeadWidget)) {
						if ((faker = wgt.$n('bdfaker')))
							faker.style.width = sw;
						if ((faker = wgt.$n('ftfaker')))
							faker.style.width = sw;
					}
				}
				i++;
			}
			
			for (var i = this._columns, len = hdcells.length; i < len; i++) {
				var n = hdcells[i],
					hdWgt = zk.Widget.$(n),
					isVisible = hdWgt && hdWgt.isVisible(),
					shallUpdate = false,
					cellWidth;
				if (cnt-- <= 0) { //show
					if (force || n.offsetWidth == 0) {
						cellWidth = hdWgt._origWd || jq.px(n.offsetWidth);
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (force || n.offsetWidth != 0) { //hide
					var faker = jq('#' + n.id + '-hdfaker')[0];
					hdWgt._origWd = hdWgt._origWd || faker.style.width;
					cellWidth = '0px';
					shallUpdate = true;
				}
				
				if (force || shallUpdate) {
					n.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-ftfaker')[0]))
						faker.style.width = cellWidth;

					// foot
					if (mesh.foot) {
						var ftrows = mesh.efootrows;
						if (ftrows)
							ftrows.rows[0].cells[i].style.width = cellWidth;
					}
				}
			}
		}
		mesh._restoreFocus();
		// Bug ZK-601, Bug ZK-1572
		if (zk.ie == 8 || zk.ie == 9)
			zk(mesh).redoCSS();
	}
});

})();