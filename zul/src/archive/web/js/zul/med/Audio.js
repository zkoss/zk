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
				if (fn === 'stop') {
					n.pause();
					n.currentTime = 0;
				} else 	
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
 * Only works for browsers supporting HTML5 audio tag (since ZK 7.0.0).
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
		 * <p>Default: false.
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
		 * <p>Default: null.
		 * @return String
		 * @since 7.0.0
		 */
		/** Sets whether and how the audio should be loaded.
		 * Refer to <a href="http://www.w3.org/TR/html5/embedded-content-0.html#attr-media-preload">Preload Attribute Description</a> for details.
		 * @param String preload
		 * @since 7.0.0
		 */	
		preload: function(v) {
			var n = this.$n();
			if (n && v !== undefined) n.preload = v;
		},
		/** Returns whether to display the audio controls.
		 *
		 * <p>Default: false.
		 * @return boolean
		 * @since 7.0.0
		 */
		/** Sets whether to display the audio controls.
		 * @param boolean controls
		 * @since 7.0.0
		 */
		controls: function (v) {
			var n = this.$n();
			if (n) n.controls = v;
		},
		/** Returns whether to play the audio repeatedly.
		 * <p>Default: false.
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
		 * <p>Default: false.
		 * @return boolean
		 * @since 7.0.0
		 */
		/** Sets whether to mute the audio.
		 * @param boolean muted
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
		_invoke(this, 'stop');		
	},
	/** Pauses the audio at the client.
	 */
	pause: function () {
		_invoke(this, 'pause');		
	},
	unbind_: function () {
		this.stop(true);
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
	domContent_: function() {
		var src = this._src,
			length = src.length,
			result = '';
		for (var i = 0; i < length; i ++) {
			result += '<source src="' + src[i] + '" type="' + this._MIMEtype(src[i]) + '">';
		}
		return result;
	},
	_MIMEtype: function(name) {
		var start = name.lastIndexOf('.'),
			type = 'wav';
		if (start !== -1) {
			var ext = name.substring(start + 1).toLowerCase();
			if (ext === 'mp3') {
				type = 'mpeg';
			} else if (ext === 'ogg') {
				type = 'ogg';
			}
		}
		return 'audio/' + type;
	}
});
})();