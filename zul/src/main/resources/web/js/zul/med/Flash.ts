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
	private _wmode = 'transparent';
	private _quality = 'high';
	private _autoplay = true;
	private _loop = false;
	private _version = '6,0,0,0';
	private _src?: string;
	private _bgcolor?: string;

	/** Returns the expected version of the Flash player.
	 * <p>Default: "6,0,0,0"
	 * @return String
	 */
	public getVersion(): string {
		return this._version;
	}

	/** Sets the expected version of the Flash player.
	 * @param String version
	 */
	public setVersion(version: string, opts?: Record<string, boolean>): this {
		const o = this._version;
		this._version = version;

		if (o !== version || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Gets the source path of Flash movie
	 * @return  String the source path of Flash movie
	 */
	public getSrc(): string | undefined {
		return this._src;
	}

	/** Sets the source path of Flash movie
	 * and redraw the component
	 * @param String src
	 */
	public setSrc(v: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = v;

		if (o !== v || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.movie = n.src = v || ''; // Use of non-standard attribute `movie`
		}

		return this;
	}

	/** Returns the Window mode property of the Flash movie
	 * <p>Default: "transparent".
	 * @return String the Window mode property of the Flash movie
	 */
	public getWmode(): string {
		return this._wmode;
	}

	/** Sets the Window Mode property of the Flash movie
	 * for transparency, layering, and positioning in the browser.
	 * @param String wmode Possible values: window, opaque, transparent.
	 */
	public setWmode(wmode: string, opts?: Record<string, boolean>): this {
		const o = this._wmode;
		this._wmode = wmode;

		if (o !== wmode || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.wmode = wmode || ''; // Use of non-standard attribute `wmode`
		}

		return this;
	}

	/** Gets the background color of Flash movie.
	 * <p>Default: null (the system default)
	 * @return String the background color of Flash movie,[ hexadecimal RGB value]
	 */
	public getBgcolor(): string | undefined {
		return this._bgcolor;
	}

	/** Sets the background color of Flash movie.
	 * @param String bgcolor [ hexadecimal RGB value]
	 */
	public setBgcolor(v: string, opts?: Record<string, boolean>): this {
		const o = this._bgcolor;
		this._bgcolor = v;

		if (o !== v || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.bgcolor = v || ''; // Use of non-standard attribute `bgcolor`
		}

		return this;
	}

	/** Returns the quality of the Flash movie
	 * <p>Default: "high".
	 * @return String the quality of the Flash movie
	 */
	public getQuality(): string {
		return this._quality;
	}

	/** Sets the quality of the Flash movie.
	 * @param String quality the quality of the Flash movie.
	 */
	public setQuality(v: string, opts?: Record<string, boolean>): this {
		const o = this._quality;
		this._quality = v;

		if (o !== v || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.quality = v || ''; // Use of non-standard attribute `quality`
		}

		return this;
	}

	/** Return true if the Flash movie starts playing automatically
	 * <p>Default: true
	 * @return boolean true if the Flash movie starts playing automatically
	 */
	public isAutoplay(): boolean {
		return this._autoplay;
	}

	/** Sets wether to play the Flash movie automatically.
	 * @param boolean autoplay whether to play the Flash movie automatically
	 */
	public setAutoplay(autoplay: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoplay;
		this._autoplay = autoplay;

		if (o !== autoplay || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.autoplay = autoplay || ''; // Use of non-standard attribute `autoplay`
		}

		return this;
	}

	/** Returns true if the Flash movie plays repeatly.
	 * <p>Default: false
	 * @return boolean true if the Flash movie plays repeatly
	 */
	public isLoop(): boolean {
		return this._loop;
	}

	/** Sets whether the Flash movie plays repeatly
	 * @param boolean loop
	 */
	public setLoop(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._loop;
		this._loop = v;

		if (o !== v || (opts && opts.force)) {
			var n = this._embedNode();
			if (n) n.loop = v || ''; // Use of non-standard attribute `loop`
		}

		return this;
	}

	protected override doMouseDown_(e: zk.Event): void {
		// Bug 3306281
		if (zk.ie < 11)
			this.fire('onClick', e.data, e.opts);
		super.doMouseDown_(e);
	}

	public override setHeight(height: string | null): void {
		this._height = height;
		var n = this._embedNode();
		if (n) n.height = height ? height : '';
	}

	public override setWidth(width: string | null): void {
		this._width = width;
		var n = this._embedNode();
		if (n) n.width = width ? width : '';
	}

	private _embedNode(): HTMLEmbedElement | null | undefined {
		return this.$n('emb');
	}
}