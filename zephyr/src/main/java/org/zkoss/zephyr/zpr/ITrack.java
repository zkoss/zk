/* ITrack.java

	Purpose:

	Description:

	History:
		3:33 PM 2022/3/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Track;

/**
 * Immutable {@link org.zkoss.zul.Track} component
 *
 * <p>It lets you specify some timed text tracks like captions or subtitles for media components
 * such as {@link IAudio} or Video ([EE]).
 * </p>
 * @author jumperchen
 * @see Track
 */
@ZephyrStyle
public interface ITrack extends IXulElement<ITrack> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITrack DEFAULT = new ITrack.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Track> getZKType() {
		return Track.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.med.Track"}</p>
	 */
	default String getWidgetClass() {
		return "zul.med.Track";
	}

	/**
	 * Returns if this track should be enabled by default.
	 * <p>Default: {@code false}.
	 */
	default boolean isAsDefault() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code asDefault}.
	 *
	 * <p> Sets if this track should be enabled by default.
	 *
	 * @param asDefault Whether this track should be enabled by default.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITrack withAsDefault(boolean asDefault);

	/**
	 * Returns what kind of track it is.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getKind();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code kind}.
	 *
	 * <p> Sets what kind of track it is. The following keywords are accepted:
	 * <ul>
	 *     <li>subtitles</li>
	 *     <li>captions</li>
	 *     <li>descriptions</li>
	 *     <li>chapters</li>
	 *     <li>metadata</li>
	 * </ul>
	 *
	 * @param kind What kind of track it is.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITrack withKind(@Nullable String kind);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code kind}.
	 *
	 * <p> Sets what kind of track it is. The following keywords are accepted:
	 * <ul>
	 *     <li>subtitles</li>
	 *     <li>captions</li>
	 *     <li>descriptions</li>
	 *     <li>chapters</li>
	 *     <li>metadata</li>
	 * </ul>
	 *
	 * @param kind What kind of track it is.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITrack withKind(@Nullable Kind kind) {
		if (kind == null)
			return withKind((String) null);
		return withKind(kind.value);
	}

	/**
	 * Returns a readable description of this track.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getLabel();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code label}.
	 *
	 * <p> Sets a readable description of this track.
	 *
	 * @param label A readable description of this track.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITrack withLabel(@Nullable String label);

	/**
	 * Returns the source address of this track.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code srclang}.
	 *
	 * <p> Sets the source address of this track. Must be a valid URL.
	 * This attribute must be specified.
	 * The URL must have the same origin as the parent {@code <audio>} or {@code <video>},
	 * unless the {@code crossorigin} attribute is set.
	 *
	 * @param src The source address of this track.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITrack withSrc(@Nullable String src);

	/**
	 * Returns the language of the source.
	 * <p>Default: {@code null}.
	 * @return the language of the source.
	 */
	@Nullable
	String getSrclang();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code srclang}.
	 *
	 * <p> Sets the language of the source.
	 *
	 * @param srclang The language of the source.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITrack withSrclang(@Nullable String srclang);

	/**
	 * Returns the instance with the given label.
	 * @param label The label of the component.
	 */
	static ITrack of(String label) {
		return new ITrack.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given src.
	 * @param src The src of the component.
	 */
	static ITrack ofSrc(String src) {
		return new ITrack.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given kind.
	 * @param kind  What kind of track it is.
	 */
	static ITrack ofKind(Kind kind) {
		return new ITrack.Builder().setKind(kind.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITrack ofId(String id) {
		return new ITrack.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		if (Strings.isEmpty(getSrc()))
			throw new UiException("src must be specified.");
		if ("subtitles".equals(getKind()) && Strings.isEmpty(getSrclang()))
			throw new UiException("srclang must be specified if kind is \"subtitles\"");

		String src = getSrc();
		render(renderer, "default", isAsDefault());
		render(renderer, "kind", getKind());
		render(renderer, "label", getLabel());
		render(renderer, "src", src != null ? Executions.getCurrent().encodeURL(src) : null);
		render(renderer, "srclang", getSrclang());
	}

	/**
	 * Builds instances of type {@link ITrack ITrack}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITrack.Builder {}

	/**
	 * Builds an updater of type {@link ITrack} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITrackUpdater {}

	/**
	 * Specifies the kind with {@link #withKind(Kind)}
	 */
	enum Kind {
		/**
		 * The track defines subtitles, used to display subtitles in a video
		 */
		SUBTITLES("subtitles"),
		/**
		 * The track defines translation of dialogue and sound effects (suitable for deaf users)
		 */
		CAPTIONS("captions"),
		/**
		 * The track defines a textual description of the video content (suitable for blind users)
		 */
		DESCRIPTIONS("descriptions"),
		/**
		 * The track defines chapter titles (suitable for navigating the media resource)
		 */
		CHAPTERS("chapters"),
		/**
		 * The track defines content used by scripts. Not visible for the user
		 */
		METADATA("metadata");
		final String value;
		Kind(String value) {
			this.value = value;
		}
	}
}
