/* uploadUtils.js

        Purpose:
                
        Description:
                
        History:
                Mon Sep 10 14:17:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

*/
(function () {
zk.UploadUtils = {
	ajaxUpload: function (wgt, xhr, formData) {
		var dt = wgt.desktop,
			tempUri = zk.ajaxURI('/dropupload', {desktop: dt,au: true}),
			ajaxUri = tempUri + (tempUri.indexOf('?') == -1 ? '?' : '&')
				+ 'uuid=' + wgt.uuid
				+ '&dtid=' + dt.id;
		xhr.open('POST', ajaxUri, true);
		xhr.send(formData);
	}
};
})();