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
	sid: 0,
	uploaders: {},
	$init: function(wgt, parent, clsnm) {
		this.maxsize = this._parseMaxsize(clsnm);
		this.isNative = clsnm.indexOf('native') != -1;
		this._clsnm = (this.maxsize || this.isNative) ? clsnm.split(',')[0] : clsnm;
		this._wgt = wgt;
		this._parent = parent;
		
		this.initContent();
	},
	sync: function () {
		var wgt = this._wgt,
			ref = wgt.$n(),
			parent = this._parent,
			outer = parent ? parent.lastChild : ref.nextSibling,
			inp = outer.firstChild.firstChild,
			refof = zk(ref).cmOffset(),
			outerof = jq(outer).css({top: '0', left: '0'}).zk.cmOffset(),
			diff = inp.offsetWidth - ref.offsetWidth,
			st = outer.style;
		st.top = refof[1] - outerof[1] + "px"; //not sure why, but cannot use offsetTop
		st.left = refof[0] - outerof[0] - diff + "px";
		inp.style.height = ref.offsetHeight + 'px';
		inp.style.clip = 'rect(auto,auto,auto,' + diff + 'px)';
	},
	initContent: function () {
		var wgt = this._wgt,
			parent = this._parent,
			ref = wgt.$n(), dt = wgt.desktop,
			html = '<span class="z-upload"><form enctype="multipart/form-data" method="POST">'
				 + '<input name="file" type="file" hidefocus="true" style="height:'
				 + ref.offsetHeight + 'px"/></form></span>';
		
		if (parent) 
			jq(parent).append(html);
		else 
			jq(wgt).after(html);
			
		this.sync();
		
		var outer = this._outer = parent ? parent.lastChild : ref.nextSibling,
			inp = outer.firstChild.firstChild;
			
		inp.z$proxy = ref;
		inp._ctrl = this;
		
		jq(inp).change(zul.Upload._onchange);
	},
	destroy: function () {
		jq(this._outer||[]).remove();
		this._wgt = this._parent = null;
		for (var v in this.uploaders) {
			var uplder = this.uploaders[v];
			if (uplder) {
				delete this.uploaders[v];
				uplder.destroy();
			}
		}
	},
	getKey: function (sid) {
		return (this._wgt ? this._wgt.uuid : '' )+ '_uplder_' + sid; 
	},
	cancel: function (sid) { //cancel upload
		this._cancel(sid);
	},
	finish: function (sid) {
		this._cancel(sid, true);
	},
	_cancel: function (sid, finish) {
		var key = this.getKey(sid),
			uplder = this.uploaders[key];
		if (uplder)
			uplder.destroy(finish);
		delete this.uploaders[key];
	},
	_parseMaxsize: function (val) {
		return val.indexOf("maxsize=") >= 0 ? val.match(zul.Upload.MaxSizeRe)[1] : '';
	},
	_start: function (form, val) { //start upload		
		var key = this.getKey(this.sid),
			uplder = new zul.Uploader(this, key, form, val);
			
		zul.Upload.start(uplder);
		this.uploaders[key] = uplder;
		
		this.sid++;
		this.initContent();
	}
},{
	MaxSizeRe: new RegExp(/maxsize=([^,]*)/),
	_onchange: function (evt) {
		var n = this,
			upload = n._ctrl,
			wgt = upload._wgt,
			dt = wgt.desktop,
			action = zk.ajaxURI('/upload', {desktop:dt,au:true}) + '?uuid=' + wgt.uuid + '&dtid=' + dt.uuid + '&sid=' + upload.sid
				+ (upload.maxsize !== '' ? '&maxsize=' + upload.maxsize : '')
				+ (upload.isNative ? '&native=true' : ''),
			form = n.form;
		form.action = action;
		jq(form.parentNode).remove();
		n._ctrl._start(form, n.value);		
	},
	error: function (msg, uuid, sid) {
		var wgt = zk.Widget.$(uuid);
		if (wgt) {
			jq.alert(msg, {desktop: wgt.desktop, icon: 'ERROR'});
			zul.Upload.close(uuid, sid);
		}
	},
	close: function (uuid, sid) {
		var wgt = zk.Widget.$(uuid);
		if (!wgt || !wgt._uplder) return;
		wgt._uplder.cancel(sid);
	},
	sendResult: function (uuid, contentId, sid) {
		var wgt = zk.Widget.$(uuid);
		if (!wgt || !wgt._uplder) return;
		wgt._uplder.finish(sid);
		zAu.send(new zk.Event(wgt.desktop, "updateResult", {
			contentId: contentId,
			wid: wgt.uuid,
			sid: sid
		}));
	},
	isFinish: function (wgt) {
		for (var key = (typeof wgt == 'string' ? wgt : wgt.uuid) + '_uplder_',
				f = zul.Upload.files, i = f.length; i--;)
			if (f[0].id.startsWith(key))
				return false;
		return true;
	},
	start: function (uplder) {
		var files = zul.Upload.files;
		if (uplder)
			files.push(uplder);
		if (files[0] && !files[0].isStart) {
			files[0].isStart = true;
			files[0].start();
		}
	},
	destroy: function (uplder) {
		for (var files = zul.Upload.files, i = files.length; i--;) 
			if (files[i].id == uplder.id) {
				files.splice(i, 1);
				break;
			}
		zul.Upload.start();
	},
	files: []
});
//default uploader
zul.Uploader = zk.$extends(zk.Object, {
	$init: function (upload, id, form, flnm) {
		this.id = id;
		this.flnm = flnm;
		this._upload = upload;
		this._form = form;
		this._parent = form.parentNode;
		this._sid = upload.sid;
		this._wgt = upload._wgt;
		
		var viewer, self = this;
		if (upload._clsnm.startsWith('true')) viewer = new zul.UploadViewer(this, flnm);
		else
			zk.$import(upload._clsnm, function (cls) {
				viewer = new cls(self, flnm);
			});
		this.viewer = viewer;
	},
	getWidget: function () {
		return this._wgt;
	},
	destroy: function (finish) {
		this.end(finish);
		if (this._form) {
			jq(this._form.parentNode).remove();
			jq('#' + this.id + '_ifm').remove();
		}
		this._form = this._upload = this._wgt = null;
	},
	start: function () {
		var wgt = this._wgt,
			frameId = this.id + '_ifm';

		document.body.appendChild(this._parent);
		if (!jq('#' + frameId).length) 
			jq.newFrame(frameId);
		this._form.target = frameId;
		this._form.submit();
		this._form.style.display = "none";
		
		var uri = zk.ajaxURI('/upload', {desktop: wgt.desktop, au: true}),
			dtid = wgt.desktop.id,
			self = this,
			data = 'cmd=uploadInfo&dtid=' + dtid + '&wid=' + wgt.uuid + '&sid=' + this._sid;
		
		if (zul.Uploader._tmupload)
			clearInterval(zul.Uploader._tmupload);
		
		function t() {
			jq.ajax({
				type: 'POST',
				url: uri,
				data: data,
				success: function(data) {
					var d = data.split(',');
					if (data.startsWith('error:')) {
						zul.Uploader.clearInterval(self.id);
							var wgt = self.getWidget();
							if (wgt) {
								self.cancel();
								zul.Upload.error(data.substring(6, data.length), wgt.uuid, self._sid);
							}
					} else if (!self.update(zk.parseInt(d[0]), zk.parseInt(d[1])))
						zul.Uploader.clearInterval(self.id);
				},
				complete: function(req, status) {
					var v;
					if ((v = req.getResponseHeader("ZK-Error")) == "404"/*SC_NOT_FOUND: server restart*/ ||
							v == "410" || status == 'error' || status == 410/*SC_GONE: session timeout*/) {
						zul.Uploader.clearInterval(self.id);
						var wgt = self.getWidget();
						if (wgt) {
							self.cancel();
							zul.Upload.error(mesg.FAILED_TO_RESPONSE, wgt.uuid, self._sid);
						}
						return;
					}
				}
			});
		}
		t.id = this.id;
		
		zul.Uploader.clearInterval = function (id) {
			if (t.id == id) {
				clearInterval(zul.Uploader._tmupload);
				zul.Uploader._tmupload = undefined;
			}
		};
		zul.Uploader._tmupload = setInterval(t, 1000);
	},
	cancel: function () {
		zul.Uploader.clearInterval(this.id);
		if (this._upload)
			this._upload.cancel(this._sid);
	},
	update: function (sent, total) {
		var wgt = this.getWidget();
		if (!wgt || total <= 0) this.end();
		else if (zul.Uploader._tmupload) {
			if (sent >= 0 && sent <= 100)
				this.viewer.update(sent, total);
			return sent >= 0 && sent < 100;
		}
		return false;
	},
	end: function (finish) {
		this.viewer.destroy(finish);
		zul.Upload.destroy(this);
	}
});
// default UploadViewer
zul.UploadViewer = zk.$extends(zk.Object, {
	$init: (function() {
		function addUM(uplder, flnm) {
			var flman = zul.UploadViewer.flman;
			if (!flman || !flman.desktop) {
				if (flman) flman.detach();
				zul.UploadViewer.flman = flman = new zul.UploadManager();
				uplder.getWidget().getPage().appendChild(flman);
			}
			flman.removeFile(uplder);
			flman.addFile(uplder);
		}
		function initUM(uplder, flnm) {
			if (zul.UploadManager)
				addUM(uplder, flnm);
			else
				zk.load('zul.wgt,zul.box', function() {
					zul.UploadManager = zk.$extends(zul.wgt.Popup, {
						_files: {},
						$init: function () {
							this.$supers('$init', arguments);
							this.setSclass('z-fileupload-manager');
						},
						onFloatUp: function(wgt){
							if (!this.isVisible()) 
								return;
							this.setTopmost();
						},
						getFileItem: function(id) {
							return this._files[id] || zk.Widget.$(id);
						},
						addFile: function(uplder) {
							var id = uplder.id,
								flnm = uplder.flnm,
								prog = this.getFileItem(id);
							if (!prog) {
								prog = new zul.wgt.Div({
									uuid: id,
									children: [new zul.wgt.Label({
										value: flnm + ':'
									}), new zul.box.Box({
										mold: 'horizontal',
										children: [new zul.wgt.Progressmeter({
											id: id,
											sclass: 'z-fileupload-progress'
										})
										, new zul.wgt.Div({
											sclass: 'z-fileupload-rm',
											listeners: {
												onClick: function () {
													var uuid = id.substring(0, id.indexOf('_uplder_'));
													zul.Uploader.clearInterval(id);
													var wgt = zk.Widget.$(uuid);
													if (wgt) wgt._uplder.cancel(id.substring(id.lastIndexOf('_')+1, id.length));
												}
											}
										})]
									}), new zul.wgt.Label({id: id + '_total'}), new zul.wgt.Separator()]
								});
								this.appendChild(prog);
								this._files[id] = prog;
							}
							return prog;
						},
						updateFile: function(uplder, val, total) {
							var id = uplder.id,
								prog = this.getFileItem(id);
							if (!prog) return;
							prog.$f(id).setValue(val);
							prog.$f(id + '_total').setValue(total);
						},
						removeFile: function(uplder) {
							var id = uplder.id,
								prog = this.getFileItem(id);
							if (prog) 
								prog.detach();
							delete this._files[id];
							var close = true;
							for (var p in this._files) 
								if (!(close = false)) 
									break;
							
							if (close) 
								this.close();
						},
						open: function(wgt, position) {
							this.$super('open', wgt, null, position || 'after_start', {
								sendOnOpen: false,
								disableMask: true
							});
						}
					});
					addUM(uplder, flnm);
				});
		}
		return function (uplder,  flnm) {
			this._uplder = uplder;
			initUM(uplder, flnm);
		}
	})(),
	update: function (sent, total) {
		var flman = zul.UploadViewer.flman;
		if (flman) {
			if (!flman.isOpen())
					flman.open(this._uplder.getWidget());
			flman.updateFile(this._uplder, sent, mesg.FILE_SIZE+Math.round(total/1024)+mesg.KBYTES);
		}
	},
	destroy: function () {
		var flman = zul.UploadViewer.flman;
		if (flman)
			flman.removeFile(this._uplder);
	}
});