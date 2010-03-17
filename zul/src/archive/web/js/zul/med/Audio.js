/* Audio.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:59:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _invoke(wgt, fn1, fn2, unbind) {
		//Note: setSrc will rerender, so we need to delay the invocation of play
		if (unbind)
			_invoke2(wgt, fn1, fn2, unbind);
		else
			setTimeout(function () {
				_invoke2(wgt, fn1, fn2/*, unbind*/);
			}, 200);
	}
	function _invoke2(wgt, fn1, fn2, unbind) {
		var n = wgt.$n();
		if (n) {
			try { //Note: we cannot do "if (n.play)" in IE
				n[fn1]();
			} catch (e) {
				try {
					n[fn2](); //Firefox
				} catch (e) {
					if (!unbind)
						jq.alert(msgzul.NO_AUDIO_SUPPORT+'\n'+e.message);
				}
			}
		}
	}

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
	play: function () {
		_invoke(this, 'play', 'Play');
	},
	/** Stops the audio at the client.
	 */
	stop: function (_unbind_) {
		_invoke(this, 'stop', 'Stop', _unbind_);
	},
	/** Pauses the audio at the cient.
	 */
	pause: function () {
		_invoke(this, 'pause', 'Pause');
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

})();