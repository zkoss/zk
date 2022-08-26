/* Flash.ts

	Purpose:

	Description:

	History:
		Sat Mar 28 22:02:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A generic flash component.
 *
 * <p>Non XUL extension.
 */
@zk.WrapClass('zul.med.Flash')
export class Flash extends zul.Widget {
	_wmode = 'transparent';
	_quality = 'high';
	_autoplay = true;
	_loop = false;
	_version = '6,0,0,0';
	_src?: string;
	_bgcolor?: string;

	/** Returns the expected version of the Flash player.
	 * <p>Default: "6,0,0,0"
	 * @return String
	 */
	getVersion(): string {
		return this._version;
	}

	/** Sets the expected version of the Flash player.
	 * @param String version
	 */
	setVersion(version: string, opts?: Record<string, boolean>): this {
		const o = this._version;
		this._version = version;

		if (o !== version || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Gets the source path of Flash movie
	 * @return  String the source path of Flash movie
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/** Sets the source path of Flash movie
	 * and redraw the component
	 * @param String src
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			var n = this._embedNode();
			if (n) n.movie = n.src = src || ''; // Use of non-standard attribute `movie`
		}

		return this;
	}

	/** Returns the Window mode property of the Flash movie
	 * <p>Default: "transparent".
	 * @return String the Window mode property of the Flash movie
	 */
	getWmode(): string {
		return this._wmode;
	}

	/** Sets the Window Mode property of the Flash movie
	 * for transparency, layering, and positioning in the browser.
	 * @param String wmode Possible values: window, opaque, transparent.
	 */
	setWmode(wmode: string, opts?: Record<string, boolean>): this {
		const o = this._wmode;
		this._wmode = wmode;

		if (o !== wmode || opts?.force) {
			var n = this._embedNode();
			if (n) n.wmode = wmode || ''; // Use of non-standard attribute `wmode`
		}

		return this;
	}

	/** Gets the background color of Flash movie.
	 * <p>Default: null (the system default)
	 * @return String the background color of Flash movie,[ hexadecimal RGB value]
	 */
	getBgcolor(): string | undefined {
		return this._bgcolor;
	}

	/** Sets the background color of Flash movie.
	 * @param String bgcolor [ hexadecimal RGB value]
	 */
	setBgcolor(bgcolor: string, opts?: Record<string, boolean>): this {
		const o = this._bgcolor;
		this._bgcolor = bgcolor;

		if (o !== bgcolor || opts?.force) {
			var n = this._embedNode();
			if (n) n.bgcolor = bgcolor || ''; // Use of non-standard attribute `bgcolor`
		}

		return this;
	}

	/** Returns the quality of the Flash movie
	 * <p>Default: "high".
	 * @return String the quality of the Flash movie
	 */
	getQuality(): string {
		return this._quality;
	}

	/** Sets the quality of the Flash movie.
	 * @param String quality the quality of the Flash movie.
	 */
	setQuality(quality: string, opts?: Record<string, boolean>): this {
		const o = this._quality;
		this._quality = quality;

		if (o !== quality || opts?.force) {
			var n = this._embedNode();
			if (n) n.quality = quality || ''; // Use of non-standard attribute `quality`
		}

		return this;
	}

	/** Return true if the Flash movie starts playing automatically
	 * <p>Default: true
	 * @return boolean true if the Flash movie starts playing automatically
	 */
	isAutoplay(): boolean {
		return this._autoplay;
	}

	/** Sets wether to play the Flash movie automatically.
	 * @param boolean autoplay whether to play the Flash movie automatically
	 */
	setAutoplay(autoplay: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoplay;
		this._autoplay = autoplay;

		if (o !== autoplay || opts?.force) {
			var n = this._embedNode();
			if (n) n.autoplay = autoplay || ''; // Use of non-standard attribute `autoplay`
		}

		return this;
	}

	/** Returns true if the Flash movie plays repeatly.
	 * <p>Default: false
	 * @return boolean true if the Flash movie plays repeatly
	 */
	isLoop(): boolean {
		return this._loop;
	}

	/** Sets whether the Flash movie plays repeatly
	 * @param boolean loop
	 */
	setLoop(loop: boolean, opts?: Record<string, boolean>): this {
		const o = this._loop;
		this._loop = loop;

		if (o !== loop || opts?.force) {
			var n = this._embedNode();
			if (n) n.loop = loop || ''; // Use of non-standard attribute `loop`
		}

		return this;
	}

	override setHeight(height?: string): this {
		this._height = height;
		const n = this._embedNode();
		if (n) n.height = height ? height : '';
		return this;
	}

	override setWidth(width?: string): this {
		this._width = width;
		const n = this._embedNode();
		if (n) n.width = width ? width : '';
		return this;
	}

	_embedNode(): HTMLEmbedElement | undefined {
		return this.$n('emb');
	}
}