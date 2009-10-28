/* InaccessibleWidgetBlockService.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 28 14:02:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import org.zkoss.lang.Library;
import org.zkoss.util.CollectionsX;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Disable;

/**
 * Inaccessible Widget Block Service (IWBS) used to block the request sent by
 * an inaccessible widget (at the client).
 *
 * <p>IWBS is designed to protect your application from attack.
 * For example, an invisible button is easy to access by using, say, Firebug.
 *
 * <p>To register this server, you can either invoke {@link Desktop#addListener}
 * manually, or specify the following in WEB-INF/zk.xml
<pre><code>&lt;listener>
  &lt;listener-class>org.zkoss.zk.au.InaccessibleWidgetBlockService$DesktopInit&lt;/listener-class>
&lt;/listener></code></pre>
 *
 * <p>This implementation considers a widget as inaccessible if
 * it is invisible ({@link Component#isVisible}).
 * If you want to block only certain events, you can specify a library
 * property called <code>org.zkoss.zk.au.IWBS.events</code> with a list
 * of the event names to block (separated with comma).
 * For example, if want to block only onClick, onChange, and onSelect, you
 * can specify the following in WEB-INF/zk.xml:
<pre><code>&lt;library-property>
  &lt;name>org.zkoss.zk.au.IWBS.events&lt;/name>
  &lt;value>onClick,onChange,onSelect&lt;/value>
&lt;/library-property></pre></code>

 * <p>In additions, you can override {@link #service} to provide more
 * accurate and versatile blocking.
 * For example, if you want to block all events except <code>onOpen</code>:
 *
 * <pre><code>public boolean service(AuRequest request, boolean everError) {
 *  return super.service(request, everError)
 *  &amp;&amp; !"onOpen".equals(request.getCommand());
 *}</code></pre>
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class InaccessibleWidgetBlockService implements AuService, java.io.Serializable {
	//super//
	public boolean service(AuRequest request, boolean everError) {
		final Component comp = request.getComponent();
		return comp != null && shallBlock(request) && (!comp.isVisible()
			|| (comp instanceof Disable && ((Disable)comp).isDisabled()));
			//block if invisible or disabled
	}

	/** The initial listener used to register in WEB-INF/zk.xml
	 */
	public static class DesktopInit implements org.zkoss.zk.ui.util.DesktopInit {
		public void init(Desktop desktop, Object request) {
			if (_svc == null)
				_svc = new InaccessibleWidgetBlockService();
			desktop.addListener(_svc);
		}
	}
	private static AuService _svc;

	private static boolean shallBlock(AuRequest request) {
		if (_evts == null) {
			String s = Library.getProperty("org.zkoss.zk.au.IWBS.events");
			if (s == null) _evts = Collections.EMPTY_SET;
			else {
				_evts = new HashSet();
				_evts.addAll(CollectionsX.parse(null, s, ','));
			}
		}
		return _evts == Collections.EMPTY_SET /*block all*/
			|| _evts.contains(request.getCommand());
	}
	private static Set _evts;
}
