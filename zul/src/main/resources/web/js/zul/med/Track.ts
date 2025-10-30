/* Track.ts

	Purpose:
		
	Description:
		
	History:
		Tue Sep 01 09:53:34 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
/**
 * A representation of `<track>`.
 *
 * <p>It is expected to use with `<audio>` or `<video>`.
 * <p>`<track>` is not supported in Internet Explorer 9.
 *
 * @author rudyhuang
 * @since 9.5.0
 */
@zk.WrapClass('zul.med.Track')
export class Track extends zul.Widget<HTMLTrackElement> {
	/** @internal */
	_default?: boolean;
	/** @internal */
	_kind?: string;
	/** @internal */
	_label?: string;
	/** @internal */
	_src?: string;
	/** @internal */
	_srclang?: string;

	/**
	 * @returns if this track should be enabled by default.
	 * @defaultValue `false`.
	 */
	isDefault(): boolean {
		return !!this._default;
	}

	/**
	 * Sets if this track should be enabled by default.
	 * @param isDefault - if this track should be enabled by default.
	 */
	// eslint-disable-next-line zk/javaStyleSetterSignature
	setDefault(isDefault: boolean, opts?: Record<string, boolean>): this {
		const o = this._default;
		this._default = isDefault;

		if (o !== isDefault || opts?.force) {
			const n = this.$n();
			if (n) n.default = isDefault;
		}

		return this;
	}

	/**
	 * @returns what kind of track it is.
	 * @defaultValue `null`.
	 */
	getKind(): string | undefined {
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
	 * @param kind - what kind of track it is.
	 */
	setKind(kind: string, opts?: Record<string, boolean>): this {
		const o = this._kind;
		this._kind = kind;

		if (o !== kind || opts?.force) {
			const n = this.$n();
			if (n) n.kind = kind;
		}

		return this;
	}

	/**
	 * @returns a readable description of this track.
	 * @defaultValue `null`.
	 */
	getLabel(): string | undefined {
		return this._label;
	}

	/**
	 * Sets a readable description of this track.
	 * @param label - a readable description of this track.
	 */
	setLabel(label: string, opts?: Record<string, boolean>): this {
		const o = this._label;
		this._label = label;

		if (o !== label || opts?.force) {
			const n = this.$n();
			if (n) n.label = label;
		}

		return this;
	}

	/**
	 * @returns the source address of this track.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the source address of this track. Must be a valid URL.
	 * This attribute must be specified.
	 * The URL must have the same origin as the parent `<audio>` or `<video>`,
	 * unless the `crossorigin` attribute is set.
	 *
	 * @param src - the source address of this track.
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			const n = this.$n();
			if (n) n.src = src;
		}

		return this;
	}

	/**
	 * @returns the language of the source.
	 * @defaultValue `null`.
	 */
	getSrclang(): string | undefined {
		return this._srclang;
	}

	/**
	 * Sets the language of the source.
	 * It must be a valid BCP 47 language tag.
	 * This attribute must be specified if kind is "subtitles".
	 * @param srclang - the language of the source.
	 */
	setSrclang(srclang: string, opts?: Record<string, boolean>): this {
		const o = this._srclang;
		this._srclang = srclang;

		if (o !== srclang || opts?.force) {
			const n = this.$n();
			if (n) n.srclang = srclang;
		}

		return this;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		let /*safe*/ attr = super.domAttrs_(no)
			+ /*safe*/ zUtl.appendAttr('kind', this._kind)
			+ /*safe*/ zUtl.appendAttr('label', this._label)
			+ /*safe*/ zUtl.appendAttr('src', this._src)
			+ /*safe*/ zUtl.appendAttr('srclang', this._srclang);
		if (this._default)
			attr += ' default="default"';
		return attr;
	}
}