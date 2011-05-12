/* LabelLoader.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 12 13:05:05     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.util.resource.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Enumeration;
import java.util.Collections;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;
import org.zkoss.util.FilterMap;
import org.zkoss.util.Maps;
import org.zkoss.util.Locales;
import org.zkoss.util.resource.LabelLocator;
import org.zkoss.util.resource.LabelLocator2;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.logging.Log;
import org.zkoss.util.WaitLock;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.util.SimpleXelContext;

/**
 * The label loader (implementation only).
 *
 * Used to implement {@link org.zkoss.util.resource.Labels}.
 *
 * <p>Notice that the encoding of the Locale dependent file (*.properties)
 * is assumed to be UTF-8. If it is not the case, please refer to 
 * <a href="http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_library-property_Element/Library_Properties#org.zkoss.util.label.web.charset">ZK Configuration Reference</a>
 * for more information.
 *
 * @author tomyeh
 */
public class LabelLoader {
	private static final Log log = Log.lookup(LabelLoader.class);

	/** A map of (Locale l, Map(String key, ExValue label)).
	 * We use two maps to speed up the access of labels.
	 * _labels allows concurrent access without synchronization.
	 * _syncLabels requires synchronization and used for update.
	 */
	private Map<Locale, Map<String, ExValue>> _labels = Collections.emptyMap();
	/** A map of (Locale 1, Map<String key1, Map<String key2...> or ExValue label>)
	 * It is used by variable resolver and allows ${labels.key1.key2}.
	 * _segLabels allows concurrent access without synchronization.
	 * See also {@link #getSegmentedLabels}.
	 */
	private Map<Locale, Map<String, Object>> _segLabels = Collections.emptyMap();
	/** Map<Locale, Map<String, String>>.
	 */
	private final Map<Locale, Object> _syncLabels = new HashMap<Locale, Object>(8);
	/** A set of LabelLocator or LabelLocator2. */
	private final Set<Object> _locators = new LinkedHashSet<Object>(4); //order is important
	/** The XEL context. */
	private final SimpleXelContext _xelc;
	private String _jarcharset, _warcharset;
	private final ExpressionFactory _expf;
	private final FilterMap.Filter _fmfilter;

	public LabelLoader() {
		_fmfilter = new FilterMap.Filter() {
			public Object filter(Object key, Object value) {
				return value instanceof ExValue ?
					((ExValue)value).getValue(): value;
			}
		};
		_expf = Expressions.newExpressionFactory();
		_xelc = new SimpleXelContext(new Resolver(), null);
	}

	/** Returns the label of the specified key for the current locale,
	 * or null if not found.
	 * @see #getSegmentedLabels
	 */
	public String getLabel(String key) {
		return getLabel(Locales.getCurrent(), key);
	}
	/** Returns the label of the specified key for the specified locale,
	 * or null if not found.
	 * @since 5.0.7
	 */
	public String getLabel(Locale locale, String key) {
		Map<String, ExValue> map = _labels.get(locale);
		if (map == null)
			map = loadLabels(locale);
		final ExValue exVal = map.get(key);
		return exVal != null ? exVal.getValue(): null;
	}
	/** Returns a map of segmented labels for the current locale (never null).
	 * Unlike {@link #getLabel}, if a key of the label contains dot, it will
	 * be splitted into multiple keys and then grouped into map. It is so-called
	 * segmented.
	 * <p>For example, the following property file will parsed into a couple of maps,
	 * and <code>getSegmentedLabels()</code> returns a map containing
	 * a single entry. The entry's key is <code>"a"</code> and the value
	 * is another map with two entries <code>"b"</code> and <code>"c"</code>.
	 * And, the value for <code>"b"</code> is another two-entries map (containing
	 * <code>"c"</code> and <code>"d"</code>).
	 * <pre><code>
	 * a.b.c=1
	 * a.b.d=2
	 * a.e=3</pre></code>
	 * <p>This method is designed to make labels easier to be accessed in
	 * EL expressions.
	 * <p>On the other hand, {@link #getLabel} does not split them, and
	 * you could access them by, say, <code>getLabel("a.b.d")</code>.
	 * @since 5.0.7
	 */
	public Map<String, Object> getSegmentedLabels() {
		return getSegmentedLabels(Locales.getCurrent());
	}
	/** Returns a map of segmented labels for the specified locale (never null).
	 * Refer to {@link #getSegmentedLabels()} for details.
	 * @since 5.0.7
	 */
	public Map<String, Object> getSegmentedLabels(Locale locale) {
		final Map<String, Object> map = _segLabels.get(locale);
		if (map != null)
			return map;
		loadLabels(locale);
		return _segLabels.get(locale);
	}

