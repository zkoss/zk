/* Upload.js

	Purpose:
		
	Description:
		
	History:
		Fri Jul 17 16:44:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _cancel(o, sid, finish) {
		var key = o.getKey(sid),
			uplder = o.uploaders[key];
		if (uplder)
			uplder.destroy(finish);
		delete o.uploaders[key];
	}
	function _parseMaxSize(val) {
		return val.indexOf("maxsize=") >= 0 ? val.match(new RegExp(/maxsize=([^,]*)/))[1] : '';
	}
	function _start(o, form, val) { //start upload		
		var key = o.getKey(o.sid),
			uplder = new zul.Uploader(o, key, form, val);
			
		zul.Upload.start(uplder);
		o.uploaders[key] = uplder;
		
		o.sid++;
		o.initContent();
	}

	function _onchange(evt) {
		var n = this,
			upload = n._ctrl,
			wgt = upload._wgt,
			dt = wgt.desktop,
			action = zk.ajaxURI('/upload', {desktop:dt,au:true}) + '?uuid=' + wgt.uuid + '&dtid=' + dt.uuid + '&sid=' + upload.sid
				+ (upload.maxsize !== '' ? '&maxsize=' + upload.maxsize : '')
				+ (upload.isNative ? '&native=true' : ''),
			form = n.form;
		form.action = action;
		
		// we don't use jq().remove() in this case, because we have to use its reference.
		var p = form.parentNode;
		p.parentNode.removeChild(p);
		_start(n._ctrl, form, n.value);		
	}

/** Helper class for implementing the fileupload.
 */
