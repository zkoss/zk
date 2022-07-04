/* Track.ts

	Purpose:
		
	Description:
		
	History:
		Tue Sep 01 09:53:34 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
/**
 * A representation of {@code <track>}.
 *
 * <p>It is expected to use with {@code <audio>} or {@code <video>}.
 * <p>{@code <track>} is not supported in Internet Explorer 9.
 *
 * @author rudyhuang
 * @since 9.5.0
 */
@zk.WrapClass('zul.med.Track')
export class Track extends zul.Widget {
	private _default?: boolean;
	private _kind?: string;
	private _label?: string;
	private _src?: string;
	private _srclang?: string;

	/**
	 * Returns if this track should be enabled by default.
	 * <p>Default: false.
	 * @return boolean if this track should be enabled by default.
	 */
	public isDefault(): boolean | undefined {
		return this._default;
	}

	/**
	 * Sets if this track should be enabled by default.
	 * @param boolean isDefault if this track should be enabled by default.
	 */
	public setDefault(isDefault: boolean, opts?: Record<string, boolean>): this {
		const o = this._default;
		this._default = isDefault;

		if (o !== isDefault || (opts && opts.force)) {
			const n = this.$n() as HTMLTrackElement;
			if (n) n.default = isDefault;
		}

		return this;
	}

	/**
	 * Returns what kind of track it is.
	 * <p>Default: null.
	 * @return String what kind of track it is.
	 */
	public getKind(): string | undefined {
		return this._kind;
	}

	/**
	 * Set what kind of track it is. The following keywords are accepted:
	 * <ul>
	 *     <li>subtitles</li>
	 *     <li>captions</li>
	 *     <li>descriptions</li>
	 *     <li>chapters</li>
	 *     <li>metadata</li>
	 * </ul>
	 * @param String kind what kind of track it is.
	 */
	public setKind(kind: string, opts?: Record<string, boolean>): this {
		const o = this._kind;
		this._kind = kind;

		if (o !== kind || (opts && opts.force)) {
			const n = this.$n() as HTMLTrackElement;
			if (n) n.kind = kind;
		}

		return this;
	}

	/**
	 * Returns a readable description of this track.
	 * <p>Default: null.
	 * @return String a readable description of this track.
	 */
	public getLabel(): string | undefined {
		return this._label;
	}

	/**
	 * Sets a readable description of this track.
	 * @param String label a readable description of this track.
	 */
	public setLabel(label: string, opts?: Record<string, boolean>): this {
		const o = this._label;
		this._label = label;

		if (o !== label || (opts && opts.force)) {
			const n = this.$n() as HTMLTrackElement;
			if (n) n.label = label;
		}

		return this;
	}

	/**
	 * Returns the source address of this track.
	 * <p>Default: null.
	 * @return String the source address of this track.
	 */
	public getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the source address of this track. Must be a valid URL.
	 * This attribute must be specified.
	 * The URL must have the same origin as the parent {@code <audio>} or {@code <video>},
	 * unless the {@code crossorigin} attribute is set.
	 *
	 * @param String src the source address of this track.
	 */
	public setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || (opts && opts.force)) {
			const n = this.$n() as HTMLTrackElement;
			if (n) n.src = src;
		}

		return this;
	}

	/**
	 * Returns the language of the source.
	 * <p>Default: null.
	 * @return String the language of the source.
	 */
	public getSrclang(): string | undefined {
		return this._srclang;
	}

	/**
	 * Sets the language of the source.
	 * It must be a valid BCP 47 language tag.
	 * This attribute must be specified if kind is "subtitles".
	 * @param String srclang the language of the source.
	 */
	public setSrclang(srclang: string, opts?: Record<string, boolean>): this {
		const o = this._srclang;
		this._srclang = srclang;

		if (o !== srclang || (opts && opts.force)) {
			const n = this.$n() as HTMLTrackElement;
			if (n) n.srclang = srclang;
		}

		return this;
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		let attr = super.domAttrs_(no)
			+ zUtl.appendAttr('kind', this._kind)
			+ zUtl.appendAttr('label', this._label)
			+ zUtl.appendAttr('src', this._src)
			+ zUtl.appendAttr('srclang', this._srclang);
		if (this._default)
			attr += ' default="default"';
		return attr;
	}
}