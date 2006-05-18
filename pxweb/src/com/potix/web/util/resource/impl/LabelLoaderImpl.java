/* LabelLoaderImpl.java

{{IS_NOTE
	$Id: LabelLoaderImpl.java,v 1.7 2006/04/10 03:02:01 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Oct  5 12:18:55     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.util.resource.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Enumeration;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ELException;

import com.potix.mesg.MCommon;
import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.lang.SystemException;
import com.potix.util.Maps;
import com.potix.util.prefs.Apps;
import com.potix.util.resource.LabelLocator;
import com.potix.util.resource.ClassLocator;
import com.potix.util.logging.Log;
import com.potix.util.sys.WaitLock;
import com.potix.el.EvaluatorImpl;
import com.potix.el.FunctionMappers;

import com.potix.web.el.ELContexts;
import com.potix.web.el.ELContext;

/**
 * The label loader used for i3m2 without database.
 *
 * <p>Currently, we use LabelLocator to load i3-label.properties from Web
 * resource.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/04/10 03:02:01 $
 */
public class LabelLoaderImpl
extends com.potix.util.resource.impl.LabelLoaderImpl {
	private static final Log log = Log.lookup(LabelLoaderImpl.class);

	/** A map of (Locale l, Map(String key, String label)). */
	private final Map _labels = new HashMap(6);
	/** A list of LabelLocator. */
	private final List _locators = new LinkedList();

	//-- LabelLoader --//
	/** Returns the label of the specified field.
	 * If not found, it searches label for the field class
	 * (by {@link #getClassLabel}).
	 */
	public String getFieldLabel(Class klass, String field) {
		klass = unproxyClass(klass);
		String label = getProperty(toKey(klass, field));
		if (label != null)
			return label;

		//Try the field's class
		try {
			final AccessibleObject acs = Classes.getAccessibleObject(
				klass, field, null, Classes.B_GET|Classes.B_PUBLIC_ONLY);
			final Class fieldClass = acs instanceof Method ?
				((Method)acs).getReturnType(): ((Field)acs).getType();
			label = realGetClassLabel(fieldClass);
			if (label != null)
				return label;
		} catch (Throwable ex) {
			//ignore it
		}
		return super.getFieldLabel(klass, field);
	}
	/** Returns the label of the specified class.
	 * It searches all super classes and implemented/extended interfaces if
	 * not found.
	 */
	public String getClassLabel(Class klass) {
		klass = unproxyClass(klass);
		final String label = realGetClassLabel(klass);
		return label != null ? label: super.getClassLabel(klass);
	}
	public String getProperty(String key) {
		final String label =
			realGetProperty(Apps.getCurrentLocale(), key);
		if (label == null || label.length() == 0 || label.indexOf("${") < 0)
			return label;

		//Interpret it
		try {
	    	return (String)new EvaluatorImpl()
	    		.evaluate(label, String.class,
	    			new LabelResolver(), FunctionMappers.getDefault());
	    } catch (Throwable ex) {
	    	log.error("Illegal label: key="+key+" value="+label, ex);
	    	return label; //recover it
	    }
	}

	public void register(LabelLocator locator) {
		if (locator == null)
			throw new NullPointerException("locator");

		synchronized (_locators) {
			//no need to use hashset because # of locators are few
			for (Iterator it = _locators.iterator(); it.hasNext();)
				if (it.next().equals(locator)) {
					log.warning("Ignored because of replication: "+locator);
					return; //replicated
				}
			_locators.add(locator);
		}

		reset(); //Labels might be loaded before, so...
	}
	public void reset() {
		synchronized (_labels) {
			_labels.clear();
		}
	}

	//-- deriver to override --//
	/** Unproxy the specified class if necessary.
	 * A class might be proxied by a wrapper class and this
	 * method is used to unveil it.
	 *
	 * <p>Defualt: nothing but returning klass.
	 */
	protected Class unproxyClass(Class klass) {
		return klass;
	}

	/** Returns the property without interprets any expression.
	 * It searches properties defined in i3-label*.properties
	 * All label accesses are eventually done by this method.
	 *
	 * <p>To alter its behavior, you might override this method.
	 */
	protected String realGetProperty(Locale locale, String key) {
		String label = (String)getLabels(locale).get(key);
		if (label != null)
			return label;

		final String lang = locale.getLanguage();
		final String cnty = locale.getCountry();
		final String var = locale.getVariant();
		if (var != null && var.length() > 0) {
			label = (String)getLabels(new Locale(lang, cnty)).get(key);
			if (label != null)
				return label;
		}
		if (cnty != null && cnty.length() > 0) {
			label = (String)getLabels(new Locale(lang, "")).get(key);
			if (label != null)
				return label;
		}
		if (!locale.equals(Locale.getDefault())) {
			label = (String)getLabels(Locale.getDefault()).get(key);
			if (label != null)
				return label;
		}
		return "en".equals(lang)
			|| "en".equals(Locale.getDefault().getLanguage()) ? 
				null: (String)getLabels(Locale.ENGLISH).get(key);
	}

	//-- private utilities --//
	/** Returns the class label, or null if not available.
	 * Note: {@link #getClassLabel} never returns null.
	 */
	private String realGetClassLabel(Class klass) {
		String label = getProperty(toKey(klass));
		if (label != null)
			return label;

		final Class c = klass.getSuperclass();
		if (c != null) {
			label = realGetClassLabel(c);
			if (label != null)
				return label;
		}
		final Class[] infs = klass.getInterfaces();
		if (infs != null) {
			for (int j = 0; j < infs.length; ++j) {
				label = realGetClassLabel(infs[j]);
				if (label != null)
					return label;
			}
		}
		return null;
	}
	/** Returns the key to access the class label. */
	private final String toKey(Class klass) {
		return "class:" + klass.getName();
	}
	/** Returns the key to access the field label. */
	private final String toKey(Class klass, String field) {
		return "class:" + klass.getName() + ':' + field;
	}
	/** Returns Map(String key, String label) of the specified locale. */
	private final Map getLabels(Locale locale) {
		WaitLock lock = null;
		for (;;) {
			final Object o;
			synchronized (_labels) {	
				o = _labels.get(locale);
				if (o == null)
					_labels.put(locale, lock = new WaitLock()); //lock it
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

		try {
			//get the class name
			log.info("Loading labels for "+locale);
			final Map labels = new HashMap(617);

			//1. load from modules
			final ClassLocator locator = new ClassLocator();
			//if (D.ON && log.finerable()) log.finer("Resources found: "+xmls);
			for (Enumeration en = locator.getResources(getI3LabelPath(locale));
			en.hasMoreElements();) {
				final URL url = (URL)en.nextElement();
				load(labels, url);
			}

			//2. load from extra resource
			for (Iterator it = _locators.iterator(); it.hasNext();) {
				final URL url = ((LabelLocator)it.next()).locate(locale);
				if (url != null)
					load(labels, url);
			}

			//add to map
			synchronized (_labels) {
				_labels.put(locale, labels);
			}

			return labels;
		} catch (Throwable ex) {
			synchronized (_labels) {
				_labels.remove(locale);
			}
			throw SystemException.Aide.wrap(ex);
		} finally {
			lock.unlock(); //unlock (always unlock to avoid deadlock)
		}
	}
	/** Returns the path of metainfo/i3-label.properties. */
	private static final String getI3LabelPath(Locale locale) {
		return locale.equals(Locale.ENGLISH) ?
			"metainfo/i3-label.properties":
			"metainfo/i3-label_" + locale + ".properties";
	}
	/** Loads all labels from the specified URL. */
	private static final void load(Map labels, URL url) throws IOException {
		log.info(MCommon.FILE_OPENING, url);
		final Map news = new HashMap();
		Maps.load(news, url.openStream());
		for (Iterator it = news.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object key = me.getKey();
			if (labels.put(key, me.getValue()) != null)
				log.warning("Label of "+key+" is replaced by "+url);
		}
	}
	/** Used to resolve variables for EL. */
	private class LabelResolver implements VariableResolver {
		private final VariableResolver _parent;
		private LabelResolver() {
			final ELContext jc = ELContexts.getCurrent();
			_parent = jc != null ? jc.getVariableResolver(): null;
		}
		public Object resolveVariable(String name) throws ELException {
			final Object o = getProperty(name);
			return o != null ? o:
				_parent != null ? _parent.resolveVariable(name): null;
		}
	}
}
