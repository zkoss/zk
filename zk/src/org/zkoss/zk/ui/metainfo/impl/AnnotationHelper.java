/* AnnotationHelper.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 15:48:07     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
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
import org.zkoss.util.resource.Location;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A helper class used to parse annotations.
 *
 * <p>How to use:
 * <ol>
 * <li>Invoke {@link #add}
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

	/** Test if the given value is an annotation.
	 * In other words, it returns true if the value matches
	 * one of two kinds of format described in {@link #addByCompoundValue}.
	 * @param val the value.
	 * @since 6.0.0
	 */
	public static boolean isAnnotation(String val) {
		int len = val.length();
		if (len >= 4) {
			len = (val = val.trim()).length();
			if (len >= 4 && val.charAt(0) == '@') {
				if (val.charAt(1) == '{') {
					if (val.charAt(len - 1) == '}') //format 1
						return true;
				} else if (val.charAt(len - 1) == ')') {
					//we have to be conserative since a non-annotation value might carry @
					int j = Strings.skipWhitespaces(val, 1);
					char cc = val.charAt(j);
					//annotation must start with the above characters
					if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')
					|| cc == '_' || cc == '$') {
						for (; j < len; ++j) {
							switch (cc = val.charAt(j)) {
							case '(': return true; //valid
							case '_': case '$': case '.': case '-':
								continue; //valid
							default:
								if (Character.isWhitespace(cc)) {
									j = Strings.skipWhitespaces(val, j + 1);
									if (j < len && val.charAt(j) == '(')
										return true;
									return false;
								}
								if ((cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z') 
								&& (cc < '0' || cc > '9'))
									return false;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/** Adds an annotation definition.
	 * The annotation's attributes must be parsed into a map (annotAttrs).
	 *
	 * @param annotName the annotation name.
	 * @param annotAttrs a map of attributes of the annotation. If null,
	 * it means no attribute at all.
	 * @see #addByCompoundValue
	 */
	public void add(String annotName, Map<String, String[]> annotAttrs) {
		if (annotName == null || annotName.length() == 0)
			throw new IllegalArgumentException("empty");
		_annots.add(new AnnotInfo(annotName, annotAttrs));
	}
	/** Adds annotation by specifying the content in the compound format.
	 * <p>There are two formats:
	 * <p>Format 1 (recommended, since 6.0):<br/>
	 * <code>@annot-name(att1-name=att1-value, att2-name=att2-value) @annot-name() @default(annot3-attrs)</code>
	 * <p>Format 2 (deprecated, 5.0 and before): <br/>
	 * <code>@{annot-name(att1-name=att1-value, att2-name=att2-value) annot2-name (annot3-attrs)}</code>
	 * <p>In the first format, it must be a list of annotations separated by space.
	 * And, each annotation is in the format of <code>@annot-name(key=value, value, key=value)</code>.
	 * That is, it starts with the annotation's name and a parenthesis to enclose
	 * any number of key and value pairs (key is optional).
	 * The annotation's names must be composed of letters, numbers, the underscore <code>_</code>, the dash <code>-</code> and the dollar sign <code>$</code>.
	 * The names may only begin with a letter, the underscore or a dollar sign.
	 * In additions, all characters are preserved, including the single and double quotes.
	 *
	 * @param cval the compound value to check. This method assumes that
	 * cval starts with @ and the length is larger than 2
	 * @param loc the location information of the value for displaying better
	 * error message. Ignored if null.
	 * @since 6.0.0
	 */
	public void addByCompoundValue(String cval, Location loc) {
		final int len = cval.length();
		if (cval.charAt(1) == '{'
		&& cval.charAt(len - 1) == '}') { //Format 1
			addInV5(cval.substring(2, len - 1));
			return;
		}

		//format 2
		//for each @name(value),
		//parse name/value, then pass to addByRawValueInV6
		for (int j = 0; j >= 0; j = cval.indexOf('@', j)) {
			//look for annotation's name
			int k = cval.indexOf('(', ++j);
			if (k < 0)
				throw wrongAnnotationException(cval, "'(' expected", loc);
			final String annotName = cval.substring(j, k).trim();

			j = ++k;
			final StringBuffer sb = new StringBuffer(len);
			int nparen = 1;
			for (char quot = (char)0;; ++j) {
				if (j >= len)
					throw wrongAnnotationException(cval, "')' expected", loc);

				char cc = cval.charAt(j);
				if (quot == (char)0) {
					if (cc == '(') {
						++nparen;
					} else if (cc == ')' && --nparen == 0) { //found
						addByRawValueInV6(annotName, sb.toString().trim(), loc);
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
	private void addByRawValueInV6(String annotName, String rval, Location loc) {
		final Map<String, String[]> attrs = new LinkedHashMap<String, String[]>(4);
		final int len = rval.length();
		final StringBuffer sb = new StringBuffer(len);
		String nm = null;
		char quot = (char)0;
		int nparen = 0;
		main: //for each name=value, parse name and value
		for (int j = 0;; ++j) {
			if (j >= len) {
				if (quot != (char)0)
					throw wrongAnnotationException(rval, quot+" expected (not paired)", loc);
				if (nparen != 0)
					throw wrongAnnotationException(rval, "')' expected", loc);

				final String val = sb.toString().trim();
				if (nm != null || val.length() > 0) //skip empty one (iincluding after last , )
					attrs.put(nm, new String[] {val}); //found
				break; //done
			}

			char cc = rval.charAt(j);
			if (quot == (char)0) {
				if (cc == ',' && nparen == 0) {
					final String val = sb.toString().trim();
					if (nm == null && val.length() == 0)
						throw wrongAnnotationException(rval, "nothing before ','", loc);

					attrs.put(nm, new String[] {val}); //found
					nm = null; //cleanup
					sb.setLength(0); //cleanup
					continue; //next name=value
				} else if (cc == '=' && nparen == 0) {
					if (nm != null)
						throw wrongAnnotationException(rval, "',' missed between two equal sign (=)", loc);
					nm = sb.toString().trim(); //name found
					sb.setLength(0); //cleanup
					continue; //parse value
				} else if (cc == '(') {
					++nparen;
				} else if (cc == ')') {
					if (--nparen < 0)
						throw wrongAnnotationException(rval, "too many ')'", loc);
				} else if (cc == '\'' || cc == '"') {
					quot = cc;
				} else if (cc == '{' && nparen == 0
				&& (sb.length() == 0 || sb.toString().trim().length() == 0)) {
					//look for }
					for (int k = ++j, ncur = 1;; ++j) {
						if (j >= len)
							throw wrongAnnotationException(rval, "'}' expected", loc);

						cc = rval.charAt(j);
						if (quot == (char)0) {
							if (cc == '}' && --ncur == 0) { //found
								attrs.put(nm, parseValueArray(rval.substring(k, j).trim(), loc));
								j = Strings.skipWhitespaces(rval, j + 1);
								if (j < len && rval.charAt(j) != ',')
									throw wrongAnnotationException(rval, "',' expected, not '" + rval.charAt(j)+'\'', loc);
								nm = null; //cleanup
								sb.setLength(0); //cleanup
								continue main;
							} else if (cc == '{') {
								++ncur;
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
	 * @param loc the location information of the value for displaying better
	 * error message. Ignored if null.
	 * @since 6.0.0
	 */
	public static String[] parseAttributeValue(String val, Location loc) {
		final int len = val.length();
		if (len >= 2 && val.charAt(0) == '{' && val.charAt(len - 1) =='}')
			return parseValueArray(val.substring(1, len - 1), loc);
		return new String[] {val};
	}
	private static String[] parseValueArray(String rval, Location loc) {
		final List<String> attrs = new ArrayList<String>();
		final int len = rval.length();
		char quot = (char)0;
		final StringBuffer sb = new StringBuffer(len);
		int nparen = 0;
		for (int j =0;; ++j) {
			if (j >= len) {
				if (quot != (char)0)
					throw wrongAnnotationException(rval, '\'' + quot+"' expected (not paired)", loc);
				if (nparen != 0)
					throw wrongAnnotationException(rval, "')' expected", loc);

				final String val = sb.toString().trim();
				if (val.length() > 0) //skip if last if it is empty
					attrs.add(val);
				break; //done
			}

			char cc = rval.charAt(j);
			if (quot == (char)0) {
				if (cc == ',' && nparen == 0) { //found
					attrs.add(sb.toString().trim()); //including empty (between ,)
					sb.setLength(0); //cleanup
					continue;
				} else if (cc == '(') {
					++nparen;
				} else if (cc == ')') {
					if (--nparen < 0)
						throw wrongAnnotationException(rval, "too many ')'", loc);
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
	private static UiException wrongAnnotationException(
	String cval, String reason, Location loc) {
		final String msg = "Illegal annotation, "+reason+": "+cval;
		return new UiException(loc != null ? loc.format(msg): msg);
	}
	private void addInV5(String cval) {
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
					addByRawValueInV5(nm, rv);
				else
					add(nm, null);
			} else {
				final String rv = 
					(k < len ? cval.substring(j, k): cval.substring(j)).trim();
				if (rv.length() > 0)
					addByRawValueInV5("default", rv);
			}
			j = k + 1;
		}
	}
	/** @param rval <code>att1-name=att1-value, att2-name = att2-value</code> */
	@SuppressWarnings("unchecked")
	private void addByRawValueInV5(String annotName, String rval) {
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
	 * @param loc the location information of the annotation in
	 * the document, or null if not available.
	 * @see #clear
	 * @since 6.0.0
	 */
	public void applyAnnotations(ComponentInfo compInfo, String propName,
	boolean clear, Location loc) {
		for (AnnotInfo info: _annots) {
			compInfo.addAnnotation(propName, info.name, info.attrs, loc);
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
			ctrl.addAnnotation(propName, info.name, info.attrs);
		}
		if (clear)
			_annots.clear();
	}
	/** Clears the annotations defined in this helper.
	 *
	 * <p>The annotations are defined by {@link #add}
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
		private final Map<String, String[]> attrs;

		private AnnotInfo(String name, Map<String, String[]> attrs) {
			this.name = name;
			this.attrs = attrs;
		}
	}
}
