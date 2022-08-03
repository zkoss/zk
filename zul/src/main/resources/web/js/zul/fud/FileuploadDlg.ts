/* FileuploadDlg.ts

	Purpose:

	Description:

	History:
		Wed Aug 24 15:38:57 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
/**
 * A FileuploadDlg.
 *
 */
@zk.WrapClass('zul.fud.FileuploadDlg')
export class FileuploadDlg extends zul.wnd.Window {
	max?: number;

	override getZclass(): string { // keep the window's zclass
		return this._zclass == null ? 'z-window' : this._zclass;
	}

	cancel(sendToServer?: boolean): void {
		var fu = this.$f('fileupload');
		if (fu) {
			fu._uplder!.destroy();
			if (sendToServer) {
				zAu.send(new zk.Event(this, 'onClose'));
				this.detach();
			}
		}
	}

	submit(): void {
		this.cancel();
		zAu.send(new zk.Event(this, 'onClose', true));
		this.detach();
	}
}

@zk.WrapClass('zul.fud.ModalFileViewer')
export class ModalFileViewer extends zk.Object {
	updated?: boolean;
	_finish?: boolean;
	_uplder: zul.Uploader;
	viewer?: HTMLElement;

	constructor(uplder: zul.Uploader, filenm: string) {
		super();
		this._uplder = uplder;
		filenm = filenm.replace(/\//g, '\\');
		filenm = filenm.substring(filenm.lastIndexOf('\\') + 1, filenm.length);

		var id = uplder.id,
			wgt = uplder.getWidget()!,
			uploaded = wgt.$f('uploaded')!,
			max = wgt.$o<zul.fud.FileuploadDlg>()!.max ?? 0, //max is stored in FileuploadDlg (i.e., $o())
			uri = zk.ajaxURI('/web/zk/img/progress2.gif', {resource: true}),
			html = '<div id="' + id + '" style="min-height:16px;background:#F4F8FF;border: 1px solid #99AABD;font-family:'
			+ 'arial,sans-serif;font-size: 11px;padding: 2px;'
			+ 'color: #0F3B82;"><img style="float: left;" src="' + uri + '"/>'
			+ '<div id="' + id + '-cnt"><a id="' + id + '-cancel" style="float: left;padding-left: 2px; color: #0366AC;'
			+ ' font-weight: bold;cursor: pointer;">' + msgzul.UPLOAD_CANCEL + ' </a>&nbsp;&nbsp;&nbsp;' + filenm
			+ '</div><div style="padding-left: 24px">'
			+ msgzk.FILE_SIZE + '&nbsp;&nbsp;<span id="' + id + '-sent">0</span> of '
			+ '<span id="' + id + '-total">0</span></div></div>';

		uploaded.setVisible(true);
		jq(uploaded).append(html);

		if (max > 0 && max <= uploaded.$n_().childNodes.length)
			uploaded.$f('fileupload')!.setVisible(false); // B50-ZK-340: need to skip rerender

		this.viewer = jq('#' + id)[0];
		jq('#' + id + '-cancel').click(() => {
			wgt.$f<zul.fud.Submit>('submit')!.revert();
			if (!this._finish) uplder.cancel();
			else {
				var $n = jq('#' + id),
					index = $n.parent().children().index($n[0]);
				zAu.send(new zk.Event(wgt.$o()!, 'onRemove', index));
				jq(this.viewer).remove();
			}

			if (max > 0 && max > uploaded.$n_().childNodes.length)
				uploaded.$f('fileupload')!.setVisible(true); // B50-ZK-340: need to skip rerender
		});
	}

	update(sent: number, total: number): void {
		jq('#' + this._uplder.id + '-sent').html(Math.round((total / 1024) * sent / 100) + msgzk.KBYTES);
		if (!this.updated) {
			this.updated = true;
			jq('#' + this._uplder.id + '-total').html(Math.round(total / 1024) + msgzk.KBYTES);
		}
	}

	destroy(finish: boolean): void {
		if (this._finish) return;
		this._finish = finish;
		if (!finish) jq(this.viewer).remove();
		else {
			jq('#' + this._uplder.id + '-sent').parent().remove();
			(jq('#' + this._uplder.id)[0].firstChild as HTMLImageElement).src = zk.ajaxURI('/web/zk/img/attachment.gif', {resource: true});
		}
	}
}