/* Files.java

{{IS_NOTE

	Purpose: File related utilities.
	Description:
	History:
	 2001/6/29, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.util.ArraysX;

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

		copy(dst, new BufferedInputStream(new FileInputStream(src)));

		if ((flags & CP_PRESERVE) != 0) {
			dst.setLastModified(src.lastModified());
		}
	}
	/** Assumes both dst and src is a directory. */
	private static final void copyDir(File dst, File src, int flags)
	throws IOException {
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
}
