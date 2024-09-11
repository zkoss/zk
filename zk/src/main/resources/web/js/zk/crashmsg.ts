/* crashmsg.ts

	Purpose: To remove "processing" mask and animation if the execution time exceeds the parameterized period

	Description:
	zkErrorCode has 5 value:
	* 1 -> before mounting zk
	* 2 -> mounting zk
	* 3 -> after mounting zk
	* 4 -> user script error
	* 5 -> user server no response
	Customization in zk.xml:
	* init crash page layout by defining window.zkShowCrashMessage function
	* init crash timeout by giving a number(sec)

	History:
		Wed, Nov 12, 2014  5:10:36 PM, Created by Chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
export default {};
declare global {
	interface Window {
		zkInitCrashTimer: number | undefined;
		zkShowCrashMessage: undefined | CallableFunction;
		zkInitCrashTimeout: number;
	}
}

window.zkInitCrashTimer = setTimeout(function () {
	var zkErrorCode,
		z_runonce = document.querySelectorAll('.z-runonce')[0],
		zk_proc = document.getElementById('zk_proc'),
		ztemp = document.querySelectorAll('.z-temp')[0],
		zna = document.getElementById('zna'),
		body = document.body;
	if (zk_proc) {
		if (z_runonce) { //zk error
			if (ztemp) {
				if (!zna)
					zkErrorCode = 1;
				else
					zkErrorCode = 2;
			} else
				zkErrorCode = 3;
		} else { // user error
			if (ztemp)
				zkErrorCode = 4;
			else
				zkErrorCode = 5;
		}

		if (!window.zkShowCrashMessage) {
			window.zkShowCrashMessage = function () {
				var styleHTML = '<style> a:visited {color: white;} </style>',
					divHTML = '<div style="background: rgb(35,48,64); text-align: center; color: white; position: absolute; \
						top: 0; left: 0; right: 0; bottom: 0; margin: auto; width: 50%; height: 300px; font-size: 20px;">',
					iconHTML = '<i class="z-icon-frown-o" style="font-size: 5em; display: block;"></i>',
					msgHTML = '<p>Something went wrong while loading the page.</p> \
						<p>Please try to reload or visit another page. If you are the administrator, \
						try to check your Javascript or Network console.</p>',
					copyrightHTML = '<div style="text-align: right; margin-top: -18px">\
						<span style="font-size: 10px;">powered by </span>\
						<span style="font-size: 14px;"><a href="http://www.zkoss.org/">ZK</a></span></div>',
					btnHTML = '<button id="z-crash-button" style="margin-top: 10px">Reload page</button>';
				switch (zkErrorCode) {
					case 1:
						msgHTML = '<p>Error code 1: ZK error, before mounting. </p>' + msgHTML;
						break;
					case 2:
						msgHTML = '<p>Error code 2: ZK error, mounting. </p>' + msgHTML;
						break;
					case 3:
						msgHTML = '<p>Error code 3: ZK error, after mounting. </p>' + msgHTML;
						break;
					case 4:
						msgHTML = '<p>Error code 4: user error, wrong script. </p>' + msgHTML;
						break;
					case 5:
						msgHTML = '<p>Error code 5: user error, server response. </p>' + msgHTML;
						break;
				}
				divHTML = styleHTML + divHTML + iconHTML + msgHTML + copyrightHTML + btnHTML + '</div>';
				body.style.background = 'rgb(35,48,64)';
				// eslint-disable-next-line @microsoft/sdl/no-inner-html
				body.innerHTML = divHTML;
				document.getElementById('z-crash-button')?.addEventListener('click', () => {
					location.reload();
				});
			};
		}
		window.zkShowCrashMessage(zkErrorCode);
	}
}, window.zkInitCrashTimeout >= 0 ? window.zkInitCrashTimeout * 1000 : 60000);