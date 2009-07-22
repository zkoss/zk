/* Upload.js

	Purpose:
		
	Description:
		
	History:
		Fri Jul 17 16:44:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.Upload = zk.$extends(zk.Object, {
	$init: function (wgt, parent, clsnm) {
		if (clsnm == 'true') this._uplder = new zul.Uploader(this);
		else
			zk.$import(clsnm, function (cls) {
				this._uplder = new cls(this);
			});

		if (!jq('#zk_upld_').length) jq.newFrame('zk_upld_');

		var ref = wgt.$n(), dt = wgt.desktop,
			html = '<span class="z-upload"><form enctype="multipart/form-data" method="POST" action="'
			+ (zk.UPLOAD_URI?zk.UPLOAD_URI:zk.ajaxURI('/upload', {desktop:dt,au:true}))
			+ '" target="zk_upld_"><input type="file" hidefocus="true" style="height:'
			+ ref.offsetHeight + 'px"/><input type="hidden" name="dtid" value="'
			+ dt.id + '"/><input type="hidden" name="uuid" value="'
			+ wgt.uuid +'"></form></span>';

		if (parent) jq(parent).append(html);
		else jq(wgt).after(html);

		var outer = this._outer = parent ? parent.lastChild: ref.nextSibling,
			inp = outer.firstChild.firstChild,
			refof = zk(ref).cmOffset(), outerof = zk(outer).cmOffset(),
			diff = inp.offsetWidth - ref.offsetWidth;
			st = outer.style;
		st.top = refof[1] - outerof[1] + "px"; //not sure why, but cannot use offsetTop
		st.left = refof[0] - outerof[0] - diff  + "px";
		inp.style.clip = 'rect(auto,auto,auto,' + diff + 'px)';
		inp.z$proxy = ref;
		inp._ctrl = this;

		jq(inp).change(zul.Upload._onchange);
	},
	destroy: function () {
		jq(this._outer||[]).remove();

		if (v = this._uplder) {
			this._uplder = null;
			v.destroy();
		}
	},
	cancel: function () { //cancel upload
	},
	_start: function (fn) { //start upload
	}
},{
	_onchange: function (evt) {
		var n = this;
		n._ctrl._start(n.value);
		setTimeout(function () {
			n.form.submit();
		}, 100);
	}
});
//default uploader
zul.Uploader = zk.$extends(zk.Object, {
	$init: function (upload) {
		this._upload = upload;
	},
	destroy: function () {
	},
	start: function (fn) {
	},
	update: function (sent, total) {
	},
	end: function () {
	}
});
