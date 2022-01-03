/* uploadUtils.ts

        Purpose:
                
        Description:
                
        History:
                Mon Sep 10 14:17:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

*/
(function () {
zk.UploadUtils = {
	ajaxUpload: function (wgt, xhr, formData, uri, sid) {
		let ajaxUri = zk.UploadUtils.getUploadAjaxUri(wgt, uri, sid);
		xhr.open('POST', ajaxUri, true);
		if (zk.xhrWithCredentials)
			xhr.withCredentials = true;
		xhr.send(formData);
	},

	fileUpload: function (wgt, blob, sid, eventName) {
		let xhr = new XMLHttpRequest(),
			formData = new FormData();
		formData.append('file', blob);
		xhr.onload = function () {
			if (xhr.readyState === 4) {
				if (xhr.status === 200) {
					wgt.fire(eventName, {sid: sid});
				} else {
					zk.error(xhr.statusText);
				}
			}
		};
		zk.UploadUtils.ajaxUpload(wgt, xhr, formData, '/dropupload', sid, eventName);  // eventName is for Zephyr.
	},
	getUploadAjaxUri: function (wgt, uri, sid) {
		let dt = wgt.desktop,
			tempUri = zk.ajaxURI(uri, {desktop: dt, au: true});
			return tempUri + (tempUri.indexOf('?') == -1 ? '?' : '&')
				+ 'uuid=' + wgt.uuid
				+ '&dtid=' + dt.id
				+ (sid != undefined ? '&sid=' + sid : '');
	}
};
})();