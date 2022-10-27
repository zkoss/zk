/* IAudio.java

	Purpose:

	Description:

	History:
		Thu Oct 07 12:29:11 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.StateChangeData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Audio;

/**
 * Immutable {@link Audio} component
 * <p> An audio clip.
 * </p>
 *
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onStateChange</td>
 *          <td><strong>ActionData</strong>: {@link StateChangeData}
 *          <br> Notifies when invoking play(), stop(), pause() or the audio is played to the end.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author katherine
 * @see Audio
 */
@StatelessStyle
public interface IAudio extends IXulElement<IAudio>, IAnyGroup<IAudio>,
		IChildable<IAudio, ITrack> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IAudio DEFAULT = new IAudio.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Audio> getZKType() {
		return Audio.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.med.Audio"}</p>
	 */
	default String getWidgetClass() {
		return "zul.med.Audio";
	}

	/** Returns the src.
	 * <p>Default: [].
	 */
	List<String> getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code placeholder}.
	 *
	 * <p> Sets the src of the audio.
	 *
	 * <b>Note:</b> If calling this with {@link #withContent(org.zkoss.sound.Audio)},
	 * the {@link #withContent(org.zkoss.sound.Audio)}
	 * has higher priority
	 *
	 * @param src The src of the component
	 * <p>Default: {@code []}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withSrc(Iterable<String> src);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code placeholder}.
	 *
	 * <p> Sets the src of the audio.
	 *
	 * <p>
	 * <b>Note:</b> If calling this with {@link #withContent(org.zkoss.sound.Audio)},
	 * the {@link #withContent(org.zkoss.sound.Audio)}
	 * has higher priority
	 *
	 * @param src The src of the component
	 * <p>Default: {@code []}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IAudio withSrc(String src) {
		List<String> list = new ArrayList<String>();
		if (src.contains(",")) {
			list = new ArrayList<String>(Arrays.asList(src.split("\\s*,\\s*")));
		} else {
			list.add(src.trim());
		}
		return withSrc(list);
	}

	/** Returns whether and how the audio should be loaded.
	 * "none", "metadata", "auto" or null
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getPreload();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code preload}.
	 *
	 * <p> Sets whether and how the audio should be loaded.
	 * Refer to <a href="http://www.w3.org/TR/html5/embedded-content-0.html#attr-media-preload">Preload Attribute Description</a> for details.
	 *
	 * @param preload Whether and how the audio should be loaded. ("none", "metadata", "auto" or null)
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withPreload(@Nullable String preload);

	/** Returns the content set by {@link #withContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #withSrc}.
	 * It simply returns what is passed to {@link #withContent}.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	org.zkoss.sound.Audio getContent();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code audio}.
	 *
	 * <p> Sets the content of the audio
	 * <p>
	 * <b>Note:</b> If calling this with {@link #withSrc},
	 * the {@link #withContent(org.zkoss.sound.Audio)}
	 * has higher priority
	 *
	 * @param audio The content of the audio.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withContent(@Nullable org.zkoss.sound.Audio audio);

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isAutoplay() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autoplay}.
	 *
	 * <p> Sets whether to auto start playing the audio.
	 *
	 * @param autoplay Whether to auto start playing the audio.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withAutoplay(boolean autoplay);

	/** Returns whether to display the audio controls.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isControls() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code controls}.
	 *
	 * <p> Sets whether to display the audio controls.
	 *
	 * @param controls Whether to display the audio controls.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withControls(boolean controls);

	/** Returns whether to play the audio repeatedly.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isLoop() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code loop}.
	 *
	 * <p> Sets whether to play the audio repeatedly.
	 *
	 * @param loop Whether to play the audio repeatedly.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withLoop(boolean loop);

	/** Returns whether to mute the audio.
	 *
	 * <p>Default: {@code false}.
	 */
	default boolean isMuted() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code muted}.
	 *
	 * <p> Sets whether to mute the audio.
	 *
	 * @param muted Whether to mute the audio.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAudio withMuted(boolean muted);

	/**
	 * Returns the instance with the given {@code src}.
	 * @param src The URI of the audio source.
	 */
	static IAudio of(String src) {
		List<String> srcList = new ArrayList<String>();
		if (src.contains(",")) {
			srcList = new ArrayList<String>(Arrays.asList(src.split("\\s*,\\s*")));
		} else {
			srcList.add(src.trim());
		}
		return new IAudio.Builder().setSrc(srcList).build();
	}

	/**
	 * Returns the instance with the given {@link ITrack} children.
	 * @param children The children of {@link ITrack}
	 */
	static IAudio of(Iterable<ITrack> children) {
		return new IAudio.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link ITrack} children.
	 * @param children The children of {@link ITrack}
	 */
	static IAudio of(ITrack... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IAudio ofId(String id) {
		return new IAudio.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "src", IAudioCtrl.getEncodedSrc(this));
		render(renderer, "autoplay", isAutoplay());
		render(renderer, "preload", getPreload());
		render(renderer, "controls", isControls());
		render(renderer, "loop", isLoop());
		render(renderer, "muted", isMuted());
	}

	/**
	 * Builds instances of type {@link IAudio IAudio}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIAudio.Builder {}

	/**
	 * Builds an updater of type {@link IAudio} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IAudioUpdater {}
}