	/** Sets the variable resolver, which is used if an EL expression
	 * is specified.
	 *
	 * @since 3.0.0
	 */
	public VariableResolver setVariableResolver(VariableResolver resolv) {
		final Resolver resolver = (Resolver)_xelc.getVariableResolver();
		final VariableResolver old = resolver.custom;
		resolver.custom = resolv;
		return old;
	}
	/** Registers a locator which is used to load the Locale-dependent labels
	 * from other resource, such as servlet contexts.
	 */
	public void register(LabelLocator locator) {
		register0(locator);
	}
	/** Registers a locator which is used to load the Locale-dependent labels
	 * from other resource, such as database.
	 * @since 5.0.5
	 */
	public void register(LabelLocator2 locator) {
		register0(locator);
	}
	private void register0(Object locator) {
		if (locator == null)
			throw new NullPointerException("locator");

		synchronized (_locators) {
			if (!_locators.add(locator))
				log.warning("Replace the old one, because it is replicated: "+locator);
		}

		reset(); //Labels might be loaded before, so...
	}
	/** Resets all cached labels and next call to {@link #getLabel}
	 * will cause re-loading the Locale-dependent labels.
	 */
	public void reset() {
		synchronized (_syncLabels) {
			_syncLabels.clear();
			_labels = Collections.emptyMap();
		}
	}

