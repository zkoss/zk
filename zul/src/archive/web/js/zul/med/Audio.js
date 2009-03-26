/* Audio.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:59:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.med.Audio = zk.$extends(zul.Widget, {
	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			var n = this.getNode();
			if (n) this.rerender(); //At least IE failed if change n.src only
		}
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n) n.align = align || '';
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (this._border != border) {
			this._border = border;
			var n = this.getNode();
			if (n) n.border = border || '';
		}
	},
	getAutostart: function () {
		return this._autostart;
	},
	setAutostart: function (autostart) {
		if (this._autostart != autostart) {
			this._autostart = autostart;
			var n = this.getNode();
			if (n) n.autostart = autostart;
		}
	},
	getLoop: function () {
		return this._loop;
	},
	setLoop: function (loop) {
		if (this._loop != loop) {
			this._loop = loop;
			var n = this.getNode();
			if (n) n.loop = loop;
		}
	},

	play: function () {
		var n = this.getNode();
		if (n) {
			try { //Note: we cannot do "if (n.play)" in IE
				n.play();
			} catch (e) {
				try {
					n.Play(); //Firefox
				} catch (e) {
					zDom.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	},

	stop: function (silent) {
		var n = this.getNode();
		if (n) {
			try { //Note: we cannot do "if (n.stop)" in IE
				n.stop();
			} catch (e) {
				try {
					n.Stop();
				} catch (e) {
					if (!silent)
						zDom.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	},

	pause: function () {
		var n = this.getNode();
		if (n) {
			try { //Note: we cannot do "if (n.pause)" in IE
				n.pause();
			} catch (e) {
				try {
					n.Pause();
				} catch (e) {
					zDom.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	},

	unbind_: function () {
		this.stop(true);
		this.$supers('unbind_', arguments);
	},

	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments)
				+ ' src="' + (this._src || '') + '" z_autohide="true"',
			v;
		if (v = this._align) 
			attr += ' align="' + v + '"';
		if (v = this._border) 
			attr += ' border="' + v + '"';
		if (v = this._autostart) 
			attr += ' autostart="' + v + '"';
		if (v = this._loop) 
			attr += ' loop="' + v + '"';
		return attr;
	}
});