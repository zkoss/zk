/* audio.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 17 15:45:10     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul"); //msgzul

// Audio //
zkAudio = {};

zkAudio.play = function (id) {
	var cmp = $e(id);
	if (cmp) {
		try { //Note: we cannot do "if (cmp.play)" in IE
			cmp.play();
		} catch (e) {
			try {
				cmp.Play(); //Firefox
			} catch (e) {
				zk.alert(msgzul.NO_AUDIO_SUPPORT);
			}
		}
	}
};

zkAudio.stop = function (id) {
	var cmp = $e(id);
	if (cmp) {
		try { //Note: we cannot do "if (cmp.stop)" in IE
			cmp.stop();
		} catch (e) {
			try {
				cmp.Stop();
			} catch (e) {
				zk.alert(msgzul.NO_AUDIO_SUPPORT);
			}
		}
	}
};

zkAudio.pause = function (id) {
	var cmp = $e(id);
	if (cmp) {
		try { //Note: we cannot do "if (cmp.pause)" in IE
			cmp.pause();
		} catch (e) {
			zk.alert(msgzul.NO_AUDIO_SUPPORT);
		}
	}
};

zkAudio.cleanup = function (cmp) {
	try { //Note: we cannot do "if (cmp.stop)" in IE
		cmp.stop();
	} catch (e) { //silent
	}
};
