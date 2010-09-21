/* TagRenderContext.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan  5 11:48:18     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zhtml.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.lang.Strings;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.event.Events;

/**
 * The render context used to render the additional part (JavaScript
 * code snippet).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class TagRenderContext {
	/** The writer to output JavaScript codes.
	 */
	private final StringBuffer _jsout = new StringBuffer();
	/** Used to decide if the component, which {@link #renderBegin} is called against,
	 * is not a first child.
	 */
	private final List<Boolean> _2ndChild = new LinkedList<Boolean>();

	public TagRenderContext() {
		HtmlPageRenders.setDirectContent(Executions.getCurrent(), true); //default: true
	}

	/** Completes the rendering by returning what are generated
	 * by {@link #renderBegin} and {@link #renderEnd} (never return null).
	 * After rendering, the context is reset.
	 */
	public String complete() {
		if (_jsout.length() > 0) {
			_jsout.insert(0, "<script type=\"text/javascript\">\nzkmb(true);try{");
			_jsout.append("\n}finally{zkme();}</script>");
			final String txt = _jsout.toString();
			_jsout.setLength(0); //reset
			return txt;
		}
		return "";
	}
	/** Renders the beginning JavaScript code snippet for the component.
	 * It must be called before rendering the children.
	 *
	 * @param clientEvents a collection of client events.
	 * It is ignored if lookup is true.
	 * @param lookup whether to look up instead of creating a widget.
	 * Specifies true if the widget is created somewhere else.
	 */
	public void
	renderBegin(Component comp, Map clientEvents, boolean lookup) {
		if (_2ndChild.isEmpty())
			_jsout.append("zkx(");
		else if (_2ndChild.get(0) == Boolean.TRUE)
			_2ndChild.set(0, Boolean.FALSE);
		else
			_jsout.append(',');
		_2ndChild.add(0, Boolean.TRUE);
		_jsout.append("\n[");

		final String wgtcls = lookup ? "zk.RefWidget": comp.getWidgetClass();
		if (!"zhtml.Widget".equals(wgtcls))
			_jsout.append('\'').append(wgtcls).append('\'');
		else
			_jsout.append(1); //1: zhtml.Widget (see mount.js)

		_jsout.append(",'").append(comp.getUuid()).append("',{");

		if (!lookup) {
			boolean first = true;
			final String id = comp.getId();
			if (id.length() > 0) {
				first = false;
				_jsout.append("id:'")
					.append(Strings.escape(id, Strings.ESCAPE_JAVASCRIPT)).append('\'');
			}

			if (clientEvents != null) {
				for (Iterator it = clientEvents.entrySet().iterator();
				it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					final String evtnm = (String)me.getKey();
					final int flags = ((Integer)me.getValue()).intValue();
					if ((flags & ComponentCtrl.CE_IMPORTANT) != 0
					|| Events.isListened(comp, evtnm, false)) {
						if (first) first = false;
						else _jsout.append(',');
						_jsout.append('$').append(evtnm).append(':')
							.append(Events.isListened(comp, evtnm, true));
							//$onClick and so on
					}
				}
			}
		}

		_jsout.append("},[");
	}
	/** Renders the ending JavaScript code snippet for the component.
	 * It must be called after rendering the children.
	 */
	public void renderEnd(Component comp) {
		_2ndChild.remove(0);
		_jsout.append("]]");
		if (_2ndChild.isEmpty()) {
			final String ac = HtmlPageRenders
				.outResponseJavaScripts(Executions.getCurrent(), true);
			if (ac.length() > 0)
				_jsout.append(",0,[").append(ac).append(']');
			_jsout.append(");");
		}
	}
}
