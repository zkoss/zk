/* dom.js

	Purpose:

	Description:

	History:
		Fri Jul 31 16:37:36     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//handle theme
	jq(function () {
		var zktheme = zk.themeName,
			tname = 'breeze'; // shall sync with default theme name
		if (zktheme)
			tname = zktheme;
		jq(document.body).addClass(tname);
	});

	var $alert = jq.alert,
		icons = {QUESTION: 'z-messagebox-icon z-messagebox-question',
			EXCLAMATION: 'z-messagebox-icon z-messagebox-exclamation',
			INFORMATION: 'z-messagebox-icon z-messagebox-information',
			ERROR: 'z-messagebox-icon z-messagebox-error',
			NONE: 'z-messagebox z-messagebox-none'
		};

	function newButton(nm, f) {
		return new zul.wgt.Button({
			label: msgzul[nm.toUpperCase()] || nm,
			listeners: {
				onClick: function (evt) {
					if (typeof f == 'function')
						f.call(this, evt);

					// backup first
					var dt = this.desktop;
					this.$o().detach();

					// B70-ZK-1683
					if (zAu.disabledRequest) {
						zAu.disabledRequest = false;
						zAu.sendNow(dt);
					}
				}
			}
		});
	}
	function getButtons(opts) {
		var btns = [];
		for (var nm in opts) {
			var f = opts[nm];
			btns.push(newButton(nm, typeof f == 'function' ? f : null));
		}
		if (!btns.length)
			btns.push(newButton(msgzul.OK));
		return btns;
	}

	jq.alert = function (msg, opts) {
		if (opts && opts.mode == 'os')
			return $alert(msg);

		opts = opts || {};
		zk.load('zul.wnd,zul.wgt,zul.box', function () {
			var wnd = zk.Widget.$(jq('$aualert'));
			if (!wnd) {
				var wnd = new zul.wnd.Window({
					id: 'aualert',
					closable: true,
					width: '250pt',
					sclass: 'z-messagebox-window',
					title: opts.title || zk.appName,
					border: 'normal',
					listeners: {onClose: function () {

						// B70-ZK-1683
						if (zAu.disabledRequest) {
							zAu.disabledRequest = false;
							zAu.sendNow(this.desktop);
						}
					}},
					children: [
						new zul.box.Box({
							mold: 'horizontal',
							children: [
								new zul.wgt.Div({sclass: icons[(opts.icon || '').toUpperCase()] || opts.icon || icons.INFORMATION}),
								new zul.wgt.Div({
									id: 'content',
									sclass: 'z-messagebox',
									width: '210pt',
									style: 'overflow:auto',
									children: [
										new zul.wgt.Label({
											id: 'msg',
											value: msg,
											multiline: true
										})
									]
								})
							]
						}),
						new zul.wgt.Separator(),
						new zul.box.Hlayout({
							sclass: 'z-messagebox-buttons',
							children: getButtons(opts.button)
						})
					],
					mode: opts.mode || 'modal'
				});
				var p = opts.desktop || zk.Desktop.$();
				if (p && (p = p.firstChild) && p.desktop)
					p.appendChild(wnd);
				else
					jq(document.body).append(wnd);
			} else {
				var label = wnd.$f('msg'),
					p = label.parent,
					pn = p.$n();
				label.setValue(label.getValue() + '\n' + msg);
				if (!pn.style.height && pn.offsetHeight >= jq.innerHeight() * 0.6) {
					pn.style.height = jq.px0(jq.innerHeight() * 0.6);
				}
			}
		});
	};
	zAu.wrongValue_ = function (wgt, msg) {
		var efs = wgt.effects_;
		if (efs.errMesg) {
			efs.errMesg.destroy();
			delete efs.errMesg;
		}
		if (msg !== false) {
			efs.errMesg = {destroy: zk.$void};
			zk.load('zul.inp', function () {
				if (efs.errMesg) //not destroyed yet
					(efs.errMesg = new zul.inp.Errorbox(wgt, msg)).show();
			});
		}
	};
})();
