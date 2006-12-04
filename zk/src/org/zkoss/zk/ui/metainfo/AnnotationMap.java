/* AnnotationMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  4 16:09:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.Collection;
import java.util.Collections;

/**
 * A map of annotations used with {@link ComponentDefinition} and
 * {@link InstanceDefinition}.
 *
 * @author tomyeh
 */
public interface AnnotationMap extends Cloneable {
	/*package*/ static final AnnotationMap EMPTY = new AnnotationMap() {
		public Annotation getAnnotation(String annotName) {
			return null;
		}
		public Annotation getAnnotation(String propName, String annotName) {
			return null;
		}
		public Collection getAnnotations() {
			return Collections.EMPTY_LIST;
		}
		public Collection getAnnotations(String propName) {
			return Collections.EMPTY_LIST;
		}
		public List getAnnotatedPropertiesBy(String annotName) {
			return Collections.EMPTY_LIST;
		}
		public List getAnnotatedProperties() {
			return Collections.EMPTY_LIST;
		}
		public Object clone() {
			return new AnnotationMapImpl();
			//due to the implementation convenience, we return AnnotationMapImpl
		}
	};
	/** Returns the annotation associated with the component definition,
	 * or null if not available.
	 *
	 * @param annotName the annotation name
	 */
	public Annotation getAnnotation(String annotName);
	/** Returns the annotation associated with the definition of the specified
	 * property, or null if not available.
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Annotation getAnnotation(String propName, String annotName);
	/** Returns a read-only collection of all annotations associated with the
	 * component definition (never null).
	 */
	public Collection getAnnotations();
	/** Returns a read-only collection of all annotations associated with the
	 * definition of the specified property (never null).
	 *
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Collection getAnnotations(String propName);
	/** Returns a read-only list of the names (String) of the properties
	 * that are associated with the specified annotation (never null).
	 */
	public List getAnnotatedPropertiesBy(String annotName);
	/** Returns a read-only list of the name (String) of properties that
	 * are associated at least one annotation (never null).
	 */
	public List getAnnotatedProperties();

	/** Clones this annotation map.
	 */
	public Object clone();
}
