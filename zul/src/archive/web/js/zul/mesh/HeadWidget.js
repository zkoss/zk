/* HeadWidget.js

	Purpose:

	Description:

	History:
		Mon Dec 29 17:15:38     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//Bug 1926480: opera failed to add listheader dynamically (since hdfakerflex introduced)
	var _fixOnChildChanged = zk.opera ? function (head) {
		return (head = head.parent) && head.rerender(); //later
	} : zk.$void;

	function _syncFrozen(wgt) {
		var mesh = wgt.getMeshWidget(), frozen;
		if (mesh && (frozen = mesh.frozen)) {
			var hdfaker;
			if (mesh._nativebar) {
				frozen._syncFrozen();
			} else if ((hdfaker = mesh.ehdfaker)) {
				//_scrollScale is used in Scrollbar.js
				frozen._scrollScale = hdfaker.childNodes.length - frozen._columns - 1;

				// Bug ZK-2264
				frozen._shallSyncScale = false;
			}
		}
	}

var HeadWidget =
/**
 * A skeletal implementation for headers, the parent of
 * a group of {@link HeaderWidget}.
 *
 */
zul.mesh.HeadWidget = zk.$extends(zul.Widget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onColSize: this}, -1000);
	},

	$define: {
		/** Returns whether the width of the child column is sizable.
		 * @return boolean
		 */
		/** Sets whether the width of the child column is sizable.
		 * If true, an user can drag the border between two columns (e.g.,
		 * {@link HeaderWidget})
		 * to change the widths of adjacent columns.
		 * <p>Default: false.
		 * @param boolean sizable
		 */
		sizable: function () {
			this.rerender();
		},

		visible: function () {
			this.rerender();
			var mesh = this.getMeshWidget();
			setTimeout(function () {
				// ZK-2217: fix height if mesh.desktop exists
				if (mesh && mesh.desktop) {
					// ZK-2130: should fix ebody height
					// ZK-2217: should contain foot and paging
					var foot = mesh.$n('foot'),
						pgib = mesh.$n('pgib'),
						hgh = zk(mesh).contentHeight() - mesh.$n('head').offsetHeight
						- (foot ? foot.offsetHeight : 0) - (pgib ? pgib.offsetHeight : 0)
						- (mesh._nativebar && mesh.frozen ? mesh.frozen.$n().offsetHeight : 0);
					mesh.ebody.style.height = jq.px0(hgh);
				}
			}, 0);
		}
	},

	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		if (!this.$instanceof(zul.mesh.Auxhead))
			for (var fs = child.$class._faker, i = fs.length; i--;)
				jq(child.uuid + '-' + fs[i], zk).remove();
	},

	//bug #3014664
	setVflex: function (v) { //vflex ignored for Listhead/Columns/Treecols
		v = false;
		this.$super(HeadWidget, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Listhead/Columns/Treecols
		v = false;
		this.$super(HeadWidget, 'setHflex', v);
	},

	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 * @since 5.0.5
	 */
	getMeshWidget: function () {
		return this.parent;
	},

	onColSize: function (evt) {
		var owner = this.parent,
			widths = evt.widths,
			headWidth = 0;
		if (widths) {
			for (var i = 0; i < widths.length; i++)
				headWidth += parseInt(widths[i]);
			if (headWidth != 0) headWidth = jq.px0(headWidth);
		}
		owner._innerWidth = headWidth || owner.eheadtbl.width || owner.eheadtbl.style.width;
		owner.fire('onInnerWidth', owner._innerWidth);
		owner.fireOnRender(zk.gecko ? 200 : 60);
		owner.disableAutoSizing_(); // clear span and sizedByContent without rerender
	},

	bind_: function (desktop, skipper, after) {
		this.$supers(HeadWidget, 'bind_', arguments);
		var w = this;
		after.push(function () {
			_syncFrozen(w);
		});
	},
	unbind_: function () {
		jq(this.hdfaker).remove();
		jq(this.bdfaker).remove();
		jq(this.ftfaker).remove();
		this.$supers(HeadWidget, 'unbind_', arguments);
	},
	insertChildHTML_: function (child) {
		//ZK-2461: ie8 will take head-bar as one of columns, and equalize the width with other normal heads
		if (zk.ie9 && this.$n('bar')) {
			this.$n('bar').style.display = 'none';
		}
		this.$supers('insertChildHTML_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (this.desktop) {
			var _getWidth = zul.mesh.MeshWidget._getWidth,
				mesh = this.getMeshWidget();
			if (!_fixOnChildChanged(this) && mesh && mesh._fixHeaders()) {
				// refix-ZK-2466 : grid dynamic add childern should 'syncSize' at the end (instead of 'add one and trigger one's onSize')
				mesh._syncSize(true);
			}
			_syncFrozen(this);
			mesh._minWd = null;
			var frozen = mesh ? mesh.frozen : null;
			if (frozen)
				frozen.onSize();

			// B70-ZK-2128: Auxhead doesn't have to add faker.
			if (this.$instanceof(zul.mesh.Auxhead))
				return;

			// ZK-2098: recovery the header faker if not exists
			var head = this,
				recoverFakerbar = !frozen && !!mesh._nativebar,
				fakers = ['hdfaker', 'bdfaker', 'ftfaker'];
			// B30-1926480: ie8 does not support array.forEach
			for (var i = 0; i < fakers.length; i++) {
				var faker = fakers[i],
					$faker = jq(mesh['e' + faker]);
				// ZK-3643: Handle fakerbar/bar even if the faker exists
				if ($faker[0] != null) {
					//B70-ZK-2130: virtual bar doesn't have to add fakerbar
					//fkaker bar need recover if native bar has vscrollbar
					var $bar = jq(mesh).find('.' + head.$s('bar')), // head.$n('bar') still exists after remove
						bar = $bar[0],
						$hdfakerbar = jq(head.$n('hdfaker')).find('[id*=hdfaker-bar]'),
						hdfakerbar = $hdfakerbar[0],
						barstyle = '', hdfakerbarstyle = '';

					// ZK-2096, ZK-2124: should refresh this.$n('bar') if children change with databinding
					// B30-1926480: the bar should be removed
					if ((faker == 'hdfaker') && bar) {
						var s;
						if (s = bar.style) {
							// ZK-2114: should not store display
							// barstyle = s.display ? 'display:' + s.display + ';' : '';
							barstyle += s.width ? 'width:' + s.width + ';' : '';
						}
						$bar.remove();
						this._subnodes['bar'] = null;

						if (recoverFakerbar && hdfakerbar && (s = hdfakerbar.style)) {
							hdfakerbarstyle = s.display ? 'display:' + s.display + ';' : '';
							hdfakerbarstyle += s.width ? 'width:' + s.width + ';' : '';
						}
						$hdfakerbar.remove();
						this._subnodes['hdfaker-bar'] = null;
					}

					if (!$faker.find(child.$n(faker)).length) {
						var wd = _getWidth(child, child._hflexWidth ? child._hflexWidth + 'px' : child.getWidth());
						wd = wd ? 'width:' + wd + ';' : '';
						var html = '<col id="' + child.uuid + '-' + faker + '" style="' + wd + '"></col>',
							index = child.getChildIndex();
						// B30-1926480: child can be added after any brother node
						if (index > 0)
							jq($faker.find('col')[index - 1]).after(html);
						else
							$faker.append(html);
					}

					// resync var
					$bar = jq(mesh).find('.' + head.$s('bar'));
					bar = $bar[0];
					$hdfakerbar = jq(head.$n('hdfaker')).find('[id*=hdfaker-bar]');
					hdfakerbar = $hdfakerbar[0];

					if ((faker == 'hdfaker') && !bar && recoverFakerbar) {
						if (!hdfakerbar)
							jq(head.$n('hdfaker')).append('<col id="' + head.uuid + '-hdfaker-bar" style="' + hdfakerbarstyle + '" ></col>');
						jq(head).append('<th id="' + head.uuid + '-bar" class="' + head.$s('bar') + '" style="' + barstyle + '" ></th>');
					}
				}
			}
		}
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (this.desktop) {
			if (!_fixOnChildChanged(this) && !this.childReplacing_
				&& this.parent._fixHeaders())
				this.parent.onSize();
			this.parent._minWd = null;
			// Fix IE, FF for the issue B30-1926480-1.zul and B30-1926480.zul
			var mesh = this.getMeshWidget();
			mesh.rerender(1);
		}
	},
	beforeChildrenFlex_: function (hwgt) { //HeaderWidget
		if (hwgt && !hwgt._flexFixed) {
			//bug #3033010
			//clear associated hdfaker and bdfaker
			var wgt = this.parent,
				hdfaker = wgt.ehdfaker,
				bdfaker = wgt.ebdfaker,
				hdf = hdfaker ? hdfaker.firstChild : null,
				bdf = bdfaker ? bdfaker.firstChild : null;
			for (var h = this.firstChild; h; h = h.nextSibling) {
				// B70-ZK-2036: Do not adjust widget's width if it is not visible.
				if (h.isVisible() && h._nhflex > 0) { //not min or undefined
					if (hdf) hdf.style.width = '';
					if (bdf) bdf.style.width = '';
				}
				if (hdf) hdf = hdf.nextSibling;
				if (bdf) bdf = bdf.nextSibling;
			}
		}
		return true;
	},
	afterChildrenFlex_: function (hwgt) { //hflex in HeaderWidget
		var wgt = this.parent,
			ebody = wgt.ebody,
			ehead = wgt.ehead,
			efoot = wgt.efoot,
			currentLeft = wgt._currentLeft;
		if (wgt) {
			if (wgt._cssflex && wgt.isChildrenFlex()) {
				var minWd = wgt._calcMinWds(),
					wds = minWd.wds,
					isFlex = false,
					emptyHead = true;

				// reset display
				ehead.style.display = '';
				var tblWidth = 0,
					sizedByContent = wgt.isSizedByContent();
				for (var i = 0, wd = -1, hwgt = this.firstChild; hwgt; hwgt = hwgt.nextSibling, i++) {
					var nhwgt = hwgt.$n();
					if (hwgt._hflex == 'min' || (!hwgt._nhflex && sizedByContent))
						wd = wds[i];
					else if (hwgt._width)
						wd = parseInt(hwgt._width);
					else if (hwgt._nhflex > 0)
						isFlex = true;

					if (!hwgt.isVisible())
						nhwgt.style.display = 'none';
					if (wd >= 0) {
						nhwgt.style.flex = '0 0 ' + jq.px(wd);
						if (!isFlex)
							tblWidth += wd;
						wd = -1; //reset
					}
					//check empty head
					if (hwgt.getLabel() || hwgt.getImage() || hwgt.nChildren)
						emptyHead = false;
				}

				//set table width
				var hdtbl = wgt.eheadtbl,
					bdtbl = wgt.ebodytbl,
					fttbl = wgt.efoottbl,
					tblWidthPx = jq.px0(tblWidth);
				if (hdtbl) {
					hdtbl.style.width = isFlex ? '100%' : tblWidthPx;
					if (bdtbl)
						bdtbl.style.width = isFlex ? '100%' : tblWidthPx;
					if (fttbl)
						fttbl.style.width = isFlex ? '100%' : tblWidthPx;
				}

				//set body column/footer column width
				var bdfaker = wgt.ebdfaker,
					ftfaker = wgt.eftfaker,
					bdcol = bdfaker.firstChild,
					ftcol,
					width0 = wgt.$class.WIDTH0;
				if (ftfaker)
					ftcol = ftfaker.firstChild;
				wds = {};
				for (var hwgt = this.firstChild; hwgt && bdcol; hwgt = hwgt.nextSibling) {
					wds[hwgt.uuid] = hwgt.$n().getBoundingClientRect().width;
					bdcol = bdcol.nextSibling;
				}
				bdcol = bdfaker.firstChild;
				for (var hwgt = this.firstChild, wd; hwgt && bdcol; hwgt = hwgt.nextSibling) {
					var wd = wds[hwgt.uuid],
						wdpx = jq.px0(wd);
					if (hwgt.isVisible() && wdpx !== undefined) {
						bdcol.style.width = wdpx;
						if (ftcol)
							ftcol.style.width = wdpx;
					} else {
						bdcol.style.width = width0;
						if (ftcol)
							ftcol.style.width = width0;
					}
					bdcol = bdcol.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
				}
				ehead.style.display = emptyHead ? 'none' : '';

				// css flex doesn't need 'bar' style, because 'colgroup' is disabled.
				this.$n('bar').style.display = 'none';
			} else {
				wgt._adjFlexWd();
				wgt._adjSpanWd(); //if there is span and shall span the column width for extra space

				// ZK-2772 Misaligned Grid columns
				if (wgt.frozen && !wgt._isAllWidths()) {
					wgt._calcSize(); // yes, we need to do it again.
				}
				// ZK-2551: need to restore scroll pos after flexs are fixed
				if (zk(ebody).hasHScroll() && currentLeft != ebody.scrollLeft) {
					ebody.scrollLeft = currentLeft;
					if (ehead)
						ehead.scrollLeft = currentLeft;
					if (efoot)
						efoot.scrollLeft = currentLeft;
				}
			}
		}
	},
	deferRedrawHTML_: function (out) {
		out.push('<tr', this.domAttrs_({domClass: 1}), ' class="z-renderdefer"></tr>');
	},
	getFlexDirection_: function () {
		return 'row';
	},
	afterClearFlex_: function () {
		var mesh = this.parent;
		if (mesh._cssflex)
			mesh.ehdfaker.style.display = '';
	}
}, { //static
	redraw: function (out) {
		out.push('<tr', this.domAttrs_(), ' style="text-align: left;">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		var mesh = this.getMeshWidget();
		if (mesh && mesh._nativebar)
			out.push('<th id="', this.uuid, '-bar" class="', this.$s('bar'), '" ></th>');
		out.push('</tr>');
	}
});

})();
