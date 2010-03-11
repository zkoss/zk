/* JQueryRenderPatch.java

	Purpose:
		
	Description:
		
	History:
		Sun Jan 17 11:48:04 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkplus.liferay;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletRequest;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.PageRenderPatch;

/**
 * Used to patch the rendering result of a ZK portlet for Liferay.
 * When using ZK portlets with Liferay under Internet Explorer, we have
 * to delay the processing at the client a bit. This patch assume 
 * "/zkau/web/js/zk.wpd" has loaded by Liferay, please refer to 
 * www.liferay.com document for configuration 
 *
 * <p>To use it, you have to specify a library proeprty called
 * ""org.zkoss.zk.portlet.PageRenderPatch.class" with this class's name
 * ("org.zkoss.zkplus.liferay.JQueryRenderCachedPatch").
 *
 * @author tomyeh, sam
 * @since 5.0.0
 */
public class JQueryRenderCachedPatch extends JQueryRenderPatch {
	private static final Log log = Log.lookup(JQueryRenderCachedPatch.class);
	
	//@Override
	public void patchRender(RequestInfo reqInfo, Page page, Writer result, Writer out)
	throws IOException {
		final String extid = page.getUuid() + "-ext";
		String[] html = processHtml(((StringWriter)result).toString());
		//we have to process CSS and append it to HEAD
		out.write(html[0]);
		out.write("<div id=\"");
		out.write(extid);
		out.write("\"></div><script>setTimeout(function(){\njQuery('#");
		out.write(extid);
		out.write("').append('");
			//we have to use append() since it is evaluated synchronously
			//while replaceWith() is not
		out.write(Strings.escape(html[1], Strings.ESCAPE_JAVASCRIPT));
		out.write("');},");
		out.write("" + getBrowserDelay());
		out.write(");</script>");
	}

	protected String getBrowserDelay() {
		return "zk.ie6_ || zk.ie7 ? 1300 : 100"; 
	}
	
	/**
	 *	Append CSS link to head, and remove zk.wpd script
	 */
	private static String[] processHtml(String html) {
		boolean isAppendCSS = false;
		StringBuffer script = new StringBuffer("<script>function _zkCSS(uri){var e=document.createElement(\"LINK\");e.rel=\"stylesheet\";e.type=\"text/css\";e.href=uri;document.getElementsByTagName(\"HEAD\")[0].appendChild(e);};");
		Pattern cssPattern = Pattern.compile("<link[^>]+href=[\"']?([^'\"> ]+)[\"']?[^>]*(/>|>\\s*</link>)");
		Pattern scriptPattern = Pattern.compile("<script[^>]+src=[\"']?([^'\"> ]+/zk.wpd)[\"']?[^>]*(/>|>\\s*</script>)");

		StringBuffer buffer = new StringBuffer();
		int parseStart = 0, scriptStart = 0, scriptEnd = 0;
		for (scriptStart = html.indexOf("<script"); scriptStart != -1;) {
			isAppendCSS = extractCSS(html, parseStart, scriptStart, cssPattern, script, buffer) ? true : isAppendCSS;

			scriptEnd = html.indexOf("</script>", scriptStart);
			if (scriptEnd == -1)
				break;
			scriptEnd += "</script>".length();

			removeScript(html, scriptStart, scriptEnd, scriptPattern, script, buffer);
			if ((scriptStart = html.indexOf("<script", scriptEnd)) != -1)
				parseStart = scriptEnd;
			else {
				buffer.append(html.substring(scriptEnd, html.length()));
				break;
			}
		}
		String[] ret = {"", html};
		if (isAppendCSS) {
			script.append("</script>");
			ret[0] = script.toString();
			ret[1] = buffer.toString();
		}
		return ret;
	}
	
	private static boolean extractCSS(String html, int start, int end, Pattern cssPattern, StringBuffer scriptBuffer, StringBuffer htmlBuffer) {
		if (!(start < end))
			return false;

		boolean isAppendCSS = false;
		Matcher m = cssPattern.matcher(html.substring(start, end));
		while (m.find()) {
			isAppendCSS = true;
			scriptBuffer.append("_zkCSS('" + m.group(1) + "');");
			m.appendReplacement(htmlBuffer, "");
		}
		m.appendTail(htmlBuffer);
		return isAppendCSS;
	}
	
	private static void removeScript(String html, int start, int end, Pattern scriptPattern, StringBuffer scriptBuffer, StringBuffer htmlBuffer) {
		String scriptBlock = html.substring(start, end);
		Matcher m = scriptPattern.matcher(scriptBlock);
		if (m.find()) {
			m.appendReplacement(scriptBuffer, "");
		}
		else
			htmlBuffer.append(scriptBlock);
	}
}