/* Locators.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  9 09:35:33 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xml;

import javax.xml.transform.SourceLocator;

import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Location;

/**
 * Utilities to handle {@link Locator}.
 * @author tomyeh
 * @since 6.0.0
 */
public class Locators {
	/** Formats the message with the location information.
	 * @param loc the location information. Ignored if null.
	 */
	public static String format(String message, Locator loc) {
		return loc != null ?
			format(message, loc.getPublicId(), loc.getSystemId(),
				loc.getLineNumber(), loc.getColumnNumber()): message;
	}
	/** Formats the message with the location information.
	 * @param loc the location information. Ignored if null.
	 */
	public static String format(String message, org.xml.sax.Locator loc) {
		return loc != null ?
			format(message, loc.getPublicId(), loc.getSystemId(),
				loc.getLineNumber(), loc.getColumnNumber()): message;
	}
	/** Formats the message with the location information.
	 * @param loc the location information. Ignored if null.
	 */
	public static String format(String message, SourceLocator loc) {
		return loc != null ?
			format(message, loc.getPublicId(), loc.getSystemId(),
				loc.getLineNumber(), loc.getColumnNumber()): message;
	}

	/** Formats the message with the location information.
	 * @param pubId the public ID (ignored if null)
	 * @param sysId the system ID (ignored if null)
	 * @param lineno the line number (igored if negative)
	 * @param colno the line number (igored if negative)
	 */
	public static String format(String message, String pubId, String sysId,
	int lineno, int colno) {
		final StringBuffer sb = new StringBuffer();
		final String inf = combine(pubId, sysId);
		if (inf != null) {
			int len;
			if ((len = inf.length()) >= 45)
				sb.append(inf.substring(0, 14))
					.append("...").append(inf.substring(len - 26));
			else
				sb.append(inf);
		}
		if (lineno >= 0) {
			if (sb.length() > 0)
				sb.append(':');
			sb.append(lineno);
			if (colno >= 0)
				sb.append(':').append(colno);
		}
		return sb.length() > 0 ?
			sb.append(": ").append(message).toString(): message;
	}
	private static String combine(String pubId, String sysId) {
		return Strings.isEmpty(pubId) ? sysId:
			Strings.isEmpty(sysId) ? pubId: pubId + ':' + sysId;
	}

	/** Converts an instance of {@link Locator} to {@link Location}.
	 * If loc is null, this method returns null.
	 */
	public static Location toLocation(Locator loc) {
		return loc != null ?
			new Loc(combine(loc.getPublicId(),
				loc.getSystemId()), loc.getLineNumber()): null;
	}
	/** Converts an instance of {@link org.xml.sax.Locator} to {@link Location}.
	 * If loc is null, this method returns null.
	 */
	public static Location toLocation(org.xml.sax.Locator loc) {
		return loc != null ?
			new Loc(combine(loc.getPublicId(),
				loc.getSystemId()), loc.getLineNumber()): null;
	}
	/** Converts an instance of {@link SourceLocator} to {@link Location}.
	 * If loc is null, this method returns null.
	 */
	public static Location toLocation(SourceLocator loc) {
		return loc != null ?
			new Loc(combine(loc.getPublicId(),
				loc.getSystemId()), loc.getLineNumber()): null;
	}
	private static class Loc implements Location, java.io.Serializable {
		private final String _path;
		private final int _lnno;

		private Loc(String path, int lnno) {
			_path = path;
			_lnno = lnno;
		}
		@Override
		public String getPath() {
			return _path;
		}
		@Override
		public int getLineNumber() {
			return _lnno;
		}
		@Override
		public String format(String message) {
			return Locators.format(message, _path, null, _lnno, -1);
		}
		@Override
		public String toString() {
			return '[' + _path + ':' + _lnno +']';
		}
	}
}
