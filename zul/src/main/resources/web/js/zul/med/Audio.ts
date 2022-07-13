/* Audio.ts

	Purpose:

	Description:

	History:
		Thu Mar 26 11:59:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _STOP = 0,
	_PLAY = 1,
	_PAUSE = 2,
	_ENDED = 3;

function _invoke(wgt: zul.med.Audio, fn: 'pause' | 'play' | 'stop'): void {
	// Note: setSrc will rerender, so we need to delay the invocation of play
	if (wgt._isUnbinded)
		_invoke2(wgt, fn);
	else
		setTimeout(function () {
			_invoke2(wgt, fn);
		}, 200);
}
function _invoke2(wgt: zul.med.Audio, fn: 'pause' | 'play' | 'stop'): void {
	var n = wgt.$n();
	if (n) {
		try {
			if (fn === 'stop') {
				n.pause();
				n.currentTime = 0;
				wgt._fireOnStateChange(_STOP);
			} else
				void n[fn]();
		} catch (e) {
			// Do not show alert if the browser did not support the source format.
			/* if (!wgt._isUnbinded)
				jq.alert(msgzul.NO_AUDIO_SUPPORT + '\n' + e.message); */
			zk.debugLog((e as Error).message || e as string);
		}
	}
}

/**
 * An audio clip.
 *
 * <p>An extension to XUL.
 * Only works for browsers supporting HTML5 audio tag (since ZK 7.0.0).
 */
@zk.WrapClass('zul.med.Audio')
export class Audio extends zul.Widget<HTMLAudioElement> {
	_src?: string;
	_autoplay?: boolean;
	_preload?: string;
	_controls?: boolean;
	_loop?: boolean;
	_muted?: boolean;
	_isUnbinded?: boolean;

	/** Returns the src.
	 * <p>Default: null.
	 * @return String
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/** Sets the src.
	 * @param String src
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	// for zephyr to treat as "src" attribute at client side
	getContent(): string | undefined {
		return this.getSrc();
	}

	// for zephyr to treat as "src" attribute at client side
	setContent(src: string, opts?: Record<string, boolean>): this {
		return this.setSrc(src, opts);
	}

	/** Returns whether to auto start playing the audio.
	 * <p>Default: false.
	 * @return boolean
	 */
	isAutoplay(): boolean | undefined {
		return this._autoplay;
	}

	/** Sets whether to auto start playing the audio.
	 * @param boolean autoplay
	 */
	setAutoplay(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoplay;
		this._autoplay = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.autoplay = v;
		}

		return this;
	}

	/** Returns whether and how the audio should be loaded.
	 *
	 * <p>Default: null.
	 * @return String
	 * @since 7.0.0
	 */
	getPreload(): string | undefined {
		return this._preload;
	}

	/** Sets whether and how the audio should be loaded.
	 * Refer to <a href="http://www.w3.org/TR/html5/embedded-content-0.html#attr-media-preload">Preload Attribute Description</a> for details.
	 * @param String preload
	 * @since 7.0.0
	 */
	setPreload(v: 'none' | 'metadata' | 'auto' | '', opts?: Record<string, boolean>): this {
		const o = this._preload;
		this._preload = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n && v !== undefined) n.preload = v;
		}

		return this;
	}

	/** Returns whether to display the audio controls.
	 *
	 * <p>Default: false.
	 * @return boolean
	 * @since 7.0.0
	 */
	isControls(): boolean | undefined {
		return this._controls;
	}

	/** Sets whether to display the audio controls.
	 * @param boolean controls
	 * @since 7.0.0
	 */
	setControls(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._controls;
		this._controls = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.controls = v;
		}

		return this;
	}

	/** Returns whether to play the audio repeatedly.
	 * <p>Default: false.
	 * @return boolean
	 */
	isLoop(): boolean | undefined {
		return this._loop;
	}

	/** Sets whether to play the audio repeatedly.
	 * @param boolean loop
	 */
	setLoop(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._loop;
		this._loop = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.loop = v;
		}

		return this;
	}

	/** Returns whether to mute the audio.
	 *
	 * <p>Default: false.
	 * @return boolean
	 * @since 7.0.0
	 */
	isMuted(): boolean | undefined {
		return this._muted;
	}

	/** Sets whether to mute the audio.
	 * @param boolean muted
	 * @since 7.0.0
	 */
	setMuted(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._muted;
		this._muted = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.muted = v;
		}

		return this;
	}

	/** Plays the audio at the client.
	 */
	play(): void {
		_invoke(this, 'play');
	}

	/** Stops the audio at the client.
	 */
	stop(): void {
		_invoke(this, 'stop');
	}

	/** Pauses the audio at the client.
	 */
	pause(): void {
		_invoke(this, 'pause');
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		const n = this.$n_();
		n.addEventListener('play', this.proxy(this._audioOnPlay));
		n.addEventListener('pause', this.proxy(this._audioOnPause));
		n.addEventListener('ended', this.proxy(this._audioOnEnded));
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		this._isUnbinded = true;
		this.stop();
		const n = this.$n_();
		n.removeEventListener('ended', this.proxy(this._audioOnEnded));
		n.removeEventListener('pause', this.proxy(this._audioOnPause));
		n.removeEventListener('play', this.proxy(this._audioOnPlay));
		super.unbind_(skipper, after, keepRod);
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
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
	}

	domContent_(): string {
		var src = this._src!,
			length = src.length,
			result = '';
		for (var i = 0; i < length; i++) {
			result += '<source src="' + src[i] + '" type="' + this._MIMEtype(src[i]) + '">';
		}
		return result;
	}

	_audioOnPlay(): void {
		this._fireOnStateChange(_PLAY);
	}

	_audioOnPause(): void {
		if (this.$n_().currentTime) {
			this._fireOnStateChange(_PAUSE);
		}
	}

	_audioOnEnded(): void {
		this._fireOnStateChange(_ENDED);
	}

	_fireOnStateChange(state: number): void {
		this.fire('onStateChange', {state: state});
	}

	_MIMEtype(name: string): string {
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
}