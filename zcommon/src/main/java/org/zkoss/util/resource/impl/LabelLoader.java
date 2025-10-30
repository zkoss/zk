/** LabelLoader.java.

	Purpose:
		
	Description:
		
	History:
		5:26:48 PM Dec 25, 2013, Created by jumperchen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util.resource.impl;

import java.util.Locale;
import java.util.Map;

import org.zkoss.util.resource.LabelLocator;
import org.zkoss.util.resource.LabelLocator2;
import org.zkoss.xel.VariableResolver;

/**
 * The label loader interface.
 *
 * Used to implement for {@link org.zkoss.util.resource.Labels}.
 *
 * <p>Notice that the encoding of the Locale dependent file (*.properties)
 * is assumed to be UTF-8. If it is not the case, please refer to 
 * <a href="http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_library-property_Element/Library_Properties#org.zkoss.util.label.web.charset">ZK Configuration Reference</a>
 * for more information.
 * <p> Specify the library property of <code>org.zkoss.util.resource.LabelLoader.class</code>
 * in zk.xml to provide a customized label loader for debugging purpose. (since 7.0.1)
 * @author jumperchen
 * @since 7.0.1
 */
public interface LabelLoader {
	
	/** Returns the label of the specified key for the specified locale,
	 * or null if not found.
	 */
	public String getLabel(Locale locale, String key);

	/** Returns the label of the specified key for the current locale,
	 * or null if not found.
	 * @see #getSegmentedLabels
	 */
	public String getLabel(String key);
	/** Returns a map of segmented labels for the current locale (never null).
	 * Unlike {@link #getLabel}, if a key of the label contains dot, it will
	 * be split into multiple keys and then grouped into map. It is so-called
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
	 */
	public Map<String, Object> getSegmentedLabels();

	/** Returns a map of segmented labels for the specified locale (never null).
	 * Refer to {@link #getSegmentedLabels()} for details.
	 */
	public Map<String, Object> getSegmentedLabels(Locale locale);

	/** Resets all cached labels and next call to {@link #getLabel}
	 * will cause re-loading the Locale-dependent labels.
	 */
	public void reset();

	/** Sets the variable resolver, which is used if an EL expression
	 * is specified.
	 */
	public VariableResolver setVariableResolver(VariableResolver resolv);

	/** Registers a locator which is used to load the Locale-dependent labels
	 * from other resource, such as servlet contexts.
	 */
	public void register(LabelLocator locator);

	/** Registers a locator which is used to load the Locale-dependent labels
	 * from other resource, such as database.
	 */
	public void register(LabelLocator2 locator);
}
