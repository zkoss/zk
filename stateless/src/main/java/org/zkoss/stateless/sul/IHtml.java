/* IHtml.java

	Purpose:

	Description:

	History:
		Wed Nov 03 14:52:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.io.Writer;

import org.immutables.value.Value;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.StatelessContentRenderer;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zul.Html;

/**
 * Immutable {@link Html} component
 *
 * <p>
 * A component used to embed the browser native content (i.e., HTML tags)
 * into the output sent to the browser.
 * The browser native content is specified by {@link #withContent}.
 *
 * <p>Notice that {@link Html} generates HTML SPAN to enclose
 * the embedded HTML tags. Thus, you can specify the style
 * ({@link #getStyle}), tooltip {@link #getTooltip} and so on.
 * </p>
 * @author katherine
 * @see Html
 */
@StatelessStyle
public interface IHtml extends IXulElement<IHtml>, IAnyGroup<IHtml> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IHtml DEFAULT = new IHtml.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Html> getZKType() {
		return Html.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Html"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Html";
	}

	/** Returns the embedded content (i.e., HTML tags).
	 */
	default String getContent() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code content}.
	 *
	 * <p> Sets the embedded content (i.e., HTML tags).
	 *
	 * <h3>Security Note</h3>
	 * <p>Unlike other methods, the content assigned to this method
	 * is generated directly to the browser without escaping.
	 * Thus, it is better not to have something input by the user to avoid
	 * any <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/Security_Tips/Cross-site_scripting">XSS</a>
	 * attach.
	 *
	 * @param content The HTML content.
	 * <p>Default: {@code ""}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IHtml withContent(String content);

	/**
	 * Returns the instance with the given content.
	 * @param content The HTML content
	 */
	static IHtml of(String content) {
		return new IHtml.Builder().setContent(content).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IHtml ofId(String id) {
		return new IHtml.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		String cnt = getContent();
		//allow deriving to override getContent()
		if (cnt.length() > 0) {
			final HtmlPageRenders.RenderContext rc = HtmlPageRenders.getRenderContext(null);
			if (rc != null) {
				Writer cwout = rc.temp;
				cwout.write("<div id=\"");
				cwout.write(((StatelessContentRenderer)renderer).getBinding().getUuid());
				cwout.write("\" style=\"display:none\">");
				cwout.write(cnt);
				cwout.write("</div>\n");
				if (!rc.included) //Use detachChildren() only if not included (since the included page is rendered a bit late because of Include handles _childjs in bind_)
					cnt = null; //means already generated
			}

			if (cnt == null) {
				renderer.render("content", new JavaScriptValue("zk('" + ((StatelessContentRenderer) renderer).getBinding()
						.getUuid() + "').detachChildren()"));
			} else {
				render(renderer, "content", cnt);
			}
		}
	}

	/**
	 * Builds instances of type {@link IHtml IHtml}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIHtml.Builder {
	}

	/**
	 * Builds an updater of type {@link IHtml} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IHtmlUpdater {}
}