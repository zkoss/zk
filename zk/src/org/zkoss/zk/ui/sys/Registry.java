/* Registry.java

	Purpose:
		
	Description:
		
	History:
		12:41 PM 2/25/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.AbstractWebApp;

/**
 * A helper class for class registry for internal use only.
 * @author jumperchen
 * @since 8.0.2
 */
public class Registry {
	private final static Registry INSTANCE = new Registry();
	private final Map<String, String> keys = new ConcurrentHashMap<String, String>(10);
	private final Map<String, String> signs = new ConcurrentHashMap<String, String>(10);
	private final Map<String, String> values = new ConcurrentHashMap<String, String>(10);
	private final static Map<String, byte[]> byteMap = new HashMap<String, byte[]>(10);

	public final static byte[] PREFS_0;
	public final static byte[] PREFS_1;
	public final static byte[] PREFS_2;

	static {
		byteMap.put(toString(new byte[] {112, 114, 101, 102, 115, 48}),
				PREFS_0 = new byte[] {98, 117, 105, 108, 100, 115, 105, 103, 110});
		byteMap.put(toString(new byte[] {112, 114, 101, 102, 115, 49}),
				PREFS_1 = new byte[] {111, 114, 103, 46, 122, 107,
				111, 115, 115, 46, 122, 107, 46, 117, 105, 46, 99, 108, 105, 101,
				110, 116, 46, 110, 111, 116, 105, 99, 101});
		byteMap.put(toString(new byte[] {112, 114, 101, 102, 115, 50}),
				PREFS_2 = new byte[] {
				89, 111, 117, 114, 32, 90, 75, 32, 98, 105, 110, 97, 114, 121, 32,
				105, 115, 32, 98, 101, 105, 110, 103, 32, 97, 108, 116, 101, 114,
				101, 100, 32, 97, 110, 100, 32,	109, 97, 121, 32, 110, 111, 116, 32,
				119, 111, 114, 107, 32, 97,	115, 32, 101, 120, 112, 101, 99, 116, 101,
				100, 46, 32, 80, 108, 101, 97, 115, 101, 32, 99, 111, 110, 116, 97,
				99, 116, 32, 117, 115, 32, 97, 116, 32, 105, 110, 102, 111, 64, 122,
				107, 111, 115, 115,	46, 111, 114, 103, 32, 102, 111, 114, 32, 97,
				115, 115, 105, 115,	116, 97, 110, 99, 101, 46});
		byteMap.put(toString(new byte[]{109, 115, 103, 48}),
				new byte[] {10, 60, 115, 99, 114, 105, 112, 116, 32, 99, 108,
						97, 115, 115, 61, 34, 122, 45, 114, 117, 110, 111, 110,
						99, 101, 34, 32, 116, 121, 112, 101, 61, 34, 116, 101,
						120, 116, 47, 106, 97, 118, 97, 115, 99, 114, 105, 112,
						116, 34, 62, 10, 122, 65, 117, 46, 115, 104, 111, 119,
						69, 114, 114, 111, 114, 40, 39, 70, 65, 73, 76, 69, 68,
						95, 84, 79, 95, 80, 82, 79, 67, 69, 83, 83, 39, 44, 32,
						39, 37, 49, 36, 115, 39, 44, 39, 37, 50, 36, 115, 39,
						41, 59, 10, 60, 47, 115, 99, 114, 105, 112, 116, 62});
	}

	private Registry() {
	}
	public static Registry getInstance() {
		return INSTANCE;
	}
	/*package*/ final void addKeys(String key, String value) {
		if (key != null && value != null)
			signs.put(key, value);
	}
	public boolean isValid(Class cls) {
		final String key = getKey(cls);

		if (signs.containsKey(key)) {
			String value = values.get(key);
			if (value == null) {
				synchronized (values) {
					if (!values.containsKey(key)) {
						value = DigestUtils
								.md5Hex(loadValue(cls) + AbstractWebApp.getBuildStamp());
						values.put(key, value);
					}
				}
			}
			return signs.get(key).equals(value);
		}
		return false;
	}

	public String getKey(Class cls) {
		final String name = cls.getName();
		if (!keys.containsKey(name)) {
			final String key =  DigestUtils.md5Hex(name);
			keys.put(cls.getName(), key);
			return key;
		}
		return keys.get(name);
	}

	public String getSign(Class cls) {
		return signs.get(getKey(cls));
	}

	// sign the cls to the webapp
	public static void sign(WebApp wapp, Class... clses) {
		for (Class cls : clses) {
			if (!INSTANCE.isValid(cls)) {
				String sign = INSTANCE.getSign(cls);
				String key = Strings.toString(Registry.PREFS_1);
				if (sign == null) {
					sign = INSTANCE.getKey(cls);
				}
				if (sign != null) {
					if (sign.length() > 8)
						sign = sign.substring(0, 9);
				}

				wapp.setAttribute(key, gen(Strings.toString(Registry.PREFS_2), sign));
				return; // done
			}
		}
	}

	private static String gen(String msg, String error) {
		return String.format(toString(
				byteMap.get(toString(new byte[] {109, 115, 103, 48}))), msg,
				error != null ? toString(new byte[] {69, 114, 114, 111, 114, 58}) + error : "");
	}

	private synchronized String loadValue(Class cls) {
		InputStream is = null;
		Reader in = null;
		try {
			URL location = cls.getResource("/" + cls.getName().replace(".", "/")
							+ toString(new byte[] {46, 99, 108, 97, 115, 115}));
			is = location.openStream();
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringWriter sw = new StringWriter();

			char[] buf = new char[1024];
			int len;
			while ((len = in.read(buf, 0, buf.length)) >= 0)
				sw.write(buf, 0, len);
			buf = null;

			StringBuffer sb = sw.getBuffer();
			return DigestUtils.md5Hex(sb.toString());
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	private static final String toString(byte[] data) {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(data);
		}
	}
}
