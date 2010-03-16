/* Audio.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:59:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An audio clip.
 *
 * <p>An extension to XUL.
 */
zul.med.Audio = zk.$extends(zul.Widget, {
	$define: {
		/** Returns the src.
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the src.
		 * @param String src
		 */
		src: function () {
			this.rerender(); //At least IE failed if change n.src only
		},
		/** Returns the alignment.
		 * <p>Default: null (use browser default).
		 * @return String
		 */
		/** Sets the alignment: one of top, texttop, middle, absmiddle,
		 * bottom, absbottom, baseline, left, right and center.
		 * @param String align
		 */
		align: function (v) {
			var n = this.$n();
			if (n) n.align = v || '';
		},
		/** Returns the width of the border.
		 * <p>Default: null (use browser default).
		 * @return String
		 */
		/** Sets the width of the border.
		 * @param String border
		 */
		border: function (v) {
			var n = this.$n();
			if (n) n.border = v || '';
		},
		/** Returns whether to auto start playing the audio.
		 * <p>Default: false;
		 * @return boolean
		 */
		/** Sets whether to auto start playing the audio.
		 * @param boolean autostart
		 */
		autostart: function (v) {
			var n = this.$n();
			if (n) n.autostart = v;
		},
		/** Returns whether to play the audio repeatedly.
		 * <p>Default: false;
		 * @return boolean
		 */
		/** Sets whether to play the audio repeatedly.
		 * @param boolean loop
		 */
		loop: function (v) {
			var n = this.$n();
			if (n) n.loop = v;
		}
	},
	/** Plays the audio at the client.
	 */
	play: function (delay) {
		
		// setSrc will rerender, we need to invoke the function later.
		if (!delay) {
			var self = this;
			setTimeout(function () {
				self.play(true);
			}, 100);
			return;
		}
		var n = this.$n();
		if (n) {
			try { //Note: we cannot do "if (n.play)" in IE
				n.play();
			} catch (e) {
				try {
					n.Play(); //Firefox
				} catch (e) {
					jq.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	},
	/** Stops the audio at the cient.
	 */
	stop: function (silent) {
		var n = this.$n();
		if (n) {
			try { //Note: we cannot do "if (n.stop)" in IE
				n.stop();
			} catch (e) {
				try {
					n.Stop();
				} catch (e) {
					if (!silent)
						jq.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	},
	/** Pauses the audio at the cient.
	 */
	pause: function () {
		var n = this.$n();
		if (n) {
			try { //Note: we cannot do "if (n.pause)" in IE
				n.pause();
			} catch (e) {
				try {
					n.Pause();
				} catch (e) {
					jq.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
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
				+ ' src="' + (this._src || '') + '"',
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
