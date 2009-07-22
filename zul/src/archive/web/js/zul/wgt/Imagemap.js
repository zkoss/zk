/* Imagemap.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:54:00     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Imagemap = zk.$extends(zul.wgt.Image, {
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!jq('#zk_hfr_')[0])
			jq.newFrame('zk_hfr_', null,
				zk.safari ? 'width:0;height:0;display:inline': null/*invisible*/);
			//creates a hidden frame. However, in safari, we cannot use invisible frame
			//otherwise, safari will open a new window
	},

	//super//
	getImageNode: function () {
		return this.$n("real");
	},
	getCaveNode: function () {
		return this.$n("map");
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
		var mapid = this.uuid + '-map';
		img = this.getImageNode();
		img.useMap = bArea ? '#' + mapid : '';
		img.isMap = !bArea;
	},
	contentAttrs_: function () {
		var attr = this.$supers('contentAttrs_', arguments);
		return attr +(this.firstChild ? ' usemap="#' + this.uuid + '-map"':
			' ismap="ismap"');
	},

	_doneURI: function () {
		var Imagemap = zul.wgt.Imagemap,
			url = Imagemap._doneURI;
		return url ? url:
			Imagemap._doneURI = zk.IMAGEMAP_DONE_URI ? zk.IMAGEMAP_DONE_URI:
				zk.ajaxURI('/web/zul/html/imagemap-done.html', this.desktop, {au:true});
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
				now = zUtl.now();
			if (Imagemap._stamp && now - Imagemap._stamp < 800)
				return true;
			Imagemap._stamp = now;
		}
		return false;
	}
});