	//-- private utilities --//
	/** Returns Map(String key, ExValue label) of the specified locale.
	 */
	@SuppressWarnings("unchecked")
	private final Map<String, ExValue> loadLabels(Locale locale) {
		WaitLock lock = null;
		for (;;) {
			final Object o;
			synchronized (_syncLabels) {	
				o = _syncLabels.get(locale);
				if (o == null)
					_syncLabels.put(locale, lock = new WaitLock()); //lock it
			}

			if (o instanceof Map)
				return (Map)o;
			if (o == null)
				break; //go to load the page

			//wait because some one is creating the servlet
			if (!((WaitLock)o).waitUntilUnlock(5*60*1000))
				log.warning("Take too long to wait loading labels: "+locale
					+"\nTry to load again automatically...");
		} //for(;;)

		if (_jarcharset == null)
			_jarcharset = Library.getProperty("org.zkoss.util.label.classpath.charset", "UTF-8");
		if (_warcharset == null) {
			_warcharset = Library.getProperty("org.zkoss.util.label.web.charset", null);
			if (_warcharset == null)
				_warcharset = Library.getProperty("org.zkoss.util.label.WEB-INF.charset", "UTF-8"); //backward compatible
		}

		try {
			//get the class name
			if (locale != null)
				log.info("Loading labels for "+locale);
			Map labels = new HashMap(512);

			//1. load from modules
			final ClassLocator locator = new ClassLocator();
			for (Enumeration en = locator.getResources(
				locale == null ? "metainfo/i3-label.properties":
				"metainfo/i3-label_" + locale + ".properties");
			en.hasMoreElements();) {
				final URL url = (URL)en.nextElement();
				load(labels, url, _jarcharset);
			}

			//2. load from extra resource
			final List locators;
			synchronized (_locators) {
				locators = new LinkedList(_locators);
			}
			for (Iterator it = locators.iterator(); it.hasNext();) {
				Object o = it.next();
				if (o instanceof LabelLocator) {
					final URL url = ((LabelLocator)o).locate(locale);
					if (url != null)
						load(labels, url, _warcharset);
				} else {
					final LabelLocator2 loc = (LabelLocator2)o;
					final InputStream is = loc.locate(locale);
					if (is != null) {
						final String cs = loc.getCharset();
						load(labels, is, cs != null ? cs: _warcharset);
					}
				}
			}

			//Convert values to ExValue
			toExValue(labels);

			//merge with labels from 'super' locale
			if (locale != null) {
				final String lang = locale.getLanguage();
				final String cnty = locale.getCountry();
				final String var = locale.getVariant();
				final Map superlabels = loadLabels(
					var != null && var.length() > 0 ? new Locale(lang, cnty):
					cnty != null && cnty.length() > 0 ? new Locale(lang, ""): null);
				if (labels.isEmpty()) {
					labels = superlabels.isEmpty() ?
						Collections.EMPTY_MAP: superlabels;
				} else if (!superlabels.isEmpty()) {
					Map combined = new HashMap(superlabels);
					combined.putAll(labels);
					labels = combined;
				}
			}

			//add to map
			synchronized (_syncLabels) {
				_syncLabels.put(locale, labels);
				cloneLables();
			}
			return labels;
		} catch (Throwable ex) {
			synchronized (_syncLabels) {
				_syncLabels.remove(locale);
				cloneLables();
			}
			throw SystemException.Aide.wrap(ex);
		} finally {
			lock.unlock(); //unlock (always unlock to avoid deadlock)
		}
	}
	@SuppressWarnings("unchecked")
	private void toExValue(Map labels) {
		if (!labels.isEmpty())
			for (Iterator it = labels.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(new ExValue((String)me.getValue()));
			}
	}
	//Copy _syncLabels to _labels. It must be called in synchronized(_syncLabels)
	@SuppressWarnings("unchecked")
	private void cloneLables() {
		final Map labels = new HashMap(),
			segLabels = new HashMap();
		for (Iterator it = _syncLabels.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object value = me.getValue();
			if (value instanceof Map) {
				final Object key = me.getKey();
				labels.put(key, value);
				segLabels.put(key, segment((Map)value));
			}
		}
		_labels = labels;
		_segLabels = segLabels;
	}
	@SuppressWarnings("unchecked")
	private Map segment(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			final String key = (String)it.next();
			if (key.indexOf(".") >= 0)
				return segmentInner(new HashMap(map)); //clone since we'll modify it
		}
		return new FilterMap(map, _fmfilter); //no special key
	}
	@SuppressWarnings("unchecked")
	private FilterMap segmentInner(Map map) {
		final Map segFound = new HashMap();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String key = (String)me.getKey();
			final Object val = me.getValue();
			final int index = key.indexOf('.');
			if (index >= 0) {
				it.remove(); //remove it

				final String newkey = key.substring(0, index);
				Map vals = (Map)segFound.get(newkey);
				if (vals == null)
					segFound.put(newkey, vals = new HashMap());
				vals.put(key.substring(index + 1), val);
			}
		}

		for (Iterator it = segFound.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final FilterMap seged;
			Object o = map.put(me.getKey(), seged = segmentInner((Map)me.getValue()));
			if (o != null && !(o instanceof Map)/*just in case*/) {
				final Map m = seged.getOrigin();
				o = m.put("$", o);
				if (o != null)
					m.put("$", o); //restore
			}
		}
		return new FilterMap(map, _fmfilter);
	}

	/** Loads all labels from the specified URL. */
	private static final void load(Map<String, String> labels, URL url, String charset)
	throws IOException {
		log.info("Opening "+url); //don't use MCommon since Messages might call getLabel
		load(labels, url.openStream(), charset);
	}
	/** Loads all labels from the specified URL. */
	private static final void load(Map<String, String> labels, InputStream is, String charset)
	throws IOException {
		final Map<String, String> news = new HashMap<String, String>();
		try {
			Maps.load(news, is, charset);
		} finally {
			try {is.close();} catch (Throwable ex) {}
		}
		for (Map.Entry<String, String> me: news.entrySet()) {
			labels.put(me.getKey(), me.getValue());
		}
	}
	private class ExValue {
		private Expression _expr;
		private String _val;
		public ExValue(String val) {
			int j;
			if ((j = val.indexOf("${")) >= 0 && val.indexOf('}', j + 2) >= 0) {
				try {
					_expr = _expf.parseExpression(_xelc, val, String.class);
					return;
				} catch (Throwable ex) {
					log.error("Illegal expression: "+val, ex);
				}
			}
			_expr = null;
			_val = val;
		}
		public String getValue() {
			return _expr != null ? (String)_expr.evaluate(_xelc): _val;
		}
	}
	private class Resolver implements VariableResolver {
		private VariableResolver custom;
		public Object resolveVariable(String name) {
			if (custom != null) {
				final Object o = custom.resolveVariable(name);
				if (o != null)
					return o;
			}
			return getSegmentedLabels().get(name);
		}
	}
}
