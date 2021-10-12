/* Track.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 01 09:44:26 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.au.DeferredValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.ext.MediaElement;
import org.zkoss.zul.impl.XulElement;

/**
 * A representation of {@code <track>}.
 *
 * <p>It is expected to use with {@code <audio>} or {@code <video>}.
 * <p>{@code <track>} is not supported in Internet Explorer 9.
 *
 * @author rudyhuang
 * @since 9.5.0
 */
public class Track extends XulElement {
	private boolean _default;
	private String _kind;
	private String _label;
	private String _src;
	private String _srclang;

	public Track() {
	}

	/**
	 * Constructs a Track component with the specific source.
	 *
	 * @param src the source address of this track.
	 */
	public Track(String src) {
		setSrc(src);
	}

	/**
	 * Returns if this track should be enabled by default.
	 * <p>Default: false.
	 * @return if this track should be enabled by default.
	 */
	public boolean isDefault() {
		return _default;
	}

	/**
	 * Sets if this track should be enabled by default.
	 *
	 * @param isDefault if this track should be enabled by default.
	 */
	public void setDefault(boolean isDefault) {
		if (_default != isDefault) {
			_default = isDefault;
			smartUpdate("default", isDefault);
		}
	}

	/**
	 * Returns what kind of track it is.
	 * <p>Default: null.
	 * @return what kind of track it is.
	 */
	public String getKind() {
		return _kind;
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
	 * @param kind what kind of track it is.
	 */
	public void setKind(String kind) {
		if (!"subtitles".equals(kind) && !"captions".equals(kind)
				&& !"descriptions".equals(kind) && !"chapters".equals(kind) && !"metadata".equals(kind))
			throw new WrongValueException("kind cannot be " + kind);
		if (isInitialized() && "subtitles".equals(kind) && Strings.isEmpty(_srclang))
			throw new UiException("srclang cannot be null or empty while kind is subtitles");
		if (!Objects.equals(_kind, kind)) {
			_kind = kind;
			smartUpdate("kind", kind);
		}
	}

	/**
	 * Returns a readable description of this track.
	 * <p>Default: null.
	 * @return a readable description of this track.
	 */
	public String getLabel() {
		return _label;
	}

	/**
	 * Sets a readable description of this track.
	 *
	 * @param label a readable description of this track.
	 */
	public void setLabel(String label) {
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("label", label);
		}
	}

	/**
	 * Returns the source address of this track.
	 * <p>Default: null.
	 * @return the source address of this track.
	 */
	public String getSrc() {
		return _src;
	}

	/**
	 * Sets the source address of this track. Must be a valid URL.
	 * This attribute must be specified.
	 * The URL must have the same origin as the parent {@code <audio>} or {@code <video>},
	 * unless the {@code crossorigin} attribute is set.
	 *
	 * @param src the source address of this track.
	 */
	public void setSrc(String src) {
		if (Strings.isEmpty(src))
			throw new WrongValueException("src cannot be empty of null");
		if (!Objects.equals(_src, src)) {
			_src = src;
			smartUpdate("src", (DeferredValue) this::getEncodedSrc);
		}
	}

	/**
	 * Returns the language of the source.
	 * <p>Default: null.
	 * @return the language of the source.
	 */
	public String getSrclang() {
		return _srclang;
	}

	/**
	 * Sets the language of the source.
	 * It must be a valid BCP 47 language tag.
	 * This attribute must be specified if kind is "subtitles".
	 *
	 * @param srclang the language of the source.
	 */
	public void setSrclang(String srclang) {
		if (!Objects.equals(_srclang, srclang)) {
			_srclang = srclang;
			smartUpdate("srclang", srclang);
		}
	}

	@Override
	public String getZclass() {
		return _zclass != null ? _zclass : "z-track";
	}

	@Override
	protected boolean isChildable() {
		return false;
	}

	@Override
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof MediaElement))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	@Override
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);

		if (Strings.isEmpty(_src))
			throw new UiException("src must be specified.");
		if ("subtitles".equals(_kind) && Strings.isEmpty(_srclang))
			throw new UiException("srclang must be specified if kind is \"subtitles\"");
		render(renderer, "default", _default);
		render(renderer, "kind", _kind);
		render(renderer, "label", _label);
		render(renderer, "src", getEncodedSrc());
		render(renderer, "srclang", _srclang);
	}

	private String getEncodedSrc() {
		final Desktop dt = getDesktop();
		return _src != null && dt != null ? dt.getExecution().encodeURL(_src) : null;
	}
}