zul.Upload = zk.$extends(zk.Object, {
	sid: 0,
	uploaders: {},
	/** Constructor
	 * @param zk.Widget wgt the widget belongs to the file upload 
	 * @param DOMElement parent the element representing where the upload element
	 * 		is appended
	 * @param String clsnm the CSS class name of the fileupload
	 */
	$init: function(wgt, parent, clsnm) {
		this.maxsize = _parseMaxSize(clsnm);
		this.isNative = clsnm.indexOf('native') != -1;
		this._clsnm = (this.maxsize || this.isNative) ? clsnm.split(',')[0] : clsnm;
		this._wgt = wgt;
		this._parent = parent;
		
		this.initContent();
	},
	/**
	 * Synchronizes the visual states of the element with fileupload
	 */
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
		
		jq(inp).change(_onchange);
	},
	/** 
	 * Destroys the fileupload. You cannot use this object any more. 
	 */
	destroy: function () {
		jq(this._outer).remove();
		this._wgt = this._parent = null;
		for (var v in this.uploaders) {
			var uplder = this.uploaders[v];
			if (uplder) {
				delete this.uploaders[v];
				uplder.destroy();
			}
		}
	},
	/**
	 * Returns the uuid of the uploader with its sequential number
	 * @return String the key of the uploader
	 */
	getKey: function (sid) {
		return (this._wgt ? this._wgt.uuid : '' )+ '_uplder_' + sid; 
	},
	/**
	 * Cancels the fileupload if the fileupload is progressing.
	 * @param int sid the sequential number of the uploader
	 */
	cancel: function (sid) { //cancel upload
		_cancel(this, sid);
	},
	/**
	 * Finishes the fileupload if the fileupload is done.
	 * @param int sid the sequential number of the uploader
	 */
	finish: function (sid) {
		_cancel(this, sid, true);
	}
},{
	/**
	 * Shows the error message of the fileupload
	 * @param String msg the error message
	 * @param String uuid the ID of the widget
	 * @param int sid the sequential number of the uploader
	 */
	error: function (msg, uuid, sid) {
		var wgt = zk.Widget.$(uuid);
		if (wgt) {
			jq.alert(msg, {desktop: wgt.desktop, icon: 'ERROR'});
			zul.Upload.close(uuid, sid);
		}
	},
	/**
	 * Closes the fileupload
	 * @param String uuid the ID of the widget
	 * @param int sid the sequential number of the uploader
	 */
	close: function (uuid, sid) {
		var wgt = zk.Widget.$(uuid);
		if (!wgt || !wgt._uplder) return;
		wgt._uplder.cancel(sid);
	},
	/**
	 * Sends the upload result to server.
	 * @param String uuid the ID of the widget
	 * @param String contentId the ID of the content being uploaded
	 * @param int sid the sequential number of the uploader
	 */
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
	/**
	 * Returns the fileupload of the widget whether is finish or not.
	 * @param zk.Widget wgt the widget
	 * @return boolean
	 */
	isFinish: function (wgt) {
		for (var key = (typeof wgt == 'string' ? wgt : wgt.uuid) + '_uplder_',
				f = zul.Upload.files, i = f.length; i--;)
			if (f[0].id.startsWith(key))
				return false;
		return true;
	},
	/**
	 * Starts the uploader to upload a file.
	 * @param Object uplder the uploader
	 */
	start: function (uplder) {
		var files = zul.Upload.files;
		if (uplder)
			files.push(uplder);
		if (files[0] && !files[0].isStart) {
			files[0].isStart = true;
			files[0].start();
		}
	},
	/**
	 * Destroys the uploader to upload
	 * @param Object uplder
	 */
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
/**
 * Default file uploader for the upload widget.
 * <p> One upload widget can have multi-instance of uploader to upload multiple
 * files at the same time.
 */
zul.Uploader = zk.$extends(zk.Object, {
	/** Constructor
	 * @param zul.Upload upload the upload object belong to the file uploader
	 * @param String id the ID of the uploader 
	 * @param DOMElement form the element representing where the uploader element
	 * 		is appended
	 * @param String flnm the name of the file to be uploaded
	 */
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
	/**
	 * Returns the widget which the uploader belongs to.
	 * @return zk.Widget
	 */
	getWidget: function () {
		return this._wgt;
	},
	/**
	 * Destroys the uploader to upload.
	 * @param boolean finish if true, the upload is finish.
	 */
	destroy: function (finish) {
		this.end(finish);
		if (this._form) {
			jq(this._form.parentNode).remove();
			jq('#' + this.id + '_ifm').remove();
		}
		this._form = this._upload = this._wgt = null;
	},
	/**
	 * Starts the uploader to upload
	 */
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
							zul.Upload.error(msgzk.FAILED_TO_RESPONSE, wgt.uuid, self._sid);
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
	/**
	 * Cancels the uploader to upload.
	 */
	cancel: function () {
		zul.Uploader.clearInterval(this.id);
		if (this._upload)
			this._upload.cancel(this._sid);
	},
	/**
	 * Updates the status of the file being uploaded.
	 * @param int sent how many percentage being sent
	 * @param int total the size of the file 
	 * @return boolean
	 */
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
	/**
	 * Ends the uploader to upload.
	 * @param boolean finish whether the file is finish.
	 */
	end: function (finish) {
		this.viewer.destroy(finish);
		zul.Upload.destroy(this);
	}
});

// default UploadViewer
	function _addUM(uplder, flnm) {
		var flman = zul.UploadViewer.flman;
		if (!flman || !flman.desktop) {
			if (flman) flman.detach();
			zul.UploadViewer.flman = flman = new zul.UploadManager();
			uplder.getWidget().getPage().appendChild(flman);
		}
		flman.removeFile(uplder);
		flman.addFile(uplder);
	}
	function _initUM(uplder, flnm) {
		if (zul.UploadManager)
			return _addUM(uplder, flnm);

		zk.load('zul.wgt,zul.box', function() {
			/**
			 * Default file upload manager to manage the uploading files in a panel.
			 * Users can add/delete the file upon the panel. 
			 */
			zul.UploadManager = zk.$extends(zul.wgt.Popup, {
				_files: {},
				$init: function () {
					this.$supers('$init', arguments);
					this.setSclass('z-fileupload-manager');
				},
				onFloatUp: function(ctl) {
					var wgt = ctl.origin;
					if (!this.isVisible()) 
						return;
					this.setTopmost();
				},
				/**
				 * Returns the file item.
				 * @param String id the ID of the file or the ID of upload widget
				 * @return zul.wgt.Div the file item widget.
				 */
				getFileItem: function(id) {
					return this._files[id] || zk.Widget.$(id);
				},
				/**
				 * Adds the file item to upload.
				 * @param zul.Uploader uplder
				 * @return zul.wgt.Div the file item widget
				 */
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
						// Bug 2987059: IE may cause JS error in the appendChild()
						try {
							this.appendChild(prog);
						} catch (e) {}
						this._files[id] = prog;
					}
					return prog;
				},
				/**
				 * Updates the status of the file item.
				 * @param zul.Uploader uplder
				 * @param int val how many percentage being uploaded
				 * @param int total the size of the file
				 */
				updateFile: function(uplder, val, total) {
					var id = uplder.id,
						prog = this.getFileItem(id);
					if (!prog) return;
					prog.$f(id).setValue(val);
					prog.$f(id + '_total').setValue(total);
				},
				/**
				 * Removes the file item.
				 * @param zul.Uploader uplder
				 */
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
				/**
				 * Opens the file manager to show.
				 * @param zk.Widget wgt the wgt where the file manager is shown
				 * @param String position the position where the file manager is located
				 */
				open: function(wgt, position) {
					this.$super('open', wgt, null, position || 'after_start', {
						sendOnOpen: false,
						disableMask: true
					});
				}
			});
			_addUM(uplder, flnm);
		});
	}
/**
 * Default file viewer to see the upload status.
 */
zul.UploadViewer = zk.$extends(zk.Object, {
	/** Constructor
	 * @param zul.Uploader uplder
	 * @param String flnm the name of the file to be uploaded
	 */
	$init: function (uplder,  flnm) {
		this._uplder = uplder;
		_initUM(uplder, flnm);
	},
	/**
	 * Updates the status of the file being uploaded.
	 * @param int sent how many percentage being sent
	 * @param int total the size of the file 
	 */
	update: function (sent, total) {
		var flman = zul.UploadViewer.flman;
		if (flman) {
			if (!flman.isOpen())
					flman.open(this._uplder.getWidget());
			flman.updateFile(this._uplder, sent, msgzk.FILE_SIZE+Math.round(total/1024)+msgzk.KBYTES);
		}
	},
	/**
	 * Destroys the upload viewer.
	 */
	destroy: function () {
		var flman = zul.UploadViewer.flman;
		if (flman)
			flman.removeFile(this._uplder);
	}
});

})();
