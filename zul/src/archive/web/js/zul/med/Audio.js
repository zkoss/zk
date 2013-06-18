/* Audio.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:59:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	
	function _invoke(wgt, fn, unbind) {
		//Note: setSrc will rerender, so we need to delay the invocation of play
		if (unbind)
			_invoke2(wgt, fn, unbind);
		else
			setTimeout(function () {
				_invoke2(wgt, fn);
			}, 200);
	}
	function _invoke2(wgt, fn, unbind) { 
		var n = wgt.$n();
		if (n) {
			try {
				n[fn]();
			} catch (e) {
				if (!unbind)
					jq.alert(msgzul.NO_AUDIO_SUPPORT + '\n' + e.message);
			}
		}
	}

	
var Audio =
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
			this.rerender();
		},
		/** Returns whether to auto start playing the audio.
		 * <p>Default: false;
		 * @return boolean
		 * @deprecated As of release 7.0.0, use getAutoplay instead.
		 */
		/** Sets whether to auto start playing the audio.
		 * @param boolean autostart
		 * @deprecated As of release 7.0.0, use setAutoplay instead.
		 */
		autostart: function (v) {
			var n = this.$n();
			if (n) n.autostart = v;
		},
		/** Returns whether to auto start playing the audio.
		 * <p>Default: false;
		 * @return boolean
		 */
		/** Sets whether to auto start playing the audio.
		 * @param boolean autoplay
		 */
		autoplay: function (v) {
			var n = this.$n();
			if (n) n.autoplay = v;
		},
		/** Returns whether and how the audio should be loaded.
		 *
		 * <p>Default: false;
		 * @since 7.0.0
		 */
		/** Sets whether and how the audio should be loaded.
		 * @since 7.0.0
		 */	
		preload: function(v) {
			var n = this.$n();
			if (n && v !== undefined) n.preload = v;
		},
		/** Returns whether to display the audio controls.
		 *
		 * <p>Default: false;
		 * @since 7.0.0
		 */
		/** Sets whether to display the audio controls.
		 * @since 7.0.0
		 */
		controls: function (v) {
			var n = this.$n();
			if (n) n.controls = v;
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
		},
		/** Returns whether to mute the audio.
		 *
		 * <p>Default: false;
		 * @since 7.0.0
		 */
		/** Sets whether to mute the audio.
		 * @since 7.0.0
		 */
		muted: function (v) {
			var n = this.$n();
			if (n) n.muted = v;
		}		
	},
	/** Plays the audio at the client.
	 */
	play: function () {
		_invoke(this, 'play');
	},
	/** Stops the audio at the client.
	 */
	stop: function () {
		_invoke(this, 'pause');		
	},
	/** Pauses the audio at the client.
	 */
	pause: function () {
		_invoke(this, 'pause');		
	},
	unbind_: function () {
		this.stop();
		this.$supers(Audio, 'unbind_', arguments);
	},
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments);
		if (this._autoplay) 
			attr += ' autoplay';
		if (this._preload !== undefined)
			attr += ' preload="' + this._preload + '"';
		if (this._controls) 
			attr += ' controls';
		if (this._loop) 
			attr += ' loop';
		if (this._muted)
			attr += ' muted';
		return attr;
	},
	_sourceHTML: function(out) {
		var src = this._src,
			length = src.length,
			result = '';
		for (var i = 0; i < length; i ++) {
			result += '<source src="' + src[i] + '" type="' + this._MIMEtype(src[i]) + '">';
		}
		if (out) {
			out.push(result);
		}
	},
	_MIMEtype: function(name) {
		var start = name.lastIndexOf('.'),
		type = 'wav';
		if (start !== -1) {
			var ext = name.substring(start + 1).toLowerCase();
			if (ext === "mp3") {
				type = 'mpeg';
			} else if (ext ==="ogg") {
				type = 'ogg';
			}
		}
		return 'audio/' + type;
	}
});
})();