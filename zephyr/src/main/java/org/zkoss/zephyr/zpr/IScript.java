/* IScript.java

	Purpose:

	Description:

	History:
		Fri Feb 25 09:58:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.util.ZephyrContentRenderer;
import org.zkoss.zk.ui.sys.ComponentRedraws;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zul.Script;

/**
 * Immutable {@link Script} component
 * <p>
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag, except the defer option ({@link #withDefer})
 * will cause the evaluation of JavaScript until the widget has been
 * instantiated and mounted to the DOM tree.
 * </p>
 * @author katherine
 * @see Script
 */
@ZephyrStyle
public interface IScript extends IComponent<IScript> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IScript DEFAULT = new IScript.Builder().build();

	/**
	 * Internal use
	 *
	 * @return
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Script> getZKType() {
		return Script.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.utl.Script"}</p>
	 *
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.utl.Script";
	}

	/** Returns the character encoding of the source.
	 * It is used with {@link #getSrc}.
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getCharset();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code charset}.
	 *
	 * <p>Sets the character encoding of the source.
	 * It is used with {@link #withSrc}.
	 * <p>Refer to <a href="http://www.w3schools.com/TAGS/ref_charactersets.asp">HTML Character Sets</a>for more information.
	 *
	 * @param charset The character encoding of the source.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IScript withCharset(@Nullable String charset);

	/** Returns the URI of the source that contains the script codes.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code charset}.
	 *
	 * <p> Sets the URI of the source that contains the script codes.
	 *
	 * @param charset The character encoding of the source.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IScript withSrc(@Nullable String charset);

	/** Returns whether to defer the execution of the script codes
	 * until the widget is instantiated and mounted.
	 *
	 * <p>Default: {@code false}.
	 *
	 * <p>Specifying false (default), if you want to do the desktop-level
	 * (or class-level) initialization, such as defining a widget class
	 * or a global function.
	 * <p>Specifying true, if you want to access widgets. Notice that
	 * <code>this</code> references to this script widget.
	 */
	default boolean isDefer() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code defer}.
	 *
	 * <p>  Sets whether to defer the execution of the script codes.
	 *
	 * @param defer Whether to defer the execution of the script codes.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IScript withDefer(boolean defer);

	/** Returns the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getContent();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code content}.
	 *
	 * <p> Sets the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * @param content The content of the script element.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IScript withContent(@Nullable String content);

	/** Returns the list of packages to load before evaluating the script
	 * defined in {@link #getContent}.
	 * It is meaning only if {@link #getContent} not null.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getPackages();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code packages}.
	 *
	 * <p> Sets the list of packages to load before evaluating the script
	 * defined in {@link #getContent}.
	 * If more than a package to load, separate them with comma.
	 *
	 * @param packages The list of packages to load before evaluating the script.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IScript withPackages(@Nullable String packages);

	/**
	 * Returns the instance with the given content.
	 * @param content The script content.
	 */
	static  IScript of(String content) {
		return new IScript.Builder().setContent(content).build();
	}

	/**
	 * Returns the instance with the given src.
	 * @param src The script URI.
	 */
	static  IScript ofSrc(String src) {
		return new IScript.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static  IScript ofId(String id) {
		return new IScript.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IComponent.super.renderProperties(renderer);

		final String cnt = getContent();
		boolean _defer = isDefer();
		//allow deriving to override getContent()
		if (cnt != null)
			if (_defer)
				renderer.renderDirectly("content", "function(){\n" + cnt + "\n}");
			else {
				Writer out = ComponentRedraws.getScriptBuffer();

				// B65-ZK-1836
				out.write(cnt.replaceAll("</(?i)(?=script>)", "<\\\\/"));
				out.write('\n');
			}
		String _src = getSrc();
		String _charset = getCharset();
		if (_src != null) {
			final HtmlPageRenders.RenderContext rc = _defer ? null : HtmlPageRenders.getRenderContext(null);
			if (rc != null && rc.perm != null) {
				final Writer cwout = rc.perm;
				cwout.write("\n<script id=\"");

				if (renderer instanceof ZephyrContentRenderer) {
					final ZephyrContentRenderer zcr = (ZephyrContentRenderer) renderer;
					cwout.write(zcr.getBinding().getUuid());
					cwout.write("\" type=\"text/javascript\" src=\"");
					cwout.write(IScriptCtrl.getEncodedSrcURL(_src));
					cwout.write('"');
					if (_charset != null) {
						cwout.write(" charset=\"");
						cwout.write(_charset);
						cwout.write('"');
					}
					cwout.write(">\n</script>\n");
				}
			} else
				render(renderer, "src", IScriptCtrl.getEncodedSrcURL(_src));
		}

		render(renderer, "charset", _charset);
		render(renderer, "packages", getPackages());
	}

	/**
	 * Builds instances of type {@link IScript IScript}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIScript.Builder {
	}

	/**
	 * Builds an updater of type {@link org.zkoss.zephyr.zpr.IScript} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(org.zkoss.zephyr.ui.Locator, org.zkoss.zephyr.ui.SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 *
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IScriptUpdater {
	}
}