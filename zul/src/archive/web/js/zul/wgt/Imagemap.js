/* Imagemap.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:54:00     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An image map.
 */
zul.wgt.Imagemap = zk.$extends(zul.wgt.Image, {
	$define: {
		width: function (v) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.width = v;
		},
		height: function (v) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.height = v;
		}
	},
	bind_: function () {
		this.$supers(zul.wgt.Imagemap, 'bind_', arguments);

		if (!jq('#zk_hfr_')[0])
			jq.newFrame('zk_hfr_', null,
				zk.webkit ? 'position:absolute;top:-1000px;left:-1000px;width:0;height:0;display:inline'
					: null/*invisible*/);
			//creates a hidden frame. However, in safari, we cannot use invisible frame
			//otherwise, safari will open a new window
	},

	//super//
	getImageNode: function () {
		return this.$n('real');
	},
	getCaveNode: function () {
		return this.$n('map');
	},
	doClick_: function (evt) {
		//does nothing (so zk.Widget won't fire onClick)
		//don't call evt.stop => it causes browsers not to handle A
	},
	onChildAdded_: function () {
		this.$supers('onChildAdded_', arguments);
		if (this.desktop && this.firstChild == this.lastChild) //first child
			this._fixchd(true);
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (this.desktop && !this.firstChild) //remove last
			this._fixchd(false);
	},
	_fixchd: function (bArea) {
		var mapid = this.uuid + '-map',
			img = this.getImageNode();
		img.useMap = bArea ? '#' + mapid : '';
		img.isMap = !bArea;
	},
	contentAttrs_: function () {
		var attr = this.$supers('contentAttrs_', arguments),
			w = this._width,
			h = this._height;
		if (w || h) { // B50-ZK-478
			attr += ' style="';
			if (w)
				attr += 'width:' + w + ';'
			if (h)
				attr += 'height:' + h + ';'
			attr += '"';
		}
		return attr +(this.firstChild ? ' usemap="#' + this.uuid + '-map"':
			' ismap="ismap"');
	},

	//@Override
	fromPageCoord: function (x, y) {
		//2997402: Imagemap rightclick/doubleclick wrong coordinates
		var ofs = zk(this.getImageNode()).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	},

	_doneURI: function () {
		var Imagemap = zul.wgt.Imagemap,
			url = Imagemap._doneURI;
		return url ? url:
			Imagemap._doneURI = zk.IMAGEMAP_DONE_URI ? zk.IMAGEMAP_DONE_URI:
				zk.ajaxURI('/web/zul/html/imagemap-done.html', {desktop:this.desktop, au:true});
	}
},{
	/** Called by imagemap-done.html. */
	onclick: function (href) {
		if (zul.wgt.Imagemap._toofast()) return;

		var j = href.indexOf('?');
		if (j < 0) return;

		var k = href.indexOf('?', ++j);
		if (k < 0 ) return;

		var id = href.substring(j, k),
			wgt = zk.Widget.$(id);
		if (!wgt) return; //component might be removed

		j = href.indexOf(',', ++k);
		if (j < 0) return;

		wgt.fire('onClick', {
			x: zk.parseInt(href.substring(k, j)),
			y: zk.parseInt(href.substring(j + 1))
		}, {ctl:true});
	},
	_toofast: function () {
		if (zk.gecko) { //bug 1510374
			var Imagemap = zul.wgt.Imagemap,
				now = jq.now();
			if (Imagemap._stamp && now - Imagemap._stamp < 800)
				return true;
			Imagemap._stamp = now;
		}
		return false;
	}
});
