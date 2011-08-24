/* FileuploadDlg.js

	Purpose:
		
	Description:
		
	History:
		Wed Aug 24 15:38:57 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
zul.fud.FileuploadDlg = zk.$extends(zul.wnd.Window, {
	cancel: function (sendToServer) {
		var fu = this.$f('fileupload');
		if (fu) {
			fu._uplder.destroy();
			if (sendToServer) {
				zAu.send(new zk.Event(this, 'onClose'));
				this.detach();
			}
		}
	},
	submit: function () {
		this.cancel();
		zAu.send(new zk.Event(this, 'onClose', true));
		this.detach();
	}
});

zul.fud.ModalFileViewer = zk.$extends(zk.Object, {
	updated: null,
	$init: function (uplder,  filenm) {
		this._uplder = uplder;
		filenm = filenm.replace(/\//g, '\\');
		filenm = filenm.substring(filenm.lastIndexOf('\\') + 1, filenm.length);
		
		var id = uplder.id,
			self = this,
			wgt = uplder.getWidget(),
			uploaded = wgt.$f('uploaded'),
			max = wgt.$o().max||0, //max is stored in FileuploadDlg (i.e., $o())
			uri = zk.ajaxURI('/web/zk/img/progress2.gif', {au:true}),
			html = '<div id="' + id + '" style="min-height:16px;background:#F4F8FF;border: 1px solid #99AABD;font-family:'
			+ 'arial,sans-serif;font-size: 11px;padding: 2px;'
			+ 'color: #0F3B82;"><image style="float: left;" src="' + uri + '"/>'
			+ '<div id="' + id + '-cnt"><a id="' + id + '-cancel" style="float: left;padding-left: 2px; color: #0366AC;'
			+ ' font-weight: bold;cursor: pointer;">' + msgzul.UPLOAD_CANCEL + ' </a>&nbsp;&nbsp;&nbsp;' + filenm
			+ '</div><div style="padding-left: 24px">'
			+ msgzk.FILE_SIZE + '&nbsp;&nbsp;<span id="' + id + '-sent">0</span> of '
			+ '<span id="' + id + '-total">0</span></div></div>';
		
		uploaded.setVisible(true);
		jq(uploaded).append(html);

		if (max > 0 && max <= uploaded.$n().childNodes.length)
			uploaded.$f("fileupload").setVisible(false); // B50-ZK-340: need to skip rerender
		
		this.viewer = jq('#'+ id)[0];
		jq('#' + id + '-cancel').click(function() {
			wgt.$f('submit').revert();
			if (!self._finish) uplder.cancel();
			else {
				var $n = jq('#' + id),
					index = $n.parent().children().index($n[0]);
				zAu.send(new zk.Event(wgt.$o(), "onRemove", index));
				jq(self.viewer).remove();
			}
			
			if (max > 0 && max > uploaded.$n().childNodes.length)
				uploaded.$f("fileupload").setVisible(true); // B50-ZK-340: need to skip rerender
		});
	},
	update: function (sent, total) {
		jq('#'+ this._uplder.id + '-sent').html(Math.round((total/1024)*sent/100) + msgzk.KBYTES);
		if (!this.updated) {
			this.updated = true;
			jq('#'+ this._uplder.id + '-total').html(Math.round(total/1024)+msgzk.KBYTES);
		}
	},
	destroy: function (finish) {
		if (this._finish) return;
		this._finish = finish;
		if (!finish) jq(this.viewer).remove();
		else {
			jq('#' + this._uplder.id + '-sent').parent().remove();
			jq('#' + this._uplder.id)[0].firstChild.src = zk.ajaxURI('/web/zk/img/attachment.gif', {au:true});
		}
	}
});
