/** JSCumulativeContentRenderer.java.

	Purpose:
		
	Description:
		
	History:
		10:35:15 AM Apr 22, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONs;
import org.zkoss.lang.Generics;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * An implementation of {@link ContentRenderer} that renders
 * the content as a JavaScript property (i.e., name: ['value', 'value1', ..]) cumulatively.
 * It can support to add the same name with different value that the different value
 * will be packed into a list.
 * @author jumperchen
 * @since 8.0
 */
public class JSCumulativeContentRenderer implements ContentRenderer {
	
	private Map<String, List<Object>> _stack = new LinkedHashMap<String,List<Object>>();
	
	private List<Object> fetch(String name) {
		List<Object> list = _stack.get(name);
		if (list == null) {
			list = new LinkedList<Object>();
			_stack.put(name, list);
		}
		return list;
	}
	public void render(String name, String value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, Date value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	private String renderValue(String value) {
		if (value == null) return null;
		else {
			return Strings.escape(value, Strings.ESCAPE_JAVASCRIPT);
		}
	}
	private String renderValue(Date value) {
		if (value == null) return null;
		else return new StringBuilder().append("jq.j2d('")
			.append(JSONs.d2j(value))
			.append("')").toString();
	}
	private String renderValue(Component value) {
		if (value == null || value.getPage() == null) return null;
		else return new StringBuilder().append("{$u:'").append(value.getUuid()).append("'}").toString();
	}
	private String renderValue(Object value) {
		if (value == null || value instanceof String) {
			return renderValue((String)value);
		}
		if (value instanceof Date) {
			return renderValue((Date)value);
		}
		if (value instanceof Component) {
			return renderValue((Component)value);
		}
		if (value instanceof Character) {
			return renderValue(((Character)value).charValue());
		}
		StringBuilder buf = new StringBuilder(); 
		if (value instanceof Map) {
			buf.append('{');
			boolean first = true;
			for (Iterator it = ((Map)value).entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				if (first) first = false;
				else buf.append(',');
				buf.append('\'').append(me.getKey()).append("':");
				renderValue(me.getValue());
			}
			buf.append('}');
			return buf.toString();
		}
		if (value instanceof List) {
			buf.append('[');
			int j = 0;
			for (Iterator it = ((List)value).iterator(); it.hasNext();j++) {
				if (j > 0) buf.append(',');
				renderValue(it.next());
			}
			buf.append(']');
			return buf.toString();
		}
		//handle array
		if (value instanceof Object[]) {
			buf.append('[');
			final Object[] ary = (Object[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof int[]) {
			buf.append('[');
			final int[] ary = (int[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof long[]) {
			buf.append('[');
			final long[] ary = (long[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof short[]) {
			buf.append('[');
			final short[] ary = (short[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof float[]) {
			buf.append('[');
			final float[] ary = (float[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof double[]) {
			buf.append('[');
			final double[] ary = (double[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof byte[]) {
			buf.append('[');
			final byte[] ary = (byte[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof char[]) {
			buf.append('[');
			final char[] ary = (char[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) buf.append(',');
				renderValue(ary[j]);
			}
			buf.append(']');
			return buf.toString();
		}
		if (value instanceof JSONAware)
			return ((JSONAware)value).toJSONString();
		else
			return renderValue(value.toString());
	}

	public void render(String name, Object value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, int value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, short value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, long value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, byte value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, boolean value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, double value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, float value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void render(String name, char value) throws IOException {
		fetch(name).add(renderValue(value));
	}

	public void renderDirectly(String name, Object value) {
		fetch(name).add(renderValue(value));
	}

	public void renderWidgetListeners(Map<String, String> listeners) {
		fetch("listeners0").add(listeners);
	}

	public void renderWidgetOverrides(Map<String, String> overrides) {
		fetch("overrides").add(overrides);
	}

	public void renderWidgetAttributes(Map<String, String> attrs) {
		fetch("domExtraAttrs").add(attrs);
	}
	private String renderValue(char value) {
		StringBuilder buf = new StringBuilder();
		buf.append('\'');
		switch (value) {
		case '\'':
		case '\\': buf.append('\\'); break;
		case '\n': buf.append('\\'); value = 'n'; break;
		case '\t': buf.append('\\'); value = 't'; break;
		case '\r': buf.append('\\'); value = 'r'; break;
		case '\f': buf.append('\\'); value = 'f'; break;
		}
		buf.append(value).append('\'');
		return buf.toString();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>(_stack);
		List<Map<String, String>> listeners = Generics.cast(result.remove("listeners0"));
		List<Map<String, String>> overrides = Generics.cast(result.remove("overrides"));
		List<Map<String, String>> attrs = Generics.cast(result.remove("domExtraAttrs"));
		
		for (Map.Entry<String, List<Object>> me : result.entrySet()) {
			renderName(sb,  me.getKey());
			sb.append('[');
			final List<Object> value = me.getValue();
			int j = 0;
			for (Iterator<Object> it = value.iterator(); it.hasNext();j++) {
				if (j > 0) sb.append(',');
				sb.append(it.next());
			}
			sb.append(']');
		}
		if (listeners != null) {
			renderName(sb, "listeners0");
			sb.append('[');
			
			int j = 0;
			for (Iterator<Map<String, String>> it = listeners.iterator(); it.hasNext();j++) {
				if (j > 0) sb.append(',');
				
				sb.append('{');
				for (Iterator itt = it.next().entrySet().iterator(); itt.hasNext();) {
					final Map.Entry me = (Map.Entry)itt.next();
					sb.append(me.getKey()).append(":function(event){\n")
						.append(me.getValue()).append("\n},");
				}
				sb.setCharAt(sb.length() - 1, '}');
			}
			sb.append(']');
		}

		if (overrides != null) {
			renderName(sb, "overrides");
			sb.append('[');
			
			int j = 0;
			for (Iterator<Map<String, String>> it = overrides.iterator(); it.hasNext();j++) {
				if (j > 0) sb.append(',');
				
				sb.append('{');
				for (Iterator itt = it.next().entrySet().iterator(); itt.hasNext();) {
					final Map.Entry me = (Map.Entry)itt.next();
					final String name = (String)me.getKey();
					final String value = (String)me.getValue();
					if (value != null) {
						//It is too costly to detect if it is a legal expression
						//so we only check the most common illegal case
						final String v = value.trim();
						char cc;
						if (v.length() != 0
						&& ((cc=v.charAt(v.length() - 1)) == ';' || cc == ','
						|| (v.indexOf("function") < 0 && v.indexOf(';') >= 0)))
							throw new UiException("Illegal client override: "+v+
								(name.startsWith("on") ? "\nTo listen an event, remember to captalize the third letter, such as onClick":
									"\nIt must be a legal JavaScript expression (not statement)"));
					}
					sb.append(name).append(":\n").append(value.length() == 0 ? "''" : value).append("\n,");
				}
				sb.setCharAt(sb.length() - 1, '}');
			}
			sb.append(']');
		}

		if (attrs != null) {
			renderName(sb, "domExtraAttrs");
			sb.append('[');
			
			int j = 0;
			for (Iterator<Map<String, String>> it = listeners.iterator(); it.hasNext();j++) {
				if (j > 0) sb.append(',');
				
				sb.append('{');
				for (Iterator itt = it.next().entrySet().iterator(); itt.hasNext();) {
					final Map.Entry me = (Map.Entry)itt.next();
					renderValue(me.getKey()); //allow ':' or others
					sb.append(':');
					renderValue(me.getValue());
					sb.append("\n,");
				}
				sb.setCharAt(sb.length() - 1, '}');
			}
			sb.append(']');
		}
		return sb.toString();
	}

	private void renderName(StringBuilder sb, String name) {
		if (sb.length() > 0) sb.append(',');
		sb.append(name).append(':');
	}
}
