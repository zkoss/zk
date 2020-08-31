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
zul.med.Track = zk.$extends(zul.Widget, {
	$define: {
		/**
		 * Returns if this track should be enabled by default.
		 * <p>Default: false.
		 * @return boolean if this track should be enabled by default.
		 */
		/**
		 * Sets if this track should be enabled by default.
		 * @param boolean isDefault if this track should be enabled by default.
		 */
		default(isDefault: boolean) {
			const n: HTMLTrackElement = this.$n();
			if (n) n.default = isDefault;
		},
		/**
		 * Returns what kind of track it is.
		 * <p>Default: null.
		 * @return String what kind of track it is.
		 */
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
		kind(kind: string) {
			const n: HTMLTrackElement = this.$n();
			if (n) n.kind = kind;
		},
		/**
		 * Returns a readable description of this track.
		 * <p>Default: null.
		 * @return String a readable description of this track.
		 */
		/**
		 * Sets a readable description of this track.
		 * @param String label a readable description of this track.
		 */
		label(label: string) {
			const n: HTMLTrackElement = this.$n();
			if (n) n.label = label;
		},
		/**
		 * Returns the source address of this track.
		 * <p>Default: null.
		 * @return String the source address of this track.
		 */
		/**
		 * Sets the source address of this track. Must be a valid URL.
		 * This attribute must be specified.
		 * The URL must have the same origin as the parent {@code <audio>} or {@code <video>},
		 * unless the {@code crossorigin} attribute is set.
		 *
		 * @param String src the source address of this track.
		 */
		src(src: string) {
			const n: HTMLTrackElement = this.$n();
			if (n) n.src = src;
		},
		/**
		 * Returns the language of the source.
		 * <p>Default: null.
		 * @return String the language of the source.
		 */
		/**
		 * Sets the language of the source.
		 * It must be a valid BCP 47 language tag.
		 * This attribute must be specified if kind is "subtitles".
		 * @param String srclang the language of the source.
		 */
		srclang(srclang: string) {
			const n: HTMLTrackElement = this.$n();
			if (n) n.srclang = srclang;
		}
	},
	domAttrs_(): string {
		let attr = this.$supers('domAttrs_', arguments)
			+ zUtl.appendAttr('kind', this._kind)
			+ zUtl.appendAttr('label', this._label)
			+ zUtl.appendAttr('src', this._src)
			+ zUtl.appendAttr('srclang', this._srclang);
		if (this._default)
			attr += ' default="default"';
		return attr;
	}
});