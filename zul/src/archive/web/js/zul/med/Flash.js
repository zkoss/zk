/* Flash.js

	Purpose:
		
	Description:
		
	History:
		Sat Mar 28 22:02:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A generic flash component.
 *
 * <p>Non XUL extension.
 */
zul.med.Flash = zk.$extends(zul.Widget, {
	_wmode: 'transparent',
	_quality: 'high',
	_autoplay: true,
	_loop: false,
	_version: '6,0,0,0',

	$define: {
		/** Returns the expected version of the Flash player.
		 * <p>Default: "6,0,0,0"
		 * @return String
		 */
		/** Sets the expected version of the Flash player.
		 * @param String version
		 */
		version: function () {
			this.rerender();
		},
		/** Gets the source path of Flash movie
		 * @return  String the source path of Flash movie
		 */
		/** Sets the source path of Flash movie 
		 * and redraw the component
		 * @param String src
		 */
		src: function (v) {
			var n = this._embedNode();
			if (n) n.movie = v || '';
		},
		/** Returns the Window mode property of the Flash movie 
		 * <p>Default: "transparent".
		 * @return String the Window mode property of the Flash movie 
		 */
		/** Sets the Window Mode property of the Flash movie 
		 * for transparency, layering, and positioning in the browser.
		 * @param String wmode Possible values: window, opaque, transparent.
		 */
		wmode: function (wmode) {
			var n = this._embedNode();
			if (n) n.wmode = v || '';
		},
		/** Gets the background color of Flash movie.
		 * <p>Default: null (the system default)
		 * @return String the background color of Flash movie,[ hexadecimal RGB value] 
		 */
		/** Sets the background color of Flash movie.
		 * @param String bgcolor [ hexadecimal RGB value] 
		 */
		bgcolor: function (v) {
			var n = this._embedNode();
			if (n) n.bgcolor = v || '';
		},
		/** Returns the quality of the Flash movie 
		 * <p>Default: "high".
		 * @return String the quality of the Flash movie
		 */
		/** Sets the quality of the Flash movie.
		 * @param String quality the quality of the Flash movie.
		 */
		quality: function (v) {
			var n = this._embedNode();
			if (n) n.quality = v || '';
		},
		/** Return true if the Flash movie starts playing automatically
		 * <p>Default: true
		 * @return boolean true if the Flash movie starts playing automatically
		 */
		/** Sets wether to play the Flash movie automatically.
		 * @param boolean autoplay whether to play the Flash movie automatically
		 */
		autoplay: function (autoplay) {
			var n = this._embedNode();
			if (n) n.autoplay = v || '';
		},
		/** Returns true if the Flash movie plays repeatly.
		 * <p>Default: false
		 * @return boolean true if the Flash movie plays repeatly 
		 */
		/** Sets whether the Flash movie plays repeatly
		 * @param boolean loop
		 */
		loop: function (v) {
			var n = this._embedNode();
			if (n) n.loop = v || '';
		}
	},

	//super//
	setHeight: function (height) {
		this._height = height;
		var n = this._embedNode();
		if (n) n.height = height ? height: '';
	},
	setWidth: function (width) {
		this._width = width;
		var n = this._embedNode();
		if (n) n.width = width ? width: '';
	},

	_embedNode: function () {
		return this.$n('emb');
	}
});
