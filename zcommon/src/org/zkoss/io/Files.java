/* Files.java


	Purpose: File related utilities.
	Description:
	History:
	 2001/6/29, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Locale;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.util.ArraysX;
import org.zkoss.util.Locales;

/**
 * File related utilities.
 *
 * @author tomyeh
 */
public class Files {
	protected Files() {}

	/**
	 * The separator representing the drive in a path. In Windows, it is
	 * ':', while 0 in other platforms.
	 */
	public final static char DRIVE_SEPARATOR_CHAR =
		System.getProperty("os.name").indexOf("Windows")<0 ? (char)0: ':';

	/** Corrects the separator from '/' to the system dependent one.
	 * Note: i3 always uses '/' while Windows uses '\\'.
	 */
	public final static String correctSeparator(String flnm) {
		return File.separatorChar != '/' ?
			flnm.replace('/', File.separatorChar): flnm;
	}
	/** Returns the conf directory.
	 *
	 * <p>The configure directory is assumed to be specified by
	 * the system property called "org.zkoss.io.conf.dir".
	 * If property not found, it assumes the conf or config directory under
	 * the directory specified by the system property called "user.dir".
	 * If property not found, it assumes the conf directory under
	 * the current directory.
	 */
	public final static File getConfigDirectory() {
		final String confdir = Library.getProperty("org.zkoss.io.conf.dir");
		if (confdir != null) return new File(confdir);

		final String userdir = System.getProperty("user.dir", ".");
		final File fl0 = new File(userdir, "conf");
		if (exists(fl0) == null) {
			File fl = exists(new File(userdir, "config"));
			if (fl != null) return fl;

			if (!".".equals(userdir)) {
				fl = exists(new File(userdir, "../conf"));
				if (fl != null) return fl;
				fl = exists(new File(userdir, "../config"));
				if (fl != null) return fl;

				fl = exists(new File("./conf"));
				if (fl != null) return fl;
				fl = exists(new File("./config"));
				if (fl != null) return fl;
			}

			fl = exists(new File("../conf"));
			if (fl != null) return fl;
			fl = exists(new File("../config"));
			if (fl != null) return fl;
		}
		return fl0;
	}
	private static final File exists(File fl) {
		return fl.exists() ? fl: null;
	}

