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
	/** @internal */
	_wmode = 'transparent';
	/** @internal */
	_quality = 'high';
	/** @internal */
	_autoplay = true;
	/** @internal */
	_loop = false;
	/** @internal */
	_version = '6,0,0,0';
	/** @internal */
	_src?: string;
	/** @internal */
	_bgcolor?: string;

	/**
	 * @returns the expected version of the Flash player.
	 * @defaultValue `"6,0,0,0"`
	 */
	getVersion(): string {
		return this._version;
	}

	/**
	 * Sets the expected version of the Flash player.
	 */
	setVersion(version: string, opts?: Record<string, boolean>): this {
		const o = this._version;
		this._version = version;

		if (o !== version || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Gets the source path of Flash movie
	 * @returns the source path of Flash movie
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the source path of Flash movie and redraw the component
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

	/**
	 * @returns the Window mode property of the Flash movie
	 * @defaultValue `"transparent"`.
	 */
	getWmode(): string {
		return this._wmode;
	}

	/**
	 * Sets the Window Mode property of the Flash movie
	 * for transparency, layering, and positioning in the browser.
	 * @param wmode - Possible values: window, opaque, transparent.
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

	/**
	 * Gets the background color of Flash movie.
	 * @defaultValue `null` (the system default)
	 * @returns the background color of Flash movie,[ hexadecimal RGB value]
	 */
	getBgcolor(): string | undefined {
		return this._bgcolor;
	}

	/**
	 * Sets the background color of Flash movie.
	 * @param bgcolor - [ hexadecimal RGB value]
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

	/**
	 * @returns the quality of the Flash movie
	 * @defaultValue `"high"`.
	 */
	getQuality(): string {
		return this._quality;
	}

	/**
	 * Sets the quality of the Flash movie.
	 * @param quality - the quality of the Flash movie.
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

	/**
	 * @returns true if the Flash movie starts playing automatically
	 * @defaultValue `true`
	 */
	isAutoplay(): boolean {
		return this._autoplay;
	}

	/**
	 * Sets wether to play the Flash movie automatically.
	 * @param autoplay - whether to play the Flash movie automatically
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

	/**
	 * @returns true if the Flash movie plays repeatly.
	 * @defaultValue `false`
	 */
	isLoop(): boolean {
		return this._loop;
	}

	/**
	 * Sets whether the Flash movie plays repeatly
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

	/** @internal */
	_embedNode(): HTMLEmbedElement | undefined {
		return this.$n('emb');
	}
}