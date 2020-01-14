/* HtmlNativeComponent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 19 18:05:01     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.idom.Namespace;
import org.zkoss.lang.Strings;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.annotation.ClientEvent;
import org.zkoss.zk.ui.annotation.ClientEventParam;
import org.zkoss.zk.ui.annotation.ClientPropertySync;
import org.zkoss.zk.ui.annotation.ClientTag;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.DirectContent;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;

public class HtmlWebComponent extends AbstractComponent implements DynamicPropertied {
	private static final Native.Helper _helper = new HtmlNativeComponent.HtmlHelper();
	private static final Map<Class<? extends Component>, Map<String, Set<String>>> _SYNC_PROPS = new HashMap<>(4);
	private static final Map<Class<? extends Component>, Map<String, Class<? extends Event>>> _EVENT_MAP = new HashMap<>(4);
	private static final Map<Class<? extends Component>, Map<String, String[]>> _EVENT_PARAM_MAP = new HashMap<>(4);
	private static final Map<Class<?>, Constructor<?>> _CONSTRUCTOR_CACHE = new HashMap<>(4);
	private static final Map<Constructor<?>, String[]> _CONSTRUCTOR_PARAM_CACHE = new HashMap<>(4);
	private static final Logger log = LoggerFactory.getLogger(HtmlWebComponent.class);

	//---------
	//structure: _prefix <_tag> _prolog children _epilog </_tag> _postifx
	//---------

	private String _tag;
	private String _prolog = "", _epilog = "";
	/** The text before the tag name. */
	private String _prefix, _postfix;
	private Map<String, Object> _props;
	/** Declared namespaces ({@link Namespace}). */
	private List<Namespace> _dns;

	/** Constructs a {@link HtmlWebComponent} component.
	 *
	 */
	public HtmlWebComponent() {
		// FIXME: Class related should be scanned once, but static block should be put on the child class
		Class<? extends HtmlWebComponent> aClass = getClass();
		for (ClientEvent event : aClass.getDeclaredAnnotationsByType(ClientEvent.class)) {
			String eventName = event.value();
			Class<? extends Event> eventClass = event.event();
			addClientEvent(aClass, eventName, event.flags());
			if (eventClass != Event.class) {
				addClientEventHandler(aClass, eventName, eventClass);
			}
		}
		for (ClientPropertySync prop : aClass.getDeclaredAnnotationsByType(ClientPropertySync.class)) {
			addPropertySyncOnEvent(aClass, prop.prop(), prop.event());
		}
		ClientTag tag = aClass.getAnnotation(ClientTag.class);
		if (tag != null) setTag(tag.value());
	}

	/** Constructs a {@link HtmlWebComponent} component with the specified tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public HtmlWebComponent(String tag) {
		this();
		setTag(tag);
	}

	/** Constructs a {@link HtmlWebComponent} component with the specified
	 * prolog and epilog.
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 * @param prolog the content right before the children, if any.
	 * @param epilog the content right after the children, if any.
	 */
	public HtmlWebComponent(String tag, String prolog, String epilog) {
		this(tag);

		_prolog = prolog != null ? prolog : "";
		_epilog = epilog != null ? epilog : "";
	}

	/** Returns the widget class, "zk.Native".
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		return "zk.WebComp";
	}

	/** Returns the tag name, or null if plain text.
	 */
	public String getTag() {
		return _tag;
	}

	//Native//
	public List<Namespace> getDeclaredNamespaces() {
		if (_dns != null)
			return _dns;
		return Collections.emptyList();
	}

	public void addDeclaredNamespace(Namespace ns) {
		if (ns == null)
			throw new IllegalArgumentException();

		if (_dns == null)
			_dns = new LinkedList<Namespace>();
		_dns.add(ns);
	}

	public String getPrologContent() {
		return _prolog;
	}

	public void setPrologContent(String prolog) {
		_prolog = prolog != null ? prolog : "";
	}

	public String getEpilogContent() {
		return _epilog;
	}

	public void setEpilogContent(String epilog) {
		_epilog = epilog != null ? epilog : "";
	}

	public Native.Helper getHelper() {
		return _helper;
	}

	//-- Component --//
	public void setId(String id) {
		super.setId(id);
		setDynamicProperty("id", id);
	}

	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use client-dependent attribute, such as display:none");
	}

	public void redraw(Writer out) throws java.io.IOException {
		//Note: _tag == null can NOT be handled specially
		final Execution exec = Executions.getCurrent();
		final boolean root = getParent() == null && (getPage().isComplete()
				|| (exec != null && "complete".equals(ExecutionsCtrl.getPageRedrawControl(exec))));
		if (exec == null || exec.isAsyncUpdate(null) || (!root && !HtmlPageRenders.isDirectContent(exec))) {
			super.redraw(out); //renderProperties (assume in zscript)
			return;
		}

		Writer oldout = null;
		if (exec != null && !HtmlPageRenders.isZkTagsGenerated(exec) && exec.getAttribute(ATTR_TOP_NATIVE) == null) { //need to check topmost native only
			String tn;
			if (root || "html".equals(tn = _tag != null ? _tag.toLowerCase(java.util.Locale.ENGLISH) : "")
					|| "body".equals(tn) || "head".equals(tn)) {
				exec.setAttribute(ATTR_TOP_NATIVE, Boolean.TRUE);
				oldout = out;
				out = new StringWriter();
			}
		}

		out.write(getPrologHalf());

		//children
		Component child = getFirstChild();
		if (child == null) {
			//need to invoke outStandalone to generate response if any (Bug 3009925)
			//however, it is not required if not root (since others will invoke)
			if (root)
				HtmlPageRenders.outStandalone(exec, null, out);
		} else {
			if (root)
				HtmlPageRenders.setDirectContent(exec, true);
			do {
				Component next = child.getNextSibling();
				if (child instanceof Native || ((ComponentCtrl) child).getExtraCtrl() instanceof DirectContent) {
					((ComponentCtrl) child).redraw(out);
				} else {
					HtmlPageRenders.setDirectContent(exec, false);
					HtmlPageRenders.outStandalone(exec, child, out);
					HtmlPageRenders.setDirectContent(exec, true);
				}
				child = next;
			} while (child != null);
		}

		out.write(getEpilogHalf());

		if (oldout != null) {
			exec.removeAttribute(ATTR_TOP_NATIVE);

			//order: <html><head><zkhead><body>
			//1. replace <zkhead/> if found
			//2. insert before </head> if found
			//3. insert after <body> if found
			//4. insert after <html> if found
			//5. insert at the end if none of above found
			final StringBuffer sb = ((StringWriter) out).getBuffer();
			if (!HtmlPageRenders.isZkTagsGenerated(exec)) {
				int jhead = -1, //anchor of header
						junav = -1, //anchor of unavailable
						head = -1, //index of <head>
						heade = -1, //index of </head>
						html = -1; //index of <html>
				for (int j = 0, len = sb.length(); (j = sb.indexOf("<", j)) >= 0;) {
					++j;
					if (jhead < 0 && startsWith(sb, "zkhead", j)) {
						int l = Strings.indexOf(sb, '>', j) + 1;
						sb.delete(jhead = --j, l); //jhead found
						len = sb.length();
					} else if (head < 0 && startsWith(sb, "head", j)) {
						head = Strings.indexOf(sb, '>', j) + 1;
					} else if (html < 0 && startsWith(sb, "html", j)) {
						html = Strings.indexOf(sb, '>', j) + 1;
					} else if (junav < 0 && startsWith(sb, "body", j)) {
						junav = Strings.indexOf(sb, '>', j) + 1; //junav found
						break; //done
					} else if (sb.charAt(j) == '/' && startsWith(sb, "head", ++j)) {
						heade = j - 2;
					}
				}

				boolean disableUnavailable = false;
				if (jhead < 0 && ((jhead = head) < 0) //use <head> if no <zkhead>
						&& ((jhead = heade) < 0) //use </head> if no <head> (though unlikely)
						&& ((jhead = junav) < 0) //use <body> if no </head>
						&& ((jhead = html) < 0)) { //use <html> if no <body>
					if (_tag != null) {
						final String tn = _tag.toLowerCase(java.util.Locale.ENGLISH);
						if ("div".equals(tn) || "span".equals(tn)) {
							l_loop: for (int j = 0, len = sb.length(); j < len; ++j)
								switch (sb.charAt(j)) {
								case '>':
									disableUnavailable = true; //make output cleaner
									jhead = j + 1; //found
								case '=': //it might have something depends on JS
								case '"':
									break l_loop;
								}
						}
					}
					if (jhead < 0)
						jhead = 0; //insert at head if not found
				}

				final String msg = HtmlPageRenders.outUnavailable(exec);
				//called if disableUnavailable (so it won't be generated later)
				if (msg != null && !disableUnavailable) {
					if (junav < 0) {
						if (html >= 0)
							junav = sb.lastIndexOf("</html");
					}
					if (junav >= 0)
						sb.insert(junav < jhead ? jhead : junav, msg);
					else
						sb.append(msg);
				}

				final String zktags = HtmlPageRenders.outHeaderZkTags(exec, getPage());
				if (zktags != null)
					sb.insert(jhead, zktags);
			}

			oldout.write(sb.toString());
		}
	}

	/** Used to indicate the redrawing of the top native is found. */
	private static final String ATTR_TOP_NATIVE = "zkHtmlTopNative";

	private static boolean startsWith(StringBuffer sb, String tag, int start) {
		int end = start + tag.length();
		char cc;
		return sb.length() > end && tag.equalsIgnoreCase(sb.substring(start, end))
				&& ((cc = sb.charAt(end)) < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z');
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		Class<? extends Component> aClass = getClass();
		render(renderer, "prolog", getPrologHalf());
		render(renderer, "epilog", getEpilogHalf());
		render(renderer, "syncProps", _SYNC_PROPS.get(aClass));
		render(renderer, "eventParameters", getEventParameters(aClass));
	}

	private Map<String, String[]> getEventParameters(Class<? extends Component> aClass) {
		Map<String, String[]> cache = _EVENT_PARAM_MAP.get(aClass);
		if (cache != null || _EVENT_PARAM_MAP.containsKey(aClass))
			return cache;

		Map<String, String[]> m = new HashMap<>();
		for (Map.Entry<String, Class<? extends Event>> entry : _EVENT_MAP.getOrDefault(aClass, Collections.emptyMap()).entrySet()) {
			Constructor<?> eventConstructor = findEventConstructor(entry.getValue());
			if (eventConstructor != null) {
				m.put(entry.getKey(), findEventConstructorParams(eventConstructor));
			}
		}
		Map<String, String[]> result = m.isEmpty() ? null : m;
		_EVENT_PARAM_MAP.put(aClass, result);
		return result;
	}

	private String getPrologHalf() {
		final StringBuffer sb = new StringBuffer(128);
		final Native.Helper helper = getHelper();
		//don't use _helper directly, since the derive might override it

		if (_prefix != null)
			sb.append(_prefix);

		//first half
		helper.getFirstHalf(sb, _tag, _props, _dns);

		//prolog
		sb.append(_prolog); //no encoding
		return sb.toString();
	}

	private String getEpilogHalf() {
		final StringBuffer sb = new StringBuffer(128);
		final Native.Helper helper = getHelper();

		//epilog
		sb.append(_epilog);

		//second half
		helper.getSecondHalf(sb, _tag);

		if (_postfix != null)
			sb.append(_postfix);
		return sb.toString();
	}

	@Override
	public void service(AuRequest request, boolean everError) {
		String cmd = request.getCommand();
		Class<? extends Event> eventClass = _EVENT_MAP
				.getOrDefault(getClass(), Collections.emptyMap())
				.get(cmd);
		if (eventClass != null) {
			Event eventObject = generateEvent(cmd, eventClass, request.getData());
			if (eventObject == null) {
				log.error("Unable to generate an event instance of {}. "
						+ "Please check if the additional arguments in the declared"
						+ " constructor are all annotated with @ClientEventParam.",
						eventClass.getSimpleName());
			} else {
				Events.postEvent(eventObject);
				return;
			}
		}

		switch (cmd) {
			case Events.ON_CLICK:
			case Events.ON_DOUBLE_CLICK:
			case Events.ON_RIGHT_CLICK:
			case "onMouseDown":
			case "onMouseUp":
			case "onMouseMove":
			case "onMouseOver":
			case "onMouseOut":
				Events.postEvent(MouseEvent.getMouseEvent(request));
				break;
			case "onKeyPress":
			case "onKeyDown":
			case "onKeyUp":
				Events.postEvent(KeyEvent.getKeyEvent(request));
				break;
			default:
				super.service(request, everError);
		}
	}

	// Rules:
	// 1. parameters > 2 (name, target are needed)
	// 2. Every additional parameter is needed to annotate with @ClientEventParam, or we don't know what to inject
	// 3. Only get the first matched (order is not defined)
	private Constructor<?> findEventConstructor(Class<?> eventClass) {
		Constructor<?> cached = _CONSTRUCTOR_CACHE.get(eventClass);
		if (cached != null || _CONSTRUCTOR_CACHE.containsKey(eventClass))
			return cached;

		for (Constructor<?> constructor : eventClass.getConstructors()) {
			int parameterCount = constructor.getParameterCount();
			if (parameterCount > 2) {
				Parameter[] parameters = constructor.getParameters();
				for (int i = 2; i < parameterCount; i++) {
					if (!parameters[i].isAnnotationPresent(ClientEventParam.class)) {
						break;
					}
					if (i == parameterCount - 1) {
						_CONSTRUCTOR_CACHE.put(eventClass, constructor);
						return constructor;
					}
				}
			}
		}
		_CONSTRUCTOR_CACHE.put(eventClass, null);
		log.warn("Could not find any suitable constructor of {}. Is it expected?",
				eventClass.getSimpleName());
		return null;
	}

	private Event generateEvent(String command, Class<? extends Event> eventClass,
	                            Map<String, Object> data) {
		Constructor<?> eventConstructor = findEventConstructor(eventClass);
		if (eventConstructor != null) {
			List<Object> arguments = new ArrayList<>();
			arguments.add(command);
			arguments.add(this);
			Arrays.stream(findEventConstructorParams(eventConstructor))
					.map(data::get)
					.forEach(arguments::add);
			try {
				return (Event) eventConstructor.newInstance(arguments.toArray());
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw UiException.Aide.wrap(e);
			}
		}
		return null;
	}

	private String[] findEventConstructorParams(Constructor<?> eventConstructor) {
		String[] params = _CONSTRUCTOR_PARAM_CACHE.get(eventConstructor);
		if (params == null) {
			Parameter[] parameters = eventConstructor.getParameters();
			params = IntStream.range(2, parameters.length)
					.mapToObj(i -> parameters[i].getDeclaredAnnotation(ClientEventParam.class))
					.map(ClientEventParam::value)
					.toArray(String[]::new);
			_CONSTRUCTOR_PARAM_CACHE.put(eventConstructor, params);
		}
		return params;
	}

	//DynamicTag//
	/** Sets the tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public void setTag(String tag) throws WrongValueException {
		_tag = tag != null && tag.length() > 0 ? tag : null;
	}

	public boolean hasTag(String tag) {
		return true; //accept anything
	}

	public boolean hasDynamicProperty(String name) {
		return ComponentsCtrl.isReservedAttribute(name);
	}

	public Object getDynamicProperty(String name) {
		return _props != null ? _props.get(name) : null;
	}

	public void setDynamicProperty(String name, Object value) throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name required");

		if (value == null) {
			if (_props != null)
				_props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap<String, Object>();
			if ("sclass".equals(name))
				_props.put("class", value);
			else
				_props.put(name, value);
		}
		smartUpdate(name, value);
	}

	public void invoke(String function) {
		response(new AuInvoke(this, "invoke", function));
	}

	public void invoke(String function, Object... arguments) {
		int argsLength = arguments.length;
		Object[] args = new Object[argsLength + 1];
		args[0] = function;
		System.arraycopy(arguments, 0, args, 1, argsLength);
		response(new AuInvoke(this, "invoke", args));
	}

	@Override
	protected void updateByClient(String name, Object value) {
		disableClientUpdate(true);
		try {
			setDynamicProperty(name, value);
		} finally {
			disableClientUpdate(false);
		}
	}

	protected static void addClientEventHandler(Class<? extends HtmlWebComponent> cls,
	                                            String eventName, Class<? extends Event> eventClass) {
		_EVENT_MAP.computeIfAbsent(cls, k -> new HashMap<>(4))
				.put(eventName, eventClass);
	}

	protected static void addPropertySyncOnEvent(Class<? extends Component> cls, String property, String eventName) {
		_SYNC_PROPS.computeIfAbsent(cls, k -> new HashMap<>(4))
			.computeIfAbsent(eventName, k -> new HashSet<>())
			.add(property);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(5);

	static {
		_properties.put("id", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((HtmlWebComponent) cmp).setId(value);
			}

			public String getValue(Component cmp) {
				return ((HtmlWebComponent) cmp).getId();
			}
		});

		_properties.put("tag", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((HtmlWebComponent) cmp).setTag(value);
			}

			public String getValue(Component cmp) {
				return ((HtmlWebComponent) cmp).getTag();
			}
		});

		_properties.put("epilogContent", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((HtmlWebComponent) cmp).setEpilogContent(value);
			}

			public String getValue(Component cmp) {
				return ((HtmlWebComponent) cmp).getEpilogContent();
			}
		});
		_properties.put("prologContent", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((HtmlWebComponent) cmp).setPrologContent(value);
			}

			public String getValue(Component cmp) {
				return ((HtmlWebComponent) cmp).getPrologContent();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl implements DirectContent, PrologAllowed {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prefix = prolog;
			//Notice: it is used as prefix (shown before the tag and children)
			//while _prolog is the text shown after the tag and before the children
		}
	}
}