	/** Returns all bytes in the input stream, never null
	 * (but its length might zero).
	 * <p>Notice: this method is memory hungry.
	 */
	public static final byte[] readAll(InputStream in)
	throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buf = new byte[1024*16];
		for (int v; (v = in.read(buf)) >= 0;) { //including 0
			out.write(buf, 0, v);
		}
		return out.toByteArray();
	}

	/** Returns all characters in the reader, never null
	 * (but its length might zero).
	 * <p>Notice: this method is memory hungry.
	 */
	public static final StringBuffer readAll(Reader reader)
	throws IOException {
		final StringWriter writer = new StringWriter(1024*16);
		copy(writer, reader);
		return writer.getBuffer();
	}

	/** Copies a reader into a writer.
	 * @param writer the destination
	 * @param reader the source
	 */
	public static final void copy(Writer writer, Reader reader)
	throws IOException {
		final char[] buf = new char[1024*4];
		for (int v; (v = reader.read(buf)) >= 0;) {
			if (v > 0)
				writer.write(buf, 0, v);
		}
	}
	/** Copies an input stream to a output stream.
	 * @param out the destination
	 * @param in the source
	 */
	public static final void copy(OutputStream out, InputStream in)
	throws IOException {
		final byte[] buf = new byte[1024*8];
		for (int v; (v = in.read(buf)) >= 0;) {
			if (v > 0)
				out.write(buf, 0, v);
		}
	}

	/** Copies a reader into a file (the original content, if any, are erased).
	 * The source and destination files will be closed after copied.
	 *
	 * @param dst the destination
	 * @param reader the source
	 * @param charset the charset; null as default (ISO-8859-1).
	 */
	public static final void copy(File dst, Reader reader, String charset)
	throws IOException {
		final File parent = dst.getParentFile();
		if (parent != null)
			parent.mkdirs();

		final Writer writer = charset != null ?
			(Writer)new FileWriter(dst, charset): new java.io.FileWriter(dst);

		try {
			copy(writer, reader);
		} finally {
			close(reader);
			writer.close();
		}
	}
	/** Copies an input stream into a file
	 * (the original content, if any, are erased).
	 * The file will be closed after copied.
	 * @param dst the destination
	 * @param in the source
	 */
	public static final void copy(File dst, InputStream in)
	throws IOException {
		final File parent = dst.getParentFile();
		if (parent != null)
			parent.mkdirs();

		final OutputStream out =
			new BufferedOutputStream(new FileOutputStream(dst));
		try {
			copy(out, in);
		} finally {
			close(in);
			out.close();
		}
	}

	/** Preserves the last modified time and other attributes if possible.
	 * @see #copy(File, File, int)
	 */
	public static int CP_PRESERVE = 0x0001;
	/** Copy only when the source is newer or when the destination is missing.
	 * @see #copy(File, File, int)
	 */
	public static int CP_UPDATE = 0x0002;
	/** Overwrites the destination file.
	 * @see #copy(File, File, int)
	 */
	public static int CP_OVERWRITE = 0x0004;
	/** Skips the SVN related files.
	 * @since 5.0.0
	 */
	public static int CP_SKIP_SVN = 0x1000;

	/** Copies a file or a directory into another.
	 *
	 * <p>If neither {@link #CP_UPDATE} nor {@link #CP_OVERWRITE},
	 * IOException is thrown if the destination exists.
	 *
	 * @param flags any combination of {@link #CP_UPDATE}, {@link #CP_PRESERVE},
	 * {@link #CP_OVERWRITE}.	 
	 */
	public static final void copy(File dst, File src, int flags)
	throws IOException {
		if (!src.exists())
			throw new FileNotFoundException(src.toString());

		if (dst.isDirectory()) {
			if (src.isDirectory()) {
				copyDir(dst, src, flags);
			} else {
				copyFile(new File(dst, src.getName()), src, flags);
			}
		} else if (dst.isFile()) {
			if (src.isDirectory()) {
				throw new IOException("Unable to copy a directory, "+src+", to a file, "+dst);
			} else {
				copyFile(dst, src, flags);
			}
		} else {
			if (src.isDirectory()) {
				copyDir(dst, src, flags);
			} else {
				copyFile(dst, src, flags);
			}
		}
	}
	/** Assumes both dst and src is a file. */
	private static final void copyFile(File dst, File src, int flags)
	throws IOException {
		assert D.OFF || src.isFile();
		if (dst.equals(src))
			throw new IOException("Copy to the same file, "+src);

		if ((flags & CP_OVERWRITE) == 0) {
			if ((flags & CP_UPDATE) != 0) {
				if (dst.lastModified() >= src.lastModified())
					return; //nothing to do
			} else if (dst.exists()) {
				throw new IOException("The destination already exists, "+dst);
			}
		}

		copy(dst, new FileInputStream(src));

		if ((flags & CP_PRESERVE) != 0) {
			dst.setLastModified(src.lastModified());
		}
	}
	/** Assumes both dst and src is a directory. */
	private static final void copyDir(File dst, File src, int flags)
	throws IOException {
		if ((flags & CP_SKIP_SVN) != 0 && ".svn".equals(src.getName()))
			return; //skip

		assert D.OFF || src.isDirectory();
		final File[] srcs = src.listFiles();
		for (int j = 0; j < srcs.length; ++j) {
			copy(new File(dst, srcs[j].getName()), srcs[j], flags); //recursive
		}
	}
	/** Deletes all files under the specified path.
	 */
	public static final boolean deleteAll(File file) {
		if (file.isDirectory()) {
			final File[] fls = file.listFiles();
			for (int j = 0; j < fls.length; ++j) {
				if (!deleteAll(fls[j]))
					return false;
					//failed as soon as possible to avoid removing extra files
			}
		}
		return file.delete();
	}

	/** Close an input stream without throwing an exception.
	 */
	public static final void close(InputStream strm) {
		if (strm != null) {
			try {
				strm.close();
			} catch (IOException ex) { //ignore it
				System.out.println("Unable to close an input stream");
			}
		}
	}
	/** Close a reader without throwing an exception.
	 */
	public static final void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException ex) { //ignore it
				System.out.println("Unable to close a reader");
			}
		}
	}

	/** Normalizes the catenation of two paths.
	 *
	 * @param parentPath the parent's path
	 * @param childPath the child's path
	 * If it starts with "/", parentPath is ignored.
	 * @since 5.0.0
	 */
	public static final String normalize(String parentPath, String childPath) {
		if (childPath == null || childPath.length() == 0)
			return normalize(parentPath);
		if ((parentPath == null || parentPath.length() == 0)
		|| childPath.charAt(0) == '/')
			return normalize(childPath);
		if (parentPath.charAt(parentPath.length() - 1) == '/')
			return normalize(parentPath + childPath);
		return normalize(parentPath + '/' + childPath);
	}
	/**
	 * Normalizes the specified path.
	 * It removes consecutive slahses, ending slahes,
	 * redudant . and ...
	 * <p>Unlike {@link File}, {@link #normalize} always assumes
	 * the separator to be '/', and it cannot handle the device prefix
	 * (e.g., c:). However, it handles //.
	 *
	 * @param path the path to normalize. If null, an empty string is returned.
	 * @since 5.0.0
	 */
	public static final String normalize(String path) {
		if (path == null)
			return "";

		//remove consecutive slashes
		final StringBuffer sb = new StringBuffer(path);
		boolean slash = false;
		for (int j = 0, len = sb.length(); j < len; ++j) {
			final boolean curslash = sb.charAt(j) == '/';
			if (curslash && slash && j != 1) {
				sb.deleteCharAt(j);
				--j; --len;
			}
			slash = curslash;
		}

		if (sb.length() > 1 && slash) //remove ending slash except "/"
			sb.setLength(sb.length() - 1);

		//remove ./
		while (sb.length() >= 2 && sb.charAt(0) == '.' && sb.charAt(1) == '/')
			sb.delete(0, 2); // "./" -> ""

		//remove /./
		for (int j = 0; (j = sb.indexOf( "/./", j)) >= 0;)
			sb.delete(j + 1, j + 3); // "/./" -> "/"

		//ends with "/."
		int len = sb.length();
		if (len >= 2 && sb.charAt(len - 1) == '.' && sb.charAt(len - 2) == '/')
			if (len == 2) return "/";
			else sb.delete(len - 2, len);

		//remove /../
		for (int j = 0; (j = sb.indexOf("/../", j)) >= 0;)
			j = removeDotDot(sb, j);

		// ends with "/.."
		len = sb.length();
		if (len >= 3 && sb.charAt(len - 1) == '.' && sb.charAt(len - 2) == '.'
		&& sb.charAt(len - 3) == '/') 
			if (len == 3) return "/";
			else removeDotDot(sb, len - 3);

		return sb.length() == path.length() ? path: sb.toString();
	}
	/** Removes "/..".
	 * @param j points '/' in "/.."
	 * @return the next index to search from
	 */
	private static int removeDotDot(StringBuffer sb, int j) {
		int k = j;
		while (--k >= 0 && sb.charAt(k) != '/') 
			;

		if (k + 3 == j && sb.charAt(k + 1) == '.' && sb.charAt(k + 2) == '.')
			return j + 4; // don't touch: "../.."

		sb.delete(j, j + 3); // "/.." -> ""

		if (j == 0) // "/.."
			return 0;

		if (k < 0) { // "a/+" => kill "a/", "a" => kill a
			sb.delete(0, j < sb.length() ? j + 1: j);
			return 0;
		}

		// "/a/+" => kill "/a", "/a" => kill "a"
		if (j >= sb.length()) ++k;
		sb.delete(k, j);
		return k;
	}

	/** Writes the specified string buffer to the specified writer.
	 * Use this method instead of out.write(sb.toString()), if sb.length()
	 * is large.
	 * @since 5.0.0
	 */
	public static final void write(Writer out, StringBuffer sb)
	throws IOException {
		//Don't convert sb to String to save the memory use
		for (int j = 0, len = sb.length(); j < len; ++j)
			out.write(sb.charAt(j));
	}

	/** Locates a file based o the current Locale. It never returns null.
	 *
	 * <p>If the filename contains "*", it will be replaced with a proper Locale.
	 * For example, if the current Locale is zh_TW and the resource is
	 * named "ab*.cd", then it searches "ab_zh_TW.cd", "ab_zh.cd" and
	 * then "ab.cd", until any of them is found.
	 *
	 * <blockquote>Note: "*" must be right before ".", or the last character.
	 * For example, "ab*.cd" and "ab*" are both correct, while
	 * "ab*cd" and "ab*\/cd" are ignored.</blockquote>
	 *
	 * <p>Unlike {@link org.zkoss.util.resource.Locators#locate}, the filename
	 * must contain '*', while {@link org.zkoss.util.resource.Locators#locate}
	 * always tries to locate the file by
	 * inserting the locale before '.'. In other words,
	 * Files.locate("/a/b*.c") is similar to
	 * Locators.locate(("/a/b.c", null, a_file_locator);
	 *
	 * @param flnm the filename to locate. If it doesn't contain any '*',
	 * it is returned directly. If the file is not found, flnm is returned, too.
	 * @see Locales#getCurrent
	 * @since 5.0.0
	 */
	public static final String locate(String flnm) {
		int j = flnm.indexOf('*');
		if (j < 0) return flnm;

		final String postfix = flnm.substring(j + 1);
		final Locale locale = Locales.getCurrent();
		final String[] secs = new String[] {
			locale.getLanguage(), locale.getCountry(), locale.getVariant()
		};

		final StringBuffer sb = new StringBuffer(flnm.substring(0, j));
		final int prefixlen = sb.length();
		for (j = secs.length;;) {
			if (--j >= 0 && secs[j].length() == 0)
				continue;

			sb.setLength(prefixlen);
			for (int k = 0; k <= j; ++k)
				sb.append('_').append(secs[k]);
			sb.append(postfix);
			flnm = sb.toString();
			if (j < 0 || new File(flnm).exists()) return flnm;
		}
	}
}
