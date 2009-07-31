/* dom.js

	Purpose:
		
	Description:
		
	History:
		Fri Jul 31 16:37:36     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var $alert = jq.alert,
		icons = {QUESTION: "z-msgbox z-msgbox-question",
			EXCLAMATION: "z-msgbox z-msgbox-exclamation",
			INFORMATION: "z-msgbox z-msgbox-information",
			ERROR: "z-msgbox z-msgbox-error",
			NONE: 'z-msgbox z-msgbox-none'
		},
		btnNames = ['OK', 'CANCEL', 'YES', 'NO', 'RETRY', 'ABORT', 'IGNORE'];

	function newButton(nm, f) {
		return new zul.wgt.Button({
			label: msgzul[nm]||nm,
			listeners: {
				onClick: function (evt) {
					if (typeof f == 'function')
						f.call(this, evt);
					this.$o().detach();
				}
			}
		});
	}
	function getButtons(opts) {
		var btns = [];
		jq.each(btnNames, function (i, nm) {
			var f;
			if (f = opts[nm]) {
				btns.push(newButton(nm, f));
			}
		});
		if (!btns.length)
			btns.push(newButton('OK'));
		return btns;
	}
	jq.alert = function (msg, opts) {
		if (opts && opts.mode == 'os')
			return $alert(msg);

		opts = opts || {};
		zk.load("zul.wnd,zul.wgt,zul.box", function () {
			var wnd = new zul.wnd.Window({
				closable: true,
				width: '250pt',
				title: opts.title||'ZK',
				border: 'normal',
				children: [
					new zul.box.Box({
						mold: 'horizontal',
						children: [
							new zul.wgt.Div({sclass: icons[(opts.icon||'').toUpperCase()]||icons.INFORMATION}),
							new zul.wgt.Div({
								sclass: 'z-messagebox',
								width: '210pt',
								children: [
									new zul.wgt.Label({
										value: msg,
										multiline: true
									})
								]
							})
						]
					}),
					new zul.wgt.Separator({bar: true}),
					new zul.box.Box({
						mold: 'horizontal',
						style: 'margin-left:auto; margin-right:auto',
						children: getButtons(opts)
					})
				],
				mode: opts.mode||'modal'
			});

			var dt = opts.desktop || zk.Desktop.$(), p;
			if (!dt) dt = zk.stateless();
			if ((p = dt.firstChild) && p.$instanceof(zk.Page))
				dt = p;
			dt.appendChild(wnd);
		});
  	};
})();