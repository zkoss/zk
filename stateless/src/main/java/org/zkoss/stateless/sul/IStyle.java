/* IStyle.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:32:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Style;

/**
 * Immutable {@link Style} component
 *
 * <p>The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a page, but it
 * affects all components in the same desktop.
 *
 * <p>There are two formats when used in a page:
 *
 * <p>Method 1: Specify the URL of the CSS file
 * <pre><code>IStyle.ofSrc("my.css");</code></pre>
 *
 * <p>Method 2: Specify the CSS content directly
 * <pre><code>IStyle.of(".mycls {border: 1px outset #777;}");</code></pre>
 *
 * <p>Note: if the {@code src} and {@code content} attributes are both set, the {@code content}
 * has higher priority.
 *
 * @author katherine
 * @see Style
 */
@StatelessStyle
public interface IStyle extends IAnyGroup<IStyle> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IStyle DEFAULT = new IStyle.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Style> getZKType() {
		return Style.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.utl.Style"}</p>
	 */
	default String getWidgetClass() {
		return "zul.utl.Style";
	}

	/** Returns the media dependencies for this style sheet.
	 *
	 * <p>Default: {@code null}.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 */
	@Nullable
	String getMedia();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code media}.
	 *
	 * <p> Sets the media dependencies for this style sheet.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 *
	 * @param media The media dependencies.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IStyle withMedia(@Nullable String media);

	/** Returns the content of the style element.
	 * By content we mean the CSS rules that will be sent to the client.
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getContent();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code content}.
	 *
	 * <p>Sets the content of the style element.
	 * By content we mean the CSS rules that will be sent to the client.
	 * <b>Note:</b> if calling this with {@link #withSrc(String)}, the {@link #withContent(String)}
	 * has higher priority.
	 * @param content The CSS content.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IStyle withContent(@Nullable String content);

	/** Returns the URI of an external style sheet.
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code src}.
	 *
	 * <p>Sets the URI of an external style sheet.
	 * <b>Note:</b> if calling this with {@link #withContent(String)}, the {@link #withContent(String)}
	 * has higher priority.
	 * @param src The style URI.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IStyle withSrc(@Nullable String src);

	/**
	 * Returns false by default.
	 */
	default boolean isVisible() {
		return false;
	}

	/**
	 * Returns the instance with the given content.
	 * @param content The style content.
	 */
	static IStyle of(String content) {
		return new IStyle.Builder().setContent(content).build();
	}

	/**
	 * Returns the instance with the given src.
	 * @param src The style URI.
	 */
	static IStyle ofSrc(String src) {
		return new IStyle.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IStyle ofId(String id) {
		return new IStyle.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IAnyGroup.super.renderProperties(renderer);

		render(renderer, "media", getMedia());
		final String cnt = getContent();
		if (cnt != null) {
			render(renderer, "content", cnt);
		} else {
			String src = getSrc();
			if (Strings.isBlank(src)) {
				render(renderer, "src", "");
			} else {
				render(renderer, "src",
						Executions.getCurrent().encodeURL(getSrc()));
			}
		}
	}

	/**
	 * Builds instances of type {@link IStyle IStyle}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIStyle.Builder {
	}

	/**
	 * Builds an updater of type {@link IStyle} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IStyleUpdater {}
}