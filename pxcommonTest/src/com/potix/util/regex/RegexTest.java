/* RegexTest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 11 16:59:31     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.regex;

import java.util.regex.*;

import junit.framework.*;

/**
 * Test the performance of regex.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class RegexTest extends TestCase {
	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(RegexTest.class);
	}

	/**
	 * Constructor.
	 */
	public RegexTest(String name)	{
		super(name);
	}

	// Must have this.
	public static Test suite() {
		return new TestSuite(RegexTest.class);
	}

	protected void setUp() {
	}

	protected void tearDown() {
	}

	private static final String[] _prefixes = {
		"/css/", "/images/", "/js/go/", "/menu/", "/sys/error/"
	};
	private static final Pattern _pattern;
	static {
		final StringBuffer sb = new StringBuffer();
		for (int j = 0; j < _prefixes.length; ++j) {
			if (j > 0) sb.append('|');
			sb.append(_prefixes[j]).append(".*");
		}
		_pattern = Pattern.compile(sb.toString());
	}
	private static final String[] _tests = {
		"/abcdefg", "/cski", "/js/gx", "/images/a.gif"
	};
	private static final Matcher[] _matchers = new Matcher[_tests.length];
	static {
		for (int j = 0; j < _tests.length; ++j)
			_matchers[j] = _pattern.matcher(_tests[j]);
	}
	private static final int LOOPS = 100000;
	public void testOneByOne() {
		for (int n = LOOPS; --n >= 0;) {
			for (int j = 0; j < _tests.length; ++j) {
				for (int k = 0; k < _prefixes.length; ++k)
					if (_tests[j].startsWith(_prefixes[k]))
						break; //found
			}
		}
	}
	public void testRegex() {
		for (int n = LOOPS; --n >= 0;) {
			for (int j = 0; j < _tests.length; ++j)
				_pattern.matcher(_tests[j]).matches();
		}
	}
	public void testRegex2() {
		for (int n = LOOPS; --n >= 0;) {
			for (int j = 0; j < _tests.length; ++j)
				_matchers[j].matches();
		}
	}
}
