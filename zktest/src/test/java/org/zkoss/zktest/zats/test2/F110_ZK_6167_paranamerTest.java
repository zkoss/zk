/* F110_ZK_6167_paranamerTest.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 22:29:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.spi.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.zkoss.bind.paranamer.AdaptiveParanamer;
import org.zkoss.bind.paranamer.CachingParanamer;

/**
 * @author peakerlee
 */
public class F110_ZK_6167_paranamerTest {
	@Test
	public void test(@TempDir Path dir) throws Exception {
		// javac 21+ turns a qualified enum constant in a pattern switch into a CONSTANT_Dynamic entry
		int feature = Runtime.version().feature();
		Optional<ToolProvider> javac = ToolProvider.findFirst("javac");
		Optional<ToolProvider> javap = ToolProvider.findFirst("javap");
		assumeTrue(feature >= 21 && javac.isPresent() && javap.isPresent(), "needs a JDK 21+ javac and javap");

		Path source = dir.resolve("CondyVM.java");
		Files.writeString(source, "public class CondyVM {\n"
				+ "sealed interface S permits K {}\n"
				+ "enum K implements S { X, Y }\n"
				+ "public void cmd(String name, int count) {}\n"
				+ "int k(S s) { return switch (s) { case K.X -> 1; case K.Y -> 2; }; }\n"
				+ "}\n");
		// -g without -parameters, the way a plain Gradle or Maven build compiles a ViewModel
		run(javac.get(), "-g", "-proc:none", "--release", String.valueOf(feature), "-d", dir.toString(), source.toString());
		assertTrue(run(javap.get(), "-v", dir.resolve("CondyVM.class").toString())
				.contains("= Dynamic "), "CondyVM.class has no CONSTANT_Dynamic entry any more; update the fixture");

		try (URLClassLoader loader = new URLClassLoader(new URL[] {dir.toUri().toURL()}, getClass().getClassLoader())) {
			Method cmd = loader.loadClass("CondyVM").getMethod("cmd", String.class, int.class);
			assertArrayEquals(new String[] {"name", "count"},
					new CachingParanamer(new AdaptiveParanamer()).lookupParameterNames(cmd, false));
		}
	}

	private static String run(ToolProvider tool, String... args) {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		int rc = tool.run(writer, writer, args);
		writer.flush();
		assertEquals(0, rc, tool.name() + " failed: " + out);
		return out.toString();
	}
}
