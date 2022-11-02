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
	/** @internal */
	_src?: string;
	/** @internal */
	_autoplay?: boolean;
	/** @internal */
	_preload?: string;
	/** @internal */
	_controls?: boolean;
	/** @internal */
	_loop?: boolean;
	/** @internal */
	_muted?: boolean;
	/** @internal */
	_isUnbinded?: boolean;

	/**
	 * @returns the src.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the src.
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			this.rerender();
		}

		return this;
	}

	// for stateless to treat as "src" attribute at client side
	getContent(): string | undefined {
		return this.getSrc();
	}

	// for stateless to treat as "src" attribute at client side
	setContent(content: string, opts?: Record<string, boolean>): this {
		return this.setSrc(content, opts);
	}

	/**
	 * @returns whether to auto start playing the audio.
	 * @defaultValue `false`.
	 */
	isAutoplay(): boolean {
		return !!this._autoplay;
	}

	/**
	 * Sets whether to auto start playing the audio.
	 */
	setAutoplay(autoplay: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoplay;
		this._autoplay = autoplay;

		if (o !== autoplay || opts?.force) {
			var n = this.$n();
			if (n) n.autoplay = autoplay;
		}

		return this;
	}

	/**
	 * @returns whether and how the audio should be loaded.
	 * @defaultValue `null`.
	 * @since 7.0.0
	 */
	getPreload(): string | undefined {
		return this._preload;
	}

	/**
	 * Sets whether and how the audio should be loaded.
	 * Refer to <a href="http://www.w3.org/TR/html5/embedded-content-0.html#attr-media-preload">Preload Attribute Description</a> for details.
	 * @since 7.0.0
	 */
	setPreload(preload: 'none' | 'metadata' | 'auto' | '', opts?: Record<string, boolean>): this {
		const o = this._preload;
		this._preload = preload;

		if (o !== preload || opts?.force) {
			var n = this.$n();
			if (n && preload !== undefined) n.preload = preload;
		}

		return this;
	}

	/**
	 * @returns whether to display the audio controls.
	 * @defaultValue `false`.
	 * @since 7.0.0
	 */
	isControls(): boolean {
		return !!this._controls;
	}

	/**
	 * Sets whether to display the audio controls.
	 * @since 7.0.0
	 */
	setControls(controls: boolean, opts?: Record<string, boolean>): this {
		const o = this._controls;
		this._controls = controls;

		if (o !== controls || opts?.force) {
			var n = this.$n();
			if (n) n.controls = controls;
		}

		return this;
	}

	/**
	 * @returns whether to play the audio repeatedly.
	 * @defaultValue `false`.
	 */
	isLoop(): boolean {
		return !!this._loop;
	}

	/**
	 * Sets whether to play the audio repeatedly.
	 */
	setLoop(loop: boolean, opts?: Record<string, boolean>): this {
		const o = this._loop;
		this._loop = loop;

		if (o !== loop || opts?.force) {
			var n = this.$n();
			if (n) n.loop = loop;
		}

		return this;
	}

	/**
	 * @returns whether to mute the audio.
	 * @defaultValue `false`.
	 * @since 7.0.0
	 */
	isMuted(): boolean {
		return !!this._muted;
	}

	/**
	 * Sets whether to mute the audio.
	 * @since 7.0.0
	 */
	setMuted(muted: boolean, opts?: Record<string, boolean>): this {
		const o = this._muted;
		this._muted = muted;

		if (o !== muted || opts?.force) {
			var n = this.$n();
			if (n) n.muted = muted;
		}

		return this;
	}

	/**
	 * Plays the audio at the client.
	 */
	play(): void {
		_invoke(this, 'play');
	}

	/**
	 * Stops the audio at the client.
	 */
	stop(): void {
		_invoke(this, 'stop');
	}

	/**
	 * Pauses the audio at the client.
	 */
	pause(): void {
		_invoke(this, 'pause');
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		const n = this.$n_();
		n.addEventListener('play', this.proxy(this._audioOnPlay));
		n.addEventListener('pause', this.proxy(this._audioOnPause));
		n.addEventListener('ended', this.proxy(this._audioOnEnded));
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._isUnbinded = true;
		this.stop();
		const n = this.$n_();
		n.removeEventListener('ended', this.proxy(this._audioOnEnded));
		n.removeEventListener('pause', this.proxy(this._audioOnPause));
		n.removeEventListener('play', this.proxy(this._audioOnPlay));
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
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

	/** @internal */
	domContent_(): string {
		var src = this._src!,
			length = src.length,
			result = '';
		for (var i = 0; i < length; i++) {
			result += '<source src="' + src[i] + '" type="' + this._MIMEtype(src[i]) + '">';
		}
		return result;
	}

	/** @internal */
	_audioOnPlay(): void {
		this._fireOnStateChange(_PLAY);
	}

	/** @internal */
	_audioOnPause(): void {
		if (this.$n_().currentTime) {
			this._fireOnStateChange(_PAUSE);
		}
	}

	/** @internal */
	_audioOnEnded(): void {
		this._fireOnStateChange(_ENDED);
	}

	/** @internal */
	_fireOnStateChange(state: number): void {
		this.fire('onStateChange', {state: state});
	}

	/** @internal */
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
	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (!(child instanceof zul.med.Track)) {
			zk.error('Unsupported child for audio: ' + child.className);
			return false;
		}
		return true;
	}
}