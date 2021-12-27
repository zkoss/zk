/* uploadUtils.ts

        Purpose:
                
        Description:
                
        History:
                Mon Sep 10 14:17:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

*/
(function () {
zk.UploadUtils = {
	ajaxUpload: function (wgt, xhr, formData, sid) {
		var dt = wgt.desktop,
			tempUri = zk.ajaxURI('/dropupload', {desktop: dt, au: true}),
			ajaxUri = tempUri + (tempUri.indexOf('?') == -1 ? '?' : '&')
				+ 'uuid=' + wgt.uuid
				+ '&dtid=' + dt.id
				+ (sid != undefined ? '&sid=' + sid : '');
		xhr.open('POST', ajaxUri, true);
		if (zk.xhrWithCredentials)
			xhr.withCredentials = true;
		xhr.send(formData);
	},

	fileUpload: function (wgt, blob, sid, eventName) {
		var xhr = new XMLHttpRequest(),
			formData = new FormData();
		formData.append('file', blob);
		xhr.onload = function (e) {
			if (this.readyState === 4) {
				if (this.status === 200) {
					wgt.fire(eventName, {sid: sid});
				} else {
					zk.error(xhr.statusText);
				}
			}
		};
		zk.UploadUtils.ajaxUpload(wgt, xhr, formData, sid);
	}
};
})();