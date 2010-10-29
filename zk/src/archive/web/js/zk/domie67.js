/* domie67.js

	Purpose:
		Patches for both IE6 and IE7
	Description:
		
	History:
		Fri Oct 29 15:49:08 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
//IE: use query string if possible to avoid incomplete-request problem
zjq._useQS = function (reqInf) {
	var s = reqInf.content, j = s.length, prev, cc;
	if (j + reqInf.uri.length < 2000) {
		while (j--) {
			cc = s.charAt(j);
			if (cc == '%' && prev >= '8') //%8x, %9x...
				return false;
			prev = cc;
		}
		return true;
	}
	return false;
};