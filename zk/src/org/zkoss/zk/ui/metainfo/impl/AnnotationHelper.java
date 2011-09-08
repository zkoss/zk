/* AnnotationHelper.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 15:48:07     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.lang.Strings;
import org.zkoss.util.Maps;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A helper class used to parse annotations.
 *
 * <p>How to use:
 * <ol>
 * <li>Invoke one of {@link #add}, {@link #addByRawValue},
 * or {@link #addByCompoundValue} to add annotations to this helper.</li>
 * <li>After annotations are all added, invoke {@link #applyAnnotations}
 * to update the annotations to the specified component info.</li>
 * </ol>
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class AnnotationHelper {
	/** A list of AnnotInfo */
	final List<AnnotInfo> _annots = new LinkedList<AnnotInfo>();

	/** Adds an annotation definition.
	 * The annotation's attributes must be parsed into a map (annotAttrs).
	 *
	 * @param annotName the annotation name.
	 * @param annotAttrs a map of attributes of the annotation. If null,
	 * it means no attribute at all.
	 * @see #addByRawValue
	 * @see #addByCompoundValue
	 */
	public void add(String annotName, Map<String, Object> annotAttrs) {
		if (annotName == null || annotName.length() == 0)
			throw new IllegalArgumentException("empty");
		_annots.add(new AnnotInfo(annotName, annotAttrs));
	}
	/** Adds an annotation by specify the value in the raw format.
	 * <code>att1-name=att1-value, att2-name = att2-value</code>.
	 * @param rval the raw value. This method assumes it has been trimmed
	 * before the call.
	 */
	public void addByRawValue(String annotName, String rval) {
		addByRawValueInFormat1(annotName, rval);
			//For backward compatible, we assume Format 1
	}
	/** Adds annotation by specifying the content in the compound format.
	 * <p>There are two formats:
	 * <p>Format 1 (deprecated, 5.0 and before): <br/>
	 * <code>@{annot-name(att1-name=att1-value, att2-name=att2-value) annot2-name (annot3-attrs)}</code>
	 * <p>Format 2 (recommended, since ZK 5.1):<br/>
	 * <code>@annot-name(att1-name=att1-value, att2-name=att2-value) @annot-name() @default(annot3-attrs)</code>
	 * <p>In the second format, it must be a list of annotations separated by space.
	 * And, each annotation is in the format of <code>@annot-name(key=value, value, key=value)</code>.
	 * That is, it starts with the annotation's name and a parenthesis to enclose
	 * any number of key and value pairs (key is optional).
	 * The annotation's names must be composed of letters, numbers, the underscore <code>_</code>, the dash <code>-</code> and the dollar sign <code>$</code>.
	 * The names may only begin with a letter, the underscore or a dollar sign.
	 * In additions, all characters are preserved, including the single and double quotes.
	 *
	 * @param cval the compound value to check. This method assumes that
	 * cval starts with @ and the length is larger than 2
	 */
	public void addByCompoundValue(String cval) {
		final int len = cval.length();
		if (cval.charAt(1) == '{'
		&& cval.charAt(len - 1) == '}') { //Format 1
			addInFormat1(cval.substring(2, len - 1));
			return;
		}

		//format 2
		//for each @name(value), parse name and value, and pass value addByRawValue
		for (int j = 0; j >= 0; j = cval.indexOf('@', j)) {
			//look for annotation's name
			int k = cval.indexOf('(', ++j);
			if (k < 0)
				throw wrongAnnotationException(cval, "( expected");
			final String annotName = cval.substring(j, k).trim();

			j = ++k;
			final StringBuffer sb = new StringBuffer(len);
			for (char quot = (char)0;; ++j) {
				if (j >= len)
					throw wrongAnnotationException(cval, ") expected");

				char cc = cval.charAt(j);
				if (quot == (char)0) {
					if (cc == ')') { //found
						addByRawValueInFormat2(annotName, sb.toString().trim());
						break; //next @name(value)
					} else if (cc == '\'' || cc == '"') {
						quot = cc; //begin-of-quot
					}
				} else if (cc == quot) {
					quot = (char)0; //end-of-quot
				}

				sb.append(cc);

				if (cc == '\\' && j < len - 1)
					sb.append(cval.charAt(++j));
					//Note: we don't decode \x. Rather, we perserve it such
					//that the data binder can use them
			}
		}
	}
	/** @param rval <code>att1-name=att1-value, att2-name = att2-value</code> */
	private void addByRawValueInFormat2(String annotName, String rval) {
		final Map<String, Object> attrs = new LinkedHashMap<String, Object>(4);
		final int len = rval.length();
		final StringBuffer sb = new StringBuffer(len);
		String nm = null;
		char quot = (char)0;
		main: //for each name=value, parse name and value
		for (int j = 0;; ++j) {
			if (j >= len) {
				if (quot != (char)0)
					throw wrongAnnotationException(rval, quot+" expected (not paired)");

				final String val = sb.toString().trim();
				if (nm != null || val.length() > 0) //skip empty one (iincluding after last , )
					attrs.put(nm, val); //found
				break; //done
			}

			char cc = rval.charAt(j);
			if (quot == (char)0) {
				if (cc == ',') {
					final String val = sb.toString().trim();
					if (nm == null && val.length() == 0)
						throw wrongAnnotationException(rval, "nothing before comma (,)");

					attrs.put(nm, val); //found
					nm = null; //cleanup
					sb.setLength(0); //cleanup
					continue; //next name=value
				} else if (cc == '=') {
					if (nm != null)
						throw wrongAnnotationException(rval, "comman (,) missed between two equal sign (=)");
					nm = sb.toString().trim(); //name found
					sb.setLength(0); //cleanup
					continue; //parse value
				} else if (cc == '\'' || cc == '"') {
					quot = cc;
				} else if (cc == '{'
				&& (sb.length() == 0 || sb.toString().trim().length() == 0)) {
					//look for }
					for (int k = ++j;; ++j) {
						if (j >= len)
							throw wrongAnnotationException(rval, "} expected");

						cc = rval.charAt(j);
						if (quot == (char)0) {
							if (cc == '}') { //found
								attrs.put(nm, parseValueArray(rval.substring(k, j).trim()));
								j = Strings.skipWhitespaces(rval, j + 1);
								if (j < len && rval.charAt(j) != ',')
									throw wrongAnnotationException(rval, rval.charAt(j)+" unexpected");
								nm = null; //cleanup
								sb.setLength(0); //cleanup
								continue main;
							} else if (cc == '\'' || cc == '"') {
								quot = cc;
							}
						} else if (cc == quot) {
							quot = (char)0;
						}
						if (cc == '\\' && j < len - 1)
							++j; //skip next \
					}
				}
			} else if (cc == quot) {
				quot = (char)0;
			}

			sb.append(cc);

			if (cc == '\\' && j < len - 1)
				sb.append(rval.charAt(++j));
				//Note: we don't decode \x. Rather, we perserve it such
				//that the data binder can use them
		}
		add(annotName, attrs);
	}
	/** Parses the attribute value.
	 * If the value starts with { and ends with }, an array of String is returned.
	 * Otherwise, the value is returned directly (without any processing).
	 * @param val the value. This method assumes val has been trimmed before the
	 * call.
	 * @exception NullPointException if val is null.
	 * @since 5.5.0
	 */
	public static Object parseAttributeValue(String val) {
		final int len = val.length();
		if (len >= 2 && val.charAt(0) == '{' && val.charAt(len - 1) =='}')
			return parseValueArray(val.substring(1, len - 1));
		return val;
	}
	private static String[] parseValueArray(String rval) {
		final List<String> attrs = new ArrayList<String>();
		final int len = rval.length();
		char quot = (char)0;
		final StringBuffer sb = new StringBuffer(len);
		for (int j =0;; ++j) {
			if (j >= len) {
				if (quot != (char)0)
					throw wrongAnnotationException(rval, quot+" expected (not paired)");

				final String val = sb.toString().trim();
				if (val.length() > 0) //skip if last if it is empty
					attrs.add(val);
				break; //done
			}

			char cc = rval.charAt(j);
			if (quot == (char)0) {
				if (cc == ',') { //found
					attrs.add(sb.toString().trim()); //including empty (between ,)
					sb.setLength(0); //cleanup
					continue;
				} else if (cc == '\'' || cc == '"') {
					quot = cc;
				}
			} else if (cc == quot) {
				quot = (char)0;
			}

			sb.append(cc);

			if (cc == '\\' && j < len - 1)
				sb.append(rval.charAt(++j));
				//Note: we don't decode \x. Rather, we perserve it such
				//that the data binder can use them
		}

		return (String[])attrs.toArray(new String[attrs.size()]);
	}
	private static UiException wrongAnnotationException(String cval, String reason) {
		return new UiException("Illegal annotation, "+reason+": "+cval);
	}
	private void addInFormat1(String cval) {
		final char[] seps1 = {'(', ' '}, seps2 = {')'};
		for (int j = 0, len = cval.length(); j < len;) {
			j = Strings.skipWhitespaces(cval, j);
			int k = Strings.nextSeparator(cval, j, seps1, true, true, false);
			if (k < len && cval.charAt(k) == '(') {
				String nm = cval.substring(j, k).trim();
				if (nm.length() == 0) nm = "default";

				j = k + 1;
				k = Strings.nextSeparator(cval, j, seps2, true, true, false);

				final String rv = 
					(k < len ? cval.substring(j, k): cval.substring(j)).trim();
				if (rv.length() > 0)
					addByRawValueInFormat1(nm, rv);
				else
					add(nm, null);
			} else {
				final String rv = 
					(k < len ? cval.substring(j, k): cval.substring(j)).trim();
				if (rv.length() > 0)
					addByRawValueInFormat1("default", rv);
			}
			j = k + 1;
		}
	}
	/** @param rval <code>att1-name=att1-value, att2-name = att2-value</code> */
	@SuppressWarnings("unchecked")
	private void addByRawValueInFormat1(String annotName, String rval) {
		final Map attrs = Maps.parse(null, rval, ',', '\'', true);
		add(annotName, attrs);
			//The parsing of the value in format 1 is different from format 2
	}

	/** Applies the annotations defined in this helper to the specified
	 * instance definition.
	 *
	 * @param compInfo the instance definition to update
	 * @param propName the property name
	 * @param clear whether to clear all definitions before returning
	 * @see #clear
	 */
	public void applyAnnotations(ComponentInfo compInfo, String propName,
	boolean clear) {
		for (AnnotInfo info: _annots) {
			if (propName != null)
				compInfo.addAnnotation(propName, info.name, info.attrs);
			else
				compInfo.addAnnotation(info.name, info.attrs);
		}
		if (clear)
			_annots.clear();
	}
	/** Applies the annotations defined in this helper to the specified
	 * component.
	 *
	 * @param comp the component to update
	 * @param propName the property name
	 * @param clear whether to clear all definitions before returning
	 * @see #clear
	 */
	public void applyAnnotations(Component comp, String propName,
	boolean clear) {
		for (AnnotInfo info: _annots) {
			ComponentCtrl ctrl = (ComponentCtrl) comp;
			if (propName != null)
				ctrl.addAnnotation(propName, info.name, info.attrs);
			else
				ctrl.addAnnotation(info.name, info.attrs);
		}
		if (clear)
			_annots.clear();
	}
	/** Clears the annotations defined in this helper.
	 *
	 * <p>The annotations are defined by {@link #add}, {@link #addByRawValue},
	 * or {@link #addByCompoundValue}.
	 *
	 * @return true if one or more annotation definitions are defined
	 * (thru {@link #add}).
	 */
	public boolean clear() {
		if (!_annots.isEmpty()) {
			_annots.clear();
			return true;
		}
		return false;
	}

	private static class AnnotInfo {
		private final String name;
		private final Map<String, Object> attrs;

		private AnnotInfo(String name, Map<String, Object> attrs) {
			this.name = name;
			this.attrs = attrs;
		}
	}
}
